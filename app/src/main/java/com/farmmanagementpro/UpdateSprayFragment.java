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
import com.farmmanagementpro.modals.Spray;
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

public class UpdateSprayFragment extends Fragment {
    private static final int GALLERY_CODE = 1;

    private EditText addSprayName;
    private EditText addSprayPurchasedDate;
    private EditText addSprayQty;
    private EditText addSprayNumber;
    private EditText addSpraySupplier;
    private EditText addSprayInformation;
    private Button updateSBtn;
    private Button resetSBtn;
    private ImageView sprayImageBtn;
    private Uri imageUri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference spraysCollection = db.collection("sprays");
    private StorageReference storageReference;

    private String name;
    private String imageUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name", ""); // Key, default value
        }
        return inflater.inflate(R.layout.fragment_update_spray, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        addSprayName = view.findViewById(R.id.addSprayName);
        addSprayPurchasedDate = view.findViewById(R.id.addSprayPurchasedDate);
        addSprayQty = view.findViewById(R.id.addSprayQty);
        addSprayNumber = view.findViewById(R.id.addSprayNumber);
        addSpraySupplier = view.findViewById(R.id.addSpraySupplier);
        addSprayInformation = view.findViewById(R.id.addSprayInformation);
        updateSBtn = view.findViewById(R.id.updateSBtn);
        resetSBtn = view.findViewById(R.id.resetBtn);
        sprayImageBtn = view.findViewById(R.id.uploadImageBtn);

        storageReference = FirebaseStorage.getInstance().getReference();

        sprayImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        addSprayPurchasedDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Calendar mCalendar = new GregorianCalendar();
                mCalendar.setTime(new Date());

                new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        addSprayPurchasedDate.setText(dayOfMonth +"-"+month+"-"+year);
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
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

        updateSBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = addSprayName.getText().toString().trim();
                String date = addSprayPurchasedDate.getText().toString().trim();
                String qty = addSprayQty.getText().toString().trim();
                String number = addSprayNumber.getText().toString().trim();
                String supplier = addSpraySupplier.getText().toString().trim();
                String information = addSprayInformation.getText().toString().trim();

                deleteSpray(name,date,qty,number,supplier,information);
            }
        });

        getSpray(name);
    }

    private void updateAnimalImage(String name, String date, String qty, String number, String supplier, String information) {
        Spray spray = new Spray();
        spray.setName(name);
        spray.setDate(date);
        spray.setQty(qty);
        spray.setNumber(number);
        spray.setSupplier(supplier);
        spray.setInformation(information);

        if (imageUri != null){
            StorageReference filePath = storageReference.child("sprays").child(name);
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
            spray.setImage(imageUrl);
            addSprayDetails(spray);
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

    public void addSprayDetails(Spray spray){
        db.collection("sprays").document(spray.getName()).set(spray)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addSprayName.setText("");
                        addSprayPurchasedDate.setText("");
                        addSprayQty.setText("");
                        addSprayNumber.setText("");
                        addSpraySupplier.setText("");
                        addSprayInformation.setText("");
                        sprayImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));

                        SpraysFragment spraysFragment = new SpraysFragment();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, spraysFragment)
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

    public void getSpray(String name){
        spraysCollection.whereEqualTo("name",name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot animals : queryDocumentSnapshots){
                            Spray spray = animals.toObject(Spray.class);
                            addSprayName.setText(spray.getName());
                            addSprayPurchasedDate.setText(spray.getDate());
                            addSprayQty.setText(spray.getQty());
                            addSprayNumber.setText(spray.getNumber());
                            addSpraySupplier.setText(spray.getSupplier());
                            addSprayInformation.setText(spray.getInformation());
                            imageUrl = spray.getImage();

                            Picasso.get()
                                    .load(spray.getImage())
                                    .placeholder(R.drawable.upload_image)
                                    .fit()
                                    .into(sprayImageBtn);

                        }
                    }
                });
    }

    public void deleteSpray(String name, String date, String qty, String number, String supplier, String information){
        spraysCollection.document(name)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateAnimalImage(name,date,qty,number,supplier,information);
                    }
                });
    }
}