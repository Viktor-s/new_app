package me.justup.upme.apprtc;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

import de.tavendo.autobahn.WebSocket.WebSocketConnectionObserver;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import me.justup.upme.apprtc.util.AsyncHttpURLConnection;
import me.justup.upme.apprtc.util.LooperExecutor;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;
import static me.justup.upme.utils.LogUtils.LOGW;

/**
 * WebSocket client implementation.
 *
 * <p>All public methods should be called from a looper executor thread
 * passed in a constructor, otherwise exception will be thrown.
 * All events are dispatched on the same thread.
 */

public class WebSocketChannelClient {
    private static final String TAG = "WSChannelRTCClient";

    private static final int CLOSE_TIMEOUT = 1000;
    private WebSocketChannelEvents events = null;
    private LooperExecutor executor = null;
    private WebSocketConnection ws = null;
    private WebSocketObserver wsObserver = null;
    private String wsServerUrl = null;
    private String postServerUrl = null;
    private String roomID = null;
    private String clientID = null;
    private WebSocketConnectionState state = null;
    private final Object closeEventLock = new Object();
    private boolean closeEvent;
    // WebSocket send queue. Messages are added to the queue when WebSocket
    // client is not registered and are consumed in register() call.
    private LinkedList<String> wsSendQueue = null;

    /**
     * Possible WebSocket connection states.
     */
    public enum WebSocketConnectionState {
        NEW, CONNECTED, REGISTERED, CLOSED, ERROR
    };

    /**
     * Callback interface for messages delivered on WebSocket.
     * All events are dispatched from a looper executor thread.
     */
    public interface WebSocketChannelEvents {
        public void onWebSocketMessage(final String message);
        public void onWebSocketClose();
        public void onWebSocketError(final String description);
    }

    public WebSocketChannelClient(LooperExecutor executor, WebSocketChannelEvents events) {
        this.executor = executor;
        this.events = events;
        roomID = null;
        clientID = null;
        wsSendQueue = new LinkedList<String>();
        state = WebSocketConnectionState.NEW;
    }

    public WebSocketConnectionState getState() {
        return state;
    }

    public void connect(final String wsUrl, final String postUrl) {
        checkIfCalledOnValidThread();
        if (state != WebSocketConnectionState.NEW) {
            LOGE(TAG, "WebSocket is already connected.");
            return;
        }
        wsServerUrl = wsUrl;
        postServerUrl = postUrl;
        closeEvent = false;

        LOGD(TAG, "Connecting WebSocket to : " + wsUrl + ". Post URL : " + postUrl);
        ws = new WebSocketConnection();
        wsObserver = new WebSocketObserver();
        try {
            ws.connect(new URI(wsServerUrl), wsObserver);
        } catch (URISyntaxException e) {
            reportError("URI error: " + e.getMessage());
        } catch (WebSocketException e) {
            reportError("WebSocket connection error: " + e.getMessage());
        }
    }

    public void register(final String roomID, final String clientID) {
        checkIfCalledOnValidThread();
        this.roomID = roomID;
        this.clientID = clientID;
        if (state != WebSocketConnectionState.CONNECTED) {
            LOGW(TAG, "WebSocket register() in state " + state);
            return;
        }
        LOGD(TAG, "Registering WebSocket for room " + roomID + ". CLientID: " + clientID);
        JSONObject json = new JSONObject();
        try {
            json.put("cmd", "register");
            json.put("roomid", roomID);
            json.put("clientid", clientID);
            LOGD(TAG, "C->WSS: " + json.toString());
            ws.sendTextMessage(json.toString());
            state = WebSocketConnectionState.REGISTERED;
            // Send any previously accumulated messages.
            for (String sendMessage : wsSendQueue) {
                send(sendMessage);
            }
            wsSendQueue.clear();
        } catch (JSONException e) {
            reportError("WebSocket register JSON error: " + e.getMessage());
        }
    }

