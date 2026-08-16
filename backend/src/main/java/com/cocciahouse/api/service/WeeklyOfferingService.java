package com.cocciahouse.api.service;

import com.cocciahouse.api.dto.WeeklyOfferingCreateRequest;
import com.cocciahouse.api.exception.DuplicateOfferingItemException;
import com.cocciahouse.api.model.WeeklyOffering;
import com.cocciahouse.api.dto.WeeklyOfferingResponse;
import com.cocciahouse.api.mapper.WeeklyOfferingMapper;
import com.cocciahouse.api.model.WeeklyOfferingStatus;
import com.cocciahouse.api.repository.WeeklyOfferingItemRepository;
import com.cocciahouse.api.repository.WeeklyOfferingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cocciahouse.api.dto.OfferingItemPriceRequest;
import com.cocciahouse.api.dto.WeeklyOfferingItemCreateRequest;
import com.cocciahouse.api.model.Recipe;
import com.cocciahouse.api.model.WeeklyOfferingItem;
import com.cocciahouse.api.model.WeeklyOfferingItemPrice;
import com.cocciahouse.api.repository.RecipeRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class WeeklyOfferingService {

    private final WeeklyOfferingRepository weeklyOfferingRepository;
    private final WeeklyOfferingMapper weeklyOfferingMapper;

    private final RecipeRepository recipeRepository;

    private final WeeklyOfferingItemRepository weeklyOfferingItemRepository;

    public WeeklyOfferingService(
            WeeklyOfferingRepository weeklyOfferingRepository,
            WeeklyOfferingMapper weeklyOfferingMapper,
            RecipeRepository recipeRepository,
            WeeklyOfferingItemRepository weeklyOfferingItemRepository

    ) {
        this.weeklyOfferingRepository = weeklyOfferingRepository;
        this.weeklyOfferingMapper = weeklyOfferingMapper;
        this.recipeRepository = recipeRepository;
        this.weeklyOfferingItemRepository = weeklyOfferingItemRepository;
    }

    @Transactional(readOnly = true)
    public Optional<WeeklyOfferingResponse> getCurrentOffering() {

        LocalDate today = LocalDate.now();

        return weeklyOfferingRepository
                .findFirstByStatusInAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByStartDateDesc(
                        List.of(
                                WeeklyOfferingStatus.SCHEDULED,
                                WeeklyOfferingStatus.PUBLISHED
                        ),
                        today,
                        today
                )
                .map(weeklyOfferingMapper::toResponse);
    }

    @Transactional
    public WeeklyOfferingResponse createOffering(
            WeeklyOfferingCreateRequest request
    ) {

        if (request.endDate().isBefore(request.startDate())) {
            throw new IllegalArgumentException(
                    "End date cannot be before start date."
            );
        }

        WeeklyOffering weeklyOffering = new WeeklyOffering();

        weeklyOffering.setStartDate(request.startDate());
        weeklyOffering.setEndDate(request.endDate());
        weeklyOffering.setStatus(WeeklyOfferingStatus.DRAFT);

        WeeklyOffering savedOffering =
                weeklyOfferingRepository.save(weeklyOffering);

        return weeklyOfferingMapper.toResponse(savedOffering);
    }

    @Transactional
    public WeeklyOfferingResponse updateOfferingDates(
            Long offeringId,
            WeeklyOfferingCreateRequest request
    ) {

        WeeklyOffering offering = weeklyOfferingRepository
                .findById(offeringId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering not found."
                        )
                );

        validateOfferingIsEditable(offering);

        if (request.startDate().isAfter(request.endDate())) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date."
            );
        }

        offering.setStartDate(request.startDate());
        offering.setEndDate(request.endDate());

        return weeklyOfferingMapper.toResponse(offering);
    }

    @Transactional
    public WeeklyOfferingResponse addItem(
            Long offeringId,
            WeeklyOfferingItemCreateRequest request
    ) {

        WeeklyOffering offering = weeklyOfferingRepository
                .findById(offeringId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering not found."
                        )
                );

        validateOfferingIsEditable(offering);

        Recipe recipe = recipeRepository
                .findById(request.recipeId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Recipe not found."
                        )
                );

        boolean recipeAlreadyIncluded = offering.getItems()
                .stream()
                .anyMatch(existingItem ->
                        existingItem.getRecipe()
                                .getId()
                                .equals(recipe.getId())
                );

        if (recipeAlreadyIncluded) {
            throw new DuplicateOfferingItemException(
                    recipe.getName()
                            + " is already included in this weekly offering."
            );
        }

        WeeklyOfferingItem item = new WeeklyOfferingItem();

        item.setRecipe(recipe);
        item.setOfferingType(request.offeringType());
        item.setPublicDescription(request.publicDescription());
        item.setImageUrl(request.imageUrl());
        item.setImageAlt(request.imageAlt());
        item.setIncludesHouseSalad(request.includesHouseSalad());
        item.setIncludesHomemadeBread(request.includesHomemadeBread());
        item.setDisplayOrder(request.displayOrder());

        if (request.prices() != null) {
            for (OfferingItemPriceRequest priceRequest : request.prices()) {

                WeeklyOfferingItemPrice price =
                        new WeeklyOfferingItemPrice(
                                priceRequest.label(),
                                priceRequest.amount(),
                                priceRequest.displayOrder()
                        );

                item.addPrice(price);
            }
        }

        offering.addItem(item);

        WeeklyOffering savedOffering =
                weeklyOfferingRepository.save(offering);

        return weeklyOfferingMapper.toResponse(savedOffering);
    }

    @Transactional
    public WeeklyOfferingResponse updateItem(
            Long offeringId,
            Long itemId,
            WeeklyOfferingItemCreateRequest request
    ) {

        WeeklyOffering offering = weeklyOfferingRepository
                .findById(offeringId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering not found."
                        )
                );

        validateOfferingIsEditable(offering);

        WeeklyOfferingItem item = weeklyOfferingItemRepository
                .findByIdAndWeeklyOfferingId(
                        itemId,
                        offeringId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering item not found."
                        )
                );

        Recipe recipe = recipeRepository
                .findById(request.recipeId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Recipe not found."
                        )
                );

        item.setRecipe(recipe);
        item.setOfferingType(request.offeringType());
        item.setPublicDescription(request.publicDescription());
        item.setImageUrl(request.imageUrl());
        item.setImageAlt(request.imageAlt());
        item.setIncludesHouseSalad(request.includesHouseSalad());
        item.setIncludesHomemadeBread(
                request.includesHomemadeBread()
        );
        item.setDisplayOrder(request.displayOrder());

        item.getPrices().clear();

        for (OfferingItemPriceRequest priceRequest : request.prices()) {

            WeeklyOfferingItemPrice price =
                    new WeeklyOfferingItemPrice(
                            priceRequest.label(),
                            priceRequest.amount(),
                            priceRequest.displayOrder()
                    );

            item.addPrice(price);
        }

        weeklyOfferingItemRepository.save(item);

        return weeklyOfferingMapper.toResponse(offering);
    }

    @Transactional
    public void deleteItem(
            Long offeringId,
            Long itemId
    ) {

        WeeklyOffering offering = weeklyOfferingRepository
                .findById(offeringId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering not found."
                        )
                );

        validateOfferingIsEditable(offering);

        WeeklyOfferingItem item = weeklyOfferingItemRepository
                .findByIdAndWeeklyOfferingId(
                        itemId,
                        offeringId
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering item not found."
                        )
                );

        offering.removeItem(item);
    }

    @Transactional
    public WeeklyOfferingResponse scheduleOffering(Long offeringId) {

        WeeklyOffering offering = weeklyOfferingRepository
                .findById(offeringId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering not found."
                        )
                );

        if (offering.getStatus() != WeeklyOfferingStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only draft offerings can be scheduled."
            );
        }

        if (offering.getStartDate().isAfter(offering.getEndDate())) {
            throw new IllegalArgumentException(
                    "Start date cannot be after end date."
            );
        }

        if (offering.getItems().isEmpty()) {
            throw new IllegalArgumentException(
                    "Weekly offering must contain at least one item."
            );
        }

        boolean itemWithoutPrices = offering.getItems()
                .stream()
                .anyMatch(item -> item.getPrices().isEmpty());

        if (itemWithoutPrices) {
            throw new IllegalArgumentException(
                    "Every weekly offering item must have at least one price."
            );
        }

        offering.setStatus(WeeklyOfferingStatus.SCHEDULED);

        return weeklyOfferingMapper.toResponse(offering);
    }

    @Transactional
    public WeeklyOfferingResponse archiveOffering(Long offeringId) {

        WeeklyOffering offering = weeklyOfferingRepository
                .findById(offeringId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering not found."
                        )
                );

        if (offering.getStatus() == WeeklyOfferingStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Draft offerings should be deleted rather than archived."
            );
        }

        if (offering.getStatus() == WeeklyOfferingStatus.ARCHIVED) {
            throw new IllegalArgumentException(
                    "Weekly offering is already archived."
            );
        }

        offering.setStatus(WeeklyOfferingStatus.ARCHIVED);

        return weeklyOfferingMapper.toResponse(offering);
    }

    @Transactional
    public void deleteOffering(Long offeringId) {

        WeeklyOffering offering = weeklyOfferingRepository
                .findById(offeringId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering not found."
                        )
                );

        if (offering.getStatus() != WeeklyOfferingStatus.DRAFT) {
            throw new IllegalArgumentException(
                    "Only draft offerings can be deleted."
            );
        }

        weeklyOfferingRepository.delete(offering);
    }

    @Transactional(readOnly = true)
    public List<WeeklyOfferingResponse> getAdminOfferings(
            WeeklyOfferingStatus status
    ) {

        List<WeeklyOffering> offerings;

        if (status == null) {
            offerings =
                    weeklyOfferingRepository
                            .findAllByStatusNotOrderByStartDateDesc(
                                    WeeklyOfferingStatus.ARCHIVED
                            );
        } else {
            offerings =
                    weeklyOfferingRepository
                            .findAllByStatusOrderByStartDateDesc(status);
        }

        return offerings
                .stream()
                .map(weeklyOfferingMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public WeeklyOfferingResponse getAdminOfferingById(Long offeringId) {

        WeeklyOffering offering = weeklyOfferingRepository
                .findById(offeringId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Weekly offering not found."
                        )
                );

        return weeklyOfferingMapper.toResponse(offering);
    }

    private void validateOfferingIsEditable(
            WeeklyOffering offering
    ) {

        if (offering.getStatus() == WeeklyOfferingStatus.ARCHIVED) {
            throw new IllegalArgumentException(
                    "Archived weekly offerings cannot be edited."
            );
        }
    }

}