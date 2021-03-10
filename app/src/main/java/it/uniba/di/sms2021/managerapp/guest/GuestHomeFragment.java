package it.uniba.di.sms2021.managerapp.guest;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.CorsoDiStudio;
import it.uniba.di.sms2021.managerapp.entities.Studente;
import it.uniba.di.sms2021.managerapp.service.ListViewAdapter;
import it.uniba.di.sms2021.managerapp.service.Model;

public class GuestHomeFragment extends Fragment {

    private View viewGuestHome;
    private ListView listView;
    private ListViewAdapter adapter;
    String[] title;
    String[] description;
    private  ArrayList<Model> arrayList = new ArrayList<>();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    public GuestHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewGuestHome = inflater.inflate(R.layout.fragment_guest_home, container, false);

        ArrayList<CorsoDiStudio> corsiDiStudio = new ArrayList<CorsoDiStudio>();

        db.collection("corsiDiStudio").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        CorsoDiStudio corsoDiStudio = new CorsoDiStudio(document.getString("id"),
                                                                        document.getString("nome"),
                                                                        document.getString("descrizione"));

                        corsiDiStudio.add(corsoDiStudio);
                    }

                    listView = viewGuestHome.findViewById(R.id.listView);

                    //pass results to listViewAdapter class
                    adapter = new ListViewAdapter(getActivity().getApplicationContext(), corsiDiStudio);

                    //bind the adapter to the listview
                    listView.setAdapter(adapter);
                }
            }
        });
        // Inflate the layout for this fragment
        return viewGuestHome;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(TextUtils.isEmpty(s)) {
                    adapter.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapter.filter(s);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }
}