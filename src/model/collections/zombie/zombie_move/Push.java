package model.collections.zombie.zombie_move;

import model.collections.plant.Plant;
import model.collections.zombie.Zombie;
import model.match_mechanisms.vector.Position;
import model.pitches.Row;
import model.pitches.Square;
import service.GameClock;
import util.GameSession;

import java.util.ArrayList;
import java.util.List;

public class Push implements MoveBehavior {
    private static final double PUSH_RANGE = 1.1;

    @Override
    public void move(Zombie zombie, GameSession session) {
        Position pos = zombie.getPosition();
        if (pos == null || session.getLawn() == null) return;

        double deltaX = zombie.getSpeed().x() * GameClock.SECONDS_PER_TICK;
        double targetZombieX = pos.x() + deltaX;
        int currentRow = (int) pos.y();

        Row row = session.getLawn().getRow(currentRow);
        if (row != null) {
            List<CellStructureMapping> itemsToPush = getCellStructureMappings(row, pos);

            for (CellStructureMapping mapping : itemsToPush) {
                Square oldCell = mapping.cell;
                PushableStructure targetStructure = mapping.structure;

                double nextStructX = targetStructure.getPosition().x() + deltaX;
                int oldCol = oldCell.getCol();
                int newCol = (int) nextStructX;

                targetStructure.setPosition(Vec2.of(nextStructX, targetStructure.getPosition().y()));

                if (oldCol != newCol && newCol >= 0) {
                    Square newCell = session.getLawn().getCell(currentRow, newCol);
                    if (newCell != null) {
                        Plant targetPlant = newCell.getPlant();
                        if (targetPlant != null && targetPlant.isAlive()) {
                            targetPlant.takeDamage(targetPlant.getHp(),zombie);
                        }

                        newCell.setStructure(targetStructure);
                        oldCell.setStructure(null);
                    }
                }
            }
        }

        zombie.setPosition(Position.of(targetZombieX, pos.y()));

        if (zombie.getPosition().x() < 0) {
            session.onZombieReachedEnd();
        }
    }

    private static List<CellStructureMapping> getCellStructureMappings(Row row, Position pos) {
        List<CellStructureMapping> itemsToPush = new ArrayList<>();

        for (Cell cell : row.getCells()) {
            var structure = cell.getInteractableStructure();
            if (structure instanceof PushableStructure ps && structure.isAlive()) {
                double structX = ps.getPosition().x();

                if (structX < pos.x() && (pos.x() - structX) <= PUSH_RANGE) {
                    itemsToPush.add(new CellStructureMapping(cell, ps));
                }
            }
        }
        return itemsToPush;
    }

    private record CellStructureMapping(Cell cell, PushableStructure structure) {
    }
