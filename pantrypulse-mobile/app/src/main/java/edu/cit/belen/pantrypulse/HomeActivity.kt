package edu.cit.belen.pantrypulse

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeActivity : AppCompatActivity() {

    private lateinit var rvPantry: RecyclerView
    private lateinit var fabAddItem: FloatingActionButton
    private lateinit var llEmptyState: LinearLayout
    private lateinit var tvUserWelcome: TextView
    private lateinit var adapter: PantryItemAdapter
    
    private lateinit var repository: InventoryRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        repository = InventoryRepository(this)

        // Initialize views
        rvPantry = findViewById(R.id.rvPantry)
        fabAddItem = findViewById(R.id.fabAddItem)
        llEmptyState = findViewById(R.id.llEmptyState)
        tvUserWelcome = findViewById(R.id.tvUserWelcome)

        rvPantry.layoutManager = LinearLayoutManager(this)

        // Initialize list and adapter
        adapter = PantryItemAdapter(emptyList()) { item ->
            // On Item click -> Open AddItemActivity in "Edit Mode"
            val intent = Intent(this, AddItemActivity::class.java)
            intent.putExtra("EXTRA_ITEM", item)
            startActivity(intent)
        }
        rvPantry.adapter = adapter

        // Welcome text customizer
        val sharedPrefs = getSharedPreferences("PantryPulsePrefs", Context.MODE_PRIVATE)
        val firstName = sharedPrefs.getString("firstName", "User")
        tvUserWelcome.text = "Welcome back, $firstName!"

        // Bottom Navigation Click Handlers
        findViewById<LinearLayout>(R.id.tabRecipes).setOnClickListener {
            startActivity(Intent(this, RecipesActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.tabProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        fabAddItem.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh pantry list every time screen is returned to
        fetchPantryList()
    }

    private fun fetchPantryList() {
        Thread {
            // Fetch items asynchronously using repository (online sync + offline local fallback)
            val items = repository.getAllItems(forceRefresh = true)
            runOnUiThread {
                if (items.isEmpty()) {
                    llEmptyState.visibility = View.VISIBLE
                    rvPantry.visibility = View.GONE
                } else {
                    llEmptyState.visibility = View.GONE
                    rvPantry.visibility = View.VISIBLE
                    adapter.updateData(items)
                }
            }
        }.start()
    }
}