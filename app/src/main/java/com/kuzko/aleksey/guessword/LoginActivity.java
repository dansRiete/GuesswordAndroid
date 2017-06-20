package com.kuzko.aleksey.guessword;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.datamodel.User;
import com.kuzko.aleksey.guessword.exceptions.NicknameExistsException;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity  {

    private Button buttonLogin, buttonCreateUser;
    private TextView textViewCreateNewUser;
    private EditText editTextNewUsersName, editTextNewUsersPassword, editTextNewUsersPasswordConfirm, editTextLoginUserPassword;
    private Spinner spinnerUsersList;
    private MyApplication application;
    private LinearLayout linearLayoutCreateUser;
    private ArrayAdapter<User> userArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        application = (MyApplication) getApplication();
        if(application.retrieveLoggedUsersLogin() != null && !application.retrieveLoggedUsersLogin().equals("")){
            //TODO remove login activity from backstack
            startActivity(new Intent(this, LearnActivity.class));
        }

        buttonLogin = (Button) findViewById(R.id.buttonLogin);
        buttonCreateUser = (Button) findViewById(R.id.buttonCreateUser);
        editTextNewUsersName = (EditText) findViewById(R.id.editTextNewUsersName);
        editTextLoginUserPassword = (EditText) findViewById(R.id.editTextLoginUserPassword);
        editTextNewUsersPassword = (EditText) findViewById(R.id.editTextNewUsersPassword);
        editTextNewUsersPasswordConfirm = (EditText) findViewById(R.id.editTextNewUsersPasswordConfirm);
        spinnerUsersList = (Spinner) findViewById(R.id.spinnerUsersList);
        linearLayoutCreateUser = (LinearLayout) findViewById(R.id.linearLayoutCreateUser);
        textViewCreateNewUser = (TextView) findViewById(R.id.textViewCreateNewUser);

        buttonLogin.setOnClickListener(this::attemptLogin);
        buttonCreateUser.setOnClickListener(this::signUp);
        linearLayoutCreateUser.setVisibility(View.GONE);
        textViewCreateNewUser.setOnClickListener(view -> {
            linearLayoutCreateUser.setVisibility(View.VISIBLE);
            editTextNewUsersName.requestFocus();
        });
        userArrayAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_spinner_item, application.getUsers());
        userArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsersList.setAdapter(userArrayAdapter);

    }

    private void signUp(View v){

        boolean cancel = false;
        View focusView = null;

        editTextNewUsersName.setError(null);
        editTextNewUsersPassword.setError(null);
        editTextNewUsersPasswordConfirm.setError(null);

        String login = editTextNewUsersName.getText().toString();
        String password = editTextNewUsersPassword.getText().toString();
        String passwordConfiramation = editTextNewUsersPasswordConfirm.getText().toString();

        if((!password.isEmpty() || !password.isEmpty()) && !password.equals(passwordConfiramation)){
            editTextNewUsersPasswordConfirm.setError("Password and it's confiramtion do not match");
            cancel = true;
            focusView = editTextNewUsersPasswordConfirm;
        }

        if(login.isEmpty()){
            editTextNewUsersName.setError("Enter a nickname");
            cancel = true;
            focusView = editTextNewUsersName;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            try {
                //TODO user with null password is not accepted
                User user = new User(login, password.equals("") ? null : password);
                application.signUp(user);
                userArrayAdapter.clear();
                userArrayAdapter.addAll(application.getUsers());
                userArrayAdapter.notifyDataSetChanged();
                Toast.makeText(this, "User with name " + user + " has been succesfully created", Toast.LENGTH_LONG).show();
                editTextNewUsersName.setText("");
                editTextNewUsersPassword.setText("");
                editTextNewUsersPasswordConfirm.setText("");
                linearLayoutCreateUser.setVisibility(View.GONE);
            } catch (NicknameExistsException e) {
                editTextNewUsersName.setError("Nickname already exists");
                editTextNewUsersName.requestFocus();
            }
        }
    }

    private void attemptLogin(View v) {

        editTextLoginUserPassword.setError(null);
        User user = (User) spinnerUsersList.getSelectedItem();

        if(editTextLoginUserPassword.getText().toString().isEmpty()){
            editTextLoginUserPassword.setError("Enter the password");
        }else if(editTextLoginUserPassword.getText().toString().equals(user.getPassword())){
            if(application.login(user)){
                //TODO remove login activity from backstack
                startActivity(new Intent(this, LearnActivity.class));
            }
        }else {
            editTextLoginUserPassword.setError("Wrong password");
        }
    }


}

