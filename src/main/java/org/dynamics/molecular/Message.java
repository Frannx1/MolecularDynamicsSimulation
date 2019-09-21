package org.dynamics.molecular;

import org.dynamics.molecular.interfaces.MessagePrinter;

public enum Message implements MessagePrinter {
    Welcome {
        public void print() {
            System.out.println("To get help write:\nhelp\n\n");
        }
    },
    Help {
        public void print() {
            System.out.println("To compare all the approach methods with a damped armonic oscilator:");
            System.out.println("compare [DT]");
            System.out.println("To get mean quadratic differences");
            System.out.println("quadratic");
            System.out.println("To simulate lennard jones:");
            System.out.println("lennard [DT] [number of particles]");
            System.out.println("To simulate lennard jones up to te, with a variation of the hole space:");
            System.out.println("lennard-t [DT] [number of particles] [hole space]");
            System.out.println("To simulate lennard jones up to two te:");
            System.out.println("lennard-2t [DT] [number of particles]");
            System.out.println("To simulate energy given a dt:");
            System.out.println("energy [DT] [number of particles]");
            System.out.println("To exit application: exit\n\n");
        }
    },
    SimulationRunning {
        public void print() {
            System.out.println("\nSimulation started running...");
        }
    },
    SimulationEnded {
        public void print() {
            System.out.println("\nSimulation finished.");
        }
    },
    InvalidParams {
        public void print() {
            System.out.println("\nParameters were not valid. To get help write:\nhelp\n\n");
        }
    }
    
}