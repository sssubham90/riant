package com.riantservices.riant.activities;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;
import com.riantservices.riant.R;
import com.riantservices.riant.fragments.OutstateBook;
import com.riantservices.riant.fragments.OutstateMap;
import com.riantservices.riant.interfaces.SendMessage;

public class OutstateActivity extends AppCompatActivity implements SendMessage{

    String state1,state2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstate);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout= findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if(getSupportActionBar()!=null) getSupportActionBar().hide();
    }

    public void initState(String s){
        if(state1 == null) state1 = s;
        else state2 = s;
        if(state1.equalsIgnoreCase(state2)){
            clearMap();
        }
    }

    public void clearState(){
        state1 = state2 = null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new OutstateMap();
                case 1:
                    return new OutstateBook();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MAP";
                case 1:
                    return "BOOK";
            }
            return null;
        }
    }

    @Override
    public void sendData(LatLng location, String message, int x) {
        String tag = "android:switcher:" + R.id.container + ":" + 1;
        OutstateBook outstateBook = (OutstateBook) getSupportFragmentManager().findFragmentByTag(tag);
        outstateBook.displayReceivedData(message,x);
    }
    public void clearMap(){
        state1=null;
        state2=null;
        String tag = "android:switcher:" + R.id.container + ":" + 0;
        OutstateMap outstateMap = (OutstateMap) getSupportFragmentManager().findFragmentByTag(tag);
        outstateMap.alertSameState();
    }

    public void fillTextViews(String value){
        String tag = "android:switcher:" + R.id.container + ":" + 0;
        OutstateMap outstateMap = (OutstateMap) getSupportFragmentManager().findFragmentByTag(tag);
        outstateMap.setTextViews(value);
    }
}