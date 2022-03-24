package com.company;

import java.io.PrintStream;
import java.text.Collator;
import java.util.*;
import java.util.function.Predicate;

public class AdminUnitList {
    List<AdminUnit> units = new ArrayList<>();

    public void read(String filename) throws Exception {
        CSVReader reader = new CSVReader(filename, ",", true);
        Map<Long,AdminUnit> idMap = new HashMap<>();
        Map<AdminUnit,Long> parentMap = new HashMap<>();
        Map<Long,List<AdminUnit>> parentid2child = new HashMap<>();
        int n = 0;
        while(reader.next()){
            AdminUnit tempUnit = new AdminUnit();
            tempUnit.name = reader.get("name");
            if(reader.isMissing("population")){
                tempUnit.population = 0;
            }
            else{
                tempUnit.population = reader.getDouble("population");
            }

            if(reader.isMissing("admin_level")){
                tempUnit.adminLevel = -1;
            }
            else{
                tempUnit.adminLevel = reader.getInt("admin_level");
            }

            if(reader.isMissing("area")){

                tempUnit.area = 0;
            }
            else{
                tempUnit.area = reader.getDouble("area");
            }

            if(reader.isMissing("density")){
                tempUnit.density = 0;
            }
            else{
                tempUnit.density = reader.getDouble("density");
            }

            double[] coords = new double[8];
            for(int i = 0; i < 4; i++){
                if(!reader.isMissing("x" + (i + 1))) {
                    coords[i * 2] = reader.getDouble("x" + (i + 1));

                    if(!reader.isMissing("y" + (i + 1))) {
                        coords[i * 2 + 1] = reader.getDouble("y" + (i + 1));
                    }
                }
            }
            tempUnit.bbox.set(coords);

            Long id = reader.getLong("id");
            Long parentid = null;
            if(!reader.isMissing("parent")){
                //System.out.print("XD");
                parentid = reader.getLong("parent");
            }
            else if (reader.isMissing("parent") && tempUnit.name.contains("województwo")){
                parentid = 0L;
            }
            idMap.put(id, tempUnit);
            parentMap.put(tempUnit, parentid);
            //fixMissingValues(tempUnit);
            units.add(tempUnit);
            if (parentid2child.get(parentid) == null) {
                List<AdminUnit> t = new ArrayList<AdminUnit>();
                parentid2child.put(parentid, t);
            }
            parentid2child.get(parentid).add(tempUnit);
        }
        for(AdminUnit u : units){
            if(parentMap.containsKey(u)){
                u.parent = idMap.get(parentMap.get(u));

            }
            else{
                u.parent = null;
            }
        }
        for (Map.Entry<Long, AdminUnit> entry : idMap.entrySet()) {
            entry.getValue().children = parentid2child.get(entry.getKey());
        }
        for(AdminUnit u : units){
            fixMissingValues(u);
        }
    }

    /**
     * Wypisuje zawartość korzystając z AdminUnit.toString()
     * @param out
     */
    void list(PrintStream out){
        for(AdminUnit u : units){
            out.print(u.toString());
            out.print("\n");
        }
    }

    void listNeighbours(PrintStream out, AdminUnit u, int maxdistance){
        for(AdminUnit un : getNeighbours(u, maxdistance).units){
            out.print(un.toString());
            out.print("\n");
        }
    }

    void listNeighboursRTree(PrintStream out, AdminUnit u, int maxdistance){
        for(AdminUnit un : getNeighboursRTree2(u, maxdistance).units){
            out.print(un.toString());
            out.print("\n");
        }
    }
    void listNeighboursRTree2(PrintStream out, AdminUnit u, int maxdistance){

        for(AdminUnit un : rTreeSearch(u, maxdistance).units){
            out.print(un.toString());
            out.print("\n");
        }
    }

    /**
     * Sortowania
     */

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByName() {
        class MyComparator
            implements Comparator<AdminUnit>
        {
            @Override
            public int compare(AdminUnit o1, AdminUnit o2) {
                //wazne, uwzglednia polskie znaki
                Collator c = Collator.getInstance(new Locale("pl", "PL"));
                return c.compare(o1.name, o2.name);
            }
        }
        MyComparator comp = new MyComparator();
        this.units.sort(comp);
        return this;
    }
    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByArea(){
        var cmp = new Comparator<AdminUnit>(){
            @Override
            public int compare(AdminUnit o1, AdminUnit o2) {
                return Double.compare(o1.area, o2.area);
            }
        };
        this.units.sort(cmp);
        return this;
    }

    /**
     * Sortuje daną listę jednostek (in place = w miejscu)
     * @return this
     */
    AdminUnitList sortInplaceByPopulation(){
        this.units.sort(((o1, o2) -> Double.compare(o1.population, o2.population)));
        return this;
    }

    AdminUnitList sortInplace(Comparator<AdminUnit> cmp){
        this.units.sort(cmp);
        return this;
    }

