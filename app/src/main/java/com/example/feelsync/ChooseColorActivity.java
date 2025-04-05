package com.example.feelsync;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
<<<<<<< HEAD
import android.widget.Button;
=======
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
import android.widget.SeekBar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;
<<<<<<< HEAD
import android.widget.Toast;
=======
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
import androidx.appcompat.app.AppCompatActivity;
import com.example.proglish2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ChooseColorActivity extends AppCompatActivity {

    private View colorHappy, colorAngry, colorSad, colorCalm, colorLove;
    private SharedPreferences sharedPreferences;
<<<<<<< HEAD
    private Button saveButton;
=======
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
<<<<<<< HEAD
        setContentView(R.layout.choose_color);
=======
        setContentView(R.layout.choose_color); // Ensure this is the correct layout
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7

        sharedPreferences = getSharedPreferences("ColorPreferences", MODE_PRIVATE);

        colorHappy = findViewById(R.id.color_happy);
        colorAngry = findViewById(R.id.color_angry);
        colorSad = findViewById(R.id.color_sad);
        colorCalm = findViewById(R.id.color_calm);
        colorLove = findViewById(R.id.color_love);
<<<<<<< HEAD
        saveButton = findViewById(R.id.save_button);
=======
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7

        setupColorPicker(colorHappy, "colorHappy");
        setupColorPicker(colorAngry, "colorAngry");
        setupColorPicker(colorSad, "colorSad");
        setupColorPicker(colorCalm, "colorCalm");
        setupColorPicker(colorLove, "colorLove");

<<<<<<< HEAD
        loadSavedColors();

        saveButton.setOnClickListener(v -> {
            if (!allColorsChosen()) {
                showWarningDialog();
            } else {
                Toast.makeText(this, "Все цвета выбраны и сохранены!", Toast.LENGTH_SHORT).show();
            }
        });
=======
        // Load previously saved colors
        loadSavedColors();
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
    }

    private void setupColorPicker(final View colorView, final String colorKey) {
        colorView.setOnClickListener(v -> showColorPickerDialog(colorView, colorKey));
    }

    private void showColorPickerDialog(View colorView, String colorKey) {
<<<<<<< HEAD
=======
        // Get the saved color
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
        int savedColor = getSavedColor(colorKey);
        int savedRed = Color.red(savedColor);
        int savedGreen = Color.green(savedColor);
        int savedBlue = Color.blue(savedColor);

<<<<<<< HEAD
        final int[] selectedColor = {savedColor};
        final int[] red = {savedRed}, green = {savedGreen}, blue = {savedBlue};

        SeekBar redSeekBar = new SeekBar(this);
        redSeekBar.setMax(255);
        redSeekBar.setProgress(savedRed);

        SeekBar greenSeekBar = new SeekBar(this);
        greenSeekBar.setMax(255);
        greenSeekBar.setProgress(savedGreen);

        SeekBar blueSeekBar = new SeekBar(this);
        blueSeekBar.setMax(255);
        blueSeekBar.setProgress(savedBlue);
=======
        final int[] selectedColor = {savedColor}; // Use saved color as initial value
        final int[] red = {savedRed}, green = {savedGreen}, blue = {savedBlue}; // Use saved RGB values

        SeekBar redSeekBar = new SeekBar(this);
        redSeekBar.setMax(255);
        redSeekBar.setProgress(savedRed); // Set initial value

        SeekBar greenSeekBar = new SeekBar(this);
        greenSeekBar.setMax(255);
        greenSeekBar.setProgress(savedGreen); // Set initial value

        SeekBar blueSeekBar = new SeekBar(this);
        blueSeekBar.setMax(255);
        blueSeekBar.setProgress(savedBlue); // Set initial value
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7

        final TextView colorPreview = new TextView(this);
        colorPreview.setText(String.format("RGB: (%d, %d, %d)", savedRed, savedGreen, savedBlue));
        colorPreview.setPadding(20, 20, 20, 20);

<<<<<<< HEAD
        colorView.setBackgroundColor(selectedColor[0]);
=======
        colorView.setBackgroundColor(selectedColor[0]); // Set the background to saved color
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(colorPreview);
        layout.addView(redSeekBar);
        layout.addView(greenSeekBar);
        layout.addView(blueSeekBar);

<<<<<<< HEAD
        redSeekBar.setOnSeekBarChangeListener(createSeekBarChangeListener(colorPreview, colorView, red, green, blue, selectedColor));
        greenSeekBar.setOnSeekBarChangeListener(createSeekBarChangeListener(colorPreview, colorView, red, green, blue, selectedColor));
        blueSeekBar.setOnSeekBarChangeListener(createSeekBarChangeListener(colorPreview, colorView, red, green, blue, selectedColor));

        new MaterialAlertDialogBuilder(this)
                .setTitle("Choose Color")
                .setView(layout)
                .setPositiveButton("OK", (dialog, which) -> saveColor(colorKey, red[0], green[0], blue[0]))
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private SeekBar.OnSeekBarChangeListener createSeekBarChangeListener(TextView colorPreview, View colorView, int[] red, int[] green, int[] blue, int[] selectedColor) {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar.getMax() == 255) {
                    if (seekBar == seekBar.getRootView().findViewById(seekBar.getId())) {
                        red[0] = progress;
                    } else if (seekBar == seekBar.getRootView().findViewById(seekBar.getId())) {
                        green[0] = progress;
                    } else {
                        blue[0] = progress;
                    }
                }
=======
        // SeekBar Listeners
        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                red[0] = progress;
>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
                selectedColor[0] = Color.rgb(red[0], green[0], blue[0]);
                colorPreview.setText(String.format("RGB: (%d, %d, %d)", red[0], green[0], blue[0]));
                colorView.setBackgroundColor(selectedColor[0]);
            }
