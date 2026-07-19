<template>

    <section class="home-gallery" aria-labelledby="gallery-title">

        <!-- <h2 id="gallery-title">From the Family Album</h2> -->

        <Swiper :modules="swiperModules" :slides-per-view="1" :space-between="16" :navigation="true"
            :pagination="{ clickable: true }" :keyboard="{ enabled: true }" :grab-cursor="true" :breakpoints="{
                700: {
                    slidesPerView: 2
                }
            }" class="home-gallery-swiper">
            <SwiperSlide v-for="(photo, index) in galleryPhotos" :key="photo.src">
                <figure class="gallery-photo">
                    <button type="button" class="gallery-slide" :aria-label="`Open ${photo.alt} in full screen`"
                        @click="openGallery(index)">
                        <img :src="photo.src" :alt="photo.alt" loading="lazy" />
                        <figcaption>
                            {{ photo.caption }}
                        </figcaption>
                        <!-- <span class="gallery-expand" aria-hidden="true">
                            ⛶
                        </span> -->
                    </button>
                    </figure>
            </SwiperSlide>
        </Swiper>
    </section>

    <Teleport to="body">
        <div v-if="isGalleryOpen" class="gallery-lightbox" role="dialog" aria-modal="true"
            aria-label="Full-screen Coccia House photo gallery" @click.self="closeGallery">
            <button ref="closeButton" type="button" class="lightbox-close" aria-label="Close full-screen gallery"
                @click="closeGallery">
                ×
            </button>

            <Swiper :modules="swiperModules" :initial-slide="activePhotoIndex" :slides-per-view="1" :space-between="20"
                :navigation="true" :pagination="{ type: 'fraction' }" :keyboard="{ enabled: true }" :grab-cursor="true"
                class="lightbox-swiper">
                <SwiperSlide v-for="photo in galleryPhotos" :key="`lightbox-${photo.src}`">
                    <figure class="lightbox-photo">
                        <img :src="photo.src" :alt="photo.alt" />

                        <figcaption>
                            {{ photo.caption }}
                        </figcaption>
                    </figure>
                </SwiperSlide>
            </Swiper>
        </div>
    </Teleport>
</template>
<script setup>
import { nextTick, onBeforeUnmount, ref } from 'vue'

import { Swiper, SwiperSlide } from 'swiper/vue'
import {
    A11y,
    Keyboard,
    Navigation,
    Pagination
} from 'swiper/modules'

import 'swiper/css'
import 'swiper/css/navigation'
import 'swiper/css/pagination'

import { galleryPhotos } from '@/data/gallery'

const swiperModules = [
    Navigation,
    Pagination,
    Keyboard
]
const isGalleryOpen = ref(false)
const activePhotoIndex = ref(0)
const closeButton = ref(null)

function openGallery(index) {
    activePhotoIndex.value = index
    isGalleryOpen.value = true
    document.body.classList.add('gallery-open')

    nextTick(() => {
        closeButton.value?.focus()
    })
}

function closeGallery() {
    isGalleryOpen.value = false
    document.body.classList.remove('gallery-open')
}

function handleKeydown(event) {
    if (event.key === 'Escape' && isGalleryOpen.value) {
        closeGallery()
    }
}

window.addEventListener('keydown', handleKeydown)

onBeforeUnmount(() => {
    window.removeEventListener('keydown', handleKeydown)
    document.body.classList.remove('gallery-open')
})
</script>

<style>
.home-gallery {
    width: 100%;
    max-width: 900px;
    margin: 5rem auto;
}

.home-gallery h2 {
    margin-bottom: 1.25rem;
}

.home-gallery-swiper {
    width: 100%;
    padding: 0 3rem 2.75rem;
}

.gallery-slide {
    position: relative;
    /* display: flex;
    align-items: center;
    justify-content: center; */

    width: 100%;
    height: 400px;
    padding: 0;

    overflow: hidden;
    cursor: zoom-in;

    background-color: rgba(20, 15, 12, 0.75);
    border: 1px solid var(--bronze-color);
    border-radius: 0.5rem;
}

