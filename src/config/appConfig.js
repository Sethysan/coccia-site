const apiUrl = import.meta.env.VITE_API_URL?.trim()

export const appConfig = {
  /*
   * WHITE NOTE:
   * Keep API content disabled until the Spring Boot backend is deployed,
   * secured, tested, and returning the same data shape as the local files.
   *
   * Switching this to true should not require changes inside components.
   */
  useRemoteContent:
    import.meta.env.VITE_USE_REMOTE_CONTENT === "true" && Boolean(apiUrl),

  apiUrl: apiUrl || "",

  /*
   * WHITE NOTE:
   * During migration, local data remains the fallback source.
   * A temporary API outage should not take down public-facing content.
   */
  allowContentFallback: true,

  requestTimeoutMs: 8000
}