package com.alexandrunica.allcabins.cabins.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.adapter.ImageAdapter;
import com.alexandrunica.allcabins.cabins.events.OnReviewAddEvent;
import com.alexandrunica.allcabins.cabins.events.OnReviewFailEvent;
import com.alexandrunica.allcabins.cabins.helper.CurrencyConverter;
import com.alexandrunica.allcabins.cabins.model.BookModel;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.CabinInfoModel;
import com.alexandrunica.allcabins.cabins.model.DateModel;
import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.alexandrunica.allcabins.cabins.model.ReviewModel;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.map.event.OnGetCabinByIdEvent;
import com.alexandrunica.allcabins.profile.activities.EditCabinInfoActivity;
import com.alexandrunica.allcabins.profile.auth.LoginFragment;
import com.alexandrunica.allcabins.profile.event.OnInsertEvent;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.BookOperation;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.alexandrunica.allcabins.service.firebase.ReviewOperations;
import com.alexandrunica.allcabins.widget.MessageDialog;
import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.gson.Gson;
import com.leavjenn.smoothdaterangepicker.date.SmoothDateRangePickerFragment;
import com.savvi.rangedatepicker.CalendarPickerView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

public class CabinInfoAcitivty extends AppCompatActivity {

    public TextView priceView, nameView, addressView, descriptionView, facilitiesView, ratingView, guestsView, rooomView, bedView, bathView, priceBottomView, ratingBottomView;
    public ImageView mainImage, phoneButton, locationButton, closeButton;
    public RatingBar rating;
    public FloatingActionsMenu add;
    public RelativeLayout imageLayout, mainLayout;
    public ProgressBar progress;
    public Button button;
    public String id;

    public ViewPager viewPager;
    public Cabin cabin;
    private CabinOperations cabinOperations;
    private ReviewOperations reviewOperations;
    private BookOperation bookOperations;

    @Inject
    DatabaseService databaseService;

    @Inject
    Bus bus;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cabin_fragment_layout);

        DaggerDbApplication app = ((DaggerDbApplication) this.getApplicationContext());
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            Window w = getWindow();
//            w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
//        }

