<template>
  <main class="about-page">
    <!-- ========================================================
         HERO
         ======================================================== -->

    <header class="about-hero">
      <p class="section-eyebrow">
        {{ cocciaStory.hero.eyebrow }}
      </p>

      <h1>
        {{ cocciaStory.hero.title }}
      </h1>

      <p class="hero-intro">
        {{ cocciaStory.hero.intro }}
      </p>

      <div class="hero-rule" aria-hidden="true">
        <span></span>
        <span class="hero-rule-mark">◆</span>
        <span></span>
      </div>
    </header>

    <!-- ========================================================
         STORY CHAPTERS
         ======================================================== -->

    <section class="story-timeline" aria-label="The history of Coccia House">
      <article v-for="(chapter, index) in cocciaStory.chapters" :id="chapter.id" :key="chapter.id" class="story-chapter"
        :class="[
          `layout-${chapter.layout}`,
          { 'chapter-even': index % 2 !== 0 }
        ]">
        <div class="chapter-photo-wrap">
          <figure class="chapter-photo">
            <div v-if="chapter.year" class="photo-year">
              {{ chapter.year }}
            </div>

            <img :src="chapter.image" :alt="chapter.alt" :class="{ 'image-contain': chapter.imageFit === 'contain' }"
              loading="lazy" />

            <figcaption>
              {{ chapter.caption }}
            </figcaption>
          </figure>
        </div>

        <div class="chapter-copy">
          <p class="section-eyebrow">
            {{ chapter.eyebrow }}
          </p>

          <h2>{{ chapter.title }}</h2>

          <p v-for="paragraph in chapter.paragraphs" :key="paragraph">
            {{ paragraph }}
          </p>
        </div>
      </article>
    </section>

    <!-- ========================================================
         CUSTOMER MEMORIES
         ======================================================== -->

    <section class="community-section" aria-labelledby="community-title">
      <div class="community-copy">
        <p class="section-eyebrow">
          {{ cocciaStory.community.eyebrow }}
        </p>

        <h2 id="community-title">
          {{ cocciaStory.community.title }}
        </h2>

        <p v-for="paragraph in cocciaStory.community.paragraphs" :key="paragraph">
          {{ paragraph }}
        </p>
      </div>

      <div class="memory-gallery" aria-label="Customer memories at Coccia House">
        <figure v-for="(photo, index) in cocciaStory.community.photos" :key="photo.src" class="memory-photo"
          :class="`memory-photo-${index + 1}`">
          <img :src="photo.src" :alt="photo.alt" loading="lazy" />

          <figcaption>
            {{ photo.caption }}
          </figcaption>
        </figure>
      </div>
    </section>

    <!-- ========================================================
         CLOSING
         ======================================================== -->

    <section class="story-closing" aria-labelledby="closing-title">
      <div class="closing-photo-wrap">
        <figure class="closing-photo">
          <img :src="cocciaStory.closing.image" :alt="cocciaStory.closing.alt" loading="lazy" />

          <figcaption>
            {{ cocciaStory.closing.caption }}
          </figcaption>
        </figure>
      </div>

      <div class="closing-copy">
        <p class="section-eyebrow">
          {{ cocciaStory.closing.eyebrow }}
        </p>

        <h2 id="closing-title">
          {{ cocciaStory.closing.title }}
        </h2>

        <p v-for="paragraph in cocciaStory.closing.paragraphs" :key="paragraph">
          {{ paragraph }}
        </p>
      </div>
    </section>

    <!-- ========================================================
         FINAL QUOTE AND ACTION
         ======================================================== -->

    <!-- ========================================================
     FINAL SLOGAN
     ======================================================== -->

    <footer class="about-finale">
      <div class="finale-divider" aria-hidden="true">
        <span></span>
        <span class="finale-mark">◆</span>
        <span></span>
      </div>

      <p class="finale-intro">
        From Our Family to Yours
      </p>

      <p class="finale-slogan">
        Buon Appetito
      </p>

      <RouterLink to="/menu" class="menu-link">
        View Our Menu
      </RouterLink>

      <div class="finale-divider finale-divider-bottom" aria-hidden="true">
        <span></span>
        <span class="finale-mark">◆</span>
        <span></span>
      </div>
    </footer>
  </main>
</template>