.gallery-slide img {
    display: block;
    width: 100%;
    height: 320px;

    object-fit: contain;

    background-color: rgba(20, 15, 12, 0.75);

    transition: transform 250ms ease;
}

.gallery-slide:hover img,
.gallery-slide:focus-visible img {
    transform: scale(1.03);
}

.gallery-expand {
    position: absolute;
    right: 0.75rem;
    bottom: 0.75rem;

    display: flex;
    align-items: center;
    justify-content: center;

    width: 2.25rem;
    height: 2.25rem;

    color: var(--default-color);
    background-color: var(--background-dark-trans);

    border: 1px solid var(--bronze-color);
    border-radius: 50%;

    font-size: 1.2rem;
}

.home-gallery-swiper :deep(.swiper-button-prev),
.home-gallery-swiper :deep(.swiper-button-next) {
    color: var(--bronze-bold);
}

.swiper-button-prev,
.swiper-button-next {
    color: var(--bronze-bold);
}

.home-gallery-swiper :deep(.swiper-pagination-bullet) {
    background-color: var(--default-color);
    color: var(--bronze-bold);
    opacity: 0.45;
}

.home-gallery-swiper .swiper-pagination-bullet-active {
    background-color: var(--bronze-bold);
    opacity: 1;
}

@media (max-width: 700px) {
    .home-gallery-swiper {
        padding-right: 2.25rem;
        padding-left: 2.25rem;
    }

    .gallery-slide {
        height: 340px;
    }

    .gallery-slide img {
        height: 280px;
    }
}

@media (max-width: 450px) {
    .home-gallery-swiper {
        padding-right: 0;
        padding-left: 0;
    }

    .home-gallery-swiper :deep(.swiper-button-prev),
    .home-gallery-swiper :deep(.swiper-button-next) {
        display: none;
    }
}

body.gallery-open {
    overflow: hidden;
}

.gallery-lightbox {
    position: fixed;
    inset: 0;
    z-index: 5000;

    display: flex;
    align-items: center;
    justify-content: center;

    padding: 4rem 3.5rem 2rem;

    background-color: rgba(8, 6, 5, 0.96);
}

.lightbox-close {
    position: absolute;
    top: 1rem;
    right: 1.25rem;
    z-index: 5100;

    display: flex;
    align-items: center;
    justify-content: center;

    width: 3rem;
    height: 3rem;

    cursor: pointer;

    color: #fff;
    background-color: rgba(20, 15, 12, 0.8);

    border: 1px solid rgba(255, 255, 255, 0.45);
    border-radius: 50%;

    font-size: 2rem;
    line-height: 1;
}

.lightbox-close:hover,
.lightbox-close:focus-visible {
    background-color: var(--bronze-bold);
}

.lightbox-swiper {
    width: min(1200px, 100%);
    height: 100%;
}

.lightbox-photo,
.gallery-photo {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    width: 100%;
    height: 100%;
    margin: 0;
}

.lightbox-photo img,
.gallery-photo img {
    display: block;

    max-width: 100%;
    max-height: calc(100vh - 9rem);

    object-fit: contain;

    border-radius: 0.35rem;
}

.lightbox-photo figcaption,
.gallery-photo figcaption {
    margin-top: 0.75rem;

    color: #fff;
    text-align: center;
}

.lightbox-swiper .swiper-button-prev,
.lightbox-swiper .swiper-button-next {
    color: var(--bronze-bold);
}

.lightbox-swiper .swiper-pagination {
    top: 1rem;
    bottom: auto;
    font-size: 1rem;
    font-weight: 600;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.8);
    color: #fff;
}

@media (max-width: 700px) {
    .gallery-lightbox {
        padding: 4.5rem 0.75rem 2rem;
    }

    .lightbox-swiper .swiper-button-prev,
    .lightbox-swiper .swiper-button-next {
        display: none;
    }

    .lightbox-photo img,
    .gallery-photo img {
        max-height: calc(100vh - 10rem);
    }
}
</style>