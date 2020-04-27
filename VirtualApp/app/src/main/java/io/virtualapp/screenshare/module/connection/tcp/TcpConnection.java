package io.virtualapp.screenshare.module.connection.tcp;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


public class TcpConnection {
    private static final String TAG = "TcpConnection";
    private Socket socket = null;

    private OutputStream outputStream;
    private InputStream inputStream;

    private TcpConnection() { }

    private static class SingletonHolder {
        private static TcpConnection instance = new TcpConnection();
    }

    public static TcpConnection getInstance() {
        return SingletonHolder.instance;
    }

    public void connect(String ip, int port, TcpConnectListener tcpConnectListener) {
        Log.i(TAG, "connect");
        try {
            if (socket != null) {
                return;
            }
            socket = new Socket(ip, port);
            Log.i(TAG, "connect: 连接成功socket.isConnected()：" + socket.isConnected());
            Log.i(TAG, "connect: 连接成功socket.isBound()：" + socket.isBound());
            Log.i(TAG, "connect: 连接成功socket.isClosed()：" + socket.isClosed());
            Log.i(TAG, "connect: 连接成功socket.isInputShutdown()：" + socket.isInputShutdown());
            Log.i(TAG, "connect: 连接成功socket.isOutputShutdown()：" + socket.isOutputShutdown());

            // read阻塞时间（超过时间抛出异常）
            socket.setSoTimeout(5 * 1000);

            outputStream = socket.getOutputStream();
            inputStream = socket.getInputStream();

            Log.i(TAG, "connect: TCP连接成功");
            if (tcpConnectListener != null) {
                tcpConnectListener.onTcpConnectSuccess();
            }
        } catch (IOException e) {
            Log.i(TAG, "connect: TCP连接失败");
            if (tcpConnectListener != null) {
                tcpConnectListener.onTcpConnectFail(e.getMessage());
            }
            e.printStackTrace();
        }
    }

    public void disconnect(TcpDisconnectListener tcpDisconnectListener) {
        Log.i(TAG, "disconnect");
        try {
            if (socket == null) {
                return;
            }
            socket.shutdownOutput();
            Log.i(TAG, "disconnect: 已shundownOutput");
            // 接收端关闭socket
            byte[] bytes = new byte[4];
            if (inputStream.read(bytes) == -1) {
                Log.i(TAG, "disconnect: read-1");

                socket.close();
                Log.i(TAG, "disconnect: 断开连接成功socket.isConnected()：" + socket.isConnected());
                Log.i(TAG, "disconnect: 断开连接成功socket.isBound()：" + socket.isBound());
                Log.i(TAG, "disconnect: 断开连接成功socket.isClosed()：" + socket.isClosed());
                Log.i(TAG, "disconnect: 断开连接成功socket.isInputShutdown()：" + socket.isInputShutdown());
                Log.i(TAG, "disconnect: 断开连接成功socket.isOutputShutdown()：" + socket.isOutputShutdown());
                socket = null;

                Log.i(TAG, "disconnect: TCP断开连接成功");
                if (tcpDisconnectListener != null) {
                    tcpDisconnectListener.onTcpDisconnectSuccess();
                }
            } else {
                Log.i(TAG, "disconnect: TCP断开连接失败");
                if (tcpDisconnectListener != null) {
                    tcpDisconnectListener.onTcpDisconnectFail("");
                }
            }
        } catch (IOException e) {
            Log.i(TAG, "disconnect: TCP断开连接失败,直接关闭socket");
            // 直接关闭
            try {
                if (socket != null) {
                    socket.close();
                    socket = null;
                    if (tcpDisconnectListener != null) {
                        tcpDisconnectListener.onTcpDisconnectSuccess();
                    }
                }
            } catch (IOException ex) {
                if (tcpDisconnectListener != null) {
                    tcpDisconnectListener.onTcpDisconnectFail(e.getMessage());
                }
                ex.printStackTrace();
            }
        }
    }

    public void sendData(byte[] data, TcpStatusListener tcpStatusListener) {
        Log.i(TAG, "sendData" + data.length);
        try {
            if (outputStream != null) {
                outputStream.write(data);
                outputStream.flush();
            }
        } catch (IOException e) {
            // 与接收器连接断开
            Log.i(TAG, "sendData: 连接断开");
            if (tcpStatusListener != null) {
                tcpStatusListener.onDisconnect();
            }
            e.printStackTrace();
        }
    }

}
