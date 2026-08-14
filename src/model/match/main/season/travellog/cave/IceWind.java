package model.match.main.season.travellog.cave;

import model.collections.plant.Plant;
import model.pitches.Cell;
import model.pitches.Environment;
import model.utils.GameSession;
import view.GeneralPrinter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class IceWind {
    private static final Random RANDOM = new Random();

    private IceWind() { }

    public static int blow(GameSession session) {
        if (session == null || session.getEnvironment() == null) return 0;
        Environment environment = session.getEnvironment();
        int maximumAffectedRows = Math.max(1, (environment.getRows() + 1) / 2);
        int affectedRowCount = 1 + RANDOM.nextInt(maximumAffectedRows);

        List<Integer> rows = new ArrayList<>();
        for (int row = 0; row < environment.getRows(); row++) rows.add(row);
        Collections.shuffle(rows, RANDOM);
        rows = rows.subList(0, Math.min(affectedRowCount, rows.size()));

        int affectedPlants = 0;
        for (int row : rows) {
            for (int col = 0; col < environment.getCols(); col++) {
                Cell cell = environment.getCell(row, col);
                Plant plant = cell == null ? null : cell.getPlant();
                if (FrostbiteFreezing.addChillLevel(session, plant)) affectedPlants++;
            }
        }

        String laneText = rows.stream()
                .sorted()
                .map(row -> Integer.toString(row + 1))
                .reduce((left, right) -> left + ", " + right)
                .orElse("-");
        GeneralPrinter.print("Ice Wind struck lane(s) " + laneText
                + " and chilled " + affectedPlants + " plant(s).");
        return affectedPlants;
    }
}

