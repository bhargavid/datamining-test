package com.mstest.datamining.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import com.mstest.datamining.model.Axis;
import com.mstest.datamining.model.Graph;

public class FileUtil {    
    
    private static final String FS = " ";
    public static File createDatFile(Graph graph, String graph_name) throws IOException {
        File tmpFile = File.createTempFile(graph_name, ".dat");
        
        StringBuilder sb = new StringBuilder();
        sb.append(graph.getXAxis()).append(FS).append(graph.getY1Axis()).append(FS).append(graph.getY2Axis());
        
        for(Axis axis: graph.getAxisList()) {
            sb.append(axis.getX()).append(FS).append(axis.getY1()).append(FS).append(axis.getY2());
        }
        
        FileWriter fw = new FileWriter(tmpFile.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(sb.toString());
        bw.close();       
        
        return tmpFile;
    }
}
