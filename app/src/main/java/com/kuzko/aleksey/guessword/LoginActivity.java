package com.kuzko.aleksey.guessword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.datamodel.GuesswordRepository;
import com.kuzko.aleksey.guessword.datamodel.User;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity {


    private TextView userLogin, userPassword;
    private Button signInButton;
    public final static String USERNAME_KEY = "username";
    GuesswordService guesswordService;
    List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userLogin = (TextView) findViewById(R.id.userLogin);
        userPassword = (TextView) findViewById(R.id.userPassword);
        signInButton = (Button) findViewById(R.id.signInButton);
        signInButton.setClickable(false);
        guesswordService = GuesswordRepository.getInstance().getGuesswordService();

        guesswordService.fetchAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        users -> {
                            this.users = users;
                            signInButton.setClickable(true);
                        },
                        throwable -> Toast.makeText(LoginActivity.this, "LoginActivity " + throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show()
                );
    }

    private User findUser(String givenUserLogin){
        if(this.users != null){
            for(User currentUser : this.users){
                if(currentUser.getLogin().equals(givenUserLogin)){
                    return currentUser;
                }
            }
        }
        return null;
    }

    public void signInButtonClicked(View view) {
        String enteredUserLogin = userLogin.getText().toString();
        String enteredUserPassword = userPassword.getText().toString();
        User foundUser = findUser(enteredUserLogin);
        if(foundUser == null){
            userLogin.setError("No users with such name were found");
        }else if(!foundUser.getPassword().equals(enteredUserPassword)){
            userPassword.setError("Invalid password");
        }else {
            Intent toLearnActivityIntent = new Intent(this, LearnActivity.class);
            toLearnActivityIntent.putExtra(USERNAME_KEY, foundUser.getId());
            startActivity(toLearnActivityIntent);
        }
    }
}
