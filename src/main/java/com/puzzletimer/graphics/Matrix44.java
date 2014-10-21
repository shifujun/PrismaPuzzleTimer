package com.puzzletimer.graphics;


public class Matrix44 {
    public final double[][] values;

    public Matrix44(double[][] matrix) {
        this.values = matrix;
    }

    public Matrix44 mul(Matrix44 m) {
        double[][] vals = new double[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                vals[i][j] = 0d;
                for (int k = 0; k < 4; k++) {
                    vals[i][j] += this.values[i][k] * m.values[k][j];
                }
            }
        }

        return new Matrix44(vals);
    }

    public Vector3 mul(Vector3 v) {
        return new Vector3(
            this.values[0][0] * v.x + this.values[0][1] * v.y + this.values[0][2] * v.z + this.values[0][3],
            this.values[1][0] * v.x + this.values[1][1] * v.y + this.values[1][2] * v.z + this.values[1][3],
            this.values[2][0] * v.x + this.values[2][1] * v.y + this.values[2][2] * v.z + this.values[2][3]);
    }

    public static Matrix44 translation(Vector3 v) {
        return new Matrix44(
            new double[][] {
                new double[] { 1, 0, 0, v.x },
                new double[] { 0, 1, 0, v.y },
                new double[] { 0, 0, 1, v.z },
                new double[] { 0, 0, 0,   1 },
            });
    }

    public static Matrix44 rotationX(double a) {
        return new Matrix44(
            new double[][] {
                new double[] { 1,            0,           0, 0 },
                new double[] { 0,  Math.cos(a), Math.sin(a), 0 },
                new double[] { 0, -Math.sin(a), Math.cos(a), 0 },
                new double[] { 0,            0,           0, 1 },
            });
    }

    public static Matrix44 rotationY(double a) {
        return new Matrix44(
            new double[][] {
                new double[] { Math.cos(a), 0, -Math.sin(a), 0 },
                new double[] { 0,           1,            0, 0 },
                new double[] { Math.sin(a), 0,  Math.cos(a), 0 },
                new double[] { 0,           0,            0, 1 },
            });
    }

    public static Matrix44 rotationZ(double a) {
        return new Matrix44(
            new double[][] {
                new double[] {  Math.cos(a), Math.sin(a), 0, 0 },
                new double[] { -Math.sin(a), Math.cos(a), 0, 0 },
                new double[] {            0,           0, 1, 0 },
                new double[] {            0,           0, 0, 1 },
            });
    }

    public static Matrix44 rotation(Vector3 v, double a) {
        double c = Math.cos(a);
        double s = Math.sin(a);
        double x = v.x;
        double y = v.y;
        double z = v.z;

        return new Matrix44(
            new double[][] {
                new double[] { 1 + (1 - c) * (x * x - 1),  -z * s + (1 - c) * x * y,   y * s + (1 - c) * x * z, 0 },
                new double[] {   z * s + (1 - c) * x * y, 1 + (1 - c) * (y * y - 1),  -x * s + (1 - c) * y * z, 0 },
                new double[] {  -y * s + (1 - c) * x * z,   x * s + (1 - c) * y * z, 1 + (1 - c) * (z * z - 1), 0 },
                new double[] {                         0,                         0,                         0, 1 },
            });
    }
}
