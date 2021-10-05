package com.farmmanagementpro.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.farmmanagementpro.R;
import com.farmmanagementpro.modals.AnimalEvent;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AnimalEventListRecyclerAdapter extends RecyclerView.Adapter<AnimalEventListRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<AnimalEvent> eventsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("events");

    public AnimalEventListRecyclerAdapter(Context context, List<AnimalEvent> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
    }

    @NonNull
    @Override
    public AnimalEventListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_event_list_item,parent,false);
        return new AnimalEventListRecyclerAdapter.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalEventListRecyclerAdapter.ViewHolder holder, int position) {
        AnimalEvent event = eventsList.get(position);
        holder.animalId.setText(event.getAnimalId());
        holder.eventDate.setText(event.getEventDate());
        holder.eventName.setText(event.getEventName());
        holder.note.setText(event.getNotes());
        holder.stockBull.setText(event.getStockBull());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView animalEventImage;
        public TextView animalId;
        public TextView eventName;
        public TextView eventDate;
        public TextView stockBull;
        public TextView note;

        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;
            animalId = view.findViewById(R.id.animalIdValue);
            animalEventImage = view.findViewById(R.id.animalEventImage);
            eventName = view.findViewById(R.id.eventNameValue);
            eventDate = view.findViewById(R.id.eventDateValue);
            stockBull = view.findViewById(R.id.stockBullValue);
            note = view.findViewById(R.id.notesValue);
        }
    }
}
