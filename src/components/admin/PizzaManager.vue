<template>
  <section class="pizza-manager">
    <div class="pizza-manager__header">
      <div>
        <h3>Pizza Setup</h3>
        <p>
          Manage pizza sizes, pricing, toppings,
          and specialty pizzas.
        </p>
      </div>
    </div>

    <p v-if="menuStore.pizzaLoading">
      Loading pizza setup...
    </p>

    <!-- ==================== SIZES & BASE PRICES ==================== -->

    <div v-else class="pizza-panel">
      <div class="pizza-panel__header">
        <div>
          <h4>Sizes & Base Prices</h4>

          <p>
            The base price is the price of a
            plain cheese pizza.
          </p>
        </div>

        <button type="button" @click="showSizeEditor = !showSizeEditor">
          {{ showSizeEditor ? 'Close' : 'Edit Sizes' }}
        </button>
      </div>

      <div v-if="showSizeEditor">

        <div class="pizza-editor-actions">
          <button v-if="!showAddSize && !editingSizeId" type="button" @click="startAddSize">
            + Add Size
          </button>
        </div>

        <div v-if="menuStore.pizzaSizes.length" class="pizza-size-list">
          <div v-for="size in menuStore.pizzaSizes" :key="size.id" class="pizza-size-row">
            <template v-if="editingSizeId !== size.id">
              <div>
                <strong>{{ size.name }}</strong>

                <span v-if="!size.active">
                  — Inactive
                </span>
              </div>

              <div class="pizza-size-row__actions">
                <strong>
                  ${{ Number(size.basePrice).toFixed(2) }}
                </strong>

                <button type="button" @click="startEditSize(size)">
                  Edit
                </button>
              </div>
            </template>

            <form v-else class="pizza-size-form" @submit.prevent="saveSize">
              <label>
                Size Name

                <input v-model="sizeForm.name" type="text" required />
              </label>

              <label>
                Base Price

                <input v-model="sizeForm.basePrice" type="number" min="0.01" step="0.01" required />
              </label>

              <label class="checkbox-label">
                <input v-model="sizeForm.active" type="checkbox" />

                Active
              </label>

              <div class="pizza-size-form__actions">
                <button type="submit">
                  Save
                </button>

                <button type="button" @click="cancelSizeForm">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
        <p v-else-if="!showAddSize">
          No pizza sizes have been added yet.
        </p>
      </div>


      <form v-if="showAddSize" class="pizza-size-form pizza-size-form--new" @submit.prevent="saveSize">
        <label>
          Size Name

          <input v-model="sizeForm.name" type="text" placeholder="Small" required />
        </label>

        <label>
          Base Price

          <input v-model="sizeForm.basePrice" type="number" min="0.01" step="0.01" placeholder="13.00" required />
        </label>

        <label class="checkbox-label">
          <input v-model="sizeForm.active" type="checkbox" />

          Active
        </label>

        <div class="pizza-size-form__actions">
          <button type="submit">
            Add Size
          </button>

          <button type="button" @click="cancelSizeForm">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <!-- ==================== PRICING & ADD-ONS ==================== -->

    <div v-if="!menuStore.pizzaLoading" class="pizza-panel pizza-panel--spaced">
      <div class="pizza-panel__header">
        <div>
          <h4>Pricing & Add-ons</h4>

          <p>
            Set the per-item topping charge and
            any additional pizza extras.
          </p>
        </div>
        <button type="button" @click="showAddOnEditor = !showAddOnEditor">
          {{ showAddOnEditor ? 'Close' : 'Edit Add-ons' }}
        </button>
      </div>
      <div v-if="showAddOnEditor">

        <button v-if="!showAddOnForm" type="button" @click="startAddOn">
          + Add Pricing Rule
        </button>

        <div v-if="menuStore.pizzaAddOns.length" class="pizza-size-list">
          <div v-for="addOn in menuStore.pizzaAddOns" :key="addOn.id" class="pizza-size-row">
            <template v-if="editingAddOnId !== addOn.id">
              <div>
                <strong>{{ addOn.name }}</strong>

                <span v-if="addOn.type === 'TOPPING'">
                  — Per Item
                </span>

                <span v-if="!addOn.active">
                  — Inactive
                </span>
              </div>

              <div class="pizza-size-row__actions">
                <strong>
                  +${{ Number(addOn.amount).toFixed(2) }}
                </strong>

                <button type="button" @click="startEditAddOn(addOn)">
                  Edit
                </button>
              </div>
            </template>

            <form v-else class="pizza-size-form" @submit.prevent="saveAddOn">
              <label>
                Name

                <input v-model="addOnForm.name" type="text" required />
              </label>

              <label>
                Amount

                <input v-model="addOnForm.amount" type="number" min="0.01" step="0.01" required />
              </label>

              <label>
                Type

                <select v-model="addOnForm.type">
                  <option value="TOPPING">
                    Per Item
                  </option>

                  <option value="EXTRA">
                    Extra
                  </option>
                </select>
              </label>

              <label class="checkbox-label">
                <input v-model="addOnForm.active" type="checkbox" />

                Active
              </label>

              <div class="pizza-size-form__actions">
                <button type="submit">
                  Save
                </button>

                <button type="button" @click="cancelAddOnForm">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
        <p v-else-if="!showAddOnForm">
          No pizza pricing rules have been added yet.
        </p>
      </div>

      <form v-if="showAddOnForm && !editingAddOnId" class="pizza-size-form pizza-size-form--new"
        @submit.prevent="saveAddOn">
        <label>
          Name

          <input v-model="addOnForm.name" type="text" placeholder="Additional Item" required />
        </label>

        <label>
          Amount

          <input v-model="addOnForm.amount" type="number" min="0.01" step="0.01" placeholder="2.00" required />
        </label>

        <label>
          Type

          <select v-model="addOnForm.type">
            <option value="TOPPING">
              Per Item
            </option>

            <option value="EXTRA">
              Extra
            </option>
          </select>
        </label>

        <label class="checkbox-label">
          <input v-model="addOnForm.active" type="checkbox" />

          Active
        </label>

        <div class="pizza-size-form__actions">
          <button type="submit">
            Add
          </button>

          <button type="button" @click="cancelAddOnForm">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <!-- ==================== TOPPINGS ==================== -->

    <div v-if="!menuStore.pizzaLoading" class="pizza-panel pizza-panel--spaced">
      <div class="pizza-panel__header">
        <div>
          <h4>Toppings</h4>

          <p>
            These toppings use the active per-item
            pizza pricing rule.
          </p>
        </div>

        <button type="button" @click="showToppingEditor = !showToppingEditor">
          {{ showToppingEditor ? 'Close' : 'Edit Toppings' }}
        </button>
      </div>

      <div v-if="showToppingEditor">

        <button v-if="!showAddSize && !editingSizeId" type="button" @click="startAddSize">
          + Add Topping
        </button>

        <div v-if="menuStore.pizzaToppings.length" class="pizza-size-list">
          <div v-for="topping in menuStore.pizzaToppings" :key="topping.id" class="pizza-size-row">
            <template v-if="editingToppingId !== topping.id">
              <div>
                <strong>{{ topping.name }}</strong>

                <span v-if="!topping.active">
                  — Inactive
                </span>
              </div>

              <button type="button" @click="startEditTopping(topping)">
                Edit
              </button>
            </template>

            <form v-else class="pizza-size-form" @submit.prevent="saveTopping">
              <label>
                Topping Name

                <input v-model="toppingForm.name" type="text" required />
              </label>

              <label class="checkbox-label">
                <input v-model="toppingForm.active" type="checkbox" />

                Active
              </label>

              <div class="pizza-size-form__actions">
                <button type="submit">
                  Save
                </button>

                <button type="button" @click="cancelToppingForm">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
        <p v-else-if="!showToppingForm">
          No pizza toppings have been added yet.
        </p>
      </div>


      <form v-if="showToppingForm && !editingToppingId" class="pizza-size-form pizza-size-form--new"
        @submit.prevent="saveTopping">
        <label>
          Topping Name

          <input v-model="toppingForm.name" type="text" placeholder="Pepperoni" required />
        </label>

        <label class="checkbox-label">
          <input v-model="toppingForm.active" type="checkbox" />

          Active
        </label>

        <div class="pizza-size-form__actions">
          <button type="submit">
            Add
          </button>

          <button type="button" @click="cancelToppingForm">
            Cancel
          </button>
        </div>
      </form>
    </div>

    <!-- =========================================================
     SPECIALTY PIZZAS
     ========================================================= -->

    <section class="pizza-panel pizza-panel--spaced">
      <div class="pizza-panel-header">
        <div>
          <h4>Specialty Pizzas</h4>
          <p>
            Build pizzas like Works or Veggie using the sizes,
            toppings, and pricing rules above.
          </p>
        </div>
      </div>

      <div v-if="menuStore.pizzaSpecialties.length" class="pizza-specialty-list">
        <div v-for="specialty in menuStore.pizzaSpecialties" :key="specialty.id" class="pizza-specialty-row">
          <div>
            <strong>
              {{ specialty.recipeName }}
            </strong>

            <p class="section-help">
              {{
                specialty.pricingMode === 'CALCULATED'
                  ? 'Calculated from toppings'
                  : 'Custom pricing'
              }}
            </p>

            <p class="section-help">
              {{ specialty.active ? 'Active' : 'Inactive' }}
            </p>
          </div>

          <button type="button" @click="startEditSpecialty(specialty)">
            Edit
          </button>
        </div>
      </div>

      <p v-else class="section-help">
        No specialty pizzas have been added yet.
      </p>

      <button v-if="!showSpecialtyForm" type="button" class="primary-button" @click="startAddSpecialty">
        + Add Specialty Pizza
      </button>

      <div v-else class="pizza-specialty-form-wrapper">
        <PizzaSpecialtyForm :specialty="editingSpecialty" :sizes="menuStore.pizzaSizes" :add-ons="menuStore.pizzaAddOns"
          :toppings="menuStore.pizzaToppings" @submit="saveSpecialty" />

        <button type="button" @click="cancelSpecialtyForm">
          Cancel
        </button>
      </div>

    </section>

  </section>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { useMenuStore } from '@/stores/menuStore'
