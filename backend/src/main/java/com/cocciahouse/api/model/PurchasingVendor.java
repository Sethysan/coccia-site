
package com.cocciahouse.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;

import java.math.BigDecimal;

import java.time.Instant;

@Entity
@Table(name = "purchasing_vendors")
public class PurchasingVendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(length = 30)
    private String phone;

    @Column(length = 500)
    private String website;

    @Column(name = "preferred_delivery_days", length = 150)
    private String preferredDeliveryDays;

    @Column(name = "next_delivery_date")
    private LocalDate nextDeliveryDate;

    @Column(name = "delivery_frequency", length = 20)
    private String deliveryFrequency;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "minimum_order_amount", precision = 12, scale = 2)
    private BigDecimal minimumOrderAmount;

    @Column(name = "minimum_order_quantity", precision = 12, scale = 3)
    private BigDecimal minimumOrderQuantity;

    @Column(name = "minimum_order_quantity_unit", length = 50)
    private String minimumOrderQuantityUnit;

    @Column(name = "below_minimum_fee", precision = 12, scale = 2)
    private BigDecimal belowMinimumFee;

    @Column(name = "off_schedule_delivery_fee", precision = 12, scale = 2)
    private BigDecimal offScheduleDeliveryFee;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false,
            insertable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public PurchasingVendor() {
    }

    public PurchasingVendor(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPreferredDeliveryDays() {
        return preferredDeliveryDays;
    }

    public void setPreferredDeliveryDays(String preferredDeliveryDays) {
        this.preferredDeliveryDays = preferredDeliveryDays;
    }

    public LocalDate getNextDeliveryDate() {
        return nextDeliveryDate;
    }

    public void setNextDeliveryDate(LocalDate nextDeliveryDate) {
        this.nextDeliveryDate = nextDeliveryDate;
    }

    public String getDeliveryFrequency() {
        return deliveryFrequency;
    }

    public void setDeliveryFrequency(String deliveryFrequency) {
        this.deliveryFrequency = deliveryFrequency;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public BigDecimal getMinimumOrderAmount() {
        return minimumOrderAmount;
    }

    public void setMinimumOrderAmount(BigDecimal minimumOrderAmount) {
        this.minimumOrderAmount = minimumOrderAmount;
    }

    public BigDecimal getMinimumOrderQuantity() {
        return minimumOrderQuantity;
    }

    public void setMinimumOrderQuantity(BigDecimal minimumOrderQuantity) {
        this.minimumOrderQuantity = minimumOrderQuantity;
    }

    public String getMinimumOrderQuantityUnit() {
        return minimumOrderQuantityUnit;
    }

    public void setMinimumOrderQuantityUnit(String minimumOrderQuantityUnit) {
        this.minimumOrderQuantityUnit = minimumOrderQuantityUnit;
    }

    public BigDecimal getBelowMinimumFee() {
        return belowMinimumFee;
    }

    public void setBelowMinimumFee(BigDecimal belowMinimumFee) {
        this.belowMinimumFee = belowMinimumFee;
    }

    public BigDecimal getOffScheduleDeliveryFee() {
        return offScheduleDeliveryFee;
    }

    public void setOffScheduleDeliveryFee(BigDecimal offScheduleDeliveryFee) {
        this.offScheduleDeliveryFee = offScheduleDeliveryFee;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
