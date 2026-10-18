
import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  getProducts,
  createProduct,
  updateProduct
} from '@/api/productsApi'

export const useProductStore = defineStore('products', () => {
  const products = ref([])
  const loading = ref(false)
  const saving = ref(false)
  const error = ref(null)

  async function loadProducts() {
    loading.value = true
    error.value = null

    try {
      products.value = await getProducts()
    } catch (err) {
      console.error('Unable to load products:', err)
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  async function addProduct(product) {
    saving.value = true
    error.value = null

    try {
      const createdProduct = await createProduct(product)

      products.value = [...products.value, createdProduct]
        .sort((a, b) => a.name.localeCompare(b.name))

      return createdProduct
    } catch (err) {
      console.error('Unable to create product:', err)
      error.value = err.message
      throw err
    } finally {
      saving.value = false
    }
  }

  async function saveProduct(id, product) {
    saving.value = true
    error.value = null

    try {
      const updatedProduct = await updateProduct(id, product)

      products.value = products.value
        .map(existing =>
          existing.id === id ? updatedProduct : existing
        )
        .sort((a, b) => a.name.localeCompare(b.name))

      return updatedProduct
    } catch (err) {
      console.error('Unable to update product:', err)
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
    products,
    loading,
    saving,
    error,
    loadProducts,
    addProduct,
    saveProduct,
    clearError
  }
})
