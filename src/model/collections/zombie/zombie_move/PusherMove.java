package model.collections.zombie.zombie_move;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.collections.zombie.zombie_pushing_item.PushableStructure;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.Environment;
import model.utils.GameSession;

import java.util.ArrayList;
import java.util.List;

public class PusherMove implements MoveBehavior {
    private static final double PUSH_GAP_LIMIT = 1.1;

    @Override
    public void move(Zombie zombie, double deltaTime, GameSession session) {
        Position pos = zombie.getPosition();
        Environment lawn = session.getLawn();
        if (pos == null || lawn == null || zombie.getSpeed() == null) return;

        double deltaX = zombie.getSpeed().x() * deltaTime;
        double targetZombieX = pos.x() + deltaX;
        int currentRow = (int) pos.y();

        Cell[] row = lawn.getRowCells(currentRow);
        if (row != null) {
            List<GridObstacleMapping> obstaclesToPush = detectPushableStructures(row, pos);

            for (GridObstacleMapping map : obstaclesToPush) {
                Cell currentCell = map.cell();
                PushableStructure targetStructure = map.structure();

                double nextStructureX = targetStructure.getPosition().x() + deltaX;
                int oldCol = currentCell.getCol();
                int newCol = (int) nextStructureX;

                targetStructure.setPosition(new Position(nextStructureX, targetStructure.getPosition().y()));

                if (oldCol != newCol && newCol >= 0) {
                    Cell nextCell = lawn.getCell(currentRow, newCol);
                    if (nextCell != null) {
                        Plant plantInWay = nextCell.getPlant();
                        if (plantInWay != null && plantInWay.isAlive()) {
                            plantInWay.takeDamage(plantInWay.getHP(), zombie);
                        }

                        nextCell.setStructure(targetStructure);
                        currentCell.setStructure(null);
                    }
                }
            }
        }

        zombie.setPosition(new Position(targetZombieX, pos.y()));

    }

    private List<GridObstacleMapping> detectPushableStructures(Cell[] row, Position pos) {
        List<GridObstacleMapping> list = new ArrayList<>();
        for (Cell cell : row) {
            if (cell == null) continue;
            PushableStructure structure = cell.getInteractableStructure();
            if (structure != null && structure.isAlive()) {
                double structX = structure.getPosition().x();
                if (structX < pos.x() && (pos.x() - structX) <= PUSH_GAP_LIMIT) {
                    list.add(new GridObstacleMapping(cell, structure));
                }
            }
        }
        return list;
    }

    private record GridObstacleMapping(Cell cell, PushableStructure structure) {}
}
