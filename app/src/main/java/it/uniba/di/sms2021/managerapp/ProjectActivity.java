package it.uniba.di.sms2021.managerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

import entities.Progetto;

public class ProjectActivity extends AppCompatActivity {

    protected static final int EDIT_ITEM_ID = View.generateViewId();
    protected static final int SAVE_ITEM_ID = View.generateViewId();
    protected static final int CANCEL_ITEM_ID = View.generateViewId();

    private Progetto progetto;
    private TextView textViewNome;
    private ArrayList<String> studenti;
    private ListView listViewStudenti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        final Intent src = getIntent();
        if(src != null) {
            progetto = src.getParcelableExtra("progetto");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(progetto.getNome());

        textViewNome = findViewById(R.id.project_name);
        textViewNome.setText(progetto.getNome());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        //Nascondo pulsante ricerca
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);

        studenti = new ArrayList<>(Arrays.asList("Mario Rossi", "Luigi Verdi", "Rosa Neri", "Filippo Neri"));
        listViewStudenti = findViewById(R.id.project_students);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, studenti);
        listViewStudenti.setAdapter(adapter);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}