
<template>
  <section class="completed-orders">
    <header>
      <h1>Completed Orders</h1>
      <p>
        Fully received supplier orders and their delivery history.
      </p>
    </header>

    <button type="button" :disabled="loading" @click="loadData">
      {{ loading ? 'Loading...' : 'Refresh Completed Orders' }}
    </button>

    <p v-if="error" class="error-message" role="alert">
      {{ error }}
    </p>

    <p v-if="loading">Loading completed orders...</p>

    <p v-else-if="!error && completedOrders.length === 0">
      No completed orders yet.
    </p>

    <article
      v-for="order in completedOrders"
      :key="order.id"
      class="completed-card"
    >
      <div class="completed-header">
        <div>
          <h2>{{ order.vendorName }}</h2>
          <p>
            Order #{{ order.id }} ·
            {{ departmentName(order.departmentId) }}
          </p>
          <p>
            Expected delivery:
            {{ formatDeliveryDate(order.expectedDeliveryDate) }}
          </p>
          <p v-if="order.confirmedAt">
            Confirmed: {{ formatDateTime(order.confirmedAt) }}
          </p>
        </div>

        <button type="button" @click="toggleOrder(order.id)">
          {{ expandedOrderId === order.id ? 'Hide Details' : 'View Details' }}
        </button>
      </div>

      <div v-if="expandedOrderId === order.id" class="completed-details">
        <h3>Products Received</h3>

        <div
          v-for="item in order.items"
          :key="item.id"
          class="completed-item"
        >
          <strong>{{ productName(item.vendorProductId) }}</strong>

          <span>
            Confirmed: {{ item.confirmedQuantity }} {{ item.orderUnit }}
          </span>

          <span>
            Received:
            {{ receivedQuantity(order.id, item.id) }}
            {{ item.orderUnit }}
          </span>

          <span>
            Unit price: {{ money(item.confirmedUnitPrice) }}
          </span>
        </div>

        <p class="order-total">
          <strong>Order Total: {{ orderTotal(order) }}</strong>
        </p>

        <h3>Delivery History</h3>

        <div
          v-for="receipt in receiptsByOrder[order.id] || []"
          :key="receipt.id"
          class="receipt-card"
        >
          <strong>
            Receipt #{{ receipt.id }} ·
            {{ formatDateTime(receipt.receivedAt) }}
          </strong>

          <p v-if="receipt.notes">{{ receipt.notes }}</p>

          <div
            v-for="item in receipt.items"
            :key="item.id"
            class="receipt-item"
          >
            {{ receiptProductName(order, item.orderItemId) }}:
            {{ item.receivedQuantity }}
            {{ receiptUnit(order, item.orderItemId) }}
          </div>
        </div>
      </div>
    </article>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useOrderStore } from '@/stores/orderStore'
import { useProductStore } from '@/stores/productStore'
import { useVendorProductStore } from '@/stores/vendorProductStore'
import { getPurchasingDepartments } from '@/api/purchasingCategoriesApi'
import { getOrderReceipts } from '@/api/receiptsApi'
import { formatDeliveryDate } from '@/utils/dateFormat'

const orderStore = useOrderStore()
const productStore = useProductStore()
const vendorProductStore = useVendorProductStore()

const { orders } = storeToRefs(orderStore)
const { products } = storeToRefs(productStore)
const { offerings } = storeToRefs(vendorProductStore)

const departments = ref([])
const receiptsByOrder = ref({})
const loading = ref(false)
const error = ref('')
const expandedOrderId = ref(null)

const completedOrders = computed(() =>
  orders.value.filter(order => {
    if (order.status !== 'CONFIRMED' || !order.items?.length) {
      return false
    }

    const receipts = receiptsByOrder.value[order.id]

    if (!receipts) return false

    return order.items.every(item => {
      const confirmed = Number(item.confirmedQuantity)

      if (!Number.isFinite(confirmed) || confirmed <= 0) {
        return false
      }

      return receivedQuantity(order.id, item.id) >= confirmed
    })
  })
)

function departmentName(id) {
  return departments.value.find(dept => dept.id === id)?.name
    || 'Unknown department'
}

function productName(offeringId) {
  const offering = offerings.value.find(item => item.id === offeringId)

  return products.value.find(
    product => product.id === offering?.productId
  )?.name || `Offering #${offeringId}`
}

function receivedQuantity(orderId, orderItemId) {
  return (receiptsByOrder.value[orderId] || []).reduce(
    (total, receipt) =>
      total + (receipt.items || [])
        .filter(item => item.orderItemId === orderItemId)
        .reduce(
          (sum, item) => sum + Number(item.receivedQuantity),
          0
        ),
    0
  )
}

function receiptProductName(order, orderItemId) {
  const item = order.items.find(item => item.id === orderItemId)
  return item ? productName(item.vendorProductId) : 'Unknown product'
}

function receiptUnit(order, orderItemId) {
  return order.items.find(item => item.id === orderItemId)?.orderUnit || ''
}

function money(value) {
  return value == null ? 'Unavailable' : `$${Number(value).toFixed(2)}`
}

function orderTotal(order) {
  if (order.items.some(item =>
    item.confirmedQuantity == null ||
    item.confirmedUnitPrice == null
  )) return 'Incomplete'

  return money(order.items.reduce(
    (total, item) =>
      total + Number(item.confirmedQuantity) *
      Number(item.confirmedUnitPrice),
    0
  ))
}

function formatDateTime(value) {
  return value ? new Date(value).toLocaleString() : 'Date unavailable'
}

function toggleOrder(id) {
  expandedOrderId.value = expandedOrderId.value === id ? null : id
}

async function loadData() {
  loading.value = true
  error.value = ''
  expandedOrderId.value = null

  try {
    await Promise.all([
      orderStore.loadOrders(),
      productStore.loadProducts(),
      vendorProductStore.loadOfferings()
    ])

    departments.value = await getPurchasingDepartments()

    const confirmedOrders = orders.value.filter(
      order => order.status === 'CONFIRMED'
    )

    const histories = await Promise.all(
      confirmedOrders.map(async order => ({
        orderId: order.id,
        receipts: await getOrderReceipts(order.id)
      }))
    )

    receiptsByOrder.value = Object.fromEntries(
      histories.map(history => [
        history.orderId,
        history.receipts
      ])
    )
  } catch (err) {
    error.value = err.message || 'Unable to load completed orders.'
  } finally {
    loading.value = false
  }
}

onMounted(loadData)
</script>

<style scoped>
.completed-orders {
  display: grid;
  gap: 1.25rem;
}

.completed-card,
.receipt-card {
  padding: 1.25rem;
  border: 1px solid rgba(190, 145, 65, 0.45);
  border-radius: 8px;
  background: rgba(15, 12, 10, 0.88);
}

.completed-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.completed-header h2 {
  margin: 0 0 0.5rem;
}

.completed-details {
  display: grid;
  gap: 1rem;
  margin-top: 1.25rem;
}

.completed-item {
  display: grid;
  gap: 0.4rem;
  padding: 0.75rem 0;
  border-top: 1px solid rgba(190, 145, 65, 0.3);
}

.order-total {
  padding-top: 1rem;
  border-top: 1px solid rgba(190, 145, 65, 0.45);
  font-size: 1.15rem;
}

.receipt-card {
  display: grid;
  gap: 0.5rem;
}

.receipt-item {
  padding-left: 0.75rem;
}

.error-message {
  color: #ef9a9a;
}
</style>
