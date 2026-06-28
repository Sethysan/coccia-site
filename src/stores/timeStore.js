import { defineStore } from 'pinia'
import dayjs from 'dayjs'

export const useTimeStore = defineStore('time', {
  state: () => ({
    currentTime: dayjs()
  }),

  actions: {
    updateTime() {
      this.currentTime = dayjs()
    }
  }
})
