# d:\College\AP\project gitlab\src\controller\assets\AssetPaths.java

Package: controller.assets
Class: AssetPaths

Imports:

Methods:
  - slug()
  - plantRegion()
  - zombieRegion()
  - plantTexturePath()
  - zombieTexturePath()
  - seasonMapPath()
  - sfxPath()
  - musicPath()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\assets\GameAssetManager.java

Package: controller.assets
Class: GameAssetManager

Imports:
  - com.badlogic.gdx.Gdx
  - com.badlogic.gdx.assets.AssetManager
  - com.badlogic.gdx.audio.Music
  - com.badlogic.gdx.audio.Sound
  - com.badlogic.gdx.graphics.Texture
  - com.badlogic.gdx.graphics.g2d.BitmapFont
  - com.badlogic.gdx.graphics.g2d.TextureAtlas
  - com.badlogic.gdx.graphics.g2d.TextureRegion

Methods:
  - getInstance()
  - initialize()
  - loadAtlas()
  - loadTexture()
  - loadFont()
  - loadSound()
  - loadMusic()
  - update()
  - getProgress()
  - finishLoading()
  - isLoaded()
  - get()
  - getAtlas()
  - getTexture()
  - getFont()
  - getSound()
  - getMusic()
  - getPlantRegion()
  - getZombieRegion()
  - findRegion()
  - raw()
  - dispose()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\assets\GameMapManager.java

Package: controller.assets
Class: GameMapManager

Imports:
  - com.badlogic.gdx.maps.tiled.TiledMap
  - com.badlogic.gdx.maps.tiled.TmxMapLoader
  - model.maps.MapMetadata
  - model.match.main.levels.Level
  - model.match.main.season.Season
  - java.util.HashMap
  - java.util.Map

Methods:
  - TmxMapLoader()
  - getInstance()
  - loadForSeason()
  - loadForLevel()
  - loadMap()
  - getIfLoaded()
  - isLoaded()
  - extractMetadata()
  - unload()
  - disposeAll()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\CollectionManager.java

Package: controller
Class: CollectionManager

Imports:
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match.main.levels.Level
  - model.match_mechanisms.ZombieWave
  - model.user_data.UserState
  - model.utils.LevelLoader
  - model.utils.LevelProgression
  - java.util.ArrayList
  - java.util.HashSet
  - java.util.List
  - java.util.Set
  - java.util.stream.Collectors

Methods:
  - getAllPlants()
  - getUnlockedPlants()
  - findPlant()
  - getAllZombieAliases()
  - getSeenZombieAliases()
  - findZombie()
  - formatPlant()
  - formatZombie()
  - purchasePlant()
  - upgradePlant()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\authentication\LoginMenu.java

Package: controller.menus.authentication
Class: LoginMenu
Extends: Menu

Imports:
  - controller.menus.MainMenu
  - controller.menus.Menu
  - model.Regex
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.regex.Matcher
  - static

Methods:
  - getName()
  - handleCommand()
  - handleLogin()
  - handleForgetPassword()
  - handleAnswer()
  - handleNewPassword()
  - isStrongPassword()
  - handleMenuEnter()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\authentication\SignupMenu.java

Package: controller.menus.authentication
Class: SignupMenu
Extends: Menu

Imports:
  - controller.menus.Menu
  - model.Regex
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.List
  - java.util.regex.Matcher
  - static

Methods:
  - getName()
  - handleCommand()
  - handleRegister()
  - handlePickQuestion()
  - validateUsername()
  - validatePassword()
  - validateNickname()
  - validateEmail()
  - validateGender()
  - handleMenuEnter()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\CollectionMenu.java

Package: controller.menus
Class: CollectionMenu
Extends: Menu

Imports:
  - controller.CollectionManager
  - model.App
  - model.Regex
  - model.collections.plant.PlantJsonParser
  - model.collections.zombie.Zombie
  - model.game_exceptions.GameException
  - model.user_data.User
  - model.user_data.UserState
  - view.GeneralPrinter
  - java.util.regex.Matcher

Methods:
  - CollectionManager()
  - getName()
  - handleCommand()
  - showAllPlants()
  - showUnlockedPlants()
  - showOnePlant()
  - showAllZombies()
  - showSeenZombies()
  - showOneZombie()
  - purchasePlant()
  - upgradePlant()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\GameMenu.java

Package: controller.menus
Class: GameMenu
Extends: Menu

Imports:
  - controller.menus.greenhouse.GreenhouseMenu
  - model.App
  - model.Regex
  - model.game_exceptions.GameException
  - model.match.main.levels.Level
  - model.match.main.season.Season
  - model.match.main.season.SeasonFactory
  - model.user_data.User
  - model.user_data.UserState
  - model.utils.LevelLoader
  - model.utils.LevelProgression
  - view.GeneralPrinter
  - java.util.List
  - java.util.regex.Matcher

Methods:
  - getName()
  - handleCommand()
  - enterChapter()
  - showChapters()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\greenhouse\GreenhouseMenu.java

Package: controller.menus.greenhouse
Class: GreenhouseMenu
Extends: Menu

Imports:
  - controller.menus.GameMenu
  - controller.menus.Menu
  - controller.menus.store.StoreMenu
  - model.App
  - model.Regex
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantFoodType
  - model.collections.plant.PlantJsonParser
  - model.game_exceptions.GameException
  - model.greenhouse.
  - model.user_data.User
  - model.user_data.UserState
  - view.GeneralPrinter
  - java.util.ArrayList
  - java.util.List
  - java.util.Map
  - java.util.Random
  - java.util.regex.Matcher

Methods:
  - plantPotPlant()
  - getName()
  - handleCommand()
  - handleGreenhouseCommand()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\greenhouse\PotController.java

Package: controller.menus.greenhouse
Class: PotController

Imports:
  - model.greenhouse.Pot
  - model.greenhouse.PotPlant
  - model.user_data.UserState

Methods:
  - collect()
  - calculateGrowCost()
  - grow()
  - removePlant()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\LeaderboardMenu.java

Package: controller.menus
Class: LeaderboardMenu
Extends: Menu

Imports:
  - model.App
  - model.Regex
  - model.match.main.levels.Level
  - model.user_data.User
  - model.utils.LevelLoader
  - view.GeneralPrinter
  - java.util.Collections
  - java.util.Comparator
  - java.util.List
  - java.util.Locale
  - java.util.regex.Matcher
  - java.util.stream.Collectors

Methods:
  - getName()
  - handleCommand()
  - sort()
  - exitMenu()
  - showMenu()
  - sortRows()
  - Row()
  - capitalize()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\MainMenu.java

Package: controller.menus
Class: MainMenu
Extends: Menu

Imports:
  - controller.NewsManager
  - controller.menus.authentication.LoginMenu
  - model.App
  - model.Regex
  - model.user_data.User
  - view.GeneralPrinter

Methods:
  - getName()
  - handleCommand()
  - exitMenu()
  - showMenu()
  - Logout()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\match\AfterMenu.java

Package: controller.menus.match
Class: AfterMenu
Extends: Menu

Imports:
  - controller.CollectionManager
  - controller.NewsManager
  - controller.menus.GameMenu
  - controller.menus.Menu
  - model.App
  - model.Regex
  - model.collections.plant.PlantJsonParser
  - model.collections.zombie.Zombie
  - model.match.main.levels.Level
  - model.user_data.User
  - model.user_data.UserState
  - model.utils.GameSession
  - model.utils.LevelLoader
  - model.utils.LevelProgression
  - view.GeneralPrinter
  - java.util.HashSet
  - java.util.List
  - java.util.Random
  - java.util.Set

Methods:
  - Random()
  - CollectionManager()
  - reset()
  - grantReward()
  - getName()
  - handleCommand()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\match\BeforeMenu.java

Package: controller.menus.match
Class: BeforeMenu
Extends: Menu

Imports:
  - controller.CollectionManager
  - controller.menus.Menu
  - model.App
  - model.Regex
  - model.collections.plant.PlantJsonParser
  - model.collections.plant.PlantFoodType
  - model.game_exceptions.GameException
  - model.match.main.levels.Level
  - model.match.main.levels.special_levels.ConveyorBeltLevel
  - model.match.main.levels.special_levels.LockedPlantsLevel
  - model.match.main.levels.special_levels.PlantWhatYouGetLevel
  - model.user_data.User
  - model.user_data.UserState
  - model.utils.GameSession
  - view.GeneralPrinter
  - java.util.ArrayList
  - java.util.List
  - java.util.regex.Matcher

Methods:
  - CollectionManager()
  - getName()
  - handleCommand()
  - currentLevel()
  - showAllPlants()
  - showAvailablePlants()
  - addPlant()
  - removePlant()
  - boostPlant()
  - startMatch()
  - isAllowedInCurrentLevel()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\match\MatchMenu.java

Package: controller.menus.match
Class: MatchMenu
Extends: Menu

Imports:
  - controller.CollectionManager
  - controller.NewsManager
  - controller.menus.GameMenu
  - controller.menus.Menu
  - model.App
  - model.Regex
  - model.collections.plant.Plant
  - model.collections.plant.PlantJsonParser
  - model.match.main.levels.Level
  - model.match.main.levels.special_levels.ConveyorBeltLevel
  - model.user_data.User
  - model.user_data.UserState
  - model.utils.GameSession
  - model.utils.LevelProgression
  - view.GeneralPrinter
  - java.util.ArrayList
  - java.util.List

Methods:
  - configureChapter()
  - getName()
  - handleCommand()
  - selectStage()
  - unlockStagePlants()
  - formatStageList()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\match\MeanwhileMenu.java

Package: controller.menus.match
Class: MeanwhileMenu
Extends: Menu

Imports:
  - controller.CollectionManager
  - controller.menus.GameMenu
  - controller.menus.Menu
  - model.App
  - model.Regex
  - model.collections.item.GroundItem
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.game_exceptions.GameException
  - model.match.main.levels.special_levels.ConveyorBeltLevel
  - model.match.main.levels.special_levels.PlantWhatYouGetLevel
  - model.match_mechanisms.vector.Position
  - model.user_data.User
  - model.user_data.UserState
  - model.utils.GameSession
  - model.utils.LevelLoader
  - view.GeneralPrinter
  - java.util.List
  - java.util.regex.Matcher

Methods:
  - CollectionManager()
  - getName()
  - handleCommand()
  - plantAt()
  - removePlantAt()
  - digPlantAt()
  - collectAt()
  - useFoodAt()
  - waitSeconds()
  - advanceTicks()
  - isAllowedWhilePaused()
  - spawnZombie()
  - restartMatch()
  - requestEndGame()
  - finishMatch()
  - startZombieWaves()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\match\ZombieWaveController.java

Package: controller.menus.match
Class: ZombieWaveController

Imports:
  - model.match_mechanisms.ZombieWave
  - java.util.List

Methods:
  - getWaves()
  - setWaves()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\Menu.java

Package: controller.menus
Class: Menu

Imports:
  - controller.menus.authentication.LoginMenu
  - controller.menus.authentication.SignupMenu
  - model.App
  - model.Regex
  - model.game_exceptions.GameException
  - view.GeneralPrinter
  - java.util.regex.Matcher

Methods:
  - changeMenu()
  - handleCommand()
  - getName()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\NetworkMenu.java

Package: controller.menus
Class: NetworkMenu
Extends: Menu

Imports:
  - model.App
  - model.Regex
  - view.GeneralPrinter

Methods:
  - getName()
  - handleCommand()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\NewsMenu.java

Package: controller.menus
Class: NewsMenu
Extends: Menu

Imports:
  - model.App
  - model.Regex
  - model.news.News
  - model.user_data.User
  - view.GeneralPrinter

Methods:
  - getName()
  - handleCommand()
  - showUnread()
  - showAll()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\ProfileMenu.java

Package: controller.menus
Class: ProfileMenu
Extends: Menu

Imports:
  - model.Regex
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.regex.Matcher

Methods:
  - getName()
  - handleCommand()
  - handleChangeUsername()
  - handleChangeNickname()
  - handleChangeEmail()
  - handleChangePassword()
  - isValidEmail()
  - isStrongPassword()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\SettingMenu.java

Package: controller.menus
Class: SettingMenu
Extends: Menu

Imports:
  - model.App
  - model.Regex
  - model.game_exceptions.GameException
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.regex.Matcher

