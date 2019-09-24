package org.dynamics.molecular;

import java.util.*;

public class LennardJonesGas {

    private static final double EPSILON = 0.001;
    private double depth;
    private Particle[] particles;
    private double sigma;
    private double rm;
    private static final double height = 200;
    private static final double width = 400;
    private double dt;
    private double hole = 25;
    private CellIndexMethod cim;
    public int SAVE_CYCLE;

    public LennardJonesGas(int n) {
        this.depth = 2;
        this.rm = 1;
        this.sigma = rm / Math.pow(2, 1.0/6.0);
        this.particles = new Particle[n];
        initializeParticles(n);
        this.cim = new CellIndexMethod(5.0, width, particles, new FiveMetersDistance());
    }

    public LennardJonesGas() {
        this.cim = new CellIndexMethod(5.0, width, particles, new FiveMetersDistance());
        this.depth = 2;
        this.rm = 1;
        this.sigma = rm / Math.pow(2, 1.0/6.0);
    }

    public void setParticles(List<Particle> particles) {
        this.particles = particles.stream().toArray(Particle[]::new);
    }

    public void simulate(double dt, IOManager io, double frCut, int energyRow, double[][] energies) {
        this.dt = dt;
        this.SAVE_CYCLE = (int)((1.0/dt) * 0.1);
        int k = 0;
        double fr = 0.0;
        double t = 0;
        int energyIndex = 0;
        int indexMax = 60;
        if (energies != null) {
            energies[energyIndex][energyRow] = getTotalEnergy(); // Append energy
            energyIndex++;
        }
        else
            io.output.append(toString()); // Append normal data
        cim.setParticles(particles);
        cim.calculateCells();
        while ((fr < frCut && energies == null) || (energies != null && energyIndex < indexMax)) {
            if (energies == null) System.out.println(fr + " " + k);
            Arrays.stream(particles).parallel().forEach(p -> updatePosition(p));
            cim.calculateCells();
            Arrays.stream(particles).parallel().forEach(p -> updateAcceleration(p));
            Arrays.stream(particles).parallel().forEach(p -> updateVelocity(p));
            fr = calculateFR();
            if (k % SAVE_CYCLE == 0) {
                // Add energy
                if(energies != null) {
                    energies[energyIndex][energyRow] = getTotalEnergy();
                    System.out.println("--Energy --" + energies[energyIndex][energyRow]);
                    energyIndex++;
                } else {
                    io.output.append(toString());
                }
            }
            if (k % (SAVE_CYCLE * 100) == 0) { // write buffer to file and clean string buffer to save memory
                io.handlePartialOutput();
                k = 0;
            }
            t+=dt;
            k++;
        }
        io.handlePartialOutput();
    }


    public double getAngleX(Particle p1, Particle p2, double distance) {
        return (p1.getXD()[0] - p2.getXD()[0]) / distance;
    }

    public double getAngleY(Particle p1, Particle p2, double distance) {
        return (p1.getYD()[0] - p2.getYD()[0]) / distance;
    }

    public double getForceModule(double distance) {
        return (12 * depth/rm)  *  (Math.pow(rm/distance ,13) - Math.pow(rm/distance ,7));
    }

    public double[] getForces(int index) {
        double[] force = new double[2];
        Particle p = particles[index];
        LinkedList<Particle> neighbours = cim.getNeighbours(index);
        for(Particle neighbour : neighbours){
            double module = getForceModule(p.getDistanceTo(neighbour));
            force[0] += module * getAngleX(p, neighbour, p.getDistanceTo(neighbour));
            force[1] += module * getAngleY(p, neighbour, p.getDistanceTo(neighbour));
        }
        force[0] += getForceParticleImageX(p);
        force[1] += getForceParticleImageY(p);
        return force;
    }

    public boolean wallInTheMiddle(Particle p1, Particle p2) {
        boolean p1Side = (p1.getXD()[0] > this.width / 2);
        boolean p2Side = (p2.getXD()[0] > this.width / 2);
        if(p1Side == p2Side)
            return  false;
        double coeficient = (p1.getYD()[0] - p2.getYD()[0]) / (p1.getXD()[0] - p2.getXD()[0]);
        double constant = p1.getYD()[0] - coeficient * p1.getXD()[0];
        return (coeficient * this.width / 2 + constant < height/2 - 5  || coeficient * this.width / 2 + constant > height/2 + 5);
    }

