package me.justup.upme.utils;


public class ServerSwitcher {
    private static final String HTTP = "http://";
    private static ServerSwitcher mServerSwitcher;
    /*
    private String url = "http://test.justup.me/uptabinterface/jsonrpc/";
    private String cloudStorageUrl = "http://test.justup.me/CloudStorage";
    private String avatarUrl = "http://test.justup.me";
    */
    private String url = "http://pre-prod.justup.me/uptabinterface/jsonrpc/";
    private String cloudStorageUrl = "http://pre-prod.justup.me/CloudStorage";
    private String avatarUrl = "http://pre-prod.justup.me";


    private ServerSwitcher() {

    }

    public static ServerSwitcher getInstance() {
        if (mServerSwitcher == null) {
            mServerSwitcher = new ServerSwitcher();
        }

        return mServerSwitcher;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getCloudStorageUrl() {
        return cloudStorageUrl;
    }

    public void setEasyUrl(String newUrl) {
        url = HTTP + newUrl + "/uptabinterface/jsonrpc/";
        cloudStorageUrl = HTTP + newUrl + "/CloudStorage";
        avatarUrl = HTTP + newUrl;
    }

}
