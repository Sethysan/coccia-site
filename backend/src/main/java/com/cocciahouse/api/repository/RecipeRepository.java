package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeRepository
        extends JpaRepository<Recipe, Long> {

    List<Recipe> findAllByActiveTrueOrderByNameAsc();

    List<Recipe> findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(
            String search
    );

    boolean existsByNameIgnoreCase(String name);
}