<template>
    <section>
        <header class="page-header">
            <div>
                <h1>Inventory</h1>
                <p class="admin-subtext">View current stock levels, record adjustments, and track inventory changes.</p>
            </div>
        </header>

        <section class="admin-card">
            <div class="inventory-heading">
                <h2>Current Inventory</h2>
                <button type="button" :disabled="loading" @click="loadInventory">
                    {{ loading ? 'Refreshing...' : 'Refresh Inventory' }}
                </button>
            </div>

            <div class="inventory-filters">
                <label>Department
                    <select v-model="selectedDepartment">
                        <option value="all">All Departments</option>
                        <option v-for="department in departments" :key="department.id" :value="String(department.id)">
                            {{ department.name }}
                        </option>
                    </select>
                </label>
                <label>Search Products
                    <input v-model="search" type="search" placeholder="Search by product name...">
                </label>
                <label>Category
                    <select v-model="selectedCategory">
                        <option value="all">All Categories</option>
                        <option v-for="category in availableCategories" :key="category" :value="category">
                            {{ category }}
                        </option>
                    </select>
                </label>
            </div>

            <p v-if="loading">Loading inventory...</p>
            <p v-else-if="error" class="error-message" role="alert">{{ error }}</p>
            <p v-else-if="balances.length === 0" class="admin-subtext">No inventory products have been added yet.</p>
            <p v-else-if="filteredBalances.length === 0" class="admin-subtext">No inventory products match your filters.
            </p>
            <div v-else class="inventory-grid">
                <article v-for="balance in filteredBalances" :key="balance.productId" class="admin-card inventory-card">
                    <div class="inventory-card-header">
                        <h3>{{ balance.productName }}</h3>
                        <span class="inventory-category">{{ balance.category }}</span>
                    </div>
                    <p class="admin-subtext">{{ departmentName(balance.productId) }}</p>
                    <div class="inventory-quantity">
                        <strong>{{ formatQuantity(balance.quantityOnHand) }}</strong>
                        <span>{{ balance.baseUnit || 'Unit not configured' }}</span>
                    </div>
                    <p v-if="!balance.baseUnit" class="inventory-warning">
                        Configure a base inventory unit in Product Management before adjusting this product.
                    </p>
                    <p class="inventory-updated admin-subtext">Last updated: {{ formatDate(balance.updatedAt) }}</p>

                    <button type="button" class="action-button" :disabled="!balance.baseUnit || saving"
                        :aria-expanded="adjustmentProductId === balance.productId"
                        @click="toggleAdjustment(balance.productId)">
                        {{ adjustmentProductId === balance.productId ? 'Cancel Adjustment' : 'Record Adjustment' }}
                    </button>

                    <form v-if="adjustmentProductId === balance.productId" class="adjustment-form"
                        @submit.prevent="submitAdjustment(balance)">
                        <h4>Record Inventory Change</h4>
                        <label>Type
                            <select v-model="adjustmentType" :disabled="saving" required>
                                <option value="USAGE">Usage — stock used</option>
                                <option value="WASTE">Waste — stock discarded</option>
                                <option value="ADJUSTMENT">Adjustment — stock correction</option>
                            </select>
                        </label>
                        <label>Quantity ({{ balance.baseUnit }})
                            <input v-model="adjustmentQuantity" type="number" step="0.25"
                                :min="adjustmentType === 'ADJUSTMENT' ? undefined : '0.25'" :disabled="saving"
                                required :placeholder="adjustmentType === 'ADJUSTMENT' ? 'e.g. -2 or 2' : 'e.g. 1'">
                        </label>
                        <p class="admin-subtext adjustment-hint">
                            {{ adjustmentType === 'ADJUSTMENT'
                                ? 'Enter a positive number to add stock or a negative number to remove stock.'
                                : 'Enter a positive amount. It will be deducted from stock.' }}
                        </p>
                        <label>Reason / Notes
                            <textarea v-model="adjustmentNotes" rows="3" maxlength="2000" :disabled="saving" required
                                placeholder="Explain why the inventory changed"></textarea>
                        </label>
                        <p v-if="adjustmentError" class="error-message" role="alert">{{ adjustmentError }}</p>
                        <button type="submit" :disabled="saving">
                            {{ saving ? 'Saving...' : 'Save Adjustment' }}
                        </button>
                    </form>

                    <button type="button" class="history-button"
                        :aria-expanded="expandedProductId === balance.productId"
                        @click="toggleHistory(balance.productId)">
                        {{ expandedProductId === balance.productId ? 'Hide Movement History' : 'View Movement History'
                        }}
                    </button>
                    <div v-if="expandedProductId === balance.productId" class="movement-history">
                        <h4>Inventory History</h4>
                        <p v-if="historyLoading">Loading movement history...</p>
                        <p v-else-if="historyError" class="error-message" role="alert">{{ historyError }}</p>
                        <p v-else-if="movements.length === 0" class="admin-subtext">No inventory movements recorded yet.
                        </p>
                        <div v-else class="movement-list">
                            <div v-for="movement in movements" :key="movement.id" class="movement-entry">
                                <div class="movement-entry-header">
                                    <strong>{{ movementTypeLabel(movement.movementType) }}</strong>
                                    <span :class="{
                                        'positive-movement': Number(movement.quantityChange) > 0,
                                        'negative-movement': Number(movement.quantityChange) < 0
                                    }">
                                        {{ Number(movement.quantityChange) > 0 ? '+' : '' }}{{
                                        formatQuantity(movement.quantityChange) }} {{
                                        movement.baseUnit }}
                                    </span>
                                </div>
                                <small class="admin-subtext">{{ formatDate(movement.createdAt) }}</small>
                                <small v-if="movement.receiptItemId != null" class="admin-subtext">
                                    Receipt item #{{ movement.receiptItemId }}
                                </small>
                                <p v-if="movement.notes">{{ movement.notes }}</p>
                            </div>
                        </div>
                    </div>
                </article>
            </div>
        </section>
    </section>
