/* ==========================================================
   RESTAURANT HOURS

   Purpose
   -------
   Defines the restaurant's weekly operating schedule.

   This file contains ONLY static data.

   Business logic such as determining whether the restaurant
   is currently open belongs inside useRestaurantHours().

   Every day follows the same structure so components and
   composables can treat each object consistently.
   ========================================================== */
export const hours = [
  {
    day: 0,
    name: "Sunday ",
    closed: false,
    open: 15,
    close: 20,
    hours: "3 PM - 8 PM"
  },
  
  {
    day: 1,
    name: "Monday ",
    closed: true,
    hours: "Closed"
  },
  {
    day: 2,
    name: "Tuesday ",
    closed: true,
    hours: "Closed"
  },
  {
    day: 3,
    name: "Wednesday ",
    closed: false,
    open: 15,
    close: 21,
    hours: "3 PM - 9 PM"
  },
  {
    day: 4,
    name: "Thursday ",
    closed: false,
    open: 15,
    close: 21,
    hours: "3 PM - 9 PM"
  },
  {
    day: 5,
    name: "Friday ",
    closed: false,
    open: 15,
    close: 21,
    hours: "3 PM - 9 PM"
  },
  {
    day: 6,
    name: "Saturday ",
    closed: false,
    open: 15,
    close: 21,
    hours: "3 PM - 9 PM"
  }
]
/*

Day Object Structure

day
    JavaScript weekday number.
    0 = Sunday
    6 = Saturday

name
    Display name shown to visitors.

closed
    Indicates whether the restaurant is closed for the
    entire day.

open / close
    Opening and closing hours using 24-hour time.
    Only required when closed is false.

hours
    Human-readable hours displayed on the website.

*/