package com.example.chartonline

// Import for handling colors
import android.graphics.Color

// Android activity lifecycle bundle
import android.os.Bundle

// AppCompat support activity
import androidx.appcompat.app.AppCompatActivity

// Utility for handling window insets
import androidx.core.view.ViewCompat

// Enables drawing behind system bars (edge-to-edge UI)
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat.enableEdgeToEdge

// Window inset compatibility support
import androidx.core.view.WindowInsetsCompat

// MPAndroidChart PieChart and BarChart components
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart

// MPAndroidChart data classes
import com.github.mikephil.charting.data.*

// Formatter used for custom X-axis labels
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

// Firebase Realtime Database imports
import com.google.firebase.database.*

/**
 * Activity responsible for displaying:
 * - A Pie Chart
 * - A Bar Chart
 *
 * Data is loaded from Firebase Realtime Database.
 */
class ChartActivity : AppCompatActivity() {

    // Pie chart view
    private lateinit var pieChart: PieChart

    // Bar chart view
    private lateinit var barChart: BarChart

    // Firebase reference pointing to "chartData" node
    private val dbRef = FirebaseDatabase.getInstance().getReference("chartData")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enables edge-to-edge display
        enableEdgeToEdge()

        // Sets the layout for this activity
        setContentView(R.layout.activity_chart)

        // Connect XML chart views to Kotlin variables
        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)

        // Load chart data from Firebase
        loadData()

        // Handles padding for system bars (status/navigation bars)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    /**
     * Reads chart data from Firebase Realtime Database.
     */
    private fun loadData() {

        // Listen for real-time updates from Firebase
        dbRef.addValueEventListener(object : ValueEventListener {

            /**
             * Called whenever data changes in Firebase.
             */
            override fun onDataChange(snapshot: DataSnapshot) {

                // Stores data for Pie Chart
                val pieEntries = ArrayList<PieEntry>()

                // Stores data for Bar Chart
                val barEntries = ArrayList<BarEntry>()

                // Stores category labels for Bar Chart X-axis
                val labels = ArrayList<String>()

                // X-axis index tracker for bar chart
                var index = 0f

                // Loop through all children inside "chartData"
                for (dataSnap in snapshot.children) {

                    // Convert snapshot into ChartData object
                    val item = dataSnap.getValue(ChartData::class.java)

                    // Ensure object is not null
                    if (item != null){

                        // Add entry to Pie Chart
                        pieEntries.add(
                            PieEntry(item.value, item.category)
                        )

                        // Add entry to Bar Chart
                        barEntries.add(
                            BarEntry(index, item.value)
                        )

                        // Add category label
                        labels.add(item.category)

                        // Increment index for next bar
                        index++
                    }
                }

                // Display Pie Chart
                setupPieChart(pieEntries)

                // Display Bar Chart
                setupBarChart(barEntries, labels)
            }

            /**
             * Called if Firebase read operation fails.
             */
            override fun onCancelled(error: DatabaseError) {
                // Error handling can be added here
            }
        })
    }

    /**
     * Configures and displays the Pie Chart.
     */
    private fun setupPieChart(entries: List<PieEntry>) {

        // Create dataset for pie chart
        val dataSet = PieDataSet(entries, "Categories")

        // Set chart slice colors
        dataSet.colors = listOf(
            Color.RED,
            Color.BLUE,
            Color.GREEN,
            Color.MAGENTA,
            Color.CYAN,
            Color.YELLOW
        )

        // Style for value text
        dataSet.valueTextSize = 14f
        dataSet.valueTextColor = Color.BLACK

        // Create PieData object
        val data = PieData(dataSet)

        // Assign data to chart
        pieChart.data = data

        // Disable default chart description
        pieChart.description.isEnabled = false

        // Text shown in center of pie chart
        pieChart.centerText = "Distribution"

        // Display values as percentages
        pieChart.setUsePercentValues(true)

        // Animate chart vertically
        pieChart.animateY(1200)

        // Refresh chart
        pieChart.invalidate()
    }

    /**
     * Configures and displays the Bar Chart.
     */
    private fun setupBarChart(
        entries: List<BarEntry>,
        labels: List<String>
    ) {

        // Create dataset for bar chart
        val dataSet = BarDataSet(entries, "Values")

        // Set bar color
        dataSet.color = Color.BLUE

        // Set value text size
        dataSet.valueTextSize = 12f

        // Create BarData object
        val data = BarData(dataSet)

        // Set width of bars
        data.barWidth = 0.9f

        // Assign data to chart
        barChart.data = data

        // Adjust bars to fit chart properly
        barChart.setFitBars(true)

        // Disable default description text
        barChart.description.isEnabled = false

        // Set category labels on X-axis
        barChart.xAxis.valueFormatter =
            IndexAxisValueFormatter(labels)

        // Ensure labels increment correctly
        barChart.xAxis.granularity = 1f

        // Display X-axis labels
        barChart.xAxis.setDrawLabels(true)

        // Animate chart vertically
        barChart.animateY(1200)

        // Refresh chart
        barChart.invalidate()
    }
}