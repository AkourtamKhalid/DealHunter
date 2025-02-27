package com.example.dealhunter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LandingPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_landing)

        // Trouver le bouton "Ça commence !" par son ID
        val getStartedButton: Button = findViewById(R.id.get_started_button)

        // Ajouter un écouteur d'événements pour le bouton
        getStartedButton.setOnClickListener {
            // Démarrer LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}