package com.kuzko.aleksey.guessword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.datamodel.User;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {


    private TextView userLogin, userPassword;
    private Button signInButton;
    public final static String USERNAME_KEY = "username";
    private User loggedUser;
    GuesswordService guesswordService;
//    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userLogin = (TextView) findViewById(R.id.userLogin);
        userPassword = (TextView) findViewById(R.id.userPassword);
        signInButton = (Button) findViewById(R.id.signInButton);
        guesswordService = GuesswordRepository.getInstance().getGuesswordService();


    }

    private boolean logIn(String givenUserLogin, String givenUserPassword, List<User> existedUsers){
        if(existedUsers != null){
            for(User currentUser : existedUsers){
                if(currentUser.getLogin().equals(givenUserLogin)){
                    if(currentUser.getPassword().equals(givenUserPassword)){
                        loggedUser = currentUser;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void signInButtonClicked(View view) {
        String enteredUserLogin = userLogin.getText().toString();
        String enteredUserPassword = userPassword.getText().toString();
        guesswordService.fetchAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        response -> {
                            Log.d("INFO", response.raw().request().url().toString());
                            if(logIn(enteredUserLogin, enteredUserPassword, response.body())){
                                Intent toLearnActivityIntent = new Intent(this, LearnActivity.class);
                                toLearnActivityIntent.putExtra(USERNAME_KEY, loggedUser.getId());
                                startActivity(toLearnActivityIntent);
                            }else {
                                userLogin.setError(getString(R.string.string_incorrect_login_password));
                                userPassword.setError(getString(R.string.string_incorrect_login_password));
                            }
                        },
                        throwable -> {
                            Toast.makeText(LoginActivity.this, "LoginActivity " + throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                            Log.d("ERROR",  "LoginActivity " + throwable.getLocalizedMessage());
                        }
                );
    }
}
