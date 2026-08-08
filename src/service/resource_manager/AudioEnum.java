package service.resource_manager;

public enum AudioEnum {
    SFX_CLICK("assets/audio/sfx/click 1.mp3"),
    MENU_MUSIC("assets/audio/music/loonboon_142032648.mp3");

    private final String filePath;

    AudioEnum(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}