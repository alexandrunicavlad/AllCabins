package com.alexandrunica.allcabins.service.firebase.auth;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.alexandrunica.allcabins.dagger.DaggerDbApplication;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.otto.Bus;

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


    public void login(final String email, String password, Activity activity){

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //bus.post(new OnLoginEvent(true, email));
                        }
                        else{
                           // bus.post(new OnLoginEvent(false, email));
                        }
                    }
                });
    }

    public void register(final String name , final String email, String password, Activity activity){
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //bus.post(new OnRegisterDoneEvent(true, email, name));
                        }
                        else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                //bus.post(new OnRegisterDoneEvent(false, true, email, name));
                            }

                                //bus.post(new OnRegisterDoneEvent(false, email, name));
                        }
                    }
                });
    }

    public void forgot(final String email){
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            //bus.post(new OnForgotEvent(true, email));
                        }
                        //else
                            //.post(new OnForgotEvent(false, email));
                    }
                });
    }

    public void logout(){
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

    public String getUserUid(){
        FirebaseUser user =  firebaseAuth.getCurrentUser();
        if(user == null)
            return null;
        else
            return user.getUid();
    }
}