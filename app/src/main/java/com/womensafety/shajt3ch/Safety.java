package com.womensafety.shajt3ch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class Safety extends AppCompatActivity {
    private TabLayout tabLayout;
    private int[] mImageArray, mColorArray;
    private ArrayList<Fragment> mFragments;
    private final String[] mTitles = {"First_Aid","Self_defence"};
    private ViewPager mViewPager;
    private Button emergencySosButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety);
        initFragments();
        initViewPager();
        initTabLayout();
        initEmergencyButton();
    }

    private void initFragments() {
        mFragments = new ArrayList<>();
        for (String title : mTitles) {
            mFragments.add(DataHolder.getInstance(title));
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), mFragments, mTitles));
    }

    private void initTabLayout() {
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(mViewPager);
        
        // Set tab icons
        tabLayout.getTabAt(0).setIcon(R.mipmap.firstaid_icon);
        tabLayout.getTabAt(1).setIcon(R.mipmap.selfdefence_icon);
        
        // Set tab colors
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(android.R.color.holo_red_light));
        tabLayout.setTabTextColors(getResources().getColor(android.R.color.darker_gray), 
                                 getResources().getColor(android.R.color.holo_red_light));
    }

    private void initEmergencyButton() {
        emergencySosButton = (Button) findViewById(R.id.emergency_sos_button);
        emergencySosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Launch SOS Activity
                Intent sosIntent = new Intent(Safety.this, SOSActivity.class);
                startActivity(sosIntent);
                Toast.makeText(Safety.this, "Emergency SOS Activated!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


