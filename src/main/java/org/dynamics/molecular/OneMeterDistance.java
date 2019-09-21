package org.dynamics.molecular;

import org.dynamics.molecular.interfaces.NeighbourFinder;

public class OneMeterDistance implements NeighbourFinder {

    @Override
    public boolean areNeighbours(Particle p1, Particle p2) {
        return (p1.getDistanceTo(p2) <= 1);
    }
}
