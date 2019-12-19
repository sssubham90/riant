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
import com.riantservices.riant.fragments.OutstationBook;
import com.riantservices.riant.fragments.OutstationMap;
import com.riantservices.riant.interfaces.SendMessage;

public class OutstationActivity extends AppCompatActivity implements SendMessage{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outstation);
        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        ViewPager mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout= findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if(getSupportActionBar()!=null) getSupportActionBar().hide();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return new OutstationMap();
                case 1:
                    return new OutstationBook();
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
        OutstationBook outstationBook = (OutstationBook) getSupportFragmentManager().findFragmentByTag(tag);
        outstationBook.displayReceivedData(location,message,x);
    }

    public void fillTextViews(String value){
        String tag = "android:switcher:" + R.id.container + ":" + 0;
        OutstationMap outstationMap = (OutstationMap) getSupportFragmentManager().findFragmentByTag(tag);
        outstationMap.setTextViews(value);
    }
}