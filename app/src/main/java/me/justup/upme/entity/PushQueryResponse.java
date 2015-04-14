package me.justup.upme.entity;

import java.util.ArrayList;


public class PushQueryResponse extends BaseHttpResponseEntity {
    public Result result = new Result();

    public class Result extends BaseHttpParams {
        public ArrayList<PushError> error;

        public class PushError extends BaseHttpParams {
            public int code = 0;
            public String message = "";
            public int data = 0;
        }
    }

}
