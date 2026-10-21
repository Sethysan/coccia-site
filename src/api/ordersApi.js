
import { apiRequest } from '@/api/apiClient'

const ORDERS_ENDPOINT = '/api/admin/purchasing/orders'

export const getOrders = () => apiRequest(ORDERS_ENDPOINT, {
  method: 'GET'
})

export const getOrder = (id) => apiRequest(`${ORDERS_ENDPOINT}/${id}`, {
  method: 'GET'
})

export const createOrderDraft = (payload) => apiRequest(ORDERS_ENDPOINT, {
  method: 'POST',
  body: JSON.stringify(payload)
})

export const confirmOrder = (id, payload) => apiRequest(`${ORDERS_ENDPOINT}/${id}/confirm`, {
  method: 'POST',
  body: JSON.stringify(payload)
})
