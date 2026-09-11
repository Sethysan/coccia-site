<template>
    <div class="pizza-specialty-form">

        <!-- =====================================================
         RECIPE
         ===================================================== -->

        <section class="specialty-form-section">
            <h5>Recipe</h5>

            <RecipePicker v-model="selectedRecipeId" @create-requested="beginCreateRecipe" />
            <div v-if="showCreateRecipe" class="inline-recipe-form">
                <h5>Create New Recipe</h5>

                <label>
                    Recipe name

                    <input v-model="newRecipeName" type="text" maxlength="150" required />
                </label>

                <label>
                    Description

                    <textarea v-model="newRecipeDescription" rows="3"
                        placeholder="Describe the specialty pizza for customers."></textarea>
                </label>

                <label>
                    Image description

                    <input v-model="newRecipeImageAlt" type="text" maxlength="255"
                        placeholder="Optional description of the photo" />
                </label>

                <div class="specialty-form-actions">
                    <button type="button" class="primary-button" :disabled="creatingRecipe" @click="createRecipe">
                        {{
                            creatingRecipe
                                ? 'Creating...'
                                : 'Create & Select Recipe'
                        }}
                    </button>

                    <button type="button" :disabled="creatingRecipe" @click="cancelCreateRecipe">
                        Cancel
                    </button>
                </div>
            </div>

        </section>

        <!-- =====================================================
         PRICING METHOD
         ===================================================== -->

        <section class="specialty-form-section">
            <h5>Pricing</h5>

            <label class="pricing-option">
                <input v-model="pricingMode" type="radio" value="CALCULATED" />

                <div>
                    <strong>Calculate from toppings</strong>

                    <p>
                        Uses the pizza size prices and per-item charge.
                    </p>
                </div>
            </label>

            <label class="pricing-option">
                <input v-model="pricingMode" type="radio" value="CUSTOM" />

                <div>
                    <strong>Enter prices manually</strong>

                    <p>
                        Use this when the specialty has its own set price.
                    </p>
                </div>
            </label>
        </section>

        <!-- =====================================================
     INCLUDED TOPPINGS
     ===================================================== -->

        <section class="specialty-form-section">
            <div>
                <h5>Included Toppings</h5>

                <p class="section-help">
                    Select the toppings that come standard on this pizza.
                </p>
            </div>

            <div v-if="activeToppings.length" class="topping-options">
                <label v-for="topping in activeToppings" :key="topping.id" class="topping-option">
                    <input v-model="selectedToppingIds" type="checkbox" :value="topping.id" />

                    <span>{{ topping.name }}</span>
                </label>
            </div>

            <p v-else class="section-help">
                No active pizza toppings are available.
            </p>
        </section>

        <!-- =====================================================
     CALCULATED PRICE PREVIEW
     ===================================================== -->

        <section v-if="pricingMode === 'CALCULATED'" class="specialty-form-section">
            <div>
                <h5>Calculated Prices</h5>

                <p class="section-help">
                    Based on the selected toppings and current
                    pizza pricing.
                </p>
            </div>

            <div v-if="activeToppingCharge" class="calculated-price-list">
                <div v-for="size in calculatedPrices" :key="size.id" class="calculated-price-row">
                    <span>{{ size.name }}</span>

                    <strong>
                        ${{ size.amount.toFixed(2) }}
                    </strong>
                </div>
            </div>

            <p v-else class="section-help">
                No active per-item topping charge is available.
            </p>
        </section>

        <!-- =====================================================
     CUSTOM PRICES
     ===================================================== -->

        <section v-if="pricingMode === 'CUSTOM'" class="specialty-form-section">
            <div>
                <h5>Specialty Prices</h5>

                <p class="section-help">
                    Enter the price for each pizza size.
                </p>
            </div>

            <div class="custom-price-list">
                <label v-for="size in sizes.filter(size => size.active)" :key="size.id" class="custom-price-row">
                    <span>{{ size.name }}</span>

                    <div class="custom-price-input">
                        <span>$</span>

                        <input v-model="customPrices[size.id]" type="number" min="0.01" step="0.01"
                            placeholder="0.00" />
                    </div>
                </label>
            </div>
        </section>

        <!-- =====================================================
     OPTIONAL ADD-ON NOTES
     ===================================================== -->

        <section class="specialty-form-section">
            <div>
                <h5>Optional Add-ons</h5>

                <p class="section-help">
                    Select any add-ons you want mentioned beneath
                    this specialty pizza.
                </p>
            </div>

            <div v-if="activeExtras.length" class="specialty-extra-list">
                <div v-for="extra in activeExtras" :key="extra.id" class="specialty-extra">
                    <label class="specialty-extra__toggle">
                        <input v-model="selectedExtraIds" type="checkbox" :value="extra.id" />

                        <strong>{{ extra.name }}</strong>
                    </label>

                    <div v-if="selectedExtraIds.includes(extra.id)" class="specialty-extra__pricing">
                        <p class="section-help">
                            Price for this specialty:
                        </p>

                        <label>
                            <input v-model="extraPricing[extra.id]" type="radio" value="STANDARD" />

                            Regular price
                            (${{ Number(extra.amount).toFixed(2) }})
                        </label>

                        <label>
                            <input v-model="extraPricing[extra.id]" type="radio" value="FREE" />

                            Included free
                        </label>

                        <label>
                            <input v-model="extraPricing[extra.id]" type="radio" value="CUSTOM" />

                            Different price
                        </label>

                        <div v-if="extraPricing[extra.id] === 'CUSTOM'" class="specialty-extra__custom-price">
                            <span>$</span>

                            <input v-model="extraPricing[`${extra.id}-amount`]" type="number" min="0" step="0.01"
                                placeholder="0.00" />
                        </div>

                        <div v-if="addOnPreview(extra)" class="customer-note-preview">
                            <strong>Customer will see:</strong>

                            <p>
                                “{{ addOnPreview(extra) }}”
                            </p>
                        </div>
                    </div>
                </div>
            </div>

            <p v-else class="section-help">
                No active pizza extras are available.
            </p>

            <div class="specialty-form-actions">
                <button type="button" class="primary-button" @click="submitSpecialty">
                    Save Specialty Pizza
                </button>
            </div>

        </section>

    </div>
