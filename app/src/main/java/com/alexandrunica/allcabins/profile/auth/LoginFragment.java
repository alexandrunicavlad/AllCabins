package com.alexandrunica.allcabins.profile.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alexandrunica.allcabins.R;
import com.alexandrunica.allcabins.dagger.AppDbComponent;
import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.ProfileFragment;
import com.alexandrunica.allcabins.profile.event.OnLoginEvent;
import com.alexandrunica.allcabins.profile.event.OnOpenAccount;
import com.alexandrunica.allcabins.profile.event.OnRegisterDoneEvent;
import com.alexandrunica.allcabins.profile.model.User;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
import com.alexandrunica.allcabins.service.firebase.auth.FirebaseAuthentication;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import java.util.Arrays;

import javax.inject.Inject;

import static com.facebook.GraphRequest.TAG;

/**
 * Created by Nica on 4/4/2018.
 */

public class LoginFragment extends Fragment {

    @Inject
    FirebaseAuthentication authentication;

    @Inject
    Bus bus;

    private Activity activity;
    private Toolbar toolbar;
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;

    public static LoginFragment newInstance() {
        LoginFragment loginFragment = new LoginFragment();
        return loginFragment;
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
                R.layout.auth_layout, container, false);
        toolbar = activity.findViewById(R.id.toolbar);
        toolbar.setVisibility(View.GONE);
        final RelativeLayout authLayout = view.findViewById(R.id.auth_main_layout);
        final RelativeLayout loginLayout = view.findViewById(R.id.login_main_layout);
        final RelativeLayout registerLayout = view.findViewById(R.id.register_main_layout);
        final RelativeLayout forgotLayout = view.findViewById(R.id.forgot_main_layout);

