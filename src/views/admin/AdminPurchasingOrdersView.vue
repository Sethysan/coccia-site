<template>

    <section class="purchasing-orders">

        <h2>Purchasing Orders</h2>

        <p v-if="!reviewingOrders">
            Select the products and quantities you need.
            We'll organize them by supplier before you review the orders.
        </p>

        <p v-else>
            Review each supplier's products, quantities, packaging,
            and estimated costs. Return to editing if anything needs
            to be changed.
        </p>

        <div class="order-filters">
            <label>
                Department
                <select v-model.number="selectedDepartmentId"
                    @change="changeOrderDepartment">
                    <option :value="null" disabled>Select Kitchen or Bar</option>
                    <option v-for="department in departments.filter(d => d.active)" :key="department.id"
                        :value="department.id">
                        {{ department.name }}
                    </option>
                </select>
            </label>

            <label>
                Category
                <select v-model.number="selectedCategoryId" :disabled="selectedDepartmentId == null">
                    <option :value="null">All Categories</option>
                    <option v-for="category in availableCategories" :key="category.id" :value="category.id">
                        {{ category.name }}
                    </option>
                </select>
            </label>
        </div>

        <p v-if="lookupError" class="pricing-warning" role="alert">
            {{ lookupError }}
        </p>

        <p v-if="loading">Loading products...</p>
        <p v-else-if="error" role="alert">
            Unable to load products: {{ error }}
        </p>

        <p v-else-if="activeProducts.length === 0">
            No active products available.
        </p>

        <div v-else-if="!reviewingOrders" class="order-product-list">

            <div v-for="product in visibleProducts" :key="product.id" class="order-product">
                <span>{{ product.name }}</span>
                <input v-model.number="orderQuantities[product.id]" type="number" min="0" step="1" placeholder="0"
                    @wheel.prevent :aria-label="`Order quantity for ${product.name}`">
                <select v-model.number="selectedOfferings[product.id]" :aria-label="`Supplier for ${product.name}`">
                    <option :value="null">
                        Select Supplier
                    </option>

                    <option v-for="offering in availableOfferingsForProduct(product.id)" :key="offering.id"
                        :value="offering.id">
                        {{
                            vendors.find(v => v.id === offering.vendorId)?.name
                            || 'Unknown Supplier'
                        }}
                        — {{ offering.orderUnit }}
                        — {{ offering.unitPrice != null
                            ? `$${Number(offering.unitPrice).toFixed(2)}`
                            : 'Price unavailable' }}
                    </option>

                </select>

            </div>

        </div>

        <div v-if="!reviewingOrders" class="order-summary">
            <h3>Order Summary</h3>
            <p v-if="selectedProducts.length === 0">
                No products selected yet.
            </p>
            <ul v-else>
                <li v-for="product in selectedProducts" :key="product.id">
                    <strong>{{ product.name }}</strong>
                    — Quantity: {{ product.quantity }}
                    <div class="preferred-supplier">
                        Selected Supplier:
                        {{ selectedVendorName(product.id) }}
                    </div>

                </li>

            </ul>

        </div>

        <div class="supplier-order-groups">
            <h3>Orders by Supplier</h3>
            <p v-if="groupedOrders.length === 0">
                Enter product quantities to begin an order.
            </p>

            <div v-for="group in groupedOrders" :key="group.vendorId ?? 'unassigned'" class="supplier-order-group">
                <h4>{{ group.vendorName }}</h4>
                <div v-if="group.vendorId != null" class="supplier-delivery-schedule">
                    <p>
                        <strong>Next Expected Delivery:</strong>
                        {{
                            formatDeliveryDate(
                                getNextDeliveryDate(
                                    vendors.find(v => v.id === group.vendorId)?.nextDeliveryDate,
                                    vendors.find(v => v.id === group.vendorId)?.deliveryFrequency
                                )
                            )
                        }}
                    </p>
                    <p>
                        <strong>Delivery Frequency:</strong>
                        {{
                            ({
                                WEEKLY: 'Weekly',
                                BIWEEKLY: 'Biweekly',
                                AS_NEEDED: 'As Needed'
                            })[
                            vendors.find(v => v.id === group.vendorId)?.deliveryFrequency
                            ] || 'Not scheduled'
                        }}
                    </p>
                </div>

                <ul>
                    <li v-for="product in group.products" :key="product.id">
                        <strong>{{ product.name }}</strong>
                        — Quantity: {{ product.quantity }}
                        <div v-if="product.offering">
                            {{ product.offering.packageQuantity }}
                            {{ product.offering.packageUnit }}
                            per {{ product.offering.orderUnit }}
                            <span v-if="product.offering.unitPrice != null">
                                —
                                Estimated:
                                ${{ (product.quantity * Number(product.offering.unitPrice)).toFixed(2) }}
                            </span>
                        </div>

                        <div v-else>
                            Supplier and pricing not assigned
                        </div>
                    </li>
                </ul>

                <p class="supplier-subtotal">
                    <strong v-if="!hasIncompletePricing(group)">
                        Estimated Subtotal:
                        ${{ supplierSubtotal(group).toFixed(2) }}
                    </strong>

                    <template v-else>
                        <strong>Estimated Subtotal: Incomplete</strong>
                        <span class="pricing-warning">
                            — Missing supplier assignment or pricing
                        </span>

                    </template>
                </p>

                <div v-if="supplierMinimumStatus(group) && !hasIncompletePricing(group)"
                    class="supplier-minimum-status">
                    <p v-if="supplierMinimumStatus(group).meetsMinimum" class="save-success">
                        ✓ Minimum order met
                        (${{ supplierMinimumStatus(group).minimum.toFixed(2) }} required)

                    </p>

                    <p v-else class="pricing-warning">

                        ⚠ Below supplier minimum:

                        ${{ supplierMinimumStatus(group).amountRemaining.toFixed(2) }}

                        more needed to reach

                        ${{ supplierMinimumStatus(group).minimum.toFixed(2) }}

                    </p>

                    <p v-if="

                        !supplierMinimumStatus(group).meetsMinimum &&

                        supplierMinimumStatus(group).belowMinimumFee != null

                    " class="pricing-warning">

                        Below-minimum order fee:

                        ${{ supplierMinimumStatus(group).belowMinimumFee.toFixed(2) }}

                        (not included in estimated total)

                    </p>

                </div>

                <div v-if="supplierQuantityMinimumStatus(group)" class="supplier-minimum-status">

                    <p v-if="supplierQuantityMinimumStatus(group).meetsMinimum === null" class="pricing-warning">

                        ⚠ Supplier quantity minimum is missing its unit.

                        Check Vendor Management.

                    </p>

                    <p v-else-if="supplierQuantityMinimumStatus(group).meetsMinimum" class="save-success">

                        ✓ Quantity minimum met:

                        {{ supplierQuantityMinimumStatus(group).quantity }}

                        {{ supplierQuantityMinimumStatus(group).unit }}

                        ordered

                    </p>

                    <p v-else class="pricing-warning">

                        ⚠ Below quantity minimum:

                        {{ supplierQuantityMinimumStatus(group).quantityRemaining }}

                        {{ supplierQuantityMinimumStatus(group).unit }}

                        more needed to reach

                        {{ supplierQuantityMinimumStatus(group).minimum }}

                    </p>

                </div>

                <div v-if="reviewingOrders && group.vendorId != null" class="draft-actions">
                    <button type="button" :disabled="savingVendorId === group.vendorId"
                        @click="saveSupplierDraft(group)">
                        {{ savingVendorId === group.vendorId ? 'Saving...' : 'Save Supplier Draft' }}
                    </button>
                    <p v-if="draftErrors[group.vendorId]" class="pricing-warning" role="alert">{{
                        draftErrors[group.vendorId] }}
                    </p>
                    <p v-if="draftSuccess[group.vendorId]" class="save-success">{{ draftSuccess[group.vendorId] }}</p>
                </div>
            </div>

        </div>

        <div v-if="reviewingOrders && selectedProducts.length > 0" class="order-readiness" role="status">

            <p v-if="orderHasIssues" class="pricing-warning">

                ⚠ Order needs attention.

                <span v-if="groupedOrders.some(group => hasIncompletePricing(group))">

                    Some products are missing a supplier assignment or price.

                </span>

                <span v-if="groupedOrders.some(group =>

                    supplierMinimumStatus(group) &&

                    !supplierMinimumStatus(group).meetsMinimum

                )">

                    One or more suppliers have not met their minimum order amount.

                </span>

                <span v-if="groupedOrders.some(group => {

                    const status = supplierQuantityMinimumStatus(group)

                    return status && status.meetsMinimum !== true

                })">

                    One or more suppliers have not met their minimum

                    order quantity, or their required quantity unit is missing.

                </span>

                Review the warnings above before finalizing.

            </p>

            <p v-else class="save-success">

                ✓ Order ready for review: All selected products

                have suppliers and prices.

            </p>

        </div>

        <div v-if="selectedProducts.length > 0" class="order-grand-total">

            <h3>
                Estimated Order Total:
                <template v-if="groupedOrders.some(group => hasIncompletePricing(group))">
                    Incomplete
                </template>
                <template v-else>
                    ${{ estimatedOrderTotal.toFixed(2) }}
                </template>
            </h3>

            <div v-if="estimatedAdditionalFees > 0" class="potential-fees-summary">

                <p>

                    Potential Below-Minimum Fees:

                    <strong>${{ estimatedAdditionalFees.toFixed(2) }}</strong>

                </p>

                <p>

                    Potential Total Including Fees:

                    <strong>${{ estimatedTotalWithFees.toFixed(2) }}</strong>

                </p>

                <p class="pricing-warning">

                    These fees are estimates and are not included

                    in the product subtotal. Confirm that each supplier

                    accepts below-minimum orders before placing an order.

                </p>

            </div>

            <p v-if="groupedOrders.some(group => hasIncompletePricing(group))" class="pricing-warning">

                Some products have missing supplier assignments or prices.

                This total includes only products with known prices.

            </p>

        </div>

        <div v-if="selectedProducts.length > 0" class="order-review-actions">

            <button type="button" @click="reviewingOrders = !reviewingOrders">

                {{ reviewingOrders ? '← Back to Editing' : 'Review Orders →' }}

            </button>

        </div>

    </section>

