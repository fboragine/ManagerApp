package it.uniba.di.sms2021.managerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import entities.Progetto;

public class ProjectActivity extends AppCompatActivity {

    protected static final int EDIT_ITEM_ID = View.generateViewId();
    protected static final int SAVE_ITEM_ID = View.generateViewId();
    protected static final int CANCEL_ITEM_ID = View.generateViewId();

    private Progetto progetto;

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
        toolbar.setTitle(progetto.getNome());
    }
}