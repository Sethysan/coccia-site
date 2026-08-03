import { getCurrentWeeklySpecial } from "@/api/weeklySpecialsApi"
import { appConfig } from "@/config/appConfig"
import { localWeeklySpecial } from "@/data/weeklySpecials"
import { weeklySpecialFallback } from "@/data/fallback/weeklySpecialFallback"
import {
  createWeeklySpecial,
  isValidWeeklySpecial
} from "@/models/weeklySpecialModel"

const loadLocalWeeklySpecial = () => {
  return createWeeklySpecial(localWeeklySpecial)
}

export const loadCurrentWeeklySpecial = async () => {
  /*
   * WHITE NOTE:
   * The frontend continues using local content until remote content
   * is explicitly enabled through an environment variable.
   */
  if (!appConfig.useRemoteContent) {
    return {
      special: loadLocalWeeklySpecial(),
      source: "local",
      error: null
    }
  }

  try {
    const remoteSpecial = createWeeklySpecial(
      await getCurrentWeeklySpecial()
    )

    if (!isValidWeeklySpecial(remoteSpecial)) {
      throw new Error("The weekly-special response was incomplete.")
    }

    return {
      special: remoteSpecial,
      source: "api",
      error: null
    }
  } catch (error) {
    console.error("Unable to load the weekly special:", error)

    if (!appConfig.allowContentFallback) {
      throw error
    }

    return {
      special: createWeeklySpecial(weeklySpecialFallback),
      source: "fallback",
      error
    }
  }
}