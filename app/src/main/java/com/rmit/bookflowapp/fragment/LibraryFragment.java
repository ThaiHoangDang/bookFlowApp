package com.rmit.bookflowapp.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.rmit.bookflowapp.Model.Book;
import com.rmit.bookflowapp.Model.Genre;
import com.rmit.bookflowapp.R;
import com.rmit.bookflowapp.activity.MainActivity;
import com.rmit.bookflowapp.adapter.SearchBookAdapter;
import com.rmit.bookflowapp.databinding.FragmentLibraryBinding;
import com.rmit.bookflowapp.interfaces.ClickCallback;
import com.rmit.bookflowapp.repository.BookRepository;
import com.rmit.bookflowapp.repository.GenreRepository;
import com.rmit.bookflowapp.util.TranslateAnimationUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class LibraryFragment extends Fragment implements ClickCallback {
    private static final String TAG = "LibraryFragment";
    private FragmentLibraryBinding bind;
    private MainActivity activity;
    private SearchBookAdapter searchBookAdapter, scanBookAdapter;
    private List<Book> books = new ArrayList<>();
    private final Handler debounceHandler = new Handler();
    private TextRecognizer recognizer;
    private int CAMERA_PERMISSION_REQUEST = 111;
    private int REQUEST_IMAGE_CAPTURE = 112;

    private final Runnable debounceRunnable = new Runnable() {
        @Override
        public void run() {
            if (bind != null) {
                String query = bind.searchView.getQuery().toString().trim();
                if (!query.isEmpty()) {
                    performSearch(query);
                } else {
                    books.clear();
                    searchBookAdapter.notifyDataSetChanged();
                }
            }
        }
    };

    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        bind = FragmentLibraryBinding.inflate(inflater, container, false);
        activity.setBottomNavigationBarVisibility(true);
        searchBookAdapter = new SearchBookAdapter(LibraryFragment.this, activity, books);
        scanBookAdapter = new SearchBookAdapter(LibraryFragment.this, activity, new ArrayList<>());

        bind.librarySearch.setVisibility(View.GONE);
        bind.scanSearch.setVisibility(View.GONE);

        bind.imageView2.setOnClickListener(v -> {
            TranslateAnimationUtil.fadeOutViewStatic(bind.libraryMain);
            TranslateAnimationUtil.fadeInViewStatic(bind.librarySearch);
        });

        bind.cancelButton.setOnClickListener(v -> {
            TranslateAnimationUtil.fadeOutViewStatic(bind.librarySearch);
            bind.searchView.setQuery("", false);
            books.clear();
            searchBookAdapter.notifyDataSetChanged();
            TranslateAnimationUtil.fadeInViewStatic(bind.libraryMain);
        });

        bind.hideScanResult.setOnClickListener(v -> {
            TranslateAnimationUtil.fadeOutViewStatic(bind.scanSearch);
            scanBookAdapter.setBooks(new ArrayList<>(books));
            TranslateAnimationUtil.fadeInViewStatic(bind.libraryMain);
        });

        bind.scannerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
                } else {
                    // Permission already granted, proceed with capturing the image
                    dispatchTakePictureIntent();
                }
            }
        });

        bind.searchBody.setAdapter(searchBookAdapter);
        bind.searchBody.setLayoutManager(new LinearLayoutManager(activity));

        bind.scanBody.setAdapter(scanBookAdapter);
        bind.scanBody.setLayoutManager(new LinearLayoutManager(activity));

        bind.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                debounceHandler.removeCallbacks(debounceRunnable);
                debounceHandler.postDelayed(debounceRunnable, 1000);
                return true;
            }
        });

