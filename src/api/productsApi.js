
import { apiRequest } from '@/api/apiClient'

const PRODUCTS_ENDPOINT = '/api/admin/purchasing/products'

export const getProducts = () => {
  return apiRequest(PRODUCTS_ENDPOINT, {
    method: 'GET'
  })
}

export const getProduct = (id) => {
  return apiRequest(`${PRODUCTS_ENDPOINT}/${id}`, {
    method: 'GET'
  })
}

export const createProduct = (product) => {
  return apiRequest(PRODUCTS_ENDPOINT, {
    method: 'POST',
    body: JSON.stringify(product)
  })
}

export const updateProduct = (id, product) => {
  return apiRequest(`${PRODUCTS_ENDPOINT}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(product)
  })
}
