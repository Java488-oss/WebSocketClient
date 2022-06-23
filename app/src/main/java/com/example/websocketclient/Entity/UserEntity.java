package com.example.websocketclient.Entity;


public class UserEntity {

    private String user;

    private String pass;

    public UserEntity(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    public UserEntity() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}