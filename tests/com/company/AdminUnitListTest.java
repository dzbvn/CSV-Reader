package com.company;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class AdminUnitListTest {

    @Test
    void selectByName() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");
        AdminUnit u = AUList.selectByName("gmina Oświęcim", false).units.get(0);
        assertTrue(u.name.contains("gmina Oświęcim"));
    }

    @Test
    void getNeighbours() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");


        for (AdminUnit u : AUList.selectByName("województwo małopolskie", false).units) {
            String res1 = "";
            for (AdminUnit c : AUList.getNeighbours(u, 15).units) {
                res1 += c.toString();
            }
            assertTrue(res1.contains("województwo śląskie") && res1.contains("województwo podkarpackie") && res1.contains("województwo świętokrzyskie"));
        }
    }

    @Test
    void getRoot() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");
        AdminUnit u = AUList.selectByName("gmina Oświęcim", false).units.get(0);
        assertTrue(AUList.getRoot(u).toString().contains("województwo małopolskie"));
    }

    @Test
    void getNeighboursRTree2() throws Exception {
        AdminUnitList AUList = new AdminUnitList();
        AUList.read("admin-units.csv");


        for (AdminUnit u : AUList.selectByName("województwo małopolskie", false).units) {
            String res1 = "";
            for (AdminUnit c : AUList.getNeighboursRTree2(u, 15).units) {
                res1 += c.toString();
            }
            assertTrue(res1.contains("województwo śląskie") && res1.contains("województwo podkarpackie") && res1.contains("województwo świętokrzyskie"));
        }
    }


}