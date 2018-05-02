package com.alexandrunica.allcabins.dagger;

import com.alexandrunica.allcabins.MainActivity;
import com.alexandrunica.allcabins.cabins.CabinsFragment;
import com.alexandrunica.allcabins.explore.ExploreFragment;
import com.alexandrunica.allcabins.favorite.FavoriteFragment;
import com.alexandrunica.allcabins.map.MapViewFragment;
import com.alexandrunica.allcabins.profile.EditProfileFragment;
import com.alexandrunica.allcabins.profile.ProfileAuthFragment;
import com.alexandrunica.allcabins.profile.ProfileFragment;
import com.alexandrunica.allcabins.profile.activities.HostActivity;
import com.alexandrunica.allcabins.profile.auth.LoginFragment;
import com.alexandrunica.allcabins.service.database.DatabaseService;
import com.alexandrunica.allcabins.service.firebase.CabinOperations;
import com.alexandrunica.allcabins.service.firebase.FirebaseService;
import com.alexandrunica.allcabins.service.firebase.ProfileOperations;
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

    void inject(HostActivity hostActivity);

    void inject(MapViewFragment mapViewFragment);

    void inject(DaggerDbApplication daggerDbApplication);

    void inject(FirebaseService firebaseService);

    void inject(FirebaseAuthentication firebaseAuthentication);

    void inject(CabinOperations cabinOperations);

    void inject(ProfileOperations profileOperations);

    void inject(CabinsFragment cabinsFragment);

    void inject(LoginFragment loginFragment);

    void inject(ProfileAuthFragment profileAuthFragment);

    void inject(ProfileFragment profileFragment);

    void inject(EditProfileFragment editProfileFragment);

    void inject(FavoriteFragment favoriteFragment);

    void inject(ExploreFragment exploreFragment);

    void inject(DatabaseService databaseService);
}
