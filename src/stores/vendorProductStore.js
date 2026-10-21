
import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  getVendorProducts,
  getVendorProductsForProduct,
  createVendorProduct,
  updateVendorProduct
} from '@/api/vendorProductsApi'

export const useVendorProductStore = defineStore('vendorProducts', () => {
  const offerings = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  async function loadOfferings() {
    loading.value = true
    error.value = null

    try {
      offerings.value = await getVendorProducts()
    } catch (err) {
      console.error('Unable to load vendor offerings:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function loadOfferingsForProduct(productId) {
    loading.value = true
    error.value = null

    try {
      offerings.value = await getVendorProductsForProduct(productId)
    } catch (err) {
      console.error('Unable to load product offerings:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function addOffering(offering) {
    saving.value = true
    error.value = null

    try {
      const created = await createVendorProduct(offering)

      // Reload to capture any preferred-vendor changes.
      await loadOfferings()

      return created
    } catch (err) {
      console.error('Unable to create vendor offering:', err)
      error.value = err.message
      throw err
    } finally {
      saving.value = false
    }
  }

  async function saveOffering(id, offering) {
    saving.value = true
    error.value = null

    try {
      const updated = await updateVendorProduct(id, offering)

      // A preferred-vendor switch can update multiple records.
      await loadOfferings()

      return updated
    } catch (err) {
      console.error('Unable to update vendor offering:', err)
      error.value = err.message
      throw err
    } finally {
      saving.value = false
    }
  }

  function clearError() {
    error.value = null
  }

  return {
    offerings,
    loading,
    saving,
    error,
    loadOfferings,
    loadOfferingsForProduct,
    addOffering,
    saveOffering,
    clearError
  }
})
