package me.justup.upme.api_rpc.utils;

import java.io.Serializable;

import me.justup.upme.api_rpc.jsonrpclibrary.JSONRPCParams;
import me.justup.upme.api_rpc.request_model.handler.AppIntentHandler;

public class RequestParamBuilder implements Serializable {

    // All final attributes
    private final String method; // required
    private final String jsonObject; // required
    private final String action; // required
    private final AppIntentHandler.RequestType requestType; // required

    private final String url; // optional
    private final JSONRPCParams.Versions version; // optional
    private final String token; // optional
    private final int connectionTimeout; // optional
    private final int soTimeout; // optional

    private RequestParamBuilder(ParamBuilder builder) {
        this.method = builder.method;
        this.jsonObject = builder.jsonObject;
        this.action = builder.action;
        this.requestType = builder.requestType;

        this.url = builder.url;
        this.version = builder.version;
        this.token = builder.token;
        this.connectionTimeout = builder.connectionTimeout;
        this.soTimeout = builder.soTimeout;
    }

    // All getter, and NO setter to provide immutability

    public String getUrl() { return url; }

    public JSONRPCParams.Versions getJSONRPCVersion() {
        return version;
    }

    public String getMethod() {
        return method;
    }

    public String getJsonObject() {
        return jsonObject;
    }

    public String getAction() {
        return action;
    }

    public AppIntentHandler.RequestType getRequestType() {
        return requestType;
    }

    public String getToken() {
        return token;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public int getSoTimeout() {
        return soTimeout;
    }

    @Override
    public String toString() {
        return "ParamBuilder : " + this.url +
                ", " + this.version +
                ", " + this.method +
                ", " + this.jsonObject +
                ", " + this.action +
                ", " + this.token +
                ", " + this.connectionTimeout +
                ", " + this.soTimeout;
    }

    public static class ParamBuilder implements Serializable {

        private final String method; // required
        private final String jsonObject; // required
        private final String action; // required
        private final AppIntentHandler.RequestType requestType; // required

        private String url = JSONRPCParams.BASE_URL; // optional
        private JSONRPCParams.Versions version = JSONRPCParams.Versions.VERSION_2; // optional
        private String token = null; // optional
        private int connectionTimeout = 2000; // optional
        private int soTimeout = 2000; // optional

        public ParamBuilder(String method, String jsonObject, String action, AppIntentHandler.RequestType requestType) {
            this.method = method;
            this.jsonObject = jsonObject;
            this.action = action;
            this.requestType = requestType;
        }

        public ParamBuilder url(String url) {
            this.url = url;
            return this;
        }

        public ParamBuilder version(JSONRPCParams.Versions version) {
            this.version = version;
            return this;
        }

        public ParamBuilder token(String token) {
            this.token = token;
            return this;
        }

        public ParamBuilder connectionTimeout(int connectionTimeout) {
            this.connectionTimeout = connectionTimeout;
            return this;
        }

        public ParamBuilder soTimeout(int soTimeout) {
            this.soTimeout = soTimeout;
            return this;
        }

        // Return the finally constructed ParamBuilder object
        public RequestParamBuilder build() {
            return new RequestParamBuilder(this);
        }
    }
}
