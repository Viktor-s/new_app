package me.justup.upme.entity;

/**
 * <h3>Example <code>toString()</code> for correct answer:</h3>
 * <p><b>Good</b> answer:</p>
 * <code>LoginResponseEntity{result=Result{PHONE='OK'}} BaseHttpResponseEntity{jsonrpc='2.0', error=null, id=123}</code>
 * <p><b>Bad</b> answer:</p>
 * <code>LoginResponseEntity{result=null} BaseHttpResponseEntity{jsonrpc='2.0', error=JsonRpcError{code=-32603, message='Internal error', data='PHONE_NOT_FOUND'}, id=123}</code>
 */
public class BaseHttpResponseEntity {
    public String jsonrpc = "";
    public JsonRpcError error;
    public int id = 0;

    public class JsonRpcError {
        public int code = 0;
        public String message = "";
        public String data = "";

        @Override
        public String toString() {
            return "JsonRpcError{" +
                    "code=" + code +
                    ", message='" + message + '\'' +
                    ", data='" + data + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "BaseHttpResponseEntity{" +
                "jsonrpc='" + jsonrpc + '\'' +
                ", error=" + error +
                ", id=" + id +
                '}';
    }

}
