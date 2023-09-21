package com.skypro.Marketplace.dto.ad;

import java.util.Objects;



public class AdDTO {
    private Integer pk;
    private String title;
    private Integer price;
    private Integer author;
    private String image;
    private String description;


    public AdDTO(Integer pk, String title, Integer price, Integer author, String image, String description) {
        this.pk = pk;
        this.title = title;
        this.price = price;
        this.author = author;
        this.image = image;
        this.description = description;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdDTO adDTO = (AdDTO) o;
        return Objects.equals(pk, adDTO.pk) && Objects.equals(title, adDTO.title) && Objects.equals(price, adDTO.price) && Objects.equals(author, adDTO.author) && Objects.equals(image, adDTO.image) && Objects.equals(description, adDTO.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pk, title, price, author, image, description);
    }

    @Override
    public String toString() {
        return "AdDTO{" +
                "pk=" + pk +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", author=" + author +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
