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
import com.farmmanagementpro.modals.FarmTask;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FarmTaskListRecyclerAdapter extends RecyclerView.Adapter<FarmTaskListRecyclerAdapter.ViewHolder>{
    private Context context;
    private List<FarmTask> farmTaskList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("task");

    public FarmTaskListRecyclerAdapter(Context context, List<FarmTask> farmTaskList) {
        this.context = context;
        this.farmTaskList = farmTaskList;
    }

    @NonNull
    @Override
    public FarmTaskListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.farm_task_list,parent,false);
        return new FarmTaskListRecyclerAdapter.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmTaskListRecyclerAdapter.ViewHolder holder, int position) {
        FarmTask farmTask = farmTaskList.get(position);
        holder.taskDate.setText(farmTask.getTaskDate());
        holder.taskStatus.setText(farmTask.getTaskStatus());
        holder.taskName.setText(farmTask.getTaskName());
        holder.description.setText(farmTask.getDescription());

    }

    @Override
    public int getItemCount() {
        return farmTaskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView farmTaskImage;
        public TextView taskDate;
        public TextView taskStatus;
        public TextView taskName;
        public TextView description;


        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;
            taskDate = view.findViewById(R.id.fertilizerQty);
            farmTaskImage = view.findViewById(R.id.fertilizerImage);
            taskStatus = view.findViewById(R.id.fertilizerDate);
            taskName = view.findViewById(R.id.fertilizerNameValue);
            description = view.findViewById(R.id.supplierValue);

        }
    }
}
