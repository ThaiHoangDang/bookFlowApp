package com.example.cleanconnect.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.example.cleanconnect.R;

import java.io.InputStream;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ImageLoaderUtils {

    private final Executor executor;
    private final Handler handler;

    public ImageLoaderUtils() {
        // Create a single-thread executor
        executor = Executors.newSingleThreadExecutor();

        // Initialize the handler with the main looper
        handler = new Handler(Looper.getMainLooper());
    }

    public void loadImage(final ImageView imageView, final String imageURL) {
        // Only for background process (can take time depending on the Internet speed)
        executor.execute(() -> {
            try {
                // Open the input stream from the URL
                InputStream inputStream = new URL(imageURL).openStream();

                // Decode the stream into a Bitmap
                Bitmap image = BitmapFactory.decodeStream(inputStream);

                // Close the input stream
                inputStream.close();

                // Only for making changes in the UI
                handler.post(() -> {
                    // Set the Bitmap in the ImageView
                    imageView.setImageBitmap(image);
                });
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.ic_default_profile_img);
                Log.d("Loader", e.getMessage());
            }
        });
    }
    public void loadImage(final Context context, final ImageView imageView, final Uri imageUri) {
        // Only for background process (can take time depending on the file size)
        executor.execute(() -> {
            try {
                // Open the input stream from the content resolver
                InputStream inputStream = context.getContentResolver().openInputStream(imageUri);

                // Decode the stream into a Bitmap
                Bitmap image = BitmapFactory.decodeStream(inputStream);

                // Close the input stream
                inputStream.close();

                // Only for making changes in the UI
                handler.post(() -> {
                    // Set the Bitmap in the ImageView
                    imageView.setImageBitmap(image);
                });
            } catch (Exception e) {
                imageView.setImageResource(R.drawable.ic_default_profile_img);
                Log.d("Loader", e.getMessage());
            }
        });
    }
}

