package model.collections.zombie.zombie_move;

import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.Environment;
import model.pitches.SlipperyDirection;
import model.pitches.Tile;
import model.pitches.TileType;
import model.utils.GameSession;

public interface MoveBehavior {
    void move(Zombie zombie, double deltaTime, GameSession session);

    default Position applySlipperyShift(Position pos, GameSession session) {
        Environment lawn = session.getLawn();
        if (lawn == null) return pos;

        int row = (int) pos.y();
        int col = (int) pos.x();
        Cell cell = lawn.getCell(row, col);
        if (cell == null || cell.getTile() == null) return pos;

        Tile tile = cell.getTile();
        if (tile.getType() != TileType.Slippery || tile.getSlipperyDirection() == null) {
            return pos;
        }

        double rowDelta = (tile.getSlipperyDirection() == SlipperyDirection.UP) ? -1.0 : 1.0;
        double newRow = row + rowDelta;

        if (newRow < 0 || newRow >= lawn.getRows()) return pos;

        return new Position(pos.x(), newRow);
    }
}
