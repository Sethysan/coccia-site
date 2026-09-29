<template>
    <div v-if="pizza" class="pizza-menu">
        <section v-if="pizza.sizes?.length" class="pizza-menu-block">
            <h3>Build Your Own</h3>

            <div class="pizza-price-table-wrap branded-scrollbar">
                <table class="pizza-price-table">
                    <thead>
                        <tr>
                            <th scope="col">Pizza</th>
                            <th v-for="size in pizza.sizes" :key="size.name" scope="col">
                                {{ size.name }}
                            </th>
                        </tr>
                    </thead>

                    <tbody>
                        <tr>
                            <th scope="row">Cheese Pizza</th>
                            <td v-for="size in pizza.sizes" :key="`base-${size.name}`">
                                {{ formatPrice(size.basePrice) }}
                            </td>
                        </tr>

                        <tr v-if="pizza.additionalToppingPrice != null">
                            <th scope="row">Additional Topping</th>
                            <td v-for="size in pizza.sizes" :key="`topping-${size.name}`">
                                +{{ formatPrice(pizza.additionalToppingPrice) }}
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </section>

        <section v-if="pizza.toppings?.length" class="pizza-menu-block">
            <h3>Toppings</h3>

            <div class="pizza-choice-list">
                <span v-for="topping in pizza.toppings" :key="topping.id ?? topping.name" class="pizza-choice">
                    {{ topping.name }}
                </span>
            </div>
        </section>

        <section v-if="pizza.addOns?.length" class="pizza-menu-block">
            <h3>Extras</h3>

            <div class="pizza-extras">
                <div v-for="addOn in pizza.addOns" :key="addOn.id ?? addOn.name" class="pizza-extra">
                    <span>{{ addOn.name }}</span>
                    <strong>{{ formatPrice(addOn.amount) }}</strong>
                </div>
            </div>
        </section>

        <section v-if="pizza.specialties?.length" class="pizza-menu-block pizza-specialties">
            <h3>Specialty Pizzas</h3>

            <article v-for="specialty in pizza.specialties" :key="specialty.id ?? specialty.name"
                class="pizza-specialty">
                <div class="pizza-specialty-heading">
                    <div>
                        <h4>{{ specialty.name }}</h4>

                        <p v-if="specialty.description">
                            {{ specialty.description }}
                        </p>
                    </div>

                    <div v-if="specialty.prices?.length" class="pizza-specialty-prices">
                        <span v-for="price in specialty.prices"
                            :key="`${specialty.name}-${price.sizeName ?? price.label}`">
                            <span>
                                {{ price.sizeName ?? price.label }}
                            </span>

                            <strong>
                                {{ formatPrice(price.amount ?? price.price) }}
                            </strong>
                        </span>
                    </div>
                </div>

                <div v-if="specialty.toppings?.length" class="pizza-specialty-toppings">
                    <span v-for="topping in specialty.toppings" :key="topping">
                        {{ topping }}
                    </span>
                </div>

                <div v-if="specialty.addOns?.length" class="pizza-specialty-addons">
                    <span v-for="addOn in specialty.addOns" :key="addOn.name">
                        {{ addOn.name }}

                        <strong v-if="isIncluded(addOn)">
                            included
                        </strong>

                        <strong v-else>
                            +{{ formatPrice(addOn.overrideAmount ?? addOn.regularAmount) }}
                        </strong>
                    </span>
                </div>
            </article>
        </section>
    </div>
</template>

<script setup>
defineProps({
    pizza: {
        type: Object,
        required: true
    }
})

function formatPrice(value) {
    const number = Number(value)

    if (!Number.isFinite(number)) {
        return ''
    }

    return `$${number.toFixed(2)}`
}

function isIncluded(addOn) {
    return (
        addOn.priceType === 'FREE' ||
        addOn.pricingType === 'FREE' ||
        addOn.free === true
    )
}
</script>

<style scoped>
.pizza-menu {
    display: grid;
    gap: 2rem;
}

.pizza-menu-block {
    min-width: 0;
}

.pizza-menu-block>h3 {
    margin: 0 0 0.85rem;
    padding-bottom: 0.65rem;

    color: var(--text-primary);

    border-bottom: 2px solid var(--bronze-color);

    font-size: 1.35rem;
}

.pizza-price-table-wrap {
    max-width: 100%;
    overflow-x: auto;
}

