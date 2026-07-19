<template>
  <main id="about" class="about-page">

    <!-- ========================================================
         PAGE INTRODUCTION
         ======================================================== -->

    <header class="about-intro">
      <p class="section-eyebrow">
        {{ cocciaStory.intro.eyebrow }}
      </p>

      <h1>
        {{ cocciaStory.intro.title }}
      </h1>

      <p class="about-intro-text">
        {{ cocciaStory.intro.text }}
      </p>
    </header>


    <!-- ========================================================
         RESTAURANT STORY
         ======================================================== -->

    <article class="story-card">
      <section v-for="section in cocciaStory.sections" :key="section.title" class="story-section">
        <h2>
          {{ section.title }}
        </h2>

        <p v-for="paragraph in section.paragraphs" :key="paragraph">
          {{ paragraph }}
        </p>
      </section>
    </article>


    <!-- ========================================================
         PIZZA PHILOSOPHY
         ======================================================== -->

    <aside class="pizza-promise">
      <p class="section-eyebrow">
        {{ cocciaStory.pizzaPromise.title }}
      </p>

      <p>
        {{ cocciaStory.pizzaPromise.text }}
      </p>
    </aside>

    <!-- ========================================================
     COCCIA HOUSE TIMELINE
     ======================================================== -->

    <section v-if="cocciaStory.timeline.events.length" class="history-timeline" aria-labelledby="timeline-title">
      <p class="section-eyebrow">
        {{ cocciaStory.timeline.eyebrow }}
      </p>

      <h2 id="timeline-title">
        {{ cocciaStory.timeline.title }}
      </h2>

      <div class="timeline-list">
        <article v-for="event in cocciaStory.timeline.events" :key="`${event.year}-${event.title}`"
          class="timeline-event">
          <p class="timeline-year">
            {{ event.year }}
          </p>

          <div class="timeline-content">
            <p v-if="event.category" class="timeline-category">
              {{ event.category }}
            </p>

            <h3>
              {{ event.title }}
            </h3>

            <p>
              {{ event.text }}
            </p>
          </div>
        </article>
      </div>
    </section>

    <!-- ========================================================
     HISTORIC ARCHIVE AND CONTRIBUTIONS
     ======================================================== -->


    <section class="history-archive" aria-labelledby="archive-title">
      <h2 id="archive-title">
        {{ cocciaStory.archive.title }}
      </h2>

      <p v-for="paragraph in cocciaStory.archive.paragraphs" :key="paragraph">
        {{ paragraph }}
      </p>

      <a class="archive-email-link"
        :href="`mailto:${cocciaStory.archive.emailAddress}?subject=Coccia%20House%20History`">
        {{ cocciaStory.archive.emailText }}
      </a>

      <p class="archive-note">
        {{ cocciaStory.archive.note }}
      </p>
    </section>
    <!-- ========================================================
         FUTURE PHOTO ALBUM PREVIEW
         ======================================================== -->

    <!-- <section class="about-photos" aria-labelledby="photos-title">

      <h2 id="photos-title">
        From the Family Album
      </h2>

      <p>
        We are gathering photographs and memories from throughout
        the history of Coccia House. A larger collection will be
        added here after the new website launches.
      </p> -->

    <!--
        Add a small group of two or three photographs here later.

        Keep this section understated. The future full gallery can
        be linked from this section when it is ready.

      </section>
      -->


    <!-- ========================================================
         CLOSING
         ======================================================== -->

    <footer class="about-closing">
      <p class="about-slogan">
        {{ cocciaStory.closing.slogan }}
      </p>

      <p class="about-signature">
        {{ cocciaStory.closing.signature }}
      </p>
    </footer>

  </main>
</template>

<script setup>
import { cocciaStory } from '@/content/cocciaStory'
</script>

<style scoped>
/* ==========================================================
   ABOUT PAGE
   ========================================================== */

.about-page {
  width: 100%;
  padding: 4rem 1.5rem 5rem;

  color: var(--text-primary);
  text-align: center;
}


/* ==========================================================
   SHARED TYPOGRAPHY
   ========================================================== */

.section-eyebrow {
  margin: 0 0 0.55rem;

  color: var(--bronze-bold);

  font-size: 0.8rem;
  font-weight: 700;
  letter-spacing: 0.14em;
  text-transform: uppercase;
}


/* ==========================================================
   INTRODUCTION
   ========================================================== */

.about-intro {
  width: min(100%, 800px);
  margin: 0 auto 4rem;
}

.about-intro h1 {
  margin: 0;

  font-size: clamp(2.5rem, 6vw, 4.5rem);
  line-height: 1.05;
}

.about-intro-text {
  max-width: 650px;
  margin: 1.25rem auto 0;

  font-size: clamp(1.05rem, 2vw, 1.25rem);
  line-height: 1.7;
}


/* ==========================================================
   STORY
   ========================================================== */

.story-card {
  width: min(100%, 760px);
  margin: 0 auto 4.5rem;
  padding: clamp(1.5rem, 4vw, 3rem);

  background-color: var(--background-dark-trans);

  border: 1px solid var(--bronze-color);
  border-radius: 0.5rem;

  text-align: left;
}

.story-section+.story-section {
  margin-top: 2.75rem;
  padding-top: 2.75rem;

  border-top: 1px solid rgba(255, 255, 255, 0.15);
}

.story-section h2 {
  margin: 0 0 1rem;

  color: var(--bronze-bold);
  text-align: center;
}

.story-section p {
  margin: 0;

  font-size: 1.02rem;
  line-height: 1.8;
}

.story-section p+p {
  margin-top: 1.15rem;
}


