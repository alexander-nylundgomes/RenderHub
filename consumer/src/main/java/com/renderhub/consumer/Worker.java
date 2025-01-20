package com.renderhub.consumer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

public class Worker {
    

    private HashMap<String, Object> instructions;

    public Worker(HashMap<String, Object> instructions){
        this.instructions = instructions;
    }

    public boolean run(){
        try {                    
            Path inputPath = Path.of((String) instructions.get("inputPath"));
            Path outputPath = Path.of((String) instructions.get("outputPath"));
            String arguments = (String) instructions.get("arguments");
    
            Process process = executeRender(inputPath, outputPath, arguments);
            process.waitFor();

            final int result = process.exitValue();
            
            if(result != 0){
                throw new Exception("Image Magick failed with process result " + result + "!");
            }
        } catch (Exception e) {
            System.out.println("Worker failed processing image");
            System.out.println(e);
            
            return false;
        }

        return true;
    }

    private Process executeRender(Path inputPath, Path outputPath, String arguments) throws IOException{
        String command = "/usr/bin/convert " + inputPath.toString() + " " + arguments + " " + outputPath.toString();
        System.out.println("Executing the following command: " + command);
        Process proc = Runtime.getRuntime().exec(command);
        return proc;
    }

}
