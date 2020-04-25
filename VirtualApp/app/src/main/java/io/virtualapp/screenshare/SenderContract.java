package io.virtualapp.screenshare;

import android.media.projection.MediaProjection;

import io.virtualapp.screenshare.base.IBasePresenter;
import io.virtualapp.screenshare.base.IBaseView;

public class SenderContract {
    public interface IPresenter extends IBasePresenter<IView> {
        void connect();
        void disconnect();
        void startScreenShare(MediaProjection mediaProjection);
        void stopScreenShare();
    }

    public interface IView extends IBaseView {
        void onConnectSuccess();
        void onDisconnectSuccess();
    }
}
