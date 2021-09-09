package com.farmmanagementpro;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;


import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import io.grpc.Context;

public class MainActivity extends AppCompatActivity implements DrawerAdapter.OnItemSelectedListener{

    private static final int POS_CLOSE = 0;
    private static final int POS_DASHBOARD = 1;
    private static final int POS_MY_ANIMALS = 2;
    private static final int POS_ANIMAL_EVENTS = 3;
    private static final int POS_FARM_TASKS= 4;
    private static final int POS_FERTILIZER_HISTORY = 5;
    private static final int POS_MEDICAL_CABINET = 6;
    private static final int POS_FEED_HISTORY = 7;
    private static final int POS_MY_MACHINERY = 8;
    private static final int POS_SPRAYS = 9;
    private static final int POS_LOGOUT = 10;
    private static final String TAG = "Main Activity";

    private String[] screenTitles;
    private Drawable[] screenIcons;

    private SlidingRootNav slideRootNav;
    private BottomNavigationView bottomNavBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
//        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        bottomNavBar = (BottomNavigationView) findViewById(R.id.bottom_nav);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        try{
            setSupportActionBar(toolbar);
        }catch(IllegalStateException e){
            Log.e(TAG, "onCreate: "+ e.getMessage() );
        }

        slideRootNav = new SlidingRootNavBuilder(this)
                .withDragDistance(180)
                .withRootViewScale(0.75f)
                .withRootViewElevation(25)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.drawer_menu)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        DrawerAdapter adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(POS_CLOSE),
                createItemFor(POS_DASHBOARD),
                createItemFor(POS_MY_ANIMALS),
                createItemFor(POS_ANIMAL_EVENTS),
                createItemFor(POS_FARM_TASKS),
                createItemFor(POS_FERTILIZER_HISTORY),
                createItemFor(POS_MEDICAL_CABINET),
                createItemFor(POS_FEED_HISTORY),
                createItemFor(POS_MY_MACHINERY),
                createItemFor(POS_SPRAYS),
                new SpaceItem(260),
                createItemFor(POS_LOGOUT)
        ));

        adapter.setListener(this);

        RecyclerView list = findViewById(R.id.drawer_list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);

        adapter.setSelected(POS_DASHBOARD);


        /**
         * bottom navigation bar
         */
       // bottomNavBar.setSelectedItemId(R.id.bottom_home);
        bottomNavBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        DashboardFragment dashboardFragment = new DashboardFragment();
                        transaction.replace(R.id.container, dashboardFragment);
                        break;

                    case R.id.bottom_settings:
                        break;

                    case R.id.bottom_user:
                        break;
                }
                transaction.addToBackStack(null);
                transaction.commit();
                return true;
            }
        });
    }

    private DrawerItem createItemFor(int position){
        return new SimpleItem(screenIcons[position],screenTitles[position])
                .withIconTint(color(R.color.green))
                .withTextTint(color(R.color.black))
                .withSelectedIconTint(color(R.color.green))
                .withSelectedTextTint(color(R.color.green));
    }

    @ColorInt
    private int color(@ColorRes int res){
        return ContextCompat.getColor(this,res);
    }

    private String[] loadScreenTitles() {
        return getResources().getStringArray(R.array.id_activityScreenTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = getResources().obtainTypedArray(R.array.id_activityScreenIcons);
        Drawable[] icons = new Drawable[ta.length()];

        for(int i=0 ; i<ta.length() ; i++) {
            int id = ta.getResourceId(i,0);
            if (id != 0){
                icons[i] = ContextCompat.getDrawable(this,id);
            }
        }

        ta.recycle();
        return icons;
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onItemSelected(int position) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        if (position == POS_DASHBOARD){
            DashboardFragment dashboardFragment = new DashboardFragment();
            transaction.replace(R.id.container, dashboardFragment);
        }else if(position == POS_MY_ANIMALS){
            MyAnimalsFragment animals = new MyAnimalsFragment();
            transaction.replace(R.id.container, animals);
        }else if(position == POS_ANIMAL_EVENTS){
            AnimalEventFragment events = new AnimalEventFragment();
            transaction.replace(R.id.container, events);
        }else if(position == POS_FARM_TASKS){
            FarmTasksFragment farmTasks = new FarmTasksFragment();
            transaction.replace(R.id.container, farmTasks);
        }else if(position == POS_FERTILIZER_HISTORY){
            FertilizerHistoryFragment fertilizer = new FertilizerHistoryFragment();
            transaction.replace(R.id.container, fertilizer);
        }else if(position == POS_MEDICAL_CABINET){
            MedicalCabinetFragment medical = new MedicalCabinetFragment();
            transaction.replace(R.id.container, medical);
        }else if(position == POS_FEED_HISTORY){
            FeedHistoryFragment feedHistoryFragment = new FeedHistoryFragment();
            transaction.replace(R.id.container, feedHistoryFragment);
        }else if(position == POS_MY_MACHINERY){
            MyMachineryFragment machines = new MyMachineryFragment();
            transaction.replace(R.id.container, machines);
        }else if(position == POS_SPRAYS){
            SpraysFragment sprays = new SpraysFragment();
            transaction.replace(R.id.container, sprays);
        }else{
            finish();
        }

        slideRootNav.closeMenu();
        transaction.addToBackStack(null);
        transaction.commit();
    }
}