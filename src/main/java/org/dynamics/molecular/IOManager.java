package org.dynamics.molecular;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.dynamics.molecular.Particle;

public final class IOManager
{
    public StringBuffer output;
    private String outputName;

    public IOManager(String fileName) { 
        output = new StringBuffer();
        
        try {
            File file = new File(fileName + Long.toString(System.currentTimeMillis()));
            outputName = file.getName();
            if (file.createNewFile()) {
                System.out.println("File was created.");
            } else {
                System.out.println("File was not created.");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void handlePartialOutput() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputName, true));
            writer.write(this.output.toString());
            writer.flush();
            writer.close();
            this.output = new StringBuffer();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(e.toString());
        }
    }

    public void generateOutputFiles() {
        try {        
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputName));
            writer.write(this.output.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(e.toString());
        }
    }

    public void generateGraphOutput(StringBuffer string, String variation) {
        try {
            File file = new File("var" + variation + Long.toString(System.currentTimeMillis()) + ".csv");
            if(file.createNewFile()) {
                System.out.println("File was created.");
            } else {
                System.out.println("File was not created.");
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputName));
            writer.write(string.toString());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(e.toString());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.out.println(e.toString());
        }
    }
}

