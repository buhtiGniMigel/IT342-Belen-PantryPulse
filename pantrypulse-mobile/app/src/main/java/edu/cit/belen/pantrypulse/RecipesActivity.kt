package edu.cit.belen.pantrypulse

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipesActivity : AppCompatActivity() {

    private lateinit var rvRecipes: RecyclerView
    private lateinit var pbLoading: ProgressBar
    private lateinit var adapter: RecipesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipes)

        rvRecipes = findViewById(R.id.rvRecipes)
        pbLoading = findViewById(R.id.pbLoading)

        rvRecipes.layoutManager = LinearLayoutManager(this)

        adapter = RecipesAdapter(emptyList()) { recipe ->
            // Open RecipeDetailActivity on item click
            val intent = Intent(this, RecipeDetailActivity::class.java)
            intent.putExtra("EXTRA_RECIPE", recipe)
            startActivity(intent)
        }
        rvRecipes.adapter = adapter

        fetchSuggestedRecipes()
    }

    private fun fetchSuggestedRecipes() {
        pbLoading.visibility = View.VISIBLE
        val apiService = ApiService.create(this)

        apiService.getSuggestedRecipes().enqueue(object : Callback<List<RecipeResponse>> {
            override fun onResponse(call: Call<List<RecipeResponse>>, response: Response<List<RecipeResponse>>) {
                pbLoading.visibility = View.GONE
                if (response.isSuccessful && response.body() != null) {
                    val recipes = response.body()!!
                    if (recipes.isEmpty()) {
                        Toast.makeText(this@RecipesActivity, "No recipes found. Add expiring items to get matches!", Toast.LENGTH_LONG).show()
                    } else {
                        adapter.updateData(recipes)
                    }
                } else {
                    Toast.makeText(this@RecipesActivity, "Failed to load suggestions", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RecipeResponse>>, t: Throwable) {
                pbLoading.visibility = View.GONE
                Toast.makeText(this@RecipesActivity, "Network Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- NESTED CUSTOM RECIPES ADAPTER ---
    class RecipesAdapter(
        private var recipes: List<RecipeResponse>,
        private val onClick: (RecipeResponse) -> Unit
    ) : RecyclerView.Adapter<RecipesAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val ivRecipeImage: ImageView = view.findViewById(R.id.ivRecipeImage)
            val tvRecipeTitle: TextView = view.findViewById(R.id.tvRecipeTitle)
            val tvRecipeDescription: TextView = view.findViewById(R.id.tvRecipeDescription)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val recipe = recipes[position]
            holder.tvRecipeTitle.text = recipe.title
            holder.tvRecipeDescription.text = recipe.description ?: "Perfect way to use up your expiring ingredients!"

            // Load recipe image with Glide
            Glide.with(holder.itemView.context)
                .load(recipe.image)
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.ivRecipeImage)

            holder.itemView.setOnClickListener { onClick(recipe) }
        }

        override fun getItemCount() = recipes.size

        fun updateData(newRecipes: List<RecipeResponse>) {
            this.recipes = newRecipes
            notifyDataSetChanged()
        }
    }
}