Methods:
  - getName()
  - handleCommand()
  - changeDifficulty()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\store\StoreMenu.java

Package: controller.menus.store
Class: StoreMenu
Extends: Menu

Imports:
  - controller.menus.Menu
  - controller.menus.greenhouse.GreenhouseMenu
  - model.App
  - model.Regex
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.game_exceptions.GameException
  - model.greenhouse.store.Store
  - model.user_data.User
  - model.user_data.UserState
  - view.GeneralPrinter
  - java.util.Map
  - java.util.regex.Matcher

Methods:
  - Store()
  - getName()
  - handleCommand()
  - handleStoreCommand()
  - resolvePlantId()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\menus\TravelLogMenu.java

Package: controller.menus
Class: TravelLogMenu
Extends: Menu

Imports:
  - controller.QuestManager
  - controller.mini_games.
  - model.App
  - model.Regex
  - model.game_exceptions.GameException
  - model.match.mini_games.Beghouled
  - model.match.mini_games.Zombotany
  - model.match.mini_games.izombie.IZombie
  - model.match.mini_games.vasebreaker.Vasebreaker
  - model.match.mini_games.wallnutbowlling.WallnutBowling
  - model.quests.GameQuest
  - model.quests.QuestLoader
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.ArrayList
  - java.util.List
  - java.util.regex.Matcher
  - java.util.regex.Pattern
  - java.util.stream.Collectors

Methods:
  - getName()
  - handleCommand()
  - changePage()
  - questsForCategory()
  - renderCurrentPage()
  - formatQuest()
  - progressBar()
  - renderMiniGames()
  - startMiniGame()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\mini_games\BeghouledController.java

Package: controller.mini_games
Class: BeghouledController
Extends: Menu

Imports:
  - controller.menus.Menu
  - controller.menus.TravelLogMenu
  - model.App
  - model.Regex
  - model.match.mini_games.Beghouled
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.List
  - java.util.regex.Matcher
  - java.util.regex.Pattern

Methods:
  - getName()
  - handleCommand()
  - advanceTime()
  - handleSwap()
  - handleUpgrade()
  - handleCollect()
  - handleSunCheat()
  - parseTwoCoords()
  - reportOutcome()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\mini_games\ImZombieController.java

Package: controller.mini_games
Class: ImZombieController
Extends: Menu

Imports:
  - controller.menus.Menu
  - controller.menus.TravelLogMenu
  - model.App
  - model.Regex
  - model.match.mini_games.izombie.IZombie
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.List
  - java.util.regex.Matcher
  - java.util.regex.Pattern

Methods:
  - getName()
  - handleCommand()
  - advanceTime()
  - handlePlace()
  - handleCollect()
  - handleSunCheat()
  - reportOutcome()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\mini_games\MiniGameEndMenu.java

Package: controller.mini_games
Class: MiniGameEndMenu
Extends: Menu

Imports:
  - controller.menus.Menu
  - controller.menus.TravelLogMenu
  - model.App
  - model.Regex
  - view.GeneralPrinter

Methods:
  - getName()
  - handleCommand()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\mini_games\VasebreakerController.java

Package: controller.mini_games
Class: VasebreakerController
Extends: Menu

Imports:
  - controller.menus.Menu
  - controller.menus.TravelLogMenu
  - model.App
  - model.Regex
  - model.match.mini_games.vasebreaker.Vasebreaker
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.regex.Matcher
  - java.util.regex.Pattern

Methods:
  - getName()
  - handleCommand()
  - advanceTime()
  - breakAtPlayerCoordinates()
  - collectAtPlayerCoordinates()
  - handleCollectItem()
  - parseCoords()
  - reportOutcome()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\mini_games\WallnutBowlingController.java

Package: controller.mini_games
Class: WallnutBowlingController
Extends: Menu

Imports:
  - controller.menus.Menu
  - controller.menus.TravelLogMenu
  - model.App
  - model.Regex
  - model.match.mini_games.wallnutbowlling.WallnutBowling
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.List
  - java.util.regex.Matcher
  - java.util.regex.Pattern

Methods:
  - getName()
  - handleCommand()
  - advanceTime()
  - handlePlant()
  - handleCollect()
  - reportOutcome()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\mini_games\ZombotanyController.java

Package: controller.mini_games
Class: ZombotanyController
Extends: Menu

Imports:
  - controller.menus.Menu
  - controller.menus.TravelLogMenu
  - model.App
  - model.Regex
  - model.match.mini_games.Zombotany
  - model.user_data.User
  - view.GeneralPrinter
  - java.util.List
  - java.util.regex.Matcher
  - java.util.regex.Pattern

Methods:
  - getName()
  - handleCommand()
  - advanceTime()
  - plant()
  - handleCollect()
  - handleSunCheat()
  - reportOutcome()
  - exitMenu()
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\NewsManager.java

Package: controller
Class: NewsManager

Imports:
  - model.App
  - model.news.News
  - model.user_data.User

Methods:
  - hasUnreadNews()
  - generateNews()
  - generateNews()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\controller\QuestManager.java

Package: controller
Class: QuestManager

Imports:
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.pitches.Cell
  - model.pitches.Environment
  - model.quests.GameQuest
  - model.quests.QuestCriterion
  - model.quests.QuestLoader
  - model.user_data.User
  - model.user_data.UserState
  - model.utils.GameSession
  - java.util.

Methods:
  - Random()
  - notifyLevelStarted()
  - notifyZombieKilled()
  - updateProgress()
  - notifyLevelWon()
  - notifyLevelLost()
  - notifyForbiddenFamilyWin()
  - addQuestProgress()
  - resetQuestProgress()
  - resetIncompleteQuestProgress()
  - stringParam()
  - findPlantConfig()
  - normalizeName()
  - mapOf()
  - matchesParams()
  - checkCompletion()
  - collectReward()
  - findQuestById()
  - getDisplayTarget()
  - rewardUser()
  - resolveSeedPacketPlantId()
  - evaluateFormula()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\DesktopLauncher.java

Class: DesktopLauncher

Imports:
  - com.badlogic.gdx.Files
  - com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application
  - com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration

Methods:
  - main()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\Main.java

Class: Main
Extends: ApplicationAdapter

Imports:
  - com.badlogic.gdx.ApplicationAdapter
  - com.badlogic.gdx.Gdx
  - com.badlogic.gdx.graphics.GL20
  - controller.assets.GameAssetManager
  - model.collections.plant.PlantFactory
  - model.quests.QuestLoader
  - view.AppView

Methods:
  - create()
  - render()
  - resize()
  - pause()
  - resume()
  - dispose()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\App.java

Package: model
Class: App

Imports:
  - controller.menus.MainMenu
  - controller.menus.Menu
  - controller.menus.authentication.SignupMenu
  - model.match.main.MainMode
  - model.user_data.User
  - static

Methods:
  - SignupMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\Ability.java

Package: model.collections
# d:\College\AP\project gitlab\src\model\collections\armour\Armour.java

Package: model.collections.armour
Class: Armour

Imports:

Methods:
  - absorbDamage()
  - changeState()
  - getHP()
  - setHP()
  - getStage()
  - setStage()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\armour\ArmourFactory.java

Package: model.collections.armour
Class: ArmourFactory

Imports:

Methods:
  - createArmour()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\armour\ArmourStage.java

Package: model.collections.armour
# d:\College\AP\project gitlab\src\model\collections\armour\ArmourType.java

Package: model.collections.armour
# d:\College\AP\project gitlab\src\model\collections\armour\PlantArmour.java

Package: model.collections.armour
Class: PlantArmour
Extends: Armour

Imports:
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie

Methods:
  - absorbDamage()
  - handleReflection()
  - isDestroyed()
  - isExplodeOnBreak()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\armour\ZombieArmour.java

Package: model.collections.armour
Class: ZombieArmour
Extends: Armour

Imports:

Methods:
  - absorbDamage()
  - changeState()
  - isDestroyed()
  - getDamageLayer()
  - getArmorType()
  - getMaxArmorHp()
  - isMetal()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\Faction.java

Package: model.collections
# d:\College\AP\project gitlab\src\model\collections\item\GroundCoin.java

Package: model.collections.item
Class: GroundCoin
Extends: GroundItem

Imports:
  - model.match_mechanisms.vector.Position
  - model.user_data.UserState
  - model.utils.GameSession
  - java.util.Random

Methods:
  - BRONZE()
  - CoinTier()
  - getValue()
  - rollRandom()
  - applyRewards()
  - getTier()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\item\GroundDiamond.java

Package: model.collections.item
Class: GroundDiamond
Extends: GroundItem

Imports:
  - model.match_mechanisms.vector.Position
  - model.user_data.UserState
  - model.utils.GameSession

Methods:
  - applyRewards()
  - getAmount()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\item\GroundItem.java

Package: model.collections.item
Class: GroundItem
Extends: Item

Imports:
  - model.collections.Item
  - model.match_mechanisms.vector.Position
  - model.user_data.UserState
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyRewards()
  - collect()
  - isNear()
  - tick()
  - getItemType()
  - isCollected()
  - getLifetimeSeconds()
  - getCollectRadius()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\item\GroundPlantFood.java

Package: model.collections.item
Class: GroundPlantFood
Extends: GroundItem

Imports:
  - model.match_mechanisms.vector.Position
  - model.user_data.UserState
  - model.utils.GameSession

Methods:
  - applyRewards()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\item\GroundPot.java

Package: model.collections.item
Class: GroundPot
Extends: GroundItem

Imports:
  - model.greenhouse.Greenhouse
  - model.match_mechanisms.vector.Position
  - model.user_data.UserState
  - model.utils.GameSession

Methods:
  - applyRewards()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\item\GroundSeedPack.java

Package: model.collections.item
Class: GroundSeedPack
Extends: GroundItem

Imports:
  - model.match_mechanisms.vector.Position
  - model.user_data.UserState
  - model.utils.GameSession

Methods:
  - applyRewards()
  - getPlantId()
  - getPackCount()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\item\GroundSun.java

Package: model.collections.item
Class: GroundSun
Extends: GroundItem

Imports:
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.user_data.UserState
  - model.utils.GameSession
  - service.GameClock
  - view.GeneralPrinter
  - java.util.Random

Methods:
  - Random()
  - REGULAR()
  - SunDropType()
  - getProbability()
  - getValue()
  - rollRandom()
  - fallFromSky()
  - tick()
  - isFalling()
  - applyRewards()
  - explodeRadioactive()
  - getDropType()
  - getSunValue()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\item\ItemType.java

Package: model.collections.item
# d:\College\AP\project gitlab\src\model\collections\Item.java

Package: model.collections
Class: Item

Imports:
  - model.match_mechanisms.vector.Position
  - model.utils.state.ItemState

Methods:
  - tick()
  - takeDamage()
  - isDead()
  - isAlive()
  - setAlive()
  - getState()
  - setState()
  - getPosition()
  - setPosition()
  - getSpeed()
  - setSpeed()
  - getHP()
  - setHP()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\AbilityType.java

Package: model.collections.plant
# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\ActStrategy.java

Package: model.collections.plant.actstrategy
# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\ExplodeStrategy.java

Package: model.collections.plant.actstrategy
Class: ExplodeStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.AbilityType
  - model.collections.plant.Plant
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.obstacles.Grave
  - model.pitches.obstacles.IceBlock
  - model.utils.GameSession
  - java.util.ArrayList

Methods:
  - act()
  - damageStructures()
  - handleObstaclePlant()
  - isZombieTouch()
  - touchDetect()
  - areaDetect()
  - lineDetect()
  - wholePitchDetect()
  - makeHole()
  - userAct()
  - isHostileTarget()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\HomingStrategy.java

Package: model.collections.plant.actstrategy
Class: HomingStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.projectile.Projectile
  - model.projectile.StraightMove
  - model.projectile.hit.NormalHit
  - model.projectile.hit.PierceHit
  - model.utils.GameSession
  - java.util.List
  - java.util.concurrent.ThreadLocalRandom

Methods:
  - act()
  - buildProjectile()
  - randomTarget()
  - nearestTarget()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\LobberStrategy.java

Package: model.collections.plant.actstrategy
Class: LobberStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.projectile.ArcMove
  - model.projectile.Projectile
  - model.projectile.hit.
  - model.utils.GameSession

Methods:
  - act()
  - buildHitEffect()
  - findNearestInLane()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\MeleeStrategy.java

