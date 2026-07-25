const trackEvent = (eventName, parameters = {}) => {
  if (typeof window === "undefined") {
    return
  }

  // Google Analytics 4
  if (typeof window.gtag === "function") {
    window.gtag("event", eventName, parameters)
  }

  // Microsoft Clarity
  if (typeof window.clarity === "function") {
    window.clarity("event", eventName)

    Object.entries(parameters).forEach(([key, value]) => {
      if (value !== undefined && value !== null) {
        window.clarity("set", key, String(value))
      }
    })
  }

  if (import.meta.env.DEV) {
    console.log(`[analytics] ${eventName}`, parameters)
  }
}

export const trackPhoneClick = (location = "unknown") => {
  trackEvent("phone_click", {
    link_location: location
  })
}

export const trackDirectionsClick = (location = "unknown") => {
  trackEvent("directions_click", {
    link_location: location
  })
}

export const trackHoursOpened = (location = "unknown") => {
  trackEvent("hours_opened", {
    link_location: location
  })
}
// todo
export const trackFacebookClick = (linkName = "unknown") => {
  trackEvent("facebook_click", {
    link_name: linkName
  })
}
// todo
export const trackGalleryOpened = (imageName = "unknown") => {
  trackEvent("gallery_opened", {
    image_name: imageName
  })
}
export const trackMenuClick = (location = "unknown") => {
  trackEvent("menu_click", {
    link_location: location
  })
}

export const trackMenuSectionClick = (sectionName = "unknown") => {
  trackEvent("menu_section_click", {
    section_name: sectionName
  })
}
export const trackNewsClick = (
  newsTitle = "unknown",
  action = "view_details"
) => {
  trackEvent("news_click", {
    news_title: newsTitle,
    news_action: action
  })
}