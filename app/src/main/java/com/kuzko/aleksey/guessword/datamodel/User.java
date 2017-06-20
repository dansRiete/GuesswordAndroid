package com.kuzko.aleksey.guessword.datamodel;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Aleks on 20.05.2016.
 */

@Entity
@Table(name = "users")
public class User implements Serializable {

    @DatabaseField(id = true)
    private String login;

    @DatabaseField()
    private String password;

    public Date getCreatingDate() {
        return creatingDate;
    }

    @DatabaseField()
    private Date creatingDate;

    public User(){}

    public User(String login, String password){
        this.login = login;
        this.password = password;
        creatingDate = new Date(System.currentTimeMillis());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this){
            return true;
        }else if(obj instanceof User){
            User givenUser = (User) obj;
            if(givenUser.getLogin().equals(this.getLogin()) && givenUser.getPassword().equals(this.getPassword())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (login + password).hashCode() * 21;
    }

    @Override
    public String toString() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }
}