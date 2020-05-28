public class GameLauncher{ 
   /**
   * Only CHANGE constant values as appropriate
   *
   * 
   * THERE SHOULD NOT B ANYTHING SUBSTANTIAL TO **ADD** TO THIS CLASS
   * 
   * The Scrolling Game is described the Project Handout. 
   *  
   * @author Elodie Fourquet 
   * @date October, 2019
   */
   
   
   
   private static final int DEMO = 0;
   private static final int BASE = 1;
   private static final int CREATIVE = 2;
   
   private static final int RUNNING = CREATIVE;
   
   private static AbstractGame game;
   
   
   public static void main(String[] args) {
      
      switch (RUNNING) {
      /*
      case DEMO:  
         // Construct a DEMO Game of the provided size
         game = new DemoGame(5, 10);
         System.out.println("From GameLauncher main: Running the executable demo version");
         break;
      case BASE:
         // Construct sized version of the base game you've written
         game = new BaseGame(12, 20);
         System.out.println("From GameLauncher main: Running the BaseGame version");
         break;
      */
      case CREATIVE:
         // Construct sized version of the creative game you've written
         game = new CreativeGame();
         System.out.println("From GameLauncher main: Running the CreativeGame version ");
         break;
      default:
         System.out.println("Not sure which version you meant...");
         
      }
      
      // launch the instantiated game version: trace from parent,
      // i.e. ScrollingGame
      game.play();
      
   }
   
}
