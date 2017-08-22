package com.vinhdn.rxjavaannotation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.vinhdn.rxjavaannotation.anotations.DBColumnName;
import com.vinhdn.rxjavaannotation.anotations.DBIgnore;
import com.vinhdn.rxjavaannotation.anotations.DBTableName;

/**
 * Created by vinh on 8/21/17.
 */

@DBTableName(name = "post")
public class Post extends DBModel{
    private int id;
    private String author;
    private String title;
    private String content;
    @DBColumnName("date_create")
    private String dateCreate;
    @DBIgnore
    private boolean isSelected;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", dateCreate='" + dateCreate + '\'' +
                ", isSelected=" + isSelected +
                '}';
    }
}
