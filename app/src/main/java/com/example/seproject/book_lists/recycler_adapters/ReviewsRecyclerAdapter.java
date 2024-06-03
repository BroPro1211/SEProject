package com.example.seproject.book_lists.recycler_adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.seproject.R;
import com.example.seproject.book_lists.BookDetailsFragment;
import com.example.seproject.data_classes.FBref;
import com.example.seproject.data_classes.Review;
import com.example.seproject.data_classes.User;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReviewsRecyclerAdapter extends ListRecyclerAdapter<ReviewsRecyclerAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public static class ReviewViewHolder extends RecyclerView.ViewHolder{
        private TextView reviewUsernameTV;
        private RatingBar reviewStarsRatingBar;
        private TextView reviewDateTV;
        private TextView reviewDescriptionV;
        private ImageButton deleteReviewButton;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);

            reviewUsernameTV = itemView.findViewById(R.id.reviewUsernameTV);
            reviewStarsRatingBar = itemView.findViewById(R.id.reviewStarsRatingBar);
            reviewDateTV = itemView.findViewById(R.id.reviewDateTV);
            reviewDescriptionV = itemView.findViewById(R.id.reviewDescriptionV);
            deleteReviewButton = itemView.findViewById(R.id.deleteReviewButton);
        }

        public TextView getReviewUsernameTV() {
            return reviewUsernameTV;
        }

        public RatingBar getReviewStarsRatingBar() {
            return reviewStarsRatingBar;
        }

        public TextView getReviewDateTV() {
            return reviewDateTV;
        }

        public TextView getReviewDescriptionV() {
            return reviewDescriptionV;
        }

        public ImageButton getDeleteReviewButton() {
            return deleteReviewButton;
        }
    }


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


        if (review.isCurrentUserReview()){
            ImageButton deleteReviewButton = holder.getDeleteReviewButton();
            deleteReviewButton.setVisibility(View.VISIBLE);
            deleteReviewButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((BookDetailsFragment)fragment).deleteUserReview();
                }
            });
        }
    }
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
