package com.cocciahouse.api.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "staff_member_recipes")
public class StaffMemberRecipe {

    @EmbeddedId
    private StaffMemberRecipeId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("staffMemberId")
    @JoinColumn(name = "staff_member_id", nullable = false)
    private StaffMember staffMember;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("recipeId")
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    public StaffMemberRecipe() {
    }

    public StaffMemberRecipe(StaffMember staffMember, Recipe recipe) {
        this.staffMember = staffMember;
        this.recipe = recipe;

        if (staffMember.getId() != null && recipe.getId() != null) {
            this.id = new StaffMemberRecipeId(
                    staffMember.getId(),
                    recipe.getId()
            );
        }
    }

    public StaffMemberRecipeId getId() {
        return id;
    }

    public void setId(StaffMemberRecipeId id) {
        this.id = id;
    }

    public StaffMember getStaffMember() {
        return staffMember;
    }

    public void setStaffMember(StaffMember staffMember) {
        this.staffMember = staffMember;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}