package me.justup.upme.entity;

import java.util.ArrayList;

import me.justup.upme.http.ApiWrapper;


public class WebRtcStopCallQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.WEBRTC_STOP_CALL;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public ArrayList<Integer> user_ids = new ArrayList<>();

        public void setUserIds(int userId) {
            user_ids.add(userId);
        }
    }

}
