package io.virtualapp.screenshare.module.screenshare;

import android.app.Activity;
import android.media.projection.MediaProjection;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.virtualapp.screenshare.common.base.BasePresenter;
import io.virtualapp.screenshare.module.connection.tcp.TcpConnectListener;
import io.virtualapp.screenshare.module.connection.tcp.TcpConnection;
import io.virtualapp.screenshare.module.connection.tcp.TcpDisconnectListener;
import io.virtualapp.screenshare.module.connection.udp.OnUdpConnectListener;
import io.virtualapp.screenshare.module.connection.udp.UdpClientThread;
import io.virtualapp.screenshare.module.sender.ScreenSender;

public class SenderPresenter extends BasePresenter<SenderContract.IView> implements SenderContract.IPresenter {

    private static final String TAG = "SenderPresenter";

    private ExecutorService executorService = null;
    private ScreenSender screenSender;
    private TcpConnection tcpConnection = TcpConnection.getInstance();

    private UdpClientThread clientThread;
    private Handler updateUiHandler = new Handler();

    public SenderPresenter() {
        Log.i(TAG, "new SenderPresenter");
        initThreadPool();
    }

    @Override
    public void connect(String userSsCode) {
        Log.i(TAG, "connect");
        clientThread = new UdpClientThread(new OnUdpConnectListener() {
            @Override
            public void udpConnectSuccess(String ip, String ssCode) {
                clientThread.interrupt();
                // 传屏码不对
                if (!userSsCode.equals(ssCode)) {
                    Log.i(TAG, "udpConnectSuccess: 传屏码不对");
                    return;
                }
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {

                        tcpConnection.connect(ip, 9988, new TcpConnectListener() {

                            @Override
                            public void onTcpConnectSuccess() {
                                updateUiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        view.onConnectSuccess();
                                    }
                                });

                            }

                            @Override
                            public void onTcpConnectFail(String message) {

                            }
                        });
                    }
                });
            }

            @Override
            public void udpDisConnect(String message) {

            }
        });

    }

    @Override
    public void disconnect() {
        Log.i(TAG, "disconnect");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                tcpConnection.disconnect(new TcpDisconnectListener() {
                    @Override
                    public void onTcpDisconnectSuccess() {
                        updateUiHandler.post(() -> view.onDisconnectSuccess());
                    }

                    @Override
                    public void onTcpDisconnectFail(String message) {

                    }
                });
            }
        });

    }

    @Override
    public void startScreenShare(MediaProjection mediaProjection) {
        Log.i(TAG, "startScreenShare");
        screenSender = new ScreenSender((Activity)view, mediaProjection);
        executorService.execute(screenSender);
    }

    @Override
    public void stopScreenShare() {
        Log.i(TAG, "stopScreenShare");
        if (screenSender != null) {
            screenSender.stop();
            screenSender = null;
        }
    }

    private void initThreadPool() {
        Log.i(TAG, "initThreadPool");
        int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        int KEEP_ALIVE_TIME = 1;
        TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
        BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<Runnable>();
        executorService = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES * 2,
                        KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, taskQueue);
    }

}
