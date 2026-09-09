package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PizzaAddOn;
import com.cocciahouse.api.model.PizzaAddOnType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PizzaAddOnRepository
        extends JpaRepository<PizzaAddOn, Long> {

    List<PizzaAddOn> findByMenuSectionIdOrderByDisplayOrderAsc(
            Long menuSectionId
    );

    List<PizzaAddOn>
    findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
            Long menuSectionId
    );

    Optional<PizzaAddOn> findByIdAndMenuSectionId(
            Long id,
            Long menuSectionId
    );

    Optional<PizzaAddOn>
    findByMenuSectionIdAndTypeAndActiveTrue(
            Long menuSectionId,
            PizzaAddOnType type
    );

    boolean existsByMenuSectionIdAndNameIgnoreCase(
            Long menuSectionId,
            String name
    );

    boolean existsByMenuSectionIdAndNameIgnoreCaseAndIdNot(
            Long menuSectionId,
            String name,
            Long id
    );

    boolean existsByMenuSectionIdAndTypeAndActiveTrue(
            Long menuSectionId,
            PizzaAddOnType type
    );

    boolean existsByMenuSectionIdAndTypeAndActiveTrueAndIdNot(
            Long menuSectionId,
            PizzaAddOnType type,
            Long id
    );
}