Package: model.collections.plant.actstrategy
Class: MeleeStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - java.util.ArrayList

Methods:
  - act()
  - attackStructures()
  - isEnemyAround()
  - frontBackDetect()
  - areaDetect()
  - waveDetect()
  - swallowDetect()
  - userAct()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\MintStrategy.java

Package: model.collections.plant.actstrategy
Class: MintStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.Plant
  - model.utils.GameSession

Methods:
  - act()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\ModifyStrategy.java

Package: model.collections.plant.actstrategy
Class: ModifyStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.projectile.Projectile
  - model.projectile.hit.FireHit
  - model.projectile.hit.IceHit
  - model.projectile.hit.PoisonHit
  - model.utils.GameSession
  - java.util.ArrayList

Methods:
  - act()
  - imitateNearestPlant()
  - disarmNearestZombie()
  - hypnotizeTouchingZombie()
  - projectileThroughDetect()
  - modifyTargets()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\ShootStrategy.java

Package: model.collections.plant.actstrategy
Class: ShootStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.projectile.Projectile
  - model.projectile.StraightMove
  - model.projectile.hit.
  - model.utils.GameSession
  - java.util.List

Methods:
  - act()
  - buildHitEffect()
  - findTargetAlongVector()
  - isInCone()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\StrikeStrategy.java

Package: model.collections.plant.actstrategy
Class: StrikeStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.projectile.Projectile
  - model.projectile.StraightMove
  - model.projectile.hit.PierceHit
  - model.utils.GameSession

Methods:
  - act()
  - findNearestInLane()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\SunProduceStrategy.java

Package: model.collections.plant.actstrategy
Class: SunProduceStrategy
Implements: ActStrategy 

Imports:
  - model.collections.item.GroundSun
  - model.collections.plant.AbilityType
  - model.collections.plant.Plant
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - service.GameClock
  - view.GeneralPrinter

Methods:
  - act()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\actstrategy\WallNutStrategy.java

Package: model.collections.plant.actstrategy
Class: WallNutStrategy
Implements: ActStrategy 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - java.util.Comparator

Methods:
  - act()
  - divertTouchingZombie()
  - attractZombie()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\GrowthTracker.java

Package: model.collections.plant
Class: GrowthTracker

Imports:
  - java.util.List
  - java.util.Map

Methods:
  - hasStages()
  - update()
  - getStageValue()
  - getCurrentStage()
  - getAgeInSeconds()
  - skipToMaxStage()
  - getRawStages()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\ModifiableStat.java

Package: model.collections.plant
Class: ModifiableStat

Imports:

Methods:
  - update()
  - getValue()
  - setBaseValue()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\Plant.java

Package: model.collections.plant
Class: Plant
Extends: Item
Implements: Pluck, Attack 

Imports:
  - model.collections.Item
  - model.collections.armour.PlantArmour
  - model.collections.plant.actstrategy.ActStrategy
  - model.collections.zombie.Zombie
  - model.match_mechanisms.Attack
  - model.match_mechanisms.Pluck
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - service.GameClock
  - view.GeneralPrinter
  - java.util.ArrayList
  - java.util.List
  - java.util.Map

Methods:
  - getIntervalTimer()
  - setInternalTimer()
  - getPosition()
  - getLocation()
  - tick()
  - tick()
  - takeDamage()
  - executeArmorExplosion()
  - dealDamage()
  - activatePlant()
  - canUsePlantFood()
  - isPlantFoodActive()
  - getId()
  - setId()
  - getName()
  - setName()
  - getLevel()
  - setLevel()
  - getRecharge()
  - setRecharge()
  - getActionInterval()
  - setActionInterval()
  - getCost()
  - setCost()
  - getDamage()
  - setDamage()
  - getType()
  - setType()
  - getAbilityType()
  - setAbilityType()
  - getBottom()
  - setBottom()
  - getTags()
  - getRawUpgrades()
  - setActStrategy()
  - getActStrategy()
  - getPlantFoodEffect()
  - setPlantFoodEffect()
  - setPlantFoodType()
  - setAbilityValue()
  - getAbilityValue()
  - setWrampUp()
  - getShootingVectors()
  - setShootingVectors()
  - getArmor()
  - setArmor()
  - setState()
  - getPlantState()
  - getChillLevel()
  - setChillLevel()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\PlantFactory.java

Package: model.collections.plant
Class: PlantFactory
Extends: Plant

Imports:
  - model.collections.armour.ArmourFactory
  - model.collections.armour.ArmourType
  - model.collections.armour.PlantArmour
  - model.collections.plant.actstrategy.
  - model.collections.plant.plantfood.
  - model.match_mechanisms.vector.Position
  - model.utils.ResourceResolver
  - view.GeneralPrinter
  - java.io.InputStream
  - java.util.ArrayList
  - java.util.HashMap
  - java.util.List
  - java.util.Map

Methods:
  - init()
  - autoInit()
  - getBlueprints()
  - createPlant()
  - buildActStrategy()
  - buildPlantFoodEffect()
  - projectileBurstCount()
  - buildShootingVectors()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\GrantArmor.java

Package: model.collections.plant.plantfood
Class: GrantArmor
Implements: PlantFoodEffect 

Imports:
  - model.collections.armour.ArmourFactory
  - model.collections.armour.ArmourType
  - model.collections.armour.PlantArmour
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\InstantKill.java

Package: model.collections.plant.plantfood
Class: InstantKill
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\KnockBackBlast.java

Package: model.collections.plant.plantfood
Class: KnockBackBlast
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\LobberBarrage.java

Package: model.collections.plant.plantfood
Class: LobberBarrage
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - fireOnce()
  - getDurationSeconds()
  - applyStatusModifiers()
  - reset()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\LocalAttack.java

Package: model.collections.plant.plantfood
Class: LocalAttack
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\MapWideFreeze.java

Package: model.collections.plant.plantfood
Class: MapWideFreeze
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\PullUnderWater.java

Package: model.collections.plant.plantfood
Class: PullUnderWater
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\RandomHypnotize.java

Package: model.collections.plant.plantfood
Class: RandomHypnotize
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.collections.zombie.Zombie
  - model.utils.GameSession
  - java.util.ArrayList
  - java.util.Collections
  - java.util.List

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\SpawnClones.java

Package: model.collections.plant.plantfood
Class: SpawnClones
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantFoodEffect
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - trySpawnAt()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\SpawnSun.java

Package: model.collections.plant.plantfood
Class: SpawnSun
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - applyStatusModifiers()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\plantfood\TimedProjectileBurst.java

Package: model.collections.plant.plantfood
Class: TimedProjectileBurst
Implements: PlantFoodEffect 

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFoodEffect
  - model.utils.GameSession

Methods:
  - triggerSuperpower()
  - tickDurationEffect()
  - fireOnce()
  - getDurationSeconds()
  - applyStatusModifiers()
  - reset()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\PlantFoodEffect.java

Package: model.collections.plant
# d:\College\AP\project gitlab\src\model\collections\plant\PlantFoodType.java

Package: model.collections.plant
# d:\College\AP\project gitlab\src\model\collections\plant\PlantJsonParser.java

Package: model.collections.plant
Class: PlantJsonParser

Imports:
  - com.google.gson.Gson
  - com.google.gson.GsonBuilder
  - com.google.gson.JsonDeserializer
  - com.google.gson.annotations.SerializedName
  - com.google.gson.reflect.TypeToken
  - view.GeneralPrinter
  - java.io.IOException
  - java.io.InputStream
  - java.io.InputStreamReader
  - java.lang.reflect.Type
  - java.nio.charset.StandardCharsets
  - java.util.HashMap
  - java.util.List
  - java.util.Map

Methods:
  - resolveTag()
  - buildGson()
  - loadConfigs()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\plant\PlantTag.java

Package: model.collections.plant
# d:\College\AP\project gitlab\src\model\collections\plant\PlantTemplate.java

Package: model.collections.plant
# d:\College\AP\project gitlab\src\model\collections\plant\PlantType.java

Package: model.collections.plant
# d:\College\AP\project gitlab\src\model\collections\plant\UpgradeType.java

Package: model.collections.plant
# d:\College\AP\project gitlab\src\model\collections\zombie\BehaviorSpec.java

Package: model.collections.zombie
Class: BehaviorSpec

Imports:
  - java.util.Collections
  - java.util.Map

Methods:
  - parse()
  - getType()
  - params()
  - getDouble()
  - getInt()
  - getBoolean()
  - getString()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\VulnerabilityType.java

Package: model.collections.zombie
# d:\College\AP\project gitlab\src\model\collections\zombie\Zombie.java

Package: model.collections.zombie
Class: Zombie
Extends: Item
Implements: Attack 

Imports:
  - model.collections.Faction
  - model.collections.Item
  - model.collections.armour.Armour
  - model.collections.plant.Plant
  - model.collections.zombie.zombie_attack.AttackBehavior
  - model.collections.zombie.zombie_attack.ZombieTargeting
  - model.collections.zombie.zombie_defense.DefenseBehavior
  - model.collections.zombie.zombie_effect.FireEffect
  - model.collections.zombie.zombie_effect.ZombieEffectStatus
  - model.collections.zombie.zombie_move.HypnotizedMoveBehavior
  - model.collections.zombie.zombie_move.MoveBehavior
  - model.collections.zombie.zombie_move.ProspectorMove
  - model.collections.zombie.zombie_pushing_item.PushableStructure
  - model.match_mechanisms.Attack
  - model.match_mechanisms.vector.Position
  - model.projectile.ArcMove
  - model.projectile.Projectile
  - model.utils.GameSession
  - java.util.List
  - java.util.Random

Methods:
  - Random()
  - chanceToHavePlantFood()
  - dealDamage()
  - takeDamage()
  - takeDamage()
  - takeDamage()
  - applyDamageCalculations()
  - handleDeath()
  - updateStatus()
  - normalizePlantName()
  - resolveKillerName()
  - tick()
  - tick()
  - move()
  - acquireTarget()
  - hypnotize()
  - getMoveBehavior()
  - setMoveBehavior()
  - getAttackBehavior()
  - setAttackBehavior()
  - getDefenseBehavior()
  - setDefenseBehavior()
  - getEffectStatus()
  - setEffectStatus()
  - getFaction()
  - setFaction()
  - isHypnotized()
  - isPlantFoodPending()
  - clearPlantFoodPending()
  - getVulnerabilityState()
  - setVulnerabilityState()
  - getName()
  - setName()
  - getHp()
  - setHp()
  - getMaxHp()
  - setMaxHp()
  - getEatDps()
  - setEatDps()
  - getRace()
  - setRace()
  - getZombieState()
  - getArmor()
  - setArmor()
  - getArmour()
  - setArmour()
  - isGlowing()
  - getAlias()
  - getStatus()
  - setStatus()
  - applyStatus()
  - isFacingRight()
  - setFacingRight()
  - hasPlantFood()
  - setHasPlantFood()
  - getPushedStructure()
  - setPushedStructure()
  - getPushableRespawnsRemaining()
  - setPushableRespawnsRemaining()
  - setDamageWhileSubmerged()
  - setDamageWhileSubmergedPlantfoodOnly()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_attack\AttackBehavior.java

Package: model.collections.zombie.zombie_attack
# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_attack\AttackBehaviorFactory.java

Package: model.collections.zombie.zombie_attack
# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_attack\AttackBehaviorRegistry.java

Package: model.collections.zombie.zombie_attack
Class: AttackBehaviorRegistry

Imports:
  - model.collections.zombie.BehaviorSpec
  - java.util.HashMap
  - java.util.Map

Methods:
  - register()
  - create()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_attack\ChompAttack.java

Package: model.collections.zombie.zombie_attack
Class: ChompAttack
Implements: AttackBehavior 

Imports:
  - model.collections.Item
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.utils.GameSession
  - service.GameClock

Methods:
  - attack()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_attack\CrushAttack.java

Package: model.collections.zombie.zombie_attack
Class: CrushAttack
Implements: AttackBehavior 

Imports:
  - model.collections.Item
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - attack()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_attack\KamikazeAttack.java

Package: model.collections.zombie.zombie_attack
Class: KamikazeAttack
Implements: AttackBehavior 

Imports:
  - model.collections.Item
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - attack()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_attack\SmashAttack.java

