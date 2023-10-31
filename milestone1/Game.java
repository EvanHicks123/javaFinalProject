/* Game.java
 * Space Invaders Main Program
 *
 */

import javax.swing.*;
import java.awt.*; 
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;

public class Game extends Canvas {

		
		
		final int originalTileSize = 12;
		final int scale = 5;
		public boolean noMove = false;
		public final int tileSize = originalTileSize * scale;
		final int screenCol = 16;
		final int screenRow = 16;
		private final int HEIGHT = tileSize * screenCol;
		private final int WIDTH = tileSize * screenRow;
		
      	private BufferStrategy strategy;   // take advantage of accelerated graphics
        private boolean waitingForKeyPress = true;  // true if game held up until
                                              // a key is pressed
        private boolean leftPressed = false;  // true if left arrow key currently pressed
        private boolean rightPressed = false; // true if right arrow key currently pressed
        private boolean firePressed = false; // true if firing
        private boolean upPressed = false;
        private boolean downPressed = false;
        private boolean bombPressed = false;
        private boolean gameRunning = true;
        public TileManager tileM = new TileManager(this);
        private String facing = "";
        ArrayList entities = new ArrayList(); // list of entities
                                                      // in game
        private ArrayList removeEntities = new ArrayList(); // list of entities
                                                            // to remove this loop
        private Entity player;  // the ship
        private double moveSpeed = 300; // hor. vel. of ship (px/s)
        private double jumpMovement = 0;
        private long lastFire = 0; // time last shot fired
        private long jumpSpeed = 5;
        private long firingInterval = 300; // interval between shots (ms)
        private int alienCount; // # of aliens left on screen
        private boolean pause = true;
        private String message = ""; // message to display while waiting
                                     // for a key press
        private boolean logicRequiredThisLoop = false; // true if logic
                                                       // needs to be 
                                                       // applied this loop
        private long aLastFire = 0;
        private long aFiringInterval = 800;
		public Graphics g;
    	/*
    	 * Construct our game and set it running.
    	 */
    	public Game() {
    		// create a frame to contain game
    		JFrame container = new JFrame("Samyaks invaision");
    
    		// get hold the content of the frame
    		JPanel panel = (JPanel) container.getContentPane();
    
    		// set up the resolution of the game
    		panel.setPreferredSize(new Dimension(HEIGHT,WIDTH));
    		panel.setLayout(null);
    
    		// set up canvas size (this) and add to frame
    		setBounds(0,0,HEIGHT,WIDTH);
    		panel.add(this);
    
    		// Tell AWT not to bother repainting canvas since that will
            // be done using graphics acceleration
    		setIgnoreRepaint(true);
    
    		// make the window visible
    		container.pack();
    		container.setResizable(false);
    		container.setVisible(true);
    
    
            // if user closes window, shutdown game and jre
    		container.addWindowListener(new WindowAdapter() {
    			public void windowClosing(WindowEvent e) {
    				System.exit(0);
    			} // windowClosing
    		});
    
    		// add key listener to this canvas
    		addKeyListener(new KeyInputHandler());
    
    		// request focus so key events are handled by this canvas
    		requestFocus();

    		// create buffer strategy to take advantage of accelerated graphics
    		createBufferStrategy(2);
    		strategy = getBufferStrategy();
    
    		// initialize entities
    		initEntities();
    		
    		// start the game
    		gameLoop();
        } // constructor
    
    
        /* initEntities
         * input: none
         * output: none
         * purpose: Initialise the starting state of the ship and alien entities.
         *          Each entity will be added to the array of entities in the game.
    	 */
    	private void initEntities() {
              // create the ship and put in center of screen
              player = new Player(this, "sprites/wizard.png",WIDTH / 2 - 20, HEIGHT / 2);
              entities.add(player);
    
              // create a block of aliens (5x12)
              alienCount = 1;
             
              
    	} // initEntities

        /* Notification from a game entity that the logic of the game
         * should be run at the next opportunity 
         */
         public void updateLogic() {
           logicRequiredThisLoop = true;
         } // updateLogic

         /* Remove an entity from the game.  It will no longer be
          * moved or drawn.
          */
         public void removeEntity(Entity entity) {
           removeEntities.add(entity);
         } // removeEntity

         /* Notification that the player has died.
          */
         public void drawMap(Graphics2D g) {
        	 tileM.draw(g);
         }
         
