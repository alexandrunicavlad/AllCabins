package com.alexandrunica.allcabins.profile.activities;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.event.OnInsertEvent;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.widget.Slidr;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class HostActivity extends AppCompatActivity {

    private int itemCounter = 1;
    private int PLACE_PICKER_REQUEST = 1;
    private LinearLayout detailsAddressLayout;
    private AutoCompleteTextView address, city, state, description, facilities, name;
    private Cabin cabin;

    @Inject
    DatabaseService databaseService;

    @Inject
    Bus bus;

    private CabinOperations cabinOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        DaggerDbApplication app = ((DaggerDbApplication) this.getApplicationContext());
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);
        cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, this);
        cabin = new Cabin();

        detailsAddressLayout = findViewById(R.id.host_details_address);
        address = findViewById(R.id.host_address);
        city = findViewById(R.id.host_city);
        state = findViewById(R.id.host_state);
        description = findViewById(R.id.host_description);
        facilities = findViewById(R.id.host_facilities);
        name = findViewById(R.id.host_name);

        TextView save = findViewById(R.id.host_submit);
        ImageView minus = findViewById(R.id.item_minus);
        ImageView plus = findViewById(R.id.item_plus);
        final TextView count = findViewById(R.id.item_count);
        final Slidr slidrPrice = findViewById(R.id.slidr_price);
        TextView location = findViewById(R.id.host_location);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cabin.setState(state.getText().toString());
                cabin.setCity(city.getText().toString());
                cabin.setAddress(address.getText().toString());
                cabin.setGuests(count.getText().toString());
                cabin.setPrice(String.valueOf(slidrPrice.getCurrentValue()));
                cabin.setDescription(description.getText().toString());
                cabin.setFacilities(facilities.getText().toString());
                cabin.setName(name.getText().toString());
                User user = databaseService.getUser();
                if (user != null) {
                    cabin.setPhone(user.getPhone());
                    cabin.setEmail(user.getEmail());
                    cabin.setIdAdded(user.getId());
                }
                cabinOperations.insertCabin(cabin);
            }
        });

        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(HostActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        slidrPrice.setMax(300);
        slidrPrice.setKm(false);
        slidrPrice.setCurrentValue(50);

        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCounter += 1;
                count.setText(String.valueOf(itemCounter));
            }
        });

        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemCounter >= 2) {
                    itemCounter -= 1;
                    count.setText(String.valueOf(itemCounter));
                }

            }
        });
    }

    @Subscribe
    public void onCabinInserted(OnInsertEvent event) {
        if (event.isSucces()) {
            finish();
        } else {
            Toast.makeText(this, "Failed to add", Toast.LENGTH_SHORT).show();

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                getDetailsFromCordinate(place.getLatLng());
            }
        }
    }

    private void getDetailsFromCordinate(LatLng latLng) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            LocationModel locationModel = new LocationModel(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
            address.setText(addresses.get(0).getAddressLine(0));
            city.setText(addresses.get(0).getLocality());
            state.setText(addresses.get(0).getAdminArea());
            detailsAddressLayout.setVisibility(View.VISIBLE);
            cabin.setAddress(addresses.get(0).getAddressLine(0));
            cabin.setCity(addresses.get(0).getLocality());
            cabin.setState(addresses.get(0).getAdminArea());
            cabin.setLocation(new Gson().toJson(locationModel));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getCordinateFromAddress(String address) {
        Geocoder geocoder = new Geocoder(this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocationName(address, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                String a = "@1";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