<<<<<<< HEAD

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        };
    }

=======
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        greenSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                green[0] = progress;
                selectedColor[0] = Color.rgb(red[0], green[0], blue[0]);
                colorPreview.setText(String.format("RGB: (%d, %d, %d)", red[0], green[0], blue[0]));
                colorView.setBackgroundColor(selectedColor[0]);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        blueSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                blue[0] = progress;
                selectedColor[0] = Color.rgb(red[0], green[0], blue[0]);
                colorPreview.setText(String.format("RGB: (%d, %d, %d)", red[0], green[0], blue[0]));
                colorView.setBackgroundColor(selectedColor[0]);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        new MaterialAlertDialogBuilder(this)
                .setTitle("Choose Color")
                .setView(layout)
                .setPositiveButton("OK", (dialog, which) -> {
                    saveColor(colorKey, red[0], green[0], blue[0]);

                    // Send the selected color to AddNoteActivity
                    Intent intent = new Intent(ChooseColorActivity.this, AddNoteActivity.class);
                    intent.putExtra("selected_red", red[0]);
                    intent.putExtra("selected_green", green[0]);
                    intent.putExtra("selected_blue", blue[0]);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
    private void saveColor(String colorKey, int red, int green, int blue) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(colorKey + "_red", red);
        editor.putInt(colorKey + "_green", green);
        editor.putInt(colorKey + "_blue", blue);
        editor.apply();
    }

    private void loadSavedColors() {
        colorHappy.setBackgroundColor(getSavedColor("colorHappy"));
        colorAngry.setBackgroundColor(getSavedColor("colorAngry"));
        colorSad.setBackgroundColor(getSavedColor("colorSad"));
        colorCalm.setBackgroundColor(getSavedColor("colorCalm"));
        colorLove.setBackgroundColor(getSavedColor("colorLove"));
    }

    private int getSavedColor(String colorKey) {
<<<<<<< HEAD
        int red = sharedPreferences.getInt(colorKey + "_red", -1);
        if (red == -1) return Color.WHITE;
        int green = sharedPreferences.getInt(colorKey + "_green", 255);
        int blue = sharedPreferences.getInt(colorKey + "_blue", 255);
        return Color.rgb(red, green, blue);
    }

    private boolean allColorsChosen() {
        return sharedPreferences.contains("colorHappy_red") &&
                sharedPreferences.contains("colorAngry_red") &&
                sharedPreferences.contains("colorSad_red") &&
                sharedPreferences.contains("colorCalm_red") &&
                sharedPreferences.contains("colorLove_red");
    }

    private void showWarningDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Выберите все цвета")
                .setMessage("Пожалуйста, выберите цвет для каждого настроения, прежде чем продолжить.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
=======
        int defaultColor;
        switch (colorKey) {
            case "colorHappy":
                defaultColor = Color.YELLOW;
                break;
            case "colorAngry":
                defaultColor = Color.RED;
                break;
            case "colorSad":
                defaultColor = Color.BLUE;
                break;
            case "colorCalm":
                defaultColor = Color.GREEN;
                break;
            case "colorLove":
                defaultColor = Color.MAGENTA;
                break;
            default:
                defaultColor = Color.WHITE;
        }
        int red = sharedPreferences.getInt(colorKey + "_red", Color.red(defaultColor));
        int green = sharedPreferences.getInt(colorKey + "_green", Color.green(defaultColor));
        int blue = sharedPreferences.getInt(colorKey + "_blue", Color.blue(defaultColor));
        return Color.rgb(red, green, blue);
    }
}

>>>>>>> 9766a59b2d9fb662435d4dc211e328896a6f58a7
