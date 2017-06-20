package com.kuzko.aleksey.guessword.datamodel;

import com.j256.ormlite.field.DatabaseField;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Aleks on 20.05.2016.
 */

@Entity
@Table(name = "users")
public class User implements Serializable {

    /*@javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    public long id;*/

    public String getLogin() {
        return login;
    }

    @DatabaseField(id = true)
    public String login;

    @DatabaseField()
    public String name;

    @DatabaseField()
    public String password;

    @DatabaseField()
    public String email;

    public User(){}

    public User(String login, String name, String password, String email){
        this.login = login;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    /*public long getId() {
        return id;
    }*/
}
