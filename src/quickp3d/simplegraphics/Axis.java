/**
 * Copyright (C) 2012 Anderson de Oliveira Antunes <anderson.utf@gmail.com>
 *
 * This file is part of QuickP3D.
 *
 * QuickP3D is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * QuickP3D is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * QuickP3D. If not, see http://www.gnu.org/licenses/.
 */

package quickp3d.simplegraphics;

import processing.core.PApplet;
import processing.opengl.PGraphics3D;
import quickp3d.DrawingPanel3D;
import quickp3d.graph.Graph3D;

/**
 * Exemplo de um objeto gráfico 3D.
 * 
 * Esta classe desenha um objeto tridmensional composto por três setas, uma para 
 * cada eixo, em um ponto pivô predefinido e coloridas em RGB, respectivamente.
 * 
 * @author Anderson Antunes
 */

public class Axis implements Graph3D { // Implementa um objeto gráfico 3D

    private final float x;
    private final float y;
    private final float z;

    public Axis() {
        x = y = z = 0;
    }
    
    public Axis(double x, double y, double z) {
        this.x = (float)x;
        this.y = (float)y;
        this.z = (float)z;
    }

    @Override
    public void draw(PGraphics3D g3d) { // Sobrescreve a função draw()
        g3d.pushMatrix();
        g3d.scale(DrawingPanel3D.RESET_SCALE);
        g3d.translate(x, y, z);
        drawAxis(100, g3d);
        g3d.popMatrix();
    }
    
    public static void drawAxis(int size, PGraphics3D g3d) {
        //eixo
        g3d.stroke(255, 0, 0);//R
        g3d.line(0, 0, 0, size, 0, 0);//X
        g3d.stroke(0, 255, 0);//G
        g3d.line(0, 0, 0, 0, -size, 0);//Y
        g3d.stroke(0, 0, 255);//B
        g3d.line(0, 0, 0, 0, 0, size);//Z

        g3d.pushMatrix();
        g3d.translate(size, 0, 0);
        g3d.rotateY(PApplet.HALF_PI);//X
        g3d.stroke(255, 0, 0);
        g3d.fill(255, 0, 0);//R
        piramid(5, g3d);
        g3d.popMatrix();

        g3d.pushMatrix();
        g3d.translate(0, -size, 0);
        g3d.rotateX(-PApplet.HALF_PI);//Y
        g3d.stroke(0, 255, 0);//G
        g3d.fill(0, 255, 0);
        piramid(5, g3d);
        g3d.popMatrix();

        g3d.pushMatrix();
        g3d.translate(0, 0, size);
        //Z
        g3d.stroke(0, 0, 255);//B
        g3d.fill(0, 0, 255);
        piramid(5, g3d);
        g3d.popMatrix();
    }

    public static void piramid(int scale, PGraphics3D g3d) {
        g3d.pushMatrix();
        g3d.scale(scale);
        g3d.beginShape(PApplet.TRIANGLES);

        g3d.vertex(0, 0, 1);
        g3d.vertex(1, 1, -1);
        g3d.vertex(1, -1, -1);

        g3d.vertex(0, 0, 1);
        g3d.vertex(1, -1, -1);
        g3d.vertex(-1, -1, -1);

        g3d.vertex(0, 0, 1);
        g3d.vertex(-1, -1, -1);
        g3d.vertex(-1, 1, -1);

        g3d.vertex(0, 0, 1);
        g3d.vertex(-1, 1, -1);
        g3d.vertex(1, 1, -1);

        g3d.endShape(PApplet.CLOSE);
        g3d.popMatrix();
    }
    
}
