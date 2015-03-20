package me.justup.upme.entity;


public class FileGetPropertiesResponse extends BaseHttpResponseEntity {
    public Result result = new Result();

    public class Result extends BaseHttpParams {
        public int size = 0;
        public long create_date = 0;
        public long update_date = 0;
        public Owner owner = new Owner();

        public class Owner {
            public int id = 0;
            public String name = "";
        }
    }

}
