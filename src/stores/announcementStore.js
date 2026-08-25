import { defineStore } from "pinia"

import {
    getCurrentAnnouncements,
    getAnnouncements,
    getAnnouncementById,
    createAnnouncement as createAnnouncementApi,
    updateAnnouncement as updateAnnouncementApi,
    scheduleAnnouncement as scheduleAnnouncementApi,
    archiveAnnouncement as archiveAnnouncementApi,
    deleteAnnouncement as deleteAnnouncementApi
} from "@/api/announcementsApi"

import {
    createAnnouncement as normalizeAnnouncement
} from "@/models/announcement"

import { announcementsFallback } from "@/data/fallback/announcementsFallback"

export const useAnnouncementStore = defineStore("announcements", {
    state: () => ({
        announcements: [],
        adminAnnouncements: [],
        currentAnnouncement: null,
        loading: false,
        error: null,
        usingFallback: false
    }),

    getters: {
        bannerAnnouncements: (state) =>
            state.announcements.filter(
                announcement =>
                    announcement.placement === "banner"
            ),

        newsAnnouncements: (state) =>
            state.announcements.filter(
                announcement =>
                    announcement.placement === "news"
            )
    },

    actions: {
        async loadAnnouncements() {
            this.loading = true
            this.error = null
            this.usingFallback = false

            try {
                const announcements =
                    await getCurrentAnnouncements()

                this.announcements =
                    announcements.map(normalizeAnnouncement)

            } catch (error) {
                console.error(error)

                this.error = error.message
                this.usingFallback = true
                this.announcements =
                    announcementsFallback

            } finally {
                this.loading = false
            }
        },

        async loadAdminAnnouncements() {
            this.loading = true
            this.error = null

            try {
                const announcements =
                    await getAnnouncements()

                this.adminAnnouncements =
                    announcements.map(normalizeAnnouncement)

            } catch (error) {
                console.error(error)

                this.adminAnnouncements = []
                this.error = error.message

            } finally {
                this.loading = false
            }
        },

        async loadAnnouncementById(id) {
            this.loading = true
            this.error = null
            this.currentAnnouncement = null

            try {
                const announcement =
                    await getAnnouncementById(id)

                this.currentAnnouncement =
                    normalizeAnnouncement(announcement)

            } catch (error) {
                console.error(error)

                this.error = error.message
                this.currentAnnouncement = null

            } finally {
                this.loading = false
            }
        },

        async createAnnouncement(data) {
            this.error = null

            try {
                const created =
                    await createAnnouncementApi(data)

                const announcement =
                    normalizeAnnouncement(created)

                this.adminAnnouncements = [
                    announcement,
                    ...this.adminAnnouncements
                ]

                return announcement

            } catch (error) {
                console.error(error)

                this.error = error.message
                return null
            }
        },

        async updateAnnouncement(id, data) {
            this.error = null

            try {
                const updated =
                    await updateAnnouncementApi(
                        id,
                        data
                    )

                const announcement =
                    normalizeAnnouncement(updated)

                this.currentAnnouncement =
                    announcement

                this.replaceAdminAnnouncement(
                    announcement
                )

                return true

            } catch (error) {
                console.error(error)

                this.error = error.message
                return false
            }
        },

        async scheduleAnnouncement(id) {
            this.error = null

            try {
                const scheduled =
                    await scheduleAnnouncementApi(id)

                const announcement =
                    normalizeAnnouncement(scheduled)

                this.currentAnnouncement =
                    announcement

                this.replaceAdminAnnouncement(
                    announcement
                )

                await this.loadAnnouncements()

                return true

            } catch (error) {
                console.error(error)

                this.error = error.message
                return false
            }
        },

        async archiveAnnouncement(id) {
            this.error = null

            try {
                const archived =
                    await archiveAnnouncementApi(id)

                const announcement =
                    normalizeAnnouncement(archived)

                this.currentAnnouncement =
                    announcement

                this.replaceAdminAnnouncement(
                    announcement
                )

                await this.loadAnnouncements()

                return true

            } catch (error) {
                console.error(error)

                this.error = error.message
                return false
            }
        },

        async deleteAnnouncement(id) {
            this.error = null

            try {
                await deleteAnnouncementApi(id)

                this.adminAnnouncements =
                    this.adminAnnouncements.filter(
                        announcement =>
                            announcement.id !== Number(id)
                    )

                if (
                    this.currentAnnouncement?.id ===
                    Number(id)
                ) {
                    this.currentAnnouncement = null
                }

                return true

            } catch (error) {
                console.error(error)

                this.error = error.message
                return false
            }
        },

        replaceAdminAnnouncement(announcement) {
            this.adminAnnouncements =
                this.adminAnnouncements.map(
                    existing =>
                        existing.id === announcement.id
                            ? announcement
                            : existing
                )
        },

        clearError() {
            this.error = null
        }
    }
})