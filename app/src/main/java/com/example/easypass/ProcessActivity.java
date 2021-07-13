package com.example.easypass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProcessActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProcessActivity";
    private static final int CAPTUE_IMAGE = 1024;
    private static final int PERMISSION_CAMERA = 888;
    private static final int PICKFILE_RESULT_CODE = 1;
    TextView welcome_mess;
    Button btnPassport, btnID, btnBirthdate, btnPoliceCertificate, btnFamilyTree, btnDownLoad;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference storageReference;
    Bitmap bitmap;
    ImageView PassPortPic, IdPic, BirthdatePic, PolicePic, FamilyTreePic;
    String imageName = "";
    String fileName = "";
    ProgressDialog dialog;
    String firstName = null, lastname = null, birthclient = null, idclient = null, passclient = null, policcerclient = null, fmilyclient = null;
    Uri uriPassPort, uriId, UriTree, uriBirthdate, uriPolice;
    static int counter = 0, countUploadFirebase = 0;
    String uriPassport = "", uriID = "", uriBirth = "", uriPol = "", uriFamilyTree = "";

    @Override
    public void onBackPressed() {
        logoutUser();
    }

    private void logoutUser() {
        new AlertDialog.Builder(ProcessActivity.this).
                setTitle("התנתקות").
                setMessage("אתה בטוח שאתה רוצה להתנתק ?").
                setPositiveButton("כן", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        startActivity(new Intent(ProcessActivity.this, MainActivity.class));
                    }
                })
                .setNegativeButton("לא", null).
                setIcon(android.R.drawable.ic_dialog_info).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process);
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("userDocuments");
        database = FirebaseDatabase.getInstance("https://easypass-dcff0-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference("Users").child(mAuth.getUid());
        initViews();
        initButtons();
        readFromDB();
        counter = 0;

    }

    private void readFromDB() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                firstName = snapshot.child("UserInfo").child("first_name").getValue(String.class);
                lastname = snapshot.child("UserInfo").child("last_name").getValue(String.class);
                welcome_mess.setText("ברוך הבא " + firstName + " " + lastname);
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
        welcome_mess = findViewById(R.id.welcome_message);
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
            case R.id.btn_ContactUs:
                startActivity(new Intent(ProcessActivity.this, ContactUs.class));
                break;
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
                uploadFile();
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
                    String case_number = UUID.randomUUID().toString();
                    case_number = case_number.substring(0, 10);
                    myRef.child("Case number").setValue(case_number);
                    myRef.child("Status Request").setValue("1");
                    startActivity(new Intent(ProcessActivity.this, InstructionsActivity.class));
                    myRef.child("userDocuments").child("Passport").setValue(uriPassport, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                            countUploadFirebase++;
                        }
                    });
                    myRef.child("userDocuments").child("Id").setValue(uriID, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                            countUploadFirebase++;

                        }
                    });
                    myRef.child("userDocuments").child("Birthdate").setValue(uriBirth, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                            countUploadFirebase++;
                        }
                    });
                    myRef.child("userDocuments").child("Police Certificate").setValue(uriPol, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                            countUploadFirebase++;
                        }
                    });
                    myRef.child("userDocuments").child("FamilyTree").setValue(uriFamilyTree, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable @org.jetbrains.annotations.Nullable DatabaseError error, @NonNull @NotNull DatabaseReference ref) {
                            countUploadFirebase++;
                        }
                    });
                    if (countUploadFirebase == 5) {
                        sendEmail();
                        startActivity(new Intent(ProcessActivity.this, InstructionsActivity.class));
                    }

                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
        }
    }

    public void sendEmail() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                birthclient = snapshot.child("userDocuments").child("Birthdate").getValue(String.class);
                idclient = snapshot.child("userDocuments").child("Id").getValue(String.class);
                passclient = snapshot.child("userDocuments").child("Passport").getValue(String.class);
                policcerclient = snapshot.child("userDocuments").child("Police Certificate").getValue(String.class);
                fmilyclient = snapshot.child("userDocuments").child("FamilyTree").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d(TAG, "send email...");

        String[] TO = {"epass876@gmail.com"};
        String[] CC = {"ron96t@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");

        String[] Documents = {birthclient, idclient, passclient, policcerclient, fmilyclient};
        String subject = "תיק מסמכים של " + firstName + " " + lastname;

        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "המסמכים: ");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Arrays.toString(Documents));


        try {
            startActivity(Intent.createChooser(emailIntent, "שולח מייל..."));
            finish();
            Log.i("מסיים לשלוח את המייל...", "");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(ProcessActivity.this,
                    "אימייל לא תקין...", Toast.LENGTH_SHORT).show();
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
            startActivityForResult(i, CAPTUE_IMAGE);
        }
    }

    private void uploadFile() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent, 1);
    }

    private void uploadToFirestorage(Uri uri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference = storageReference.child(mAuth.getUid()).child("userDocuments").child("photo_" + System.currentTimeMillis() + "." + getFileExtension(uri));
        if (uri != null) {
            storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl()
                            .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    if (counter == 1) {
                                        uriPassport = uri.toString();
                                    } else if (counter == 2) {
                                        uriID = uri.toString();
                                    } else if (counter == 3) {
                                        uriBirth = uri.toString();
                                    } else if (counter == 4) {
                                        uriPol = uri.toString();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "לא הצלחת לשמור את התמונה!", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Uri> task) {
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
            case CAPTUE_IMAGE:
                if (resultCode == RESULT_OK) {
                    if (imageName.equals("passport")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        uriPassPort = getImageUri(ProcessActivity.this, bitmap);
                        PassPortPic.setImageBitmap(bitmap);
                        counter++;
                        uploadToFirestorage(uriPassPort);
                    } else if (imageName.equals("id")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        IdPic.setImageBitmap(bitmap);
                        uriId = getImageUri(ProcessActivity.this, bitmap);
                        counter++;
                        uploadToFirestorage(uriId);
                    } else if (imageName.equals("birthdate")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        BirthdatePic.setImageBitmap(bitmap);
                        uriBirthdate = getImageUri(ProcessActivity.this, bitmap);
                        counter++;
                        uploadToFirestorage(uriBirthdate);
                    } else if (imageName.equals("police")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        PolicePic.setImageBitmap(bitmap);
                        uriPolice = getImageUri(ProcessActivity.this, bitmap);
                        counter++;
                        uploadToFirestorage(uriPolice);
                    } else {
                        Toast.makeText(getApplicationContext(), "לא הצלחת לעלות תמונה!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    Date date = new Date();
                    UriTree = data.getData();
                    final String message = firstName + "family_tree" + date.getDate();
                    storageReference = FirebaseStorage.getInstance().getReference();
                    final StorageReference filepath = storageReference.child(mAuth.getUid()).child("userDocuments").child(message + "." + getFileExtension(UriTree));
                    UploadToFireBaseFromMediaStorage(UriTree, filepath);
                    FamilyTreePic.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void UploadToFireBaseFromMediaStorage(Uri uri, StorageReference filepath) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        if (UriTree != null)
            filepath.putFile(UriTree).continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filepath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        UriTree = task.getResult();
                        uriFamilyTree = UriTree.toString();
                    } else {
                        dialog.dismiss();
                    }
                }
            });
        else {
            Toast.makeText(ProcessActivity.this, "uri is null", Toast.LENGTH_SHORT).show();
        }
    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    //Recognize file extension
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}