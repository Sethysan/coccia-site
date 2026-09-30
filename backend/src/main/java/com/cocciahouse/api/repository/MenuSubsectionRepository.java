package com.cocciahouse.api.repository;

import com.cocciahouse.api.model.MenuSubsection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MenuSubsectionRepository
        extends JpaRepository<MenuSubsection, Long> {

    List<MenuSubsection>
    findByMenuSectionIdOrderByDisplayOrderAscIdAsc(
            Long menuSectionId
    );

    List<MenuSubsection>
    findByMenuSectionIdAndActiveTrueOrderByDisplayOrderAscIdAsc(
            Long menuSectionId
    );

    Optional<MenuSubsection>
    findByIdAndMenuSectionId(
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