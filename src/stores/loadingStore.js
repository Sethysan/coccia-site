import { defineStore } from "pinia"

export const useLoadingStore = defineStore("loading", {
    state: () => ({
        visible: false,
        frame: 0,
        hasMounted: false,
        animationToken: 0,
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

            const token = ++this.animationToken

            // Run independently while the page/API is loading.
            this.animateWhileLoading(token)
        },

        async finish() {
            if (!this.visible) return

            // Cancel the repeating loading animation.
            ++this.animationToken

            // Always finish by building a complete pizza.
            await this.animatePizzaIn()

            this.visible = false
            this.frame = 0
        },

        async animateWhileLoading(token) {
            while (
                this.visible &&
                token === this.animationToken
            ) {
                await this.animatePizzaOut(token)

                if (
                    !this.visible ||
                    token !== this.animationToken
                ) {
                    return
                }

                await this.animatePizzaIn(token)

                if (
                    !this.visible ||
                    token !== this.animationToken
                ) {
                    return
                }

                // Let the completed pizza sit for a moment
                // before beginning another cycle.
                await this.wait(350)
            }
        },

        async animatePizzaOut(token = null) {
            for (let i = 0; i <= 8; i++) {
                if (
                    token !== null &&
                    token !== this.animationToken
                ) {
                    return
                }

                this.frame = i
                await this.wait(80)
            }
        },

        async animatePizzaIn(token = null) {
            for (let i = 9; i <= 17; i++) {
                if (
                    token !== null &&
                    token !== this.animationToken
                ) {
                    return
                }

                this.frame = i
                await this.wait(80)
            }
        }
    }
})