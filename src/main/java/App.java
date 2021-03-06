import com.google.gson.Gson;
import dao.Sql2oFoodtypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import exceptions.ApiException;
import models.Foodtype;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;

public class App {
    public static void main(String[] args) {
        Sql2oFoodtypeDao foodtypeDao;
        Sql2oRestaurantDao restaurantDao;
        Sql2oReviewDao reviewDao;
        Connection conn;
        Gson gson = new Gson();

        String connectionString = "jdbc:h2:~/jadle.db;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
//        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/create.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");

        restaurantDao = new Sql2oRestaurantDao(sql2o);
        foodtypeDao = new Sql2oFoodtypeDao(sql2o);
        reviewDao = new Sql2oReviewDao(sql2o);
        conn = sql2o.open();

        //CREATE
        post("/restaurants/new", "application/json", (request, response) -> {
            Restaurant restaurant = gson.fromJson(request.body(), Restaurant.class);
            restaurantDao.add(restaurant);
            response.status(201);
            return gson.toJson(restaurant);
        });

        post("/foodtypes/new", "application/json", (request, response) -> {
            Foodtype foodtype = gson.fromJson(request.body(), Foodtype.class);
            foodtypeDao.add(foodtype);
            response.status(201);
            return gson.toJson(foodtype);
        });

        post("/restaurants/:restaurantId/reviews/new", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            Review review = gson.fromJson(req.body(), Review.class);
            review.setCreatedat();
            review.setRestaurantId(restaurantId); //why do I need to get set separately?
//            System.out.println(review.getContent() + " --- " + review.getId());
            reviewDao.add(review);
            res.status(201);
            return gson.toJson(review);
        });

        //READ
        get("/restaurants", "application/json", (request, response) -> {
            return gson.toJson(restaurantDao.getAll());
        });

        get("/foodtypes", "application/json", (request, response) -> {
            return gson.toJson(reviewDao.getAll());
        });

        get("/restaurants/:id", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("id"));

            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);

            if (restaurantToFind == null){
                throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", request.params("id")));
            }
            return gson.toJson(restaurantDao.findById(restaurantId));
        });

        get("/restaurants/:id/reviews", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("id"));


            List<Review> allReviews = reviewDao.getAllReviewsByRestaurant(restaurantId);
//            System.out.println(allReviews.get(0).getContent());
            return gson.toJson(allReviews);
        });

        get("/restaurants/:id/foodtypes", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            if (restaurantToFind == null){
                throw new ApiException(404, String.format("No restaurant with the id: \'%s\' exists", request.params("id")));
            } else if (restaurantDao.getAllFoodtypesByRestaurant(restaurantId).size()==0){
                return "{\"message\":\"I'm sorry, but no food types are listed for this restaurant.\"}";
            } else {
                return gson.toJson(restaurantDao.getAllFoodtypesByRestaurant(restaurantId));
            }
        });

        get("/foodtypes/:id/restaurants", "application/json", (request, response) -> {
            int foodtypeId = Integer.parseInt(request.params("id"));
            Foodtype foodtypeToFind = foodtypeDao.findById(foodtypeId);
            if (foodtypeToFind == null){
                throw new ApiException(404, String.format("No foodtype with the id: \"%s\" exists", request.params("id")));
            }
            else if (foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId).size()==0){
                return "{\"message\":\"I'm sorry, but no restaurants are listed for this foodtype.\"}";
            }
            else {
                return gson.toJson(foodtypeDao.getAllRestaurantsForAFoodtype(foodtypeId));
            }
        });

        post("/restaurants/:restaurantId/foodtype/:foodtypeId", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("restaurantId"));
            int foodtypeId = Integer.parseInt(request.params("foodtypeId"));
            Restaurant restaurant = restaurantDao.findById(restaurantId);
            Foodtype foodtype = foodtypeDao.findById(foodtypeId);

            if (restaurant != null && foodtype != null) {
                foodtypeDao.addFoodtypeToRestaurant(foodtype, restaurant);
                response.status(201);
                return gson.toJson(String.format("Restaurant '%s' and Foodtype '%s' have been associated", restaurant.getName(), foodtype.getName()));
            } else {
                throw new ApiException(404, String.format("Restaurant or Foodtype does not exist"));
            }
        });

        post("/restaurants/:restaurantId/reviews/new", "application/json", (request, response) -> {
            int restaurantId = Integer.parseInt(request.params("restaurantId"));
            Review review = gson.fromJson(request.body(), Review.class);
            review.setCreatedat();
            review.setFormattedCreatedAt();
            review.setRestaurantId(restaurantId);
            reviewDao.add(review);
            response.status(201);
            return gson.toJson(review);
        });

        get("/restaurants/:id/sortedReviews", "application/json", (req, res) -> { //// TODO: 1/18/18 generalize this route so that it can be used to return either sorted reviews or unsorted ones.
            int restaurantId = Integer.parseInt(req.params("id"));
            Restaurant restaurantToFind = restaurantDao.findById(restaurantId);
            List<Review> allReviews;
            if (restaurantToFind == null){
                throw new ApiException(404, String.format("No restaurant with the id: \"%s\" exists", req.params("id")));
            }
            allReviews = reviewDao.getAllReviewsByRestaurantSortedNewestToOldest(restaurantId);
            return gson.toJson(allReviews);
        });

        //FILTERS
        exception(ApiException.class, (exception, request, response) -> {
            ApiException err = (ApiException) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatusCode());
            jsonMap.put("errorMessage", err.getMessage());
            response.type("application/json");
            response.status(err.getStatusCode());
            response.body(gson.toJson(jsonMap));
        });

        after((req, res) -> {
           res.type("application/json");
        });




//        post("/foodtypes/new"
//        get("/foodtypes"

//        post("/restaurants/:restaurantId/reviews/new
    }
}
