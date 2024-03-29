package me.justup.upme.apprtc;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Scanner;

import me.justup.upme.apprtc.AppRTCClient.SignalingParameters;
import me.justup.upme.apprtc.util.AsyncHttpURLConnection;

import static me.justup.upme.utils.LogUtils.LOGD;
import static me.justup.upme.utils.LogUtils.LOGE;

/**
 * AsyncTask that converts an AppRTC room URL into the set of signaling
 * parameters to use with that room.
 */
public class RoomParametersFetcher {
    private static final String TAG = "RoomRTCClient";

    private RoomParametersFetcherEvents events = null;
    private static final int TURN_HTTP_TIMEOUT_MS = 10000; // 5000
    private boolean loopback;
    private String registerUrl = null;
    private String registerMessage = null;
    private AsyncHttpURLConnection httpConnection = null;

    /**
     * Room parameters fetcher callbacks.
     */
    public static interface RoomParametersFetcherEvents {
        /**
         * Callback fired once the room's signaling parameters
         * SignalingParameters are extracted.
         */
        public void onSignalingParametersReady(final SignalingParameters params);

        /**
         * Callback for room parameters extraction error.
         */
        public void onSignalingParametersError(final String description);
    }

    public RoomParametersFetcher(boolean loopback, String roomUrl, String roomMessage, final RoomParametersFetcherEvents events) {
        this.loopback = loopback;
        this.registerUrl = roomUrl;
        this.registerMessage = roomMessage;
        this.events = events;
    }

    public RoomParametersFetcher(String roomUrl, String roomMessage, final RoomParametersFetcherEvents events) {
        this.registerUrl = roomUrl;
        this.registerMessage = roomMessage;
        this.events = events;
    }

    public void makeRequest() {
        LOGD(TAG, "Connecting to room:  " + registerUrl);
        httpConnection = new AsyncHttpURLConnection("POST", registerUrl, registerMessage, new AsyncHttpURLConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                LOGE(TAG, "Room connection error : " + errorMessage);
                events.onSignalingParametersError(errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                roomHttpResponseParse(response);
            }

        });

