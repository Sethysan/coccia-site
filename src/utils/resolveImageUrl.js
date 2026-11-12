
import optimizedImages from './optimizedImages.json'

const imagesById = new Map(
  optimizedImages.map(image => [image.id, image.path])
)

export function resolveImageUrl(url) {
  if (!url || typeof url !== 'string') {
    return url
  }

  if (!url.includes('res.cloudinary.com/')) {
    return url
  }

  try {
    const pathname = new URL(url).pathname
    const filename = pathname.split('/').pop()
    const imageId = filename.replace(/\.[^.]+$/, '')

    return imagesById.get(imageId) || url
  } catch {
    return url
  }
}
