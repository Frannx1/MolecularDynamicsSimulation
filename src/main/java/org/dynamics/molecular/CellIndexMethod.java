package org.dynamics.molecular;

import org.dynamics.molecular.Particle;
import org.dynamics.molecular.interfaces.NeighbourFinder;

import java.util.HashMap;
import java.util.LinkedList;

public class CellIndexMethod{
    private HashMap<Integer, LinkedList<Integer>> cells;
    private HashMap<Integer, Integer> particleInCell;
    private double cellSize;
    private double l;
    private Particle[] particles;
    private NeighbourFinder neighbourFinder;

    public CellIndexMethod(double cellSize, double l, Particle[] ls, NeighbourFinder nf){
        cells = new HashMap<>();
        particleInCell = new HashMap<>();
        this.cellSize = cellSize;
        particles = ls;
        this.l = l;
        neighbourFinder = nf;
    }

    public void setParticles(Particle[] p){
        this.particles = p;
    }

    public void calculateCells(){
        cells.clear();
        for(Particle p : particles){
            int row = (int)Math.floor(p.getYD()[0] / cellSize);
            int column = (int)Math.floor(p.getXD()[0] / cellSize);
            int cell = (int)(row * (l/cellSize) + column);
            particleInCell.put(p.getNumber(), cell);
            if(cells.get(cell) == null){
                cells.put(cell, new LinkedList<>());
            }
            LinkedList<Integer> ls = cells.get(cell);
            ls.add(p.getNumber());
            cells.put(cell, ls);
        }
    }

    public LinkedList<Particle> superPositionWithOneParticle(double x, double y){
        Particle particle = new Particle(x, y, -1, 0, 0, 0);
        int row = (int)Math.floor(y / cellSize);
        int col = (int)Math.floor(x / cellSize);
        LinkedList<Particle> ret = new LinkedList<>();
        for(int i = row-1; i <= row+1 && i < l/cellSize; i++){
            for(int j = col-1; j <= col+1 && j < l/cellSize; j++){
                if(row + i >= 0 && col + j >= 0 && cells.get(j + (int)(l/cellSize * i)) != null){
                    for(int p : cells.get(j + (int)(l/cellSize * i) )){
                        if(neighbourFinder.areNeighbours(particle, particles[p])){
                            ret.add(particles[p]);
                        }
                    }
                }
            }
        }
        return ret;
    }

    public LinkedList<Particle> getNeighbours(int number){
        LinkedList<Particle> ret = new LinkedList<>();
        if(number >= particles.length){
            return ret;
        }
        Particle p1 = particles[number];
        int cell = particleInCell.get(number);
        int row = (int)(cell / (l/cellSize));
        int col = (int)(cell % (l/cellSize));
        for(int i = row-1; i <= row+1 && i < l/cellSize; i++){
            for(int j = col-1; j <= col+1 && j < l/cellSize; j++){
                if(row + i >= 0 && col + j >= 0 && cells.get(j + (int)(l/cellSize * i)) != null){
                    for(int p : cells.get(j + (int)(l/cellSize * i) )){
                        if(number != p && neighbourFinder.areNeighbours(p1, particles[p])){
                            ret.add(particles[p]);
                        }
                    }
                }
            }
        }
        return ret;
    }
}