package it.uniba.di.sms2021.managerapp.loggedUser;

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
import it.uniba.di.sms2021.managerapp.service.ExamListAdapter;
import it.uniba.di.sms2021.managerapp.service.ListViewAdapter;
import it.uniba.di.sms2021.managerapp.service.Model;

public class ExamListFragment extends Fragment {


    private  View viewExamList;
    private ListView listView;
    private ExamListAdapter adapterEsami;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<String> idEsami = new ArrayList<>();
    private ArrayList<Esame> esami = new ArrayList<>();

    public ExamListFragment() {}

    public ExamListFragment(ArrayList<String> idEsami) {
        this.idEsami = idEsami;
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
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        boolean flag;
                        int count = 0;

                        do {
                            flag = false;
                            if(idEsami.get(count).equals(document.getString("id"))) {
                                Esame esame = new Esame(document.getString("id"),
                                        document.getString("nome"),
                                        document.getString("cDs"),
                                        document.getString("dataEsame"),
                                        (ArrayList<String>) document.get("idDocenti"));
                                esami.add(esame);
                            }
                            count ++;
                        }while(!flag  && count < idEsami.size());
                    }



                    listView = viewExamList.findViewById(R.id.listView);
                    //pass results to listViewAdapter class
                    adapterEsami = new ExamListAdapter(getActivity().getApplicationContext(), esami);
                    //bind the adapter to the listview
                    listView.setAdapter(adapterEsami);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            //passa dati
                            Intent intent = new Intent(getActivity().getApplicationContext(), ExamActivity.class);
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