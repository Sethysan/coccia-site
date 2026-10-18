
import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  getVendors,
  createVendor,
  updateVendor
} from '@/api/vendorsApi'

export const useVendorStore = defineStore('vendors', () => {
  const vendors = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  async function loadVendors() {
    loading.value = true
    error.value = null

    try {
      vendors.value = await getVendors()
    } catch (err) {
      console.error('Unable to load vendors:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function addVendor(vendor) {
    saving.value = true
    error.value = null

    try {
      const createdVendor = await createVendor(vendor)

      vendors.value = [...vendors.value, createdVendor]
        .sort((a, b) => a.name.localeCompare(b.name))

      return createdVendor
    } catch (err) {
      console.error('Unable to create vendor:', err)
      error.value = err.message
      throw err
    } finally {
      saving.value = false
    }
  }

  async function saveVendor(id, vendor) {
    saving.value = true
    error.value = null

    try {
      const updatedVendor = await updateVendor(id, vendor)

      vendors.value = vendors.value
        .map(existing =>
          existing.id === id ? updatedVendor : existing
        )
        .sort((a, b) => a.name.localeCompare(b.name))

      return updatedVendor
    } catch (err) {
      console.error('Unable to update vendor:', err)
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
    vendors,
    loading,
    saving,
    error,
    loadVendors,
    addVendor,
    saveVendor,
    clearError
  }
})
