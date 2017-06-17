package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kuzko.aleksey.guessword.database.HelperFactory;
import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.utils.PhrasesRecyclerAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EditFragment extends Fragment{

    private View dialogView;
    private AlertDialog.Builder alertDialog;
    private PhrasesRecyclerAdapter phrasesRecyclerAdapter;
    private List<Phrase> phrases = new ArrayList<>();
    private FloatingActionButton floatingActionButton;
    private final static String LOG_TAG = "EditFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "onActivityCreated(@Nullable Bundle savedInstanceState)");
        try {
            phrasesRecyclerAdapter = new PhrasesRecyclerAdapter(HelperFactory.getHelper().getPhraseDao().retrieveAll());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fetching Phrases in onActivityCreated()");
        }
        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floatingButtonAddPhrase);
        floatingActionButton.setOnClickListener(v -> {
            removeView();
            alertDialog.show();
        });
        RecyclerView mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.phrases_edit_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(phrasesRecyclerAdapter);
        initDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(LOG_TAG, "onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)");
        return inflater.inflate(R.layout.fragment_edit, container, false);

    }

    private void initDialog() {
        alertDialog = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.phrases_add_dialog_layout, null);
        alertDialog.setView(dialogView);
        alertDialog.setPositiveButton("Save", (dialog, which) -> {

            EditText editTextDialogForeingWord = (EditText) dialogView.findViewById(R.id.editTextDialogForeingWord);
            EditText editTextDialogNativeWord = (EditText) dialogView.findViewById(R.id.editTextDialogNativeWord);
            EditText editTextDialogTranscription = (EditText) dialogView.findViewById(R.id.editTextDialogTranscription);
            EditText editTextDialogLabel = (EditText) dialogView.findViewById(R.id.editTextDialogLabel);

            String givenNativeWord = editTextDialogNativeWord.getText().toString();
            String givenForeignWord = editTextDialogForeingWord.getText().toString();
            String givenTranscription = editTextDialogTranscription.getText().toString();
            String givenLabel = editTextDialogLabel.getText().toString();

            Phrase addedPhrase = new Phrase(/*GuesswordRepository.getInstance().giveNewPhraseId(), */givenForeignWord, givenNativeWord, givenTranscription, givenLabel);
            phrasesRecyclerAdapter.add(addedPhrase);
            GuesswordRepository.getInstance().addPhrase(addedPhrase);
            dialog.dismiss();

        });
    }

    private void removeView() {
        if(dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart()");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume()");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(LOG_TAG, "onPause()");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(LOG_TAG, "onStop()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(getTag(), "onSaveInstanceState(Bundle outState)");
    }
}
