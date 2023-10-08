package com.skypro.Marketplace.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * Represents a comment on an advertisement in the marketplace.
 */
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "created_at")
    private Long createdAt;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    @Column(name = "text")
    private String text;


    public Comment(Integer id, User author, Long createdAt, Ad ad, String text) {
        this.id = id;
        this.author = author;

        this.createdAt = createdAt;
        this.ad = ad;
        this.text = text;
    }

    public Comment() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }



    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public Ad getAd() {
        return ad;
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id) && Objects.equals(author, comment.author) && Objects.equals(createdAt, comment.createdAt) && Objects.equals(ad, comment.ad) && Objects.equals(text, comment.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, author, createdAt, ad, text);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", author=" + author +
                ", createdAt=" + createdAt +
                ", ad=" + ad +
                ", text='" + text + '\'' +
                '}';
    }
}
