package com.example.alphaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity  {

    // The activity for the event to calender
    TextView userState;
    EditText eventTitleET, inventorET;
    myDate selectedDate;

    String eventTitle, inventorName;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userState = findViewById(R.id.userState);
        eventTitleET = findViewById(R.id.eventTitleET);
        inventorET = findViewById(R.id.inventorET);

        SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
        Variable.setEmailVer(settings.getString("email",""));
        userState.setText("You signed-in as: "+  Variable.getEmailVer());

        selectedDate = new myDate();
    }

    public void createEvent(View view) {
        eventTitle = eventTitleET.getText().toString();
        inventorName = inventorET.getText().toString();

        if (eventTitle.isEmpty()){
            eventTitleET.requestFocus();
        }
        else if (inventorName.isEmpty()){
            inventorET.requestFocus();
        }
        else{
            Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(Calendar.YEAR);
            final int month = calendar.get(Calendar.MONTH);
            final int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    month ++;
                    selectedDate = new myDate(year, month, dayOfMonth, 0, 0);
                    openTimePicker();
                }
            }, year, month, day);

            dpd.show();
        }
    }

    private void openTimePicker() {
        final Calendar cldr = Calendar.getInstance();
        int hour = cldr.get(Calendar.HOUR_OF_DAY);
        int minutes = cldr.get(Calendar.MINUTE);
        // time picker dialog
        TimePickerDialog picker = new TimePickerDialog(MainActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int hour, int minute) {
                        selectedDate.setHour(hour);
                        selectedDate.setMinute(minute);

                        Event tempEvent = new Event(eventTitle, inventorName, selectedDate);
                        sendToGoogleCalender(tempEvent);
                        eventTitleET.setText("");
                        inventorET.setText("");
                    }
                }, hour, minutes, true);
        picker.show();
    }

    private void sendToGoogleCalender(Event tempEvent) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, eventTitle);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, inventorName);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Local");
        intent.putExtra(CalendarContract.Events.DTSTART, selectedDate.getHour());
        intent.putExtra(Intent.EXTRA_EMAIL, Variable.emailVer);

        if (intent.resolveActivity(getPackageManager()) != null){
            Intent intent1 = Intent.createChooser(intent, "Open using");
            startActivity(intent1);
        } else {
            Toast.makeText(this, "No supported app", Toast.LENGTH_SHORT).show();
        }
    }



    private void Logout() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure?");
        SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
        Variable.setEmailVer(settings.getString("email",""));
        adb.setMessage(Variable.getEmailVer().substring(0,Variable.emailVer.indexOf("@"))+" will logged out");

        adb.setNegativeButton("DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FirebaseAuth.getInstance().signOut();
                Intent si = new Intent(MainActivity.this, LoginActivity.class);

                SharedPreferences settings = getSharedPreferences("Status",MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("email", "");
                editor.apply();

                startActivity(si);
                finish();
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       int id = item.getItemId();

       Intent si;
       if (id == R.id.logOut){
           Logout();
       }
       else if (id == R.id.pdf){
           si = new Intent(this, pdfCreatorActivity.class);
           startActivity(si);
       }
       else if (id == R.id.record){
           si = new Intent(this, RecordActivity.class);
           startActivity(si);
       }
       else if (id == R.id.map){
           si = new Intent(this, LocationActivity.class);
           startActivity(si);
       }


        return super.onOptionsItemSelected(item);
    }

}