package com.kuzko.aleksey.guessword.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.data.Phrase;
import com.kuzko.aleksey.guessword.utils.WebguesswordRepository;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.google.common.base.Preconditions.checkNotNull;

public class SettingsActivity extends DrawerActivity {

    private final static String BASE_NEWS_URL = "http://guessword-aleks267.rhcloud.com/rest/";
    private final static String UNAUTHORIZED_MSG = "HTTP 401 Unauthorized";
    private WebguesswordRepository webguesswordRepository;
    private AlertDialog passwordDialog;
    private String enteredPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Retrofit webGuessWord = new Retrofit.Builder().baseUrl(BASE_NEWS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        webguesswordRepository = webGuessWord.create(WebguesswordRepository.class);
        initDialog();
    }

    private void initDialog() {

        passwordDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_password)
                .setTitle(R.string.dialog_title_password)
                .setPositiveButton(R.string.save_dialog_button, null) //Set to null. We override the onclick
                .create();

        passwordDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                final EditText editTextDialogPassword = checkNotNull((EditText) passwordDialog.findViewById(R.id.editTextDialogPassword));

                editTextDialogPassword.setText("");
                editTextDialogPassword.requestFocus();

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String password = editTextDialogPassword.getText().toString();

                        if(password.equals("")){
                            editTextDialogPassword.requestFocus();
                            editTextDialogPassword.setError("Enter the password");
                        }else {
                            enteredPassword = editTextDialogPassword.getText().toString();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }

    public void startSync(View view) {

        passwordDialog.show();

        webguesswordRepository.retrievePhrases()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Phrase>>() {
                    @Override
                    public void call(List<Phrase> phrases) {
                        Toast.makeText(SettingsActivity.this, "Size = " + phrases.size(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
//                        if(throwable instanceof HttpException && throwable.getLocalizedMessage().equals())
                        Toast.makeText(SettingsActivity.this, "Error " + throwable.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