    public double getForceParticleImageX(Particle p) {
        if(p.getXD()[0] <= 5){
            return getForceModule(p.getXD()[0]);
        }
        else if(p.getXD()[0] >= width - 5){
            return -getForceModule(width - p.getXD()[0]);
        }
        else if(Math.abs(p.getXD()[0] - width/2) <= 5 && (p.getYD()[0] < height/2 - hole || p.getYD()[0] > height/2 + hole)){
            if(p.getXD()[0] > width/2)
                return getForceModule(p.getXD()[0] - width/2);
            else
                return -getForceModule(width/2 - p.getXD()[0]);
        }
        return  0.0;
    }

    public double getForceParticleImageY(Particle p) {
        if(p.getYD()[0] <= 5){
            return getForceModule(p.getYD()[0]);
        }
        else if(p.getYD()[0] >= height - 5){
            return -getForceModule(height - p.getYD()[0]);
        }
        return  0.0;
    }

    private void initializeParticles(int n) {
        double x = 0.0, y = 0.0;
        Random r = new Random();
        LinkedList<Particle> ls = new LinkedList<>();
        for(int i = 0; i < n; i++){
            CellIndexMethod auxCIM = new CellIndexMethod(1.0, height, ls.stream().toArray(Particle[]::new), new OneMeterDistance());
            auxCIM.calculateCells();
            boolean found = false;
            while(!found) {
                x = r.nextDouble() * (height-2) + 1;
                y = r.nextDouble() * (width/2 - 2) + 1;
                found = auxCIM.superPositionWithOneParticle(x, y).isEmpty();
            }
            ls.addLast(new Particle(x, y, i, 10, r.nextDouble() * Math.PI * 2, 0.1));
            particles = ls.stream().toArray(Particle[]::new);
        }
    }

    public double calculateFR(){
        double fr = 0;
        for(int i = 0; i < particles.length; i++){
            if(particles[i].getXD()[0] > width/2){
                fr++;
            }
        }
        return fr/particles.length;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("");
        s.append(particles.length);
        s.append("\n\n");
        for (Particle p : particles) {
            s.append(p.toString());
            s.append("\n");
        }
        return s.toString();
    }

    public double getTotalEnergy() {
        return getKineticEnergy() + getPotentialEnergy();
    }

    public double getKineticEnergy() {
        double ret = 0;
        for (Particle p : particles) {
            ret += p.getMass() * Math.pow(p.getVelocityModule(), 2);
        }
        return 0.5 * ret;
    }

    public double getPotentialEnergy() {
        double totalPotentialEnergy = 0.0;
        double distance;
        for (Particle p1 : particles) {
            for (Particle p2 : particles) {
                if (p1.getNumber() != p2.getNumber()) {
                    distance = p1.getDistanceTo(p2);
                    if (distance < 5)
                        totalPotentialEnergy += depth * (Math.pow(rm / distance, 12) - 2 * Math.pow(rm / distance, 6));
                }
            }
        }
        return totalPotentialEnergy;
    }

    public void updatePosition(Particle p) {
        p.setX(p.getXD()[0] + dt * p.getXD()[1] + Math.pow(dt, 2) * p.getXD()[2]);
        p.setY(p.getYD()[0] + dt * p.getYD()[1] + Math.pow(dt, 2) * p.getYD()[2]);
        p.setVx(p.getXD()[1] + 0.5 * dt * p.getXD()[2]);
        p.setVy(p.getYD()[1] + 0.5 * dt * p.getYD()[2]);
    }

    public void updateVelocity(Particle p) {
        p.setVx(p.getXD()[1] + 0.5 * dt * p.getXD()[2]);
        p.setVy(p.getYD()[1] + 0.5 * dt * p.getYD()[2]);
    }

    public void updateAcceleration(Particle p) {
        double[] forces = getForces(p.getNumber());
        p.setAx(forces[0] / p.getMass());
        p.setAy(forces[1] / p.getMass());
    }

