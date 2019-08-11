package fi.helsinki.btls;

import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import fi.helsinki.ubipositioning.datamodels.Beacon;
import fi.helsinki.ubipositioning.datamodels.Location;
import fi.helsinki.ubipositioning.datamodels.Location2D;
import fi.helsinki.ubipositioning.utils.IResultConverter;

public class ResultAs2D implements IResultConverter {
    @Override
    public Location convert(Beacon beacon, double[] centroid, double[] standardDeviation, RealMatrix covMatrix) {
        if (Double.isNaN(standardDeviation[0]) || Double.isNaN(standardDeviation[1])) {
            throw new RuntimeException("not wanted value!");
        }

        EigenDecomposition ed = new EigenDecomposition(covMatrix);
        double[] realEigenvalues = ed.getRealEigenvalues();
        RealVector principal = ed.getEigenvector(0);
        double highestEigenValue = realEigenvalues[0];

        for (int i = 1; i < realEigenvalues.length; i++) {
            RealVector eigenvector = ed.getEigenvector(i);
            double eigenvalue = realEigenvalues[i];

            if (highestEigenValue < eigenvalue) {
                principal = eigenvector;
                highestEigenValue = eigenvalue;
            }
        }

        double[] asArray = principal.toArray();
        double arctan = Math.atan(asArray[0] / asArray[1]); // angle is gotten through arc tangent.

        return new Location2D(beacon.getId(),
                centroid[0], centroid[1],
                standardDeviation[0], standardDeviation[1], arctan);
    }
}
