package com.example.feelsync;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proglish2.R;

public class OnlineMusicFragment extends Fragment {

    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_music, container, false);

        // Initialize the WebView
        webView = view.findViewById(R.id.web_view);
        if (webView == null) {
            throw new RuntimeException("WebView not found in layout");
        }

        // Configure WebView settings
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Enable JavaScript for Spotify web player

        // Set a WebViewClient to open links within the WebView
        webView.setWebViewClient(new WebViewClient());

        // Load the Spotify web player URL
        String spotifyWebPlayerUrl = "https://open.spotify.com";
        webView.loadUrl(spotifyWebPlayerUrl);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.destroy();
        }
    }
}