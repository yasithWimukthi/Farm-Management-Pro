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
import com.farmmanagementpro.modals.Animal;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnimalListRecyclerAdapter extends RecyclerView.Adapter<AnimalListRecyclerAdapter.ViewHolder>{

    private Context context;
    private List<Animal> animalList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("animals");

    public AnimalListRecyclerAdapter(Context context, List<Animal> animalList) {
        this.context = context;
        this.animalList = animalList;
    }

    @NonNull
    @Override
    public AnimalListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_list_item,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalListRecyclerAdapter.ViewHolder holder, int position) {
        Animal animal = animalList.get(position);
        holder.animalId.setText(animal.getAnimalId());
        holder.breed.setText(animal.getBreed());
        holder.dob.setText(animal.getDob());
        holder.gender.setText(animal.getGender());
        holder.registerDate.setText(animal.getRegisteredDate());

        Picasso.get()
                .load(animal.getImage())
                .placeholder(R.drawable.vaccine)
                .fit()
                .into(holder.animalImage);
    }


    @Override
    public int getItemCount() {
        return animalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView animalImage;
        public TextView animalId;
        public TextView breed;
        public TextView dob;
        public TextView gender;
        public TextView registerDate;

        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;
            animalImage = view.findViewById(R.id.animalImage);
            animalId = view.findViewById(R.id.animalIdValue);
            breed = view.findViewById(R.id.breedValue);
            dob = view.findViewById(R.id.dobValue);
            gender = view.findViewById(R.id.genderValue);
            registerDate = view.findViewById(R.id.regDateValue);
        }
    }
}