    // Tworzy wyjściową listę
    // Kopiuje wszystkie jednostki
    // woła sortInPlace
    AdminUnitList sort(Comparator<AdminUnit> cmp){
        AdminUnitList sortedList = new AdminUnitList();

        for(AdminUnit u : this.units){
            sortedList.units.add(u);
        }

        sortedList.sortInplace(cmp);
        return sortedList;
    }

    /**
     *
     * @param pred referencja do interfejsu Predicate
     * @return nową listę, na której pozostawiono tylko te jednostki,
     * dla których metoda test() zwraca true
     */
    AdminUnitList filter(Predicate<AdminUnit> pred){
        AdminUnitList filteredList = new AdminUnitList();

        for(AdminUnit u : this.units){
            if(pred.test(u)) {
                filteredList.units.add(u);
            }
        }
        return filteredList;
    }


    /**
     * Zwraca co najwyżej limit elementów spełniających pred
     * @param pred - predykat
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int limit){
        AdminUnitList filteredList = new AdminUnitList();

        for(AdminUnit u : this.units){
            if(pred.test(u)) {
                filteredList.units.add(u);
            }
        }
        filteredList.units = filteredList.units.subList(0, limit);
        return filteredList;
    }

    /**
     * Zwraca co najwyżej limit elementów spełniających pred począwszy od offset
     * Offest jest obliczany po przefiltrowaniu
     * @param pred - predykat
     * @param - od którego elementu
     * @param limit - maksymalna liczba elementów
     * @return nową listę
     */
    AdminUnitList filter(Predicate<AdminUnit> pred, int offset, int limit){
        AdminUnitList filteredList = new AdminUnitList();

        for(AdminUnit u : this.units){
            if(pred.test(u)) {
                filteredList.units.add(u);
            }
        }

        filteredList.units = filteredList.units.subList(offset, offset + limit);
        return filteredList;
    }



    /**
     * Wypisuje co najwyżej limit elementów począwszy od elementu o indeksie offset
     * @param out - strumień wyjsciowy
     * @param offset - od którego elementu rozpocząć wypisywanie
     * @param limit - ile (maksymalnie) elementów wypisać
     */
    void list(PrintStream out,int offset, int limit ){
        if(offset + limit < units.size()) {
            for (int i = offset; i < offset + limit; i++) {
                out.print(units.get(i).toString());
            }
        }
    }

    /**
     * Zwraca nową listę zawierającą te obiekty AdminUnit, których nazwa pasuje do wzorca
     * @param pattern - wzorzec dla nazwy
     * @param regex - jeśli regex=true, użyj finkcji String matches(); jeśli false użyj funkcji contains()
     * @return podzbiór elementów, których nazwy spełniają kryterium wyboru
     */
    AdminUnitList selectByName(String pattern, boolean regex){
        AdminUnitList ret = new AdminUnitList();
        // przeiteruj po zawartości units
        // jeżeli nazwa jednostki pasuje do wzorca dodaj do ret
        for(AdminUnit u : units){
            if(regex){
                if(u.toString().matches(pattern)){
                    ret.units.add(u);
                }
            }
            else {
                if(u.toString().contains(pattern)){
                    ret.units.add(u);
                }
            }
        }
        return ret;
    }
    private void fixMissingValues(){
        for(AdminUnit u : units){
            fixMissingValues(u);
        }
    }
    public void fixMissingValues(AdminUnit au){
        if (au.parent == null){
            return;
        }
        if (au.density > 0 && au.population > 0){
            return;
        }
        if (au.area <= 0){
            return;
        }
        fixMissingValues(au.parent);
        au.density = au.parent.density;
        au.population = au.area * au.density;
    }
    /**
     * Zwraca listę jednostek sąsiadujących z jednostką unit na tym samym poziomie hierarchii admin_level.
     * Czyli sąsiadami wojweództw są województwa, powiatów - powiaty, gmin - gminy, miejscowości - inne miejscowości
     * @param unit - jednostka, której sąsiedzi mają być wyznaczeni
     * @param maxdistance - parametr stosowany wyłącznie dla miejscowości, maksymalny promień odległości od środka unit,
     *                    w którym mają sie znaleźć punkty środkowe BoundingBox sąsiadów
     * @return lista wypełniona sąsiadami
     */
    AdminUnitList getNeighbours(AdminUnit unit, double maxdistance){
        AdminUnitList neighbours = new AdminUnitList();
        if(unit.adminLevel != 8) {
            for(AdminUnit u : units) {
                if (u.adminLevel == unit.adminLevel && u.bbox.intersects(unit.bbox) && u != unit) {
                    neighbours.units.add(u);
                }
            }
        }
        else{
            for(AdminUnit u : units) {
                if(unit.bbox.distanceTo(u.bbox) < maxdistance && u.adminLevel == unit.adminLevel && u != unit){
                    neighbours.units.add(u);
                }
            }
        }
        return neighbours;
    }


