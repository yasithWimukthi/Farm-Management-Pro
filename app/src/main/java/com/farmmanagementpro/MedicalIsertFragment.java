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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


public class MedicalIsertFragment extends Fragment  {

    private static final int GALLERY_CODE = 1;

    private ImageView uploadImage;
//    TextView purchaseDate, name, qty, prescribedBy,PrescribedBy;
    private EditText purchaseDateEt, nameEt, qtyEt, prescribedByEt,SupplierValue;
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
        return inflater.inflate(R.layout.medical_insert_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View v, @Nullable  Bundle savedInstanceState) {
        uploadImage = v.findViewById(R.id.uploadImageView);
        purchaseDateEt = v.findViewById(R.id.editTextDate);
        nameEt = v.findViewById(R.id.editTextName);
        qtyEt = v.findViewById(R.id.editTextQty);
        prescribedByEt =v.findViewById(R.id.editTextPrescribeBy);
        SupplierValue = v.findViewById(R.id.editTextSupplier);
        resetBtn = v.findViewById(R.id.resetBtn);
        saveBtn = v.findViewById(R.id.saveBtn);

        storageReference = FirebaseStorage.getInstance().getReference();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseDateEt.setText("");
                nameEt.setText("");
                qtyEt.setText("");
                prescribedByEt.setText("");
                SupplierValue.setText("");
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
                    String prescribedBy = prescribedByEt.getText().toString().trim();
                    String Supplier = SupplierValue.getText().toString().trim();
                    
                    addMedicineImage(date,name,qty,prescribedBy,Supplier);
                }

            }
        });
        
    }

    private void addMedicineImage(String date, String name, String qty, String prescribedBy, String supplier) {

        Medicine medicine = new Medicine();
        medicine.setDate(date);
        medicine.setName(name);
        medicine.setQty(qty);
        medicine.setPrescribedBy(prescribedBy);
        medicine.setSupplier(supplier);

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
                                            medicine.setImage(uri.toString());
                                            addMedicineDetails(medicine);
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
            medicine.setImage("Not Added");
            addMedicineDetails(medicine);
        }
    }

    private void addMedicineDetails(Medicine medicine) {
        db.collection("medicine").document(medicine.getName()).set(medicine)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        nameEt.setText("");
                        purchaseDateEt.setText("");
                        qtyEt.setText("");
                        prescribedByEt.setText("");
                        SupplierValue.setText("");
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