package com.gpeters.geoffagecalculator;

import java.util.Date;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Dialog pickerDialog;
	Button select;
	TextView ageResults;
	
	DatePicker datePicker;
	Button set;
	Integer month,day,year;
	
	Double ageDays, ageWeeks, ageMonth, ageYears, ageRemainder;
	
	Double roundDays, roundWeeks, roundMonth, roundYears, roundRemainder;
	
	private void showAboutDialog() {
		final Dialog aboutDialog = new Dialog(MainActivity.this);
		aboutDialog.setContentView(R.layout.about_frag);
		aboutDialog.setTitle("About this App");

		TextView textAbout = (TextView)aboutDialog.findViewById(R.id.textAbout);
        Button closeAbout = (Button)aboutDialog.findViewById(R.id.btnCloseAbout);
        
        textAbout.setText(Html.fromHtml("<p>Created by <b>Geoff Peters</b>, a software developer and videographer in Vancouver BC Canada."
        		+ " To watch some of his videos, please visit: <b><a href='http://geoffmobile.com'>www.geoffmobile.com</a></b></p>"
        		+ "<p>Birthday Cake image by Will Clayton <a href='http://www.flickr.com/photos/spool32/'>www.flickr.com/photos/spool32/</a></p>"));

        // make links clickable
        textAbout.setMovementMethod(LinkMovementMethod.getInstance());
        
        closeAbout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
            	aboutDialog.dismiss();
            }
        });
        aboutDialog.show();
	}
	
	private boolean calculateAge(Date birthDate, Date currentDate) {
	    ageDays  = 0.0;
	    ageWeeks = 0.0;
	    ageMonth = 0.0;
	    ageYears = 0.0;
	    ageRemainder = 0.0;

	    Long milliSecondDiff   = currentDate.getTime() - birthDate.getTime();
	    if (milliSecondDiff < 0) {
	    	return false;
	    }
	    ageDays  = milliSecondDiff / 86400000.0;
	    ageWeeks = ageDays / 7.0;
	    ageMonth = ageDays / 30.4375;
	    ageYears = ageDays / 365.24;    
	    roundYears = Math.floor(ageYears);
	    ageRemainder  = (ageDays - roundYears * 365.24) / 30.4375;

	    roundDays  = Math.round(ageDays * 10) / 10.0;
	    roundWeeks = Math.round(ageWeeks * 10) / 10.0;
	    roundMonth = Math.round(ageMonth * 10) / 10.0;
	    roundRemainder  = Math.round(ageRemainder * 10) / 10.0;
	    
	    return true;
	}
	
	private String formatDouble(Double value, int decimalPlaces) {
		return String.format( "%." + decimalPlaces + "f", value );
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        select = (Button)findViewById(R.id.button_select_birthdate);
        ageResults = (TextView) findViewById(R.id.textview_age_results);
        
        select.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				pickerDialog = new Dialog(MainActivity.this);
				pickerDialog.setContentView(R.layout.picker_frag);
				pickerDialog.setTitle("Select Date of Birth");
 
                datePicker = (DatePicker)pickerDialog.findViewById(R.id.datePicker);
                set = (Button)pickerDialog.findViewById(R.id.btnSet);
                
                // Requires API Level 11, so don't do it.
                //Date now = new Date();                
                //datePicker.setMaxDate(now.getTime());
                
                datePicker.updateDate(1986, 02, 10);
 
                set.setOnClickListener(new View.OnClickListener() {
 
                    @Override
                    public void onClick(View view) {
                        month = datePicker.getMonth();
                        day = datePicker.getDayOfMonth();
                        year = datePicker.getYear();

                        GregorianCalendar cal = new GregorianCalendar(year, month, day);
                        Date userDate = cal.getTime();
                        
                        Date now = new Date();
                        boolean ageOk = calculateAge(userDate, now);
                        
                        if (!ageOk) {
                        	Context context = getApplicationContext();
                        	CharSequence text = "Please choose a date in the past.";
                        	int duration = Toast.LENGTH_LONG;

                        	Toast toast = Toast.makeText(context, text, duration);
                        	toast.show();
                        	return;
                        }
                        
                        String dateString = DateFormat.format("MMMM d, yyyy", cal).toString();
                        
                        Double monthsToNextBirthday = 12 - roundRemainder;
                        
                        String presentMessage;
                        if (monthsToNextBirthday < 6) {
                        	presentMessage = "Better start thinking of a present to give them.";
                        } else {
                        	presentMessage = "You have lots of time to think of a suitable gift.";
                        }
                        
                        ageResults.setText(Html.fromHtml("<p>Date of Birth: " + dateString + "</p>"
                        		+ "<p>Age = <b>" + formatDouble(roundYears, 0) + "</b> years " + formatDouble(roundRemainder,1) + " months<br>" 
                        		+ "&nbsp;&nbsp;&nbsp; = " + formatDouble(roundMonth, 1) + " months<br>"
                        		+ "&nbsp;&nbsp;&nbsp; = " + formatDouble(roundWeeks, 1) + " weeks<br>" 
                        		+ "&nbsp;&nbsp;&nbsp; = " + formatDouble(roundDays, 0) + " days</p>"
                        		+ "<p>It's " + formatDouble(monthsToNextBirthday, 1) + " months to their next birthday."
                        		+ " " + presentMessage + "</p>"));
                        
                        pickerDialog.dismiss();
                    }
                });
                pickerDialog.show();
			}        	
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.action_about:
            showAboutDialog();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
