package me.justup.upme.api_rpc.jsonrpclibrary;

import org.apache.http.ProtocolVersion;
import org.apache.http.protocol.HTTP;

import java.io.Serializable;
import java.util.UUID;

import me.justup.upme.BuildConfig;

public class JSONRPCParams {

    public static final boolean DEBUG = BuildConfig.BUILD_TYPE.equals("debug");
    public static final boolean UPME = BuildConfig.APPLICATION_ID.contains("me.justup.upme");

    public static final String BASE_HTTP = "http://";
    public static final String BASE_URL = BASE_HTTP + "test.justup.me/uptabinterface/jsonrpc/";

    public static final String AUTHORIZATION_HEADER = "X-AUTH-UPMETOKEN";
    public static final String CONTENT_TYPE = "application/json";
    public static final String ENCODING = HTTP.UTF_8;

    public static final String JSON_RESULT = "result";
    public static final String ID = "id";
    public static final String METHOD = "method";
    public static final String PARAMS = "params";
    public static final String JSONRPC = "jsonrpc";
    public static final String ERROR = "error";
    public static final String CODE = "code";

    public static final ProtocolVersion PROTOCOL_VERSION = new ProtocolVersion("HTTP", 1, 0);
    public static final String JSON_RPC_VERSION = "2.0";
    public static final int JSON_RPC_ID = UPME ? 123 : UUID.randomUUID().hashCode();

    public static enum Versions implements Serializable{
        VERSION_1,
        VERSION_2
    }
}
