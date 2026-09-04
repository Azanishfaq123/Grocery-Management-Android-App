package nssoftwares.com.storemanagment.Connection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import nssoftwares.com.storemanagment.R;
import nssoftwares.com.storemanagment.SuggestMeActivity;

public class SuggestMeAdapter extends RecyclerView.Adapter<SuggestMeAdapter.ViewHolder> {

    private List<String> recipeList;
    private Context context;

    public SuggestMeAdapter(Context context, List<String> recipeList) {
        this.context = context;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String recipeName = recipeList.get(position);
        holder.recipeNameTextView.setText(recipeName);

        // Set the tag for the "Pick" button to the recipe name
        holder.pickButton.setTag(recipeName);

        holder.pickButton.setOnClickListener(v -> {
            // Call the onPick method in SuggestMeActivity
            if (context instanceof SuggestMeActivity) {
                ((SuggestMeActivity) context).onPick(v);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView recipeNameTextView;
        Button pickButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipe_name);
            pickButton = itemView.findViewById(R.id.pick_button); // Ensure correct button ID
        }
    }
}