</template>

<script setup>

import { ref, onMounted, computed } from 'vue'
import { storeToRefs } from 'pinia'
import { useProductStore } from '@/stores/productStore'
import { useVendorProductStore } from '@/stores/vendorProductStore'
import { useVendorStore } from '@/stores/vendorStore'
import { formatDeliveryDate, getNextDeliveryDate } from '@/utils/dateFormat'
import { useOrderStore } from '@/stores/orderStore'
import {
    getPurchasingDepartments,
    getPurchasingCategories
} from '@/api/purchasingCategoriesApi'

const orderStore = useOrderStore()

const {
    orders: savedOrders,
    loading: savedOrdersLoading
} = storeToRefs(orderStore)

const savedOrdersError = ref('')
const savingVendorId = ref(null)
const busyOrderId = ref(null)
const draftErrors = ref({})
const draftSuccess = ref({})
const orderActionErrors = ref({})
const confirmationValues = ref({})

const departments = ref([])
const categories = ref([])
const selectedDepartmentId = ref(null)
const selectedCategoryId = ref(null)
const lookupError = ref('')

function moneyOrUnknown(value) {
    return value == null
        ? 'Price unavailable'
        : `$${Number(value).toFixed(2)}`
}

function offeringProductName(offeringId) {
    const offering = offerings.value.find(o => o.id === offeringId)

    return products.value.find(p => p.id === offering?.productId)?.name
        || `Offering #${offeringId}`
}

