package blacklister_model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by maxma on 4/1/2017.
 */
public class Blacklist {
    private TreeMap<String, TreeMap<String, String>> blacklist = new TreeMap<>();

    public void addToBlacklist(String mac, String reason) {
        TreeMap<String, String> temp = new TreeMap<>();

        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();

        temp.put("Time", String.valueOf(df.format(calobj.getTime())));
        temp.put("Reason", reason);
        blacklist.put(mac, temp);
    }

    public void removeFromBlacklist(String itemToRemove) {
        blacklist.remove(itemToRemove);
    }

    public TreeMap<String, TreeMap<String, String>> getBlacklist() {
        return (blacklist);
    }

    public void SaveFile(String fileName) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(fileName, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        for(Map.Entry<String, TreeMap<String, String>> entry : blacklist.entrySet()) {
            String Time = entry.getValue().get("Time");
            String Reason = entry.getValue().get("Reason");

            writer.println(entry.getKey() + "<=>" + Time + "<=>" + Reason);
        }
        writer.close();
    }

    public void ReadFile(String filename) {
        File file = new File(filename);
        Scanner inFile = null;

        try {
            inFile = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println(e);
            System.exit(1);
        }

        while (inFile.hasNextLine()) {
            String curLine = inFile.nextLine();
            String[] temp = curLine.split("<=>");
            TreeMap<String, String> tempTreeMap = new TreeMap<>();
            tempTreeMap.put("Time", temp[1]);
            tempTreeMap.put("Reason", temp[2]);

            blacklist.put(temp[0], tempTreeMap);
        }
    }

    public void ClearFile(String filename) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(filename, "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.close();
    }
}

