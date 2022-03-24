package com.company;

import java.io.PrintStream;
import java.util.Locale;

public class BoundingBox {
    double xmin = Double.NaN;
    double ymin = Double.NaN;
    double xmax = Double.NaN;
    double ymax = Double.NaN;

    /**
     * Powiększa BB tak, aby zawierał punkt (x,y)
     * @paramx - współrzędna x
     * @paramy - współrzędna y
     *
     *
     *
     *
     *          xmin, ymax             xmax, ymax
     *
     *
     *
     *          xmin, ymin             xmax, ymin
     *
     */

    void set(double[] coords){
        double minX = coords[0];
        double minY = coords[1];
        double maxX = coords[0];
        double maxY = coords[1];
        for(int i = 0; i < coords.length/2; i++){
            if(coords[i*2] < minX){
                minX = coords[i*2];
            }
            if(coords[i*2] > maxX){
                maxX = coords[i*2];
            }
            if(coords[i*2+1] < minY){
                minY = coords[i*2+1];
            }
            if(coords[i*2+1] > maxY){
                maxY = coords[i*2+1];
            }
        }
        this.xmin = minX;
        this.xmax = maxX;
        this.ymin = minY;
        this.ymax = maxY;
    }

    public void getWKT(PrintStream out){
        out.printf(Locale.US, "LINESTRING(%f %f, %f %f, %f %f, %f %f)\n", xmin, ymin, xmin, ymax, xmax, ymax, xmax, ymin);
    }
    public String toString(){
        return "xmin=" + this.xmin +
                ", ymin=" + this.ymin +
                ", xmax=" + this.xmax +
                ", ymax=" + this.ymax;
    }
    void addPoint(double x, double y){
        if(this.isEmpty()){
            this.xmin = x;
            this.xmax = x;
            this.ymax = y;
            this.ymin = y;
        }
        if (x > xmax){
            xmax = x;
        }
        else{
            xmin = x;
        }
        if (y > ymax){
            ymax = y;
        }
        else{
            ymin = y;
        }

    }

    /**
     * Sprawdza, czy BB zawiera punkt (x,y)
     * @param x
     * @param y
     * @return
     */
    boolean contains(double x, double y){
        return x <= xmax && x >= xmin && y <= ymax && y >= ymin;
    }

    /**
     * Sprawdza czy dany BB zawiera bb
     * @param bb
     * @return
     */
    boolean contains(BoundingBox bb){
        return bb.xmax <= xmax && bb.xmin >= xmin && bb.ymax <= ymax && bb.ymin >= ymin;
    }

    /**
     * Sprawdza, czy dany BB przecina się z bb
     * @param bb
     * @return
     */
    boolean intersects(BoundingBox bb){
        return !(this.xmin > bb.xmax || this.xmax < bb.xmin || this.ymin > bb.ymax || this.ymax < bb.ymin);
    }
    /**
     * Powiększa rozmiary tak, aby zawierał bb oraz poprzednią wersję this
     * @param bb
     * @return
     */
    BoundingBox add(BoundingBox bb){
        if(bb.xmax > this.xmax){
            this.xmax = bb.xmax;
        }
        if(bb.ymax > this.ymax){
            this.ymax = bb.ymax;
        }
        if(bb.xmin < this.xmin){
            this.xmin = bb.xmin;
        }
        if(bb.ymin < this.ymin){
            this.ymin = bb.ymin;
        }
        return this;
    }
    /**
     * Sprawdza czy BB jest pusty
     * @return
     */
    boolean isEmpty(){
        return (Double.isNaN(ymax) && Double.isNaN(ymin) && Double.isNaN(xmax) && Double.isNaN(xmin));
    }

    /**
     * Oblicza i zwraca współrzędną x środka
     * @return if !isEmpty() współrzędna x środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterX(){
        if (this.isEmpty()){
            throw new RuntimeException("Bounding Box empty");
        }
        else{
            return this.xmin + ((this.xmax - this.xmin)/2);
        }
    }
    /**
     * Oblicza i zwraca współrzędną y środka
     * @return if !isEmpty() współrzędna y środka else wyrzuca wyjątek
     * (sam dobierz typ)
     */
    double getCenterY(){
        if (this.isEmpty()){
            throw new RuntimeException("Bounding Box empty");
        }
        else{
            return this.ymin + ((this.ymax - this.ymin)/2);
        }
    }

    /**
     * Oblicza odległość pomiędzy środkami this bounding box oraz bbx
     * @param bbx prostokąt, do którego liczona jest odległość
     * @return if !isEmpty odległość, else wyrzuca wyjątek lub zwraca maksymalną możliwą wartość double
     * Ze względu na to, że są to współrzędne geograficzne, zamiast odległości użyj wzoru haversine
     * (ang. haversine formula)
     *
     * Gotowy kod można znaleźć w Internecie...
     */
    double distanceTo(BoundingBox bbx){
        double R = 6372.8;
        double lat1 = this.getCenterY();
        double lon1 = this.getCenterX();
        double lat2 = bbx.getCenterY();
        double lon2 = bbx.getCenterX();

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
