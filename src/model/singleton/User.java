package model.singleton;

import model.news.News;

public class User {
    public String name;



    private String password;
    public UserState userState = new UserState();

    private News[] news;

    public News[] getNews() {
        return news;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void setNews(News[] news) {
        this.news = news;
    }


}