</template>

<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import { storeToRefs } from 'pinia'
import { useProductStore } from '@/stores/productStore'
import { getPurchasingDepartments } from '@/api/purchasingCategoriesApi'
import {
    getPurchasingInventoryBalances,
    getPurchasingInventoryMovements,
    createPurchasingInventoryAdjustment
} from '@/api/purchasingInventoryApi'

const productStore = useProductStore()
const { products } = storeToRefs(productStore)
const balances = ref([])
const departments = ref([])
const loading = ref(false)
const error = ref('')
const selectedDepartment = ref('all')
const selectedCategory = ref('all')
const search = ref('')
const expandedProductId = ref(null)
const movements = ref([])
const historyLoading = ref(false)
const historyError = ref('')
let historyRequestId = 0

const adjustmentProductId = ref(null)
const adjustmentType = ref('USAGE')
const adjustmentQuantity = ref('')
const adjustmentNotes = ref('')
const adjustmentError = ref('')
const saving = ref(false)

const productById = computed(() => new Map(products.value.map(product => [product.id, product])))

const availableCategories = computed(() => {
    const matching = balances.value.filter(balance => {
        if (selectedDepartment.value === 'all') return true
        const product = productById.value.get(balance.productId)
        return product && String(product.departmentId) === selectedDepartment.value
    })
    return [...new Set(matching.map(balance => balance.category))]
        .filter(Boolean)
        .sort((a, b) => a.localeCompare(b))
})

const filteredBalances = computed(() => {
    const query = search.value.trim().toLowerCase()
    return balances.value.filter(balance => {
        const product = productById.value.get(balance.productId)
        const matchesDepartment = selectedDepartment.value === 'all' ||
            (product && String(product.departmentId) === selectedDepartment.value)
        const matchesCategory = selectedCategory.value === 'all' || balance.category === selectedCategory.value
        const matchesSearch = balance.productName.toLowerCase().includes(query)
        return matchesDepartment && matchesCategory && matchesSearch
    })
})

watch(selectedDepartment, () => { selectedCategory.value = 'all' })

function departmentName(productId) {
    const product = productById.value.get(productId)
    if (!product) return 'Unknown department'
    const department = departments.value.find(item => item.id === product.departmentId)
    return department?.name || 'Unknown department'
}

function formatQuantity(value) {
    const number = Number(value)
    if (!Number.isFinite(number)) return '—'
    return new Intl.NumberFormat('en-US', { maximumFractionDigits: 6 }).format(number)
}

function formatDate(value) {
    if (!value) return 'Not available'
    const date = new Date(value)
    return Number.isNaN(date.getTime()) ? 'Not available' : date.toLocaleString()
}

function movementTypeLabel(type) {
    const labels = {
        RECEIPT: 'Delivery Received',
        OPENING_BALANCE: 'Opening Balance',
        USAGE: 'Inventory Used',
        WASTE: 'Waste',
        ADJUSTMENT: 'Inventory Adjustment'
    }
    return labels[type] || type
}

async function loadInventory() {
    if (loading.value) return
    loading.value = true
    error.value = ''
    try {
        const [inventoryData, departmentData] = await Promise.all([
            getPurchasingInventoryBalances(),
            getPurchasingDepartments(),
            productStore.loadProducts()
        ])
        balances.value = inventoryData
        departments.value = departmentData
    } catch (err) {
        error.value = err.message || 'Unable to load inventory.'
    } finally {
        loading.value = false
    }
}

async function refreshHistory(productId) {
    movements.value = []
    historyError.value = ''
    historyLoading.value = true
    const requestId = ++historyRequestId
    try {
        const data = await getPurchasingInventoryMovements(productId)
        if (requestId === historyRequestId) movements.value = data
    } catch (err) {
        if (requestId === historyRequestId) historyError.value = err.message || 'Unable to load movement history.'
    } finally {
        if (requestId === historyRequestId) historyLoading.value = false
    }
}

async function toggleHistory(productId) {
    if (expandedProductId.value === productId) {
        expandedProductId.value = null
        historyRequestId++
        return
    }
    expandedProductId.value = productId
    await refreshHistory(productId)
}

