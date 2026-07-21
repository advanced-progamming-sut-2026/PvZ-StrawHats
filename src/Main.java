import model.collections.plant.PlantFactory;
import model.quests.QuestLoader;
import view.AppView;

public class Main {
    public static void main(String[] args) {
        PlantFactory.autoInit();
        QuestLoader.loadTemplates("src/resource/Quest.json");
        AppView.run();
    }
}
