package blacklister_gui;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;

import blacklister_model.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

/**
 *
 * @author Brett Dixon
 */
public class mainBlacklisterController implements Initializable {
    private File file;
    private Blacklist macBlacklist = new Blacklist();
    private Reader pcap = new Reader();
    private generateStatistics newGen = new generateStatistics();
    private IpMap badIPs = new IpMap();
    private TreeMap<String, ArrayList<String>> visitedBadIPs = new TreeMap<String, ArrayList<String>>();
    private HashSet<String> macsBadWeb = new HashSet<String>();

    private CategoryAxis xAxis = new CategoryAxis();
    private NumberAxis yAxis = new NumberAxis();


    @FXML
    private BarChart<String, Number> bitBarChart= new BarChart<String, Number>(xAxis,yAxis);


    @FXML
    private TextField newWebsite = new TextField();

    @FXML
    private Label webStats = new Label();

    @FXML
    private Label btStats = new Label();

    @FXML
    private Label pcapStatus = new Label();

    @FXML
    private Label pcapStatus2 = new Label();

    @FXML
    private MenuItem fileMenuItem = new MenuItem();

    @FXML
    private MenuItem closeMenuItem = new MenuItem();

    @FXML
    private ListView<String> btMACs = new ListView<String>();

    @FXML
    private ListView<String> websites = new ListView<String>();

    @FXML
    private ListView<String> allMacs = new ListView<String>();

    @FXML
    private Label deviceDetLabel = new Label();

    @FXML
    private Button addWebsite = new Button();

    @FXML
    private Button addMACSWeb = new Button();

    @FXML
    private Button addMACSBt = new Button();

    @FXML
    private MenuItem helpMenuItem = new MenuItem();

    @FXML
    private void handleOpenAction(ActionEvent e) {
        file = load();
        pcap.LoadPcap(file.getAbsolutePath());
        newGen.generateStatistics(pcap.getPacketInfo());
        String btStatStr = "Number of Bittorrent Packets: " + newGen.getTotalBittorrentPackets();
        btStats.setText(btStatStr);
        pcapStatus.setText(file.getName());
        pcapStatus2.setText(file.getName());
        pcapStatus.setTextFill(Color.web("#3fad2e"));
        pcapStatus2.setTextFill(Color.web("#3fad2e"));
        String[] bitGraphLabels = {"Time VS # of Bittorent Packets", "Timestamp", "# of Bittorent Packets"};
        drawBarChart(bitGraphLabels);
        ObservableList<String> items = FXCollections.observableArrayList(newGen.getAllBittorrentMACs());
        btMACs.setItems(items);
        macBlacklist.ReadFile("blacklist.txt");
        deviceBLStuff();
        webStuff();
    }

    @FXML
    private void closeProgram(ActionEvent e){
        System.exit(0);
    }

    @FXML
    private void getHelp(ActionEvent e){
       String title = "Blacklister Help";
       String helpText = "Blacklister was created by Dylan Chew and Brett Dixon\n\n"+
                         "Please be patient when Loading Pcap files.  Large Files could take over a minute.\n\n"+
                         "Direct all complaints to Brett Dixon at: brettdixon1@gmail.com.  It's all his fault.  :P";
       AlertBox.display(title, helpText);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }


    public File load(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Packet Capture", "*.pcap"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );
        File file = fileChooser.showOpenDialog(null);
        if(file==null){
            System.out.println("No File Choosen");
        }
        return file;
    }

    public void drawBarChart(String[] graphLabels){
        bitBarChart.setTitle(graphLabels[0]);
        xAxis.setLabel(graphLabels[1]);
        yAxis.setLabel(graphLabels[2]);
        XYChart.Series series1 = new XYChart.Series();
        TreeMap<Integer, TreeMap<String, String>> bitStats = newGen.getBittorrentStatistics();
        Set<Integer> packNums = bitStats.keySet();
        TreeMap<String, Integer> dateVSPackets = new TreeMap<String, Integer>();
        for(int packet : packNums){
            TreeMap<String, String> bitStat = bitStats.get(packet);
            String time = bitStat.get("Timestamp");
            if (dateVSPackets.containsKey(time)){
                int count = dateVSPackets.get(time);
                count++;
                dateVSPackets.put(time, count);
            } else {
                int count = 1;
                dateVSPackets.put(time, count);
            }
        }
        Set<String> times = dateVSPackets.keySet();
        for(String time: times){
            series1.getData().add(new XYChart.Data(time, dateVSPackets.get(time)));
        }

        bitBarChart.getData().addAll(series1);
    }


