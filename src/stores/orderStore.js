
import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  getOrders,
  createOrderDraft,
  confirmOrder
} from '@/api/ordersApi'

export const useOrderStore = defineStore('purchasingOrders', () => {
  const orders = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const confirming = ref(false)
  const error = ref(null)

  async function loadOrders() {
    loading.value = true
    error.value = null

    try {
      orders.value = await getOrders()
      return orders.value
    } catch (err) {
      console.error('Unable to load purchasing orders:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function saveDraft(payload) {
    saving.value = true
    error.value = null

    try {
      const saved = await createOrderDraft(payload)

      orders.value = [...orders.value, saved]

      return saved
    } catch (err) {
      console.error('Unable to save purchasing order:', err)
      error.value = err.message
      throw err
    } finally {
      saving.value = false
    }
  }

  async function confirmSavedOrder(id, payload) {
    confirming.value = true
    error.value = null

    try {
      const updated = await confirmOrder(id, payload)

      orders.value = orders.value.map(order =>
        order.id === id ? updated : order
      )

      return updated
    } catch (err) {
      console.error('Unable to confirm purchasing order:', err)
      error.value = err.message
      throw err
    } finally {
      confirming.value = false
    }
  }

  function clearError() {
    error.value = null
  }

  return {
    orders,
    loading,
    saving,
    confirming,
    error,
    loadOrders,
    saveDraft,
    confirmSavedOrder,
    clearError
  }
})
