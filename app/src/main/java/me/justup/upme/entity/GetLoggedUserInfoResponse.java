package me.justup.upme.entity;


public class GetLoggedUserInfoResponse extends BaseHttpResponseEntity {
    public Result result = new Result();

    public class Result extends BaseHttpParams {
        public int id;
        public int parent_id;
        public String name = "";
        public String jabber_id = "";
        public int dateAdd = 0;
        public String login = "";
        public String phone = "";
        public String img = "";
        public double latitude = 0.0;
        public double longitude = 0.0;
        public String in_system = "";
        public int total_sum = 0;
    }

}
