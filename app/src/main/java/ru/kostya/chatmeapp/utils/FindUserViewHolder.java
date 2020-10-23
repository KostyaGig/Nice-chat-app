package ru.kostya.chatmeapp.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.kostya.chatmeapp.R;

public class FindUserViewHolder extends RecyclerView.ViewHolder {

    public ImageView profileImage;
    public TextView userName,userProfession;

    public FindUserViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImage = itemView.findViewById(R.id.profileImage);
        userName = itemView.findViewById(R.id.userName);
        userProfession = itemView.findViewById(R.id.userProfession);
    }
}
