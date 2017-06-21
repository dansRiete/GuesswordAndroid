package com.kuzko.aleksey.guessword;

import android.app.Application;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
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
        super.onTerminate();
    }

    public boolean login(String givenLogin, String givenPassword){
        Log.d(LOG_TAG, users + "givenLogin = " + givenLogin + ", givenPassword = " + givenPassword);
        User currentUser = retrieveUserByLogin(givenLogin);
        if(currentUser != null) {
            if(currentUser.getPassword() == null && (givenPassword == null || givenPassword.equals(""))){
                writeLoggedUser(currentUser);
                return true;
            }else if(currentUser.getPassword().equals(givenPassword)){
                writeLoggedUser(currentUser);
                return true;
            }
        }
        return false;
    }

    public void logout(){
        eraseLoggedUser();
    }

    private @Nullable User retrieveUserByLogin(String login){
        if(login == null){
            return null;
        }
        for(User currentUser : users){
            if(currentUser.getLogin().equals(login)){
                return currentUser;
            }
        }
        return null;
    }

    public List<String> retrieveUsersLogins() {
        ArrayList<String> logins = new ArrayList<>();
        for(User user : users){
            logins.add(/*(hasUserPassword(user) ? "\uD83D\uDD12" : "") +*/ user.getLogin());
        }
        return logins;
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

    public String retrieveActiveUserLogin(){
        SharedPreferences sPref = getSharedPreferences(MyApplication.class.getName(), MODE_PRIVATE);
        return sPref.getString(LOGGED_USER_SPREF_TAG, null);
    }

    public User retrieveActiveUser(){
        return retrieveUserByLogin(retrieveActiveUserLogin());
    }

    public void registerNewUser(User createdUser) throws NicknameExistsException{

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

    private void writeLoggedUser(User user){
        SharedPreferences sPref = getSharedPreferences(MyApplication.class.getName(), MODE_PRIVATE);
        sPref.edit().putString(LOGGED_USER_SPREF_TAG, user.getLogin()).apply();
    }

    private void eraseLoggedUser(){
        SharedPreferences sPref = getSharedPreferences(MyApplication.class.getName(), MODE_PRIVATE);
        sPref.edit().putString(LOGGED_USER_SPREF_TAG, null).apply();
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
