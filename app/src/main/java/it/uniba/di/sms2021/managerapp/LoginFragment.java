package it.uniba.di.sms2021.managerapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "Pippo";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login2Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        /*Button bRegistazione = (Button) getActivity().findViewById(R.id.button);
        bRegistazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister(v);
            }
        });

        //bRegistazione.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.sign_up, null));
        /*FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
        NavHostFragment navHostFragment =
                (NavHostFragment) supportFragmentManager.findFragmentById(R.id.btn_goto_register);
        NavController navController = navHostFragment.getNavController();*/



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

    /*
        View v = inflater.inflate(R.layout.fragment_login, container,   false);

        Button bRegistazione= getActivity().findViewById(R.id.btn_goto_register);
        bRegistazione.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {

                SignUpFragment signUpFragment = new SignUpFragment();
                FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.guest_layout, signUpFragment);
                fragmentTransaction.commit();
            }
        });*/

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    public void goToRegister(View v) {
        Log.d(TAG, "Entro");
    }

}