/* ShipEntity.java
 * March 27, 2006
 * Represents player's ship
 */
public class Player extends Entity {

  private Game game; // the game in which the ship exists
  protected int health;
  /* construct the player's ship
   * input: game - the game in which the ship is being created
   *        ref - a string with the name of the image associated to
   *              the sprite for the ship
   *        x, y - initial location of ship
   */
  public Player(Game g, String r, int newX, int newY) {
    super(r, newX, newY);  // calls the constructor in Entity
    game = g;
    health = 5;
  } // constructor

  /* move
   * input: delta - time elapsed since last move (ms)
   * purpose: move ship 
   */
  public void move (long delta){
    // stop at left side of screen
    if ((dx < 0) && (x < 10)) {
      return;
    } // if
    // stop at right side of screen
    if ((dx > 0) && (x + sprite.getWidth()> 1000)) {
      return;
    } // if
    for(int i = 0; i < game.tileM.tileData.size(); i++) {
  	   if(game.tileM.tileData.get(i).getCollidable() == true) {
  		   if(this.collidesWith(game.tileM.tileData.get(i), delta )) {
  			  return; 
  		   }
  	   }
  	 
     }
    super.move(delta);  // calls the move method in Entity
  } // move
  
  public void shipHit() {
	  health--;
	  if(health <= 0) {
		  game.notifyDeath();
	  }
  }
 
  /* collidedWith
   * input: other - the entity with which the ship has collided
   * purpose: notification that the player's ship has collided
   *          with something
   */
   public void collidedWith(Entity other) {
     if (other instanceof AlienEntity) {
        game.notifyDeath();
     } // if
     
   } // collidedWith    
 
} // ShipEntity class