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

import com.farmmanagementpro.modals.FarmTask;
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

public class UpdateFarmTaskFragment extends Fragment {

    private EditText taskDateEditText;
    private EditText taskNameEditText;
    private AutoCompleteTextView taskStatusEditText;
    private EditText descriptionEditText;
    private Button updateBtn;
    private Button resetBtn;


    // FIRESTORE CONNECTION
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksCollection = db.collection("task");

    private String date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            date = bundle.getString("taskDate", ""); // Key, default value
        }
        return inflater.inflate(R.layout.fragment_update_farm_task, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskDateEditText = view.findViewById(R.id.updateTaskDateEditText);
        taskNameEditText = view.findViewById(R.id.updateTaskNameEditText);
        taskStatusEditText = view.findViewById(R.id.updateTaskStatusEditText);
        descriptionEditText = view.findViewById(R.id.updateDescriptionEditText);
        updateBtn = view.findViewById(R.id.updateTaskSaveBtn);
        resetBtn = view.findViewById(R.id.updatetaskResetBtn);

        taskStatusEditText.setThreshold(2);

        final String [] status = new String[] {"On going", "Completed", "Not Completed"};

        ArrayAdapter<String> taskNameAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,status);
        taskStatusEditText.setAdapter(taskNameAdapter);

        taskStatusEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                taskStatusEditText.showDropDown();
                return true;
            }
        });

        taskDateEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent task) {
                Calendar mCalendar = new GregorianCalendar();
                mCalendar.setTime(new Date());

                new DatePickerDialog(getActivity(), R.style.my_dialog_theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        taskDateEditText.setText(dayOfMonth +"-"+month+"-"+year);
                    }

                }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
                return true;
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskDateEditText.setText("");
                taskNameEditText.setText("");
                taskStatusEditText.setText("");
                descriptionEditText.setText("");

            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(taskDateEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter task date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (TextUtils.isEmpty(taskNameEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter task name !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else if (TextUtils.isEmpty(taskStatusEditText.getText().toString().trim())){
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter event status !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                }else{
                    String date = taskDateEditText.getText().toString().trim();
                    String name = taskNameEditText.getText().toString().trim();
                    String status = taskStatusEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();


                    deleteTask(date,name,status,description);
                }
            }
        });

        getTask(date);
    }


    private void updateTask(String date, String name, String status, String description) {
        FarmTask farmTask = new FarmTask();
        farmTask.setTaskDate(date);
        farmTask.setTaskName(name);
        farmTask.setTaskStatus(status);
        farmTask.setDescription(description);



        db.collection("task").document(date).set(farmTask)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        taskDateEditText.setText("");
                        taskNameEditText.setText("");
                        taskStatusEditText.setText("");
                        descriptionEditText.setText("");

                        FarmTasksFragment farmTasksFragment = new FarmTasksFragment();
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, farmTasksFragment)
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

    private void getTask(String date) {
        tasksCollection.whereEqualTo("taskDate",date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot tasks : queryDocumentSnapshots){
                            FarmTask farmTask = tasks.toObject(FarmTask.class);
                            taskDateEditText.setText(farmTask.getTaskDate());
                            taskNameEditText.setText(farmTask.getTaskName());
                            taskStatusEditText.setText(farmTask.getTaskStatus());
                            descriptionEditText.setText(farmTask.getDescription());

                        }
                    }
                });
    }

    private void deleteTask(String date, String name, String status, String description){
        tasksCollection.document(date)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateTask(date,name,status,description);
                    }
                });
    }
}