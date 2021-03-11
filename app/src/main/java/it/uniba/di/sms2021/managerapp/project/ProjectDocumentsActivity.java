package it.uniba.di.sms2021.managerapp.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InlineSuggestionsRequest;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.entities.SpecsFile;
import it.uniba.di.sms2021.managerapp.service.FileListAdapter;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;

@RequiresApi(api = Build.VERSION_CODES.R)
public class ProjectDocumentsActivity extends AppCompatActivity {

    private Progetto progetto;
    private ArrayList<SpecsFile> files;
    FirebaseStorage storage;
    private static final String TAG = "ProjectDocumentActivityLog";

    /*  private ListView listViewFiles;
    private List<String> item;
    private List<String> path;
    private List<String> files;
    private List<String> filesPath;
    private String currentDir;
    private String root = Environment.getStorageDirectory().getPath();
    private FileListAdapter fileListAdapter;
    private String text;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_documents);

        final Intent src = getIntent();
        if(src != null) {
            progetto = src.getParcelableExtra("progetto");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setTitle(progetto.getNome());

        // Get the default bucket from a custom FirebaseApp
        storage = FirebaseStorage.getInstance();
        files = new ArrayList<>();

        getFileList(1);
        /*
        ArrayList<String> files = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            files.add("File " + (i+1));
        }*/

        /*listViewFiles = findViewById(R.id.project_files);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_item, files);
        listViewFiles.setAdapter(adapter);


        getDirFromRoot(root);*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), item.getTitle()+" Clicked", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("LongLogTag")
    private void getFileList(int numberRelease) {

        StorageReference storageRef = storage.getReference();
        // Get reference to the file
        StorageReference forestRef = storageRef.child("progetti/" + progetto.getId());

        forestRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        for (StorageReference prefix : listResult.getPrefixes()) {
                            // All the prefixes under listRef.
                            // You may call listAll() recursively on them.
                            Log.d(TAG, "PREFIX");
                            Log.d(TAG, prefix.getMetadata().toString());
                        }

                        for (StorageReference item : listResult.getItems()) {
                            // All the items under listRef.
                            getMetadataFile(item.getName());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "errore",e);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @SuppressLint("LongLogTag")
    private void getMetadataFile(String nomeFile) {
        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Get reference to the file
        StorageReference forestRef = storageRef.child("progetti/"+progetto.getId()+"/"+nomeFile);

        forestRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                files.add(new SpecsFile(storageMetadata.getMd5Hash(),
                                        storageMetadata.getName(),
                                        formatter.format(new Date(storageMetadata.getUpdatedTimeMillis())),
                                        (storageMetadata.getSizeBytes()/1024),
                                        storageMetadata.getContentType() ));
                Log.d(TAG, storageMetadata.getName());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
   /* // Get directories and files from selected path
    public void getDirFromRoot(String rootPath) {
        item = new ArrayList<>();
        Boolean isRoot = true;
        path = new ArrayList<>();
        files = new ArrayList<>();
        filesPath = new ArrayList<>();

        File file = new File(rootPath);
        File[] filesArray = file.listFiles();
        if(rootPath.equals(root)) {
            item.add("../");
            path.add(file.getParent());
            isRoot = false;
        }
        currentDir = rootPath;

        //sorting file list in alphabetic order
        Arrays.sort(filesArray);
        for(int i = 0; i < filesArray.length; i++) {
            File fileAux = filesArray[i];
            if(fileAux.isDirectory()) {
                item.add(fileAux.getName());
                path.add(fileAux.getPath());
            } else {
                files.add(fileAux.getName());
                filesPath.add(fileAux.getPath());
            }
        }

        for(String addFile : files) {
            item.add(addFile);
        }
        for (String addPath : filesPath) {
            path.add(addPath);
        }

        fileListAdapter = new FileListAdapter(this, item, path, isRoot);
        listViewFiles.setAdapter(fileListAdapter);
        listViewFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                File isFile = new File(path.get(position));
                if (isFile.isDirectory()) {
                    getDirFromRoot(isFile.toString());
                } else {
                    Toast.makeText(ProjectDocumentsActivity.this, "This is File", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }*/
/*
    //Method to delete selected files
    void deleteFile() {
        for (int deleteItem : fileListAdapter.selectedItem) {
            File deleteFIle = new File(path.get(deleteItem));
            Log.d("FILE", path.get(deleteItem));
            Boolean isDeleted = deleteFIle.delete();
            Toast.makeText(ProjectDocumentsActivity.this, "File(s) Deleted", Toast.LENGTH_SHORT).show();
            getDirFromRoot(currentDir);
        }
    }

    //Create file or directory in the directory in which we are present currently
    void createNewFolder(final int opt) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText editInput = new EditText(this);
        // Specify the type of the input expected
        editInput.setInputType(InputType.TYPE_CLASS_TEXT);
        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                text = editInput.getText().toString();
                if(opt == 1) {
                    File newPath = new File(currentDir, text);
                    Log.d("CURRENT DIR", currentDir);
                    if(!newPath.exists()) {
                        newPath.mkdirs();
                    }
                } else {
                    try {
                        FileOutputStream outputStream = new FileOutputStream((currentDir+File.separator+text), false);
                        outputStream.close();
                        //  <!--<intent-filter>
                        //  <action android:name="android.intent.action.SEARCH" />
                        //  </intent-filter>
                        //  <meta-data android:name="android.app.searchable"
                        //  android:resource="@xml/searchable"/>-->
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                getDirFromRoot(currentDir);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setView(editInput);
        builder.show();
    }*/

}

//https://www.youtube.com/watch?v=ZmgncLHk_s4&t=104s
//https://www.technetexperts.com/mobile/custom-file-explorer-in-android-application-development/