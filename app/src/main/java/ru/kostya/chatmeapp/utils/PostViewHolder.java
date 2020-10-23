package ru.kostya.chatmeapp.utils;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import ru.kostya.chatmeapp.R;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public ImageView profileImage,postImage,likeImage,commentImage,sendCommentImage;
    public TextView userName,dataPost,descriptionPost,likeCounter,commentCounter;
    public RecyclerView commentRecView;
    public EditText edComment;
    public PostViewHolder(@NonNull View itemView) {
        super(itemView);

        profileImage = itemView.findViewById(R.id.imageProfile);
        postImage = itemView.findViewById(R.id.imagePost);
        likeImage = itemView.findViewById(R.id.like);
        commentImage = itemView.findViewById(R.id.comment);
        sendCommentImage = itemView.findViewById(R.id.sendComment);

        userName = itemView.findViewById(R.id.userName);
        dataPost = itemView.findViewById(R.id.dataPost);
        descriptionPost = itemView.findViewById(R.id.descriptionPost);
        likeCounter = itemView.findViewById(R.id.likeCounter);
        commentCounter = itemView.findViewById(R.id.commentCounter);

        commentRecView = itemView.findViewById(R.id.commentRecView);
        edComment = itemView.findViewById(R.id.edComment);
    }

    public void likeCount(String postKey, final String uid, DatabaseReference likeRef) {
        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Таким образом мы получаем детей у likeref.child(postkey),тоесть получаем детей postkey
                    //А дети postKey ЭТО ЕСТЬ юзеры,которые лайкнули данный пост
                    //И в нашу пееременную записываем колличество детей postkey (данного поста),тоесть записываем колличество юзеров)))

                    int totalLikes = (int) snapshot.getChildrenCount();
                    likeCounter.setText(String.valueOf(totalLikes));
                } else {
                    likeCounter.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        likeRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(uid).exists()){
                    likeImage.setColorFilter(Color.GREEN);
                } else {
                    likeImage.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void commentCount(String postKey, final String uid, DatabaseReference commentRef) {
        commentRef.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    //Таким образом мы получаем детей у likeref.child(postkey),тоесть получаем детей postkey
                    //А дети postKey ЭТО ЕСТЬ юзеры,которые лайкнули данный пост
                    //И в нашу пееременную записываем колличество детей postkey (данного поста),тоесть записываем колличество юзеров)))

                    int totalComments = (int) snapshot.getChildrenCount();
                    commentCounter.setText(String.valueOf(totalComments));
                } else {
                    commentCounter.setText("");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}
