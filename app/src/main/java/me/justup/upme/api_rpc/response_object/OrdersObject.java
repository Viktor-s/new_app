package me.justup.upme.api_rpc.response_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.justup.upme.api_rpc.response_object.sub_object.ProcessingData;
import me.justup.upme.api_rpc.utils.Constants;

public class OrdersObject implements Serializable {

    @SerializedName(Constants.SUCCESS)
    private boolean success;

    @SerializedName(Constants.ORDER_STATUS)
    private String order_status;

    @SerializedName(Constants.SUM)
    private int sum;

    @SerializedName(Constants.HASH)
    private String hash;

    @SerializedName(Constants.CURRENCY)
    private String currency;

    @SerializedName(Constants.PROCESSING_DATA)
    private List<ProcessingData> processing_data;

    private ArrayList<OrdersObject> ordersObjectList;

    public boolean isSuccess() {
        return success;
    }

    public String getOrder_status() {
        return order_status;
    }

    public int getSum() {
        return sum;
    }

    public String getHash() {
        return hash;
    }

    public String getCurrency() {
        return currency;
    }

    public List<ProcessingData> getProcessing_data() {
        return processing_data;
    }

    public ArrayList<OrdersObject> getOrdersObjectList() {
        return ordersObjectList;
    }

    public void setOrdersObjectList(ArrayList<OrdersObject> ordersObjectList) {
        this.ordersObjectList = ordersObjectList;
    }

    @Override
    public String toString() {
        return "OrdersObject{" +
                "success=" + success +
                ", order_status='" + order_status + '\'' +
                ", sum=" + sum +
                ", hash='" + hash + '\'' +
                ", currency='" + currency + '\'' +
                ", processing_data=" + processing_data +
                '}';
    }
}