<script setup>
import { RouterLink } from "vue-router"
import { cocciaStory } from "@/content/cocciaStory.js"
</script>

<style scoped>
/* ==========================================================
   ABOUT PAGE
   ========================================================== */

.about-page {
  width: min(100%, 1280px);
  margin: 0 auto;
  padding: clamp(2.5rem, 6vw, 6rem) clamp(1rem, 4vw, 3rem) 6rem;

  color: var(--default-color);
}

.about-page h1,
.about-page h2,
.about-page p,
.about-page figure,
.about-page blockquote {
  margin-top: 0;
}

.about-page p {
  line-height: 1.8;
}


/* ==========================================================
   SHARED TYPOGRAPHY
   ========================================================== */

.section-eyebrow {
  margin-bottom: 0.65rem;

  color: var(--bronze-bold);

  font-size: clamp(0.72rem, 1.2vw, 0.85rem);
  font-weight: 700;
  letter-spacing: 0.18em;
  text-transform: uppercase;
}

.about-page h1,
.about-page h2 {
  font-family: Georgia, "Times New Roman", serif;
  font-weight: 500;
  line-height: 1.12;
}

.about-page h1 {
  margin-bottom: 1.25rem;

  font-size: clamp(2.6rem, 7vw, 5.8rem);
}

.about-page h2 {
  margin-bottom: 1.25rem;

  font-size: clamp(2rem, 4vw, 3.6rem);
}


/* ==========================================================
   HERO
   ========================================================== */

.about-hero {
  width: min(100%, 850px);
  margin: 0 auto clamp(5rem, 10vw, 9rem);

  text-align: center;
}

.hero-intro {
  width: min(100%, 720px);
  margin-right: auto;
  margin-bottom: 2.25rem;
  margin-left: auto;

  font-size: clamp(1.05rem, 2vw, 1.35rem);
}

.hero-rule {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.85rem;

  width: min(100%, 420px);
  margin: 0 auto;

  color: var(--bronze-bold);
}

.hero-rule span:not(.hero-rule-mark) {
  width: 100%;
  height: 1px;

  background: linear-gradient(to right,
      transparent,
      var(--bronze-color));
}

.hero-rule span:last-child {
  background: linear-gradient(to left,
      transparent,
      var(--bronze-color));
}

.hero-rule-mark {
  font-size: 0.7rem;
}


/* ==========================================================
   STORY TIMELINE
   ========================================================== */

.story-timeline {
  position: relative;

  display: grid;
  gap: clamp(6rem, 12vw, 11rem);
}

.story-chapter {
  display: grid;
  grid-template-columns: minmax(280px, 0.9fr) minmax(320px, 1fr);
  align-items: center;
  gap: clamp(2.5rem, 7vw, 7rem);
}

.story-chapter.layout-right .chapter-photo-wrap {
  order: 2;
}

.story-chapter.layout-right .chapter-copy {
  order: 1;
}

.chapter-copy {
  max-width: 600px;
}

.chapter-copy p:not(.section-eyebrow) {
  color: rgba(255, 250, 241, 0.88);
  font-size: clamp(1rem, 1.5vw, 1.13rem);
}

.chapter-copy p:last-child {
  margin-bottom: 0;
}


/* ==========================================================
   HISTORIC PHOTOGRAPHS
   ========================================================== */

.chapter-photo-wrap {
  position: relative;

  display: flex;
  justify-content: center;
}

.chapter-photo {
  position: relative;

  width: min(100%, 520px);
  margin: 0;
  padding: clamp(0.55rem, 1.5vw, 0.9rem);

  background-color: #eee5d4;
  border: 1px solid rgba(138, 106, 50, 0.8);
  box-shadow:
    0 25px 60px rgba(0, 0, 0, 0.34),
    0 4px 12px rgba(0, 0, 0, 0.22);

  transform: rotate(-0.6deg);
}

.chapter-even .chapter-photo {
  transform: rotate(0.6deg);
}

.chapter-photo img {
  display: block;

  width: 100%;
  max-height: 650px;

  object-fit: cover;
  object-position: center;

  background-color: #d9d0c1;
}

.chapter-photo img.image-contain {
  object-fit: contain;
}

