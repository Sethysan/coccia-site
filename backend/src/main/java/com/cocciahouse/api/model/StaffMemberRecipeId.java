package com.cocciahouse.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class StaffMemberRecipeId implements Serializable {

    @Column(name = "staff_member_id")
    private Long staffMemberId;

    @Column(name = "recipe_id")
    private Long recipeId;

    public StaffMemberRecipeId() {
    }

    public StaffMemberRecipeId(Long staffMemberId, Long recipeId) {
        this.staffMemberId = staffMemberId;
        this.recipeId = recipeId;
    }

    public Long getStaffMemberId() {
        return staffMemberId;
    }

    public void setStaffMemberId(Long staffMemberId) {
        this.staffMemberId = staffMemberId;
    }

    public Long getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Long recipeId) {
        this.recipeId = recipeId;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof StaffMemberRecipeId other)) {
            return false;
        }

        return Objects.equals(staffMemberId, other.staffMemberId)
                && Objects.equals(recipeId, other.recipeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffMemberId, recipeId);
    }
}