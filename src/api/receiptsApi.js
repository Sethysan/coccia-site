
import { apiRequest } from '@/api/apiClient'

const ORDERS_ENDPOINT = '/api/admin/purchasing/orders'
const RECEIPTS_ENDPOINT = '/api/admin/purchasing/receipts'

export function getOrderReceipts(orderId) {
  return apiRequest(
    `${ORDERS_ENDPOINT}/${encodeURIComponent(orderId)}/receipts`,
    { method: 'GET' }
  )
}

export function getReceipt(receiptId) {
  return apiRequest(
    `${RECEIPTS_ENDPOINT}/${encodeURIComponent(receiptId)}`,
    { method: 'GET' }
  )
}

export function receiveOrder(orderId, receiptData) {
  return apiRequest(
    `${ORDERS_ENDPOINT}/${encodeURIComponent(orderId)}/receipts`,
    {
      method: 'POST',
      body: JSON.stringify(receiptData)
    }
  )
}