.chapter-photo figcaption,
.closing-photo figcaption {
  padding: 0.85rem 0.65rem 0.2rem;

  color: #493e31;

  font-family: Georgia, "Times New Roman", serif;
  font-size: 0.88rem;
  font-style: italic;
  line-height: 1.45;
  text-align: center;
}

.photo-year {
  position: absolute;
  top: -1.1rem;
  left: -1rem;
  z-index: 2;

  padding: 0.65rem 1rem;

  color: #fffaf1;
  background-color: var(--bronze-bold);
  border: 1px solid rgba(255, 250, 241, 0.4);
  box-shadow: 0 5px 12px rgba(0, 0, 0, 0.25);

  font-family: Georgia, "Times New Roman", serif;
  font-size: 1rem;
  letter-spacing: 0.12em;
}


/* ==========================================================
   FEATURE CHAPTERS

   Full-width sections interrupt the alternating pattern so
   the page does not feel like a repetitive list of rows.
   ========================================================== */

.story-chapter.layout-feature {
  display: block;
}

.layout-feature .chapter-copy {
  width: min(100%, 760px);
  max-width: none;
  margin: 0 auto 3rem;

  text-align: center;
}

.layout-feature .chapter-photo {
  width: min(100%, 940px);

  transform: none;
}

.layout-feature .chapter-photo img {
  max-height: none;
}


/* ==========================================================
   COMMUNITY SECTION
   ========================================================== */

.community-section {
  margin-top: clamp(7rem, 14vw, 12rem);
  padding: clamp(3rem, 7vw, 6rem);

  background:
    linear-gradient(rgba(19, 14, 11, 0.9),
      rgba(19, 14, 11, 0.9));

  border-top: 1px solid var(--bronze-color);
  border-bottom: 1px solid var(--bronze-color);
}

.community-copy {
  width: min(100%, 780px);
  margin: 0 auto clamp(3rem, 7vw, 5rem);

  text-align: center;
}

.community-copy p:not(.section-eyebrow) {
  font-size: clamp(1rem, 1.7vw, 1.17rem);
}

.memory-gallery {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: clamp(1rem, 2vw, 1.75rem);
}

.memory-photo {
  position: relative;

  grid-column: span 6;

  margin: 0;
  padding: 0.55rem;

  background-color: #eee5d4;
  box-shadow: 0 18px 40px rgba(0, 0, 0, 0.3);

  transition:
    transform 180ms ease,
    box-shadow 180ms ease;
}

.memory-photo:hover {
  z-index: 2;

  transform: translateY(-5px);
  box-shadow: 0 24px 55px rgba(0, 0, 0, 0.38);
}

.memory-photo img {
  display: block;

  width: 100%;
  aspect-ratio: 4 / 3;

  object-fit: cover;
}

.memory-photo figcaption {
  padding: 0.7rem 0.4rem 0.2rem;

  color: #493e31;

  font-family: Georgia, "Times New Roman", serif;
  font-style: italic;
  text-align: center;
}

.memory-photo-1 {
  transform: rotate(-0.7deg);
}

.memory-photo-2 {
  transform: rotate(0.6deg);
}

.memory-photo-3 {
  transform: rotate(0.4deg);
}

.memory-photo-4 {
  transform: rotate(-0.5deg);
}


/* ==========================================================
   CLOSING STORY
   ========================================================== */

.story-closing {
  display: grid;
  grid-template-columns: minmax(320px, 1.1fr) minmax(300px, 0.9fr);
  align-items: center;
  gap: clamp(3rem, 8vw, 8rem);

  margin-top: clamp(7rem, 14vw, 12rem);
}

.closing-photo {
  width: 100%;
  margin: 0;
  padding: clamp(0.6rem, 1.5vw, 1rem);

  background-color: #eee5d4;
  border: 1px solid var(--bronze-color);
  box-shadow: 0 25px 65px rgba(0, 0, 0, 0.36);
}

.closing-photo img {
  display: block;

  width: 100%;
  max-height: 690px;

  object-fit: cover;
  object-position: center;
}

.closing-copy {
  max-width: 570px;
}

.closing-copy p:not(.section-eyebrow) {
  font-size: clamp(1rem, 1.6vw, 1.15rem);
}


/* ==========================================================
   FINAL SLOGAN
   ========================================================== */

