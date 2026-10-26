package com.example.virtualbrowser;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText urlEditText, userAgentEditText;
    private Button launchButton;
    private Spinner engineSpinner;
    private List<String> massiveUserAgents;

    private final String[] browserEngines = {
        "Chrome Mobile Engine",
        "Firefox Mobile Engine",
        "Opera Mobile Engine",
        "Safari Mobile Engine",
        "Samsung Internet Engine"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        urlEditText = findViewById(R.id.urlEditText);
        userAgentEditText = findViewById(R.id.userAgentEditText);
        launchButton = findViewById(R.id.launchButton);
        engineSpinner = findViewById(R.id.engineSpinner);

        // 5000+ User Agents ni generate cheyadam
        generate5000PlusUserAgents();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, browserEngines);
        engineSpinner.setAdapter(adapter);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        // WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false;
                }
                return true;
            }
        });

        // WebChromeClient: Popups and multiple windows handler
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(MainActivity.this);
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        webView.loadUrl(request.getUrl().toString());
                        return true;
                    }
                });
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }
        });

        if (!massiveUserAgents.isEmpty()) {
            String initialUa = massiveUserAgents.get(new Random().nextInt(massiveUserAgents.size()));
            userAgentEditText.setText(initialUa);
            webSettings.setUserAgentString(initialUa);
        }

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAllWebViewData();

                String randomUserAgent = massiveUserAgents.get(new Random().nextInt(massiveUserAgents.size()));
                userAgentEditText.setText(randomUserAgent);
                webSettings.setUserAgentString(randomUserAgent);

                String targetUrl = urlEditText.getText().toString().trim();
                if (targetUrl.isEmpty()) {
                    targetUrl = "https://whoer.net";
                }

                if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                    targetUrl = "https://" + targetUrl;
                }

                String cacheBuster = (targetUrl.contains("?")) ? "&r=" + System.currentTimeMillis() : "?r=" + System.currentTimeMillis();
                String finalUrl = targetUrl + cacheBuster;

                webView.loadUrl(finalUrl);
            }
        });
    }

    private void clearAllWebViewData() {
        try {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();

            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            WebStorage.getInstance().deleteAllData();

            File dir = new File(getFilesDir().getParent(), "app_webview");
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new File(dir, children[i]));
                    if (!success) {
                        return false;
                    }
                }
            }
        }
        return dir != null && dir.delete();
    }

    // 5000+ Unique User Agents Generator
    private void generate5000PlusUserAgents() {
        massiveUserAgents = new ArrayList<>();
        String[] androidVersions = {"8.1", "9.0", "10", "11", "12", "13", "14", "15"};
        String[] devices = {
            "Pixel 4", "Pixel 5", "Pixel 6", "Pixel 7 Pro", "Pixel 8", 
            "Galaxy S10", "Galaxy S20", "Galaxy S21", "Galaxy S22 Ultra", "Galaxy S23", "Galaxy S24",
            "Redmi Note 8", "Redmi Note 9", "Redmi Note 11", "Redmi Note 12",
            "OnePlus 7T", "OnePlus 8", "OnePlus 10 Pro", "OnePlus 11",
            "Vivo V20", "Vivo X90", "Oppo Reno 6", "Oppo Reno 8", "Realme gt"
        };
        
        for (String android : androidVersions) {
            for (String device : devices) {
                for (int i = 10; i <= 80; i++) {
                    // Chrome Mobile
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/12" + (i % 10) + ".0.6" + i + "0." + (i+15) + " Mobile Safari/537.36");
                    // Firefox Mobile
                    massiveUserAgents.add("Mozilla/5.0 (Android " + android + "; Mobile; rv:12" + (i % 10) + ".0) Gecko/12" + (i % 10) + ".0 Firefox/12" + (i % 10) + ".0");
                    // Samsung Internet
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/2" + (i % 5) + ".0 Chrome/12" + (i % 10) + ".0 Mobile Safari/537.36");
                    // Opera Mobile
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/11" + (i % 10) + ".0.5" + i + "0.1 Mobile OPR/7" + (i % 10) + ".2.2");
                    // iOS Safari (Mixed pool for multi-engine feel)
                    massiveUserAgents.add("Mozilla/5.0 (iPhone; CPU iPhone OS " + (i % 5 + 13) + "_" + (i % 3) + " like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16." + (i % 4) + " Mobile/15E148 Safari/604.1");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
            }
                                 
