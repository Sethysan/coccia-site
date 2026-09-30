export const createWeeklyOffering = (data = {}) => ({
  id: data.id ?? null,

  startDate: data.startDate ?? null,
  endDate: data.endDate ?? null,

  status: data.status ?? "DRAFT",

  items: data.items ?? [],

  createdAt: data.createdAt ?? null,
  updatedAt: data.updatedAt ?? null
})

export const isValidWeeklyOffering = (offering) => {
  return Boolean(
    offering &&
    offering.startDate &&
    offering.endDate &&
    offering.items &&
    offering.items.length > 0
  )
}