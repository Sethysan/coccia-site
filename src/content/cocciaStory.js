/* ==========================================================
   COCCIA HOUSE STORY

   Text, photographs, and captions used on the About page.

   Layout options:
   - "left"    Image left, text right
   - "right"   Image right, text left
   - "feature" Large centered photograph
   ========================================================== */

import openingNightAd from "@/assets/about/opening-night-ad.png"
import minnieGarden from "@/assets/about/minnie-garden.jpg"
import afterShiftDinner from "@/assets/about/after-shift-dinner.jpg"
import earlyYears from "@/assets/about/early-years.jpg"
import staff1968 from "@/assets/about/1968-staff.jpg"
import jeanetteJoe from "@/assets/about/jeanette-joe.jpg"
import jeffJeanette from "@/assets/about/jeff-jeanette.jpg"
import jeffFamily from "@/assets/about/jeff-family.jpg"
import steveKaren from "@/assets/about/steve-karen.jpg"
import freshDough from "@/assets/about/fresh-dough.jpg"
import kitchenTeam from "@/assets/about/kitchen-team.jpg"
import steveJoeBar from "@/assets/about/steve-joe-bar.jpg"
import customerOne from "@/assets/about/karen-bianca-sis.jpg"
import customerTwo from "@/assets/about/customer-2.jpg"
import customerThree from "@/assets/about/customer-3.jpg"
import customerFour from "@/assets/about/customer-4.jpg"
import customerFive from "@/assets/about/customer-5.jpg"
import customerSix from "@/assets/about/customer-6.jpg"
import customerSeven from "@/assets/about/customer-7.jpg"
import welcomeSign from "@/assets/about/welcome-sign.jpg"



