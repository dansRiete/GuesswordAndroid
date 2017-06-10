package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.exceptions.EmptyCollectionException;

import java.util.ArrayList;
import java.util.List;

public class LearnActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private List<Phrase> askedPhrasesLog = new ArrayList<>();
    private Button answerButton;
    private int counter = 0;
    private RecyclerAdapter recyclerAdapter;
    private RecyclerView mRecyclerView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        answerButton = (Button) findViewById(R.id.answerButton);
        recyclerAdapter = new RecyclerAdapter(askedPhrasesLog);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(recyclerAdapter);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                ((TextView) findViewById(R.id.user_name_drawer_textview)).setText("Some text");
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_logout:
//                application.logOut();
                mDrawerLayout.closeDrawer(GravityCompat.START);
//                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.nav_weather:
//                fragmentManager.beginTransaction().replace(R.id.fragment_frame, weatherFragment).commit();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_map:
//                fragmentManager.beginTransaction().replace(R.id.fragment_frame, mapFragment).commit();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_contacts:
//                fragmentManager.beginTransaction().replace(R.id.fragment_frame, contactsViewFragment).commit();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.menu);
        return true;
    }

    public void answerButtonClicked(View view) {
        Phrase askedPhrase = null;
        try {
            askedPhrase = GuesswordRepository.getInstance().retrieveRandomPhrase();
        } catch (EmptyCollectionException e) {
            Toast.makeText(this, "Phrases collection is empty", Toast.LENGTH_LONG).show();
            return;
        }
        askedPhrasesLog.add(askedPhrase);
        recyclerAdapter.notifyDataSetChanged();
        Log.d("INFO", "Phrase asked: " + askedPhrase.toString());
    }

    public void wrongButtonClicked(View view) {

    }

    public void rightButtonClicked(View view) {

    }

    public void previousWrongButtonClicked(View view) {

    }

    public void previousRightButtonClicked(View view) {

    }

    public void add(MenuItem item) {


    }
}
