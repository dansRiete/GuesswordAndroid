package com.kuzko.aleksey.guessword.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.MyApplication;
import com.kuzko.aleksey.guessword.R;

/**
 * Created by Aleks on 17.06.2017.
 */

public class DrawerActivity extends LoggerActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected LinearLayout fullLayout;
    protected FrameLayout actContent;
    protected DrawerLayout drawerLayout;
    protected ActionBarDrawerToggle drawerToggle;
    protected Toolbar toolbar;
    private MyApplication application;

    @Override
    public void setContentView(final int layoutResID) {

        fullLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.act_layout, null);
        actContent = (FrameLayout) fullLayout.findViewById(R.id.act_content);
        application = (MyApplication) getApplication();
        getLayoutInflater().inflate(layoutResID, actContent, true);
        super.setContentView(fullLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        TextView userHeaderName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name_drawer_textview);
        userHeaderName.setText(application.retrieveLoggedUser().getLogin());
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.addDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.drawer_item_menu_edit:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, EditActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;
            case R.id.drawer_item_menu_learn:
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, LearnActivity.class).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
                break;
            case R.id.drawer_item_menu_logout:
                drawerLayout.closeDrawer(GravityCompat.START);
                application.logout();
                startActivity(new Intent(this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                break;
            default:
                break;
        }
        return true;
    }


}
