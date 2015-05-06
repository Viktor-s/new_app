package me.justup.upme.entity;

import java.io.Serializable;

import static me.justup.upme.utils.ExplorerUtils.AVI;
import static me.justup.upme.utils.ExplorerUtils.CLOUD_FILE;
import static me.justup.upme.utils.ExplorerUtils.DOT_PDF;
import static me.justup.upme.utils.ExplorerUtils.FILE;
import static me.justup.upme.utils.ExplorerUtils.GP;
import static me.justup.upme.utils.ExplorerUtils.IMAGE;
import static me.justup.upme.utils.ExplorerUtils.JPEG;
import static me.justup.upme.utils.ExplorerUtils.JPG;
import static me.justup.upme.utils.ExplorerUtils.LOCAL_AND_CLOUD_FILE;
import static me.justup.upme.utils.ExplorerUtils.LOCAL_FILE;
import static me.justup.upme.utils.ExplorerUtils.MP4;
import static me.justup.upme.utils.ExplorerUtils.PDF;
import static me.justup.upme.utils.ExplorerUtils.PNG;
import static me.justup.upme.utils.ExplorerUtils.VIDEO;


public class FileEntity implements Serializable {
    private static final long serialVersionUID = 0L;

    private final boolean isFavorite;
    private final String name;
    private final String path;
    private final long size;
    private final long date;
    private String hash;
    private int type;
    private final boolean isOnTablet;
    private boolean isOnCloud;
    private final int fileType;
    private boolean hiddenForSearch;


    public FileEntity(boolean isFavorite, String name, String path, long size, long date, String hash, int type) {
        this.isFavorite = isFavorite;
        this.name = name;
        this.path = path;
        this.size = size;
        this.date = date;
        this.hash = hash;
        this.type = type;

        isOnTablet = type == LOCAL_FILE || type == LOCAL_AND_CLOUD_FILE;
        isOnCloud = type == CLOUD_FILE;

        if (name.contains(JPG) || name.contains(JPEG) || name.contains(PNG))
            fileType = IMAGE;
        else if (name.contains(DOT_PDF))
            fileType = PDF;
        else if (name.contains(MP4) || name.contains(AVI) || name.contains(GP))
            fileType = VIDEO;
        else
            fileType = FILE;
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

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setOnCloud(boolean isOnCloud) {
        this.isOnCloud = isOnCloud;
    }

    public int getFileType() {
        return fileType;
    }

    public boolean isOnTablet() {
        return isOnTablet;
    }

    public boolean isOnCloud() {
        return isOnCloud;
    }

    public boolean isHiddenForSearch() {
        return hiddenForSearch;
    }

    public void setHiddenForSearch(boolean hide) {
        hiddenForSearch = hide;
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
                " isOnTablet=" + isOnTablet +
                " isOnCloud=" + isOnCloud +
                " fileType=" + fileType +
                '}';
    }

}
