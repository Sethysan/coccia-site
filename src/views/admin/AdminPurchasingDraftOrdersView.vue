<template>
  <section class="draft-orders">
    <header>
      <h1>Draft Orders</h1>
      <p>Review saved supplier orders before making your phone calls.</p>
    </header>

    <button type="button" :disabled="loading" @click="loadOrders">
      {{ loading ? 'Loading...' : 'Refresh Draft Orders' }}
    </button>

    <p v-if="error" class="error-message" role="alert">
      {{ error }}
    </p>

    <p v-else-if="!loading && draftOrders.length === 0">
      No draft orders available.
    </p>

    <article v-for="order in draftOrders" :key="order.id" class="draft-card">
      <div class="draft-header">
        <div>
          <h2>{{ order.vendorName }}</h2>
          <p>Order #{{ order.id }} · {{ departmentName(order.departmentId) }}</p>
          <p>
            Expected delivery:
            {{ formatDeliveryDate(order.expectedDeliveryDate) }}
          </p>
        </div>
        <button type="button" @click="toggleOrder(order.id)">
          {{ expandedOrderId === order.id ? 'Hide Details' : 'View Details' }}
        </button>
      </div>

      <div v-if="expandedOrderId === order.id" class="draft-details">
        <p v-if="order.notes">{{ order.notes }}</p>

        <div v-for="item in order.items" :key="item.id" class="draft-item">
          <strong>{{ productName(item.vendorProductId) }}</strong>
          <span>
            Planned: {{ item.plannedQuantity }} {{ item.orderUnit }}
          </span>
          <span>
            Estimated unit price: {{ formatMoney(item.estimatedUnitPrice) }}
          </span>

          <label>
            Confirmed quantity
            <input v-model="confirmationValues[item.id].quantity" type="number" min="0" step="0.001">
          </label>

          <label>
            Confirmed unit price ($)
            <input v-model="confirmationValues[item.id].price" type="number" min="0" step="0.01">
          </label>
          <span>
            Estimated line total:
            {{ formatMoney(sumLineTotals([item], 'plannedQuantity', 'estimatedUnitPrice')) }}
          </span>
          <span>
            Confirmed line total:
            {{ formatMoney(sumLineTotals([{
              quantity: confirmationValues[item.id]?.quantity,
              price: confirmationValues[item.id]?.price
            }], 'quantity', 'price')) }}
          </span>
        </div>

        <button type="button" :disabled="confirmingOrderId !== null ||
          deletingOrderId !== null
          " @click="confirmOrder(order)">
          {{
            confirmingOrderId === order.id
              ? 'Confirming...'
              : 'Confirm Phone Order'
          }}
        </button>
        <p><strong>Live confirmed total: {{ formatMoney(liveConfirmedTotal(order)) }}</strong></p>
      </div>
      <p><strong>Estimated total: {{ formatMoney(estimatedOrderTotal(order)) }}</strong></p>
      <button type="button" :disabled="confirmingOrderId !== null ||
        deletingOrderId !== null
        " @click="deleteDraft(order)">
        {{ deletingOrderId === order.id ? 'Deleting...' : 'Delete Draft' }}
      </button>
      <p v-if="actionErrors[order.id]" class="error-message" role="alert">
        {{ actionErrors[order.id] }}
      </p>
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
import { formatDeliveryDate } from '@/utils/dateFormat'
import { money as formatMoney, estimatedOrderTotal, sumLineTotals } from '@/utils/purchasingTotals'

const orderStore = useOrderStore()
const productStore = useProductStore()
const vendorProductStore = useVendorProductStore()

const { orders, loading } = storeToRefs(orderStore)
const { products } = storeToRefs(productStore)
const { offerings } = storeToRefs(vendorProductStore)

const departments = ref([])
const error = ref('')
const expandedOrderId = ref(null)
const confirmingOrderId = ref(null)
const confirmationValues = ref({})
const actionErrors = ref({})

const draftOrders = computed(() =>
  orders.value.filter(order => order.status === 'DRAFT')
)

function departmentName(id) {
  return departments.value.find(dept => dept.id === id)?.name
    || 'Unknown department'
}

