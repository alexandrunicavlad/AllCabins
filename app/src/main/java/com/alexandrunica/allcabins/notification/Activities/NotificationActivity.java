package com.alexandrunica.allcabins.notification.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.activities.CabinInfoAcitivty;
import com.alexandrunica.allcabins.cabins.model.BookModel;
import com.alexandrunica.allcabins.cabins.model.DateModel;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.notification.event.OnGetBookByIdEvent;
import com.alexandrunica.allcabins.notification.event.OnUserGetById;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.firebase.BookOperation;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

public class NotificationActivity extends AppCompatActivity {

    private BookOperation bookOperations;
    private ProfileOperations profileOperations;
    private BookModel book;
    private ImageView back;
    private ScrollView mainLayout;
    private ProgressBar progressBar;

    @Inject
    Bus bus;


    private User user;
    private String id, type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        DaggerDbApplication app = ((DaggerDbApplication) this.getApplicationContext());
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);

        back = findViewById(R.id.notification_back);
        mainLayout = findViewById(R.id.main_layout_cabin);
        progressBar = findViewById(R.id.notification_progress);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        id = getIntent().getStringExtra("notification");
        type = getIntent().getStringExtra("type");

        bookOperations = (BookOperation) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_BOOK, this);
        profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, this);
        bookOperations.getBook(id);

    }

    public void updateUI() {
        progressBar.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
        TextView name = findViewById(R.id.notification_name);
        TextView labelName = findViewById(R.id.notification_label_name);
        TextView date = findViewById(R.id.notification_time);
        TextView status = findViewById(R.id.notification_status);
        TextView message = findViewById(R.id.notification_message);
        TextView title = findViewById(R.id.host_title);
        Button accept = findViewById(R.id.accept_button);
        Button decline = findViewById(R.id.refuse_button);
        Button resend = findViewById(R.id.resend_button);

        if (type!= null && !type.equals("")) {
            if (type.equals("host")) {
                if(book.getStatus().equals("Pending")) {
                    accept.setVisibility(View.VISIBLE);
                    decline.setVisibility(View.VISIBLE);

                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            book.setStatus("Confirmed");
                            bookOperations.updateBook(book, id);
                        }
                    });
                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            book.setStatus("Refused");
                            bookOperations.updateBook(book, id);
                        }
                    });
                } else {
                    accept.setVisibility(View.GONE);
                    decline.setVisibility(View.GONE);
                }
                resend.setVisibility(View.GONE);
                labelName.setText(getResources().getString(R.string.notification_name));
            } else {
                if (book.getStatus().equals("Pending")) {

                    resend.setVisibility(View.VISIBLE);
                    resend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                } else {

                    resend.setVisibility(View.GONE);
                }
                labelName.setText(getResources().getString(R.string.notification_name_label));
                accept.setVisibility(View.GONE);
                decline.setVisibility(View.GONE);
            }
        }

        name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCall();
            }
        });

        title.setText(getResources().getString(R.string.notification_title_cabin, book.getCabinName()));
        name.setText(user.getUsername());
        DateModel dateModel = new Gson().fromJson(book.getDate(), DateModel.class);
        date.setText(getResources().getString(R.string.book_date_time,dateModel.getStart(), dateModel.getEnd()));
        status.setText(book.getStatus());
        message.setText(book.getMessage());
    }

    public void onCall() {
        if (user.getPhone() != null && !user.getPhone().equals("")) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + user.getPhone()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
                ActivityCompat.requestPermissions(NotificationActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 51);
            }

        } else {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(NotificationActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 51: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCall();
                } else {
                    Toast.makeText(NotificationActivity.this, getResources().getString(R.string.err_perm_call), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Subscribe
    public void onGetBook(OnGetBookByIdEvent event) {
        if (event.getBookModel() != null) {
            book = event.getBookModel();
            if (type.equals("host")) {
                profileOperations.getUserById(event.getBookModel().getFrom());
            } else {
                profileOperations.getUserById(event.getBookModel().getTo());
            }
        } else {

        }
    }

    @Subscribe
    public void onGetUser(OnUserGetById event) {
        if (event.getUser()!=null) {
            user = event.getUser();
            updateUI();
        }
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