function toggleAdjustment(productId) {
    if (saving.value) return
    if (adjustmentProductId.value === productId) {
        adjustmentProductId.value = null
        return
    }
    adjustmentProductId.value = productId
    adjustmentType.value = 'USAGE'
    adjustmentQuantity.value = ''
    adjustmentNotes.value = ''
    adjustmentError.value = ''
}

async function submitAdjustment(balance) {
    if (saving.value) return
    adjustmentError.value = ''
    const rawQuantity = String(adjustmentQuantity.value ?? '').trim()
    const quantity = Number(rawQuantity)
    const notes = adjustmentNotes.value.trim()
    const type = adjustmentType.value

    if (!rawQuantity || !Number.isFinite(quantity) || quantity === 0 ||
        !/^-?\d+(?:\.\d{1,6})?$/.test(rawQuantity)) {
        adjustmentError.value = 'Enter a nonzero quantity with up to 6 decimal places.'
        return
    }
    if (type !== 'ADJUSTMENT' && quantity < 0) {
        adjustmentError.value = 'Usage and waste quantities must be positive.'
        return
    }
    if (!notes) {
        adjustmentError.value = 'Please enter a reason for this change.'
        return
    }

    const change = type === 'ADJUSTMENT' ? quantity : -quantity
    const direction = change > 0 ? 'add' : 'remove'
    const confirmed = window.confirm(
        `Confirm ${type.toLowerCase()} for ${balance.productName}:\n` +
        `${direction} ${formatQuantity(Math.abs(change))} ${balance.baseUnit}?\n\n` +
        `Reason: ${notes}`
    )
    if (!confirmed) return

    saving.value = true
    try {
        const updated = await createPurchasingInventoryAdjustment(balance.productId, {
            movementType: type,
            quantity,
            notes
        })
        balances.value = balances.value.map(item =>
            item.productId === balance.productId ? updated : item
        )
        adjustmentProductId.value = null
        adjustmentQuantity.value = ''
        adjustmentNotes.value = ''
        if (expandedProductId.value === balance.productId) {
            await refreshHistory(balance.productId)
        }
    } catch (err) {
        adjustmentError.value = err.message || 'Unable to save inventory adjustment.'
    } finally {
        saving.value = false
    }
}

onMounted(loadInventory)
</script>

<style scoped>
.inventory-heading {
    display: flex;
    justify-content: space-between;
    align-items: center;
    flex-wrap: wrap;
    gap: 1rem;
}

.inventory-heading h2 {
    margin: 0;
}

.inventory-filters {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 1rem;
    margin: 1.25rem 0;
}

.inventory-filters label {
    display: grid;
    gap: 0.4rem;
    font-weight: 700;
}

.inventory-filters input,
.inventory-filters select {
    width: 100%;
    box-sizing: border-box;
}

.inventory-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(min(100%, 300px), 1fr));
    gap: 1rem;
    margin-top: 1rem;
    align-items: start;
}

.inventory-card {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    min-width: 0;
}

.inventory-card-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 0.75rem;
    flex-wrap: wrap;
}

.inventory-card-header h3 {
    margin: 0;
}

.inventory-category {
    font-size: 0.8rem;
    opacity: 0.8;
}

.inventory-quantity {
    display: flex;
    align-items: baseline;
    gap: 0.5rem;
    flex-wrap: wrap;
}

.inventory-quantity strong {
    font-size: 2rem;
    line-height: 1.1;
}

.inventory-quantity span {
    font-weight: 600;
}

.inventory-updated {
    font-size: 0.8rem;
}

.inventory-warning {
    color: #ef9a9a;
    font-weight: 600;
}

.history-button,
.action-button {
    width: 100%;
    margin-top: 0.25rem;
}

.adjustment-form {
    display: grid;
    gap: 0.75rem;
    padding: 1rem 0;
    border-top: 1px solid rgba(190, 145, 65, 0.45);
}

.adjustment-form h4 {
    margin: 0;
}

.adjustment-form label {
    display: grid;
    gap: 0.35rem;
    font-weight: 700;
}

.adjustment-form input,
.adjustment-form select,
.adjustment-form textarea {
    width: 100%;
    box-sizing: border-box;
}

.adjustment-hint {
    font-size: 0.8rem;
    margin: 0;
}

.movement-history {
    display: grid;
    gap: 0.75rem;
    padding-top: 1rem;
    border-top: 1px solid rgba(190, 145, 65, 0.45);
}

.movement-history h4 {
    margin: 0;
}

.movement-list {
    display: grid;
    gap: 0.75rem;
}

.movement-entry {
    display: grid;
    gap: 0.3rem;
    padding: 0.75rem;
    border: 1px solid rgba(190, 145, 65, 0.35);
    border-radius: 6px;
}

.movement-entry p {
    margin: 0;
}

.movement-entry-header {
    display: flex;
    justify-content: space-between;
    gap: 0.75rem;
    flex-wrap: wrap;
}

.positive-movement {
    color: #81c784;
    font-weight: 700;
}

.negative-movement {
    color: #ef9a9a;
    font-weight: 700;
}

@media (max-width: 750px) {
    .inventory-filters {
        grid-template-columns: 1fr;
    }
}
</style>
