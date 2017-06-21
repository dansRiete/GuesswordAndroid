package com.kuzko.aleksey.guessword.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.MyApplication;
import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.datamodel.User;
import com.kuzko.aleksey.guessword.exceptions.NicknameExistsException;

public class RegisterActivity extends LoggerActivity {

    private EditText editTextNewUsersName, editTextNewUsersPassword, editTextNewUsersPasswordConfirm;
    private MyApplication application;
    private CheckBox checkBoxPasswordNecessary;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        application = (MyApplication) getApplication();
        checkBoxPasswordNecessary = (CheckBox) findViewById(R.id.checkBoxPasswordNecessary);
        checkBoxPasswordNecessary.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                editTextNewUsersPassword.setVisibility(View.VISIBLE);
                editTextNewUsersPasswordConfirm.setVisibility(View.VISIBLE);
            }else {
                editTextNewUsersPassword.setVisibility(View.GONE);
                editTextNewUsersPasswordConfirm.setVisibility(View.GONE);
            }
        });
        editTextNewUsersName = (EditText) findViewById(R.id.editTextNewUsersName);
        editTextNewUsersPassword = (EditText) findViewById(R.id.editTextNewUsersPassword);
        editTextNewUsersPasswordConfirm = (EditText) findViewById(R.id.editTextNewUsersPasswordConfirm);
        TextView textViewAlreadyHaveAccount = (TextView) findViewById(R.id.textViewAlreadyHaveAccount);
        textViewAlreadyHaveAccount.setOnClickListener(this::switchToLoginActivity);
        Button buttonCreateUser = (Button) findViewById(R.id.buttonCreateUser);
        buttonCreateUser.setOnClickListener(this::signUp);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(getClass().getSimpleName(), "checkBoxPasswordNecessary.isChecked() = " + checkBoxPasswordNecessary.isChecked());
        if(checkBoxPasswordNecessary.isChecked()){
            editTextNewUsersPassword.setVisibility(View.VISIBLE);
            editTextNewUsersPasswordConfirm.setVisibility(View.VISIBLE);
        }else {
            editTextNewUsersPassword.setVisibility(View.GONE);
            editTextNewUsersPasswordConfirm.setVisibility(View.GONE);
        }
    }

    private void switchToLoginActivity(View v){
        switchToLoginActivity();
    }

    private void switchToLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void signUp(View v){

        boolean cancel = false;
        View focusView = null;

        editTextNewUsersName.setError(null);
        editTextNewUsersPassword.setError(null);
        editTextNewUsersPasswordConfirm.setError(null);

        String login = editTextNewUsersName.getText().toString();
        String password = editTextNewUsersPassword.getText().toString();
        String passwordConfirmation = editTextNewUsersPasswordConfirm.getText().toString();

        if((!password.isEmpty() || !password.isEmpty()) && !password.equals(passwordConfirmation)){
            editTextNewUsersPasswordConfirm.setError("Password and it's confirmation do not match");
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
                User user = new User(login, password.equals("") ? null : password);
                application.registerNewUser(user);
                editTextNewUsersName.setText("");
                editTextNewUsersPassword.setText("");
                editTextNewUsersPasswordConfirm.setText("");
                if(application.login(login, password)){
                    startActivity(new Intent(this, LearnActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }else {
                    switchToLoginActivity();
                }
            } catch (NicknameExistsException e) {
                editTextNewUsersName.setError("Nickname already exists");
                editTextNewUsersName.requestFocus();
            }
        }
    }
}
