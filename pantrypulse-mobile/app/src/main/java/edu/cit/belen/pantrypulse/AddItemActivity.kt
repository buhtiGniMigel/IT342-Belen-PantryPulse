package edu.cit.belen.pantrypulse

import android.Manifest
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private lateinit var tvAddTitle: TextView
    private lateinit var etItemName: EditText
    private lateinit var spCategory: Spinner
    private lateinit var etQuantity: EditText
    private lateinit var etExpiryDate: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnSave: Button
    private lateinit var btnDelete: Button
    private lateinit var btnScanExpiry: Button
    private lateinit var ivExpiryPhoto: ImageView
    private lateinit var pbScanProgress: ProgressBar

    private var editingItem: PantryItem? = null
    private lateinit var repository: InventoryRepository

    private val categories = arrayOf("Dairy", "Produce", "Meat", "Pantry", "Bakery", "Beverages", "Snacks", "Others")
    private val calendar = Calendar.getInstance()

    // URI for full-resolution camera photo
    private var cameraPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null

    // ML Kit recognizer
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    // ── Activity Result Launchers ──────────────────────────────────────────────

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchCameraCapture()
        else Toast.makeText(this, "Camera permission is needed to take photos", Toast.LENGTH_SHORT).show()
    }

    private val galleryPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) launchGalleryPicker()
        else Toast.makeText(this, "Storage permission is needed to pick photos", Toast.LENGTH_SHORT).show()
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            cameraPhotoUri?.let { uri ->
                val bitmap = loadBitmapFromUri(uri)
                bitmap?.let { processImageForExpiry(it) }
            }
        }
    }

    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val uri = result.data?.data ?: return@registerForActivityResult
            val bitmap = loadBitmapFromUri(uri)
            bitmap?.let { processImageForExpiry(it) }
        }
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        repository = InventoryRepository(this)

        // Bind views
        tvAddTitle    = findViewById(R.id.tvAddTitle)
        etItemName    = findViewById(R.id.etItemName)
        spCategory    = findViewById(R.id.spCategory)
        etQuantity    = findViewById(R.id.etQuantity)
        etExpiryDate  = findViewById(R.id.etExpiryDate)
        btnCancel     = findViewById(R.id.btnCancel)
        btnSave       = findViewById(R.id.btnSave)
        btnDelete     = findViewById(R.id.btnDelete)
        btnScanExpiry = findViewById(R.id.btnScanExpiry)
        ivExpiryPhoto = findViewById(R.id.ivExpiryPhoto)
        pbScanProgress = findViewById(R.id.pbScanProgress)

        // Category spinner
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories)
        spCategory.adapter = spinnerAdapter

        // Date picker on expiry field tap
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, day)
            etExpiryDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.time))
        }
        etExpiryDate.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Scan button
        btnScanExpiry.setOnClickListener { showImageSourceChooser() }

        // Edit mode
        editingItem = intent.getSerializableExtra("EXTRA_ITEM") as? PantryItem
        if (editingItem != null) {
            tvAddTitle.text = "Edit Item"
            etItemName.setText(editingItem!!.itemName)
            etQuantity.setText(editingItem!!.quantity.toString())
            etExpiryDate.setText(editingItem!!.expiryDate)
            val idx = categories.indexOf(editingItem!!.category)
            if (idx >= 0) spCategory.setSelection(idx)
            btnDelete.visibility = View.VISIBLE
        }

        btnCancel.setOnClickListener { finish() }

        btnSave.setOnClickListener {
            val name       = etItemName.text.toString().trim()
            val category   = spCategory.selectedItem.toString()
            val quantityStr = etQuantity.text.toString().trim()
            val expiryDate = etExpiryDate.text.toString().trim()

            if (name.isEmpty() || quantityStr.isEmpty() || expiryDate.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val quantity = quantityStr.toDoubleOrNull()
            if (quantity == null || quantity <= 0) {
                Toast.makeText(this, "Please enter a valid quantity", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnSave.isEnabled = false
            Thread {
                val success = if (editingItem != null) {
                    repository.updateItem(editingItem!!.id, name, category, quantity, expiryDate) != null
                } else {
                    repository.addItem(name, category, quantity, expiryDate) != null
                }
                runOnUiThread {
                    btnSave.isEnabled = true
                    if (success) {
                        Toast.makeText(this, "Item saved successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to save item. Cache updated locally.", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }.start()
        }

        btnDelete.setOnClickListener {
            if (editingItem == null) return@setOnClickListener
            btnDelete.isEnabled = false
            Thread {
                val success = repository.deleteItem(editingItem!!.id)
                runOnUiThread {
                    btnDelete.isEnabled = true
                    if (success) {
                        Toast.makeText(this, "Item deleted successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to delete item from server", Toast.LENGTH_SHORT).show()
                    }
                }
            }.start()
        }
    }

    // ── Image Source Chooser ───────────────────────────────────────────────────

    private fun showImageSourceChooser() {
        AlertDialog.Builder(this)
            .setTitle("Scan Expiry Label")
            .setMessage("Choose how to capture the expiration label:")
            .setPositiveButton("📷 Camera") { _, _ -> checkCameraAndCapture() }
            .setNegativeButton("🖼 Gallery") { _, _ -> checkGalleryAndPick() }
            .setNeutralButton("Cancel", null)
            .show()
    }

    // ── Camera Flow ────────────────────────────────────────────────────────────

    private fun checkCameraAndCapture() {
        when {
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> launchCameraCapture()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCameraCapture() {
        val photoFile = createImageFile() ?: run {
            Toast.makeText(this, "Could not create image file", Toast.LENGTH_SHORT).show()
            return
        }
        cameraPhotoUri = FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            photoFile
        )
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri)
        }
        try {
            cameraLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
            val storageDir = externalCacheDir ?: cacheDir
            File.createTempFile("EXPIRY_${timeStamp}_", ".jpg", storageDir).also {
                currentPhotoPath = it.absolutePath
            }
        } catch (e: IOException) {
            null
        }
    }

    // ── Gallery Flow ───────────────────────────────────────────────────────────

    private fun checkGalleryAndPick() {
        val permission = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> launchGalleryPicker()
            else -> galleryPermissionLauncher.launch(permission)
        }
    }

    private fun launchGalleryPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
            type = "image/*"
        }
        galleryLauncher.launch(intent)
    }

    // ── Image Loading ──────────────────────────────────────────────────────────

    private fun loadBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    // ── ML Kit OCR + Date Parsing ──────────────────────────────────────────────

    private fun processImageForExpiry(bitmap: Bitmap) {
        // Show thumbnail
        ivExpiryPhoto.setImageBitmap(bitmap)
        ivExpiryPhoto.visibility = View.VISIBLE
        pbScanProgress.visibility = View.VISIBLE
        btnScanExpiry.isEnabled = false

        val image = InputImage.fromBitmap(bitmap, 0)
        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                pbScanProgress.visibility = View.GONE
                btnScanExpiry.isEnabled = true

                val rawText = visionText.text
                val extracted = extractExpiryDate(rawText)
                if (extracted != null) {
                    etExpiryDate.setText(extracted)
                    Toast.makeText(this, "✅ Expiry date detected: $extracted", Toast.LENGTH_LONG).show()
                } else {
                    showDateNotFoundDialog(rawText)
                }
            }
            .addOnFailureListener { e ->
                pbScanProgress.visibility = View.GONE
                btnScanExpiry.isEnabled = true
                Toast.makeText(this, "OCR failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Smart date extractor — tries to find an expiry/best-before date in OCR text.
     * Handles a wide variety of label formats:
     *   "EXP 12/2026", "Best Before: 31 DEC 2025", "BB: 2025-12-31",
     *   "USE BY 06/25", "12/25", "31-DEC-25", etc.
     * Returns date in "yyyy-MM-dd" format, or null if nothing found.
     */
    private fun extractExpiryDate(text: String): String? {
        val upper = text.uppercase(Locale.US).replace('\n', ' ')

        // Patterns in priority order
        val patterns = listOf(
            // yyyy-MM-dd  or  yyyy/MM/dd
            Regex("""(\d{4})[-/](\d{1,2})[-/](\d{1,2})"""),
            // DD MMM YYYY  e.g. "31 DEC 2025" or "31-DEC-2025"
            Regex("""(\d{1,2})[\s\-/](JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)[A-Z]*[\s\-/](\d{4})"""),
            // MMM YYYY  e.g. "DEC 2025"
            Regex("""(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)[A-Z]*[\s\-/](\d{4})"""),
            // DD MMM YY  e.g. "31 DEC 25"
            Regex("""(\d{1,2})[\s\-/](JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)[A-Z]*[\s\-/](\d{2})(?!\d)"""),
            // MMM YY  e.g. "DEC 25"
            Regex("""(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)[A-Z]*[\s\-/](\d{2})(?!\d)"""),
            // MM/YYYY  e.g. "12/2026"
            Regex("""(\d{1,2})[/\-](\d{4})"""),
            // MM/YY  e.g. "12/25" or "12-25"
            Regex("""(\d{1,2})[/\-](\d{2})(?!\d)"""),
            // YYYY/MM  e.g. "2025/12"
            Regex("""(\d{4})[/\-](\d{1,2})""")
        )

        val monthMap = mapOf(
            "JAN" to 1, "FEB" to 2, "MAR" to 3, "APR" to 4, "MAY" to 5, "JUN" to 6,
            "JUL" to 7, "AUG" to 8, "SEP" to 9, "OCT" to 10, "NOV" to 11, "DEC" to 12
        )

        // Helper to expand 2-digit year
        fun expandYear(yy: Int): Int = if (yy < 50) 2000 + yy else 1900 + yy

        // Try context-aware: look near keywords first
        val keywordZones = Regex(
            """(EXP(?:IRY|IRES?)?|BEST BEFORE|BB|USE BY|SELL BY|USE BEFORE|EXPIRY DATE?)\s*:?\s*(.{0,30})"""
        ).findAll(upper).mapNotNull { it.groupValues[2] }.toList()

        val searchTexts = keywordZones + listOf(upper) // try keyword zones first, then full text

        for (source in searchTexts) {
            // yyyy-MM-dd / yyyy/MM/dd
            patterns[0].find(source)?.let { m ->
                val y = m.groupValues[1].toInt()
                val mo = m.groupValues[2].toInt()
                val d = m.groupValues[3].toInt()
                if (mo in 1..12 && d in 1..31 && y >= 2020)
                    return "%04d-%02d-%02d".format(y, mo, d)
            }

            // DD MMM YYYY
            patterns[1].find(source)?.let { m ->
                val d = m.groupValues[1].toInt()
                val mo = monthMap[m.groupValues[2].take(3)] ?: return@let
                val y = m.groupValues[3].toInt()
                if (d in 1..31 && y >= 2020)
                    return "%04d-%02d-%02d".format(y, mo, d)
            }

            // MMM YYYY
            patterns[2].find(source)?.let { m ->
                val mo = monthMap[m.groupValues[1].take(3)] ?: return@let
                val y = m.groupValues[2].toInt()
                if (y >= 2020) return "%04d-%02d-01".format(y, mo)
            }

            // DD MMM YY
            patterns[3].find(source)?.let { m ->
                val d = m.groupValues[1].toInt()
                val mo = monthMap[m.groupValues[2].take(3)] ?: return@let
                val y = expandYear(m.groupValues[3].toInt())
                if (d in 1..31 && y >= 2020)
                    return "%04d-%02d-%02d".format(y, mo, d)
            }

            // MMM YY
            patterns[4].find(source)?.let { m ->
                val mo = monthMap[m.groupValues[1].take(3)] ?: return@let
                val y = expandYear(m.groupValues[2].toInt())
                if (y >= 2020) return "%04d-%02d-01".format(y, mo)
            }

            // MM/YYYY
            patterns[5].find(source)?.let { m ->
                val mo = m.groupValues[1].toInt()
                val y = m.groupValues[2].toInt()
                if (mo in 1..12 && y >= 2020) return "%04d-%02d-01".format(y, mo)
            }

            // MM/YY
            patterns[6].find(source)?.let { m ->
                val mo = m.groupValues[1].toInt()
                val y = expandYear(m.groupValues[2].toInt())
                if (mo in 1..12 && y >= 2020) return "%04d-%02d-01".format(y, mo)
            }

            // YYYY/MM
            patterns[7].find(source)?.let { m ->
                val y = m.groupValues[1].toInt()
                val mo = m.groupValues[2].toInt()
                if (mo in 1..12 && y >= 2020) return "%04d-%02d-01".format(y, mo)
            }
        }

        return null
    }

    private fun showDateNotFoundDialog(rawText: String) {
        val preview = if (rawText.length > 200) rawText.take(200) + "…" else rawText
        AlertDialog.Builder(this)
            .setTitle("No Date Found")
            .setMessage(
                "Could not automatically detect an expiry date.\n\n" +
                "Text detected from label:\n\"$preview\"\n\n" +
                "Please enter the date manually."
            )
            .setPositiveButton("OK", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        recognizer.close()
    }
}