/* ==========================================================
   PIZZA PHILOSOPHY
   ========================================================== */

.pizza-promise {
  width: min(100%, 640px);
  margin: 0 auto 5rem;
  padding: 2rem 1.5rem;

  border-top: 1px solid var(--bronze-color);
  border-bottom: 1px solid var(--bronze-color);
}

.pizza-promise p:last-child {
  max-width: 570px;
  margin: 0 auto;

  font-size: clamp(1.1rem, 2vw, 1.3rem);
  line-height: 1.7;
}


/* ==========================================================
   FAMILY PHOTOGRAPHS
   ========================================================== */

.about-photos {
  width: min(100%, 760px);
  margin: 0 auto 5rem;
}

.about-photos h2 {
  margin: 0 0 1rem;

  font-size: clamp(1.8rem, 4vw, 2.7rem);
}

.about-photos p {
  max-width: 600px;
  margin: 0 auto;

  line-height: 1.7;
}


/* ==========================================================
   CLOSING
   ========================================================== */

.about-closing {
  width: min(100%, 760px);
  margin: 0 auto;
  padding-top: 3rem;

  border-top: 1px solid var(--bronze-color);
}

.about-slogan {
  margin: 0;

  color: var(--bronze-bold);

  font-size: clamp(1.4rem, 4vw, 2.2rem);
  font-style: italic;
  font-weight: 700;
  line-height: 1.4;
}

.about-signature {
  max-width: 520px;
  margin: 1rem auto 0;

  font-size: 0.95rem;
  line-height: 1.6;
}

/* ==========================================================
   HISTORY TIMELINE
   ========================================================== */

.history-timeline {
  width: min(100%, 760px);
  margin: 0 auto 5rem;
}

.history-timeline>h2 {
  margin: 0 0 2rem;

  font-size: clamp(1.8rem, 4vw, 2.7rem);
}

.timeline-list {
  position: relative;

  text-align: left;
}

.timeline-list::before {
  position: absolute;
  top: 0;
  bottom: 0;
  left: 5.1rem;

  width: 1px;

  background-color: var(--bronze-color);

  content: '';
}

.timeline-event {
  position: relative;

  display: grid;
  grid-template-columns: 4rem 1fr;
  gap: 2rem;

  padding-bottom: 2.5rem;
}

.timeline-event:last-child {
  padding-bottom: 0;
}

.timeline-year {
  margin: 0;

  color: var(--bronze-bold);

  font-size: 1.1rem;
  font-weight: 700;
  text-align: right;
}

.timeline-content {
  position: relative;

  padding: 0 0 0 1.5rem;
}

.timeline-content::before {
  position: absolute;
  top: 0.35rem;
  left: -0.42rem;

  width: 0.75rem;
  height: 0.75rem;

  background-color: var(--bronze-bold);

  border: 3px solid var(--background-dark);
  border-radius: 50%;

  content: '';
}

.timeline-category {
  margin: 0 0 0.3rem;

  color: var(--bronze-bold);

  font-size: 0.75rem;
  font-weight: 700;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.timeline-content h3 {
  margin: 0 0 0.5rem;
}

.timeline-content p:last-child {
  margin: 0;

  line-height: 1.7;
}


/* ==========================================================
   HISTORIC ARCHIVE
   ========================================================== */

.history-archive {
  width: min(100%, 680px);
  margin: 0 auto 5rem;
  padding: 2rem;

  background-color: var(--background-dark-trans);

  border: 1px solid var(--bronze-color);
  border-radius: 0.5rem;
}

.history-archive h2 {
  margin: 0 0 1rem;
}

.history-archive>p {
  margin: 0 auto;

  line-height: 1.7;
}

.history-archive>p+p {
  margin-top: 1rem;
}

.archive-email-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  margin-top: 1.5rem;
  padding: 0.75rem 1.2rem;

  color: var(--default-color);
  background-color: var(--bronze-bold);

  border: 1px solid var(--bronze-bold);
  border-radius: 0.35rem;

  font-weight: 700;
  text-decoration: none;

  transition:
    background-color 200ms ease,
    border-color 200ms ease,
    transform 200ms ease;
}

.archive-email-link:hover,
.archive-email-link:focus-visible {
  background-color: var(--bronze-hover);
  border-color: var(--bronze-hover);
  transform: translateY(-2px);
}

.archive-note {
  max-width: 540px;
  margin-top: 1rem !important;

  color: var(--default-dark);

  font-size: 0.9rem;
  font-style: italic;
}


/* ==========================================================
   MOBILE TIMELINE
   ========================================================== */

@media (max-width: 600px) {
  .history-archive {
    padding: 1.25rem;
  }

  .timeline-list::before {
    left: 0.35rem;
  }

  .timeline-event {
    display: block;

    padding-left: 1.75rem;
  }

  .timeline-year {
    margin-bottom: 0.35rem;

    text-align: left;
  }

  .timeline-content {
    padding-left: 0;
  }

  .timeline-content::before {
    top: -1.55rem;
    left: -1.77rem;
  }
}

/* ==========================================================
   MOBILE
   ========================================================== */

@media (max-width: 600px) {
  .about-page {
    padding: 3rem 0.8rem 4rem;
  }

  .about-intro {
    margin-bottom: 3rem;
  }

  .story-card {
    margin-bottom: 4rem;
    padding: 1.25rem;
  }

  .story-section+.story-section {
    margin-top: 2rem;
    padding-top: 2rem;
  }

  .story-section p {
    font-size: 1rem;
    line-height: 1.7;
  }

  .pizza-promise,
  .about-photos {
    margin-bottom: 4rem;
  }
}
</style>