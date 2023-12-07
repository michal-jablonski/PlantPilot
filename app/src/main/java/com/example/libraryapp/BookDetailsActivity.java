package com.example.libraryapp;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

import static com.example.libraryapp.MainActivity.IMAGE_URL_BASE;

public class BookDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_BOOK_DETAILS = "pb.edu.pl.BOOK_DETAILS";
    private TextView titleTextView;
    private TextView authorTextView;
    private TextView subjectsTextView;
    private TextView publishYearTextView;
    private ImageView coverImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        titleTextView = findViewById(R.id.book_title);
        authorTextView = findViewById(R.id.book_author);
        subjectsTextView = findViewById(R.id.subjects);
        publishYearTextView = findViewById(R.id.first_publish_year);
        coverImageView = findViewById(R.id.img_cover);

        if(getIntent().hasExtra(EXTRA_BOOK_DETAILS)){
            Book book = (Book)getIntent().getSerializableExtra(EXTRA_BOOK_DETAILS);
            titleTextView.setText(getString(R.string.book_title, book.getTitle()));
            authorTextView.setText(getString(R.string.book_author, TextUtils.join(", ", book.getAuthors())));
            subjectsTextView.setText(getString(R.string.book_subjects, TextUtils.join("\n", book.getSubjects())));
            publishYearTextView.setText(getString(R.string.book_published_year, book.getFirstPublishYear()));
            if (book.getCover() != null) {
                Picasso.with(this)
                        .load(IMAGE_URL_BASE + book.getCover() + "-L.jpg")
                        .placeholder(R.drawable.ic_book_24).into(coverImageView);
            } else {
                coverImageView.setImageResource(R.drawable.ic_book_24);
            }
        }
    }
}
