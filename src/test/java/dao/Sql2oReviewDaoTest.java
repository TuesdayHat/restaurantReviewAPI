package dao;

import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Sql2o;


import org.sql2o.Connection;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class Sql2oReviewDaoTest {

    private Connection conn;
    private Sql2oReviewDao reviewDao;
    private Sql2oRestaurantDao restaurantDao;
    private Sql2oFoodtypeDao foodtypeDao;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        reviewDao = new Sql2oReviewDao(sql2o);
        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        conn = sql2o.open();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingReviewSetsId() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        Review testReview = new Review("Captain Kirk", 3, "foodcoma!",testRestaurant.getId());
        reviewDao.add(testReview);
        assertEquals(1,testReview.getId());
    }

    @Test
    public void getAllReviewsByRestaurant() throws Exception {

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);


        Restaurant newRestaurant = setupRestaurant(); //add in some extra data to see if it interferes
        restaurantDao.add(newRestaurant);

        int testRestId = testRestaurant.getId();
//        System.out.println(testRestId);

        Review testReview = new Review("Captain Kirk", 3, "foodcoma!", testRestaurant.getId());

        reviewDao.add(testReview);
        System.out.println(reviewDao.getAll().get(0));

        Review otherReview = new Review("Mr. Spock", 1, "passable", testRestaurant.getId());

        reviewDao.add(otherReview);

        Review secondReview = new Review("Mr Spock", 1, "passable", 1234); //to be sure the right one gets deleted, i am adding a second review for a fake restaurant.
        reviewDao.add(secondReview);

        System.out.println(reviewDao.getAllReviewsByRestaurant(testRestaurant.getId()));
        List<Review> someList = reviewDao.getAllReviewsByRestaurant(testRestaurant.getId());

        assertEquals(testReview, reviewDao.getAllReviewsByRestaurant(testRestaurant.getId()).get(0));

    }

    @Test
    public void getAllFoodtypesForARestaurentReturnsFoodtypesCorrectly() throws Exception {
        Foodtype testFoodtype = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Foodtype otherFoodtype = new Foodtype("Bar Food");
        foodtypeDao.add(otherFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        restaurantDao.addRestaurantToFoodtype(testRestaurant, testFoodtype);
        restaurantDao.addRestaurantToFoodtype(testRestaurant, otherFoodtype);

        Foodtype[] foodtypes = {testFoodtype, otherFoodtype};

        assertEquals(restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()), Arrays.asList(foodtypes));
    }

    @Test
    public void deletingRestaurantAlsoUpdatesJoinTable() throws Exception {
        Foodtype testFoodtype = new Foodtype("Seafood");
        foodtypeDao.add(testFoodtype);

        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);

        Restaurant altRestaurant = setupAltRestaurant();
        restaurantDao.add(altRestaurant);

        restaurantDao.addRestaurantToFoodtype(testRestaurant, testFoodtype);
        restaurantDao.addRestaurantToFoodtype(altRestaurant, testFoodtype);

        restaurantDao.deleteById(testRestaurant.getId());
        assertNotEquals(0, restaurantDao.getAllFoodtypesByRestaurant(testRestaurant.getId()).size());
    }

    @Test
    public void timeStampIsReturnedCorrectly() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        Review testReview = new Review("Captain Kirk", 3, "foodcoma", testRestaurant.getId());
        reviewDao.add(testReview);

        long creationTime = testReview.getCreatedat();
        long savedTime = reviewDao.getAll().get(0).getCreatedat();
        String formattedCreationTime = testReview.getFormattedCreatedAt();
        String formattedSavedTime = reviewDao.getAll().get(0).getFormattedCreatedAt();
        assertEquals(creationTime, reviewDao.getAll().get(0).getCreatedat());
        assertEquals(formattedCreationTime, formattedSavedTime);
    }

    @Test
    public void reviewsReturnInRightOrder() throws Exception {
        Restaurant testRestaurant = setupRestaurant();
        restaurantDao.add(testRestaurant);
        Review testReview = new Review("Captain Kirk", 3, "foodcoma!", testRestaurant.getId());
        reviewDao.add(testReview);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        Review testSecondReview = new Review("Mr Spock", 1, "passable", testRestaurant.getId());
        reviewDao.add(testSecondReview);

        assertEquals("passable", reviewDao.getAllReviewsByRestaurantSortedNewestToOldest(testRestaurant.getId()).get(0).getContent());
    }

    //helpers

    public Restaurant setupRestaurant (){
        return new Restaurant("Fish Witch", "214 NE Broadway", "97232", "503-402-9874", "http://fishwitch.com", "hellofishy@fishwitch.com");
    }

    public Restaurant setupAltRestaurant (){
        return new Restaurant("Donut Wolf", "100 Possum Springs Drive", "97000", "503-555-9874", "http://donutwolf.com", "isaac@donutwolf.com");
    }
}
