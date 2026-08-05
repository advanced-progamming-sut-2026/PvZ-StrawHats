package service.resource_manager;

public enum MiniAudioEnum {
 ;

    private final String filePath;

    MiniAudioEnum(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}