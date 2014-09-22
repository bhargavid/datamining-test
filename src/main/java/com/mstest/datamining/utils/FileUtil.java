package com.mstest.datamining.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.FileChannel;

import com.mstest.datamining.model.Axis;
import com.mstest.datamining.model.Graph;

public class FileUtil {    
    
    private static final String FS = " ";
    private static final String NEW_LINE = "\n";
    
    public static File createDatFile(Graph graph, String graph_name) throws IOException {
        File tmpFile = File.createTempFile(graph_name, ".dat");
        
        StringBuilder sb = new StringBuilder();
        //sb.append(graph.getXAxis()).append(FS).append(graph.getY1Axis()).append(FS).append(graph.getY2Axis()).append(NEW_LINE);
        
        for(Axis axis: graph.getAxisList()) {
            sb.append(axis.getX()).append(FS).append(axis.getY1()).append(FS).append(axis.getY2()).append(NEW_LINE);
        }
        
        FileWriter fw = new FileWriter(tmpFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(sb.toString());
        bw.close();       
        
        return tmpFile;
    }
    
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if(!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
    }
}
