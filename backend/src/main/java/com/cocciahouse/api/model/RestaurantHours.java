package com.cocciahouse.api.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.time.LocalTime;

@Entity
@Table(
        name = "restaurant_hours",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_restaurant_hours_day_of_week",
                        columnNames = "day_of_week"
                )
        }
)
public class RestaurantHours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "day_of_week",
            nullable = false,
            unique = true
    )
    private Integer dayOfWeek;

    @Column(
            name = "day_name",
            nullable = false,
            length = 20
    )
    private String dayName;

    @Column(nullable = false)
    private boolean closed;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    @Column(length = 255)
    private String note;

    @Column(
            name = "display_order",
            nullable = false
    )
    private Integer displayOrder;

    @Column(
            name = "updated_at",
            nullable = false,
            insertable = false
    )
    private Instant updatedAt;

    public RestaurantHours() {
    }

    public Long getId() {
        return id;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public LocalTime getOpenTime() {
        return openTime;
    }

    public void setOpenTime(LocalTime openTime) {
        this.openTime = openTime;
    }

    public LocalTime getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(LocalTime closeTime) {
        this.closeTime = closeTime;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}