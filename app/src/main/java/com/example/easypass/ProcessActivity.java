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
import java.util.Arrays;
import java.util.Date;
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
    String fileName = "";
    String firstName = null, lastname = null, birthclient = null, idclient = null, passclient = null, policcerclient = null, fmilyclient = null;
    Uri uriPassPort, uriId, uriTree, uriBirthdate, uriPolice;
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
        btnDownLoad = findViewById(R.id.SendBtn);
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
                fileName = "passport";
                new AlertDialog.Builder(ProcessActivity.this).
                        setTitle("אופציות העלאה").
                        setMessage("בחר אופציית העלאה").
                        setPositiveButton("צלם מסמך", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takeImage();
                            }
                        })
                        .setNegativeButton("מסמך מתוך האחסון", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadFile();
                            }
                        }).
                        setIcon(android.R.drawable.ic_dialog_info).show();
                break;
            case R.id.btn_upload_ID:
                fileName = "id";
                new AlertDialog.Builder(ProcessActivity.this).
                        setTitle("אופציות העלאה").
                        setMessage("בחר אופציית העלאה").
                        setPositiveButton("צלם מסמך", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takeImage();
                            }
                        })
                        .setNegativeButton("מסמך מתוך האחסון", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadFile();
                            }
                        }).
                        setIcon(android.R.drawable.ic_dialog_info).show();
                break;
            case R.id.btn_upload_birthdate:
                fileName = "birthdate";
                new AlertDialog.Builder(ProcessActivity.this).
                        setTitle("אופציות העלאה").
                        setMessage("בחר אופציית העלאה").
                        setPositiveButton("צלם מסמך", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takeImage();
                            }
                        })
                        .setNegativeButton("מסמך מתוך האחסון", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadFile();
                            }
                        }).
                        setIcon(android.R.drawable.ic_dialog_info).show();
                break;
            case R.id.btn_upload_police_crteif:
                fileName = "police";
                new AlertDialog.Builder(ProcessActivity.this).
                        setTitle("אופציות העלאה").
                        setMessage("בחר אופציית העלאה").
                        setPositiveButton("צלם מסמך", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takeImage();
                            }
                        })
                        .setNegativeButton("מסמך מתוך האחסון", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadFile();
                            }
                        }).
                        setIcon(android.R.drawable.ic_dialog_info).show();
                break;
            case R.id.btn_upload_family_tree:
                fileName = "family";
                new AlertDialog.Builder(ProcessActivity.this).
                        setTitle("אופציות העלאה").
                        setMessage("בחר אופציית העלאה").
                        setPositiveButton("צלם מסמך", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                takeImage();
                            }
                        })
                        .setNegativeButton("מסמך מתוך האחסון", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                uploadFile();
                            }
                        }).
                        setIcon(android.R.drawable.ic_dialog_info).show();
                break;
            case R.id.SendBtn:
                if (PolicePic.getDrawable() == null || BirthdatePic.getDrawable() == null || IdPic.getDrawable() == null || PassPortPic.getDrawable() == null) {
                    new AlertDialog.Builder(ProcessActivity.this).
                            setTitle("שגיאה").
                            setMessage("לא עלו כל המסמכים !").
                            setPositiveButton("בסדר", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            }).
                            setIcon(android.R.drawable.ic_dialog_alert).show();
                } else if (findViewById((R.id.tree_view)).getVisibility() == View.GONE) {
                    new AlertDialog.Builder(ProcessActivity.this).
                            setTitle("שגיאה").
                            setMessage("לא הועלאה עץ משפחה !").
                            setPositiveButton("בסדר", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            }).
                            setIcon(android.R.drawable.ic_dialog_alert).show();
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "המסמכים נשלחו בהצלחה !", Toast.LENGTH_SHORT).show();
                    String case_number = UUID.randomUUID().toString();
                    case_number = case_number.substring(0, 10);
                    myRef.child("Case number").setValue(case_number);
                    myRef.child("Status Request").setValue("1");
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
            startActivity(new Intent(ProcessActivity.this, InstructionsActivity.class));
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
        if (counter >= 4)
            galleryIntent.setType("application/pdf");
        else {
            galleryIntent.setType("application/");
        }
        startActivityForResult(galleryIntent, 1);
    }

    private void UploadToFireStorage(Uri uri) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        Date date = new Date();
        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference = storageReference.child(mAuth.getUid()).child("userDocuments").child("file_" + date.getTime() + "." + getFileExtension(uri));
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
                                    } else if (counter == 5) {
                                        uriFamilyTree = uri.toString();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_LONG).show();
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
                    if (fileName.equals("passport")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        uriPassPort = getImageUri(ProcessActivity.this, bitmap);
                        PassPortPic.setImageBitmap(bitmap);
                        counter++;
                        UploadToFireStorage(uriPassPort);
                    } else if (fileName.equals("id")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        IdPic.setImageBitmap(bitmap);
                        uriId = getImageUri(ProcessActivity.this, bitmap);
                        counter++;
                        UploadToFireStorage(uriId);
                    } else if (fileName.equals("birthdate")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        BirthdatePic.setImageBitmap(bitmap);
                        uriBirthdate = getImageUri(ProcessActivity.this, bitmap);
                        counter++;
                        UploadToFireStorage(uriBirthdate);
                    } else if (fileName.equals("police")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        PolicePic.setImageBitmap(bitmap);
                        uriPolice = getImageUri(ProcessActivity.this, bitmap);
                        counter++;
                        UploadToFireStorage(uriPolice);
                    } else if (fileName.equals("family")) {
                        bitmap = (Bitmap) data.getExtras().get("data");
                        FamilyTreePic.setImageBitmap(bitmap);
                        uriTree = getImageUri(ProcessActivity.this, bitmap);
                        counter++;
                        UploadToFireStorage(uriTree);
                    } else {
                        Toast.makeText(getApplicationContext(), "לא הצלחת לעלות תמונה!", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    if (fileName.equals("passport")) {
                        counter++;
                        uriPassPort = data.getData();
                        PassPortPic.setImageURI(uriPassPort);
                        UploadToFireStorage(uriPassPort);
                    } else if (fileName.equals("id")) {
                        counter++;
                        uriId = data.getData();
                        IdPic.setImageURI(uriId);
                        UploadToFireStorage(uriId);
                    } else if (fileName.equals("birthdate")) {
                        counter++;
                        uriBirthdate = data.getData();
                        BirthdatePic.setImageURI(uriBirthdate);
                        UploadToFireStorage(uriBirthdate);
                    } else if (fileName.equals("police")) {
                        counter++;
                        uriPolice = data.getData();
                        UploadToFireStorage(uriPolice);
                    } else if (fileName.equals("family")) {
                        counter++;
                        uriTree = data.getData();
                        UploadToFireStorage(uriTree);
                    } else {
                        Toast.makeText(getApplicationContext(), "לא הצלחת לעלות קובץ!", Toast.LENGTH_SHORT).show();
                    }
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