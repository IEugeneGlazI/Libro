package com.example.libro

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.libro.data.JsonHelper
import com.example.libro.databinding.ActivityAddBookBinding
import com.example.libro.network.RetrofitClient
import com.google.android.material.chip.Chip
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class AddBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBookBinding
    private lateinit var cameraExecutor: ExecutorService
    private var barcodeScanned = false
    private var selectedImageUri: Uri? = null
    private var remoteCoverUrl: String? = null

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.bookCoverPreview.load(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()
        setupUI()

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.toggleMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                if (checkedId == R.id.btn_manual) {
                    binding.manualInputLayout.visibility = View.VISIBLE
                    binding.scanLayout.visibility = View.GONE
                } else {
                    binding.manualInputLayout.visibility = View.GONE
                    binding.scanLayout.visibility = View.VISIBLE
                    barcodeScanned = false // Allow scanning again
                }
            }
        }

        binding.btnSelectImage.setOnClickListener { imagePickerLauncher.launch("image/*") }

        val genres = listOf("Классика", "Роман", "Фэнтези", "Детектив", "Научная фантастика", "Программирование", "Бизнес", "Психология", "История", "Биография", "Поэзия", "Драма")
        for (genre in genres) {
            val chip = Chip(this).apply { text = genre; isCheckable = true }
            binding.genreChipGroup.addView(chip)
        }

        binding.btnCancel.setOnClickListener { finish() }
        binding.btnAdd.setOnClickListener { saveBook() }
    }

    private fun saveBook() {
        val title = binding.inputTitle.text.toString()
        if (title.isBlank()) {
            Toast.makeText(this, "Название не может быть пустым", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedGenres = binding.genreChipGroup.children
            .filter { (it as Chip).isChecked }
            .map { (it as Chip).text.toString() }
            .toList()

        val coverUrl = selectedImageUri?.toString() ?: remoteCoverUrl
        val shelfName = intent.getStringExtra("SHELF_NAME")
        val shelfNumber = binding.inputShelfNumber.text.toString().takeIf { it.isNotBlank() }
        val placeNumber = binding.inputPlaceNumber.text.toString().takeIf { it.isNotBlank() }
        
        // Получаем location шкафа по его названию
        val shelfLocation = shelfName?.let { name ->
            val shelves = JsonHelper.loadShelves(this)
            shelves.find { it.name == name }?.location
        }

        val newBook = Book(
            title = title,
            author = binding.inputAuthor.text.toString(),
            year = binding.inputYear.text.toString().toIntOrNull() ?: 0,
            publisher = binding.inputPublisher.text.toString(),
            description = binding.inputDescription.text.toString(),
            isbn = binding.inputIsbn.text.toString(),
            tags = selectedGenres,
            status = "Не начата",
            rating = 0,
            commentCount = 0,
            bookmarkCount = 0,
            coverUrl = coverUrl,
            shelfName = shelfName,
            shelfLocation = shelfLocation,
            shelfNumber = shelfNumber,
            placeNumber = placeNumber
        )

        val books = JsonHelper.loadBooks(this)
        books.add(newBook)
        JsonHelper.saveBooks(this, books)

        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }

            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, BarcodeAnalyzer { barcode ->
                        if (!barcodeScanned) {
                            barcodeScanned = true
                            runOnUiThread {
                                fetchBookData(barcode)
                            }
                        }
                    })
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer)
            } catch(exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun fetchBookData(isbn: String) {
        binding.scanStatusText.text = "Поиск книги..."
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.apiService.getBookByISBN(isbn)
                val bookItem = response.items?.firstOrNull()
                val volumeInfo = bookItem?.volumeInfo

                if (volumeInfo != null) {
                    binding.inputTitle.setText(volumeInfo.title)
                    binding.inputAuthor.setText(volumeInfo.authors?.joinToString(", "))
                    binding.inputYear.setText(volumeInfo.publishedDate?.substring(0, 4))
                    binding.inputDescription.setText(volumeInfo.description)
                    binding.inputIsbn.setText(isbn)

                    volumeInfo.imageLinks?.thumbnail?.let {
                        remoteCoverUrl = it.replace("http://", "https://")
                        binding.bookCoverPreview.load(remoteCoverUrl)
                    }

                    Toast.makeText(this@AddBookActivity, "Книга найдена!", Toast.LENGTH_SHORT).show()
                    binding.toggleMode.check(R.id.btn_manual)
                } else {
                    Toast.makeText(this@AddBookActivity, "Книга не найдена", Toast.LENGTH_SHORT).show()
                    binding.scanStatusText.text = "Книга не найдена. Попробуйте еще раз."
                    barcodeScanned = false // Allow scanning again
                }
            } catch (e: Exception) {
                Log.e(TAG, "Network request failed", e)
                val errorMessage = when (e) {
                    is HttpException -> "Ошибка сервера: ${e.code()}"
                    is UnknownHostException -> "Ошибка сети: Проверьте подключение к интернету."
                    else -> "Произошла неизвестная ошибка."
                }
                binding.scanStatusText.text = errorMessage
                Toast.makeText(this@AddBookActivity, errorMessage, Toast.LENGTH_LONG).show()
                barcodeScanned = false // Allow scanning again
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "AddBookActivity"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    private class BarcodeAnalyzer(private val listener: (barcode: String) -> Unit) : ImageAnalysis.Analyzer {
        private val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_EAN_13, Barcode.FORMAT_UPC_A)
            .build()
        private val scanner = BarcodeScanning.getClient(options)

        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            barcode.rawValue?.let { listener(it) }
                        }
                    }
                    .addOnFailureListener { e -> Log.e(TAG, "Barcode analysis failure.", e) }
                    .addOnCompleteListener { imageProxy.close() }
            }
        }
    }
}
