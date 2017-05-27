package com.example.raj.samchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Msgsend extends AppCompatActivity {

    EditText msgedittext;
    Button bsend;
    String msg = "",sender = "",receiver = "";
    StringBuilder str = new StringBuilder();
    TextView tv;
    FirebaseDatabase mdatabase;
    DatabaseReference mdatabasereference;
    ArrayList<Map> userlist;

    SharedPreferences pref;
    SharedPreferences.Editor edit;

    ListView lsview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msgsend);

        msgedittext = (EditText)findViewById(R.id.messageEditText);

        lsview = (ListView) findViewById(R.id.msgshow);

        pref = getApplicationContext().getSharedPreferences("mypref",0);
        edit = pref.edit();


        receiver = getIntent().getStringExtra("email");
        sender = pref.getString("email","Anonymus");



        mdatabase = FirebaseDatabase.getInstance();

        mdatabasereference = mdatabase.getReference().child("messages");


        bsend = (Button)findViewById(R.id.sendButton);

        userlist = new ArrayList<Map>();




        totalmethod();









        bsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg = msgedittext.getText().toString();

                String key = mdatabasereference.push().getKey();
                mdatabasereference.child(key).child("sender").setValue(sender);
                mdatabasereference.child(key).child("receiver").setValue(receiver);
                mdatabasereference.child(key).child("msg").setValue(msg);

                mdatabasereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Toast.makeText(Msgsend.this,"message sent",Toast.LENGTH_LONG).show();
                        totalmethod();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(Msgsend.this,"Unable to send messages",Toast.LENGTH_LONG).show();
                    }
                });

            }
        });





    }

    private void totalmethod() {
        Query query = mdatabasereference.orderByChild("sender").equalTo(sender);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dsp :dataSnapshot.getChildren()) {
                        userlist.add((Map) (dsp.getValue()));
                    }
                    lsview.setAdapter(new adaapter());
                }else {
                    Toast.makeText(Msgsend.this,"No Data Found",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    class adaapter extends BaseAdapter{

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
            LayoutInflater inf = LayoutInflater.from(Msgsend.this);
            View v = inf.inflate(R.layout.single,null);
            TextView tv = (TextView)v.findViewById(R.id.tvshow);

            if (position>3){
                tv.setGravity(Gravity.RIGHT);
                tv.setText(userlist.get(position).get("msg").toString());
            }else{
                tv.setGravity(Gravity.LEFT);
                tv.setText(userlist.get(position).get("msg").toString());
            }
            //Toast.makeText(Msgsend.this, userlist.get(position).get("msg").toString(), Toast.LENGTH_SHORT).show();
            return v;
        }
    }




}
