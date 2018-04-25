package com.alexandrunica.allcabins.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.auth.LoginFragment;
import com.alexandrunica.allcabins.profile.event.OnOpenAccount;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.profile.model.UserAddressModel;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static com.alexandrunica.allcabins.profile.util.PictureConverter.bitMapToString;
import static com.alexandrunica.allcabins.profile.util.PictureConverter.stringToBitMap;

public class EditProfileFragment extends Fragment {

    private Activity activity;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private CircleImageView image;
    private User user;

    @Inject
    Bus bus;

    @Inject
    DatabaseService databaseService;

    public static EditProfileFragment newInstance(User user) {
        EditProfileFragment editProfileFragment = new EditProfileFragment();
        if (user != null) {
            Bundle bundle = new Bundle();
            bundle.putString("user", new Gson().toJson(user));
            editProfileFragment.setArguments(bundle);
        }
        return editProfileFragment;
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DaggerDbApplication app = (DaggerDbApplication) activity.getApplicationContext();
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.edit_profile_fragment, container, false);

        if (getArguments() != null) {
            user = new Gson().fromJson(getArguments().getString("user"), new TypeToken<User>() {
            }.getType());
            TextView save = view.findViewById(R.id.profile_edit_submit);


            final AutoCompleteTextView nameText = view.findViewById(R.id.profile_edit_name);
            final AutoCompleteTextView streetText = view.findViewById(R.id.profile_edit_address);
            final AutoCompleteTextView cityText = view.findViewById(R.id.profile_edit_city);
            final AutoCompleteTextView stateText = view.findViewById(R.id.profile_edit_state);
            final AutoCompleteTextView phoneText = view.findViewById(R.id.profile_edit_phone);
            image = view.findViewById(R.id.profile_image);
            TextView editPhoto = view.findViewById(R.id.profile_edit_photo);
            ImageView bakc = view.findViewById(R.id.cabin_close);

            editPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dispatchTakePictureIntent();
                }
            });

            bakc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.post(new OnOpenAccount(ProfileFragment.newInstance(user)));
                }
            });

            if (user.getUsername() != null) {
                nameText.setText(user.getUsername());
            }

            if (user.getProfilePhoto() != null) {
                image.setImageBitmap(stringToBitMap(user.getProfilePhoto()));
            }

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameText.getText().toString();
                    String address = streetText.getText().toString();
                    String city = cityText.getText().toString();
                    String state = stateText.getText().toString();
                    String phone = phoneText.getText().toString();


                    if (!name.isEmpty()) {
                        user.setUsername(name);
                    }

                    UserAddressModel addressModel = new UserAddressModel();

                    if (!address.isEmpty()) {
                        addressModel.setStreet(address);
                    }

                    if (!city.isEmpty()) {
                        addressModel.setCity(city);
                    }

                    if (!state.isEmpty()) {
                        addressModel.setState(state);
                    }

                    if (!phone.isEmpty()) {
                        user.setPhone(phone);
                    }

                    user.setAddressModel(addressModel);
                    final ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, activity);
                    profileOperations.insertUser(user);
                    bus.post(new OnOpenAccount(ProfileFragment.newInstance(user)));
                }
            });
        }


        return view;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            if (user != null && imageBitmap != null) {
                user.setProfilePhoto(bitMapToString(imageBitmap));
            }
            image.setImageBitmap(imageBitmap);
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        bus.unregister(this);
    }

}

