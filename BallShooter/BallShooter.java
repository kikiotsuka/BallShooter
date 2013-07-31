/*
 * Ball Shooter Game by Mitsuru Otsuka
 * Version 1.1.4
 */

package BallShooter;

import java.applet.*;
import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.ArrayList;

//implements applet to create applet
//implements runnable for threads(?)
//implemetns mouselistener to get mouse events
public class BallShooter extends Applet implements Runnable, MouseListener, MouseMotionListener, KeyListener {
	static final long serialVersionUID = 32L;
	
	final String version = "V 1.1.4";
	
	ArrayList<Ball> ball;
	Player player;
	Ball bomb;
	boolean isBomb;
	
	//Difficulty settings
	//0 = noob - radius doesnt decrease
	//1 = medium - normal
	//2 = hard - extra ball
	//3 = lunatic - 2 extra balls, fixed fire speed (5), radius starts at 30
	Color colors[] = {Color.red, Color.blue, Color.green, Color.yellow};
	//by default difficulty will be 1
	int difficultySetting;
	boolean isMenu;
	boolean isGame;
	boolean gameOver;
	boolean isInstructions;
	boolean isDifficulty;
	
	//Menu button pictures
	//variable name, dimensions, (x,y) coordinates
	Image start;//180x65, (160, 150)
	Image instructions;//235x65,(132, 250) 
	Image difficulty;//190x65, (155, 350)
	Image noob;//115x55, (193, 50)
	Image medium;//180x60, (160, 150)
	Image hard;//115x50, (193, 250)
	Image lunatic;//222x62, (139, 350)
	Image back;//110x45, (350, 435)
	Image instructionsText;//400x385, (50, 30)
	
	//Change the cursor
	Image cursor;
	Toolkit toolkit;
	Point hotspot;
	Cursor cursorIcon;
	
	//non-click box x,y,width,height
	int nonClickBox[] = {220, 220, 60, 60};
	Rectangle2D box = new Rectangle(220, 220, 60, 60);
	
	//x, y, width, height
	int startButton[] = {160, 150, 180, 65};
	int instructionsButton[] = {132, 250, 235, 65};
	int difficultyButton[] = {155, 350, 190, 65};
	int noobButton[] = {193, 50, 115, 55};
	int mediumButton[] = {160, 150, 180, 60};
	int hardButton[] = {193, 250, 115, 50};
	int lunaticButton[] = {139, 350, 222, 62};
	int backButton[] = {350, 435, 110, 45};
	int instructionsTextButton[] = {50, 30, 400, 385};
	final int highlightDistance = 15;
	Rectangle2D highlight;
	Rectangle2D grey;
	final Color greyColor = new Color(128, 128, 128);
	
	//Double Buffer stuff
	Image image;
	Graphics graphics;
	Thread th;
	
