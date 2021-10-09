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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.farmmanagementpro.helper.HelperMethod;
import com.farmmanagementpro.modals.Animal;
import com.google.firebase.storage.UploadTask;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddAnimalFragment extends Fragment {

    private static final int GALLERY_CODE = 1;

    private AutoCompleteTextView statusEditText;
    private EditText regDateEditText;
    private EditText animalIdEditText;
    private EditText dobEditText;
    private AutoCompleteTextView genderEditText;
    private EditText breedEditText;
    private EditText sireEditText;
    private Button saveBtn;
    private Button resetBtn;
    private ImageView animalImageButton;
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
        return inflater.inflate(R.layout.fragment_add_animal, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        statusEditText = view.findViewById(R.id.statusEditText);
        regDateEditText = view.findViewById(R.id.regDateEditText);
        animalIdEditText = view.findViewById(R.id.animalIdEditText);
        dobEditText = view.findViewById(R.id.dobEditText);
        genderEditText = view.findViewById(R.id.genderEditText);
        breedEditText = view.findViewById(R.id.breedEditText);
        sireEditText = view.findViewById(R.id.sireEditText);
        saveBtn = view.findViewById(R.id.saveBtn);
        resetBtn = view.findViewById(R.id.resetBtn);
        animalImageButton = view.findViewById(R.id.animalImageButton);

        statusEditText.setThreshold(2);
        genderEditText.setThreshold(2);

        storageReference = FirebaseStorage.getInstance().getReference();

        final String [] gender = new String[] {"Male","Female"};
        final String [] status = new String[] {"Born on Farm","Purchased","Sold","Death on Farm"};

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,gender);
        genderEditText.setAdapter(genderAdapter);
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,status);
        statusEditText.setAdapter(statusAdapter);

        genderEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                genderEditText.showDropDown();
                return true;
            }
        });


        statusEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                statusEditText.showDropDown();
                return  true;
            }
        });

        animalImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        regDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Calendar mCalendar = new GregorianCalendar();
                mCalendar.setTime(new Date());

                new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        regDateEditText.setText(dayOfMonth +"-"+month+"-"+year);
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });

        dobEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Calendar mCalendar = new GregorianCalendar();
                mCalendar.setTime(new Date());

                new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        dobEditText.setText(dayOfMonth +"-"+month+"-"+year);
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusEditText.setText("");
                regDateEditText.setText("");
                animalIdEditText.setText("");
                dobEditText.setText("");
                genderEditText.setText("");
                breedEditText.setText("");
                sireEditText.setText("");
                animalImageButton.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(statusEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter status !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(regDateEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter register date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(animalIdEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter animal ID !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(dobEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter DOB !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(genderEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter gender !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(breedEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter breed !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(sireEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter sire !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else{
                    String status = statusEditText.getText().toString().trim();
                    String regDate = regDateEditText.getText().toString().trim();
                    String animalId = animalIdEditText.getText().toString().trim();
                    String dob = dobEditText.getText().toString().trim();
                    String gender = genderEditText.getText().toString().trim();
                    String breed = breedEditText.getText().toString().trim();
                    String sire = sireEditText.getText().toString().trim();

                    addAnimalImage(status,regDate,animalId,dob,gender,breed,sire);
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
                animalImageButton.setImageURI(imageUri);
                Log.d("Image uri", imageUri.toString());
            }
        }
    }

    public void addAnimalImage(String status, String regDate, String animalId, String dob, String gender, String breed, String sire){
        Animal animal = new Animal();
        animal.setSire(sire);
        animal.setRegisteredDate(regDate);
        animal.setGender(gender);
        animal.setDob(dob);
        animal.setStatus(status);
        animal.setAnimalId(animalId);
        animal.setBreed(breed);

        if (imageUri != null){
            StorageReference filePath = storageReference.child("animals").child(animalId);
            filePath.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            animal.setImage(uri.toString());
                                            addAnimalDetails(animal);
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
            animal.setImage("Not Added");
            addAnimalDetails(animal);
        }
    }

    public void addAnimalDetails(Animal animal){
        db.collection("animals").document(animal.getAnimalId()).set(animal)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        statusEditText.setText("");
                        regDateEditText.setText("");
                        animalIdEditText.setText("");
                        dobEditText.setText("");
                        genderEditText.setText("");
                        breedEditText.setText("");
                        sireEditText.setText("");
                        animalImageButton.setImageDrawable(getResources().getDrawable(R.drawable.upload_image));

                        MyAnimalsFragment myAnimalsFragment = new MyAnimalsFragment();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, myAnimalsFragment)
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