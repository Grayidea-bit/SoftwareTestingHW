package org.example;

import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
class MainTest {

    @DisplayName("排序")
    @org.junit.jupiter.api.Test
    void sortArray() {
        int i;

        String[] array = {"Four", "Three", "Two", "One", "Six", "Eight"};
        int[] winrate = {4,3,2,1,6,8};
        String[] exceptarray = {"Eight", "Six", "Four", "Three", "Two", "One"};
        int[] expectwinrate = {8,6,4,3,2,1};
        ArrayList<team> team = new ArrayList<>();
        ArrayList<team> expect = new ArrayList<>();

        for(i=0;i<6;i++) {
            team.add(i,new team(array[i], winrate[i]));
            expect.add(i,new team(exceptarray[i], expectwinrate[i]));
        }
        Main.sortArray(team);
        for(i=0;i<6;i++) {
            assertEquals(expect.get(i).name, team.get(i).name);
            assertEquals(expect.get(i).winrate, team.get(i).winrate);
            System.out.println("AL test "+i+" passed");
        }
    }
}