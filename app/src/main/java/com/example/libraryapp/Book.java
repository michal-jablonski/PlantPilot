package com.example.libraryapp;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Book implements Serializable {

    @SerializedName("title")
    private String title;

    @SerializedName("author_name")
    private List<String> authors;

    @SerializedName("cover_i")
    private String cover;

    @SerializedName("number_of_pages_median")
    private String numberOfPages;

    @SerializedName("subject_facet")
    private List<String> subjects;

    @SerializedName("first_publish_year")
    private int firstPublishYear;

    public String getTitle() {
        return title;
    }


    public List<String> getAuthors() {
        return authors;
    }


    public String getCover() {
        return cover;
    }


    public String getNumberOfPages() {
        return numberOfPages;
    }

    public List<String> getSubjects() {
        return subjects;
    }

    public int getFirstPublishYear() {
        return firstPublishYear;
    }
}