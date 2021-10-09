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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.farmmanagementpro.helper.HelperMethod;
import com.farmmanagementpro.modals.Animal;
import com.farmmanagementpro.modals.Medicine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.squareup.picasso.Picasso;

public class UpdateMedicalFragment extends Fragment {
    private static final int GALLERY_CODE = 1;

    private ImageView uploadImage;
    private EditText purchaseDateEt, nameEt, qtyEt, prescribedByEt,SupplierValue;
    private Button resetBtn,updateBtn;
    private Uri imageUri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference medicineCollection = db.collection("medicine");
    private StorageReference storageReference;

    private String name;
    private String imageUrl;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name", ""); // Key, default value
        }
        return inflater.inflate(R.layout.fragment_update_medical, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View v, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        uploadImage = v.findViewById(R.id.updateMedicalImage);
        purchaseDateEt = v.findViewById(R.id.editTextDateUpdateMedical);
        nameEt = v.findViewById(R.id.editTextNameUpdateMedical);
        qtyEt = v.findViewById(R.id.editTextQtyUpdateMedical);
        prescribedByEt =v.findViewById(R.id.editTextPrescribeByUpdateMedical);
        SupplierValue = v.findViewById(R.id.editTextSupplierUpdateMedical);
        resetBtn = v.findViewById(R.id.resetBtnUpdateMedical);
        updateBtn = v.findViewById(R.id.saveBtnUpdateMedical);

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

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = purchaseDateEt.getText().toString().trim();
                String name = nameEt.getText().toString().trim();
                String qty = qtyEt.getText().toString().trim();
                String prescribedBy = prescribedByEt.getText().toString().trim();
                String supplier = SupplierValue.getText().toString().trim();



                deleteMedicine(date,name,qty,prescribedBy,supplier);
            }
        });

        getMedicine(name);
    }

    private void getMedicine(String name) {
        medicineCollection.whereEqualTo("name",name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot medicines : queryDocumentSnapshots){
                            Medicine medicine = medicines.toObject(Medicine.class);
                            purchaseDateEt.setText(medicine.getDate());
                            nameEt.setText(medicine.getName());
                            qtyEt.setText(medicine.getQty());
                            prescribedByEt.setText(medicine.getPrescribedBy());
                            SupplierValue.setText(medicine.getSupplier());

                            imageUrl = medicine.getImage();

                            Picasso.get()
                                    .load(medicine.getImage())
                                    .placeholder(R.drawable.upload_image)
                                    .fit()
                                    .into(uploadImage);

                        }
                    }
                });
    }

    private void deleteMedicine(String date, String name, String qty, String prescribedBy, String supplier) {
        medicineCollection.document(name)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateMedicineImage(date,name,qty,prescribedBy,supplier);
                    }
                });
    }

    private void updateMedicineImage(String date, String name, String qty, String prescribedBy, String supplier) {
        Medicine medicine = new Medicine();
        medicine.setDate(date);
        medicine.setName(name);
        medicine.setQty(qty);
        medicine.setPrescribedBy(prescribedBy);
        medicine.setSupplier(supplier);

        if (imageUri != null){
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
            medicine.setImage(imageUrl);
            addMedicineDetails(medicine);
        }

    }

    private void addMedicineDetails(Medicine medicine) {
        db.collection("medicine").document(medicine.getName()).set(medicine)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        purchaseDateEt.setText("");
                        nameEt.setText("");
                        qtyEt.setText("");
                        prescribedByEt.setText("");
                        SupplierValue.setText("");
                        uploadImage.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));


                        MedicalCabinetFragment medicalCabinetFragment = new MedicalCabinetFragment();

                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, medicalCabinetFragment)
                                .commit();
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
}