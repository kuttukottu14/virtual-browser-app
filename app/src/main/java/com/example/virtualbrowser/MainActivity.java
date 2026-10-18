package com.example.virtualbrowser;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.ProxyConfig;
import androidx.webkit.ProxyController;
import androidx.webkit.WebViewFeature;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText proxyIpEditText, proxyPortEditText, urlEditText;
    private Button launchButton;
    private Spinner engineSpinner;

    private final String[] browserEngines = {
        "Chrome Engine",
        "Firefox Engine",
        "Safari Engine"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        proxyIpEditText = findViewById(R.id.proxyIpEditText);
        proxyPortEditText = findViewById(R.id.proxyPortEditText);
        urlEditText = findViewById(R.id.urlEditText);
        launchButton = findViewById(R.id.launchButton);
        engineSpinner = findViewById(R.id.engineSpinner);

        // Spinner Setup
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, browserEngines);
        engineSpinner.setAdapter(adapter);

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Engine change listener to update User-Agent
        engineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedEngine = browserEngines[position];
                if (selectedEngine.contains("Chrome")) {
                    webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36");
                } else if (selectedEngine.contains("Firefox")) {
                    webSettings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:123.0) Gecko/20100101 Firefox/123.0");
                } else if (selectedEngine.contains("Safari")) {
                    webSettings.setUserAgentString("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.2.1 Safari/605.1.15");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        launchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proxyIp = proxyIpEditText.getText().toString().trim();
                String proxyPort = proxyPortEditText.getText().toString().trim();
                String targetUrl = urlEditText.getText().toString().trim();

                if (targetUrl.isEmpty()) {
                    targetUrl = "https://whoer.net";
                }

                if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                    targetUrl = "https://" + targetUrl;
                }

                final String finalUrl = targetUrl;

                if (!proxyIp.isEmpty() && !proxyPort.isEmpty()) {
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
                        ProxyConfig.Builder proxyConfigBuilder = new ProxyConfig.Builder()
                                .addProxyRule(proxyIp + ":" + proxyPort);

                        ProxyController.getInstance().setProxyOverride(
                                proxyConfigBuilder.build(),
                                Runnable::run,
                                () -> {
                                    runOnUiThread(() -> {
                                        Toast.makeText(MainActivity.this, "Proxy Applied: " + proxyIp, Toast.LENGTH_SHORT).show();
                                        webView.loadUrl(finalUrl);
                                    });
                                }
                        );
                    } else {
                        Toast.makeText(MainActivity.this, "Proxy Override not supported", Toast.LENGTH_SHORT).show();
                        webView.loadUrl(finalUrl);
                    }
                } else {
                    if (WebViewFeature.isFeatureSupported(WebViewFeature.PROXY_OVERRIDE)) {
                        ProxyController.getInstance().clearProxyOverride(Runnable::run, () -> { });
                    }
                    webView.loadUrl(finalUrl);
                }
            }
        });
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
