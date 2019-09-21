package org.dynamics.molecular;

import org.dynamics.molecular.interfaces.Approacher;

public enum ApproachMethod implements Approacher {

    GEAR_PREDICTOR_CORRECTOR_ORDER_5{

        @Override
        public StringBuffer getApproachValue(double tMax, double dt, boolean compareToOscilator) {
            StringBuffer sb = new StringBuffer("");
            DampedArmonicOscilator dao = new DampedArmonicOscilator();
            int j = 0;
            double actualForce, dR2, da;
            double [] x = new double[6];
            double [] fact = new double[] {1.0, 1.0, 2.0, 6.0,  24.0, 120.0};
            double [] dtExp = new double[6];
            dtExp[0] = 1.0;
            dtExp[1] = dt;
            dtExp[2] = dt * dt;
            dtExp[3] = Math.pow(dt, 3);
            dtExp[4] = Math.pow(dt, 4);
            dtExp[5] = Math.pow(dt, 5);
            double [] alphas = new double[] {3/16.0, 251/360.0, 1.0, 11/18.0, 1/6.0, 1/60.0};
            x[0] = dao.getPositionForCertainTime(0);
            x[1] = dao.getStartingVelocity();
            x[2] = dao.getStartingForce() / dao.getMass();
            x[3] = -(dao.getK() / dao.getMass()) * x[1];
            x[4] = -(dao.getK() / dao.getMass()) * x[2];
            x[5] = -(dao.getK() / dao.getMass()) * x[3];
            sb.append(x[0] + " ");
            for(double i = dt; i <= tMax; i+=dt) {
                // calculate next step
                x[0] = x[0] + x[1] * dt + x[2] * (dtExp[2] / fact[2]) + x[3] * (dtExp[3] / fact[3])
                    + x[4] * (dtExp[4] / fact[4]) + x[5] * (dtExp[5] / fact[5]);
                x[1] = x[1] + x[2] * dt + x[3] * (dtExp[2] / fact[2]) + x[4] * (dtExp[3] / fact[3]) 
                    + x[5] * (dtExp[4] / fact[4]);
                x[2] = x[2] + x[3] * dt + x[4] * (dtExp[2] / fact[2]) + x[5] * (dtExp[3] / fact[3]);
                x[3] = x[3] + x[4] * dt + x[5] * (dtExp[2] / fact[2]);
                x[4] = x[4] + x[5] * dt;
                x[5] = x[5];
                // calculate dR2
                actualForce = dao.getForce(x[1], x[0]);
                da = actualForce / dao.getMass() - x[2];
                dR2 = da * (dtExp[2]) / fact[2];
                // calculate corrections
                j = 0;
                while (j < x.length) {
                    x[j] = x[j] + (alphas[j] * dR2 * (fact[j] / dtExp[j]));
                    j++;
                }
                sb.append(x[0] + " ");
                

            }
            sb.append("\n");
            return sb;
        }

        @Override
        public double meanQuadraticDifference(double tMax, double dt) {
            DampedArmonicOscilator dao = new DampedArmonicOscilator();
            double meanQuadraticDifference = 0.0;
            int j = 0;
            double actualForce, dR2, da;
            double [] x = new double[6];
            double [] fact = new double[] {1.0, 1.0, 2.0, 6.0,  24.0, 120.0};
            double [] dtExp = new double[6];
            dtExp[0] = 1.0;
            dtExp[1] = dt;
            dtExp[2] = dt * dt;
            dtExp[3] = Math.pow(dt, 3);
            dtExp[4] = Math.pow(dt, 4);
            dtExp[5] = Math.pow(dt, 5);
            double [] alphas = new double[] {(3/16.0), (251/360.0), 1.0, (11/18.0), (1/6.0), (1/60.0)};
            x[0] =  dao.getPositionForCertainTime(0);
            x[1] = dao.getStartingVelocity();
            x[2] = dao.getStartingForce() / dao.getMass();
            x[3] = -(dao.getK() / dao.getMass()) * x[1];
            x[4] = -(dao.getK() / dao.getMass()) * x[2];
            x[5] = -(dao.getK() / dao.getMass()) * x[3];
            for(double i = dt; i <= tMax; i+=dt) {
                // calculate next step
                x[0] = x[0] + x[1] * dt + x[2] * (dtExp[2] / fact[2]) + x[3] * (dtExp[3] / fact[3])
                        + x[4] * (dtExp[4] / fact[4]) + x[5] * (dtExp[5] / fact[5]);
                x[1] = x[1] + x[2] * dt + x[3] * (dtExp[2] / fact[2]) + x[4] * (dtExp[3] / fact[3])
                        + x[5] * (dtExp[4] / fact[4]);
                x[2] = x[2] + x[3] * dt + x[4] * (dtExp[2] / fact[2]) + x[5] * (dtExp[3] / fact[3]);
                x[3] = x[3] + x[4] * dt + x[5] * (dtExp[2] / fact[2]);
                x[4] = x[4] + x[5] * dt;
                x[5] = x[5];
                // calculate dR2
                actualForce = dao.getForce(x[1], x[0]);
                da = actualForce / dao.getMass() - x[2];
                dR2 = da * (dtExp[2]) / fact[2];
                // calculate corrections
                j = 0;
                while (j < x.length) {
                    x[j] = x[j] + (alphas[j] * dR2 * (fact[j] / dtExp[j]));
                    j++;
                }

                meanQuadraticDifference += Math.pow(x[0] - dao.getPositionForCertainTime(i), 2);
            }
            return meanQuadraticDifference / (tMax/dt + 1);
        }

    }, BEEMAN{
        @Override
        public StringBuffer getApproachValue(double tMax, double dt, boolean compareToOscilator) {
            if(compareToOscilator){
                return getApproachValueToOscilator(tMax, dt);
            }
            return null;
        }

        @Override
        public double meanQuadraticDifference(double tMax, double dt) {
            double meanQuadraticDifference = 0.0;
            DampedArmonicOscilator dao = new DampedArmonicOscilator();
            double actualPosition = dao.getPositionForCertainTime(0);
            double actualVelocity = dao.getStartingVelocity();
            double actualForce = dao.getStartingForce();
            double lastForce = dao.getForce(dao.getPreviousStartingVelocity(dt), dao.getPreviousStartingPosition(dt));
            for(double i = 0; i <= tMax; i+=dt){
                actualPosition += actualVelocity * dt + 2 * Math.pow(dt,2)*actualForce/(3*dao.getMass()) - Math.pow(dt,2) * lastForce/(6*dao.getMass());
                double predictedVelocity = actualVelocity + 3 * actualForce * dt / (2*dao.getMass()) - lastForce * dt/(6*dao.getMass());
                double auxForce = dao.getForce(predictedVelocity, actualPosition);
                actualVelocity += auxForce * dt / (3*dao.getMass()) + 5 * actualForce * dt / (6*dao.getMass()) - lastForce * dt / (6 * dao.getMass());
                lastForce = actualForce;
                actualForce = dao.getForce(actualVelocity, actualPosition);
                meanQuadraticDifference += Math.pow(actualPosition - dao.getPositionForCertainTime(i), 2);
            }
            return meanQuadraticDifference / (tMax/dt + 1);
        }

        private StringBuffer getApproachValueToOscilator(double tMax, double dt){
            StringBuffer sb = new StringBuffer("");
            DampedArmonicOscilator dao = new DampedArmonicOscilator();
            double actualPosition = dao.getPositionForCertainTime(0);
            double actualVelocity = dao.getStartingVelocity();
            double actualForce = dao.getStartingForce();
            double lastForce = dao.getForce(dao.getPreviousStartingVelocity(dt), dao.getPreviousStartingPosition(dt));
            sb.append(actualPosition + " ");
            for(double i = 0; i <= tMax; i+=dt){
                actualPosition += actualVelocity * dt + 2 * Math.pow(dt,2)*actualForce/(3*dao.getMass()) - Math.pow(dt,2) * lastForce/(6*dao.getMass());
                double predictedVelocity = actualVelocity + 3 * actualForce * dt / (2*dao.getMass()) - lastForce * dt/(6*dao.getMass());
                double auxForce = dao.getForce(predictedVelocity, actualPosition);
                actualVelocity += auxForce * dt / (3*dao.getMass()) + 5 * actualForce * dt / (6*dao.getMass()) - lastForce * dt / (6 * dao.getMass());
                lastForce = actualForce;
                actualForce = dao.getForce(actualVelocity, actualPosition);
                sb.append(actualPosition + " ");
            }
            sb.append("\n");
            return sb;
        }

    }, VERLET{
        @Override
        public StringBuffer getApproachValue(double tMax, double dt, boolean compareToOscilator) {
            if(compareToOscilator){
                return getApproachValueToOscilator(tMax, dt);
            }
            return null;
        }

        @Override
        public double meanQuadraticDifference(double tMax, double dt) {
            double meanQuadraticDifference = 0.0;
            DampedArmonicOscilator dao = new DampedArmonicOscilator();
            double lastPosition = dao.getPreviousStartingPosition(dt);
            double actualPosition = dao.getPositionForCertainTime(0);
            double actualForce = dao.getStartingForce();
            double actualVelocity = dao.getStartingVelocity();
            for (double i = 0; i <= tMax; i+=dt){
                double aux = 2*actualPosition - lastPosition + Math.pow(dt, 2) * actualForce/dao.getMass();
                if (i >= dt*2) {
                    /**
                     * Como no puedo utilizar este algoritmo no es valido para fuerzas que dependen de la posicion uso la
                     * ultima velocidad disponible para calcular la fuerza
                     */
                    actualVelocity = 0.5 * (aux - lastPosition) / dt;
                }
                actualForce = dao.getForce(actualVelocity, aux);
                lastPosition = actualPosition;
                actualPosition = aux;
                meanQuadraticDifference += Math.pow(actualPosition - dao.getPositionForCertainTime(i), 2);
            }
            return meanQuadraticDifference / (tMax/dt + 1);
        }

        private StringBuffer getApproachValueToOscilator(double tMax, double dt){
            StringBuffer sb = new StringBuffer("");
            DampedArmonicOscilator dao = new DampedArmonicOscilator();
            double lastPosition = dao.getPreviousStartingPosition(dt);
            double actualPosition = dao.getPositionForCertainTime(0);
            double actualForce = dao.getStartingForce();
            double actualVelocity = dao.getStartingVelocity();
            sb.append(actualPosition + " ");
            for (double i = 0; i <= tMax; i+=dt){
                double aux = 2*actualPosition - lastPosition + Math.pow(dt, 2) * actualForce/dao.getMass();
                if (i >= dt*2) {
                    /**
                     * Como no puedo utilizar este algoritmo no es valido para fuerzas que dependen de la posicion uso la
                     * ultima velocidad disponible para calcular la fuerza
                     */
                    actualVelocity = 0.5 * (aux - lastPosition) / dt;
                }
                actualForce = dao.getForce(actualVelocity, aux);
                lastPosition = actualPosition;
                actualPosition = aux;
                sb.append(actualPosition + " ");
            }
            sb.append("\n");
            return sb;
        }

    }

}
