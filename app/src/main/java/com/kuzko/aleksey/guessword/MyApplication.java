package com.kuzko.aleksey.guessword;

import android.app.Application;
import android.content.SharedPreferences;

import com.kuzko.aleksey.guessword.database.HelperFactory;
import com.kuzko.aleksey.guessword.datamodel.User;
import com.kuzko.aleksey.guessword.exceptions.NicknameExistsException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Aleks on 10.06.2017.
 */

public class MyApplication extends Application {

    private List<User> users;
    private static final String LOGGED_USER_SPREF_TAG = "LOGGED_USER";

    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
        reloadUsers();
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        super.onTerminate();
    }

    public boolean login(User user){
        if(users.contains(user)){
            SharedPreferences sPref = getSharedPreferences(MyApplication.class.getName(), MODE_PRIVATE);
            sPref.edit().putString(LOGGED_USER_SPREF_TAG, user.getLogin()).apply();
            return true;
        }else {
            return false;
        }
    }

    private void reloadUsers(){
        try {
            users = HelperFactory.getHelper().getUserDao().retrieveAll();
            sortUsersByCreatingDate(users);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong during retrieving Users in MyApplication class");
        }
    }

    public String retrieveLoggedUsersLogin(){
        SharedPreferences sPref = getSharedPreferences(MyApplication.class.getName(), MODE_PRIVATE);
        return sPref.getString(LOGGED_USER_SPREF_TAG, null);
    }

    public User retrieveLoggedUser(){
        String login = retrieveLoggedUsersLogin();
        for(User user : users){
            if(user.getLogin().equals(login)){
                return user;
            }
        }
        return null;
    }

    public void signUp(User createdUser) throws NicknameExistsException{

        for(User curentUser : users){
            if(curentUser.getLogin().equals(createdUser.getLogin())){
                throw new NicknameExistsException();
            }
        }

        try {
            HelperFactory.getHelper().getUserDao().create(createdUser);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        reloadUsers();
    }

    private void sortUsersByCreatingDate(List<User> usersToBeSorted){
        Collections.sort(usersToBeSorted, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {
                if(user1 == user2 || user1.getCreatingDate().getTime() == user2.getCreatingDate().getTime()){
                    return 0;
                }else if(user1.getCreatingDate().getTime() > user2.getCreatingDate().getTime()){
                    return -1;
                }else {
                    return 1;
                }
            }
        });
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }


}
