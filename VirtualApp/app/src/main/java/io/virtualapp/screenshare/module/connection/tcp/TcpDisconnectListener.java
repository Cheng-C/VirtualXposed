package io.virtualapp.screenshare.module.connection.tcp;

/**
 * TCP断开连接结果监听
 */
public interface TcpDisconnectListener {
    void onTcpDisconnectSuccess();
    void onTcpDisconnectFail(String message);
}
