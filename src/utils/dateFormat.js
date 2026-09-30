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
