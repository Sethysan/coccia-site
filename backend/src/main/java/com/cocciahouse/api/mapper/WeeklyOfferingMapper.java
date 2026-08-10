package com.cocciahouse.api.mapper;

import com.cocciahouse.api.dto.OfferingItemPriceResponse;
import com.cocciahouse.api.dto.WeeklyOfferingItemResponse;
import com.cocciahouse.api.dto.WeeklyOfferingResponse;
import com.cocciahouse.api.model.OfferingType;
import com.cocciahouse.api.model.WeeklyOffering;
import com.cocciahouse.api.model.WeeklyOfferingItem;
import com.cocciahouse.api.model.WeeklyOfferingItemPrice;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class WeeklyOfferingMapper {

    public WeeklyOfferingResponse toResponse(WeeklyOffering offering) {
        if (offering == null) {
            throw new IllegalArgumentException(
                    "Weekly offering cannot be null."
            );
        }

        List<WeeklyOfferingItemResponse> items = offering.getItems()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                WeeklyOfferingItem::getDisplayOrder
                        )
                )
                .map(this::toItemResponse)
                .toList();

        return new WeeklyOfferingResponse(
                offering.getId(),
                offering.getStartDate(),
                offering.getEndDate(),
                offering.getStatus(),
                items,
                offering.getCreatedAt(),
                offering.getUpdatedAt()
        );
    }

    private WeeklyOfferingItemResponse toItemResponse(
            WeeklyOfferingItem item
    ) {
        List<OfferingItemPriceResponse> prices = item.getPrices()
                .stream()
                .sorted(
                        Comparator.comparingInt(
                                WeeklyOfferingItemPrice::getDisplayOrder
                        )
                )
                .map(this::toPriceResponse)
                .toList();

        return new WeeklyOfferingItemResponse(
                item.getId(),
                item.getRecipe().getId(),
                item.getRecipe().getName(),
                item.getOfferingType(),
                item.getPublicTitle(),
                item.getPublicDescription(),
                item.getImageUrl(),
                item.getImageAlt(),
                item.isIncludesHouseSalad(),
                item.isIncludesHomemadeBread(),
                buildIncludedSidesText(item),
                item.getDisplayOrder(),
                prices
        );
    }

    private OfferingItemPriceResponse toPriceResponse(
            WeeklyOfferingItemPrice price
    ) {
        return new OfferingItemPriceResponse(
                price.getId(),
                normalizeLabel(price.getLabel()),
                price.getAmount(),
                price.getDisplayOrder()
        );
    }

    private String buildIncludedSidesText(
            WeeklyOfferingItem item
    ) {
        if (item.getOfferingType() != OfferingType.DINNER) {
            return null;
        }

        boolean includesSalad = item.isIncludesHouseSalad();
        boolean includesBread = item.isIncludesHomemadeBread();

        if (includesSalad && includesBread) {
            return "Served with house salad and homemade bread.";
        }

        if (includesSalad) {
            return "Served with house salad.";
        }

        if (includesBread) {
            return "Served with homemade bread.";
        }

        return null;
    }

    private String normalizeLabel(String label) {
        if (label == null || label.isBlank()) {
            return null;
        }

        return label.trim();
    }
}