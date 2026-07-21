package model;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Regex {
    // authentication
    REGISTER(
            "^\\s*register\\s+-u\\s+(?<username>\\S+)\\s+-p\\s+(?<password>\\S+)\\s+(?<passwordConfirm>\\S+)\\s+-n\\s+(?<nickname>\\S+)\\s+-e\\s+(?<email>\\S+)\\s+-g\\s+(?<gender>\\S+)\\s*$"
    ),
    PICK_QUESTION(
            "^\\s*pick\\s+question\\s+-q\\s+(?<questionnumber>\\S+)\\s+-a\\s+(?<answer>\\S+)\\s+-c\\s+(?<answerconfirm>\\S+)\\s*$"
    ),
    LOGIN(
            "^\\s*login\\s+-u\\s+(?<username>\\S+)\\s+-p\\s+(?<password>\\S+)(?:\\s+-stay-logged-in)?\\s*$"
    ),
    FORGET_PASSWORD(
            "^\\s*forget\\s+password\\s+-u\\s+(?<username>\\S+)\\s+-e\\s+(?<email>\\S+)\\s*$"
    ),
    ANSWER(
            "^\\s*answer\\s+-a\\s+(?<answer>\\S+)\\s*$"
    ),
    MENU_LOGOUT(
            "^\\s*menu\\s+logout\\s*$"
    ),

    // main menu
    MENU_ENTER_CHAPTER(
            "^\\s*menu\\s+enter\\s+chapter\\s+-c\\s+(?<chaptername>\\S+)\\s*$"
    ),
    MENU_GREENHOUSE(
            "^\\s*menu\\s+greenhouse\\s*$"
    ),
    MENU_TRAVEL_LOG(
            "^\\s*menu\\s+travel-log\\s*$"
    ),
    MENU_LEADERBOARD(
            "^\\s*menu\\s+leaderboard\\s*$"
    ),
    MENU_COIN_WALLET(
            "^\\s*menu\\s+coin-wallet\\s*$"
    ),
    MENU_GEM_WALLET(
            "^\\s*menu\\s+gem-wallet\\s*$"
    ),
    MENU_SETTINGS_CHANGE_DIFFICULTY(
            "^\\s*menu\\s+settings\\s+change-difficulty\\s+-l\\s+(?<difficultylevel>\\S+)\\s*$"
    ),

    MENU_NEWS_SHOW_UNREAD(
            "^\\s*menu\\s+news\\s+show-unread\\s*$"
    ),
    MENU_NEWS_SHOW_ALL(
            "^\\s*menu\\s+news\\s+show-all\\s*$"
    ),
    MENU_CHEAT_ADD(
            "^\\s*menu\\s+cheat\\s+add\\s+(?<n>\\S+)\\s+(?<r>\\S+)\\s*$"
    ),
    TRAVEL_LOG_PAGE(
            "^\\s*travel\\s+log\\s+page\\s+(?<pagename>\\S+)\\s*$"
    ),

    // profile menu
    MENU_PROFILE_CHANGE_USERNAME(
            "^\\s*menu\\s+profile\\s+change-username\\s+-u\\s+(?<username>\\S+)\\s*$"
    ),
    MENU_PROFILE_CHANGE_NICKNAME(
            "^\\s*menu\\s+profile\\s+change-nickname\\s+-u\\s+(?<nickname>\\S+)\\s*$"
    ),
    MENU_PROFILE_CHANGE_EMAIL(
            "^\\s*menu\\s+profile\\s+change-email\\s+-e\\s+(?<email>\\S+)\\s*$"
    ),
    MENU_PROFILE_CHANGE_PASSWORD(
            "^\\s*menu\\s+profile\\s+change-password\\s+-p\\s+(?<newpassword>\\S+)\\s+-o\\s+(?<oldpassword>\\S+)\\s*$"
    ),
    MENU_PROFILE_SHOW_INFO(
            "^\\s*menu\\s+profile\\s+show-info\\s*$"
    ),

    // collection
    MENU_COLLECTION_SHOW_PLANTS(
            "^\\s*menu\\s+collection\\s+showplants\\s*$"
    ),
    MENU_COLLECTION_SHOW_ALL_PLANTS(
            "^\\s*menu\\s+collection\\s+show-allplants\\s*$"
    ),
    MENU_COLLECTION_SHOW_ZOMBIES(
            "^\\s*menu\\s+collection\\s+showzombies\\s*$"
    ),
    MENU_COLLECTION_SHOW_ALL_ZOMBIES(
            "^\\s*menu\\s+collection\\s+show-all-zombies\\s*$"
    ),
    MENU_COLLECTION_SHOW_PLANT(
            "^\\s*menu\\s+collection\\s+showplant\\s+-p\\s+(?<plantname>.+?)\\s*$"
    ),
    MENU_COLLECTION_SHOW_ZOMBIE(
            "^\\s*menu\\s+collection\\s+show-zombie\\s+-z\\s+(?<zombiename>.+?)\\s*$"
    ),
    MENU_COLLECTION_UPGRADE_PLANT(
            "^\\s*menu\\s+collection\\s+upgradeplant\\s+-p\\s+(?<plantname>.+?)\\s*$"
    ),
    MENU_COLLECTION_PURCHASE_PLANT(
            "^\\s*menu\\s+collection\\s+purchaseplant\\s+-p\\s+(?<plantname>.+?)\\s*$"
    ),

    // green house
    SHOW_ALL_PLANTS(
            "^\\s*show\\s+all\\s+plants\\s*$"
    ),
    SHOW_AVAILABLE_PLANTS(
            "^\\s*show\\s+available\\s+plants\\s*$"
    ),
    ADD_PLANT(
            "^\\s*add\\s+plant\\s+-t\\s+(?<type>.+?)\\s*$"
    ),
    REMOVE_PLANT(
            "^\\s*remove\\s+plant\\s+-t\\s+(?<type>.+?)\\s*$"
    ),
    BOOST_PLANT(
            "^\\s*boost\\s+plant\\s+-t\\s+(?<type>.+?)\\s*$"
    ),
    START_GAME(
            "^\\s*start\\s+game\\s*$"
    ),

    SHOW_GREENHOUSE(
            "^\\s*show\\s+greenhouse\\s*$"
    ),
    PLANT_POT_AT(
            "^\\s*plant\\s+pot\\s+at\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"
    ),
    COLLECT_POT(
            "^\\s*collect\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"
    ),
    GROW_POT(
            "^\\s*grow\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"
    ),
    ENTER_SHOP(
            "^\\s*enter\\s+shop\\s*$"
    ),
    SHOPPING_LIST(
            "^\\s*shopping\\s+list\\s*$"
    ),
    SHOP_DAILY(
            "^\\s*shop\\s+daily\\s*$"
    ),
    SHOP_BUY(
            "^\\s*shop\\s+buy\\s+-i\\s+(?<itemid>\\S+)\\s+-n\\s+(?<count>\\d+)(?:\\s+-t\\s+(?<planttype>.+?))?\\s*$"
    ),

    // in 3 ta hameja hastan vali handeleshoon fargh dare moragheb bashid toye override ha sooti nadid
    MENU_ENTER(
            "^\\s*menu\\s+enter\\s+(?<menuname>.+?)\\s*$"
    ),
    MENU_SHOW_CURRENT(
            "^\\s*menu\\s+show\\s+current\\s*$"
    ),
    ADVANCE_TIME(
            "^\\s*advance\\s+time\\s+-t\\s+(?<ticks>\\d+)\\s+ticks?\\s*$"
    ),
    SHOW_MAP(
            "^\\s*show\\s+map\\s*$"
    ),
    SHOW_SUN_AMOUNT(
            "^\\s*show\\s+sun\\s+amount\\s*$"
    ),
    SHOW_PLANT_STATUS(
            "^\\s*show\\s+plant\\s+status\\s*$"
    ),
    SHOW_TILE_STATUS(
            "^\\s*show\\s+tile\\s+status\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"
    ),
    ZOMBIES_INFO(
            "^\\s*zombies\\s+info\\s*$"
    ),
    PLANT_ON_FIELD(
            "^\\s*plant\\s+plant\\s+-t\\s+(?<type>.+?)\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"
    ),
    PLUCK_PLANT_FIELD(
            "^\\s*pluck\\s+plant\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"
    ),
    FEED_PLANT_FIELD(
            "^\\s*feed\\s+plant\\s+-l\\s*\\(\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)\\s*$"
    ),
    CHEAT_ADD_SUNS(
            "^\\s*cheat\\s+add\\s+-n\\s+(?<count>\\d+)\\s+suns\\s*$"
    ),
    CHEAT_ADD_PLANT_FOOD(
            "^\\s*cheat\\s+add-plant-food\\s*$"
    ),
    CHEAT_REMOVE_COOLDOWN(
            "^\\s*cheat\\s+remove-cooldown\\s*$"
    ),
    CHEAT_SPAWN_ZOMBIE(
            "^\\s*cheat\\s+spawn-zombie\\s+-t\\s+(?<type>\\S+)\\s+-l\\s*\\(?\\s*(?<x>\\d+)\\s*,\\s*(?<y>\\d+)\\s*\\)?\\s*$"
    ),
    RELEASE_THE_NUKE(
            "^\\s*release\\s+the\\s+nuke\\s*$"
    ),

    MENU_EXIT(
            "^\\s*menu\\s+exit\\s*$"
    );


    private final Pattern compiledPattern;

    Regex(String pattern) {
        this.compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    }

    public Pattern getPattern() {
        return compiledPattern;
    }

    public Matcher getMatcherRaw(String input) {
        return compiledPattern.matcher(input.trim());
    }
}