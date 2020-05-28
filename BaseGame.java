import java.awt.event.KeyEvent;
import java.awt.*;

public class BaseGame extends AbstractGame {
    
    private static final int INTRO = 0; 
    
    private String PLAYER_IMG = "user.gif";    // specify user image file
    private String SPLASH_IMG = "ink.png";
    
    // ADD others for Avoid/Get items 
    private static String AVOID = "avoid.gif";
    private static String GET = "get.gif";
    private static final String SCREENSHOT = "screenshot.gif";
    
    private static int A = 0;
    private static int G = 1;
    
    // default number of vertical/horizontal cells: height/width of grid
    private static final int DEFAULT_GRID_H = 5;
    private static final int DEFAULT_GRID_W = 10;
    
    private static final int DEFAULT_TIMER_DELAY = 100;
    
    // default location of user at start
    private static final int DEFAULT_PLAYER_ROW = 0;
    
    protected static final int STARTING_FACTOR = 3;      // you might change that this when working on timing
    protected int factor = STARTING_FACTOR;
    
    protected Location player;
    
    protected int screen = INTRO;
    
    protected GameGrid grid;
    
    protected int score = 0;
    protected int lives = 10;
    protected static final int WINNINGSCORE = 100;
    protected static int DIFFICULTY = 12; 
    
    protected boolean gameRunning = true;   
    
    public BaseGame() {
        this(DEFAULT_GRID_H, DEFAULT_GRID_W);
    }
    
    public BaseGame(int grid_h, int grid_w){
    	this(grid_h, grid_w, DEFAULT_TIMER_DELAY);
    }
    
    
    public BaseGame(int hdim, int wdim, int init_delay_ms) {
        super(init_delay_ms);
        //set up our "board" (i.e., game grid) 
        grid = new GameGrid(hdim, wdim);   
        
    }
    
    /******************** Methods **********************/
    
    protected void initGame(){
    	
    	// store and initialize user position
    	player = new Location(DEFAULT_PLAYER_ROW, 0);
    	grid.setCellImage(player, PLAYER_IMG);
    	
    	updateTitle();                           
    }
    
    
    // Display the intro screen: not too interesting at the moment
    // Notice the similarity with the while structure in play()
    // sleep is required to not consume all the CPU; going too fast freezes app 
    protected void displayIntro(){
    	
    	grid.setSplash(SPLASH_IMG);
    	while (screen == INTRO) {
    		super.sleep(timerDelay);
    		// Listen to keep press to break out of intro 
    		// in particular here --> space bar necessary
    		handleKeyPress();
    	}
    	grid.setGameBackground(null);
    }
    
    protected void updateGameLoop() {
        
        handleKeyPress();        // update state based on user key press
        handleMouseClick();      // when the game is running: 
        // click & read the console output 
        
        if (turnsElapsed % factor == 0 && gameRunning) {  // if it's the FACTOR timer tick
            // constant 3 initially
            scrollLeft();
            populateRightEdge();
        }     
        updateTitle();
        
    }    		
    
    // update game state to reflect adding in new cells in the right-most column
    private void populateRightEdge() {
        for (int i = 0; i < grid.getNumRows(); i++){
        	Location loc = new Location(i, grid.getNumCols() - 1);
        	if (!loc.equals(player)){
        		grid.setCellImage(loc, null);
        	}
        	int num = rand.nextInt(DIFFICULTY);
        	if (!loc.equals(player)){
        		if (num == A)
        			grid.setCellImage(loc, AVOID);
        		else if (num == G)
        			grid.setCellImage(loc, GET);
        	}
        }
    }
    
    // updates the game state to reflect scrolling left by one column
    private void scrollLeft(){
        for (int i = 0; i < grid.getNumRows(); i++){
        	for (int j = 1; j < grid.getNumCols(); j++){
        		String cell = grid.getCellImage(new Location(i, j));
        		Location loc = new Location(i, j - 1);
        		if (cell == null || cell.equals(PLAYER_IMG))
        			grid.setCellImage(loc, null);
        		else
        			grid.setCellImage(loc, cell);
        		handleCollision();
        	}
        }
    }
    
    
    /* handleCollision()
    * handle a collision between the user and an object in the game
    */    
    private void handleCollision() {
    	for (int i = 0; i < grid.getNumRows(); i++){
    		for (int j = 0; j < grid.getNumCols(); j++){
    			Location loc = new Location(i, j);
    			if (player.equals(loc)){
    				if (grid.getCellImage(loc) == GET)
    					score += 5;
    				else if (grid.getCellImage(loc) == AVOID)
    					lives--;
    				grid.setCellImage(player, PLAYER_IMG);
    			}
    			
    		}
    	}     	   	   
        
    }
    
    //---------------------------------------------------//
    
    // handles actions upon mouse click in game
    private void handleMouseClick() {
        
        Location loc = grid.checkLastLocationClicked();
        
        if (loc != null) 
            System.out.println("You clicked on a square " + loc);
        
    }
    
    // handles actions upon key press in game
    protected void handleKeyPress() {
        
        int key = grid.checkLastKeyPressed();
        
        //use Java constant names for key presses
        //http://docs.oracle.com/javase/7/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_DOWN
        
        // Q for quit
        if (key == KeyEvent.VK_Q)
            System.exit(0);
        
        else if (key == KeyEvent.VK_S){
        		grid.save(SCREENSHOT);
        		System.out.println("could save the screen: add the call");
        } 
        
        else if (key == KeyEvent.VK_P){
        	if (gameRunning)
        		gameRunning = false;
        	else
        		gameRunning = true;
        }        	
        
        if (gameRunning){
        	if (key == KeyEvent.VK_SPACE)
        		screen += 1;        	
        	
        	else if (key == KeyEvent.VK_UP){
        		if (player.getRow() > 0){
        			player = new Location(player.getRow() - 1, player.getCol());
        		}
        	}
        	
        	else if (key == KeyEvent.VK_DOWN){
        		if (player.getRow() < grid.getNumRows() - 1){
        			player = new Location(player.getRow() + 1, player.getCol());
        		}
        	}	
        	
        	else if (key == KeyEvent.VK_LEFT){
        		if (player.getCol() > 0){
        			player = new Location(player.getRow(), player.getCol() - 1);
        		}
        	}	
        	
        	else if (key == KeyEvent.VK_RIGHT){
        		if (player.getCol() < grid.getNumCols() - 1){
        			player = new Location(player.getRow(), player.getCol() + 1);
        		}
        	}
        	
        	/* To help you with step 9: 
        	use the 'T' key to help you with implementing speed up/slow down/pause
        	this prints out a debugging message */
        	else if (key == KeyEvent.VK_COMMA)  {
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
    
    // return the "score" of the game 
    private int getScore() {
        return score;    
    }
    
    private int getLives(){
    	return lives;
    }
    
    // update the title bar of the game window 
    private void updateTitle() {
        grid.setTitle("Scrolling Game ____ Score: " + getScore()+ "  Lives: " + getLives());
    }
    
    // return true if the game is finished, false otherwise
    //      used by play() to terminate the main game loop 
    protected boolean isGameOver() {
    	return (score == WINNINGSCORE || lives == 0);
    }
    
    // display the game over screen, blank for now
    protected void displayOutcome() {
    	if (lives == 0)
    		grid.setTitle("You lose! Your score was " + getScore() + "!");
		if (score == WINNINGSCORE)
			grid.setTitle("You win! You've achieved a score of " + getScore() + "!");
    }
}
