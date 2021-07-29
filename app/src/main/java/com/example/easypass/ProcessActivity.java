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
import android.os.Handler;
import android.provider.MediaStore;
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
import java.util.Date;
import java.util.UUID;

public class ProcessActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int CAPTURE_IMAGE = 1024;
    private static final int PERMISSION_CAMERA = 888;
    private static final int PICK_FILE_RESULT_CODE = 1;
    TextView welcome_mess;
    Button btnPassport, btnID, btnBirthdate, btnPoliceCertificate, btnFamilyTree, btnDownLoad;
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    StorageReference storageReference;
    ImageView PassPortPic, IdPic, BirthdatePic, PolicePic, FamilyTreePic;
    String fileName = "";
    String firstName = null, lastname = null;
    Uri uriPassPort = null, uriId = null, uriTree = null, uriBirthdate = null, uriPolice = null;
    static int counter = 0;
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
                        setTitle("אפשרויות העלאה").
                        setMessage("בחר/י אפשרות העלאה").
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
                        setTitle("אפשרויות העלאה").
                        setMessage("בחר/י אפשרות העלאה").
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
                        setTitle("אפשרויות העלאה").
                        setMessage("בחר/י אפשרות העלאה").
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
                        setTitle("אפשרויות העלאה").
                        setMessage("בחר/י אפשרות העלאה").
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
                        setTitle("אפשרויות העלאה").
                        setMessage("בחר/י אפשרות העלאה").
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
                if (PolicePic.getDrawable() == null || BirthdatePic.getDrawable() == null || IdPic.getDrawable() == null || PassPortPic.getDrawable() == null || FamilyTreePic.getDrawable() == null) {
                    new AlertDialog.Builder(ProcessActivity.this).
                            setTitle("שגיאה").
                            setMessage("אנא העלא/י את כל המסמכים ורק אז תוכל/י להמשיך לשלב הבא !").
                            setPositiveButton("בסדר", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    return;
                                }
                            }).
                            setIcon(android.R.drawable.ic_dialog_alert).show();
                } else {
                    String case_number = UUID.randomUUID().toString();
                    case_number = case_number.substring(0, 10);
                    myRef.child("Case number").setValue(case_number);
                    myRef.child("Status Request").setValue("1");
                    myRef.child("userDocuments").child("Passport").setValue(uriPassport);
                    myRef.child("userDocuments").child("Id").setValue(uriID);
                    myRef.child("userDocuments").child("Birthdate").setValue(uriBirth);
                    myRef.child("userDocuments").child("Police Certificate").setValue(uriPol);
                    myRef.child("userDocuments").child("FamilyTree").setValue(uriFamilyTree);
                    if (counter >= 5) {
                        Toast.makeText(getApplicationContext(), "המסמכים הועלאו למערכת בהצלחה !", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                myRef.child("count_send").setValue("0");
                                startActivity(new Intent(ProcessActivity.this, InstructionsActivity.class));
                            }
                        }, 1500);
                    }
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + v.getId());
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

    private void uploadFile() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/*");
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
            case CAPTURE_IMAGE:
                if (resultCode == RESULT_OK) {
                    switch (fileName) {
                        case "passport":
                            Bitmap bitmapPass = (Bitmap) data.getExtras().get("data");
                            uriPassPort = getImageUri(ProcessActivity.this, bitmapPass);
                            UploadToFireStorage(uriPassPort);
                            PassPortPic.setImageBitmap(bitmapPass);
                            counter++;
                            break;
                        case "id":
                            Bitmap bitmapId = (Bitmap) data.getExtras().get("data");
                            uriId = getImageUri(ProcessActivity.this, bitmapId);
                            UploadToFireStorage(uriId);
                            IdPic.setImageBitmap(bitmapId);
                            counter++;
                            break;
                        case "birthdate":
                            Bitmap bitmapBirth = (Bitmap) data.getExtras().get("data");
                            uriBirthdate = getImageUri(ProcessActivity.this, bitmapBirth);
                            UploadToFireStorage(uriBirthdate);
                            BirthdatePic.setImageBitmap(bitmapBirth);
                            counter++;
                            break;
                        case "police":
                            Bitmap bitmapPol = (Bitmap) data.getExtras().get("data");
                            uriPolice = getImageUri(ProcessActivity.this, bitmapPol);
                            UploadToFireStorage(uriPolice);
                            PolicePic.setImageBitmap(bitmapPol);
                            counter++;
                            break;
                        case "family":
                            Bitmap bitmapFamily = (Bitmap) data.getExtras().get("data");
                            uriTree = getImageUri(ProcessActivity.this, bitmapFamily);
                            UploadToFireStorage(uriTree);
                            FamilyTreePic.setImageBitmap(bitmapFamily);
                            counter++;
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "לא הצלחת לעלות תמונה!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                break;
            case PICK_FILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    switch (fileName) {
                        case "passport":
                            counter++;
                            uriPassPort = data.getData();
                            PassPortPic.setImageURI(uriPassPort);
                            UploadToFireStorage(uriPassPort);
                            break;
                        case "id":
                            counter++;
                            uriId = data.getData();
                            IdPic.setImageURI(uriId);
                            UploadToFireStorage(uriId);
                            break;
                        case "birthdate":
                            counter++;
                            uriBirthdate = data.getData();
                            BirthdatePic.setImageURI(uriBirthdate);
                            UploadToFireStorage(uriBirthdate);
                            break;
                        case "police":
                            counter++;
                            uriPolice = data.getData();
                            PolicePic.setImageURI(uriPassPort);
                            UploadToFireStorage(uriPolice);
                            break;
                        case "family":
                            counter++;
                            uriTree = data.getData();
                            FamilyTreePic.setImageURI(uriPassPort);
                            UploadToFireStorage(uriTree);
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "לא הצלחת לעלות קובץ!", Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
                break;
            default:
                Toast.makeText(getApplicationContext(), "שגיאת אימות קוד קובץ!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.WEBP, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "הרשאה התקבלה בהצלחה!", Toast.LENGTH_LONG).show();
                takeImage();
            } else {
                Toast.makeText(getApplicationContext(), "לא התקבלה הרשאה!", Toast.LENGTH_LONG).show();
            }
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

}