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
package quickp3d;

import com.jogamp.newt.event.KeyEvent;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import processing.core.PApplet;
import static processing.core.PConstants.PI;
import processing.core.PGraphics;
import processing.opengl.PGraphics2D;
import processing.opengl.PGraphics3D;
import quickp3d.graph.Graph;
import quickp3d.graph.Graph2D;
import quickp3d.graph.Graph3D;
import quickp3d.simplegraphics.Axis;

/**
 * Exemplo de um objeto gráfico 3D.
 *
 * Esta classe desenha um objeto tridmensional composto por três setas, uma para
 * cada eixo, em um ponto pivô predefinido e coloridas em RGB, respectivamente.
 *
 * @author Anderson Antunes
 */
public class DrawingPanel3D implements Graph2D, Graph3D {

    /**
     *
     */
    public static final float DEFAULT_SCALE = 100f;
    public static final float RESET_SCALE = 1 / 100f;
    /**
     * Deslocamento
     */
    protected static final int DEFAULT_MOVE = 10;
    protected boolean panInsteadOfZoom = false;
    protected int width;
    protected int height;
    protected int posX = 0;
    protected int posY = 0;
    protected float atX = 0;
    protected float atY = 0;
    protected float atZ = 0;
    protected int atDist = 200;
    protected float upX = 0;
    protected float upY = 0;
    protected float eyeZ = 100;
    protected float theta = 250;
    protected Scene3D scene3D;
    protected int scale = 100;
    private Point tmpPoint = new Point();
    private ArrayList<Graph> graphics = new ArrayList<>();

    /*
     * Função de teste para a classe DrawingPanel3D.
     */
    public static void main(String[] args) {

        // instanciando um painel de desenho de 300px x 300px
        DrawingPanel3D drawingPanel = new DrawingPanel3D(300, 300);
        // criando e exibindo a janela principal
        drawingPanel.createFrame("Meu teste de desenho 3D");
        // incluindo um objeto 3D (no caso as setas RGB no ponto [0,0,0])
        drawingPanel.append(new Axis());

    }

    /**
     * Classe controladora das funções de desenho (2D/3D) e movimentação usando
     * o teclado e o mouse.
     *
     * <dl>
     * <dt>Mouse:</dt>
     * <dd>Mover a câmera</dd>
     * <dt>W/Up:</dt>
     * <dd>Andar para frente</dd>
     * <dt>S/Down:</dt>
     * <dd>Andar para trás</dd>
     * <dt>A/Left:</dt>
     * <dd>Andar para a esquerda</dd>
     * <dt>D/Right:</dt>
     * <dd>Andar para a direita</dd>
     * <dt>Q:</dt>
     * <dd>Zoom +</dd>
     * <dt>E:</dt>
     * <dd>Zoom -</dd>
     * <dt>Shift/PgUp/Space/MouseBtnRight:</dt>
     * <dd>Sobe</dd>
     * <dt>PqDown:</dt>
     * <dd>Desce</dd>
     *
     */
    public class Scene3D extends PApplet {

        private final ArrayList<Integer> keys = new ArrayList<>();
        private PGraphics3D pGraphics3D;
        private PGraphics2D pGraphics2D;

        protected Scene3D() {

        }

        protected Scene3D(boolean fillParentComponent) {
            if (fillParentComponent) {
                addComponentListener(new ComponentAdapter() {
                    @Override
                    public void componentResized(ComponentEvent e) {
                        DrawingPanel3D.this.width = e.getComponent().getWidth();
                        DrawingPanel3D.this.height = e.getComponent().getHeight();
                        resizeFrame(e.getComponent().getWidth(), e.getComponent().getHeight());
                    }
                });
            }
        }

        public JFrame createFrame(String name) {

            JFrame jFrame = new JFrame(name) {
                private long time = System.currentTimeMillis();

                @Override
                public void validate() {
                    super.validate();

                    //evita reconstruir os buffers com frequencia
                    if (System.currentTimeMillis() - time > 50) {
//                        System.out.println("validate");
                        scene3D.resizeFrame(this.getWidth(), this.getHeight());
                    } else {
                        time = System.currentTimeMillis();
                    }
                }
            };

            PApplet pApplet = this;
            pApplet.init();
            jFrame.getContentPane().add(pApplet);
            jFrame.pack();
            jFrame.setSize(DrawingPanel3D.this.width, DrawingPanel3D.this.height);
            jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
            mouseDragged();

            return jFrame;
        }

        public JPanel createPanel() {
            JPanel panel = new JPanel() {
                private long time = System.currentTimeMillis();

                @Override
                public void validate() {
                    super.validate();

                    //evita reconstruir os buffers com frequencia
                    if (System.currentTimeMillis() - time > 50) {
//                        System.out.println("validate");
                        scene3D.resizeFrame(this.getWidth(), this.getHeight());
                    } else {
                        time = System.currentTimeMillis();
                    }
                }
            };

            PApplet pApplet = this;
            pApplet.init();
            panel.add(pApplet);
            panel.setSize(DrawingPanel3D.this.width, DrawingPanel3D.this.height);
            mouseDragged();

            return panel;
        }

