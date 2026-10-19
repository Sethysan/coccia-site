
package com.cocciahouse.api.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Map;

@Service
public class InventoryUnitConversionService {

    private record UnitDefinition(
            String dimension,
            BigDecimal toReferenceUnit
    ) {
    }

    private static final Map<String, UnitDefinition> UNITS = Map.ofEntries(
            // Weight: reference unit = gram
            Map.entry("mg", unit("WEIGHT", "0.001")),
            Map.entry("g", unit("WEIGHT", "1")),
            Map.entry("kg", unit("WEIGHT", "1000")),
            Map.entry("oz", unit("WEIGHT", "28.349523125")),
            Map.entry("lb", unit("WEIGHT", "453.59237")),

            // Volume: reference unit = milliliter
            Map.entry("ml", unit("VOLUME", "1")),
            Map.entry("l", unit("VOLUME", "1000")),
            Map.entry("fl_oz_us", unit("VOLUME", "29.5735295625")),
            Map.entry("cup_us", unit("VOLUME", "236.5882365")),
            Map.entry("pint_us", unit("VOLUME", "473.176473")),
            Map.entry("quart_us", unit("VOLUME", "946.352946")),
            Map.entry("gallon_us", unit("VOLUME", "3785.411784")),

            // Count-based units must match exactly.
            Map.entry("each", unit("COUNT_EACH", "1")),
            Map.entry("can", unit("COUNT_CAN", "1")),
            Map.entry("bottle", unit("COUNT_BOTTLE", "1")),
            Map.entry("bag", unit("COUNT_BAG", "1")),
            Map.entry("box", unit("COUNT_BOX", "1"))
    );

    private static UnitDefinition unit(
            String dimension,
            String factor
    ) {
        return new UnitDefinition(
                dimension,
                new BigDecimal(factor)
        );
    }

    public BigDecimal convert(
            BigDecimal quantity,
            String fromUnit,
            String toUnit
    ) {
        if (quantity == null) {
            throw new IllegalArgumentException(
                    "Quantity is required."
            );
        }

        String source = normalize(fromUnit);
        String target = normalize(toUnit);

        UnitDefinition from = UNITS.get(source);
        UnitDefinition to = UNITS.get(target);

        if (from == null || to == null) {
            throw new IllegalArgumentException(
                    "Unsupported inventory unit: "
                            + fromUnit + " or " + toUnit
            );
        }

        if (!from.dimension().equals(to.dimension())) {
            throw new IllegalArgumentException(
                    "Cannot convert " + fromUnit
                            + " to " + toUnit
            );
        }

        return quantity
                .multiply(from.toReferenceUnit())
                .divide(
                        to.toReferenceUnit(),
                        6,
                        RoundingMode.HALF_UP
                );
    }


    private String normalize(String unit) {
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException(
                    "Inventory unit is required"
            );
        }

        String normalized = unit.trim().toLowerCase(Locale.ROOT);

        return switch (normalized) {
            case "milligram", "milligrams" -> "mg";
            case "gram", "grams" -> "g";
            case "kilogram", "kilograms" -> "kg";
            case "ounce", "ounces" -> "oz";
            case "pound", "pounds", "lbs" -> "lb";

            case "milliliter", "milliliters" -> "ml";
            case "liter", "liters" -> "l";
            case "fluid ounce", "fluid ounces", "fl oz" -> "fl_oz_us";
            case "cup", "cups" -> "cup_us";
            case "pint", "pints" -> "pint_us";
            case "quart", "quarts" -> "quart_us";
            case "gallon", "gallons" -> "gallon_us";

            case "item", "items", "piece", "pieces" -> "each";
            case "cans" -> "can";
            case "bottles" -> "bottle";
            case "bags" -> "bag";
            case "boxes" -> "box";

            default -> normalized;
        };
    }

}
