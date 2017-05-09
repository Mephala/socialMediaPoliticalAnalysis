package com.gokhanozg.ptnla;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mephala on 5/9/17.
 */
public class SerializableMatrix {
    private Integer cols;
    private Integer rows;
    private List<List<Double>> entries;

    public SerializableMatrix() {
    }

    public SerializableMatrix(SimpleMatrix sm) {
        this.cols = sm.numCols();
        this.rows = sm.numRows();
        List<List<Double>> entries = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            List<Double> rowEntries = new ArrayList<>();
            for (int j = 0; j < cols; j++) {
                rowEntries.add(sm.get(i, j));
            }
            entries.add(rowEntries);
        }
        this.entries = entries;
    }

    public Integer getCols() {
        return cols;
    }

    public void setCols(Integer cols) {
        this.cols = cols;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public List<List<Double>> getEntries() {
        return entries;
    }

    public void setEntries(List<List<Double>> entries) {
        this.entries = entries;
    }

    public SimpleMatrix convertToSM() {
        SimpleMatrix s = new SimpleMatrix(rows, cols);
        for (int i = 0; i < entries.size(); i++) {
            List<Double> doubles = entries.get(i);
            double[] vals = new double[doubles.size()];
            for (int j = 0; j < doubles.size(); j++) {
                vals[j] = doubles.get(j);
            }
            s.setRow(i, 0, vals);
        }
        return s;
    }
}
