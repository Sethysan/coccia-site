export const createWeeklyOfferingItemPrice = (data = {}) => ({
  id: data.id ?? null,

  label: data.label ?? "",

  amount: data.amount ?? null,

  displayOrder: data.displayOrder ?? 0
})