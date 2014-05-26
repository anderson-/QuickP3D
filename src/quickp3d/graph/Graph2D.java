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

package quickp3d.graph;

import processing.core.PGraphics;

/**
 * Interface para a implementação de um elemento gráfico 2D.
 * 
 * @author Anderson Antunes
 */

public interface Graph2D extends Graph{
    
    /**
     * Função que desenha o objeto em 2D.
     * 
     * @param g2d Manipulador {@link processing.core.PGraphics}
     */
    
    public void draw(PGraphics g2d);
    
}
