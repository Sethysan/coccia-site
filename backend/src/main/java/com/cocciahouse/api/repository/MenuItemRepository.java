package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long> {

    @Query("""
            SELECT DISTINCT menuItem
            FROM MenuItem menuItem
            JOIN FETCH menuItem.recipe
            LEFT JOIN FETCH menuItem.prices
            WHERE menuItem.menuSection.id = :menuSectionId
            ORDER BY menuItem.displayOrder
            """)
    List<MenuItem> findByMenuSectionIdOrderByDisplayOrderAsc(
            @Param("menuSectionId") Long menuSectionId
    );

    @Query("""
            SELECT DISTINCT menuItem
            FROM MenuItem menuItem
            JOIN FETCH menuItem.recipe
            LEFT JOIN FETCH menuItem.prices
            WHERE menuItem.menuSection.id = :menuSectionId
              AND menuItem.visible = true
            ORDER BY menuItem.displayOrder
            """)
    List<MenuItem> findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
            @Param("menuSectionId") Long menuSectionId
    );

    Optional<MenuItem>
    findByMenuSectionIdAndRecipeId(
            Long menuSectionId,
            Long recipeId
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

    Optional<MenuItem>
    findByIdAndMenuSectionId(
            Long id,
            Long menuSectionId
    );
}