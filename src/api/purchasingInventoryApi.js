
import { apiRequest } from '@/api/apiClient'

const INVENTORY_ENDPOINT = '/api/admin/purchasing/inventory'

// Retrieve current inventory balances for all products.
export const getPurchasingInventoryBalances = () => {
    return apiRequest(INVENTORY_ENDPOINT, {
        method: 'GET'
    })
}

// Retrieve the inventory balance for one product.
export const getPurchasingInventoryBalance = (productId) => {
    return apiRequest(
        `${INVENTORY_ENDPOINT}/${encodeURIComponent(productId)}`,
        {
            method: 'GET'
        }
    )
}

// Retrieve inventory movement history for one product.
export const getPurchasingInventoryMovements = (productId) => {
    return apiRequest(
        `${INVENTORY_ENDPOINT}/${encodeURIComponent(productId)}/movements`,
        {
            method: 'GET'
        }
    )
}

export const createPurchasingInventoryAdjustment = (
    productId,
    { movementType, quantity, notes }
) =>
    apiRequest(
        `${INVENTORY_ENDPOINT}/${encodeURIComponent(productId)}/adjustments`,
        {
            method: "POST",
            body: JSON.stringify({
                movementType,
                quantity,
                notes
            })
        }
    )
