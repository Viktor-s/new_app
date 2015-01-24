package me.justup.upme.entity;


public class LoginPhoneQueryEntity extends BaseHttpQueryEntity {
    public String method = "";
    public Params params = new Params();
    public int id = 123;

    public class Params {
        public String phone = "";
    }

}