    public void handleAddMACBt(ActionEvent e){
        ObservableList<String> temp = btMACs.getItems();
        String alertTitle = "MAC ID's Added";
        String alertText = "The following MAC ID's were added to the blacklist:\n"+ temp.toString();
        AlertBox.display(alertTitle, alertText);
        for (String mac: temp){
            macBlacklist.addToBlacklist(mac, "Bittorent");
        }
        macBlacklist.SaveFile("blacklist.txt");
        deviceBLStuff();
    }

    public void handleAddMACWeb(ActionEvent e){
        String alertTitle = "MAC ID's Added";
        String alertText = "The following MAC ID's were added to the blacklist:\n"+ macsBadWeb.toString();
        AlertBox.display(alertTitle, alertText);
        macBlacklist.ReadFile("blacklist.txt");
        for (String mac: macsBadWeb){
            macBlacklist.addToBlacklist(mac, "Visited Blacklisted Website ");
        }
        macBlacklist.SaveFile("blacklist.txt");
        deviceBLStuff();
    }

    public void webStuff(){
        badIPs.loadFromFile("websites.txt");
        newGen.compareBlacklist(pcap.getPacketInfo(),badIPs.getIPSet());
        visitedBadIPs = newGen.getBlacklistUsers();
        for (String ip : visitedBadIPs.keySet()){
            macsBadWeb.addAll(visitedBadIPs.get(ip));
        }
        ObservableList<String> webs = FXCollections.observableArrayList(badIPs.getWebSet());
        websites.setItems(webs);
        websites.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val,
                 String new_val) -> {websiteDetails(new_val);});
    }

    public void deviceBLStuff(){
        ObservableList<String> devices = FXCollections.observableArrayList(macBlacklist.getBlacklist().keySet());
        allMacs.setItems(devices);
        allMacs.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> ov, String old_val,
                 String new_val) -> {deviceDetails(new_val);});
    }

    public void deviceDetails(String mac){
        TreeMap<String, String> details = macBlacklist.getBlacklist().get(mac);
        String output = "Device Details:\n"+
                        "--------------\n\n"+
                        "MAC ID: "+ mac+"\n"+
                        "Time Added to Blacklist: "+ details.get("Time")+"\n"+
                        "Reason Added to Blacklist: "+ details.get("Reason");
        deviceDetLabel.setText(output);
    }

    public void websiteDetails(String web){
        String[] ips = badIPs.getIPByURL(web);

        String output = "Website Details:\n"+
                        "--------------\n\n"+
                        "Website: "+ web+"\n"+
                        "IP's:\n";
        HashSet<String> macs = new HashSet<String>();
        for(String ip: ips){
            output += ip+"\n";
            macs.addAll(visitedBadIPs.get(ip));
        }
        output += "\nMAC ID's that Visited this Website:\n";
        for (String mac:macs){
            output += mac+"\n";
        }
        webStats.setText(output);
    }

    public void addNewWebsite(ActionEvent e){
        String newWeb = newWebsite.getText();
        if(!newWeb.isEmpty() && newWeb != null){
            try {
                UrlToIP temp = new UrlToIP(newWeb);
                badIPs.addWebsite(temp);
                String alertTitle = "Website Added Added";
                String alertText = temp.getUrl() +" Successfully added.";
                AlertBox.display(alertTitle, alertText);
                webStuff();
                newWebsite.clear();
            } catch (MalformedURLException e1) {
                String alertTitle = "Malformed URL";
                String alertText = "Please use correct format.  Example:  'website.com'";
                AlertBox.display(alertTitle, alertText);
                newWebsite.clear();
            } catch (UnknownHostException e1) {
                String alertTitle = "Unknown Host";
                String alertText = "Sorry couldn't find that website.  Please try another.";
                AlertBox.display(alertTitle, alertText);
                newWebsite.clear();
            }
        }

    }
}


