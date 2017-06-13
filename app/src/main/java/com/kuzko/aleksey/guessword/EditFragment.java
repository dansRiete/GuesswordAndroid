package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.utils.PhrasesRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;


public class EditFragment extends Fragment {

    private View view;
    private AlertDialog.Builder alertDialog;
    private PhrasesRecyclerAdapter phrasesRecyclerAdapter;
    private List<Phrase> phrases = new ArrayList<>();
    private FloatingActionButton floatingActionButton;
    EditText editTextDialogForeingWord;
    EditText editTextDialogNativeWord;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        phrasesRecyclerAdapter = new PhrasesRecyclerAdapter(phrases);
        floatingActionButton = (FloatingActionButton) getActivity().findViewById(R.id.floatingButtonAddPhrase);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView();
                alertDialog.show();
            }
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
        return inflater.inflate(R.layout.fragment_edit, container, false);

    }

    private void initDialog() {
        alertDialog = new AlertDialog.Builder(getActivity());
        view = getActivity().getLayoutInflater().inflate(R.layout.contacts_edit_dialog_layout, null);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("Save", (dialog, which) -> {

            EditText editTextDialogForeingWord = (EditText)view.findViewById(R.id.editTextDialogForeingWord);
            EditText editTextDialogNativeWord = (EditText)view.findViewById(R.id.editTextDialogNativeWord);
            Phrase addedPhrase = new Phrase(editTextDialogForeingWord.getText().toString(), editTextDialogNativeWord.getText().toString());
            phrasesRecyclerAdapter.add(addedPhrase);
            dialog.dismiss();

        });
//        contactNameEditText = (EditText)view.findViewById(R.id.editTextDialogForeingWord);
//        contactPhoneEditText = (EditText)view.findViewById(R.id.editTextDialogNativeWord);
    }

    private void removeView() {
        if(view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

}
