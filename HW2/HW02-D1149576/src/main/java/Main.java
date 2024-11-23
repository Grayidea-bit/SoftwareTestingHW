//Json import
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

//Array import
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

//Exception import
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class Main {
    //程式使用的資料型態Teams
    static class Teams{
        String name;        //team name
        int rank;           //rank
        int seats;          //number of seats
        int playoffs;       //季後滿座率
        int world;          //世界賽滿座率
        int seats_playoff;  //seats*playoffs，非世界賽的預估售出座位
        int seats_world;    //seats*world，世界賽的預估售出座位
        float winrate;      //勝率，用來在冠軍賽時比較，確定主客場
        int best;           //最佳收入
        int worst;          //最差收入
        void addBest(int num)
        {
            this.best = num;
        }     //設置最佳收入
        void addWorst(int num)
        {
            this.worst = num;
        }   //設置最差收入
        Teams(JSONObject temp) //初始化
        {
            this.name = (String) temp.get("name");
            this.rank = ((Long) temp.get("rank")).intValue();
            this.seats = ((Long) temp.get("seats")).intValue();
            this.playoffs = ((Long) temp.get("playoffs")).intValue();
            this.world = ((Long) temp.get("world")).intValue();
            this.seats_playoff = (this.seats * this.playoffs) /100;
            this.seats_world = (this.seats*this.world)/100;
            this.winrate = ((Double) temp.get("winrate")).floatValue();
            this.best = 0;
            this.worst = 0;
        }
    }

    public static List<Teams> AL = new CopyOnWriteArrayList<>(); //AL區域隊伍的Teams型態陣列
    public static List<Teams> NL = new CopyOnWriteArrayList<>(); //NL區域隊伍的Teams型態陣列

    public static void main(String[] args) throws FileNotFoundException {
        try
        {
            //讀取JSON檔案
            Object o = new JSONParser().parse(new FileReader("src/main/resources/demo/TeamsWithSites.json"));

//            src/main/resources/xdemo/TeamsWithSites_WrongTeamNumber.json
//            src/main/resources/xdemo/TeamsWithSites_WrongTeamPlayoffs.json
//            src/main/resources/xdemo/TeamsWithSites_WrongTeamRank.json
//            src/main/resources/xdemo/TeamsWithSites_WrongTeamWorld.json

            //轉為JSONObject再轉成各區的JSONArray
            JSONObject j = (JSONObject) o;
            JSONArray ALTeams = (JSONArray) j.get("AL");
            JSONArray NLTeams = (JSONArray) j.get("NL");
            //確認各區隊伍數量是否正確
            if(ALTeams.size()!=6||NLTeams.size()!=6)
                throw new IllegalArgumentException("各區隊數須為六隊，各區隊伍數： AL: "+ALTeams.size()+" NL: "+NLTeams.size());
            //將JSONArray轉存到Teams型態中
            for(Object team : ALTeams)
            {
                JSONObject temp = (JSONObject) team;
                //確認各項數值都在正確範圍內
                CheckInputValue(temp);
                AL.add(new Teams(temp));
            }
            for(Object team : NLTeams)
            {
                JSONObject temp = (JSONObject) team;
                CheckInputValue(temp);
                NL.add(new Teams(temp));
            }

            //設定線程1計算最差收入
            Thread thread1 = new Thread()
            {
                public void run()
                {
                    for(Teams temp : AL)
                    {
                        WorstIncome("AL", temp.rank);
                    }
                    for(Teams temp : NL)
                    {
                        WorstIncome("NL", temp.rank);
                    }
                }
            };

            //設定線程2計算最佳收入
            Thread thread2 = new Thread()
            {
                public void run()
                {
                    for(Teams temp : AL)
                    {
                        BestIncome("AL", temp.rank);
                    }
                    for(Teams temp : NL)
                    {
                        BestIncome("NL", temp.rank);
                    }
                }
            };

            //執行計算
            thread1.start();
            thread2.start();

            //print
            //TestArray();
        }
        catch (FileNotFoundException e)
        {
            throw new FileNotFoundException("找不到檔案");
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
    }

    //印出內容
    private static void TestArray()
    {
        int i=0;
        for(Teams team : AL)
        {
            i++;
            System.out.print("AL-"+i+" : name : "+team.name+", ");
            System.out.print("rank : "+team.rank+", ");
            System.out.print("seats : "+team.seats+", ");
            System.out.print("playoffs : "+team.playoffs+", ");
            System.out.print("seats_playoff : "+team.seats_playoff+", ");
            System.out.print("world : "+team.world+", ");
            System.out.print("seats_world : "+team.seats_world+", ");
            System.out.print("winrate : "+team.winrate+", ");
            System.out.print("best : "+team.best+", ");
            System.out.println("worst : "+team.worst);
        }
        i=0;
        System.out.println();
        for(Teams team : NL)
        {
            i++;
            System.out.print("NL-"+i+" : name : "+team.name+", ");
            System.out.print("rank : "+team.rank+", ");
            System.out.print("seats : "+team.seats+", ");
            System.out.print("playoffs : "+team.playoffs+", ");
            System.out.print("seats_playoff : "+team.seats_playoff+", ");
            System.out.print("world : "+team.world+", ");
            System.out.print("seats_world : "+team.seats_world+", ");
            System.out.print("winrate : "+team.winrate+", ");
            System.out.print("best : "+team.best+", ");
            System.out.println("worst : "+team.worst);
        }
    }

    //輸入成class時之前確認數字符合條件
    private static void CheckInputValue(JSONObject temp)
    {
        if(((Long) temp.get("rank")).intValue()<=0||((Long) temp.get("rank")).intValue()>=7)
            throw new IllegalArgumentException("季後賽各區僅有六隊，rank不可超過1~6區間： "+(String) temp.get("name")+"的排名錯誤");
        if(((Long) temp.get("playoffs")).intValue()<=0||((Long) temp.get("playoffs")).intValue()>100)
            throw new IllegalArgumentException("各隊主場季後賽滿座率不會高於100低於0： "+(String) temp.get("name")+"的季後賽滿座率錯誤");
        if(((Long) temp.get("world")).intValue()<=0||((Long) temp.get("world")).intValue()>100)
            throw new IllegalArgumentException("各隊主場世界賽滿座率不會高於100低於0："+(String) temp.get("name")+"的世界賽滿座率錯誤");
        if(((Double) temp.get("winrate")).floatValue()<0||((Double) temp.get("winrate")).floatValue()>100)
            throw new IllegalArgumentException("各隊勝率不會高於100低於0："+(String) temp.get("name")+"的世界賽滿座率錯誤");
    }

    //計算最佳收入
    private static void BestIncome(String region, int rank)
    {
        int sum=0;  //總收入
        int wild_Team = 0,division_Team=0,championship_Team=0,world_Team=0; //各階段對手rank
        int ticket_Wild=70,ticket_Division=188,ticket_Championship=325,ticket_World=393;    //各階段門票

        if(region.equals("AL"))
        {
            AL.sort(Comparator.comparing(AL->AL.seats_playoff));    //將AL的隊伍依照座位數*滿座率排列
            if(rank==2||rank==3||rank==6)
            {
                //wild 3
                if(rank==3)
                {
                    sum += FindTeam("AL",3).seats_playoff*ticket_Wild*0.85*3;
                    wild_Team = 6;
                }
                else if(rank==6)
                {
                    sum += FindTeam("AL",3).seats_playoff*ticket_Wild*0.15*3;
                    wild_Team = 3;
                }

                //division 2-2-1
                if(rank==2)
                {
                    sum += FindTeam("AL",rank).seats_playoff*ticket_Division*0.85*3;
                    if(FindTeam("AL",3).seats_playoff>FindTeam("AL",6).seats_playoff)
                    {
                        sum += FindTeam("AL",3).seats_playoff*ticket_Division*0.15*2;
                        division_Team = 3;
                    }
                    else
                    {
                        sum += FindTeam("AL",6).seats_playoff*ticket_Division*0.15*2;
                        division_Team = 6;
                    }
                }
                else
                {
                    sum += FindTeam("AL",2).seats_playoff*ticket_Division*0.15*3;
                    sum += FindTeam("AL",rank).seats_playoff*ticket_Division*0.85*2;
                    division_Team = 2;
                }

                // Championship 2-3-2
                for(Teams team : AL)
                {
                    if(team.rank==1||team.rank==4||team.rank==5)    //先被找到的隊伍代表座位數*滿座率為三者最大
                    {
                        if(team.rank>rank)
                        {
                            sum += team.seats_playoff*ticket_Championship*0.15*3;
                            sum += FindTeam("AL",rank).seats_playoff*ticket_Championship*0.85*4;
                        }
                        else
                        {
                            sum += team.seats_playoff*ticket_Championship*0.15*4;
                            sum += FindTeam("AL",rank).seats_playoff*ticket_Championship*0.85*3;
                        }
                        championship_Team = team.rank;
                        break;
                    }
                }
            }
            else if(rank==1||rank==4||rank==5)
            {
                //wild 3
                if(rank==4)
                {
                    sum += FindTeam("AL",4).seats_playoff*ticket_Wild*0.85*3;
                    wild_Team = 5;
                }
                else if(rank==5)
                {
                    sum += FindTeam("AL",4).seats_playoff*ticket_Wild*0.15*3;
                    wild_Team = 4;
                }

                //division 2-2-1
                if(rank==1)
                {
                    sum += FindTeam("AL",rank).seats_playoff*ticket_Division*0.85*3;
                    if(FindTeam("AL",4).seats_playoff>FindTeam("AL",5).seats_playoff)
                    {
                        sum += FindTeam("AL",4).seats_playoff*ticket_Division*0.15*2;
                        division_Team = 4;
                    }
                    else
                    {
                        sum += FindTeam("AL",5).seats_playoff*ticket_Division*0.15*2;
                        division_Team = 5;
                    }
                }
                else
                {
                    sum += FindTeam("AL",1).seats_playoff*ticket_Division*0.15*3;
                    sum += FindTeam("AL",rank).seats_playoff*ticket_Division*0.85*2;
                    division_Team = 1;
                }

                // Championship 2-3-2
                for(Teams team : AL)
                {
                    if(team.rank==2||team.rank==3||team.rank==6)    //先被找到的隊伍代表座位數*滿座率為三者最大
                    {
                        if(team.rank>rank)
                        {
                            sum += team.seats_playoff*ticket_Championship*0.15*3;
                            sum += FindTeam("AL",rank).seats_playoff*ticket_Championship*0.85*4;
                        }
                        else
                        {
                            sum += team.seats_playoff*ticket_Championship*0.15*4;
                            sum += FindTeam("AL",rank).seats_playoff*ticket_Championship*0.85*3;
                        }
                        championship_Team = team.rank;
                        break;
                    }
                }
            }
            AL.sort(Comparator.comparing(AL->AL.rank)); //恢復AL原本的排列

            //world 2-3-2
            NL.sort(Comparator.comparing(NL->NL.seats_playoff));    //將NL的隊伍依照座位數*滿座率排列
            sum += NL.get(5).seats_world*ticket_World*0.15*4;       //最後一隊的座位數*滿座率為最大
            world_Team = NL.get(5).rank;
            sum += FindTeam("AL",rank).seats_world*ticket_World*0.85*3;
            NL.sort(Comparator.comparing(NL->NL.rank)); //恢復AL原本的排列
            FindTeam("AL",rank).addBest(sum);   //將收入加到best函數中
            if(rank==1||rank==2)
            {
                System.out.println(FindTeam("AL",rank).name+"最佳收入過程(各階段比賽皆打滿)：總收入為"+sum);
                System.out.println("分區賽對上"+FindTeam("AL",division_Team).name+", 聯盟冠軍賽對上"+FindTeam("AL",championship_Team).name+", 世界賽對上"+FindTeam("NL",world_Team).name);
                System.out.println();
            }
            else
            {
                System.out.println(FindTeam("AL",rank).name+"最佳收入過程(各階段比賽皆打滿)：總收入為"+sum);
                System.out.println("外卡賽對上"+FindTeam("AL",wild_Team).name+", 分區賽對上"+FindTeam("AL",division_Team).name+", 聯盟冠軍賽對上"+FindTeam("AL",championship_Team).name+", 世界賽對上"+FindTeam("NL",world_Team).name);
                System.out.println();
            }
        }
        else if(region.equals("NL"))
        {
            NL.sort(Comparator.comparing(NL->NL.seats_playoff));
            if(rank==2||rank==3||rank==6)
            {
                //wild 3
                if(rank==3)
                {
                    sum += FindTeam(region,3).seats_playoff*ticket_Wild*0.85*3;
                    wild_Team = 4;
                }
                else if(rank==6) {
                    sum += FindTeam(region, 3).seats_playoff * ticket_Wild * 0.15 * 3;
                    wild_Team = 4;
                }

                //division 2-2-1
                if(rank==2)
                {
                    sum += FindTeam(region,rank).seats_playoff*ticket_Division*0.85*3;
                    if(FindTeam(region,3).seats_playoff>FindTeam("AL",6).seats_playoff)
                    {
                        sum += FindTeam(region,3).seats_playoff*ticket_Division*0.15*2;
                        division_Team = 3;
                    }
                    else
                    {
                        sum += FindTeam(region,6).seats_playoff*ticket_Division*0.15*2;
                        division_Team = 6;
                    }
                }
                else
                {
                    sum += FindTeam(region,2).seats_playoff*ticket_Division*0.15*3;
                    sum += FindTeam(region,rank).seats_playoff*ticket_Division*0.85*2;
                    division_Team = 2;
                }

                // Championship 2-3-2
                for(Teams team : NL)
                {
                    if(team.rank==1||team.rank==4||team.rank==5)
                    {
                        if(team.rank>rank)
                        {
                            sum += team.seats_playoff*ticket_Championship*0.15*3;
                            sum += FindTeam(region,rank).seats_playoff*ticket_Championship*0.85*4;
                        }
                        else
                        {
                            sum += team.seats_playoff*ticket_Championship*0.15*4;
                            sum += FindTeam(region,rank).seats_playoff*ticket_Championship*0.85*3;
                        }
                        championship_Team = team.rank;
                        break;
                    }
                }
            }
            else if(rank==1||rank==4||rank==5)
            {
                //wild 3
                if(rank==4)
                {
                    sum += FindTeam(region,4).seats_playoff*ticket_Wild*0.85*3;
                    wild_Team = 5;
                }
                else if(rank==5)
                {
                    sum += FindTeam(region,4).seats_playoff*ticket_Wild*0.15*3;
                    wild_Team = 4;
                }

                //division 2-2-1
                if(rank==1)
                {
                    sum += FindTeam(region,rank).seats_playoff*ticket_Division*0.85*3;
                    if(FindTeam(region,4).seats_playoff>FindTeam("AL",5).seats_playoff)
                    {
                        sum += FindTeam(region,4).seats_playoff*ticket_Division*0.15*2;
                        division_Team = 4;
                    }
                    else
                    {
                        sum += FindTeam(region,5).seats_playoff*ticket_Division*0.15*2;
                        division_Team = 5;
                    }
                }
                else
                {
                    sum += FindTeam(region,1).seats_playoff*ticket_Division*0.15*3;
                    sum += FindTeam(region,rank).seats_playoff*ticket_Division*0.85*2;
                    division_Team = 1;
                }

                // Championship 2-3-2
                for(Teams team : NL)
                {
                    if(team.rank==2||team.rank==3||team.rank==6)
                    {
                        if(team.rank>rank)
                        {
                            sum += team.seats_playoff*ticket_Championship*0.15*3;
                            sum += FindTeam(region,rank).seats_playoff*ticket_Championship*0.85*4;
                        }
                        else
                        {
                            sum += team.seats_playoff*ticket_Championship*0.15*4;
                            sum += FindTeam(region,rank).seats_playoff*ticket_Championship*0.85*3;
                        }
                        championship_Team = team.rank;
                        break;
                    }
                }
            }
            NL.sort(Comparator.comparing(NL->NL.rank));

            //world 2-3-2
            AL.sort(Comparator.comparing(AL->AL.seats_playoff));
            sum += AL.get(5).seats_world*ticket_World*0.15*4;
            world_Team = AL.get(5).rank;
            sum += FindTeam(region,rank).seats_world*ticket_World*0.85*3;
            AL.sort(Comparator.comparing(AL->AL.rank));
            FindTeam(region,rank).addBest(sum);
            if(rank==1||rank==2)
            {
                System.out.println(FindTeam(region,rank).name+"最佳收入過程(各階段比賽皆打滿)：總收入為"+sum);
                System.out.println("分區賽對上"+FindTeam(region,division_Team).name+", 聯盟冠軍賽對上"+FindTeam(region,championship_Team).name+", 世界賽對上"+FindTeam("AL",world_Team).name);
                System.out.println();
            }
            else
            {
                System.out.println(FindTeam(region,rank).name+"最佳收入過程(各階段比賽皆打滿)：總收入為"+sum);
                System.out.println("外卡賽對上"+FindTeam(region,wild_Team).name+", 分區賽對上"+FindTeam(region,division_Team).name+", 聯盟冠軍賽對上"+FindTeam(region,championship_Team).name+", 世界賽對上"+FindTeam("AL",world_Team).name);
                System.out.println();
            }
        }
        else throw new IllegalArgumentException("BestIncome函式接收值錯誤");
    }

    //計算最差收入
    private static void WorstIncome(String region, int rank)
    {
        Teams f,l;
        int num;
        int ticket_Wild=70,ticket_Division=188;
        if(rank==6||rank==3)
        {
            f = FindTeam(region,3);
            if(region.equals("AL"))
            {
                AL.get(2).addWorst((int) (ticket_Wild*f.seats_playoff*0.85*2));
                AL.get(5).addWorst((int) (ticket_Wild*f.seats_playoff*0.15*2));
            }
            else
            {
                NL.get(2).addWorst((int) (ticket_Wild*f.seats_playoff*0.85*2));
                NL.get(5).addWorst((int) (ticket_Wild*f.seats_playoff*0.15*2));
            }
        }
        else if(rank==4||rank==5)
        {
            f = FindTeam(region,4);
            if(region.equals("AL"))
            {
                AL.get(3).addWorst((int) (ticket_Wild*f.seats_playoff*0.85*2));
                AL.get(4).addWorst((int) (ticket_Wild*f.seats_playoff*0.15*2));
            }
            else
            {
                NL.get(3).addWorst((int) (ticket_Wild*f.seats_playoff*0.85*2));
                NL.get(4).addWorst((int) (ticket_Wild*f.seats_playoff*0.15*2));
            }
        }
        else if (rank==1)
        {
            f = FindTeam(region,4);
            l = FindTeam(region,5);
            if(f.seats_playoff<l.seats_playoff)
            {
                if(region.equals("AL"))
                {
                    AL.get(0).addWorst((int) ((ticket_Division*f.seats_playoff*0.15)+(ticket_Division*AL.get(0).seats_playoff*0.85*2)));
                }
                else
                {
                    NL.get(0).addWorst((int) ((ticket_Division*f.seats_playoff*0.15)+(ticket_Division*NL.get(0).seats_playoff*0.85*2)));
                }
            }
            else
            {
                if(region.equals("AL"))
                {
                    AL.get(0).addWorst((int) ((ticket_Division*l.seats_playoff*0.15)+(ticket_Division*AL.get(0).seats_playoff*0.85*2)));
                }
                else
                {
                    NL.get(0).addWorst((int) ((ticket_Division*l.seats_playoff*0.15)+(ticket_Division*NL.get(0).seats_playoff*0.85*2)));
                }
            }
        }
        else if(rank==2)
        {
            f = FindTeam(region,3);
            l = FindTeam(region,6);
            if(f.seats_playoff<l.seats_playoff)
            {
                if(region.equals("AL"))
                {
                    AL.get(1).addWorst((int) ((ticket_Division*f.seats_playoff*0.15)+(ticket_Division*AL.get(1).seats_playoff*0.85*2)));
                }
                else
                {
                    NL.get(1).addWorst((int) ((ticket_Division*f.seats_playoff*0.15)+(ticket_Division*NL.get(1).seats_playoff*0.85*2)));
                }
            }
            else
            {
                if(region.equals("AL"))
                {
                    AL.get(1).addWorst((int) ((ticket_Division*l.seats_playoff*0.15)+(ticket_Division*AL.get(1).seats_playoff*0.85*2)));
                }
                else
                {
                    NL.get(1).addWorst((int) ((ticket_Division*l.seats_playoff*0.15)+(ticket_Division*NL.get(1).seats_playoff*0.85*2)));
                }
            }
        }
        else throw new IllegalArgumentException("WorstIncome函式接收值錯誤");
    }

    //透過輸入地區和排名尋找隊伍
    private static Teams FindTeam(String region, int rank)
    {
        if(region.equals("AL"))
        {
            for(Teams team:AL)
            {
                if(team.rank==rank)
                {
                    return team;
                }
            }
        }
        else if(region.equals("NL"))
        {
            for(Teams team:NL)
            {
                if(team.rank==rank)
                {
                    return team;
                }
            }
        }
        else{throw new IllegalArgumentException(("FindTeam函式搜尋的區域名稱不對"));}
        System.out.println("找不到指定隊伍");
        return null;
    }
}