Package: model.collections.zombie.zombie_attack
Class: SmashAttack
Implements: AttackBehavior 

Imports:
  - model.collections.Item
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - service.GameClock

Methods:
  - attack()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_attack\ZombieTargeting.java

Package: model.collections.zombie.zombie_attack
Class: ZombieTargeting

Imports:
  - model.collections.Faction
  - model.collections.Item
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.pitches.Cell
  - model.pitches.obstacles.Obstacle
  - model.utils.GameSession
  - java.util.Comparator

Methods:
  - findTarget()
  - getZombieCell()
  - isPlantIncapacitated()
  - findPlantSideTarget()
  - findZombieSideTarget()
  - isInEatingRange()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_defense\ArmorBasedDefense.java

Package: model.collections.zombie.zombie_defense
Class: ArmorBasedDefense
Implements: DefenseBehavior 

Imports:
  - model.collections.armour.Armour
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - handleDamage()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_defense\DefenseBehavior.java

Package: model.collections.zombie.zombie_defense
# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_defense\DefenseBehaviorFactory.java

Package: model.collections.zombie.zombie_defense
# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_defense\DefenseBehaviorRegistry.java

Package: model.collections.zombie.zombie_defense
Class: DefenseBehaviorRegistry

Imports:
  - model.collections.zombie.BehaviorSpec
  - java.util.HashMap
  - java.util.Map

Methods:
  - register()
  - create()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_defense\JesterDeflection.java

Package: model.collections.zombie.zombie_defense
Class: JesterDeflection
Implements: DefenseBehavior 

Imports:
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.collections.zombie.zombie_effect.RotationalTurbulenceState
  - model.match_mechanisms.vector.Position
  - model.projectile.Projectile
  - model.utils.GameSession

Methods:
  - handleDamage()
  - isDeflectable()
  - activateSpinning()
  - reflectTowardsPlant()
  - searchClosestPlantInRow()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_defense\ParasolDeflection.java

Package: model.collections.zombie.zombie_defense
Class: ParasolDeflection
Implements: DefenseBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.projectile.ArcMove
  - model.projectile.Projectile
  - model.utils.GameSession

Methods:
  - handleDamage()
  - triggerDeflectionEffect()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_defense\StandardDefense.java

Package: model.collections.zombie.zombie_defense
Class: StandardDefense
Implements: DefenseBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - handleDamage()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_defense\ThermalImmunityDefense.java

Package: model.collections.zombie.zombie_defense
Class: ThermalImmunityDefense
Implements: DefenseBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.projectile.Projectile
  - model.utils.GameSession

Methods:
  - handleDamage()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\EffectStatusFactory.java

Package: model.collections.zombie.zombie_effect
# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\EffectStatusRegistry.java

Package: model.collections.zombie.zombie_effect
Class: EffectStatusRegistry

Imports:
  - java.util.HashMap
  - java.util.List
  - java.util.Map

Methods:
  - createOrNull()
  - gargThreshold()
  - resolveImpAlias()
  - value()
  - getDouble()
  - getInt()
  - getBoolean()
  - getString()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\FireEffect.java

Package: model.collections.zombie.zombie_effect
Class: FireEffect
Implements: ZombieEffectStatus 

Imports:
  - model.collections.Faction
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.pitches.Cell
  - model.utils.GameSession

Methods:
  - isActiveFlame()
  - setActiveFlame()
  - applyTickEffect()
  - applySearingDamage()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\GigantorImpChucker.java

Package: model.collections.zombie.zombie_effect
Class: GigantorImpChucker
Implements: ZombieEffectStatus 

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.projectile.zombie_projectile.GargantuarImpProjectile
  - model.utils.GameSession

Methods:
  - applyTickEffect()
  - executeImpThrow()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\GraveErectorStatus.java

Package: model.collections.zombie.zombie_effect
Class: GraveErectorStatus
Implements: ZombieEffectStatus 

Imports:
  - model.collections.Faction
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.projectile.zombie_projectile.BoneProjectile
  - model.utils.GameSession
  - service.GameClock
  - java.util.ArrayList
  - java.util.Collections
  - java.util.List

Methods:
  - applyTickEffect()
  - launchNecroticSpire()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\IceAgeHunterEffect.java

Package: model.collections.zombie.zombie_effect
Class: IceAgeHunterEffect
Implements: ZombieEffectStatus 

Imports:
  - model.collections.Faction
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.projectile.zombie_projectile.SnowballProjectile
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()
  - dischargeFrostProjectiles()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\KingBuffEffect.java

Package: model.collections.zombie.zombie_effect
Class: KingBuffEffect
Implements: ZombieEffectStatus 

Imports:
  - model.collections.armour.Armour
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()
  - bestowKnightArmor()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\MageState.java

Package: model.collections.zombie.zombie_effect
Class: MageState
Implements: ZombieEffectStatus 

Imports:
  - model.collections.Faction
  - model.collections.Item
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.utils.GameSession
  - service.GameClock
  - java.util.ArrayList
  - java.util.List

Methods:
  - applyTickEffect()
  - onDeath()
  - castHexOnRandomObjective()
  - interceptPhysicalCollisions()
  - applyCurseDebuff()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\OctopusThrow.java

Package: model.collections.zombie.zombie_effect
Class: OctopusThrow
Implements: ZombieEffectStatus 

Imports:
  - model.collections.Faction
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.projectile.zombie_projectile.OctopusProjectile
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()
  - launchEntanglingOctopus()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\PeashooterZombieEffect.java

Package: model.collections.zombie.zombie_effect
Class: PeashooterZombieEffect
Implements: ZombieEffectStatus 

Imports:
  - model.collections.zombie.Zombie
  - model.projectile.zombie_projectile.ZombiePeaProjectile
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\PianistMusicEffect.java

Package: model.collections.zombie.zombie_effect
Class: PianistMusicEffect
Implements: ZombieEffectStatus 

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()
  - triggerZombieRowShift()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\ReelingTackleStatus.java

Package: model.collections.zombie.zombie_effect
Class: ReelingTackleStatus
Implements: ZombieEffectStatus 

Imports:
  - model.collections.Faction
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()
  - launchHookLine()
  - dragPlantTowardZombie()
  - pullHostileZombie()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\RotationalTurbulenceState.java

Package: model.collections.zombie.zombie_effect
Class: RotationalTurbulenceState
Implements: ZombieEffectStatus 

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - service.GameClock

Methods:
  - triggerGyratingState()
  - triggerGyratingState()
  - isActivelyGyrating()
  - applyTickEffect()
  - escalateVelocity()
  - restoreNormalVelocity()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\SunProducer.java

Package: model.collections.zombie.zombie_effect
Class: SunProducer
Implements: ZombieEffectStatus 

Imports:
  - model.collections.item.GroundSun
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\SunThief.java

Package: model.collections.zombie.zombie_effect
Class: SunThief
Implements: ZombieEffectStatus 

Imports:
  - model.collections.Faction
  - model.collections.Item
  - model.collections.item.GroundItem
  - model.collections.item.GroundSun
  - model.collections.item.ItemType
  - model.collections.zombie.Zombie
  - model.pitches.Cell
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()
  - onDeath()
  - handleScavengerBehavior()
  - scanForFallenSun()
  - consumeGroundSun()
  - handleVaultBreaker()
  - detectImminentVegetation()
  - dischargeBeamWeapons()
  - scorchHostilesWithBeam()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\ThermiteExplosion.java

Package: model.collections.zombie.zombie_effect
Class: ThermiteExplosion
Implements: ZombieEffectStatus 

Imports:
  - model.collections.Faction
  - model.collections.zombie.Zombie
  - model.pitches.Cell
  - model.utils.GameSession
  - service.GameClock

Methods:
  - applyTickEffect()
  - detonate()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_effect\ZombieEffectStatus.java

Package: model.collections.zombie.zombie_effect
# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\HypnotizedMoveBehavior.java

Package: model.collections.zombie.zombie_move
Class: HypnotizedMoveBehavior
Implements: MoveBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - move()
  - hasWalkedOffRightBoundary()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\JumpMove.java

Package: model.collections.zombie.zombie_move
Class: JumpMove
Implements: MoveBehavior 

Imports:
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.Environment
  - model.utils.GameSession
  - java.util.List

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\MoveBehavior.java

Package: model.collections.zombie.zombie_move
# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\MoveBehaviorFactory.java

Package: model.collections.zombie.zombie_move
# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\MoveBehaviorRegistry.java

Package: model.collections.zombie.zombie_move
Class: MoveBehaviorRegistry

Imports:
  - model.collections.zombie.BehaviorSpec
  - java.util.ArrayList
  - java.util.HashMap
  - java.util.List
  - java.util.Map

Methods:
  - register()
  - create()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\NormalWalk.java

Package: model.collections.zombie.zombie_move
Class: NormalWalk
Implements: MoveBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\ProspectorMove.java

Package: model.collections.zombie.zombie_move
Class: ProspectorMove
Implements: MoveBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - extinguishDynamite()
  - litDynamite()
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\PusherMove.java

Package: model.collections.zombie.zombie_move
Class: PusherMove
Implements: MoveBehavior 

Imports:
  - model.collections.Faction
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.collections.zombie.zombie_pushing_item.PushableStructure
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.Environment
  - model.utils.GameSession

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\SnorkelMove.java

Package: model.collections.zombie.zombie_move
Class: SnorkelMove
Implements: MoveBehavior 

Imports:
  - model.collections.zombie.VulnerabilityType
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieState
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\SprintMove.java

Package: model.collections.zombie.zombie_move
Class: SprintMove
Implements: MoveBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - move()
  - getActiveSpeedX()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\StationaryMove.java

Package: model.collections.zombie.zombie_move
Class: StationaryMove
Implements: MoveBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_move\StunnedMoveBehavior.java

Package: model.collections.zombie.zombie_move
Class: StunnedMoveBehavior
Implements: MoveBehavior 

Imports:
  - model.collections.zombie.Zombie
  - model.utils.GameSession

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\zombie_pushing_item\PushableStructure.java

Package: model.collections.zombie.zombie_pushing_item
Class: PushableStructure

Imports:
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match_mechanisms.vector.Position
  - model.pitches.obstacles.PushableType
  - model.utils.GameSession

Methods:
  - isAlive()
  - getType()
  - getPosition()
  - setPosition()
  - getHp()
  - setHp()
  - getOwner()
  - setOwner()
  - takeDamage()
  - onDestroyed()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\ZombieFactory.java

Package: model.collections.zombie
Class: ZombieFactory

Imports:
  - com.google.gson.Gson
  - com.google.gson.reflect.TypeToken
  - model.collections.armour.Armour
  - model.collections.armour.ArmourType
  - model.collections.armour.ZombieArmour
  - model.collections.zombie.zombie_attack.AttackBehaviorRegistry
  - model.collections.zombie.zombie_defense.DefenseBehaviorRegistry
  - model.collections.zombie.zombie_effect.EffectStatusRegistry
  - model.collections.zombie.zombie_move.MoveBehaviorRegistry
  - model.collections.zombie.zombie_pushing_item.PushableStructure
  - model.match_mechanisms.vector.Position
  - model.pitches.obstacles.PushableType
  - model.utils.GameSession
  - model.utils.ResourceResolver
  - java.io.IOException
  - java.lang.reflect.Type
  - java.util.HashMap
  - java.util.List
  - java.util.Map
  - java.util.Set

Methods:
  - init()
  - loadZombies()
  - loadArmorData()
  - getZombieCost()
  - create()
  - buildBaseZombie()
  - applyDifficultyScaling()
  - attachPushedStructureIfNeeded()
  - respawnPushedStructureIfNeeded()
  - placeOnLawnIfPossible()
  - resolveArmor()
  - createKnightArmor()
  - parseRtidAlias()
  - resolveArmorType()
  - getAllZombieAliases()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\collections\zombie\ZombieRace.java

Package: model.collections.zombie
# d:\College\AP\project gitlab\src\model\collections\zombie\ZombieState.java

Package: model.collections.zombie
# d:\College\AP\project gitlab\src\model\collections\zombie\ZombieTemplate.java

Package: model.collections.zombie
# d:\College\AP\project gitlab\src\model\game_exceptions\GameException.java

Package: model.game_exceptions
Class: GameException
Extends: RuntimeException

Imports:

Methods:

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\greenhouse\Greenhouse.java

