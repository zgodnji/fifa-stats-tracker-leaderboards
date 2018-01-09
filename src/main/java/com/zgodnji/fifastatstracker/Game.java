package com.zgodnji.fifastatstracker;

public class Game {

    private String id;
    private String title;
    private String developer;
    private String platform;

    public Game(String id, String title, String developer, String platform) {
        this.id = id;
        this.title = title;
        this.developer = developer;
        this.platform = platform;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

}
