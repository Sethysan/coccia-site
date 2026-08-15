package com.cocciahouse.api.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "weekly_offering_items")
public class WeeklyOfferingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "weekly_offering_id", nullable = false)
    private WeeklyOffering weeklyOffering;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @Enumerated(EnumType.STRING)
    @Column(name = "offering_type", nullable = false, length = 20)
    private OfferingType offeringType;

    @Column(name = "public_description")
    private String publicDescription;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "image_alt", length = 255)
    private String imageAlt;

    @Column(name = "includes_house_salad", nullable = false)
    private boolean includesHouseSalad = false;

    @Column(name = "includes_homemade_bread", nullable = false)
    private boolean includesHomemadeBread = false;

    @Column(name = "display_order", nullable = false)
    private int displayOrder = 0;

    @Column(name = "created_at", nullable = false, insertable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false, insertable = false)
    private Instant updatedAt;

    @OneToMany(
            mappedBy = "weeklyOfferingItem",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("displayOrder ASC")
    private List<WeeklyOfferingItemPrice> prices = new ArrayList<>();

    public WeeklyOfferingItem() {
    }

    public Long getId() {
        return id;
    }

    public WeeklyOffering getWeeklyOffering() {
        return weeklyOffering;
    }

    public void setWeeklyOffering(WeeklyOffering weeklyOffering) {
        this.weeklyOffering = weeklyOffering;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public OfferingType getOfferingType() {
        return offeringType;
    }

    public void setOfferingType(OfferingType offeringType) {
        this.offeringType = offeringType;
    }

    public String getPublicDescription() {
        return publicDescription;
    }

    public void setPublicDescription(String publicDescription) {
        this.publicDescription = publicDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageAlt() {
        return imageAlt;
    }

    public void setImageAlt(String imageAlt) {
        this.imageAlt = imageAlt;
    }

    public boolean isIncludesHouseSalad() {
        return includesHouseSalad;
    }

    public void setIncludesHouseSalad(boolean includesHouseSalad) {
        this.includesHouseSalad = includesHouseSalad;
    }

    public boolean isIncludesHomemadeBread() {
        return includesHomemadeBread;
    }

    public void setIncludesHomemadeBread(boolean includesHomemadeBread) {
        this.includesHomemadeBread = includesHomemadeBread;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public List<WeeklyOfferingItemPrice> getPrices() {
        return prices;
    }

    public void addPrice(WeeklyOfferingItemPrice price) {
        prices.add(price);
        price.setWeeklyOfferingItem(this);
    }

    public void removePrice(WeeklyOfferingItemPrice price) {
        prices.remove(price);
        price.setWeeklyOfferingItem(null);
    }
}