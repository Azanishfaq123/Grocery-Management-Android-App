package nssoftwares.com.storemanagment.Connection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nssoftwares.com.storemanagment.R;

public class ExistingItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<FetchExistingItem> itemList;
    private OnItemDoubleClickListener doubleClickListener;

    // Interface for handling double-clicks
    public interface OnItemDoubleClickListener {
        void onItemDoubleClick(FetchExistingItem item);
    }

    // Constructor
    public ExistingItemAdapter(List<FetchExistingItem> itemList, OnItemDoubleClickListener doubleClickListener) {
        this.itemList = itemList;
        this.doubleClickListener = doubleClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        // Return header view type for the first row, and item view type for others
        return (position == 0) ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.existingitemheaderlayout, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.existingitemlayout, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            FetchExistingItem item = itemList.get(position - 1); // Subtract 1 for header
            ((ItemViewHolder) holder).itemCode.setText(item.getItemCode());
            ((ItemViewHolder) holder).itemName.setText(item.getItemName());
            ((ItemViewHolder) holder).minQty.setText(String.valueOf(item.getMinQty()));

            // Handle double-click listener for each item
            holder.itemView.setOnClickListener(new DoubleClickListener() {
                @Override
                public void onDoubleClick(View v) {
                    doubleClickListener.onItemDoubleClick(item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        // Add 1 to the list size for the header row
        return itemList.size() + 1;
    }

    // ViewHolder class for the header row
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    // ViewHolder class for the item rows
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemCode, itemName, minQty;

        public ItemViewHolder(View itemView) {
            super(itemView);
            itemCode = itemView.findViewById(R.id.txtItemCode);
            itemName = itemView.findViewById(R.id.txtItemName);
            minQty = itemView.findViewById(R.id.txtMinQty);
        }
    }
}
