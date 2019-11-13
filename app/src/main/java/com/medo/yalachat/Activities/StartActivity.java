package com.medo.yalachat.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medo.yalachat.Fragments.MyRoomsFragment;
import com.medo.yalachat.Fragments.Rooms_Fragment;
import com.medo.yalachat.MainActivity;
import com.medo.yalachat.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class StartActivity extends AppCompatActivity {

    TabLayout tabs;
    ViewPager viewPager;

    FragmentPagerAdapter fragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initView();
    }

    private void initView() {
        tabs                     = findViewById(R.id.tabs);
        viewPager                = findViewById(R.id.viewpager);

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            Fragment[] fragments = new Fragment[]{
                    new Rooms_Fragment(),
                    new MyRoomsFragment()
            };

            String[] names = new String[]{
                    "Rooms",
                    "My Rooms"
            };

            @Override
            public Fragment getItem(int position) {
                return fragments[position];
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return names[position];
            }
        };
        viewPager.setAdapter(fragmentPagerAdapter);
        tabs.setupWithViewPager(viewPager);
    }

}
