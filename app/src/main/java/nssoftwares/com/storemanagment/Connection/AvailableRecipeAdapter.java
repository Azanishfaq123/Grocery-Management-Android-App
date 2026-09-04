package nssoftwares.com.storemanagment.Connection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import nssoftwares.com.storemanagment.R;

public class AvailableRecipeAdapter extends RecyclerView.Adapter<AvailableRecipeAdapter.RecipeViewHolder> {

    private List<AvailableRecipe> recipeList;

    public AvailableRecipeAdapter(List<AvailableRecipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.available_recipe_item_layout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        AvailableRecipe recipe = recipeList.get(position);

        holder.recipeNameTextView.setText(recipe.getName());
        holder.recipeCodeTextView.setText(recipe.getRecipeCode());

        holder.ingredientsContainer.removeAllViews();
        for (AvailableIngredient ingredient : recipe.getIngredients()) {
            View ingredientView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.available_ingredient_item_layout, holder.ingredientsContainer, false);
            ((TextView) ingredientView.findViewById(R.id.itemNameTextView)).setText(ingredient.getItemName());
            ((TextView) ingredientView.findViewById(R.id.barcodeTextView)).setText("Barcode: " + ingredient.getBarcode());
            ((TextView) ingredientView.findViewById(R.id.quantityTextView)).setText("Req Qty: " + ingredient.getQuantity());
            ((TextView) ingredientView.findViewById(R.id.availQtyTextView)).setText("Stock Qty: " + ingredient.getAvailQty());
            holder.ingredientsContainer.addView(ingredientView);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeNameTextView;
        public TextView recipeCodeTextView;
        public LinearLayout ingredientsContainer;

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeCodeTextView = itemView.findViewById(R.id.recipeCodeTextView);
            ingredientsContainer = itemView.findViewById(R.id.ingredientsContainer);
        }
    }
}
