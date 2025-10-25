package com.example.curanova

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.curanova.ui.theme.CuraNovaTheme

class MainActivity : ComponentActivity() {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val fileChooserLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (filePathCallback == null) return@registerForActivityResult
        val uris = WebChromeClient.FileChooserParams.parseResult(result.resultCode, result.data)
        filePathCallback?.onReceiveValue(uris)
        filePathCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CuraNovaTheme {
                WebViewScreen(
                    url = "https://curanova-magic.lovable.app/",
                    onShowFileChooser = { callback, params ->
                        filePathCallback = callback
                        val intent = params.createIntent()
                        try {
                            fileChooserLauncher.launch(intent)
                        } catch (e: Exception) {
                            Toast.makeText(this, "No file picker found", Toast.LENGTH_LONG).show()
                            filePathCallback = null
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun WebViewScreen(
    url: String,
    modifier: Modifier = Modifier,
    onShowFileChooser: (ValueCallback<Array<Uri>>, WebChromeClient.FileChooserParams) -> Unit
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                webViewClient = WebViewClient()
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true

                webChromeClient = object : WebChromeClient() {
                    override fun onShowFileChooser(
                        webView: WebView?,
                        filePathCallback: ValueCallback<Array<Uri>>,
                        fileChooserParams: FileChooserParams?
                    ): Boolean {
                        fileChooserParams?.let { onShowFileChooser(filePathCallback, it) }
                        return true
                    }
                }

                setDownloadListener { downloadUrl, userAgent, contentDisposition, mimetype, _ ->
                    val request = DownloadManager.Request(Uri.parse(downloadUrl))
                    request.setMimeType(mimetype)
                    request.addRequestHeader("User-Agent", userAgent)
                    request.setDescription("Downloading summary...")
                    val fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimetype)
                    request.setTitle(fileName)
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    downloadManager.enqueue(request)
                    Toast.makeText(context, "Downloading Summary...", Toast.LENGTH_LONG).show()
                }

                loadUrl(url)
            }
        }
    )
}
