package me.justup.upme.entity;

import java.util.ArrayList;

import me.justup.upme.http.ApiWrapper;


public class WebRtcStartCallQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.WEBRTC_START_CALL;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public ArrayList<Integer> user_ids = new ArrayList<>();
        public String room_id;

        public void setUserIds(int userId) {
            user_ids.add(userId);
        }
    }

}
