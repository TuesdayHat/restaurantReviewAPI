package dao;


import models.Foodtype;
import models.Restaurant;
import models.Review;

import java.util.List;

public interface FoodtypeDao {

    //create
    void add(Foodtype foodtype); // Q
    void addFoodtypeToRestaurant(Foodtype foodtype, Restaurant restaurant); // E


    //read
    List<Foodtype> getAll();
    List<Restaurant> getAllRestaurantsForAFoodtype(int id); //E

    //update
    //omit for now

    //delete
    void deleteById(int id); //see above
}