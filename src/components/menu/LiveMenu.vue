<template>
    <section class="live-menu">
        <nav class="live-menu-navigation" aria-label="Menu sections">
            <button v-for="section in menu.sections" :key="section.name" type="button"
                class="live-menu-navigation-button" :class="{ active: selectedSectionName === section.name }"
                @click="selectedSectionName = section.name">
                {{ section.name }}
            </button>
        </nav>

        <section v-if="selectedSectionName === null" class="live-menu-home">
            <header class="live-menu-home-header">
                <p class="live-menu-home-eyebrow">
                    This Week at Coccia House
                </p>

                <h2>Weekly Features</h2>

                <p>
                    See what's cooking this week, or choose a menu section above.
                </p>
            </header>

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

const selectedSection = computed(() => {
    if (selectedSectionName.value === null) {
        return null
    }

    return props.menu.sections.find(
        (section) => section.name === selectedSectionName.value
    ) ?? null
})

</script>