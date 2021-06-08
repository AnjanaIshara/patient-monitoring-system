package com.example.espapp10;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyachi.stepview.HorizontalStepView;
import com.baoyachi.stepview.VerticalStepView;
import com.baoyachi.stepview.bean.StepBean;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Today extends AppCompatActivity {
    DatabaseReference mRef;
    //dialog box variable initialization
    Dialog custom,outofroom;
    MediaPlayer alert;
    ImageView dialogClose;
    Button dialogOK;
    Handler handler = new Handler();
    int delay = 60000; //milliseconds
    TextView dialogRoomName,dialogOutlierTime,dialogTitle;
    DatabaseReference databaseReference,mRef3;
PieChart piechart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_today);
        Calendar cal = Calendar.getInstance();
        int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH)+1;
        String strmonth="";
        String strDay="";

        mRef3=FirebaseDatabase.getInstance().getReference("Status: ");
        mRef3.orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getValue(String.class).toLowerCase().equals("falling")){
                    //Toast.makeText(getApplicationContext(),"falling",Toast.LENGTH_SHORT).show();
                    String message="The patient has fallen down.";
                    NotificationCompat.Builder builder=new NotificationCompat.Builder(
                            Today.this
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


        if(dayOfMonth<10){
            strDay+="0";
        }
        if(month<10){
            strmonth+="0";
        }
        strDay+=String.valueOf(dayOfMonth);
        strmonth+=String.valueOf(month);
        String DateFormat=strmonth+"_"+strDay;
        LoadDataFromDataBase(DateFormat);//before the presentation please add DateFormat withn the paranthesis
        mRef= FirebaseDatabase.getInstance().getReference("Outliers");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true") || dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(Today.this);
                    alert=MediaPlayer.create(Today.this,R.raw.swiftly);
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
        CheckOutOfTheHouse();
    }

    private void CheckOutOfTheHouse() {
        mRef= FirebaseDatabase.getInstance().getReference("Location/DateFormat");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                LocationModel loc=dataSnapshot.getValue(LocationModel.class);
                if(loc.getLocation().equals("Out")){
                    outofroom=new Dialog(Today.this);
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
                if(dataSnapshot.child("Temporal Outlier").getValue().toString().toLowerCase().equals("true")|| dataSnapshot.child("Transition Outlier").getValue().toString().toLowerCase().equals("true")){
                    custom=new Dialog(Today.this);
                    alert=MediaPlayer.create(Today.this,R.raw.swiftly);
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

    private void LoadDataFromDataBase(final String dateFormat) {

        final ArrayList<TodayDataModel> listoftoday=new ArrayList<>();

        mRef= FirebaseDatabase.getInstance().getReference("SensorData/"+dateFormat);/*dateFormat*///IF YOU WANT TO SET A FIXED DATE DELETE dateFormat variable and add the preferred date
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    TodayDataModel finaldata=null;
                    HashMap<String,Integer> map=new HashMap<String,Integer>();
                    for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                        TodayDataModel todayData=postSnapshot.getValue(TodayDataModel.class);


                        if(map.containsKey(todayData.getR())){
                            map.put(todayData.getR(),map.get(todayData.getR())+1);
                        }
                        else{
                            map.put(todayData.getR(),0);
                        }




                    }



                piechart=(PieChart) findViewById(R.id.piechart);
                piechart.getDescription().setEnabled(false);
                piechart.setExtraOffsets(5,10,5,5);
                piechart.setDragDecelerationFrictionCoef(0.95f);
                piechart.setDrawHoleEnabled(false);
                piechart.setTransparentCircleRadius(61f);
                Legend l = piechart.getLegend();
                l.setTextSize(15f);
                l.setTextColor(Color.BLACK);
                l.setForm(Legend.LegendForm.CIRCLE);

                ArrayList<PieEntry> yValues=new ArrayList<>();
                int TotalCount=0;
                for(Map.Entry<String,Integer> entry:map.entrySet()){
                    TotalCount+=entry.getValue();
                }
                for(Map.Entry<String,Integer> entry:map.entrySet()){
                    yValues.add(new PieEntry((float) (entry.getValue()*100.0/TotalCount),entry.getKey()));
                    //Toast.makeText(getApplicationContext(),"Key "+entry.getKey()+"Value : "+entry.getValue().toString(),Toast.LENGTH_SHORT).show();
                }
                PieDataSet dataSet=new PieDataSet(yValues,"");
                dataSet.setValueFormatter(new PercentFormatter());
                dataSet.setSliceSpace(3f);
                dataSet.setSelectionShift(5f);
                dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                PieData data=new PieData((dataSet));
                data.setValueTextSize(20f);
                data.setValueTextColor(Color.BLACK);
                piechart.setData(data);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public  void ViewRoutine(View v){
        Intent intentGoMap = new Intent (Today.this,HouseMap.class);
        Today.this.startActivity(intentGoMap);
    }
}