<template>
    <main class="admin-page">
        <div class="admin-page-container">

            <header class="page-header">
                <div>
                    <p class="admin-eyebrow">
                        Coccia House
                    </p>

                    <h1>Users & Access</h1>

                    <p class="admin-subtext">
                        Manage who can access and update the website.
                    </p>
                </div>

                <RouterLink to="/admin">
                    Back to Dashboard
                </RouterLink>
            </header>

            <section class="admin-card create-user-card">
                <h2>Add User</h2>

                <form class="admin-form user-form" @submit.prevent="handleCreateUser">
                    <label>
                        Display Name

                        <input v-model.trim="newUser.displayName" type="text" maxlength="100" required>
                    </label>

                    <label>
                        Username

                        <input v-model.trim="newUser.username" type="text" maxlength="100" autocomplete="username"
                            required>
                    </label>

                    <label>
                        Role

                        <select v-model="newUser.role">
                            <option value="STAFF">
                                Staff
                            </option>

                            <option value="ADMIN">
                                Administrator
                            </option>
                        </select>
                    </label>

                    <label>
                        Temporary Password

                        <input v-model="newUser.password" type="password" minlength="8" maxlength="100"
                            autocomplete="new-password" required>
                    </label>

                    <p v-if="createError" class="admin-form-error">
                        {{ createError }}
                    </p>

                    <button type="submit" :disabled="creating">
                        {{ creating ? 'Creating...' : 'Add User' }}
                    </button>
                </form>
            </section>

            <section class="users-section">
                <div class="admin-section-heading">
                    <h2>Website Users</h2>

                    <span v-if="usersStore.users.length">
                        {{ usersStore.users.length }}
                        {{ usersStore.users.length === 1 ? 'user' : 'users' }}
                    </span>
                </div>

                <p v-if="usersStore.loading">
                    Loading users...
                </p>

                <div v-else-if="usersStore.error && !usersStore.users.length" class="error-message">
                    <strong>Unable to load website users.</strong>

                    <p>
                        {{ usersStore.error }}
                    </p>
                </div>

                <div v-else class="users-list">
                    <article v-for="user in usersStore.users" :key="user.id" class="admin-card user-card"
                        :class="{ 'is-inactive': !user.active }">
                        <div class="user-card-header">
                            <div>
                                <h3>{{ user.displayName }}</h3>

                                <p>@{{ user.username }}</p>
                            </div>

                            <span class="role-badge">
                                {{ roleLabel(user.role) }}
                            </span>
                        </div>

                        <div class="user-status">
                            {{ user.active ? 'Active' : 'Inactive' }}
                        </div>

                        <div class="admin-form-actions">
                            <button type="button" @click="startEditing(user)">
                                Edit Access
                            </button>

                            <button type="button" @click="startPasswordReset(user)">
                                Reset Password
                            </button>
                        </div>

                        <form v-if="editingUserId === user.id" class="admin-form admin-divider-panel"
                            @submit.prevent="handleUpdateUser(user.id)">
                            <label>
                                Display Name

                                <input v-model.trim="editUser.displayName" type="text" maxlength="100" required>
                            </label>

                            <label v-if="user.username !== auth.username">
                                Role

                                <select v-model="editUser.role">
                                    <option value="STAFF">
                                        Staff
                                    </option>

                                    <option value="ADMIN">
                                        Administrator
                                    </option>
                                </select>
                            </label>

                            <p v-else>
                                Role: Administrator
                            </p>
                            <label v-if="user.username !== auth.username" class="admin-checkbox-label">
                                <input v-model="editUser.active" type="checkbox">

                                Active
                            </label>

                            <p v-else>
                                Your administrator access cannot be removed from this page.
                            </p>

                            <p v-if="editError" class="admin-form-error">
                                {{ editError }}
                            </p>

                            <div class="admin-form-actions">
                                <button type="submit" :disabled="savingUserId === user.id">
                                    {{
                                        savingUserId === user.id
                                            ? 'Saving...'
                                            : 'Save Changes'
                                    }}
                                </button>

                                <button type="button" @click="cancelEditing">
                                    Cancel
                                </button>
                            </div>
                        </form>

                        <form v-if="passwordUserId === user.id" class="admin-form admin-divider-panel"
                            @submit.prevent="handleResetPassword(user.id)">
                            <label>
                                New Password

                                <input v-model="newPassword" type="password" minlength="8" maxlength="100"
                                    autocomplete="new-password" required>
                            </label>

                            <p v-if="passwordError" class="admin-form-error">
                                {{ passwordError }}
                            </p>

                            <p v-if="passwordSaved" class="admin-success-message">
                                Password updated.
                            </p>

                            <div class="admin-form-actions">
                                <button type="submit" :disabled="resettingPassword">
                                    {{
                                        resettingPassword
                                            ? 'Updating...'
                                            : 'Update Password'
                                    }}
                                </button>

                                <button type="button" @click="cancelPasswordReset">
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </article>
                </div>
            </section>

        </div>
    </main>
