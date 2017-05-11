package blacklister_model;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by Brett Dixon on 2017-04-08.
 *
 * Class uses java.net library to find the ips associated with a URL.
 */
public class UrlToIP {
    private InetAddress[] machines;
    private String url = "";
    private String[] ips;


    public static void main(String[] args) throws UnknownHostException {
        InetAddress[] machines = InetAddress.getAllByName("reddit.com");
        ArrayList<String> temp = new ArrayList<>();
        for (InetAddress address : machines){
            temp.add(address.getHostAddress().toString().trim());
        }
        System.out.println(temp.toString());
    }

    public UrlToIP(String newurl) throws MalformedURLException, UnknownHostException {
        this.url = newurl;
        machines = InetAddress.getAllByName(this.url);
        ArrayList<String> temp = new ArrayList<>();
        for(InetAddress address: machines){
            temp.add(address.getHostAddress().toString().trim());
        }
        ips = temp.toArray(new String[0]);
    }

    public String[] getIPs(){
        return ips;
    }

    public String getUrl() {
        return url;
    }


}
