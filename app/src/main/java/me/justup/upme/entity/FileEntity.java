package me.justup.upme.entity;

import java.io.Serializable;


public class FileEntity implements Serializable {
    private static final long serialVersionUID = 0L;

    public static final int LOCAL_FILE = 1;
    public static final int CLOUD_FILE = 2;
    public static final int SHARE_FILE = 3;
    public static final int LOCAL_AND_CLOUD_FILE = 4;

    private boolean isFavorite;
    private String name;
    private String path;
    private long size;
    private long date;
    private String hash;
    private int type;


    public FileEntity(boolean isFavorite, String name, String path, long size, long date, String hash, int type) {
        this.isFavorite = isFavorite;
        this.name = name;
        this.path = path;
        this.size = size;
        this.date = date;
        this.hash = hash;
        this.type = type;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public long getSize() {
        return size;
    }

    public long getDate() {
        return date;
    }

    public String getHash() {
        return hash;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "isFavorite=" + isFavorite +
                " name=" + name +
                " path=" + path +
                " size=" + size +
                " date=" + date +
                " hash=" + hash +
                " type=" + type +
                '}';
    }

}
