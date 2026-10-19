package com.example.virtualbrowser;

import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
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

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        if (!massiveUserAgents.isEmpty()) {
            String initialUa = massiveUserAgents.get(new Random().nextInt(massiveUserAgents.size()));
            userAgentEditText.setText(initialUa);
            webSettings.setUserAgentString(initialUa);
        }

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. డీప్ క్లీనింగ్: WebView ఇంటర్నల్ స్టోరేజ్ & IndexedDB ని పూర్తిగా తుడిచిపెట్టడం
                clearAllWebViewData();

                // 2. కొత్త ర్యాండమ్ యూజర్ ఏజెంట్ సెట్ చేసి స్క్రీన్ మీద చూపించడం
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

                webView.loadUrl(targetUrl);
            }
        });
    }

    // WebView కి సంబంధించిన అన్ని రకాల డేటాను, స్టోరేజ్‌ని పూర్తిగా క్లియర్ చేసే ఫంక్షన్
    private void clearAllWebViewData() {
        try {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();

            webView.clearCache(true);
            webView.clearHistory();
            webView.clearFormData();
            WebStorage.getInstance().deleteAllData();

            // app_webview ఫోల్డర్‌ను పూర్తిగా తొలగించడం వల్ల LocalStorage/IndexedDB ఉండవు
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
        String[] androidVersions = {"9.0", "10", "11", "12", "13", "14"};
        String[] devices = {"Pixel 5", "Pixel 6", "Pixel 7 Pro", "Galaxy S20", "Galaxy S21", "Galaxy S23", "Redmi Note 9", "Redmi Note 11", "OnePlus 8", "OnePlus 10", "Vivo V20", "Oppo Reno"};
        
        for (String android : androidVersions) {
            for (String device : devices) {
                for (int i = 10; i <= 65; i++) {
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/12" + (i % 10) + ".0.6" + i + "0." + (i+20) + " Mobile Safari/537.36");
                    massiveUserAgents.add("Mozilla/5.0 (Android " + android + "; Mobile; rv:12" + (i % 10) + ".0) Gecko/12" + (i % 10) + ".0 Firefox/12" + (i % 10) + ".0");
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/11" + (i % 10) + ".0.5" + i + "0.1" + " Mobile OPR/7" + (i % 10) + ".2.2");
                    massiveUserAgents.add("Mozilla/5.0 (iPhone; CPU iPhone OS " + (i % 5 + 14) + "_" + (i % 3) + " like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/16." + (i % 4) + " Mobile/15E148 Safari/604.1");
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
