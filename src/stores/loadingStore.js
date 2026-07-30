import { defineStore } from "pinia"

export const useLoadingStore = defineStore("loading", {
    state: () => ({
        visible: false,
        frame: 0,
        hasMounted: false,

        // Reserved for future full-stack request tracking
        activeRequests: 0
    }),

    actions: {
        wait(duration) {
            return new Promise(resolve => {
                setTimeout(resolve, duration)
            })
        },

        /*
         * CURRENT VERSION
         *
         * Plays the complete animation:
         * full pizza → empty tray → full pizza
         *
         * This is currently used for page transitions.
         */
        async play() {
            if (!this.hasMounted) {
                this.hasMounted = true
                return
            }

            if (this.visible) return

            this.visible = true
            this.frame = 0

            for (let i = 0; i < 18; i++) {
                this.frame = i
                await this.wait(80)
            }

            this.visible = false
        }

        /*
         * ============================================================
         * FUTURE FULL-STACK LOADER
         * ============================================================
         *
         * Use these methods when the site begins making backend
         * requests.
         *
         * The pizza empties when a request starts, remains on the empty
         * tray while the request is pending, and fills back up after
         * the request finishes.
         *
         * Uncomment these methods and add a comma after play() when
         * you are ready to use them.
         */

        // async animatePizzaOut() {
        //     // Frames 0–8:
        //     // full pizza → empty tray
        //     for (let i = 0; i <= 8; i++) {
        //         this.frame = i
        //         await this.wait(80)
        //     }
        // },

        // async animatePizzaIn() {
        //     // Frames 9–17:
        //     // empty tray → full pizza
        //     for (let i = 9; i <= 17; i++) {
        //         this.frame = i
        //         await this.wait(80)
        //     }
        // },

        // async runWithLoader(requestFunction) {
        //     this.activeRequests++

        //     const isFirstRequest = this.activeRequests === 1

        //     if (isFirstRequest) {
        //         this.visible = true
        //         this.frame = 0

        //         await this.animatePizzaOut()
        //     }

        //     try {
        //         return await requestFunction()
        //     } finally {
        //         this.activeRequests--

        //         if (this.activeRequests === 0) {
        //             await this.animatePizzaIn()

        //             this.visible = false
        //             this.frame = 0
        //         }
        //     }
        // }
    }
})

/*
 * ============================================================
 * FUTURE USAGE EXAMPLE
 * ============================================================
 *
 * The request code belongs in the corresponding feature store,
 * such as menuStore.js, eventStore.js, or orderStore.js.
 *
 * The loading store controls only the animation. It should not
 * contain API URLs or know what data is being requested.
 *
 *
 * Example feature store:
 *
 * import { defineStore } from "pinia"
 * import axios from "axios"
 * import { useLoadingStore } from "@/stores/loadingStore"
 *
 * export const useMenuStore = defineStore("menu", {
 *     state: () => ({
 *         menu: []
 *     }),
 *
 *     actions: {
 *         async loadMenu() {
 *             const loadingStore = useLoadingStore()
 *
 *             const response = await loadingStore.runWithLoader(() => {
 *                 return axios.get("/api/menu")
 *             })
 *
 *             this.menu = response.data
 *         }
 *     }
 * })
 *
 *
 * Example POST request:
 *
 * const response = await loadingStore.runWithLoader(() => {
 *     return axios.post("/api/events", eventData)
 * })
 *
 *
 * Example using fetch instead of Axios:
 *
 * const menu = await loadingStore.runWithLoader(async () => {
 *     const response = await fetch("/api/menu")
 *
 *     if (!response.ok) {
 *         throw new Error("Unable to load the menu")
 *     }
 *
 *     return response.json()
 * })
 */