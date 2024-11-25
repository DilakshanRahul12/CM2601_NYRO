package org.example.Nyro;

import java.time.LocalDateTime;

public class Article {
    private String title;
    private String description;
    private String content;
    private String source;
    private LocalDateTime publishedAt;
    private String url;

    // Default Constructor
    public Article() {}

    // Parameterized Constructor
    public Article(String title, String description, String content, String source, LocalDateTime publishedAt, String url) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.source = source;
        this.publishedAt = publishedAt;
        this.url = url;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // toString Method for Debugging
    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", content='" + content + '\'' +
                ", source='" + source + '\'' +
                ", publishedAt=" + publishedAt +
                ", url='" + url + '\'' +
                '}';
    }
}
