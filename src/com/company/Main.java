package com.company;

import java.io.*;
import java.time.LocalTime;
import java.util.Locale;

public class Main {
    //najprostsza i najwolniejsza metoda wyszukiwania sasiadow
    static void neighboursTest() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");
        //AUList.list(System.out);

        for(AdminUnit u : AUList.selectByName("Tarnowskie Góry", false).units){
            u.bbox.getWKT(System.out);
            System.out.println("---SASIEDZI---" + u.name + "---");
            AUList.listNeighbours(System.out, u, 15);
        }
    }
    //metoda bez rekurencji korzystajaca z relacji parent->child
    static void neighboursTestWithRTree() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");
        //AUList.list(System.out);

        for(AdminUnit u : AUList.selectByName("Tarnowskie Góry", false).units){
            System.out.println("---SASIEDZI---DRZEWO---" + u.name + "---");
            AUList.listNeighboursRTree(System.out, u, 15);
        }
    }
    //metoda rekurencyjna
    static void neighboursTestWithRTree2() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");
        //AUList.list(System.out);

        for(AdminUnit u : AUList.selectByName("Tarnowskie Góry", false).units){
            System.out.println("---SASIEDZI---DRZEWO---" + u.name + "---");
            AUList.listNeighboursRTree2(System.out, u, 15);
        }
    }


    static void stringTest() throws Exception {
        String text = "a,b,c\n123.4,567.8,91011.12";
        CSVReader reader = new CSVReader(new StringReader(text), ",", true);
        while(reader.next()){
            double a = reader.getDouble("a");
            double b = reader.getDouble("b");
            double c = reader.getDouble("c");
            System.out.printf(Locale.US, "%f %f %f\n", a, b, c);
        }
    }

    //do pliku titanic-part.csv dodalem kolumne Date, aby przetestowac metody zwiazane z LocalTime
    static void titanicTest() throws Exception {
        CSVReader reader = new CSVReader("titanic-part.csv",",",true);
        System.out.println("titanicTest");
        while(reader.next()){
            int id = reader.getInt("PassengerId");
            String name = reader.get("Name");
            double fare = reader.getDouble("Fare");
            LocalTime t = reader.getTime("Date", "HH:mm");
            System.out.printf(Locale.US,"%d %s %f %s\n",id, name, fare, t);
        }
    }

    static void adminUnitTest() throws Exception {
        CSVReader reader = new CSVReader("admin-units.csv",",",true);
        System.out.println("adminUnitTest");
        while(reader.next()){
            int id = reader.getInt("id");
            String name = reader.get("name");
            int parent;
            try {
                parent = reader.getInt("parent");
            } catch (Exception e){
                parent = 0;
            }

            System.out.printf(Locale.US,"%d %s %d\n",id, name, parent);
        }
    }

    static void withheaderTest() throws Exception {
        CSVReader reader = new CSVReader("with-header.csv",";",true);
        System.out.println("withHeaderTest");
        while(reader.next()){
            int id = reader.getInt("id");
            String imie = reader.get("imię");
            String nazwisko = reader.get("nazwisko");
            String ulica = reader.get("ulica");
            int nrdomu = reader.getInt("nrdomu");
            int nrmieszkania = reader.getInt("nrmieszkania");

            System.out.printf(Locale.US,"%d %s %s %s %d %d\n", id, imie, nazwisko, ulica, nrdomu, nrmieszkania);
        }
    }

    static void noheaderTest() throws Exception {
        CSVReader reader = new CSVReader("no-header.csv",";",false);
        System.out.println("noHeaderTest");
        while(reader.next()){
            int id = reader.getInt(0);
            String imie = reader.get(1);
            String nazwisko = reader.get(2);
            String ulica = reader.get(3);
            int nrdomu = reader.getInt(4);
            int nrmieszkania = reader.getInt(5);

            System.out.printf(Locale.US,"%d %s %s %s %d %d\n", id, imie, nazwisko, ulica, nrdomu, nrmieszkania);
        }
    }
    static  void adminUnitListTest() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");
        AUList.list(System.out);
    }

    static void missingValuesTest() throws Exception {
        CSVReader reader = new CSVReader("missing-values.csv",";",true);
        System.out.println("adminUnitTest");
        while(reader.next()){
            //nie wiedzialem czy gdzie zaimplementowac łapanie wyjatkow, zdecydowalem zostawic je w tescie, poniewaz
            //ma on budowe podobna do metody read()
            int id = reader.getInt("id");
            String name = reader.get("name");
            int parent;
            try {
                parent = reader.getInt("parent");
            } catch (Exception e){
                parent = 0;
            }
            int adminLevel;
            try {
                adminLevel = reader.getInt("admin_level");
            } catch (Exception e){
                adminLevel = 0;
            }
            double population;
            try{
                population = reader.getDouble("population");
            }
            catch (Exception e){
                population = 0;
            }

            double area;
            try{
                area = reader.getDouble("area");
            }
            catch (Exception e){
                area = 0;
            }

            double density;
            try{
                density = reader.getDouble("density");
            }
            catch (Exception e){
                density = 0;
            }
            System.out.printf(Locale.US,"%d %d %s %d %f %f %f\n",id, parent, name, adminLevel, population, area, density);
        }
    }
    static void getWKTTest() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");
        AdminUnit u = AUList.selectByName("gmina Oświęcim", false).units.get(0);
        u.bbox.getWKT(System.out);
    }


    static void acceleratorTest() throws Exception {
        CSVReader reader = new CSVReader("accelerator.csv",";",true);
        System.out.println("acceleratorTest");
        while(reader.next()){
            double X = reader.getDouble("X");
            double Y = reader.getDouble("Y");
            double Z = reader.getDouble("Z");
            double longitude = reader.getDouble("longitude");
            double latitude = reader.getDouble("latitude");
            double speed = reader.getDouble("speed");
            double time = reader.getDouble("time");
            int label = reader.getInt("label");

            System.out.printf(Locale.US,"X:%f Y:%f Z:%f longitude:%f latitude:%f speed:%f time:%f label:%d\n", X,Y,Z,longitude,latitude,speed,time,label);
        }
    }

    static void elecTest() throws Exception {
        CSVReader reader = new CSVReader("elec.csv",",",true);
        System.out.println("elecTest");
        while(reader.next()){
            int date = reader.getInt("date");
            int day = reader.getInt("day");
            double period = reader.getDouble("period");
            double nswprice = reader.getDouble("nswprice");
            double nswdemand = reader.getDouble("nswdemand");
            double vicprice = reader.getDouble("vicprice");
            double vicdemand = reader.getDouble("vicdemand");
            double transfer = reader.getDouble("transfer");
            String c = reader.get("class");

            System.out.printf(Locale.US,"date:%d day:%d period:%f nswprice:%f nswdemand:%f vicprice:%f vicdemand:%f transfer:%f class:%s\n",
                    date,day,period,nswprice,nswdemand,vicprice,vicdemand,transfer,c);
        }
    }

    static void adminUnitQueryTest() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");

        AdminUnitQuery query = new AdminUnitQuery()
                .selectFrom(AUList)
                .where(a->a.area>1000)
                .or(a->a.name.startsWith("Sz"))
                .sort((a,b)->Double.compare(a.area,b.area))
                .limit(100);
        query.execute().list(System.out);
    }

    static void adminUnitFilterTest2() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");

        //jednostki o nazwach na “Ż” posortowane według powierzchni
        AUList.filter(a -> a.name.startsWith("Ż")).sortInplaceByArea().list(System.out);

        //jednostki o nazwach na “K” posortowane alfabetycznie
        AUList.filter(a -> a.name.startsWith("K")).sortInplaceByName().list(System.out);

        //powiaty, których parent.name to województwo małopolskie
        AUList.filter(a -> a.adminLevel == 6).filter(a -> a.parent.name.contains("województwo małopolskie")).list(System.out);

        //jednostki nalezace do powiatu tarnogorskiego posortowane alfabetycznie
        AUList.filter(a -> a.adminLevel > 6).filter(a -> a.parent.name.contains("powiat tarnogórski")).sortInplaceByName().list(System.out);

        //pierwsze 10 jednostek zaczynajac od 50, ktorych populacja wieksza jest od 10.000 zostaly posortowane wedlug populacji
        AUList.filter(a -> a.population > 10000, 50, 10).sortInplaceByPopulation().list(System.out);

        //dwie pierwsze jednostki z jednostek posortowanych alfabetycznie, ktorych nazwa zaczyna sie na "A"
        AUList.filter(a -> a.name.startsWith("A")).sortInplaceByName().filter(a -> true, 2).list(System.out);
    }

    public static void main(String[] args) throws Exception {


        //titanicTest();
        //withheaderTest();
        //noheaderTest();
        //adminUnitTest();
        //adminUnitListTest();
        //missingValuesTest();
        //acceleratorTest();
        //elecTest();
        //getWKTTest();
        //neighboursTestWithRTree2();
        //neighboursTestWithRTree();
        //adminUnitQueryTest();
        //adminUnitFilterTest2();


        //jesli chodzi o wyszukiwania sasiadow - pojawia sie blad jesli chodzi o wojewodztwo mazowieckie i slaskie,
        //otoz kazda implementacja wyszukiwania sasiadow wskazuje te wojewodztwa jako sasiadujace ze soba
        //jednak problem nie lezy w samych implementacjach, a w BoundingBoxach
        //po sprawdzenie WKT na mapie box woj. mazowieckiego i woj. slaskiego nachodza na siebie
        //oprocz przygotowania if'a na te sytuacje, sensownym rozwiazaniem byloby dodanie dodatkowych wspolrzednych
        //w BoundingBoxach

        double t1 = System.nanoTime()/1e6;
        neighboursTest();
        double t2 = System.nanoTime()/1e6;

        double t3 = System.nanoTime()/1e6;
        neighboursTestWithRTree();
        double t4 = System.nanoTime()/1e6;

        double t5 = System.nanoTime()/1e6;
        neighboursTestWithRTree2();
        double t6 = System.nanoTime()/1e6;

        System.out.printf(Locale.US,"Czas dla zwyklego szukania: t2-t1=%f\n",t2-t1);
        System.out.printf(Locale.US,"Czas dla szukania z R-Tree bez rekurencji: t4-t3=%f\n",t4-t3);
        System.out.printf(Locale.US,"Czas dla szukania z R-Tree z rekurencja: t6-t5=%f\n",t6-t5);

        /*
        try (BufferedReader input = new BufferedReader(new InputStreamReader( new FileInputStream(inname), Charset.forName("Cp1250")))){
            try(PrintWriter output = new PrintWriter( new OutputStreamWriter(new FileOutputStream(outname),"Cp1250"))){
                for(;;){
                    String line = input.readLine();
                    if(line==null)break;
                    output.println(line);
                }
            }

         */
    }

}
