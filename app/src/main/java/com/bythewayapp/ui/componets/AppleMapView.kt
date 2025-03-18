package com.bythewayapp.ui.componets

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable

fun AppleMap(
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            settings.cacheMode = WebSettings.LOAD_NO_CACHE

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView, url: String) {
                    super.onPageFinished(view, url)
                    val js = """
                        addCircularMarker(0.48, 2.55, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTEcOYN57DOwU0mvfwhTxAQndvPHeKOnM67dg&s");
                    """
                    evaluateJavascript(js, null)
                }
            }
            addJavascriptInterface(MapKitJSInterface(context), "AndroidInterface")

            webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
                    Log.d("WebView", "${consoleMessage.message()} -- Line: ${consoleMessage.lineNumber()} of ${consoleMessage.sourceId()}")
                    return true
                }
            }
            loadUrl("file:///android_asset/mapkit.html")
        }
    }

    DisposableEffect(webView) {
        onDispose {
            webView.destroy()
        }
    }

    AndroidView (
        factory = { webView },
        modifier = Modifier.fillMaxSize().padding(0.dp)
    )
}

class MapKitJSInterface(private val context: Context) {
    @JavascriptInterface
    fun onMapClicked(latitude: Double, longitude: Double) {

    }

    @JavascriptInterface
    fun getDeviceLocation(): String {
        return "{\"latitude\": 48.8566, \"longitude\": 2.3522}"
    }
}