import PizzaSpecialtyForm from './PizzaSpecialtyForm.vue'

const props = defineProps({
  section: {
    type: Object,
    required: true,
  },
})

const menuStore = useMenuStore()

const showSizeEditor = ref(false)
const showAddOnEditor = ref(false)
const showToppingEditor = ref(false)

const editingSizeId = ref(null)
const showAddSize = ref(false)

const sizeForm = ref({
  name: '',
  basePrice: '',
  active: true,
})

const editingAddOnId = ref(null)
const showAddOnForm = ref(false)

const addOnForm = ref({
  name: '',
  amount: '',
  type: 'EXTRA',
  active: true,
})

const editingToppingId = ref(null)
const showToppingForm = ref(false)

const toppingForm = ref({
  name: '',
  active: true,
})

const showSpecialtyForm = ref(false)
const editingSpecialty = ref(null)

onMounted(async () => {
  await menuStore.loadPizzaConfiguration(
    props.section.id
  )
})

function startAddSize() {
  editingSizeId.value = null

  sizeForm.value = {
    name: '',
    basePrice: '',
    active: true,
  }

  showAddSize.value = true
}

function startEditSize(size) {
  showAddSize.value = false
  editingSizeId.value = size.id

  sizeForm.value = {
    name: size.name,
    basePrice: size.basePrice,
    active: size.active,
  }
}

