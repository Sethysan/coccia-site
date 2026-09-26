import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

import { appConfig } from '@/config/appConfig'

export const useAuthStore = defineStore('auth', () => {

    const username = ref(null)
    const displayName = ref(null)
    const role = ref(null)
    const sessionChecked = ref(false)
    const authenticated = ref(false)

    function authUrl(endpoint) {
        return `${appConfig.apiUrl}${endpoint}`
    }

    function clearSession() {
        authenticated.value = false
        username.value = null
        displayName.value = null
        role.value = null
    }

    async function checkSession() {
        try {
            const response = await fetch(
                authUrl('/api/auth/session'),
                {
                    method: 'GET',
                    credentials: 'include'
                }
            )

            if (!response.ok) {
                clearSession()
                return false
            }

            const session = await response.json()

            authenticated.value = session.authenticated
            username.value = session.username
            displayName.value = session.displayName
            role.value = session.role

            return session.authenticated

        } catch (error) {
            console.error(
                'Unable to check authentication session:',
                error
            )

            clearSession()
            return false

        } finally {
            sessionChecked.value = true
        }
    }

    async function login(usernameInput, passwordInput) {
        const response = await fetch(
            authUrl('/api/auth/login'),
            {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include',
                body: JSON.stringify({
                    username: usernameInput,
                    password: passwordInput
                })
            }
        )

        if (!response.ok) {
            clearSession()
            return false
        }

        const session = await response.json()

        authenticated.value = session.authenticated
        username.value = session.username
        displayName.value = session.displayName
        role.value = session.role
        sessionChecked.value = true

        await fetch(
            authUrl('/api/auth/csrf'),
            {
                method: 'GET',
                credentials: 'include'
            }
        )

        return session.authenticated
    }

    async function logout() {
        try {
            const response = await fetch(
                authUrl('/api/auth/logout'),
                {
                    method: 'POST',
                    credentials: 'include'
                }
            )

            if (!response.ok) {
                throw new Error('Logout failed.')
            }

        } finally {
            clearSession()
            sessionChecked.value = true
        }
    }

    const isAdmin = computed(
        () => role.value === 'ADMIN'
    )

    const isStaff = computed(
        () => role.value === 'STAFF'
    )

    return {
        authenticated,
        username,
        displayName,
        role,
        sessionChecked,
        checkSession,
        login,
        logout,
        isAdmin,
        isStaff
    }
})