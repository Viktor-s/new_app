package me.justup.upme.api_rpc.response_object.sub_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import me.justup.upme.api_rpc.utils.Constants;

public class ProcessingData implements Serializable {

    @SerializedName(Constants.SUCCESS)
    private List<InputOutputData> input_data;

    @SerializedName(Constants.SUCCESS)
    private List<Object> input_errors;

    @SerializedName(Constants.SUCCESS)
    private List<InputOutputData> output_data;

    @SerializedName(Constants.SUCCESS)
    private List<Object> output_errors;

    @SerializedName(Constants.SUCCESS)
    private String hash;

    @SerializedName(Constants.SUCCESS)
    private long start_time;

    @SerializedName(Constants.SUCCESS)
    private long finish_time;

    @SerializedName(Constants.SUCCESS)
    private String status;

    public List<InputOutputData> getInput_data() {
        return input_data;
    }

    public List<Object> getInput_errors() {
        return input_errors;
    }

    public List<InputOutputData> getOutput_data() {
        return output_data;
    }

    public List<Object> getOutput_errors() {
        return output_errors;
    }

    public String getHash() {
        return hash;
    }

    public long getStart_time() {
        return start_time;
    }

    public long getFinish_time() {
        return finish_time;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "ProcessingData{" +
                "input_data=" + input_data +
                ", input_errors=" + input_errors +
                ", output_data=" + output_data +
                ", output_errors=" + output_errors +
                ", hash='" + hash + '\'' +
                ", start_time=" + start_time +
                ", finish_time=" + finish_time +
                ", status='" + status + '\'' +
                '}';
    }
}
