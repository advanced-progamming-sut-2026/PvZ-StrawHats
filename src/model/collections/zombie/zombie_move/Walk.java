package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.pitches.LawnMower;
import model.pitches.Square;
import model.pitches.Tile;
import service.GameClock;
import util.GameSession;

public class Walk implements MoveBehavior {

    @Override
    public void move(Zombie zombie, GameSession session) {
        Position pos = zombie.getPosition();
        Position vel = zombie.getSpeed();

        Position newPos = pos.add(vel.scale(GameClock.SECONDS_PER_TICK));

        int oldCol = (int) pos.x();
        int newCol = (int) newPos.x();
        if (newCol != oldCol) {
            newPos = applySlipperyShift(newPos, session);
        }

        zombie.setPosition(newPos);

        if (newPos.x() < 0) {
            session.onZombieReachedEnd();
        }
    }

    private Position applySlipperyShift(Position pos, GameSession session) {
        LawnMower lawn = session.getLawn();
        if (lawn == null) return pos;

        int row = (int) pos.y();
        int col = (int) pos.x();
        Square cell = lawn.getCell(row, col);
        if (cell == null || cell.getTile() == null) return pos;

        Tile tile = cell.getTile();
        if (tile.getType() != TileType.Slippery || tile.getSlipperyDirection() == null) {
            return pos;
        }

        double rowDelta = tile.getSlipperyDirection() == Tile.SlipperyDirection.UP ? -1.0 : 1.0;
        if (rowDelta < 0 || rowDelta >= lawn.getRows()) return pos;

        return new Position(pos.x(), rowDelta);
    }
}
