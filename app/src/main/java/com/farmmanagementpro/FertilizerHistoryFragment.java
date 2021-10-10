package com.farmmanagementpro;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farmmanagementpro.UI.AnimalListRecyclerAdapter;
import com.farmmanagementpro.UI.FertilizerListRecyclerAdapter;
import com.farmmanagementpro.helper.CommonConstants;
import com.farmmanagementpro.modals.Animal;
import com.farmmanagementpro.modals.Fertilizer;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.sdsmdg.tastytoast.TastyToast;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class FertilizerHistoryFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference fertilizersCollection = db.collection("fertilizer");

    private RecyclerView recyclerview;
    private FertilizerListRecyclerAdapter fertilizerListRecyclerAdapter;

    private List<Fertilizer> fertilizerList;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private Fertilizer deletedFertilizer;
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fertilizer_history_fragment,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fertilizerList = new ArrayList<>();
        recyclerview = view.findViewById(R.id.fertilizerListRecyclerView);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        getAnimalList();

        simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }


            @SuppressLint({"SupportAnnotationUsage", "ResourceAsColor"})
            @ColorRes
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String name = fertilizerList.get(position).getName();
                deletedFertilizer = fertilizerList.get(position);

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        FancyAlertDialog.Builder
                                .with(getActivity())
                            .setTitle("Warning !")
                            .setBackgroundColor(Color.parseColor("#ff0000"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                            .setMessage("Do you want to delete this fertilizer ?")
                            .setNegativeBtnText("Delete")
                            .setPositiveBtnBackground(Color.parseColor("#F57C00"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                            .setPositiveBtnText("Cancel")
                            .setNegativeBtnBackground(R.color.negativeBtn)  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue).setAnimation(Animation.POP)
                                .isCancellable(true)
                                .setIcon(R.drawable.ic_baseline_pan_tool_24, View.VISIBLE)
                                .onPositiveClicked(dialog -> {
                                    FertilizerHistoryFragment fertilizerHistoryFragment = new FertilizerHistoryFragment();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, fertilizerHistoryFragment)
                                            .commit();
                                })
                                .onNegativeClicked(dialog -> {
                                    fertilizerList.remove(position);
                                    fertilizerListRecyclerAdapter.notifyItemRemoved(position);
                                    removeAnimal(name);
                                })
                                .build()
                                .show();

                        break;
                    case ItemTouchHelper.RIGHT:
                        UpdateFertilizerFragment updateFertilizerFragment = new UpdateFertilizerFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        updateFertilizerFragment.setArguments(bundle);
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, updateFertilizerFragment)
                                .commit();
                        break;
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getActivity(),R.color.positiveBtn))
                        .addSwipeLeftActionIcon(R.drawable.ic_baseline_delete_24)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(getActivity(),R.color.green))
                        .addSwipeRightActionIcon(R.drawable.ic_baseline_edit_24)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

    }

    private void removeAnimal(String animalId) {
        fertilizersCollection.document(animalId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(recyclerview,"Do you want to undo",Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        fertilizersCollection.document(deletedFertilizer.getName()).set(deletedFertilizer);
                                        fertilizerList.add(position,deletedFertilizer);
                                        fertilizerListRecyclerAdapter.notifyItemInserted(position);
                                    }
                                }).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void getAnimalList(){
        fertilizersCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot fertilizers : queryDocumentSnapshots){
                                Fertilizer fertilizer = fertilizers.toObject(Fertilizer.class);
                                fertilizerList.add(fertilizer);
                            }
                            fertilizerListRecyclerAdapter = new FertilizerListRecyclerAdapter(getActivity(),fertilizerList);
                            recyclerview.setAdapter(fertilizerListRecyclerAdapter);
                            fertilizerListRecyclerAdapter.notifyDataSetChanged();

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
