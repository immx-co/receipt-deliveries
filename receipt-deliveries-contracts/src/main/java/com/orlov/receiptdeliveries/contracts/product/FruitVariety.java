package com.orlov.receiptdeliveries.contracts.product;

/**
 * Перечисление сортов фруктов.
 */
public enum FruitVariety {

    GRANNY_SMITH(
            FruitType.APPLE,
            "Грэнни Смит"),

    FUJI(
            FruitType.APPLE,
            "Фуджи"),

    CONFERENCE(
            FruitType.PEAR,
            "Конференция"),

    ABBE_FETEL(
            FruitType.PEAR,
            "Аббат Феттель");

    private final FruitType fruitType;

    private final String displayName;

    FruitVariety(FruitType fruitType,
                 String displayName) {
        this.fruitType = fruitType;
        this.displayName = displayName;
    }

    public FruitType getFruitType() {
        return fruitType;
    }

    public String getDisplayName() {
        return displayName;
    }
}
