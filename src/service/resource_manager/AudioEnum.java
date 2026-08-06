package service.resource_manager;

public enum AudioEnum {
   SFX_CLICK("");

    private final String filePath;

    AudioEnum(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}