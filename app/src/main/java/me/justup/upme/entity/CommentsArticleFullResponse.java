package me.justup.upme.entity;

import java.io.Serializable;
import java.util.List;

public class CommentsArticleFullResponse implements Serializable {
    public List<Comment> result;

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
