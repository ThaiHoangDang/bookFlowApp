package com.rmit.bookflowapp.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.databinding.FragmentMessageListBinding;
import com.rmit.bookflowapp.databinding.FragmentMoreBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.rmit.bookflowapp.repository.BookRepository;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MoreFragment extends Fragment {
    private String TAG = "MoreFragment";
    private FragmentMoreBinding binding;
    private MainActivity activity;
    private String API_KEY = "AIzaSyB8nWq104Nb-Ak_qtgex72pTGGl2dYAYoU";
    private String allBooksTitle = "";
    private enum TYPE {
        SUGGEST, IDENTIFY
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMoreBinding.inflate(inflater, container, false);
        activity = (MainActivity) getActivity();
        activity.setBottomNavigationBarVisibility(true);

        binding.userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("USER_ID", FirebaseAuth.getInstance().getCurrentUser().getUid());
                Navigation.findNavController(getView()).navigate(R.id.userProfileFragment, bundle);
            }
        });
        binding.followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "follow", Toast.LENGTH_SHORT).show();
            }
        });
        binding.aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.navController.navigate(R.id.aboutFragment);
            }
        });
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
//                activity.navController.navigate(R.id.authenticationFragment);
                activity.navController.navigate(R.id.action_moreFragment_to_authenticationFragment);
            }
        });

        // bind suggestion button to open dialog, the camera, and later the result
        binding.suggestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // asks to take photo
                suggestionDialog(TYPE.SUGGEST);
                disableTouch();
            }
        });

        binding.identificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // asks to take photo
                suggestionDialog(TYPE.IDENTIFY);
                disableTouch();
            }
        });

        return binding.getRoot();
    }

    public void suggestionDialog(TYPE type) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//         builder.setTitle("AI will suggest a book based on your photo");
        builder.setMessage("AI will " + type + " a book based on your photo");
        builder.setPositiveButton("Open Camera", (dialog, which) -> {
            // step 1: open camera
            if (type.equals(TYPE.SUGGEST)) {
                checkCameraPermission(9999);
            } else if (type.equals(TYPE.IDENTIFY)) {
                checkCameraPermission(8888);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
            enableTouch();
        });
        builder.setOnDismissListener(dialog -> {
            enableTouch();
        });
        builder.show();
    }

    public void checkCameraPermission(int requestCode) {
        // check if camera permission is granted
        // if not, request permission
        // if permission is granted, open camera

        if (activity.checkSelfPermission(android.Manifest.permission.CAMERA) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{android.Manifest.permission.CAMERA}, 1);
        } else {
            // open camera
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//                    cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT);  // Tested on API 24 Android version 7.0(Samsung S6)
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", CameraCharacteristics.LENS_FACING_FRONT); // Tested on API 27 Android version 8.0(Nexus 6P)
//                cameraIntent.putExtra("android.intent.extra.USE_FRONT_CAMERA", true);
//            } else {
//                cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 9999);  // Tested API 21 Android version 5.0.1(Samsung S4)
//            }
            startActivityForResult(cameraIntent, requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        // step 2: get photo
        try {
            Bitmap photo = (Bitmap) data.getExtras().get("data");

            // step 3: build query
            Task<List<Book>> allBooks = BookRepository.getInstance().getAllBooks();
            Tasks.whenAllSuccess(allBooks).addOnSuccessListener(task2 -> {
                for (Book book : allBooks.getResult()) {
                    allBooksTitle += book.getTitle() + "; ";
                }
                Log.d(TAG, "onActivityResult: " + allBooksTitle);
                // step 4: call
                if (requestCode == 9999) {
                    suggestion(photo, allBooksTitle, requestCode);
                } else if (requestCode == 8888) {
                    suggestion(photo, allBooksTitle, requestCode);
                }
                disableTouch();
            });
        } catch (Exception e) {
            // no photo taken
            enableTouch();
            return;
        }
    }

    public void suggestion(Bitmap photo, String allBooksTitle, int requestCode) {

        String query = "";
        if (requestCode == 9999) {
//            query = "Based on the photo, pick me a book from this given list or outside it that I would enjoy reading and state why you thought that way: " + booksInput;
            query = "Based on the photo which could be anything, from a photo of scenery to photo of a book or a human face showing their mood, suggest me JUST ONE BOOK from our database: " + allBooksTitle + "or books outside our database that I would enjoy reading and state why you thought that way.";
        } else if (requestCode == 8888) {
            query = "You are representing BookFlow, do not say \"I\". If the photo contains a book, identify the book such as its title (like Harry Potter) and author (like J.K. Rowling) and any possible metadata (like genre, year of publication, etc.) so that the reader can search for them on the Internet. If you cannot identify the field, don't include that in the answer prompt. If the photo does not contain a book, tell the reader that the photo does not contain a book.";
        }

        GenerativeModel gm = new GenerativeModel(
                "gemini-pro-vision",
                API_KEY
        );
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addImage(photo)
                .addText(query)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String resultText = result.getText();
                Log.d(TAG, "onSuccess: " + resultText); // RESULT TEXT

                // step 5: show result in dialog. Code must on UI thread
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        // return screen to normal
                        enableTouch();

                        // show result in dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Result");
                        builder.setMessage(resultText.trim());

                        // copy to clipboard button
                        builder.setNeutralButton("Copy", (dialog, which) -> {
                            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", resultText);
                            clipboard.setPrimaryClip(clip);
                            Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
                        });

                        // confirm button
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            dialog.dismiss();
                        });
                        builder.show();
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        }, executor);
    }

    public void enableTouch() {
        binding.progressBar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void disableTouch() {
        binding.progressBar.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
