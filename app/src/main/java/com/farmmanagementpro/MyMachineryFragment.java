package com.farmmanagementpro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farmmanagementpro.UI.MachineListRecyclerAdapter;
import com.farmmanagementpro.modals.Machine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.ArrayList;
import java.util.List;

public class MyMachineryFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference machinesCollection = db.collection("machines");

    private RecyclerView recyclerview;
    private MachineListRecyclerAdapter machinesAdapter;

    private List<Machine> machinesList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.my_machinery_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        machinesList = new ArrayList<>();
        recyclerview = view.findViewById(R.id.myMachineryRecyclerView);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        getMachinesList();
    }

    private void getMachinesList() {
        machinesCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot machines : queryDocumentSnapshots){
                                Machine machine = machines.toObject(Machine.class);
                                machinesList.add(machine);
                            }
                            machinesAdapter = new MachineListRecyclerAdapter(getActivity(),machinesList);
                            recyclerview.setAdapter(machinesAdapter);
                            machinesAdapter.notifyDataSetChanged();
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
