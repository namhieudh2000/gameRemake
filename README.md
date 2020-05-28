A simple recreation of Jetpack Joyride.

Theme: the game is inspired by Halfbricks Studio's Jetpack Joyride game. All of the images
were taken from the game itself. The player is a man using his jetpack to avoid obstacles 
and obtain coins.

Customization:
1. Movement: Instead of being able to move in all four directions, the player can only move upwards.
This is reasonable since the main character is using a jetpack and it can only fire upwards.
However, gravitational force will naturally pull him down.

2. Obstacles: The obstacles have different speed. The laser is a static object so it "shifts" left 
in the same pace as the game. The rocket is moving towards the player, so it "shifts" faster 
than the laser. As a result, rockets will instantly kill you after collision, while 
colliding with lasers is less lethal.

3. "Recovery time": this is the period where the player is flickering. When the player is in
recovery, he is immune to collision with any obstacles (he can still collect coins and tokens).
The recovery time usually takes place after collision or special power for the player to
adapt to the change.

4. Special Power: the token gives you special power. "Invincibility" will fast forward you in
the game, make you immune to collision but still have the ability to collect coins. "Lil Stomper"
gives you an extra armor. You will not die from rockets, however, you only lose the armor. The
armor also provides you with lives against the laser.

5. Difficulty: The game will VERY SLIGHTLY increase the difficulty as time passes (the likelihood
of spawning object increases). After you reach halfway (score = 0.5 * winningScore), you will 
temporarily face only rockets (at a higher spawn rate) and the speed of the game slightly increases.
After you reach three-forth of the game, lasers will be brought back to the game, but
rockets are still spawned at a high rate.

6. Winning/Losing Condition: reach 100 points (to win) or lives = 0 or collide with rockets (to lose)

How did the extra features were implemented:
1. Gravity:
- constant: 
	(boolean) activateGravity: indicate when to activate gravity
	(int) STARTING_GRAVITY: the default speed of falling
	(int) gravityFactor: speed of falling (will be adjusted during the game)
	(int) DEFAULT_MSLASTSPACE, msLastSpace: keep track of the last instance the player
											hits space to change the gravityFactor accordingly
- method: gravity(); handleKeyPressed();

2. Different Speed:
- constant: 
	(int) ROCKETSPEEDRATIO: how faster the rocket moves in respect to other objects
	(boolean) populate: indicate when to populate right edge
- method: scrollLeft(), checkPopulate(), populateRightEdge()
--> the different speed creates a new problem when populating right edge because they
are scrolled left differently

3. Spawn Object:
- constant: (int) coinSpawn, laserSpawn, rocketSpawn, tokenSpawn, maxToken
			(double) probabilityGem, probabilityPower
			(int) msLastToken, DEFAULT_MSLASTTOKEN: keep track of the token spawn
- method: getObject()
			
4. Difficulty:
- constant: (int) DIFFICULTY: affects spawn rate
			(int) MIN_DIFFICULT: the highest spawn rate in the game
			(int) rateIncreaseDifficulty: how often difficulty is updated
- method: increaseDifficulty()

5. Special Power & Recovery Time
- constant: (boolean) lilStomper, invincible: to indicate which special power is activated
			(boolean) shield: recovery time
			(int) INVINCIBLETIME, startInvincible: keep track of how long the player 
												   "invincible"
			(int) msGamePause: keep track of the small pause when the player gets the special
								power
			(int) SHIELDTIME, startShield: keep track of how long the player is "in recovery"
- method: invincible(), handleCollision(), printPlayer()

Most Proud Implementation:
- I think it's the different speed between the rockets and the lasers. It seems very easy
to change but once you do that, it affects your populateRightEdge() and ScrollLeft(): it
keeps updating the right edge even thought the laser has not moved yet (since the rocket moves
faster). It took me a while to fix it using boolean variable. The same problem occurs with
gravity. Since gravity does not necessarily move at the same speed as the other object, I have
to try a lot of the numbers to make gravity seems real and playable. There were times when 
gravity glitched too fast that it seems like the player teleport, and there were times when
gravity had so little effect that the player fell down too slowly. I had to do a lot of 
outside calculation to make it seem realistic.
