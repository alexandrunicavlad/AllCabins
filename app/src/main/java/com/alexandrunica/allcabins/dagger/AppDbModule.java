package com.alexandrunica.allcabins.dagger;

import android.app.Application;
import android.content.Context;
import android.provider.SyncStateContract;

import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.auth.FirebaseAuthentication;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.otto.Bus;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Nica on 4/4/2018.
 */

@Module
@Singleton
public class AppDbModule {

    DaggerDbApplication app;

    public AppDbModule(DaggerDbApplication application) {
        this.app = application;
    }

    /**
     * @return application instance
     */
    @Provides
    @Singleton
    public Application provideApplication() {
        return app;
    }

    /**
     * @return the instance of the FirebaseAuth object
     */
    @Provides
    @Singleton
    public FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }


    /**
     * @return the instance of the FirebaseAuth object
     */
    @Provides
    @Singleton
    public FirebaseAuthentication provideFirebaseAuthentification() {
        return new FirebaseAuthentication(app);
    }

    /**
     * @return an instance of the cloud Firebase database
     */
    @Provides
    @Singleton
    public FirebaseDatabase provideFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }

    /**
     * @return an instance of a class providing access to operations on Firebase
     */
    @Provides
    @Singleton
    public FirebaseService provideFirebaseService() {
        return new FirebaseService(app);
    }

    @Provides
    @Singleton
    public DatabaseService provideDatabaseService() {
        return new DatabaseService(app);
    }


    /**
     * @return the context of the application
     */
    @Provides
    @Singleton
    public Context provideContext() {
        return app.getApplicationContext();
    }

    /**
     * @return an instance of the Otto bus
     */
    @Provides
    @Singleton
    public Bus provideOttoBus() {
        return new Bus();
    }

    /**
     * @return an instance of Gson
     */
    @Provides
    @Singleton
    public Gson provideGson() {
        return new Gson();
    }



}