        public void resizeFrame(int width, int height) {
            if (pGraphics3D != null) {
                /*
                 * Nota: Ignorar quaisquer warnings de sincronização de campos 
                 * não finais.
                 */
                synchronized (pGraphics3D) {
                    pGraphics3D.setSize(width, height);
                    //pGraphics3D = (PGraphics3D) createGraphics(width, height, P3D);
                }
                synchronized (pGraphics2D) {
                    pGraphics2D.setSize(width, height);
                    //pGraphics2D = (PGraphics2D) createGraphics(width, height, P2D);
                }
            }
        }

        @Override
        public synchronized void setup() {
            size(DrawingPanel3D.this.width, DrawingPanel3D.this.height, P3D);
            pGraphics3D = (PGraphics3D) createGraphics(DrawingPanel3D.this.width, DrawingPanel3D.this.height, P3D);
            pGraphics2D = (PGraphics2D) createGraphics(DrawingPanel3D.this.width, DrawingPanel3D.this.height, P2D);
            DrawingPanel3D.this.setup(this);
//            hint(DISABLE_DEPTH_TEST);
//            hint(DISABLE_OPENGL_ERRORS);
        }

        @Override
        public void draw() {
            background(0);
            //3d
            synchronized (pGraphics3D) {
                pGraphics3D.beginDraw();
                //Camera
                applyCameraTransform(pGraphics3D);
                pGraphics3D.scale(scale, scale, scale);
                DrawingPanel3D.this.draw(pGraphics3D);

                for (Graph g : graphics) {
                    if (g instanceof Graph3D) {
                        ((Graph3D) g).draw(pGraphics3D);
                    }
                }

                pGraphics3D.endDraw();
                image(pGraphics3D, 0, 0);
            }
            //2d
            synchronized (pGraphics2D) {
                pGraphics2D.beginDraw();
                DrawingPanel3D.this.draw(pGraphics2D);

                for (Graph g : graphics) {
                    if (g instanceof Graph2D) {
                        ((Graph2D) g).draw(pGraphics2D);
                    }
                }

                pGraphics2D.endDraw();
                image(pGraphics2D, 0, 0);
            }
            //teclado
            synchronized (keys) {
                for (int code : keys) {
                    switch (code) {
                        case KeyEvent.VK_S:
                        case DOWN:
                            posX -= DEFAULT_MOVE * upX;
                            posY -= DEFAULT_MOVE * upY;
                            break;
                        case KeyEvent.VK_W:
                        case UP:
                            posX += DEFAULT_MOVE * upX;
                            posY += DEFAULT_MOVE * upY;
                            break;
                        case KeyEvent.VK_D:
                        case RIGHT:
                            posX -= DEFAULT_MOVE * upY;
                            posY += DEFAULT_MOVE * upX;
                            break;
                        case KeyEvent.VK_A:
                        case LEFT:
                            posX += DEFAULT_MOVE * upY;
                            posY -= DEFAULT_MOVE * upX;
                            break;
                        case KeyEvent.VK_SPACE:
                        case KeyEvent.VK_PAGE_UP:
                            eyeZ += DEFAULT_MOVE;
                            atZ += DEFAULT_MOVE;
                            break;
                        case KeyEvent.VK_SHIFT:
                        case KeyEvent.VK_PAGE_DOWN:
                            if (eyeZ - DEFAULT_MOVE > 80) {
                                eyeZ -= DEFAULT_MOVE;
                                atZ -= DEFAULT_MOVE;
                            }
                            break;
                        case KeyEvent.VK_Q:
                            if (panInsteadOfZoom) {
                                theta += 5;
                                float c = 2 * PI * theta / 1000;

                                atX = atDist * sin(c);
                                atY = atDist * cos(c);

                                upX = -sin(c);
                                upY = -cos(c);
                            } else {
                                scale++;
                            }
                            break;
                        case KeyEvent.VK_E:
                            if (panInsteadOfZoom) {
                                theta -= 5;
                                float c = 2 * PI * theta / 1000;

                                atX = atDist * sin(c);
                                atY = atDist * cos(c);

                                upX = -sin(c);
                                upY = -cos(c);
                            } else {
                                scale--;
                            }
                            break;
                    }
                }
            }
        }

        public void applyCameraTransform(PGraphics3D p3d) {
            p3d.camera(0, 0, eyeZ, atX, atY, atZ, upX, upY, 0);
            p3d.translate(posX, posY);
        }

        @Override
        public void mouseClicked() {
            DrawingPanel3D.this.mouseClicked(this);
        }

        @Override
        public void mousePressed() {
            if (mouseButton == RIGHT) {
                synchronized (keys) {
                    if (!keys.contains(new Integer(KeyEvent.VK_PAGE_UP))) {
                        keys.add(new Integer(KeyEvent.VK_PAGE_UP));
                    }
                }
                eyeZ += DEFAULT_MOVE;
            }
            DrawingPanel3D.this.mousePressed(this);
        }

