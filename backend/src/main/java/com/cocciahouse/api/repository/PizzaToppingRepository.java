package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PizzaTopping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PizzaToppingRepository
        extends JpaRepository<PizzaTopping, Long> {

    List<PizzaTopping> findByMenuSectionIdOrderByDisplayOrderAsc(
            Long menuSectionId
    );

    List<PizzaTopping>
    findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAsc(
            Long menuSectionId
    );

    Optional<PizzaTopping> findByIdAndMenuSectionId(
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