const trackEvent = (eventName, parameters = {}) => {
  if (typeof window === "undefined" || typeof window.gtag !== "function") {
    return
  }

  window.gtag("event", eventName, parameters)
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

export const trackFacebookClick = (linkName = "unknown") => {
  trackEvent("facebook_click", {
    link_name: linkName
  })
}

export const trackGalleryOpened = (imageName = "unknown") => {
  trackEvent("gallery_opened", {
    image_name: imageName
  })
}
