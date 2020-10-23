package ru.kostya.chatmeapp.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import ru.kostya.chatmeapp.R;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public ImageView profileImage;
    public TextView userName,textComment;
    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profileImage);
        userName = itemView.findViewById(R.id.userName);
        textComment = itemView.findViewById(R.id.textComment);
    }
}
