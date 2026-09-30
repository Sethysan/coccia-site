<template>
    <div class="recipe-summary">
        <div class="recipe-summary-image">
            <img
                v-if="imageUrl"
                :src="imageUrl"
                :alt="imageAlt || name"
            >

            <div
                v-else
                class="recipe-summary-placeholder"
            >
                No Image
            </div>
        </div>

        <div class="recipe-summary-content">
            <h3>{{ name }}</h3>

            <p
                v-if="description"
                class="recipe-summary-description"
            >
                {{ description }}
            </p>

            <p
                v-else
                class="recipe-summary-empty"
            >
                No description added yet.
            </p>

            <slot />
        </div>
    </div>
</template>

<script setup>
defineProps({
    name: {
        type: String,
        required: true
    },

    description: {
        type: String,
        default: ''
    },

    imageUrl: {
        type: String,
        default: null
    },

    imageAlt: {
        type: String,
        default: ''
    }
})
</script>

<style scoped>
.recipe-summary {
    display: grid;
    grid-template-columns: 150px minmax(0, 1fr);
    gap: 1rem;
    align-items: start;
}

.recipe-summary-image,
.recipe-summary-placeholder {
    width: 150px;
    height: 125px;

    border-radius: 0.4rem;
}

.recipe-summary-image {
    overflow: hidden;
}

.recipe-summary-image img {
    display: block;

    width: 100%;
    height: 100%;

    object-fit: cover;
}

.recipe-summary-placeholder {
    display: flex;
    align-items: center;
    justify-content: center;

    background: rgba(255, 255, 255, 0.06);

    font-size: 0.8rem;
    opacity: 0.6;
}

.recipe-summary-content {
    min-width: 0;
}

.recipe-summary-content h3 {
    margin: 0 0 0.6rem;
}

.recipe-summary-description {
    margin: 0.4rem 0;

    line-height: 1.5;
    white-space: pre-line;
}

.recipe-summary-empty {
    margin: 0.4rem 0;

    font-style: italic;
    opacity: 0.6;
}

@media (max-width: 650px) {
    .recipe-summary {
        grid-template-columns: 1fr;
    }

    .recipe-summary-image,
    .recipe-summary-placeholder {
        width: 100%;
        height: 200px;
    }
}
</style>