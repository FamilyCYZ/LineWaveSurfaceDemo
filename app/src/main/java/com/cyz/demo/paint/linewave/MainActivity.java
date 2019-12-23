package com.cyz.demo.paint.linewave;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cyz.demo.paint.linewave.model.WaveLine;
import com.cyz.demo.paint.linewave.presenter.DataUtils;
import com.cyz.demo.paint.linewave.presenter.RecordManager;
import com.cyz.demo.paint.linewave.views.WaveLineView;

import java.io.File;

/**
 * @author：cyz 参考https://github.com/UCodeUStory/RecordVoiceView的效果三完成
 * 需要自己打开录音权限，否则不会根据音量变化
 */
public class MainActivity extends AppCompatActivity {

    private WaveLineView mWaveLineView;
    private RecordManager mRecordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWaveLineView = findViewById(R.id.waveLineView);

        // 画出一条波动平稳的线
        WaveLine stableLine = new WaveLine(5f, 10f, 4f, 0.1f);
        stableLine.setmLineWidth(3);
        stableLine.setLineColors(DataUtils.lineStableColors);
        mWaveLineView.addLine(stableLine);

        // 画出一条波动较大的线
        WaveLine waveLine = new WaveLine(5f, 10f, 4f, 2f);
        waveLine.setLineColors(DataUtils.lineWaveColors);
        waveLine.setmLineWidth(6);
        mWaveLineView.addLine(waveLine);

        // 检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

            if (getExternalCacheDir() != null) {

                mRecordManager = new RecordManager(new File(getExternalCacheDir().getPath() + "/" + System.currentTimeMillis() + ".amr"));
                mRecordManager.setOnVolume(new RecordManager.OnVolume() {
                    @Override
                    public void onVolume(int db) {
                        mWaveLineView.setVoiceVolume(db);
                    }
                });
                mRecordManager.startRecord();
            }
        }
    }
}
