package me.justup.upme.entity;


import java.io.Serializable;
import java.util.List;

public class ArticlesGetShortDescriptionResponse extends BaseHttpResponseEntity {
 //   public Result result;

  //  public class Result implements Serializable {
        public List<ResultList> result;
   // }

    public class ResultList implements Serializable {
        public int id;
        public String title;
        public String short_descr;
        public String thumbnail;
        public String posted_at;
        public List<Comment> comments;
    }

    public class Comment implements Serializable {
        public int id;
        public String content;
        public String posted_at;
        public int author_id;
        public Author author;

        public class Author implements Serializable {
            public String name;
            public String img;
        }
    }
}