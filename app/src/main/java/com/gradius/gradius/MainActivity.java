package com.gradius.gradius;


//DataBase Rules
//Drivers => Table store info about Location of Driver available =>Common.driver_tbl
//PickupRequest => Table store info about Pickup Request of User =>Common.PickupRequest
//Rider => Table store info about User who register from Rider app =>Common.user_rider_tbl
//Users => Table store info about Driver who register from Driver app =>Common.user_driver_tbl

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gradius.gradius.Common.Common;
import com.gradius.gradius.Model.User;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {


    Button btnSingIn,btnRegister;
    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Fonts
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                                            .setDefaultFontPath("fonts/Keep Singing.ttf")
                                            .setFontAttrId(R.attr.fontPath).build());
        setContentView(R.layout.activity_main);


        //Init Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance();
        users = db.getReference(Common.user_driver_tbl);

        //Init View
        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnSingIn = (Button)findViewById(R.id.btnSignIn);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);

        //Event
        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showRegisterDialog();
            }
        });

        btnSingIn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showLoginDialog();
            }
        });

    }



    private void showLoginDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("SIGN IN");
        dialog.setMessage("Please use email to sign in");

        LayoutInflater inflater = LayoutInflater.from(this);
        View login_layout = inflater.inflate(R.layout.layout_login,null);

        final MaterialEditText edtEmail = (MaterialEditText) login_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = (MaterialEditText) login_layout.findViewById(R.id.edtPassword);

        dialog.setView(login_layout);

        //Set button
        dialog.setPositiveButton("SIGN IN",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                        //Set disable button Sing In if is processing
                        btnSingIn.setEnabled(false);

                        //Check validation
                        if (TextUtils.isEmpty(edtEmail.getText().toString())) {
                            Snackbar.make(rootLayout, "Pleas enter email address", Snackbar.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        if (TextUtils.isEmpty(edtPassword.getText().toString())) {
                            Snackbar.make(rootLayout, "Pleas enter password", Snackbar.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        if (edtPassword.getText().toString().length() < 6) {
                            Snackbar.make(rootLayout, "Password too short !!!", Snackbar.LENGTH_LONG)
                                    .show();
                            return;
                        }

                        final AlertDialog waitingDialog = new SpotsDialog(MainActivity.this);     //loding bar
                        waitingDialog.show();     //on/off

                        //Login
                        auth.signInWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        waitingDialog.dismiss();  //loading bar

                                        FirebaseDatabase.getInstance().getReference(Common.user_driver_tbl)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Common.currentUser = dataSnapshot.getValue(User.class);
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });

                                        startActivity(new Intent(MainActivity.this, Welcome.class));
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                waitingDialog.dismiss(); //loading bar
                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG)
                                        .show();

                                //Active button
                                btnSingIn.setEnabled(true);

                            }
                        });
                    }
                });
        dialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    private void showRegisterDialog(){
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("REGISTER");
        dialog.setMessage("Please use email to register");

        LayoutInflater inflater = LayoutInflater.from(this);
        View register_layout = inflater.inflate(R.layout.layout_register,null);

        final MaterialEditText edtEmail = (MaterialEditText) register_layout.findViewById(R.id.edtEmail);
        final MaterialEditText edtPassword = (MaterialEditText) register_layout.findViewById(R.id.edtPassword);
        final MaterialEditText edtName = (MaterialEditText) register_layout.findViewById(R.id.edtName);
        final MaterialEditText edtPhone = (MaterialEditText) register_layout.findViewById(R.id.edtPhone);

        dialog.setView(register_layout);

        //Set button
        dialog.setPositiveButton("REGISTER",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){

                dialogInterface.dismiss();

                //Check validation
                if (TextUtils.isEmpty(edtEmail.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Pleas enter email address",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                if (TextUtils.isEmpty(edtPhone.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Pleas enter phone number",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                if (TextUtils.isEmpty(edtPassword.getText().toString()))
                {
                    Snackbar.make(rootLayout,"Pleas enter password",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                if (edtPassword.getText().toString().length()<6)
                {
                    Snackbar.make(rootLayout,"Password too short !!!",Snackbar.LENGTH_LONG)
                            .show();
                    return;
                }

                //Register new user
                auth.createUserWithEmailAndPassword(edtEmail.getText().toString(),edtPassword.getText().toString())
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                //Save user to db
                                User user = new User();
                                user.setEmail(edtEmail.getText().toString());
                                user.setName(edtName.getText().toString());
                                user.setPhone(edtPhone.getText().toString());
                                user.setPassword(edtPassword.getText().toString());

                                //Use email to key
                                users.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Snackbar.make(rootLayout, "Register successfully !!!", Snackbar.LENGTH_LONG)
                                                        .show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG)
                                                        .show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Snackbar.make(rootLayout, "Failed " + e.getMessage(), Snackbar.LENGTH_LONG)
                                        .show();
                            }
                        });
            }
        });

        dialog.setNegativeButton("CANCEL",new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i){
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed(){
        System.gc();
        System.exit(0);
    }
}
