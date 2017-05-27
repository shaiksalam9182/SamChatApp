package com.example.raj.samchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class Users extends AppCompatActivity {


    FirebaseDatabase mdatabase;
    DatabaseReference mdatabasereference;

    ArrayList<Map> userlist;

    ListView showusers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);


        mdatabase = FirebaseDatabase.getInstance();

        mdatabasereference = mdatabase.getReference().child("users");

        showusers = (ListView)findViewById(R.id.showusers);

        userlist = new ArrayList<Map>();


        Query query = mdatabasereference.orderByChild("email");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dsp :dataSnapshot.getChildren()) {
                        userlist.add((Map) (dsp.getValue()));
                    }
                    showusers.setAdapter(new adapter());
                    showusers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String email = userlist.get(position).get("email").toString();
                            Intent var = new Intent(Users.this,Msgsend.class);
                            var.putExtra("email",email);
                            startActivity(var);
                        }
                    });
                }else {
                    Toast.makeText(Users.this,"No Users Registered",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    class adapter extends BaseAdapter{

        @Override
        public int getCount() {
            return userlist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inf = LayoutInflater.from(Users.this);
            View v = inf.inflate(R.layout.userlist,null);
            TextView tv = (TextView)v.findViewById(R.id.tvbindo);
            tv.setText(userlist.get(position).get("email").toString());

            return v;
        }
    }
}
