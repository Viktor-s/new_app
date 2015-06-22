package me.justup.upme.api_rpc.response_object;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.justup.upme.api_rpc.response_object.sub_object.Owner;
import me.justup.upme.api_rpc.response_object.sub_object.PushResult;
import me.justup.upme.api_rpc.utils.Constants;

public class FileObject implements Serializable {

    @SerializedName(Constants.ID)
    private int id;

    @SerializedName(Constants.NAME)
    private String name;

    @SerializedName(Constants.PUSH_RESULT)
    private List<PushResult> push_result;

    @SerializedName(Constants.ERROR)
    private List<RPCError> error;

    @SerializedName(Constants.STATUS)
    private boolean status;

    @SerializedName(Constants.FILE_HASH)
    private String file_hash;

    @SerializedName(Constants.SIZE)
    private int size;

    @SerializedName(Constants.HASH_NAME)
    private String hash_name;

    @SerializedName(Constants.SHARE_ALL)
    private boolean share_all;

    @SerializedName(Constants.LINK)
    private String link;

    @SerializedName(Constants.CREATE_DATE)
    private long create_date;

    @SerializedName(Constants.UPDATE_DATE)
    private long update_date;

    @SerializedName(Constants.OWNER)
    private Owner owner;

    @SerializedName(Constants.FAVORITE)
    private boolean favorite;

    @SerializedName(Constants.SUCCESS)
    private boolean success;

    private ArrayList<FileObject> fileObjectArrayList = null;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<PushResult> getPush_result() {
        return push_result;
    }

    public List<RPCError> getError() {
        return error;
    }

    public boolean isStatus() {
        return status;
    }

    public String getFile_hash() {
        return file_hash;
    }

    public int getSize() {
        return size;
    }

    public String getHash_name() {
        return hash_name;
    }

    public boolean isShare_all() {
        return share_all;
    }

    public String getLink() {
        return link;
    }

    public long getCreate_date() {
        return create_date;
    }

    public long getUpdate_date() {
        return update_date;
    }

    public Owner getOwner() {
        return owner;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public boolean isSuccess() {
        return success;
    }

    public ArrayList<FileObject> getFileObjectArrayList() {
        return fileObjectArrayList;
    }

    public void setFileObjectArrayList(ArrayList<FileObject> fileObjectArrayList) {
        this.fileObjectArrayList = fileObjectArrayList;
    }

    @Override
    public String toString() {
        return "FileStorageObject{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", push_result=" + push_result +
                ", error=" + error +
                ", status=" + status +
                ", file_hash='" + file_hash + '\'' +
                ", size=" + size +
                ", hash_name='" + hash_name + '\'' +
                ", share_all=" + share_all +
                ", link='" + link + '\'' +
                ", create_date=" + create_date +
                ", update_date=" + update_date +
                ", owner=" + owner +
                ", favorite=" + favorite +
                ", success=" + success +
                '}';
    }
}