</template>

<script setup>
import {
    computed,
    ref,
    watch
} from 'vue'
import RecipePicker from './RecipePicker.vue'
import { useRecipeStore } from '@/stores/recipeStore'

const props = defineProps({
    specialty: {
        type: Object,
        default: null,
    },
    sizes: {
        type: Array,
        default: () => [],
    },

    addOns: {
        type: Array,
        default: () => [],
    },

    toppings: {
        type: Array,
        default: () => [],
    },
})

const emit = defineEmits([
    'submit'
])

const recipeStore = useRecipeStore()

const showCreateRecipe = ref(false)
const newRecipeName = ref('')
const newRecipeDescription = ref('')
const newRecipeImageAlt = ref('')
const creatingRecipe = ref(false)

const saving = ref(false)

const selectedRecipeId = ref(null)
const pricingMode = ref('CALCULATED')

const selectedToppingIds = ref([])
const customPrices = ref({})

const selectedExtraIds = ref([])
const extraPricing = ref({})

function populateSpecialtyForm(specialty) {
    if (!specialty) {
        selectedRecipeId.value = null
        pricingMode.value = 'CALCULATED'
        selectedToppingIds.value = []
        customPrices.value = {}
        selectedExtraIds.value = []
        extraPricing.value = {}

        return
    }

    selectedRecipeId.value =
        specialty.recipeId

    pricingMode.value =
        specialty.pricingMode

    selectedToppingIds.value =
        [...(specialty.toppingIds ?? [])]

    customPrices.value = {}

    for (const price of specialty.prices ?? []) {
        customPrices.value[price.pizzaSizeId] =
            price.amount
    }

    selectedExtraIds.value =
        (specialty.addOns ?? []).map(
            addOn => addOn.pizzaAddOnId
        )

    extraPricing.value = {}

    for (const addOn of specialty.addOns ?? []) {
        extraPricing.value[addOn.pizzaAddOnId] =
            addOn.pricingType

        if (
            addOn.pricingType === 'CUSTOM'
        ) {
            extraPricing.value[
                `${addOn.pizzaAddOnId}-amount`
            ] = addOn.overrideAmount
        }
    }
}

watch(
    () => props.specialty,
    specialty => {
        populateSpecialtyForm(specialty)
    },
    {
        immediate: true
    }
)

const activeToppings = computed(() =>
    props.toppings.filter(
        topping => topping.active
    )
)

const activeToppingCharge = computed(() =>
    props.addOns.find(
        addOn =>
            addOn.active &&
            addOn.type === 'TOPPING'
    )
)

const activeExtras = computed(() =>
    props.addOns.filter(
        addOn =>
            addOn.active &&
            addOn.type === 'EXTRA'
    )
)

const calculatedPrices = computed(() => {
    const toppingCharge =
        Number(activeToppingCharge.value?.amount ?? 0)

    const toppingCount =
        selectedToppingIds.value.length

    return props.sizes
        .filter(size => size.active)
        .map(size => ({
            id: size.id,
            name: size.name,
            amount:
                Number(size.basePrice) +
                toppingCount * toppingCharge,
        }))
})

function addOnPreview(extra) {
    const pricingType = extraPricing.value[extra.id]

    if (!pricingType) {
        return ''
    }

    if (pricingType === 'FREE') {
        return `Add ${extra.name} at no additional charge.`
    }

    if (pricingType === 'STANDARD') {
        return `Add ${extra.name} for an additional $${Number(
            extra.amount
        ).toFixed(2)}.`
    }

    if (pricingType === 'CUSTOM') {
        const customAmount =
            extraPricing.value[`${extra.id}-amount`]

        if (
            customAmount === '' ||
            customAmount === null ||
            customAmount === undefined
        ) {
            return ''
        }

        return `Add ${extra.name} for an additional $${Number(
            customAmount
        ).toFixed(2)}.`
    }

    return ''
}

