package com.skypro.Marketplace.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an advertisement in the marketplace.
 */
@Entity
@Table(name = "ad")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "title")
    private String title;

    @Column(name = "price")
    private Integer price;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Lob
    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    public Ad(Integer id, String title, Integer price, String description, List<Comment> comments, User user, String image) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
        this.comments = comments;
        this.user = user;
        this.image = image;
    }

    public Ad() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
        return Objects.equals(id, ad.id) && Objects.equals(title, ad.title) && Objects.equals(price, ad.price) && Objects.equals(description, ad.description) && Objects.equals(comments, ad.comments) && Objects.equals(user, ad.user) && Objects.equals(image, ad.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, price, description, comments, user, image);
    }

    @Override
    public String toString() {
        return "Ad{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", comments=" + comments +
                ", user=" + user +
                ", image='" + image + '\'' +
                '}';
    }
}