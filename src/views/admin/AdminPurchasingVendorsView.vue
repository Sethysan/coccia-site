<template>
    <section>
        <header class="page-header">
            <div>
                <h1>Vendor Management</h1>
                <p class="admin-subtext">
                    Manage purchasing vendors, delivery schedules,
                    minimum orders, and additional fees.
                </p>
            </div>
        </header>

        <section class="admin-card">
            <h2>Purchasing Vendors</h2>

            <button type="button" :disabled="saving" @click="startCreating">
                + Add Vendor
            </button>

            <div class="vendor-filters">
                <label class="vendor-search">
                    Search Vendors
                    <input v-model="vendorSearch" type="search" placeholder="Search by vendor name...">
                </label>

                <label class="vendor-status">
                    Vendor Status
                    <select v-model="vendorStatus">
                        <option value="active">Active Vendors</option>
                        <option value="inactive">Inactive Vendors</option>
                        <option value="all">All Vendors</option>
                    </select>
                </label>
            </div>

            <section v-if="editingVendor" class="admin-card vendor-editor">
                <h3>
                    {{ editingVendor.id == null
                        ? 'Add New Vendor'
                        : `Edit Vendor: ${editingVendor.name}` }}
                </h3>

                <div class="vendor-fields">
                    <label>
                        Vendor Name
                        <input v-model="editingVendor.name" type="text" maxlength="150" required>
                    </label>

                    <label>
                        Phone Number
                        <input v-model="editingVendor.phone" type="tel" maxlength="30" placeholder="Optional">
                    </label>

                    <label>
                        Website
                        <input v-model="editingVendor.website" type="url" maxlength="500"
                            placeholder="https://example.com">
                    </label>

                    <label>
                        Preferred Delivery Days
                        <input v-model="editingVendor.preferredDeliveryDays" type="text" maxlength="150"
                            placeholder="e.g. Monday, Thursday">
                    </label>

                    <label>
                        Next Expected Delivery Date
                        <input v-model="editingVendor.nextDeliveryDate" type="date">
                    </label>

                    <label>
                        Delivery Frequency
                        <select v-model="editingVendor.deliveryFrequency">
                            <option value="">Not Scheduled</option>
                            <option value="WEEKLY">Weekly</option>
                            <option value="BIWEEKLY">Biweekly (Every 2 Weeks)</option>
                            <option value="AS_NEEDED">As Needed</option>
                        </select>
                    </label>

                </div>


                <h4>Ordering Requirements & Fees</h4>

                <div class="vendor-fields">
                    <label>
                        Minimum Order Amount ($)
                        <input v-model="editingVendor.minimumOrderAmount" type="number" min="0" step="0.01"
                            placeholder="Optional">
                    </label>

                    <label>
                        Minimum Order Quantity
                        <input v-model="editingVendor.minimumOrderQuantity" type="number" min="0.001" step="0.001"
                            placeholder="Optional">
                    </label>

                    <label>
                        Quantity Unit
                        <input v-model="editingVendor.minimumOrderQuantityUnit" type="text" maxlength="50"
                            placeholder="e.g. cases, bottles, kegs">
                    </label>

                    <label>
                        Below-Minimum Order Fee ($)
                        <input v-model="editingVendor.belowMinimumFee" type="number" min="0" step="0.01"
                            placeholder="Optional">
                    </label>

                    <label>
                        Off-Schedule Delivery Fee ($)
                        <input v-model="editingVendor.offScheduleDeliveryFee" type="number" min="0" step="0.01"
                            placeholder="Optional">
                    </label>
                </div>


                <div class="vendor-notes">
                    <label for="vendor-notes">
                        Vendor Notes
                    </label>

                    <textarea id="vendor-notes" v-model="editingVendor.notes" rows="4"
                        placeholder="Ordering instructions, contact details, delivery requirements, etc."></textarea>
                </div>

                <label class="vendor-active-control">
                    <input v-model="editingVendor.active" type="checkbox">
                    Active Vendor
                </label>



                <div class="vendor-actions">
                    <button type="button" :disabled="saving" @click="saveVendor">
                        {{ saving
                            ? 'Saving...'
                            : editingVendor.id == null
                                ? 'Create Vendor'
                                : 'Save Vendor' }}
                    </button>

                    <button type="button" :disabled="saving" @click="editingVendor = null">
                        Cancel Editing
                    </button>
                </div>

                <p v-if="saveError" class="error-message" role="alert">
                    {{ saveError }}
                </p>

            </section>

            <p v-if="saveSuccess" class="save-success" role="status">
                {{ saveSuccess }}
            </p>

            <p v-if="loading">Loading vendors...</p>

            <p v-else-if="error" role="alert">
                {{ error }}
            </p>

            <p v-else-if="vendors.length === 0">
                No vendors have been added yet.
            </p>

            <p v-else-if="filteredVendors.length === 0" class="admin-subtext">
                No vendors match your current search or status filter.
            </p>

            <div v-else class="vendor-grid">
                <article v-for="vendor in filteredVendors" :key="vendor.id" class="admin-card vendor-card">
                    <div class="vendor-header">
                        <h3>{{ vendor.name }}</h3>

                        <span :class="vendor.active ? 'active' : 'inactive'">
                            {{ vendor.active ? 'Active' : 'Inactive' }}
                        </span>
                    </div>

                    <p>
                        <strong>Delivery Days:</strong>
                        {{ vendor.preferredDeliveryDays || 'Not specified' }}
                    </p>


                    <p>
                        <strong>Next Delivery:</strong>
                        {{
                            formatDeliveryDate(
                                getNextDeliveryDate(
                                    vendor.nextDeliveryDate,
                                    vendor.deliveryFrequency
                        )
                        )
                        }}
                    </p>


                    <p>
                        <strong>Delivery Frequency:</strong>
                        {{
                            vendor.deliveryFrequency === 'WEEKLY'
                                ? 'Weekly'
                                : vendor.deliveryFrequency === 'BIWEEKLY'
                                    ? 'Biweekly'
                                    : vendor.deliveryFrequency === 'AS_NEEDED'
                                        ? 'As Needed'
                                        : 'Not scheduled'
                        }}
                    </p>

                    <p>
                        <strong>Minimum Order:</strong>
                        {{
                            vendor.minimumOrderAmount != null
                                ? `$${Number(vendor.minimumOrderAmount).toFixed(2)}`
                                : 'None specified'
                        }}
                    </p>

                    <p v-if="vendor.minimumOrderQuantity != null">
                        <strong>Minimum Quantity:</strong>
                        {{ vendor.minimumOrderQuantity }}
                        {{ vendor.minimumOrderQuantityUnit }}
                    </p>

                    <p v-if="vendor.phone">
                        <strong>Phone:</strong> {{ vendor.phone }}
                    </p>

                    <button type="button" @click="startEditing(vendor)">
                        Edit Vendor
                    </button>
                </article>
            </div>

        </section>
    </section>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { storeToRefs } from 'pinia'
