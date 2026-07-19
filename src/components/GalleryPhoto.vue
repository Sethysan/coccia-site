<!-- <template>
    <article class="gallery-card">
        <button
            type="button"
            class="gallery-photo-button"
            :aria-label="`Open ${photo.alt} in full-screen gallery`"
            @click="$emit('open')"
        >
            <div class="gallery-image-frame">
                <img
                    :src="photo.src"
                    :alt="photo.alt"
                    class="gallery-image"
                />

                <span class="gallery-position">
                    {{ position }} of {{ total }}
                </span>

                <footer
                    v-if="photo.caption"
                    class="gallery-caption"
                >
                    {{ photo.caption }}
                </footer>
            </div>
        </button>
    </article>
</template>

<script setup>
defineProps({
    photo: {
        type: Object,
        required: true
    },

    position: {
        type: Number,
        required: true
    },

    total: {
        type: Number,
        required: true
    }
})

defineEmits(['open'])
</script>

<style scoped>
.gallery-card {
    position: relative;

    width: 100%;
    max-width: 100%;
    margin: 1rem auto;

    overflow: hidden;

    background-color: #000;

    border: 1px solid var(--bronze-color);
    border-radius: 8px;

    box-shadow: 0 25px 55px rgba(0, 0, 0, 0.48);

    transition:
        transform 0.25s ease,
        box-shadow 0.25s ease;
}

.gallery-card:hover {
    transform: scale(1.015);
    box-shadow: 0 30px 65px rgba(0, 0, 0, 0.6);
}

.gallery-photo-button {
    display: block;

    width: 100%;
    padding: 0;

    cursor: pointer;

    color: inherit;
    background: transparent;

    border: 0;
    border-radius: inherit;
}

.gallery-photo-button:focus-visible {
    outline: 3px solid var(--bronze-bold);
    outline-offset: 4px;
}

.gallery-image-frame {
    position: relative;

    display: flex;
    align-items: center;
    justify-content: center;

    width: 100%;
    height: 500px;

    overflow: hidden;

    background-color: #000;
    border-radius: inherit;
}

.gallery-image {
    display: block;

    width: 100%;
    height: 100%;

    object-fit: contain;
    object-position: center;
}

.gallery-position {
    position: absolute;
    top: 0.75rem;
    right: 0.75rem;
    z-index: 4;

    padding: 0.4rem 0.7rem;

    color: #fff;
    background-color: rgba(0, 0, 0, 0.75);

    border: 1px solid rgba(255, 255, 255, 0.25);
    border-radius: 999px;

    font-size: 0.8rem;
    font-weight: 600;
    line-height: 1;
    letter-spacing: 0.03em;
}

.gallery-caption {
    position: absolute;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 3;

    margin: 0;
    padding: 3rem 1rem 1rem;

    color: #fff;
    background:
        linear-gradient(
            to top,
            rgba(0, 0, 0, 0.95),
            rgba(0, 0, 0, 0.62) 55%,
            transparent
        );

    font-size: 1rem;
    line-height: 1.4;
    text-align: left;
}

@media (max-width: 700px) {
    .gallery-image-frame {
        height: 340px;
    }

    .gallery-caption {
        padding: 2.5rem 0.85rem 0.85rem;
        font-size: 0.9rem;
    }
}
</style> -->