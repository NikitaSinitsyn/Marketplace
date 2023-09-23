package com.skypro.Marketplace.dto.ad;

import lombok.*;

import javax.validation.constraints.*;
import java.util.Objects;


public class CreateOrUpdateAd {

    @NotEmpty(message = "Заголовок объявления не может быть пустым")
    @Size(min = 4, max = 32, message = "Заголовок объявления должен содержать от 4 до 32 символов")
    private String title;

    @NotNull(message = "Цена объявления не может быть пустой")
    @Min(value = 0, message = "Цена объявления не может быть меньше 0")
    @Max(value = 10000000, message = "Цена объявления не может быть больше 10000000")
    private Integer price;

    @NotEmpty(message = "Описание объявления не может быть пустым")
    @Size(min = 8, max = 64, message = "Описание объявления должно содержать от 8 до 64 символов")
    private String description;

    public CreateOrUpdateAd(String title, Integer price, String description) {
        this.title = title;
        this.price = price;
        this.description = description;
    }

    public CreateOrUpdateAd() {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CreateOrUpdateAd that = (CreateOrUpdateAd) o;
        return Objects.equals(title, that.title) && Objects.equals(price, that.price) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, price, description);
    }

    @Override
    public String toString() {
        return "CreateOrUpdateAd{" +
                "title='" + title + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                '}';
    }
}
