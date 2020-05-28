import java.awt.event.KeyEvent;
import java.awt.*;

public class CreativeGame extends AbstractGame {
    
	// intro & instruction
    private static final int INTRO = 0; 
    private static final int INSTRUCTION = 1;
    
    // player & background
    private String PLAYER_IMG = "jetpack.png";    
    private String SPLASH_IMG = "intro1.png";
    private String INSTRUCTION_IMG = "intro2.png";
    private String BACKGROUND = "sky.jpg";
    private String COLLIDE = "collide.png";
    private String LILSTOMPER = "lil stomper.png";
    
    // the object in game
    private static final String LASER = "laser.png";
    private static final String COIN = "coin.png";
    private static final String ROCKET = "rocket.png";
    private static final String TOKEN = "token.png";
    private static final String GEM = "gem.png";
    private static final String SCREENSHOT = "screenshot.gif";
    
    // The window size
    private static final int DEFAULT_GRID_H = 8;
    private static final int DEFAULT_GRID_W = 15;
    
    // speed of the game
    private static final int DEFAULT_TIMER_DELAY = 5;
    protected static final int STARTING_FACTOR = 10;
    protected int STARTING_GRAVITY = STARTING_FACTOR * 3;
    protected int factor = STARTING_FACTOR;
    protected int gravityFactor = STARTING_GRAVITY;
    private static final int ROCKETSPEEDRATIO = 2;
    
    // likelihood of spawning new objects
    private int coinSpawn = 2;
    private static final double probabilityGem = 0.1;
    private static final double probabilityPower = 0.5;
    private int laserSpawn = 5;
    private int rocketSpawn = 7;
    private int tokenSpawn = 8;
    private int maxToken = 2;
    
    // gamegrid variables
    protected Location player;
    
    protected int screen = INTRO;
    
    protected GameGrid grid;
    
    // object timing variables
    private int msGamePause;
    protected int msLastSpace;
    protected int msLastToken;
    protected int DEFAULT_MSLASTSPACE = 180;
    protected int DEFAULT_MSLASTTOKEN = 400;
    protected final int INVINCIBLETIME = 1750;
    private int startInvincible = 0;
    protected final int SHIELDTIME = 200;
    private int startShield = 0;
    
    // difficulty & win/lose variables
    protected int score = 0;
    protected int lives = 3;
    private int livesBeforeLilStomper;
    protected static final int WINNINGSCORE = 100;
    protected int DIFFICULTY = 375; 
    protected static final int MIN_DIFFICULT = 200;
    private int rateIncreaseDifficulty = 5;
    private int increaseDifficulty = 1;
    
    // special properties boolean variables
    private boolean gameRunning = true;   
    private boolean populate = true;
    private boolean invincible = false;
    private boolean lilStomper = false;
    private boolean activateGravity = true;
    private boolean shield = false;
    private boolean launchToken = true;
    
    public CreativeGame() {
        this(DEFAULT_GRID_H, DEFAULT_GRID_W);
    }
    
    public CreativeGame(int grid_h, int grid_w){
    	this(grid_h, grid_w, DEFAULT_TIMER_DELAY);
    }
    
    public CreativeGame(int hdim, int wdim, int init_delay_ms) {
        super(init_delay_ms);
        grid = new GameGrid(hdim, wdim, SPLASH_IMG, BACKGROUND);   
    }
    
    /******************** Methods **********************/
    
    protected void initGame(){
    	player = new Location(grid.getNumRows() - 1, 0);
    	grid.setCellImage(player, PLAYER_IMG);
    	
    	updateTitle();                           
    }
     
    protected void displayIntro(){
    	while (screen <= INSTRUCTION) {
    		// intro
    		if (screen == INTRO){
    			grid.setSplash(SPLASH_IMG);
    			super.sleep(timerDelay);
    			handleKeyPress();
    			handleMouseClick(); 
    		}
    		// instruction
    		if (screen == INSTRUCTION){
    			grid.setSplash(INSTRUCTION_IMG);
    			super.sleep(timerDelay);
    			handleKeyPress();
    			handleMouseClick(); 
    		}
    	}
    	grid.setSplash(null);
    	grid.setGameBackground(BACKGROUND);
    }

    protected void updateGameLoop() { 
        handleKeyPress();        
        
        if (gameRunning){
        	msLastSpace += timerDelay;
        	if (turnsElapsed % factor == 0) {
        		scrollLeft();
        		populateRightEdge();
        	}
        	gravity();
        	specialPower();
        	increaseDifficulty();
        	updateTitle();
        }
    }
    
    // activate special powerwhen collide with the token
    private void specialPower(){
    	// slow the game pace for a second for the player to adapt to the change
    	if ((invincible && !shield) || lilStomper){
    		if (msGamePause < timerDelay * 20){
    			timerDelay = DEFAULT_TIMER_DELAY * 8;
    			msGamePause += timerDelay;
    		} else{
    			// invincible power
    			timerDelay = DEFAULT_TIMER_DELAY;
    			if (invincible){
    				if (factor > 1)
    					factor--;
    				if (startInvincible < INVINCIBLETIME)
    					startInvincible += timerDelay;
    				else{
    					shield = true;
    					factor = STARTING_FACTOR;
    					msGamePause = 0;
    				}
    			}   				
    		}
    	}
    }
    
