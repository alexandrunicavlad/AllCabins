package com.alexandrunica.allcabins.profile;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.auth.LoginFragment;
import com.alexandrunica.allcabins.profile.event.OnOpenAccount;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Nica on 4/5/2018.
 */

public class ProfileAuthFragment extends Fragment {

    private Activity activity;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;

    @Inject
    Bus bus;

    @Inject
    DatabaseService databaseService;


    public static ProfileAuthFragment newInstance() {
        ProfileAuthFragment profileFragment = new ProfileAuthFragment();
        return profileFragment;
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.profilelogin_layout, container, false);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String id = preferences.getString("uid", "");
        Fragment fragment;
        if (!id.equals("")) {
            User user = databaseService.getUser();
            if (user != null) {
                fragment = ProfileFragment.newInstance(user);
            } else {
                fragment = ProfileFragment.newInstance(null);
            }
        } else
            fragment = LoginFragment.newInstance();

        getFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment).commit();

        return view;
    }

    @Subscribe
    public void replaceFragment(OnOpenAccount event) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, event.getFragment());
        transaction.addToBackStack(null);
        transaction.commit();
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
