export const createAnnouncement = (data = {}) => ({
  id: data.id ?? null,
  title: data.title ?? "",
  message: data.message ?? "",
  placement: data.placement?.toLowerCase() ?? "",
  type: data.type?.toLowerCase() ?? "general",
  status: data.status?.toLowerCase() ?? "draft",
  startDateTime: data.startDateTime ?? null,
  endDateTime: data.endDateTime ?? null,
  displayOrder: data.displayOrder ?? 0,
  imageUrl: data.imageUrl ?? null,
  imageAlt: data.imageAlt ?? null,
  createdAt: data.createdAt ?? null,
  updatedAt: data.updatedAt ?? null
})