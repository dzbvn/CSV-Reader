package com.company;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CSVReader {
    BufferedReader reader;
    String delimiter;
    boolean hasHeader;

    /**
     *
     * @param filename - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */

    public CSVReader(String filename,String delimiter,boolean hasHeader) throws IOException {
        /*reader = new BufferedReader(new FileReader(filename));
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();*/
        this(new FileReader(filename), delimiter, hasHeader);
    }

    public CSVReader(String filename, String delimeter) throws IOException {
        this(filename, delimeter, false);
    }
    public CSVReader(String filename) throws IOException {
        this(filename, ",");
    }

    public CSVReader(Reader reader, String delimiter, boolean hasHeader) throws IOException {
        this.reader = new BufferedReader(reader);
        this.delimiter = delimiter;
        this.hasHeader = hasHeader;
        if(hasHeader)parseHeader();
    }
    // nazwy kolumn w takiej kolejności, jak w pliku
    List<String> columnLabels = new ArrayList<>();
    // odwzorowanie: nazwa kolumny -> numer kolumny
    Map<String,Integer> columnLabelsToInt = new HashMap<>();

    // zwraca etykiety kolumn
    List<String> getColumnLabels(){
        return columnLabels;
    }

    int getRecordLength(){
        return current.length;
    }

    // jesli brakuje - true
    boolean isMissing(int columnIndex){
        return (columnIndex >= getRecordLength() || current[columnIndex] == null || current[columnIndex].isEmpty());
    }

    boolean isMissing(String columnLabel){
        //int i = columnLabelsToInt.get(columnLabel);

        return (columnLabelsToInt.get(columnLabel) == null ||
                columnLabelsToInt.get(columnLabel) >= getRecordLength() ||
                current[columnLabelsToInt.get(columnLabel)] == null ||
                columnLabelsToInt.get(columnLabel) >= getRecordLength() ||
                current[columnLabelsToInt.get(columnLabel)].isEmpty());
    }

    String get(int columnIndex){
        if (current[columnIndex] == null){
            return "";
        }
        return (current[columnIndex]);
    }

    String get(String columnLabel){
        if (current[columnLabelsToInt.get(columnLabel)] == null){
            return "";
        }
        return current[columnLabelsToInt.get(columnLabel)];
    }

    int getInt(int columnIndex) throws Exception {
        if (isMissing(columnIndex)){
            throw new Exception("columnIndex is empty");
        }
        return Integer.parseInt(current[columnIndex]);
    }

    int getInt(String columnLabel) throws Exception {
        if (isMissing(columnLabel)){
            throw new Exception("columnIndex is empty");
        }
        return Integer.parseInt(current[columnLabelsToInt.get(columnLabel)]);
    }

    long getLong(int columnIndex) throws Exception {
        if (isMissing(columnIndex)){
            throw new Exception("columnIndex is empty");
        }
        return Long.parseLong(current[columnIndex]);
    }

    long getLong(String columnLabel) throws Exception {
        if (isMissing(columnLabel)){
            throw new Exception("columnIndex is empty");
        }
        return Long.parseLong(current[columnLabelsToInt.get(columnLabel)]);
    }

    double getDouble(int columnIndex) throws Exception {
        if (isMissing(columnIndex)){
            throw new Exception("columnIndex is empty");
        }
        return Double.parseDouble(current[columnIndex]);
    }

    double getDouble(String columnLabel) throws Exception {
        if (isMissing(columnLabel)){
            throw new Exception("columnIndex is empty");

        }
        return Double.parseDouble(current[columnLabelsToInt.get(columnLabel)]);
    }

    LocalTime getTime(int columnIndex, String format){
        return LocalTime.parse(current[columnIndex], DateTimeFormatter.ofPattern(format));
    }

    LocalTime getTime(String columnLabel, String format){
        return LocalTime.parse(current[columnLabelsToInt.get(columnLabel)], DateTimeFormatter.ofPattern(format));
    }

    LocalDate getDate(int columnIndex, String format){
        return LocalDate.parse(current[columnIndex], DateTimeFormatter.ofPattern(format));
    }

    LocalDate getDate(String columnLabel, String format){
        return LocalDate.parse(current[columnLabelsToInt.get(columnLabel)], DateTimeFormatter.ofPattern(format));
    }

    LocalDateTime getDateTime(int columnIndex, String format){
        return LocalDateTime.parse(current[columnIndex], DateTimeFormatter.ofPattern(format));
    }

    LocalDateTime getDateTime(String columnLabel, String format){
        return LocalDateTime.parse(current[columnLabelsToInt.get(columnLabel)], DateTimeFormatter.ofPattern(format));
    }



    void parseHeader() throws IOException {
        // wczytaj wiersz

        String line = reader.readLine();
        if (line == null) {
            return;
        }
        // podziel na pola
        String[] header = line.split(delimiter);
        // przetwarzaj dane w wierszu
        for (int i = 0; i < header.length; i++) {
            // dodaj nazwy kolumn do columnLabels i numery do columnLabelsToInt
            columnLabels.add(header[i]);
            columnLabelsToInt.put(header[i], i);
        }
    }
    String[]current;
    boolean next() {
        // czyta następny wiersz, dzieli na elementy i przypisuje do current
        //

        String tmp;
        try {
            tmp = reader.readLine();
        } catch (IOException e) {
            return false;
        }
        if(tmp == null){
            return false;
        }

        //current = tmp.split(delimiter);

        //tmp = tmp.replaceAll(delimiter, ",");


        //tmp = tmp.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
        tmp = tmp.replaceAll("\"(.+)" + delimiter + "(.+)\"", "\"$1!delimiter!$2\"");
        //System.out.print(tmp);
        current = tmp.split(delimiter);

        for(int i = 0; i < getRecordLength(); i++){
            current[i] = current[i].replaceAll("!delimiter!", delimiter);
        }

        return true;
    }

}
