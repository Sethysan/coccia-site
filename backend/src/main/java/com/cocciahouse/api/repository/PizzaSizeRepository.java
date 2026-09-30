package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PizzaSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PizzaSizeRepository
        extends JpaRepository<PizzaSize, Long> {

    List<PizzaSize> findByMenuSectionIdOrderByDisplayOrderAsc(
            Long menuSectionId
    );

    List<PizzaSize> findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
            Long menuSectionId
    );

    Optional<PizzaSize> findByIdAndMenuSectionId(
            Long id,
            Long menuSectionId
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
}