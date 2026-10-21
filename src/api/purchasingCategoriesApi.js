
import { apiRequest } from '@/api/apiClient'

const DEPARTMENTS_ENDPOINT = '/api/admin/purchasing/departments'
const CATEGORIES_ENDPOINT = '/api/admin/purchasing/categories'

export const getPurchasingDepartments = () => {
  return apiRequest(DEPARTMENTS_ENDPOINT, {
    method: 'GET'
  })
}

export const getPurchasingCategories = (departmentId = null) => {
  const endpoint = departmentId == null
    ? CATEGORIES_ENDPOINT
    : `${CATEGORIES_ENDPOINT}?departmentId=${encodeURIComponent(departmentId)}`

  return apiRequest(endpoint, {
    method: 'GET'
  })
}
