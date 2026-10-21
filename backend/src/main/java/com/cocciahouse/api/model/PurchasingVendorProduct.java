
package com.cocciahouse.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.PreUpdate;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "purchasing_vendor_products")
public class PurchasingVendorProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private PurchasingProduct product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vendor_id", nullable = false)
    private PurchasingVendor vendor;

    @Column(name = "vendor_sku", length = 100)
    private String vendorSku;

    @Column(name = "order_unit", nullable = false, length = 50)
    private String orderUnit;

    @Column(
            name = "package_quantity",
            nullable = false,
            precision = 12,
            scale = 3
    )
    private BigDecimal packageQuantity = BigDecimal.ONE;

    @Column(name = "package_unit", nullable = false, length = 50)
    private String packageUnit;

    @Column(name = "unit_price", precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false)
    private boolean preferred = false;

    @Column(nullable = false)
    private boolean active = true;

    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private Instant createdAt;

    @Column(
            name = "updated_at",
            nullable = false,
            insertable = false
    )
    private Instant updatedAt;

    @PreUpdate
    public void updateTimestamp() {
        this.updatedAt = Instant.now();
    }

    public PurchasingVendorProduct() {
    }

    public Long getId() {
        return id;
    }

    public PurchasingProduct getProduct() {
        return product;
    }

    public void setProduct(PurchasingProduct product) {
        this.product = product;
    }

    public PurchasingVendor getVendor() {
        return vendor;
    }

    public void setVendor(PurchasingVendor vendor) {
        this.vendor = vendor;
    }

    public String getVendorSku() {
        return vendorSku;
    }

    public void setVendorSku(String vendorSku) {
        this.vendorSku = vendorSku;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public boolean isPreferred() {
        return preferred;
    }

    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
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
