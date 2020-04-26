package io.virtualapp.screenshare.module.screenshare;

import android.media.projection.MediaProjection;

import io.virtualapp.screenshare.common.base.IBasePresenter;
import io.virtualapp.screenshare.common.base.IBaseView;

public class SenderContract {
    public interface IPresenter extends IBasePresenter<IView> {
        void connect(String userSsCode);
        void disconnect();
        void startScreenShare(MediaProjection mediaProjection);
        void stopScreenShare();
    }

    public interface IView extends IBaseView {
        void onConnectSuccess();
        void onDisconnectSuccess();
    }
}
