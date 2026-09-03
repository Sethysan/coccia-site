import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
    getMenuSections,
    createMenuSection,
    updateMenuSection,
    getMenuItems,
    createMenuItem,
    updateMenuItem
} from '@/api/menuApi'

export const useMenuStore = defineStore('menu', () => {
    const sections = ref([])
    const itemsBySection = ref({})
    const loading = ref(false)
    const error = ref(null)

    function clearError() {
        error.value = null
    }

    function sortSections() {
        sections.value = [...sections.value]
            .sort((a, b) =>
                a.displayOrder - b.displayOrder
            )
    }

    function sortItems(sectionId) {
        const items =
            itemsBySection.value[sectionId] ?? []

        itemsBySection.value = {
            ...itemsBySection.value,
            [sectionId]: [...items].sort((a, b) =>
                a.displayOrder - b.displayOrder
            )
        }
    }

    async function fetchSections() {
        loading.value = true
        error.value = null

        try {
            sections.value =
                await getMenuSections()

            sortSections()

        } catch (err) {
            console.error(err)

            sections.value = []
            error.value = err.message

        } finally {
            loading.value = false
        }
    }

    async function addSection(section) {
        error.value = null

        try {
            const createdSection =
                await createMenuSection(section)

            sections.value = [
                ...sections.value,
                createdSection
            ]

            sortSections()

            return createdSection

        } catch (err) {
            console.error(err)
            error.value = err.message

            return null
        }
    }

    async function saveSection(id, section) {
        error.value = null

        try {
            const updatedSection =
                await updateMenuSection(
                    id,
                    section
                )

            sections.value =
                sections.value.map(existing =>
                    existing.id === updatedSection.id
                        ? updatedSection
                        : existing
                )

            sortSections()

            return updatedSection

        } catch (err) {
            console.error(err)
            error.value = err.message

            return null
        }
    }

    async function fetchItems(sectionId) {
        error.value = null

        try {
            const items =
                await getMenuItems(sectionId)

            itemsBySection.value = {
                ...itemsBySection.value,
                [sectionId]: items
            }

            sortItems(sectionId)

            return itemsBySection.value[sectionId]

        } catch (err) {
            console.error(err)
            error.value = err.message

            return []
        }
    }

    async function addItem(sectionId, item) {
        error.value = null

        try {
            const createdItem =
                await createMenuItem(
                    sectionId,
                    item
                )

            const existingItems =
                itemsBySection.value[sectionId] ?? []

            itemsBySection.value = {
                ...itemsBySection.value,
                [sectionId]: [
                    ...existingItems,
                    createdItem
                ]
            }

            sortItems(sectionId)

            return createdItem

        } catch (err) {
            console.error(err)
            error.value = err.message

            return null
        }
    }

    async function saveItem(
        sectionId,
        menuItemId,
        item
    ) {
        error.value = null

        try {
            const updatedItem =
                await updateMenuItem(
                    sectionId,
                    menuItemId,
                    item
                )

            const existingItems =
                itemsBySection.value[sectionId] ?? []

            itemsBySection.value = {
                ...itemsBySection.value,
                [sectionId]:
                    existingItems.map(existing =>
                        existing.id === updatedItem.id
                            ? updatedItem
                            : existing
                    )
            }

            sortItems(sectionId)

            return updatedItem

        } catch (err) {
            console.error(err)
            error.value = err.message

            return null
        }
    }

    return {
        sections,
        itemsBySection,
        loading,
        error,
        fetchSections,
        addSection,
        saveSection,
        fetchItems,
        addItem,
        saveItem,
        clearError
    }
})