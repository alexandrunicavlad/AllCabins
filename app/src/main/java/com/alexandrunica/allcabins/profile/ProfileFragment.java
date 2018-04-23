package com.alexandrunica.allcabins.profile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.auth.LoginFragment;
import com.alexandrunica.allcabins.profile.event.OnOpenAccount;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.otto.Bus;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by Nica on 4/2/2018.
 */

public class ProfileFragment extends Fragment {

    private Activity activity;


    public static ProfileFragment newInstance(User user) {
        ProfileFragment profileFragment = new ProfileFragment();
        if (user != null) {
            Bundle bundle = new Bundle();
            bundle.putString("user", new Gson().toJson(user));
            profileFragment.setArguments(bundle);
        }
        return profileFragment;
    }

    @Inject
    Bus bus;

    @Inject
    DatabaseService databaseService;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        DaggerDbApplication app = (DaggerDbApplication) activity.getApplicationContext();
        AppDbComponent appDbComponent = app.getAppDbComponent();
        appDbComponent.inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.profile_fragment, container, false);

        if (getArguments() != null) {
            User user = new Gson().fromJson(getArguments().getString("user"), new TypeToken<User>() {
            }.getType());
            TextView logout = view.findViewById(R.id.logout_account);

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("email", "");
                    editor.apply();
                    databaseService.deleteUser();
                    bus.post(new OnOpenAccount(LoginFragment.newInstance()));
                }
            });

            TextView nameText = view.findViewById(R.id.profile_name);
            TextView emailText = view.findViewById(R.id.profile_email);
            ImageView image = view.findViewById(R.id.profile_image);

            if (user.getEmail() != null) {
                emailText.setText(user.getEmail());
            }

            if (user.getUsername() != null) {
                nameText.setText(user.getUsername());
            }

            if (user.getProfilePhoto() != null) {
                Picasso.with(activity).load(user.getProfilePhoto()).into(image);
            }
        }


        return view;
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
