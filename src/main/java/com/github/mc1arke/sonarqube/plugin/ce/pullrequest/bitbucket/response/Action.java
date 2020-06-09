package com.github.mc1arke.sonarqube.plugin.ce.pullrequest.bitbucket.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Action {
    private final String id;
    private final String action;
    private final Comment comment;
    private final CommentAnchor commentAnchor;
    @JsonCreator
    public Action(@JsonProperty("id") String id, @JsonProperty("action") String action,
                  @JsonProperty("comment") final Comment comment,
                  @JsonProperty("commentAnchor") final CommentAnchor commentAnchor) {
        this.id = id;
        this.action = action;
        this.comment = comment;
        this.commentAnchor = commentAnchor;
    }

    public String getId() { return id; }
    public String getAction() {
        return action;
    }
    public String getText() {
        return comment.getText();
    }
    public String getPath() {
        return commentAnchor.getPath();
    }
    public int getLine() {
        return commentAnchor.getLine();
    }

    private static class Comment {
        private final String text;

        @JsonCreator
        public Comment(@JsonProperty("text") String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
    }

    private static class CommentAnchor {
        private final String path;
        private final int line;

        @JsonCreator
        public CommentAnchor(@JsonProperty("text") String path, @JsonProperty("line") int line) {
            this.path = path;
            this.line = line;
        }
        public String getPath() { return path; }
        public int getLine() { return line; }
    }
}
