package io.virtualapp.screenshare.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseMvpActivity<T extends IBasePresenter> extends AppCompatActivity implements IBaseView {

    protected T presenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = injectPresenter();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        presenter.attachView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            presenter.detachView(this);
        }
    }

    protected abstract T injectPresenter();

}