        @Override
        public void mouseReleased() {
            if (mouseButton == RIGHT) {
                synchronized (keys) {
                    keys.remove(new Integer(KeyEvent.VK_PAGE_UP));
                }
            }
            DrawingPanel3D.this.mouseReleased(this);
        }

        @Override
        public void mouseEntered() {
            DrawingPanel3D.this.mouseEntered(this);
        }

        @Override
        public void mouseExited() {
            DrawingPanel3D.this.mouseExited(this);
        }

        @Override
        public void mouseDragged() {
            // controle do mouse
            // Z
            int dy = (pmouseY - mouseY);
            atZ += (atZ + dy >= eyeZ) ? 0 : dy;

            // X e Y
            theta += (pmouseX - mouseX);

            float c = 2 * PI * theta / 1000;

            atX = atDist * sin(c);
            atY = atDist * cos(c);

            upX = -sin(c);
            upY = -cos(c);
            DrawingPanel3D.this.mouseDragged(this);
        }

        @Override
        public void mouseMoved() {
            DrawingPanel3D.this.mouseMoved(this);
        }

        @Override
        public void keyTyped() {
            DrawingPanel3D.this.keyTyped(this);
        }

        @Override
        public void keyPressed() {
            synchronized (keys) {
                if (!keys.contains(keyCode)) {
                    keys.add(keyCode);
                }
            }
            DrawingPanel3D.this.keyPressed(this);
        }

        @Override
        public void keyReleased() {
            synchronized (keys) {
                keys.remove(new Integer(keyCode));
            }
            DrawingPanel3D.this.keyReleased(this);
        }
    }

    /**
     * Cria um painel de desenho utilizando uma classe que extende/sobrescreve a
     * classe {@link quickp3d.DrawingPanel3D.Scene3D}.
     * <p>
     * Util <b>apenas</b> quando se quer alterar/remover funções de controle do
     * teclado ou mouse já existentes.
     *
     * @param width comprimento x da janela
     * @param height altura y da janela
     * @param scene3D nova classe controladora
     */
    public DrawingPanel3D(int width, int height, Scene3D scene3D) {
        this.width = width;
        this.height = height;
        this.scene3D = scene3D;
    }

    public DrawingPanel3D(int width, int height, boolean fillParentComponent) {
        this.width = width;
        this.height = height;
        this.scene3D = new Scene3D(fillParentComponent);
    }

    public DrawingPanel3D(int width, int height) {
        this(width, height, false);
    }

    public void append(Graph g) {
        graphics.add(g);
    }

    public Scene3D getApplet() {
        return scene3D;
    }

    public JFrame createFrame(String name) {
        return scene3D.createFrame(name);
    }

    public JPanel createPanel() {
        return scene3D.createPanel();
    }

    public void setup(Scene3D applet) {
    }

    @Override
    public void draw(PGraphics3D g3d) {
        g3d.background(200);
    }

    @Override
    public void draw(PGraphics g2d) {
        g2d.clear();
        g2d.fill(0);
        g2d.textSize(15);
        g2d.textAlign = PApplet.RIGHT;
        g2d.text("fps: " + (int) scene3D.frameRate, scene3D.width - 30, scene3D.height - 40);
    }

    public Point left(Point p) {
        float t = theta % 1000;
        if (t < 0) {
            t += 1000;
        }
        t += 125;
        if (t < 250) {
            p.x = 1;
            p.y = 0;
        } else if (t < 500) {
            p.x = 0;
            p.y = -1;
        } else if (t < 750) {
            p.x = -1;
            p.y = 0;
        } else {
            p.x = 0;
            p.y = 1;
        }
        return p;
    }

    public Point right(Point p) {
        p.setLocation(left(p));
        p.x *= -1;
        p.y *= -1;
        return p;
    }

    public Point left() {
        return left(tmpPoint);
    }

    public Point right() {
        return right(tmpPoint);
    }

    public void mouseClicked(PApplet applet) {
//        System.out.println("mouseClicked" + mouseButton);
    }

    public void mousePressed(PApplet applet) {
//        System.out.println("mousePressed" + mouseButton);
    }

    public void mouseReleased(PApplet applet) {
//        System.out.println("mouseReleased" + mouseButton);
    }

    public void mouseEntered(PApplet applet) {
//        System.out.println("mouseEntered" + mouseButton);
    }

    public void mouseExited(PApplet applet) {
//        System.out.println("mouseExited" + mouseButton);
    }

    public void mouseDragged(PApplet applet) {
//        System.out.println("mouseDragged" + mouseButton);
    }

    public void mouseMoved(PApplet applet) {
//        System.out.println("mouseMoved" + mouseButton);
    }

    public void keyTyped(PApplet applet) {
//        System.out.println("keyTyped" + keyCode);
    }

    public void keyPressed(PApplet applet) {
//        System.out.println("keyPressed" + keyCode);
    }

    public void keyReleased(PApplet applet) {
//        System.out.println("keyReleased" + keyCode);
    }
}
