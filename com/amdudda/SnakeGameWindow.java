package com.amdudda;

import javax.swing.*;

/**
 * Created by amdudda on 11/12/2015.
 */
public class SnakeGameWindow extends JFrame {
    // this is the window that is inside the entire game
    private JFrame snakeWindow;
    private DrawSnakeGamePanel sPanel;

    // constructor
    public SnakeGameWindow(Snake snake, Kibble kibble, Score game_score) {
        this.snakeWindow = new JFrame();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(SnakeGame.getxPixelMaxDimension(), SnakeGame.getyPixelMaxDimension());
        this.setUndecorated(false); // AMD: Show title bar so game can be moved around screen //hide title bar
        this.setTitle("Snake Game: feed the snake and avoid the walls!");
        this.setVisible(true);
        this.setResizable(false);

        //FINDBUGS: use setter to change snake Panel
        this.sPanel = new DrawSnakeGamePanel(snake, kibble, game_score);
        this.sPanel.setFocusable(true);
        this.sPanel.requestFocusInWindow(); //required to give this component the focus so it can generate KeyEvents

        this.add(this.sPanel);
        this.sPanel.addKeyListener(new GameControls(snake));

        SnakeGame.setSnakePanel(this.sPanel);
        SnakeGame.setGameStage(SnakeGame.BEFORE_GAME);

        this.setVisible(true);
        this.resize(); // need to make sure grid squares are all fully visible.
    }

    protected void resize() {
        /*
        AMD: trying to get draggable window that doesn't hide bottom row(s) of board
        https://home.java.net/node/650887
        http://stackoverflow.com/questions/12803963/how-can-i-get-the-height-of-the-title-bar-of-a-jinternalframe
        harrumph, that's annoying, the window sizing isn't measured to hold contents of a given size, so you have to
        explicitly tell it to be big enough for its contents by telling it what size its borders are:
        http://www.coderanch.com/t/333985/GUI/java/getInsets-Frames
        */
        int titlebarheight = SnakeGameWindow.this.getInsets().top + SnakeGameWindow.this.getInsets().bottom;
        int borders = SnakeGameWindow.this.getInsets().left + SnakeGameWindow.this.getInsets().right;

        SnakeGameWindow.this.setSize(SnakeGame.getxPixelMaxDimension() + borders, SnakeGame.getyPixelMaxDimension() + titlebarheight);
    }
}
