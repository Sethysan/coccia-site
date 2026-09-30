package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.PizzaSpecialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PizzaSpecialtyRepository
        extends JpaRepository<PizzaSpecialty, Long> {

    @Query("""
            SELECT DISTINCT specialty
            FROM PizzaSpecialty specialty
            JOIN FETCH specialty.recipe
            LEFT JOIN FETCH specialty.prices price
            LEFT JOIN FETCH price.pizzaSize
            WHERE specialty.menuSection.id = :menuSectionId
            ORDER BY specialty.displayOrder
            """)
    List<PizzaSpecialty> findByMenuSectionIdWithDetails(
            @Param("menuSectionId") Long menuSectionId
    );

    @Query("""
            SELECT DISTINCT specialty
            FROM PizzaSpecialty specialty
            JOIN FETCH specialty.recipe
            LEFT JOIN FETCH specialty.prices price
            LEFT JOIN FETCH price.pizzaSize
            WHERE specialty.menuSection.id = :menuSectionId
              AND specialty.active = true
            ORDER BY specialty.displayOrder
            """)
    List<PizzaSpecialty> findActiveByMenuSectionIdWithDetails(
            @Param("menuSectionId") Long menuSectionId
    );

    @Query("""
            SELECT DISTINCT specialty
            FROM PizzaSpecialty specialty
            JOIN FETCH specialty.recipe
            LEFT JOIN FETCH specialty.prices price
            LEFT JOIN FETCH price.pizzaSize
            WHERE specialty.id = :specialtyId
              AND specialty.menuSection.id = :menuSectionId
            """)
    Optional<PizzaSpecialty> findByIdAndMenuSectionIdWithDetails(
            @Param("specialtyId") Long specialtyId,
            @Param("menuSectionId") Long menuSectionId
    );

    boolean existsByMenuSectionIdAndRecipeId(
            Long menuSectionId,
            Long recipeId
    );

    boolean existsByMenuSectionIdAndRecipeIdAndIdNot(
            Long menuSectionId,
            Long recipeId,
            Long id
    );
}