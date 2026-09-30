export const createWeeklyOfferingItem = (data = {}) => ({
  id: data.id ?? null,

  recipeId: data.recipeId ?? null,

  offeringType: data.offeringType ?? "DINNER",

  publicTitle: data.publicTitle ?? "",
  publicDescription: data.publicDescription ?? "",

  imageUrl: data.imageUrl ?? null,
  imageAlt: data.imageAlt ?? "",

  includesHouseSalad: data.includesHouseSalad ?? false,
  includesHomemadeBread: data.includesHomemadeBread ?? false,

  displayOrder: data.displayOrder ?? 0,

  prices: data.prices ?? []
})