import { useVendorStore } from '@/stores/vendorStore'
import { formatDeliveryDate, getNextDeliveryDate } from '@/utils/dateFormat'

const vendorStore = useVendorStore()

const {
    vendors,
    loading,
    saving,
    error
} = storeToRefs(vendorStore)

const editingVendor = ref(null)
const vendorSearch = ref('')
const vendorStatus = ref('active')

const filteredVendors = computed(() => {
    const query = vendorSearch.value.trim().toLowerCase()

    return vendors.value.filter(vendor => {
        const matchesName = vendor.name.toLowerCase().includes(query)

        const matchesStatus =
            vendorStatus.value === 'all' ||
            (vendorStatus.value === 'active' && vendor.active) ||
            (vendorStatus.value === 'inactive' && !vendor.active)

        return matchesName && matchesStatus
    })
})

const saveError = ref('')
const saveSuccess = ref('')


async function saveVendor() {
    if (!editingVendor.value || saving.value) return

    saveError.value = ''
    saveSuccess.value = ''

    const vendor = editingVendor.value

    const optionalNumber = (value) =>
        value === '' || value == null ? null : Number(value)

    const payload = {
        name: vendor.name.trim(),
        phone: vendor.phone || null,
        website: vendor.website || null,
        preferredDeliveryDays: vendor.preferredDeliveryDays || null,
        nextDeliveryDate: vendor.nextDeliveryDate || null,
        deliveryFrequency: vendor.deliveryFrequency || null,
        notes: vendor.notes || null,
        minimumOrderAmount: optionalNumber(vendor.minimumOrderAmount),
        minimumOrderQuantity: optionalNumber(vendor.minimumOrderQuantity),
        minimumOrderQuantityUnit:
            vendor.minimumOrderQuantityUnit?.trim() || null,
        belowMinimumFee: optionalNumber(vendor.belowMinimumFee),
        offScheduleDeliveryFee:
            optionalNumber(vendor.offScheduleDeliveryFee),
        active: vendor.active
    }

    try {
        const savedVendor = vendor.id == null
            ? await vendorStore.addVendor(payload)
            : await vendorStore.saveVendor(vendor.id, payload)

        editingVendor.value = null
        saveSuccess.value =
            `${savedVendor.name} saved successfully.`

    } catch (err) {
        saveError.value = err.message
    }
}

