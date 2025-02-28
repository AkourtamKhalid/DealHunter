package com.example.dealhunter

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dealhunter.adapter.DealAdapter
import com.example.dealhunter.api.CheapSharkApi
import com.example.dealhunter.model.Deal
import com.example.dealhunter.model.Store
import com.example.dealhunter.utils.NetworkUtils
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameSearchActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var searchButton: Button
    private lateinit var resultsCount: TextView
    private lateinit var loadingIndicator: ProgressBar
    private lateinit var errorMessage: TextView
    private lateinit var gamesRecyclerView: RecyclerView
    private lateinit var dealAdapter: DealAdapter
    private lateinit var auth: FirebaseAuth
    private var stores: Map<String, Store> = emptyMap()
    private lateinit var filterSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_search)

        auth = FirebaseAuth.getInstance()

        initializeViews()
        setupRecyclerView()
        setupSearchListeners()
        loadStores()
        setupFilterSpinner()

        val logoutButton: Button = findViewById(R.id.btnLogout)
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun initializeViews() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        searchInput = findViewById(R.id.searchInput)
        searchButton = findViewById(R.id.searchButton)
        resultsCount = findViewById(R.id.resultsCount)
        loadingIndicator = findViewById(R.id.loadingIndicator)
        errorMessage = findViewById(R.id.errorMessage)
        gamesRecyclerView = findViewById(R.id.gamesRecyclerView)
        filterSpinner = findViewById(R.id.filterSpinner)
    }

    private fun setupRecyclerView() {
        gamesRecyclerView.layoutManager = LinearLayoutManager(this)
        dealAdapter = DealAdapter(emptyList())
        gamesRecyclerView.adapter = dealAdapter
    }

    private fun setupSearchListeners() {
        searchButton.setOnClickListener { performSearch() }
        
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val query = searchInput.text.toString().trim()
        if (query.isEmpty()) {
            Toast.makeText(this, "Please enter a game title", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading()
        searchGames(query)
    }

    private fun searchGames(query: String) {
        val api = NetworkUtils.createRetrofit()
            .create(CheapSharkApi::class.java)

        api.searchDeals(title = query).enqueue(object : Callback<List<Deal>> {
            override fun onResponse(call: Call<List<Deal>>, response: Response<List<Deal>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val deals = response.body()
                    if (deals != null) {
                        Log.d("GameSearchActivity", "Received ${deals.size} deals")
                        val dealsWithStoreNames = deals.map { deal ->
                            val store = stores[deal.storeID]
                            deal.copy(storeName = store?.storeName ?: "Unknown Store")
                        }
                        updateUI(dealsWithStoreNames)
                    } else {
                        showError("No deals found")
                    }
                } else {
                    showError("Error: ${response.code()}")
                    Log.e("GameSearchActivity", "Error response: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<Deal>>, t: Throwable) {
                hideLoading()
                showError("Network error: ${t.localizedMessage}")
            }
        })
    }

    private fun updateUI(deals: List<Deal>) {
        if (deals.isEmpty()) {
            showError("No deals found")
            return
        }
        
        Log.d("GameSearchActivity", "Updating UI with ${deals.size} deals")
        resultsCount.text = "${deals.size} deals found"
        errorMessage.visibility = View.GONE
        dealAdapter.updateDeals(deals)
        gamesRecyclerView.visibility = View.VISIBLE
    }

    private fun showLoading() {
        loadingIndicator.visibility = View.VISIBLE
        errorMessage.visibility = View.GONE
    }

    private fun hideLoading() {
        loadingIndicator.visibility = View.GONE
    }

    private fun showError(message: String) {
        errorMessage.text = message
        errorMessage.visibility = View.VISIBLE
    }

    private fun loadStores() {
        val api = NetworkUtils.createRetrofit().create(CheapSharkApi::class.java)
        api.getStores().enqueue(object : Callback<List<Store>> {
            override fun onResponse(
                call: Call<List<Store>>,
                response: Response<List<Store>>
            ) {
                if (response.isSuccessful) {
                    stores = response.body()?.associateBy { store -> store.storeID } ?: emptyMap()
                }
            }

            override fun onFailure(call: Call<List<Store>>, t: Throwable) {
                Log.e("GameSearchActivity", "Failed to load stores", t)
            }
        })
    }

    private fun setupFilterSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.deal_filter_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            filterSpinner.adapter = adapter
        }

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                when (pos) {
                    0 -> dealAdapter.updateDeals(dealAdapter.getOriginalDeals()) // No filter
                    1 -> dealAdapter.sortBySalePrice(ascending = true)
                    2 -> dealAdapter.sortBySalePrice(ascending = false)
                    3 -> dealAdapter.sortBySavings()
                    4 -> dealAdapter.sortByDealRating()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }
}
