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

package quickp3d.tools;

import java.util.ArrayList;
import java.util.Iterator;
import processing.core.PGraphics;

/**
 * Exemplo de um objeto gráfico 3D.
 * 
 * Esta classe desenha um objeto tridmensional composto por três setas, uma para 
 * cada eixo, em um ponto pivô predefinido e coloridas em RGB, respectivamente.
 * 
 * @author Anderson Antunes
 */

public class ObjectPicker<T> implements Iterable<T> {

    public interface Selectable<T> {

        public T select(int index);
    }
    private PGraphics buffer;
    private ArrayList<T> selected;
    private Selectable<T> source;

    public ObjectPicker(PGraphics buffer, Selectable<T> source) {
        this.buffer = buffer;
        this.source = source;
        selected = new ArrayList<>();
    }

    public void setBuffer(PGraphics buffer) {
        this.buffer = buffer;
    }

    public PGraphics getBuffer() {
        return buffer;
    }

    public int getIndex(int x, int y) {
        return -buffer.get(x, y) - 2;
    }

    public int getColor(int index) {
        return -(index + 2);
    }

    public void select(int x, int y) {
        T tmp = source.select(-buffer.get(x, y) - 2);
        if (tmp != null) {
            if (selected.contains(tmp)){
                selected.remove(tmp);
            } else {
                selected.add(tmp);
            }
        }

        //DEBUG
//        for (T t : this) {
//            if (t instanceof BaseNode) {
//                BaseNode bn = (BaseNode) t;
//                System.out.println(">" + bn.getAddress() + " " + bn.getInfo());
//            }
//        }
    }

    @Override
    public Iterator<T> iterator() {
        return selected.iterator();
    }
}
