package cct.mad.lab;

import cct.mad.lab.R.raw;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/* Activity class for the game.
 * Creates the GameView and can pass parameters to the gameView.
 * Can also receive/return Intent data to configure game and report game status(eg score)
 */
public class GameActivity extends Activity {

	GameView gameView;// Reference the gameView

	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the layout
		setContentView(R.layout.game_view_container);
		// Get the id of the layout
		RelativeLayout mainscreen = (RelativeLayout) findViewById(R.id.mainscreen);
		// Make the GameView
		gameView = new GameView(this);
		// Get  data from intent and config gameView here
		
  	    Bundle extras = getIntent().getExtras();
  	    if (extras != null) {
  	    	gameView.highScore = extras.getString("highScore"); 
  	    	gameView.diff = extras.getString("diffsetting");
  		 }
  	    
		gameView.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT));
		// Add GameView
		mainscreen.addView(gameView);
	}
	
	/* Called when activity is done and should be closed. 
	 * The ActivityResult is propagated back to whoever launched via onActivityResult()
	 */
	public void finish(){
		  gameView.mp.stop();
		  Intent returnIntent = new Intent();
		  returnIntent.putExtra("GAME_SCORE",gameView.getHitCount());
		  setResult(RESULT_OK, returnIntent);
		  super.finish();
	}


}