function confirmedTotal(order) {
    if (order.items.some(item =>
        item.confirmedQuantity == null ||
        item.confirmedUnitPrice == null
    )) {
        return 'Incomplete'
    }

    const total = order.items.reduce(
        (sum, item) =>
            sum +
            Number(item.confirmedQuantity) *
            Number(item.confirmedUnitPrice),
        0
    )

    return moneyOrUnknown(total)
}

async function loadSavedOrders() {
    savedOrdersError.value = ''

    try {
        await orderStore.loadOrders()

        for (const order of savedOrders.value) {
            for (const item of order.items) {
                if (!confirmationValues.value[item.id]) {
                    confirmationValues.value[item.id] = {
                        quantity: String(
                            item.confirmedQuantity ?? item.plannedQuantity
                        ),
                        price: String(
                            item.confirmedUnitPrice ??
                            item.estimatedUnitPrice ??
                            ''
                        )
                    }
                }
            }
        }
    } catch (err) {
        savedOrdersError.value = err.message
    }
}

async function saveSupplierDraft(group) {
    if (group.vendorId == null) {
        return
    }

    savingVendorId.value = group.vendorId
    draftErrors.value[group.vendorId] = ''
    draftSuccess.value[group.vendorId] = ''

    const vendor = vendors.value.find(
        v => v.id === group.vendorId
    )

    try {
        const saved = await orderStore.saveDraft({
            departmentId: selectedDepartmentId.value,
            vendorId: group.vendorId,
            expectedDeliveryDate: getNextDeliveryDate(
                vendor?.nextDeliveryDate,
                vendor?.deliveryFrequency
            ),
            notes: null,
            items: group.products.map(product => ({
                vendorProductId: product.offering.id,
                plannedQuantity: product.quantity,
                estimatedUnitPrice: product.offering.unitPrice
            }))
        })

        draftSuccess.value[group.vendorId] =
            `Draft order #${saved.id} saved. Saving again creates another draft.`

        await loadSavedOrders()
    } catch (err) {
        draftErrors.value[group.vendorId] = err.message
    } finally {
        savingVendorId.value = null
    }
}

