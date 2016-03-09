package cct.mad.lab;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenu extends Activity {

	private static final int SCORE_REQUEST_CODE = 1;// The request code for the intent

	TextView tvScore;
	TextView tvHighestScore;
	String score;
	Intent gameIntent;
	int lastScore, highScore, oldScore;
	String scoreToBeat = "0";
	String diffSetting = "Easy";
	Button clearHighScore;
	public boolean easy = true;
	public boolean medium, hard = false;
		
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game_start);
		tvScore = (TextView) findViewById(R.id.tvGuessGame);
		tvHighestScore = (TextView) findViewById(R.id.tvHighestScore);
		SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
		oldScore = prefs.getInt("key", 0);  
		tvHighestScore.setText(Integer.toString(oldScore));
		clearHighScore = (Button) findViewById(R.id.clearScore);
	}
	
    /* Create Options Menu */
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}
	
	// Respond to item selected on OPTIONS MENU
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		//put data in Intent
		case R.id.easy:
			easy = true;
			medium = false;
			hard = false;
			Toast.makeText(this, "Easy chosen", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.medium:
			medium = true;
			easy = false;
			hard = false;
			Toast.makeText(this, "Medium chosen", Toast.LENGTH_SHORT).show();
			return true;
		case R.id.hard:
			hard = true;
			medium = false;
			easy = false;
			Toast.makeText(this, "Hard chosen", Toast.LENGTH_SHORT).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void startGame(View v){
		gameIntent = new Intent(this,GameActivity.class);
	    scoreToBeat = tvHighestScore.getText().toString();
		gameIntent.putExtra("highScore", scoreToBeat); 
		
		if (easy == true) {
			diffSetting = "Easy";
			gameIntent.putExtra("diffsetting", diffSetting); 
		} else if (medium == true) {
			diffSetting = "Medium";
			gameIntent.putExtra("diffsetting", diffSetting);
		} else if (hard == true) {
			diffSetting = "Hard";
			gameIntent.putExtra("diffsetting", diffSetting);
		}
		
	    startActivityForResult(gameIntent, SCORE_REQUEST_CODE ); 
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent retIntent) {
	    // Check which request we're responding to
	    if (requestCode == SCORE_REQUEST_CODE) {
	        // Make sure the request was successful   	
	        if (resultCode == RESULT_OK) {
	        	if (retIntent.hasExtra("GAME_SCORE")) {
					int scoreFromGame = retIntent.getExtras().getInt("GAME_SCORE");
					tvScore.setText(Integer.toString(scoreFromGame));
					lastScore = Integer.parseInt(tvScore.getText().toString());
					highScore = Integer.parseInt(tvHighestScore.getText().toString());
					SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
					if(lastScore > highScore ){
					   Editor edit = prefs.edit();
					   edit.putInt("key", lastScore);
					   edit.commit();
					   tvHighestScore.setText(Integer.toString(lastScore));
					}

	        	}
	        }	
	    }
	}
	
	//Clear high score listener
    public void clearClick(View v) {	      
    	SharedPreferences prefs = this.getSharedPreferences("myPrefsKey", Context.MODE_PRIVATE);
		Editor edit = prefs.edit();
		edit.putInt("key", 0);
		edit.commit();
		oldScore = prefs.getInt("key", 0);
		tvHighestScore.setText(Integer.toString(oldScore));
    }
	
	
}
