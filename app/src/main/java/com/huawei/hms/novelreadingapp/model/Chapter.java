package com.huawei.hms.novelreadingapp.model;

public class Chapter {
    public Chapter(String chapter, String content, String title, String id) {
        this.chapter = chapter;
        this.content = content;
        this.title = title;
        this.id = id;
    }
    public Chapter(){

    }

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    private String novelId;

    private String chapter;
    private String content;

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String title;
    private String id;


}
