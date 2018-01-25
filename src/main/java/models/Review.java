package models;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class Review implements Comparable<Review>{

    private int id;
    private String writtenBy;
    private int rating;
    private String content;
    private int restaurantId;
    private long createdat;
    private String formattedCreatedAt;

    public Review(String writtenBy, int rating, String nContent, int restaurantId) {
        this.writtenBy = writtenBy;
        this.rating = rating;
        this.content = nContent;
        this.restaurantId = restaurantId;
        this.createdat = System.currentTimeMillis();
        setFormattedCreatedAt();
    }

    @Override
    public int compareTo(Review reviewObject) {
        if (this.createdat < reviewObject.createdat) {
            return -1;
        } else if (this.createdat > reviewObject.createdat) {
            return 1;
        } else {
            return 0;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWrittenBy() {
        return writtenBy;
    }

    public void setWrittenBy(String writtenBy) {
        this.writtenBy = writtenBy;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(int restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setCreatedat() {
        this.createdat = System.currentTimeMillis();
    }
    public long getCreatedat() {
        return createdat;
    }

    public String getFormattedCreatedAt(){
        Date date = new Date(createdat);
        String datePatternToUse = "MM/dd/yyyy @ K:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternToUse);
        return sdf.format(date);
    }

    public void setFormattedCreatedAt(){
        Date date = new Date(this.createdat);
        String datePatternToUse = "MM/dd/yyyy @ K:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(datePatternToUse);
        this.formattedCreatedAt = sdf.format(date);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (id != review.id) return false;
        if (rating != review.rating) return false;
        if (restaurantId != review.restaurantId) return false;
        if (writtenBy != null ? !writtenBy.equals(review.writtenBy) : review.writtenBy != null) return false;
        return content != null ? content.equals(review.content) : review.content == null;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (writtenBy != null ? writtenBy.hashCode() : 0);
        result = 31 * result + rating;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + restaurantId;
        return result;
    }
}