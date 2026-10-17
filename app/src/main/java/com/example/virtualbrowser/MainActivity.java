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

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private EditText etUrl, etUserAgent, etProxyIp, etProxyPort;
    private Spinner spBrowserType;
    private Button btnLoad;

    private final String CHROME_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36";
    private final String FIREFOX_UA = "Mozilla/5.0 (Android 14; Mobile; rv:123.0) Gecko/123.0 Firefox/123.0";
    private final String OPERA_UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36 OPR/108.0.0.0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        etUrl = findViewById(R.id.etUrl);
        etUserAgent = findViewById(R.id.etUserAgent);
        etProxyIp = findViewById(R.id.etProxyIp);
        etProxyPort = findViewById(R.id.etProxyPort);
        spBrowserType = findViewById(R.id.spBrowserType);
        btnLoad = findViewById(R.id.btnLoad);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String[] browsers = {"Chrome Engine", "Firefox Engine", "Opera Engine"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, browsers);
        spBrowserType.setAdapter(adapter);

        spBrowserType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    etUserAgent.setText(CHROME_UA);
                } else if (position == 1) {
                    etUserAgent.setText(FIREFOX_UA);
                } else if (position == 2) {
                    etUserAgent.setText(OPERA_UA);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnLoad.setOnClickListener(v -> {
            String url = etUrl.getText().toString().trim();
            String ua = etUserAgent.getText().toString().trim();
            String proxyIp = etProxyIp.getText().toString().trim();
            String proxyPortStr = etProxyPort.getText().toString().trim();

            if (url.isEmpty()) {
                url = "https://whoer.net";
            }

            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }

            settings.setUserAgentString(ua);

            if (!proxyIp.isEmpty() && !proxyPortStr.isEmpty()) {
                setSystemProxy(proxyIp, proxyPortStr);
                Toast.makeText(this, "Proxy Applied: " + proxyIp, Toast.LENGTH_SHORT).show();
            }

            webView.loadUrl(url);
        });
    }

    private void setSystemProxy(String host, String port) {
        System.setProperty("http.proxyHost", host);
        System.setProperty("http.proxyPort", port);
        System.setProperty("https.proxyHost", host);
        System.setProperty("https.proxyPort", port);
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
