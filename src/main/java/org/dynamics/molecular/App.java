package org.dynamics.molecular;

import java.util.ArrayList;
import java.util.Scanner;

public class App 
{
    private ArrayList<Double> energies = new ArrayList<>();
    
    public static void main(String[] args) {
        if(args.length > 0) {
            System.out.println("First run the program with no arguments.");
            return;
        }
        Scanner in = new Scanner(System.in);
        boolean exit = false;
        Message.Welcome.print();
        while(!exit) 
        {
            exit = processCommand(in.nextLine());
        }
        in.close();
    }

    private static boolean processCommand(String s) {
        String[] args = s.split(" ");
        if( args.length == 1 && args[0].equals("help"))
            Message.Help.print();
        else if(args.length == 2 && args[0].equals("compare")){
            IOManager io = new IOManager("compareall" + args[1]);
            DampedArmonicOscilator dao = new DampedArmonicOscilator();
            io.output = dao.simulate(5, Double.valueOf(args[1]));
            io.generateOutputFiles();
        }
        else if(args.length == 3 && args[0].equals("lennard")) {
            IOManager io = new IOManager("lennard");
            Message.SimulationRunning.print();
            LennardJonesGas ljg = new LennardJonesGas(Integer.valueOf(args[2]));
            ljg.simulate(Double.parseDouble(args[1]), io, 0.1, 0, null);
            Message.SimulationEnded.print();
        }
        else if(args.length == 3 && args[0].equals("lennard-2t")) {
            IOManager io = new IOManager("lennard-2t");
            IOManager io2 = new IOManager("lennard-2t-fr");
            Message.SimulationRunning.print();
            LennardJonesGas ljg = new LennardJonesGas(Integer.valueOf(args[2]));
            ljg.simulate2t(Double.parseDouble(args[1]), io, io2);
            Message.SimulationEnded.print();
        }
        else if(args.length == 4 && args[0].equals("lennard-t")) {
            IOManager io = new IOManager("lennard-t");
            IOManager io2 = new IOManager("lennard-t-fr");
            Message.SimulationRunning.print();
            LennardJonesGas ljg = new LennardJonesGas(Integer.valueOf(args[2]));
            ljg.simulateT(Double.parseDouble(args[1]), Double.parseDouble(args[3]), io, io2);
            Message.SimulationEnded.print();
        }
        else if(args.length == 3 && args[0].equals("energy")) {
            LennardJonesGas ljg = null;
            double dt = Double.valueOf(args[1]);
            IOManager io = new IOManager("energy" + dt);
            Message.SimulationRunning.print();
            double [][] energies = new double[60][5];
            for (int i = 0; i <  5; i++) {
                ljg = new LennardJonesGas(Integer.valueOf(args[2]));
                ljg.simulate(dt, io, 0.25, i, energies);
            }
            for (int i = 0; i < energies.length; i++) {
                double mean = getMean(energies[i]);
                double std = getStd(energies[i], mean);
                double t = ljg.SAVE_CYCLE * i * dt;
                io.output.append(t + " " + mean + " " + std + "\n");
            }
            io.handlePartialOutput();
            Message.SimulationEnded.print();
        }
        else if(args.length == 1 && args[0].equals("quadratic")){
            IOManager io = new IOManager("quadraticDifference");
            DampedArmonicOscilator dao = new DampedArmonicOscilator();
            io.output = dao.getQuadraticDifferences();
            io.generateOutputFiles();
        }
        else if(args.length == 1 && args[0].equals("exit"))
            return true;
        else 
            Message.InvalidParams.print();
        return false;
    }

    static private double getMean(double[] values) {
        double ret = 0;
        for (double val : values) {
            ret += val;
        }
        return ret / values.length;
    }

    static private double getStd(double[] values, double mean) {
        double ret = 0;
        for (double val : values) {
            ret += Math.pow(val - mean, 2);
        }
        return Math.sqrt(ret / values.length);
    }


}