Package: model.greenhouse
Class: Greenhouse

Imports:
  - model.collections.plant.PlantFactory
  - java.util.ArrayList
  - java.util.List

Methods:
  - getInstance()
  - resetToFreshLayout()
  - getColCount()
  - getRowCount()
  - getPots()
  - setPots()
  - getPot()
  - countUnlockedPots()
  - unlockNextLockedPot()
  - serialize()
  - load()
  - isValidSave()
  - renderStatus()
  - formatDuration()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\greenhouse\GreenhousePlant.java

Package: model.greenhouse
Class: GreenhousePlant
Extends: PotPlant

Imports:

Methods:
  - isMarigold()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\greenhouse\Marigold.java

Package: model.greenhouse
Class: Marigold
Extends: PotPlant

Imports:

Methods:
  - isMarigold()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\greenhouse\Pot.java

Package: model.greenhouse
Class: Pot

Imports:
  - controller.menus.greenhouse.PotController

Methods:
  - freePot()
  - unlockPot()
  - getUnlockCost()
  - getRow()
  - getCol()
  - getPotPlant()
  - isLocked()
  - getPotController()
  - setLocked()
  - setPotPlant()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\greenhouse\PotData.java

Package: model.greenhouse
Class: PotData

Imports:

Methods:

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\greenhouse\PotPlant.java

Package: model.greenhouse
Class: PotPlant

Imports:

Methods:
  - getPlantedAtMillis()
  - setPlantedAtMillis()
  - isCollectAble()
  - getRemainingSeconds()
  - growInstantly()
  - isMarigold()
  - getPot()
  - getPlantId()
  - getPlantName()
  - getAwardCoins()
  - setPot()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\greenhouse\store\Product.java

Package: model.greenhouse.store
# d:\College\AP\project gitlab\src\model\greenhouse\store\Store.java

Package: model.greenhouse.store
Class: Store

Imports:
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.match_mechanisms.seed_packets.RandomSeedPacket
  - model.match_mechanisms.seed_packets.SelectableSeedPacket
  - model.user_data.UserState
  - java.time.Duration
  - java.time.Instant
  - java.util.ArrayList
  - java.util.List
  - java.util.Map
  - java.util.Random

Methods:
  - Random()
  - renderPermanentGoods()
  - renderDailyOffer()
  - refreshDailyOffer()
  - buy()
  - buyPot()
  - buyPlantFood()
  - buyRandomSeedPacket()
  - buySelectedSeedPacket()
  - exchange()
  - buyDailyOffer()
  - nameOf()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\maps\MapMetadata.java

Package: model.maps
Class: MapMetadata

Imports:

Methods:
  - tileWidthPx()
  - tileHeightPx()
  - cellPixelX()
  - cellPixelY()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\Level.java

Package: model.match.main.levels
Class: Level

Imports:
  - model.match.main.season.Season
  - model.match_mechanisms.ZombieWave
  - model.utils.GameSession
  - java.util.ArrayList
  - java.util.LinkedHashSet
  - java.util.List

Methods:
  - getId()
  - setId()
  - getName()
  - setName()
  - getSeason()
  - setSeason()
  - getRows()
  - setRows()
  - getCols()
  - setCols()
  - getInitialSun()
  - setInitialSun()
  - getGameMode()
  - setGameMode()
  - getWaves()
  - setWaves()
  - getAvailablePlants()
  - setAvailablePlants()
  - getForcedPlants()
  - setForcedPlants()
  - getZombiePool()
  - setZombiePool()
  - getCurrentTideColumn()
  - setCurrentTideColumn()
  - getMaxTideColumn()
  - setMaxTideColumn()
  - updateTide()
  - initSpecial()
  - checkLossCondition()
  - checkWinCondition()
  - isSkySunEnabled()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\normal_levels\NormalLevel.java

Package: model.match.main.levels.normal_levels
Class: NormalLevel
Extends: Level

Imports:
  - model.match.main.levels.Level

Methods:

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\BossLevel.java

Package: model.match.main.levels.special_levels
Class: BossLevel
Extends: Level

Imports:
  - model.collections.zombie.Zombie
  - model.match.main.levels.Level

Methods:
  - getBossZombie()
  - setBossZombie()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\BossLevelFactory.java

Package: model.match.main.levels.special_levels
Class: BossLevelFactory

Imports:
  - model.collections.zombie.ZombieFactory
  - model.match.main.season.Season
  - model.match_mechanisms.ZombieWave
  - java.util.Collections
  - java.util.List

Methods:
  - createBossLevel()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\ConveyorBeltLevel.java

Package: model.match.main.levels.special_levels
Class: ConveyorBeltLevel
Extends: Level

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.match.main.levels.Level
  - model.utils.GameSession
  - service.GameClock
  - java.util.List
  - java.util.Random

Methods:
  - Random()
  - initSpecial()
  - tickConveyor()
  - offerNextPlant()
  - getCurrentPlant()
  - takeCurrentPlant()
  - getConveyorPlants()
  - setConveyorPlants()
  - getMaxConveyorSize()
  - setMaxConveyorSize()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\DeadLineLevel.java

Package: model.match.main.levels.special_levels
Class: DeadLineLevel
Extends: Level

Imports:
  - model.match.main.levels.Level
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - checkLossCondition()
  - getDeadLine()
  - setDeadLine()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\IntroductionLevel.java

Package: model.match.main.levels.special_levels
Class: IntroductionLevel
Extends: Level

Imports:
  - model.match.main.levels.Level
  - model.utils.GameSession
  - view.GeneralPrinter

Methods:
  - initSpecial()
  - introductionHandle()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\LockedPlantsLevel.java

Package: model.match.main.levels.special_levels
Class: LockedPlantsLevel
Extends: Level

Imports:
  - model.match.main.levels.Level
  - java.util.List

Methods:
  - isPlantLocked()
  - containsIgnoreCase()
  - getLockedPlants()
  - setLockedPlants()
  - getAlwaysAvailable()
  - setAlwaysAvailable()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\LoveYourPlantsLevel.java

Package: model.match.main.levels.special_levels
Class: LoveYourPlantsLevel
Extends: Level

Imports:
  - model.match.main.levels.Level
  - model.utils.GameSession

Methods:
  - initSpecial()
  - recordPlantLoss()
  - checkLossCondition()
  - getPlantsLost()
  - getMaxPlantLoss()
  - setMaxPlantLoss()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\NightOpsLevel.java

Package: model.match.main.levels.special_levels
Class: NightOpsLevel
Extends: Level

Imports:
  - model.match.main.levels.Level

Methods:
  - isSkySunEnabled()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\PlantWhatYouGetLevel.java

Package: model.match.main.levels.special_levels
Class: PlantWhatYouGetLevel
Extends: Level

Imports:
  - model.match.main.levels.Level
  - model.utils.GameSession

Methods:
  - initSpecial()
  - isSkySunEnabled()
  - handleBanSunflower()
  - startWave()
  - getPrimarySun()
  - setPrimarySun()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\SaveOurSeedsLevel.java

Package: model.match.main.levels.special_levels
Class: SaveOurSeedsLevel
Extends: Level

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.match.main.levels.Level
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.utils.GameSession
  - java.util.HashSet
  - java.util.Map
  - java.util.Set

Methods:
  - initSpecial()
  - checkLossCondition()
  - getSeedPositions()
  - setSeedPositions()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\levels\special_levels\TimedWarLevel.java

Package: model.match.main.levels.special_levels
Class: TimedWarLevel
Extends: Level

Imports:
  - model.match.main.levels.Level
  - model.match_mechanisms.Time
  - model.utils.GameSession

Methods:
  - initSpecial()
  - tickTimer()
  - recordZombieKill()
  - checkLossCondition()
  - checkWinCondition()
  - getZombiesKilledSoFar()
  - getTimeLimit()
  - setTimeLimit()
  - getZombiesToKill()
  - setZombiesToKill()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\MainMode.java

Package: model.match.main
Class: MainMode
Extends: Match

Imports:
  - model.match.Match
  - model.match.main.season.Season

Methods:

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\Season.java

Package: model.match.main.season
Class: Season

Imports:
  - model.match.main.season.travellog.TravelLog
  - model.pitches.obstacles.ObstacleInformation
  - java.util.ArrayList

Methods:
  - getName()
  - hasTide()
  - isNight()
  - applyPerTickEffect()
  - onWaveStart()
  - placeSeasonObstacles()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\SeasonFactory.java

Package: model.match.main.season
Class: SeasonFactory

Imports:
  - model.match.main.season.travellog.beach.Beach
  - model.match.main.season.travellog.cave.Cave
  - model.match.main.season.travellog.darkage.DarkAge
  - model.match.main.season.travellog.egypt.Egypt

Methods:
  - create()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\travellog\beach\Beach.java

Package: model.match.main.season.travellog.beach
Class: Beach
Extends: Season

Imports:
  - model.match.main.season.Season
  - model.utils.GameSession

Methods:
  - hasTide()
  - onWaveStart()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\travellog\beach\Flood.java

Package: model.match.main.season.travellog.beach
Class: Flood

Imports:
  - model.collections.plant.Plant
  - model.collections.plant.PlantTag
  - model.collections.zombie.VulnerabilityType
  - model.collections.zombie.Zombie
  - model.collections.zombie.zombie_move.SnorkelMove
  - model.match.main.levels.Level
  - model.pitches.Cell
  - model.pitches.Tile
  - model.pitches.TileType
  - model.utils.GameSession

Methods:
  - initialize()
  - riselevel()
  - falllevel()
  - riselevel()
  - falllevel()
  - apply()
  - isWaterSafe()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\travellog\cave\Cave.java

Package: model.match.main.season.travellog.cave
Class: Cave
Extends: Season

Imports:
  - model.collections.zombie.Zombie
  - model.match.main.season.Season
  - model.pitches.Cell
  - model.pitches.Environment
  - model.pitches.Tile
  - model.pitches.TileType
  - model.pitches.obstacles.SlipperyDirection
  - model.utils.GameSession
  - java.util.ArrayList
  - java.util.Random

Methods:
  - Random()
  - hasIceTiles()
  - placeSeasonObstacles()
  - meltIce()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\travellog\darkage\DarkAge.java

Package: model.match.main.season.travellog.darkage
Class: DarkAge
Extends: Season

Imports:
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match.main.season.Season
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.Environment
  - model.pitches.obstacles.Grave
  - model.utils.GameSession
  - java.util.Random

Methods:
  - Random()
  - isNight()
  - placeSeasonObstacles()
  - onWaveStart()
  - necromancy()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\travellog\egypt\Egypt.java

Package: model.match.main.season.travellog.egypt
Class: Egypt
Extends: Season

Imports:
  - model.match.main.season.Season
  - model.pitches.Cell
  - model.pitches.Environment
  - model.pitches.obstacles.Grave
  - model.utils.GameSession
  - java.util.Random

Methods:
  - Random()
  - placeSeasonObstacles()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\travellog\egypt\SandStorm.java

Package: model.match.main.season.travellog.egypt
Class: SandStorm

Imports:
  - java.util.Random

Methods:
  - Random()
  - sandstorm()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\main\season\travellog\TravelLog.java

Package: model.match.main.season.travellog
Class: TravelLog

Imports:

Methods:

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\Match.java

Package: model.match
Class: Match

Imports:

Methods:

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\Beghouled.java

Package: model.match.mini_games
Class: Beghouled
Extends: MiniGameMode

Imports:
  - model.collections.item.GroundItem
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.Environment
  - model.pitches.obstacles.Crater
  - model.utils.GameSession
  - view.GeneralPrinter
  - java.util.
  - java.util.stream.Collectors

Methods:
  - Random()
  - plantPoolFor()
  - buildUpgradePaths()
  - seedBoard()
  - placeRandomPlant()
  - isCrater()
  - trySwap()
  - inBounds()
  - areAdjacent()
  - swapCells()
  - hasMatchThrough()
  - matchLength()
  - plantIdAt()
  - resolveMatches()
  - finishResolution()
  - awardSun()
  - findMatchedGroups()
  - groupConnectedCells()
  - neighborsOf()
  - key()
  - scanLineForMatches()
  - applyGravityAndRefill()
  - applyGravityToColumn()
  - moveDown()
  - fillRemainingGaps()
  - anyMoveWouldMatch()
  - wouldMatchIfSwapped()
  - resetBoard()
  - ensurePlayableBoard()
  - hasAnyMatch()
  - upgrade()
  - normaliseName()
  - tick()
  - occupiedPlantCells()
  - markCratersWherePlantsWereEaten()
  - spawnEndlessWave()
  - isWon()
  - isLost()
  - getMatchesMade()
  - getMatchesNeeded()
  - getSession()
  - getZombiePool()
  - getBoardPlantIds()
  - collectItemsAt()
  - addSunCheat()
  - renderPlantsInfo()
  - renderZombiesInfo()
  - renderState()
  - renderGroundItems()
  - log()
  - zombiePoolFor()
  - UpgradePath()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\izombie\Brain.java

