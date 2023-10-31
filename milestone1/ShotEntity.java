/* ShotEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class ShotEntity extends Entity {
  private Entity entity;
  private double moveSpeed = -300; // vert speed shot moves
  private boolean used = false; // true if shot hits something
  private Game game; // the game in which the ship exists
  private int direction;
  private String facing;

  /* construct the shot
   * input: game - the game in which the shot is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the shot
   *        x, y - initial location of shot
   */
  public ShotEntity(Game g, String r, int newX, int newY, int dir,String facing) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    dy = moveSpeed;
    direction = dir;
    this.facing = facing;
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move shot
   */
  /*
  public void move (long delta){
	  
    super.move(delta * direction);  // calls the move method in Entity

    // if shot moves off top of screen, remove it from entity list
    if (y < -100) {
      game.removeEntity(this);
    } // if
    if(y > 1100) {
      game.removeEntity(this);
    }

  } // move
*/
  
  public void move(long delta) {
	    super.move(delta * direction); // moves the shot in the given direction

	    // If the player is facing "left" or "right," adjust the shot's movement
	    if (facing.equals("left")) {
	        dx = moveSpeed * direction * 2; // Set horizontal movement based on direction
	        dy = 0; // Set vertical movement to 0
	    } else if (facing.equals("right")) {
	        dx = moveSpeed * direction * -2; // Set horizontal movement based on direction
	        dy = 0; // Set vertical movement to 0
	    } else if (facing.equals("up")) {
	        dy = moveSpeed * direction * 2; // Set vertical movement based on direction
	        dx = 0; // Set horizontal movement to 0
	    } else if (facing.equals("down")) {
	        dy = moveSpeed * direction * -2; // Set vertical movement based on direction
	        dx = 0; // Set horizontal movement to 0
	    }

	    // if shot moves off the screen, remove it from entity list
	    if (y < -100 || y > 1100 || x < -100 || x > 1100) {
	        game.removeEntity(this);
	    }
	}

  /* collidedWith
   * input: other - the entity with which the shot has collided
   * purpose: notification that the shot has collided
   *          with something
   */
   public void collidedWith(Entity other) {
     // prevents double kills
     if (used) {
       return;
     } // if
     
     //if(other instanceof Player) {
    //	 game.removeEntity(this);
      //   game.notifyShipHit();
  //   }
     
     // if it has hit an alien, kill it!
     if (other instanceof AlienEntity) {
       // remove affect entities from the Entity list
       game.removeEntity(this);
       game.removeEntity(other);
       
       // notify the game that the alien is dead
       game.notifyAlienKilled();
       used = true;
     } // if

   } // collidedWith

} // ShotEntity class