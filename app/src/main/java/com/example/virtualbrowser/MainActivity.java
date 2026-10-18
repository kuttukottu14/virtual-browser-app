package com.example.virtualbrowser;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText urlEditText;
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
        launchButton = findViewById(R.id.launchButton);
        engineSpinner = findViewById(R.id.engineSpinner);

        // Load 1000+ User Agents
        generateMassiveUserAgents();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, browserEngines);
        engineSpinner.setAdapter(adapter);

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String targetUrl = urlEditText.getText().toString().trim();

                if (targetUrl.isEmpty()) {
                    targetUrl = "https://www.google.com";
                }

                if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                    targetUrl = "https://" + targetUrl;
                }

                // Random User Agent select chesi apply cheyyadam (1000+ pool nundi)
                String randomUserAgent = massiveUserAgents.get(new Random().nextInt(massiveUserAgents.size()));
                webSettings.setUserAgentString(randomUserAgent);

                webView.loadUrl(targetUrl);
            }
        });
    }

    // 1000+ Dynamic User Agents Generator
    private void generateMassiveUserAgents() {
        massiveUserAgents = new ArrayList<>();
        String[] androidVersions = {"10", "11", "12", "13", "14"};
        String[] devices = {"Pixel 6", "Pixel 7", "Galaxy S21", "Galaxy S22", "Redmi Note 10", "OnePlus 9", "Moto Gpower"};
        
        // Generating 1000+ combinations automatically
        for (String android : androidVersions) {
            for (String device : devices) {
                for (int i = 100; i <= 300; i++) {
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/12" + (i % 10) + ".0.0." + i + " Mobile Safari/537.36");
                    massiveUserAgents.add("Mozilla/5.0 (Android " + android + "; Mobile; rv:12" + (i % 10) + ".0) Gecko/12" + (i % 10) + ".0 Firefox/12" + (i % 10) + ".0");
                    massiveUserAgents.add("Mozilla/5.0 (Linux; Android " + android + "; " + device + ") AppleWebKit/537.36 (KHTML, like Gecko) Chrome/11" + (i % 10) + ".0.0." + i + " Mobile OPR/7" + (i % 10) + ".0.2");
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