Package: model.match.mini_games.izombie
Class: Brain

Imports:
  - model.match_mechanisms.vector.Position

Methods:
  - getPosition()
  - isEaten()
  - markEaten()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\izombie\IZombie.java

Package: model.match.mini_games.izombie
Class: IZombie
Extends: MiniGameMode

Imports:
  - model.collections.item.GroundItem
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match.mini_games.MiniGameMode
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.Environment
  - model.utils.GameSession
  - view.GeneralPrinter
  - java.util.
  - java.util.stream.Collectors
  - java.util.stream.IntStream

Methods:
  - Random()
  - buildRosters()
  - seedDefendingPlants()
  - spawnSunZombies()
  - placeZombie()
  - placeZombie()
  - findRosterAlias()
  - tick()
  - detectSunZombieDeaths()
  - generateSunFromSunZombies()
  - currentProductionInterval()
  - checkBrainsEaten()
  - isWon()
  - isLost()
  - getRoster()
  - getBrains()
  - getSession()
  - getSunZombies()
  - getRedLineColumn()
  - getSunProductionInterval()
  - isSunProducer()
  - collectItemsAt()
  - addSunCheat()
  - renderPlantAt()
  - renderDefendingPlants()
  - renderZombiesInfo()
  - renderState()
  - renderGroundItems()
  - symbolAt()
  - renderPlants()
  - renderZombies()
  - log()
  - buildRosters()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\MiniGameMode.java

Package: model.match.mini_games
Class: MiniGameMode
Extends: Match

Imports:
  - model.match.Match
  - model.user_data.User
  - model.utils.GameSession

Methods:
  - getDifficulty()
  - setDifficulty()
  - getGameMode()
  - getStageDetails()
  - configureSession()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\MiniGameWaves.java

Package: model.match.mini_games
Class: MiniGameWaves

Imports:
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match_mechanisms.ZombieWave
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - java.util.ArrayList
  - java.util.List

Methods:
  - create()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\vasebreaker\vase\GargantuarVase.java

Package: model.match.mini_games.vasebreaker.vase
Class: GargantuarVase
Extends: Vase

Imports:
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match.mini_games.vasebreaker.Vasebreaker
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - getVaseType()
  - onBreak()
  - getRevealedContents()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\vasebreaker\vase\PlantVase.java

Package: model.match.mini_games.vasebreaker.vase
Class: PlantVase
Extends: Vase

Imports:
  - model.match.mini_games.vasebreaker.Vasebreaker
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - getPlantId()
  - getVaseType()
  - getRevealedContents()
  - onBreak()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\vasebreaker\vase\RandomVase.java

Package: model.match.mini_games.vasebreaker.vase
Class: RandomVase
Extends: Vase

Imports:
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match.mini_games.vasebreaker.Vasebreaker
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - java.util.Random

Methods:
  - Random()
  - getContent()
  - getRevealedContents()
  - onBreak()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\vasebreaker\vase\Vase.java

Package: model.match.mini_games.vasebreaker.vase
Class: Vase

Imports:
  - model.match.mini_games.vasebreaker.Vasebreaker
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - getPosition()
  - isBroken()
  - getVaseType()
  - getDisplayName()
  - getMapSymbol()
  - getRevealedContents()
  - breakVase()
  - onBreak()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\vasebreaker\vase\ZombieVase.java

Package: model.match.mini_games.vasebreaker.vase
Class: ZombieVase
Extends: Vase

Imports:
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match.mini_games.vasebreaker.Vasebreaker
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - getVaseType()
  - onBreak()
  - getRevealedContents()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\vasebreaker\Vasebreaker.java

Package: model.match.mini_games.vasebreaker
Class: Vasebreaker
Extends: MiniGameMode

Imports:
  - model.collections.item.GroundItem
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.collections.zombie.Zombie
  - model.match.mini_games.MiniGameMode
  - model.match.mini_games.vasebreaker.vase.
  - model.match_mechanisms.vector.Position
  - model.pitches.Environment
  - model.utils.GameSession
  - view.GeneralPrinter
  - java.util.
  - java.util.stream.Collectors

Methods:
  - Random()
  - normalisePlantIds()
  - layoutVases()
  - randomPlantId()
  - candidateSpots()
  - breakVaseAt()
  - announceBreakResult()
  - dropSeedPacket()
  - collectSeedPacket()
  - plantSeed()
  - plantSeed()
  - findPlantId()
  - plantName()
  - tick()
  - isWon()
  - isLost()
  - getSession()
  - getVases()
  - getDroppedPackets()
  - getZombiePool()
  - getSeedInventory()
  - getSeedInventoryNames()
  - getVaseAt()
  - renderZombiesInfo()
  - renderPlantsInfo()
  - renderState()
  - renderGroundItems()
  - renderSeeds()
  - log()
  - zombiePoolFor()
  - DroppedSeedPacket()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\wallnutbowlling\nut\BigNut.java

Package: model.match.mini_games.wallnutbowlling.nut
Class: BigNut
Extends: Nut

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - onHitZombie()
  - getKindName()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\wallnutbowlling\nut\BowlingWallnut.java

Package: model.match.mini_games.wallnutbowlling.nut
Class: BowlingWallnut
Extends: Nut

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - onHitZombie()
  - getKindName()
  - getHitsSoFar()
  - turnAfterHit()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\wallnutbowlling\nut\ExplodeONut.java

Package: model.match.mini_games.wallnutbowlling.nut
Class: ExplodeONut
Extends: Nut

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - onHitZombie()
  - getKindName()
  - withinBlast()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\wallnutbowlling\nut\Nut.java

Package: model.match.mini_games.wallnutbowlling.nut
Class: Nut

Imports:
  - model.collections.zombie.Zombie
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession

Methods:
  - getPosition()
  - setPosition()
  - isAlive()
  - kill()
  - getKindName()
  - move()
  - bounceVertical()
  - onHitZombie()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\wallnutbowlling\WallnutBowling.java

Package: model.match.mini_games.wallnutbowlling
Class: WallnutBowling
Extends: MiniGameMode

Imports:
  - model.collections.item.GroundItem
  - model.collections.zombie.Zombie
  - model.match.mini_games.MiniGameMode
  - model.match.mini_games.MiniGameWaves
  - model.match.mini_games.wallnutbowlling.nut.BigNut
  - model.match.mini_games.wallnutbowlling.nut.BowlingWallnut
  - model.match.mini_games.wallnutbowlling.nut.ExplodeONut
  - model.match.mini_games.wallnutbowlling.nut.Nut
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - view.GeneralPrinter
  - java.util.

Methods:
  - Random()
  - initialiseConveyor()
  - randomNutKind()
  - updateNextNut()
  - getNextNutKind()
  - getConveyorBelt()
  - getAvailableNutKinds()
  - getConveyorSecondsUntilNextNut()
  - plantNut()
  - plantNut()
  - kindMatches()
  - findRequestedKind()
  - tick()
  - moveNuts()
  - resolveCollisions()
  - isTouching()
  - isWon()
  - isLost()
  - getSession()
  - getRedLineColumn()
  - getActiveNuts()
  - getZombiePool()
  - collectItemsAt()
  - renderState()
  - renderZombiesInfo()
  - log()
  - zombiePoolFor()
  - wavesFor()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match\mini_games\Zombotany.java

Package: model.match.mini_games
Class: Zombotany
Extends: MiniGameMode

Imports:
  - model.collections.item.GroundItem
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - view.GeneralPrinter
  - java.util.

Methods:
  - Random()
  - spawnZombieForWave()
  - pickSpecialAlias()
  - tick()
  - configureSpecialZombies()
  - increaseJalapenoSpeed()
  - tickPeashooterZombies()
  - shootNearestPlantInRow()
  - tickJalapenoZombies()
  - burnRow()
  - tickSquashZombies()
  - isTouching()
  - isAlias()
  - forgetDeadZombies()
  - isWon()
  - isLost()
  - getSession()
  - getAvailablePlants()
  - getZombiePool()
  - getJalapenoSpeedMultiplier()
  - collectItemsAt()
  - addSunCheat()
  - renderZombiesInfo()
  - renderPlantsInfo()
  - plantAt()
  - renderState()
  - log()
  - availablePlantsFor()
  - wavesFor()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\Attack.java

Package: model.match_mechanisms
# d:\College\AP\project gitlab\src\model\match_mechanisms\Capacities.java

Package: model.match_mechanisms
Class: Capacities

Imports:

Methods:
  - getCAPACITY()
  - getMatchPlantsCapacity()
  - setMatchPlantsCapacity()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\plant_food\PlantFood.java

Package: model.match_mechanisms.plant_food
Class: PlantFood

Imports:
  - model.match_mechanisms.vector.Position

Methods:
  - collect()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\plant_food\PlantFoodFactory.java

Package: model.match_mechanisms.plant_food
Class: PlantFoodFactory

Imports:
  - model.match_mechanisms.vector.Position

Methods:
  - createPlantFood()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\Pluck.java

Package: model.match_mechanisms
# d:\College\AP\project gitlab\src\model\match_mechanisms\Rule.java

Package: model.match_mechanisms
# d:\College\AP\project gitlab\src\model\match_mechanisms\seed_packets\RandomSeedPacket.java

Package: model.match_mechanisms.seed_packets
Class: RandomSeedPacket
Extends: SeedPacket

Imports:
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.user_data.UserState
  - java.util.ArrayList
  - java.util.List
  - java.util.Random

Methods:
  - Random()
  - open()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\seed_packets\SeedPacket.java

Package: model.match_mechanisms.seed_packets
Class: SeedPacket

Imports:
  - model.user_data.UserState

Methods:
  - getName()
  - getCount()
  - addCount()
  - consumeOne()
  - open()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\seed_packets\SelectableSeedPacket.java

Package: model.match_mechanisms.seed_packets
Class: SelectableSeedPacket
Extends: SeedPacket

Imports:
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.user_data.UserState

Methods:
  - openWithChoice()
  - open()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\sun\NormalSun.java

Package: model.match_mechanisms.sun
Class: NormalSun
Extends: Sun

Imports:

Methods:
  - getSunAmount()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\sun\RadioactivateSun.java

Package: model.match_mechanisms.sun
Class: RadioactivateSun
Extends: Sun

Imports:

Methods:
  - getSunAmount()
  - explode()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\sun\SpecialSun.java

Package: model.match_mechanisms.sun
Class: SpecialSun
Extends: Sun

Imports:

Methods:
  - getSunAmount()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\sun\Sun.java

Package: model.match_mechanisms.sun
Class: Sun

Imports:
  - model.match_mechanisms.vector.Position

Methods:
  - dispose()
  - getSunAmount()
  - isFallen()
  - setFallen()
  - getFallPosition()
  - setFallPosition()
  - getFallSpeed()
  - setFallSpeed()
  - dropPositionEngine()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\sun\SunFactory.java

Package: model.match_mechanisms.sun
Class: SunFactory

Imports:
  - model.match_mechanisms.Time
  - model.match_mechanisms.vector.Position
  - java.util.Random

Methods:
  - Random()
  - startFactory()
  - createSkySun()
  - createPlantSun()
  - getOrigin()
  - getCreationDelay()
  - setCreationDelay()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\Time.java

Package: model.match_mechanisms
Class: Time

Imports:
  - service.GameClock

Methods:
  - getTick()
  - setTick()
  - tick()
  - getSecondsRemaining()
  - isZero()
  - isRunning()
  - setRunning()
  - reset()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\match_mechanisms\vector\Position.java

Package: model.match_mechanisms.vector
# d:\College\AP\project gitlab\src\model\match_mechanisms\ZombieWave.java

Package: model.match_mechanisms
Class: ZombieWave