//        Generate data for library fragment
        BookRepository.getInstance().getBookForLibraryFragment().addOnCompleteListener(getBooksTask -> {
            if (getBooksTask.isSuccessful()) {
                List<Book> bookList = getBooksTask.getResult();
                if (bookList != null) {
                    for (int i = 0; i < bookList.size() / 2; i++) {
                        Book currentBook = bookList.get(i);
                        View bookLayout = LayoutInflater.from(activity).inflate(R.layout.book_card, null);
                        ((TextView) bookLayout.findViewById(R.id.cardBookName)).setText(currentBook.getTitle());
                        ((TextView) bookLayout.findViewById(R.id.cardBookAuthor)).setText(currentBook.getAuthorString());
                        ImageView bookImage = bookLayout.findViewById(R.id.cardBookImage);
                        String imageUrl = currentBook.getImageUrl();
                        Picasso.get().load(imageUrl).into(bookImage);
                        bind.bestOfAllTime.addView(bookLayout);

                        if (!Float.isNaN(currentBook.getRating())) {
                            ((RatingBar) bookLayout.findViewById(R.id.cardBookRating)).setRating(currentBook.getRating());
                        } else {
                            ((RatingBar) bookLayout.findViewById(R.id.cardBookRating)).setRating(0);
                        }

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("BOOK_OBJECT", currentBook);
                        bookLayout.setOnClickListener(v -> {
                            Navigation.findNavController(getView()).navigate(R.id.bookDetailFragment, bundle);
                        });
                    }

                    for (int i = bookList.size() / 2; i < bookList.size(); i++) {
                        Book currentBook = bookList.get(i);
                        View bookLayout = LayoutInflater.from(requireContext()).inflate(R.layout.book_card, null);
                        ((TextView) bookLayout.findViewById(R.id.cardBookName)).setText(currentBook.getTitle());
                        ((TextView) bookLayout.findViewById(R.id.cardBookAuthor)).setText(currentBook.getAuthorString());
                        ImageView bookImage = bookLayout.findViewById(R.id.cardBookImage);
                        String imageUrl = currentBook.getImageUrl();
                        Picasso.get().load(imageUrl).into(bookImage);
                        bind.trending.addView(bookLayout);
                        Bundle bundle = new Bundle();

                        if (!Float.isNaN(currentBook.getRating())) {
                            ((RatingBar) bookLayout.findViewById(R.id.cardBookRating)).setRating(currentBook.getRating());
                        } else {
                            ((RatingBar) bookLayout.findViewById(R.id.cardBookRating)).setRating(0);
                        }

                        bundle.putSerializable("BOOK_OBJECT", currentBook);
                        bookLayout.setOnClickListener(v -> {
                            Navigation.findNavController(getView()).navigate(R.id.bookDetailFragment, bundle);
                        });
                    }
                }
            }
        });

        GenreRepository.getInstance().getGenreForLibraryFragment().addOnCompleteListener(getGenresTask -> {
            if (getGenresTask.isSuccessful()) {
                List<Genre> genreList = getGenresTask.getResult();
                for (int i = 0; i < genreList.size(); i++) {
                    Genre currentGenre = genreList.get(i);
                    View genreCardLayout = LayoutInflater.from(requireContext()).inflate(R.layout.genre_card, null);
                    ((TextView) genreCardLayout.findViewById(R.id.textView3)).setText(currentGenre.getName());
                    ImageView genreImage = genreCardLayout.findViewById(R.id.imageView3);
                    Picasso.get().load(currentGenre.getImageUrl()).into(genreImage);
                    bind.genre.addView(genreCardLayout);

                    Bundle bundle = new Bundle();

                    bundle.putSerializable("GENRE_OBJECT", currentGenre);
                    genreCardLayout.setOnClickListener(v -> {
                        Navigation.findNavController(getView()).navigate(R.id.genreFragment, bundle);
                    });
                }
            }
        });

        return bind.getRoot();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                // Get the captured image as a Bitmap directly from the extras
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                processImage(imageBitmap);
            } else {
                Toast.makeText(activity, "Failed to get image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void processImage(Bitmap imageBitmap) {
        int rotation = 0;
        InputImage image = InputImage.fromBitmap(imageBitmap, rotation);

        Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        BookRepository.getInstance().getBookContainedInQuery(extractString(visionText))
                                .addOnSuccessListener(new OnSuccessListener<List<Book>>() {
                                    @Override
                                    public void onSuccess(List<Book> books) {

                                        if (books.size() == 0) {
                                            Toast.makeText(activity, "We cannot find any matching books", Toast.LENGTH_SHORT).show();
                                        } else {
                                            scanBookAdapter.setBooks(new ArrayList<>(books));
                                            TranslateAnimationUtil.fadeOutViewStatic(bind.libraryMain);
                                            TranslateAnimationUtil.fadeInViewStatic(bind.scanSearch);
                                        }
                                    }
                                });

                    }
                })
                .addOnFailureListener(e -> Toast.makeText(activity, "Scan Failed!", Toast.LENGTH_SHORT).show());
    }

    private String extractString(Text result) {
        StringBuilder allText = new StringBuilder();

        for (Text.TextBlock block : result.getTextBlocks()) {
            String blockText = block.getText();
            allText.append(blockText).append("\n");

            for (Text.Line line : block.getLines()) {
                String lineText = line.getText();
                allText.append(lineText).append("\n");

                for (Text.Element element : line.getElements()) {
                    String elementText = element.getText();
                    allText.append(elementText).append(" ");

                    for (Text.Symbol symbol : element.getSymbols()) {
                        String symbolText = symbol.getText();
                        allText.append(symbolText);
                    }
                    allText.append("\n");
                }
            }
        }

        return allText.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bind = null;
    }

    private void performSearch(String query) {
        books.clear();
        BookRepository.getInstance().getBookByQuery(query).addOnCompleteListener(
                task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult() != null) {
                            books.clear();
                            books.addAll(task.getResult());
                            searchBookAdapter.notifyDataSetChanged();
                        }
                    }
                }
        );
    }

    @Override
    public void onSiteClick(Bundle bundle) {
        Navigation.findNavController(getView()).navigate(R.id.bookDetailFragment, bundle);
    }
}