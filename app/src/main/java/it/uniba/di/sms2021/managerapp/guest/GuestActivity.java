package it.uniba.di.sms2021.managerapp.guest;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.Set;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity;
import it.uniba.di.sms2021.managerapp.service.Settings;

public class GuestActivity extends AppCompatActivity {

    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest);

        String pathStudente = getExternalFilesDir(null).getPath() + "/studenti.srl";
        String pathDocente = getExternalFilesDir(null).getPath() + "/docenti.srl";
        File loggedStudente = new File(pathStudente);
        File loggedDocente = new File(pathDocente);

        if(getApplicationContext().getExternalFilesDir(null).listFiles().length == 0 ||
            !loggedStudente.exists() &&
            !loggedDocente.exists()){
            toolbar = findViewById(R.id.top_toolbar);
            setSupportActionBar(toolbar);
            toolbar.setLogo(R.mipmap.ic_launcher);
            toolbar.setTitle(R.string.home);
            toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_ios_new_24);
            toolbar.setNavigationOnClickListener(view -> goBackFragment());

            BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
            NavController navController = Navigation.findNavController(findViewById(R.id.fragment));
            AppBarConfiguration appBarConfiguration = (new AppBarConfiguration.Builder(Set.of(R.id.guestHomeFragment, R.id.loginFragment))).build();

            openDialog();

            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(bottomNavigationView,navController);
        } else {
            Intent intent = new Intent(getApplicationContext(), StudentActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.alert_home)
                .setNegativeButton(R.string.close, (dialog, id) -> dialog.cancel());

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(R.string.welcome_msg);
        alert.show();
    }

    public void go_to_register(View view) {
        Intent intent = new Intent(getApplicationContext(), SignActivity.class);
        startActivity(intent);
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
        toolbar.setNavigationOnClickListener(view -> goBackFragment());
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}