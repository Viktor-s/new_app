package me.justup.upme.api_rpc.jsonrpclibrary;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static me.justup.upme.utils.LogUtils.LOGI;

/**
 * Implementation of JSON-RPC over HTTP/POST
 */
public class JSONRPCHttpClient extends JSONRPCClient {
    private static final String TAG = JSONRPCHttpClient.class.getSimpleName();

    // HttpClient to issue the HTTP/POST request
    private HttpClient httpClient;

    // Service URI
    private String serviceUri;

    // HTTP 1.0
    private static final ProtocolVersion PROTOCOL_VERSION = JSONRPCParams.PROTOCOL_VERSION;

    /**
     * Construct a JsonRPCClient with the given httpClient and service uri
     *
     * @param client httpClient to use
     * @param uri    uri of the service
     */
    public JSONRPCHttpClient(HttpClient client, String uri) {
        httpClient = client;
        serviceUri = uri;
    }

    /**
     * Construct a JsonRPCClient with the given service uri
     *
     * @param uri uri of the service
     */
    public JSONRPCHttpClient(String uri) {
        this(new DefaultHttpClient(), uri);
    }

    protected JSONObject doJSONRequest(JSONObject jsonRequest) throws JSONRPCException {
        // Create HTTP/POST request with a JSON entity containing the request
        HttpPost request = new HttpPost(serviceUri);
        // If token not null set request header
        if(getToken()!=null) {
            LOGI(TAG, "Header Token : " + getToken());
            request.setHeader(JSONRPCParams.AUTHORIZATION_HEADER, getToken());
        }

        HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(params, getSoTimeout());
        HttpProtocolParams.setVersion(params, PROTOCOL_VERSION);

        request.setParams(params);

        if (JSONRPCParams.DEBUG) {
            Log.i(TAG, "Request : " + jsonRequest.toString());
        }

        HttpEntity entity;

        try {
            if (encoding.length() > 0) {
                entity = new JSONEntity(jsonRequest, encoding);
            } else {
                entity = new JSONEntity(jsonRequest);
            }
        } catch (UnsupportedEncodingException e1) {
            throw new JSONRPCException("Unsupported encoding : ", e1);
        }

        request.setEntity(entity);

        try {
            // Execute the request and try to decode the JSON Response
            long t = System.currentTimeMillis();
            HttpResponse response = httpClient.execute(request);


            t = System.currentTimeMillis() - t;
            String responseString = EntityUtils.toString(response.getEntity());

            responseString = responseString.trim();

            if (JSONRPCParams.DEBUG) {
                Log.i(TAG, "Response : " + responseString);
            }

            JSONObject jsonResponse = new JSONObject(responseString);

            // Check for remote errors
            if (jsonResponse.has(JSONRPCParams.ERROR)) {
                Object jsonError = jsonResponse.get(JSONRPCParams.ERROR);
                if (!jsonError.equals(null)) {
                    JSONObject errorObj = jsonResponse.getJSONObject(JSONRPCParams.ERROR);
                    int code = errorObj.getInt(JSONRPCParams.CODE);
                    if (code >= -32099 && code <= -32000) {
                        return jsonResponse;
                    }else {
                        throw new JSONRPCException(jsonResponse.get(JSONRPCParams.ERROR));
                    }
                }

                return jsonResponse; // JSON-RPC 1.0
            } else {
                return jsonResponse; // JSON-RPC 2.0
            }
        }

        // Underlying errors are wrapped into a JSONRPCException instance
        catch (ClientProtocolException e) {
            throw new JSONRPCException("HTTP error : ", e);
        } catch (IOException e) {
            throw new JSONRPCException("IO error : ", e);
        } catch (JSONException e) {
            throw new JSONRPCException("Invalid JSON response : ", e);
        }
    }
}
