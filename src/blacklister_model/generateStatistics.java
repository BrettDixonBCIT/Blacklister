package blacklister_model;

import java.util.*;

/**
 * Created by maxma on 4/8/2017.
 */
public class generateStatistics {
    private Integer totalPacketSize = 0;
    private long averagePacketSize = 0;
    private Integer totalBittorrentPackets = 0;
    private TreeMap<Integer, TreeMap<String, String>> bittorrentStatistics = new TreeMap<>();
    private ArrayList allMacAddresses = new ArrayList();
    private ArrayList allIpAddresses = new ArrayList();
    private ArrayList allBittorrentMACs = new ArrayList();
    private TreeMap<String, Set<String>> blacklistUsers = new TreeMap<>();

    public void generateStatistics(TreeMap<Integer, TreeMap<String, String>> packetInfo) {
        generateAllIPs(packetInfo);
        generateAllMACs(packetInfo);
        generateTotalPacketSize(packetInfo);
        generateAveragePacketSize(packetInfo);
        generateBittorrentStats(packetInfo);
        generateBittorrentMACs(getBittorrentStatistics());
    }

    public void generateAllIPs(TreeMap<Integer, TreeMap<String, String>> packetInfo) {
        for(Map.Entry<Integer, TreeMap<String, String>> entry : packetInfo.entrySet()) {
            String destIP = entry.getValue().get("Destination IP");
            String srcIP = entry.getValue().get("Source IP");
            if(!(allIpAddresses.contains(destIP))) {
                allIpAddresses.add(destIP);
            }
            if(!(allIpAddresses.contains(srcIP))) {
                allIpAddresses.add(srcIP);
            }
        }
    }
    public void generateAllMACs(TreeMap<Integer, TreeMap<String, String>> packetInfo) {
        for(Map.Entry<Integer, TreeMap<String, String>> entry : packetInfo.entrySet()) {
            String destMAC = entry.getValue().get("Destination MAC");
            String srcMAC = entry.getValue().get("Source MAC");
            if(!(allMacAddresses.contains(destMAC))) {
                allMacAddresses.add(destMAC);
            }
            if(!(allMacAddresses.contains(srcMAC))) {
                allMacAddresses.add(srcMAC);
            }
        }
    }
    public void generateBittorrentMACs(TreeMap<Integer, TreeMap<String, String>> bittorrentPackets) {
        for(Map.Entry<Integer, TreeMap<String, String>> entry : bittorrentPackets.entrySet()) {
            String MAC = entry.getValue().get("MAC Address");
            if(!(allBittorrentMACs.contains(MAC))) allBittorrentMACs.add(MAC);
        }
    }
    public void generateTotalPacketSize(TreeMap<Integer, TreeMap<String, String>> packetInfo) {
        totalPacketSize = 0;
        for(Map.Entry<Integer, TreeMap<String, String>> entry : packetInfo.entrySet()) {

            Integer packetSize = Integer.parseInt(entry.getValue().get("Packet Size"));
            totalPacketSize += packetSize;
        }
    }
    public void generateAveragePacketSize(TreeMap<Integer, TreeMap<String, String>> packetInfo) {
        if(totalPacketSize == 0) {
            generateTotalPacketSize(packetInfo);
        }
        averagePacketSize = 0;

        for(Map.Entry<Integer, TreeMap<String, String>> entry : packetInfo.entrySet()) {
            averagePacketSize++;
        }
        totalPacketSize = getTotalPacketSize();
        averagePacketSize = totalPacketSize / averagePacketSize;
    }
    public void generateBittorrentStats(TreeMap<Integer, TreeMap<String, String>> packetInfo) {
        for(Map.Entry<Integer, TreeMap<String, String>> entry: packetInfo.entrySet()) {
            if(entry.getValue().get("Is Bittorrent") == "True") {
                totalBittorrentPackets++;
                TreeMap<String, String> temp = new TreeMap<>();

                if(entry.getValue().get("Source IP").contains("192.168")) {
                    temp.put("MAC Address", entry.getValue().get("Source MAC"));
                }
                if(entry.getValue().get("Destination IP").contains("192.168")) {
                    temp.put("MAC Address", entry.getValue().get("Destination MAC"));
                }
                temp.put("Timestamp", entry.getValue().get("Timestamp"));
                bittorrentStatistics.put(entry.getKey(), temp);
            }
        }
    }
    public void compareBlacklist(TreeMap<Integer, TreeMap<String, String>> packetInfo, Set<String> users) {
        for(String s : users) {
            HashSet<String> temp = new HashSet<String>();
            System.out.println(s);
            for(Map.Entry<Integer, TreeMap<String, String>> entry : packetInfo.entrySet()) {
                System.out.println(s + " IPS " + entry.getValue().get("Destination IP") + " " + entry.getValue().get("Source IP"));
                if((entry.getValue().get("Source IP").toString().equals(s.toString()))) {
//                    temp.add(entry.getValue().get("Destination IP")); //PICK ONE ONLY
                    temp.add(entry.getValue().get("Destination MAC"));
                }
                if((entry.getValue().get("Destination IP").toString().equals(s.toString()))) {
//                    temp.add(entry.getValue().get("Source IP")); //PICK ONE ONLY
                    temp.add(entry.getValue().get("Source MAC"));
                }
            }
            blacklistUsers.put(s, temp);
        }
    }

    public ArrayList getAllMacAddresses() {
        return(allMacAddresses);
    }
    public ArrayList getAllIpAddresses() {
        return(allIpAddresses);
    }
    public ArrayList getAllBittorrentMACs() {
        return(allBittorrentMACs);
    }
    public Integer getTotalPacketSize() {
        return(totalPacketSize);
    }
    public long getAveragePacketSize() {
        return(averagePacketSize);
    }
    public TreeMap getBittorrentStatistics() {
        return(bittorrentStatistics);
    }
    public Integer getTotalBittorrentPackets(){
        return(totalBittorrentPackets);
    }
    public TreeMap getBlacklistUsers() {
        return(blacklistUsers);
    }
}
