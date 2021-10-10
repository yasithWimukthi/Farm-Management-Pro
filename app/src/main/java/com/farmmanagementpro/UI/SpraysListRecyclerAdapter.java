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
import com.farmmanagementpro.modals.Spray;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SpraysListRecyclerAdapter extends RecyclerView.Adapter<SpraysListRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<Spray> sprayList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("sprays");

    public SpraysListRecyclerAdapter(Context context, List<Spray> sprayList) {
        this.context = context;
        this.sprayList = sprayList;
    }

    @NonNull
    @Override
    public SpraysListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sprays_item_list,parent,false);
        return new SpraysListRecyclerAdapter.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull SpraysListRecyclerAdapter.ViewHolder holder, int position) {
        Spray spray = sprayList.get(position);
        holder.name.setText(spray.getName());
        holder.qty.setText(spray.getQty());
        holder.date.setText(spray.getDate());
        holder.supplier.setText(spray.getSupplier());

        Picasso.get()
                .load(spray.getImage())
                .placeholder(R.drawable.gramoxone)
                .fit()
                .into(holder.sprayImage);
    }


    @Override
    public int getItemCount() {
        return sprayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView sprayImage;
        public TextView name;
        public TextView qty;
        public TextView date;
        public TextView supplier;

        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;
            sprayImage = view.findViewById(R.id.sprayImage);
            name = view.findViewById(R.id.sprayNameValue);
            qty = view.findViewById(R.id.sprayQuantityValue);
            date = view.findViewById(R.id.sprayDateValue);
            supplier = view.findViewById(R.id.supplierNameValue);
        }
    }
}
