package model.collections.item;

import model.collections.Item;
import model.match_mechanisms.vector.Position;
import model.user_data.UserState;
import model.utils.GameSession;
import service.GameClock;

public abstract class GroundItem extends Item {
    private final ItemType itemType;
    private final double lifetimeSeconds;
    private final double collectRadius;
    private boolean collected = false;
    private double aliveSeconds = 0;

    protected GroundItem(ItemType itemType, Position position, double lifetimeSeconds, double collectRadius) {
        super(position, 1);
        this.itemType = itemType;
        this.lifetimeSeconds = lifetimeSeconds;
        this.collectRadius = collectRadius;
        setPosition(position);
    }

    public abstract void applyRewards(GameSession session, UserState state);

    public void collect(GameSession session, UserState state) {
        if (collected || !isAlive()) return;
        applyRewards(session, state);
        collected = true;
        setAlive(false);
    }

    public boolean isNear(Position target) {
        Position here = getPosition();
        if (here == null || target == null) return false;
        return Math.abs(here.x() - target.x()) <= collectRadius
                && Math.abs(here.y() - target.y()) <= collectRadius;
    }

    @Override
    public void tick() {
        aliveSeconds += service.GameClock.SECONDS_PER_TICK;
        if (!collected && lifetimeSeconds > 0 && GameClock.hasReached(aliveSeconds, lifetimeSeconds)) {
            setAlive(false);
        }
    }

    public ItemType getItemType() {
        return itemType;
    }

    public boolean isCollected() {
        return collected;
    }

    public double getLifetimeSeconds() {
        return lifetimeSeconds;
    }

    public double getCollectRadius() {
        return collectRadius;
    }
}
