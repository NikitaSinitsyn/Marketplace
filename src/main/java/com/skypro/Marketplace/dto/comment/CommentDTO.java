package com.skypro.Marketplace.dto.comment;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) for representing a comment.
 */
public class CommentDTO {

    /**
     * Unique identifier of the comment.
     */
    private Integer pk;

    /**
     * Identifier of the comment author.
     */
    private Integer author;

    /**
     * Image of the comment author in string format.
     */
    private String authorImage;

    /**
     * First name of the comment author.
     */
    private String authorFirstName;

    /**
     * Timestamp when the comment was created.
     */
    private Long createdAt;

    /**
     * Text content of the comment.
     */
    private String text;


    public CommentDTO(Integer pk, Integer author, String authorImage, String authorFirstName, Long createdAt, String text) {
        this.pk = pk;
        this.author = author;
        this.authorImage = authorImage;
        this.authorFirstName = authorFirstName;
        this.createdAt = createdAt;
        this.text = text;
    }

    public CommentDTO() {
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getAuthorImage() {
        return authorImage;
    }

    public void setAuthorImage(String authorImage) {
        this.authorImage = authorImage;
    }

    public String getAuthorFirstName() {
        return authorFirstName;
    }

    public void setAuthorFirstName(String authorFirstName) {
        this.authorFirstName = authorFirstName;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
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
        CommentDTO that = (CommentDTO) o;
        return Objects.equals(pk, that.pk) && Objects.equals(author, that.author) && Objects.equals(authorImage, that.authorImage) && Objects.equals(authorFirstName, that.authorFirstName) && Objects.equals(createdAt, that.createdAt) && Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, author, authorImage, authorFirstName, createdAt, text);
    }

    @Override
    public String toString() {
        return "CommentDTO{" +
                "pk=" + pk +
                ", author=" + author +
                ", authorImage='" + authorImage + '\'' +
                ", authorFirstName='" + authorFirstName + '\'' +
                ", createdAt=" + createdAt +
                ", text='" + text + '\'' +
                '}';
    }
}
