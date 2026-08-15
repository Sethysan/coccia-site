export async function getCurrentWeeklyOffering() {
    const response = await fetch(
        '/api/public/weekly-offerings/current',
        {
            method: 'GET'
        }
    )

    if (response.status === 204) {
        return null
    }

    if (!response.ok) {
        throw new Error(
            'Unable to load the current weekly offering.'
        )
    }

    return response.json()
}