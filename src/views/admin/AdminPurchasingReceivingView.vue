
<template>
  <section class="receiving">
    <header>
      <h1>Receive Deliveries</h1>
      <p>
        Review confirmed supplier orders and record delivered quantities.
      </p>
    </header>

    <button
      type="button"
      :disabled="loading || saving || loadingOrders"
      @click="loadOrders"
    >
      {{ loadingOrders ? 'Loading...' : 'Refresh Orders' }}
    </button>

    <p v-if="error" class="error-message" role="alert">
      {{ error }}
    </p>

    <p v-if="success" class="success-message" role="status">
      {{ success }}
    </p>

    <p v-if="loadingOrders">
      Checking outstanding deliveries...
    </p>

    <p
      v-else-if="!error && receivingOrders.length === 0"
      class="empty-message"
    >
      No outstanding deliveries. All confirmed orders have been received.
    </p>

    <article
      v-for="order in receivingOrders"
      :key="order.id"
      class="receiving-card"
    >
      <div class="order-header">
        <div>
          <h2>{{ order.vendorName }}</h2>

          <p>
            Order #{{ order.id }} ·
            {{ departmentName(order.departmentId) }}
          </p>

          <p>
            Expected:
            {{ formatDeliveryDate(order.expectedDeliveryDate) }}
          </p>
        </div>

        <button
          type="button"
          :disabled="saving"
          @click="selectOrder(order)"
        >
          {{ selectedOrderId === order.id
            ? 'Close'
            : 'Receive Delivery' }}
        </button>
      </div>

      <div
        v-if="selectedOrderId === order.id"
        class="order-details"
      >
        <div
          v-for="item in order.items"
          :key="item.id"
          class="receiving-item"
        >
          <strong>
            {{ productName(item.vendorProductId) }}
          </strong>

          <span>
            Confirmed:
            {{ item.confirmedQuantity }}
            {{ item.orderUnit }}
          </span>

          <span>
            Previously received:
            {{ receivedQuantity(order.id, item.id) }}
            {{ item.orderUnit }}
          </span>

          <span>
            Remaining:
            {{ remainingQuantity(order.id, item) }}
            {{ item.orderUnit }}
          </span>

          <div
            v-if="remainingQuantity(order.id, item) > 0"
            class="quantity-row"
          >
            <label :for="`received-${item.id}`">
              Receiving quantity
            </label>

            <input
              :id="`received-${item.id}`"
              v-model="quantities[item.id]"
              type="number"
              min="0"
              :max="remainingQuantity(order.id, item)"
              step="0.001"
              :disabled="!editing[item.id] || saving"
            />

            <span>{{ item.orderUnit }}</span>

            <button
              type="button"
              :disabled="saving"
              @click="toggleEdit(item.id)"
            >
              {{ editing[item.id] ? 'Done' : 'Edit Quantity' }}
            </button>
          </div>

          <p v-else class="complete-message">
            Fully received
          </p>
        </div>

        <label for="receiving-notes">
          Delivery notes (optional)
        </label>

        <textarea
          id="receiving-notes"
          v-model="notes"
          rows="3"
          :disabled="saving"
          placeholder="Missing items, damaged packaging, etc."
        ></textarea>

        <button
          type="button"
          class="confirm-button"
          :disabled="saving || !canSubmit(order)"
          @click="confirmReceipt(order)"
        >
          {{ saving ? 'Saving Receipt...' : 'Confirm Receipt' }}
        </button>

        <p v-if="!canSubmit(order)" class="hint">
          Enter a valid quantity for at least one outstanding item.
        </p>
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
import { getOrderReceipts, receiveOrder } from '@/api/receiptsApi'
import { formatDeliveryDate } from '@/utils/dateFormat'

const orderStore = useOrderStore()
const productStore = useProductStore()
const vendorProductStore = useVendorProductStore()

const { orders, loading } = storeToRefs(orderStore)
const { products } = storeToRefs(productStore)
const { offerings } = storeToRefs(vendorProductStore)

const departments = ref([])
const receiptsByOrder = ref({})
const selectedOrderId = ref(null)
const quantities = ref({})
const editing = ref({})
const notes = ref('')
const loadingOrders = ref(false)
const saving = ref(false)
const error = ref('')
const success = ref('')

const receivingOrders = computed(() => {
  if (loadingOrders.value || error.value) return []

  return orders.value.filter(order => {
    if (order.status !== 'CONFIRMED') return false

    // Do not treat unknown receipt history as a completed order.
    if (!Object.prototype.hasOwnProperty.call(
      receiptsByOrder.value,
      order.id
    )) {
      return false
    }

    return order.items?.some(item =>
      remainingQuantity(order.id, item) > 0
    )
  })
})

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

