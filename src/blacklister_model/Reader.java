package blacklister_model;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.JPacket;
import org.jnetpcap.packet.JPacketHandler;
import org.jnetpcap.packet.Payload;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import sun.reflect.generics.tree.Tree;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Created by maxma on 4/1/2017.
 */
public class Reader {
    private TreeMap<Integer, TreeMap<String, String>> packetInfo = new TreeMap<>();

    public TreeMap getPacketInfo() {
        return(packetInfo);
    }
    public void clearPacketInfo() {
        packetInfo = new TreeMap<>();
    }
    public void printPacketInfo() {
        for(Map.Entry<Integer, TreeMap<String, String>> entry : packetInfo.entrySet()) {
            int key = entry.getKey();
            TreeMap<String, String> value = entry.getValue();

            System.out.println("Frame Number: " + key + " : " + value);
        }
    }
//
    public void LoadPcap(String filename) {
        clearPacketInfo();

        final StringBuilder errbuf = new StringBuilder();
        final Pcap pcap = Pcap.openOffline(filename, errbuf);

        if (pcap == null) {
            System.err.println(errbuf);
            System.exit(1);
        }

        int loop = pcap.loop(pcap.LOOP_INFINITE, new JPacketHandler<StringBuilder>() {

            final Ethernet eth = new Ethernet();
            final Ip4 ip4 = new Ip4();
            final Payload pay = new Payload();

            @Override
            public void nextPacket(JPacket packet, StringBuilder stringBuilder) {
                inputToArray(packet, ip4, eth, pay);
            }
        }, errbuf);
    }

    public void LoadPcap(String filename, Integer amountToRead) {
        clearPacketInfo();
        final StringBuilder errbuf = new StringBuilder();
        final Pcap pcap = Pcap.openOffline(filename, errbuf);
        if (pcap == null) {
            System.err.println(errbuf);
            System.exit(1);
        }
        int loop = pcap.loop(amountToRead, new JPacketHandler<StringBuilder>() {

            final Ethernet eth = new Ethernet();
            final Ip4 ip4 = new Ip4();
            final Payload pay = new Payload();

            @Override
            public void nextPacket(JPacket packet, StringBuilder stringBuilder) {
                inputToArray(packet, ip4, eth, pay);
            }
        }, errbuf);
    }

    public void inputToArray(JPacket packet, Ip4 ip4, Ethernet eth, Payload pay) {
        packet.getHeader(eth);
        packet.getHeader(ip4);
        packet.getHeader(pay);

        String payload = pay.toString();
        String isBittorrent = "False";
        String sourceMac = FormatUtils.mac(eth.source());
        String destinationMac = FormatUtils.mac(eth.destination());
        String sourceIP = FormatUtils.ip(ip4.source());
        String destinationIP = FormatUtils.ip(ip4.destination());
        Integer packetSize = packet.getTotalSize();
        Integer packetLength = packet.getPacketWirelen();
        Date timestamp = new Date(packet.getCaptureHeader().timestampInMillis());

        TreeMap<String, String> temp = new TreeMap<>();

        System.out.println("Frame Read: " + packet.getFrameNumber());

        temp.put("Source MAC", sourceMac);
        temp.put("Destination MAC", destinationMac);
        temp.put("Source IP", sourceIP);
        temp.put("Destination IP", destinationIP);
        temp.put("Packet Size",  packetSize.toString());
        temp.put("Packet Length", packetLength.toString());
        temp.put("Timestamp", timestamp.toString());

        if(payload.contains(".BitTorrent")) isBittorrent = "True";
        else isBittorrent = "False";
        temp.put("Is Bittorrent", isBittorrent);

        packetInfo.put((int) packet.getFrameNumber(), temp);
    }

    public void inputToArray2(JPacket packet, Ip4 ip4, Ethernet eth, Payload pay) {
        packet.getHeader(eth);
        packet.getHeader(ip4);
        packet.getHeader(pay);

        String payload = pay.toString();
        String isBittorrent = "False";
        String sourceMac = FormatUtils.mac(eth.source());
        String destinationMac = FormatUtils.mac(eth.destination());
        String sourceIP = FormatUtils.ip(ip4.source());
        String destinationIP = FormatUtils.ip(ip4.destination());
        Integer packetSize = packet.getTotalSize();
        Integer packetLength = packet.getPacketWirelen();
        String timestamp = packet.toString().split("\n")[3].substring(25);

        TreeMap<String, String> temp = new TreeMap<>();
        TreeMap<Integer, TreeMap<String, String>> frameNumTemp = new TreeMap<>();

        temp.put("Source MAC", sourceMac);
        temp.put("Destination MAC", destinationMac);
        temp.put("Source IP", sourceIP);
        temp.put("Destination IP", destinationIP);
        temp.put("Packet Size",  packetSize.toString());
        temp.put("Packet Length", packetLength.toString());
        temp.put("Timestamp", timestamp);

        if(payload.contains(".BitTorrent")) isBittorrent = "True";
        else isBittorrent = "False";
        temp.put("Is Bittorrent", isBittorrent);

        frameNumTemp.put((int) packet.getFrameNumber(), temp);

        PrintWriter writer = null;
        try {
            writer = new PrintWriter("cache.txt", "UTF-8");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        writer.println(frameNumTemp);
    }
}
