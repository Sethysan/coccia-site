<script setup>
import { onMounted } from "vue"
import fullPizza from "@/assets/loading/fullpizza.png"
import pizzaMinus1 from "@/assets/loading/pizzaMinus1.png"
import pizzaMinus2 from "@/assets/loading/pizzaMinus2.png"
import pizzaMinus3 from "@/assets/loading/pizzaMinus3.png"
import pizzaMinus4 from "@/assets/loading/pizzaMinus4.png"
import pizzaMinus5 from "@/assets/loading/pizzaMinus5.png"
import pizzaMinus6 from "@/assets/loading/pizzaMinus6.png"
import pizzaMinus7 from "@/assets/loading/pizzaMinus7.png"
import pizzaMinus8 from "@/assets/loading/pizzaMinus8.png"
import pizzaPlus0 from "@/assets/loading/pizzaPlus0.png"
import pizzaPlus1 from "@/assets/loading/pizzaPlus1.png"
import pizzaPlus2 from "@/assets/loading/pizzaPlus2.png"
import pizzaPlus3 from "@/assets/loading/pizzaPlus3.png"
import pizzaPlus4 from "@/assets/loading/pizzaPlus4.png"
import pizzaPlus5 from "@/assets/loading/pizzaPlus5.png"
import pizzaPlus6 from "@/assets/loading/pizzaPlus6.png"
import pizzaPlus7 from "@/assets/loading/pizzaPlus7.png"
import pizzaPlus8 from "@/assets/loading/pizzaPlus8.png"



defineProps({
    frame: {
        type: Number,
        default: 0
    },
    visible: {
        type: Boolean,
        default: false
    }
})

const frames = [
    fullPizza,
    pizzaMinus1,
    pizzaMinus2,
    pizzaMinus3,
    pizzaMinus4,
    pizzaMinus5,
    pizzaMinus6,
    pizzaMinus7,
    pizzaMinus8,
    pizzaPlus0,
    pizzaPlus1,
    pizzaPlus2,
    pizzaPlus3,
    pizzaPlus4,
    pizzaPlus5,
    pizzaPlus6,
    pizzaPlus7,
    pizzaPlus8,
]
onMounted(() => {
    frames.forEach(src => {
        const image = new Image()
        image.src = src

        if (typeof image.decode === "function") {
            image.decode().catch(() => {
                // The image can still load normally if decode is unavailable
                // or rejects before the resource finishes loading.
            })
        }
    })
})
</script>

<template>
    <Transition name="loader-fade">
        <div v-if="visible" class="loader" role="status" aria-live="polite" aria-label="Loading page">
            <div class="pizza-frame">
                <img :src="frames[frame] || frames[0]" class="pizza" alt="" />
            </div>
        </div>
    </Transition>
</template>

<style scoped>
.loader {
    position: fixed;
    inset: 0;
    z-index: 99999;

    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    gap: 0.1rem;

    background:
        linear-gradient(rgba(20, 15, 12, 0.58),
            rgba(20, 15, 12, 0.66));

}

/* .pizza {
    width: min(78vw, 420px);
    display: block;
    user-select: none;
    pointer-events: none;
} */

.pizza-frame {
    
    aspect-ratio: 1 / 1;
    display: flex;
    justify-content: center;
    align-items: center;
    overflow: hidden;
}

.pizza {
    display: block;
    width: 100%;
    height: 100%;
    object-fit: contain;
    object-position: center;
    user-select: none;
    pointer-events: none;
    
}

.loader-fade-enter-active,
.loader-fade-leave-active {
    transition: opacity 0.25s ease;
}

.loader-fade-enter-from,
.loader-fade-leave-to {
    opacity: 0;
}

@media (prefers-reduced-motion: reduce) {

    .loader-fade-enter-active,
    .loader-fade-leave-active {
        transition: none;
    }
}
</style>