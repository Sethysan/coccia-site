import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
    getMenuSections,
    createMenuSection,
    updateMenuSection,
    getMenuItems,
    createMenuItem,
    updateMenuItem,
    moveMenuItem,
    getPizzaSizes,
    createPizzaSize,
    updatePizzaSize,

    getPizzaAddOns,
    createPizzaAddOn,
    updatePizzaAddOn,

    getPizzaToppings,
    createPizzaTopping,
    updatePizzaTopping,

    getPizzaSpecialties,
    createPizzaSpecialty,
    updatePizzaSpecialty,
} from '@/api/menuApi'

export const useMenuStore = defineStore('menu', () => {
    const sections = ref([])
    const itemsBySection = ref({})
    const loading = ref(false)
    const error = ref(null)

    const pizzaSizes = ref([])
    const pizzaAddOns = ref([])
    const pizzaToppings = ref([])
    const pizzaSpecialties = ref([])

    const pizzaLoading = ref(false)

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

    async function moveItem(sectionId, menuItemId, direction) {
        clearError()

        try {
            const items = await moveMenuItem(
                sectionId,
                menuItemId,
                direction
            )

            itemsBySection.value[sectionId] = items

            return items
        } catch (err) {
            error.value =
                err.message || 'Unable to move menu item.'

            return null
        }
    }

    async function loadPizzaConfiguration(menuSectionId) {
        pizzaLoading.value = true

        try {
            const [
                sizes,
                addOns,
                toppings,
                specialties,
            ] = await Promise.all([
                getPizzaSizes(menuSectionId),
                getPizzaAddOns(menuSectionId),
                getPizzaToppings(menuSectionId),
                getPizzaSpecialties(menuSectionId),
            ])

            pizzaSizes.value = sizes
            pizzaAddOns.value = addOns
            pizzaToppings.value = toppings
            pizzaSpecialties.value = specialties
        } finally {
            pizzaLoading.value = false
        }
    }

    async function addPizzaSize(menuSectionId, data) {
        const created =
            await createPizzaSize(menuSectionId, data)

        pizzaSizes.value.push(created)

        return created
    }

    async function editPizzaSize(
        menuSectionId,
        pizzaSizeId,
        data
    ) {
        const updated =
            await updatePizzaSize(
                menuSectionId,
                pizzaSizeId,
                data
            )

        const index =
            pizzaSizes.value.findIndex(
                size => size.id === pizzaSizeId
            )

        if (index !== -1) {
            pizzaSizes.value[index] = updated
        }

        return updated
    }

    async function addPizzaAddOn(menuSectionId, data) {
        const created =
            await createPizzaAddOn(menuSectionId, data)

        pizzaAddOns.value.push(created)

        return created
    }

    async function editPizzaAddOn(
        menuSectionId,
        pizzaAddOnId,
        data
    ) {
        const updated =
            await updatePizzaAddOn(
                menuSectionId,
                pizzaAddOnId,
                data
            )

        const index =
            pizzaAddOns.value.findIndex(
                addOn => addOn.id === pizzaAddOnId
            )

        if (index !== -1) {
            pizzaAddOns.value[index] = updated
        }

        return updated
    }

    async function addPizzaTopping(menuSectionId, data) {
        const created =
            await createPizzaTopping(menuSectionId, data)

        pizzaToppings.value.push(created)

        return created
    }

    async function editPizzaTopping(
        menuSectionId,
        pizzaToppingId,
        data
    ) {
        const updated =
            await updatePizzaTopping(
                menuSectionId,
                pizzaToppingId,
                data
            )

        const index =
            pizzaToppings.value.findIndex(
                topping => topping.id === pizzaToppingId
            )

        if (index !== -1) {
            pizzaToppings.value[index] = updated
        }

        return updated
    }

    async function addPizzaSpecialty(
        menuSectionId,
        data
    ) {
        const created =
            await createPizzaSpecialty(
                menuSectionId,
                data
            )

        pizzaSpecialties.value.push(created)

        return created
    }

    async function editPizzaSpecialty(
        menuSectionId,
        specialtyId,
        data
    ) {
        const updated =
            await updatePizzaSpecialty(
                menuSectionId,
                specialtyId,
                data
            )

        const index =
            pizzaSpecialties.value.findIndex(
                specialty =>
                    specialty.id === specialtyId
            )

        if (index !== -1) {
            pizzaSpecialties.value[index] = updated
        }

        return updated
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
        clearError,
        moveItem,
        pizzaSizes,
        pizzaAddOns,
        pizzaToppings,
        pizzaSpecialties,
        pizzaLoading,

        loadPizzaConfiguration,

        addPizzaSize,
        editPizzaSize,

        addPizzaAddOn,
        editPizzaAddOn,

        addPizzaTopping,
        editPizzaTopping,

        addPizzaSpecialty,
        editPizzaSpecialty,
    }
})