<template>
    <section>
        <header class="page-header">
            <div>
                <h1>Product Management</h1>
                <p class="admin-subtext">
                    Manage purchasing products and their supplier options.
                </p>
            </div>
        </header>

        <section class="admin-card">
            <h2>Purchasing Products</h2>


            <button type="button" :disabled="saving" @click="startCreating">
                + Add Product
            </button>


            <div class="product-filters">
                <label>
                    Search Products
                    <input v-model="productSearch" type="search" placeholder="Search by product name...">
                </label>

                <label>
                    Product Status
                    <select v-model="productStatus">
                        <option value="active">Active Products</option>
                        <option value="inactive">Inactive Products</option>
                        <option value="all">All Products</option>
                    </select>
                </label>
            </div>


            <section v-if="editingProduct" ref="productEditorElement" class="admin-card product-editor">

                <h3>
                    {{
                        editingProduct.id == null
                            ? 'Add New Product'
                            : `Edit Product: ${editingProduct.name}`
                    }}
                </h3>

                <div class="product-fields">

                    <label>
                        Department
                        <select v-model.number="editingProduct.departmentId" required
                            @change="editingProduct.categoryId = null">
                            <option :value="null" disabled>
                                Select a department
                            </option>
                            <option v-for="department in departments.filter(d => d.active)" :key="department.id"
                                :value="department.id">
                                {{ department.name }}
                            </option>
                        </select>
                    </label>

                    <label>
                        Category
                        <select v-model.number="editingProduct.categoryId" required
                            :disabled="!editingProduct.departmentId">
                            <option :value="null" disabled>
                                Select a category
                            </option>
                            <option v-for="category in categories.filter(
                                c => c.active &&
                                    c.departmentId === editingProduct.departmentId
                            )" :key="category.id" :value="category.id">
                                {{ category.name }}
                            </option>
                        </select>
                    </label>

                    <label>
                        Product Name
                        <input v-model="editingProduct.name" type="text" maxlength="200" required>
                    </label>

                    <label>
                        Description
                        <textarea v-model="editingProduct.description" rows="3"
                            placeholder="Optional product description"></textarea>
                    </label>
                </div>

                <label class="product-active-control">
                    <input v-model="editingProduct.active" type="checkbox">
                    Active Product
                </label>


                <button type="button" :disabled="saving" @click="saveProduct">
                    {{ saving ? 'Saving...' : 'Save Product' }}
                </button>

                <p v-if="saveError" class="error-message" role="alert">
                    {{ saveError }}
                </p>


                <button type="button" @click="editingProduct = null">
                    Cancel Editing
                </button>
            </section>


            <p v-if="saveSuccess" class="save-success" role="status">
                {{ saveSuccess }}
            </p>


            <p v-if="loading">Loading products...</p>

            <p v-else-if="error" role="alert">
                {{ error }}
            </p>

            <p v-else-if="products.length === 0">
                No products have been added yet.
            </p>

            <p v-else-if="filteredProducts.length === 0" class="admin-subtext">
                No products match your search or status filter.
            </p>

            <div v-else class="product-grid">
                <article v-for="product in filteredProducts" :key="product.id" class="admin-card product-card">
                    <div class="product-header">
                        <h3>{{ product.name }}</h3>

                        <span :class="product.active ? 'active' : 'inactive'">
                            {{ product.active ? 'Active' : 'Inactive' }}
                        </span>
                    </div>

                    <p v-if="product.description">
                        {{ product.description }}
                    </p>


                    <p>
                        <strong>Department:</strong>
                        {{ departmentName(product.departmentId) }}
                    </p>

                    <p>
                        <strong>Category:</strong>
                        {{ categoryName(product.categoryId) }}
                    </p>

                    <p>
                        <strong>Supplier Options:</strong>
                        {{ offeringCount(product.id) }}
                    </p>

                    <div v-if="!offeringsLoading && !offeringsError &&
                        offeringCount(product.id) > 0" class="supplier-options">
                        <h4>Available Suppliers</h4>

                        <div v-for="offering in offeringsForProduct(product.id)" :key="offering.id"
                            class="supplier-option">
                            <p>
                                <strong>{{ vendorName(offering.vendorId) }}</strong>
                                <span v-if="offering.preferred" class="preferred-label">
                                    ★ Preferred
                                </span>
                            </p>

                            <p>
                                {{ offering.packageQuantity }}
                                {{ offering.packageUnit }}
                                per {{ offering.orderUnit }}
                            </p>

                            <p>
                                <strong>Price:</strong>
                                {{
                                    offering.unitPrice != null
                                        ? `$${Number(offering.unitPrice).toFixed(2)}`
                                        : 'Not specified'
                                }}
                            </p>

                            <button type="button" class="edit-offering-button" @click="startEditingOffering(offering)">
                                ✎ Edit Offering
                            </button>

                            <button type="button" @click="removeOffering(offering)">
                                Remove from Product
                            </button>

                        </div>
                    </div>

                    <p v-if="offeringsLoading" class="admin-subtext">
                        Loading supplier options...
                    </p>

                    <p v-else-if="offeringsError" class="error-message" role="alert">
                        Unable to load supplier options: {{ offeringsError }}
                    </p>

                    <button type="button" class="edit-product-button" @click="startEditing(product)">
                        ✎ Edit Product
                    </button>

                    <button type="button" @click="startManagingSuppliers(product)">
                        {{
                            managingSuppliersFor === product.id
                                ? 'Close Suppliers'
                                : 'Manage Suppliers'
                        }}
                    </button>

                    <div v-if="managingSuppliersFor === product.id" class="supplier-management">
                        <h4>Manage Suppliers</h4>

                        <button type="button" @click="startAddingOffering(product)">
                            + Add Supplier
                        </button>


                        <div v-if="editingOffering?.productId === product.id" ref="supplierEditorElement"
                            class="supplier-offering-editor">
                            <h4>
                                {{ editingOffering.id == null ? 'Add Supplier Offering' : 'Edit Supplier Offering' }}
                            </h4>

                            <label>
                                Supplier
                                <select v-model.number="editingOffering.vendorId" required>
                                    <option :value="null" disabled>
                                        Select a supplier
                                    </option>

                                    <option v-for="vendor in vendors.filter(v => v.active)" :key="vendor.id"
                                        :value="vendor.id">
                                        {{ vendor.name }}
                                    </option>
                                </select>
                            </label>

                            <label>
                                Supplier SKU
                                <input v-model="editingOffering.vendorSku" type="text" maxlength="100"
                                    placeholder="Optional supplier item number">
                            </label>

                            <label>
                                Order Unit
                                <input v-model="editingOffering.orderUnit" type="text" maxlength="50"
                                    placeholder="e.g. Bag, Case, Box" required>
                            </label>

                            <label>
                                Package Quantity
                                <input v-model.number="editingOffering.packageQuantity" type="number" min="0.001"
                                    step="0.001" required @wheel.prevent>
                            </label>

                            <label>
                                Package Unit
                                <input v-model="editingOffering.packageUnit" type="text" maxlength="50"
                                    placeholder="e.g. Pound, Ounce, Each" required>
                            </label>

                            <label>
                                Price per Order Unit ($)
                                <input v-model.number="editingOffering.unitPrice" type="number" min="0" step="0.1"
                                    placeholder="e.g. 20.79" @wheel.prevent>
                            </label>

                            <label class="supplier-checkbox">
                                <input v-model="editingOffering.preferred" type="checkbox">
                                Preferred Supplier
                            </label>

                            <label class="supplier-checkbox">
                                <input v-model="editingOffering.active" type="checkbox">
                                Active Supplier Offering
                            </label>

                            <div class="supplier-form-actions">

                                <button type="button" :disabled="offeringSaving" @click="saveOffering">
                                    {{ offeringSaving ? 'Saving...' : 'Save Supplier Offering' }}
                                </button>


                                <button type="button" @click="cancelOfferingEdit">
                                    Cancel
                                </button>
                            </div>

                            <p v-if="offeringSaveError" class="error-message" role="alert">
                                {{ offeringSaveError }}
                            </p>

                        </div>

                        <p v-if="offeringSaveSuccess && managingSuppliersFor === product.id" class="save-success"
                            role="status">
                            {{ offeringSaveSuccess }}
                        </p>

                    </div>

                </article>
            </div>
        </section>
    </section>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useProductStore } from '@/stores/productStore'