        httpConnection.send();
    }

    private void roomHttpResponseParse(String response) {
        LOGD(TAG, "Room response : " + response);

        try {
            LinkedList<IceCandidate> iceCandidates = null;
            SessionDescription offerSdp = null;
            JSONObject roomJson = new JSONObject(response);

            String result = roomJson.getString("result");
            if (!result.equals("SUCCESS")) {
                events.onSignalingParametersError("Room response error : " + result);
                return;
            }

            response = roomJson.getString("params");
            roomJson = new JSONObject(response);
            String roomId = roomJson.getString("room_id");
            String clientId = roomJson.getString("client_id");
            String wssUrl = roomJson.getString("wss_url");
            String wssPostUrl = roomJson.getString("wss_post_url");
            boolean initiator = (roomJson.getBoolean("is_initiator"));
            if (!initiator) {
                iceCandidates = new LinkedList<IceCandidate>();
                String messagesString = roomJson.getString("messages");
                JSONArray messages = new JSONArray(messagesString);
                for (int i = 0; i < messages.length(); ++i) {
                    String messageString = messages.getString(i);
                    JSONObject message = new JSONObject(messageString);
                    String messageType = message.getString("type");
                    LOGD(TAG, "GAE->C #" + i + " : " + messageString);

                    if (messageType.equals("offer")) {
                        offerSdp = new SessionDescription(SessionDescription.Type.fromCanonicalForm(messageType), message.getString("sdp"));
                    } else if (messageType.equals("candidate")) {
                        IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                        iceCandidates.add(candidate);
                    } else {
                        LOGE(TAG, "Unknown message : " + messageString);
                    }
                }
            }

            LOGD(TAG, "RoomId: " + roomId + ". ClientId: " + clientId);
            LOGD(TAG, "Initiator: " + initiator);
            LOGD(TAG, "WSS url: " + wssUrl);
            LOGD(TAG, "WSS POST url: " + wssPostUrl);

            LinkedList<PeerConnection.IceServer> iceServers =  iceServersFromPCConfigJSON(roomJson.getString("pc_config"));
            boolean isTurnPresent = false;
            for (PeerConnection.IceServer server : iceServers) {
                LOGD(TAG, "IceServer : " + server);
                if (server.uri.startsWith("turn:")) {
                    isTurnPresent = true;
                    break;
                }
            }

            if (!isTurnPresent) {
                LinkedList<PeerConnection.IceServer> turnServers = requestTurnServers(roomJson.getString("turn_url"));
                for (PeerConnection.IceServer turnServer : turnServers) {
                    LOGD(TAG, "TurnServer : " + turnServer);
                    iceServers.add(turnServer);
                }
            }

//            MediaConstraints pcConstraints = constraintsFromJSON(roomJson.getString("pc_constraints"));
//            addDTLSConstraintIfMissing(pcConstraints, loopback);
//            LOGD(TAG, "PcConstraints : " + pcConstraints);
//            MediaConstraints videoConstraints = constraintsFromJSON(getAVConstraints("video", roomJson.getString("media_constraints")));
//            LOGD(TAG, "VideoConstraints : " + videoConstraints);
//            MediaConstraints audioConstraints = constraintsFromJSON(getAVConstraints("audio", roomJson.getString("media_constraints")));
//            LOGD(TAG, "AudioConstraints : " + audioConstraints);
//
//            SignalingParameters params = new SignalingParameters(
//                    iceServers,
//                    initiator,
//                    pcConstraints,
//                    videoConstraints,
//                    audioConstraints,
//                    clientId,
//                    wssUrl,
//                    wssPostUrl,
//                    offerSdp,
//                    iceCandidates); 08.04

            SignalingParameters params = new SignalingParameters(
                    iceServers, initiator,
                    clientId, wssUrl, wssPostUrl,
                    offerSdp, iceCandidates);

            events.onSignalingParametersReady(params);
        } catch (JSONException e) {
            events.onSignalingParametersError("Room JSON parsing error : " + e.toString());
        } catch (IOException e) {
            events.onSignalingParametersError("Room IO error : " + e.toString());
        }
    }

    // Requests & returns a TURN ICE Server based on a request URL.  Must be run
    // off the main thread!
