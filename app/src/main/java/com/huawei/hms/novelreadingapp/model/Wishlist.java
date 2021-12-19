package com.huawei.hms.novelreadingapp.model;

public class Wishlist {

    private String chapter_id;
    private String novel_id;

    public Wishlist(String novel_id, String chapter_id){
        this.chapter_id = chapter_id;
        this.novel_id = novel_id;
    }
    public  Wishlist(){

    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getNovel_id() {
        return novel_id;
    }

    public void setNovel_id(String novel_id) {
        this.novel_id = novel_id;
    }
}
