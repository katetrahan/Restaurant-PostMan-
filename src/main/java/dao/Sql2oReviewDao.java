package dao;


import models.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.Collections;
import java.util.List;

public class Sql2oReviewDao implements ReviewDao{

    Sql2o sql2o;

    public Sql2oReviewDao(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Review review){
        String sql = "INSERT INTO reviews (writtenBy, rating, restaurantId, content, createdat) VALUES (:writtenBy, :rating, :restaurantId, :content, :createdat)";
        try (Connection con = sql2o.open()){
            int id = (int) con.createQuery(sql)
                    .bind(review)
                    .executeUpdate()
                    .getKey();
            review.setId(id);
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Review> getAll(){
        String sql = "SELECT * FROM reviews";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .executeAndFetch(Review.class);
        }
    }

    @Override
    public List<Review> getAllReviewsByRestaurant(int restaurantId){
        String sql = "SELECT * FROM reviews WHERE restaurantId = :restaurantId";
        try (Connection con = sql2o.open()) {
            return con.createQuery(sql)
                    .addParameter("restaurantId", restaurantId)
                    .executeAndFetch(Review.class);
        }
    }

    @Override
    public void deleteById(int id){
        String sql = "DELETE FROM reviews WHERE id = :id";
        try (Connection con = sql2o.open()) {
            con.createQuery(sql)
                    .addParameter("id", id)
                    .executeUpdate();
        } catch (Sql2oException ex) {
            System.out.println(ex);
        }
    }

    @Override
    public List<Review> getAllReviewsByRestaurantSortedNewestToOldest(int restaurantId) {
        List<Review> sortedReviews = getAllReviewsByRestaurant(restaurantId);
        int swaps = 1;
        int compared;

        while (swaps != 0) {
            swaps = 0;
            for (int i = 0; i < (sortedReviews.size() - 1); i++) {
                Review current = sortedReviews.get(i);
                Review next = sortedReviews.get(i + 1);
                compared = current.compareTo(next);
                if (compared == -1) {
                    Collections.swap(sortedReviews, i, i+1);
                    swaps++;
                }
            }
        }
        return sortedReviews;
    }
}
