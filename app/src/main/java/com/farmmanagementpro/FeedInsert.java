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

import com.farmmanagementpro.modals.Feed;
import com.farmmanagementpro.modals.Medicine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

public class FeedInsert extends Fragment {
    private static final int GALLERY_CODE = 1;

    private ImageView uploadImage;
    private EditText purchaseDateEt, nameEt, qtyEt,SupplierEt, descriptonEt;
    private Button resetBtn,saveBtn;
    private Uri imageUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //fireStore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed_insert, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View v, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        uploadImage = v.findViewById(R.id.uploadImageBtnFeed);
        purchaseDateEt = v.findViewById(R.id.editTextDateFeed);
        nameEt = v.findViewById(R.id.editTextNameFeed);
        qtyEt = v.findViewById(R.id.editTextQtyFeed);
        descriptonEt =v.findViewById(R.id.editTextDescritpionFeed);
        SupplierEt = v.findViewById(R.id.editTextSupplierFeed);
        resetBtn = v.findViewById(R.id.resetBtnFeed);
        saveBtn = v.findViewById(R.id.saveBtnFeed);

        storageReference = FirebaseStorage.getInstance().getReference();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

//        reset form
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseDateEt.setText("");
                nameEt.setText("");
                qtyEt.setText("");
                descriptonEt.setText("");
                SupplierEt.setText("");
                uploadImage.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(purchaseDateEt.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter purchase date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (TextUtils.isEmpty(nameEt.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter Name !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (TextUtils.isEmpty(qtyEt.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter event name !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else{
                    String date = purchaseDateEt.getText().toString().trim();
                    String name = nameEt.getText().toString().trim();
                    String qty = qtyEt.getText().toString().trim();
                    String description = descriptonEt.getText().toString().trim();
                    String Supplier = SupplierEt.getText().toString().trim();

                    addFeedImage(date,name,qty,Supplier,description);
                }

            }
        });




    }

    private void addFeedImage(String date, String name, String qty, String supplier, String description) {
        Feed feed = new Feed();
        feed.setDate(date);
        feed.setName(name);
        feed.setQty(qty);
        feed.setDesctiption(description);
        feed.setSupplier(supplier);

        if(imageUri != null){
            StorageReference filePath = storageReference.child("medicine").child(name);
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            feed.setImage(uri.toString());
                                            addFeedDetails(feed);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            int duration = Toast.LENGTH_SHORT;
                                            Toast toast = Toast.makeText(getContext(),"Error happened while uploading image.", duration);
                                            toast.show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(getContext(),"Error happened while uploading image.", duration);
                            toast.show();
                        }
                    });
        }else{
            feed.setImage("Not Added");
            addFeedDetails(feed);
        }
    }

    private void addFeedDetails(Feed feed) {
        db.collection("feed").document(feed.getName()).set(feed)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nameEt.setText("");
                        purchaseDateEt.setText("");
                        qtyEt.setText("");
                        descriptonEt.setText("");
                        SupplierEt.setText("");
                        uploadImage.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
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
                uploadImage.setImageURI(imageUri);
                Log.d("Image uri", imageUri.toString());
            }
        }
    }
}