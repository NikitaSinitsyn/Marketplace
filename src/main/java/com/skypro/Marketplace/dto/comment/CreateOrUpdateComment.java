package com.skypro.Marketplace.dto.comment;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for creating or updating a comment.
 */
public class CreateOrUpdateComment {

    /**
     * Text content of the comment.
     */
    @NotEmpty(message = "Текст комментария не может быть пустым")
    @Size(min = 8, max = 64, message = "Текст комментария должен содержать от 8 до 64 символов")
    private String text;

    public CreateOrUpdateComment(String text) {
        this.text = text;
    }
    public CreateOrUpdateComment() {

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
        CreateOrUpdateComment that = (CreateOrUpdateComment) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    @Override
    public String toString() {
        return "CreateOrUpdateComment{" +
                "text='" + text + '\'' +
                '}';
    }
}
