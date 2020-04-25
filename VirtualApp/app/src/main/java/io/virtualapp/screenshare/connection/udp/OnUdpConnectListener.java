package io.virtualapp.screenshare.connection.udp;

/**
 * Created by wt on 2018/7/11.
 */
public interface OnUdpConnectListener {
    void udpConnectSuccess(String ip);
    void udpDisConnect(String message);
}