async function confirmSavedOrder(order) {
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
            orderActionErrors.value[order.id] =
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
        `Confirm phone order #${order.id} with ${order.vendorName}? This cannot be edited in the current version.`
    )) {
        return
    }

    busyOrderId.value = order.id
    orderActionErrors.value[order.id] = ''

    try {
        await orderStore.confirmSavedOrder(order.id, { items })
        await loadSavedOrders()
    } catch (err) {
        orderActionErrors.value[order.id] = err.message
    } finally {
        busyOrderId.value = null
    }
}

const productStore = useProductStore()

const vendorProductStore = useVendorProductStore()

const vendorStore = useVendorStore()

const { vendors } = storeToRefs(vendorStore)

const { offerings } = storeToRefs(vendorProductStore)

const orderQuantities = ref({})

const selectedOfferings = ref({})

const reviewingOrders = ref(false)

const { products, loading, error } = storeToRefs(productStore)


const activeProducts = computed(() =>
  products.value.filter(product =>
    product.active &&
    selectedDepartmentId.value != null &&
    product.departmentId === selectedDepartmentId.value
  )
)

const visibleProducts = computed(() =>
  activeProducts.value.filter(product =>
    selectedCategoryId.value == null ||
    product.categoryId === selectedCategoryId.value
  )
)

const selectedProducts = computed(() =>
  activeProducts.value
    .filter(product =>
      Number(orderQuantities.value[product.id]) > 0
    )
    .map(product => ({
      ...product,
      quantity: Number(orderQuantities.value[product.id])
    }))
)

const availableCategories = computed(() =>
    categories.value.filter(category =>
        category.active &&
        category.departmentId === selectedDepartmentId.value
    )
)

function preferredOfferingForProduct(productId) {
    return offerings.value.find(
        offering =>
            offering.productId === productId &&
            offering.active &&
            offering.preferred
    ) || null
}

function selectedOfferingForProduct(productId) {
    const offeringId = selectedOfferings.value[productId]
    if (offeringId == null) return null
    return availableOfferingsForProduct(productId).find(
        offering => offering.id === Number(offeringId)
    ) || null
}

function availableOfferingsForProduct(productId) {
    return offerings.value.filter(
        offering =>
            offering.productId === productId &&
            offering.active
    )
}

function selectedVendorName(productId) {
    const offering = selectedOfferingForProduct(productId)
    if (!offering) {
        return 'No supplier selected'
    }

    const vendor = vendors.value.find(
        vendor => vendor.id === offering.vendorId
    )
    return vendor?.name || 'Supplier not found'
}

const groupedOrders = computed(() => {
    const groups = {}
    for (const product of selectedProducts.value) {

        const offering = selectedOfferingForProduct(product.id)
        const vendorId = offering?.vendorId ?? null
        const key = vendorId === null
            ? 'unassigned'
            : String(vendorId)
        if (!groups[key]) {

            groups[key] = {

                vendorId,
                vendorName: vendorId === null
                    ? 'Unassigned Supplier'
                    : vendors.value.find(vendor => vendor.id === vendorId)?.name
                    || 'Supplier not found',
                products: []

            }

        }

        groups[key].products.push({
            ...product,
            offering
        })
    }
    return Object.values(groups)
})

