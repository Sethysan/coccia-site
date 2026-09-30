package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByNameIgnoreCase(String name);

    List<Recipe> findAllByActiveTrueOrderByNameAsc();

    List<Recipe> findByActiveTrueAndNameContainingIgnoreCaseOrderByNameAsc(String search);

    List<Recipe> findAllByOrderByNameAsc();

    List<Recipe> findByNameContainingIgnoreCaseOrderByNameAsc(String search);

    boolean existsByNameIgnoreCase(String name);
}