package com.kuzko.aleksey.guessword;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kuzko.aleksey.guessword.data.GuesswordRepository;
import com.kuzko.aleksey.guessword.database.HelperFactory;
import com.kuzko.aleksey.guessword.data.User;
import com.kuzko.aleksey.guessword.exceptions.LoginExistsException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Aleks on 10.06.2017.
 */

public class MyApplication extends Application {

    private List<User> users;
    private static final String LOGGED_USER_SPREF_TAG = "LOGGED_USER";
    private final static String LOG_TAG = "MyApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        HelperFactory.setHelper(getApplicationContext());
        reloadUsers();
    }

    @Override
    public void onTerminate() {
        HelperFactory.releaseHelper();
        GuesswordRepository.close();
        super.onTerminate();
    }

    public boolean login(String givenLogin, String givenPassword){
        Log.d(LOG_TAG, users + "givenLogin = " + givenLogin + ", givenPassword = " + givenPassword);
        User currentUser = retrieveUserByLogin(givenLogin);
        if(currentUser != null) {
            try {
                if (currentUser.getPassword() == null &&
                    (givenPassword == null || givenPassword.equals(""))) {
                    writeLoggedUser(currentUser);
                    currentUser.setLastEnter(new Date(System.currentTimeMillis()));
                    HelperFactory.getHelper().getUserDao().update(currentUser);
                    return true;
                } else if (currentUser.getPassword().equals(givenPassword)) {
                    writeLoggedUser(currentUser);
                    currentUser.setLastEnter(new Date(System.currentTimeMillis()));
                    HelperFactory.getHelper().getUserDao().update(currentUser);
                    return true;
                }
            }catch (SQLException e){
                e.printStackTrace();
                throw new RuntimeException("updateDataInDb(currentUser) in MyApplication login() went wrong");
            }
        }
        return false;
    }

    public void logout(){
        eraseLoggedUser();
        GuesswordRepository.close();
    }

    private @Nullable User retrieveUserByLogin(String login){
        if(login == null || login.equals("")){
            return null;
        }
        for(User currentUser : users){
            if(currentUser.getLogin().equals(login)){
                return currentUser;
            }
        }
        return null;
    }

    public boolean hasUserPassword(String login){
        User user = retrieveUserByLogin(login);
        return hasUserPassword(user);
    }

    public boolean hasUserPassword(User user){

        if(user == null){
            return false;
        }else if(user.getPassword() == null || user.getPassword().equals("")){
            return false;
        }
        return true;
    }

    public User retrieveLoggedUser(){
        SharedPreferences sPref = getSharedPreferences(MyApplication.class.getName(), MODE_PRIVATE);
        String loggedUserLogin = sPref.getString(LOGGED_USER_SPREF_TAG, null);
        return retrieveUserByLogin(loggedUserLogin);
    }

    private void writeLoggedUser(User user){
        SharedPreferences sPref = getSharedPreferences(MyApplication.class.getName(), MODE_PRIVATE);
        sPref.edit().putString(LOGGED_USER_SPREF_TAG, user.getLogin()).apply();
    }

    private void eraseLoggedUser(){
        SharedPreferences sPref = getSharedPreferences(MyApplication.class.getName(), MODE_PRIVATE);
        sPref.edit().putString(LOGGED_USER_SPREF_TAG, null).apply();
    }

    public void registerNewUser(final User createdUser) throws LoginExistsException {

        for(User currentUser : users){
            if(currentUser.getLogin().equals(createdUser.getLogin())){
                throw new LoginExistsException();
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

    private void reloadUsers(){
        try {
            users = HelperFactory.getHelper().getUserDao().retrieveAll();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Something went wrong during retrieving Users in MyApplication class");
        }
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }


}
