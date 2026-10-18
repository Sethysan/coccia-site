package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.model.PurchasingCategory;
import com.cocciahouse.api.repository.PurchasingCategoryRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchasing/categories")
public class AdminPurchasingCategoryController {

    private final PurchasingCategoryRepository categoryRepository;

    public AdminPurchasingCategoryController(
            PurchasingCategoryRepository categoryRepository
    ) {
        this.categoryRepository = categoryRepository;
    }

    public record CategoryResponse(
            Long id,
            Long departmentId,
            String name,
            boolean active
    ) {}

    @GetMapping
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(
            @RequestParam(required = false) Long departmentId
    ) {
        List<PurchasingCategory> categories =
                departmentId == null
                        ? categoryRepository.findAll()
                        : categoryRepository
                        .findByDepartmentIdOrderByNameAsc(departmentId);

        return categories.stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getDepartment().getId(),
                        category.getName(),
                        category.isActive()
                ))
                .sorted((a, b) ->
                        a.name().compareToIgnoreCase(b.name()))
                .toList();
    }
}
