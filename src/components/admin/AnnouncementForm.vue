<template>
    <form class="announcement-form" @submit.prevent="handleSubmit">
        <div class="form-field">
            <label for="announcement-title">
                Title
            </label>

            <input id="announcement-title" v-model.trim="form.title" type="text" required />
        </div>

        <div class="form-field">
            <label for="announcement-message">
                Message
            </label>

            <textarea id="announcement-message" v-model.trim="form.message" rows="5" required />
        </div>

        <div class="form-grid">
            <div class="form-field">
                <label for="announcement-placement">
                    Placement
                </label>

                <select id="announcement-placement" v-model="form.placement" required>
                    <option value="BANNER">
                        Site Banner
                    </option>

                    <option value="NEWS">
                        Latest News
                    </option>
                </select>
            </div>

            <div class="form-field">
                <label for="announcement-type">
                    Type
                </label>

                <select id="announcement-type" v-model="form.type" required>
                    <option value="GENERAL">
                        General
                    </option>

                    <option value="INFO">
                        Information
                    </option>

                    <option value="EVENT">
                        Event
                    </option>

                    <option value="CLOSURE">
                        Closure
                    </option>

                    <option value="WARNING">
                        Warning
                    </option>
                </select>
            </div>
        </div>

        <div class="form-grid">
            <div class="form-field">
                <label for="announcement-start">
                    Start
                </label>

                <input id="announcement-start" v-model="form.startDateTime" type="datetime-local" />
            </div>

            <div class="form-field">
                <label for="announcement-end">
                    End
                </label>

                <input id="announcement-end" v-model="form.endDateTime" type="datetime-local" />

                <small>
                    Optional
                </small>
            </div>
        </div>

        <div class="form-field">
            <label for="announcement-order">
                Display Order
            </label>

            <input id="announcement-order" v-model.number="form.displayOrder" type="number" min="0" />
        </div>

        <p v-if="validationError" class="error-message" role="alert">
            {{ validationError }}
        </p>

        <div class="form-actions">
            <button type="button" @click="$emit('cancel')">
                Cancel
            </button>

            <button type="submit" class="primary-button" :disabled="saving">
                {{
                    saving
                        ? "Saving..."
                        : submitLabel
                }}
            </button>
        </div>
    </form>
</template>

<script setup>
import { reactive, ref } from "vue"

const props = defineProps({
    announcement: {
        type: Object,
        default: null
    },

    saving: {
        type: Boolean,
        default: false
    },

    submitLabel: {
        type: String,
        default: "Create Draft"
    }
})

const emit = defineEmits([
    "submit",
    "cancel"
])

const form = reactive({
    title: props.announcement?.title ?? "",
    message: props.announcement?.message ?? "",
    placement:
        props.announcement?.placement?.toUpperCase()
        ?? "BANNER",
    type:
        props.announcement?.type?.toUpperCase()
        ?? "GENERAL",
    startDateTime: toLocalDateTime(
        props.announcement?.startDateTime
    ),
    endDateTime: toLocalDateTime(
        props.announcement?.endDateTime
    ),
    displayOrder:
        props.announcement?.displayOrder ?? 0
})

function toInstant(value) {
    if (!value) {
        return null
    }

    return new Date(value).toISOString()
}

const validationError = ref(null)

function handleSubmit() {
    validationError.value = null

    if (
        form.startDateTime &&
        form.endDateTime &&
        new Date(form.endDateTime) <=
        new Date(form.startDateTime)
    ) {
        validationError.value =
            "End date and time must be after the start date and time."

        return
    }

    emit("submit", {
        title: form.title,
        message: form.message,
        placement: form.placement,
        type: form.type,
        startDateTime: toInstant(form.startDateTime),
        endDateTime: toInstant(form.endDateTime),
        displayOrder: form.displayOrder
    })
}

function toLocalDateTime(value) {
    if (!value) {
        return ""
    }

    const date = new Date(value)

    const offset = date.getTimezoneOffset()

    const localDate = new Date(
        date.getTime() - offset * 60 * 1000
    )

    return localDate
        .toISOString()
        .slice(0, 16)
}

</script>

<style scoped>
.announcement-form {
    display: grid;
    gap: 1.25rem;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 1rem;
}

.form-field {
    display: grid;
    gap: 0.4rem;
}

.form-field label {
    font-weight: 700;
}

.form-field textarea {
    resize: vertical;
}

.form-field small {
    opacity: 0.7;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 0.75rem;
}

@media (max-width: 600px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>