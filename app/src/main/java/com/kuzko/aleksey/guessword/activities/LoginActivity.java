package com.kuzko.aleksey.guessword.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.MyApplication;
import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.datamodel.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends LoggerActivity {

    //TODO status bar translucent so invisible
    //TODO in landscape mode layout should be corrected

    private EditText editTextLoginUserPassword;
    private Spinner spinnerUsersList;
    private MyApplication application;
    private ArrayAdapter<String> userArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        application = (MyApplication) getApplication();
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
        TextView textViewCreateNewUser = (TextView) findViewById(R.id.textViewCreateNewUser);

        if(application.retrieveLoggedUser() != null){
            //TODO remove login activity from backstack
            startActivity(new Intent(this, LearnActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        }

        editTextLoginUserPassword = (EditText) findViewById(R.id.editTextLoginUserPassword);
        spinnerUsersList = (Spinner) findViewById(R.id.spinnerUsersList);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextLoginUserPassword.setError(null);
                String login = (String) spinnerUsersList.getSelectedItem();
                String password = editTextLoginUserPassword.getText().toString();

                if(login == null){
                    //Do nothing, user isn't selected
                }else if (application.login(login, password)) {
                    switchToLearnActivity();
                } else {
                    editTextLoginUserPassword.setError("Wrong password");
                    editTextLoginUserPassword.requestFocus();
                }
            }
        });
//        buttonLogin.setOnClickListener(this::attemptLogin);
        textViewCreateNewUser.setOnClickListener(this::switchToRegisterActivity);

        spinnerUsersList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                editTextLoginUserPassword.setError(null);
                String selectedLogin = userArrayAdapter.getItem(position);
                if(!application.hasUserPassword(selectedLogin)){
                    editTextLoginUserPassword.setVisibility(View.INVISIBLE);
                }else {
                    editTextLoginUserPassword.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        List<User> availableUsers = application.getUsers();
        if(availableUsers.isEmpty()){
            editTextLoginUserPassword.setVisibility(View.INVISIBLE);
        }
        sortUsersByCreatingDate(availableUsers);

        userArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, retrieveLogins(availableUsers));
        userArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsersList.setAdapter(userArrayAdapter);
    }

    private void attemptLogin(View v) {

        editTextLoginUserPassword.setError(null);
        String login = (String) spinnerUsersList.getSelectedItem();
        String password = editTextLoginUserPassword.getText().toString();

        if(login == null){
            //Do nothing, user isn't selected
        }else if (application.login(login, password)) {
            switchToLearnActivity();
        } else {
            editTextLoginUserPassword.setError("Wrong password");
            editTextLoginUserPassword.requestFocus();
        }
    }

    private void sortUsersByCreatingDate(List<User> usersToBeSorted){
        Collections.sort(usersToBeSorted, new Comparator<User>() {
            @Override
            public int compare(User user1, User user2) {

                if(user1.getLastEnter() == null && user2.getLastEnter() == null){
                    return 0/*user1.getCreatingDate().compareTo(user2.getCreatingDate())*/;
                }else if(user1.getLastEnter() == null && user2.getLastEnter() != null){
                    return 1;
                }else if(user1.getLastEnter() != null && user2.getLastEnter() == null){
                    return -1;
                }else if(user1 == user2 || user1.getLastEnter().equals(user2.getLastEnter())){
                    return 0/*user1.getCreatingDate().compareTo(user2.getCreatingDate())*/;
                }else if(user1.getLastEnter().after(user2.getLastEnter())){
                    return -1;
                }else {
                    return 1;
                }
            }
        });
    }

    public List<String> retrieveLogins(List<User> users) {
        ArrayList<String> logins = new ArrayList<>();
        for(User user : users){
            logins.add(/*(hasUserPassword(user) ? "\uD83D\uDD12" : "") +*/ user.getLogin());
        }
        return logins;
    }

    private void switchToLearnActivity(){
        Intent intent = new Intent(this, LearnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void switchToRegisterActivity(View v){
        startActivity(
                new Intent(this, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        );
    }


}

