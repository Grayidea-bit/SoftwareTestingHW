package org.example;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final String[] forcedBases;
        Scanner s = new Scanner(System.in);

        System.out.println("Enter status: ");
        forcedBases = Forceout(ReadStatus(s.nextLine()));

        System.out.print("-> ");
        for(String forcedBase : forcedBases) {
            if(forcedBase != null) {
                System.out.print(forcedBase+", ");
            }
        }
        System.out.print("\b\b");
    }

    public static int[] ReadStatus(final String status) {
        int[] code={0,0,0};
        final String[] statusArray = status.split(", ");

        for(String base : statusArray) {
            if(base.equals("B1")) {
                code[0]++;
            }
            else if(base.equals("B2")) {
                code[1]++;
            }
            else if(base.equals("B3")) {
                code[2]++;
            }
            else if(!base.equals("x")) {
                throw new IllegalArgumentException("輸入錯誤: " + status);
            }
        }

        return code;
    }

    public static String[] Forceout(final int[] statusArray) {
        String[] forcedBases = new String[4];
        final boolean base1 = (statusArray[0] == 1);
        final boolean base2 = (statusArray[1] == 1);
        final boolean base3 = (statusArray[2] == 1);

        forcedBases[0] = "B1";
        if(base1) {
            forcedBases[1] = "B2";
        }
        if(base1&&base2) {
            forcedBases[2] = "B3";
        }
        if(base1&&base2&&base3) {
            forcedBases[3] = "HB";
        }

        return forcedBases;
    }
}