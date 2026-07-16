package model.match.main.season.travellog.cave;

import model.collections.zombie.Zombie;
import model.match.main.season.Season;
import java.util.ArrayList;

public class Cave extends Season {
    public ArrayList<Zombie> frozenZombies = new ArrayList<>();

    public Cave() {
        super("Frostbite Caves");
    }

    @Override
    public boolean hasIceTiles() { return true; }

    /**
     * Thaws a single frozen zombie (e.g. once its ice has taken enough damage,
     * or a fire plant has been sitting next to it long enough). Callers are
     * responsible for deciding *when* a zombie has thawed; this just applies it.
     */
    public static void meltIce(Zombie zombie) {
        if (zombie == null) return;
        if (zombie.getStatus() == Zombie.Status.FREEZE) {
            zombie.setStatus(Zombie.Status.NORMAL);
        }
    }
}