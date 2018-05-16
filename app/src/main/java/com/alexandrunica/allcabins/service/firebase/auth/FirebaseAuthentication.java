package com.alexandrunica.allcabins.service.firebase.auth;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.alexandrunica.allcabins.profile.event.OnLoginEvent;
import com.alexandrunica.allcabins.profile.event.OnRegisterDoneEvent;
import com.alexandrunica.allcabins.profile.event.OnSocialLoginEvent;
import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.otto.Bus;

import java.util.concurrent.Executor;

import javax.inject.Inject;

/**
 * Created by Nica on 4/4/2018.
 */

public class FirebaseAuthentication {

    @Inject
    FirebaseAuth firebaseAuth;

    @Inject
    Context context;

    @Inject
    Bus bus;

    public FirebaseAuthentication(DaggerDbApplication app) {
        app.getAppDbComponent().inject(this);
    }


    public void login(final String email, String password, Activity activity) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bus.post(new OnLoginEvent(true, email));
                        } else {
                            bus.post(new OnLoginEvent(false, email));
                        }
                    }
                });
    }

    public void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    bus.post(new OnRegisterDoneEvent(true, user.getEmail(), user.getDisplayName()));
                } else {
                    bus.post(new OnRegisterDoneEvent(false, "", ""));
                }
            }
        });
    }

    public void googleLogin(final GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user != null) {
                        bus.post(new OnSocialLoginEvent(true, user));
                    } else {
                        bus.post(new OnSocialLoginEvent(false, user));
                    }
                } else {
                    bus.post(new OnSocialLoginEvent(false, null));

                }
            }
        });
    }

    public void register(final String name, final String email, String password, Activity activity) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bus.post(new OnRegisterDoneEvent(true, email, name));
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                bus.post(new OnRegisterDoneEvent(false, true, email, name));
                            }

                            bus.post(new OnRegisterDoneEvent(false, email, name));
                        }
                    }
                });
    }

    public void forgot(final String email) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //bus.post(new OnForgotEvent(true, email));
                        }
                        //else
                        //.post(new OnForgotEvent(false, email));
                    }
                });
    }

    public void logout() {
        //bus.post(new OnUserLogout());
        firebaseAuth.signOut();
        logIn();
    }


    public void logIn() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null) {
            firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //PreferenceHelper.getPreference(context).edit().putBoolean(context.getString(R.string.is_logged_anonim), true).apply();
                        //bus.post(new OnUserAnonimLogin(true));
                    } else {
                        //PreferenceHelper.getPreference(context).edit().putBoolean(context.getString(R.string.is_logged_anonim), false).apply();
                        //bus.post(new OnUserAnonimLogin(false));

                    }

                }
            });
        }
    }

    public FirebaseUser getFirebaseUser() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
            return null;
        else
            return user;
    }

    public String getUserUid() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null)
            return null;
        else
            return user.getUid();
    }
}