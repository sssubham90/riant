package com.riantservices.riant.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.riantservices.riant.R;
import com.riantservices.riant.fragments.AirportBookFragment;
import com.riantservices.riant.fragments.CorporateBookFragment;
import com.riantservices.riant.fragments.LocalBookFragment;
import com.riantservices.riant.fragments.OutstateBookFragment;
import com.riantservices.riant.fragments.OutstationBookFragment;
import com.riantservices.riant.fragments.StationBookFragment;

public class BookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        /*      To start the fragments with intent Extras.

        if (savedInstanceState == null) {
            Bundle intentExtra = getIntent().getExtras();
            // During initial setup, plug in the fragment.
            AirportBookFragment airportBookFragment = AirportBookFragment.newInstance(intentExtra);
            CorporateBookFragment corporateBookFragment = CorporateBookFragment.newInstance(intentExtra);
            LocalBookFragment localBookFragment = LocalBookFragment.newInstance(intentExtra);
            OutstateBookFragment outstateBookFragment = OutstateBookFragment.newInstance(intentExtra);
            OutstationBookFragment outstationBookFragment = OutstationBookFragment.newInstance(intentExtra);
            StationBookFragment stationBookFragment = StationBookFragment.newInstance(intentExtra);

            getSupportFragmentManager().beginTransaction().add(                     //will do that for other fragments like this.
                    android.R.id.content, airportBookFragment).commit();
        }*/

    }
}