import { useVendorStore } from '@/stores/vendorStore'

import { useVendorProductStore } from '@/stores/vendorProductStore'

import {
    getPurchasingDepartments,
    getPurchasingCategories
} from '@/api/purchasingCategoriesApi'

const productStore = useProductStore()

const vendorProductStore = useVendorProductStore()

const offeringSaveError = ref('')
const offeringSaveSuccess = ref('')

const { saving: offeringSaving } = storeToRefs(vendorProductStore)

const vendorStore = useVendorStore()

const { vendors } = storeToRefs(vendorStore)

const {
    offerings,
    loading: offeringsLoading,
    error: offeringsError
} = storeToRefs(vendorProductStore)

const { products, loading, saving, error } = storeToRefs(productStore)

const saveError = ref('')
const saveSuccess = ref('')


const productSearch = ref('')
const productStatus = ref('active')

const departments = ref([])
const categories = ref([])
const lookupError = ref('')

const editingProduct = ref(null)

const productEditorElement = ref(null)

const managingSuppliersFor = ref(null)

const editingOffering = ref(null)
const supplierEditorElement = ref(null)

function startAddingOffering(product) {
    editingOffering.value = {
        id: null,
        productId: product.id,
        vendorId: null,
        vendorSku: '',
        orderUnit: '',
        packageQuantity: 1,
        packageUnit: '',
        unitPrice: null,
        preferred: false,
        active: true
    }
}