         public void notifyDeath() {
           message = "you are buns at this game!  Try again?";
           waitingForKeyPress = true;
         } // notifyDeath


         /* Notification that the play has killed all aliens
          */
         public void notifyWin(){
           message = "where did the rain spawn in???  You win!";
           waitingForKeyPress = true;
         } // notifyWin

        /* Notification than an alien has been killed
         */
         public void notifyShipHit() {
        	 ((Player) player).shipHit();
         }
         public void notifyAlienKilled() {
           alienCount--;
           
           if (alienCount == 0) {
             notifyWin();
           } // if
           
           // speed up existing aliens
           for (int i=0; i < entities.size(); i++) {
             Entity entity = (Entity) entities.get(i);
             if (entity instanceof AlienEntity) {
               // speed up by 2%
               entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.04);
             } // if
           } // for
         } // notifyAlienKilled

        /* Attempt to fire.*/
         public void tryToFire() {
             // check that we've waited long enough to fire
             if ((System.currentTimeMillis() - lastFire) < firingInterval){
               return;
             } // if

             // otherwise add a shot
             lastFire = System.currentTimeMillis();
             ShotEntity shot = null;

             if(facing.equals("left")) {
           	  
           	   shot = new ShotEntity(this, "sprites/shot.gif", 
                         player.getX() - 60, player.getY() + 40, 1,facing);
           	  
             } else if (facing.equals("right")) {
          
           	   shot = new ShotEntity(this, "sprites/shot.gif", 
                         player.getX() + 120, player.getY() + 40, 1, facing);
           	  
             } else if (facing.equals("up")) {
           	  
           	   shot = new ShotEntity(this, "sprites/shot.gif", 
                         player.getX() + 35, player.getY() - 50, 1,facing);
           	  
             } else if (facing.equals("down")) {
           	  
           	   shot = new ShotEntity(this, "sprites/shot.gif", 
                         player.getX() + 35, player.getY() + 150, 1,facing);
           	  
             } 
             entities.add(shot);
           } // tryToFire
           
        public void tryBomb() {
            // check that we've waited long enough to fire
            if ((System.currentTimeMillis() - lastFire) < firingInterval){
              return;
            } // if

            // otherwise add a shot
            lastFire = System.currentTimeMillis();
            ShotEntity shot = new ShotEntity(this, "sprites/bomb.png", 
                              player.getX() + 10, player.getY() - 30, 1,facing);
            entities.add(shot);
          }  
        
	/*
	 * gameLoop
         * input: none
         * output: none
         * purpose: Main game loop. Runs throughout game play.
         *          Responsible for the following activities:
	 *           - calculates speed of the game loop to update moves
	 *           - moves the game entities
	 *           - draws the screen contents (entities, text)
	 *           - updates game events
	 *           - checks input
	 */
	public void gameLoop() {

          long lastLoopTime = System.currentTimeMillis();
          tileM.setMap("maps/map1.txt");
          // keep loop running until game ends
          while (gameRunning) {

          

            	// calc. time since last update, will be used to calculate
                // entities movement
                long delta = System.currentTimeMillis() - lastLoopTime;
                lastLoopTime = System.currentTimeMillis();

                // get graphics context for the accelerated surface and make it black
                Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
                g.setColor(Color.black);
                g.fillRect(0,0,HEIGHT,WIDTH);
                drawMap(g);
                // move each entity
                if (!waitingForKeyPress && !pause) {
                  for (int i = 0; i < entities.size(); i++) {
                    Entity entity = (Entity) entities.get(i);
                    entity.move(delta);
                  } // for
                } // if

                // draw all entities
                for (int i = 0; i < entities.size(); i++) {
                   Entity entity = (Entity) entities.get(i);
                   entity.draw(g);
                } // for

               
                	
                // brute force collisions, compare every entity
                // against every other entity.  If any collisions
                // are detected notify both entities that it has
                // occurred
               for (int i = 0; i < entities.size(); i++) {
                 for (int j = i + 1; j < entities.size(); j++) {
                    Entity me = (Entity)entities.get(i);
                    Entity him = (Entity)entities.get(j);

                    if (me.collidesWith(him)) {
                      me.collidedWith(him);
                      him.collidedWith(me);
                    } // if
                 } // inner for
               } // outer for
               
               
               
               // remove dead entities
               entities.removeAll(removeEntities);
               removeEntities.clear();

               // run logic if required
               if (logicRequiredThisLoop) {
                 for (int i = 0; i < entities.size(); i++) {
                   Entity entity = (Entity) entities.get(i);
                   entity.doLogic();
                 } // for
                 logicRequiredThisLoop = false;
               } // if

               // if waiting for "any key press", draw message
               if (waitingForKeyPress || pause) {
                 g.setColor(Color.white);
                 g.drawString(message, (800 - g.getFontMetrics().stringWidth(message))/2, 250);
                 g.drawString("Press any key", (800 - g.getFontMetrics().stringWidth("Press any key"))/2, 300);
               }  // if

                // clear graphics and flip buffer
                g.dispose();
                strategy.show();

                // ship should not move without user input
                player.setHorizontalMovement(0);
                player.setVerticalMovement(0);
                
                // respond to user moving ship                        
                if ((leftPressed) && (!rightPressed)) {
                    player.setHorizontalMovement(-moveSpeed);
                } else if ((rightPressed) && (!leftPressed)) {
                    player.setHorizontalMovement(moveSpeed);
                } 
                if((upPressed) && (!downPressed)) {
                    player.setVerticalMovement(-moveSpeed);            	 
                } else if((!upPressed) && (downPressed)) {
                	player.setVerticalMovement(moveSpeed);
                }
                
                
                // if spacebar pressed, try to fire
                if (firePressed && !pause) {
                	tryToFire();
              
                	  
                } // if
            
                 
                // pause
                try { Thread.sleep(0); } catch (Exception e) {}
                
              } // while
            
	} // gameLoop


        /* startGame
         * input: none                   
         * output: none
         * purpose: start a fresh game, clear old data
         */
         private void startGame() {
            // clear out any existing entities and initalize a new set
            entities.clear();
            
            initEntities();
            
            // blank out any keyboard settings that might exist
            leftPressed = false;
            rightPressed = false;
            firePressed = false;
         } // startGame


        /* inner class KeyInputHandler
         * handles keyboard input from the user
         */
	private class KeyInputHandler extends KeyAdapter {
                 
                 private int pressCount = 1;  // the number of key presses since
                                              // waiting for 'any' key press

                /* The following methods are required
                 * for any class that extends the abstract
                 * class KeyAdapter.  They handle keyPressed,
                 * keyReleased and keyTyped events.
                 */
		public void keyPressed(KeyEvent e) {

                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if
                  
                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftPressed = true;
                    facing = "left";
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightPressed = true;
                    facing = "right";
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    firePressed = true;
                  } // if
                  if(e.getKeyCode() == KeyEvent.VK_UP) {
                	  upPressed = true;
                	  facing = "up";
                	  
                  }
                  if(e.getKeyCode() == KeyEvent.VK_P && !pause) {
                	  pause = true;
                	  
                  }else if(e.getKeyCode() == KeyEvent.VK_P) {
                	  pause = false;
                  }
                  if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                	  downPressed = true;
                	  facing = "down";
                  }
                  
                  
               
		} // keyPressed

		public void keyReleased(KeyEvent e) {
                  // if waiting for keypress to start game, do nothing
                  if (waitingForKeyPress) {
                    return;
                  } // if
                  
                  // respond to move left, right or fire
                  if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                    leftPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                    rightPressed = false;
                  } // if

                  if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    firePressed = false;
                  } // if
                 
                  if(e.getKeyCode() == KeyEvent.VK_UP) {
                	  upPressed = false;
                  }
                  if(e.getKeyCode() == KeyEvent.VK_DOWN) {
                	  downPressed = false;
                  }
		} // keyReleased

 	        public void keyTyped(KeyEvent e) {

                   // if waiting for key press to start game
 	           if (waitingForKeyPress) {
                     if (pressCount == 1) {
                       pause = false;
                       waitingForKeyPress = false;
                       startGame();
                       pressCount = 0;
                     } else {
                       pressCount++;
                     } // else
                   } // if waitingForKeyPress

                   // if escape is pressed, end game
                   if (e.getKeyChar() == 27) {
                     System.exit(0);
                   } // if escape pressed

		} // keyTyped

	} // class KeyInputHandler


	/**
	 * Main Program
	 */
	public static void main(String [] args) {
        // instantiate this object
		Game game = new Game();
		
	} // main
} // Game
