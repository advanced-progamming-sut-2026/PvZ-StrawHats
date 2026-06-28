package model.user_data;

import model.news.News;

public class UserState {

    public News[] news;
    public int lastLevel, diamonds, coins;

    public UserState(News[] news, int lastLevel, int diamonds, int coins) {
        this.news = news;
        this.lastLevel = lastLevel;
        this.diamonds = diamonds;
        this.coins = coins;
    }
}