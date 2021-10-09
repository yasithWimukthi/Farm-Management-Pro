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
import com.farmmanagementpro.modals.Medicine;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MedicineListRecyclerAdapter  extends RecyclerView.Adapter<MedicineListRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Medicine> medicineList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("medicine");

    public MedicineListRecyclerAdapter(Context context, List<Medicine> animalList) {
        this.context = context;
        this.medicineList = animalList;
    }

    @NonNull
    @Override
    public MedicineListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medicine_item_card,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicine medicine = medicineList.get(position);
        holder.name.setText(medicine.getName());
        holder.date.setText(medicine.getDate());
        holder.qty.setText(medicine.getQty());
        holder.prescriptBy.setText(medicine.getPrescribedBy());
        holder.supplier.setText(medicine.getSupplier());

        Picasso.get()
                .load(medicine.getImage())
                .placeholder(R.drawable.upload_image)
                .fit()
                .into(holder.medicineImage);
    }


    @Override
    public int getItemCount() {
        return medicineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView medicineImage;
        public TextView name;
        public TextView date;
        public TextView qty;
        public TextView prescriptBy;
        public TextView supplier;


        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;
            medicineImage = view.findViewById(R.id.medicineCardImage);
            date = view.findViewById(R.id.PurchasedDateValueMedicineCard);
            name = view.findViewById(R.id.NameValueMediCard);
            qty = view.findViewById(R.id.QuantityValueMediCard);
            prescriptBy = view.findViewById(R.id.PrescribedByValueMediCard);
            supplier = view.findViewById(R.id.SupplierValueMediCard);
        }
    }
}