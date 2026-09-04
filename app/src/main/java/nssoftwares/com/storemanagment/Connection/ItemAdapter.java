package nssoftwares.com.storemanagment.Connection;

import nssoftwares.com.storemanagment.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Item> itemList;
    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    public ItemAdapter(List<Item> itemList) {
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_layout, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Item item = itemList.get(position - 1); // Subtract 1 to account for header
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            itemHolder.itemCode.setText(item.getItemCode());
            itemHolder.itemName.setText(item.getItemName());
            itemHolder.quantity.setText(String.valueOf(item.getQuantity()));
            itemHolder.barcode.setText(item.getBarcode()); // Bind barcode to TextView
        }
    }

    @Override
    public int getItemCount() {
        return itemList.size() + 1; // +1 for the header
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return VIEW_TYPE_HEADER;
        } else {
            return VIEW_TYPE_ITEM;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize header views if needed
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemCode;
        TextView itemName;
        TextView quantity;
        TextView barcode; // New TextView for barcode

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCode = itemView.findViewById(R.id.itemCode);   // Referencing the IDs from item_layout.xml
            itemName = itemView.findViewById(R.id.itemName);
            quantity = itemView.findViewById(R.id.quantity);
            barcode = itemView.findViewById(R.id.barcode); // Initialize barcode TextView
        }
    }
}
