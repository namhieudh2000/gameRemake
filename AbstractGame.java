import java.util.Random;

public abstract class AbstractGame {
   
   //USE ME, i.e. this instance (Don't instantiate a new Random object every frame) 
   protected static Random rand = new Random();   
   
   
   // Game clock: ms elapsed since start of game main loop---see play() 
   protected long msElapsed;  
   
   // used to space animation and key presses
   protected int turnsElapsed; 
   
   // Control speed of game
   protected int timerDelay;  
   
   
   //---------------- Constructor -----------------//
   
   
   public AbstractGame(int timerDelay) {
      
      this.timerDelay = timerDelay;
      
      // animation settings
      turnsElapsed = 0;
      msElapsed = 0;
      
   }
   
   
   //---------------------------------------------------//
   
   public void play(){     
      
      initGame();
      displayIntro();
      
      while (!isGameOver()){
         this.sleep(timerDelay);
         msElapsed += timerDelay;
         
         // implemented in child (check player input and update assets)
         updateGameLoop();
         turnsElapsed++;   
      }
      displayOutcome();     
   }
   
   // Give CPU time to other application: essential in gameLoop
   protected void sleep(int milliseconds) {
      try {
         Thread.sleep(milliseconds);
    } catch(Exception e) { }
   }
   
   //---------------- Abstract methods -----------------//
   // methods you must implement in the Base and Creative Versions    
   
   // checks for game over conditions
   protected abstract boolean isGameOver();
   
   // changes the title bar in the game window
   protected abstract void initGame();
   
   // gives the game introduction
   protected abstract void displayIntro();
                                       
   // handles actions upon game end
   protected abstract void displayOutcome();
   
   protected abstract void updateGameLoop();
   
   protected abstract void handleKeyPress();

   
   
}