function supplierSubtotal(group) {
    return group.products.reduce((total, product) => {
        const price = product.offering?.unitPrice
        if (price == null) return total
        return total + product.quantity * Number(price)
    }, 0)
}

function supplierMinimumStatus(group) {
    if (group.vendorId == null) return null
    const vendor = vendors.value.find(
        vendor => vendor.id === group.vendorId
    )

    if (!vendor || vendor.minimumOrderAmount == null) {
        return null

    }

    const minimum = Number(vendor.minimumOrderAmount)
    const subtotal = supplierSubtotal(group)
    const belowMinimumFee =
        vendor.belowMinimumFee == null
            ? null
            : Number(vendor.belowMinimumFee)

    return {
        minimum,
        subtotal,
        meetsMinimum: subtotal >= minimum,
        amountRemaining: Math.max(0, minimum - subtotal),
        belowMinimumFee
    }
}

function estimatedBelowMinimumFee(group) {

    if (hasIncompletePricing(group)) return 0
    const status = supplierMinimumStatus(group)
    if (
        !status ||
        status.meetsMinimum ||
        status.belowMinimumFee == null
    ) {
        return 0
    }
    return status.belowMinimumFee
}

const estimatedAdditionalFees = computed(() =>

    groupedOrders.value.reduce(

        (total, group) =>
            total + estimatedBelowMinimumFee(group),
        0
    )
)

const estimatedTotalWithFees = computed(() =>
    estimatedOrderTotal.value + estimatedAdditionalFees.value
)

function supplierQuantityMinimumStatus(group) {
    if (group.vendorId == null) return null
    const vendor = vendors.value.find(
        vendor => vendor.id === group.vendorId
    )
    if (!vendor || vendor.minimumOrderQuantity == null) {
        return null
    }
    const minimum = Number(vendor.minimumOrderQuantity)
    const unit = vendor.minimumOrderQuantityUnit?.trim() || null
    if (!unit) {
        return {
            minimum,
            unit: null,
            quantity: null,
            meetsMinimum: null,
            quantityRemaining: null
        }
    }
    const quantity = matchingOrderQuantity(group, unit)

    return {

        minimum,
        unit,
        quantity,
        meetsMinimum: quantity >= minimum,
        quantityRemaining: Math.max(0, minimum - quantity)

    }

}

function matchingOrderQuantity(group, requiredUnit) {

    if (!requiredUnit) return null

    const normalize = value => {

        const unit = String(value || '').trim().toLowerCase()
        const singularUnits = {

            cases: 'case',
            bags: 'bag',
            bottles: 'bottle',
            cans: 'can',
            kegs: 'keg',
            boxes: 'box',
            packs: 'pack',
            cartons: 'carton'

        }

        return singularUnits[unit] || unit

    }

    return group.products.reduce((total, product) => {

        const orderUnit = product.offering?.orderUnit

        if (normalize(orderUnit) !== normalize(requiredUnit)) {

            return total

        }

        return total + product.quantity

    }, 0)

}


const departmentOrderSelections = ref({})

function changeOrderDepartment() {
  const departmentId = selectedDepartmentId.value

  // Each department keeps its own quantities and supplier choices.
  if (!departmentOrderSelections.value[departmentId]) {
    departmentOrderSelections.value[departmentId] = {
      quantities: {},
      offerings: {}
    }
  }

  const selection = departmentOrderSelections.value[departmentId]

  // Restore the selected department's unfinished order.
  orderQuantities.value = selection.quantities
  selectedOfferings.value = selection.offerings

  // Assign preferred suppliers only to products without a selection.
  for (const product of products.value) {
    if (
      product.active &&
      product.departmentId === departmentId &&
      !(product.id in selectedOfferings.value)
    ) {
      const preferred = preferredOfferingForProduct(product.id)
      selectedOfferings.value[product.id] = preferred?.id ?? null
    }
  }

  selectedCategoryId.value = null
  reviewingOrders.value = false
  draftErrors.value = {}
  draftSuccess.value = {}
}


const estimatedOrderTotal = computed(() =>

    groupedOrders.value.reduce(

        (total, group) => total + supplierSubtotal(group),

        0

    )

)

function hasIncompletePricing(group) {

    return group.products.some(

        product =>
            !product.offering ||
            product.offering.unitPrice == null

    )

}

