package com.skypro.Marketplace.dto.user;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) for updating user information.
 */
public class UpdateUser {
    /**
     * The updated first name of the user.
     */
    private String firstName;

    /**
     * The updated last name of the user.
     */
    private String lastName;

    /**
     * The updated phone number of the user.
     */
    private String phone;

    public UpdateUser(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public UpdateUser() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UpdateUser that = (UpdateUser) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName) && Objects.equals(phone, that.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, phone);
    }

    @Override
    public String toString() {
        return "UpdateUser{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
