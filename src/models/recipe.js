export const createRecipe = (data = {}) => ({
    id: data.id ?? null,
    name: data.name ?? "",
    active: data.active ?? true,
    staffMembers: Array.isArray(data.staffMembers)
        ? data.staffMembers.map(createRecipeStaffMember)
        : [],
    createdAt: data.createdAt ?? null,
    updatedAt: data.updatedAt ?? null
})

const createRecipeStaffMember = (data = {}) => ({
    id: data.id ?? null,
    displayName: data.displayName ?? "",
    active: data.active ?? true
})

export const isValidRecipe = (recipe) => {
    return Boolean(
        recipe &&
        recipe.name?.trim() &&
        Array.isArray(recipe.staffMembers)
    )
}