package me.justup.upme.api_rpc.jsonrpclibrary;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONRPCClient {

    protected JSONRPCParams.Versions version;
    protected String encoding = HTTP.UTF_8;

    /**
     * Create a JSONRPCClient from a given uri
     *
     * @param uri The URI of the JSON-RPC service
     * @return a JSONRPCClient instance acting as a proxy for the web service
     */
    public static JSONRPCClient create(String uri, JSONRPCParams.Versions version) {
        JSONRPCClient client = new JSONRPCHttpClient(uri);
        client.version = version;
        return client;
    }

    /**
     * Get the debugging mode
     */
    public boolean isDebug() {
        return JSONRPCParams.DEBUG;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void delEncoding() {
        this.encoding = "";
    }

    protected abstract JSONObject doJSONRequest(JSONObject request) throws JSONRPCException;

    protected static JSONArray getJSONArray(Object[] array) {
        JSONArray arr = new JSONArray();
        for (Object item : array) {
            if (item.getClass().isArray()) {
                arr.put(getJSONArray((Object[]) item));
            } else {
                arr.put(item);
            }
        }

        return arr;
    }

    protected JSONObject doRequest(String method, Object[] params) throws JSONRPCException {
        // Copy method arguments in a json array
        JSONArray jsonParams = new JSONArray();
        for (int i = 0; i < params.length; i++) {
            if (params[i].getClass().isArray()) {
                jsonParams.put(getJSONArray((Object[]) params[i]));
            }
            jsonParams.put(params[i]);
        }

        //Create the json request object
        JSONObject jsonRequest = new JSONObject();
        try {
            jsonRequest.put(JSONRPCParams.ID, JSONRPCParams.JSON_RPC_ID);
            jsonRequest.put(JSONRPCParams.METHOD, method);
            jsonRequest.put(JSONRPCParams.PARAMS, jsonParams);
        } catch (JSONException e1) {
            throw new JSONRPCException("Invalid JSON request : ", e1);
        }

        return doJSONRequest(jsonRequest);
    }

    // TODO : Most popular in UPME !
    protected JSONObject doRequest(String method, String params) throws JSONRPCException, JSONException {
        JSONObject jsonRequest = new JSONObject();

        try {
            jsonRequest.put(JSONRPCParams.ID, JSONRPCParams.JSON_RPC_ID);
            jsonRequest.put(JSONRPCParams.METHOD, method);
            jsonRequest.put(JSONRPCParams.PARAMS, new JSONObject(params));
            jsonRequest.put(JSONRPCParams.JSONRPC, JSONRPCParams.JSON_RPC_VERSION);
        } catch (JSONException e1) {
            throw new JSONRPCException("Invalid JSON request : ", e1);
        }

        return doJSONRequest(jsonRequest);
    }

    protected int soTimeout = 0, connectionTimeout = 0;
    protected String token = null;

    /**
     * Get the socket operation timeout in milliseconds
     */
    public int getSoTimeout() {
        return soTimeout;
    }

    /**
     * Set the socket operation timeout
     *
     * @param soTimeout timeout in milliseconds
     */
    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    /**
     * Get the connection timeout in milliseconds
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * Set the connection timeout
     *
     * @param connectionTimeout timeout in milliseconds
     */
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    /**
     * Get token from request header
     */
    public String getToken() {
        return token;
    }

    /**
     * Set token to request header
     *
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public Object call(String method, Object... params) throws JSONRPCException {
        try {
            return doRequest(method, params).get(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            throw new JSONRPCException("Cannot convert result : ", e);
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public Object call(String method, String params) throws JSONRPCException {
        try {
            return doRequest(method, params).get(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            throw new JSONRPCException("Cannot convert result to String : ", e);
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a String
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public String callString(String method, Object... params) throws JSONRPCException {
        try {
            return doRequest(method, params).getString(JSONRPCParams.JSON_RESULT);
        } catch (JSONRPCException | JSONException e) {
            throw new JSONRPCException("Cannot convert result to String : ", e);
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a String
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public String callString(String method, String params) throws JSONRPCException {
        try {
            return doRequest(method, params).getString(JSONRPCParams.JSON_RESULT);
        } catch (JSONException | JSONRPCException e) {
            throw new JSONRPCException("Cannot convert result to String : ", e);
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as an int
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public int callInt(String method, Object... params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getInt(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return Integer.parseInt(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to int : ", e1);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as an int
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public Object callInt(String method, String params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);
            return response.getInt(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return Integer.parseInt(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to int : ", e1);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a long
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public long callLong(String method, Object... params) throws JSONRPCException {
        JSONObject response = null;
        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);
            return response.getLong(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return Long.parseLong(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to long : ", e);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a long
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public long callLong(String method, String params) throws JSONRPCException {
        JSONObject response = null;
        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);
            return response.getLong(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return Long.parseLong(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to long : ", e);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a boolean
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public boolean callBoolean(String method, Object... params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getBoolean(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return Boolean.parseBoolean(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to boolean : ", e1);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a boolean
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public boolean callBoolean(String method, String params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getBoolean(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return Boolean.parseBoolean(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to boolean : ", e);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a double
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public double callDouble(String method, Object... params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getDouble(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return Double.parseDouble(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to double : ", e);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a double
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public double callDouble(String method, String params) throws JSONRPCException {
        JSONObject response = null;
        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getDouble(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return Double.parseDouble(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to double : ", e);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a JSONObject
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public JSONObject callJSONObject(String method, String params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getJSONObject(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                if (response.has(JSONRPCParams.JSON_RESULT)) {
                    return new JSONObject(response.getString(JSONRPCParams.JSON_RESULT));
                } else {
                    return new JSONObject(response.getString(JSONRPCParams.ERROR));
                }
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to JSONObject : ", e);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a JSONObject
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public JSONObject callJSONObject(String method, Object... params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getJSONObject(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                if (response.has(JSONRPCParams.JSON_RESULT)) {
                    return new JSONObject(response.getString(JSONRPCParams.JSON_RESULT));
                } else {
                    return new JSONObject(response.getString(JSONRPCParams.ERROR));
                }
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to JSONObject : ", e);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a JSONArray
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public JSONArray callJSONArray(String method, Object... params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getJSONArray(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return new JSONArray(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to JSONArray : ", e);
            }
        }
    }

    /**
     * Perform a remote JSON-RPC method call
     *
     * @param method The name of the method to invoke
     * @param params Arguments of the method
     * @return The result of the RPC as a JSONArray
     * @throws JSONRPCException if an error is encountered during JSON-RPC method call
     */
    public JSONArray callJSONArray(String method, String params) throws JSONRPCException {
        JSONObject response = null;

        try {
            response = doRequest(method, params);
            if (response == null) throw new JSONRPCException("Cannot call method : " + method);

            return response.getJSONArray(JSONRPCParams.JSON_RESULT);
        } catch (JSONException e) {
            try {
                return new JSONArray(response.getString(JSONRPCParams.JSON_RESULT));
            } catch (NumberFormatException | JSONException e1) {
                throw new JSONRPCException("Cannot convert result to JSONArray : ", e);
            }
        }
    }
}
