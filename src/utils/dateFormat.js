export function formatDateRange(startDate, endDate) {
    if (!startDate || !endDate) {
        return ''
    }

    const start = parseLocalDate(startDate)
    const end = parseLocalDate(endDate)

    const startMonth = start.toLocaleDateString('en-US', {
        month: 'short'
    })

    const endMonth = end.toLocaleDateString('en-US', {
        month: 'short'
    })

    const startDay = start.getDate()
    const endDay = end.getDate()

    const startYear = start.getFullYear()
    const endYear = end.getFullYear()

    if (
        startYear === endYear
        && startMonth === endMonth
    ) {
        return `${startMonth} ${startDay}–${endDay}, ${startYear}`
    }

    if (startYear === endYear) {
        return `${startMonth} ${startDay}–${endMonth} ${endDay}, ${startYear}`
    }

    return `${startMonth} ${startDay}, ${startYear}–${endMonth} ${endDay}, ${endYear}`
}

function parseLocalDate(dateString) {
    const [year, month, day] =
        dateString.split('-').map(Number)

    return new Date(year, month - 1, day)
}


export function formatDeliveryDate(dateString) {
    if (!dateString) return 'Not scheduled'

    const date = parseLocalDate(dateString)

    return date.toLocaleDateString('en-US', {
        weekday: 'long',
        month: 'long',
        day: 'numeric',
        year: 'numeric'
    })
}

export function getNextDeliveryDate(dateString, frequency) {
    if (!dateString) return null

    const originalDate = parseLocalDate(dateString)

    if (!['WEEKLY', 'BIWEEKLY'].includes(frequency)) {
        return dateString
    }

    const intervalDays = frequency === 'WEEKLY' ? 7 : 14

    const today = new Date()
    today.setHours(0, 0, 0, 0)

    const nextDate = new Date(originalDate)

    while (nextDate < today) {
        nextDate.setDate(nextDate.getDate() + intervalDays)
    }

    const year = nextDate.getFullYear()
    const month = String(nextDate.getMonth() + 1).padStart(2, '0')
    const day = String(nextDate.getDate()).padStart(2, '0')

    return `${year}-${month}-${day}`
}
