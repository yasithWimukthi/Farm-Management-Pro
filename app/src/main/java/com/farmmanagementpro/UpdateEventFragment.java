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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

public class UpdateEventFragment extends Fragment {

    private EditText eventDateEditText;
    private EditText animalIdEditText;
    private AutoCompleteTextView eventNameEditText;
    private EditText stockBullEditText;
    private EditText notesEditText;
    private Button updateBtn;
    private Button resetBtn;

    // FIRESTORE CONNECTION
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsCollection = db.collection("events");

    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("eventDate", ""); // Key, default value
        }
        return inflater.inflate(R.layout.fragment_update_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventDateEditText = view.findViewById(R.id.eventDateEditText);
        animalIdEditText = view.findViewById(R.id.AnimalIdEditText);
        eventNameEditText = view.findViewById(R.id.eventNameEditText);
        stockBullEditText = view.findViewById(R.id.stockBullEditText);
        notesEditText = view.findViewById(R.id.notesEditText);
        updateBtn = view.findViewById(R.id.updateBtn);
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

        updateBtn.setOnClickListener(new View.OnClickListener() {
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

                    deleteEvent(animalId,eventName,date,stockBull,note);
                }
            }
        });

        getEvent(date);
    }


    private void updateEvent(String animalId, String eventName, String date, String stockBull, String note) {
        AnimalEvent event = new AnimalEvent();
        event.setAnimalId(animalId);
        event.setEventDate(date);
        event.setEventName(eventName);
        event.setStockBull(stockBull);
        event.setNotes(note);

        db.collection("events").document(date).set(event)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        eventDateEditText.setText("");
                        animalIdEditText.setText("");
                        eventNameEditText.setText("");
                        stockBullEditText.setText("");
                        notesEditText.setText("");

                        AnimalEventFragment animalEventFragment = new AnimalEventFragment();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, animalEventFragment)
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

    private void getEvent(String date) {
        eventsCollection.whereEqualTo("eventDate",date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot events : queryDocumentSnapshots){
                            AnimalEvent event = events.toObject(AnimalEvent.class);
                            eventDateEditText.setText(event.getEventDate());
                            animalIdEditText.setText(event.getAnimalId());
                            eventNameEditText.setText(event.getEventName());
                            stockBullEditText.setText(event.getStockBull());
                            notesEditText.setText(event.getNotes());
                        }
                    }
                });
    }

    private void deleteEvent(String animalId, String eventName, String date, String stockBull, String note){
        eventsCollection.document(date)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateEvent(animalId,eventName,date,stockBull,note);
                    }
                });
    }
}