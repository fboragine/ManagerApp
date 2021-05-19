package it.uniba.di.sms2021.managerapp.loggedUser;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.exam.ExamActivity;
import it.uniba.di.sms2021.managerapp.guest.GuestActivity;
import it.uniba.di.sms2021.managerapp.loggedUser.StudentActivity;
import it.uniba.di.sms2021.managerapp.service.ExamListAdapter;


public class LibrettoFragment extends Fragment {

    private String cds;
    private View viewExamList;
    private ListView listView;
    private ExamListAdapter adapterEsami;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String idCdS;
    private String uidStudente;
    private String uidDocente;
    private ArrayList<Esame> esami;
    private Context context;
    private RadioButton passato;
    protected static File loginFile;
    private TextView passedExam;
    private TextView notpassedExam;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginFile = new File(getActivity().getApplicationContext().getExternalFilesDir(null), "studenti.srl");
        if(loginFile.exists()) {
            cds = StudentActivity.loggedStudent.getcDs();
            uidStudente = StudentActivity.loggedStudent.getId();
            uidDocente = "";

            riempiArray();
        }else {
            loginFile = new File(getActivity().getApplicationContext().getExternalFilesDir(null), "docenti.srl");
            if (loginFile.exists()) {
                uidDocente = StudentActivity.loggedDocent.getId();
                uidStudente = "";
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        viewExamList = inflater.inflate(R.layout.fragment_libretto, container, false);

        //((StudentActivity)getActivity()).enableBackArrow();

        if(uidStudente != "") {
            passato = (RadioButton) viewExamList.findViewById(R.id.passed);

            passato.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    riempiArray();
                }
            });
        } else if(uidDocente != "") {
            getEsamiDocente();

            passedExam = viewExamList.findViewById(R.id.passed);
            passedExam.setVisibility(View.GONE);
            notpassedExam = viewExamList.findViewById(R.id.not_passed);
            notpassedExam.setVisibility(View.GONE);
        }

        // Inflate the layout for this fragment
        return viewExamList;
    }



    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
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

        // Add Filter Menu Item
        int filterId = StudentActivity.FILTER_ITEM_ID;
        if (menu.findItem(filterId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem filter = menu.add(
                    Menu.NONE,
                    filterId,
                    2,
                    getString(R.string.action_filter)
            );

            // Set an icon for the new menu item
            filter.setIcon(R.drawable.ic_baseline_filter_alt_24);

            // Set the show as action flags for new menu item
            filter.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            // Set a click listener for the new menu item
            filter.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });

        }

        super.onPrepareOptionsMenu(menu);
    }

    public synchronized void riempiArray() {
        esami = new ArrayList<>();
        db.collection("esami").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                context = getActivity().getApplicationContext();
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(cds.equals(document.getString("cDs"))) {
                            Esame esame = new Esame(document.getString("id"),
                                    document.getString("nome"),
                                    document.getString("commento"),
                                    document.getString("desrizione"),
                                    document.getString("cDs"),
                                    (ArrayList<String>) document.get("idDocenti"));

                            db.collection("esamiStudente").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    context = getActivity().getApplicationContext();
                                    if(task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if(passato.isChecked()) {
                                                if((esame.getId().equals(document.getString("idEsame")) && (uidStudente.equals(document.getString("idStudente")))  && document.getBoolean("stato"))) {
                                                    esami.add(esame);
                                                }
                                            } else {
                                                if((esame.getId().equals(document.getString("idEsame")) && (uidStudente.equals(document.getString("idStudente")))  && !document.getBoolean("stato"))) {
                                                    esami.add(esame);
                                                }
                                            }
                                        }
                                        listView = viewExamList.findViewById(R.id.listLibretto);
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
                        }
                    }
                }
            }
        });
    }

    private void getEsamiDocente() {
        esami = new ArrayList<>();
        db.collection("esami").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                context = getActivity().getApplicationContext();
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Esame esame = new Esame(document.getString("id"),
                                document.getString("nome"),
                                document.getString("commento"),
                                document.getString("desrizione"),
                                document.getString("cDs"),
                                (ArrayList<String>) document.get("idDocenti"));

                        for(int i = 0; i < esame.getIdDocenti().size(); i++) {
                            if(uidDocente.equals(esame.getIdDocenti().get(i))) {
                                esami.add(esame);
                            }
                        }

                        listView = viewExamList.findViewById(R.id.listLibretto);
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
            }
        });
    }
}