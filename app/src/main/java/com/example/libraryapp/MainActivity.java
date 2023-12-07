package com.example.libraryapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.libraryapp.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String IMAGE_URL_BASE = "http://covers.openlibrary.org/b/id/";
    public static final int SHOW_BOOK_DETAILS_ACTIVITY_REQUEST_CODE = 1;

    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.book_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                fetchBooksData(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_clear) {
            searchView.setQuery("", false);
            setupBookListView(new ArrayList<>());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchBooksData(String query) {
        String finalQuery = prepareQuery(query);
        BookService bookService = RetrofitInstance.getRetrofitInstance().create(BookService.class);
        Call<BookContainer> booksApiCall = bookService.findBooks(finalQuery);
        booksApiCall.enqueue(new Callback<BookContainer>() {
            @Override
            public void onResponse(@NonNull Call<BookContainer> call, @NonNull Response<BookContainer> response) {
                if (response.body() != null) {
                    setupBookListView(response.body().getBookList());
                }
            }

            @Override
            public void onFailure(@NonNull Call<BookContainer> call, @NonNull Throwable t) {
                Snackbar.make(findViewById(R.id.coordinator_layout), R.string.error_message,
                        BaseTransientBottomBar.LENGTH_LONG).show();
            }
        });
    }

    private String prepareQuery(String query) {
        String[] queryParts = query.split("\\s+");
        return TextUtils.join("+", queryParts);
    }

    private void setupBookListView(List<Book> books) {
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final BookAdapter adapter = new BookAdapter();
        adapter.setBooks(books);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public boolean checkNullOrEmpty(String text) {
        return text != null && !TextUtils.isEmpty(text);
    }


    private class BookHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {

        private TextView bookTitleTextView;
        private TextView bookAuthorTextView;
        private TextView numberOfPagesTextView;
        private ImageView bookCover;

        private Book book;

        public BookHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.book_list_item, parent, false));
            bookTitleTextView = itemView.findViewById(R.id.book_title);
            bookAuthorTextView = itemView.findViewById(R.id.book_author);
            numberOfPagesTextView = itemView.findViewById(R.id.number_of_pages);
            bookCover = itemView.findViewById(R.id.img_cover);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Book book) {
            if (book != null && checkNullOrEmpty(book.getTitle()) && book.getAuthors() != null) {
                this.book = book;
                bookTitleTextView.setText(book.getTitle());
                bookAuthorTextView.setText(TextUtils.join(", ", book.getAuthors()));
                numberOfPagesTextView.setText(book.getNumberOfPages());
                if (book.getCover() != null) {
                    Picasso.with(itemView.getContext())
                            .load(IMAGE_URL_BASE + book.getCover() + "-S.jpg")
                            .placeholder(R.drawable.ic_book_24).into(bookCover);
                } else {
                    bookCover.setImageResource(R.drawable.ic_book_24);
                }
            }
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this, BookDetailsActivity.class);
            intent.putExtra(BookDetailsActivity.EXTRA_BOOK_DETAILS, book);
            startActivityForResult(intent, SHOW_BOOK_DETAILS_ACTIVITY_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View v) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.confirm_deletion_title)
                    .setMessage(R.string.confirm_deletion_question)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
//                        bookViewModel.delete(book);
                        Snackbar.make(findViewById(R.id.coordinator_layout), R.string.book_removed, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .show();

            return true;
        }
    }

    private class BookAdapter extends RecyclerView.Adapter<BookHolder> {
        private List<Book> books;

        @NonNull
        @Override
        public BookHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BookHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull BookHolder holder, int position) {
            if (books != null) {
                Book book = books.get(position);
                holder.bind(book);
            } else {
                Log.d("MainActivity", "No books");
            }
        }

        @Override
        public int getItemCount() {
            if (books != null) {
                return books.size();
            } else {
                return 0;
            }
        }

        void setBooks(List<Book> books) {
            this.books = books;
            notifyDataSetChanged();
        }
    }
}