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
import com.farmmanagementpro.modals.Feed;
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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class UpdateFeed extends Fragment {

    private static final int GALLERY_CODE = 1;

    private ImageView uploadImage;
    private EditText purchaseDateEt, nameEt, qtyEt, descriptionEt,SupplierValue;
    private Button resetBtn,updateBtn;
    private Uri imageUri;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference feedCollection = db.collection("feed");
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
        return inflater.inflate(R.layout.fragment_update_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull  View v, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        uploadImage = v.findViewById(R.id.uploadImageBtnFeedUpdate);
        purchaseDateEt = v.findViewById(R.id.editTextDateFeedUpdate);
        nameEt = v.findViewById(R.id.editTextNameFeedUpdate);
        qtyEt = v.findViewById(R.id.editTextQtyFeedUpdate);
        descriptionEt =v.findViewById(R.id.editTextDescritpionFeedUpdate);
        SupplierValue = v.findViewById(R.id.editTextSupplierFeedUpdate);
        resetBtn = v.findViewById(R.id.resetBtnFeedUpdate);
        updateBtn = v.findViewById(R.id.saveBtnFeedUpdate);

        storageReference = FirebaseStorage.getInstance().getReference();

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        purchaseDateEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Calendar mCalendar = new GregorianCalendar();
                mCalendar.setTime(new Date());

                new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        purchaseDateEt.setText(dayOfMonth +"-"+(month+1)+"-"+year);
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseDateEt.setText("");
                nameEt.setText("");
                qtyEt.setText("");
                descriptionEt.setText("");
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
                String description = descriptionEt.getText().toString().trim();
                String supplier = SupplierValue.getText().toString().trim();



                deleteFeed(date,name,qty,description,supplier);
            }
        });

        getMedicine(name);
    }

    private void getMedicine(String name) {
        feedCollection.whereEqualTo("name",name)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot feeds : queryDocumentSnapshots){
                            Feed feed = feeds.toObject(Feed.class);
                            purchaseDateEt.setText(feed.getDate());
                            nameEt.setText(feed.getName());
                            qtyEt.setText(feed.getQty());
                            descriptionEt.setText(feed.getDesctiption());
                            SupplierValue.setText(feed.getSupplier());

                            imageUrl = feed.getImage();

                            Picasso.get()
                                    .load(feed.getImage())
                                    .placeholder(R.drawable.upload_image)
                                    .fit()
                                    .into(uploadImage);

                        }
                    }
                });
    }

    private void deleteFeed(String date, String name, String qty, String description, String supplier) {
        feedCollection.document(name)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateFeedImage(date,name,qty,description,supplier);
                    }
                });
    }

    private void updateFeedImage(String date, String name, String qty, String description, String supplier) {

        Feed feed =new Feed();
        feed.setDate(date);
        feed.setName(name);
        feed.setQty(qty);
        feed.setDesctiption(description);
        feed.setSupplier(supplier);

        if (imageUri != null){
            StorageReference filePath = storageReference.child("feed").child(name);
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
            feed.setImage(imageUrl);
            addFeedDetails(feed);
        }
    }

    private void addFeedDetails(Feed feed) {
        db.collection("feed").document(feed.getName()).set(feed)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        purchaseDateEt.setText("");
                        nameEt.setText("");
                        qtyEt.setText("");
                        descriptionEt.setText("");
                        SupplierValue.setText("");
                        uploadImage.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));


                        FeedHistoryFragment feedHistoryFragment  = new FeedHistoryFragment();

                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, feedHistoryFragment)
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
