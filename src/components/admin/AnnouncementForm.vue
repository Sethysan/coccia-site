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

        <div class="form-field">
            <label for="announcement-image">
                Image
            </label>

            <div v-if="imagePreviewUrl" class="current-announcement-image">
                <span>
                    {{
                        imageFile
                            ? "Selected image"
                    : "Current image"
                    }}
                </span>

                <img :src="imagePreviewUrl" :alt="form.imageAlt ||
                    'Current announcement image'
                    " />
            </div>

            <div class="announcement-image-picker">
                <input id="announcement-image" class="announcement-image-input" type="file" accept="image/*"
                    @change="handleImageChange" />

                <label for="announcement-image" class="announcement-image-button">
                    {{
                        imageFile || props.announcement?.imageUrl
                            ? "Replace Image"
                            : "Choose Image"
                    }}
                </label>

                <span v-if="imageFile" class="announcement-image-filename">
                    {{ imageFile.name }}
                </span>
            </div>

            <small v-if="props.announcement?.imageUrl && !imageFile">
                Choose a new image only if you want to replace the current one.
            </small>

            <small v-else>
                Optional · Maximum 5 MB
            </small>
        </div>

        <div class="form-field">
            <label for="announcement-image-alt">
                Image description
            </label>

            <input id="announcement-image-alt" v-model.trim="form.imageAlt" type="text" maxlength="255"
                placeholder="Describe what is shown in the image" />

            <small>
                Used for accessibility.
            </small>
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

import {
    onBeforeUnmount,
    reactive,
    ref
} from "vue"


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
        props.announcement?.displayOrder ?? 0,
    imageAlt:
        props.announcement?.imageAlt ?? ""
})

const imageFile = ref(null)

const imagePreviewUrl = ref(
    props.announcement?.imageUrl ?? null
)

function toInstant(value) {
    if (!value) {
        return null
    }

    return new Date(value).toISOString()
}

const validationError = ref(null)

function handleImageChange(event) {
    const file =
        event.target.files?.[0] ?? null

    if (
        imagePreviewUrl.value &&
        imagePreviewUrl.value !==
        props.announcement?.imageUrl
    ) {
        URL.revokeObjectURL(
            imagePreviewUrl.value
        )
    }

    imageFile.value = file

    imagePreviewUrl.value = file
        ? URL.createObjectURL(file)
        : props.announcement?.imageUrl ?? null
}

onBeforeUnmount(() => {
    if (
        imagePreviewUrl.value &&
        imagePreviewUrl.value !==
        props.announcement?.imageUrl
    ) {
        URL.revokeObjectURL(
            imagePreviewUrl.value
        )
    }
})

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
        announcement: {
            title: form.title,
            message: form.message,
            placement: form.placement,
            type: form.type,
            startDateTime: toInstant(form.startDateTime),
            endDateTime: toInstant(form.endDateTime),
            displayOrder: form.displayOrder,
            imageUrl:
                props.announcement?.imageUrl ?? null,
            imageAlt: form.imageAlt || null
        },
        imageFile: imageFile.value
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

.current-announcement-image {
    display: grid;
    gap: 0.5rem;
    max-width: 32rem;
}

.current-announcement-image span {
    font-size: 0.85rem;
    opacity: 0.75;
}

.current-announcement-image img {
    display: block;
    width: 100%;
    max-height: 18rem;
    object-fit: contain;

    border: 1px solid var(--bronze-bold);
    border-radius: 0.5rem;
}

.announcement-image-picker {
    display: flex;
    align-items: center;
    flex-wrap: wrap;
    gap: 0.75rem;
}

.announcement-image-input {
    position: absolute;

    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;

    overflow: hidden;
    clip: rect(0, 0, 0, 0);
    white-space: nowrap;

    border: 0;
}

.announcement-image-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;

    min-height: 2.75rem;
    padding: 0.65rem 1rem;

    color: var(--default-color);
    background-color: var(--background-dark-trans);

    border: 1px solid var(--bronze-bold);
    border-radius: 0.4rem;

    font-weight: 700;

    cursor: pointer;
}

.announcement-image-button:hover,
.announcement-image-button:focus-visible {
    color: var(--background-color);
    background-color: var(--bronze-bold);
}

.announcement-image-filename {
    opacity: 0.75;
    font-size: 0.9rem;
}

@media (max-width: 600px) {
    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>