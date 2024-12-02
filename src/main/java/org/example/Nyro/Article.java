package org.example.Nyro;

import java.sql.Timestamp;
import java.util.List;

public class Article {
    private final String title;
    private Timestamp publishedAt; // Change from String to Timestamp
    private int id;
    private String content;
    private String category;
    private String description;
    private String source;
    private String url;
    private  String imgURL;

    private List<String> keywords;  // Store keywords

    public Article(int id, String title, Timestamp publishedAt, String description, String source, String url, String imgURL) {
           this.id = id;
           this.title = title;
           this.publishedAt = publishedAt;
           this.description = description;
           this.source = source;
           this.url = url;
           this.imgURL = imgURL;
    }

    public Article(int id, String title, String description, String content) {
        this.title = title;
        this.id = id;
        this.description = description;
        this.content = content;
    }

    public Article(int id, String title, Timestamp publishedAt, String content, String description, String source, String url, String imgURL, String category) {
        this.title = title;
        this.publishedAt = publishedAt;
        this.id = id;
        this.content = content;
        this.category = category;
        this.description = description;
        this.source = source;
        this.url = url;
        this.imgURL = imgURL;
    }

    public Article(int id, String title, Timestamp publishedAt, String description, String source, String url, String imgURL, String category) {
        this.title = title;
        this.publishedAt = publishedAt;
        this.id = id;
        this.description = description;
        this.source = source;
        this.url = url;
        this.imgURL = imgURL;
        this.category = category;
    }
    // Getters
    public String getTitle() { return title; }
    public Timestamp getPublishedAt() { return publishedAt; } // Return as Timestamp
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getSource() { return source; }
    public String getUrl() { return url; }

    public String getContent() {
        return content;
    }

    public String getImgURL() { return imgURL; }

    public List<String> getKeywords() {
        return keywords;
    }


    public int getId() {
        return id;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", publishedAt=" + publishedAt +
                ", description='" + description + '\'' +
                ", source='" + source + '\'' +
                ", url='" + url + '\'' +
                ", imgURL='" + imgURL + '\'' +
                ", category='" + category + '\'' +
                ", keywords=" + keywords +
                '}';
    }
}
