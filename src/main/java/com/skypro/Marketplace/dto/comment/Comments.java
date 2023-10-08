package com.skypro.Marketplace.dto.comment;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for representing a list of comments.
 */
public class Comments {

    /**
     * Count of comments in the list.
     */
    private Integer count;

    /**
     * List of comments.
     */
    private List<CommentDTO> results;

    public Comments(Integer count, List<CommentDTO> results) {
        this.count = count;
        this.results = results;
    }

    public Comments() {
    }


    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<CommentDTO> getResults() {
        return results;
    }

    public void setResults(List<CommentDTO> results) {
        this.results = results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comments comments = (Comments) o;
        return Objects.equals(count, comments.count) && Objects.equals(results, comments.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, results);
    }

    @Override
    public String toString() {
        return "Comments{" +
                "count=" + count +
                ", results=" + results +
                '}';
    }
}
