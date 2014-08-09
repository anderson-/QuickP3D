/**
 * Copyright (C) 2012 Anderson de Oliveira Antunes <anderson.utf@gmail.com>
 *
 * This file is part of QuickP3D.
 *
 * QuickP3D is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * QuickP3D is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * QuickP3D. If not, see http://www.gnu.org/licenses/.
 */
package quickp3d.tools;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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

    public static final String ADD = "add";
    public static final String REMOVE = "remove";

    private final PropertyChangeSupport support;
    private final ArrayList<T> selected;
    private final Selectable<T> source;
    private PGraphics buffer;

    public ObjectPicker(PGraphics buffer, Selectable<T> source) {
        this.buffer = buffer;
        this.source = source;
        selected = new ArrayList<>();
        support = new PropertyChangeSupport(this);
    }

    public synchronized void add(T t) {
        if (!selected.contains(t)) {
            selected.add(t);
            support.firePropertyChange(ADD, null, t);
        }
    }

    public synchronized void remove(T t) {
        if (selected.remove(t)) {
            support.firePropertyChange(REMOVE, null, t);
        }
    }

    public synchronized void clear() {
        for (T t : new ArrayList<T>(selected)) {
            support.firePropertyChange(REMOVE, null, t);
        }
        selected.clear();
    }

    public int getSize() {
        return selected.size();
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

    public final T pick(int x, int y) {
        return source.select(-buffer.get(x, y) - 2);
    }

    public final void select(int x, int y) {
        T tmp = pick(x, y);
        if (tmp != null) {
            synchronized (this) {
                if (selected.contains(tmp)) {
                    selected.remove(tmp);
                    support.firePropertyChange(REMOVE, null, tmp);
                } else {
                    selected.add(tmp);
                    support.firePropertyChange(ADD, null, tmp);
                }
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
    public synchronized Iterator<T> iterator() {
        return selected.iterator();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        support.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        support.removePropertyChangeListener(l);
    }

}
