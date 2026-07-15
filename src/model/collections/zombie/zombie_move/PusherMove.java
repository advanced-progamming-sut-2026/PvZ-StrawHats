package model.collections.zombie.zombie_move;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.collections.zombie.zombie_pushing_item.PushableStructure;
import model.match_mechanisms.vector.Position;
import model.pitches.Cell;
import model.pitches.LawnMower;
import util.GameSession;

import java.util.List;

public class PusherMove implements MoveBehavior {
    private static final double PUSH_GAP_LIMIT = 1.1;

    @Override
    public void move(Zombie zombie, double deltaTime, GameSession session) {
        Position pos = zombie.getPosition();
        LawnMower lawn = session.getLawn();
        if (pos == null || lawn == null || zombie.getSpeed() == null) return;

        double deltaX = zombie.getSpeed().x() * deltaTime;
        double targetZombieX = pos.x() + deltaX;
        int currentRow = (int) pos.y();

        Row row = lawn.getRow(currentRow);
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

        if (zombie.getPosition().x() < 0) {
            session.onZombieReachedEnd();
        }
    }

    private List<GridObstacleMapping> detectPushableStructures(Row row, Position pos) {
        List<GridObstacleMapping> list = new ArrayList<>();
        for (Cell cell : row.getCells()) {
            var structure = cell.getInteractableStructure();
            if (structure instanceof PushableStructure ps && structure.isAlive()) {
                double structX = ps.getPosition().x();
                if (structX < pos.x() && (pos.x() - structX) <= PUSH_GAP_LIMIT) {
                    list.add(new GridObstacleMapping(cell, ps));
                }
            }
        }
        return list;
    }

    private record GridObstacleMapping(Cell cell, PushableStructure structure) {}
}