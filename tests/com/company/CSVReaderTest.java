package com.company;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class CSVReaderTest {

    @Test
    void get() throws Exception {
        CSVReader reader = new CSVReader("no-header.csv",";",false);
        while(reader.next()) {
            String id;
            try {
                id = reader.get(7);
            }catch (Exception e){
                assertTrue(true);
            }
        }
    }

    @Test
    void testGet() throws IOException {
        CSVReader reader = new CSVReader("no-header.csv",";",false);

        while(reader.next()) {
            String id;
            try {
                id = reader.get("kolumna");
            }catch (Exception e){
                assertTrue(true);
            }
        }
    }

    @Test
    void getInt() throws IOException {
        CSVReader reader = new CSVReader("no-header.csv",";",false);

        while(reader.next()) {
            int id;
            try {
                id = reader.getInt(62);
            }catch (Exception e){
                assertTrue(true);
            }
        }
    }

    @Test
    void testGetInt() throws IOException {
        CSVReader reader = new CSVReader("no-header.csv",";",false);

        while(reader.next()) {
            int id;
            try {
                id = reader.getInt("kolumna");
            }catch (Exception e){
                assertTrue(true);
            }
        }
    }

    @Test
    void getDouble() throws IOException {
        CSVReader reader = new CSVReader("no-header.csv",";",false);

        while(reader.next()) {
            double test;
            try {
                test = reader.getDouble(367);
            }catch (Exception e){
                assertTrue(true);
            }
        }
    }

    @Test
    void testGetDouble() throws IOException {
        CSVReader reader = new CSVReader("no-header.csv",";",false);

        while(reader.next()) {
            double test;
            try {
                test = reader.getDouble("kolumna");
            }catch (Exception e){
                assertTrue(true);
            }
        }
    }
}