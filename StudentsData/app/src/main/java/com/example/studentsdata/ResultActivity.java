package com.example.studentsdata;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.studentsdata.databinding.ActivityResultBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;
    private DatabaseReference databaseReference;
    private List<StudentClassModel> modelList;
    private ResultAdapter adapter;
    String str;

    Dialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityResultBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);


        binding.resultRv.setHasFixedSize(true);
        binding.resultRv.setLayoutManager(new LinearLayoutManager(this));

        modelList = new ArrayList<>();
        Intent intent = getIntent();
        str = intent.getStringExtra("no");

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Searching "+str);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);


        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("StudentsData").orderByChild("studentRollNo").startAt(str).endAt(str+"uf8ff");


        progressDialog.show();
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        StudentClassModel model = dataSnapshot.getValue(StudentClassModel.class);
                        modelList.add(model);
                        progressDialog.dismiss();
                    }

                }else{
                    Toast.makeText(getApplicationContext(),"No data Found",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    openDialog();

                }

                adapter = new ResultAdapter(ResultActivity.this,modelList);
                binding.resultRv.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.fabBtn.setOnClickListener( v ->{

            startActivity(new Intent(ResultActivity.this,UploadData.class));

        });

    }

    private void openDialog() {

        dialog.setContentView(R.layout.no_data_layout);
        TextView rollNumber = dialog.findViewById(R.id.roll_number);
        AppCompatButton button = dialog.findViewById(R.id.continueBtn);
        dialog.show();

        rollNumber.setText(str);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ResultActivity.this, SearchActivity.class));
                finish();

            }
        });



    }
}