<template>
  <main class="admin-login">
    <h1>Admin Login</h1>

    <form @submit.prevent="login">
      <div>
        <label for="username">Username</label>

        <input id="username" v-model="username" type="text" autocomplete="username" required />
      </div>

      <div>
        <label for="password">Password</label>

        <input id="password" v-model="password" type="password" autocomplete="current-password" required />
      </div>

      <p v-if="errorMessage">
        {{ errorMessage }}
      </p>

      <button type="submit" :disabled="submitting">
        {{ submitting ? 'Signing in...' : 'Sign In' }}
      </button>
    </form>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/authStore'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const username = ref('')
const password = ref('')
const errorMessage = ref('')
const submitting = ref(false)

async function login() {
  errorMessage.value = ''
  submitting.value = true

  try {
    const authenticated = await auth.login(
      username.value,
      password.value
    )

    if (!authenticated) {
      errorMessage.value = 'Invalid username or password.'
      return
    }

    const redirect =
      typeof route.query.redirect === 'string'
        ? route.query.redirect
        : '/admin/weekly-offerings'

    await router.push(redirect)

  } catch (error) {
    console.error(error)
    errorMessage.value = 'Unable to connect to the server.'
  } finally {
    submitting.value = false
  }
}
</script>