    private void increaseDifficulty(){
    	if (turnsElapsed % rateIncreaseDifficulty == 0 && DIFFICULTY >= MIN_DIFFICULT)
    		DIFFICULTY--;
    	// halfway: change to all rockets
    	if (score == WINNINGSCORE / 2 && factor > 1 && increaseDifficulty == 1){
    		factor = factor / 2;
    		gravityFactor = gravityFactor / 2;
    		STARTING_GRAVITY = STARTING_GRAVITY / 2;
    		DEFAULT_MSLASTSPACE = DEFAULT_MSLASTSPACE / 2;
    		increaseDifficulty--;
    	}
    	// three forth: more rockets fewer lasers
    	if (score < WINNINGSCORE * 3 / 4 && score > WINNINGSCORE / 2)
    		laserSpawn = 0;
    	else if (increaseDifficulty == 0){
    		laserSpawn--;
    		increaseDifficulty++;
    	}
    }
    
    // gravity features
    private void gravity(){
    	// fall down faster if space is not pressed for a while
    	if (activateGravity && turnsElapsed % gravityFactor == 0){
    		if (msLastSpace > DEFAULT_MSLASTSPACE * 3 && gravityFactor >= STARTING_GRAVITY/2){
    			gravityFactor = STARTING_GRAVITY/2;
    		}
    		if (player.getRow() < grid.getNumRows() - 1){
    			player = new Location(player.getRow() + 1, player.getCol());
    		}
    	}
    	// if on the ground, reset gravity
    	if (player.getRow() == grid.getNumRows() - 1)
    		gravityFactor = STARTING_GRAVITY;
    }
    
    private String getObject(int num){
    	if (num < coinSpawn){
    		double n = rand.nextDouble();
    		if (n < probabilityGem)
    			return GEM;
    		else
    			return COIN;
    	}
    	else if (num < laserSpawn)
    		return LASER;
    	else if (num < rocketSpawn)
    		return ROCKET;
    	else if (num < tokenSpawn && score > WINNINGSCORE/5 && maxToken > 0 && !invincible && !lilStomper && launchToken)
    		return TOKEN;    		
    	return null;
    }
    
    // only launch another token after the last token was launched for a while
    private void checkLaunchToken(){
    	if (!launchToken)
    		msLastToken += timerDelay;
    	if (msLastToken > DEFAULT_MSLASTTOKEN)
    		launchToken = true;
    	else
    		launchToken = false;
    }
    
    // only launch rocket if there is no other objects in the same row
    private boolean checkLaunchRocket(Location loc){
    	for (int j = 1; j < grid.getNumCols(); j++){
    		if (grid.getCellImage(new Location(loc.getRow(), j)) != null){
    			return false;
    		}
    	}
    	return true;
    }
    
    // only repopulate if all the cells in the last column are empty
    private boolean checkPopulate(){
    	for (int i = 0; i < grid.getNumRows(); i++){
    		if (grid.getCellImage(new Location(i, grid.getNumCols() - 1)) != null)
    			return false;
    	}
    	return true;
    }
    
    // update game state to reflect adding in new cells in the right-most column
    private void populateRightEdge() {
    	populate = checkPopulate();
    	checkLaunchToken();
    	if (populate){
    		for (int i = 0; i < grid.getNumRows(); i++){
    			Location loc = new Location(i, grid.getNumCols() - 1);
    			grid.setCellImage(loc, null);
    			String object = getObject(rand.nextInt(DIFFICULTY));
    			if (object != null){
    				if (object.equals(ROCKET) && checkLaunchRocket(loc)){
    					grid.setCellImage(loc, ROCKET);
    				}
    				else if (!object.equals(ROCKET)){
    					grid.setCellImage(loc, object);
    					if (object.equals(TOKEN)){
    						msLastToken = 0;
    						maxToken--;
    					}
    				}
    			}
    		}
    	}
    }
    
    private void printPlayer(){
    	if (lives == 0)
    		grid.setCellImage(player, COLLIDE);
    	// the player flickers when he has a shield, suggesting that he is 
    	// invincible for a short while
    	else if (shield){
    		startShield += timerDelay;
    		invincible = true;
    		if (startShield % (2*timerDelay) == 0)
    	    	grid.setCellImage(player, PLAYER_IMG);
    	    else 
    	    	grid.setCellImage(player, null);
    	    if (startShield > SHIELDTIME){
    	    	shield = false;
    	    	invincible = false;
    	    	startShield = 0;
    	    }    	    	
    	} else if (lilStomper){
    		grid.setCellImage(player, LILSTOMPER);
    	} else
    		grid.setCellImage(player, PLAYER_IMG);
    }
    		
    
    // updates the game state to reflect scrolling left by one column
    private void scrollLeft(){
        for (int i = 0; i < grid.getNumRows(); i++){
        	for (int j = 0; j < grid.getNumCols(); j++){
        		String cell = grid.getCellImage(new Location(i, j));
        		Location oldLoc = new Location(i, j);
        		if (j > 0){
        			Location newLoc = new Location(i, j - 1);
        			if (cell != null){
        				// rocket moves faster than other objects
        				if (cell.equals(ROCKET) ){
        					grid.setCellImage(oldLoc, null);
        					grid.setCellImage(newLoc, ROCKET);
        				}
        				else if (turnsElapsed % (factor*ROCKETSPEEDRATIO) == 0){
        					populate = true;
        					grid.setCellImage(oldLoc, null);
        					grid.setCellImage(newLoc, cell);
        				}    				       				
        			}
        		}else if (!player.equals(oldLoc))
        			grid.setCellImage(oldLoc, null);  		
        	}
        }
        handleCollision();
        printPlayer();
    }
    
