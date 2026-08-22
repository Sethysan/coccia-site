<template>
  <main class="admin-login">
    <section class="login-card">
      <header class="login-header">
        <p class="section-eyebrow">Coccia House</p>
        <h1>Admin Login</h1>
        <p class="login-intro">
          Sign in to manage website content.
        </p>
      </header>

      <form class="login-form" @submit.prevent="login">
        <div class="form-field">
          <label for="username">Username</label>

          <input id="username" v-model="username" type="text" autocomplete="username" required />
        </div>

        <div class="form-field">
          <label for="password">Password</label>

          <input id="password" v-model="password" type="password" autocomplete="current-password" required />
        </div>

        <p v-if="errorMessage" class="error-message" role="alert">
          {{ errorMessage }}
        </p>

        <button class="login-button" type="submit" :disabled="submitting">
          {{ submitting ? 'Signing in...' : 'Sign In' }}
        </button>
      </form>
    </section>
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
        : '/admin'

    await router.push(redirect)

  } catch (error) {
    console.error(error)
    errorMessage.value = 'Unable to connect to the server.'
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.admin-login {
  width: min(92%, 560px);
  margin: 3rem auto;
}

.login-card {
  padding: 2rem;

  background: var(--background-dark-trans);

  border: 1px solid var(--bronze-color);
  border-radius: 0.6rem;

  box-shadow: var(--shadow-medium);
}

.login-header {
  margin-bottom: 2rem;
  text-align: center;
}

.section-eyebrow {
  margin: 0 0 0.4rem;

  color: var(--bronze-bold);

  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.login-header h1 {
  margin: 0;

  color: var(--text-primary);

  font-size: clamp(2rem, 5vw, 2.6rem);
}

.login-intro {
  margin: 0.75rem 0 0;

  color: var(--text-secondary);

  line-height: 1.5;
}

.login-form {
  display: grid;
  gap: 1.25rem;
}

.form-field {
  display: grid;
  gap: 0.45rem;
}

.form-field label {
  color: var(--text-primary);

  font-size: 0.9rem;
  font-weight: 700;
}

.form-field input {
  width: 100%;
  padding: 0.8rem 0.9rem;

  color: var(--text-primary);
  background: rgba(0, 0, 0, 0.28);

  border: 1px solid var(--bronze-color);
  border-radius: 0.35rem;

  font: inherit;

  transition:
    border-color 0.2s ease,
    box-shadow 0.2s ease,
    background-color 0.2s ease;
}

.form-field input:focus {
  outline: none;

  background: rgba(0, 0, 0, 0.4);

  border-color: var(--bronze-hover);

  box-shadow: 0 0 0 2px rgba(168, 132, 68, 0.2);
}

.error-message {
  margin: 0;
  padding: 0.75rem 0.9rem;

  color: var(--text-primary);
  background: rgba(122, 31, 31, 0.45);

  border: 1px solid var(--status-closed);
  border-radius: 0.35rem;

  font-size: 0.9rem;
}

.login-button {
  width: 100%;
  padding: 0.85rem 1rem;
  margin-top: 0.25rem;

  color: var(--text-primary);
  background: var(--bronze-bold);

  border: 1px solid var(--bronze-color);
  border-radius: 0.35rem;

  font: inherit;
  font-weight: 700;

  cursor: pointer;

  transition:
    background-color 0.2s ease,
    transform 0.1s ease;
}

.login-button:hover:not(:disabled) {
  background: var(--bronze-hover);
}

.login-button:active:not(:disabled) {
  transform: translateY(1px);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: wait;
}

@media (max-width: 700px) {
  .admin-login {
    width: min(92%, 480px);
    margin: 2rem auto;
  }

  .login-card {
    padding: 1.5rem;
  }
}
</style>