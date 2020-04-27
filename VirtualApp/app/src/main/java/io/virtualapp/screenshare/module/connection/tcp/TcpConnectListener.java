package io.virtualapp.screenshare.module.connection.tcp;

/**
 * TCP连接结果监听
 */
public interface TcpConnectListener {
    void onTcpConnectSuccess();
    void onTcpConnectFail(String message);
}
