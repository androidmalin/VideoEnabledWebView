package name.cpr;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

/**
 * https://segmentfault.com/a/1190000007561455
 */
public class ExampleActivity extends AppCompatActivity {
    private VideoEnabledWebView mVideoWebView;
    private VideoEnabledWebChromeClient mWebChromeClient;
    private static final String URL = "https://3g.163.com/news/article/FCGL5PTP000189FH.html?clickfrom=channel2018_news_newsList#offset=0";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);
        mVideoWebView = findViewById(R.id.webView);

        // Initialize the VideoEnabledWebChromeClient and set event handlers
        View nonVideoLayout = findViewById(R.id.nonVideoLayout);
        ViewGroup videoLayout = findViewById(R.id.videoLayout);
        View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);
        mWebChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, mVideoWebView) {
            // See all available constructors...
            // Subscribe to standard events, such as onProgressChanged()...
            @Override
            public void onProgressChanged(WebView view, int progress) {
                // Your code...
            }
        };
        mWebChromeClient.setOnToggledFullscreen(toggledFullscreenCallback);

        mVideoWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mVideoWebView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        mVideoWebView.getSettings().setTextZoom(100);
        mVideoWebView.setWebChromeClient(mWebChromeClient);
        mVideoWebView.setWebViewClient(new InsideWebViewClient());
        mVideoWebView.loadUrl(URL);

    }

    private static class InsideWebViewClient extends WebViewClient {
        @Override
        // Force links to be opened inside WebView and not in Default Browser
        // Thanks http://stackoverflow.com/a/33681975/1815624
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private VideoEnabledWebChromeClient.ToggledFullscreenCallback toggledFullscreenCallback = new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
        @Override
        public void toggledFullscreen(boolean fullscreen) {
            // Your code to handle the full-screen change, for example showing and hiding the title bar. Example:
            WindowManager.LayoutParams attrs = getWindow().getAttributes();
            if (fullscreen) {
                attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getWindow().setAttributes(attrs);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            } else {
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
                attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
                getWindow().setAttributes(attrs);
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    };

    @Override
    public void onBackPressed() {
        // Notify the VideoEnabledWebChromeClient, and handle it ourselves if it doesn't handle it
        if (!mWebChromeClient.onBackPressed()) {
            if (mVideoWebView.canGoBack()) {
                mVideoWebView.goBack();
            } else {
                // Standard back button implementation (for example this could close the app)
                super.onBackPressed();
            }
        }
    }

}
