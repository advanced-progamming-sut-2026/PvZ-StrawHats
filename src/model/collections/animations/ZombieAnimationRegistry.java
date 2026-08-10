package model.collections.animations;

import java.util.Map;

/**
 * Maps a zombie's Zombie.json alias (the key {@link model.collections.zombie.ZombieFactory}
 * creates zombies from, e.g. "ZombieDefault") to its animation name in animations.json
 * (e.g. "ZOMBIE_TUTORIAL"). Zombies can't be resolved by normalizing a display name the way
 * plants can - Zombie.json only has internal aliases, no display names, and several worlds
 * reuse/rename basic zombies in non-obvious ways - so this is a hand-verified table instead.
 * <p>
 * Verified against all 31 aliases currently in Zombie.json using each alias's
 * ZombieArmorProps/Size fields plus the clip lists in animations.json (e.g. ZOMBIE_TUTORIAL's
 * clip set - idle/walk/eat/die/particles - matches every other "basic zombie" entry, confirming
 * it's the base Zombie).
 */
public class ZombieAnimationRegistry {

    /** High confidence: alias identity or ZombieArmorProps/Size directly confirms the mapping. */
    private static final Map<String, String> VERIFIED = Map.ofEntries(
            Map.entry("ZombieDefault", "ZOMBIE_TUTORIAL"),
            Map.entry("ZombieImp", "ZOMBIE_TUTORIAL_IMP"),
            Map.entry("ZombieNewspaper", "ZOMBIE_MODERN_NEWSPAPER"),
            Map.entry("ZombieGargantuar", "GARGANTUAR"),
            Map.entry("ZombieRa", "ZOMBIE_EGYPT_RA"),
            Map.entry("ZombieExplorer", "ZOMBIE_EXPLORER"),
            Map.entry("ZombieTombRaiser", "ZOMBIE_EGYPT_TOMBRAISER"),
            Map.entry("ZombieIceAgeDodo", "ZOMBIE_ICEAGE_DODORIDER"),
            Map.entry("ZombieIceAgeHunter", "ZOMBIE_ICEAGE_HUNTER"),
            Map.entry("ZombieIceAgeTroglobite", "ZOMBIE_ICEAGE_TROGLOBITE"),
            Map.entry("ZombieBeachFisherman", "ZOMBIE_BEACH_FISHERMAN"),
            Map.entry("ZombieBeachOctopus", "ZOMBIE_BEACH_OCTOPUS"),
            Map.entry("ZombieBeachSnorkel", "ZOMBIE_BEACH_SNORKELER"),
            Map.entry("ZombieDarkKing", "ZOMBIE_DARK_KING"),
            Map.entry("ZombieDarkImpDragon", "ZOMBIE_DARK_IMP_DRAGON"),
            Map.entry("ZombieModernAllStar", "ZOMBIE_MODERN_ALLSTAR"),
            Map.entry("ZombieLostCityJane", "ZOMBIE_LOSTCITY_JANE"),
            Map.entry("ZombieCrystalSkull", "ZOMBIE_LOSTCITY_CRYSTALSKULL"),
            Map.entry("ZombieProspector", "ZOMBIE_PROSPECTOR"),
            Map.entry("ZombiePiano", "ZOMBIE_PIANO"),
            Map.entry("ZombieArcade", "ZOMBIE_80S_ARCADE"),

            // Zombotany-style disguised zombies reuse the plant's own PAM - same asset, zombie AI.
            Map.entry("ZombiePeashooter", "PEASHOOTER"),
            Map.entry("ZombieWallnut", "WALLNUT"),
            Map.entry("ZombieJalapeno", "JALAPENO"),
            Map.entry("ZombieSquash", "SQUASH"),

            // ZombieArmor1/2/4 carry Cone/Bucket/Brick via ZombieArmorProps, but there's no
            // separate "wearing a cone" full-body animation in this pack - only ZOMBIE_TUTORIAL.
            // The armor itself isn't PAM data here; render it as a separate icon/overlay using
            // ArmorTypeData.json, same as the HP-only data ZombieFactory already reads from it.
            Map.entry("ZombieArmor1", "ZOMBIE_TUTORIAL"),
            Map.entry("ZombieArmor2", "ZOMBIE_TUTORIAL"),
            Map.entry("ZombieArmor4", "ZOMBIE_TUTORIAL")
    );

    /**
     * Medium confidence: best available match by theme/world, but not confirmed the way
     * {@link #VERIFIED} entries are (multiple similarly-named candidates exist, e.g. Dark Ages
     * Wizard vs. Easter Wizard vs. Sportzball Wizard). Worth a visual check before shipping.
     */
    private static final Map<String, String> BEST_GUESS = Map.of(
            "ZombieWizard", "ZOMBIE_DARK_WIZARD"
    );

    /**
     * No matching entry anywhere in animations.json - checked exhaustively, not just missed
     * by a naming mismatch. You'll need art from elsewhere for these:
     * - ZombieDarkArmor3 (shoulder armor + crown - closest engine concept is
     *   {@code ZombieFactory.createKnightArmor()}, but no "knight" PAM exists in this pack)
     * - ZombieDarkJuggler (no "juggler" entry at all)
     */
    private static final java.util.Set<String> NOT_FOUND = java.util.Set.of(
            "ZombieDarkArmor3", "ZombieDarkJuggler"
    );

    public static AnimationJsonParser.AnimationConfig resolve(String zombieAlias) {
        if (zombieAlias == null || NOT_FOUND.contains(zombieAlias)) return null;

        String animationName = VERIFIED.get(zombieAlias);
        if (animationName == null) animationName = BEST_GUESS.get(zombieAlias);
        if (animationName == null) return null;

        return AnimationFactory.get(animationName);
    }

    public static String pathFor(String zombieAlias) {
        AnimationJsonParser.AnimationConfig config = resolve(zombieAlias);
        return config == null ? null : config.path;
    }

    /** True only for entries hand-verified against ZombieArmorProps/Size/clip data. */
    public static boolean isVerified(String zombieAlias) {
        return VERIFIED.containsKey(zombieAlias);
    }
}
