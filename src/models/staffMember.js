export const createStaffMember = (data = {}) => ({
    id: data.id ?? null,
    displayName: data.displayName ?? "",
    active: data.active ?? true,
    createdAt: data.createdAt ?? null,
    updatedAt: data.updatedAt ?? null
})

export const isValidStaffMember = (staffMember) => {
    return Boolean(
        staffMember &&
        staffMember.displayName?.trim()
    )
}