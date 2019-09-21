package org.dynamics.molecular.interfaces;


public interface PhysicalSystemWithSolution {

    public double getPositionForCertainTime(double t);
    public double getStartingVelocity();

}

