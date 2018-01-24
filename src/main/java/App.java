import com.google.gson.Gson;
import dao.Sql2oFoodtypeDao;
import dao.Sql2oRestaurantDao;
import dao.Sql2oReviewDao;
import models.Restaurant;
import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

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
            response.type("application/json");
            return gson.toJson(restaurant);
        });

        //READ
        get("/restaurants", "application/json", (request, response) -> {
            response.type("application/json");
            return gson.toJson(restaurantDao.getAll());
        });

        get("/restaurants/:id", "application/json", (request, response) -> {
            response.type("application/json");
            int restaurantId = Integer.parseInt(request.params("id"));
            response.type("application/json");
            return gson.toJson(restaurantDao.findById(restaurantId));
        });

        //FILTERS
        after((req, res) -> {
           res.type("application/json");
        });

        post("/restaurants/:restaurantId/reviews/new", "application/json", (req, res) -> {
            int restaurantId = Integer.parseInt(req.params("restaurantId"));
            Review review = gson.fromJson(req.body(), Review.class);

            review.setRestaurantId(restaurantId); //why do I need to get set separately?
            reviewDao.add(review);
            res.status(201);
            return gson.toJson(review);
        });

//        get("/restaurants"
//        get("/restaurants/:id"
//        post("/restaurants/new"
//        post("/foodtypes/new"
//        get("/foodtypes"
//        get("/restaurants/:id/reviews"
//        post("/restaurants/:restaurantId/reviews/new
    }
}
