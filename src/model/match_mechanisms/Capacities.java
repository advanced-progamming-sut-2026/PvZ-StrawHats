package model.match_mechanisms;

public class Capacities {
    private static final int PLANT_FOOD_CAPACITY = 3;

    private static int matchPlantsCapacity;

    public static int getCAPACITY() {
        return PLANT_FOOD_CAPACITY;
    }

    public static int getMatchPlantsCapacity() {
        return matchPlantsCapacity;
    }

    public static void setMatchPlantsCapacity(int matchPlantsCapacity) {
        Capacities.matchPlantsCapacity = matchPlantsCapacity;
    }
}
