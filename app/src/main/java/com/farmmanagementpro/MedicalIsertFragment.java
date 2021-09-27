package com.farmmanagementpro;


import android.os.Bundle;


import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;



public class MedicalIsertFragment extends Fragment  {
    ImageButton imageButton;
    TextView purchaseDate, name, qty, prescribedBy,PrescribedBy;
    EditText purchaseDateEt, nameEt, qtyEt, prescribedByEt,Supplier;
    Button resetBtn,saveBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.medical_insert_fragment, container, false);
        imageButton = v.findViewById(R.id.eventImageView);
        purchaseDateEt = v.findViewById(R.id.editTextDate);
        nameEt = v.findViewById(R.id.editTextName);
        qtyEt = v.findViewById(R.id.editTextQty);
        prescribedByEt =v.findViewById(R.id.PrescribedBy);
        Supplier = v.findViewById(R.id.editTextSupplier);
        resetBtn = v.findViewById(R.id.resetBtn);
        saveBtn = v.findViewById(R.id.saveBtn);

        return v;
    }

}