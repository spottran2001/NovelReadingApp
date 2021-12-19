package com.huawei.hms.novelreadingapp.model;

public class Chapter {
    private String chapter;

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chaper) {
        this.chapter = chaper;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private String content;

    public Chapter (String chapter, String content)
    {
        this.chapter = chapter;
        this.content = content;
    }

    public Chapter(){

    }
}
