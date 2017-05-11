package blacklister_model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Brett Dixon on 2017-04-08.
 */
public class IpMap {

    private TreeMap<String, String[]> ipMap = new TreeMap<>();
    private String curWebsiteFile = "websites.txt";

    public IpMap() {
    }

    public void addWebsite (UrlToIP newIP){
        ipMap.put(newIP.getUrl(), newIP.getIPs());
        saveToFile(newIP.getUrl());
    }



    public Set<String> getWebSet(){
        return ipMap.keySet();
    }

    public String[] getIPByURL(String url){
        return ipMap.get(url);
    }

    public HashSet<String> getIPSet(){
        ArrayList<String> ipList = new ArrayList<>();
        Set<String> urls = ipMap.keySet();
        for (String url: urls){
            String[] ips = ipMap.get(url);
            for (String ip: ips){
                ipList.add(ip);
            }
        }
        HashSet<String> ipSet = new HashSet<String>(ipList);
        return ipSet;
    }

    public void loadFromFile(String filename) {
        if (filename==null){
            filename = curWebsiteFile;
        } else {
            curWebsiteFile = filename;
        }

        File file = new File(filename);
        Scanner inFile = null;
        try
        {
            inFile = new Scanner(file);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
            System.exit(1);
        }
        while (inFile.hasNextLine())
        {
            String currentLine = inFile.nextLine();
            String[] elements = currentLine.split("<=>");
            String[] tempIPs = elements[1].trim().split(" ");
            ipMap.put(elements[0].trim(), tempIPs);
        }
        inFile.close();
    }

    private void saveToFile(String url){
        File file = new File(curWebsiteFile);
        try {
            FileWriter fw = new FileWriter(file, true);
            String[] ips = ipMap.get(url);
            String newline = url+"<=>";
            for (String ip: ips){
                newline += ip+" ";
            }
            newline.trim();
            newline += "\n";
            fw.write(newline);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String output = "";
        Set<String> urls = ipMap.keySet();
        for(String url : urls){
            String[] ips = ipMap.get(url);
            for (String ip: ips){
                output += url+"<=>"+ ip +"\n";
            }
        }
        return output.trim();
    }
}
