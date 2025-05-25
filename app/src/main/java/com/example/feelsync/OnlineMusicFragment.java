package com.example.feelsync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.proglish2.R;

public class OnlineMusicFragment extends Fragment {

    private WebView webView;
    private ProgressBar progressBar;
    private MusicViewModel viewModel;
    private SharedViewModel sharedViewModel;

    @SuppressLint({"MissingInflatedId", "SetJavaScriptEnabled"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online_music, container, false);

        // Initialize views
        webView = view.findViewById(R.id.web_view);
        progressBar = view.findViewById(R.id.progress_bar);

        if (webView == null || progressBar == null) {
            throw new RuntimeException("WebView or ProgressBar not found in layout");
        }

        // Initialize ViewModels
        viewModel = new ViewModelProvider(requireActivity()).get(MusicViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.setOnlineFragmentActive(true);

        // Configure WebView
        setupWebView();

        // Check network and load content
        if (isNetworkAvailable()) {
            loadSoundCloud();
        } else {
            showOfflineMessage();
        }

        return view;
    }

    private void setupWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.startsWith("soundcloud://")) {
                    handleSoundCloudDeepLink(url);
                    return true;
                }
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void loadSoundCloud() {
        String soundCloudWebPlayerUrl = "https://soundcloud.com";
        webView.loadUrl(soundCloudWebPlayerUrl);
    }

    private void showOfflineMessage() {
        Toast.makeText(requireContext(),
                "You are offline. Please connect to network to listen to online music.",
                Toast.LENGTH_LONG).show();
        webView.setVisibility(View.GONE);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = requireActivity().getSystemService(ConnectivityManager.class);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork == null || !activeNetwork.isConnectedOrConnecting()) {
            return false;
        }

        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 www.google.com");
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            Log.e("OnlineMusicFragment", "Error checking network", e);
            return false;
        }
    }

    private void handleSoundCloudDeepLink(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            } else {
                String playStoreUrl = "https://play.google.com/store/apps/details?id=com.soundcloud.android";
                Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl));
                if (playStoreIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(playStoreIntent);
                } else {
                    Toast.makeText(requireContext(),
                            "Unable to open SoundCloud. Please install the app.",
                            Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Log.e("OnlineMusicFragment", "Error handling deep link", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedViewModel.setOnlineFragmentActive(true);
        viewModel.setIsOnlineMusicActive(true);
        pauseLocalMusicIfPlaying();
    }

    @Override
    public void onPause() {
        super.onPause();
        sharedViewModel.setOnlineFragmentActive(false);
        viewModel.setIsOnlineMusicActive(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (webView != null) {
            webView.stopLoading();
            webView.clearHistory();
            webView.clearCache(true);
            webView.destroy();
            webView = null;
        }
    }

    private void pauseLocalMusicIfPlaying() {
        if (viewModel.getIsPlaying().getValue() != null && viewModel.getIsPlaying().getValue()) {
            viewModel.setPlaying(false);
        }
    }
}