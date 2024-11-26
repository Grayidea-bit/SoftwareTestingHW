package org.example;
import org.json.simple.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.json.simple.parser.*;
import java.io.FileReader;
import java.util.Collections;

class team{
    String name;
    float winrate;
    public team(String name, float winrate) {
        this.name = name;
        this.winrate = winrate;
    }
}
public class Main {
    public static void main(String[] args) throws Exception {
        ArrayList<team> ALteam = new ArrayList<>();
        ArrayList<team> NLteam = new ArrayList<>();
        Object o = new Object();
        try{o = new JSONParser().parse(new FileReader("src/main/java/org/example/team.json"));}
        catch(FileNotFoundException e){System.out.println("請檢查檔案位置是否正確");}
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

        for (int k = 0; k < ALteam.size(); k++) {
            for(int i = k; i < ALteam.size(); i++)
            if(ALteam.get(k).winrate < ALteam.get(i).winrate) {
                Collections.swap(ALteam,k,i);
            }
        }
        for (int k = 0; k < NLteam.size(); k++) {
            for(int i = k; i < NLteam.size(); i++)
                if(NLteam.get(k).winrate < NLteam.get(i).winrate) {
                    Collections.swap(NLteam,k,i);
                }
        }
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