package it.uniba.di.sms2021.managerapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Set;

public class GuestActivity extends AppCompatActivity {

    private static final String TAG = "SimpleToolbarTest";
    protected static final int SEARCH_ITEM_ID = View.generateViewId();
    protected static final int FILTER_ITEM_ID = View.generateViewId();

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("Home");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(findViewById(R.id.fragment));
        AppBarConfiguration appBarConfiguration = (new AppBarConfiguration.Builder(Set.of(R.id.guestHomeFragment, R.id.loginFragment))).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
    }

    public void go_to_register(View view) {
        NavDirections action = LoginFragmentDirections.actionLoginFragmentToSignUpFragment();
        Navigation.findNavController(this,R.id.fragment).navigate(action);
    }

    public void go_to_login(View view) {
        NavDirections action = SignUpFragmentDirections.actionSignUpFragmentToLoginFragment();
        Navigation.findNavController(this,R.id.fragment).navigate(action);
    }
}