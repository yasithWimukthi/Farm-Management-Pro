package com.farmmanagementpro;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farmmanagementpro.UI.AnimalEventListRecyclerAdapter;
import com.farmmanagementpro.UI.FarmTaskListRecyclerAdapter;
import com.farmmanagementpro.modals.AnimalEvent;
import com.farmmanagementpro.modals.FarmTask;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FarmTasksFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tasksCollection = db.collection("task");

    private RecyclerView recyclerview;
    private FarmTaskListRecyclerAdapter tasksAdapter;

    private List<FarmTask> taskList;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private FarmTask deletedTask;
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.farm_tasks_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        taskList = new ArrayList<>();
        recyclerview = view.findViewById(R.id.taskRecyclerView);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        getTaskList();

        simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String taskDate = taskList.get(position).getTaskDate();
                deletedTask = taskList.get(position);

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        FancyAlertDialog.Builder
                                .with(getActivity())
                                .setTitle("Do you want to delete this task ?")
                                .setBackgroundColor(Color.parseColor("#F57C00"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                                .setMessage("Do you really want to Exit ?")
                                .setNegativeBtnText("Delete")
                                .setPositiveBtnBackground(Color.parseColor("#F57C00"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                                .setPositiveBtnText("Cancel")
                                .setNegativeBtnBackground(Color.parseColor("#A8A7A8"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                                .setAnimation(Animation.POP)
                                .isCancellable(true)
                                .setIcon(R.drawable.ic_baseline_pan_tool_24, View.VISIBLE)
                                .onPositiveClicked(dialog -> {
                                    FarmTasksFragment farmTasksFragment = new FarmTasksFragment();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, farmTasksFragment)
                                            .commit();
                                })
                                .onNegativeClicked(dialog -> {
                                    taskList.remove(position);
                                    tasksAdapter.notifyItemRemoved(position);
                                    removeTask(taskDate);
                                })
                                .build()
                                .show();

                        break;
                    case ItemTouchHelper.RIGHT:
                        UpdateFarmTaskFragment updateFarmTaskFragment = new UpdateFarmTaskFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("taskDate", taskDate);
                        updateFarmTaskFragment.setArguments(bundle);
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, updateFarmTaskFragment)
                                .commit();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(),R.color.colorAccent))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(),R.color.green))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    private void removeTask(String taskDate) {
        tasksCollection.document(taskDate)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(recyclerview,"Do you want to undo",Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        tasksCollection.document(taskDate).set(deletedTask);
                                        taskList.add(position,deletedTask);
                                        tasksAdapter.notifyItemInserted(position);
                                    }
                                }).show();
                    }
                });
    }

    private void getTaskList() {
        tasksCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot tasks : queryDocumentSnapshots){
                                FarmTask farmTask = tasks.toObject(FarmTask.class);
                                taskList.add(farmTask);
                            }
                            tasksAdapter = new FarmTaskListRecyclerAdapter(getActivity(),taskList);
                            recyclerview.setAdapter(tasksAdapter);
                            tasksAdapter.notifyDataSetChanged();

                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
                            itemTouchHelper.attachToRecyclerView(recyclerview);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        TastyToast.makeText(
                                getActivity(),
                                e.getLocalizedMessage(),
                                TastyToast.LENGTH_LONG,
                                TastyToast.INFO
                        );
                    }
                });
    }



}
