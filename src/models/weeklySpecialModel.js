export const createWeeklySpecial = (data = {}) => ({
  id: data.id ?? null,
  title: data.title ?? "",
  description: data.description ?? "",
  price: data.price ?? null,
  imageUrl: data.imageUrl ?? null,
  imageAlt: data.imageAlt ?? "",
  startDate: data.startDate ?? null,
  endDate: data.endDate ?? null,
  active: data.active ?? false,
  createdAt: data.createdAt ?? null,
  updatedAt: data.updatedAt ?? null
})

export const isValidWeeklySpecial = (special) => {
  return Boolean(
    special &&
      special.title &&
      special.description &&
      special.startDate &&
      special.endDate
  )
}