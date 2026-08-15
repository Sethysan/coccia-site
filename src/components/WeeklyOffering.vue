<template>
    <section v-if="offering" class="weekly-offering" aria-labelledby="weekly-offering-title">
        <p class="section-eyebrow">
            This Week at Coccia House
        </p>

        <h2 id="weekly-offering-title">
            Weekly Features
        </h2>

        <article v-for="item in sortedItems" :key="item.id" class="weekly-offering-item">
            <p class="weekly-offering-type">
                Featured {{ formatOfferingType(item.offeringType) }}
            </p>

            <h3>
                {{ item.recipeName }}
            </h3>

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
    </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { getCurrentWeeklyOffering } from '@/api/weeklyOfferingsApi'

const offering = ref(null)

const sortedItems = computed(() => {
    return [...(offering.value?.items ?? [])]
        .sort((a, b) => a.displayOrder - b.displayOrder)
})

onMounted(async () => {
    try {
        offering.value =
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

<style scoped></style>