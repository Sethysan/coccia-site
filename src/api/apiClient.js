import { appConfig } from "@/config/appConfig"
import { getCsrfToken } from "@/utils/csrf"

class ApiError extends Error {
  constructor(message, status = null, details = null) {
    super(message)

    this.name = "ApiError"
    this.status = status
    this.details = details
  }
}

const createTimeoutSignal = (timeoutMs) => {
  const controller = new AbortController()

  const timeoutId = window.setTimeout(() => {
    controller.abort()
  }, timeoutMs)

  return {
    signal: controller.signal,
    clear: () => window.clearTimeout(timeoutId)
  }
}

export const apiRequest = async (endpoint, options = {}) => {
  if (!appConfig.apiUrl) {
    throw new ApiError("The API URL has not been configured.")
  }

  const timeout = createTimeoutSignal(appConfig.requestTimeoutMs)

  const method = options.method?.toUpperCase() || "GET"

  const requiresCsrf = !["GET", "HEAD", "OPTIONS"].includes(method)

  const csrfToken = requiresCsrf
    ? getCsrfToken()
    : null

  try {
    const response = await fetch(`${appConfig.apiUrl}${endpoint}`, {
      ...options,
      signal: timeout.signal,
      credentials: "include",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        ...(csrfToken && {
          "X-XSRF-TOKEN": csrfToken
        }),
        ...options.headers
      }
    })

    if (!response.ok) {
      let details = null

      try {
        details = await response.json()
      } catch {
        // The server may return an empty or non-JSON error response.
      }

      throw new ApiError(
        details?.message || `Request failed with status ${response.status}.`,
        response.status,
        details
      )
    }

    if (response.status === 204) {
      return null
    }

    return await response.json()
  } catch (error) {
    if (error.name === "AbortError") {
      throw new ApiError("The API request timed out.")
    }

    if (error instanceof ApiError) {
      throw error
    }

    throw new ApiError("The API could not be reached.", null, error)
  } finally {
    timeout.clear()
  }
}

export { ApiError }

