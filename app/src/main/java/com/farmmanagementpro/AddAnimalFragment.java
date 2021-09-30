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

import com.farmmanagementpro.helper.HelperMethod;
import com.sdsmdg.tastytoast.TastyToast;

public class AddAnimalFragment extends Fragment {

    private static final int GALLERY_CODE = 1;

    private EditText statusEditText;
    private EditText regDateEditText;
    private EditText animalIdEditText;
    private EditText dobEditText;
    private EditText genderEditText;
    private EditText breedEditText;
    private EditText sireEditText;
    private Button saveBtn;
    private Button resetBtn;
    private ImageView animalImageButton;
    private Uri imageUri;

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

        animalImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
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
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(statusEditText.getText().toString().trim())){
                    HelperMethod.createErrorToast(getActivity(),"Please enter status !");
                }else if(TextUtils.isEmpty(regDateEditText.getText().toString().trim())){
                    HelperMethod.createErrorToast(getActivity(),"Please enter register date !");
                }else if(TextUtils.isEmpty(animalIdEditText.getText().toString().trim())){
                    HelperMethod.createErrorToast(getActivity(),"Please enter animal ID !");
                }else if(TextUtils.isEmpty(dobEditText.getText().toString().trim())){
                    HelperMethod.createErrorToast(getActivity(),"Please enter DOB !");
                }else if(TextUtils.isEmpty(genderEditText.getText().toString().trim())){
                    HelperMethod.createErrorToast(getActivity(),"Please enter gender !");
                }else if(TextUtils.isEmpty(breedEditText.getText().toString().trim())){
                    HelperMethod.createErrorToast(getActivity(),"Please enter breed !");
                }else if(TextUtils.isEmpty(sireEditText.getText().toString().trim())){
                    HelperMethod.createErrorToast(getActivity(),"Please enter sire !");
                }else{
                    String status = statusEditText.getText().toString().trim();
                    String regDate = regDateEditText.getText().toString().trim();
                    String animalId = animalIdEditText.getText().toString().trim();
                    String dob = dobEditText.getText().toString().trim();
                    String gender = genderEditText.getText().toString().trim();
                    String breed = breedEditText.getText().toString().trim();
                    String sire = sireEditText.getText().toString().trim();
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

    public void addAnimal(String status, String regDate, String animalId, String dob, String gender, String breed, String sire){

    }
}