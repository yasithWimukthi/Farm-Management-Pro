package com.farmmanagementpro;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farmmanagementpro.UI.MachineListRecyclerAdapter;
import com.farmmanagementpro.modals.Machine;
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

public class MyMachineryFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference machinesCollection = db.collection("machines");

    private RecyclerView recyclerview;
    private MachineListRecyclerAdapter machinesAdapter;

    private List<Machine> machinesList;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private Machine deletedMachine;
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_machinery_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        machinesList = new ArrayList<>();
        recyclerview = view.findViewById(R.id.myMachineryRecyclerView);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        getMachinesList();

        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }


            @SuppressLint({"SupportAnnotationUsage", "ResourceAsColor"})
            @ColorRes
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String date = machinesList.get(position).getDate();
                deletedMachine = machinesList.get(position);

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        FancyAlertDialog.Builder
                                .with(getActivity())
                                .setTitle("Warning !")
                                .setBackgroundColor(Color.parseColor("#ff0000"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                                .setMessage("Do you want to delete this machine ?")
                                .setNegativeBtnText("Delete")
                                .setPositiveBtnBackground(Color.parseColor("#F57C00"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                                .setPositiveBtnText("Cancel")
                                .setNegativeBtnBackground(R.color.negativeBtn)  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                                .setAnimation(Animation.POP)
                                .isCancellable(true)
                                .setIcon(R.drawable.ic_baseline_pan_tool_24, View.VISIBLE)
                                .onPositiveClicked(dialog -> {
                                    MyMachineryFragment myMachineryFragment = new MyMachineryFragment();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, myMachineryFragment)
                                            .commit();
                                })
                                .onNegativeClicked(dialog -> {
                                    machinesList.remove(position);
                                    machinesAdapter.notifyItemRemoved(position);
                                    removeMachine(date);
                                })
                                .build()
                                .show();

                        break;
                    case ItemTouchHelper.RIGHT:
                        UpdateMachine updateMachine = new UpdateMachine();
                        Bundle bundle = new Bundle();
                        bundle.putString("date", date);
                        updateMachine.setArguments(bundle);
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, updateMachine)
                                .commit();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(), R.color.positiveBtn))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(), R.color.green))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    private void removeMachine(String date) {
        machinesCollection.document(date)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(recyclerview, "Do you want to undo", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        machinesCollection.document(date).set(deletedMachine);
                                        machinesList.add(position, deletedMachine);
                                        machinesAdapter.notifyItemInserted(position);
                                    }
                                }).show();
                    }
                });
    }

    private void getMachinesList() {
        machinesCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot machines : queryDocumentSnapshots) {
                                Machine machine = machines.toObject(Machine.class);
                                machinesList.add(machine);
                            }
                            machinesAdapter = new MachineListRecyclerAdapter(getActivity(), machinesList);
                            recyclerview.setAdapter(machinesAdapter);
                            machinesAdapter.notifyDataSetChanged();

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

