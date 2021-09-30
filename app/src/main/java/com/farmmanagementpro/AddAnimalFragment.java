package com.farmmanagementpro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class AddAnimalFragment extends Fragment {

    private EditText statusEditText;
    private EditText regDateEditText;
    private EditText animalIdEditText;
    private EditText dobEditText;
    private EditText genderEditText;
    private EditText breedEditText;
    private EditText sireEditText;
    private Button saveBtn;
    private Button resetBtn;
    private ImageButton animalImageButton;

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



    }
}