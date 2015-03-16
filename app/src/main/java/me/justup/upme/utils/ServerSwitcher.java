package me.justup.upme.utils;


public class ServerSwitcher {
    private static ServerSwitcher mServerSwitcher;

    private String url = "http://test.justup.me/uptabinterface/jsonrpc/";
    private String cloudStorageUrl = "http://test.justup.me/CloudStorage";

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

    public String getCloudStorageUrl() {
        return cloudStorageUrl;
    }

    public void setCloudStorageUrl(String cloudStorageUrl) {
        this.cloudStorageUrl = cloudStorageUrl;
    }

}
