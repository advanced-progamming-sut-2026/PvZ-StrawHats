package model.match_mechanisms.sun;

import model.match_mechanisms.Time;
import model.match_mechanisms.vector.Position;

import java.util.Random;

public class SunFactory {

    private double creationDelay;
    private final Random random = new Random();
    int skySunFallDuration = 5;

    public void startFactory() {
        creationDelay = Math.max(6 + 0.05 * Time.getTick(), 12d);
    }

    public Sun createSkySun() {
        Sun sun;

        int chance = random.nextInt(100);
        if (chance < 80)
            sun = new NormalSun();
        else if (chance < 95)
            sun = new SpecialSun();
        else
            sun = new RadioactivateSun();

        double colOfCreation = random.nextInt(0, 10);
        sun.position = new Position(colOfCreation, 0d);

        Position fallingPosition = new Position(random.nextDouble(0, 5), colOfCreation);
        sun.setFallPosition(fallingPosition);

        sun.setFallSpeed((sun.getFallPosition().y() - sun.position.y()) / skySunFallDuration);

        return sun;
    }

    public Sun createPlantSun(int plantX, int plantY) {
        Sun sun = new NormalSun();
        sun.position = new Position(plantX, plantY);

        double fallingPositionRow = random.nextInt(getOrigin(plantX), plantX + 1);
        double fallingPositionCol = random.nextDouble((plantY >= 1) ? plantY - 1 : 0, (plantY < 9) ? plantY + 2 : 10);
        Position fallingPosition = new Position(fallingPositionCol, fallingPositionRow);
        sun.setFallPosition(fallingPosition);

        return sun;
    }

    private static int getOrigin(int plantX) {
        return (plantX >= 1) ? plantX - 1 : 0;
    }

    public double getCreationDelay() {
        return creationDelay;
    }

    public void setCreationDelay(double creationDelay) {
        this.creationDelay = creationDelay;
    }
}
