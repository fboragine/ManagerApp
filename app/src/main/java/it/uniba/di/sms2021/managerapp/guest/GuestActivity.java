package it.uniba.di.sms2021.managerapp.guest;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Set;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.loggedUser.ExamListFragment;
import it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity;

public class GuestActivity extends AppCompatActivity {

    private static final String TAG = "SimpleToolbarTest";
    protected static final int SEARCH_ITEM_ID = View.generateViewId();
    protected static final int FILTER_ITEM_ID = View.generateViewId();
    protected static final int SAVE_ITEM_ID = View.generateViewId();
    protected static final int CANCEL_ITEM_ID = View.generateViewId();

    private ArrayList<Progetto> progetti;

    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        if(getApplicationContext().getExternalFilesDir(null).listFiles().length == 0){
            toolbar = (Toolbar) findViewById(R.id.top_toolbar);
            setSupportActionBar(toolbar);
            toolbar.setLogo(R.mipmap.ic_launcher);
            toolbar.setTitle("Home");
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    goBackFragment();
                }
            });

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            NavController navController = Navigation.findNavController(findViewById(R.id.fragment));
            AppBarConfiguration appBarConfiguration = (new AppBarConfiguration.Builder(Set.of(R.id.guestHomeFragment, R.id.loginFragment))).build();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(bottomNavigationView,navController);
        } else {
            Intent intent = new Intent(getApplicationContext(), StudentActivity.class);;
            startActivity(intent);
            finish();
        }
    }

    public void go_to_register(View view) {
        Intent intent = new Intent(getApplicationContext(), SignActivity.class);
        startActivity(intent);
    }

    public void fakeLogin(View view) {
        Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
        intent.putParcelableArrayListExtra("progetti", progetti);
        startActivity(intent);
        finish();
    }

    public void goBackFragment() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }

    public void disableBackArrow() {
        toolbar.setNavigationIcon(null);
    }

    public void enableBackArrow() {
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBackFragment();
            }
        });
    }
}