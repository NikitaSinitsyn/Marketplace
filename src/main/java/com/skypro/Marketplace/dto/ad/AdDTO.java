package com.skypro.Marketplace.dto.ad;

import lombok.*;

import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdDTO {
    private Integer author;
    private String image;
    private Integer pk;
    private Integer price;
    private String title;
    private String description;



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
