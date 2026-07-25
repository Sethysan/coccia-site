<template>
  <footer class="site-footer">
    <div class="footer-content">

      <!-- Restaurant identity -->

      <section class="footer-section footer-brand">
        <h2>Coccia House</h2>

        <p class="footer-slogan">
          A Wooster tradition since 1958.
        </p>

        <p>
          Homemade pizza, Italian favorites, and generations of memories.
        </p>
      </section>

      <!-- Site navigation -->

      <nav class="footer-section" aria-label="Footer navigation">
        <h2>Explore</h2>

        <ul class="footer-links">
          <li>
            <RouterLink to="/">Home</RouterLink>
          </li>

          <li>
            <RouterLink to="/menu">Menu</RouterLink>
          </li>

          <li>
            <RouterLink to="/about">Our Story</RouterLink>
          </li>
          <!-- TODO: Create Contact view if needed -->
          <!-- <li>
            <RouterLink to="/contact">Contact</RouterLink>
          </li> -->
        </ul>
      </nav>

      <!-- Contact links -->

      <section class="footer-section">
        <h2>Visit & Contact</h2>

        <ul class="footer-links">
          <li>
            <a href="tel:13302627136" @click="trackPhoneClick('footer')">
              (330) 262-7136
            </a>
          </li>

          <li>
            <a href="https://www.google.com/maps/search/?api=1&query=Coccia+House+Wooster+Ohio" target="_blank"
              rel="noopener noreferrer" @click="trackDirectionsClick('footer')">
              Get Directions
            </a>
          </li>

          <!--TODo: Update Contact info <li>
            <a href="mailto:YOUR-EMAIL-HERE">
              Events & Business Inquiries
            </a>
          </li> -->
        </ul>
      </section>

      <!-- Community links -->

      <section class="footer-section">
        <h2>Follow Us</h2>
        <ul class="footer-links">
          <!-- official fb site link -->
          <li>
            <a class="facebook-row"
              href="https://www.facebook.com/people/Coccia-House-Ristorante-Pizzeria/100063523334678/" target="_blank"
              rel="noopener noreferrer" aria-label="Visit the official Coccia House Facebook page">
              <i class="bi bi-facebook facebook-icon" aria-hidden="true"></i>

              <span>
                Official site, weekly specials and more
              </span>
            </a>
          </li>

          <li>
            <a class="facebook-row" href="https://www.facebook.com/groups/59045658576/" target="_blank"
              rel="noopener noreferrer" aria-label="Join the Fans of Coccia House Facebook group">
              <i class="bi bi-facebook facebook-icon" aria-hidden="true"></i>

              <span>
                Join Fans of Coccia House Group
              </span>
            </a>
          </li>
        </ul>
      </section>

    </div>

    <div class="footer-bottom">
      <p>
        &copy; {{ currentYear }} Coccia House. All rights reserved.
      </p>

      <p class="website-credit">
        Website designed and developed by
        <a href="https://www.sethrakich.com/" target="_blank" rel="noopener noreferrer">
          Seth Rakich
        </a>
      </p>
    </div>
  </footer>
</template>

<script setup>
import { RouterLink } from 'vue-router'
import { trackPhoneClick } from "@/utils/analytics"
import { trackDirectionsClick } from "@/utils/analytics"
const currentYear = new Date().getFullYear()
</script>

<style scoped>
.site-footer {
  width: 100%;
  color: var(--default-color);
  background-color: rgba(12, 9, 7, 0.96);

  border-top: 1px solid var(--bronze-color);
  box-shadow: 0 -8px 24px rgba(0, 0, 0, 0.2);
}

.footer-content {
  display: grid;
  grid-template-columns: 1.5fr repeat(3, 1fr);
  gap: 2.5rem;

  width: min(1200px, 92%);
  margin: 0 auto;
  padding: 3rem 1rem;
}

.footer-section h2 {
  margin-bottom: 1rem;

  color: var(--bronze-bold);
  font-size: 1.05rem;
  letter-spacing: 0.04em;
}

.footer-brand h2 {
  font-size: 1.5rem;
}

.footer-brand p {
  max-width: 340px;
  margin: 0;

  line-height: 1.65;
  color: var(--default-dark);
}

.footer-brand .footer-slogan {
  margin-bottom: 0.6rem;

  color: var(--default-color);
  font-style: italic;
  font-weight: 700;
}

.footer-links {
  display: flex;
  flex-direction: column;
  gap: 0.7rem;

  margin: 0;
  padding: 0;

  list-style: none;
}

.footer-links a,
.website-credit a {
  color: var(--default-dark);
  text-decoration: none;

  transition:
    color 180ms ease,
    text-decoration-color 180ms ease;
}

.footer-links a:hover,
.footer-links a:focus-visible,
.website-credit a:hover,
.website-credit a:focus-visible {
  color: var(--bronze-bold);
  text-decoration: underline;
  text-underline-offset: 0.25rem;
}

.facebook-row {
  display: flex;
  align-items: center;
  gap: 0.65rem;
}

.facebook-icon {
  display: inline-block;

  flex-shrink: 0;

  color: var(--default-color);
  font-size: 1.7rem;

  transition:
    color 200ms ease,
    transform 200ms ease;
}

.footer-links a.facebook-row:hover .facebook-icon,
.footer-links a.facebook-row:focus-visible .facebook-icon {
  color: var(--bronze-bold);
  transform: scale(1.1);
}

.footer-links a.facebook-row:hover,
.footer-links a.facebook-row:focus-visible {
  color: var(--default-dark);
  text-decoration: none;
}

.footer-bottom {
  display: flex;
  justify-content: space-between;
  gap: 1rem;

  width: min(1200px, 92%);
  margin: 0 auto;
  padding: 1.25rem 1rem;

  border-top: 1px solid rgba(255, 255, 255, 0.12);

  color: var(--default-dark);
  font-size: 0.85rem;
}

.footer-bottom p {
  margin: 0;
}

.website-credit {
  text-align: right;
}

@media (max-width: 900px) {
  .footer-content {
    grid-template-columns: repeat(2, 1fr);
    padding: 2.5rem 0.5rem;
  }
}

@media (max-width: 600px) {

  .footer-content {
    grid-template-columns: 1fr;
    gap: 2rem;

    padding: 0;
  }

  .footer-section {
    text-align: center;
  }

  .footer-brand p {
    margin-right: auto;
    margin-left: auto;
  }

  .footer-links {
    align-items: center;
  }

  .footer-bottom {
    flex-direction: column;
    align-items: center;

    padding: 1.25rem 0.5rem;

    text-align: center;
  }

  .website-credit {
    text-align: center;
  }
}
</style>