package model.user_data;

import model.news.News;

public record UserState(News[] news, int lastLevel, int diamonds, int coins) {

}
