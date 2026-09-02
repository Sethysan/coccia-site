package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long> {

    List<MenuItem>
    findByMenuSectionIdOrderByDisplayOrderAsc(
            Long menuSectionId
    );

    List<MenuItem>
    findByMenuSectionIdAndVisibleTrueOrderByDisplayOrderAsc(
            Long menuSectionId
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