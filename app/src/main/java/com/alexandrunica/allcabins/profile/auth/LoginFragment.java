package com.alexandrunica.allcabins.profile.auth;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.profile.ProfileFragment;

/**
 * Created by Nica on 4/4/2018.
 */

public class LoginFragment extends Fragment {

    private Activity activity;
    private Toolbar toolbar;

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = (ViewGroup) inflater.inflate(
                R.layout.auth_layout, container, false);
        toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        final RelativeLayout authLayout = view.findViewById(R.id.auth_main_layout);
        final RelativeLayout loginLayout = view.findViewById(R.id.login_main_layout);
        final RelativeLayout registerLayout = view.findViewById(R.id.register_main_layout);
        final RelativeLayout forgotLayout = view.findViewById(R.id.forgot_main_layout);

        ImageView backLogin = view.findViewById(R.id.login_back);
        ImageView backRegister = view.findViewById(R.id.register_back);
        ImageView backForgot = view.findViewById(R.id.forgot_back);

        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.GONE);
                authLayout.setVisibility(View.VISIBLE);
            }
        });

        backRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLayout.setVisibility(View.GONE);
                authLayout.setVisibility(View.VISIBLE);
            }
        });

        backForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
            }
        });

        final TextView loginButton = view.findViewById(R.id.login);
        TextView forgotButton = view.findViewById(R.id.forgot_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.GONE);
                forgotLayout.setVisibility(View.VISIBLE);
            }
        });

        TextView facebookButton = view.findViewById(R.id.facebook_button);
        TextView createButton = view.findViewById(R.id.create_account);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);
            }
        });


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
}