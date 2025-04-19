package com.example.feelsync;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.content.SharedPreferences;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proglish2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ChooseColorActivity extends AppCompatActivity {

    private View colorHappy, colorAngry, colorSad, colorCalm, colorLove;
    private SharedPreferences sharedPreferences;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_color);

        sharedPreferences = getSharedPreferences("ColorPreferences", MODE_PRIVATE);

        colorHappy = findViewById(R.id.color_happy);
        colorAngry = findViewById(R.id.color_angry);
        colorSad = findViewById(R.id.color_sad);
        colorCalm = findViewById(R.id.color_calm);
        colorLove = findViewById(R.id.color_love);
        saveButton = findViewById(R.id.save_button);

        setupColorPicker(colorHappy, "colorHappy");
        setupColorPicker(colorAngry, "colorAngry");
        setupColorPicker(colorSad, "colorSad");
        setupColorPicker(colorCalm, "colorCalm");
        setupColorPicker(colorLove, "colorLove");

        loadSavedColors();

        saveButton.setOnClickListener(v -> {
            if (!allColorsChosen()) {
                showWarningDialog();
            } else {
                Toast.makeText(this, "Все цвета выбраны и сохранены!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupColorPicker(final View colorView, final String colorKey) {
        colorView.setOnClickListener(v -> showColorPickerDialog(colorView, colorKey));
    }

    private void showColorPickerDialog(View colorView, String colorKey) {
        int savedColor = getSavedColor(colorKey);
        int savedRed = Color.red(savedColor);
        int savedGreen = Color.green(savedColor);
        int savedBlue = Color.blue(savedColor);
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

        final TextView colorPreview = new TextView(this);
        colorPreview.setText(String.format("RGB: (%d, %d, %d)", savedRed, savedGreen, savedBlue));
        colorPreview.setPadding(20, 20, 20, 20);

        colorView.setBackgroundColor(selectedColor[0]);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(colorPreview);
        layout.addView(redSeekBar);
        layout.addView(greenSeekBar);
        layout.addView(blueSeekBar);

        redSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                red[0] = progress;
                selectedColor[0] = Color.rgb(red[0], green[0], blue[0]);
                colorPreview.setText(String.format("RGB: (%d, %d, %d)", red[0], green[0], blue[0]));
                colorView.setBackgroundColor(selectedColor[0]);
            }

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
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

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