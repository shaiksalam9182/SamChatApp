package com.example.raj.samchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    EditText etuname,etpwd;
    Button btlogin,btgmail;

    FirebaseAuth mauth;

    GoogleApiClient mapiclinet;

    FirebaseAuth.AuthStateListener mauthlistener;




    SharedPreferences pref;
    SharedPreferences.Editor editor;

    FirebaseDatabase mdatabase;
    DatabaseReference mdatabasereference;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etuname = (EditText)findViewById(R.id.etuname);
        etpwd = (EditText)findViewById(R.id.etpwd);

        btlogin = (Button)findViewById(R.id.blogin);
        btgmail = (Button)findViewById(R.id.bgmail);

       pref = getApplicationContext().getSharedPreferences("mypref",0);
        editor = pref.edit();

        mdatabase = FirebaseDatabase.getInstance();
        mdatabasereference = mdatabase.getReference().child("users");


        String sta = pref.getString("status","nothing");
        if (sta.equals("nothing")){
            Toast.makeText(MainActivity.this,"Signed out",Toast.LENGTH_LONG).show();
        }else {
            startActivity(new Intent(MainActivity.this,Users.class));
        }



       GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
               .requestIdToken(getString(R.string.default_web_client_id))
               .requestEmail()
               .build();


        mapiclinet = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,this)
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        mauth = FirebaseAuth.getInstance();

        mauthlistener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();



                if (user!=null){
                    Toast.makeText(MainActivity.this,user.getUid(),Toast.LENGTH_LONG).show();
                    editor.putString("status","loggedin");
                    editor.putString("email",user.getEmail());
                    editor.commit();

                    Query query = mdatabasereference.orderByChild("email").equalTo(user.getEmail());
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){

                            }else {
                                String key = mdatabasereference.push().getKey();
                                mdatabasereference.child(key).child("email").setValue(user.getEmail());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }else {
                    Toast.makeText(MainActivity.this,"Signed Out",Toast.LENGTH_LONG).show();

                }

            }
        };



        btlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
        
        btgmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sigininwithgamail();
            }

        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mauth.addAuthStateListener(mauthlistener);
    }





    private void sigininwithgamail() {
        Intent singinin = Auth.GoogleSignInApi.getSignInIntent(mapiclinet);
        startActivityForResult(singinin,9001);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==9001){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                Toast.makeText(MainActivity.this,result.toString(),Toast.LENGTH_LONG).show();
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseauthwithgoogle(account);
            }else {
                //updatebutton(null);
            }
        }
    }

    private void firebaseauthwithgoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Status"+task.isSuccessful(),Toast.LENGTH_LONG).show();
                           //btgmail.setText(email);
                            startActivity(new Intent(MainActivity.this,Users.class));
                            editor.putString("status","loggedin");
                            editor.commit();
                        }else {
                            Toast.makeText(MainActivity.this,"Status"+task.getException(),Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainActivity.this,"Google Play Service Error",Toast.LENGTH_LONG).show();

    }
}
