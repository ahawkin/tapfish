package cct.mad.lab;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Game loop thread. This loop is the main driver for the 
 * Game and calls methods of Game object such as updata and draw.
 * 
 */

public class GameLoopThread extends Thread {
	
	private final static int FPS = 30;          		// How many times per second the game should be updated, drawn?
	private final static int MAX_FRAME_SKIPS = 5; 			// Maximum number of frames to be skipped
	private final static int FRAME_PERIOD = 1000 / FPS;    // The time for one frame 
	
	// Surface holder that can access the physical surface.
	private SurfaceHolder surfaceHolder;
	
	private GameView gameView;//To link to GameView 

	// Holds the state of the game loop.
	public boolean running;

	public GameLoopThread(SurfaceHolder surfaceHolder, GameView gameView) {
		super();
		this.surfaceHolder = surfaceHolder;
		this.gameView = gameView; // To link to GameView
	}

	@Override
	public void run() {
		Canvas canvas;
		long startTime;		// the time when the cycle begun
		long timeDiff;		// the time it took for the cycle to execute
		int sleepTime;		// ms to sleep (<0 if we're behind)
		int framesSkipped;	// number of frames being skipped 

		sleepTime = 0;
		
		while(running){
			canvas = null;
			
			try {
				canvas = this.surfaceHolder.lockCanvas(); // Try locking the canvas for exclusive pixel editing in the surface.
				synchronized(surfaceHolder) {
					startTime = System.currentTimeMillis();//of canvas locked
					framesSkipped = 0;	// Resetting the frames skipped.	
					gameView.update();//Update the assets
					gameView.doDraw(canvas);//Draw on the surface
                    /* SYNCHRONISE THE LOOP CYCLE WITH THE FRAME PERIOD */
					// First calculate how long the drawing/frame took.
					timeDiff = System.currentTimeMillis() - startTime;
					//Calculate the time to sleep to synch with frame time(period)
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					if (sleepTime > 0) {//Loop going faster than Frame, so sleep
						try {
							// Send the thread to sleep for a short period.
							Thread.sleep(sleepTime);	
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					
					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
						// We need to catch up, so we update without drawing the game to the screen.
						sleepTime += FRAME_PERIOD; // Add FRAME_PERIOD to check while condition again.
						framesSkipped++;
					}

				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				// In case of an exception the surface is not left in an inconsistent state.
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);//All in synch - unlock canvas
				}
			}
		}
	}

}
