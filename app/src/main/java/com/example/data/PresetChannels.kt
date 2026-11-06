package com.example.data

import com.example.R
import com.example.model.PollOption
import com.example.model.TvChannel
import com.example.model.TvScene

object PresetChannels {
    fun getDefaultChannels(): List<TvChannel> {
        return listOf(
            TvChannel(
                id = "ch_03",
                channelNumber = 3,
                name = "CH 03 - Neo-Tokyo 2099 News",
                genre = "Cyberpunk Sci-Fi News",
                hostName = "Bradley Steele",
                hostTitle = "Chief Interplanetary Anchor",
                hostEmoji = "🎙️",
                promptContext = "High-octane futuristic news broadcast covering rogue cyborgs, space elevators, alien diplomacy, and cyber conspiracies with satirical charisma.",
                backdropRes = R.drawable.tv_channel_news,
                currentScene = TvScene(
                    title = "Lunar Helium Refinery Crisis",
                    sceneDescription = "Bradley sits at a glowing holographic desk while neon news sirens flash across the studio monitors.",
                    speaker = "Bradley Steele",
                    dialogue = "Good evening Earth, Mars, and orbital colonies. Breaking news tonight as an automated robot workforce has declared independence on the Sea of Tranquility!",
                    choices = listOf(
                        "Interview the rebel android leader live via holocall",
                        "Dispatch the emergency news drone into the crater",
                        "Check the sudden panic on the orbital stock exchange"
                    ),
                    visualMood = "Neon Cyberpunk",
                    pollQuestion = "Should the lunar council grant the robots citizenship?",
                    pollOptions = listOf(
                        PollOption(1, "Yes, free the droids!", 642),
                        PollOption(2, "No, check their firmware", 389)
                    ),
                    breakingNewsTicker = "BREAKING /// MOON TRANSIT SHUTTLE GROUNDED /// SYNTHETIC COFFEE SHORTAGE IN ORBIT"
                )
            ),
            TvChannel(
                id = "ch_07",
                channelNumber = 7,
                name = "CH 07 - Quantum Kitchen Arena",
                genre = "Sci-Fi Culinary Showdown",
                hostName = "Chef Zargon",
                hostTitle = "Intergalactic 5-Star Chef",
                hostEmoji = "🍳",
                promptContext = "Fast-paced, hyper-dramatic intergalactic cooking competition where ingredients have dangerous quantum properties, strange physics, and delicious flavors.",
                backdropRes = R.drawable.tv_channel_cosmic_kitchen,
                currentScene = TvScene(
                    title = "The Nebula Soufflé Time Bomb",
                    sceneDescription = "Chef Zargon frantically stirs a bowl of glowing purple antimatter batter hovering between two magnetic field coils.",
                    speaker = "Chef Zargon",
                    dialogue = "If you don't fold the cosmic plasma at exactly 4000 RPM, the entire soufflé collapses into a micro-singularity! Where did my assistant put the neutron butter?!",
                    choices = listOf(
                        "Hand Zargon the ultra-spicy supernova chili pepper",
                        "Sabotage the rival chef's zero-gravity spatula",
                        "Crank the hyper-plasma convection oven to maximum heat"
                    ),
                    visualMood = "Cosmic Kitchen",
                    pollQuestion = "Would you taste the vibrating Nebula Soufflé?",
                    pollOptions = listOf(
                        PollOption(1, "Give me a huge spoonful!", 720),
                        PollOption(2, "I prefer to keep all my atoms", 215)
                    ),
                    breakingNewsTicker = "LIVE ARENA /// 4 MINUTES REMAINING ON THE QUANTUM CLOCK /// AUDIENCE WEARING BLAST SHIELDS"
                )
            ),
            TvChannel(
                id = "ch_12",
                channelNumber = 12,
                name = "CH 12 - Midnight Noir: Stone Detective",
                genre = "Hardboiled Crime Mystery",
                hostName = "Jack Stone",
                hostTitle = "Private Investigator",
                hostEmoji = "🕵️‍♂️",
                promptContext = "Gritty 1940s detective noir with cynical narration, smoke in the air, jazz saxophone melodies in the rain, femme fatales, and corrupt city conspiracies.",
                backdropRes = R.drawable.tv_channel_detective_noir,
                currentScene = TvScene(
                    title = "The Dame with the Microfilm",
                    sceneDescription = "Rain hammers the fogged precinct window as streetlights cut through the Venetian blinds.",
                    speaker = "Jack Stone",
                    dialogue = "She slid into my office smelling of cheap rain and expensive perfume. She slid a rusty padlock key across my desk and whispered: 'Stone, they're erasing people.'",
                    choices = listOf(
                        "Examine the encrypted serial number on the key",
                        "Pour two glasses of scotch and demand who hired her",
                        "Check the shadowy alley out the window for tailing hitmen"
                    ),
                    visualMood = "Shadow Noir",
                    pollQuestion = "Can Stone trust this mysterious informant?",
                    pollOptions = listOf(
                        PollOption(1, "She's setting him up!", 580),
                        PollOption(2, "She's genuinely terrified", 430)
                    ),
                    breakingNewsTicker = "POLICE DISPATCH: 10-33 CALL NEAR DOCK 4 /// SAXOPHONE SOLO ECHOES IN THE RAIN"
                )
            ),
            TvChannel(
                id = "ch_24",
                channelNumber = 24,
                name = "CH 24 - Saturday Toon: Captain Cosmo",
                genre = "90s Retro Animated Adventure",
                hostName = "Captain Cosmo",
                hostTitle = "Defender of the Galaxy",
                hostEmoji = "🚀",
                promptContext = "Energetic, vibrant Saturday morning cartoon full of slapstick humor, wild cartoon sound effects, heroic monologues, and ridiculous super-villain inventions.",
                backdropRes = null,
                currentScene = TvScene(
                    title = "Dr. Doom-Bot's Giant Helium Ray",
                    sceneDescription = "A cartoon city skyline wobbles comically as a giant cartoon blimp hovers overhead.",
                    speaker = "Captain Cosmo",
                    dialogue = "Hold onto your cereal bowls, Cadets! Doctor Doom-Bot has turned the downtown skyscrapers into giant inflatable bouncy castles!",
                    choices = listOf(
                        "Unleash the Cosmic Whoopee Cushion Cannon",
                        "Challenge Doom-Bot to an intergalactic dance-off",
                        "Call in Pip the Robotic Hamster for backup"
                    ),
                    visualMood = "Retro Cartoon",
                    pollQuestion = "Which wacky gadget should Cosmo activate?",
                    pollOptions = listOf(
                        PollOption(1, "Hypno-Yo-Yo", 610),
                        PollOption(2, "Mega Jet-Boots", 490)
                    ),
                    breakingNewsTicker = "CARTOON ALERT: GRAVITY HAS BEEN TEMPORARILY SUSPENDED FOR FUN"
                )
            ),
            TvChannel(
                id = "ch_42",
                channelNumber = 42,
                name = "CH 42 - Xenoverse Wildlife Safari",
                genre = "Poetic Cosmic Nature Documentary",
                hostName = "Sir David Star-Borough",
                hostTitle = "Master Exo-Naturalist",
                hostEmoji = "🌿",
                promptContext = "Calm, deeply poetic, awe-inspiring nature documentary narrating fantastical alien ecosystems, strange glowing wildlife, and delicate interplanetary balances.",
                backdropRes = null,
                currentScene = TvScene(
                    title = "The Bioluminescent Spore-Whales of Titan",
                    sceneDescription = "Ethereal translucent creatures drift gracefully through shimmering emerald atmospheric fog.",
                    speaker = "Sir David Star-Borough",
                    dialogue = "Notice how the alpha matriarch pulses her deep cobalt fins, singing at forty megahertz to guide her newborn calves through the liquid nitrogen geysers...",
                    choices = listOf(
                        "Deploy the silent optical sub-drone closer to the herd",
                        "Record the ethereal harmonic mating songs",
                        "Observe the unexpected approach of a Crystal Apex Predator"
                    ),
                    visualMood = "Cosmic Wonder",
                    pollQuestion = "Will the newborn calf avoid the methane vortex?",
                    pollOptions = listOf(
                        PollOption(1, "The mother will protect it", 890),
                        PollOption(2, "Nature is unpredictable", 180)
                    ),
                    breakingNewsTicker = "TITAN EXPEDITION CAM 04 /// OPTICAL SENSORS REGISTER 98% CLARITY"
                )
            ),
            TvChannel(
                id = "ch_77",
                channelNumber = 77,
                name = "CH 77 - SynthWave Retro MTV",
                genre = "80s Cyber Lo-Fi Music & Narrative",
                hostName = "DJ Neon Vibe",
                hostTitle = "Midnight Midnight Host",
                hostEmoji = "🎧",
                promptContext = "Vaporwave and synthwave music television channel with neon wireframe grids, cruising sports cars, cassette tape aesthetics, and moody late-night vibes.",
                backdropRes = null,
                currentScene = TvScene(
                    title = "Midnight Cruise on Outrun Highway",
                    sceneDescription = "A cherry red sports car blazes down an endless grid towards a gigantic pink wireframe sunset.",
                    speaker = "DJ Neon Vibe",
                    dialogue = "You're listening to Neon 107.5 FM on your television set. The city behind you is sleeping, but out here on the digital highway, time doesn't exist.",
                    choices = listOf(
                        "Engage Twin-Turbo Nitro boost into the horizon",
                        "Tune into the secret encrypted pirate frequency",
                        "Pull over at the glowing 24-hour holographic diner"
                    ),
                    visualMood = "Vaporwave Neon",
                    pollQuestion = "Which tape cassette to pop into the deck?",
                    pollOptions = listOf(
                        PollOption(1, "Ultra Darkwave Synth", 520),
                        PollOption(2, "Chill Lo-Fi Chillwave", 670)
                    ),
                    breakingNewsTicker = "CASSETTE TAPE INSERTED /// 120 BPM /// STEREO SYNCHRONIZED"
                )
            )
        )
    }
}
