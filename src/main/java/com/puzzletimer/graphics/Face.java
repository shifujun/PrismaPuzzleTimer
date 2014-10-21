package com.puzzletimer.graphics;

import java.awt.Color;
import java.util.ArrayList;


public class Face {
    public final Vector3[] vertices;
    public final Color color;

    public Face(Vector3[] vertices, Color color) {
        this.vertices = vertices;
        this.color = color;
    }

    public Face setVertices(Vector3[] vertices) {
        return new Face(vertices, this.color);
    }

    public Face setColor(Color color) {
        return new Face(this.vertices, color);
    }

    public Vector3 centroid() {
        Vector3 sum = new Vector3(0, 0, 0);
        for (Vector3 v : this.vertices) {
            sum = sum.add(v);
        }

        return sum.mul(1.0 / this.vertices.length);
    }

    public Face transform(Matrix44 matrix) {
        Vector3[] vertices = new Vector3[this.vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = matrix.mul(this.vertices[i]);
        }

        return setVertices(vertices);
    }

    public Face shorten(double length) {
        Vector3 centroid = centroid();

        Vector3[] vertices = new Vector3[this.vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            Vector3 d = this.vertices[i].sub(centroid).unit();
            vertices[i] = this.vertices[i].sub(d.mul(length));
        }

        return setVertices(vertices);
    }

    public Face soften(double length) {
        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        for (int i = 0; i < this.vertices.length; i++) {
            Vector3 v1 = this.vertices[i];
            Vector3 v2 = this.vertices[(i + 1) % this.vertices.length];

            if (v2.sub(v1).norm() > 2 * length) {
                vertices.add(v1.add(v2.sub(v1).unit().mul(length)));
                vertices.add(v2.add(v1.sub(v2).unit().mul(length)));
            } else {
                vertices.add(v1.add(v2).mul(0.5));
            }
        }

        Vector3[] verticesArray = new Vector3[vertices.size()];
        vertices.toArray(verticesArray);

        return setVertices(verticesArray);
    }

    public Face clip(Plane plane) {
        double EPSILON = 0.01;
        int INSIDE = 0;
        int FRONT = 1;
        int BACK = 2;

        int[] position = new int[this.vertices.length];
        boolean allFront = true;
        boolean allBack = true;
        for (int i = 0; i < this.vertices.length; i++) {
            double d = this.vertices[i].sub(plane.p).dot(plane.n);

            if (d > EPSILON) {
                position[i] = FRONT;
                allBack = false;
            } else if (d < -EPSILON) {
                position[i] = BACK;
                allFront = false;
            } else {
                position[i] = INSIDE;
            }
        }

        if (allBack) {
            return setVertices(new Vector3[0]);
        }

        if (allFront) {
            return this;
        }

        ArrayList<Vector3> vertices = new ArrayList<Vector3>();
        for (int i = 0; i < this.vertices.length; i++) {
            Vector3 v1 = this.vertices[i];
            Vector3 v2 = this.vertices[(i + 1) % this.vertices.length];
            int p1 = position[i];
            int p2 = position[(i + 1) % this.vertices.length];

            if (p1 != BACK) {
                vertices.add(v1);
            }

            if ((p1 == FRONT && p2 == BACK) || (p1 == BACK && p2 == FRONT)) {
                double t = -(plane.n.dot(v1) + plane.n.neg().dot(plane.p)) / v2.sub(v1).dot(plane.n);
                vertices.add(v1.add(v2.sub(v1).mul(t)));
            }
        }

        Vector3[] verticesArray = new Vector3[vertices.size()];
        vertices.toArray(verticesArray);

        return setVertices(verticesArray);
    }
}
