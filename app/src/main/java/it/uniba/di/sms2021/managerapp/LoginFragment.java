package it.uniba.di.sms2021.managerapp;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    View vistaLogin;
    Button btnLogin;
    Button btnResetPw;
    EditText email;
    EditText password;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this
        vistaLogin = inflater.inflate(R.layout.fragment_login, container, false);

        btnLogin = (Button) vistaLogin.findViewById(R.id.buttonLogin);
        btnResetPw = (Button) vistaLogin.findViewById(R.id.btn_reset_password);

        btnLogin.setOnClickListener(this);
        btnResetPw.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = FirebaseAuth.getInstance().getCurrentUser();

        // Inflate the layout for this fragment
        return vistaLogin;
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.buttonLogin)    //Click sul pulsante login
        {
            email = (EditText)vistaLogin.findViewById(R.id.emailTxt);
            password = (EditText)vistaLogin.findViewById(R.id.passwordTxt);
            login(email.getText().toString(), password.getText().toString());
        }
        else if(view.getId() == R.id.btn_reset_password)    //Click sul pulsante di reset della pw
        {
            //reset della password
        }
    }

    /**
     * Funzione di login al database Firebase
     * La funzione confonta i dati relativi al login (parametri) e controlla se l'utente
     * corrispondente alle credenziali è nel database, in caso di successo le credenziali e il token
     * di accesso viene salvato nei file cache (vedi slide persistenza), in caso di login fallito
     * visualizza un toast message che informa l'utente.
     * N.B: In caso di logout i file di cache delle credenziali vengono cancellati
     *
     * @param email: email registrata nel database;
     * @param password : password utilizzata per l'accesso; "password non in chiaro nel database"
     */
    public void login(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener((Activity) getContext(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) //Login avvenuto con successo
                {
                    FirebaseUser user;
                    user = mAuth.getCurrentUser();

                    Toast.makeText(getActivity().getApplicationContext()," Benvenuto " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    //salvaCacheFile((Object) user);

                    //Trasferire a fragment home
                }
                else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(getActivity().getApplicationContext()," Autenticazione fallita" + task.getException(), Toast.LENGTH_LONG).show();
                }
            }
        });

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

}