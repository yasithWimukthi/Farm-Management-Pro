package com.farmmanagementpro;

import android.app.DatePickerDialog;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.farmmanagementpro.modals.FarmTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddNewFarmTask extends Fragment {

    private EditText taskDateEditText;
    private EditText TaskNameEditText;
    private AutoCompleteTextView taskStatusEditText;
    private EditText descriptionEditText;
    private Button saveBtn;
    private Button resetBtn;

    // FIRESTORE CONNECTION

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_new_farm_task, container, false);
    }


    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        taskDateEditText = view.findViewById(R.id.updateTaskDateEditText);
        TaskNameEditText = view.findViewById(R.id.updateTaskNameEditText);
        taskStatusEditText = view.findViewById(R.id.updateTaskStatusEditText);
        descriptionEditText = view.findViewById(R.id.updateDescriptionEditText);
        saveBtn = view.findViewById(R.id.updateTaskSaveBtn);
        resetBtn = view.findViewById(R.id.updatetaskResetBtn);

        taskStatusEditText.setThreshold(2);

        final String[] status = new String[]{"On going", "Completed", "Not Completed"};

        ArrayAdapter<String> taskNameAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, status);
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
            public boolean onTouch(View v, MotionEvent event) {
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




        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(taskDateEditText.getText().toString().trim())) {
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter event date !",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                } else if (TextUtils.isEmpty(TaskNameEditText.getText().toString().trim())) {
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter task name!",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                } else if (TextUtils.isEmpty(taskStatusEditText.getText().toString().trim())) {
                    TastyToast.makeText(
                            getActivity(),
                            "Please enter task status!",
                            TastyToast.LENGTH_LONG,
                            TastyToast.ERROR
                    );
                } else {
                    String date = taskDateEditText.getText().toString().trim();
                    String status =taskStatusEditText.getText().toString().trim();
                    String taskName = TaskNameEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();

                    saveTask(taskName, date, status, description);
                }
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskDateEditText.setText("");
                TaskNameEditText.setText("");
                taskStatusEditText.setText("");
                descriptionEditText.setText("");
            }
        });
    }


    public void saveTask(String taskName, String date, String description, String status) {
        FarmTask task = new FarmTask();
        task.setTaskStatus(status);
        task.setTaskDate(date);
        task.setTaskName(taskName);
        task.setDescription(description);

        db.collection("task").document(date).set(task)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}







