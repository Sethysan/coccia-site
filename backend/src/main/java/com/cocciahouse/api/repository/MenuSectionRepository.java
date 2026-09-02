package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.MenuSection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuSectionRepository
        extends JpaRepository<MenuSection, Long> {

    List<MenuSection> findAllByOrderByDisplayOrderAsc();

    List<MenuSection>
    findByActiveTrueOrderByDisplayOrderAsc();

    Optional<MenuSection> findByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCase(
            String name
    );

    boolean existsByNameIgnoreCaseAndIdNot(
            String name,
            Long id
    );

}