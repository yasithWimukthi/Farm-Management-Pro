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
import com.farmmanagementpro.modals.Feed;
import com.farmmanagementpro.modals.Medicine;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FeedListRecyclerAdapter extends RecyclerView.Adapter<FeedListRecyclerAdapter.ViewHolder>{

    private Context context;
    private List<Feed> feedList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference patientCollection = db.collection("feed");

    public FeedListRecyclerAdapter(Context context, List<Feed> feedList) {
        this.context = context;
        this.feedList = feedList;
    }
    @NonNull
    @Override
    public FeedListRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_feed_item_card,parent,false);
        return new FeedListRecyclerAdapter.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull  FeedListRecyclerAdapter.ViewHolder holder, int possition) {


        Feed feed = feedList.get(possition);
        holder.name.setText(feed.getName());
        holder.date.setText(feed.getDate());
        holder.qty.setText(feed.getQty());
        holder.description.setText(feed.getDesctiption());
        holder.supplier.setText(feed.getSupplier());

        Picasso.get()
                .load(feed.getImage())
                .placeholder(R.drawable.upload_image)
                .fit()
                .into(holder.feedImage);
    }

    @Override
    public int getItemCount() {
        return feedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView feedImage;
        public TextView name;
        public TextView date;
        public TextView qty;
        public TextView description;
        public TextView supplier;


        public ViewHolder(View view, Context ctx) {
            super(view);
            context = ctx;
            feedImage = view.findViewById(R.id.imageViewFeedCard);
            date = view.findViewById(R.id.PurchasedDateFeedCard);
            name = view.findViewById(R.id.NameFeedCard);
            qty = view.findViewById(R.id.QuantityFeedCard);
            description = view.findViewById(R.id.DescriptionFeedCard);
            supplier = view.findViewById(R.id.SupplierFeedCard);
        }
    }
}
