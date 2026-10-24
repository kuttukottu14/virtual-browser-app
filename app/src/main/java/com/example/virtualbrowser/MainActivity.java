package com.example.virtualbrowser;

import android.os.Bundle;
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

        generate2000PlusUserAgents();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, browserEngines);
        engineSpinner.setAdapter(adapter);

        // WebView Settings అడ్వాన్స్డ్ కాన్ఫిగరేషన్
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setSupportMultipleWindows(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUserAgentString("Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36");

        // WebViewClient: ఎక్స్‌టర్నల్ రీడైరెక్ట్‌లను కంట్రోల్ చేయడానికి
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                
                // కేవలం http లేదా https లింక్‌లను మాత్రమే వెబ్‌వ్యూలో లోడ్ చేయాలి
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return false; // వెబ్‌వ్యూలోనే ఓపెన్ అవుతుంది
                }
                
                // ఇంటెంట్ లేదా ఇతర స్కీమ్‌లను బ్లాక్ చేసి యాప్ క్రాష్ కాకుండా ఆపడం
                return true;
            }
        });

        // WebChromeClient: పాప్-అప్‌లు మరియు కొత్త విండోలను హ్యాండిల్ చేయడానికి
        webView.setWebChromeClient(new WebChromeClient());

        if (!massiveUserAgents.isEmpty()) {
            String initialUa = massiveUserAgents.get(new Random().nextInt(massiveUserAgents.size()));
            userAgentEditText.setText(initialUa);
            webSettings.setUserAgentString(initialUa);
        }

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. డీప్ క్లీనింగ్
                clearAllWebViewData();

                // 2. కొత్త ర్యాండమ్ యూజర్ ఏజెంట్
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

                // 3. Cache-Buster Parameter
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

    private void generate2000PlusUserAgents() {
        massiveUserAgents = new ArrayList<>();
        String[] androidVersions = {"11", "12", "13", "14"};
        String[] devices = {"Pixel 7 Pro", "Galaxy S23", "Redmi Note 11", "OnePlus 10"};
        
        for (String android : androidVersions) {
            for (String device : devices) {
                for (int i = 10; i <= 60; i++) {
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/12" + (i % 10) + ".0.0.0 Mobile Safari/537.36");
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
            
