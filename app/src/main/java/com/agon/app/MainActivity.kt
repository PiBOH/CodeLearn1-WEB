package com.agon.app

import android.os.Bundle
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.agon.app.ui.theme.AgonAppTheme

class MainActivity : ComponentActivity() {
    
    private var webView: WebView? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Handle back button for WebView navigation
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                webView?.let { wv ->
                    if (wv.canGoBack()) {
                        wv.goBack()
                    } else {
                        finish()
                    }
                } ?: finish()
            }
        })
        
        setContent {
            AgonAppTheme {
                var isLoading by remember { mutableStateOf(true) }
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.surface)
                        .windowInsetsPadding(WindowInsets.systemBars)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                webView = this
                                
                                // Configure WebView settings
                                settings.apply {
                                    javaScriptEnabled = true
                                    domStorageEnabled = true
                                    databaseEnabled = true
                                    loadWithOverviewMode = true
                                    useWideViewPort = true
                                    setSupportZoom(false)
                                    builtInZoomControls = false
                                    displayZoomControls = false
                                    mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                    allowFileAccess = true
                                    allowContentAccess = true
                                    cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                                    setSupportMultipleWindows(false)
                                    javaScriptCanOpenWindowsAutomatically = false
                                    blockNetworkImage = false
                                    loadsImagesAutomatically = true
                                }
                                
                                // Enable cookies
                                val wv = this@apply
                                CookieManager.getInstance().apply {
                                    setAcceptCookie(true)
                                    setAcceptThirdPartyCookies(wv, true)
                                }
                                
                                // Keep links inside WebView
                                webViewClient = object : WebViewClient() {
                                    override fun shouldOverrideUrlLoading(
                                        view: WebView?,
                                        request: WebResourceRequest?
                                    ): Boolean {
                                        val url = request?.url.toString()
                                        view?.loadUrl(url)
                                        return false
                                    }
                                    
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        isLoading = false
                                    }
                                }
                                
                                webChromeClient = object : WebChromeClient() {
                                    override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                        if (newProgress == 100) {
                                            isLoading = false
                                        }
                                    }
                                }
                                
                                // Hide scroll bars for cleaner look
                                isVerticalScrollBarEnabled = false
                                isHorizontalScrollBarEnabled = false
                                scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
                                
                                // Set background color
                                setBackgroundColor(android.graphics.Color.TRANSPARENT)
                                
                                // Load the website
                                loadUrl("https://code-learn1.vercel.app/")
                            }
                        },
                        update = { wv ->
                            webView = wv
                        }
                    )
                    
                    // Loading indicator
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
    
    override fun onDestroy() {
        webView?.destroy()
        webView = null
        super.onDestroy()
    }
}
