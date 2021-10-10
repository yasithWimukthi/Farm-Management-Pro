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

import com.farmmanagementpro.UI.FeedListRecyclerAdapter;
import com.farmmanagementpro.UI.MedicineListRecyclerAdapter;
import com.farmmanagementpro.modals.Feed;
import com.farmmanagementpro.modals.Medicine;
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

public class FeedHistoryFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference feedCollection = db.collection("feed");

    private RecyclerView recyclerview;
    private FeedListRecyclerAdapter feedListRecyclerAdapter;

    private List<Feed> feedList;
    private ItemTouchHelper.SimpleCallback simpleCallback;
    private Feed deletedFeed;
    private int position;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.feed_history_fragment,container,false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull  View v, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        feedList = new ArrayList<>();
        recyclerview = v.findViewById(R.id.feedListRecyclerView);
        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        getFeedList();

        simpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                String name = feedList.get(position).getName();
                deletedFeed = feedList.get(position);

                switch (direction) {
                    case ItemTouchHelper.LEFT:
                        FancyAlertDialog.Builder
                                .with(getActivity())
                                .setTitle("Warning !")
                                .setBackgroundColor(Color.parseColor("#ff0000"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                                .setMessage("Do you want to delete this Feed?")
                                .setNegativeBtnText("Delete")
                                .setPositiveBtnBackground(Color.parseColor("#00912B"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                                .setPositiveBtnText("Cancel")
                                .setNegativeBtnBackground(Color.parseColor("#00912B"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                                .setAnimation(Animation.POP)
                                .isCancellable(true)
                                .setIcon(R.drawable.ic_baseline_pan_tool_24, View.VISIBLE)
                                .onPositiveClicked(dialog -> {
                                    FeedHistoryFragment feedHistoryFragment = new FeedHistoryFragment();
                                    getFragmentManager()
                                            .beginTransaction()
                                            .replace(R.id.container, feedHistoryFragment)
                                            .commit();
                                })
                                .onNegativeClicked(dialog -> {
                                    feedList.remove(position);
                                    feedListRecyclerAdapter.notifyItemRemoved(position);
                                    removeFeed(name);
                                })
                                .build()
                                .show();
                        break;
                    case ItemTouchHelper.RIGHT:
                        UpdateFeed updateFeed = new UpdateFeed();
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        updateFeed.setArguments(bundle);
                        getFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, updateFeed)
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

    private void removeFeed(String name) {
        feedCollection.document(name)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(recyclerview,"Do you want to undo", Snackbar.LENGTH_LONG)
                                .setAction("Undo", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        feedCollection.document(deletedFeed.getName()).set(deletedFeed);
                                        feedList.add(position,deletedFeed);
                                        feedListRecyclerAdapter.notifyItemInserted(position);
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

    private void getFeedList() {

        feedCollection.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for(QueryDocumentSnapshot feeds : queryDocumentSnapshots){
                                Feed feed = feeds.toObject(Feed.class);
                                feedList.add(feed);
                            }

                            feedListRecyclerAdapter = new FeedListRecyclerAdapter(getActivity(),feedList);
                            recyclerview.setAdapter(feedListRecyclerAdapter);
                            feedListRecyclerAdapter.notifyDataSetChanged();

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
