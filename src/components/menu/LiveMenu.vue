<template>
    <section class="live-menu">
        <nav class="live-menu-navigation" aria-label="Menu sections">
            <button type="button" class="live-menu-navigation-button" :class="{ active: selectedSectionName === null }"
                @click="selectedSectionName = null">
                Menu Home
            </button>
            <button v-for="section in menu.sections" :key="section.name" type="button"
                class="live-menu-navigation-button" :class="{ active: selectedSectionName === section.name }"
                @click="selectSection(section.name, $event)">
                {{ section.name }}
            </button>
        </nav>

        <section v-if="selectedSectionName === null" class="live-menu-home">
            <div v-if="weeklyOfferingLoading" class="weekly-offering-loading">
                Loading this week's features...
            </div>

            <WeeklyOffering v-else-if="weeklyOffering" :offering="weeklyOffering" />

            <p v-else class="weekly-offering-empty">
                Check back soon for this week's features.
            </p>
        </section>

        <div v-if="selectedSection" class="live-menu-section">
            <h2>{{ selectedSection.name }}</h2>

            <p v-if="selectedSection.subtitle" class="section-subtitle">
                {{ selectedSection.subtitle }}
            </p>

            <!-- Ungrouped menu items -->
            <div v-for="item in selectedSection.items" :key="item.name" class="menu-item">
                <div class="menu-item-content">
                    <h3>{{ item.name }}</h3>

                    <p v-if="item.description">
                        {{ item.description }}
                    </p>
                </div>

                <div class="menu-item-prices">
                    <span v-for="price in item.prices" :key="`${item.name}-${price.label ?? 'price'}`"
                        class="menu-item-price">
                        <span v-if="price.label">
                            {{ price.label }}
                        </span>

                        ${{ Number(price.amount).toFixed(2) }}
                    </span>
                </div>
            </div>

            <!-- Subsections -->
            <div v-for="subsection in selectedSection.subsections" :key="subsection.name" class="menu-subsection">
                <div class="menu-subsection-heading">
                    <h3>{{ subsection.name }}</h3>

                    <span v-if="subsection.price != null">
                        ${{ Number(subsection.price).toFixed(2) }}
                    </span>
                </div>

                <div v-for="item in subsection.items" :key="item.name" class="menu-item">
                    <div class="menu-item-content">
                        <h4>{{ item.name }}</h4>

                        <p v-if="item.description">
                            {{ item.description }}
                        </p>
                    </div>

                    <div class="menu-item-prices">
                        <span v-for="price in item.prices" :key="`${item.name}-${price.label ?? 'price'}`"
                            class="menu-item-price">
                            <span v-if="price.label">
                                {{ price.label }}
                            </span>

                            ${{ Number(price.amount).toFixed(2) }}
                        </span>
                    </div>
                </div>
            </div>

            <p v-if="selectedSection.footerText" class="section-footer">
                {{ selectedSection.footerText }}
            </p>
        </div>
    </section>
</template>

<script setup>

import { computed, onMounted, ref } from 'vue'
import { getCurrentWeeklyOffering } from '@/api/weeklyOfferingsApi'
import WeeklyOffering from '@/components/WeeklyOffering.vue'

const weeklyOffering = ref(null)
const weeklyOfferingLoading = ref(true)

async function loadWeeklyOffering() {
    weeklyOfferingLoading.value = true

    try {
        weeklyOffering.value = await getCurrentWeeklyOffering()
    } catch (error) {
        /*
         * Weekly Features are optional menu-home content.
         * A failure here should not affect the regular menu.
         */
        weeklyOffering.value = null
    } finally {
        weeklyOfferingLoading.value = false
    }
}

onMounted(() => {
    loadWeeklyOffering()
})

const props = defineProps({
    menu: {
        type: Object,
        required: true
    }
})

const selectedSectionName = ref(null)

function selectSection(sectionName, event) {
    selectedSectionName.value = sectionName

    event.currentTarget.scrollIntoView({
        behavior: 'smooth',
        block: 'nearest',
        inline: 'center'
    })
}

const selectedSection = computed(() => {
    if (selectedSectionName.value === null) {
        return null
    }

    return props.menu.sections.find(
        (section) => section.name === selectedSectionName.value
    ) ?? null
})

</script>

<style scoped>
/* ==========================================================
   MENU NAVIGATION
   ========================================================== */

.live-menu-navigation {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
    gap: 0.65rem;

    width: fit-content;
    max-width: 94%;
    margin: 1.5rem auto;
    padding: 0.5rem;

    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;
}

.live-menu-navigation-button {
    padding: 0.7rem 1.1rem;

    color: var(--text-primary);
    background: rgba(20, 15, 12, 0.72);

    border: 1px solid var(--bronze-color);
    border-radius: 0.35rem;

    font: inherit;
    font-weight: 700;

    cursor: pointer;

    transition:
        color 180ms ease,
        background-color 180ms ease,
        border-color 180ms ease,
        transform 180ms ease;
}

.live-menu-navigation-button:hover {
    color: var(--text-primary);
    background: rgba(138, 106, 50, 0.35);
    border-color: var(--bronze-hover);

    transform: translateY(-1px);
}

.live-menu-navigation-button.active {
    color: #1a120c;
    background: var(--bronze-hover);
    border-color: var(--bronze-hover);
}

/* ==========================================================
   SELECTED MENU SECTION
   ========================================================== */

.live-menu-section {
    width: min(900px, 92%);
    margin: 2rem auto;
    padding: 2rem;

    color: var(--text-primary);
    background: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;

    box-shadow: var(--shadow-soft);
}

.live-menu-section>h2 {
    margin: 0;

    color: var(--text-primary);

    font-size: clamp(2rem, 5vw, 3rem);
    line-height: 1.1;
    text-align: center;
}

