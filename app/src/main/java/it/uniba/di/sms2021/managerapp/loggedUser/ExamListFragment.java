package it.uniba.di.sms2021.managerapp.loggedUser;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.guest.GuestActivity;
import it.uniba.di.sms2021.managerapp.guest.SignActivity;
import it.uniba.di.sms2021.managerapp.exam.ExamActivity;
import it.uniba.di.sms2021.managerapp.project.ProjectActivity;
import it.uniba.di.sms2021.managerapp.service.ExamListAdapter;
import it.uniba.di.sms2021.managerapp.service.ListViewAdapter;
import it.uniba.di.sms2021.managerapp.service.Model;
import it.uniba.di.sms2021.managerapp.service.Settings;

public class ExamListFragment extends Fragment {


    private View viewExamList;
    private ListView listView;
    private ExamListAdapter adapterEsami;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idCdS;
    private ArrayList<Esame> esami = new ArrayList<>();
    private Context context;

    public ExamListFragment() {}

    public ExamListFragment(String idCdS) {
        this.idCdS = idCdS;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewExamList = inflater.inflate(R.layout.fragment_guest_home, container, false);

        ((GuestActivity)getActivity()).enableBackArrow();

        db.collection("esami").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                context = getActivity().getApplicationContext();
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(idCdS.equals(document.getString("cDs"))) {
                            Esame esame = new Esame(document.getString("id"),
                                    document.getString("nome"),
                                    document.getString("commento"),
                                    document.getString("desrizione"),
                                    document.getString("cDs"),
                                    (ArrayList<String>) document.get("idDocenti"));
                            esami.add(esame);
                        }
                    }

                    listView = viewExamList.findViewById(R.id.listView);
                    //pass results to listViewAdapter class
                    adapterEsami = new ExamListAdapter(getActivity().getApplicationContext(), esami);
                    //bind the adapter to the listview
                    listView.setAdapter(adapterEsami);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Intent intent = new Intent(getActivity().getApplicationContext(), ExamActivity.class);
                            intent.putExtra("esame",esami.get(i));
                            startActivity(intent);
                        }
                    });
                }
            }
        });

        // Inflate the layout for this fragment
        return viewExamList;
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
                    adapterEsami.filter("");
                    listView.clearTextFilter();
                }
                else {
                    adapterEsami.filter(s);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity().getApplicationContext(), Settings.class);
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