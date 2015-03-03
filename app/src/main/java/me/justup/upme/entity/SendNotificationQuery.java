package me.justup.upme.entity;

import me.justup.upme.http.ApiWrapper;


public class SendNotificationQuery extends BaseHttpQueryEntity {
    private String method = ApiWrapper.ACCOUNT_SEND_NOTIFICATION;

    public Params params = new Params();
    public int id = 123;

    public class Params extends BaseHttpParams {
        public int user_id;
        public Data data = new Data();

        public class Data extends BaseHttpParams {
            public int owner_id;
            public String owner_name;
            public int connection_type;
            public int room;
            public String link;
        }
    }

}
