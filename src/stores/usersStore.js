import { defineStore } from 'pinia'
import { ref } from 'vue'

import {
  getAdminUsers,
  createAdminUser,
  updateAdminUser,
  resetAdminUserPassword
} from '@/api/usersApi'

export const useUsersStore = defineStore('users', () => {

  const users = ref([])
  const loading = ref(false)
  const error = ref(null)

  async function loadUsers() {
    loading.value = true
    error.value = null

    try {
      users.value = await getAdminUsers()
    } catch (err) {
      console.error('Unable to load admin users:', err)

      error.value =
        err?.message ||
        'Unable to load users.'

      throw err
    } finally {
      loading.value = false
    }
  }

  async function createUser(user) {
    error.value = null

    try {
      const createdUser =
        await createAdminUser(user)

      users.value = [
        ...users.value,
        createdUser
      ].sort((a, b) =>
        a.displayName.localeCompare(b.displayName)
      )

      return createdUser
    } catch (err) {
      console.error('Unable to create admin user:', err)

      error.value =
        err?.message ||
        'Unable to create user.'

      throw err
    }
  }

  async function updateUser(id, user) {
    error.value = null

    try {
      const updatedUser =
        await updateAdminUser(id, user)

      users.value = users.value
        .map(existingUser =>
          existingUser.id === id
            ? updatedUser
            : existingUser
        )
        .sort((a, b) =>
          a.displayName.localeCompare(b.displayName)
        )

      return updatedUser
    } catch (err) {
      console.error('Unable to update admin user:', err)

      error.value =
        err?.message ||
        'Unable to update user.'

      throw err
    }
  }

  async function resetPassword(id, password) {
    error.value = null

    try {
      await resetAdminUserPassword(
        id,
        password
      )
    } catch (err) {
      console.error(
        'Unable to reset admin user password:',
        err
      )

      error.value =
        err?.message ||
        'Unable to reset password.'

      throw err
    }
  }

  function clearError() {
    error.value = null
  }

  return {
    users,
    loading,
    error,
    loadUsers,
    createUser,
    updateUser,
    resetPassword,
    clearError
  }
})