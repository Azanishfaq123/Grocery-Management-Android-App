package nssoftwares.com.storemanagment.Connection;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import nssoftwares.com.storemanagment.R;
import nssoftwares.com.storemanagment.RecipiesListActivity;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;

    public RecipeAdapter(List<Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item_layout, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe recipe = recipeList.get(position);

        // Set recipe name and code
        holder.recipeNameTextView.setText(recipe.getName());
        holder.recipeCodeTextView.setText(String.valueOf(recipe.getRecipecodee()));

        // Set OnClickListener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            // Call the delete method from the activity with the recipe code
            ((RecipiesListActivity) holder.itemView.getContext()).deletenow(v, recipe.getRecipecodee());
        });

        // Clear previous ingredients
        holder.ingredientsContainer.removeAllViews();

        // Populate ingredients
        for (Ingredient ingredient : recipe.getIngredients()) {
            View ingredientView = LayoutInflater.from(holder.itemView.getContext())
                    .inflate(R.layout.ingredient_item_layout, holder.ingredientsContainer, false);
            ((TextView) ingredientView.findViewById(R.id.itemNameTextView)).setText(ingredient.getItemName());
            ((TextView) ingredientView.findViewById(R.id.quantityTextView)).setText("Qty: " + ingredient.getQuantity());
            ((TextView) ingredientView.findViewById(R.id.barcodeTextView)).setText("Barcode: " + ingredient.getBarcode());
            holder.ingredientsContainer.addView(ingredientView);
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeNameTextView;
        public TextView recipeCodeTextView; // Declare recipeCodeTextView here
        public LinearLayout ingredientsContainer;
        public Button deleteButton; // Declare deleteButton here

        public RecipeViewHolder(View itemView) {
            super(itemView);
            recipeNameTextView = itemView.findViewById(R.id.recipeNameTextView);
            recipeCodeTextView = itemView.findViewById(R.id.recipeCodeTextView); // Initialize recipeCodeTextView
            ingredientsContainer = itemView.findViewById(R.id.ingredientsContainer);
            deleteButton = itemView.findViewById(R.id.deleteButton); // Initialize deleteButton
        }
    }
}