function startEditing(vendor) {
    editingVendor.value = { ...vendor }
}


function startCreating() {
    saveError.value = ''
    saveSuccess.value = ''

    editingVendor.value = {
        id: null,
        name: '',
        phone: '',
        website: '',
        preferredDeliveryDays: '',
        nextDeliveryDate: '',
        deliveryFrequency: '',
        notes: '',
        minimumOrderAmount: null,
        minimumOrderQuantity: null,
        minimumOrderQuantityUnit: '',
        belowMinimumFee: null,
        offScheduleDeliveryFee: null,
        active: true
    }
}

onMounted(async () => {
    try {
        await vendorStore.loadVendors()
    } catch (err) {
        // The vendor store already records the error.
    }
})
</script>


<style scoped>
.vendor-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill,
            minmax(min(100%, 300px), 1fr));
    gap: 1rem;
    margin-top: 1rem;
}

.vendor-card {
    display: grid;
    gap: 0.65rem;
}

.vendor-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 1rem;
}

.vendor-header h3,
.vendor-card p {
    margin: 0;
}

.active {
    color: #81c784;
}

.inactive {
    color: #ef9a9a;
}


.vendor-editor {
    display: grid;
    gap: 1rem;
    margin-top: 1.5rem;
}

.vendor-editor h3 {
    margin: 0;
}

.vendor-fields {
    display: grid;
    grid-template-columns: repeat(auto-fit,
            minmax(min(100%, 250px), 1fr));
    gap: 1rem;
}

.vendor-fields label {
    display: grid;
    gap: 0.4rem;
    font-weight: 700;
}

.vendor-fields input {
    width: 100%;
    box-sizing: border-box;
}

.vendor-editor button {
    justify-self: start;
}


.vendor-notes {
    display: grid;
    gap: 0.4rem;
}

.vendor-notes label {
    font-weight: 700;
}

.vendor-notes textarea {
    width: 100%;
    box-sizing: border-box;
    resize: vertical;
}

.vendor-active-control {
    display: flex;
    align-items: center;
    gap: 0.6rem;
    font-weight: 700;
}

.vendor-active-control input {
    width: auto;
}


.vendor-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 0.75rem;
}

.vendor-actions button {
    width: auto;
}

.save-success {
    color: #81c784;
    font-weight: 700;
}

.vendor-filters {
    display: grid;
    grid-template-columns: repeat(2,
            minmax(0, 350px));
    gap: 1rem;
    margin-top: 1rem;
}

.vendor-search,
.vendor-status {
    display: grid;
    gap: 0.4rem;
    margin-top: 0;
    font-weight: 700;
}

@media (max-width: 650px) {
    .vendor-filters {
        grid-template-columns: 1fr;
    }
}
</style>
