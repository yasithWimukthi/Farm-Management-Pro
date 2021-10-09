package com.farmmanagementpro;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.farmmanagementpro.UI.AnimalEventListRecyclerAdapter;
import com.farmmanagementpro.modals.AnimalEvent;
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

public class AnimalEventFragment extends Fragment {
    /**/

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference eventsCollection = db.collection("events");

    private RecyclerView recyclerview;
    private AnimalEventListRecyclerAdapter eventsAdapter;

    private List<AnimalEvent> eventsList;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private AnimalEvent deletedEvent;
    private int position;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.animal_events_fragment,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        eventsList = new ArrayList<>();
        recyclerview = view.findViewById(R.id.eventsRecyclerView);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        
        getEventsList();

        simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                String eventDate = eventsList.get(position).getEventDate();
                deletedEvent = eventsList.get(position);

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        FancyAlertDialog.Builder
                                .with(getActivity())
                                .setTitle("Do you want to delete this event ?")
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
                                    AnimalEventFragment animalEventFragment = new AnimalEventFragment();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, animalEventFragment)
                                            .commit();
                                })
                                .onNegativeClicked(dialog -> {
                                    eventsList.remove(position);
                                    eventsAdapter.notifyItemRemoved(position);
                                    removeEvent(eventDate);
                                })
                                .build()
                                .show();

                        break;
                    case ItemTouchHelper.RIGHT:
                        UpdateEventFragment updateEventFragment = new UpdateEventFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("eventDate", eventDate);
                        updateEventFragment.setArguments(bundle);
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, updateEventFragment)
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

    private void removeEvent(String eventDate) {
        eventsCollection.document(eventDate)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(recyclerview,"Do you want to undo",Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        eventsCollection.document(eventDate).set(deletedEvent);
                                        eventsList.add(position,deletedEvent);
                                        eventsAdapter.notifyItemInserted(position);
                                    }
                                }).show();
                    }
                });
    }

    private void getEventsList() {
        eventsCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot events : queryDocumentSnapshots){
                                AnimalEvent event = events.toObject(AnimalEvent.class);
                                eventsList.add(event);
                            }
                            eventsAdapter = new AnimalEventListRecyclerAdapter(getActivity(),eventsList);
                            recyclerview.setAdapter(eventsAdapter);
                            eventsAdapter.notifyDataSetChanged();

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
