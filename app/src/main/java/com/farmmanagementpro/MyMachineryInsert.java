package com.farmmanagementpro;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.farmmanagementpro.modals.Machine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.tastytoast.TastyToast;

public class MyMachineryInsert extends Fragment {

    private AutoCompleteTextView addMachineName;
    private EditText addMachineServiceDate;
    private EditText addMachineServiceType;
    private EditText addMachineServiceCost;
    private EditText addMachineNotes;
    private Button saveBtn;
    private Button resetBtn;

    // FIRESTORE CONNECTION
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_machinery_insert, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        addMachineName = view.findViewById(R.id.addMachineName);
        addMachineServiceDate = view.findViewById(R.id.addMachineServiceDate);
        addMachineServiceType = view.findViewById(R.id.addMachineServiceType);
        addMachineServiceCost = view.findViewById(R.id.addMachineServiceCost);
        addMachineNotes = view.findViewById(R.id.addMachineNotes);
        saveBtn = view.findViewById(R.id.saveBtn);
        resetBtn = view.findViewById(R.id.resetSBtn);

        addMachineName.setThreshold(2);

        storageReference = FirebaseStorage.getInstance().getReference();

        final String [] name = new String[] {"Browns Tafe" , "John Deer"};

        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,name);
        addMachineName.setAdapter(nameAdapter);

        addMachineName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                addMachineName.showDropDown();
                return true;
            }
        });



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addMachineName.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter name !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addMachineServiceDate.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter service date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addMachineServiceType.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter a service type !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addMachineServiceCost.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter service cost !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if(TextUtils.isEmpty(addMachineNotes.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter a service note !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );

                }else{
                    String name = addMachineName.getText().toString().trim();
                    String date = addMachineServiceDate.getText().toString().trim();
                    String type = addMachineServiceType.getText().toString().trim();
                    String cost = addMachineServiceCost.getText().toString().trim();
                    String notes = addMachineNotes.getText().toString().trim();

                    saveMachine(name,date,type,cost,notes);
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMachineName.setText("");
                addMachineServiceDate.setText("");
                addMachineServiceType.setText("");
                addMachineServiceCost.setText("");
                addMachineNotes.setText("");
            }
        });

    }

    public void saveMachine(String name, String date, String type, String cost, String notes){
        Machine machine = new Machine();
        machine.setName(name);
        machine.setDate(date);
        machine.setType(type);
        machine.setCost(cost);
        machine.setNotes(notes);

        db.collection("machines").document().set(machine)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getContext(),"Added Successfully",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}