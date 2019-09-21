package org.dynamics.molecular;

public class Particle {
    private double mass;
    private double xD[] = new double[6]; // derivatives of x
    private double yD[] = new double[6]; // derivatives of y
    private int number;
    private double radius;

    public Particle() { }

    public Particle(double x, double y, int number, double velocity, double angle, double mass) {
        this.number = number;
        xD[0] = x;
        xD[1] = Math.cos(angle) * velocity;
        yD[0] = y;
        yD[1] = Math.sin(angle) * velocity;
        this.mass = mass;
    }

    public Particle(Particle particle) {
        this.xD[0] = particle.getXD()[0];
        this.yD[0] = particle.getYD()[0];
        this.number = particle.getNumber();
        this.xD[1] = particle.getXD()[1];
        this.yD[1] = particle.getYD()[1];
        this.mass = particle.getMass();
        this.radius = particle.getRadius();
    }

    public int getNumber() {
        return number;
    }

    public void setX(double x){
        this.xD[0] = x;
    }

    public void setY(double y){
        this.yD[0] = y;
    }

    public void setVx(double vx){
        this.xD[1] = vx;
    }

    public void setVy(double vy){
        this.yD[1] = vy;
    }

    public void setAx(double ax){
        this.xD[2] = ax;
    }

    public void setAy(double ay){
        this.yD[2] = ay;
    }



    public void setNumber(int number) {
        this.number = number;
    }

    public double getMass() {
        return this.mass;
    }

    public void setMass(double mass) {
        this.mass = mass;
    }

    public double getRadius() {
        return this.radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return (xD[0] - 100) + " " + yD[0] + " " + xD[1] + " " + yD[1];
    }

    public double getDistanceTo(Particle particle) {
        double distanceX = xD[0] - particle.getXD()[0];
        double distanceY = yD[0] - particle.getYD()[0];
        return Math.pow(Math.pow(distanceX, 2) + Math.pow(distanceY, 2), 0.5);
    }

    public double[] getXD() {
        return xD;
    }

    public double[] getYD() {
        return yD;
    }

    public double getVelocityModule() {
        return Math.sqrt(Math.pow(xD[1], 2) + Math.pow(yD[1], 2));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + number;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Particle other = (Particle) obj;
        if (number != other.getNumber())
            return false;
        return true;
    }

}