function productName(offeringId) {
  const offering = offerings.value.find(item => item.id === offeringId)
  return products.value.find(product => product.id === offering?.productId)?.name
    || `Offering #${offeringId}`
}

function toggleOrder(id) {
  expandedOrderId.value = expandedOrderId.value === id ? null : id
}

async function loadOrders() {
  error.value = ''

  try {
    await orderStore.loadOrders()

    for (const order of draftOrders.value) {
      for (const item of order.items) {
        if (!confirmationValues.value[item.id]) {
          confirmationValues.value[item.id] = {
            quantity: String(item.plannedQuantity),
            price: item.estimatedUnitPrice == null
              ? ''
              : String(item.estimatedUnitPrice)
          }
        }
      }
    }
  } catch (err) {
    error.value = err.message
  }
}

async function confirmOrder(order) {
  const items = []

  for (const item of order.items) {
    const values = confirmationValues.value[item.id]

    if (
      !values ||
      values.quantity === '' ||
      values.price === '' ||
      !Number.isFinite(Number(values.quantity)) ||
      Number(values.quantity) < 0 ||
      !Number.isFinite(Number(values.price)) ||
      Number(values.price) < 0
    ) {
      actionErrors.value[order.id] =
        'Enter a valid confirmed quantity and price for every item.'
      return
    }

    items.push({
      orderItemId: item.id,
      confirmedQuantity: Number(values.quantity),
      confirmedUnitPrice: Number(values.price)
    })
  }

  if (!window.confirm(
    `Confirm phone order #${order.id} with ${order.vendorName}?`
  )) return

  confirmingOrderId.value = order.id
  actionErrors.value[order.id] = ''

  try {
    await orderStore.confirmSavedOrder(order.id, { items })
    await loadOrders()
  } catch (err) {
    actionErrors.value[order.id] = err.message
  } finally {
    confirmingOrderId.value = null
  }
}

const deletingOrderId = ref(null)

function liveConfirmedTotal(order) {
  const items = order.items.map(item => {
    const values = confirmationValues.value[item.id]

    return {
      quantity: values?.quantity === ''
        ? null
        : values?.quantity ?? null,
      price: values?.price === ''
        ? null
        : values?.price ?? null
    }
  })

  return sumLineTotals(items, 'quantity', 'price')
}

async function deleteDraft(order) {
  if (!window.confirm(
    `Permanently delete draft order #${order.id} for ${order.vendorName}? This cannot be undone.`
  )) return

  deletingOrderId.value = order.id
  actionErrors.value[order.id] = ''
  try {
    await orderStore.deleteDraft(order.id)
    if (expandedOrderId.value === order.id) expandedOrderId.value = null
  } catch (err) {
    actionErrors.value[order.id] = err.message || 'Unable to delete draft.'
  } finally {
    deletingOrderId.value = null
  }
}

onMounted(async () => {
  const results = await Promise.allSettled([
    productStore.loadProducts(),
    vendorProductStore.loadOfferings(),
    getPurchasingDepartments(),
    loadOrders()
  ])

  if (results[2].status === 'fulfilled') {
    departments.value = results[2].value
  } else {
    error.value = results[2].reason.message
  }
})
</script>

<style scoped>
.draft-orders {
  display: grid;
  gap: 1.25rem;
}

.draft-card {
  padding: 1.25rem;
  border: 1px solid rgba(190, 145, 65, 0.45);
  border-radius: 8px;
  background: rgba(15, 12, 10, 0.88);
}

.draft-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;
  flex-wrap: wrap;
}

.draft-header h2 {
  margin: 0 0 0.5rem;
}

.draft-details {
  display: grid;
  gap: 1rem;
  margin-top: 1rem;
}

.draft-item {
  display: grid;
  gap: 0.4rem;
  padding: 0.75rem 0;
  border-top: 1px solid rgba(190, 145, 65, 0.3);
}

.draft-item label {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.5rem;
}

.draft-item input {
  width: 8rem;
  padding: 0.4rem;
}

.error-message {
  color: #ef9a9a;
}
</style>
