<template>
    <form
        class="admin-form menu-item-form"
        @submit.prevent="submitForm"
    >
        <h3>
            {{ editing ? 'Edit Menu Item' : 'Add Menu Item' }}
        </h3>

        <RecipePicker
            v-model="form.recipeId"
            @cleared="handleRecipeCleared"
        />

        <div>
            <h4>Prices</h4>

            <div
                v-for="(price, index) in form.prices"
                :key="index"
                class="price-row"
            >
                <label>
                    Label

                    <input
                        v-model="price.label"
                        type="text"
                        maxlength="100"
                        placeholder="Optional, e.g. Regular"
                    >
                </label>

                <label>
                    Amount

                    <input
                        v-model="price.amount"
                        type="number"
                        min="0.01"
                        step="0.01"
                        required
                    >
                </label>

                <button
                    v-if="form.prices.length > 1"
                    type="button"
                    @click="removePrice(index)"
                >
                    Remove
                </button>
            </div>

            <button
                type="button"
                @click="addPrice"
            >
                + Add Size / Price
            </button>
        </div>

        <label>
            Display order

            <input
                v-model.number="form.displayOrder"
                type="number"
                min="0"
                required
            >
        </label>

        <label class="visible-control">
            <input
                v-model="form.visible"
                type="checkbox"
            >

            Visible on menu
        </label>

        <div class="admin-form-actions">
            <button
                type="submit"
                class="primary-button"
                :disabled="saving"
            >
                {{
                    saving
                        ? 'Saving...'
                        : editing
                            ? 'Save Changes'
                            : 'Add Menu Item'
                }}
            </button>

            <button
                type="button"
                :disabled="saving"
                @click="$emit('cancel')"
            >
                Cancel
            </button>
        </div>
    </form>
</template>

<script setup>
import {
    computed,
    reactive,
    watch
} from 'vue'

import RecipePicker from '@/components/admin/RecipePicker.vue'

const props = defineProps({
    item: {
        type: Object,
        default: null
    },

    saving: {
        type: Boolean,
        default: false
    },

    defaultDisplayOrder: {
        type: Number,
        default: 0
    }
})

const emit = defineEmits([
    'submit',
    'cancel'
])

const editing = computed(() => Boolean(props.item))

const form = reactive({
    recipeId: null,
    displayOrder: 0,
    visible: true,
    prices: [
        {
            label: '',
            amount: null,
            displayOrder: 0
        }
    ]
})

watch(
    () => props.item,
    item => {
        populateForm(item)
    },
    {
        immediate: true
    }
)

watch(
    () => props.defaultDisplayOrder,
    value => {
        if (!props.item) {
            form.displayOrder = value
        }
    }
)

function populateForm(item) {
    if (!item) {
        resetForm()
        return
    }

    form.recipeId = item.recipeId
    form.displayOrder = item.displayOrder
    form.visible = item.visible

    form.prices = item.prices.map(
        (price, index) => ({
            label: price.label ?? '',
            amount: price.amount,
            displayOrder: index
        })
    )
}

function resetForm() {
    form.recipeId = null
    form.displayOrder = props.defaultDisplayOrder
    form.visible = true

    form.prices = [
        {
            label: '',
            amount: null,
            displayOrder: 0
        }
    ]
}

function handleRecipeCleared() {
    // Menu-specific details stay intact when changing Recipe.
    // This allows staff to correct a Recipe selection without
    // re-entering prices and display order.
}

function addPrice() {
    form.prices.push({
        label: '',
        amount: null,
        displayOrder: form.prices.length
    })
}

function removePrice(index) {
    form.prices.splice(index, 1)

    form.prices.forEach((price, priceIndex) => {
        price.displayOrder = priceIndex
    })
}

function submitForm() {
    if (!form.recipeId) {
        return
    }

    emit('submit', {
        recipeId: form.recipeId,
        displayOrder: form.displayOrder,
        visible: form.visible,

        prices: form.prices.map(
            (price, index) => ({
                label:
                    price.label?.trim()
                        ? price.label.trim()
                        : null,

                amount: Number(price.amount),
                displayOrder: index
            })
        )
    })
}
</script>

<style scoped>
.menu-item-form {
    grid-template-columns: 1fr;
}

.menu-item-form h3,
.menu-item-form h4 {
    margin: 0;
}

.menu-item-form > div {
    display: grid;
    gap: 0.75rem;
}

.price-row {
    display: grid;
    grid-template-columns: minmax(0, 1fr) 130px auto;
    gap: 0.75rem;
    align-items: end;

    padding: 0.75rem;

    border: 1px solid rgba(255, 255, 255, 0.12);
    border-radius: 0.4rem;
}

.price-row label {
    display: grid;
    gap: 0.4rem;
}

.visible-control {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.visible-control input {
    width: auto;
}

@media (max-width: 600px) {
    .price-row {
        grid-template-columns: 1fr;
    }
}
</style>