.pizza-price-table {
    width: 100%;
    min-width: 500px;

    border-collapse: collapse;

    background: rgba(20, 15, 12, 0.45);
    border: 1px solid rgba(138, 106, 50, 0.55);
}

.pizza-price-table th,
.pizza-price-table td {
    padding: 0.8rem 1rem;

    border-bottom: 1px solid rgba(138, 106, 50, 0.35);

    text-align: center;
}

.pizza-price-table th:first-child {
    text-align: left;
}

.pizza-price-table thead th {
    color: var(--text-primary);
    background: rgba(138, 106, 50, 0.16);
}

.pizza-price-table tbody th {
    color: var(--text-secondary);
    font-weight: 700;
}

.pizza-price-table td {
    color: var(--text-primary);
    font-weight: 700;
}

.pizza-choice-list {
    display: flex;
    flex-wrap: wrap;
    gap: 0.55rem;
}

.pizza-choice {
    padding: 0.45rem 0.7rem;

    color: var(--text-secondary);
    background: rgba(20, 15, 12, 0.55);

    border: 1px solid rgba(138, 106, 50, 0.45);
    border-radius: 999px;

    font-weight: 600;
}

.pizza-extras {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0.75rem;
}

.pizza-extra {
    display: flex;
    justify-content: space-between;
    gap: 1rem;

    padding: 0.85rem 1rem;

    background: rgba(20, 15, 12, 0.55);

    border: 1px solid rgba(138, 106, 50, 0.45);
    border-radius: 0.35rem;
}

.pizza-extra span {
    color: var(--text-secondary);
    font-weight: 600;
}

.pizza-extra strong {
    color: var(--text-primary);
}

.pizza-specialties {
    margin-top: 0.25rem;
}

.pizza-specialty {
    padding: 1.25rem 0;

    border-bottom: 1px solid rgba(138, 106, 50, 0.4);
}

.pizza-specialty:last-child {
    padding-bottom: 0;

    border-bottom: 0;
}

.pizza-specialty-heading {
    display: grid;
    grid-template-columns: minmax(0, 1fr) auto;
    gap: 1rem 2rem;
}

.pizza-specialty h4 {
    margin: 0;

    color: var(--text-primary);

    font-size: 1.2rem;
}

.pizza-specialty p {
    margin: 0.3rem 0 0;

    color: var(--text-secondary);

    font-style: italic;
    line-height: 1.4;
}

.pizza-specialty-prices {
    display: flex;
    gap: 1rem;
}

.pizza-specialty-prices>span {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.15rem;
}

.pizza-specialty-prices span span {
    color: var(--text-secondary);

    font-size: 0.8rem;
    font-weight: 600;
}

.pizza-specialty-prices strong {
    color: var(--text-primary);
}

.pizza-specialty-toppings,
.pizza-specialty-addons {
    display: flex;
    flex-wrap: wrap;
    gap: 0.35rem 0.7rem;

    margin-top: 0.75rem;

    color: var(--text-secondary);

    font-size: 0.9rem;
    line-height: 1.4;
}

.pizza-specialty-toppings span:not(:last-child)::after,
.pizza-specialty-addons span:not(:last-child)::after {
    margin-left: 0.7rem;

    color: var(--bronze-hover);

    content: "•";
}

.pizza-specialty-addons strong {
    color: var(--text-primary);

    font-size: 0.8rem;
    font-style: italic;
}

@media (max-width: 600px) {
    .pizza-menu {
        gap: 1.5rem;
    }

    .pizza-menu-block>h3 {
        font-size: 1.15rem;
    }

    .pizza-price-table {
        min-width: 0;
        table-layout: fixed;
    }

    .pizza-price-table th:first-child,
    .pizza-price-table td:first-child {
        width: 40%;
    }

    .pizza-price-table th:not(:first-child),
    .pizza-price-table td:not(:first-child) {
        width: 20%;
    }

    .pizza-price-table th,
    .pizza-price-table td {
        padding: 0.65rem 0.35rem;

        font-size: 0.9rem;
    }

    .pizza-extras {
        grid-template-columns: 1fr;
    }

    .pizza-specialty-heading {
        grid-template-columns: 1fr;
    }

    .pizza-specialty-prices {
        flex-wrap: wrap;
        gap: 0.6rem 1.25rem;
    }

    .pizza-specialty-prices>span {
        flex-direction: row;
        gap: 0.35rem;
    }
}
</style>