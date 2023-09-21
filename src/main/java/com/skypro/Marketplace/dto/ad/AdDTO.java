package com.skypro.Marketplace.dto.ad;

import com.skypro.Marketplace.dto.comment.CommentDTO;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;



public class AdDTO {
    private Integer pk;
    private String title;
    private Integer price;
    private String description;
    private Integer author;
    private String image;
    private List<CommentDTO> comments = new ArrayList<>();

    public AdDTO(Integer pk, String title, Integer price, String description, Integer author, String image, List<CommentDTO> comments) {
        this.pk = pk;
        this.title = title;
        this.price = price;
        this.description = description;
        this.author = author;
        this.image = image;
        this.comments = comments;
    }

    public AdDTO() {
    }

    public Integer getAuthor() {
        return author;
    }

    public void setAuthor(Integer author) {
        this.author = author;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getPk() {
        return pk;
    }

    public void setPk(Integer pk) {
        this.pk = pk;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

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

    public List<CommentDTO> getComments() {
        return comments;
    }

    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdDTO adDTO = (AdDTO) o;
        return Objects.equals(author, adDTO.author) && Objects.equals(image, adDTO.image) && Objects.equals(pk, adDTO.pk) && Objects.equals(price, adDTO.price) && Objects.equals(title, adDTO.title) && Objects.equals(description, adDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, image, pk, price, title, description);
    }

    @Override
    public String toString() {
        return "AdDTO{" +
                "author=" + author +
                ", image='" + image + '\'' +
                ", pk=" + pk +
                ", price=" + price +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
