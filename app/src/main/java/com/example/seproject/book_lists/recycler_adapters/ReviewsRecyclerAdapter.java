package com.example.seproject.book_lists.recycler_adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seproject.MainActivity;
import com.example.seproject.R;
import com.example.seproject.book_lists.BookDetailsFragment;
import com.example.seproject.data_classes.Review;


import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * @author		Daniel Bronfenbrener
 * @version     1.0
 * @since       04/06/2024
 * Recycler adapter to display a book's reviews
 */
public class ReviewsRecyclerAdapter extends ListRecyclerAdapter<ReviewsRecyclerAdapter.ReviewViewHolder, BookDetailsFragment> {

    private final List<Review> reviews;

    /**
     * View holder for a review
     */
    public static class ReviewViewHolder extends RecyclerView.ViewHolder{
        private final TextView reviewUsernameTV;
        private final RatingBar reviewStarsRatingBar;
        private final TextView reviewDateTV;
        private final TextView reviewDescriptionV;
        private final ImageButton deleteReviewButton;
        private final ProgressBar imageProgressBar;
        private final CardView imageCardView;
        private final ImageView profileImageView;
        private final ImageView likeImageView;
        private final TextView likesNumberTV;


        /**
         * Initializes the review view holder
         * @param itemView The view holder's view
         */
        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewUsernameTV = itemView.findViewById(R.id.reviewUsernameTV);
            reviewStarsRatingBar = itemView.findViewById(R.id.reviewStarsRatingBar);
            reviewDateTV = itemView.findViewById(R.id.reviewDateTV);
            reviewDescriptionV = itemView.findViewById(R.id.reviewDescriptionV);

            deleteReviewButton = itemView.findViewById(R.id.deleteReviewButton);

            profileImageView = itemView.findViewById(R.id.profileImageView);
            imageProgressBar = itemView.findViewById(R.id.imageProgressBar);
            imageCardView = itemView.findViewById(R.id.imageCardView);

            likeImageView = itemView.findViewById(R.id.likeImageView);
            likesNumberTV = itemView.findViewById(R.id.likesNumberTV);

            likeImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = getAbsoluteAdapterPosition();
                    ReviewsRecyclerAdapter adapter = (ReviewsRecyclerAdapter)getBindingAdapter();
                    Review review = adapter.reviews.get(pos);

                    if (review.getUserLikesMap().containsKey(MainActivity.getCurrentUser().getUid())){
                        ((BookDetailsFragment)adapter.fragment).removeReviewLikeFromFB(pos);
                    }
                    else{
                        ((BookDetailsFragment)adapter.fragment).addReviewLikeToFB(pos);
                    }
                }
            });
        }

        /**
         * Returns the username text view
         * @return The reviewUsernameTV
         */
        public TextView getReviewUsernameTV() {
            return reviewUsernameTV;
        }
        /**
         * Returns the review stars rating bar
         * @return The reviewStarsRatingBar
         */
        public RatingBar getReviewStarsRatingBar() {
            return reviewStarsRatingBar;
        }
        /**
         * Returns the review date text view
         * @return The reviewDateTV
         */
        public TextView getReviewDateTV() {
            return reviewDateTV;
        }
        /**
         * Returns the review description text view
         * @return The reviewDescriptionV
         */
        public TextView getReviewDescriptionV() {
            return reviewDescriptionV;
        }
        /**
         * Returns the delete review button
         * @return The deleteReviewButton
         */
        public ImageButton getDeleteReviewButton() {
            return deleteReviewButton;
        }

        /**
         * Returns the like image view
         * @return The likeImageView
         */
        public ImageView getLikeImageView(){
            return likeImageView;
        }
        /**
         * Returns the likes number text view
         * @return The likesNumberTV
         */
        public TextView getLikesNumberTV(){
            return likesNumberTV;
        }

        /**
         * Displays the user's profile image. If user doesn't have profile image, shows the default profile image.
         * @param context The context
         * @param image The profile image
         */
        public void showProfileImage(Context context, Bitmap image) {
            imageProgressBar.setVisibility(View.GONE);
            imageCardView.setVisibility(View.VISIBLE);

            if (image != null)
                profileImageView.setImageBitmap(image);
            else
                profileImageView.setImageBitmap(MainActivity.getBitmapFromDrawable(context, R.drawable.baseline_person_gray_100));

        }
        /**
         * Hides the profile image and shows the progress bar
         */
        public void hideProfileImage(){
            imageProgressBar.setVisibility(View.VISIBLE);
            imageCardView.setVisibility(View.INVISIBLE);
        }



    }

    /**
     * Constructor for the adapter
     * @param fragment The fragment displaying this book
     * @param reviews The list of reviews
     */
    public ReviewsRecyclerAdapter(BookDetailsFragment fragment, List<Review> reviews) {
        super(fragment);
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_recycler_layout, parent, false);
        return new ReviewsRecyclerAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.getReviewUsernameTV().setText(review.getUsername());
        holder.getReviewStarsRatingBar().setRating(review.getStarNum());
        holder.getReviewDateTV().setText(convertMillisecondsToDate(review.getTimeAdded()));

        String description = review.getReviewText();
        TextView reviewDescriptionTV = holder.getReviewDescriptionV();
        reviewDescriptionTV.setText(description);
        if (description.length() != 0){
            reviewDescriptionTV.setOnClickListener(new View.OnClickListener() {
                private boolean showingFullDescription = false;
                @Override
                public void onClick(View v) {
                    if (!showingFullDescription)
                        reviewDescriptionTV.setMaxLines(Integer.MAX_VALUE);
                    else
                        reviewDescriptionTV.setMaxLines(1);
                    showingFullDescription = ! showingFullDescription;
                }
            });
        }

        Map<String, Boolean> userLikesMap = review.getUserLikesMap();
        if (userLikesMap.containsKey(MainActivity.getCurrentUser().getUid())){
            holder.getLikeImageView().setImageBitmap(MainActivity.getBitmapFromDrawable(fragment.getContext(), R.drawable.baseline_thumb_up_24));
        }
        else{
            holder.getLikeImageView().setImageBitmap(MainActivity.getBitmapFromDrawable(fragment.getContext(), R.drawable.baseline_thumb_up_off_alt_24));
        }

        int size = userLikesMap.size();
        if (size == 0){
            holder.getLikesNumberTV().setVisibility(View.GONE);
        }
        else{
            holder.getLikesNumberTV().setVisibility(View.VISIBLE);
            holder.getLikesNumberTV().setText(String.valueOf(size));
        }

        ImageButton deleteReviewButton = holder.getDeleteReviewButton();
        if (review.isCurrentUserReview()){
            deleteReviewButton.setVisibility(View.VISIBLE);
            deleteReviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BookDetailsFragment)fragment).deleteUserReview();
                }
            });
        }
        else{
            deleteReviewButton.setVisibility(View.GONE);
        }

        if (review.getUserProfileImage() == null)
            holder.hideProfileImage();
        else
            holder.showProfileImage(fragment.getContext(), review.getUserProfileImage());

    }

    /**
     * Converts the time in milliseconds to a formatted date
     * @param timeInMilliseconds Time in milliseconds
     * @return Formatted date
     */
    private String convertMillisecondsToDate(long timeInMilliseconds){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMilliseconds);

        return String.format("%1$02d/%2$02d/%3$02d",
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.YEAR));

    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }



}
