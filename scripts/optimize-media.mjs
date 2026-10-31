
import sharp from 'sharp'
import { readdir, mkdir, stat } from 'node:fs/promises'
import path from 'node:path'

const sourceDir = path.resolve('public/media')
const outputDir = path.resolve('public/media-optimized')

let originalBytes = 0
let optimizedBytes = 0
let processed = 0

async function processDirectory(directory) {
  const entries = await readdir(directory, { withFileTypes: true })

  for (const entry of entries) {
    const sourcePath = path.join(directory, entry.name)

    if (entry.isDirectory()) {
      await processDirectory(sourcePath)
      continue
    }

    if (!/\.(jpg|jpeg|png|webp)$/i.test(entry.name)) continue

    const relativePath = path.relative(sourceDir, sourcePath)
    const destination = path.join(
      outputDir,
      relativePath.replace(/\.(jpg|jpeg|png|webp)$/i, '.webp')
    )

    await mkdir(path.dirname(destination), { recursive: true })

    await sharp(sourcePath)
      .rotate()
      .resize({
        width: 1200,
        height: 1200,
        fit: 'inside',
        withoutEnlargement: true
      })
      .webp({ quality: 80, effort: 5 })
      .toFile(destination)

    originalBytes += (await stat(sourcePath)).size
    optimizedBytes += (await stat(destination)).size
    processed++
  }
}

await processDirectory(sourceDir)

const mb = bytes => (bytes / 1024 / 1024).toFixed(2)

console.log(`Images processed: ${processed}`)
console.log(`Original size: ${mb(originalBytes)} MB`)
console.log(`Optimized size: ${mb(optimizedBytes)} MB`)
console.log(
  `Reduction: ${(100 * (1 - optimizedBytes / originalBytes)).toFixed(1)}%`
)
