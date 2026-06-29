import { defineStore } from 'pinia'
import dayjs from 'dayjs'

export const useTimeStore = defineStore('time', {
  state: () => ({
    currentTime: dayjs(),
    timer: null
  }),

  actions: {
    updateTime() {
      this.currentTime = dayjs()
    },

    startClock() {
      this.updateTime()

      this.timer = setInterval(() => {
        this.updateTime()
      }, 60000)
    },

    stopClock() {
      clearInterval(this.timer)
    }
  }
})