
package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.purchasing.PurchasingVendorResponse;
import com.cocciahouse.api.model.PurchasingVendor;
import com.cocciahouse.api.dto.purchasing.PurchasingVendorRequest;
import com.cocciahouse.api.repository.PurchasingVendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PurchasingVendorService {

    private final PurchasingVendorRepository vendorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public PurchasingVendorService(PurchasingVendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @Transactional(readOnly = true)
    public List<PurchasingVendorResponse> getAllVendors() {
        return vendorRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchasingVendorResponse getVendor(Long id) {
        PurchasingVendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vendor not found: " + id
                ));

        return toResponse(vendor);
    }


    @Transactional
    public PurchasingVendorResponse createVendor(PurchasingVendorRequest request) {

        String name = validateVendorRequest(request);

        if (vendorRepository.existsByNameIgnoreCase(name)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A vendor with this name already exists"
            );
        }

        PurchasingVendor vendor = new PurchasingVendor();

        vendor.setName(name);
        vendor.setPhone(request.phone());
        vendor.setWebsite(request.website());
        vendor.setPreferredDeliveryDays(request.preferredDeliveryDays());
        vendor.setNextDeliveryDate(request.nextDeliveryDate());
        vendor.setDeliveryFrequency(request.deliveryFrequency());
        vendor.setNotes(request.notes());

        vendor.setMinimumOrderAmount(request.minimumOrderAmount());
        vendor.setMinimumOrderQuantity(request.minimumOrderQuantity());
        vendor.setMinimumOrderQuantityUnit(request.minimumOrderQuantityUnit());
        vendor.setBelowMinimumFee(request.belowMinimumFee());
        vendor.setOffScheduleDeliveryFee(request.offScheduleDeliveryFee());

        vendor.setActive(request.active());

        PurchasingVendor savedVendor = vendorRepository.saveAndFlush(vendor);
        entityManager.refresh(savedVendor);
        return toResponse(savedVendor);
    }


    @Transactional
    public PurchasingVendorResponse updateVendor(
            Long id,
            PurchasingVendorRequest request
    ) {
        PurchasingVendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vendor not found: " + id
                ));

        String name = validateVendorRequest(request);

        if (vendorRepository.existsByNameIgnoreCaseAndIdNot(name, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A different vendor already uses this name"
            );
        }

        vendor.setName(name);
        vendor.setPhone(request.phone());
        vendor.setWebsite(request.website());
        vendor.setPreferredDeliveryDays(request.preferredDeliveryDays());
        vendor.setNextDeliveryDate(request.nextDeliveryDate());
        vendor.setDeliveryFrequency(request.deliveryFrequency());
        vendor.setNotes(request.notes());

        vendor.setMinimumOrderAmount(request.minimumOrderAmount());
        vendor.setMinimumOrderQuantity(request.minimumOrderQuantity());
        vendor.setMinimumOrderQuantityUnit(request.minimumOrderQuantityUnit());
        vendor.setBelowMinimumFee(request.belowMinimumFee());
        vendor.setOffScheduleDeliveryFee(request.offScheduleDeliveryFee());

        vendor.setActive(request.active());

        PurchasingVendor savedVendor = vendorRepository.saveAndFlush(vendor);
        entityManager.refresh(savedVendor);

        return toResponse(savedVendor);
    }


    private String validateVendorRequest(PurchasingVendorRequest request) {

        if (request.name() == null || request.name().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vendor name is required"
            );
        }

        if (request.minimumOrderQuantity() != null) {
            if (request.minimumOrderQuantityUnit() == null ||
                    request.minimumOrderQuantityUnit().isBlank()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "A quantity unit is required when a minimum order quantity is set"
                );
            }
        } else if (request.minimumOrderQuantityUnit() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A minimum order quantity is required when a quantity unit is set"
            );
        }

        return request.name().trim();
    }

    private PurchasingVendorResponse toResponse(PurchasingVendor vendor) {
        return new PurchasingVendorResponse(
                vendor.getId(),
                vendor.getName(),
                vendor.getPhone(),
                vendor.getWebsite(),
                vendor.getPreferredDeliveryDays(),
                vendor.getNextDeliveryDate(),
                vendor.getDeliveryFrequency(),
                vendor.getNotes(),
                vendor.getMinimumOrderAmount(),
                vendor.getMinimumOrderQuantity(),
                vendor.getMinimumOrderQuantityUnit(),
                vendor.getBelowMinimumFee(),
                vendor.getOffScheduleDeliveryFee(),
                vendor.isActive(),
                vendor.getCreatedAt(),
                vendor.getUpdatedAt()
        );
    }
}
