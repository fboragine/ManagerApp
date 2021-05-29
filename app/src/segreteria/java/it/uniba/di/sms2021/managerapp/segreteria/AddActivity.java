package it.uniba.di.sms2021.managerapp.segreteria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import it.uniba.di.sms2021.managerapp.R;

public class AddActivity extends AppCompatActivity {

    private String[] items;
    private AddExamFragment examFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        Toolbar toolbar = findViewById(R.id.top_register_item_toolbar);
        setSupportActionBar(toolbar);

        // Add the drop down menu
        items = new String[]{getString(R.string.teacher), getString(R.string.exam), getString(R.string.course)};
        Spinner spinner = findViewById(R.id.itemSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO implementare cambio fragment aggiunta item
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO implementare default?
            }
        });

        // Add the fragment
        examFragment = new AddExamFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.currentFrame, examFragment);
        transaction.commit();
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_register_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.do_registration:
                // TODO implementare aggiunta item al db
                break;
            case R.id.cancel_registration:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}