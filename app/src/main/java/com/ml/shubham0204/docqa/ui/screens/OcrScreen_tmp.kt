//package com.ml.shubham0204.docqa.ui.screens
//
//import android.content.Context
//import android.graphics.BitmapFactory
//import android.net.Uri
//import android.provider.MediaStore
//import android.util.Log
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.text.BasicTextField
//import androidx.compose.foundation.text.KeyboardActions
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.CameraAlt
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.*
//import androidx.compose.ui.text.input.ImeAction
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.ml.shubham0204.docqa.domain.ocr.OcrManager
//import org.json.JSONArray
//import java.io.InputStream
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OcrScreen(onBackClick: (() -> Unit),ocrManager:OcrManager) {
////    val ocrManager=
//
//    val context = LocalContext.current
//    var searchText by remember { mutableStateOf("") }
//    var searchResult by remember { mutableStateOf<List<AnnotatedString>>(emptyList()) }
//    var errorMessage by remember { mutableStateOf<String?>(null) }
//
//    // OCR Image Launcher
//    val ocrLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            processImageForText(context, it,ocrManager) { extractedText ->
//                searchText = extractedText.toString()
//                searchResult = searchQuestionsWithHighlight(context, extractedText.toString())
//                if (searchResult.isEmpty()) {
//                    errorMessage = "未找到匹配的题目。"
//                } else {
//                    errorMessage = null
//                }
//            }
//        }
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("题库搜索") },
//                navigationIcon = {
//                    IconButton(onClick = onBackClick) {
//                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .padding(16.dp)
//                .fillMaxSize(),
//            verticalArrangement = Arrangement.Top,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Search Input Field
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .background(Color(0xFFF5F5F5), MaterialTheme.shapes.small)
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                BasicTextField(
//                    value = searchText,
//                    onValueChange = { searchText = it },
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(16.dp),
//                    textStyle = TextStyle(fontSize = 16.sp, color = Color.Black),
//                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
//                    keyboardActions = KeyboardActions(
//                        onSearch = {
//                            searchResult = searchQuestionsWithHighlight(context, searchText)
//                            if (searchResult.isEmpty()) {
//                                errorMessage = "未找到匹配的题目。"
//                            } else {
//                                errorMessage = null
//                            }
//                        }
//                    ),
//                    singleLine = true
//                )
//
//                IconButton(onClick = { ocrLauncher.launch("image/*") }) {
//                    Icon(Icons.Filled.CameraAlt, contentDescription = "OCR")
//                }
//            }
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // Display Search Results
//            if (searchResult.isNotEmpty()) {
//                Column(modifier = Modifier.fillMaxWidth()) {
//                    searchResult.forEach { annotatedText ->
//                        Text(
//                            text = annotatedText,
//                            modifier = Modifier.padding(8.dp),
//                            style = MaterialTheme.typography.bodyLarge
//                        )
//                        Divider(color = Color.Gray, thickness = 1.dp)
//                    }
//                }
//            }
//
//            // Display Error Message
//            errorMessage?.let {
//                Text(
//                    text = it,
//                    modifier = Modifier.padding(16.dp),
//                    style = MaterialTheme.typography.bodyLarge,
//                    color = Color.Red
//                )
//            }
//        }
//    }
//}
//
//fun searchQuestionsWithHighlight(context: Context, keyword: String, threshold: Float = 0.6f): List<AnnotatedString> {
//    if (keyword.isBlank()) return emptyList()
//
//    // Tokenize keywords
//    val tokens = keyword.split(Regex("\\s+")).map { it.trim().toLowerCase() }
//
//    return try {
//        val inputStream: InputStream = context.assets.open("data.json")
//        val jsonString = inputStream.bufferedReader().use { it.readText() }
//        val jsonArray = JSONArray(jsonString)
//        val results = mutableListOf<AnnotatedString>()
//
//        for (i in 0 until jsonArray.length()) {
//            val questionObject = jsonArray.getJSONObject(i)
//            val question = questionObject.getString("question")
//            val answer = questionObject.getString("answer")
//
//            // List to store matched tokens and their positions
//            val matches = mutableListOf<Pair<Int, Int>>()
//
//            // Split the question into words
//            val questionWords = question.split(Regex("\\s+"))
//
//            // For each token (keyword), find its best match in the question
//            tokens.forEach { token ->
//                questionWords.forEach { word ->
//                    val similarity = calculateSimilarity(token, word)
//                    if (similarity >= threshold) {
//                        // Find the position of the matched word in the question
//                        val startIndex = question.indexOf(word, ignoreCase = true)
//                        if (startIndex != -1) {
//                            val endIndex = startIndex + word.length
//                            matches.add(startIndex to endIndex)
//                        }
//                    }
//                }
//            }
//
//            // If there are any matches, build the highlighted string
//            if (matches.isNotEmpty()) {
//                val annotatedString = buildAnnotatedString {
//                    var currentIndex = 0
//                    matches.sortedBy { it.first }.forEach { (startIndex, endIndex) ->
//                        append(question.substring(currentIndex, startIndex))
//                        withStyle(style = SpanStyle(color = Color.Red)) {
//                            append(question.substring(startIndex, endIndex))
//                        }
//                        currentIndex = endIndex
//                    }
//                    append(question.substring(currentIndex))
//                    append("\n答案：$answer")
//                }
//                results.add(annotatedString)
//            }
//        }
//        results
//    } catch (e: Exception) {
//        Log.e("SearchQuestion", "Error reading question bank: ${e.message}", e)
//        emptyList()
//    }
//}
//
///**
// * Calculate the similarity between two strings using Levenshtein Distance.
// * Returns a float between 0 and 1 where 1 means identical strings.
// */
//fun calculateSimilarity(str1: String, str2: String): Float {
//    val longer = if (str1.length > str2.length) str1 else str2
//    val shorter = if (str1.length > str2.length) str2 else str1
//    val longerLength = longer.length
//
//    if (longerLength == 0) return 1.0f
//
//    val editDistance = levenshteinDistance(longer, shorter)
//    return (longerLength - editDistance).toFloat() / longerLength
//}
//
///**
// * Calculate Levenshtein Distance between two strings.
// */
//fun levenshteinDistance(s1: String, s2: String): Int {
//    val m = s1.length
//    val n = s2.length
//    val dp = Array(m + 1) { IntArray(n + 1) }
//
//    for (i in 0..m) dp[i][0] = i
//    for (j in 0..n) dp[0][j] = j
//
//    for (i in 1..m) {
//        for (j in 1..n) {
//            val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
//            dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost)
//        }
//    }
//    return dp[m][n]
//}
//
//
//// Function to process image and extract text using PaddleOCR
//fun processImageForText(
//    context: Context,
//    imageUri: Uri,
//    ocrManager: OcrManager,  // Pass OcrManager instance
//    onResult: (String) -> Unit // Pass a callback that receives the extracted text
//) {
//    try {
//        // Convert the Uri to Bitmap
//        val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
////        val inputStream = context.assets.open("wb.png") // 图片文件位于 src/main/assets/test2.png
////        val bitmap = BitmapFactory.decodeStream(inputStream)//
//        // Run OCR with PaddleOCR
//        ocrManager.runOcr(bitmap, onSuccess = { result ->
//            // Extract text from the result
//            val extractedText = result.simpleText.replace("\n", " ").replace(Regex("\\s+"), " ").trim() // This is the recognized text from OCR
//            println("=========ocr result========")
//            println(extractedText)
//            onResult(extractedText)  // Pass the extracted text to the callback
//        }, onError = { error ->
//            // Log the error if OCR fails
//            Log.e("OCR", "OCR failed: ${error.message}")
//            onResult("")  // If OCR fails, pass an empty string
//        })
//    } catch (e: Exception) {
//        // Handle any exceptions that occur during the image processing
//        Log.e("OCR", "Error processing image: ${e.message}", e)
//        onResult("")  // Pass an empty string if an error occurs
//    }
//}