function submitSpecialty() {
    if (!selectedRecipeId.value) {
        window.alert('Please select or create a Recipe before saving.')
        return
    }

    const addOns = selectedExtraIds.value.map(
        extraId => {
            const pricingType =
                extraPricing.value[extraId]

            return {
                pizzaAddOnId: extraId,
                pricingType,
                overrideAmount:
                    pricingType === 'CUSTOM'
                        ? Number(
                            extraPricing.value[
                            `${extraId}-amount`
                            ]
                        )
                        : null
            }
        }
    )

    const prices =
        pricingMode.value === 'CUSTOM'
            ? props.sizes
                .filter(size => size.active)
                .map(size => ({
                    pizzaSizeId: size.id,
                    amount: Number(
                        customPrices.value[size.id]
                    )
                }))
            : []

    emit('submit', {
        recipeId: selectedRecipeId.value,
        active: true,
        pricingMode: pricingMode.value,

        toppingIds:
            pricingMode.value === 'CALCULATED'
                ? [...selectedToppingIds.value]
                : [],

        addOns,
        prices
    })
}

function beginCreateRecipe(name) {
    newRecipeName.value = name
    newRecipeDescription.value = ''
    newRecipeImageAlt.value = ''
    showCreateRecipe.value = true

    recipeStore.clearError()
}

function cancelCreateRecipe() {
    showCreateRecipe.value = false
    newRecipeName.value = ''
    newRecipeDescription.value = ''
    newRecipeImageAlt.value = ''

    recipeStore.clearError()
}

async function createRecipe() {
    const name = newRecipeName.value.trim()

    if (!name) {
        return
    }

    creatingRecipe.value = true
    recipeStore.clearError()

    try {
        const createdRecipe =
            await recipeStore.addRecipe(
                name,
                newRecipeDescription.value,
                newRecipeImageAlt.value
            )

        if (createdRecipe) {
            selectedRecipeId.value = createdRecipe.id
            cancelCreateRecipe()
        }
    } finally {
        creatingRecipe.value = false
    }
}

</script>

<style scoped>
.pizza-specialty-form {
    display: grid;
    gap: 1rem;
}

.specialty-form-section {
    display: grid;
    gap: 0.75rem;
}

.specialty-form-section h5 {
    margin: 0;
}

.pricing-option {
    display: flex;
    align-items: flex-start;
    gap: 0.75rem;
    padding: 0.85rem;
    border: 1px solid var(--border-color, #d8d8d8);
    border-radius: 0.5rem;
    cursor: pointer;
}

.pricing-option input {
    margin-top: 0.25rem;
}

.pricing-option p {
    margin: 0.2rem 0 0;
    font-size: 0.9rem;
}

.section-help {
    margin: 0.25rem 0 0;
    font-size: 0.9rem;
}

.topping-options {
    display: grid;
    grid-template-columns:
        repeat(auto-fit, minmax(180px, 1fr));
    gap: 0.5rem;
}

.topping-option {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    padding: 0.65rem 0.75rem;
    border: 1px solid var(--border-color, #d8d8d8);
    border-radius: 0.5rem;
    cursor: pointer;
}

.topping-option input {
    margin: 0;
}

.calculated-price-list {
    display: grid;
    gap: 0.5rem;
}

.calculated-price-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
    padding: 0.65rem 0.75rem;
    border: 1px solid var(--border-color, #d8d8d8);
    border-radius: 0.5rem;
}

.custom-price-list {
    display: grid;
    gap: 0.5rem;
}

.custom-price-row {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 1rem;
    padding: 0.65rem 0.75rem;
    border: 1px solid var(--border-color, #d8d8d8);
    border-radius: 0.5rem;
}

.custom-price-input {
    display: flex;
    align-items: center;
    gap: 0.25rem;
}

.custom-price-input input {
    width: 7rem;
}

.specialty-extra-list {
    display: grid;
    gap: 0.75rem;
}

.specialty-extra {
    padding: 0.75rem;
    border: 1px solid var(--border-color, #d8d8d8);
    border-radius: 0.5rem;
}

.specialty-extra__toggle {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.specialty-extra__pricing {
    display: grid;
    gap: 0.5rem;
    margin-top: 0.75rem;
    padding-left: 1.5rem;
}

.specialty-extra__pricing label {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.specialty-extra__custom-price {
    display: flex;
    align-items: center;
    gap: 0.25rem;
    margin-left: 1.5rem;
}

.specialty-extra__custom-price input {
    width: 7rem;
}

.customer-note-preview {
    margin-top: 0.75rem;
    padding: 0.75rem;
    border-left: 3px solid currentColor;
}

.customer-note-preview p {
    margin: 0.25rem 0 0;
}

.specialty-form-actions {
    display: flex;
    justify-content: flex-end;
    margin-top: 0.5rem;
}

.inline-recipe-form {
    display: grid;
    gap: 0.75rem;
    padding: 1rem;
    border: 1px solid var(--border-color, #d8d8d8);
    border-radius: 0.5rem;
}

.inline-recipe-form label {
    display: grid;
    gap: 0.4rem;
}
</style>