    private void handleCollision() {
    	for (int i = 0; i < grid.getNumRows(); i++){
    		Location loc = new Location(i, 0);
    		if (player.equals(loc) && grid.getCellImage(loc) != null){
    			if (grid.getCellImage(loc).equals(COIN)){
    				score += 4;
    			}
    			else if (grid.getCellImage(loc).equals(GEM))
    				score += 8;
    			else if (grid.getCellImage(loc).equals(LASER) && !invincible){    					
    				lives--;
    				if (!lilStomper)
    					shield = true;
    				else if (lives == 0){
    					lilStomper = false;
    					lives = livesBeforeLilStomper;
    					shield = true;
    				} 
    			}
    			else if (grid.getCellImage(loc).equals(ROCKET) && !invincible){
    				if (lilStomper){
    					lilStomper = false;
    					lives = livesBeforeLilStomper;
    					shield = true;
    				} else{
    					lives = 0;
    				}
    			} else if (grid.getCellImage(loc).equals(TOKEN)){
    				double n = rand.nextDouble();
    				if (n < probabilityPower)
    					invincible = true;
    				else{
    					lilStomper = true;
    					livesBeforeLilStomper = lives;
    				}
    			}
    		}   
    	}
    }
    
    //---------------------------------------------------//
    
    // handles actions upon mouse click in game, for intro and instruction
    private void handleMouseClick() {
        
        Location loc = grid.checkLastLocationClicked();
        
        if (screen == INTRO && loc != null){
        	if (loc.getRow() >= 3 && loc.getRow() <= 4 && loc.getCol() >= 8 && loc.getCol() <= 12) 
            	screen = INSTRUCTION + 1;
            else if (loc.getRow() >= 5 && loc.getRow() <= 7 && loc.getCol() >= 8 && loc.getCol() <= 12)
            	screen = INSTRUCTION;
            loc = null;
        } 
        if (screen == INSTRUCTION && loc != null){
        	screen++;
        	loc = null;
        }    
    }
    
    // handles actions upon key press in game
    protected void handleKeyPress() {
        
        int key = grid.checkLastKeyPressed();
        
        // Q for quit
        if (key == KeyEvent.VK_Q)
            System.exit(0);
        
        // screenshot
        else if (key == KeyEvent.VK_S){
        		grid.save(SCREENSHOT);
        		System.out.println("could save the screen: add the call");
        } 
        
        // pause
        else if (key == KeyEvent.VK_P){
        	if (gameRunning)
        		gameRunning = false;
        	else
        		gameRunning = true;
        }        	
        
        if (gameRunning){
        	if (key == KeyEvent.VK_SPACE){
        		if (screen <= INSTRUCTION)
        			screen++;        	
        		else if (player.getRow() > 0){
        			activateGravity = false;
        			msLastSpace = 0;
        			player = new Location(player.getRow() - 1, player.getCol());
        		}
        	}
        	else{
        		// count the time between the last space pressed
        		if (msLastSpace > DEFAULT_MSLASTSPACE)
        			activateGravity = true;
        		
        		if (key == KeyEvent.VK_COMMA)  {
        			timerDelay = timerDelay * 2; 
        		}
        		
        		else if (key == KeyEvent.VK_PERIOD) {
        			timerDelay = timerDelay / 2;
        		}
        		
        		else if (key == KeyEvent.VK_D){
        			if (grid.getLineColor() == null)
        				grid.setLineColor(Color.RED);
        			else
        				grid.setLineColor(null);
        		}
        	}
        }
    }
    
    private int getScore() {
        return score;    
    }
    
    private int getLives(){
    	return lives;
    }
    
    private void updateTitle() {
        grid.setTitle("Jetpack Joyride ____ Score: " + getScore()+ "/100  Lives: " + getLives());
    }
    
    protected boolean isGameOver() {
    	return (score >= WINNINGSCORE || lives == 0);
    }
    
    protected void displayOutcome() {
    	if (lives == 0)
    		grid.setTitle("You lose! Your score was " + getScore() + "!");
		if (score == WINNINGSCORE)
			grid.setTitle("You win! You've achieved a score of " + getScore() + "!");
    }
}