	//initialize variables
	public void init() {
		setSize(500, 500);
		//FIX CURSOR
		toolkit = Toolkit.getDefaultToolkit();
		cursor = getImage(getCodeBase(), "cursor.gif");
		hotspot = new Point(16, 16);
		cursorIcon = toolkit.createCustomCursor(cursor, hotspot, "Cursor");
		setCursor(cursorIcon);
		//instantiate game variables
		ball = new ArrayList<Ball>();
		player = new Player();
		isBomb = false;
		//set background color
		setBackground(new Color(0, 191, 255));
		//set current game state to the menu
		isMenu = true;
		isGame = false;
		gameOver = false;
		isInstructions = false;
		isDifficulty = false;
		difficultySetting = 1;
		//get images
		start = getImage(getCodeBase(), "start.png");
		instructions = getImage(getCodeBase(), "instructions.png");
		difficulty = getImage(getCodeBase(), "difficulty.png");
		noob = getImage(getCodeBase(), "easy.png");
		medium = getImage(getCodeBase(), "medium.png");
		hard = getImage(getCodeBase(), "hard.png");
		lunatic = getImage(getCodeBase(), "impossible.png");
		back = getImage(getCodeBase(), "back.png");
		instructionsText = getImage(getCodeBase(), "instructionsText.png");
		//add event listener object for mouse
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
		setFocusable(true);
	}
	public void reset() {
		ball = new ArrayList<Ball>();
		player = new Player();
		difficultySetting = 1;
		isBomb = false;
		bomb = null;
		grey = null;
	}
	//start game thread
	public void start() {
		th = new Thread(this);
		th.start();
	}
	//called to run the stuff, contains the game loop
	public void run() {
		//set game thread priority
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
		//game loop
		while (true) {
			//game loop
			//calculations go here
			if (isGame) {
				//move balls
				for (Ball x : ball) {
					x.move();
				}
				//check for out of bounds
				//if out of bounds, decrease player lives and reset ball location and vectors
				for (Ball x : ball) {
					if (x.outOfBounds()) {
						player.decreaseLives();
						x.lightReset();
					}
				}
				if (!isBomb) {
					if (spawnBomb()) {
						bomb = new Ball(2);
						isBomb = true;
					}
				}
				if (isBomb) {
					bomb.move();
					if (bomb.outOfBounds()) {
						bomb = null;
						isBomb = false;
					}
				}
				//check if number of lives is 0
				//if it is 0, display game over screen
				if (player.getLives() <= 0) {
					gameOver = true;
					isGame = false;
				}
				//the reason why the repaint method is inside is so that
				//it paints when necessary, on the menu screen, it gets repainted
				//in the method mouseClicked
				repaint();
			}
			//FPS clock
			try {
				Thread.sleep(20);
			}
			catch (Exception e) {
			}
			//set game thread priority to maximum
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		}
	}
	//check if a bomb is spawnable or not
	public boolean spawnBomb() {
		//if bomb is present on screen, do not spawn another one
		if (isBomb) {
			return false;
		}
		//1 in 15 chance a bomb will spawn
		int x = (int) (Math.random() * 30);
		//why 2? because 2 is my favorite number.
		if (x == 2) {
			return true;
		}
		return false;
	}
	//check if menu button was clicked
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (isMenu) {
			//0 - x
			//1 - y
			//2 - width
			//3 - height
			
			//start button
			if ((x > startButton[0] && x < startButton[0] + startButton[2]) && 
					(y > startButton[1] && y < startButton[1] + startButton[3])) {
				isMenu = false;
				isGame = true;
				for (int i = 0; i <= difficultySetting; i++) {
					ball.add(new Ball(difficultySetting));
				}
				grey = null;
			}
			//instructions button
			else if ((x > instructionsButton[0] && x < instructionsButton[0] + instructionsButton[2]) && 
					(y > instructionsButton[1] && y < instructionsButton[1] + instructionsButton[3])) {
				isMenu = false;
				isInstructions = true;
				grey = null;
			}
			else if ((x > difficultyButton[0] && x < difficultyButton[0] + difficultyButton[2]) && 
					(y > difficultyButton[1] && y < difficultyButton[1] + difficultyButton[3])) {
				isMenu = false;
				isDifficulty = true;
				x = 0;
				y = 0;
				grey = new Rectangle(lunaticButton[0] - highlightDistance, lunaticButton[1] - highlightDistance, 
						lunaticButton[2] + highlightDistance * 2, lunaticButton[3] + highlightDistance * 2);
			}
			repaint();
		}
		if (isInstructions) {
			if ((x > backButton[0] && x < backButton[0] + backButton[2]) && 
					(y > backButton[1] && y < backButton[1] + backButton[3])) {
				isInstructions = false;
				isMenu = true;
				grey = null;
			}
			repaint();
		}
		if (isDifficulty) {
			if ((x > backButton[0] && x < backButton[0] + backButton[2]) && 
					(y > backButton[1] && y < backButton[1] + backButton[3])) {
				isDifficulty = false;
				isMenu = true;
				grey = null;
			}
			else if ((x > noobButton[0] && x < noobButton[0] + noobButton[2]) && 
					(y > noobButton[1] && y < noobButton[1] + noobButton[3])) {
				difficultySetting = 0;
			}
			else if ((x > mediumButton[0] && x < mediumButton[0] + mediumButton[2]) && 
					(y > mediumButton[1] && y < mediumButton[1] + mediumButton[3])) {
				difficultySetting = 1;
			}
			else if ((x > hardButton[0] && x < hardButton[0] + hardButton[2]) && 
					(y > hardButton[1] && y < hardButton[1] + hardButton[3])) {
				difficultySetting = 2;
			}
			else if ((x > lunaticButton[0] && x < lunaticButton[0] + lunaticButton[2]) && 
					(y > lunaticButton[1] && y < lunaticButton[1] + lunaticButton[3])) {
				difficultySetting = 3;
			}
			repaint();
		}
		if (gameOver) {
			if ((x > backButton[0] && x < backButton[0] + backButton[2]) && 
					(y > backButton[1] && y < backButton[1] + backButton[3])) {
				gameOver = false;
				isMenu = true;
				reset();
			}
		}
	}
	//check if balls were pressed
	//gets the mouse coordinates and compares with ball coordinates
	public void mousePressed(MouseEvent e) {
		if (isGame) {
			int x = e.getX();
			int y = e.getY();
			//if player has hit a ball, this turns true
			//other wise if it stays false, then player missclick increases by one
			boolean tempHit = false;
			if (!((x > nonClickBox[0] && x < nonClickBox[0] + nonClickBox[2]) && 
					(y > nonClickBox[1] && y < nonClickBox[1] + nonClickBox[3]))) {
				for (Ball i : ball) {
					if (i.isMoving()) {
						if (i.userHit(x, y)) {
							player.increaseScore();
							tempHit = true;
						}
					}
				}
			}
			if (isBomb) {
				if (bomb.userHit(x, y)) {
					player.decreaseLives();
					bomb = null;
					isBomb = false;
					tempHit = true;
				}
			}
			if (!tempHit) {
				player.increaseMiss();
				if (player.getMissClick() == 3) {
					player.penalizeScore();
					player.resetMiss();
				}
			}
		}
	}
	//paint screen using double buffers
	public void update(Graphics g) {
		//check if there is an image
		if (image == null) {
			image = createImage(this.getWidth(), this.getHeight());
			graphics = image.getGraphics();
		}
		//create graphics
		graphics.setColor(getBackground());
		graphics.fillRect(0,  0,  this.getWidth(),  this.getHeight());
		graphics.setColor(getForeground());
		//display graphics
		paint(graphics);
		g.drawImage(image, 0, 0, this);
	}
	//paint the screen
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		//set screen dimensions to 500x500
		setSize(500, 500);
		//display menu
		if (isMenu) {
			//display grey highlight
			g2.setColor(greyColor);
			try {
				g2.fill(grey);
			}
			catch (Exception e) {
				
			}
			//display menu buttons
			g2.drawImage(start, startButton[0], startButton[1], this);
			g2.drawImage(instructions, instructionsButton[0], instructionsButton[1], this);
			g2.drawImage(difficulty, difficultyButton[0], difficultyButton[1], this);
			g2.setColor(Color.black);
			g2.drawString(version, 5, 495);
		}
		if (isInstructions) {
			g2.setColor(greyColor);
			try {
				g2.fill(grey);
			}
			catch (Exception e) {	
			}
			g2.drawImage(instructionsText, instructionsTextButton[0], instructionsTextButton[1], this);
			g2.drawImage(back, backButton[0], backButton[1], this);
		}
		if (isDifficulty) {
			g2.setColor(greyColor);
			try {
				g2.fill(grey);
			}
			catch (Exception e) {
				
			}
			highlight = null;
			switch (difficultySetting) {
				case(0):
					highlight = new Rectangle(noobButton[0] - highlightDistance, noobButton[1] - highlightDistance, 
							noobButton[2] + highlightDistance * 2, noobButton[3] + highlightDistance * 2);
					break;
				case(1):
					highlight = new Rectangle(mediumButton[0] - highlightDistance, mediumButton[1] - highlightDistance, 
							mediumButton[2] + highlightDistance * 2, mediumButton[3] + highlightDistance * 2);
					break;
				case(2):
					highlight = new Rectangle(hardButton[0] - highlightDistance, hardButton[1] - highlightDistance, 
							hardButton[2] + highlightDistance * 2, hardButton[3] + highlightDistance * 2);
					break;
				case(3):
					highlight = new Rectangle(lunaticButton[0] - highlightDistance, lunaticButton[1] - highlightDistance, 
							lunaticButton[2] + highlightDistance * 2, lunaticButton[3] + highlightDistance * 2);
					break;
			}
			g2.setColor(Color.yellow);
			g2.fill(highlight);
			g2.drawImage(noob, noobButton[0], noobButton[1], this);
			g2.drawImage(medium, mediumButton[0], mediumButton[1], this);
			g2.drawImage(hard, hardButton[0], hardButton[1], this);
			g2.drawImage(lunatic, lunaticButton[0], lunaticButton[1], this);
			g2.drawImage(back, backButton[0], backButton[1], this);
		}
		//display game
		if (isGame) {
			ArrayList<Ellipse2D> tempEllipse = new ArrayList<Ellipse2D>();
			ArrayList<Ellipse2D> tempEllipseCircumference = new ArrayList<Ellipse2D>();
			for (Ball x : ball) {
				int tempX = x.getX();
				int tempY = x.getY();
				int tempRadius = x.getRadius();
				//remember that ellipses are drawn like a rectangle, the x,y point is taken, and then
				//the ellipse is drawn to fit the given width/height as if it is being inscribed within a rectangle
				tempEllipse.add(new Ellipse2D.Double(tempX - tempRadius / 2, tempY - tempRadius / 2, tempRadius, tempRadius));
				tempEllipseCircumference.add(new Ellipse2D.Double(tempX - tempRadius / 2 - 3, tempY - tempRadius / 2 - 3, 
						tempRadius + 6, tempRadius + 6));
			}
			//draw outline of balls
			for (int x = 0; x < tempEllipse.size(); x++) {
				g2.setColor(Color.black);
				g2.fill(tempEllipseCircumference.get(x));
				g2.setColor(colors[x]);
				g2.fill(tempEllipse.get(x));
			}
			//display bomb
			if (isBomb) {
				g2.setColor(Color.black);
				int tempX = bomb.getX();
				int tempY = bomb.getY();
				int tempRadius = bomb.getRadius();
				g2.fill(new Ellipse2D.Double(tempX - tempRadius / 2, tempY - tempRadius / 2, tempRadius, tempRadius));
			}
			//display player score/lives
			g2.setColor(Color.black);
			g2.setFont(new Font("SANS SERIF", Font.PLAIN, 12));
			g2.drawString("Score: " + player.getScore(), 10, 10);
			g2.drawString("Lives: " + player.getLives(), 10, 25);
			g2.drawString("Miss Clicks: " + player.getMissClick(), 10, 40);
			//display non-clickable box
			//prevents spam click
			g2.draw(box);
			//display center crosshair (idk why i did this)
			g2.drawLine(240,250,260,250);
			g2.drawLine(250,240,250,260);
		}
		if (gameOver) {
			g2.setColor(greyColor);
			try {
				g2.fill(grey);
			}
			catch (Exception e) {
				
			}
			g2.setColor(Color.black);
			g2.setFont(new Font("SANS SERIF", Font.BOLD, 30));
			g2.drawString("GAME OVER", 175, 250);
			g2.drawString("Your Score: ", 160, 280);
			g2.drawString("" + player.getScore(), 340, 280);
			g2.drawImage(back, backButton[0], backButton[1], this);
		}
		g2.setColor(Color.black);
		g2.setFont(new Font("SANS SERIF", Font.PLAIN, 12));
		g2.drawString("Game \"art\" and programming by Mitsuru Otsuka", 235, 495);
	}
	//method for when mouse enters applet
	public void mouseEntered(MouseEvent e) {

	}
	//method for when mouse exits the applet
	public void mouseExited(MouseEvent e) {
		
	}
	//method for when mouse button is released inside the applet
	public void mouseReleased(MouseEvent e) {
		
	}
	//method for when mouse is moved within the applet, does not include keys perssed
	public void mouseDragged(MouseEvent e) {
		
	}
	//method for when mouse is moved within teh applet, while keys are pressed down
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (isMenu) {
			//0 - x
			//1 - y
			//2 - width
			//3 - height
			
			//start button
			if ((x > startButton[0] && x < startButton[0] + startButton[2]) && 
					(y > startButton[1] && y < startButton[1] + startButton[3])) {
				grey = new Rectangle(startButton[0] - highlightDistance, startButton[1] - highlightDistance, 
						startButton[2] + highlightDistance * 2, startButton[3] + highlightDistance * 2);
			}
			//instructions button
			else if ((x > instructionsButton[0] && x < instructionsButton[0] + instructionsButton[2]) && 
					(y > instructionsButton[1] && y < instructionsButton[1] + instructionsButton[3])) {
				grey = new Rectangle(instructionsButton[0] - highlightDistance, instructionsButton[1] - highlightDistance, 
						instructionsButton[2] + highlightDistance * 2, instructionsButton[3] + highlightDistance * 2);
			}
			//difficulty button
			else if ((x > difficultyButton[0] && x < difficultyButton[0] + difficultyButton[2]) && 
					(y > difficultyButton[1] && y < difficultyButton[1] + difficultyButton[3])) {
				grey = new Rectangle(difficultyButton[0] - highlightDistance, difficultyButton[1] - highlightDistance, 
						difficultyButton[2] + highlightDistance * 2, difficultyButton[3] + highlightDistance * 2);
			}
			else {
				grey = null;
			}
			repaint();
		}
		if (isInstructions) {
			if ((x > backButton[0] && x < backButton[0] + backButton[2]) && 
					(y > backButton[1] && y < backButton[1] + backButton[3])) {
				grey = new Rectangle(backButton[0] - highlightDistance, backButton[1] - highlightDistance, 
						backButton[2] + highlightDistance * 2, backButton[3] + highlightDistance * 2);
			}
			else {
				grey = null;
			}
			repaint();
		}
		if (isDifficulty) {
			if ((x > backButton[0] && x < backButton[0] + backButton[2]) && 
					(y > backButton[1] && y < backButton[1] + backButton[3])) {
				grey = new Rectangle(backButton[0] - highlightDistance, backButton[1] - highlightDistance, 
						backButton[2] + highlightDistance * 2, backButton[3] + highlightDistance * 2);
			}
			else if ((x > noobButton[0] && x < noobButton[0] + noobButton[2]) && 
					(y > noobButton[1] && y < noobButton[1] + noobButton[3])) {
				grey = new Rectangle(noobButton[0] - highlightDistance, noobButton[1] - highlightDistance, 
						noobButton[2] + highlightDistance * 2, noobButton[3] + highlightDistance * 2);
			}
			else if ((x > mediumButton[0] && x < mediumButton[0] + mediumButton[2]) && 
					(y > mediumButton[1] && y < mediumButton[1] + mediumButton[3])) {
				grey = new Rectangle(mediumButton[0] - highlightDistance, mediumButton[1] - highlightDistance, 
						mediumButton[2] + highlightDistance * 2, mediumButton[3] + highlightDistance * 2);
			}
			else if ((x > hardButton[0] && x < hardButton[0] + hardButton[2]) && 
					(y > hardButton[1] && y < hardButton[1] + hardButton[3])) {
				grey = new Rectangle(hardButton[0] - highlightDistance, hardButton[1] - highlightDistance, 
						hardButton[2] + highlightDistance * 2, hardButton[3] + highlightDistance * 2);
			}
			else if ((x > lunaticButton[0] && x < lunaticButton[0] + lunaticButton[2]) && 
					(y > lunaticButton[1] && y < lunaticButton[1] + lunaticButton[3])) {
				grey = new Rectangle(lunaticButton[0] - highlightDistance, lunaticButton[1] - highlightDistance, 
						lunaticButton[2] + highlightDistance * 2, lunaticButton[3] + highlightDistance * 2);
			}
			else {
				grey = null;
			}
			repaint();
		}
		if (gameOver) {
			if ((x > backButton[0] && x < backButton[0] + backButton[2]) && 
					(y > backButton[1] && y < backButton[1] + backButton[3])) {
				grey = new Rectangle(backButton[0] - highlightDistance, backButton[1] - highlightDistance, 
						backButton[2] + highlightDistance * 2, backButton[3] + highlightDistance * 2);
			}
			else {
				grey = null;
			}
			repaint();
		}
	}
	public void keyPressed(KeyEvent e) {
		
	}
	public void keyReleased(KeyEvent e) {
		
	}
	public void keyTyped(KeyEvent e) {
		if (isGame) {
			if (e.getKeyChar() == KeyEvent.VK_ESCAPE) {
				gameOver = true;
				isGame = false;
			}
		}
	}
}
