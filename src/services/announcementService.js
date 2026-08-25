import { getCurrentAnnouncements } from "@/api/announcementsApi"
import { createAnnouncement } from "@/models/announcement"

export const loadCurrentAnnouncements = async () => {
  const announcements = await getCurrentAnnouncements()

  return announcements.map(createAnnouncement)
}