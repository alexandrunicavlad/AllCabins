package com.alexandrunica.allcabins.dagger;

import com.alexandrunica.allcabins.MainActivity;
import com.alexandrunica.allcabins.cabins.CabinsFragment;
import com.alexandrunica.allcabins.map.MapViewFragment;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.auth.FirebaseAuthentication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Nica on 4/4/2018.
 */

@Component(modules = {AppDbModule.class})
@Singleton
public interface AppDbComponent {

    void inject(MainActivity mainActivity);

    void inject(MapViewFragment mapViewFragment);

    void inject(DaggerDbApplication daggerDbApplication);

    void inject(FirebaseService firebaseService);

    void inject(FirebaseAuthentication firebaseAuthentication);

    void inject(CabinOperations cabinOperations);

    void inject(CabinsFragment cabinsFragment);
}
