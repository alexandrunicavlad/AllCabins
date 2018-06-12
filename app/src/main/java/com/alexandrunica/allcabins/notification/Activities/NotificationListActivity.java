package com.alexandrunica.allcabins.notification.Activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.activities.CabinInfoAcitivty;
import com.alexandrunica.allcabins.cabins.model.BookModel;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.notification.adapter.BookingAdapter;
import com.alexandrunica.allcabins.notification.event.OnGetBooksEvent;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.BookOperation;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.List;

import javax.inject.Inject;

public class NotificationListActivity extends AppCompatActivity {

    @Inject
    Bus bus;

    @Inject
    DatabaseService databaseService;

    private ImageView back;
    private BookOperation bookOperations;
    private List<BookModel> fromBook;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_list);

        DaggerDbApplication app = ((DaggerDbApplication) this.getApplicationContext());
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);

        back = findViewById(R.id.notification_back);
        recyclerView = findViewById(R.id.notification_recylcer);
        progressBar = findViewById(R.id.notification_progress);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bookOperations = (BookOperation) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_BOOK, this);
        User currentUser = databaseService.getUser();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(NotificationListActivity.this);
        final String id = preferences.getString("uid", "");
        if (id != null && !id.equals("")) {
            if (currentUser != null && currentUser.getCabins() != null) {
                bookOperations.getAllBook(id, "to");
            } else {
                bookOperations.getAllBook(id, "from");
            }
        } else {

        }
    }

    @Subscribe
    public void onGetBooks(OnGetBooksEvent event) {
        if (event.getFilter().equals("from")) {
            if (event.getBookList() != null) {
                fromBook = event.getBookList();
                updateUI();
            }
        } else {
            if (event.getBookList() != null) {
                fromBook = event.getBookList();
                updateUI();
            }
        }
    }

    private void updateUI() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        BookingAdapter bookingAdapter = new BookingAdapter(this, fromBook);
        recyclerView.setAdapter(bookingAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        bus.register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        bus.unregister(this);
    }


}
