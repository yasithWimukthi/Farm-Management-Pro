package com.farmmanagementpro;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.farmmanagementpro.helper.HelperMethod;
import com.farmmanagementpro.modals.Spray;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

public class SpraysInsert extends Fragment {
    private static final int GALLERY_CODE = 1;

    private EditText addSprayName;
    private EditText addSprayPurchasedDate;
    private EditText addSprayQty;
    private EditText addSprayNumber;
    private EditText addSpraySupplier;
    private EditText addSprayInformation;
    private Button saveSBtn;
    private Button resetSBtn;
    private ImageView sprayImageBtn;
    private Uri imageUri;

//    private FirebaseAuth firebaseAuth;
//    private FirebaseAuth.AuthStateListener authStateListener;
//    private FirebaseUser currentUser;

    // FIRESTORE CONNECTION
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sprays_insert, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addSprayName = view.findViewById(R.id.addSprayName);
        addSprayPurchasedDate = view.findViewById(R.id.addSprayPurchasedDate);
        addSprayQty = view.findViewById(R.id.addSprayQty);
        addSprayNumber = view.findViewById(R.id.addSprayNumber);
        addSpraySupplier = view.findViewById(R.id.addSpraySupplier);
        addSprayInformation = view.findViewById(R.id.addSprayInformation);
        saveSBtn = view.findViewById(R.id.saveSBtn);
        resetSBtn = view.findViewById(R.id.resetSBtn);
        sprayImageBtn = view.findViewById(R.id.sprayImageBtn);

        storageReference = FirebaseStorage.getInstance().getReference();

        sprayImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        saveSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addSprayName.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter spray name !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addSprayPurchasedDate.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter purchased date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addSprayQty.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter a quantity !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addSprayNumber.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter a number !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addSpraySupplier.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter a supplier !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addSprayInformation.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter the information !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else{
                    String name = addSprayName.getText().toString().trim();
                    String date = addSprayPurchasedDate.getText().toString().trim();
                    String qty = addSprayQty.getText().toString().trim();
                    String number = addSprayNumber.getText().toString().trim();
                    String supplier = addSpraySupplier.getText().toString().trim();
                    String information = addSprayInformation.getText().toString().trim();

                    addSprayImage(name,date,qty,number,supplier,information);
                }
            }
        });

        resetSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSprayName.setText("");
                addSprayPurchasedDate.setText("");
                addSprayQty.setText("");
                addSprayNumber.setText("");
                addSpraySupplier.setText("");
                addSprayInformation.setText("");
                sprayImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
            }
        });

    }

    private void showDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        Button chooseGalleryBtn = dialog.findViewById(R.id.choose_gallery);
        Button chooseCameraBtn = dialog.findViewById(R.id.choose_camera);

        chooseGalleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == -1){
            if(data != null){
                imageUri = data.getData();
                sprayImageBtn.setImageURI(imageUri);
                Log.d("Image uri", imageUri.toString());
            }
        }
    }

    public void addSprayImage(String name, String date, String qty, String number, String supplier, String information){
        Spray spray = new Spray();
        spray.setName(name);
        spray.setDate(date);
        spray.setQty(qty);
        spray.setNumber(number);
        spray.setSupplier(supplier);
        spray.setInformation(information);

        if (imageUri != null){
            StorageReference filePath = storageReference.child("sprays").child(number);
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            spray.setImage(uri.toString());
                                            addSprayDetails(spray);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            HelperMethod.createErrorToast(getActivity(),"Error happened while uploading image.");
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            HelperMethod.createErrorToast(getActivity(),"Error happened while uploading image.");
                        }
                    });
        }else{
            spray.setImage("Not Added");
            addSprayDetails(spray);
        }
    }

    public void addSprayDetails(Spray spray){
        db.collection("sprays").document(spray.getNumber()).set(spray)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"Added Successfully",Toast.LENGTH_LONG).show();
                        addSprayName.setText("");
                        addSprayPurchasedDate.setText("");
                        addSprayQty.setText("");
                        addSprayNumber.setText("");
                        addSpraySupplier.setText("");
                        addSprayInformation.setText("");
                        sprayImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        TastyToast.makeText(
                                getActivity(),
                                "Error happened. Try again.",
                                TastyToast.LENGTH_LONG,
                                TastyToast.ERROR
                        );
                    }
                });
    }
}