        final TextView loginButton = view.findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
            }
        });


        TextView createButton = view.findViewById(R.id.create_account);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authLayout.setVisibility(View.GONE);
                registerLayout.setVisibility(View.VISIBLE);
            }
        });

        //login

        final AutoCompleteTextView loginEmail = view.findViewById(R.id.login_email);
        final AutoCompleteTextView loginPassword = view.findViewById(R.id.login_password);
        TextView loginSubmit = view.findViewById(R.id.login_submit);
        ImageView backLogin = view.findViewById(R.id.login_back);
        TextView forgotButton = view.findViewById(R.id.forgot_button);

        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.GONE);
                authLayout.setVisibility(View.VISIBLE);
                loginEmail.setError(null);
                loginPassword.setError(null);
                loginEmail.setText("");
                loginEmail.setText("");

            }
        });

        forgotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginLayout.setVisibility(View.GONE);
                forgotLayout.setVisibility(View.VISIBLE);
                loginEmail.setError(null);
                loginPassword.setError(null);
                loginEmail.setText("");
                loginEmail.setText("");
            }
        });

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginEmail.setText("alexandru.nica@freshbyteinc.com");
                loginPassword.setText("Nelu@123");
                loginEmail.setError(null);
                loginPassword.setError(null);
                boolean cancel = false;
                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();
                if (email.equals("")) {
                    loginEmail.setError(getResources().getString(R.string.login_field));
                    cancel = true;
                }
                if (password.equals("")) {
                    loginPassword.setError(getResources().getString(R.string.login_field));
                    cancel = true;
                }
                if (cancel) {

                } else {
                    authentication.login(email, password, activity);
                }
            }
        });

        //register
        final AutoCompleteTextView registerName = view.findViewById(R.id.register_name);
        final AutoCompleteTextView registerEmail = view.findViewById(R.id.register_email);
        final AutoCompleteTextView registerPassword = view.findViewById(R.id.register_password);
        final AutoCompleteTextView registerConfirmPassword = view.findViewById(R.id.register_confirm_password);
        TextView registerSubmit = view.findViewById(R.id.register_submit);
        ImageView backRegister = view.findViewById(R.id.register_back);

        backRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerLayout.setVisibility(View.GONE);
                authLayout.setVisibility(View.VISIBLE);
                registerName.setError(null);
                registerEmail.setError(null);
                registerPassword.setError(null);
                registerConfirmPassword.setError(null);
                registerName.setText("");
                registerEmail.setText("");
                registerPassword.setText("");
                registerConfirmPassword.setText("");
            }
        });

        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerName.setError(null);
                registerEmail.setError(null);
                registerPassword.setError(null);
                registerConfirmPassword.setError(null);

                boolean cancel = false;
                String name = registerName.getText().toString();
                String email = registerEmail.getText().toString();
                String password = registerPassword.getText().toString();
                String confirmPassword = registerConfirmPassword.getText().toString();

                if (confirmPassword.equals("")) {
                    registerConfirmPassword.setError(getResources().getString(R.string.login_field));
                    cancel = true;
                }

                if (password.equals("")) {
                    registerPassword.setError(getResources().getString(R.string.login_field));
                    cancel = true;
                }
                if (email.equals("")) {
                    registerEmail.setError(getResources().getString(R.string.login_field));
                    cancel = true;
                }
                if (name.equals("")) {
                    registerName.setError(getResources().getString(R.string.login_field));
                    cancel = true;
                }
                if (cancel) {
                } else {
                    authentication.register(name, email, password, activity);
                }
            }
        });

        //forgot
        final AutoCompleteTextView forgotEmail = view.findViewById(R.id.forgot_email);
        TextView forgotSubmit = view.findViewById(R.id.forgot_submit);
        ImageView backForgot = view.findViewById(R.id.forgot_back);

        backForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotLayout.setVisibility(View.GONE);
                loginLayout.setVisibility(View.VISIBLE);
                forgotEmail.setError(null);
                forgotEmail.setText("");
            }
        });

        forgotSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotEmail.setError(null);
                boolean cancel = false;
                String email = forgotEmail.getText().toString();
                if (email.equals("")) {
                    forgotEmail.setError(getResources().getString(R.string.login_field));
                    cancel = true;
                }
                if (cancel) {
                } else {
                    //forgot
                }
            }
        });
        facebookLogin(view);

        return view;
    }

    @Subscribe
    public void onUserLogin(OnLoginEvent event){
        if(event.isLogged()){
            User user = new User();
            String id = authentication.getUserUid();
            if(id == null){
                //showMessage(activity.getString(R.string.ok_btn),activity.getString(R.string.error_string));
                return;
            }
            FirebaseUser userFirebase = FirebaseAuth.getInstance().getCurrentUser();
            user.setId(id);
            user.setEmail(event.getEmail());
            user.setUsername(userFirebase.getDisplayName());
            ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, activity);
            profileOperations.checkandInsertProfileExists(user);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("uid", id);
            editor.apply();
            profileOperations.getUser(id);
            //openAccount();
        }
        else{
            //error
        }
    }

    private void facebookLogin(View view) {
        mCallbackManager = CallbackManager.Factory.create();
        TextView facebookButton = view.findViewById(R.id.facebook_button);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(activity, Arrays.asList("email", "public_profile"));
            }
        });

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                authentication.handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe
    public void onRegisterEvent(OnRegisterDoneEvent event) {
        if (event.isRegistered()) {
            User user = new User();
            String id = authentication.getUserUid();
            if (id == null) {
                //showMessage(activity.getString(R.string.ok_btn),activity.getString(R.string.error_string));
                return;
            }
            user.setId(id);
            user.setEmail(event.getEmail());
            user.setUsername(event.getName());
            ProfileOperations profileOperations = (ProfileOperations) FirebaseService.getFirebaseOperation(FirebaseService.TableNames.USERS_TABLE, activity);
            profileOperations.checkandInsertProfileExists(user);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("uid", id);
            editor.apply();

            //openAccount();
        } else {
            if (event.isExistEmail()) {
                Toast.makeText(activity, getResources().getString(R.string.register_exist), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(activity, getResources().getString(R.string.register_err), Toast.LENGTH_SHORT).show();
        }
    }

//   private void openAccount() {
//       bus.post(new OnOpenAccount(ProfileFragment.newInstance(user)));
//    }

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.activity = (Activity) context;
    }
}