.about-finale {
  width: min(100%, 760px);
  margin: clamp(7rem, 14vw, 12rem) auto 0;
  padding: clamp(2.5rem, 6vw, 4.5rem) 1rem;

  text-align: center;
}

.finale-divider {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.9rem;

  width: min(100%, 560px);
  margin: 0 auto 2.5rem;

  color: var(--bronze-bold);
}

.finale-divider-bottom {
  margin-top: 2.5rem;
  margin-bottom: 0;
}

.finale-divider span:not(.finale-mark) {
  width: 100%;
  height: 1px;

  background: linear-gradient(to right,
      transparent,
      var(--bronze-color));
}

.finale-divider span:last-child {
  background: linear-gradient(to left,
      transparent,
      var(--bronze-color));
}

.finale-mark {
  flex: 0 0 auto;

  font-size: 0.65rem;
}

.finale-intro {
  margin: 0 0 0.65rem;

  color: var(--default-color);

  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(1.15rem, 2.5vw, 1.65rem);
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.finale-slogan {
  margin: 0;

  color: var(--bronze-bold);

  font-family: Georgia, "Times New Roman", serif;
  font-size: clamp(2.5rem, 7vw, 5rem);
  font-style: italic;
  line-height: 1.1;
}

@media (max-width: 600px) {
  .about-finale {
    margin-top: 6rem;
    padding-right: 0;
    padding-left: 0;
  }

  .finale-intro {
    font-size: 1rem;
    letter-spacing: 0.06em;
  }

  .finale-divider {
    gap: 0.65rem;
  }
}

.menu-link {
  display: inline-flex;
  align-items: center;
  justify-content: center;

  min-width: 190px;
  padding: 0.9rem 1.5rem;

  color: #fffaf1;
  background-color: transparent;
  border: 1px solid var(--bronze-bold);

  font-weight: 700;
  letter-spacing: 0.08em;
  text-decoration: none;
  text-transform: uppercase;

  transition:
    color 160ms ease,
    background-color 160ms ease,
    transform 160ms ease;
}

.menu-link:hover,
.menu-link:focus-visible {
  color: #17110d;
  background-color: var(--bronze-bold);

  transform: translateY(-2px);
}


/* ==========================================================
   TABLET
   ========================================================== */

@media (max-width: 850px) {

  .story-chapter,
  .story-closing {
    grid-template-columns: 1fr;
    gap: 2.5rem;
  }

  .story-chapter.layout-right .chapter-photo-wrap,
  .story-chapter.layout-right .chapter-copy {
    order: initial;
  }

  .chapter-photo-wrap,
  .closing-photo-wrap {
    order: 1;
  }

  .chapter-copy,
  .closing-copy {
    order: 2;

    width: min(100%, 650px);
    max-width: none;
    margin: 0 auto;

    text-align: center;
  }

  .story-chapter {
    gap: 2.5rem;
  }

  .memory-gallery {
    grid-template-columns: 1fr 1fr;
  }

  .memory-photo {
    grid-column: auto;
  }
}


/* ==========================================================
   MOBILE
   ========================================================== */

@media (max-width: 600px) {
  .about-page {
    padding-right: 1rem;
    padding-left: 1rem;
  }

  .about-hero {
    margin-bottom: 5rem;
  }

  .story-timeline {
    gap: 5.5rem;
  }

  .chapter-photo {
    padding: 0.45rem;

    transform: none;
  }

  .chapter-even .chapter-photo {
    transform: none;
  }

  .chapter-photo figcaption,
  .closing-photo figcaption {
    font-size: 0.8rem;
  }

  .photo-year {
    top: -0.8rem;
    left: -0.35rem;

    padding: 0.5rem 0.75rem;
  }

  .community-section {
    margin-right: -1rem;
    margin-left: -1rem;
    padding: 3.5rem 1rem;
  }

  .memory-gallery {
    grid-template-columns: 1fr;
  }

  .memory-photo {
    transform: none;
  }

  .memory-photo:hover {
    transform: translateY(-3px);
  }

  .story-closing {
    margin-top: 6rem;
  }

  .about-finale {
    margin-top: 6rem;
  }
}


/* ==========================================================
   REDUCED MOTION
   ========================================================== */

@media (prefers-reduced-motion: reduce) {

  .memory-photo,
  .menu-link {
    transition: none;
  }
}
</style>