export const cocciaStory = {
    hero: {
        eyebrow: "Our Story",
        title: "A Family Tradition Since 1958",
        intro:
            "From a humble opening night to generations of family recipes, friendships, and memories, this is the story of Coccia House."
    },

    chapters: [
        {
            id: "beginning",
            year: "1958",
            eyebrow: "A Dream Begins",
            title: "Opening the Doors",
            paragraphs: [
                "Coccia House opened at 5:00 PM on Friday, September 26, 1958, at the corner of Pittsburgh Avenue and Catherine Street in Wooster.",
                "Opening night brought in just $17 in sales. It was a humble beginning for what would become a treasured Wooster tradition."
            ],
            image: openingNightAd,
            alt: "Original Coccia House grand-opening newspaper advertisement from 1958",
            caption:
                "The original advertisement announcing the September 26, 1958 opening of Coccia House.",
            layout: "left",
            imageFit: "contain"
        },

        {
            id: "minnie",
            eyebrow: "The Founder",
            title: "Minnie Coccia",
            paragraphs: [
                "At the center of the restaurant's beginning was Minnie Coccia. Her cooking, determination, and devotion to family helped establish the traditions that would shape Coccia House for generations.",
                "The restaurant grew from the same values found around her family table: food prepared with care, hard work shared among relatives, and a warm welcome extended to everyone."
            ],
            image: minnieGarden,
            alt: "Minnie Coccia standing in her garden",
            caption:
                "Minnie Coccia in her garden. Family, hard work, and homemade cooking have been at the heart of Coccia House since the beginning.",
            layout: "right"
        },

        {
            id: "family-business",
            eyebrow: "More Than a Restaurant",
            title: "A Family Around the Table",
            paragraphs: [
                "In the early years, Coccia House was more than a business—it was an extension of home.",
                "After the last customers left and the work was finished, family members often gathered around the table to share a meal. Long days in the restaurant ended the same way they had begun: together."
            ],
            image: afterShiftDinner,
            alt: "The Coccia family eating dinner together after a restaurant shift",
            caption:
                "Family members, including Minnie and Jeannette, sharing a meal after a day of work at the restaurant.",
            layout: "feature"
        },

        {
            id: "pizza-bar",
            eyebrow: "The Early Years",
            title: "The Original Pizza Bar",
            paragraphs: [
                "During the restaurant's first years, guests could watch pizzas being prepared at the original pizza bar.",
                "Each pizza was shaped, topped, and baked by hand. The room, equipment, and faces have changed over time, but that hands-on tradition remains part of every Coccia House pizza."
            ],
            image: earlyYears,
            alt: "Family members preparing pizzas at the original Coccia House pizza bar",
            caption:
                "Preparing pizzas at the original Coccia House pizza bar during the restaurant's early years.",
            layout: "left"
        },

        {
            id: "staff-1968",
            year: "1968",
            eyebrow: "Growing Together",
            title: "The People Behind the Tradition",
            paragraphs: [
                "By 1968, only ten years after opening, Coccia House had grown from a new family restaurant into a close-knit team.",
                "Family members and employees worked side by side, helping establish the quality, hospitality, and familiar atmosphere that guests continue to value today."
            ],
            image: staff1968,
            alt: "Coccia House staff gathered together in August 1968",
            caption:
                "The Coccia House staff in August 1968. Together, they helped build a lasting Wooster tradition.",
            layout: "feature"
        },

        {
            id: "next-generation",
            eyebrow: "The Next Generation",
            title: "Jeannette and Joseph Calabria",
            paragraphs: [
                "The restaurant continued with Minnie's daughter Jeannette and son-in-law Joseph Calabria.",
                "Together, they carried forward the family's recipes, work ethic, and welcoming hospitality while helping Coccia House become a familiar gathering place for generations of Wooster families."
            ],
            image: jeanetteJoe,
            alt: "Jeannette and Joseph Calabria together",
            caption:
                "Jeannette and Joseph Calabria, continuing the family tradition begun by Minnie Coccia.",
            layout: "right"
        },

        {
            id: "jeff",
            eyebrow: "Passing It Forward",
            title: "A Tradition Shared Across Generations",
            paragraphs: [
                "As the years passed, another generation became part of the restaurant's daily life.",
                "Jeannette's nephew Jeff Raynor joined the family tradition, helping preserve the recipes, character, and relationships that customers had come to cherish."
            ],
            image: jeffJeanette,
            alt: "Jeff Raynor seated with his aunt Jeannette Calabria",
            caption:
                "Jeff Raynor with his aunt, Jeannette Calabria.",
            layout: "left"
        },

        {
            id: "today",
            eyebrow: "Coccia House Today",
            title: "Still Family Owned and Operated",
            paragraphs: [
                "Today, Coccia House is operated by Karen and Steve Calabria along with their cousin Jeff Raynor.",
                "Together, they continue the family tradition that began in 1958, preserving the recipes, personal hospitality, and unmistakable character that have made the restaurant part of the Wooster community."
            ],
            image: steveKaren,
            alt: "Steve and Karen Calabria working together at Coccia House",
            caption:
                "Steve and Karen Calabria continuing the Coccia House family tradition.",
            layout: "right"
        },

        {
            id: "fresh-dough",
            eyebrow: "Made in Our Kitchen",
            title: "Fresh Dough Every Day",
            paragraphs: [
                "Every Coccia House pizza begins with dough prepared fresh daily in our kitchen.",
                "Our dough is never frozen. Each batch is made with care so every pizza carries the flavor and texture generations of customers have come to expect."
            ],
            image: freshDough,
            alt: "Steve Calabria beside the dough mixer in the Coccia House kitchen",
            caption:
                "Steve Calabria beside the kitchen mixer used to prepare fresh dough.",
            layout: "left"
        },

        {
            id: "worth-the-wait",
            eyebrow: "Old-World Methods",
            title: "Worth the Wait",
            paragraphs: [
                "Our pizzas take about 40 minutes to cook properly and may take a little longer when we are busy.",
                "We have never believed in rushing a pizza out of the oven before it is ready. Maintaining the quality and consistency of our old-world recipe is worth the wait."
            ],
            image: kitchenTeam,
            alt: "The Coccia House kitchen team standing beside freshly baked pizzas",
            caption:
                "The Coccia House kitchen team with pizzas fresh from the oven.",
            layout: "right"
        },

        {
            id: "bar",
            eyebrow: "A Place to Gather",
            title: "Hospitality Beyond the Dining Room",
            paragraphs: [
                "Along with our dining room, a full-service bar is available for guests.",
                "For generations, the bar has offered friends, neighbors, and familiar faces a comfortable place to share a drink, exchange stories, and enjoy the welcoming atmosphere of Coccia House."
            ],
            image: steveJoeBar,
            alt: "Joseph and Steve Calabria standing behind the Coccia House bar",
            caption:
                "Two generations behind the bar: Joseph and Steve Calabria.",
            layout: "left"
        }
    ],

    community: {
        eyebrow: "A Tradition Shared",
        title: "The Memories Made Around Our Tables",
        paragraphs: [
            "For more than six decades, customers have returned for family dinners, celebrations, college visits, and everyday meals.",
            "We have watched children grow into parents, parents become grandparents, and new generations discover the same recipes their families have enjoyed for decades.",
            "Seeing lifelong friends and generations of families come through our doors remains one of the greatest compliments we could receive."
        ],

        photos: [
            {
                src: customerOne,
                alt: "Karen with her godchildren, sisters Jenna and Bianca",
                caption: "Karen with her godchildren, Jenna and Bianca."
            },
            {
                src: customerTwo,
                alt: "Lifelong friends gathered around a table at Coccia House",
                caption: "Lifelong friendships."
            },
            {
                src: customerThree,
                alt: "Friends gathered together around a table at Coccia House",
                caption: "Friends gathering around the table."
            },
            {
                src: customerFour,
                alt: "Generations of family and friends sharing time together at Coccia House",
                caption: "From one generation to the next."
            },
            {
                src: customerFive,
                alt: "Longtime friends enjoying time together at Coccia House",
                caption: "A place to feel at home."
            },
            {
                src: customerSix,
                alt: "Family and friends celebrating together at Coccia House",
                caption: "A place to celebrate."
            },
            {
                src: customerSeven,
                alt: "Smiling guests enjoying a visit to Coccia House",
                caption: "We can’t wait to welcome you."
            }
        ]
    },

    closing: {
        eyebrow: "From Our Family to Yours",
        title: "Thank You for Being Part of Our Story",
        paragraphs: [
            "For more than six decades, Coccia House has been built on family, tradition, and the friendships we have shared with the Wooster community.",
            "Whether this is your first visit or you have been coming to Coccia House for generations, thank you for being part of our story."
        ],
        quote:
            "For nearly seventy years, the faces have changed—but the welcome has remained the same.",
        image: welcomeSign,
        alt: "Joseph and Jeannette Calabria standing beside the Coccia House welcome sign",
        caption:
            "Joseph and Jeannette Calabria welcoming guests to Coccia House."
    }
}