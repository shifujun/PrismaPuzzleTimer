package com.puzzletimer.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.JPanel;


@SuppressWarnings("serial")
public class Panel3D extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
    private Mesh mesh;
    private Vector3 lightDirection;
    private Vector3 viewerPosition;
    private Vector3 cameraPosition;
    private Vector3 cameraRotation;
    private int lastX;
    private int lastY;

    public Panel3D() {
        this.mesh = new Mesh(new Face[0]);
        this.lightDirection = new Vector3(0, 0.25, -1).unit();
        this.viewerPosition = new Vector3(0, 0, -325);
        this.cameraPosition = new Vector3(0, 0, -2.8);
        this.cameraRotation = new Vector3(0, 0, 0);

        this.lastX = 0;
        this.lastY = 0;

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
        repaint();
    }

    public void setLightDirection(Vector3 lightDirection) {
        this.lightDirection = lightDirection;
        repaint();
    }

    public void setViewerPosition(Vector3 viewerPosition) {
        this.viewerPosition = viewerPosition;
        repaint();
    }

    public void setCameraPosition(Vector3 cameraPosition) {
        this.cameraPosition = cameraPosition;
        repaint();
    }

    public void setCameraRotation(Vector3 cameraRotation) {
        this.cameraRotation = cameraRotation;
        repaint();
    }

    private Vector3 toCameraCoordinates(Vector3 v) {
        return Matrix44.rotationX(-this.cameraRotation.x).mul(
               Matrix44.rotationY(-this.cameraRotation.y).mul(
               Matrix44.rotationZ(-this.cameraRotation.z).mul(
               v.sub(this.cameraPosition))));
    }

    private Vector3 perspectiveProjection(Vector3 v) {
        return new Vector3(
            (getWidth() / 2.0) + (-v.x - this.viewerPosition.x) * (this.viewerPosition.z / v.z),
            (getHeight() / 2.0) + (v.y - this.viewerPosition.y) * (this.viewerPosition.z / v.z),
            0);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // painter's algorithm
        Face[] faces = Arrays.copyOf(this.mesh.faces, this.mesh.faces.length);
        Arrays.sort(faces, new Comparator<Face>() {
            @Override
            public int compare(Face f1, Face f2) {
                return Double.compare(
                    f2.centroid().z,
                    f1.centroid().z);
            }
        });

        // projection
        Face[] pFaces = new Face[faces.length];
        for (int i = 0; i < pFaces.length; i++) {
            Vector3[] vertices = new Vector3[faces[i].vertices.length];
            for (int j = 0; j < vertices.length; j++) {
                vertices[j] = perspectiveProjection(toCameraCoordinates(faces[i].vertices[j]));
            }

            pFaces[i] = faces[i].setVertices(vertices);
        }

        // rendering
        Color backfacingColor =
            new Color(
                (4 * getBackground().getRed()   + 32) / 5,
                (4 * getBackground().getGreen() + 32) / 5,
                (4 * getBackground().getBlue()  + 32) / 5);

        for (Face pFace : pFaces) {
            Polygon polygon = new Polygon();
            for (Vector3 v : pFace.vertices) {
                polygon.addPoint((int) v.x, (int) v.y);
            }

            Plane plane =
                new Plane(
                    pFace.vertices[0],
                    pFace.vertices[1],
                    pFace.vertices[2]);

            // front facing
            if (plane.n.z >= 0d) {
                // flat shading
                double light = Math.abs(this.lightDirection.dot(plane.n));

                // draw polygon
                float[] hsbColor = Color.RGBtoHSB(
                    pFace.color.getRed(),
                    pFace.color.getGreen(),
                    pFace.color.getBlue(),
                    null);
                Color fillColor = new Color(
                    Color.HSBtoRGB(
                        hsbColor[0],
                        (float) (0.875 + 0.125 * light) * hsbColor[1],
                        (float) (0.875 + 0.125 * light) * hsbColor[2]));
                g2.setColor(fillColor);
                g2.fillPolygon(polygon);

                // draw outline
                Color outlineColor = new Color(
                    Color.HSBtoRGB(
                        hsbColor[0],
                        (float) (0.9 * (0.875 + 0.125 * light) * hsbColor[1]),
                        (float) (0.9 * (0.875 + 0.125 * light) * hsbColor[2])));
                g2.setColor(outlineColor);
                g2.drawPolygon(polygon);
            }

            // back facing
            else {
                g2.setColor(backfacingColor);
                g2.fillPolygon(polygon);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
      this.lastX = e.getX();
      this.lastY = e.getY();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        double angleX = (e.getY() - this.lastY) / 50.0;
        double angleY = (e.getX() - this.lastX) / 50.0;

        this.mesh = this.mesh.transform(
            Matrix44.rotationZ(this.cameraRotation.z).mul(
            Matrix44.rotationY(this.cameraRotation.y).mul(
            Matrix44.rotationX(this.cameraRotation.x).mul(
            Matrix44.rotationX(angleX).mul(
            Matrix44.rotationY(angleY).mul(
            Matrix44.rotationX(-this.cameraRotation.x).mul(
            Matrix44.rotationY(-this.cameraRotation.y).mul(
            Matrix44.rotationZ(-this.cameraRotation.z)))))))));

        this.lastX = e.getX();
        this.lastY = e.getY();

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        Vector3 direction = this.cameraPosition.unit();
        Vector3 newPosition = this.cameraPosition.add(direction.mul(0.1 * e.getWheelRotation()));
        if (1.0 < newPosition.norm() && newPosition.norm() < 50.0) {
            this.cameraPosition = newPosition;
        }

        repaint();
    }
}
