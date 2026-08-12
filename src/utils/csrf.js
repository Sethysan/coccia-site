export function getCsrfToken() {
    const cookie = document.cookie
        .split('; ')
        .find(cookie =>
            cookie.startsWith('XSRF-TOKEN=')
        )

    if (!cookie) {
        return null
    }

    return decodeURIComponent(
        cookie.substring('XSRF-TOKEN='.length)
    )
}