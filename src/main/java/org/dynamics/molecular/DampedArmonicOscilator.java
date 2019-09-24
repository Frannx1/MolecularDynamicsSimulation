package org.dynamics.molecular;

import org.dynamics.molecular.interfaces.PhysicalSystemWithSolution;

public class DampedArmonicOscilator implements PhysicalSystemWithSolution {

    private double amplitude = 1;
    private final double mass = 70.0;
    private final double k = Math.pow(10,4);
    private final double gamma = 100.0;


    @Override
    public double getPositionForCertainTime(double t) {
        return amplitude * Math.exp(-1 * t * gamma / (2*mass)) * Math.cos(Math.sqrt((k/mass) - Math.pow(0.5 * gamma / mass,2)) * t);
    }

    @Override
    public double getStartingVelocity() {
        return (-0.5) * amplitude * gamma / mass;
    }

    public double getPreviousStartingPosition(double dt){
        return getPositionForCertainTime(0) - dt * getStartingVelocity() + 0.5 * Math.pow(dt, 2) * getStartingForce()/mass;
    }

    public double getPreviousStartingVelocity(double dt){
        return getStartingVelocity() - dt * getStartingForce()/mass;
    }

    public double getStartingForce() {
        return -(getStartingVelocity() * gamma + getPositionForCertainTime(0) * k);
    }

    public double getForce(double velocity, double position){
        return -(k * position + gamma * velocity);
    }


    public double getMass(){
        return mass;
    }

    public double getK() {
        return k;
    }

    public StringBuffer simulate(double tMax, double dt){
        StringBuffer ret = new StringBuffer();
        IOManager io = new IOManager("comapre-original" + dt);
        DampedArmonicOscilator dao = new DampedArmonicOscilator();
        for(double i = 0; i <= tMax; i+=dt){
            ret.append(dao.getPositionForCertainTime(i) + " ");
        }
        ret.append("\n");
        io.generateOutputFiles(ret);

        io = new IOManager("comapre-verlet" + dt);
        ret = new StringBuffer();
        ret.append(ApproachMethod.VERLET.getApproachValue(tMax, dt, true));
        io.generateOutputFiles(ret);

        io = new IOManager("comapre-beeman" + dt);
        ret = new StringBuffer();
        ret.append(ApproachMethod.BEEMAN.getApproachValue(tMax, dt, true));
        io.generateOutputFiles(ret);

        io = new IOManager("comapre-gear" + dt);
        ret = new StringBuffer();
        ret.append(ApproachMethod.GEAR_PREDICTOR_CORRECTOR_ORDER_5.getApproachValue(tMax, dt, true));
        io.generateOutputFiles(ret);

        return ret;
    }

    public StringBuffer getQuadraticDifferences(){
        StringBuffer s = new StringBuffer("");
        for(int i = 2; i <= 7; i++){
            s.append(ApproachMethod.VERLET.meanQuadraticDifference(5, Math.pow(10, -i)));
            s.append(" ");
            s.append(ApproachMethod.VERLET.meanQuadraticDifference(5, Math.pow(10, -i - 0.5)));
            s.append(" ");
        }
        System.out.println("Verlet simulated");
        s.append("\n");
        for(int i = 2; i <= 7; i++){
            s.append(ApproachMethod.BEEMAN.meanQuadraticDifference(5, Math.pow(10, -i)));
            s.append(" ");
            s.append(ApproachMethod.BEEMAN.meanQuadraticDifference(5, Math.pow(10, -i - 0.5)));
            s.append(" ");
        }
        System.out.println("Beeman simulated");
        s.append("\n");
        for(int i = 2; i <= 7; i++){
            s.append(ApproachMethod.GEAR_PREDICTOR_CORRECTOR_ORDER_5.meanQuadraticDifference(5, Math.pow(10, -i)));
            s.append(" ");
            s.append(ApproachMethod.GEAR_PREDICTOR_CORRECTOR_ORDER_5.meanQuadraticDifference(5, Math.pow(10, -i - 0.5)));
            s.append(" ");
        }
        System.out.println("Gear simulated");
        s.append("\n");
        for(int i = 2; i <= 7; i++){
            s.append(Math.pow(10, -i));
            s.append(" ");
            s.append(Math.pow(10, -i - 0.5));
            s.append(" ");
        }
        s.append("\n");
        System.out.println(s.toString().length());
        return s;
    }

}
