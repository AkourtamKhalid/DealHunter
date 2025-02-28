package com.example.dealhunter

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = Firebase.auth

        val loginButton: TextView = findViewById(R.id.tvLogin)
        loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val registerButton: Button = findViewById(R.id.btnRegister)
        registerButton.setOnClickListener {
            val firstName = findViewById<EditText>(R.id.etFirstName).text.toString()
            val lastName = findViewById<EditText>(R.id.etLastName).text.toString()
            val email = findViewById<EditText>(R.id.etEmail).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()

            if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            // Updated to use the new API
                            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                                displayName = "$firstName $lastName"
                            }
                            user?.updateProfile(profileUpdates)?.addOnCompleteListener { profileTask ->
                                if (profileTask.isSuccessful) {
                                    Toast.makeText(this, "Inscription réussie", Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, GameSearchActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this, "Mise à jour du profil échouée: ${profileTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(this, "Inscription échouée: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
            }
        }
    }
}