.live-menu-section>h2::after {
    display: block;

    width: 4rem;
    height: 1px;
    margin: 0.8rem auto 1.5rem;

    background: var(--bronze-bold);

    content: "";
}

.section-subtitle {
    max-width: 700px;
    margin: 0 auto 2rem;

    color: var(--text-secondary);

    line-height: 1.5;
    text-align: center;
}

.section-footer {
    margin: 2rem 0 0;
    padding-top: 1rem;

    color: var(--text-secondary);

    border-top: 1px solid var(--bronze-color);

    font-size: 0.9rem;
    font-style: italic;
    line-height: 1.5;
    text-align: center;
}

/* ==========================================================
   MENU ITEMS
   ========================================================== */

.menu-item {
    display: grid;
    grid-template-columns: minmax(0, 1fr) auto;
    gap: 0.35rem 1.5rem;

    padding: 1.25rem 0;

    border-bottom: 1px solid rgba(138, 106, 50, 0.45);
}

.menu-item-content {
    min-width: 0;
}

.menu-item-content h3,
.menu-item-content h4 {
    margin: 0 0 0.35rem;

    color: var(--text-primary);

    font-size: 1.15rem;
}

.menu-item-content p {
    margin: 0;

    color: var(--text-secondary);

    line-height: 1.5;
}

.menu-item-prices {
    display: flex;
    flex-direction: column;
    gap: 0.35rem;

    min-width: max-content;
}

.menu-item-price {
    display: flex;
    justify-content: space-between;
    gap: 1.25rem;

    font-weight: 700;
}

.menu-item-price>span {
    color: var(--text-secondary);
    font-weight: 600;
    text-transform: capitalize;
}

/* ==========================================================
   MENU SUBSECTIONS
   ========================================================== */

.menu-subsection {
    margin-top: 2rem;
}

.menu-subsection+.menu-subsection {
    margin-top: 2.5rem;
}

.menu-subsection-heading {
    display: flex;
    align-items: baseline;
    justify-content: space-between;
    gap: 1rem;

    padding-bottom: 0.65rem;

    border-bottom: 2px solid var(--bronze-color);
}

.menu-subsection-heading h3 {
    margin: 0;

    color: var(--text-primary);

    font-size: 1.35rem;
}

.menu-subsection-heading>span {
    color: var(--text-primary);

    font-weight: 700;
}

.menu-subsection .menu-item {
    display: inline-block;

    margin: 0.75rem 1.5rem 0 0;
    padding: 0;

    border-bottom: 0;
}

.menu-subsection .menu-item-content h4 {
    margin: 0;

    color: var(--text-secondary);

    font-size: 1rem;
    font-weight: 600;
}

.menu-subsection .menu-item-content p,
.menu-subsection .menu-item-prices {
    margin-top: 0.35rem;
}

/* ==========================================================
   MOBILE
   ========================================================== */

@media (max-width: 600px) {

    /* Keep section navigation accessible without wrapping
       into a large multi-row block. */

    .live-menu {
        width: 100%;
        max-width: 100%;
        min-width: 0;
        overflow-x: hidden;
    }

    .live-menu-navigation {
        flex-wrap: nowrap;
        justify-content: flex-start;
        gap: 0.5rem;

        width: 100%;
        max-width: 100%;
        margin: 0;
        padding: 0.65rem 0.75rem;

        overflow-x: auto;
        overscroll-behavior-inline: contain;

        border-right: 0;
        border-left: 0;
        border-radius: 0;

        scrollbar-width: thin;
    }

    .live-menu-navigation-button {
        flex: 0 0 auto;

        padding: 0.6rem 0.85rem;

        font-size: 0.9rem;
    }

    /* Give the actual menu as much phone width as possible. */
    .live-menu-section {
        width: calc(100% - 1rem);
        margin: 0.75rem auto;
        padding: 1.25rem 1rem;
    }

    .live-menu-section>h2 {
        font-size: 2rem;
    }

    .section-subtitle {
        margin-bottom: 1.5rem;

        font-size: 0.9rem;
    }

    /* On phones, don't force descriptions and prices
       to compete for the same horizontal space. */
    .menu-item {
        display: block;

        padding: 1rem 0;
    }

    .menu-item-content h3,
    .menu-item-content h4 {
        margin-bottom: 0.25rem;

        font-size: 1.05rem;
    }

    .menu-item-content p {
        font-size: 0.9rem;
        line-height: 1.4;
    }

    .menu-item-prices {
        display: flex;
        flex-direction: row;
        flex-wrap: wrap;
        gap: 0.4rem 1.25rem;

        margin-top: 0.5rem;
    }

    .menu-item-price {
        justify-content: flex-start;
        gap: 0.4rem;

        font-size: 0.95rem;
    }

    /* Preserve the category → choices hierarchy used
       by shared-price subsections such as Beverages. */
    .menu-subsection {
        margin-top: 1.5rem;
    }

    .menu-subsection+.menu-subsection {
        margin-top: 2rem;
    }

    .menu-subsection-heading {
        gap: 0.75rem;

        padding-bottom: 0.5rem;
    }

    .menu-subsection-heading h3 {
        font-size: 1.15rem;
    }

    .menu-subsection {
        max-width: 100%;
        overflow-wrap: anywhere;
    }

    .menu-subsection .menu-item {
        display: inline-block;

        max-width: 100%;
        margin: 0.65rem 1rem 0 0;
        padding: 0;

        white-space: normal;

        border-bottom: 0;
    }

    .menu-subsection .menu-item-content h4 {
        font-size: 0.95rem;
    }

    .section-footer {
        margin-top: 1.5rem;

        font-size: 0.85rem;
    }
}
</style>