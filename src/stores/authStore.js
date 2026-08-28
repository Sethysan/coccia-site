import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {

    const username = ref(null)
    const displayName = ref(null)
    const role = ref(null)
    const sessionChecked = ref(false)
    const authenticated = ref(false)

    // function getCookie(name) {
    //     const match = document.cookie
    //         .split('; ')
    //         .find(cookie => cookie.startsWith(`${name}=`))

    //     return match
    //         ? decodeURIComponent(match.split('=').slice(1).join('='))
    //         : null
    // }

    async function checkSession() {
        try {
            const response = await fetch('/api/auth/session', {
                method: 'GET',
                credentials: 'include'
            })

            if (!response.ok) {

                authenticated.value = false
                username.value = null
                displayName.value = null
                role.value = null
                return false
            }

            const session = await response.json()

            authenticated.value = session.authenticated
            username.value = session.username
            displayName.value = session.displayName
            role.value = session.role

            return session.authenticated
        } catch (error) {
            console.error('Unable to check authentication session:', error)

            authenticated.value = false
            username.value = null
            displayName.value = null
            role.value = null

            return false
        } finally {
            sessionChecked.value = true
        }
    }

    async function login(usernameInput, passwordInput) {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            credentials: 'include',
            body: JSON.stringify({
                username: usernameInput,
                password: passwordInput
            })
        })

        if (!response.ok) {
            if (!response.ok) {
                authenticated.value = false
                username.value = null
                displayName.value = null
                role.value = null

                return false
            }
            return false
        }

        const session = await response.json()

        authenticated.value = session.authenticated
        username.value = session.username
        displayName.value = session.displayName
        role.value = session.role
        sessionChecked.value = true

        await fetch('/api/auth/csrf', {
            method: 'GET',
            credentials: 'include'
        })

        return session.authenticated
    }

    async function logout() {
        try {
            const response = await fetch('/api/auth/logout', {
                method: 'POST',
                credentials: 'include'
            })

            if (!response.ok) {
                throw new Error('Logout failed.')
            }
        } finally {
            authenticated.value = false
            username.value = null
            displayName.value = null
            role.value = null
            sessionChecked.value = true
        }
    }

    const isAdmin = computed(() => role.value === 'ADMIN')
    const isStaff = computed(() => role.value === 'STAFF')

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