Imports:
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - java.util.List

Methods:
  - getDelay()
  - setDelay()
  - getWaveZombies()
  - setWaveZombies()
  - isFinalWave()
  - setFinalWave()
  - getWaveCost()
  - toString()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\news\News.java

Package: model.news
Class: News

Imports:

Methods:
  - getText()
  - setText()
  - isRead()
  - setRead()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\Cell.java

Package: model.pitches
Class: Cell

Imports:
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.collections.zombie.zombie_pushing_item.PushableStructure
  - model.pitches.obstacles.Obstacle
  - java.util.ArrayList
  - java.util.List

Methods:
  - getRow()
  - getCol()
  - getPlant()
  - setPlant()
  - hasPlant()
  - getObstacle()
  - setObstacle()
  - getStructure()
  - setStructure()
  - getInteractableStructure()
  - getTile()
  - setTile()
  - getZombies()
  - addZombie()
  - removeZombie()
  - clearZombies()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\Environment.java

Package: model.pitches
Class: Environment

Imports:

Methods:
  - getCell()
  - getRowCells()
  - getRows()
  - getCols()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\LawnMower.java

Package: model.pitches
Class: LawnMower

Imports:
  - model.collections.zombie.Zombie
  - view.GeneralPrinter
  - java.util.List

Methods:
  - killZombiesInRow()
  - isUsed()
  - setUsed()
  - getRowNumber()
  - getRow()
  - setRow()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\obstacles\Crater.java

Package: model.pitches.obstacles
Class: Crater
Implements: Obstacle 

Imports:

Methods:
  - blocksPlanting()
  - getName()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\obstacles\Grave.java

Package: model.pitches.obstacles
Class: Grave
Implements: Obstacle 

Imports:

Methods:
  - blocksPlanting()
  - getName()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\obstacles\IceBlock.java

Package: model.pitches.obstacles
Class: IceBlock
Implements: Obstacle 

Imports:
  - model.collections.plant.Plant

Methods:
  - takeDamage()
  - release()
  - getFrozenPlant()
  - getHp()
  - blocksPlanting()
  - getName()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\obstacles\Obstacle.java

Package: model.pitches.obstacles
# d:\College\AP\project gitlab\src\model\pitches\obstacles\ObstacleFactory.java

Package: model.pitches.obstacles
Class: ObstacleFactory

Imports:

Methods:
  - create()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\obstacles\ObstacleInformation.java

Package: model.pitches.obstacles
# d:\College\AP\project gitlab\src\model\pitches\obstacles\OctopusWrap.java

Package: model.pitches.obstacles
Class: OctopusWrap
Implements: Obstacle 

Imports:
  - model.collections.plant.Plant

Methods:
  - takeDamage()
  - release()
  - getWrappedPlant()
  - getHp()
  - blocksPlanting()
  - getName()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\pitches\obstacles\PushableType.java

Package: model.pitches.obstacles
# d:\College\AP\project gitlab\src\model\pitches\obstacles\SlipperyDirection.java

Package: model.pitches.obstacles
# d:\College\AP\project gitlab\src\model\pitches\Tile.java

Package: model.pitches
# d:\College\AP\project gitlab\src\model\pitches\TileType.java

Package: model.pitches
# d:\College\AP\project gitlab\src\model\projectile\ArcMove.java

Package: model.projectile
Class: ArcMove
Implements: MoveStrategy 

Imports:
  - model.match_mechanisms.vector.Position
  - service.GameClock

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\BounceMove.java

Package: model.projectile
Class: BounceMove
Implements: MoveStrategy 

Imports:
  - model.match_mechanisms.vector.Position
  - service.GameClock

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\hit\ButterHit.java

Package: model.projectile.hit
Class: ButterHit
Implements: HitEffectStrategy 

Imports:
  - model.collections.zombie.Zombie

Methods:
  - apply()
  - getAreaLength()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\hit\FireHit.java

Package: model.projectile.hit
Class: FireHit
Implements: HitEffectStrategy 

Imports:
  - model.collections.zombie.Zombie

Methods:
  - apply()
  - getAreaLength()
  - getDamageMultiplier()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\hit\HitEffectStrategy.java

Package: model.projectile.hit
# d:\College\AP\project gitlab\src\model\projectile\hit\IceHit.java

Package: model.projectile.hit
Class: IceHit
Implements: HitEffectStrategy 

Imports:
  - model.collections.zombie.Zombie

Methods:
  - apply()
  - getAreaLength()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\hit\NormalHit.java

Package: model.projectile.hit
Class: NormalHit
Implements: HitEffectStrategy 

Imports:
  - model.collections.zombie.Zombie

Methods:
  - apply()
  - getAreaLength()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\hit\PierceHit.java

Package: model.projectile.hit
Class: PierceHit
Implements: HitEffectStrategy 

Imports:
  - model.collections.zombie.Zombie

Methods:
  - apply()
  - getPierceCount()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\hit\PierceKnockBackHit.java

Package: model.projectile.hit
Class: PierceKnockBackHit
Implements: HitEffectStrategy 

Imports:
  - model.collections.zombie.Zombie

Methods:
  - apply()
  - getPierceCount()
  - getKnockbackDistance()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\hit\PoisonHit.java

Package: model.projectile.hit
Class: PoisonHit
Implements: HitEffectStrategy 

Imports:
  - model.collections.zombie.Zombie

Methods:
  - apply()
  - getAreaLength()
  - bypassesArmor()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\MoveStrategy.java

Package: model.projectile
# d:\College\AP\project gitlab\src\model\projectile\Projectile.java

Package: model.projectile
Class: Projectile
Extends: Item

Imports:
  - model.collections.Item
  - model.collections.plant.Plant
  - model.collections.zombie.Zombie
  - model.collections.zombie.zombie_pushing_item.PushableStructure
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.projectile.hit.HitEffectStrategy
  - model.utils.GameSession
  - java.util.ArrayList
  - java.util.Comparator
  - java.util.IdentityHashMap
  - java.util.List
  - java.util.Set

Methods:
  - setStunning()
  - tick()
  - hitOriginalTarget()
  - hitZombie()
  - applyDamageAndEffect()
  - getEffectiveDamage()
  - isValidTarget()
  - findFirstStructureCollision()
  - collisionProjection()
  - isOutsideLawn()
  - resolveTargetPosition()
  - setHitEffectStrategy()
  - getHitEffectStrategy()
  - getMoveStrategy()
  - getDamage()
  - getSourcePlant()
  - ZombieHit()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\ProjectileType.java

Package: model.projectile
# d:\College\AP\project gitlab\src\model\projectile\StraightMove.java

Package: model.projectile
Class: StraightMove
Implements: MoveStrategy 

Imports:
  - model.match_mechanisms.vector.Position
  - service.GameClock

Methods:
  - move()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\zombie_projectile\BoneProjectile.java

Package: model.projectile.zombie_projectile
Class: BoneProjectile
Extends: ZombieProjectile

Imports:
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.obstacles.ObstacleFactory
  - model.pitches.obstacles.ObstacleInformation
  - model.utils.GameSession

Methods:
  - updateFlightPath()
  - onDestinationReached()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\zombie_projectile\GargantuarImpProjectile.java

Package: model.projectile.zombie_projectile
Class: GargantuarImpProjectile
Extends: ZombieProjectile

Imports:
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.utils.GameSession

Methods:
  - updateFlightPath()
  - onDestinationReached()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\zombie_projectile\OctopusProjectile.java

Package: model.projectile.zombie_projectile
Class: OctopusProjectile
Extends: ZombieProjectile

Imports:
  - model.collections.plant.Plant
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.obstacles.OctopusWrap
  - model.utils.GameSession

Methods:
  - updateFlightPath()
  - onDestinationReached()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\zombie_projectile\SnowballProjectile.java

Package: model.projectile.zombie_projectile
Class: SnowballProjectile
Extends: ZombieProjectile

Imports:
  - model.collections.plant.Plant
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.pitches.obstacles.IceBlock
  - model.utils.GameSession

Methods:
  - updateFlightPath()
  - onDestinationReached()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\zombie_projectile\ZombiePeaProjectile.java

Package: model.projectile.zombie_projectile
Class: ZombiePeaProjectile
Extends: ZombieProjectile

Imports:
  - model.match_mechanisms.vector.Position
  - model.pitches.Cell
  - model.utils.GameSession

Methods:
  - updateFlightPath()
  - onDestinationReached()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\projectile\zombie_projectile\ZombieProjectile.java

Package: model.projectile.zombie_projectile
Class: ZombieProjectile
Extends: Item

Imports:
  - model.collections.Item
  - model.collections.plant.Plant
  - model.match_mechanisms.vector.Position
  - model.utils.GameSession
  - service.GameClock
  - java.lang.reflect.Field

Methods:
  - tick()
  - updateFlightPath()
  - onDestinationReached()
  - isPlantIncapacitated()
  - getChillLevel()
  - setChillLevel()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\quests\GameQuest.java

Package: model.quests
Class: GameQuest
Extends: Quest

Imports:
  - java.util.List

Methods:
  - getId()
  - setId()
  - getTitle()
  - setTitle()
  - getType()
  - setType()
  - getPriority()
  - setPriority()
  - getExpiresAfterSeconds()
  - setExpiresAfterSeconds()
  - getCriteria()
  - setCriteria()
  - getReward()
  - setReward()
  - getAssignedAtEpochSecond()
  - setAssignedAtEpochSecond()
  - isExpired()
  - isRewardCollected()
  - setRewardCollected()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\quests\Quest.java

Package: model.quests
Class: Quest

Imports:

Methods:
  - getQuestDescription()
  - setQuestDescription()
  - getProgress()
  - setProgress()
  - isCompleted()
  - setCompleted()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\quests\QuestCriterion.java

Package: model.quests
Class: QuestCriterion

Imports:
  - java.util.Map

Methods:
  - getType()
  - setType()
  - getTarget()
  - setTarget()
  - getParams()
  - setParams()
  - getVariableParam()
  - setVariableParam()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\quests\QuestLoader.java

Package: model.quests
Class: QuestLoader

Imports:
  - com.google.gson.Gson
  - com.google.gson.reflect.TypeToken
  - model.user_data.User
  - model.user_data.UserState
  - java.io.
  - java.lang.reflect.Type
  - java.nio.charset.StandardCharsets
  - java.time.Instant
  - java.util.

Methods:
  - loadTemplates()
  - openQuestResource()
  - initializeActiveQuestsForUser()
  - cloneQuest()
  - saveActiveQuestsProgress()
  - loadActiveQuestsProgress()
  - getAllQuests()
  - getTemplateQuests()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\quests\QuestReward.java

Package: model.quests
Class: QuestReward

Imports:

Methods:
  - getRewardType()
  - setRewardType()
  - getAmount()
  - setAmount()
  - getFormula()
  - setFormula()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\Regex.java

Package: model
# d:\College\AP\project gitlab\src\model\resoures\Coin.java

Package: model.resoures
Class: Coin

Imports:

Methods:

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\resoures\Diamond.java

Package: model.resoures
Class: Diamond

Imports:

Methods:

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\user_data\User.java

Package: model.user_data
Class: User

Imports:
  - com.google.gson.Gson
  - com.google.gson.GsonBuilder
  - com.google.gson.reflect.TypeToken
  - model.greenhouse.Greenhouse
  - view.GeneralPrinter
  - java.io.
  - java.lang.reflect.Type
  - java.nio.charset.StandardCharsets
  - java.security.MessageDigest
  - java.security.NoSuchAlgorithmException
  - java.util.ArrayList

Methods:
  - GsonBuilder()
  - hashPassword()
  - checkPassword()
  - setPassword()
  - setSecurityQuestion()
  - checkSecurityAnswer()
  - findByUsername()
  - usernameExists()
  - load()
  - setUser()
  - save()
  - addUser()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\user_data\UserState.java

Package: model.user_data
Class: UserState

Imports:
  - model.greenhouse.PotData
  - model.news.News
  - model.quests.GameQuest
  - java.util.

Methods:
  - isPlantUnlocked()
  - unlockPlant()
  - getPlantLevel()
  - setPlantLevel()
  - hasBoost()
  - grantBoost()
  - consumeBoost()
  - addSeedPackets()
  - addNews()
  - recordGameResult()
  - hasUnreadNews()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\utils\GameSession.java

