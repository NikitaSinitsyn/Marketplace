package com.skypro.Marketplace.dto.user;

import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) for updating a user's password.
 */
public class NewPassword {

    /**
     * The current password of the user.
     */
    @Size(min = 8, max = 16, message = "Длина текущего пароля должна быть от 8 до 16 символов")
    private String currentPassword;

    /**
     * The new password to set for the user.
     */
    @Size(min = 8, max = 16, message = "Длина нового пароля должна быть от 8 до 16 символов")
    private String newPassword;

    public NewPassword(String currentPassword, String newPassword) {
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public NewPassword() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewPassword that = (NewPassword) o;
        return Objects.equals(currentPassword, that.currentPassword) && Objects.equals(newPassword, that.newPassword);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentPassword, newPassword);
    }

    @Override
    public String toString() {
        return "NewPassword{" +
                "currentPassword='" + currentPassword + '\'' +
                ", newPassword='" + newPassword + '\'' +
                '}';
    }
}
