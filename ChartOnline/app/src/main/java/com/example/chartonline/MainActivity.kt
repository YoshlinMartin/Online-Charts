package com.example.chartonline

// Import for switching between activities
import android.content.Intent

// Android lifecycle bundle
import android.os.Bundle

// Used for displaying logs in Logcat
import android.util.Log

// UI components
import android.widget.Button
import android.widget.EditText

// Enables edge-to-edge display
import androidx.activity.enableEdgeToEdge

// Base activity class
import androidx.appcompat.app.AppCompatActivity

// Utility for handling window insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

// Firebase Realtime Database
import com.google.firebase.database.FirebaseDatabase

/**
 * Main screen of the application.
 *
 * This activity allows the user to:
 * - Enter chart data
 * - Save data to Firebase
 * - Open the chart display screen
 */
class MainActivity : AppCompatActivity() {

    // Input field for category name
    private lateinit var etCategory: EditText

    // Input field for numeric value
    private lateinit var etValue: EditText

    // Button used to add/save data
    private lateinit var btnAdd: Button

    // Button used to open chart screen
    private lateinit var btnView: Button

    // Firebase reference pointing to "chartData" node
    private val dbRef = FirebaseDatabase.getInstance().getReference("chartData")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables drawing behind system bars
        enableEdgeToEdge()

        // Set activity layout
        setContentView(R.layout.activity_main)

        // Connect XML views to Kotlin variables
        etCategory = findViewById(R.id.etCategory)
        etValue = findViewById(R.id.etValue)
        btnAdd = findViewById(R.id.btnAdd)
        btnView = findViewById(R.id.btnViewCharts)

        /**
         * Add Button Click Listener
         *
         * Saves user input to Firebase Realtime Database.
         */
        btnAdd.setOnClickListener {

            // Read category text from input field
            val category = etCategory.text.toString()

            // Convert input value to Float
            // If conversion fails, default to 0f
            val value = etValue.text.toString().toFloatOrNull() ?: 0f

            // Generate unique Firebase ID
            val id = dbRef.push().key!!

            // Create ChartData object
            val data = ChartData(category, value)

            // Save data into Firebase under generated ID
            dbRef.child(id).setValue(data)

                // Called if save operation succeeds
                .addOnSuccessListener {
                    Log.d("FIREBASE", "Saved successfully")
                }

                // Called if save operation fails
                .addOnFailureListener {
                    Log.e("FIREBASE", "Failed: ${it.message}")
                }

            // Clear category input field
            etCategory.text.clear()

            // Clear value input field
            etValue.text.clear()
        }

        /**
         * View Charts Button Click Listener
         *
         * Opens ChartActivity screen.
         */
        btnView.setOnClickListener {

            // Navigate to ChartActivity
            startActivity(
                Intent(this, ChartActivity::class.java)
            )
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            // Get dimensions of system bars
            val systemBars =
                insets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding so content is not hidden
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }
    }
}