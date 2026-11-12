export function money(amount) {
  if (amount == null || !Number.isFinite(Number(amount))) return 'Price missing'
  return new Intl.NumberFormat('en-US', {
    style: 'currency', currency: 'USD'
  }).format(Number(amount))
}

export function sumLineTotals(items, quantityKey, priceKey) {
  let sum = 0
  for (const item of items || []) {
    const quantity = item[quantityKey]
    const price = item[priceKey]
    if (quantity === '' || price === '' || quantity == null || price == null ||
        !Number.isFinite(Number(quantity)) || !Number.isFinite(Number(price))) {
      return null
    }
    sum += Number(quantity) * Number(price)
  }
  return sum
}

export function estimatedOrderTotal(order) {
  return sumLineTotals(order.items, 'plannedQuantity', 'estimatedUnitPrice')
}

export function confirmedOrderTotal(order) {
  return sumLineTotals(order.items, 'confirmedQuantity', 'confirmedUnitPrice')
}