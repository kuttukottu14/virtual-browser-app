package com.example.virtualbrowser;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

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
                    // Proxy లేకపోతే డైరెక్ట్ గా లోడ్ అవుతుంది
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

