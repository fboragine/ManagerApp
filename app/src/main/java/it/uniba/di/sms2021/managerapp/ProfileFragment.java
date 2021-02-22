package it.uniba.di.sms2021.managerapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import javax.security.auth.callback.Callback;

import entities.Studente;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GuestActivity callback;
    private FirebaseDatabase db;
    private DatabaseReference userRef;

    private static final String TAG = "SimpleToolbarTest";

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Profilo");

        Intent intent = getActivity().getIntent();
        String matricola = intent.getStringExtra("matricola");

        TextView fullname = getActivity().findViewById(R.id.full_name);
        TextView email = getActivity().findViewById(R.id.profile_email);

        db = FirebaseDatabase.getInstance();
        userRef = db.getReference("Studenti");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Log.d(TAG, "Arrivoooooo");
                    if(ds.child("Matricola").equals("1")) {
                        fullname.setText(ds.child("Nome" + " " + "Cognome").getValue(String.class));
                        email.setText(ds.child("Email").getValue(String.class));
                        Log.d(TAG, "Risultatooooooo");
                        Log.d(TAG, fullname.toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    /*@Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            callback = (GuestActivity) context;
        }
    }*/
}