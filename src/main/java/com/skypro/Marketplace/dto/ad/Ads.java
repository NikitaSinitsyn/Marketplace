package com.skypro.Marketplace.dto.ad;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for representing a list of advertisements.
 */
public class Ads {

    /**
     * Count of advertisements in the list.
     */
    private Integer count;

    /**
     * List of advertisements.
     */
    private List<AdDTO> results;

    public Ads(Integer count, List<AdDTO> results) {
        this.count = count;
        this.results = results;
    }

    public Ads() {
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<AdDTO> getResults() {
        return results;
    }

    public void setResults(List<AdDTO> results) {
        this.results = results;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ads ads = (Ads) o;
        return Objects.equals(count, ads.count) && Objects.equals(results, ads.results);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, results);
    }

    @Override
    public String toString() {
        return "Ads{" +
                "count=" + count +
                ", results=" + results +
                '}';
    }
}
