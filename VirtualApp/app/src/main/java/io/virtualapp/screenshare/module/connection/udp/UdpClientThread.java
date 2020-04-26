package io.virtualapp.screenshare.module.connection.udp;

import android.util.Log;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import io.virtualapp.screenshare.common.utils.ByteUtils;

/**
 * Created by wt on 2018/7/11.
 * 基于udp的组网连接
 */
public class UdpClientThread extends Thread {
    static final String BROADCAST_IP = "224.0.0.1";
    //监听的端口号
    static final int BROADCAST_PORT = 15000;
    private InetAddress inetAddress = null;
    //服务端的局域网IP
    private static String ip;
    private static String ssCode;
    private OnUdpConnectListener mListener;

    public UdpClientThread(OnUdpConnectListener listener) {
        this.mListener = listener;
        this.start();
    }

    @Override
    public void run() {
        MulticastSocket multicastSocket = null;//多点广播套接字
        try {
            /**
             * 1.实例化MulticastSocket对象，并指定端口
             * 2.加入广播地址，MulticastSocket使用public void joinGroup(InetAddress mcastaddr)
             * 3.开始接收广播
             * 4.关闭广播
             */
            multicastSocket = new MulticastSocket(BROADCAST_PORT);
            inetAddress = InetAddress.getByName(BROADCAST_IP);
            Log.e("UdpClientThread", "udp server start");
            multicastSocket.joinGroup(inetAddress);
            byte buf[] = new byte[1024];
            DatagramPacket dp = new DatagramPacket(buf, buf.length);
            while (true) {
                multicastSocket.receive(dp);
                Log.e("UdpClientThread", "receive a msg");
                byte[] ipLength = new byte[4];
                byte[] ssCodeLength = new byte[4];
                System.arraycopy(buf, 0, ipLength, 0, 4);
                System.arraycopy(buf, 4, ssCodeLength, 0, 4);
                byte[] ipData = new byte[ByteUtils.bytesToInt(ipLength)];
                byte[] ssCodeData = new byte[ByteUtils.bytesToInt(ssCodeLength)];
                System.arraycopy(buf, 8, ipData, 0, ipData.length);
                System.arraycopy(buf, 8 + ipData.length, ssCodeData, 0, ssCodeData.length);
                ip = new String(ipData);
                ssCode = new String(ssCodeData);
                multicastSocket.leaveGroup(inetAddress);
                multicastSocket.close();
                mListener.udpConnectSuccess(ip, ssCode);
            }
        } catch (Exception e) {
            mListener.udpDisConnect(e.getMessage());
        } finally {
            Log.e("UdpClientThread", "udp server close");
        }
    }
}
