
import { apiRequest } from '@/api/apiClient'

const VENDOR_PRODUCTS_ENDPOINT =
  '/api/admin/purchasing/vendor-products'

export const getVendorProducts = () => {
  return apiRequest(VENDOR_PRODUCTS_ENDPOINT, {
    method: 'GET'
  })
}

export const getVendorProductsForProduct = (productId) => {
  return apiRequest(
    `${VENDOR_PRODUCTS_ENDPOINT}/product/${productId}`,
    {
      method: 'GET'
    }
  )
}

export const getVendorProduct = (id) => {
  return apiRequest(`${VENDOR_PRODUCTS_ENDPOINT}/${id}`, {
    method: 'GET'
  })
}

export const createVendorProduct = (offering) => {
  return apiRequest(VENDOR_PRODUCTS_ENDPOINT, {
    method: 'POST',
    body: JSON.stringify(offering)
  })
}

export const updateVendorProduct = (id, offering) => {
  return apiRequest(`${VENDOR_PRODUCTS_ENDPOINT}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(offering)
  })
}
