package com.example.virtualbrowser;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button btnTab1, btnTab2, btnTab3, btnSearchAll;
    private LinearLayout layoutContainer1, layoutContainer2, layoutContainer3;

    private WebView webView1, webView2, webView3;
    private EditText urlEditText1, urlEditText2, urlEditText3;
    private EditText uaEditText1, uaEditText2, uaEditText3;

    private List<String> massiveUserAgents;
    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind Views
        btnTab1 = findViewById(R.id.btnTab1);
        btnTab2 = findViewById(R.id.btnTab2);
        btnTab3 = findViewById(R.id.btnTab3);
        btnSearchAll = findViewById(R.id.btnSearchAll);

        layoutContainer1 = findViewById(R.id.layoutContainer1);
        layoutContainer2 = findViewById(R.id.layoutContainer2);
        layoutContainer3 = findViewById(R.id.layoutContainer3);

        webView1 = findViewById(R.id.webView1);
        webView2 = findViewById(R.id.webView2);
        webView3 = findViewById(R.id.webView3);

        urlEditText1 = findViewById(R.id.urlEditText1);
        urlEditText2 = findViewById(R.id.urlEditText2);
        urlEditText3 = findViewById(R.id.urlEditText3);

        uaEditText1 = findViewById(R.id.uaEditText1);
        uaEditText2 = findViewById(R.id.uaEditText2);
        uaEditText3 = findViewById(R.id.uaEditText3);

        // Generate 5000+ User-Agents
        generate5000PlusUserAgents();

        // Setup WebViews
        setupWebViewConfig(webView1);
        setupWebViewConfig(webView2);
        setupWebViewConfig(webView3);

        // Tab Switches
        btnTab1.setOnClickListener(v -> switchTab(1));
        btnTab2.setOnClickListener(v -> switchTab(2));
        btnTab3.setOnClickListener(v -> switchTab(3));

        // Global Search Button: Launches all 3 Layouts simultaneously with distinct UAs
        btnSearchAll.setOnClickListener(v -> executeSearchAll());
    }

    private void switchTab(int tabIndex) {
        layoutContainer1.setVisibility(tabIndex == 1 ? View.VISIBLE : View.GONE);
        layoutContainer2.setVisibility(tabIndex == 2 ? View.VISIBLE : View.GONE);
        layoutContainer3.setVisibility(tabIndex == 3 ? View.VISIBLE : View.GONE);
    }

    private void setupWebViewConfig(final WebView wv) {
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return !(url.startsWith("http://") || url.startsWith("https://"));
            }
        });

        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
                WebView newWebView = new WebView(MainActivity.this);
                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        wv.loadUrl(request.getUrl().toString());
                        return true;
                    }
                });
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();
                return true;
            }
        });
    }

    private void executeSearchAll() {
        // Deep Clean data before launching
        clearAllWebViewData();

        // Distinct User Agents for each layout
        String ua1 = getRandomUserAgent();
        String ua2 = getRandomUserAgent();
        while (ua2.equals(ua1)) ua2 = getRandomUserAgent();

        String ua3 = getRandomUserAgent();
        while (ua3.equals(ua1) || ua3.equals(ua2)) ua3 = getRandomUserAgent();

        // Assign UAs & Load URLs for Layout 1
        loadLayoutUrl(webView1, urlEditText1, uaEditText1, ua1);
        // Assign UAs & Load URLs for Layout 2
        loadLayoutUrl(webView2, urlEditText2, uaEditText2, ua2);
        // Assign UAs & Load URLs for Layout 3
        loadLayoutUrl(webView3, urlEditText3, uaEditText3, ua3);
    }

    private void loadLayoutUrl(WebView wv, EditText urlInput, EditText uaDisplay, String userAgent) {
        wv.getSettings().setUserAgentString(userAgent);
        uaDisplay.setText(userAgent);

        String targetUrl = urlInput.getText().toString().trim();
        if (targetUrl.isEmpty()) {
            targetUrl = "https://whoer.net";
        }

        if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
            targetUrl = "https://" + targetUrl;
        }

        String cacheBuster = (targetUrl.contains("?")) ? "&r=" + System.currentTimeMillis() : "?r=" + System.currentTimeMillis();
        wv.loadUrl(targetUrl + cacheBuster);
    }

    private String getRandomUserAgent() {
        return massiveUserAgents.get(random.nextInt(massiveUserAgents.size()));
    }

    private void clearAllWebViewData() {
        try {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();

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
                for (String child : children) {
                    if (!deleteDir(new File(dir, child))) {
                        return false;
                    }
                }
            }
        }
        return dir != null && dir.delete();
    }

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
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/12" + (i % 10) + ".0.6" + i + "0." + (i+15) + " Mobile Safari/537.36");
                    massiveUserAgents.add("Mozilla/5.0 (Android " + android + "; Mobile; rv:12" + (i % 10) + ".0) Gecko/12" + (i % 10) + ".0 Firefox/12" + (i % 10) + ".0");
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/2" + (i % 5) + ".0 Chrome/12" + (i % 10) + ".0 Mobile Safari/537.36");
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/11" + (i % 10) + ".0.5" + i + "0.1 Mobile OPR/7" + (i % 10) + ".2.2");
                    massiveUserAgents.add("Mozilla/5.0 (iPhone; CPU iPhone OS " + (i % 5 + 13) + "_" + (i % 3) + " like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16." + (i % 4) + " Mobile/15E148 Safari/604.1");
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (layoutContainer1.getVisibility() == View.VISIBLE && webView1.canGoBack()) {
            webView1.goBack();
        } else if (layoutContainer2.getVisibility() == View.VISIBLE && webView2.canGoBack()) {
            webView2.goBack();
        } else if (layoutContainer3.getVisibility() == View.VISIBLE && webView3.canGoBack()) {
            webView3.goBack();
        } else {
            super.onBackPressed();
        }
    }
                      }
                
