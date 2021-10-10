package com.farmmanagementpro;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.farmmanagementpro.modals.AnimalEvent;
import com.farmmanagementpro.modals.Machine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class UpdateMachine extends Fragment {
    private AutoCompleteTextView addMachineName2;
    private EditText addMachineServiceDate;
    private EditText addMachineServiceType;
    private EditText addMachineServiceCost;
    private EditText addMachineNotes;
    private Button updateMBtn;
    private Button resetMBtn;

    // FIRESTORE CONNECTION
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference machinesCollection = db.collection("machines");

    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("date", ""); // Key, default value
        }
        return inflater.inflate(R.layout.fragment_update_machine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addMachineName2 = view.findViewById(R.id.addMachineName2);
        addMachineServiceDate = view.findViewById(R.id.addMachineServiceDate);
        addMachineServiceType = view.findViewById(R.id.addMachineServiceType);
        addMachineServiceCost = view.findViewById(R.id.addMachineServiceCost);
        addMachineNotes = view.findViewById(R.id.addMachineNotes);
        updateMBtn = view.findViewById(R.id.updateMBtn);
        resetMBtn = view.findViewById(R.id.resetMBtn);

        addMachineName2.setThreshold(2);

        final String [] name = new String[] {"Browns Tafe","John Deer"};

        ArrayAdapter<String> machinesAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,name);
        addMachineName2.setAdapter(machinesAdapter);

        addMachineName2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                addMachineName2.showDropDown();
                return true;
            }
        });

        addMachineServiceDate.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Calendar mCalendar = new GregorianCalendar();
                mCalendar.setTime(new Date());

                new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        addMachineServiceDate.setText(dayOfMonth +"-"+month+"-"+year);
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });

        resetMBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMachineName2.setText("");
                addMachineServiceDate.setText("");
                addMachineServiceType.setText("");
                addMachineServiceCost.setText("");
                addMachineNotes.setText("");
            }
        });

        updateMBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(addMachineName2.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter machine name !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (TextUtils.isEmpty(addMachineServiceDate.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (TextUtils.isEmpty(addMachineServiceType.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter a service type !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else{
                    String name = addMachineName2.getText().toString().trim();
                    //String date = addMachineServiceDate.getText().toString().trim();
                    String type = addMachineServiceType.getText().toString().trim();
                    String cost = addMachineServiceCost.getText().toString().trim();
                    String notes = addMachineNotes.getText().toString().trim();

                    deleteMachine(name,date,type,cost,notes);
                }
            }
        });

        getMachine(date);
    }


    private void updateMachine(String name, String date, String type, String cost, String notes) {
        Machine machine = new Machine();
        machine.setName(name);
        machine.setDate(date);
        machine.setType(type);
        machine.setCost(cost);
        machine.setNotes(notes);

        db.collection("machines").document(date).set(machine)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        addMachineName2.setText("");
                        addMachineServiceDate.setText("");
                        addMachineServiceType.setText("");
                        addMachineServiceCost.setText("");
                        addMachineNotes.setText("");

                        MyMachineryFragment myMachineryFragment = new MyMachineryFragment();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, myMachineryFragment)
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

    private void getMachine(String date) {
        machinesCollection.whereEqualTo("date",date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot machines : queryDocumentSnapshots){
                            Machine machine = machines.toObject(Machine.class);
                            addMachineName2.setText(machine.getName());
                            addMachineServiceDate.setText(machine.getDate());
                            addMachineServiceType.setText(machine.getType());
                            addMachineServiceCost.setText(machine.getCost());
                            addMachineNotes.setText(machine.getNotes());
                        }
                    }
                });
    }

    private void deleteMachine(String name, String date, String type, String cost, String notes){
        machinesCollection.document(date)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateMachine(name,date,type,cost,notes);
                    }
                });
    }
}