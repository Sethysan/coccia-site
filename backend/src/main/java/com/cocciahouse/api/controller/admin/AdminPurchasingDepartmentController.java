
package com.cocciahouse.api.controller.admin;

import com.cocciahouse.api.repository.PurchasingDepartmentRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/purchasing/departments")
public class AdminPurchasingDepartmentController {

    private final PurchasingDepartmentRepository departmentRepository;

    public AdminPurchasingDepartmentController(
            PurchasingDepartmentRepository departmentRepository
    ) {
        this.departmentRepository = departmentRepository;
    }

    public record DepartmentResponse(
            Long id,
            String name,
            boolean active
    ) {}

    @GetMapping
    public List<DepartmentResponse> getDepartments() {
        return departmentRepository.findAll()
                .stream()
                .map(department -> new DepartmentResponse(
                        department.getId(),
                        department.getName(),
                        department.isActive()
                ))
                .sorted((a, b) ->
                        a.name().compareToIgnoreCase(b.name()))
                .toList();
    }
}
