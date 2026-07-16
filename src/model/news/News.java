package model.news;

public class News {
    private String text;
    private boolean isRead;

    public News(String text) {
        this.text = text;
        this.isRead = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
        // *
    }
}