//        Window window = getWindow();
//        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black_semi_transparent));
//        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, this);
        reviewOperations = (ReviewOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_REVIEWS_TABLE, this);
        bookOperations = (BookOperation) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_BOOK, this);

        mainLayout = findViewById(R.id.main_layout_cabin);
        progress = findViewById(R.id.cabin_progress);
        priceView = findViewById(R.id.cabin_info_price);
        mainImage = findViewById(R.id.cabin_no_image);
        phoneButton = findViewById(R.id.cabin_info_phone);
        closeButton = findViewById(R.id.cabin_close);
        locationButton = findViewById(R.id.cabin_info_location);
        nameView = findViewById(R.id.cabin_info_name);
        addressView = findViewById(R.id.cabin_info_address);
        descriptionView = findViewById(R.id.cabin_info_description);
        facilitiesView = findViewById(R.id.cabin_info_facilities);
        guestsView = findViewById(R.id.guest_number);
        rooomView = findViewById(R.id.bedroom_number);
        bedView = findViewById(R.id.bed_number);
        bathView = findViewById(R.id.bath_number);
        rating = findViewById(R.id.cabin_rating_star);
        ratingView = findViewById(R.id.cabin_info_rating);
        add = findViewById(R.id.cabin_add_fav);
        imageLayout = findViewById(R.id.image_layout);
        viewPager = findViewById(R.id.image_pager);
        priceBottomView = findViewById(R.id.cabin_price_bottom);
        ratingBottomView = findViewById(R.id.cabin_review_bottom);
        button = findViewById(R.id.bottom_button);

        id = getIntent().getStringExtra("cabin");
        cabinOperations.getCabin(id);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void updateUI() {
        if (cabin != null) {
            addressView.setText(cabin.getAddress());
            nameView.setText(cabin.getName());
            CurrencyConverter currencyConverter = new CurrencyConverter(this);
            priceView.setText(currencyConverter.convertCurrency(cabin.getPrice()) + " " + currencyConverter.addCurrency() + "/" + getResources().getString(R.string.night));
            priceBottomView.setText(Html.fromHtml("<big><b>" + currencyConverter.convertCurrency(cabin.getPrice()) + " " + currencyConverter.addCurrency() + "</b></big> per " +
                    getResources().getString(R.string.night).toLowerCase()));
            descriptionView.setText(cabin.getDescription());
            facilitiesView.setText(cabin.getFacilities());
            rating.setRating(cabin.getRating());
            ratingView.setText(String.valueOf(cabin.getRating()) + "/5");

            phoneButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCall();
                }
            });

            user = databaseService.getUser();

            if (user != null) {

                if (user.getCabins()!=null && user.getCabins().containsKey(cabin.getId())) {
                    button.setText(getResources().getString(R.string.manage_cabin));
                    add.setVisibility(View.GONE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startEdit(cabin.getId());
                        }
                    });
                } else {
                    add.setVisibility(View.VISIBLE);
                    button.setText(getResources().getString(R.string.book_now));
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            showBookDialog();
                        }
                    });
                }
            } else {
                button.setText(getResources().getString(R.string.book_now));
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MessageDialog.newInstance("Ok", getString(R.string.login_book), CabinInfoAcitivty.this).show(getFragmentManager(), getResources().getString(R.string.error_dialog));
                    }
                });
            }

            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onOpenRoute();
                }
            });

            CabinInfoModel cabinInfoModel = new Gson().fromJson(cabin.getCabinInfo(), CabinInfoModel.class);
            if (cabinInfoModel != null) {
                guestsView.setText(cabinInfoModel.getGuests() + " " + getResources().getString(R.string.host_guest).toLowerCase());
                rooomView.setText(cabinInfoModel.getBedrooms() + " " + getResources().getString(R.string.host_bedrooms).toLowerCase());
                bedView.setText(cabinInfoModel.getBeds() + " " + getResources().getString(R.string.host_bed).toLowerCase());
                bathView.setText(cabinInfoModel.getBath() + " " + getResources().getString(R.string.host_bath).toLowerCase());
            }

            if (cabin.getPictures() != null) {
                List<String> listOfImage = new ArrayList<>();
                Iterator myVeryOwnIterator = cabin.getPictures().keySet().iterator();
                while (myVeryOwnIterator.hasNext()) {
                    String key = (String) myVeryOwnIterator.next();
                    String value = (String) cabin.getPictures().get(key);
                    listOfImage.add(value);
                }
                ImageAdapter adapter = new ImageAdapter(this, listOfImage);
                viewPager.setAdapter(adapter);
                viewPager.setOffscreenPageLimit(3);
                mainImage.setVisibility(View.GONE);
            } else {
                mainImage.setVisibility(View.VISIBLE);
            }

            if (cabin.getReviews() != null) {
                float media = 0;
                for (Map.Entry<String, String> entry : cabin.getReviews().entrySet()) {
                    media = media + Float.parseFloat(entry.getValue());
                }
                float rang = media / cabin.getReviews().size();
                ratingView.setText(String.format("%.1f/5", rang));
                rating.setRating(rang);
                ratingBottomView.setText(cabin.getReviews().size() + " " + getResources().getQuantityText(R.plurals.reviews, cabin.getReviews().size()));
                ratingBottomView.setVisibility(View.VISIBLE);
                ratingView.setVisibility(View.VISIBLE);
                rating.setVisibility(View.VISIBLE);
            } else {
                ratingView.setVisibility(View.GONE);
                rating.setVisibility(View.GONE);
                ratingBottomView.setText("0 " + getResources().getQuantityText(R.plurals.reviews, 0));
            }


            FloatingActionButton save = (FloatingActionButton) findViewById(R.id.action_a);
            FloatingActionButton writeReview = (FloatingActionButton) findViewById(R.id.action_b);
            FloatingActionButton uploadPhoto = (FloatingActionButton) findViewById(R.id.action_c);

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CabinInfoAcitivty.this);
            final String id = preferences.getString("uid", "");

            if (id != null && !id.equals("")) {
                save.setVisibility(View.VISIBLE);
            } else {
                save.setVisibility(View.GONE);
            }
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, CabinInfoAcitivty.this);
                    profileOperations.addFavorite(id, cabin.getId());
                    add.collapse();
                }
            });

            writeReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog();
                    add.collapse();
                }
            });

            uploadPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add.collapse();
                }
            });

        }

        progress.setVisibility(View.GONE);
        mainLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onGetCabin(OnGetCabinByIdEvent event) {
        if (event.getCabin() != null) {
            cabin = event.getCabin();
            updateUI();
        } else {

        }
    }

    private void startEdit(String id) {
        Intent intent = new Intent(this, EditCabinInfoActivity.class);
        intent.putExtra("cabin", id);
        startActivity(intent);
    }

    public void onOpenRoute() {
        LocationModel location = new Gson().fromJson(cabin.getLocation(), LocationModel.class);
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?&daddr= " +
                        location.getLatitude() + ", " + location.getLongitude()));
        startActivity(intent);
    }

    public void onCall() {
        if (cabin.getPhone() != null && !cabin.getPhone().equals("")) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + cabin.getPhone()));
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(callIntent);
            } else {
                ActivityCompat.requestPermissions(CabinInfoAcitivty.this, new String[]{Manifest.permission.CALL_PHONE}, 55);
            }

        } else {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("message/rfc822");
            i.putExtra(Intent.EXTRA_EMAIL, new String[]{cabin.getEmail()});
            try {
                startActivity(Intent.createChooser(i, "Send mail..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(CabinInfoAcitivty.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 55: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCall();
                } else {
                    Toast.makeText(CabinInfoAcitivty.this, getResources().getString(R.string.err_perm_call), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private int getPreviousYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -1);
        return prevYear.get(Calendar.YEAR);
    }

    private int getNextYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, +1);
        return prevYear.get(Calendar.YEAR);
    }


    public void showBookDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.book_row);
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        dialog.getWindow().setLayout(width, LinearLayout.LayoutParams.WRAP_CONTENT);
        final AutoCompleteTextView ratingEdit = dialog.findViewById(R.id.book_edit);
        final TextView bookDate = dialog.findViewById(R.id.book_date);
        final DateModel dateModel = new DateModel();


        SmoothDateRangePickerFragment smoothDateRangePickerFragment = SmoothDateRangePickerFragment.newInstance(
                new SmoothDateRangePickerFragment.OnDateRangeSetListener() {
                    @Override
                    public void onDateRangeSet(SmoothDateRangePickerFragment view,
                                               int yearStart, int monthStart,
                                               int dayStart, int yearEnd,
                                               int monthEnd, int dayEnd) {


                        dateModel.setStart(dayStart + "/" + (++monthStart) + "/" + yearStart);
                        dateModel.setEnd(dayEnd + "/" + (++monthEnd) + "/" + yearEnd);
                        bookDate.setText(getResources().getString(R.string.book_date_time, dateModel.getStart(), dateModel.getEnd()));

                    }
                });
        smoothDateRangePickerFragment.setAccentColor(ContextCompat.getColor(this, R.color.seekbar_color));
        smoothDateRangePickerFragment.show(getFragmentManager(), "smoothDateRangePicker");


        TextView dialogButton = dialog.findViewById(R.id.submit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BookModel bookModel = new BookModel();
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CabinInfoAcitivty.this);
                final String id = preferences.getString("uid", "");
                bookModel.setFrom(id);
                bookModel.setDate(new Gson().toJson(dateModel));
                bookModel.setTo(cabin.getIdAdded());
                bookModel.setCabinName(cabin.getName());
                bookModel.setStatus("Pending");
                bookModel.setBookName(user.getUsername());
                bookModel.setCabin(cabin.getId());
                bookModel.setMessage(ratingEdit.getText().toString());
                bookOperations.insertBook(bookModel);
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    public void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.rating_row);

        final RatingBar ratingBar = dialog.findViewById(R.id.rating);
        final AutoCompleteTextView ratingEdit = dialog.findViewById(R.id.rating_edit);


        TextView dialogButton = dialog.findViewById(R.id.submit);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewModel ratingModel = new ReviewModel();
                ratingModel.setCabin(cabin.getId());
                ratingModel.setDetails(ratingEdit.getText().toString());
                ratingModel.setFrom("");
                ratingModel.setRating(String.valueOf(ratingBar.getRating()));
                ratingModel.setDate(Calendar.getInstance().getTime().toString());
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(CabinInfoAcitivty.this);
                final String id = preferences.getString("uid", "");
                if (id != null && !id.equals("")) {
                    ratingModel.setFrom(id);
                } else {
                    ratingModel.setFrom(getResources().getString(R.string.anonim));
                }
                reviewOperations.getReviewFromName(ratingModel);
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Subscribe
    public void onReviewFail(OnReviewFailEvent event) {
        if (!event.isOk) {
            MessageDialog.newInstance("Ok", getResources().getString(R.string.review_message), this).show(getFragmentManager(), getResources().getString(R.string.error_dialog));
        }
    }

    @Subscribe
    public void onCabinInserted(OnInsertEvent event) {
        cabin.setId(id);
        cabinOperations.getCabin(id);
    }


    @Subscribe
    public void onReviewAdded(OnReviewAddEvent event) {
        if (event.getReviewModel() != null) {
            HashMap<String, String> reviews = cabin.getReviews();
            if (reviews == null) {
                reviews = new HashMap<>();
            }
            reviews.put(event.getId(), event.getReviewModel().getRating());
            cabin.setReviews(reviews);
            cabinOperations.insertCabin(cabin);
        } else {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
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
