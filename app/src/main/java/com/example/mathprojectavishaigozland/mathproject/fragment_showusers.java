package com.example.mathprojectavishaigozland.mathproject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mathprojectavishaigozland.R;
import com.google.gson.Gson;

import java.util.ArrayList;


public class fragment_showusers extends Fragment {

    // הגדרת תכונות המחלקה עבור רכיבי הממשק והנתונים
    EditText userName;
    TextView score;
    TextView raiting;
    Button add_picture;
    ImageView picture;
    Button add_user;
    User myUser;
    Uri uri;
    Log log;
    // אובייקט האחראי על קבלת התוצאה מהמצלמה ועדכון הממשק בהתאם
    ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    //בדיקה שהפעולה הצלאיחה- שהמשתמש אכן צילם תמונה
                    if (result.getResultCode() == Activity.RESULT_OK) {

                        //הפיכת ה- ImageButton לנראה
                        picture.setVisibility(View.VISIBLE);

                        //הצבת התמונה שצולמה בתוך ה- ImageButton
                        picture.setImageURI(uri);

                        myUser.setUri(uri);
                    }
                }
            }
    );
    ArrayList<User> usersList = new ArrayList<>();

    //ArrayList<User> = new ArrayList<>();
    private RecyclerView rvShowUsers;
    private RecyclerView.Adapter MyUserAdapter;

    //פעולה ליצירת תצוגת הפרגמנט, קישור רכיבי ה UI ושליפת נתונים
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // העברת ה-Layout של ה-Fragment לתוך ה-View
        View view = inflater.inflate(R.layout.fragment_showusers, container, false);

        //אתחול וקישור רכיבי הממשק מה-XML למשתנים בקוד
        userName = view.findViewById(R.id.et_user);
        score = view.findViewById(R.id.tv_score);
        raiting = view.findViewById(R.id.tv_rating);
        add_picture = view.findViewById(R.id.btn_add_pic);
        picture = view.findViewById(R.id.ib_pic);
        add_user = view.findViewById(R.id.btn_add_user);
        rvShowUsers = view.findViewById(R.id.rvShowUsers);


        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAddUser();


            }
        });

        // קריאה לפעולה שתראה את כל המשתמשים הקיימים
        getAllUsers();

        add_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //יצירת תוכן זמני לתמונה
                ContentValues values = new ContentValues();

                values.put(MediaStore.Images.Media.TITLE, "New Picture");
                values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

                uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startCamera.launch(cameraIntent);

            }
        });

        //המרת מחרוזת ה-Json בחזרה לסוג User
        String userStr = getArguments().getString("myUser");
        Gson gson = new Gson();
        myUser = gson.fromJson(userStr, User.class);

        //עדכון רכיבי ה-UI עם הנתונים שנשלחו מהאובייקט
        userName.setText("User name: " + myUser.getUserName());
        score.setText("Youre score: " + myUser.getScore());
        raiting.setText("Your raiting: " + myUser.getRating());


        return view;
    }

    public void dbAddUser() {
        // יצירת אובייקט מטיפוס dbHelper
        DBHelper dbHelper = new DBHelper(requireActivity());

        long id = dbHelper.insert(myUser, requireActivity());

        getAllUsers();
    }

    public void getAllUsers() {
        DBHelper dbHelper = new DBHelper(requireActivity());
        usersList = dbHelper.selectAll();
        showUsers();
        int n = 0;
    }

    private void showUsers() {
        MyUsersAdapter myUsersAdapter = new MyUsersAdapter(usersList, new InterMyOnItemClickListener() {
            @Override
            public void onItemClick(User item) {
                Toast.makeText(requireActivity(), "you clicked on " + item.getUserName(), Toast.LENGTH_SHORT).show();
            }
        });
        rvShowUsers.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvShowUsers.setAdapter(MyUserAdapter);
        rvShowUsers.setHasFixedSize(true);
    }
}