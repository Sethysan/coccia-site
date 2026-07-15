<template>
    <div class="scrollwork-divider" aria-hidden="true">
        <svg class="scrollwork-svg" viewBox="0 0 1200 60" preserveAspectRatio="none">

            <!-- <defs>
                <linearGradient id="bronze-shine" x1="0%" y1="0%" x2="100%" y2="0%">
                    <stop offset="0%" stop-color="white" stop-opacity="0" />

                    <stop offset="45%" stop-color="#ffe0a3" stop-opacity="0" />

                    <stop offset="50%" stop-color="#ffe0a3" stop-opacity="0.9" />

                    <stop offset="55%" stop-color="#ffe0a3" stop-opacity="0" />

                    <stop offset="100%" stop-color="white" stop-opacity="0" />
                </linearGradient>
            </defs> -->

            <!-- Main bronze line -->
            <path class="divider-line" d="M 0 30 H 1200" />


            <!-- Center scrollwork -->
            <g class="ornament">

                <!-- Left curls -->
                <path d="
            M 600 30
            C 575 8, 545 8, 535 30
            C 525 50, 495 50, 485 30
            C 475 12, 450 12, 440 30
          " />

                <path d="
            M 520 30
            C 510 18, 495 18, 490 30
            C 485 40, 470 40, 465 30
          " />


                <!-- Right curls -->
                <path d="
            M 600 30
            C 625 8, 655 8, 665 30
            C 675 50, 705 50, 715 30
            C 725 12, 750 12, 760 30
          " />

                <path d="
            M 680 30
            C 690 18, 705 18, 710 30
            C 715 40, 730 40, 735 30
          " />


                <!-- Center vertical ornament -->
                <path d="
            M 600 5
            L 610 22
            L 600 30
            L 590 22
            Z
          " />

                <path d="
            M 600 30
            L 610 38
            L 600 55
            L 590 38
            Z
          " />


                <!-- Small decorative circles -->
                <circle cx="430" cy="30" r="4" />
                <circle cx="420" cy="30" r="3" />

                <circle cx="770" cy="30" r="4" />
                <circle cx="780" cy="30" r="3" />

            </g>


            <!-- Moving highlight -->
            <!-- <rect class="bronze-highlight" x="-180" y="26" width="150" height="6" /> -->

        </svg>
    </div>
</template>

<style scoped>
.scrollwork-svg {
    display: block;

    width: 100%;
    height: 100%;
}

.ornament {
    fill: #0c033069;

    stroke: #4c3018;
    stroke-width: 1.;

    stroke-linecap: round;
    stroke-linejoin: round;
}

.ornament path {
    fill: none;
}

.ornament path:nth-of-type(5),
.ornament path:nth-of-type(6) {
    fill: #24010474;
}

.divider-line,
.ornament {
    filter:
        drop-shadow(0 1px 0 rgb(255 210 140 / 20%)) drop-shadow(0 3px 3px rgb(0 0 0 / 55%));
}

/* .bronze-highlight {
    fill: url("#bronze-shine");

    mix-blend-mode: screen;

    opacity: 0;

    animation: bronzeSweep 10s ease-in-out infinite;
}

@keyframes bronzeSweep {
  0% {
    transform: translateX(0);
    opacity: 0;
  }

  3% {
    opacity: 0.8;
  }

  12% {
    transform: translateX(1380px);
    opacity: 0.8;
  }

  15% {
    opacity: 0;
  }

  100% {
    transform: translateX(1380px);
    opacity: 0;
  }
} */
</style>