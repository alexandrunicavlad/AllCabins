package com.alexandrunica.allcabins.profile.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.cabins.model.Cabin;
import com.alexandrunica.allcabins.cabins.model.CabinInfoModel;
import com.alexandrunica.allcabins.cabins.model.LocationModel;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

public class HostActivity extends AppCompatActivity {

    private int itemCounter, itemCounterBath, itemCounterBed, itemCounterRoom = 1;
    private final static int PLACE_PICKER_REQUEST = 1;
    private final static int REQUEST_PERMISSION_READ_EXTERNAL = 2;
    private final static int REQUEST_CODE_BROWSE_PICTURE = 3;
    private LinearLayout detailsAddressLayout, photosLayout;
    private AutoCompleteTextView address, city, state, description, facilities, name;
    private Cabin cabin;
    private ImageView photo;
    private List<Uri> userSelectedImageUriList = null;
    private StorageReference mStorage;
    private TextView countGuests, countBedrooms, countBed, countBath;
    private Slidr slidrPrice;

    @Inject
    DatabaseService databaseService;

    @Inject
    Bus bus;

    private CabinOperations cabinOperations;
    private ProfileOperations profileOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host);
        DaggerDbApplication app = ((DaggerDbApplication) this.getApplicationContext());
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);
        cabinOperations = (CabinOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.CABINS_TABLE, this);
        profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, this);
        cabin = new Cabin();
        userSelectedImageUriList = new ArrayList<>();
        mStorage = FirebaseStorage.getInstance().getReference();

        detailsAddressLayout = findViewById(R.id.host_details_address);
        address = findViewById(R.id.host_address);
        city = findViewById(R.id.host_city);
        state = findViewById(R.id.host_state);
        description = findViewById(R.id.host_description);
        facilities = findViewById(R.id.host_facilities);
        name = findViewById(R.id.host_name);
        photo = findViewById(R.id.host_add_image);
        photosLayout = findViewById(R.id.host_images);

        TextView save = findViewById(R.id.host_submit);
        ImageView minusGuests = findViewById(R.id.item_minus);
        ImageView plusGuests = findViewById(R.id.item_plus);
        ImageView minusBed = findViewById(R.id.item_minus_bed);
        ImageView plusBed = findViewById(R.id.item_plus_bed);
        ImageView minusBath = findViewById(R.id.item_minus_bath);
        ImageView plusBath = findViewById(R.id.item_plus_bed_bath);
        ImageView minusBedrooms = findViewById(R.id.item_minus_bedroom);
        ImageView plusBedrooms = findViewById(R.id.item_plus_bedroom);
        countGuests = findViewById(R.id.item_count);
        countBed = findViewById(R.id.item_count_bed);
        countBath = findViewById(R.id.item_count_bed_bath);
        countBedrooms = findViewById(R.id.item_count_bed_room);

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
                    ActivityCompat.requestPermissions(HostActivity.this, requirePermission, REQUEST_PERMISSION_READ_EXTERNAL);
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

        plusGuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCounter += 1;
                countGuests.setText(String.valueOf(itemCounter));
            }
        });

        minusGuests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemCounter >= 2) {
                    itemCounter -= 1;
                    countGuests.setText(String.valueOf(itemCounter));
                }
            }
        });

        plusBath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCounterBath += 1;
                countBath.setText(String.valueOf(itemCounterBath));
            }
        });

        minusBath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemCounterBath >= 2) {
                    itemCounterBath -= 1;
                    countBath.setText(String.valueOf(itemCounterBath));
                }
            }
        });

        plusBed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCounterBed += 1;
                countBed.setText(String.valueOf(itemCounterBed));
            }
        });

        minusBed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemCounterBed >= 2) {
                    itemCounterBed -= 1;
                    countBed.setText(String.valueOf(itemCounterBed));
                }
            }
        });

        plusBedrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemCounterRoom += 1;
                countBedrooms.setText(String.valueOf(itemCounterRoom));
            }
        });

        minusBedrooms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (itemCounterRoom >= 2) {
                    itemCounterRoom -= 1;
                    countBedrooms.setText(String.valueOf(itemCounterRoom));
                }
            }
        });


    }

    private void saveAllInfo() {
        cabin.setState(state.getText().toString());
        cabin.setCity(city.getText().toString());
        cabin.setAddress(address.getText().toString());
        CabinInfoModel cabinInfoModel = new CabinInfoModel(countGuests.getText().toString(), countBed.getText().toString(), countBedrooms.getText().toString(), countBath.getText().toString() );
        cabin.setCabinInfo(new Gson().toJson(cabinInfoModel));
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
                        return;
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
                        return;
                    }
                }
            });
        }
        saveAllInfo();
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
