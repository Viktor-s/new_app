package me.justup.upme.entity;


public class SetAvatarResponse extends BaseHttpResponseEntity {
    public Result result = new Result();

    public class Result extends BaseHttpParams {
        public String file_hash = "";
    }

}
