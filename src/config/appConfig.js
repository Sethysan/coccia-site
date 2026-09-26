export const appConfig = {
  /*
   * Public database-driven content remains behind this flag
   * until production content has been verified.
   */
  useRemoteContent:
    import.meta.env.VITE_USE_REMOTE_CONTENT === "true",

  /*
   * API requests are same-origin.
   *
   * Development:
   *   Vite proxies /api -> local Spring Boot
   *
   * Production:
   *   Netlify proxies /api -> Railway
   */
  apiUrl: "",

  /*
   * Public-facing content may fall back to local data
   * if the API is unavailable.
   */
  allowContentFallback: true,

  requestTimeoutMs: 8000
}