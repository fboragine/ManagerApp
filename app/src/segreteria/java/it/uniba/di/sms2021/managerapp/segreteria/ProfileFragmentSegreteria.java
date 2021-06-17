package it.uniba.di.sms2021.managerapp.segreteria;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Esame;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.entities.Utente;
import it.uniba.di.sms2021.managerapp.segreteria.admin.HomeAdminActivity;
import it.uniba.di.sms2021.managerapp.service.Settings;



public class ProfileFragmentSegreteria extends Fragment {

    private FirebaseFirestore db;
    private ImageView profileImg;
    private TextView label;
    private Utente utente;
    private Boolean isStudent;
    private Boolean errDocente;

    public ProfileFragmentSegreteria() {
    }

    public ProfileFragmentSegreteria(Utente utente, boolean flag) {
        this.utente = utente;
        this.isStudent = flag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = FirebaseFirestore.getInstance();
        setHasOptionsMenu(true);
    }

    private void viewImgProfile() {
        String defaultImgProfile = "imgProfile.png";
        File localFile = new File(getActivity().getExternalFilesDir(null) + "/user media", defaultImgProfile);
        if(localFile.exists()) {
            profileImg.setImageURI(Uri.parse(localFile.getPath()));
        }else {
            downloadFile(defaultImgProfile, "file user/" + utente.getId());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View vistaProfilo = inflater.inflate(R.layout.fragment_profile, container, false);

        label = vistaProfilo.findViewById(R.id.name);
        label.setText(utente.getNome());

        label = vistaProfilo.findViewById(R.id.surname);
        label.setText(utente.getCognome());

        label = vistaProfilo.findViewById(R.id.serial_number);
        label.setText(utente.getMatricola());

        label = vistaProfilo.findViewById(R.id.profile_email);
        label.setText(utente.getEmail());

        profileImg = vistaProfilo.findViewById(R.id.ic_action_name);
        viewImgProfile();

        return vistaProfilo;
    }

    public void downloadFile(String nomeFile, String percorso) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(percorso + "/" + nomeFile);

        File localFile = new File(getActivity().getExternalFilesDir(null) + "/user media", nomeFile);

        islandRef.getFile(localFile).addOnSuccessListener(taskSnapshot -> profileImg.setImageURI(Uri.parse(localFile.getPath()))).addOnFailureListener(exception -> {
        }).addOnPausedListener(taskSnapshot -> {
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        menuItem.setVisible(false);
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

        int cancelId  = View.generateViewId();

        if (menu.findItem(cancelId) == null) {
            // If it not exists then add the menu item to menu
            MenuItem cancel = menu.add(
                    Menu.NONE,
                    cancelId,
                    1,
                    getString(R.string.cancel)
            );

            cancel.setIcon(R.drawable.ic_whatsapp);
            cancel.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM);

            cancel.setOnMenuItemClickListener(item -> {
                delete();

                return true;
            });
        }

        super.onPrepareOptionsMenu(menu);
    }

    private void delete() {
        Map<String ,Object> userModify = new HashMap<>();
        DocumentReference docUpdate = null;

        userModify.put("email",utente.getEmail());
        userModify.put("id",utente.getId());

        if(isStudent) {
            rimozioneEsamiStudente(utente.getId());
            docUpdate = db.collection("studenti").document(utente.getId());
        }else if(!isStudent) {
            rimozioneEsamiDocente(utente.getId());
            docUpdate = db.collection("docenti").document(utente.getId());
        }

        docUpdate
                .delete()
                .addOnSuccessListener(aVoid -> Toast.makeText(getActivity().getApplicationContext(), R.string.succesful_save, Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(getActivity().getApplicationContext(), R.string.error_save, Toast.LENGTH_SHORT).show());

        Intent intent = new Intent(getActivity().getApplicationContext(), HomeAdminActivity.class);
        startActivity(intent);
    }

    private void rimozioneEsamiDocente(String idDocente) {
        db.collection("esami").get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Esame esame = new Esame(document.getString("id"),
                            document.getString("nome"),
                            document.getString("descrizione"),
                            document.getString("cDs"),
                            (ArrayList<String>) document.get("idDocenti"));

                    for (int i = 0; i < esame.getIdDocenti().size(); i++) {
                        if((esame.getIdDocenti().get(i)).equals(idDocente)) {

                                esame.getIdDocenti().remove(i);
                                Map<String ,Object> exam = new HashMap<>();

                                exam.put("id",esame.getId());
                                exam.put("descrizione",esame.getDescrizione());
                                exam.put("cDs",esame.getcDs());
                                exam.put("idDocenti",esame.getIdDocenti());
                                exam.put("nome", esame.getNome());

                                db.collection("esami").document(document.getId()).update(exam);
                        }
                    }
                }
            }
        });
    }

    private void rimozioneEsamiStudente(String idStudente) {
        db.collection("esamiStudente")
                .whereEqualTo("idStudente", idStudente)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(DocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                    rimozioniProgetti(idStudente);
                    db.collection("esamiStudente").document(document.getId()).delete();
                }
            }
        });
    }

    private void rimozioniProgetti(String idStudente) {
        db.collection("progetti").get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
            if(task.isSuccessful()) {
                FirebaseStorage storage = FirebaseStorage.getInstance();

                for (QueryDocumentSnapshot document : task.getResult()) {
                        Progetto progetto = new Progetto(document.getString("id"),
                                document.getString("nome"),
                                document.getString("descrizione"),
                                document.getString("codiceEsame"),
                                document.getString("data"),
                                (ArrayList<String>) document.get("idStudenti"),
                                document.getBoolean("stato"),
                                document.getBoolean("valutato"));

                        for (int i = 0; i < progetto.getIdStudenti().size(); i++) {
                            if((progetto.getIdStudenti().get(i)).equals(idStudente)) {
                                if(progetto.getIdStudenti().size() == 1) {
                                    rimozioniFileProgetto(storage, document.getId());
                                    db.collection("progetti").document(document.getId()).delete();
                                } else {
                                    progetto.getIdStudenti().remove(i);
                                    Map<String ,Object> project = new HashMap<>();

                                    project.put("codiceEsame",progetto.getCodiceEsame());
                                    project.put("data",progetto.getDataCreazione());
                                    project.put("descrizione",progetto.getDescrizione());
                                    project.put("id",progetto.getId());
                                    project.put("idStudenti",progetto.getIdStudenti());
                                    project.put("nome", progetto.getNome());
                                    project.put("stato", progetto.getStato());
                                    project.put("valutato", progetto.isValutato());

                                    db.collection("progetti").document(document.getId()).update(project);
                                }
                            }
                        }
                }
            }
        });
    }

    private void rimozioniFileProgetto(FirebaseStorage storage, String idProgetto) {
        StorageReference listRef = storage.getReference().child("progetti/" + idProgetto);
        listRef.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference prefix : listResult.getPrefixes()) {
                        prefix.delete();
                    }

                    for (StorageReference item : listResult.getItems()) {
                        item.delete();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(requireActivity().getApplicationContext(), "File project: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}