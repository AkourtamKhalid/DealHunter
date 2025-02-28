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
import com.example.dealhunter.adapter.GameAdapter
import com.example.dealhunter.api.CheapSharkApi
import com.example.dealhunter.model.Game
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.google.android.gms.common.GoogleApiAvailability
import okhttp3.OkHttpClient
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import com.example.dealhunter.utils.NetworkUtils

class HomeActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var searchInput: EditText
    private lateinit var gameList: RecyclerView
    private lateinit var gamesFoundText: TextView
    private lateinit var gameAdapter: GameAdapter
    private lateinit var searchQueryDisplay: TextView
    private lateinit var searchButton: Button
    private lateinit var filterSpinner: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        checkGooglePlayServices()

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Welcome, ${user?.displayName ?: "User"}"

        initializeViews()
        setupRecyclerView()
        setupSearch()
        setupFilterSpinner()
        loadGames("")

        val logoutButton: Button = findViewById(R.id.btnLogout)
        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, GameSearchActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this)
        if (resultCode != com.google.android.gms.common.ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(resultCode)) {
                googleApiAvailability.getErrorDialog(this, resultCode, 2404)?.show()
            } else {
                Toast.makeText(this, "This device is not supported", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun initializeViews() {
        searchInput = findViewById(R.id.searchInput)
        gameList = findViewById(R.id.gameList)
        gamesFoundText = findViewById(R.id.gamesFound)
        searchQueryDisplay = findViewById(R.id.searchQueryDisplay)
        searchButton = findViewById(R.id.searchButton)
        filterSpinner = findViewById(R.id.filterSpinner)
    }

    private fun setupRecyclerView() {
        gameList.layoutManager = LinearLayoutManager(this)
        gameAdapter = GameAdapter(emptyList())
        gameList.adapter = gameAdapter
    }

    private fun setupSearch() {
        // Keep existing keyboard search functionality
        searchInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Add button click handler
        searchButton.setOnClickListener {
            performSearch()
        }
    }

    private fun setupFilterSpinner() {
        ArrayAdapter.createFromResource(
            this,
            R.array.filter_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            filterSpinner.adapter = adapter
        }

        filterSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                gameAdapter.filterDeals(pos)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun performSearch() {
        val searchQuery = searchInput.text.toString()
        if (searchQuery.isNotEmpty()) {
            searchQueryDisplay.text = "Recherche: $searchQuery"
            searchQueryDisplay.visibility = View.VISIBLE
            loadGames(searchQuery)
        } else {
            // Use default search (batman)
            searchQueryDisplay.text = "Recherche: batman"
            searchQueryDisplay.visibility = View.VISIBLE
            loadGames("batman")
        }
    }

    private fun loadGames(query: String) {
        val api = NetworkUtils.createRetrofit()
            .create(CheapSharkApi::class.java)
        
        api.getGames(query).enqueue(object : Callback<List<Game>> {
            override fun onResponse(call: Call<List<Game>>, response: Response<List<Game>>) {
                if (response.isSuccessful) {
                    val games = response.body() ?: emptyList()
                    gameAdapter.updateGames(games)
                    gamesFoundText.text = "${games.size} JEUX TROUVÉS"
                } else {
                    Log.e("HomeActivity", "Error loading games: ${response.code()}")
                    Toast.makeText(
                        this@HomeActivity,
                        "Error loading games: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<List<Game>>, t: Throwable) {
                Log.e("HomeActivity", "Failed to load games", t)
                Toast.makeText(
                    this@HomeActivity,
                    "Error loading games: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}