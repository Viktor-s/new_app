package me.justup.upme.api_rpc.response_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import me.justup.upme.api_rpc.response_object.sub_object.PushResult;
import me.justup.upme.api_rpc.utils.Constants;

public class UniversalPushResult implements Serializable{

    @SerializedName(Constants.PUSH_RESULT)
    private List<PushResult> push_result;

    @SerializedName(Constants.ERROR)
    private List<RPCError> error;

    public List<PushResult> getPush_result() {
        return push_result;
    }

    public List<RPCError> getError() {
        return error;
    }

    @Override
    public String toString() {
        return "WebRTCObject{" +
                "push_result=" + push_result +
                ", error=" + error +
                '}';
    }
}
