package com.puzzletimer.graphics;

import java.awt.Color;
import java.util.ArrayList;


public class Mesh {
    public final Face[] faces;

    public Mesh(Face[] faces) {
        this.faces = faces;
    }

    public Mesh transform(Matrix44 matrix) {
        Face[] faces = new Face[this.faces.length];
        for (int i = 0; i < faces.length; i++) {
            faces[i] = this.faces[i].transform(matrix);
        }

        return new Mesh(faces);
    }

    public Mesh rotateHalfspace(Plane plane, double angle) {
        Matrix44 matrix = Matrix44.rotation(plane.n, angle);

        Face[] faces = new Face[this.faces.length];
        for (int i = 0; i < faces.length; i++) {
            faces[i] = this.faces[i];
            if (faces[i].centroid().sub(plane.p).dot(plane.n) >= 0) {
                faces[i] = faces[i].transform(matrix);
            }
        }

        return new Mesh(faces);
    }

    public Mesh shortenFaces(double length) {
        Face[] faces = new Face[this.faces.length];
        for (int i = 0; i < faces.length; i++) {
            faces[i] = this.faces[i].shorten(length);
        }

        return new Mesh(faces);
    }

    public Mesh softenFaces(double length) {
        Face[] faces = new Face[this.faces.length];
        for (int i = 0; i < faces.length; i++) {
            faces[i] = this.faces[i].soften(length);
        }

        return new Mesh(faces);
    }

    public Mesh clip(Plane plane) {
        ArrayList<Face> faces = new ArrayList<Face>();
        for (int i = 0; i < this.faces.length; i++) {
            Face face = this.faces[i].clip(plane);
            if (face.vertices.length > 0) {
                faces.add(face);
            }
        }

        Face[] facesArray = new Face[faces.size()];
        faces.toArray(facesArray);

        return new Mesh(facesArray);
    }

    public Mesh cut(Plane plane, double width) {
        Mesh front = clip(new Plane(plane.p.add(plane.n.mul(width / 2)), plane.n));
        Mesh back = clip(new Plane(plane.p.sub(plane.n.mul(width / 2)), plane.n.neg()));

        return front.union(back);
    }

    public Mesh union(Mesh mesh) {
        Face[] faces = new Face[this.faces.length + mesh.faces.length];
        int next = 0;

        for (int i = 0; i < this.faces.length; i++) {
            faces[next] = this.faces[i];
            next++;
        }

        for (int i = 0; i < mesh.faces.length; i++) {
            faces[next] = mesh.faces[i];
            next++;
        }

        return new Mesh(faces);
    }

    public static Mesh cube(Color[] colors) {
        double a = 0.5;

        Vector3[] vertices = {
            new Vector3(-a, -a, -a),
            new Vector3(-a, -a,  a),
            new Vector3(-a,  a, -a),
            new Vector3(-a,  a,  a),
            new Vector3( a, -a, -a),
            new Vector3( a, -a,  a),
            new Vector3( a,  a, -a),
            new Vector3( a,  a,  a),
        };

        Face[] faces = {
            new Face(new Vector3[] { vertices[0], vertices[1], vertices[3], vertices[2] }, colors[0]),
            new Face(new Vector3[] { vertices[1], vertices[5], vertices[7], vertices[3] }, colors[1]),
            new Face(new Vector3[] { vertices[0], vertices[4], vertices[5], vertices[1] }, colors[2]),
            new Face(new Vector3[] { vertices[4], vertices[6], vertices[7], vertices[5] }, colors[3]),
            new Face(new Vector3[] { vertices[0], vertices[2], vertices[6], vertices[4] }, colors[4]),
            new Face(new Vector3[] { vertices[2], vertices[3], vertices[7], vertices[6] }, colors[5]),
        };

        return new Mesh(faces);
    }

