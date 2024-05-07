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
import  javax.swing.*;
import  ru.game.reversi.Stone;

public class Board extends JPanel {

    private int selectedPlace[];
    private boolean place[][][];
    private Stone stone[][];
    private Color color[];

    private Stone.Color playerColor;
    private int size;

    public Board(Stone.Color playerColor, int size) {
        this.playerColor = playerColor;
        this.size = size;

        stone = new Stone[size][size];
        place = new boolean[size][size][2];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                stone[i][j] = null;
                place[i][j][0] = false;
                place[i][j][1] = false;
            }
        }

        int center = size / 2;
        stone[center - 0][center - 1] = new Stone(Stone.Color.BLACK);
        stone[center - 0][center - 0] = new Stone(Stone.Color.WHITE);
        stone[center - 1][center - 1] = new Stone(Stone.Color.WHITE);
        stone[center - 1][center - 0] = new Stone(Stone.Color.BLACK);
        this.calculateAvailablePlace();

        selectedPlace = new int[2];
        selectedPlace[0] = -1;
        selectedPlace[1] = -1;

        color = new Color[2];
        color[0] = new Color(0x008a00);
        color[1] = new Color(0x008200);
    }

    private void paint(Graphics2D g) {
        Dimension dimension = this.getSize();
        int width  = dimension.width  / size;
        int height = dimension.height / size;

        boolean repaint = false;
        boolean colorID = true;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                g.setColor(color[colorID ? 0 : 1]);
                g.fillRect(width * j, height * i, width, height);

                g.setColor(Color.BLACK);
                g.drawRect(width * j, height * i, width, height);

                if (place[i][j][playerColor == Stone.Color.WHITE ? 1 : 0]) {
                    int minSize = width < height ? width : height;
                    int placeWidth  = (int)(minSize * 0.75);
                    int placeHeight = (int)(minSize * 0.75);

                    int placeX = (width  * j) + (width  - placeWidth)  / 2;
                    int placeY = (height * i) + (height - placeHeight) / 2;

                    if (i == selectedPlace[0] && j == selectedPlace[1])
                        g.setColor(Color.YELLOW);
                    else
                        g.setColor(Color.BLACK);

                    g.drawOval(placeX, placeY, placeWidth, placeHeight);
                }

                if (this.isStoneOn(i, j)) {
                    Stone stone = this.getStone(i, j);
                    stone.draw(g, width * j, height * i, width, height);

                    if (stone.isAnimation())
                        repaint = true;
                }

                colorID = !colorID;
            }
            colorID = !colorID;
        }

        if (repaint)
            this.repaint();
    }

    private void calculateAvailablePlace() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < 2; k++) {
                    Stone.Color color = Stone.Color.BLACK;

                    if (k > 0)
                        color = Stone.Color.WHITE;

                    int gain = calculateTransformationGain(i, j, color);
                    place[i][j][k] = !this.isStoneOn(i, j) && gain > 0;
                }
            }
        }
    }

    public int calculateAvailablePlaceCount(Stone.Color color) {
        int count = 0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (place[i][j][color == Stone.Color.WHITE ? 1 : 0])
                    count++;
            }
        }

        return count;
    }

    public boolean[][] calculateTransformation(int i, int j, Stone.Color color) {
        boolean transform[][] = new boolean[size][size];

        int stoneID = -1;
        for (int k = j - 1; k >= 0; k--) {
            if (this.isStoneOn(i, k)) {
                if (this.getStone(i, k).getColor() == color) {
                    stoneID = k;
                    break;
                }
            } else
                break;
        }

        if (stoneID > -1) {
            for (int k = j - 1; k > stoneID; k--)
                transform[i][k] = true;
        }
       
        stoneID = -1;
        for (int k = j + 1; k < size; k++) {
            if (this.isStoneOn(i, k)) {
                if (this.getStone(i, k).getColor() == color) {
                    stoneID = k;
                    break;
                }
            } else
                break;
        }

        if (stoneID > -1) {
            for (int k = j + 1; k < stoneID; k++)
                transform[i][k] = true;
        }

        stoneID = -1;
        for (int k = i - 1; k >= 0; k--) {
            if (this.isStoneOn(k, j)) {
                if (this.getStone(k, j).getColor() == color) {
                    stoneID = k;
                    break;
                }
            } else
                break;
        }

        if (stoneID > -1) {
            for (int k = i - 1; k > stoneID; k--)
                transform[k][j] = true;
        }

        stoneID = -1;
        for (int k = i + 1; k < size; k++) {
            if (this.isStoneOn(k, j)) {
                if (this.getStone(k, j).getColor() == color) {
                    stoneID = k;
                    break;
                }
            } else
                break;
        }

        if (stoneID > -1) {
            for (int k = i + 1; k < stoneID; k++)
                transform[k][j] = true;
        }

        stoneID = -1;
        for (int k = i - 1, l = j - 1; k >= 0 && l >= 0; k--, l--) {
            if (this.isStoneOn(k, l)) {
                if (this.getStone(k, l).getColor() == color) {
                    stoneID = k;
                    break;
                }
            } else
                break;
        }

        if (stoneID > -1) {
            for (int k = i - 1, l = j - 1; k > stoneID; k--, l--)
                transform[k][l] = true;
        }

        stoneID = -1;
        for (int k = i + 1, l = j + 1; k < size && l < size; k++, l++) {
            if (this.isStoneOn(k, l)) {
                if (this.getStone(k, l).getColor() == color) {
                    stoneID = k;
                    break;
                }
            } else
                break;
        }

        if (stoneID > -1) {
            for (int k = i + 1, l = j + 1; k < stoneID; k++, l++)
                transform[k][l] = true;
        }

        stoneID = -1;
        for (int k = i - 1, l = j + 1; k >= 0 && l < size; k--, l++) {
            if (this.isStoneOn(k, l)) {
                if (this.getStone(k, l).getColor() == color) {
                    stoneID = k;
                    break;
                }
            } else
                break;
        }

        if (stoneID > -1) {
            for (int k = i - 1, l = j + 1; k > stoneID; k--, l++)
                transform[k][l] = true;
        }

        stoneID = -1;
        for (int k = i + 1, l = j - 1; k < size && l >= 0; k++, l--) {
            if (this.isStoneOn(k, l)) {
                if (this.getStone(k, l).getColor() == color) {
                    stoneID = k;
                    break;
                }
            } else
                break;
        }

        if (stoneID > -1) {
            for (int k = i + 1, l = j - 1; k < stoneID; k++, l--)
                transform[k][l] = true;
        }

        return transform;
    }

    public int calculateTransformationGain(int i, int j, Stone.Color color) {
        boolean transformation[][] = calculateTransformation(i, j, color);
        int gain = 0;

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if (transformation[i][j])
                    gain++;
            }
        }

        return gain;
    }

    public Stone.Color calculateWinningColor() {
        int colorCount[] = new int[2];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (this.isStoneOn(i, j)) {
                    Stone.Color color = this.getStone(i, j).getColor();
                    colorCount[color == Stone.Color.WHITE ? 1 : 0]++;
                }
            }
        }

        Stone.Color color = Stone.Color.BLACK;

        if (colorCount[1] > colorCount[0])
            color = Stone.Color.WHITE;

        return color;
    }

    public boolean isPlaceOn(int i, int j, Stone.Color color) {
        if (i < 0 || i < 0 || i >= size || j >= size)
            return false;

        return place[i][j][color == Stone.Color.WHITE ? 1 : 0];
    }

    public void setSelectedPlace(int i, int j) {
        if (i == selectedPlace[0] && j == selectedPlace[1])
            return;

        selectedPlace[0] = i;
        selectedPlace[1] = j;
        this.repaint();
    }

    public boolean isStoneOn(int i, int j) {
        if (i < 0 || j < 0 || i >= size || j >= size)
            return false;

        return stone[i][j] != null;
    }

    public void setStone(int i, int j, Stone.Color color) {
        stone[i][j] = new Stone(color);

        boolean transformation[][] = this.calculateTransformation(i, j, color);

        for (i = 0; i < size; i++) {
            for (j = 0; j < size; j++) {
                if (transformation[i][j])
                    stone[i][j].setColor(color);
            }
        }
        
        this.calculateAvailablePlace();
    }

    public Stone getStone(int i, int j) {
        return stone[i][j];
    }

    public int getSizeInPlaces() {
        return size;
    }

    @Override
    public void paintComponent(Graphics g) {
        this.paint((Graphics2D)g);
    }

}
