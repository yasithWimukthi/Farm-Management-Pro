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
import android.text.TextUtils;
import android.util.Log;
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

import com.farmmanagementpro.modals.Fertilizer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.farmmanagementpro.helper.HelperMethod;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddNewFertilizer extends Fragment {

    private static final int GALLERY_CODE = 1;

    private EditText DateEditText;
    private EditText FertilizerNameEditText2;
    private EditText quantityEditText;
    private EditText additionalInfoEditText2;
    private EditText supplierEditText;
    private EditText BatchNumberEditText;
    private Button fertilizerSaveBtn;
    private Button fertilizerResetBtn;
    private ImageView FertilizerImageButton;
    private Uri imageUri;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // FIRESTORE CONNECTION
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_new_fertilizer, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        DateEditText = view.findViewById(R.id.dateEditText);
        FertilizerNameEditText2 = view.findViewById(R.id.fertilizerNameEditText);
        quantityEditText = view.findViewById(R.id.quantityEditText);
        additionalInfoEditText2 = view.findViewById(R.id.updateAdditionalInfoEditText);
        supplierEditText = view.findViewById(R.id.supplierEditText);
        BatchNumberEditText = view.findViewById(R.id.batchNumberEditText);
        fertilizerSaveBtn = view.findViewById(R.id.fertilizerSaveBtn);
        fertilizerResetBtn = view.findViewById(R.id.fertilizerResetBtn);
        FertilizerImageButton = view.findViewById(R.id.fertilizerImageButton);


        storageReference = FirebaseStorage.getInstance().getReference();


        FertilizerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        fertilizerResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateEditText.setText("");
                FertilizerNameEditText2.setText("");
                quantityEditText.setText("");
                additionalInfoEditText2.setText("");
                supplierEditText.setText("");
                BatchNumberEditText.setText("");
                FertilizerImageButton.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
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





        fertilizerSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(DateEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(FertilizerNameEditText2.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter fertilizer name!",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(quantityEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter quantity!",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(additionalInfoEditText2.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter additional Information !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(supplierEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter supplier !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(BatchNumberEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter batch number !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else{
                    String date = DateEditText.getText().toString().trim();
                    String name = FertilizerNameEditText2.getText().toString().trim();
                    String quantity = quantityEditText.getText().toString().trim();
                    String additionalInfo = additionalInfoEditText2.getText().toString().trim();
                    String supplier = supplierEditText.getText().toString().trim();
                    String batchNo = BatchNumberEditText.getText().toString().trim();


                    addFertilizerImage(date,name,quantity,additionalInfo,supplier,batchNo);
                }
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
                FertilizerImageButton.setImageURI(imageUri);
                Log.d("Image uri", imageUri.toString());
            }
        }
    }

    public void addFertilizerImage(String date, String name, String quantity, String additionalInfo, String supplier, String batchNo){
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
            fertilizer.setImage("Not Added");
            addFertilizerDetails(fertilizer);
        }
    }

    public void addFertilizerDetails(Fertilizer fertilizer){
        db.collection("fertilizer").document(fertilizer.getName()).set(fertilizer)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DateEditText.setText("");
                        FertilizerNameEditText2.setText("");
                        quantityEditText.setText("");
                        additionalInfoEditText2.setText("");
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
}