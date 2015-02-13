package me.justup.upme.entity;

import java.io.Serializable;
import java.util.List;

public class ArticleFullResponse {
    public Result result;

    public class Result implements Serializable {
        public ResultFull result;
    }

    public class ResultFull implements Serializable {
        public int id;
        public String full_descr;
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
