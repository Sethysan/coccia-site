
import { apiRequest } from '@/api/apiClient'

const VENDORS_ENDPOINT = '/api/admin/purchasing/vendors'

export const getVendors = () => {
  return apiRequest(VENDORS_ENDPOINT, {
    method: 'GET'
  })
}

export const getVendor = (id) => {
  return apiRequest(`${VENDORS_ENDPOINT}/${id}`, {
    method: 'GET'
  })
}

export const createVendor = (vendor) => {
  return apiRequest(VENDORS_ENDPOINT, {
    method: 'POST',
    body: JSON.stringify(vendor)
  })
}

export const updateVendor = (id, vendor) => {
  return apiRequest(`${VENDORS_ENDPOINT}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(vendor)
  })
}
