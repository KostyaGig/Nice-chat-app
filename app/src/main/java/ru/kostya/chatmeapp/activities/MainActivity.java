package ru.kostya.chatmeapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import ru.kostya.chatmeapp.R;
import ru.kostya.chatmeapp.auth.LoginActivity;
import ru.kostya.chatmeapp.auth.RegisterActivity;
import ru.kostya.chatmeapp.friend.FindFriendActivity;
import ru.kostya.chatmeapp.friend.FriendActivity;
import ru.kostya.chatmeapp.model.Comment;
import ru.kostya.chatmeapp.model.Post;
import ru.kostya.chatmeapp.profile.ProfileActivity;
import ru.kostya.chatmeapp.utils.CommentViewHolder;
import ru.kostya.chatmeapp.utils.PostViewHolder;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    public static final int MAX_LENGTH_DESCRIPTION = 200;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private View headerView;
    private ImageView headerProfileImage;
    private TextView headerUserName;
    private ImageView addImage,sendImage;
    private EditText edPost;

    private RecyclerView postRecView;
    private LinearLayoutManager manager;

    private String imageUrlPost;
    private Uri imageUri;
    public static final int REQUEST_IMAGE = 111;

    private StorageReference storagePosts;

    private DatabaseReference postRef,userRef,likeRef,commentRef;
    private FirebaseUser currentUser;
    private FirebaseRecyclerAdapter<Post, PostViewHolder> postAdapter;
    private FirebaseRecyclerOptions<Post> postOptions;
    private FirebaseRecyclerOptions<Comment> commentOptions;
    private FirebaseRecyclerAdapter<Comment, CommentViewHolder> commentAdapter;
    //В эти поля мы будем записывать url и username пользователя,которые мы получим из DatabaseReference
    private String imageUrlUser,userName;

    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initializeProgressDialog();

        init();

        //C помощью данного трюка мы можем находить вьюшки,находящиеся в headerLayout
        headerView = navigationView.getHeaderView(0);

        //Nav view setListener
        navigationView.setNavigationItemSelectedListener(this);

        //imageView setListener
        addImage.setOnClickListener(this);
        sendImage.setOnClickListener(this);

        postRecView.setLayoutManager(manager);

        loadDataFromFirebaseToDrawerLayout();
        loadPosts();
    }

    private void loadPosts() {
        //Заагрузка постов
        pd.show();
        //post ref - сыылка,откуда recycler view берет данные
        postOptions = new FirebaseRecyclerOptions.Builder<Post>().setQuery(postRef,Post.class).build();
        postAdapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(postOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final PostViewHolder holder, int position, @NonNull Post model) {

                //Получаем ключ поста,он нам нужен для того,чтобы при клике на кнопку Like мы добавляли это ключи в likeRef
                final String postKey = getRef(position).getKey();

                Glide.with(getApplicationContext()).load(model.getUserImage()).into(holder.profileImage);
                Glide.with(getApplicationContext()).load(model.getPostImage()).into(holder.postImage);

                //Сколько времени назад был опубликован пост
                String timeAgo = getTimeAgo(model.getDate());
                holder.userName.setText(model.getUserName());
                holder.dataPost.setText(timeAgo);
                holder.descriptionPost.setText(model.getPostDescription());

                holder.likeCount(postKey,currentUser.getUid(),likeRef);
                holder.commentCount(postKey,currentUser.getUid(),commentRef);
                holder.sendCommentImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //По нажтию на отправить комментарий

                        String comment = holder.edComment.getText().toString().trim();
                        if (comment.isEmpty()){
                            Toast.makeText(MainActivity.this, "Введите комментарий!", Toast.LENGTH_SHORT).show();
                        } else {
                            addComment(holder,postKey,commentRef,currentUser.getUid(),comment);
                        }
                    }
                });

                holder.likeImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Тоесть по нажатию мы проверяем есть ли в ветке likeRef уже такой postKey,с помощью проверки snapshot.exists мы это проверяем,если имеется (тоесть мы уже лайкали данный пост)
                        //То мы просто его удаляем (тоесть отлайкиваем),устанавливаем цвет лайку - серый
                        likeRef.child(postKey).child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()){
                                    likeRef.child(postKey).child(currentUser.getUid()).removeValue();
                                    holder.likeImage.setColorFilter(Color.GRAY);
                                    //Обновляем адаптер,т.к мы изменили цвет у лайка
                                    notifyDataSetChanged();
                                } else {
                                    //Если не лайкали пост
                                    likeRef.child(postKey).child(currentUser.getUid()).setValue("like");
                                    holder.likeImage.setColorFilter(Color.GREEN);
                                    //Обновляем адаптер,т.к мы изменили цвет у лайка
                                    notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(MainActivity.this, "Error like: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                //Загрузить комментарии
                loadCommentsFromFirebase(postKey,holder);

                pd.dismiss();
            }

            @NonNull
            @Override
            public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.post_item,parent,false);
                return new PostViewHolder(view);
            }
        };
        postAdapter.startListening();
        postRecView.setAdapter(postAdapter);
    }

    //Загрузка комментариев из firebase
    private void loadCommentsFromFirebase(String postKey,PostViewHolder holder) {
        //Будем брать данные из commentref.child(postkey)
        commentOptions = new FirebaseRecyclerOptions.Builder<Comment>().setQuery(commentRef.child(postKey),Comment.class).build();
        commentAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(commentOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder, int position, @NonNull Comment model) {
                Glide.with(MainActivity.this).load(model.getProfileImage()).into(holder.profileImage);
                holder.userName.setText(model.getUserName());
                holder.textComment.setText(model.getComment());
            }

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.comment_item,parent,false);
                return new CommentViewHolder(view);
            }
        };
        //Adapter Для комментариев
        commentAdapter.startListening();
        holder.commentRecView.setLayoutManager(new LinearLayoutManager(this));
        holder.commentRecView.setAdapter(commentAdapter);
    }

    private void addComment(final PostViewHolder holder, String postKey, DatabaseReference commentRef, String uid, String comment) {
        //При добавлении комментрария

        HashMap<String,Object> commentMap = new HashMap<>();
        commentMap.put("userName",userName);
        commentMap.put("profileImage",imageUrlUser);
        commentMap.put("comment",comment);

        commentRef.child(postKey).child(uid).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this, "Комментарий был успешно добавлен", Toast.LENGTH_SHORT).show();
                    //Не понимаю,зачем нужно обновлять адаптер
                    postAdapter.notifyDataSetChanged();
                    holder.edComment.setText(null);
                } else {
                    Toast.makeText(MainActivity.this, "Ошибка при добавлении комментария : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String getTimeAgo(String date) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            long time = sdf.parse(date).getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =
                    DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            return ago +"";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void init() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navView);
        addImage = findViewById(R.id.addImage);
        sendImage = findViewById(R.id.sendImage);
        edPost = findViewById(R.id.edPost);

        storagePosts = FirebaseStorage.getInstance().getReference("Post Image");
        postRef = FirebaseDatabase.getInstance().getReference("Posts");
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        likeRef = FirebaseDatabase.getInstance().getReference("Likes");
        commentRef = FirebaseDatabase.getInstance().getReference("Comments");
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        postRecView = findViewById(R.id.postRecView);
        manager = new LinearLayoutManager(this);
    }

    private void loadDataFromFirebaseToDrawerLayout() {
        //Показываем диалог при загрузке данных,чтобы юзер не мог открыть дравер и увидеть там пустую картинку и имя пустое
        //А когда мы будем уверены в получении данных и установлении в headerlayout,то отключаем его
        pd.show();
        userRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //Можем получать не только через модель,но и так:
               imageUrlUser = (String) dataSnapshot.child("ProfileImage").getValue();
               userName = (String) dataSnapshot.child("Name").getValue();

                headerProfileImage = headerView.findViewById(R.id.imageProfile);
                headerUserName = headerView.findViewById(R.id.userName);

               //Устанавливаем полученные данные
                Glide.with(MainActivity.this).load(imageUrlUser).into(headerProfileImage);
                headerUserName.setText(userName);
                //При загрузке нужных данных в headerlayout вырбуаем диалогове окно
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Main Screen");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_menu_24);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
         int id = item.getItemId();

         switch (id){
             case R.id.item_home:

                 break;
             case R.id.item_chat:

                 break;
             case R.id.item_findFriend:
                 Intent findFriendActivity = new Intent(MainActivity.this, FindFriendActivity.class);
                 startActivity(findFriendActivity);
                 break;
             case R.id.item_friend:
                 Intent friendActivity = new Intent(MainActivity.this, FriendActivity.class);
                 startActivity(friendActivity);

             break;
             case R.id.item_profile:
                 Intent profileActivity = new Intent(MainActivity.this, ProfileActivity.class);
                 startActivity(profileActivity);
                 break;
             case R.id.item_logOut:
                 //Выходим из аккаунта
                    FirebaseAuth.getInstance().signOut();
                    Intent registerActivity = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(registerActivity);
                 break;
         }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            //Home - и есть наше гамбургер - меню,т.к мы устанавливаем иконку гамбургер меню вместо back стрелочки

            drawerLayout.openDrawer(GravityCompat.START);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser == null){
            //Если юзер не зареган перебрасываем на вход активити
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void initializeProgressDialog() {
        pd = new ProgressDialog(this);
        pd.setCancelable(false);
        pd.setTitle("Загрузка");
        pd.setMessage("Пожалуйста подождите...");
    }

    @Override
    public void onClick(View view) {
        int id =  view.getId();

        switch (id){
            case R.id.addImage:
                addImagePost();
                break;

            case R.id.sendImage:
                addPost();
                break;
        }
    }

    private void addImagePost() {
        Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        imageIntent.setType("image/*");
        startActivityForResult(imageIntent,REQUEST_IMAGE);
    }

    private void addPost(){

        String postDescription = edPost.getText().toString().trim();
        if (TextUtils.isEmpty(postDescription) || imageUri == null || postDescription.length() > MAX_LENGTH_DESCRIPTION){
            Toast.makeText(this, "Напишите описание,а также добавьте фото, СООБЩЕНИЕ НЕ МОЖЕТ ПРЕВЫШАТЬ 200 СИМВОЛОВ", Toast.LENGTH_SHORT).show();
        } else {
            pd.show();

            storagePosts.child(currentUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        //Если фото успешно зарузилось
                         storagePosts.child(currentUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Наш конечный url
                                imageUrlPost = uri.toString();

                                //Для даты
                                Date date = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                                String stringDate = simpleDateFormat.format(date);

                                //Теперь пушим в postReference Наши данные
                                HashMap<String ,Object> post = new HashMap<>();
                                //Так же добавляем имя и фото юзера,чтобы в recyclerview отображать все нужное сразу
                                post.put("userName",userName);
                                post.put("userImage",imageUrlUser);
                                post.put("postImage",imageUrlPost);
                                post.put("postDescription",edPost.getText().toString());
                                post.put("date",stringDate);
                                post.put("status","offline");
                                postRef.child(currentUser.getUid()).updateChildren(post).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            pd.dismiss();

                                            Toast.makeText(MainActivity.this, "Пост был успешно добавлен!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            pd.dismiss();

                                            Toast.makeText(MainActivity.this, "При добавлении поста возникла ошибка : " + task.getException().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE && data != null){
            imageUri = data.getData();
            addImage.setImageURI(imageUri);
        }

    }

}