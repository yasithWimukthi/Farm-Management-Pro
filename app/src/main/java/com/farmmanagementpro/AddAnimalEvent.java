package com.farmmanagementpro;

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
import android.widget.EditText;
import android.widget.Toast;

import com.farmmanagementpro.modals.AnimalEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.tastytoast.TastyToast;

public class AddAnimalEvent extends Fragment {

    private EditText eventDateEditText;
    private EditText animalIdEditText;
    private AutoCompleteTextView eventNameEditText;
    private EditText stockBullEditText;
    private EditText notesEditText;
    private Button saveBtn;
    private Button resetBtn;

    // FIRESTORE CONNECTION
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_animal_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        eventDateEditText = view.findViewById(R.id.eventDateEditText);
        animalIdEditText = view.findViewById(R.id.AnimalIdEditText);
        eventNameEditText = view.findViewById(R.id.eventNameEditText);
        stockBullEditText = view.findViewById(R.id.stockBullEditText);
        notesEditText = view.findViewById(R.id.notesEditText);
        saveBtn = view.findViewById(R.id.saveBtn);
        resetBtn = view.findViewById(R.id.resetBtn);

        eventNameEditText.setThreshold(2);

        final String [] events = new String[] {"Heat Detection","Serves","Weight Check"};

        ArrayAdapter<String> eventNameAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,events);
        eventNameEditText.setAdapter(eventNameAdapter);

        eventNameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                eventNameEditText.showDropDown();
                return true;
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(eventDateEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter event date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (TextUtils.isEmpty(animalIdEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter animal ID !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (TextUtils.isEmpty(eventNameEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter event name !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else{
                    String date = eventDateEditText.getText().toString().trim();
                    String animalId = animalIdEditText.getText().toString().trim();
                    String eventName = eventNameEditText.getText().toString().trim();
                    String stockBull = stockBullEditText.getText().toString().trim();
                    String note = notesEditText.getText().toString().trim();

                    saveEvent(animalId,eventName,date,stockBull,note);
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventDateEditText.setText("");
                animalIdEditText.setText("");
                eventNameEditText.setText("");
                stockBullEditText.setText("");
                notesEditText.setText("");
            }
        });
    }

    public void saveEvent(String animalId, String eventName, String date, String stock,String note){
        AnimalEvent event = new AnimalEvent();
        event.setAnimalId(animalId);
        event.setEventDate(date);
        event.setEventName(eventName);
        event.setStockBull(stock);
        event.setNotes(note);

        db.collection("events").document().set(event)
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