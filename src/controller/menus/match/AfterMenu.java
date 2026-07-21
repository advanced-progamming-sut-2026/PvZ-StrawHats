package controller.menus.match;

import controller.CollectionManager;
import controller.QuestManager;
import controller.menus.GameMenu;
import controller.menus.Menu;

import model.App;
import model.Regex;
import model.collections.plant.PlantJsonParser;
import model.match.main.levels.Level;
import model.user_data.User;
import model.user_data.UserState;
import model.utils.GameSession;

import java.util.List;
import java.util.Random;

public class AfterMenu extends Menu {
    private static final Random RANDOM = new Random();
    private static final CollectionManager MANAGER = new CollectionManager();

    private static boolean won;
    private static boolean rewardGranted = false;
    private static int coinsAwarded;
    private static String seedPacketPlantName = "None";

    public static void reset(boolean matchWon) {
        won = matchWon;
        rewardGranted = false;
    }

    public AfterMenu() {
        if (!rewardGranted) {
            grantReward();
            rewardGranted = true;
            System.out.println(showMenu());
        }
    }

    private void grantReward() {
        UserState state = User.currentUser.userState;
        Level level = GameSession.getInstance().getLevel();

        coinsAwarded = won ? 100 + RANDOM.nextInt(101) : 20 + RANDOM.nextInt(21);
        state.coins += coinsAwarded;

        List<PlantJsonParser.PlantConfig> unlocked = MANAGER.getUnlockedPlants(state);
        if (!unlocked.isEmpty()) {
            PlantJsonParser.PlantConfig picked = unlocked.get(RANDOM.nextInt(unlocked.size()));
            state.addSeedPackets(picked.id, 1);
            seedPacketPlantName = picked.name;
        } else {
            seedPacketPlantName = "None";
        }

        if (won && level != null) {
            state.recordGameResult(level.getId(), coinsAwarded);
        }

        if (won) {
            QuestManager.notifyLevelWon(GameSession.getInstance());
        }
    }

    @Override
    public String getName() {
        return "After Menu";
    }

    @Override
    public void handleCommand(String text){
    super.handleCommand(text);


        if (Regex.MENU_EXIT.getMatcherRaw(text).matches()) {
            exitMenu();
        } else {
            System.out.println("Not Valid");
        }
    }

    @Override
    public void exitMenu() {
        App.currentMenu = new GameMenu();
    }

    @Override
    public String showMenu() {
        return (won ? "YOU WIN!" : "The zombie ate your brain; LOSER !!!") +
                "\nReward: +" + coinsAwarded + " coins, +1 seed packet (" + seedPacketPlantName + ")";
    }
}
