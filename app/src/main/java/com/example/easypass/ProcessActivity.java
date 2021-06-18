package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class ProcessActivity extends AppCompatActivity implements View.OnClickListener {
    TextView welcome_mess;
    Button btnPassport, btnID, btnBirthdate, btnPoliceCertificate, btnFamilyTree, btnDownLoad;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    static final int PERMISSION_CAMERA = 888;
    static final int CAPTURE_IMAGE = 1024;
    static final int PICKFILE_RESULT_CODE = 1;
    ImageView PassPortPic, IdPic, BirthdatePic, PolicePic, FamilyTreePic;
    StorageReference storageReference;
    String imageName = "";
    String fileName = "";
    ProgressDialog dialog;
    String user = null;
    Uri uriPassPort, uriId, UriTree, uriBirthdate, uriPolice;
    static int counter = 0;

    @Override
    public void onBackPressed() {
        logoutUser();
    }

    private void logoutUser() {
        new AlertDialog.Builder(ProcessActivity.this).
                setTitle("התנתקות").
                setMessage("אתה בטוח שאתה רוצה להתנתק ?").
                setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        startActivity(new Intent(ProcessActivity.this, MainActivity.class));
                    }
                })
                .setNegativeButton(android.R.string.no, null).
                setIcon(android.R.drawable.ic_dialog_info).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        storageReference = FirebaseStorage.getInstance().getReference().child("userDocuments");
        database = FirebaseDatabase.getInstance("https://easypass-dcff0-default-rtdb.europe-west1.firebasedatabase.app/");
        initViews();
        initButtons();
        readFromDB();
        counter = 0;

    }

    private void readFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.child("userName").getValue(String.class);
                welcome_mess.setText("ברוך הבא " + user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initButtons() {
        btnPassport.setOnClickListener(this);
        btnID.setOnClickListener(this);
        btnBirthdate.setOnClickListener(this);
        btnPoliceCertificate.setOnClickListener(this);
        btnFamilyTree.setOnClickListener(this);
        btnDownLoad.setOnClickListener(this);
    }

    private void initViews() {
        mAuth = FirebaseAuth.getInstance();
        welcome_mess = findViewById(R.id.welcome_message);
        myRef = database.getReference("Users").child(mAuth.getUid());
        btnPassport = findViewById(R.id.btn_upload_pass);
        btnID = findViewById(R.id.btn_upload_ID);
        btnDownLoad = findViewById(R.id.downloadBtn);
        btnBirthdate = findViewById(R.id.btn_upload_birthdate);
        btnPoliceCertificate = findViewById(R.id.btn_upload_police_crteif);
        btnFamilyTree = findViewById(R.id.btn_upload_family_tree);
        PassPortPic = findViewById(R.id.passport_view);
        IdPic = findViewById(R.id.id_view);
        BirthdatePic = findViewById(R.id.birthdate_view);
        PolicePic = findViewById(R.id.police_view);
        FamilyTreePic = findViewById(R.id.tree_view);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload_pass:
                imageName = "passport";
                takeImage();
                break;
            case R.id.btn_upload_ID:
                imageName = "id";
                takeImage();
                break;
            case R.id.btn_upload_birthdate:
                imageName = "birthdate";
                takeImage();
                break;
            case R.id.btn_upload_police_crteif:
                imageName = "police";
                takeImage();
                break;
            case R.id.btn_upload_family_tree:
                fileName = "family";
                uplodFile();
                break;
            case R.id.downloadBtn:
                if (PolicePic.getDrawable() == null || BirthdatePic.getDrawable() == null || IdPic.getDrawable() == null || PassPortPic.getDrawable() == null) {
                    Toast.makeText(getApplicationContext(), "אנא העלאה את כל המסמכים !", Toast.LENGTH_SHORT).show();
                    return;
                } else if (findViewById((R.id.tree_view)).getVisibility() == View.GONE) {
                    Toast.makeText(getApplicationContext(), "אנא העלאה קובץ עץ משפחה !", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "המסמכים נשלחו בהצלחה !", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(ProcessActivity.this, SatutsRequestActivity.class));
                }
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "permission granted!", Toast.LENGTH_LONG).show();
                takeImage();
            } else {
                Toast.makeText(getApplicationContext(), "permission declined!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void takeImage() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CAMERA);
        } else {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(i, CAPTURE_IMAGE);
        }
    }

    private void uplodFile() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        // We will be redirected to choose pdf
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent, 1);
    }


    private void uploadTofirestorage(Uri uri) {
        counter++;
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference = storageReference.child(mAuth.getUid()).child("userDocuments").child("photo_" + System.currentTimeMillis() + "." + getFileExtension(uri));
        if (uri != null) {
            storageReference
                    .putFile(uri)
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (counter == 1)
                                                myRef.child("userDocuments").child("Passport").setValue(uri.toString());
                                            else if (counter == 2) {
                                                myRef.child("userDocuments").child("Id").setValue(uri.toString());
                                            } else if (counter == 3) {
                                                myRef.child("userDocuments").child("Birthdate").setValue(uri.toString());
                                            } else if (counter == 4) {
                                                myRef.child("userDocuments").child("Police Certificate").setValue(uri.toString());
                                            } else {
                                                Toast.makeText(getApplicationContext(), "לא הצלחת לשמור את התמונה!", Toast.LENGTH_LONG).show();
                                            }
                                            progressDialog.dismiss();
                                        }
                                    });
                        }
                    });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAPTURE_IMAGE:
                if (resultCode == RESULT_OK) {
                    DocumentUser pi = new DocumentUser();
                    myRef.child(mAuth.getUid()).child("UserDoc").setValue(pi);
                    if (imageName.equals("passport")) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        PassPortPic.setImageBitmap(bitmap);
                        uriPassPort = getImageUri(ProcessActivity.this, bitmap);
                        uploadTofirestorage(uriPassPort);
                    } else if (imageName.equals("id")) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        uriId = getImageUri(ProcessActivity.this, bitmap);
                        uploadTofirestorage(uriId);
                        IdPic.setImageBitmap(bitmap);
                    } else if (imageName.equals("birthdate")) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        uriBirthdate = getImageUri(ProcessActivity.this, bitmap);
                        uploadTofirestorage(uriBirthdate);
                        BirthdatePic.setImageBitmap(bitmap);
                    } else if (imageName.equals("police")) {
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        uriPolice = getImageUri(ProcessActivity.this, bitmap);
                        uploadTofirestorage(uriPolice);
                        PolicePic.setImageBitmap(bitmap);
                    } else {
                        Toast.makeText(getApplicationContext(), "לא הצלחת לעלות תמונה!", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    ProgressDialog progressDialog = new ProgressDialog(this);
                    progressDialog.setMessage("Uploading...");
                    progressDialog.show();
                    Date date = new Date();
                    final String message = user + " " + "family_tree" + date.getDate();
                    Toast.makeText(ProcessActivity.this, UriTree.toString(), Toast.LENGTH_SHORT).show();
                    UriTree = data.getData();
                    storageReference = FirebaseStorage.getInstance().getReference();
                    storageReference = storageReference.child(mAuth.getUid()).child("userDocuments").child(message + "." + getFileExtension(UriTree));
                    if(UriTree !=null)
                        storageReference
                            .putFile(UriTree)
                            .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    storageReference.getDownloadUrl()
                                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    if (task.isSuccessful()) {
                                                        myRef.child("userDocuments").child("FamilyTree").setValue(uri.toString());
                                                        dialog.dismiss();
                                                        Toast.makeText(ProcessActivity.this, "העלאה הועלה בהצלחה", Toast.LENGTH_SHORT).show();
                                                        progressDialog.dismiss();
                                                    } else {
                                                        dialog.dismiss();
                                                        Toast.makeText(ProcessActivity.this, "העלאה נכשלה בדוק את הקובץ", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            });
                    else {
                        Toast.makeText(ProcessActivity.this, "uri is null", Toast.LENGTH_SHORT).show();
                    }
                    //final StorageReference filepath = storageReference.child(message + getFileExtension(UriTree));
//                    Toast.makeText(ProcessActivity.this, filepath.getName(), Toast.LENGTH_SHORT).show();
//                    filepath.putFile(UriTree).continueWithTask(new Continuation() {
//                        @Override
//                        public Object then(@NonNull Task task) throws Exception {
//                            if (!task.isSuccessful()) {
//                                throw task.getException();
//                            }
//                            return filepath.getDownloadUrl();
//                        }
//                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete (@NonNull Task < Uri > task) {
//                        if (task.isSuccessful()) {
//                            Uri uri = task.getResult();
//                            myRef.child("userDocuments").child("FamilyTree").setValue(uri.toString());
//                            dialog.dismiss();
//                            Toast.makeText(ProcessActivity.this, "העלאה הועלה בהצלחה", Toast.LENGTH_SHORT).show();
//                        } else {
//                            dialog.dismiss();
//                            Toast.makeText(ProcessActivity.this, "העלאה נכשלה בדוק את הקובץ", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
                }
                break;
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}