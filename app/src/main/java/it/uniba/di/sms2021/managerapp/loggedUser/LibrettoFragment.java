package it.uniba.di.sms2021.managerapp.loggedUser;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.ArrayList;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.exam.ExamActivity;
import it.uniba.di.sms2021.managerapp.service.ExamListAdapter;
import it.uniba.di.sms2021.managerapp.service.Settings;


public class LibrettoFragment extends Fragment {

    private String cds;
    private View viewExamList;
    private ListView listView;
    private ExamListAdapter adapterEsami;
    private FirebaseFirestore db;
    private String uidStudente;
    private String uidDocente;
    private ArrayList<Esame> esami;
    private RadioButton passato;
    protected static File loginFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        esami = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        loginFile = new File(getActivity().getApplicationContext().getExternalFilesDir(null), "studenti.srl");
        if(loginFile.exists()) {
            cds = StudentActivity.loggedStudent.getcDs();
            uidStudente = StudentActivity.loggedStudent.getId();
            uidDocente = "";
            if (!esami.isEmpty()) {
                riempiArray();
            }
        }else {
            loginFile = new File(getActivity().getApplicationContext().getExternalFilesDir(null), "docenti.srl");
            if (loginFile.exists()) {
                uidDocente = StudentActivity.loggedDocent.getId();
                uidStudente = "";
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewExamList = inflater.inflate(R.layout.fragment_libretto, container, false);

        if(!uidStudente.equals("")) {
            passato = viewExamList.findViewById(R.id.passed);

            passato.setOnCheckedChangeListener((buttonView, isChecked) -> riempiArray());
        } else if(!uidDocente.equals("")) {
            getEsamiDocente();

            TextView passedExam = viewExamList.findViewById(R.id.passed);
            passedExam.setVisibility(View.GONE);
            TextView notpassedExam = viewExamList.findViewById(R.id.not_passed);
            notpassedExam.setVisibility(View.GONE);
        }

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
            Intent intent = new Intent(getActivity().getApplicationContext(), Settings.class);
            startActivity(intent);
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
            filter.setOnMenuItemClickListener(item -> {
                Toast.makeText(getActivity().getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                return true;
            });

        }

        super.onPrepareOptionsMenu(menu);
    }

    public synchronized void riempiArray() {
        esami = new ArrayList<>();
        db.collection("esami").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if(cds.equals(document.getString("cDs"))) {
                        Esame esame = new Esame(document.getString("id"),
                                document.getString("nome"),
                                document.getString("descrizione"),
                                document.getString("cDs"),
                                (ArrayList<String>) document.get("idDocenti"));

                        db.collection("esamiStudente").get().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                for (QueryDocumentSnapshot document1 : task1.getResult()) {
                                    if(passato.isChecked()) {
                                        if((esame.getId().equals(document1.getString("idEsame")) && (uidStudente.equals(document1.getString("idStudente")))  && document1.getBoolean("stato"))) {
                                            esami.add(esame);
                                        }
                                    } else {
                                        if((esame.getId().equals(document1.getString("idEsame")) && (uidStudente.equals(document1.getString("idStudente")))  && !document1.getBoolean("stato"))) {
                                            esami.add(esame);
                                        }
                                    }
                                }
                                listView = viewExamList.findViewById(R.id.listLibretto);
                                //pass results to listViewAdapter class
                                adapterEsami = new ExamListAdapter(getActivity().getApplicationContext(), esami);
                                //bind the adapter to the listview
                                listView.setAdapter(adapterEsami);

                                listView.setOnItemClickListener((adapterView, view, i, l) -> {
                                    Intent intent = new Intent(getActivity().getApplicationContext(), ExamActivity.class);
                                    intent.putExtra("esame",esami.get(i));
                                    startActivity(intent);
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    private void getEsamiDocente() {
        esami = new ArrayList<>();
        db.collection("esami").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {

                    Esame esame = new Esame(document.getString("id"),
                            document.getString("nome"),
                            document.getString("descrizione"),
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

                    listView.setOnItemClickListener((adapterView, view, i, l) -> {
                        Intent intent = new Intent(getActivity().getApplicationContext(), ExamActivity.class);
                        intent.putExtra("esame",esami.get(i));
                        startActivity(intent);
                    });
                }
            }
        });
    }
}