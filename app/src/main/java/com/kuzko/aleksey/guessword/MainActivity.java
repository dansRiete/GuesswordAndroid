package com.kuzko.aleksey.guessword;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentManager fragmentManager;
    private LearnFragment learnFragment;
    private EditFragment editFragment;
    private final static String LEARN_FRAGMENT_TAG = "LEARN_FRAGMENT";
    private final static String EDIT_FRAGMENT_TAG = "EDIT_FRAGMENT";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        learnFragment = new LearnFragment();
        editFragment = new EditFragment();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();
        if(savedInstanceState == null) {
            learnFragment = new LearnFragment();
            editFragment = new EditFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_frame, learnFragment, LEARN_FRAGMENT_TAG).commit();
        }/*else {
            learnFragment = (LearnFragment) getSupportFragmentManager().findFragmentByTag(LEARN_FRAGMENT_TAG);
            editFragment = (EditFragment) getSupportFragmentManager().findFragmentByTag(EDIT_FRAGMENT_TAG);
        }*/
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
        mDrawerLayout.addDrawerListener(mDrawerToggle);
//        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.colorAccent));


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
            case R.id.drawer_item_menu_edit:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, editFragment, EDIT_FRAGMENT_TAG).commit();
                break;
            case R.id.drawer_item_menu_learn:
                mDrawerLayout.closeDrawer(GravityCompat.START);
                fragmentManager.beginTransaction().replace(R.id.fragment_frame, learnFragment, LEARN_FRAGMENT_TAG).commit();
                break;
            default:
                break;
        }
        return true;
    }
}
