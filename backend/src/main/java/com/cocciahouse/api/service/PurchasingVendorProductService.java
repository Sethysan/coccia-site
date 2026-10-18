
package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.purchasing.PurchasingVendorProductRequest;
import com.cocciahouse.api.dto.purchasing.PurchasingVendorProductResponse;
import com.cocciahouse.api.model.PurchasingProduct;
import com.cocciahouse.api.model.PurchasingVendor;
import com.cocciahouse.api.model.PurchasingVendorProduct;
import com.cocciahouse.api.repository.PurchasingProductRepository;
import com.cocciahouse.api.repository.PurchasingVendorRepository;
import com.cocciahouse.api.repository.PurchasingVendorProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PurchasingVendorProductService {

    private final PurchasingVendorProductRepository offeringRepository;
    private final PurchasingProductRepository productRepository;
    private final PurchasingVendorRepository vendorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PurchasingVendorProductService(
            PurchasingVendorProductRepository offeringRepository,
            PurchasingProductRepository productRepository,
            PurchasingVendorRepository vendorRepository
    ) {
        this.offeringRepository = offeringRepository;
        this.productRepository = productRepository;
        this.vendorRepository = vendorRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchasingVendorProductResponse> getAllOfferings() {
        return offeringRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PurchasingVendorProductResponse> getOfferingsForProduct(
            Long productId
    ) {
        findProduct(productId);

        return offeringRepository.findByProductIdOrderByIdAsc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchasingVendorProductResponse getOffering(Long id) {
        return toResponse(findOffering(id));
    }

    @Transactional
    public PurchasingVendorProductResponse createOffering(
            PurchasingVendorProductRequest request
    ) {
        PurchasingProduct product = findProduct(request.productId());
        PurchasingVendor vendor = findVendor(request.vendorId());

        PurchasingVendorProduct offering = new PurchasingVendorProduct();

        applyRequest(offering, request, product, vendor);

        if (request.preferred()) {
            clearPreferredOffering(product.getId(), null);
        }

        offering.setPreferred(request.preferred());

        PurchasingVendorProduct saved =
                offeringRepository.saveAndFlush(offering);

        entityManager.refresh(saved);

        return toResponse(saved);
    }

    @Transactional
    public PurchasingVendorProductResponse updateOffering(
            Long id,
            PurchasingVendorProductRequest request
    ) {
        PurchasingVendorProduct offering = findOffering(id);

        PurchasingProduct product = findProduct(request.productId());
        PurchasingVendor vendor = findVendor(request.vendorId());

        if (request.active() && request.preferred()) {
            clearPreferredOffering(product.getId(), id);
        }

        applyRequest(offering, request, product, vendor);
        offering.setPreferred(request.active() && request.preferred());

        PurchasingVendorProduct saved =
                offeringRepository.saveAndFlush(offering);

        entityManager.refresh(saved);

        return toResponse(saved);
    }

    private void clearPreferredOffering(Long productId, Long excludeId) {
        List<PurchasingVendorProduct> offerings =
                offeringRepository.findByProductIdOrderByIdAsc(productId);

        for (PurchasingVendorProduct existing : offerings) {
            if (existing.isPreferred()
                    && !existing.getId().equals(excludeId)) {
                existing.setPreferred(false);
                offeringRepository.saveAndFlush(existing);
            }
        }
    }

    private void applyRequest(
            PurchasingVendorProduct offering,
            PurchasingVendorProductRequest request,
            PurchasingProduct product,
            PurchasingVendor vendor
    ) {
        offering.setProduct(product);
        offering.setVendor(vendor);
        offering.setVendorSku(request.vendorSku());
        offering.setOrderUnit(request.orderUnit().trim());
        offering.setPackageQuantity(request.packageQuantity());
        offering.setPackageUnit(request.packageUnit().trim());
        offering.setUnitPrice(request.unitPrice());
        offering.setActive(request.active());
    }

    private PurchasingProduct findProduct(Long id) {
        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product is required"
            );
        }

        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + id
                ));
    }

    private PurchasingVendor findVendor(Long id) {
        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vendor is required"
            );
        }

        return vendorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vendor not found: " + id
                ));
    }

    private PurchasingVendorProduct findOffering(Long id) {
        return offeringRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vendor offering not found: " + id
                ));
    }

    private PurchasingVendorProductResponse toResponse(
            PurchasingVendorProduct offering
    ) {
        return new PurchasingVendorProductResponse(
                offering.getId(),
                offering.getProduct().getId(),
                offering.getVendor().getId(),
                offering.getVendorSku(),
                offering.getOrderUnit(),
                offering.getPackageQuantity(),
                offering.getPackageUnit(),
                offering.getUnitPrice(),
                offering.isPreferred(),
                offering.isActive(),
                offering.getCreatedAt(),
                offering.getUpdatedAt()
        );
    }
}