function cancelOfferingEdit() {
    editingOffering.value = null
}

function startManagingSuppliers(product) {
    offeringSaveSuccess.value = ''
    offeringSaveError.value = ''
    managingSuppliersFor.value =
        managingSuppliersFor.value === product.id
            ? null
            : product.id
}

async function startEditing(product) {
    editingProduct.value = { ...product }

    await nextTick()

    productEditorElement.value?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
    })
}

function startCreating() {
    saveError.value = ''
    saveSuccess.value = ''

    editingProduct.value = {
        id: null,
        departmentId: null,
        categoryId: null,
        name: '',
        description: '',
        active: true
    }
}

async function saveProduct() {
    if (!editingProduct.value || saving.value) return

    saveError.value = ''
    saveSuccess.value = ''

    const product = editingProduct.value

    const payload = {
        departmentId: product.departmentId,
        categoryId: product.categoryId,
        name: product.name.trim(),
        description: product.description?.trim() || null,
        active: product.active
    }

    if (!payload.name) {
        saveError.value = 'Product name is required.'
        return
    }


    if (payload.departmentId == null) {
        saveError.value = 'Please select a department.'
        return
    }

    if (payload.categoryId == null) {
        saveError.value = 'Please select a category.'
        return
    }

    try {

        const savedProduct = product.id == null
            ? await productStore.addProduct(payload)
            : await productStore.saveProduct(product.id, payload)


        editingProduct.value = null
        saveSuccess.value =
            `${savedProduct.name} saved successfully.`
    } catch (err) {
        saveError.value = err.message
    }
}


const filteredProducts = computed(() => {
    const query = productSearch.value.trim().toLowerCase()

    return products.value.filter(product => {
        const matchesName = product.name.toLowerCase().includes(query)

        const matchesStatus =
            productStatus.value === 'all' ||
            (productStatus.value === 'active' && product.active) ||
            (productStatus.value === 'inactive' && !product.active)

        return matchesName && matchesStatus
    })
})

function departmentName(departmentId) {
    const department = departments.value.find(
        department => department.id === departmentId
    )

    return department?.name || 'Unknown department'
}

