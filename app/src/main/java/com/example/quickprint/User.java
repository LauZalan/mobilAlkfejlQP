package com.example.quickprint;

public class User {
    private String UserName;
    private String Email;
    private String Secret;

    public User(String UserName, String Email, String Secret) {
        this.UserName = UserName;
        this.Email = Email;
        this.Secret = Secret;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public void setSecret(String secret) {
        Secret = secret;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

}
