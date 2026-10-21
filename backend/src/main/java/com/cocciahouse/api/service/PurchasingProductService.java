
package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.purchasing.PurchasingProductRequest;
import com.cocciahouse.api.dto.purchasing.PurchasingProductResponse;
import com.cocciahouse.api.model.PurchasingCategory;
import com.cocciahouse.api.model.PurchasingDepartment;
import com.cocciahouse.api.model.PurchasingProduct;
import com.cocciahouse.api.repository.PurchasingCategoryRepository;
import com.cocciahouse.api.repository.PurchasingDepartmentRepository;
import com.cocciahouse.api.repository.PurchasingProductRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.cocciahouse.api.model.PurchasingInventoryBalance;
import com.cocciahouse.api.repository.PurchasingInventoryBalanceRepository;

import java.util.List;

@Service
public class PurchasingProductService {

    private final PurchasingProductRepository productRepository;
    private final PurchasingDepartmentRepository departmentRepository;
    private final PurchasingCategoryRepository categoryRepository;
    private final PurchasingInventoryBalanceRepository inventoryBalanceRepository;
    private final InventoryUnitConversionService unitConversionService;

    @PersistenceContext
    private EntityManager entityManager;


    public PurchasingProductService(
            PurchasingProductRepository productRepository,
            PurchasingDepartmentRepository departmentRepository,
            PurchasingCategoryRepository categoryRepository,
            PurchasingInventoryBalanceRepository inventoryBalanceRepository,
            InventoryUnitConversionService unitConversionService

    ) {
        this.productRepository = productRepository;
        this.departmentRepository = departmentRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryBalanceRepository = inventoryBalanceRepository;
        this.unitConversionService = unitConversionService;

    }

    @Transactional(readOnly = true)
    public List<PurchasingProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PurchasingProductResponse getProduct(Long id) {
        PurchasingProduct product = findProduct(id);
        return toResponse(product);
    }

    @Transactional
    public PurchasingProductResponse createProduct(
            PurchasingProductRequest request
    ) {
        validateName(request.name());

        PurchasingDepartment department =
                findDepartment(request.departmentId());

        PurchasingCategory category =
                findCategory(request.categoryId());

        validateCategoryDepartment(category, department);

        PurchasingProduct product = new PurchasingProduct();

        applyRequest(product, request, department, category);

        PurchasingProduct savedProduct =
                productRepository.saveAndFlush(product);

        inventoryBalanceRepository.save(
                new PurchasingInventoryBalance(savedProduct.getId())
        );

        entityManager.refresh(savedProduct);
        return toResponse(savedProduct);
    }

    @Transactional
    public PurchasingProductResponse updateProduct(
            Long id,
            PurchasingProductRequest request
    ) {
        PurchasingProduct product = findProduct(id);

        validateName(request.name());

        PurchasingDepartment department =
                findDepartment(request.departmentId());

        PurchasingCategory category =
                findCategory(request.categoryId());

        validateCategoryDepartment(category, department);

        applyRequest(product, request, department, category);

        PurchasingProduct savedProduct =
                productRepository.saveAndFlush(product);

        entityManager.refresh(savedProduct);

        return toResponse(savedProduct);
    }

    private PurchasingProduct findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + id
                ));
    }

    private PurchasingDepartment findDepartment(Long id) {
        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Department is required"
            );
        }

        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid department: " + id
                ));
    }

    private PurchasingCategory findCategory(Long id) {
        if (id == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category is required"
            );
        }

        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid category: " + id
                ));
    }

    private void validateCategoryDepartment(
            PurchasingCategory category,
            PurchasingDepartment department
    ) {
        if (!category.getDepartment().getId().equals(department.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Category does not belong to the selected department"
            );
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product name is required"
            );
        }

        if (name.trim().length() > 200) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product name cannot exceed 200 characters"
            );
        }
    }

    private void applyRequest(
            PurchasingProduct product,
            PurchasingProductRequest request,
            PurchasingDepartment department,
            PurchasingCategory category
    ) {
        product.setDepartment(department);
        product.setCategory(category);
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setActive(request.active());

        String baseUnit = request.baseUnit();

        if (baseUnit == null || baseUnit.isBlank()) {
            // Existing products may remain unconfigured temporarily.
            // Do not allow clearing an already configured unit.
            if (product.getBaseUnit() != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Base inventory unit cannot be cleared"
                );
            }

            product.setBaseUnit(null);
        } else {
            baseUnit = baseUnit.trim().toLowerCase(java.util.Locale.ROOT);

            try {
                // Validates that the unit is supported.
                unitConversionService.convert(
                        java.math.BigDecimal.ONE,
                        baseUnit,
                        baseUnit
                );
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage()
                );
            }

            if (product.getBaseUnit() != null
                    && !product.getBaseUnit().equals(baseUnit)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Changing an existing inventory base unit "
                                + "requires an inventory conversion process"
                );
            }

            product.setBaseUnit(baseUnit);
        }

    }

    private PurchasingProductResponse toResponse(
            PurchasingProduct product
    ) {
        return new PurchasingProductResponse(
                product.getId(),
                product.getDepartment().getId(),
                product.getCategory().getId(),
                product.getName(),
                product.getDescription(),
                product.isActive(),
                product.getBaseUnit(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}
