package entities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class GestioneProgetti {

    private static final String TAG = "GestioneProgetti";
    private ArrayList<Progetto> progetti;

    public ArrayList<Progetto> getProgetti() {

        FirebaseFirestore rootRef = FirebaseFirestore.getInstance();

        rootRef.collection("progetti").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Progetto> progetti = new ArrayList<Progetto>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Progetto progetto = new Progetto(document.getString("id"), document.getString("nome"), document.getString("descrizione"),
                                                     document.getString("codiceEsame"), document.getDate("dataCreazione"),
                                                     (ArrayList<String>) document.get("idStudenti"));
                    progetti.add(progetto);
                    Log.d(TAG, progetto.getNome());
                }
                Log.d(TAG, "Escooooo");

            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });

        return  progetti;
    }
}