    //wersja szukania  sasiadow z rekurencja
    AdminUnitList rTreeSearch(AdminUnit unit, double maxdistance){
        AdminUnit root = getRoot(unit); //znajduje korzen
        AdminUnitList rootNeighbours = getRootNeighbours(root);
        if(unit.adminLevel == 4){
            return rootNeighbours;
        }
        rootNeighbours.units.add(root);
        AdminUnitList potentialNeighbours = new AdminUnitList();
        AdminUnitList neighbours = new AdminUnitList();
        for(AdminUnit rootN : rootNeighbours.units){
            potentialNeighbours = rTree(unit, potentialNeighbours, rootN);
        }

        for(AdminUnit pUnit : potentialNeighbours.units){
            if(pUnit.adminLevel == unit.adminLevel){
                if(unit.adminLevel != 8){
                    if (pUnit.bbox.intersects(unit.bbox) && pUnit != unit){
                        neighbours.units.add(pUnit);
                    }
                }
                else{
                    if(unit.bbox.distanceTo(pUnit.bbox) < maxdistance  && pUnit != unit){
                        neighbours.units.add(pUnit);
                    }
                }
            }
        }
        return neighbours;
    }

    AdminUnitList rTree(AdminUnit unit, AdminUnitList potentialNeighbours, AdminUnit root){
        if(root.children == null){
            return potentialNeighbours;
        }
        if(root.children.get(0).adminLevel > unit.adminLevel){
            return potentialNeighbours;
        }
        potentialNeighbours.units.addAll(root.children);
        for(AdminUnit c : root.children){
            rTree(unit, potentialNeighbours, c);
        }
        return potentialNeighbours;
    }


    AdminUnit getRoot(AdminUnit u){
        if(u.parent == null){
            return u;
        }
        return getRoot(u.parent);
    }

    //druga wersja sposobu z R-Tree, wykorzytuje parent->child
    AdminUnitList getNeighboursRTree2(AdminUnit unit, double maxdistance){
        AdminUnit root =  getRoot(unit);
        AdminUnitList rootNeighbours = getNeighbours(root, maxdistance);
        rootNeighbours.units.add(root);
        AdminUnitList neighbours = new AdminUnitList();

        while(true){
            if(rootNeighbours.units.size() > 0 && rootNeighbours.units.get(0).adminLevel == unit.adminLevel){
                break;
            }
            else{
                AdminUnitList temp = new AdminUnitList();
                for(AdminUnit u : rootNeighbours.units){
                    temp.units.add(u);
                }
                rootNeighbours.units.clear();
                for(int i = 0; i < temp.units.size(); i++){
                    if(temp.units.get(i).children != null) {
                        rootNeighbours.units.addAll(temp.units.get(i).children);
                    }
                }
            }
        }
        if(unit.adminLevel != 8) {
            for(AdminUnit u : rootNeighbours.units) {
                if (u.adminLevel == unit.adminLevel && u.bbox.intersects(unit.bbox) && u != unit) {
                    neighbours.units.add(u);
                }
            }
        }
        else{
            for(AdminUnit u : rootNeighbours.units) {
                if(unit.bbox.distanceTo(u.bbox) < maxdistance && u.adminLevel == unit.adminLevel && u != unit){
                    neighbours.units.add(u);
                }
            }
        }
        return neighbours;
    }

    AdminUnitList getRootNeighbours(AdminUnit unit){
        AdminUnitList neighbours = new AdminUnitList();
        for(AdminUnit u : units) {
            if (u.adminLevel == unit.adminLevel && u.bbox.intersects(unit.bbox) && u != unit) {
                neighbours.units.add(u);
            }
        }
        return neighbours;
    }
    /*
    AdminUnitList getNeighboursRTree(AdminUnit unit, double maxdistance){

        if(unit.parent == null){
            AdminUnitList t = new AdminUnitList();
            for(AdminUnit u : getNeighbours(unit, maxdistance).units){
                t.units.add(u);
            }
            return t;
        }
        AdminUnitList neighbours = new AdminUnitList();
        AdminUnitList rNeighbours = new AdminUnitList();
        if(unit.adminLevel != 8) {

            for(AdminUnit u : getNeighboursRTree(unit.parent, maxdistance).units){
                rNeighbours.units.addAll(u.children);
            }
            for(AdminUnit c : rNeighbours.units) {
                if (c.adminLevel == unit.adminLevel && c.bbox.intersects(unit.bbox) && c != unit) {
                    neighbours.units.add(c);
                }
            }
        }
        else{
            for(AdminUnit u : getNeighboursRTree(unit.parent, maxdistance).units){
                rNeighbours.units.addAll(u.children);
            }
            for(AdminUnit c : rNeighbours.units) {
                if(unit.bbox.distanceTo(c.bbox) < maxdistance && c.adminLevel == unit.adminLevel && c != unit){
                    neighbours.units.add(c);
                }
            }
        }
        return neighbours;
    }*/


}
