import model.collections.plant.PlantFactory;
import view.AppView;

public class Main {
    public static void main(String[] args) {
        PlantFactory.autoInit();
        AppView.run();
    }
}
