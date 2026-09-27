import { defineStore } from "pinia"

export const useLoadingStore = defineStore("loading", {
    state: () => ({
        visible: false,
        frame: 0,
        hasMounted: false,
    }),

    actions: {
        wait(duration) {
            return new Promise(resolve => {
                setTimeout(resolve, duration)
            })
        },

        async start() {
            if (!this.hasMounted) {
                this.hasMounted = true
                return
            }

            if (this.visible) return

            this.visible = true
            this.frame = 0

            // Empty the pizza while the new page begins loading.
            await this.animatePizzaOut()
        },

        async finish() {
            if (!this.visible) return

            // Make sure emptying has finished.
            while (this.frame < 8) {
                await this.wait(40)
            }

            await this.animatePizzaIn()

            this.visible = false
            this.frame = 0
        },

        async animatePizzaOut() {
            for (let i = 0; i <= 8; i++) {
                this.frame = i
                await this.wait(80)
            }
        },

        async animatePizzaIn() {
            for (let i = 9; i <= 17; i++) {
                this.frame = i
                await this.wait(80)
            }
        }
    }
})
