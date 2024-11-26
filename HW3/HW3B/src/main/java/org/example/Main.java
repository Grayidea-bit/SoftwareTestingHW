package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.simple.JSONObject;
import org.json.simple.parser.*;

class team{
    String name;
    float winrate;
    public team(String name, float winrate) {
        this.name = name;
        this.winrate = winrate;
    }
}

public class Main {
    public static void main(String[] args) {
        ArrayList<team> ALteam = new ArrayList<>();
        ArrayList<team> NLteam = new ArrayList<>();
        readJsonIntoArray(ALteam, NLteam, "src/main/java/source/team.json");
        sortArray(ALteam);
        sortArray(NLteam);
        output(ALteam, NLteam);
    }

    public static void readJsonIntoArray(ArrayList<team> ALteam, ArrayList<team> NLteam, String path) {
        Object o = new Object();
        try{o = new JSONParser().parse(new FileReader(path));}
        catch(IOException | ParseException e){System.out.println("請檢查檔案位置是否正確");}
        JSONObject j = (JSONObject)o;
        try{
            for (Object object : j.keySet()) {
                JSONObject l = (JSONObject)j.get(object);
                for(Object t : l.keySet()) {
                    if(object.toString().equals("AL")){
                        float value = Float.parseFloat(l.get(t).toString());
                        ALteam.add(new team(t.toString(),value));
                    }
                    else if(object.toString().equals("NL")){
                        float value = Float.parseFloat(l.get(t).toString());
                        NLteam.add(new team(t.toString(),value));
                    }
                }
            }
        }
        catch (NumberFormatException e){
            System.out.println("勝率須為浮點數後一位，不含％符號");
        }
    }

    public static void sortArray(ArrayList<team> t) {
        for (int k = 0; k < t.size(); k++) {
            for(int i = k; i < t.size(); i++)
                if(t.get(k).winrate < t.get(i).winrate) {
                    Collections.swap(t,k,i);
                }
        }
    }

    public static void output(ArrayList<team> ALteam, ArrayList<team> NLteam) {
        try{
            System.out.println(ALteam.get(5).name+" 6 -----");
            System.out.println(ALteam.get(2).name+" 3 ----- ? -----");
            System.out.println("        "+ALteam.get(1).name+" 2 ----- ?");
            System.out.println(ALteam.get(4).name+" 5 -----");
            System.out.println(ALteam.get(3).name+" 4 ----- ? -----");
            System.out.println("        "+ALteam.get(0).name+" 1 ----- ? ----- ?");
            System.out.println("                              ---- ?");
            System.out.println(NLteam.get(5).name+" 6 ----- ? ----- ? ----- ?");
            System.out.println(NLteam.get(2).name+" 3 -----");
            System.out.println("        "+NLteam.get(1).name+" 2 ----");
            System.out.println(NLteam.get(4).name+" 5 ----- ? ----- ?");
            System.out.println(NLteam.get(3).name+" 4 -----");
            System.out.println("        "+NLteam.get(0).name+" 1 -----");
        }
        catch(IndexOutOfBoundsException e){
            System.out.println("請檢查json檔案中的賽區AL/NL是否填寫正確，或各區隊伍是否有不足或超過六隊");
        }
    }
}