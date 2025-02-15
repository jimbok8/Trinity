package edu.jhuapl.trinity.utils;

/*-
 * #%L
 * trinity
 * %%
 * Copyright (C) 2021 - 2023 The Johns Hopkins University Applied Physics Laboratory LLC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import edu.jhuapl.trinity.data.messages.FeatureCollection;
import edu.jhuapl.trinity.utils.umap.Umap;
import javafx.geometry.Point2D;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.commons.math3.stat.correlation.Covariance;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Sean Phillips
 * @link https://stats.stackexchange.com/questions/2691/making-sense-of-principal-component-analysis-eigenvectors-eigenvalues
 */
public enum AnalysisUtils {
    INSTANCE;
    public static double EPISILON = 0.0000000001;

    public static double lerp1(double start, double end, double ratio) {
        return start * (1 - ratio) + end * ratio;
    }

    public static double lerp2(double s, double e, double t) {
        return s + (e - s) * t;
    }

    public static double blerp(double c11, double c21, double c12, double c22, double tx, double ty) {
        return lerp2(lerp2(c22, c21, tx), lerp2(c12, c22, tx), ty);
    }

    public static double cerp(double y1, double y2, double mu) {
        double mu2 = (1 - Math.cos(mu * Math.PI)) / 2;
        return (y1 * (1 - mu2) + y2 * mu2);
    }

    private List<Point2D> arcTerp(Point2D center, int count, double arcDegrees, double radius) {
        double fx = Math.cos(Math.toRadians(arcDegrees));
        double fy = Math.sin(Math.toRadians(arcDegrees));
        double lx = -Math.sin(Math.toRadians(arcDegrees));
        double ly = Math.cos(Math.toRadians(arcDegrees));
        List<Point2D> arcPoints = new ArrayList<>();
        for (int i = 0; i <= count; i++) {
            double sub_angle = (i / count) * Math.toRadians(arcDegrees);
            double xi = center.getX() + radius * (Math.sin(sub_angle) * fx + (1 - Math.cos(sub_angle)) * (-lx));
            double yi = center.getY() + radius * (Math.sin(sub_angle) * fy + (1 - Math.cos(sub_angle)) * (-ly));
            arcPoints.add(new Point2D(xi, yi));
        }
        return arcPoints;
    }

    /**
     * Interpolates between two end points.
     *
     * @param a The end point at <code>t = 0</code>.
     * @param b The end point at <code>t = 1</code>.
     * @param t The value at which to interpolate.
     * @return The value that is the fraction <code>t</code> of the way from
     * <code>a</code> to <code>b</code>: <code>(1-t)a + tb</code>.
     */
    public static double interpolate(double a, double b, double t) {
        return a + t * (b - a);
    }

    /**
     * Interpolates between two points on a line.
     *
     * @param x0 The x-coordinate of the first point.
     * @param y0 The y-coordinate of the first point.
     * @param x1 The x-coordinate of the second point.
     * @param y1 The y-coordinate of the second point.
     * @param x  The x-coordinate at which to interpolate.
     * @return The y-coordinate corresponding to <code>x</code>.
     */
    public static double interpolate(double x0, double y0, double x1,
                                     double y1, double x) {
        double t = (x - x0) / (x1 - x0);
        return interpolate(y0, y1, t);
    }

    /**
     * Performs a bilinear interpolation between four values.
     *
     * @param _00 The value at <code>(t, u) = (0, 0)</code>.
     * @param _10 The value at <code>(t, u) = (1, 0)</code>.
     * @param _01 The value at <code>(t, u) = (0, 1)</code>.
     * @param _11 The value at <code>(t, u) = (1, 1)</code>.
     * @param t   The first value at which to interpolate.
     * @param u   The second value at which to interpolate.
     * @return The interpolated value at <code>(t, u)</code>.
     */
    public static double bilinearInterpolate(double _00, double _10,
                                             double _01, double _11, double t, double u) {

        return interpolate(interpolate(_00, _10, t),
            interpolate(_01, _11, t), u);

    }

    public static double[][] featuresMultWeights(double[] features, double[][] weights) {
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(weights);
        RealMatrix realMatrixColumn = MatrixUtils.createColumnRealMatrix(features);
        RealMatrix resultMatrix = realMatrix.multiply(realMatrixColumn);
        return resultMatrix.getData();
    }

    /**
     * @author Sean Phillips
     * Principal Component Analysis using Apache Math Commons EigenDecomposition
     * @link https://stackoverflow.com/questions/10604507/pca-implementation-in-java
     */
    public static double[] doCommonsPCA(double[][] array) {
        //create real matrix
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(array);
        Covariance covariance = new Covariance(realMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        EigenDecomposition ed = new EigenDecomposition(covarianceMatrix);
        return ed.getRealEigenvalues();
    }

    public static double[] doCommonsSVD(double[][] array) {
        return getSVD(array).getSingularValues();
    }

    public static SingularValueDecomposition getSVD(double[][] array) {
        //create real matrix
        RealMatrix realMatrix = MatrixUtils.createRealMatrix(array);
        Covariance covariance = new Covariance(realMatrix);
        RealMatrix covarianceMatrix = covariance.getCovarianceMatrix();
        return new SingularValueDecomposition(covarianceMatrix);
    }

    public static List<Double> gmmFullCovToDiag(List<List<Double>> fullCov) {
        //Copy our covariance matrix into a 2D array (required by apache commons)
        int xSize = fullCov.size();
        int ySize = fullCov.get(0).size();
        double[][] array = new double[xSize][ySize];
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                array[x][y] = fullCov.get(x).get(y);
            }
        }
        //perform the SVD on the covariance matrix
        SingularValueDecomposition svd = getSVD(array);
        //@TODO SMP Rotate the values to get orientation

        //Copy rotated values into List<Double>
        ArrayList<Double> svdValues = new ArrayList<>();
        for (double d : svd.getSingularValues()) {
            svdValues.add(d);
        }
        return svdValues;
    }

    public static double[][] fitUMAP(FeatureCollection featureCollection) {
        Umap umap = new Umap();
        umap.setVerbose(true);
        umap.setNumberComponents(3);
        umap.setNumberEpochs(100);
        umap.setNumberNearestNeighbours(4);
        umap.setMinDist(0.25f);
        umap.setSpread(0.75f);
        umap.setNegativeSampleRate(20);
        return fitUMAP(featureCollection, umap);
    }

    public static double[][] fitUMAP(FeatureCollection featureCollection, Umap umap) {
        //for each dimension extract transform via UMAP
        double[][] data = featureCollection.convertFeaturesToArray();
        System.out.println("Starting UMAP Fit... ");
        long start = System.nanoTime();
        double[][] projected = umap.fitTransform(data);
        Utils.printTotalTime(start);
        return projected;
    }
}
