package com.example.espapp10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ScannerNetwork extends AppCompatActivity {
    //dialog box variable initialization
    Dialog custom,outofroom,heartrate,heartratenormal,heartratenm,patientstatus,patientstatuswr;
    MediaPlayer alert;
    ImageView dialogClose,dialogheartclose,dialogheartnormalclose,dialogheartnmclose,dialogstatusclose,dialogstatuswrclose;
    Button dialogOK,dialogHeartOK,dialogHeartnormalOK,dialogHeartnmOK,dialogstatusok,dialogstatuswrok;
    Handler handler = new Handler();
    int delay = 60000; //milliseconds
    TextView dialogRoomName,dialogOutlierTime,dialogTitle,Heartstatus,Heartstatusnormal,PatientStatusTxt,PatientStatuswrTxt;
    String heartval,statusval;

    Button goProfile,goHome,goPattern,goToday,goMean,goHeartrate,goPatientstatus;

    TextView scanner2_result,scanner1_result,scanner3_result,scanner4_result;



    DatabaseReference mRef,mRef3;
    DatabaseReference databaseReference;
    DatabaseReference heartrateRef,statusRef;
    private String heartcondition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_scanner_network);
        goProfile = findViewById(R.id.profile_btn);
        goHome = findViewById(R.id.home_btn);
        goPattern=findViewById(R.id.pattern_btn);
        goToday=findViewById(R.id.today_btn);
        //goTomorrow=findViewById(R.id.tomorrow_btn);
        goMean=findViewById(R.id.mean_btn);
        goHeartrate=findViewById(R.id.heartrate_btn);
        goPatientstatus=findViewById(R.id.status_btn);


        ///////////////////////////push notification//////////////////////////////////////
        mRef3=FirebaseDatabase.getInstance().getReference("Status: ");
        mRef3.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(String.class).toLowerCase().equals("falling")){
                    //Toast.makeText(getApplicationContext(),"falling",Toast.LENGTH_SHORT).show();
                    String message="The patient has fallen down.";
                    NotificationCompat.Builder builder=new NotificationCompat.Builder(
                            ScannerNetwork.this
                    ).setSmallIcon(R.drawable.ic_baseline_accessible)
                            .setContentTitle("Alert")
                            .setContentText(message)
                            .setAutoCancel(true);
                    NotificationManager notificationmanager=(NotificationManager)getSystemService(
                            Context.NOTIFICATION_SERVICE
                    );
                    notificationmanager.notify(0,builder.build());
                }
                else{
                    //Toast.makeText(getApplicationContext(),dataSnapshot.getValue(String.class),Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////
        heartrate=new Dialog(ScannerNetwork.this);
        heartratenormal=new Dialog(ScannerNetwork.this);
        heartratenm=new Dialog(ScannerNetwork.this);
        heartrate.setContentView(R.layout.heartrate_popup);
        heartratenormal.setContentView(R.layout.heartrate_popup_normal);
        heartratenm.setContentView(R.layout.heartrate_popup_notmounted);
        Heartstatus=(TextView) heartrate.findViewById(R.id.popupheart_Title);
        Heartstatusnormal=(TextView)heartratenormal.findViewById(R.id.popupheart_normal_Title);
        heartrateRef=FirebaseDatabase.getInstance().getReference().child("HeartRate: ");
        heartrateRef.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                heartval=dataSnapshot.getValue(String.class).toLowerCase();
                if(dataSnapshot.getValue(String.class).toLowerCase().equals("normal")){
                    Heartstatusnormal.setText("Normal");
                    //Toast.makeText(getApplicationContext(),"Normal value read",Toast.LENGTH_SHORT).show();
                }
                else{
                    Heartstatus.setText("Abnormal");
                    //Toast.makeText(getApplicationContext(),"Abnormal value read",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /*heartrateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    heartcondition = d.getValue(String.class);
                    Heartstatus.setText(d.getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/




        dialogheartclose=(ImageView)heartrate.findViewById(R.id.closeheartPopUp);
        dialogheartnormalclose=(ImageView)heartratenormal.findViewById((R.id.closeheartnormalPopUp));
        dialogheartnmclose=(ImageView)heartratenm.findViewById(R.id.closeheartnmPopUp);
        dialogHeartOK=(Button)heartrate.findViewById(R.id.btnheartAccept);
        dialogHeartnormalOK=(Button)heartratenormal.findViewById(R.id.btnheartnormalAccept);
        dialogHeartnmOK=(Button)heartratenm.findViewById(R.id.btnheartnmAccept);

        dialogheartclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heartrate.dismiss();

            }
        });
        dialogHeartOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heartrate.dismiss();

            }
        });
        dialogheartnormalclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heartratenormal.dismiss();

            }
        });
        dialogHeartnormalOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heartratenormal.dismiss();

            }
        });
        dialogheartnmclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heartratenm.dismiss();

            }
        });
        dialogHeartnmOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                heartratenm.dismiss();

            }
        });
        ///////////////////////////


        ////////////////////////////////////
        patientstatus=new Dialog(ScannerNetwork.this);
        patientstatuswr=new Dialog(ScannerNetwork.this);
        patientstatus.setContentView(R.layout.status_popup);
        patientstatuswr.setContentView(R.layout.status_popupwr);
        PatientStatusTxt=(TextView) patientstatus.findViewById(R.id.popupStatus_Title);
        PatientStatuswrTxt=(TextView) patientstatuswr.findViewById(R.id.popupStatuswr_Title);
        statusRef=FirebaseDatabase.getInstance().getReference().child("Status: ");
        statusRef.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                PatientStatusTxt.setText(dataSnapshot.getValue(String.class));
                PatientStatuswrTxt.setText(dataSnapshot.getValue(String.class));
                statusval=dataSnapshot.getValue(String.class).toLowerCase();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       /* statusRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    PatientStatusTxt.setText(d.getValue(String.class));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        dialogstatusclose=(ImageView) patientstatus.findViewById(R.id.closestatusPopUp);
        dialogstatuswrclose=(ImageView) patientstatuswr.findViewById(R.id.closestatuswrPopUp);

        dialogstatusok=(Button) patientstatus.findViewById(R.id.btnstatusAccept);
        dialogstatuswrok=(Button) patientstatuswr.findViewById(R.id.btnstatuswrAccept);

        dialogstatusclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientstatus.dismiss();

            }
        });
        dialogstatusok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientstatus.dismiss();

            }
        });
        dialogstatuswrclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientstatuswr.dismiss();

            }
        });
        dialogstatuswrok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                patientstatuswr.dismiss();

            }
        });

        ///////////////////////////////////
        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(ScannerNetwork.this);
                    alert=MediaPlayer.create(ScannerNetwork.this,R.raw.swiftly);
                    custom.setContentView(R.layout.popup);
                    dialogClose=(ImageView)custom.findViewById(R.id.closePopUp);
                    dialogOK=(Button)custom.findViewById(R.id.btnAccept);
                    //add for set Text

                    dialogRoomName=(TextView)custom.findViewById(R.id.roomName);
                    dialogOutlierTime=(TextView)custom.findViewById(R.id.outlierTime);
                    /////////////////////////////////////////
                    //reading the last query
                    databaseReference = FirebaseDatabase.getInstance().getReference("Outliers/Temporal Outlier Time");
                    databaseReference.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            OutlierDataModel message=dataSnapshot.getValue(OutlierDataModel.class);
                            dialogRoomName.setText(message.getRoom());
                            dialogOutlierTime.setText(message.getTime());
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    /////////////////////////////////////////
                    dialogClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();

                        }
                    });
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();

                        }
                    });
                    custom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    custom.show();
                    alert.start();
                    //Assigns the function to check in every minute
                    handler.postDelayed(new Runnable(){
                        public void run(){
                            checkStatus();
                            handler.postDelayed(this, delay);
                        }
                    }, delay);


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        goProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler = new Handler();
                Intent intent= getIntent();
                final String user_email = intent.getStringExtra("email");
                final String user_name = intent.getStringExtra("name");
                final String user_phone = intent.getStringExtra("phoneNo");
                final String user_username = intent.getStringExtra("username");

               // Toast.makeText(getApplicationContext(),user_name, Toast.LENGTH_SHORT).show();
                Intent intentGoProfile = new Intent (ScannerNetwork.this,UserProfile.class);
                intentGoProfile.putExtra("email",user_email);
                intentGoProfile.putExtra("name",user_name);
                intentGoProfile.putExtra("phoneNo",user_phone);
                intentGoProfile.putExtra("username",user_username);
                ScannerNetwork.this.startActivity(intentGoProfile);

            }
        });
           goHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler = new Handler();
                Intent intentGoHome = new Intent (ScannerNetwork.this,Login.class);
                ScannerNetwork.this.startActivity(intentGoHome);
            }
        });

        goPattern.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler = new Handler();
                Intent intent = new Intent (ScannerNetwork.this,HouseMap.class);
                ScannerNetwork.this.startActivity(intent);
            }
        });
        goToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler = new Handler();
                Intent intent = new Intent (ScannerNetwork.this,Today.class);
                ScannerNetwork.this.startActivity(intent);
            }
        });
        
        goMean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler = new Handler();
                Intent intent = new Intent (ScannerNetwork.this,Mean.class);
                ScannerNetwork.this.startActivity(intent);
            }
        });
        goHeartrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(heartval.equals("abnormal")){
                    //Toast.makeText(getApplicationContext(),"Hello heart rate",Toast.LENGTH_SHORT).show();
                    heartrate.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    heartrate.show();
                }
                else if(heartval.equals("normal")){
                    //Toast.makeText(getApplicationContext(),heartval,Toast.LENGTH_SHORT).show();
                    heartratenormal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    heartratenormal.show();
                }
                else{
                    //Toast.makeText(getApplicationContext(),heartval,Toast.LENGTH_SHORT).show();
                    heartratenm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    heartratenm.show();
                }


            }
        });
        goPatientstatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(),statusval,Toast.LENGTH_SHORT).show();
                if(statusval.equals("falling")){
                    patientstatus.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    patientstatus.show();
                }
                else{
                    patientstatuswr.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    patientstatuswr.show();
                }



            }
        });

        CheckOutOfTheHouse();

    }
    private void CheckOutOfTheHouse() {
        mRef= FirebaseDatabase.getInstance().getReference("Location/DateFormat");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationModel loc=dataSnapshot.getValue(LocationModel.class);
                if(loc.getLocation().equals("Out")){
                    outofroom=new Dialog(ScannerNetwork.this);
                    outofroom.setContentView(R.layout.popup);
                    dialogClose=(ImageView)outofroom.findViewById(R.id.closePopUp);
                    dialogOK=(Button)outofroom.findViewById(R.id.btnAccept);
                    dialogRoomName=(TextView)outofroom.findViewById(R.id.roomName);
                    dialogOutlierTime=(TextView)outofroom.findViewById(R.id.outlierTime);
                    dialogTitle=(TextView)outofroom.findViewById(R.id.popup_Title);
                    dialogOutlierTime.setVisibility(View.INVISIBLE);
                    dialogTitle.setVisibility(View.INVISIBLE);
                    dialogRoomName.setText("Patient is Out of the House");
                    dialogClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            outofroom.dismiss();

                        }
                    });
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            outofroom.dismiss();

                        }
                    });
                    outofroom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    outofroom.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void checkStatus() {
        custom.dismiss();
        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(ScannerNetwork.this);
                    alert=MediaPlayer.create(ScannerNetwork.this,R.raw.swiftly);
                    custom.setContentView(R.layout.popup);
                    dialogClose=(ImageView)custom.findViewById(R.id.closePopUp);
                    dialogOK=(Button)custom.findViewById(R.id.btnAccept);
                    //add for set Text

                    dialogRoomName=(TextView)custom.findViewById(R.id.roomName);
                    dialogOutlierTime=(TextView)custom.findViewById(R.id.outlierTime);
                    /////////////////////////////////////////
                    //reading the last query
                    databaseReference = FirebaseDatabase.getInstance().getReference("Outliers/Temporal Outlier Time");
                    databaseReference.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                            OutlierDataModel message=dataSnapshot.getValue(OutlierDataModel.class);
                            dialogRoomName.setText(message.getRoom());
                            dialogOutlierTime.setText(message.getTime());
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    /////////////////////////////////////////
                    dialogClose.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();
                        }
                    });
                    dialogOK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            custom.dismiss();
                        }
                    });
                    custom.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    custom.show();
                    alert.start();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}