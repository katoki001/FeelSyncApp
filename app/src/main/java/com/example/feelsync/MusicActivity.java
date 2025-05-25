package com.example.feelsync;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proglish2.BuildConfig;
import com.example.proglish2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private MusicService musicService;
    private boolean isBound = false;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.LocalBinder binder = (MusicService.LocalBinder) service;
            musicService = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .build());
        }
        setContentView(R.layout.activity_music);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);

        // Set up the ViewPager2 with fragments
        MusicPagerAdapter pagerAdapter = new MusicPagerAdapter(this);
        pagerAdapter.addFragment(new LocalMusicFragment());
        pagerAdapter.addFragment(new OnlineMusicFragment());
        viewPager.setAdapter(pagerAdapter);

        // Link TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Local");
                    break;
                case 1:
                    tab.setText("Online");
                    break;
            }
        }).attach();
        setupBottomNavigation(bottomNavigation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        startService(intent); // Ensure service is running
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    public void playSong(String url) {
        if (isBound && musicService != null) {
            musicService.playSong(url);
        } else {
            Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT).show();
        }
    }

    public void togglePlayback() {
        if (isBound && musicService != null) {
            musicService.togglePlayback();
        }
    }

    private static class MusicPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();

        public MusicPagerAdapter(@NonNull AppCompatActivity activity) {
            super(activity);
        }

        public void addFragment(Fragment fragment) {
            fragments.add(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }

    private void setupBottomNavigation(BottomNavigationView nav) {
        nav.setSelectedItemId(R.id.nav_music);

        nav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_music) {
                return true;
            }

            Class<?> cls = id == R.id.nav_calendar ? CalendarActivity.class :
                    id == R.id.nav_home ? MainPageActivity.class :
                            id == R.id.nav_settings ? SettingsActivity.class :
                                    id == R.id.nav_ai ? AIChatActivity.class : null;

            if (cls != null) {
                startActivity(new Intent(this, cls));
                finish();
                return true;
            }
            return false;
        });
    }
}