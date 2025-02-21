package com.example.feelsync;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proglish2.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ViewNoteActivity extends AppCompatActivity {
    private ImageView noteImageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_note);

        TextView fullNoteTextView = findViewById(R.id.full_note_text);
        noteImageView = findViewById(R.id.note_image);
        progressBar = findViewById(R.id.progress_bar); // Progress bar for loading indicator

        // Get the note text and image URL/URI from the Intent
        String fullNoteText = getIntent().getStringExtra("NOTE_TEXT");
        String imageUrl = getIntent().getStringExtra("IMAGE_URL");
        Uri imageUri = getIntent().getParcelableExtra("IMAGE_URI");  // Get the image URI if it's from the gallery

        // Log the image URL and URI to ensure they are being passed correctly
        Log.d("ViewNoteActivity", "Full Note: " + fullNoteText);
        Log.d("ViewNoteActivity", "Image URL: " + imageUrl);
        Log.d("ViewNoteActivity", "Image URI: " + (imageUri != null ? imageUri.toString() : "null"));

        // Set the note text
        if (fullNoteText != null) {
            fullNoteTextView.setText(fullNoteText);
        }

        // Check if the image is from a URL or gallery (URI)
        if (imageUrl != null && !imageUrl.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE);  // Show the progress bar while the image loads
            new ImageLoader().execute(imageUrl);
        } else if (imageUri != null) {
            // Load the image directly from the URI (gallery image)
            progressBar.setVisibility(View.VISIBLE);
            loadImageFromUri(imageUri);
        } else {
            noteImageView.setImageResource(R.drawable.placeholder); // Fallback if no image URL/URI is provided
        }
    }

    // AsyncTask class for loading the image from a URL
    private class ImageLoader extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            Bitmap bitmap = null;
            try {
                String imageUrl = urls[0];
                Log.d("ImageLoader", "Loading image from URL: " + imageUrl);

                // Open a connection to the URL and load the image
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
                // Log any errors during image loading
                Log.e("ImageLoader", "Error loading image from URL: " + urls[0]);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            // Hide the progress bar once the image is loaded
            progressBar.setVisibility(View.GONE);

            if (bitmap != null) {
                noteImageView.setImageBitmap(bitmap);
                Log.d("ImageLoader", "Image loaded successfully");
            } else {
                Log.e("ImageLoader", "Failed to load image");
                noteImageView.setImageResource(R.drawable.placeholder);  // Fallback to placeholder image
            }
        }
    }

    // Method to load image from gallery (URI)
    private void loadImageFromUri(Uri imageUri) {
        try {
            // Log the URI for debugging
            Log.d("ViewNoteActivity", "Loading image from URI: " + imageUri.toString());

            // Use MediaStore to get the bitmap from the URI
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            noteImageView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);  // Hide the progress bar after image is loaded
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ViewNoteActivity", "Error loading image from URI: " + imageUri);
            noteImageView.setImageResource(R.drawable.placeholder);  // Fallback to placeholder if there's an error
            progressBar.setVisibility(View.GONE);
        }
    }
}