</template>

<script setup>
import { onMounted, reactive, ref } from 'vue'
import { RouterLink } from 'vue-router'
import { useUsersStore } from '@/stores/usersStore'
import { useAuthStore } from '@/stores/authStore'

const usersStore = useUsersStore()

const creating = ref(false)
const createError = ref(null)

const auth = useAuthStore()

const editingUserId = ref(null)
const savingUserId = ref(null)
const editError = ref(null)

const passwordUserId = ref(null)
const resettingPassword = ref(false)
const passwordError = ref(null)
const passwordSaved = ref(false)
const newPassword = ref('')

const newUser = reactive({
    displayName: '',
    username: '',
    role: 'STAFF',
    password: ''
})

const editUser = reactive({
    displayName: '',
    role: 'STAFF',
    active: true
})

onMounted(async () => {
    try {
        await usersStore.loadUsers()
    } catch {
        // Store already contains the load error.
    }
})

async function handleCreateUser() {
    creating.value = true
    createError.value = null

    try {
        await usersStore.createUser({
            username: newUser.username,
            displayName: newUser.displayName,
            role: newUser.role,
            password: newUser.password
        })

        newUser.displayName = ''
        newUser.username = ''
        newUser.role = 'STAFF'
        newUser.password = ''
    } catch (error) {
        createError.value =
            error?.message || 'Unable to create user.'
    } finally {
        creating.value = false
    }
}

function startEditing(user) {
    cancelPasswordReset()

    editingUserId.value = user.id
    editError.value = null

    editUser.displayName = user.displayName
    editUser.role = user.role
    editUser.active = user.active
}

function cancelEditing() {
    editingUserId.value = null
    editError.value = null
}

async function handleUpdateUser(id) {
    savingUserId.value = id
    editError.value = null

    try {
        await usersStore.updateUser(id, {
            displayName: editUser.displayName,
            role: editUser.role,
            active: editUser.active
        })

        cancelEditing()
    } catch (error) {
        editError.value =
            error?.message || 'Unable to update user.'
    } finally {
        savingUserId.value = null
    }
}

function startPasswordReset(user) {
    cancelEditing()

    passwordUserId.value = user.id
    newPassword.value = ''
    passwordError.value = null
    passwordSaved.value = false
}

function cancelPasswordReset() {
    passwordUserId.value = null
    newPassword.value = ''
    passwordError.value = null
    passwordSaved.value = false
}

async function handleResetPassword(id) {
    resettingPassword.value = true
    passwordError.value = null
    passwordSaved.value = false

    try {
        await usersStore.resetPassword(
            id,
            newPassword.value
        )

        newPassword.value = ''
        passwordSaved.value = true
    } catch (error) {
        passwordError.value =
            error?.message || 'Unable to reset password.'
    } finally {
        resettingPassword.value = false
    }
}

function roleLabel(role) {
    return role === 'ADMIN'
        ? 'Administrator'
        : 'Staff'
}
</script>

<style scoped>
.create-user-card {
    margin-bottom: 2rem;
}

.create-user-card h2 {
    margin-top: 0;
}

.user-form {
    grid-template-columns: repeat(2, minmax(0, 1fr));
}

.user-form button,
.user-form .admin-form-error {
    grid-column: 1 / -1;
}

.users-list {
    display: grid;
    gap: 1rem;
}

.user-card {
    transition: opacity 180ms ease;
}

.user-card.is-inactive {
    opacity: 0.65;
}

.user-card-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 1rem;
}

.user-card-header h3 {
    margin: 0;
}

.user-card-header p {
    margin: 0.25rem 0 0;
}

.role-badge {
    font-size: 0.75rem;
    font-weight: 700;
    letter-spacing: 0.06em;
    text-transform: uppercase;
}

.user-status {
    margin-top: 0.75rem;

    font-size: 0.875rem;
}

@media (max-width: 700px) {
    .user-form {
        grid-template-columns: 1fr;
    }
}
</style>