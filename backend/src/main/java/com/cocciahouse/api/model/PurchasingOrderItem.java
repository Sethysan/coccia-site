package com.cocciahouse.api.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchasing_order_items")
public class PurchasingOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private Long orderId;

    @Column(name = "vendor_product_id", nullable = false)
    private Long vendorProductId;

    @Column(name = "planned_quantity", nullable = false)
    private BigDecimal plannedQuantity;

    @Column(name = "estimated_unit_price")
    private BigDecimal estimatedUnitPrice;

    @Column(name = "confirmed_quantity")
    private BigDecimal confirmedQuantity;

    @Column(name = "confirmed_unit_price")
    private BigDecimal confirmedUnitPrice;

    @Column(name = "order_unit", nullable = false)
    private String orderUnit;

    @Column(name = "package_quantity", nullable = false)
    private BigDecimal packageQuantity;

    @Column(name = "package_unit", nullable = false)
    private String packageUnit;

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getVendorProductId() {
        return vendorProductId;
    }

    public void setVendorProductId(Long vendorProductId) {
        this.vendorProductId = vendorProductId;
    }

    public BigDecimal getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(BigDecimal plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public BigDecimal getEstimatedUnitPrice() {
        return estimatedUnitPrice;
    }

    public void setEstimatedUnitPrice(BigDecimal estimatedUnitPrice) {
        this.estimatedUnitPrice = estimatedUnitPrice;
    }

    public BigDecimal getConfirmedQuantity() {
        return confirmedQuantity;
    }

    public void setConfirmedQuantity(BigDecimal confirmedQuantity) {
        this.confirmedQuantity = confirmedQuantity;
    }

    public BigDecimal getConfirmedUnitPrice() {
        return confirmedUnitPrice;
    }

    public void setConfirmedUnitPrice(BigDecimal confirmedUnitPrice) {
        this.confirmedUnitPrice = confirmedUnitPrice;
    }

    public String getOrderUnit() {
        return orderUnit;
    }

    public void setOrderUnit(String orderUnit) {
        this.orderUnit = orderUnit;
    }

    public BigDecimal getPackageQuantity() {
        return packageQuantity;
    }

    public void setPackageQuantity(BigDecimal packageQuantity) {
        this.packageQuantity = packageQuantity;
    }

    public String getPackageUnit() {
        return packageUnit;
    }

    public void setPackageUnit(String packageUnit) {
        this.packageUnit = packageUnit;
    }
}