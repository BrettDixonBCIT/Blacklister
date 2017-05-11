# Blacklister
Blacklister is a JavaFX application that uses the Jnetpcap library to search Pcap files for MAC ID's that use Bittorrent and access blacklisted websites.

With Blacklister you can do the following:
* Find the Devices that are initiating Bittorrent Handshakes.
* Find the Devices that are visiting Blacklisted websites.
* Blacklist new websites.
* View a list of Devices that have mis used your network.

## How to install
Before running Blacklister you must first make sure the JNetpcap 1.3 library is installed on your system.  It can be found at this url:
<http://jnetpcap.com/download>

On Windows machines you may also need to install WinPcap:  <https://www.winpcap.org/install/default.htm>

## How to Run
You can run Blacklister by running "src/Main.java".

Blacklister requires internet access inorder to Blacklist new websites.

It is recommended to use Pcap files under 250 MB.  Larger files may cause Blacklister to hang or possibly crash.


## About Authors
Blacklister was created by Dylan Chew and myself Brett Dixon.  It was created for BCIT's ACIT_2515 course.

If you have any questions you can send them to <brettdixon1@gmail.com>
