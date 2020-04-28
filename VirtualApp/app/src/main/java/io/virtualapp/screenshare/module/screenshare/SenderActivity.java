package io.virtualapp.screenshare.module.screenshare;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.virtualapp.R;
import io.virtualapp.screenshare.common.base.BaseMvpActivity;

public class SenderActivity extends BaseMvpActivity<SenderContract.IPresenter> implements SenderContract.IView {

    private static final String TAG = "SenderActivity";
    private static final int REQUEST_SCREEN_SHARE = 1;

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;

    private TextView tvInputTips;
    private EditText etSsCode;
    private Button connectButton;
    private Button screenShareButton;
    private TextView tvTips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);
        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        tvInputTips = findViewById(R.id.tvInputTips);
        etSsCode = findViewById(R.id.etSsCode);

        connectButton = findViewById(R.id.btnConnect);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectButton.getText().equals("连接设备")) {
                    presenter.connect(etSsCode.getText().toString());
                    Toast.makeText(SenderActivity.this, "连接中...", Toast.LENGTH_SHORT).show();
                } else {
                    // 如果正在传屏则停止传屏
                    if (mediaProjection != null) {
                        presenter.stopScreenShare();
                        mediaProjection.stop();
                        mediaProjection = null;
                        screenShareButton.setText("开始传屏");
                    }
                    presenter.disconnect();
                }
            }
        });

        screenShareButton = findViewById(R.id.btnScreenShare);
        screenShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaProjection != null) {
                    presenter.stopScreenShare();
                    mediaProjection.stop();
                    mediaProjection = null;
                    screenShareButton.setText("开始传屏");
                } else {
                    Intent captureIntent = mediaProjectionManager.createScreenCaptureIntent();
                    startActivityForResult(captureIntent, REQUEST_SCREEN_SHARE);
                }
            }
        });

        tvTips = findViewById(R.id.tvTips);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            // 如果应用退出
            presenter.stopScreenShare();
            presenter.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCREEN_SHARE) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "未获取录屏权限", Toast.LENGTH_SHORT).show();
                return;
            }
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);

            Toast.makeText(this, "正在传屏...", Toast.LENGTH_SHORT).show();

            presenter.startScreenShare(mediaProjection);
            screenShareButton.setText("停止传屏");

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "Key_Status = " + event.getAction());
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected SenderContract.IPresenter injectPresenter() {
        return new SenderPresenter();
    }

    @Override
    public void onConnectSuccess() {
        Toast.makeText(this, "连接成功", Toast.LENGTH_SHORT).show();
        tvInputTips.setVisibility(View.GONE);
        etSsCode.setVisibility(View.GONE);
        tvTips.setVisibility(View.GONE);
        screenShareButton.setVisibility(View.VISIBLE);
        connectButton.setText("断开连接");
    }

    @Override
    public void onDisconnectSuccess() {
        Toast.makeText(this, "断开连接成功", Toast.LENGTH_SHORT).show();
        screenShareButton.setVisibility(View.GONE);
        if (mediaProjection != null) {
            presenter.stopScreenShare();
            mediaProjection.stop();
            mediaProjection = null;
            screenShareButton.setText("开始传屏");
        }
        tvInputTips.setVisibility(View.VISIBLE);
        etSsCode.setVisibility(View.VISIBLE);
        tvTips.setVisibility(View.VISIBLE);
        connectButton.setText("连接设备");
    }

    public void clickStopScreenShareButton() {
        if (mediaProjection != null) {
            presenter.stopScreenShare();
            mediaProjection.stop();
            mediaProjection = null;
            screenShareButton.setText("开始传屏");
        }
    }
}
