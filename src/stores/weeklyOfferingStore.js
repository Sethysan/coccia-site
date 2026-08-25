import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
    getWeeklyOfferings,
    getWeeklyOfferingById,
    createWeeklyOffering,
    updateWeeklyOfferingDates,
    addWeeklyOfferingItem,
    updateWeeklyOfferingItem,
    deleteWeeklyOfferingItem,
    scheduleWeeklyOffering,
    archiveWeeklyOffering,
    deleteWeeklyOffering
} from '@/api/weeklyOfferingsApi'

export const useWeeklyOfferingStore = defineStore(
    'weeklyOfferings',
    () => {
        const offerings = ref([])
        const loading = ref(false)
        const error = ref(null)
        const currentOffering = ref(null)

        function clearError() {
            error.value = null
        }

        async function fetchOfferings(status = null) {
            loading.value = true
            error.value = null

            try {
                offerings.value = await getWeeklyOfferings(status)

            } catch (err) {
                console.error(err)

                offerings.value = []
                error.value = err.message

            } finally {
                loading.value = false
            }
        }

        async function addItem(offeringId, item) {
            error.value = null

            try {
                currentOffering.value =
                    await addWeeklyOfferingItem(
                        offeringId,
                        item
                    )

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }

        async function fetchOfferingById(id) {
            loading.value = true
            error.value = null
            currentOffering.value = null

            try {
                currentOffering.value =
                    await getWeeklyOfferingById(id)

            } catch (err) {
                console.error(err)

                currentOffering.value = null
                error.value = err.message

            } finally {
                loading.value = false
            }
        }

        async function deleteItem(offeringId, itemId) {
            error.value = null

            try {
                await deleteWeeklyOfferingItem(
                    offeringId,
                    itemId
                )

                await fetchOfferingById(offeringId)

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }

        async function updateItem(
            offeringId,
            itemId,
            item
        ) {
            error.value = null

            try {
                currentOffering.value =
                    await updateWeeklyOfferingItem(
                        offeringId,
                        itemId,
                        item
                    )

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }
        async function createOffering(startDate, endDate) {
            error.value = null

            try {
                const createdOffering =
                    await createWeeklyOffering({
                        startDate,
                        endDate
                    })

                offerings.value = [
                    createdOffering,
                    ...offerings.value
                ]

                return createdOffering

            } catch (err) {
                console.error(err)
                error.value = err.message

                return null
            }
        }

        async function updateOfferingDates(
            offeringId,
            startDate,
            endDate
        ) {
            error.value = null

            try {
                currentOffering.value =
                    await updateWeeklyOfferingDates(
                        offeringId,
                        {
                            startDate,
                            endDate
                        }
                    )

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }
        async function scheduleOffering(offeringId) {
            error.value = null

            try {
                currentOffering.value =
                    await scheduleWeeklyOffering(offeringId)

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }
        async function deleteOffering(offeringId) {
            error.value = null

            try {
                await deleteWeeklyOffering(offeringId)

                offerings.value = offerings.value.filter(
                    offering => offering.id !== Number(offeringId)
                )

                if (
                    currentOffering.value?.id === Number(offeringId)
                ) {
                    currentOffering.value = null
                }

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }

        async function archiveOffering(offeringId) {
            error.value = null

            try {
                const archivedOffering =
                    await archiveWeeklyOffering(offeringId)

                currentOffering.value = archivedOffering

                offerings.value = offerings.value.map(
                    offering =>
                        offering.id === archivedOffering.id
                            ? archivedOffering
                            : offering
                )

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }

        return {
            offerings,
            loading,
            error,
            currentOffering,
            fetchOfferings,
            fetchOfferingById,
            addItem,
            updateItem,
            deleteItem,
            deleteOffering,
            archiveOffering,
            clearError,
            createOffering,
            updateOfferingDates,
            scheduleOffering
        }

    }
)