    public void send(String message) {
        checkIfCalledOnValidThread();
        switch (state) {
            case NEW:
            case CONNECTED:
                // Store outgoing messages and send them after websocket client
                // is registered.
                LOGD(TAG, "WS ACC : " + message);
                wsSendQueue.add(message);
                return;
            case ERROR:
            case CLOSED:
                LOGE(TAG, "WebSocket send() in error or closed state : " + message);
                return;
            case REGISTERED:
                JSONObject json = new JSONObject();
                try {
                    json.put("cmd", "send");
                    json.put("msg", message);
                    message = json.toString();
                    LOGD(TAG, "C->WSS : " + message);
                    ws.sendTextMessage(message);
                } catch (JSONException e) {
                    reportError("WebSocket send JSON error: " + e.getMessage());
                }
                break;
        }
        return;
    }

    // This call can be used to send WebSocket messages before WebSocket
    // connection is opened.
    public void post(String message) {
        checkIfCalledOnValidThread();
        sendWSSMessage("POST", message);
    }

    public void disconnect(boolean waitForComplete) {
        checkIfCalledOnValidThread();
        LOGD(TAG, "Disonnect WebSocket. State : " + state);
        if (state == WebSocketConnectionState.REGISTERED) {
            // Send "bye" to WebSocket server.
            send("{\"type\": \"bye\"}");
            state = WebSocketConnectionState.CONNECTED;
            // Send http DELETE to http WebSocket server.
            sendWSSMessage("DELETE", "");
        }
        // Close WebSocket in CONNECTED or ERROR states only.
        if (state == WebSocketConnectionState.CONNECTED
                || state == WebSocketConnectionState.ERROR) {
            ws.disconnect();
            state = WebSocketConnectionState.CLOSED;

            // Wait for websocket close event to prevent websocket library from
            // sending any pending messages to deleted looper thread.
            if (waitForComplete) {
                synchronized (closeEventLock) {
                    while (!closeEvent) {
                        try {
                            closeEventLock.wait(CLOSE_TIMEOUT);
                            break;
                        } catch (InterruptedException e) {
                            LOGE(TAG, "Wait error : " + e.toString());
                        }
                    }
                }
            }
        }

        LOGD(TAG, "Disonnecting WebSocket done.");
    }

    private void reportError(final String errorMessage) {
        Log.e(TAG, errorMessage);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                if (state != WebSocketConnectionState.ERROR) {
                    state = WebSocketConnectionState.ERROR;
                    events.onWebSocketError(errorMessage);
                }
            }
        });
    }

    // Asynchronously send POST/DELETE to WebSocket server.
    private void sendWSSMessage(final String method, final String message) {
        String postUrl = postServerUrl + "/" + roomID + "/" + clientID;
        LOGD(TAG, "WS " + method + " : " + postUrl + " : " + message);
        AsyncHttpURLConnection httpConnection = new AsyncHttpURLConnection(method, postUrl, message, new AsyncHttpURLConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                reportError("WS " + method + " error: " + errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
            }
        });
        httpConnection.send();
    }

    // Helper method for debugging purposes. Ensures that WebSocket method is
    // called on a looper thread.
    private void checkIfCalledOnValidThread() {
        if (!executor.checkOnLooperThread()) {
            throw new IllegalStateException("WebSocket method is not called on valid thread");
        }
    }

    private class WebSocketObserver implements WebSocketConnectionObserver {
        @Override
        public void onOpen() {
            LOGD(TAG, "WebSocket connection opened to : " + wsServerUrl);
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    state = WebSocketConnectionState.CONNECTED;
                    // Check if we have pending register request.
                    if (roomID != null && clientID != null) {
                        register(roomID, clientID);
                    }
                }
            });
        }

        @Override
        public void onClose(WebSocketCloseNotification code, String reason) {
            LOGD(TAG, "WebSocket connection closed. Code: " + code
                    + ". Reason: " + reason + ". State: " + state);
            synchronized (closeEventLock) {
                closeEvent = true;
                closeEventLock.notify();
            }
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (state != WebSocketConnectionState.CLOSED) {
                        state = WebSocketConnectionState.CLOSED;
                        events.onWebSocketClose();
                    }
                }
            });
        }

        @Override
        public void onTextMessage(String payload) {
            LOGD(TAG, "WSS->C: " + payload);
            final String message = payload;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    if (state == WebSocketConnectionState.CONNECTED
                            || state == WebSocketConnectionState.REGISTERED) {
                        events.onWebSocketMessage(message);
                    }
                }
            });
        }

        @Override
        public void onRawTextMessage(byte[] payload) {
        }

        @Override
        public void onBinaryMessage(byte[] payload) {
        }
    }

}