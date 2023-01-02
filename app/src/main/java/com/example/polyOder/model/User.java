package com.example.polyOder.model;


import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    private String id;
    private String name_user;
    private Boolean sex = true;
    private String phone_number;
    private String birthday;
    private String address;
    private String password;
    private String email;
    private boolean userAuthorization;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName_user() {
        return name_user;
    }

    public void setName_user(String name_user) {
        this.name_user = name_user;
    }


    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isUserAuthorization() {
        return userAuthorization;
    }

    public void setUserAuthorization(boolean userAuthorization) {
        this.userAuthorization = userAuthorization;
    }

    public User(String id, String name_user, Boolean sex, String phone_number, String birthday, String address, String password, String email, boolean userAuthorization) {
        this.id = id;
        this.name_user = name_user;
        this.sex = sex;
        this.phone_number = phone_number;
        this.birthday = birthday;
        this.address = address;
        this.password = password;
        this.email = email;
        this.userAuthorization = userAuthorization;
    }

    public User(String id, String name_user, Boolean sex, String phone_number, String birthday, String address, String password, String email) {
        this.id = id;
        this.name_user = name_user;
        this.sex = sex;
        this.phone_number = phone_number;
        this.birthday = birthday;
        this.address = address;
        this.password = password;
        this.email = email;
    }

    public User(String name_user, Boolean sex, String phone_number, String birthday, String address, String email) {
        this.name_user = name_user;
        this.sex = sex;
        this.phone_number = phone_number;
        this.birthday = birthday;
        this.address = address;
        this.email = email;
    }

    public User() {
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name_user", name_user);
        result.put("phone_number", phone_number);
        result.put("sex", sex);
        result.put("birthday", birthday);
        result.put("address", address);
        result.put("email", email);
        return result;
    }
}
