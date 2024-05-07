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

public class Stone {

    public enum Color {
        BLACK, WHITE
    }

    private Stone.Color color;

    private long animationTimeTotal;
    private long animationTime;
    private boolean animation;
    private long timer;

    public Stone(Stone.Color color, long animationTimeTotal) {
        this.color = color;
        this.animationTimeTotal = animationTimeTotal;

        animation = false;
        animationTime = animationTimeTotal;
    }

    public Stone(Stone.Color color) {
        this(color, (long)3e8);
    }

    public void setColor(Stone.Color color) {
        this.color = color;

        animation = true;
        animationTime = 0;
        timer = System.nanoTime();
    }

    public Stone.Color getColor() {
        return color;
    }

    public boolean isAnimation() {
        return animation;
    }

    public void draw(Graphics2D g, int x, int y, int width, int height) {

        if (animationTime < animationTimeTotal)
            animation = true;
        else
            animation = false;

        float frame = 1.0f;
        if (animation) {
            animationTime += System.nanoTime() - timer;
            frame = (float)animationTime / (float)animationTimeTotal;

            if (frame > 1.0f)
                frame = 1.0f;
        }

        int intensity = 0xFF;
        if (color == Stone.Color.BLACK)
            intensity *= 1 - frame;
        else
            intensity *= frame;

        if (intensity > 0xFF)
            intensity = 0xFF;

        if (intensity < 0x0)
            intensity = 0x0;

        java.awt.Color colorStart = new java.awt.Color(0x606060);
        java.awt.Color colorEnd   = new java.awt.Color(intensity, intensity, intensity);

        GradientPaint gradient = new GradientPaint(x + width / 2, y, colorStart, 
                                                   x + width / 2, y + height,
                                                   colorEnd, true);

        int minSize = width < height ? width : height;
        int stoneWidth  = (int)(minSize * 0.75);
        int stoneHeight = (int)(minSize * 0.75);

        if (frame > 0.5)
            stoneWidth *= (frame - 0.5) * 2;
        else
            stoneWidth *= (0.5 - frame) * 2;

        int stoneX = x + (width - stoneWidth) / 2;
        int stoneY = y + (height - stoneHeight) / 2;

        g.setPaint(gradient);
        g.fillOval(stoneX, stoneY, stoneWidth, stoneHeight);

        g.setColor(java.awt.Color.BLACK);
        g.drawOval(stoneX, stoneY, stoneWidth, stoneHeight);

        if (animation)
            timer = System.nanoTime();
    }

}
