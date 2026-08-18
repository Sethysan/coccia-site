<template>
    <section v-if="displayOffering" class="weekly-offering" aria-labelledby="weekly-offering-title">
        <p class="section-eyebrow">
            This Week at Coccia House
        </p>

        <h2 id="weekly-offering-title">
            Weekly Features
        </h2>

        <article v-if="dinner" class="weekly-offering-item featured-dinner">
            <p class="weekly-offering-type">
                Featured Dinner
            </p>

            <h3>
                {{ dinner.recipeName }}
            </h3>

            <img v-if="dinner.imageUrl" :src="dinner.imageUrl" :alt="dinner.imageAlt || dinner.recipeName"
                class="weekly-offering-image" />

            <p v-if="dinner.publicDescription">
                {{ dinner.publicDescription }}
            </p>

            <p v-if="dinner.includedSidesText">
                {{ dinner.includedSidesText }}
            </p>

            <ul>
                <li v-for="price in dinner.prices" :key="price.id">
                    <span v-if="price.label">
                        {{ price.label }}:
                    </span>

                    ${{ price.amount }}
                </li>
            </ul>
        </article>

        <div class="secondary-features">
            <article v-for="item in secondaryItems" :key="item.id" class="weekly-offering-item secondary-feature">
                <p class="weekly-offering-type">
                    Featured {{ formatOfferingType(item.offeringType) }}
                </p>

                <h3>
                    {{ item.recipeName }}
                </h3>

                <img v-if="item.imageUrl" :src="item.imageUrl" :alt="item.imageAlt || item.recipeName"
                    class="weekly-offering-image" />

                <p v-if="item.publicDescription">
                    {{ item.publicDescription }}
                </p>

                <p v-if="item.includedSidesText">
                    {{ item.includedSidesText }}
                </p>

                <ul>
                    <li v-for="price in item.prices" :key="price.id">
                        <span v-if="price.label">
                            {{ price.label }}:
                        </span>

                        ${{ price.amount }}
                    </li>
                </ul>
            </article>
        </div><!--  -->
    </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getCurrentWeeklyOffering } from '@/api/weeklyOfferingsApi'

const props = defineProps({
    offering: {
        type: Object,
        default: null
    }
})

const fetchedOffering = ref(null)

const displayOffering = computed(() =>
    props.offering ?? fetchedOffering.value
)

const sortedItems = computed(() => {
    return [...(displayOffering.value?.items ?? [])]
        .sort((a, b) => a.displayOrder - b.displayOrder)
})

const dinner = computed(() =>
    sortedItems.value.find(
        item => item.offeringType === 'DINNER'
    ) ?? null
)

const secondaryItems = computed(() =>
    sortedItems.value.filter(
        item => item.offeringType !== 'DINNER'
    )
)

onMounted(async () => {
    if (props.offering) {
        return
    }

    try {
        fetchedOffering.value =
            await getCurrentWeeklyOffering()
    } catch (error) {
        console.error(
            'Unable to load weekly offering:',
            error
        )
    }
})

function formatOfferingType(type) {
    if (!type) {
        return ''
    }

    return (
        type.charAt(0).toUpperCase()
        + type.slice(1).toLowerCase()
    )
}

</script>

<style scoped>
.weekly-offering {
    width: min(92%, 900px);
    padding: 1.5rem;
    margin: 2rem auto 1rem;

    background-color: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;

    text-align: left;
}

.weekly-offering>.section-eyebrow,
.weekly-offering>h2 {
    text-align: center;
}

.weekly-offering>h2 {
    margin: 0 0 1.5rem;
    font-size: clamp(1.8rem, 4vw, 2.6rem);
}


/* ==========================================================
   FEATURED DINNER
   ========================================================== */

.featured-dinner {
    display: grid;
    grid-template-columns: minmax(0, 0.95fr) minmax(260px, 1.05fr);
    gap: 1.5rem;
    align-items: center;

    padding-bottom: 2rem;

    border-bottom: 1px solid var(--bronze-color);
}

.featured-dinner .weekly-offering-image {
    grid-column: 1;
    grid-row: 1 / span 5;
    
    width: 100%;
    aspect-ratio: 4 / 3;
    height: auto;

    object-fit: cover;
}

.featured-dinner .weekly-offering-type,
.featured-dinner h3,
.featured-dinner p,
.featured-dinner ul {
    grid-column: 2;
}

.featured-dinner p {
    margin: 0.5rem 0;
}

.featured-dinner ul {
    margin-top: 0.75rem;
}

.weekly-offering-type {
    margin: 0 0 0.4rem;

    color: var(--bronze-bold);

    font-size: 0.8rem;
    font-weight: 700;
    letter-spacing: 0.12em;
    text-transform: uppercase;
}

.weekly-offering-item h3 {
    margin: 0 0 0.75rem;

    font-size: clamp(1.4rem, 3vw, 2rem);
}

.weekly-offering-item p {
    line-height: 1.6;
    
}

.weekly-offering-image {
    display: block;
    width: 100%;
    max-width: 100%;
    height: auto;

    border-radius: 0.5rem;

    object-fit: cover;
}


/* ==========================================================
   PRICES
   ========================================================== */

.weekly-offering-item ul {
    margin: 1rem 0 0;
    padding: 0;

    list-style: none;
}

.weekly-offering-item li {
    margin: 0.25rem 0;

    font-weight: 700;
}


/* ==========================================================
   SOUP + DESSERT
   ========================================================== */

.secondary-features {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 0;
    margin-top: 1.5rem;
}

.secondary-feature {
    padding: 0 1.5rem;
}

.secondary-feature:first-child {
    padding-left: 0;
    padding-right: 1.5rem;
    border-right: 1px solid var(--bronze-color);
}

.secondary-feature:last-child {
    padding-right: 0;
}

.secondary-feature .weekly-offering-image {
    width: 100%;
    aspect-ratio: 4 / 3;
    height: auto;
    margin-bottom: 1rem;
    object-fit: cover;
}

.secondary-feature p {
    margin: 0.6rem 0;
}

/* ==========================================================
   MOBILE
   ========================================================== */

@media (max-width:  800px) {
    .weekly-offering {
        padding: 1.25rem;
    }

    .featured-dinner {
        display: block;
    }

    .featured-dinner .weekly-offering-image {
        width: 100%;
        aspect-ratio: 16 / 10;
        margin-bottom: 1.5rem;
        object-fit: cover;
    }

    .secondary-features {
        display: block;
    }

    .secondary-feature {
        padding: 1.75rem 0;
    }

    .secondary-feature:first-child {
        padding: 0 0 1.75rem;

        border-right: 0;
        border-bottom: 1px solid var(--bronze-color);
    }

    .secondary-feature:last-child {
        padding-bottom: 0;
    }
}
</style>