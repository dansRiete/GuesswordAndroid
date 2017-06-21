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

        if(application.retrieveActiveUserLogin() != null && !application.retrieveActiveUserLogin().equals("")){
            //TODO remove login activity from backstack
            startActivity(new Intent(this, LearnActivity.class));
        }

        editTextLoginUserPassword = (EditText) findViewById(R.id.editTextLoginUserPassword);
        spinnerUsersList = (Spinner) findViewById(R.id.spinnerUsersList);
        buttonLogin.setOnClickListener(this::attemptLogin);
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
        List<String> availableUsers = application.retrieveUsersLogins();
        if(availableUsers.isEmpty()){
            editTextLoginUserPassword.setVisibility(View.INVISIBLE);
        }
        userArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, availableUsers);
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

