package io.virtualapp.screenshare.module.connection.udp;

/**
 * Created by wt on 2018/7/11.
 */
public interface OnUdpConnectListener {
    void udpConnectSuccess(String ip, String ssCode);
    void udpDisConnect(String message);
}