function cancelSizeForm() {
  editingSizeId.value = null
  showAddSize.value = false
}

async function saveSize() {
  const data = {
    name: sizeForm.value.name,
    basePrice: Number(sizeForm.value.basePrice),
    active: sizeForm.value.active,
  }

  if (editingSizeId.value) {
    await menuStore.editPizzaSize(
      props.section.id,
      editingSizeId.value,
      data
    )
  } else {
    await menuStore.addPizzaSize(
      props.section.id,
      data
    )
  }

  cancelSizeForm()
}

function startAddOn() {
  editingAddOnId.value = null

  addOnForm.value = {
    name: '',
    amount: '',
    type: 'EXTRA',
    active: true,
  }

  showAddOnForm.value = true
}

function startEditAddOn(addOn) {
  editingAddOnId.value = addOn.id
  showAddOnForm.value = true

  addOnForm.value = {
    name: addOn.name,
    amount: addOn.amount,
    type: addOn.type,
    active: addOn.active,
  }
}

function cancelAddOnForm() {
  editingAddOnId.value = null
  showAddOnForm.value = false
}

async function saveAddOn() {
  const data = {
    name: addOnForm.value.name,
    amount: Number(addOnForm.value.amount),
    type: addOnForm.value.type,
    active: addOnForm.value.active,
  }

  if (editingAddOnId.value) {
    await menuStore.editPizzaAddOn(
      props.section.id,
      editingAddOnId.value,
      data
    )
  } else {
    await menuStore.addPizzaAddOn(
      props.section.id,
      data
    )
  }

  cancelAddOnForm()
}

