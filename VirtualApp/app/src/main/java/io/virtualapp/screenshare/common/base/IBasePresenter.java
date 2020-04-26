package io.virtualapp.screenshare.common.base;

public interface IBasePresenter<V extends IBaseView> {

    void attachView(V view);

    void detachView(V view);

}