function remainingQuantity(orderId, item) {
  return Math.max(
    0,
    Number(item.confirmedQuantity || 0) -
      receivedQuantity(orderId, item.id)
  )
}

function toggleEdit(itemId) {
  editing.value[itemId] = !editing.value[itemId]
}

function validQuantity(order, item) {
  const raw = quantities.value[item.id]

  if (raw === '' || raw === null || raw === undefined) {
    return false
  }

  const quantity = Number(raw)
  const remaining = remainingQuantity(order.id, item)

  return Number.isFinite(quantity)
    && quantity >= 0
    && quantity <= remaining
    && Math.abs(quantity * 1000 - Math.round(quantity * 1000)) < 1e-8
}

function canSubmit(order) {
  if (loadingOrders.value || saving.value) return false

  const outstanding = order.items.filter(
    item => remainingQuantity(order.id, item) > 0
  )

  return outstanding.length > 0
    && outstanding.every(item => validQuantity(order, item))
    && outstanding.some(item =>
      Number(quantities.value[item.id]) > 0
    )
}

function selectOrder(order) {
  error.value = ''
  success.value = ''

  if (selectedOrderId.value === order.id) {
    selectedOrderId.value = null
    return
  }

  selectedOrderId.value = order.id
  quantities.value = {}
  editing.value = {}
  notes.value = ''

  for (const item of order.items) {
    quantities.value[item.id] = String(
      remainingQuantity(order.id, item)
    )
    editing.value[item.id] = false
  }
}

async function confirmReceipt(order) {
  error.value = ''
  success.value = ''

  if (!canSubmit(order)) return

  const items = order.items
    .filter(item =>
      remainingQuantity(order.id, item) > 0 &&
      Number(quantities.value[item.id]) > 0
    )
    .map(item => ({
      orderItemId: item.id,
      receivedQuantity: Number(quantities.value[item.id])
    }))

  if (!window.confirm(
    `Confirm receipt of ${items.length} product(s) for Order #${order.id}?`
  )) return

  saving.value = true

  try {
    await receiveOrder(order.id, {
      notes: notes.value.trim() || null,
      items
    })

    // Refresh receipt history before recalculating outstanding orders.
    receiptsByOrder.value = {
      ...receiptsByOrder.value,
      [order.id]: await getOrderReceipts(order.id)
    }

    selectedOrderId.value = null
    success.value =
      `Delivery recorded successfully for Order #${order.id}.`

    await orderStore.loadOrders()
  } catch (err) {
    error.value = err.message || 'Unable to record delivery.'
  } finally {
    saving.value = false
  }
}

async function loadOrders() {
  loadingOrders.value = true
  error.value = ''
  success.value = ''
  selectedOrderId.value = null

  try {
    await orderStore.loadOrders()

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
    receiptsByOrder.value = {}
    error.value = err.message || 'Unable to load receiving orders.'
  } finally {
    loadingOrders.value = false
  }
}

onMounted(async () => {
  const results = await Promise.allSettled([
    productStore.loadProducts(),
    vendorProductStore.loadOfferings(),
    getPurchasingDepartments()
  ])

  if (results[2].status === 'fulfilled') {
    departments.value = results[2].value
  } else {
    error.value = results[2].reason.message
  }

  await loadOrders()
})
</script>

<style scoped>
.receiving {
  display: grid;
  gap: 1.25rem;
}

.receiving-card {
  padding: 1.25rem;
  border: 1px solid rgba(190, 145, 65, 0.45);
  border-radius: 8px;
  background: rgba(15, 12, 10, 0.88);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.order-header h2 {
  margin: 0 0 0.5rem;
}

.order-details {
  display: grid;
  gap: 1rem;
  margin-top: 1.25rem;
}

.receiving-item {
  display: grid;
  gap: 0.45rem;
  padding: 1rem 0;
  border-top: 1px solid rgba(190, 145, 65, 0.3);
}

.quantity-row {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.quantity-row input {
  width: 100px;
  padding: 0.5rem;
}

.quantity-row input:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

textarea {
  width: 100%;
  box-sizing: border-box;
  padding: 0.75rem;
}

.confirm-button {
  justify-self: start;
  padding: 0.75rem 1.25rem;
  font-weight: bold;
}

.error-message {
  color: #ef9a9a;
}

.success-message,
.complete-message {
  color: #a5d6a7;
}

.hint {
  opacity: 0.75;
  font-size: 0.9rem;
}

.empty-message {
  padding: 1rem;
  border: 1px solid rgba(190, 145, 65, 0.3);
  border-radius: 8px;
}
</style>