function startAddTopping() {
  editingToppingId.value = null

  toppingForm.value = {
    name: '',
    active: true,
  }

  showToppingForm.value = true
}

function startEditTopping(topping) {
  editingToppingId.value = topping.id
  showToppingForm.value = true

  toppingForm.value = {
    name: topping.name,
    active: topping.active,
  }
}

function cancelToppingForm() {
  editingToppingId.value = null
  showToppingForm.value = false
}

async function saveTopping() {
  const data = {
    name: toppingForm.value.name,
    active: toppingForm.value.active,
  }

  if (editingToppingId.value) {
    await menuStore.editPizzaTopping(
      props.section.id,
      editingToppingId.value,
      data
    )
  } else {
    await menuStore.addPizzaTopping(
      props.section.id,
      data
    )
  }

  cancelToppingForm()
}

function startAddSpecialty() {
  editingSpecialty.value = null
  showSpecialtyForm.value = true
}

function startEditSpecialty(specialty) {
  editingSpecialty.value = specialty
  showSpecialtyForm.value = true
}

function cancelSpecialtyForm() {
  editingSpecialty.value = null
  showSpecialtyForm.value = false
}

async function saveSpecialty(data) {
  try {
    const wasEditing =
      Boolean(editingSpecialty.value)

    let saved

    if (wasEditing) {
      saved =
        await menuStore.editPizzaSpecialty(
          props.section.id,
          editingSpecialty.value.id,
          data
        )
    } else {
      saved =
        await menuStore.addPizzaSpecialty(
          props.section.id,
          data
        )
    }

    if (saved) {
      cancelSpecialtyForm()

      window.alert(
        wasEditing
          ? 'Specialty pizza updated successfully.'
          : 'Specialty pizza saved successfully.'
      )
    }
  } catch (error) {
    console.error(error)

    window.alert(
      error.message ||
      'Unable to save specialty pizza.'
    )
  }
}

</script>

<style scoped>
.pizza-manager {
  margin-top: 1rem;
}

.pizza-manager__header {
  margin-bottom: 1rem;
}

.pizza-manager__header h3,
.pizza-panel__header h4 {
  margin: 0;
}

.pizza-manager__header p,
.pizza-panel__header p {
  margin: 0.35rem 0 0;
}

.pizza-panel {
  border: 1px solid #d8d8d8;
  border-radius: 8px;
  padding: 1rem;
}

.pizza-panel__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 1rem;
  margin-bottom: 1rem;
}

.pizza-size-list {
  display: grid;
  gap: 0.5rem;
}

.pizza-size-row {
  border-top: 1px solid #e5e5e5;
  padding: 0.75rem 0;

  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.pizza-size-row__actions {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.pizza-size-form {
  width: 100%;

  display: grid;
  grid-template-columns:
    minmax(140px, 1fr) minmax(120px, 160px) auto auto;

  align-items: end;
  gap: 0.75rem;
}

.pizza-size-form--new {
  border-top: 1px solid #e5e5e5;
  padding-top: 1rem;
}

.pizza-size-form label {
  display: grid;
  gap: 0.25rem;
}

.pizza-size-form input[type='text'],
.pizza-size-form input[type='number'] {
  width: 100%;
  box-sizing: border-box;
}

.checkbox-label {
  display: flex !important;
  align-items: center;
  gap: 0.4rem !important;
}

.pizza-size-form__actions {
  display: flex;
  gap: 0.5rem;
}

.pizza-panel--spaced {
  margin-top: 1rem;
}

@media (max-width: 700px) {

  .pizza-panel__header,
  .pizza-size-row {
    align-items: stretch;
  }

  .pizza-size-form {
    grid-template-columns: 1fr;
  }
}

.pizza-specialty-list {
  display: grid;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.pizza-specialty-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 1rem;

  padding: 0.9rem;

  border: 1px solid rgba(255, 255, 255, 0.12);
  border-radius: 0.5rem;
}

.pizza-specialty-form-wrapper {
  display: grid;
  gap: 1rem;
  margin-top: 1rem;
}
</style>