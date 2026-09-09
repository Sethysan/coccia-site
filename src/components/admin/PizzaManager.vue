<script setup>
import { onMounted, ref } from 'vue'
import { useMenuStore } from '@/stores/menuStore'

const props = defineProps({
  section: {
    type: Object,
    required: true,
  },
})

const menuStore = useMenuStore()

const editingSizeId = ref(null)
const showAddSize = ref(false)

const sizeForm = ref({
  name: '',
  basePrice: '',
  active: true,
})

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
</script>

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

    <div
      v-else
      class="pizza-panel"
    >
      <div class="pizza-panel__header">
        <div>
          <h4>Sizes & Base Prices</h4>
          <p>
            The base price is the price of a
            plain cheese pizza.
          </p>
        </div>

        <button
          v-if="!showAddSize && !editingSizeId"
          type="button"
          @click="startAddSize"
        >
          + Add Size
        </button>
      </div>

      <div
        v-if="menuStore.pizzaSizes.length"
        class="pizza-size-list"
      >
        <div
          v-for="size in menuStore.pizzaSizes"
          :key="size.id"
          class="pizza-size-row"
        >
          <template
            v-if="editingSizeId !== size.id"
          >
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

              <button
                type="button"
                @click="startEditSize(size)"
              >
                Edit
              </button>
            </div>
          </template>

          <form
            v-else
            class="pizza-size-form"
            @submit.prevent="saveSize"
          >
            <label>
              Size Name

              <input
                v-model="sizeForm.name"
                type="text"
                required
              />
            </label>

            <label>
              Base Price

              <input
                v-model="sizeForm.basePrice"
                type="number"
                min="0.01"
                step="0.01"
                required
              />
            </label>

            <label class="checkbox-label">
              <input
                v-model="sizeForm.active"
                type="checkbox"
              />

              Active
            </label>

            <div class="pizza-size-form__actions">
              <button type="submit">
                Save
              </button>

              <button
                type="button"
                @click="cancelSizeForm"
              >
                Cancel
              </button>
            </div>
          </form>
        </div>
      </div>

      <p v-else-if="!showAddSize">
        No pizza sizes have been added yet.
      </p>

      <form
        v-if="showAddSize"
        class="pizza-size-form pizza-size-form--new"
        @submit.prevent="saveSize"
      >
        <label>
          Size Name

          <input
            v-model="sizeForm.name"
            type="text"
            placeholder="Small"
            required
          />
        </label>

        <label>
          Base Price

          <input
            v-model="sizeForm.basePrice"
            type="number"
            min="0.01"
            step="0.01"
            placeholder="13.00"
            required
          />
        </label>

        <label class="checkbox-label">
          <input
            v-model="sizeForm.active"
            type="checkbox"
          />

          Active
        </label>

        <div class="pizza-size-form__actions">
          <button type="submit">
            Add Size
          </button>

          <button
            type="button"
            @click="cancelSizeForm"
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  </section>
</template>

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
    minmax(140px, 1fr)
    minmax(120px, 160px)
    auto
    auto;

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

@media (max-width: 700px) {
  .pizza-panel__header,
  .pizza-size-row {
    align-items: stretch;
  }

  .pizza-size-form {
    grid-template-columns: 1fr;
  }
}
</style>