package it.uniba.di.sms2021.managerapp.segreteria.admin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Docente;
import it.uniba.di.sms2021.managerapp.entities.Utente;
import it.uniba.di.sms2021.managerapp.segreteria.editItem.EditProfileActivity;
import it.uniba.di.sms2021.managerapp.segreteria.service.SettingsAdmin;
import it.uniba.di.sms2021.managerapp.segreteria.service.UserListAdapter;

public class TeachersListFragment extends Fragment {

    private View viewTeachersList;
    private ListView teacherListView;
    private UserListAdapter adapter;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<Utente> docenti;

    public TeachersListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewTeachersList = inflater.inflate(R.layout.fragment_teachers_list, container, false);

        return viewTeachersList;
    }

    private synchronized void getTeachers(ArrayList<Utente> docenti) {
        if(docenti.isEmpty()) {
            db.collection("docenti").get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        Docente docente = new Docente(document.getString("id"),
                                document.getString("matricola"),
                                document.getString("nome"),
                                document.getString("cognome"),
                                document.getString("email"));
                        docenti.add(docente);
                    }

                    teacherListView = viewTeachersList.findViewById(R.id.teacherListView);

                    //pass results to listViewAdapter class
                    adapter = new UserListAdapter(requireActivity().getApplicationContext(), docenti);

                    //bind the adapter to the listView
                    teacherListView.setAdapter(adapter);

                    teacherListView.setOnItemClickListener((parent, view, position, id) -> {
                        Intent intent = new Intent(requireActivity().getApplicationContext(), EditProfileActivity.class);
                        intent.putExtra("utente", (Parcelable) docenti.get(position));
                        intent.putExtra("isStudent", false);
                        startActivity(intent);
                    });
                }
            });
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(TextUtils.isEmpty(newText)) {
                    adapter.filter("");
                    teacherListView.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(requireActivity().getApplicationContext(), SettingsAdmin.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        docenti = new ArrayList<>();
        getTeachers(docenti);
        super.onResume();
    }
}