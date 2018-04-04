package com.alexandrunica.allcabins.dagger;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import com.alexandrunica.allcabins.service.firebase.auth.FirebaseAuthentication;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

/**
 * Created by Nica on 4/4/2018.
 */

public class DaggerDbApplication extends Application {

    private static DaggerDbApplication application;
    private AppDbComponent appDbComponent;

    @Inject
    FirebaseAuthentication firebaseAuthentication;

    @Inject
    Bus bus;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        appDbComponent = DaggerAppDbComponent.builder()
                .appDbModule(new AppDbModule(this))
                .build();
        appDbComponent.inject(this);
        bus.register(this);
        firebaseAuthentication.logIn();

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        bus.unregister(this);
    }

    public static DaggerDbApplication getApplication() {
        return application;
    }

    public AppDbComponent getAppDbComponent() {
        return appDbComponent;
    }

}

