<template>
    <form @submit.prevent="submitForm">
        <div>
            <label for="start-date">
                Start Date
            </label>

            <input
                id="start-date"
                v-model="form.startDate"
                type="date"
                required
            />
        </div>

        <div>
            <label for="end-date">
                End Date
            </label>

            <input
                id="end-date"
                v-model="form.endDate"
                type="date"
                required
            />
        </div>

        <button
            type="submit"
            :disabled="saving"
        >
            {{ saving ? 'Saving...' : submitLabel }}
        </button>

        <button
            type="button"
            @click="$emit('cancel')"
        >
            Cancel
        </button>
    </form>
</template>

<script setup>
import { reactive, watch } from 'vue'

const props = defineProps({
    saving: {
        type: Boolean,
        default: false
    },

    submitLabel: {
        type: String,
        default: 'Create Draft'
    },

    initialStartDate: {
        type: String,
        default: ''
    },

    initialEndDate: {
        type: String,
        default: ''
    }
})

const emit = defineEmits([
    'submit',
    'cancel'
])

const form = reactive({
    startDate: props.initialStartDate,
    endDate: props.initialEndDate
})

watch(
    () => [
        props.initialStartDate,
        props.initialEndDate
    ],
    ([startDate, endDate]) => {
        form.startDate = startDate
        form.endDate = endDate
    }
)

function submitForm() {
    emit('submit', {
        startDate: form.startDate,
        endDate: form.endDate
    })
}
</script>

<style scoped>
</style>