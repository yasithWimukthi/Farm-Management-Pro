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
import com.farmmanagementpro.modals.Fertilizer;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FertilizerListRecyclerAdapter extends RecyclerView.Adapter<FertilizerListRecyclerAdapter.ViewHolder>{

    private Context context;
    private List<Fertilizer> fertilizerList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("fertilizer");

    public FertilizerListRecyclerAdapter(Context context, List<Fertilizer> fertilizerList) {
        this.context = context;
        this.fertilizerList = fertilizerList;
    }

    @NonNull
    @Override
    public FertilizerListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fertilizer_list,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull FertilizerListRecyclerAdapter.ViewHolder holder, int position) {
        Fertilizer fertilizer = fertilizerList.get(position);
        holder.name.setText(fertilizer.getName());
        holder.date.setText(fertilizer.getDate());
        holder.quantity.setText(fertilizer.getQuantity());
        holder.supplier.setText(fertilizer.getSupplier());
        holder.batchNo.setText(fertilizer.getBatchNo());

        Picasso.get()
                .load(fertilizer.getImage())
                .placeholder(R.drawable.vaccine)
                .fit()
                .into(holder.fertilizerImage);
    }


    @Override
    public int getItemCount() {
        return fertilizerList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView fertilizerImage;
        public TextView name;
        public TextView date;
        public TextView quantity;
        public TextView supplier;
        public TextView batchNo;

        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;
            fertilizerImage = view.findViewById(R.id.fertilizerImage);
            name = view.findViewById(R.id.fertilizerNameValue);
            date = view.findViewById(R.id.fertilizerDate);
            quantity = view.findViewById(R.id.fertilizerQty);
            supplier = view.findViewById(R.id.supplierValue);
            batchNo = view.findViewById(R.id.batchNumberValue);

        }
    }
}
