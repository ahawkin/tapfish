package cct.mad.lab;

import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;

public class BadSprite {

    private GameView gameView;
    private Bitmap spritebmp;
    //Width and Height of the Sprite image
    private int bmp_width;
	private int bmp_height;
    // Needed for new random coordinates.
  	private Random random = new Random();
	//x,y position of sprite - initial position (0,50 + RANDOM)
	private int x = 0 + (random.nextInt(360)+1);; 
	private int y = 50 + (random.nextInt(900)+1);
	//x and y speeds with randomisation
    private int xSpeed = -7 + (random.nextInt(14)+1);
    private int ySpeed = -5 + (random.nextInt(10)+1);
    
    private static final int ANIMATION_ROWS = 2;
    private static final int ANIMATION_COLUMNS = 1;
    private int currentFrame = 0;
    
    public BadSprite(GameView gameView) {
          this.gameView=gameView;
          spritebmp = BitmapFactory.decodeResource(gameView.getResources(),
  				R.drawable.bombs);
		  this.bmp_width = spritebmp.getWidth()/ANIMATION_COLUMNS;
		  this.bmp_height= spritebmp.getHeight()/ANIMATION_ROWS;
     }
    
    //update the position of the sprite
    public void update() {
    	currentFrame = ++currentFrame % ANIMATION_COLUMNS;
    	x = x + xSpeed;
    	y = y + ySpeed;
        wrapAround(); //Adjust motion of sprite.
    }
    
    public void draw(Canvas canvas) {
    	   int srcX = currentFrame * bmp_width;//frame - x direction
    	   int srcY; 					//row
    	   if (xSpeed > 0){//Sprite going right; row = 0
    	     		srcY = 0 * bmp_height;
    	   }
    	   else { //Going left; row = 1
    	        	srcY = 1 * bmp_height;
    	   }
    	   //Create Rect around the source image to be drawn
    	   Rect src = new Rect(srcX, srcY, srcX+bmp_width, srcY + bmp_height);
    	   //Rect for destination image
    	   Rect dst = new Rect(x, y, x + bmp_width, y + bmp_height);
    	   //draw the image frame
    	   canvas.drawBitmap(spritebmp, src, dst, null);
    }
    
    public void wrapAround(){
    	//Code to wrap around	
      	if (x < 0) x = x + gameView.getWidth(); //increment x whilst not off screen
    	if (x >= gameView.getWidth()){ //if gone of the right sides of screen
    			x = x - gameView.getWidth(); //Reset x
    	}
    	if (y < 0) y = y + gameView.getHeight();//increment y whilst not off screen
    	if (y >= gameView.getHeight()){//if gone of the bottom of screen
    		y -= gameView.getHeight();//Reset y
    	}
    }
    
    /* Checks if the Sprite was touched. */
    public boolean wasItTouched(float ex, float ey){
    	boolean touched = false; 
    	if ((x <= ex) && (ex < x + bmp_width) &&
    		(y <= ey) && (ey < y + bmp_height)) {
          		touched = true;
    	}
    	return touched;
    }//End of wasItTouched

}  
