package org.dynamics.molecular.interfaces;

import org.dynamics.molecular.LennardJonesGas;

public interface Approacher {

    public StringBuffer getApproachValue(double tMax, double dt, boolean compareToOscilator);
    public double meanQuadraticDifference(double tMax, double dt);
}