function categoryName(categoryId) {
    const category = categories.value.find(
        category => category.id === categoryId
    )

    return category?.name || 'Unknown category'
}

function offeringCount(productId) {
    return offeringsForProduct(productId).length
}

function offeringsForProduct(productId) {
    return offerings.value.filter(
        offering =>
            offering.productId === productId &&
            offering.active
    )
}

function vendorName(vendorId) {
    const vendor = vendors.value.find(
        vendor => vendor.id === vendorId
    )

    return vendor?.name || 'Unknown supplier'
}

onMounted(async () => {
    try {
        await productStore.loadProducts()
    } catch (err) {
        // The product store already records the error.
    }

    try {
        const [departmentData, categoryData] = await Promise.all([
            getPurchasingDepartments(),
            getPurchasingCategories()
        ])

        departments.value = departmentData
        categories.value = categoryData
    } catch (err) {
        console.error('Unable to load purchasing departments or categories:', err)
        lookupError.value = err.message
    }

    try {
        await vendorProductStore.loadOfferings()
    } catch (err) {
        // The vendor product store already records the error.
    }

    try {
        await vendorStore.loadVendors()
    } catch (err) {
        // The vendor store already records the error.
    }

})

async function startEditingOffering(offering) {
    offeringSaveError.value = ''
    offeringSaveSuccess.value = ''

    managingSuppliersFor.value = offering.productId

    editingOffering.value = {
        id: offering.id,
        productId: offering.productId,
        vendorId: offering.vendorId,
        vendorSku: offering.vendorSku,
        orderUnit: offering.orderUnit,
        packageQuantity: offering.packageQuantity,
        packageUnit: offering.packageUnit,
        unitPrice: offering.unitPrice,
        preferred: offering.preferred,
        active: offering.active
    }

    await nextTick()

    const editor = Array.isArray(supplierEditorElement.value)
        ? supplierEditorElement.value[0]
        : supplierEditorElement.value

    editor?.scrollIntoView({
        behavior: 'smooth',
        block: 'start'
    })

}

async function saveOffering() {
    if (!editingOffering.value || offeringSaving.value) return

    offeringSaveError.value = ''
    offeringSaveSuccess.value = ''

    const offering = editingOffering.value

    const payload = {
        productId: offering.productId,
        vendorId: offering.vendorId,
        vendorSku: offering.vendorSku?.trim() || null,
        orderUnit: offering.orderUnit?.trim() || '',
        packageQuantity: Number(offering.packageQuantity),
        packageUnit: offering.packageUnit?.trim() || '',
        unitPrice:
            offering.unitPrice === null ||
                offering.unitPrice === ''
                ? null
                : Number(offering.unitPrice),
        preferred: offering.preferred,
        active: offering.active
    }

    if (payload.vendorId == null) {
        offeringSaveError.value = 'Please select a supplier.'
        return
    }

    if (!payload.orderUnit || !payload.packageUnit) {
        offeringSaveError.value =
            'Order unit and package unit are required.'
        return
    }

    if (
        !Number.isFinite(payload.packageQuantity) ||
        payload.packageQuantity <= 0
    ) {
        offeringSaveError.value =
            'Package quantity must be greater than zero.'
        return
    }

    if (
        payload.unitPrice !== null &&
        (!Number.isFinite(payload.unitPrice) || payload.unitPrice < 0)
    ) {
        offeringSaveError.value =
            'Price must be zero or greater.'
        return
    }

    try {
        const saved = offering.id == null
            ? await vendorProductStore.addOffering(payload)
            : await vendorProductStore.saveOffering(offering.id, payload)

        editingOffering.value = null
        offeringSaveSuccess.value =
            `Supplier offering #${saved.id} saved successfully.`
    } catch (err) {
        offeringSaveError.value =
            err.message || 'Unable to save supplier offering.'
    }
}

