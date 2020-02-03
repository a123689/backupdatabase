package com.example.dat.backupdatabase;

public class Note {
    private long id;
    private String title;
    private String content;
    private String lastModified;

    public String getTitle() {
        return title;
    }

    public Note setTitle(String title) {
        this.title = title;
        return this;
    }

    public long getId() {
        return id;
    }

    public Note setId(long id) {
        this.id = id;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Note setContent(String content) {
        this.content = content;
        return this;
    }

    public String getLastModified() {
        return lastModified;
    }

    public Note setLastModified(String lastModified) {
        this.lastModified = lastModified;
        return this;
    }
}
