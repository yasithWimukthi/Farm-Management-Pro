package com.farmmanagementpro;

import android.app.DatePickerDialog;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.farmmanagementpro.helper.HelperMethod;
import com.farmmanagementpro.modals.Fertilizer;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UpdateFertilizerFragment extends Fragment {

    private static final int GALLERY_CODE = 1;

    private EditText DateEditText;
    private EditText FertilizerNameEditText;
    private EditText quantityEditText;
    private EditText additionalInfoEditText;
    private EditText supplierEditText;
    private EditText BatchNumberEditText;
    private Button fertilizerUpdateBtn;
    private Button fertilizerResetBtn;
    private ImageView FertilizerImageButton;
    private Uri imageUri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference fertilizersCollection = db.collection("fertilizer");
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
        return inflater.inflate(R.layout.fragment_update_fertilizer, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DateEditText = view.findViewById(R.id.dateEditText);
        FertilizerNameEditText = view.findViewById(R.id.fertilizerNameEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        additionalInfoEditText = view.findViewById(R.id.updateAdditionalInfoEditText);
        supplierEditText = view.findViewById(R.id.supplierEditText);
        BatchNumberEditText = view.findViewById(R.id.batchNumberEditText);
        fertilizerUpdateBtn = view.findViewById(R.id.fertilizerSaveBtn);
        fertilizerResetBtn = view.findViewById(R.id.fertilizerResetBtn);
        FertilizerImageButton = view.findViewById(R.id.fertilizerImageButton);


        storageReference = FirebaseStorage.getInstance().getReference();


        FertilizerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        DateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Calendar mCalendar = new GregorianCalendar();
                mCalendar.setTime(new Date());

                new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        DateEditText.setText(dayOfMonth +"-"+month+"-"+year);
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });



        fertilizerResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateEditText.setText("");
                FertilizerNameEditText.setText("");
                quantityEditText.setText("");
                additionalInfoEditText.setText("");
                supplierEditText.setText("");
                BatchNumberEditText.setText("");
                FertilizerImageButton.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
            }
        });

        fertilizerUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = DateEditText.getText().toString().trim();
                String name = FertilizerNameEditText.getText().toString().trim();
                String quantity = quantityEditText.getText().toString().trim();
                String additionalInfo = additionalInfoEditText.getText().toString().trim();
                String supplier = supplierEditText.getText().toString().trim();
                String batchNo = BatchNumberEditText.getText().toString().trim();


                deleteFertilizer(date,name,quantity,additionalInfo,supplier,batchNo);
            }
        });

        getFertilizer(name);
    }

    private void updateFertilizerImage(String date, String name, String quantity, String additionalInfo, String supplier, String batchNo) {
        Fertilizer fertilizer = new Fertilizer();
        fertilizer.setDate(date);
        fertilizer.setName(name);
        fertilizer.setQuantity(quantity);
        fertilizer.setAdditionalInfo(additionalInfo);
        fertilizer.setSupplier(supplier);
        fertilizer.setBatchNo(batchNo);


        if (imageUri != null){
            StorageReference filePath = storageReference.child("fertilizer").child(name);
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            fertilizer.setImage(uri.toString());
                                            addFertilizerDetails(fertilizer);
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
            fertilizer.setImage(imageUrl);
            addFertilizerDetails(fertilizer);
        }
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

    public void addFertilizerDetails(Fertilizer fertilizer){
        db.collection("fertilizer").document(fertilizer.getName()).set(fertilizer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DateEditText.setText("");
                        FertilizerNameEditText.setText("");
                        quantityEditText.setText("");
                        additionalInfoEditText.setText("");
                        supplierEditText.setText("");
                        BatchNumberEditText.setText("");
                        FertilizerImageButton.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));

                        FertilizerHistoryFragment fertilizerHistoryFragment = new FertilizerHistoryFragment();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, fertilizerHistoryFragment)
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

    public void getFertilizer(String name){
        fertilizersCollection.whereEqualTo("name",name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot fertilizers : queryDocumentSnapshots){
                            Fertilizer fertilizer = fertilizers.toObject(Fertilizer.class);
                            DateEditText.setText(fertilizer.getDate());
                            FertilizerNameEditText.setText(fertilizer.getName());
                            quantityEditText.setText(fertilizer.getQuantity());
                            additionalInfoEditText.setText(fertilizer.getAdditionalInfo());
                            supplierEditText.setText(fertilizer.getSupplier());
                            BatchNumberEditText.setText(fertilizer.getBatchNo());
                            imageUrl = fertilizer.getImage();

                            Picasso.get()
                                    .load(fertilizer.getImage())
                                    .placeholder(R.drawable.upload_image)
                                    .fit()
                                    .into(FertilizerImageButton);

                        }
                    }
                });
    }

    public void deleteFertilizer(String date, String name, String quantity, String additionalInfo, String supplier, String batchNo){
        fertilizersCollection.document(name)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateFertilizerImage(date,name,quantity,additionalInfo,supplier,batchNo);
                    }
                });
    }
}