Package: model.utils
Class: GameSession

Imports:
  - controller.QuestManager
  - model.collections.Item
  - model.collections.item.
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.collections.plant.PlantJsonParser
  - model.collections.plant.PlantTag
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.collections.zombie.zombie_pushing_item.PushableStructure
  - model.match.main.levels.Level
  - model.match.main.season.travellog.beach.Flood
  - model.match.main.season.travellog.egypt.Egypt
  - model.match.main.season.travellog.egypt.SandStorm
  - model.match_mechanisms.ZombieWave
  - model.match_mechanisms.vector.Position
  - model.pitches.
  - model.projectile.Projectile
  - model.projectile.zombie_projectile.ZombieProjectile
  - model.user_data.User
  - model.user_data.UserState
  - service.GameClock
  - view.GeneralPrinter
  - java.util.
  - java.util.function.ToIntFunction

Methods:
  - Random()
  - GameClock()
  - getInstance()
  - peekInstance()
  - setGridSize()
  - tick()
  - getEffectiveSkySunInterval()
  - isSkySunEnabledForSession()
  - recordLevelSpecificDeaths()
  - tickLevelSpecificLogic()
  - clearDeadPlantsFromGrid()
  - clearDeadStructuresFromGrid()
  - refreshZombieOccupancy()
  - tickWaveScheduler()
  - previousWaveMostlyCleared()
  - spawnWave()
  - allWavesSpawned()
  - getTotalWaveCount()
  - getWavesSpawnedCount()
  - getSecondsUntilNextWave()
  - checkZombieBreaches()
  - zombiesInRow()
  - spawnZombie()
  - onZombieReachedEnd()
  - notifyZombieDied()
  - collectItemsNear()
  - announceCollection()
  - startWaves()
  - isWavesStarted()
  - areWavesDone()
  - getSunCount()
  - addSun()
  - getPlantsLostThisMatch()
  - spendSun()
  - getPlantFoodCount()
  - addPlantFood()
  - spendPlantFood()
  - grantMatchBoost()
  - hasMatchBoost()
  - getMatchBoostedPlantIds()
  - restoreMatchBoosts()
  - killAllZombies()
  - removeAllCooldowns()
  - isPlantReady()
  - getPlantCooldown()
  - startPlantCooldown()
  - plantAt()
  - removePlantAt()
  - digPlantAt()
  - findPlantAt()
  - renderMap()
  - mapSymbolFor()
  - renderPlantsStatus()
  - renderTileStatus()
  - renderZombiesInfo()
  - isGameOver()
  - isGameWon()
  - getRows()
  - getCols()
  - getEnvironment()
  - getPlants()
  - setPlants()
  - getZombies()
  - getWaves()
  - setZombies()
  - getLevel()
  - setLevel()
  - setWaves()
  - getItems()
  - getGroundItems()
  - setItems()
  - setGroundItems()
  - getProjectiles()
  - addZombieProjectile()
  - getZombieProjectiles()
  - getElapsedSeconds()
  - getElapsedSecondsSinceWavesStarted()
  - hasUsedPlantFamily()
  - usedOnlyNightPlants()
  - isLawnMowerUsed()
  - getDifficultyLevel()
  - setDifficultyLevel()
  - setZombieBreachesEnabled()
  - isZombieBreachesEnabled()
  - setSkySunEnabled()
  - isSkySunEnabled()
  - getPushableStructures()
  - registerStructure()
  - getLawn()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\utils\GameSettings.java

Package: model.utils
Class: GameSettings

Imports:

Methods:
  - get()
  - isDebugMode()
  - setDebugMode()
  - isShowGrid()
  - setShowGrid()
  - getGameSpeed()
  - setGameSpeed()
  - getMusicVolume()
  - setMusicVolume()
  - getSfxVolume()
  - setSfxVolume()
  - isSfxEnabled()
  - setSfxEnabled()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\utils\LevelLoader.java

Package: model.utils
Class: LevelLoader

Imports:
  - com.google.gson.Gson
  - com.google.gson.JsonArray
  - com.google.gson.JsonObject
  - com.google.gson.reflect.TypeToken
  - model.collections.plant.Plant
  - model.collections.plant.PlantFactory
  - model.collections.zombie.Zombie
  - model.collections.zombie.ZombieFactory
  - model.match.main.levels.Level
  - model.match.main.levels.normal_levels.NormalLevel
  - model.match.main.levels.special_levels.
  - model.match.main.season.SeasonFactory
  - model.match_mechanisms.Time
  - model.match_mechanisms.ZombieWave
  - model.match_mechanisms.vector.Position
  - java.io.InputStreamReader
  - java.lang.reflect.Type
  - java.nio.charset.StandardCharsets
  - java.util.ArrayList
  - java.util.HashMap
  - java.util.List
  - java.util.Map

Methods:
  - Gson()
  - loadLevels()
  - loadLevels()
  - parseLevel()
  - defaultGameMode()
  - validateAdventureZombie()
  - validateWaveDifficulty()
  - loadLevelById()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\utils\LevelProgression.java

Package: model.utils
Class: LevelProgression

Imports:
  - model.match.main.levels.Level
  - java.util.ArrayList
  - java.util.Comparator
  - java.util.List

Methods:
  - sorted()
  - isCompleted()
  - isUnlocked()
  - completedIndex()
  - indexOf()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\utils\ResourceResolver.java

Package: model.utils
Class: ResourceResolver

Imports:
  - java.io.File
  - java.io.FileInputStream
  - java.io.IOException
  - java.io.InputStream
  - java.util.LinkedHashSet
  - java.util.Set

Methods:
  - open()
  - stripLeadingSlash()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\model\utils\state\ItemState.java

Package: model.utils.state
# d:\College\AP\project gitlab\src\service\GameClock.java

Package: service
Class: GameClock

Imports:

Methods:
  - tick()
  - getTicks()
  - getElapsedSeconds()
  - reset()
  - hasReached()
  - isZero()
  - countDown()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\service\GameEntity.java

Package: service
Class: GameEntity
Extends: Time

Imports:
  - model.match_mechanisms.Time
  - model.match_mechanisms.vector.Position

Methods:
  - tick()
  - getPosition()
  - setPosition()
  - getSpeed()
  - setSpeed()
  - isAlive()
  - setAlive()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\service\resource_manager\AudioEnum.java

Package: service.resource_manager
# d:\College\AP\project gitlab\src\service\resource_manager\AudioManager.java

Package: service.resource_manager
Class: AudioManager
Implements: Disposable 

Imports:
  - com.badlogic.gdx.audio.Music
  - com.badlogic.gdx.audio.Sound
  - com.badlogic.gdx.utils.Disposable
  - controller.assets.GameAssetManager
  - java.util.EnumMap
  - java.util.Map

Methods:
  - loadAll()
  - loadAudio()
  - playSound()
  - playSound()
  - playSound()
  - getSound()
  - playMusic()
  - getMusic()
  - stopMusic()
  - pauseMusic()
  - resumeMusic()
  - isMusicPath()
  - setSoundVolume()
  - setMusicVolume()
  - setSoundMuted()
  - setMusicMuted()
  - getSoundVolume()
  - getMusicVolume()
  - isSoundMuted()
  - isMusicMuted()
  - update()
  - dispose()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\service\resource_manager\MiniAudioEnum.java

Package: service.resource_manager
# d:\College\AP\project gitlab\src\service\resource_manager\MiniAudioManager.java

Package: service.resource_manager
Class: MiniAudioManager
Implements: Disposable 

Imports:
  - com.badlogic.gdx.audio.Sound
  - com.badlogic.gdx.utils.Disposable
  - controller.assets.GameAssetManager
  - java.util.EnumMap
  - java.util.Map

Methods:
  - preloadSound()
  - playSound()
  - playSound()
  - setGlobalVolume()
  - getGlobalVolume()
  - setMuted()
  - isMuted()
  - dispose()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\service\resource_manager\SkinManager.java

Package: service.resource_manager
Class: SkinManager
Implements: Disposable 

Imports:
  - com.badlogic.gdx.graphics.Color
  - com.badlogic.gdx.graphics.g2d.BitmapFont
  - com.badlogic.gdx.graphics.g2d.TextureAtlas
  - com.badlogic.gdx.graphics.g2d.TextureRegion
  - com.badlogic.gdx.scenes.scene2d.ui.Label
  - com.badlogic.gdx.scenes.scene2d.ui.Skin
  - com.badlogic.gdx.scenes.scene2d.ui.TextButton
  - com.badlogic.gdx.scenes.scene2d.ui.TextField
  - com.badlogic.gdx.scenes.scene2d.utils.Drawable
  - com.badlogic.gdx.utils.Disposable
  - controller.assets.AssetPaths
  - controller.assets.GameAssetManager
  - java.util.EnumMap
  - java.util.HashMap
  - java.util.Map

Methods:
  - DEFAULT()
  - SkinEnum()
  - getAtlasPath()
  - DEFAULT_FONT()
  - FontEnum()
  - getFontPath()
  - loadAll()
  - load()
  - setCurrentSkin()
  - getCurrentSkin()
  - getSkin()
  - getFont()
  - getFont()
  - getFont()
  - addCustomFont()
  - setFontScale()
  - setFontScale()
  - setFontScale()
  - getDrawable()
  - getRegion()
  - getColor()
  - getLabelStyle()
  - getTextButtonStyle()
  - getTextFieldStyle()
  - resetToDefault()
  - update()
  - dispose()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\AppView.java

Package: view
Class: AppView

Imports:
  - model.App
  - model.game_exceptions.GameException
  - view.menus.
  - view.menus.collection_view.CollectionMenuView
  - view.menus.while_match.AfterMatchView
  - view.menus.while_match.BeforeMatchView
  - view.menus.while_match.MatchMenuView
  - view.menus.while_match.MeanwhileMatchView

Methods:
  - run()
  - resolveView()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\GeneralPrinter.java

Package: view
Class: GeneralPrinter

Imports:

Methods:
  - print()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\collection_view\CollectionMenuView.java

Package: view.menus.collection_view
Class: CollectionMenuView
Extends: MenuView

Imports:
  - view.menus.MenuView

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\collection_view\PlantsCollectionView.java

Package: view.menus.collection_view
Class: PlantsCollectionView
Extends: MenuView

Imports:
  - view.menus.MenuView

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\collection_view\ZombiesCollectionView.java

Package: view.menus.collection_view
Class: ZombiesCollectionView
Extends: MenuView

Imports:
  - view.menus.MenuView

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\GameMenuView.java

Package: view.menus
Class: GameMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\GreenhouseMenuView.java

Package: view.menus
Class: GreenhouseMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\LeaderboardMenuView.java

Package: view.menus
Class: LeaderboardMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\LoginMenuView.java

Package: view.menus
Class: LoginMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\MainMenuView.java

Package: view.menus
Class: MainMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\MenuView.java

Package: view.menus
Class: MenuView

Imports:
  - model.App
  - java.util.Scanner

Methods:
  - Scanner()
  - showMenu()
  - getInput()
  - getScanner()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\NewsMenuView.java

Package: view.menus
Class: NewsMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\ProfileMenuView.java

Package: view.menus
Class: ProfileMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\SettingMenuView.java

Package: view.menus
Class: SettingMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\SignupMenuView.java

Package: view.menus
Class: SignupMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\StoreMenuView.java

Package: view.menus
Class: StoreMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\TravelLogMenuView.java

Package: view.menus
Class: TravelLogMenuView
Extends: MenuView

Imports:

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\while_match\AfterMatchView.java

Package: view.menus.while_match
Class: AfterMatchView
Extends: MenuView

Imports:
  - view.menus.MenuView

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\while_match\BeforeMatchView.java

Package: view.menus.while_match
Class: BeforeMatchView
Extends: MenuView

Imports:
  - view.menus.MenuView

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\while_match\MatchMenuView.java

Package: view.menus.while_match
Class: MatchMenuView
Extends: MenuView

Imports:
  - view.menus.MenuView

Methods:
  - showMenu()

------------------------------------------------------------

# d:\College\AP\project gitlab\src\view\menus\while_match\MeanwhileMatchView.java

Package: view.menus.while_match
Class: MeanwhileMatchView
Extends: MenuView

Imports:
  - view.menus.MenuView
  - java.util.regex.Pattern

Methods:
  - showMenu()

------------------------------------------------------------