async function removeOffering(offering) {
    const confirmed = window.confirm(
        `Remove ${vendorName(offering.vendorId)} from this product?`
    )

    if (!confirmed) return

    offeringSaveError.value = ''
    offeringSaveSuccess.value = ''

    try {
        await vendorProductStore.saveOffering(offering.id, {
            productId: offering.productId,
            vendorId: offering.vendorId,
            vendorSku: offering.vendorSku,
            orderUnit: offering.orderUnit,
            packageQuantity: offering.packageQuantity,
            packageUnit: offering.packageUnit,
            unitPrice: offering.unitPrice,
            preferred: false,
            active: false
        })

        offeringSaveSuccess.value =
            'Supplier removed from this product.'
    } catch (err) {
        offeringSaveError.value =
            err.message || 'Unable to remove supplier.'
    }
}

</script>

<style scoped>
.product-filters {
    display: grid;
    grid-template-columns: repeat(2, minmax(0, 350px));
    gap: 1rem;
    margin-top: 1rem;
}

.product-filters label {
    display: grid;
    gap: 0.4rem;
    font-weight: 700;
}

.product-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill,
            minmax(min(100%, 300px), 1fr));
    gap: 1rem;
    margin-top: 1rem;
    align-items: start;
}

.product-card {
    display: flex;
    flex-direction: column;
    gap: 0.65rem;
    min-width: 0;
    align-self: start;
}

.product-card>button {
    width: 100%;
}

.edit-product-button {
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.3);
    color: #f0f0f0;
}

.edit-product-button:hover {
    background: rgba(255, 255, 255, 0.16);
    border-color: rgba(255, 255, 255, 0.6);
}

.product-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
}

.product-header h3,
.product-card p {
    margin: 0;
}

.active {
    color: #81c784;
}

.inactive {
    color: #ef9a9a;
}


.product-editor {
    display: grid;
    gap: 1rem;
    margin-top: 1.5rem;
    margin-bottom: 1.5rem;
    scroll-margin-top: calc(var(--header-height, 0px) + 24px);
}

.product-editor h3 {
    margin: 0;
}

.product-fields {
    display: grid;
    gap: 1rem;
}

.product-fields label {
    display: grid;
    gap: 0.4rem;
    font-weight: 700;
}

.product-fields input,
.product-fields select,
.product-fields textarea {
    width: 100%;
    box-sizing: border-box;
}

.product-active-control {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    font-weight: 700;
}

.product-active-control input {
    width: auto;
}

.product-editor button {
    justify-self: start;
}

.supplier-options {
    display: grid;
    gap: 0.75rem;
    margin-top: 0.5rem;
}

.supplier-options h4 {
    margin: 0;
}

.supplier-option {
    padding: 0.75rem;
    border: 1px solid rgba(190, 145, 65, 0.45);
    border-radius: 6px;
    display: grid;
    gap: 0.35rem;
}

.supplier-option p {
    margin: 0;
}

.supplier-option button {
    width: auto;
    justify-self: end;
    padding: 0.4rem 0.75rem;
    font-size: 0.85rem;
    font-weight: 600;
    color: #ef9a9a;
    border: 1px solid rgba(239, 154, 154, 0.55);
    background: transparent;
    border-radius: 6px;
}

.supplier-option button:hover {
    color: #fff;
    background: rgba(180, 55, 55, 0.25);
    border-color: #ef9a9a;
}

.preferred-label {
    display: inline-block;
    margin-left: 0.5rem;
    color: #81c784;
    font-size: 0.85rem;
}

.save-success {
    color: #81c784;
    font-weight: 700;
}

.supplier-offering-editor {
    display: grid;
    gap: 0.75rem;
    margin-top: 1rem;
    scroll-margin-top: calc(var(--header-height, 0px) + 24px);
}

.supplier-offering-editor label {
    display: grid;
    gap: 0.4rem;
    font-weight: 700;
}

.supplier-offering-editor input,
.supplier-offering-editor select {
    width: 100%;
    box-sizing: border-box;
}

.supplier-offering-editor .supplier-checkbox {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.supplier-checkbox input {
    width: auto;
}

.supplier-form-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;
    margin-top: 0.5rem;
}

@media (max-width: 650px) {
    .product-filters {
        grid-template-columns: 1fr;
    }
}
</style>
