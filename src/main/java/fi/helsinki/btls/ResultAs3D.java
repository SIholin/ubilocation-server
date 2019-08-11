package fi.helsinki.btls;

import org.apache.commons.math3.linear.RealMatrix;
import fi.helsinki.ubipositioning.datamodels.Beacon;
import fi.helsinki.ubipositioning.datamodels.Location;
import fi.helsinki.ubipositioning.datamodels.Location3D;
import fi.helsinki.ubipositioning.utils.IResultConverter;

public class ResultAs3D implements IResultConverter {
    @Override
    public Location convert(Beacon beacon, double[] centroid, double[] standardDeviation, RealMatrix covMatrix) {
        if (Double.isNaN(standardDeviation[0]) || Double.isNaN(standardDeviation[1]) || Double.isNaN(standardDeviation[2])) {
            throw new RuntimeException("not wanted value!");
        }

        return new Location3D(beacon.getId(),
                centroid[0], centroid[1], centroid[2],
                standardDeviation[0], standardDeviation[1], standardDeviation[2]);
    }
}
