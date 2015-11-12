package com.amdudda;

/*
    @author clara -- code initially downloaded from
	@student A.M. Dudda  -- revised/added coded commented with AMD: prefix
* */

import java.util.Timer;
import javax.swing.*;


public class SnakeGame {

    // AMD: what if we make timer global?
    public static Timer timer = new Timer();
    // AMD: we want to set a final constant as a base dimension so we can multiply, then add 1 pixel, for board sizing.
    public static final int INITIAL_GAME_SIZE = 500;

	// made Not Final so user can adjust this.
	public static int xPixelMaxDimension = INITIAL_GAME_SIZE + 1;  //Pixels in window. 501 to have 50-pixel squares plus 1 to draw a border on last square
	public static int yPixelMaxDimension = INITIAL_GAME_SIZE + 1;

	public static int xSquares ;
	public static int ySquares ;
	public static int squareSize = 50;
    // AMD: Some additional variables that are set at the start of the game
    public static boolean hasWarpWalls = false; // AMD: variable to help implement warp walls.
    public static boolean hasMazeWalls = false;  // AMD: variable to help implement maze walls
    public static final int NUM_MAZE_WALLS = 3; // Number of walls to build if maze walls enabled.
	public static boolean enableExtendedFeatures = false; // turns on/off additional mazewalls and axe
    public static final int ADD_WALL_INTERVAL = 3; // Number of kibbles to eat between new maze walls
	public static final int SHOW_AXE_INTERVAL = 5; // axe shows up every fifth kibble

	protected static Snake snake ;

	protected static Kibble kibble;
	// AMD: added Axe
	protected static Axe game_axe;

	protected static Score game_score;  // AMD: refactored this because I want to be clear that I'm referring to game score and not object's score variable

	static final int BEFORE_GAME = 1;
	static final int DURING_GAME = 2;
	static final int GAME_OVER = 3;
	static final int GAME_WON = 4;   //The values are not important. The important thing is to use the constants 
	//instead of the values so you are clear what you are setting. Easy to forget what number is Game over vs. game won
	//Using constant names instead makes it easier to keep it straight. Refer to these variables 
	//using statements such as SnakeGame.GAME_OVER
	// AMD: added SET_OPTIONS to keep game from starting while options are being set
	static final int SET_OPTIONS = 5;

	private static int gameStage = BEFORE_GAME;  //use this to figure out what should be happening. 
	//Other classes like Snake and DrawSnakeGamePanel will need to query this, and change it's value

	protected static long clockInterval = 500; //controls game speed
	//Every time the clock ticks, the snake moves
	//This is the time between clock ticks, in milliseconds
	//1000 milliseconds = 1  second.

	static JFrame snakeFrame;
	static DrawSnakeGamePanel snakePanel;
	//Framework for this class adapted from the Java Swing Tutorial, FrameDemo and Custom Painting Demo. You should find them useful too.
	//http://docs.oracle.com/javase/tutorial/displayCode.html?code=http://docs.oracle.com/javase/tutorial/uiswing/examples/components/FrameDemoProject/src/components/FrameDemo.java
	//http://docs.oracle.com/javase/tutorial/uiswing/painting/step2.html

	protected static void createAndShowGUI() {
		//Create and set up the window.
		snakeFrame = new JFrame();
		snakeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        snakeFrame.setSize(xPixelMaxDimension, yPixelMaxDimension);
		snakeFrame.setUndecorated(false); // AMD: Show title bar so game can be moved around screen //hide title bar
        snakeFrame.setTitle("Snake Game: feed the snake and avoid the walls!");
		snakeFrame.setVisible(true);

		snakeFrame.setResizable(false);

		snakePanel = new DrawSnakeGamePanel(snake, kibble, game_score);
		snakePanel.setFocusable(true);
		snakePanel.requestFocusInWindow(); //required to give this component the focus so it can generate KeyEvents

		snakeFrame.add(snakePanel);
		snakePanel.addKeyListener(new GameControls(snake));

		setGameStage(BEFORE_GAME);

		snakeFrame.setVisible(true);
	}

	protected static void initializeGame() {
		//set up game_score, snake and first kibble
		xSquares = xPixelMaxDimension / squareSize;
		ySquares = yPixelMaxDimension / squareSize;

		snake = new Snake(xSquares, ySquares, squareSize);
		kibble = new Kibble(snake);
		game_axe = new Axe(snake);
		game_score = new Score();

        // AMD: debugging: System.out.println("xSquares = " + xSquares);
        gameStage = BEFORE_GAME;
	}

	protected static void newGame() {
		// AMD: restart the timer when we kick off a new game.  Also updated Gameclock to deal with Axe.
        timer = new Timer();
		GameClock clockTick = new GameClock(snake, kibble, game_score, snakePanel, game_axe);
		timer.scheduleAtFixedRate(clockTick, 0 , clockInterval);
        /*AMD: this causes the game to refresh every clockInterval milliseconds so the snake
        * can be redrawn as game play progresses.
        * */
        // also seed a new set of mazeWalls
        DrawSnakeGamePanel.gameWalls.clear();
		for (int i=0; i< NUM_MAZE_WALLS; i++) {
			DrawSnakeGamePanel.gameWalls.add(new MazeWall());
		}
	}

	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
		//creating and showing this application's GUI.
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				initializeGame();
				createAndShowGUI();
			}
		});
	}



	public static int getGameStage() {
		return gameStage;
	}

	public static boolean gameEnded() {
		/*if (gameStage == GAME_OVER || gameStage == GAME_WON){
			return true;
		}
		return false;*/
        // another pointless if-then statement
        return gameStage == GAME_OVER || gameStage == GAME_WON;
	}

	public static void setGameStage(int gameStage) {
		SnakeGame.gameStage = gameStage;
	}

    public static void resizewindow() {
        /*
        AMD: trying to get draggable window that doesn't hide bottom row(s) of board
        https://home.java.net/node/650887
        http://stackoverflow.com/questions/12803963/how-can-i-get-the-height-of-the-title-bar-of-a-jinternalframe
        harrumph, that's annoying:
        http://www.coderanch.com/t/333985/GUI/java/getInsets-Frames
        */
        int titlebarheight = snakeFrame.getInsets().top + snakeFrame.getInsets().bottom;
        int borders = snakeFrame.getInsets().left + snakeFrame.getInsets().right;

        snakeFrame.setSize(xPixelMaxDimension+borders, yPixelMaxDimension+titlebarheight);
    }
}
