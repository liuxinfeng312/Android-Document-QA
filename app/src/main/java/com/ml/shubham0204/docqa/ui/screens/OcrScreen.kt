package com.ml.shubham0204.docqa.ui.screens

import android.content.Context
import android.content.pm.PackageManager

import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.ml.shubham0204.docqa.domain.ocr.OcrManager
import com.ml.shubham0204.docqa.domain.search.ApiClient
import com.ml.shubham0204.docqa.domain.search.QueryRequest
import com.ml.shubham0204.docqa.domain.search.SearchResponse
import org.json.JSONArray
import java.io.File
import java.io.InputStream



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OcrScreen(onBackClick: (() -> Unit), ocrManager: OcrManager) {
    val context = LocalContext.current
    var searchText by remember { mutableStateOf("") }
    var searchResult by remember { mutableStateOf<List<String>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Manage captured image URI
    val imageUri = remember { mutableStateOf<Uri?>(null) }

    // Runtime permissions
    val requiredPermissions = listOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (!allGranted) {
            errorMessage = "需要相机和存储权限才能继续"
        }
    }

    // Camera launcher for taking pictures
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri.value?.let { uri ->
                processImageForText(context, uri, ocrManager) { extractedText ->
                    searchText = extractedText.toString()
                    searchDocuments(context, extractedText.toString()) { results ->
                        searchResult = results
                        if (results.isEmpty()) {
                            errorMessage = "未找到匹配的题目。"
                        } else {
                            errorMessage = null
                        }
                    }
                }
            }
        } else {
            errorMessage = "拍照失败，请重试。"
        }
    }

    // Create URI for the image
    val file = remember {
        File(context.cacheDir, "captured_image.jpg").apply {
            createNewFile()
            deleteOnExit()
        }
    }
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    imageUri.value = uri

    // Check and request permissions when Composable is displayed
    LaunchedEffect(Unit) {
        permissionLauncher.launch(requiredPermissions.toTypedArray())
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("题库搜索") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search Input Field
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), MaterialTheme.shapes.small)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp),
                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            searchDocuments(context, searchText) { results ->
                                searchResult = results
                                if (results.isEmpty()) {
                                    errorMessage = "未找到匹配的题目。"
                                } else {
                                    errorMessage = null
                                }
                            }
                        }
                    ),
                    singleLine = true
                )

                IconButton(onClick = {
                    if (requiredPermissions.all {
                            androidx.core.content.ContextCompat.checkSelfPermission(
                                context,
                                it
                            ) == PackageManager.PERMISSION_GRANTED
                        }) {
                        cameraLauncher.launch(uri)
                    } else {
                        errorMessage = "需要相机和存储权限才能继续"
                    }
                }) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = "OCR")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display Search Results
            if (searchResult.isNotEmpty()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    searchResult.forEach { result ->
                        Text(
                            text = result,
                            modifier = Modifier.padding(8.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Divider(color = Color.Gray, thickness = 1.dp)
                    }
                }
            }

            // Display Error Message
            errorMessage?.let {
                Text(
                    text = it,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Red
                )
            }
        }
    }
}

/**
 * Replace local file search with API call to fetch search results.
 */
fun searchDocuments(context: Context, keyword: String, onResult: (List<String>) -> Unit) {
    if (keyword.isBlank()) {
        onResult(emptyList())
        return
    }

    val api = ApiClient.milvusApi // Retrofit API client

    val request = QueryRequest(
        collection_name = "demo_collection", // Replace with your collection name
        query_text = keyword,
        limit = 5 // Limit the number of results
    )

    api.searchDocuments(request).enqueue(object : retrofit2.Callback<SearchResponse> {
        override fun onResponse(call: retrofit2.Call<SearchResponse>, response: retrofit2.Response<SearchResponse>) {
            if (response.isSuccessful && response.body() != null) {
                val flattenedResults = response.body()?.results?.flatten() ?: emptyList()
                val results = flattenedResults.map { result ->
                    "题目: ${result.entity.text}\nsubject: ${result.entity.subject}\n相似度: ${result.distance}"
                }
                onResult(results)
            } else {
                Log.e("Milvus", "Search Failed: ${response.code()}")
                onResult(emptyList())
            }
        }

        override fun onFailure(call: retrofit2.Call<SearchResponse>, t: Throwable) {
            Log.e("Milvus", "Search Error: ${t.message}")
            onResult(emptyList())
        }
    })
}

fun processImageForText(
    context: Context,
    imageUri: Uri,
    ocrManager: OcrManager,  // Pass OcrManager instance
    onResult: (String) -> Unit // Pass a callback that receives the extracted text
) {
    try {
        // Convert the Uri to Bitmap
        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
//        val inputStream = context.assets.open("wb.png") // 图片文件位于 src/main/assets/test2.png
//        val bitmap = BitmapFactory.decodeStream(inputStream)//
        // Run OCR with PaddleOCR
        ocrManager.runOcr(bitmap, onSuccess = { result ->
            // Extract text from the result
            val extractedText = result.simpleText.replace("\n", " ").replace(Regex("\\s+"), " ").trim() // This is the recognized text from OCR
            println("=========ocr result========")
            println(extractedText)
            onResult(extractedText)  // Pass the extracted text to the callback
        }, onError = { error ->
            // Log the error if OCR fails
            Log.e("OCR", "OCR failed: ${error.message}")
            onResult("")  // If OCR fails, pass an empty string
        })
    } catch (e: Exception) {
        // Handle any exceptions that occur during the image processing
        Log.e("OCR", "Error processing image: ${e.message}", e)
        onResult("")  // Pass an empty string if an error occurs
    }
}