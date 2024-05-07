/*
 * MIT License
 * Copyright (c) 2024 Grigorii Zherlitsyn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

package ru.game.reversi;

import  java.awt.*;
import  java.awt.event.*;
import  javax.swing.*;
import  javax.swing.border.*;
import  javax.swing.event.*;
import  ru.game.reversi.Board;
import  ru.game.reversi.Stone;
import  ru.game.reversi.ComputerPlayer;

public class Game implements ChangeListener, ActionListener, MouseListener,
                             MouseMotionListener, KeyListener {

    private JFrame window;
    private JPanel panelMenu;
    private JPanel panelMenuRestart;
    private JLabel labelInfo;
    private JSlider sliderBoardSize;
    private ButtonGroup radioButtonGroup;
    private JRadioButton radioButtonBlack;
    private JRadioButton radioButtonWhite;
    private JButton buttonRestart;
    private Board board;

    private ComputerPlayer computerPlayer;
    private Timer computerTurnTimer;

    private Stone.Color playerColor;
    private Stone.Color turn;

    public Game() {
        window = new JFrame();
        window.setSize(637, 700);
        window.setMinimumSize(new Dimension(637, 700));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setLayout(new BorderLayout());
        window.setTitle("Reversi");
        window.setFocusable(true);
        window.setFocusTraversalKeysEnabled(false);
        window.addKeyListener(this);

        labelInfo = new JLabel("Turn: Player", JLabel.CENTER);
        labelInfo.setPreferredSize(new Dimension(128, 16));

        BoundedRangeModel boundedRange = new DefaultBoundedRangeModel(6, 2, 6, 32);

        sliderBoardSize = new JSlider(boundedRange);
        sliderBoardSize.setBorder(new EmptyBorder(0, 32, 0, 32));
        sliderBoardSize.setSize(512, 32);
        sliderBoardSize.setMinorTickSpacing(2);
        sliderBoardSize.setMajorTickSpacing(2);
        sliderBoardSize.setPaintTicks(true);
        sliderBoardSize.setPaintLabels(true);
        sliderBoardSize.setSnapToTicks(true);
        sliderBoardSize.setFocusable(false);
        sliderBoardSize.addChangeListener(this);

        radioButtonWhite = new JRadioButton("White", true);
        radioButtonWhite.setFocusable(false);
        radioButtonWhite.addActionListener(this);

        radioButtonBlack = new JRadioButton("Black");
        radioButtonBlack.setFocusable(false);
        radioButtonBlack.addActionListener(this);

        radioButtonGroup = new ButtonGroup();
        radioButtonGroup.add(radioButtonWhite);
        radioButtonGroup.add(radioButtonBlack);

        buttonRestart = new JButton("Restart");
        buttonRestart.setFocusable(false);
        buttonRestart.addActionListener(this);

        panelMenuRestart = new JPanel();
        panelMenuRestart.setLayout(new BorderLayout());
        panelMenuRestart.add(radioButtonWhite, BorderLayout.WEST);
        panelMenuRestart.add(radioButtonBlack, BorderLayout.EAST);
        panelMenuRestart.add(buttonRestart, BorderLayout.SOUTH);       

        panelMenu = new JPanel();
        panelMenu.setBorder(new EmptyBorder(16, 16, 16, 16));
        panelMenu.setLayout(new BorderLayout());
        panelMenu.add(labelInfo, BorderLayout.WEST);
        panelMenu.add(sliderBoardSize, BorderLayout.CENTER);
        panelMenu.add(panelMenuRestart, BorderLayout.EAST);

        window.add(panelMenu, BorderLayout.NORTH);

        board = new Board(Stone.Color.WHITE, 6);
        board.addMouseListener(this);
        board.addMouseMotionListener(this);

        window.add(board, BorderLayout.CENTER);

        computerPlayer = new ComputerPlayer(Stone.Color.BLACK);
        computerTurnTimer = new Timer(500, this);
        computerTurnTimer.setRepeats(false);

        playerColor = Stone.Color.WHITE;
        turn = playerColor;

        window.setVisible(true);
    }

    private void restart() {
        window.remove(board);
        window.repaint();

        board = new Board(playerColor, sliderBoardSize.getValue());
        board.addMouseListener(this);
        board.addMouseMotionListener(this);
        window.add(board, BorderLayout.CENTER);

        labelInfo.setText("Turn: Player");
        turn = Stone.Color.WHITE;

        if (playerColor == Stone.Color.BLACK) {
            computerPlayer = new ComputerPlayer(Stone.Color.WHITE);
            computerTurnTimer.start();
        } else
            computerPlayer = new ComputerPlayer(Stone.Color.BLACK);

        window.repaint();
        window.setVisible(true);
    }

    private boolean checkGameEnd() {
        int blackPlaces = board.calculateAvailablePlaceCount(Stone.Color.BLACK);
        int whitePlaces = board.calculateAvailablePlaceCount(Stone.Color.BLACK);

        if (blackPlaces < 1 || whitePlaces < 1) {
            if (board.calculateWinningColor() == computerPlayer.getColor())
                labelInfo.setText("Computer win!");
            else
                labelInfo.setText("Player win!");

            return true;
        } else
            return false;
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
        int value = sliderBoardSize.getValue();
        if (value == board.getSizeInPlaces() || value % 2 != 0)
            return;

        this.restart();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == computerTurnTimer) {
            Stone.Color computerColor = computerPlayer.getColor();

            if (turn != computerColor)
                return;


            int place[] = computerPlayer.calculateBestPlace(board);

            boolean isStoneOnPlace = board.isStoneOn(place[0], place[1]); 
            boolean isPlace = board.isPlaceOn(place[0], place[1], computerColor);

            if (isPlace && !isStoneOnPlace) {
                board.setStone(place[0], place[1], computerPlayer.getColor());
                board.repaint();
            } else
                System.out.println("Invailed place");

            if (!checkGameEnd()) {
                labelInfo.setText("Turn: Player");
                turn = playerColor;
            }
        } else {
            if (e.getSource() == radioButtonBlack)
                playerColor = Stone.Color.BLACK;
            else if (e.getSource() == radioButtonWhite)
                playerColor = Stone.Color.WHITE;

            this.restart();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        Dimension dimension = board.getSize();
        int size = board.getSizeInPlaces();

        int width  = dimension.width  / size;
        int height = dimension.height / size;
        
        int j = e.getX() / width;
        int i = e.getY() / height;

        if (i >= size || j >= size)
            return;

        if (board.isPlaceOn(i, j, playerColor))
            board.setSelectedPlace(i, j);
        else
            board.setSelectedPlace(-1, -1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (turn != playerColor)
            return;

        Dimension dimension = board.getSize();
        int size = board.getSizeInPlaces();

        int width  = dimension.width  / size;
        int height = dimension.height / size;
        
        int j = e.getX() / width;
        int i = e.getY() / height;

        if (i >= size || j >= size)
            return;

        if (!board.isStoneOn(i, j) && board.isPlaceOn(i, j, playerColor)) {
            board.setStone(i, j, playerColor);
            board.repaint();

            labelInfo.setText("Turn: Computer");
            turn = computerPlayer.getColor();

            computerTurnTimer.start();
        }

        checkGameEnd();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);
        else if (e.getKeyCode() == KeyEvent.VK_R)
            this.restart();
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

}