    public static Mesh tetrahedron(Color[] colors) {
        double a = 1.5;
        double h = Math.sqrt(3.0) / 2.0 * a;
        double h1 = 2.0 * Math.sqrt(2.0) / 3.0 * h;

        Vector3[] vertices = {
            new Vector3(     0.0,      -h1 / 4.0, 2.0 * h / 3.0),
            new Vector3(-a / 2.0,      -h1 / 4.0,      -h / 3.0),
            new Vector3( a / 2.0,      -h1 / 4.0,      -h / 3.0),
            new Vector3(     0.0, 3.0 * h1 / 4.0,           0.0),
        };

        Face[] faces = {
            new Face(new Vector3[] { vertices[0], vertices[1], vertices[2] }, colors[0]),
            new Face(new Vector3[] { vertices[0], vertices[3], vertices[1] }, colors[1]),
            new Face(new Vector3[] { vertices[0], vertices[2], vertices[3] }, colors[2]),
            new Face(new Vector3[] { vertices[1], vertices[3], vertices[2] }, colors[3]),
        };

        return new Mesh(faces);
    }

    public static Mesh dodecahedron(Color[] colors) {
        double a = 0.85 * 1.0 / Math.sqrt(3.0);
        double b = 0.85 * Math.sqrt((3.0 - Math.sqrt(5.0)) / 6.0);
        double c = 0.85 * Math.sqrt((3.0 + Math.sqrt(5.0)) / 6.0);

        Face[] faces = {
            new Face(new Vector3[] { new Vector3( a,  a,  a), new Vector3( b,  c,  0), new Vector3(-b,  c,  0), new Vector3(-a,  a,  a), new Vector3( 0,  b,  c) }, colors[ 0]),
            new Face(new Vector3[] { new Vector3( a,  a,  a), new Vector3( 0,  b,  c), new Vector3( 0, -b,  c), new Vector3( a, -a,  a), new Vector3( c,  0,  b) }, colors[ 1]),
            new Face(new Vector3[] { new Vector3( c,  0,  b), new Vector3( a, -a,  a), new Vector3( b, -c,  0), new Vector3( a, -a, -a), new Vector3( c,  0, -b) }, colors[ 2]),
            new Face(new Vector3[] { new Vector3(-b,  c,  0), new Vector3(-a,  a, -a), new Vector3(-c,  0, -b), new Vector3(-c,  0,  b), new Vector3(-a,  a,  a) }, colors[ 3]),
            new Face(new Vector3[] { new Vector3( a, -a, -a), new Vector3( 0, -b, -c), new Vector3( 0,  b, -c), new Vector3( a,  a, -a), new Vector3( c,  0, -b) }, colors[ 4]),
            new Face(new Vector3[] { new Vector3(-a, -a, -a), new Vector3(-b, -c,  0), new Vector3(-a, -a,  a), new Vector3(-c,  0,  b), new Vector3(-c,  0, -b) }, colors[ 5]),
            new Face(new Vector3[] { new Vector3( a,  a,  a), new Vector3( c,  0,  b), new Vector3( c,  0, -b), new Vector3( a,  a, -a), new Vector3( b,  c,  0) }, colors[ 6]),
            new Face(new Vector3[] { new Vector3( b,  c,  0), new Vector3( a,  a, -a), new Vector3( 0,  b, -c), new Vector3(-a,  a, -a), new Vector3(-b,  c,  0) }, colors[ 7]),
            new Face(new Vector3[] { new Vector3( 0,  b,  c), new Vector3(-a,  a,  a), new Vector3(-c,  0,  b), new Vector3(-a, -a,  a), new Vector3( 0, -b,  c) }, colors[ 8]),
            new Face(new Vector3[] { new Vector3(-a, -a,  a), new Vector3(-b, -c,  0), new Vector3( b, -c,  0), new Vector3( a, -a,  a), new Vector3( 0, -b,  c) }, colors[ 9]),
            new Face(new Vector3[] { new Vector3(-a, -a, -a), new Vector3(-c,  0, -b), new Vector3(-a,  a, -a), new Vector3( 0,  b, -c), new Vector3( 0, -b, -c) }, colors[10]),
            new Face(new Vector3[] { new Vector3(-a, -a, -a), new Vector3( 0, -b, -c), new Vector3( a, -a, -a), new Vector3( b, -c,  0), new Vector3(-b, -c,  0) }, colors[11]),
        };

        return new Mesh(faces);
    }
}
