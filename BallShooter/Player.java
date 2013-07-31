/*
 * Ball Shooter Game by Mitsuru Otsuka
 * Version 0.2.1
 */

package BallShooter;

public class Player {
	private int lives;
	private int score;
	private int missClick;
	//constructor
	public Player() {
		score = 0;
		lives = 3;
		missClick = 0;
	}
	//increases player score if player clicked on ball
	public void increaseScore() {
		score++;
	}
	//decreases player lives if ball exited screen
	public void decreaseLives() {
		lives--;
	}
	//increases missclick counter
	public void increaseMiss() {
		missClick++;
	}
	//resets missclicks
	public void resetMiss() {
		missClick = 0;
	}
	//returns number of times player missclicked
	public int getMissClick() {
		return missClick;
	}
	//penalizes player by 5 points
	public void penalizeScore() {
		score -= 5;
		if (score < 0) {
			score = 0;
		}
	}
	//returns player's score
	public int getScore() {
		return score;
	}
	//returns player's lives
	public int getLives() {
		return lives;
	}
}