const orderHasIssues = computed(() =>

    groupedOrders.value.some(group => {

        if (hasIncompletePricing(group)) {

            return true

        }

        const dollarMinimum = supplierMinimumStatus(group)

        if (dollarMinimum && !dollarMinimum.meetsMinimum) {

            return true

        }

        const quantityMinimum = supplierQuantityMinimumStatus(group)

        if (quantityMinimum && quantityMinimum.meetsMinimum !== true) {

            return true

        }

        return false

    })

)

onMounted(async () => {

    try {
        const [departmentData, categoryData] = await Promise.all([
            getPurchasingDepartments(),
            getPurchasingCategories()
        ])

        departments.value = departmentData
        categories.value = categoryData
    } catch (err) {
        lookupError.value = err.message
    }
    try {
        await productStore.loadProducts()
        for (const product of products.value) {
            if (orderQuantities.value[product.id] == null) {
                orderQuantities.value[product.id] = 0
            }
        }
    } catch (err) {
    }
    try {
        await vendorProductStore.loadOfferings()
        for (const product of products.value) {
            const preferred = preferredOfferingForProduct(product.id)
            selectedOfferings.value[product.id] = preferred?.id ?? null
        }
    } catch (err) {
    }
    try {
        await vendorStore.loadVendors()
    } catch (err) {
    }
    await loadSavedOrders()
})

</script>

<style scoped>
.purchasing-orders {

    display: grid;

    gap: 1.5rem;

}

.supplier-order-groups {

    display: grid;

    gap: 1rem;

    margin-top: 1rem;

}

.supplier-order-group {

    padding: 1.25rem;

    border: 1px solid rgba(190, 145, 65, 0.45);

    border-radius: 8px;

    background: rgba(15, 12, 10, 0.88);

}

.supplier-order-group h4 {

    margin: 0 0 1rem;

    font-size: 1.15rem;

}

.supplier-order-group ul {

    padding-left: 1.5rem;

}

.supplier-order-group li {

    margin-bottom: 0.75rem;

}

.supplier-subtotal {

    margin-top: 1rem;

    padding-top: 0.75rem;

    border-top: 1px solid rgba(190, 145, 65, 0.35);

}

.pricing-warning {

    color: #ef9a9a;

}

.order-product-list {

    display: grid;

    gap: 0.5rem;

    max-width: 700px;

}

.order-product {

    display: grid;

    grid-template-columns: minmax(0, 1fr) 100px;

    align-items: center;

    gap: 1rem;

    padding: 0.65rem 0.85rem;

    border: 1px solid rgba(190, 145, 65, 0.35);

    border-radius: 6px;

    background: rgba(15, 12, 10, 0.85);

}

.order-product input {

    width: 100%;

    box-sizing: border-box;

    padding: 0.5rem;

    text-align: center;

    font-size: 1rem;

}

.order-grand-total {

    padding: 1.25rem;

    margin-top: 0.5rem;

    border: 2px solid rgba(190, 145, 65, 0.7);

    border-radius: 8px;

    background: rgba(15, 12, 10, 0.92);

}

.order-grand-total h3 {

    margin: 0;

    font-size: 1.5rem;

}

.order-grand-total .pricing-warning {

    margin: 0.75rem 0 0;

    color: #ef9a9a;

}

.order-readiness {

    padding: 0.85rem 1rem;

    border-radius: 6px;

    background: rgba(15, 12, 10, 0.88);

    border: 1px solid rgba(190, 145, 65, 0.35);

}

.order-readiness p {

    margin: 0;

    font-weight: 700;

}

.order-readiness .save-success {

    color: #81c784;

}

.order-readiness .pricing-warning {

    color: #ef9a9a;

}


.saved-orders-panel {
    display: grid;
    gap: 1rem;
}

.confirmation-line {
    display: grid;
    gap: 0.5rem;
    padding: 0.75rem 0;
    border-top: 1px solid rgba(190, 145, 65, .3);
}

.confirmation-line label {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 0.5rem;
}

.confirmation-line input {
    width: 8rem;
    padding: 0.4rem;
}

.draft-actions {
    margin-top: 1rem;
}
.order-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.order-filters label {
  display: grid;
  gap: 0.4rem;
  min-width: 180px;
}
</style>
