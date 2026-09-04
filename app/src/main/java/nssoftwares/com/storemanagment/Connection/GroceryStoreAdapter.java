package nssoftwares.com.storemanagment.Connection;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // For loading images

import java.util.List;

import nssoftwares.com.storemanagment.R;

public class GroceryStoreAdapter extends RecyclerView.Adapter<GroceryStoreAdapter.ViewHolder> {

    private List<GroceryStore> groceryStores;
    private Context context;

    public GroceryStoreAdapter(List<GroceryStore> groceryStores, Context context) {
        this.groceryStores = groceryStores;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grocery_store_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GroceryStore store = groceryStores.get(position);
        holder.storeName.setText(store.getName());
        Glide.with(context).load(store.getLogoUrl()).into(holder.storeLogo); // Load the logo

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(store.getWebsiteUrl()));
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return groceryStores.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView storeName;
        ImageView storeLogo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            storeName = itemView.findViewById(R.id.storeName);
            storeLogo = itemView.findViewById(R.id.storeLogo);
        }
    }
}