    public void simulate2t(double dt, IOManager io, IOManager ioFr) {
        this.dt = dt;
        this.SAVE_CYCLE = (int)((1.0/dt) * 0.1);
        long k = 0;
        double fr = 0.0;
        double te = 0;
        double t = 0;
        boolean finished = false;

        io.output.append(toString()); // Append normal data
        cim.setParticles(particles);
        cim.calculateCells();

        while (!finished) {
            Arrays.stream(particles).forEach(this::updatePosition);
            cim.calculateCells();
            Arrays.stream(particles).forEach(this::updateAcceleration);
            Arrays.stream(particles).forEach(this::updateVelocity);
            fr = calculateFR();
            if (k % SAVE_CYCLE == 0) {
                io.output.append(toString());
                ioFr.output.append(k).append(" ").append(fr).append(" ").append(t).append('\n');
                System.out.println(k + " " + fr + " " + t);
            }
            if (k % (SAVE_CYCLE * 50) == 0) { // write buffer to file and clean string buffer to save memory
                io.handlePartialOutput();
                ioFr.handlePartialOutput();
            }
            if (fr >= 0.46 && te < EPSILON) {
                te = t;
                System.out.println("Encontramos te: " + te + " en la iteracion: " + k);
            }
            if (te > EPSILON && t >= 2 * te) {
                finished = true;
            }
            t+=dt;
            k++;
        }
        io.handlePartialOutput();
        ioFr.handlePartialOutput();
    }


    public void continueSimulate2t(double dt, double t, double te, long k, IOManager io, IOManager ioFr) {
        this.dt = dt;
        this.SAVE_CYCLE = (int)((1.0/dt) * 0.1);
        double fr = 0.0;
        boolean finished = false;

        io.output.append(toString()); // Append normal data
        cim.setParticles(particles);
        cim.calculateCells();
        k++;
        while (!finished) {
            Arrays.stream(particles).forEach(this::updatePosition);
            cim.calculateCells();
            Arrays.stream(particles).forEach(this::updateAcceleration);
            Arrays.stream(particles).forEach(this::updateVelocity);
            fr = calculateFR();
            if (k % SAVE_CYCLE == 0) {
                ioFr.output.append(k).append(" ").append(fr).append(" ").append(t).append('\n');
                io.output.append(toString());
                System.out.println(k + " " + fr + " " + t);
            }
            if (k % (SAVE_CYCLE * 50) == 0) { // write buffer to file and clean string buffer to save memory
                io.handlePartialOutput();
                ioFr.handlePartialOutput();
            }
            if (fr >= 0.46 && te < EPSILON) {
                te = t;
                System.out.println("Encontramos te: " + te + " en la iteracion: " + k);
            }
            if (te > EPSILON && t >= 2 * te) {
                finished = true;
            }
            t+=dt;
            k++;
        }
        io.handlePartialOutput();
        ioFr.handlePartialOutput();
    }

    public void simulateT(double dt, double hole, IOManager io, IOManager ioFr) {
        this.dt = dt;
        this.SAVE_CYCLE = (int)((1.0/dt) * 0.1);
        long k = 0;
        double fr = 0.0;
        double te = 0;
        double t = 0;
        boolean finished = false;

        io.output.append(toString()); // Append normal data
        cim.setParticles(particles);
        cim.calculateCells();
        this.hole = hole;

        while (!finished) {
            Arrays.stream(particles).forEach(this::updatePosition);
            cim.calculateCells();
            Arrays.stream(particles).forEach(this::updateAcceleration);
            Arrays.stream(particles).forEach(this::updateVelocity);
            fr = calculateFR();
            if (k % SAVE_CYCLE == 0) {
                io.output.append(toString());
                ioFr.output.append(k).append(" ").append(fr).append(" ").append(t).append('\n');
                System.out.println(k + " " + fr + " " + t);
            }
            if (k % (SAVE_CYCLE * 50) == 0) { // write buffer to file and clean string buffer to save memory
                io.handlePartialOutput();
                ioFr.handlePartialOutput();
            }
            if (fr >= 0.48) {
                finished = true;
                System.out.println("Encontramos te: " + te + " en la iteracion: " + k);
            }
            t+=dt;
            k++;
        }
        io.handlePartialOutput();
        ioFr.handlePartialOutput();
    }
}
