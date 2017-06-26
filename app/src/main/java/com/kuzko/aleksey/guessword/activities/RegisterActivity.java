package com.kuzko.aleksey.guessword.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.MyApplication;
import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.data.User;
import com.kuzko.aleksey.guessword.exceptions.LoginExistsException;

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
        checkBoxPasswordNecessary.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    editTextNewUsersPassword.setVisibility(View.VISIBLE);
                    editTextNewUsersPasswordConfirm.setVisibility(View.VISIBLE);
                }else {
                    editTextNewUsersPassword.setVisibility(View.GONE);
                    editTextNewUsersPasswordConfirm.setVisibility(View.GONE);
                }
            }
        });
        editTextNewUsersName = (EditText) findViewById(R.id.editTextNewUsersName);
        editTextNewUsersPassword = (EditText) findViewById(R.id.editTextNewUsersPassword);
        editTextNewUsersPasswordConfirm = (EditText) findViewById(R.id.editTextNewUsersPasswordConfirm);
        TextView textViewAlreadyHaveAccount = (TextView) findViewById(R.id.textViewAlreadyHaveAccount);
        textViewAlreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        Button buttonCreateUser = (Button) findViewById(R.id.buttonCreateUser);
        buttonCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean cancel = false;
                View focusView = null;

                editTextNewUsersName.setError(null);
                editTextNewUsersPassword.setError(null);
                editTextNewUsersPasswordConfirm.setError(null);

                String login = editTextNewUsersName.getText().toString();
                String password = editTextNewUsersPassword.getText().toString();
                String passwordConfirmation = editTextNewUsersPasswordConfirm.getText().toString();

                if(/*(!password.isEmpty() || !passwordConfirmation.isEmpty()) &&*/ !password.equals(passwordConfirmation)){
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
                    editTextNewUsersName.setText("");
                    editTextNewUsersPassword.setText("");
                    editTextNewUsersPasswordConfirm.setText("");
                    //TODO showProgressBar at here
                    asyncRegisterNewUser(login, password);
                }
            }
        });

    }

    private void asyncRegisterNewUser(final String login, final String password){

        final User user = new User(login, password.equals("") ? null : password);

        new AsyncTask<Void, Void, Void>() {

            Exception loginExists = null;

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    application.registerNewUser(user);
                } catch (LoginExistsException e) {
                    this.loginExists = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //TODO hideProgressBar at here
                if(loginExists != null){
                    editTextNewUsersName.setError("Nickname already exists");
                    editTextNewUsersName.requestFocus();
                }else {

                    if (application.login(login, password)) {
                        startActivity(
                                new Intent(RegisterActivity.this, LearnActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                  Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        );
                    } else {
                        startActivity(
                                new Intent(RegisterActivity.this, LoginActivity.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                  Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        );
                    }
                }
            }
        }.execute();
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

    }

    private void signUp(View v){


    }
}
