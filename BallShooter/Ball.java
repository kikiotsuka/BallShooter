/*
 * Ball Shooter Game by Mitsuru Otsuka
 * Version 0.2.1
 */

package BallShooter;

public class Ball {
	//instantiate private ball variables
	//contains x coordinate
	private int x;
	//contains y coordinate
	private int y;
	//contains radius of ball
	private int radius;
	//contains xvector of ball
	private int xvector;
	//contains yvector of ball
	private int yvector;
	//contains the minimum speed of ball
	private int lowerSpeed;
	//contains the maximum speed of ball
	private int upperSpeed;
	//if true, moves outwards, if false, moves inwards
	private boolean isMoving;
	//difficulty setting
	
	//Difficulty settings
	//0 = noob - radius doesnt decrease
	//1 = medium - normal
	//2 = hard - extra ball, firing speeds are a bit faster
	//3 = lunatic - 2 extra balls, fixed fire speed (5), radius starts at 30
	private int difficultySetting;
	//constructor
	public Ball(int initDifficultySetting) {
		difficultySetting = initDifficultySetting;	
		x = resetX();
		y = resetY();
		radius = 50;
		lowerSpeed = 1;
		upperSpeed = 2;
		isMoving = true;
		if (difficultySetting == 2) {
			lowerSpeed = 2;
			upperSpeed = 4;
		}
		else if (difficultySetting == 3) {
			radius = 30;
			lowerSpeed = 5;
			upperSpeed = 8;
		}
		xvector = resetXVector();
		yvector = resetYVector();
	}
	//moves the ball by a given speed
	public void move() {
		if (isMoving) {
			x += xvector;
			y += yvector;
		}
		else {
			x -= xvector * 15;
			y -= yvector * 15;
			if (x > 250 - 50 && x < 250 + 50 && y > 250 - 50 && y < 250 + 50) {
				reset();
			}
		}
	}
	//after ball has been hit, resets its coordinates and randomizes new vectors
	public void reset() {
		x = resetX();
		y = resetY();
		xvector = resetXVector();
		yvector = resetYVector();
		isMoving = true;
	}
	public void lightReset() {
		isMoving = false;
	}
	//Checks if out of bounds
	//if out of bounds, returns true
	//if not out of bounds, returns false
	public boolean outOfBounds() {
		if (x < -20 || x > 500 || y < -20 || y > 500) {
			return true;
		}
		return false;
	}
	//checks if player hit the ball
	public boolean userHit(int playerX, int playerY) {
		if ((playerX > x - radius && playerX < x + radius) && (playerY > y - radius && playerY < y + radius)) {
			lightReset();
			if (difficultySetting == 0) {
				return true;
			}
			//make the difficultry harder
			//decrease size of ball
			if (radius > 10) {
				radius--;
			}
			//increase speed of ball
			if (radius == 40) {
				upperSpeed = 3;
			}
			//increase speed of ball
			if (radius == 35) {
				lowerSpeed = 2;
				upperSpeed = 4;
			}
			//increase speed of ball
			if (radius == 25) {
				lowerSpeed = 3;
				upperSpeed = 5;
			}
			return true;
		}
		return false;
	}
	//resets the X coordinate to be +- 250
	public int resetX() {
		int sign = (int) (Math.random() * 2);
		int tempX = (int) (1 + Math.random() * 20);
		if (sign == 0) {
			return (250 - tempX);
		}
		return (250 + tempX);
	}
	//resets the Y coordinate to be +- 250
	public int resetY() {
		int sign = (int) (Math.random() * 2);
		int tempY = (int) (1 + Math.random() * 20);
		if (sign == 0) {
			return (250 - tempY);
		}
		return (250 + tempY);
	}
	//resets x vector
	public int resetXVector() {
		int sign = (int) (Math.random() * 2);
		int xVector = (int) (lowerSpeed + Math.random() * upperSpeed);
		if (sign == 0) {
			return -xVector;
		}
		return xVector;
	}
	//resets y vector
	public int resetYVector() {
		int sign = (int) (Math.random() * 2);
		int yVector = (int) (lowerSpeed + Math.random() * upperSpeed);
		if (sign == 0) {
			return -yVector;
		}
		return yVector;
	}
	public void increaseLowerSpeed(int increase) {
		lowerSpeed += increase;
	}
	public void increaseUpperSpeed(int increase) {
		upperSpeed += increase;
	}
	//return x coordinate
	public int getX() {
		return x;
	}
	//return y coordinate
	public int getY() {
		return y;
	}
	//return radius of ball
	public int getRadius() {
		return radius;
	}
	public boolean isMoving() {
		return isMoving;
	}
}
