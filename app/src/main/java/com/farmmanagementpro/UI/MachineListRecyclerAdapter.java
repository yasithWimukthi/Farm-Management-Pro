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
import com.farmmanagementpro.modals.Machine;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MachineListRecyclerAdapter extends RecyclerView.Adapter<MachineListRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<Machine> machineList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("machines");

    public MachineListRecyclerAdapter(Context context, List<Machine> eventsList) {
        this.context = context;
        this.machineList = machineList;
    }

    @NonNull
    @Override
    public MachineListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.my_machinery_item_list,parent,false);
        return new MachineListRecyclerAdapter.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull MachineListRecyclerAdapter.ViewHolder holder, int position) {
        Machine machine = machineList.get(position);
        holder.name.setText(machine.getName());
        holder.date.setText(machine.getDate());
        holder.type.setText(machine.getType());
        holder.cost.setText(machine.getCost());
        holder.notes.setText(machine.getNotes());
    }

    @Override
    public int getItemCount() {
        return machineList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView machineImage;
        public TextView name;
        public TextView date;
        public TextView type;
        public TextView cost;
        public TextView notes;

        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;
            machineImage = view.findViewById(R.id.machineImage);
            name = view.findViewById(R.id.machineNameValue);
            date = view.findViewById(R.id.serviceDateValue);
            type = view.findViewById(R.id.serviceTypeValue);
            cost = view.findViewById(R.id.serviceCostValue);
            notes = view.findViewById(R.id.machineNotesValue);
        }
    }
}
