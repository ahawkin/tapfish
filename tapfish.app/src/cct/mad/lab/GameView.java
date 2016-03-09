package cct.mad.lab;

import java.util.ArrayList;
import java.util.List;

import cct.mad.lab.R.raw;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * This class takes care of surface for drawing and touches
 * 
 */

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	/* Member (state) fields   */
	private GameLoopThread gameLoopThread;
	private Paint paint; //Reference a paint object 
	private Paint paintLarge;
    /** The drawable to use as the background of the animation canvas */
    private Bitmap mBackgroundImage;
    private int hitCount;
    public String highScore;
    public String diff;
    
    //Fish Sprite
    private Sprite sprite;
    private List<Sprite> sprites = new ArrayList<Sprite>();
	private int spriteNum = 10;
	private int spriteCounter;
	
	//Bomb/Bad Sprite 
    private BadSprite badsprite;
    private List<BadSprite> badsprites = new ArrayList<BadSprite>();
    private int badSpriteNum;
	private int badSpriteCounter;
	
    /* For the countdown timer */
    private long  startTime ;			//Timer to count down from
    private final long interval = 1 * 1000; 	//1 sec interval
    private CountDownTimer countDownTimer; 	//Reference to class
    private boolean timerRunning = false;
    private String displayTime; 		//To display time on the screen
    
    /* For Gameover */
    public boolean gameOver = false;
    
    // Game Sounds
	public MediaPlayer mp;
	public MediaPlayer hit;
	public MediaPlayer bomb;
  
	public GameView(Context context) {
		super(context);
		// Focus must be on GameView so that events can be handled.
		this.setFocusable(true);
		// For intercepting events on the surface.
		this.getHolder().addCallback(this);
		
  	    mp = MediaPlayer.create(context, raw.gameloop);
  	    hit = MediaPlayer.create(context, raw.hit);
  	    bomb = MediaPlayer.create(context, raw.bomb);
  	    mp.setLooping(true);
  	    mp.start();
		
		mBackgroundImage = BitmapFactory.decodeResource(this.getResources(),
		R.drawable.gamebg);
	}
	
    private void createSprites() {
    	if (diff.equals("Easy")) {
    		badSpriteNum = 10;
    		spriteNum = 15;
    	} else if (diff.equals("Medium")) {
    		badSpriteNum = 20;
    		spriteNum = 12;
    	} else if (diff.equals("Hard")) {
    		badSpriteNum = 35;
    		spriteNum = 7;
    	}
		for(int i=0; i < spriteNum; i++){
			sprites.add(createSprite(R.drawable.spriteboard));
		}
		for(int i=0; i < badSpriteNum; i++){
			badsprites.add(createBadSprite(R.drawable.bombs));
		}
    }	
    
	private Sprite createSprite(int resouce) {
           Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
           return new Sprite(this);
	}
	
	private BadSprite createBadSprite(int resouce) {
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), resouce);
        return new BadSprite(this);
	}
	
	
	/* Called immediately after the surface created */
	public void surfaceCreated(SurfaceHolder holder) {
		
		mBackgroundImage = Bitmap.createScaledBitmap(mBackgroundImage, getWidth(), getHeight(), true);
		
		// We can now safely setup the game start the game loop.
		ResetGame();//Set up a new game up - could be called by a 'play again option'
		gameLoopThread = new GameLoopThread(this.getHolder(), this);
		gameLoopThread.running = true;
		gameLoopThread.start();
		createSprites();
	}
		
	//To initialise/reset game 
	private void ResetGame(){
		//Game Over
		gameOver = false;
		sprite = new Sprite(this);
		badsprite = new BadSprite(this);
		/* Set paint details */
	    paint = new Paint();
		paint.setColor(Color.WHITE); 
		paint.setTextSize(20); 
		hitCount = 0;		
 		spriteCounter = 0;
 		badSpriteCounter = 0;
		//Set timer
		startTime = 20;//Start at 10s to count down
		//Create new object - convert startTime to milliseconds
		countDownTimer=new MyCountDownTimer(startTime*1000,interval);
		countDownTimer.start();//Start it running
		timerRunning = true;

	}
	
	//This class updates and manages the assets prior to drawing - called from the Thread
	public void update(){
		if (gameOver == false) {
           for (Sprite sprite : sprites) {
        	   sprite.update();
            }
           for (BadSprite badsprite : badsprites) {
        	   badsprite.update();
            }
		}
	}
	
	/* Countdown Timer - private class */
	private class MyCountDownTimer extends CountDownTimer {

	  public MyCountDownTimer(long startTime, long interval) {
			super(startTime, interval);
	  }
	  public void onFinish() {
			displayTime = "Times Up!";
			timerRunning = false;
			countDownTimer.cancel();
			gameOver = true;
			mp.stop();	
	  }
	  public void onTick(long millisUntilFinished) {
			displayTime = " " + millisUntilFinished / 1000;
	  }
	}//End of MyCountDownTimer

	/**
	 * To draw the game to the screen
	 * This is called from Thread, so synchronisation can be done
	 */
	public void doDraw(Canvas canvas) {
		canvas.drawBitmap(mBackgroundImage, 0, 0, null);
		//Draw all the objects on the canvas
		if (gameOver == false) {
           for (Sprite sprite : sprites) {
        	   sprite.draw(canvas);
            }
           for (BadSprite badsprite : badsprites) {
        	   badsprite.draw(canvas);
            }
			canvas.drawText("Mode: " + diff + " | Score To Beat: " + highScore,5,25, paint);
			canvas.drawText("Score: " + hitCount + " | Time:" + displayTime,5,50, paint);
		} else if (gameOver == true) {
			paintLarge = new Paint();
			paintLarge.setTextSize(40);
			paintLarge.setColor(Color.WHITE); 
			canvas.drawText("Game Over!", 135, 320, paintLarge);
			canvas.drawText("Final Score: " + hitCount, 160, 350, paint);
			canvas.drawText("Press back to return to main menu.", 90, 390, paint);
		}
	}
	
	//To be used if we need to find where screen was touched
	public boolean onTouchEvent(MotionEvent event) {		
		if (gameOver == false) {
			for (Sprite sprite : sprites) {
				if (sprite.wasItTouched(event.getX(), event.getY())){
					sprites.remove(sprite);
					hit.start();
		    	   	hitCount++; 
		    	   	spriteCounter++;
		    	   	break;
		    	}
			}
			for (BadSprite badsprite : badsprites) {
				if (badsprite.wasItTouched(event.getX(), event.getY())){
					badsprites.remove(badsprite);
					bomb.start();
		    	   	hitCount--; 
		    	   	badSpriteCounter++;
		    	   	break;
		    	}
			}
		   	if (spriteCounter == 1) {
		   		spriteCounter = 0;
		   		sprites.add(createSprite(R.drawable.spriteboard));
		   	}		
		   	if (badSpriteCounter == 1) {
		   		badSpriteCounter = 0;
		   		badsprites.add(createBadSprite(R.drawable.bombs));
		   	}	
		}
		return true;
	}
	
	public Integer getHitCount() {
		return hitCount;
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		gameLoopThread.running = false;
		
		// Shut down the game loop thread cleanly.
		boolean retry = true;
		while(retry) {
			try {
				gameLoopThread.join();
				retry = false;
			} catch (InterruptedException e) {}
		}
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		
	}

}
