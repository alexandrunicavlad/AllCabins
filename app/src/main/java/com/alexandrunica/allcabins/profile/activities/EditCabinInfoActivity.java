package com.alexandrunica.allcabins.profile.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.map.event.OnGetCabinByIdEvent;
import com.alexandrunica.allcabins.profile.event.OnInsertEvent;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.alexandrunica.allcabins.widget.Slidr;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class EditCabinInfoActivity extends AppCompatActivity {


    private int itemCounter = 1;
    private final static int PLACE_PICKER_REQUEST = 1;
    private final static int REQUEST_PERMISSION_READ_EXTERNAL = 2;
    private final static int REQUEST_CODE_BROWSE_PICTURE = 3;
    private LinearLayout detailsAddressLayout, photosLayout;
    private AutoCompleteTextView address, city, state, description, facilities, name, phone, email;
    private Cabin cabin;
    private ImageView photo;
    private List<Uri> userSelectedImageUriList = null;
    private StorageReference mStorage;
    private TextView count, title;
    private Slidr slidrPrice;
    private RelativeLayout editLayout, progressLayout;

    @Inject
    DatabaseService databaseService;

    @Inject
    Bus bus;

    private CabinOperations cabinOperations;
    private ProfileOperations profileOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cabin_info);
        DaggerDbApplication app = ((DaggerDbApplication) this.getApplicationContext());
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);
        cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, this);
        profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, this);
        cabin = new Cabin();
        userSelectedImageUriList = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance().getReference();

        String id = getIntent().getStringExtra("cabin");
        cabinOperations.getCabin(id);

        title = findViewById(R.id.host_title);
        detailsAddressLayout = findViewById(R.id.host_details_address);
        address = findViewById(R.id.host_address);
        city = findViewById(R.id.host_city);
        state = findViewById(R.id.host_state);
        description = findViewById(R.id.host_description);
        facilities = findViewById(R.id.host_facilities);
        name = findViewById(R.id.host_name);
        phone = findViewById(R.id.host_phone);
        email = findViewById(R.id.host_email);
        photo = findViewById(R.id.host_add_image);
        photosLayout = findViewById(R.id.host_images);

        progressLayout = findViewById(R.id.progress_layout);
        editLayout = findViewById(R.id.host_main_layout);

        TextView save = findViewById(R.id.host_submit);
        ImageView minus = findViewById(R.id.item_minus);
        ImageView plus = findViewById(R.id.item_plus);
        count = findViewById(R.id.item_count);
        slidrPrice = findViewById(R.id.slidr_price);
        TextView location = findViewById(R.id.host_location);

        ImageView back = findViewById(R.id.host_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int readExternalStoragePermission = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
                if (readExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                    String requirePermission[] = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions(EditCabinInfoActivity.this, requirePermission, REQUEST_PERMISSION_READ_EXTERNAL);
                } else {
                    openPictureGallery();
                }
//                Intent intent = new Intent();
//                intent.setType("image/*");
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_GALLERY);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadPhoto(name.getText().toString());
            }
        });


        slidrPrice.setMax(300);
        slidrPrice.setKm(false);

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

    private void updateUI() {

        name.setText(cabin.getName());
        title.setText(getResources().getString(R.string.manage_title, cabin.getName()));
        address.setText(cabin.getAddress());
        city.setText(cabin.getCity());
        email.setText(cabin.getEmail());
        phone.setText(cabin.getPhone());
        state.setText(cabin.getState());
        count.setText(cabin.getGuests());
        itemCounter = Integer.parseInt(cabin.getGuests());
        slidrPrice.setCurrentValue(Float.valueOf(cabin.getPrice()));
        description.setText(cabin.getDescription());
        facilities.setText(cabin.getFacilities());
        progressLayout.setVisibility(View.GONE);
        editLayout.setVisibility(View.VISIBLE);
    }

    @Subscribe
    public void onGetCabin(OnGetCabinByIdEvent event) {
        if (event.getCabin() != null) {
            cabin = event.getCabin();
            updateUI();
        } else {

        }

    }



    private void saveAllInfo() {
        cabin.setState(state.getText().toString());
        cabin.setCity(city.getText().toString());
        cabin.setAddress(address.getText().toString());
        cabin.setGuests(count.getText().toString());
        cabin.setPrice(String.valueOf(slidrPrice.getCurrentValue()));
        cabin.setDescription(description.getText().toString());
        cabin.setFacilities(facilities.getText().toString());
        cabin.setName(name.getText().toString());
        cabin.setPhone(phone.getText().toString());
        cabin.setEmail(email.getText().toString());
        cabinOperations.insertCabin(cabin);
    }

    private void openPictureGallery() {
        Intent openAlbumIntent = new Intent();
        openAlbumIntent.setType("image/*");
        openAlbumIntent.setAction(Intent.ACTION_GET_CONTENT);
        openAlbumIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(openAlbumIntent, REQUEST_CODE_BROWSE_PICTURE);
    }

    @Subscribe
    public void onCabinInserted(OnInsertEvent event) {
        if (event.isSucces()) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            final String id = preferences.getString("uid", "");
            profileOperations.addCabin(id, event.getCabin());
            finish();
        } else {
            Toast.makeText(this, getResources().getString(R.string.err_add), Toast.LENGTH_SHORT).show();

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
        } else if (requestCode == REQUEST_CODE_BROWSE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount(); //evaluate the count before the for loop --- otherwise, the count is evaluated every loop.
                    for (int i = 0; i < count; i++) {

                        Uri imageUri = data.getClipData().getItemAt(i).getUri();
                        if (imageUri != null) {
                            userSelectedImageUriList.add(imageUri);
                        }
                    }

                } else if (data.getData() != null) {
                    Uri imagePath = data.getData();
                    userSelectedImageUriList.add(imagePath);
                }
                showThumbPhoto();
            }
        }
    }

    private void showThumbPhoto() {
        photosLayout.removeAllViews();
        if (userSelectedImageUriList != null) {
            for (Uri selectedImageUri : userSelectedImageUriList) {
                int imageSize = (int) (60 * getResources().getDisplayMetrics().density);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(imageSize, imageSize, 1f);
                lp.setMargins(8, 8, 8, 8);
                ImageView img = new ImageView(this);
                img.setLayoutParams(lp);
                Picasso.with(this).load(selectedImageUri)
                        .skipMemoryCache()
                        .into(img);

                photosLayout.addView(img);
            }
        }
    }

    private void uploadPhoto(String name) {
        final HashMap<String, String> uploaded = new HashMap<>();
        if (userSelectedImageUriList.size() !=0) {
            for (final Uri selectedImageUri : userSelectedImageUriList) {
                StorageReference fileToUpload = mStorage.child("Images").child(name).child(selectedImageUri.getLastPathSegment());
                fileToUpload.putFile(selectedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        uploaded.put(selectedImageUri.getLastPathSegment(), downloadUrl.toString());
                        if (uploaded.size() == userSelectedImageUriList.size()) {
                            cabin.setPictures(uploaded);
                            cabin.setThumbPhotoUrl(uploaded.entrySet().iterator().next().getValue());
                            saveAllInfo();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        uploaded.put(selectedImageUri.getLastPathSegment(), "");
                        if (uploaded.size() == userSelectedImageUriList.size()) {
                            cabin.setPictures(uploaded);
                            cabin.setThumbPhotoUrl(uploaded.entrySet().iterator().next().getValue());
                            saveAllInfo();
                        }
                    }
                });
            }
        } else {
            saveAllInfo();
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_READ_EXTERNAL) {
            if (grantResults.length > 0) {
                int grantResult = grantResults[0];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    openPictureGallery();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.err_perm), Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
