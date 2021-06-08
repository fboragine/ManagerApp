package it.uniba.di.sms2021.managerapp.segreteria.admin;

import android.content.Intent;
import android.os.Bundle;
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
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.segreteria.editItem.EditCourseActivity;
import it.uniba.di.sms2021.managerapp.segreteria.service.SettingsAdmin;
import it.uniba.di.sms2021.managerapp.service.ListViewAdapter;

public class CoursesListFragment extends Fragment {

    private View viewCoursesList;
    private ListView courseListView;
    private ListViewAdapter adapter;
    private FirebaseFirestore db;
    private ArrayList<CorsoDiStudio> corsi;

    public CoursesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewCoursesList = inflater.inflate(R.layout.fragment_courses_list, container, false);
        return viewCoursesList;
    }

    private synchronized void getCourses() {
        db.collection("corsiDiStudio").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                corsi = new ArrayList<>();
                for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                    CorsoDiStudio corsoDiStudio = new CorsoDiStudio(document.getString("id"),
                            document.getString("nome"),
                            document.getString("descrizione"));
                    corsi.add(corsoDiStudio);
                }

                courseListView = viewCoursesList.findViewById(R.id.courseListView);

                //pass results to listViewAdapter class
                adapter = new ListViewAdapter(requireActivity().getApplicationContext(), corsi);

                //bind the adapter to the listView
                courseListView.setAdapter(adapter);

                courseListView.setOnItemClickListener((parent, view, position, id) -> {
//                    EditCourseFragment courseEditFragment = new EditCourseFragment(corsi.get(position));
//                    FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
//                    fragmentTransaction.hide(Objects.requireNonNull(getParentFragmentManager().getPrimaryNavigationFragment()));
//                    fragmentTransaction.add(R.id.fragment, courseEditFragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();
                    Intent intent = new Intent(requireActivity().getApplicationContext(), EditCourseActivity.class);
                    intent.putExtra("cDs",corsi.get(position));
                    startActivity(intent);
                });
            }
        });
    }

    @Override
    public void onResume() {
        getCourses();
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
                    courseListView.clearTextFilter();
                } else {
                    adapter.filter(newText);
                }
                return true;
            }
        });
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
}