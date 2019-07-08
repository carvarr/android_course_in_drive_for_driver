package curso.carlos.indrivefordriver

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myWebView = WebView(this)
        setContentView(myWebView)

        myWebView.webViewClient = WebViewClient()
        myWebView.loadUrl("http://www.facebook.com")
    }
}
