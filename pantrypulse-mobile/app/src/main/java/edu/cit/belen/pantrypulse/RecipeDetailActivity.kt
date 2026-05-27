package edu.cit.belen.pantrypulse

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class RecipeDetailActivity : AppCompatActivity() {

    private lateinit var ivRecipeDetailImage: ImageView
    private lateinit var tvRecipeDetailTitle: TextView
    private lateinit var tvRecipeDetailDesc: TextView
    private lateinit var tvRecipeIngredients: TextView
    private lateinit var tvRecipeInstructions: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        ivRecipeDetailImage = findViewById(R.id.ivRecipeDetailImage)
        tvRecipeDetailTitle = findViewById(R.id.tvRecipeDetailTitle)
        tvRecipeDetailDesc = findViewById(R.id.tvRecipeDetailDesc)
        tvRecipeIngredients = findViewById(R.id.tvRecipeIngredients)
        tvRecipeInstructions = findViewById(R.id.tvRecipeInstructions)

        val recipe = intent.getSerializableExtra("EXTRA_RECIPE") as? RecipeResponse
        if (recipe != null) {
            tvRecipeDetailTitle.text = recipe.title
            tvRecipeDetailDesc.text = recipe.description ?: "A healthy and sustainable home-cooked meal."
            
            // Format ingredients and instructions nicely
            tvRecipeIngredients.text = recipe.ingredients?.replace("; ", "\n• ")?.let { "• $it" } ?: "Ingredients details not available."
            tvRecipeInstructions.text = recipe.instructions?.replace(". ", ".\n\n") ?: "Instructions details not available."

            // Load recipe banner image with Glide
            Glide.with(this)
                .load(recipe.image)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(ivRecipeDetailImage)
        }
    }
}