//    private LinkedList<PeerConnection.IceServer> requestTurnServers(String url) throws IOException, JSONException {
//        LinkedList<PeerConnection.IceServer> turnServers = new LinkedList<PeerConnection.IceServer>();
//        LOGD(TAG, "Request TURN from : " + url);
//
//        URLConnection connection = (new URL(url)).openConnection();
//        connection.addRequestProperty("user-agent", "Mozilla/5.0");
//        connection.addRequestProperty("origin", "http://95.213.170.164:8080");
//        String response = drainStream(connection.getInputStream());
//        LOGD(TAG, "TURN response : " + response);
//        JSONObject responseJSON = new JSONObject(response);
//        String username = responseJSON.getString("username");
//        String password = responseJSON.getString("password");
//        JSONArray turnUris = responseJSON.getJSONArray("uris");
//        for (int i = 0; i < turnUris.length(); i++) {
//            String uri = turnUris.getString(i);
//            turnServers.add(new PeerConnection.IceServer(uri, username, password));
//        }
//
//        return turnServers;
//    }     08.04

    // Requests & returns a TURN ICE Server based on a request URL.  Must be run
    // off the main thread!
    private LinkedList<PeerConnection.IceServer> requestTurnServers(String url) throws IOException, JSONException {
        LinkedList<PeerConnection.IceServer> turnServers = new LinkedList<PeerConnection.IceServer>();
        LOGD(TAG, "Request TURN from : " + url);
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setConnectTimeout(TURN_HTTP_TIMEOUT_MS);
        connection.setReadTimeout(TURN_HTTP_TIMEOUT_MS);
        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("Non-200 response when requesting TURN server from " + url + " : " + connection.getHeaderField(null));
        }

        InputStream responseStream = connection.getInputStream();
        String response = drainStream(responseStream);
        connection.disconnect();
        LOGD(TAG, "TURN response : " + response);
        JSONObject responseJSON = new JSONObject(response);
        String username = responseJSON.getString("username");
        String password = responseJSON.getString("password");
        JSONArray turnUris = responseJSON.getJSONArray("uris");
        for (int i = 0; i < turnUris.length(); i++) {
            String uri = turnUris.getString(i);
            turnServers.add(new PeerConnection.IceServer(uri, username, password));
        }

        return turnServers;
    }

    // Return the list of ICE servers described by a WebRTCPeerConnection
    // configuration string.
    private LinkedList<PeerConnection.IceServer> iceServersFromPCConfigJSON(String pcConfig) throws JSONException {
        JSONObject json = new JSONObject(pcConfig);
        JSONArray servers = json.getJSONArray("iceServers");
        LinkedList<PeerConnection.IceServer> ret = new LinkedList<PeerConnection.IceServer>();
        for (int i = 0; i < servers.length(); ++i) {
            JSONObject server = servers.getJSONObject(i);
            String url = server.getString("urls");
            String credential = server.has("credential") ? server.getString("credential") : "";
            ret.add(new PeerConnection.IceServer(url, "", credential));
        }

        return ret;
    }

    // Return the contents of an InputStream as a String.
    private String drainStream(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    // TODO Why it is needed?
    // Mimic Chrome and set DtlsSrtpKeyAgreement to true if not set to false by
    // the web-app.
    private void addDTLSConstraintIfMissing(MediaConstraints pcConstraints, boolean loopback) {
        for (MediaConstraints.KeyValuePair pair : pcConstraints.mandatory) {
            if (pair.getKey().equals("DtlsSrtpKeyAgreement")) {
                return;
            }
        }

        for (MediaConstraints.KeyValuePair pair : pcConstraints.optional) {
            if (pair.getKey().equals("DtlsSrtpKeyAgreement")) {
                return;
            }
        }
        // DTLS isn't being specified (e.g. for debug=loopback calls), so enable
        // it for normal calls and disable for loopback calls.
        if (loopback) {
            pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "false"));
        } else {
            pcConstraints.optional.add(new MediaConstraints.KeyValuePair("DtlsSrtpKeyAgreement", "true"));
        }
    }

    // Return the constraints specified for |type| of "audio" or "video" in
    // |mediaConstraintsString|.
    private String getAVConstraints(String type, String mediaConstraintsString) throws JSONException {
        JSONObject json = new JSONObject(mediaConstraintsString);
        // Tricky handling of values that are allowed to be (boolean or
        // MediaTrackConstraints) by the getUserMedia() spec.  There are three
        // cases below.
        if (!json.has(type) || !json.optBoolean(type, true)) {
            // Case 1: "audio"/"video" is not present, or is an explicit "false"
            // boolean.
            return null;
        }

        if (json.optBoolean(type, false)) {
            // Case 2: "audio"/"video" is an explicit "true" boolean.
            return "{\"mandatory\": {}, \"optional\": []}";
        }

        // Case 3: "audio"/"video" is an object.
        return json.getJSONObject(type).toString();
    }

    private MediaConstraints constraintsFromJSON(String jsonString) throws JSONException {
        if (jsonString == null) {
            return null;
        }

        MediaConstraints constraints = new MediaConstraints();
        JSONObject json = new JSONObject(jsonString);
        JSONObject mandatoryJSON = json.optJSONObject("mandatory");
        if (mandatoryJSON != null) {
            JSONArray mandatoryKeys = mandatoryJSON.names();
            if (mandatoryKeys != null) {
                for (int i = 0; i < mandatoryKeys.length(); ++i) {
                    String key = mandatoryKeys.getString(i);
                    String value = mandatoryJSON.getString(key);
                    constraints.mandatory.add(new MediaConstraints.KeyValuePair(key, value));
                }
            }
        }

        JSONArray optionalJSON = json.optJSONArray("optional");
        if (optionalJSON != null) {
            for (int i = 0; i < optionalJSON.length(); ++i) {
                JSONObject keyValueDict = optionalJSON.getJSONObject(i);
                String key = keyValueDict.names().getString(0);
                String value = keyValueDict.getString(key);
                constraints.optional.add(new MediaConstraints.KeyValuePair(key, value));
            }
        }

        return constraints;
    }

}
