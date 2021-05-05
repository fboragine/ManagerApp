package it.uniba.di.sms2021.managerapp.project;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.uniba.di.sms2021.managerapp.R;
import it.uniba.di.sms2021.managerapp.entities.Progetto;
import it.uniba.di.sms2021.managerapp.entities.SpecsFile;
import it.uniba.di.sms2021.managerapp.service.FileListAdapter;

@RequiresApi(api = Build.VERSION_CODES.R)
public class ProjectDocumentsActivity extends AppCompatActivity {

    interface SpecsCallback {
        void onCallback(SpecsFile specsFile, boolean flag);
    }

    FirebaseStorage storage;
    private Progetto progetto;
    private ListView listViewFiles;
    private static List<SpecsFile> files;

    Button btnDownload, btnUploadFile, btnSelectFile;
    Switch switchUpload;
    TextView selectedFileLab;

    //track Choosing Image Intent
    private static final int CHOOSING_IMAGE_REQUEST = 1234;
    private Uri fileUri;
    private Bitmap bitmap;
    private ProgressDialog progressDialog;
    private StorageReference fileReference;

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
        progressDialog = new ProgressDialog(this);

        btnDownload = (Button) findViewById(R.id.button_download);
        btnUploadFile = (Button) findViewById(R.id.button_add_file);
        btnSelectFile = (Button) findViewById(R.id.button_select_file);
        switchUpload = (Switch) findViewById(R.id.switch_upload);
        selectedFileLab = (TextView) findViewById(R.id.selected_file);

        selectedFileLab.setVisibility(View.INVISIBLE);
        createExplorerFile();
    }

    private void createExplorerFile() {
        getFileList(1,new SpecsCallback() {
            @Override
            public synchronized void onCallback(SpecsFile specsFile, boolean flag) {
                files.add(specsFile);
                if(flag) {
                    listViewFiles = findViewById(R.id.project_files);
                    FileListAdapter adapter = new FileListAdapter(getApplicationContext(), files);
                    listViewFiles.setAdapter(adapter);

                    btnDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(adapter.selectedItem.size() == 0) {
                                Toast.makeText(getApplicationContext(), getString(R.string.no_selection_file), Toast.LENGTH_LONG).show();
                            }else {
                                for(int i = 0; i< adapter.selectedItem.size(); i++) {
                                    downloadFile(adapter.getItem(adapter.selectedItem.get(i)).getNome(),
                                                         adapter.getItem(adapter.selectedItem.get(i)).getPercorso());
                                }
                            }
                        }
                    });

                    btnSelectFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showChoosingFile();

                        }
                    });

                    btnUploadFile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uploadFile(fileUri);
                        }
                    });
                }
            }
        });
    }

    private void showChoosingFile() {
        Intent intent = new Intent();
        if(switchUpload.isChecked()) {
            intent.setType("image/*");
        }else {
            intent.setType("application/*");
        }
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select file"), CHOOSING_IMAGE_REQUEST);
    }

    public void downloadFile(String nomeFile, String percorso) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = storageRef.child(percorso);

        File localFile = new File(getExternalFilesDir(null), nomeFile);
/*
        if(!localFile.exists()) {
            localFile.mkdir();
        }*/
        progressDialog.setTitle(getString(R.string.download_select));
        progressDialog.show();

        islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), getString(R.string.download_succ) + ": " + nomeFile, Toast.LENGTH_LONG).show();
            }
        }).addOnProgressListener(new OnProgressListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onProgress(FileDownloadTask.TaskSnapshot taskSnapshot) {
                // progress percentage
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                // percentage in progress dialog
                progressDialog.setMessage("Downloaded " + ((int) progress) + "%...");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
            }
        }).addOnPausedListener(new OnPausedListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onPaused(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), getString(R.string.donwload_stop), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void uploadFile(Uri fileUrl) {
        if (fileUrl != null) {
            String fileName = fileUrl.getPath().substring(fileUrl.getPath().lastIndexOf("/")+1, fileUrl.getPath().lastIndexOf("."));

            if (!validateInputFileName(fileName)) {
                return;
            }
            progressDialog.setTitle(getString(R.string.upload_label));
            progressDialog.show();

            fileReference = FirebaseStorage.getInstance().getReference().child("progetti/" + progetto.getId());
            StorageReference fileRef = fileReference.child(fileName + "." + getFileExtension(fileUrl));

            fileRef.putFile(fileUrl)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), getString(R.string.file_upload), Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(getIntent());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            // progress percentage
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                            // percentage in progress dialog
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    })
                    .addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), getString(R.string.upload_stop), Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.no_file_upload), Toast.LENGTH_LONG).show();
        }
    }

    private boolean validateInputFileName(String fileName) {

        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(getApplicationContext(), "Enter file name!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (bitmap != null) {
            bitmap.recycle();
        }

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            this.fileUri = data.getData();
            try {
                if(switchUpload.isChecked()) {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), fileUri);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            selectedFileLab.setText(fileUri.getPath().substring(fileUri.getPath().lastIndexOf("/")+1));
            selectedFileLab.setVisibility(View.VISIBLE);
        }
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

    private synchronized void getFileList(int numberRelease, SpecsCallback myCallback) {

        StorageReference storageRef = storage.getReference();
        // Get reference to the file
        StorageReference forestRef = storageRef.child("progetti/" + progetto.getId());

        forestRef.listAll()
        .addOnSuccessListener(listResult -> {
            for (StorageReference prefix : listResult.getPrefixes()) {
                // All the prefixes under listRef.
                // You may call listAll() recursively on them.
            }

            AtomicInteger i = new AtomicInteger();

            for (StorageReference item : listResult.getItems()) {
                // All the items under listRef.
                String percorso = "progetti/" + progetto.getId() + "/" + item.getName();
                StorageReference forestRefFile = storageRef.child(percorso);
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");

                synchronized (this) {
                    forestRefFile.getMetadata().addOnSuccessListener((storageMetadata) -> {
                        SpecsFile auxFile = new SpecsFile(storageMetadata.getMd5Hash(),
                                storageMetadata.getName(),
                                formatter.format(new Date(storageMetadata.getUpdatedTimeMillis())),
                                (storageMetadata.getSizeBytes() / 1024),
                                storageMetadata.getContentType(),
                                percorso
                                );

                        if(i.get() < listResult.getItems().size()-1) {
                            myCallback.onCallback(auxFile, false);
                        }else {
                            myCallback.onCallback(auxFile, true);
                        }
                        i.getAndIncrement();
                    });
                }
            }
        })
        .addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

}