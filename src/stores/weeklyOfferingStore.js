import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCsrfToken } from '@/utils/csrf'

export const useWeeklyOfferingStore = defineStore(
    'weeklyOfferings',
    () => {
        const offerings = ref([])
        const loading = ref(false)
        const error = ref(null)
        const currentOffering = ref(null)

        async function fetchOfferings(status = null) {
            loading.value = true
            error.value = null

            try {
                const params = new URLSearchParams()

                if (status) {
                    params.set('status', status)
                }

                const query = params.toString()

                const url = query
                    ? `/api/admin/weekly-offerings?${query}`
                    : '/api/admin/weekly-offerings'

                const response = await fetch(url, {
                    method: 'GET',
                    credentials: 'include'
                })

                if (!response.ok) {
                    throw new Error(
                        'Unable to load weekly offerings.'
                    )
                }

                offerings.value = await response.json()

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
                const csrfToken = getCsrfToken()

                const response = await fetch(
                    `/api/admin/weekly-offerings/${offeringId}/items`,
                    {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-XSRF-TOKEN': csrfToken
                        },
                        credentials: 'include',
                        body: JSON.stringify(item)
                    }
                )

                if (!response.ok) {
                    const errorResponse = await response.json().catch(() => null)

                    throw new Error(
                        errorResponse?.message
                        ?? 'Unable to add weekly offering item.'
                    )
                }

                currentOffering.value = await response.json()

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }

        function clearError() {
            error.value = null
        }

        async function fetchOfferingById(id) {
            loading.value = true
            error.value = null
            currentOffering.value = null

            try {
                const response = await fetch(
                    `/api/admin/weekly-offerings/${id}`,
                    {
                        method: 'GET',
                        credentials: 'include'
                    }
                )

                if (!response.ok) {
                    throw new Error(
                        'Unable to load weekly offering.'
                    )
                }

                currentOffering.value = await response.json()

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
                const csrfToken = getCsrfToken()

                const response = await fetch(
                    `/api/admin/weekly-offerings/${offeringId}/items/${itemId}`,
                    {
                        method: 'DELETE',
                        headers: {
                            'X-XSRF-TOKEN': csrfToken
                        },
                        credentials: 'include'
                    }
                )

                if (!response.ok) {
                    const errorResponse =
                        await response.json().catch(() => null)

                    throw new Error(
                        errorResponse?.message
                        ?? 'Unable to delete weekly offering item.'
                    )
                }

                await fetchOfferingById(offeringId)

                return true

            } catch (err) {
                console.error(err)
                error.value = err.message

                return false
            }
        }

        async function updateItem(offeringId, itemId, item) {
            error.value = null

            try {
                const csrfToken = getCsrfToken()

                const response = await fetch(
                    `/api/admin/weekly-offerings/${offeringId}/items/${itemId}`,
                    {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-XSRF-TOKEN': csrfToken
                        },
                        credentials: 'include',
                        body: JSON.stringify(item)
                    }
                )

                if (!response.ok) {
                    const errorResponse =
                        await response.json().catch(() => null)

                    throw new Error(
                        errorResponse?.message
                        ?? 'Unable to update weekly offering item.'
                    )
                }

                currentOffering.value = await response.json()

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
                const csrfToken = getCsrfToken()

                const response = await fetch(
                    '/api/admin/weekly-offerings',
                    {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-XSRF-TOKEN': csrfToken
                        },
                        credentials: 'include',
                        body: JSON.stringify({
                            startDate,
                            endDate
                        })
                    }
                )

                if (!response.ok) {
                    const errorResponse =
                        await response.json().catch(() => null)

                    throw new Error(
                        errorResponse?.message
                        ?? 'Unable to create weekly offering.'
                    )
                }

                const createdOffering = await response.json()

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
                const csrfToken = getCsrfToken()

                const response = await fetch(
                    `/api/admin/weekly-offerings/${offeringId}`,
                    {
                        method: 'PUT',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-XSRF-TOKEN': csrfToken
                        },
                        credentials: 'include',
                        body: JSON.stringify({
                            startDate,
                            endDate
                        })
                    }
                )

                if (!response.ok) {
                    const errorResponse =
                        await response.json().catch(() => null)

                    throw new Error(
                        errorResponse?.message
                        ?? 'Unable to update weekly offering dates.'
                    )
                }

                currentOffering.value = await response.json()

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
                const csrfToken = getCsrfToken()

                const response = await fetch(
                    `/api/admin/weekly-offerings/${offeringId}/schedule`,
                    {
                        method: 'PUT',
                        headers: {
                            'X-XSRF-TOKEN': csrfToken
                        },
                        credentials: 'include'
                    }
                )

                if (!response.ok) {
                    const errorResponse =
                        await response.json().catch(() => null)

                    throw new Error(
                        errorResponse?.message
                        ?? 'Unable to schedule weekly offering.'
                    )
                }

                currentOffering.value = await response.json()

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
            clearError,
            createOffering,
            updateOfferingDates,
            scheduleOffering
        }

    }
)