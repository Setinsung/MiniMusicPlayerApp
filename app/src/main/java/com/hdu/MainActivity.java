package com.hdu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hdu.api.ApiResponseCallback;
import com.hdu.api.FreeMusicAPI;
import com.hdu.bean.FreeMusicAPIResp;
import com.hdu.utils.AndroidDownloadManager;
import com.hdu.utils.AndroidDownloadManagerListener;

public class MainActivity extends AppCompatActivity {

    private Button enter_btn;
    private RadioGroup music_player_mode_rg;
    private SearchView music_search_sv;

    private int music_mode = 0; // 0 本地播放模式， 1 网络搜索模式
    private String search_txt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enter_btn = findViewById(R.id.enter_music_btn);
        music_player_mode_rg = findViewById(R.id.music_player_mode);
        music_search_sv = findViewById(R.id.music_search);


        music_search_sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(MainActivity.this, "搜索成功：" + query, Toast.LENGTH_SHORT).show();
                search_txt = query;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        music_player_mode_rg.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = findViewById(checkedId);
            if (selectedRadioButton.getText().equals("在线搜索模式")) {
                music_search_sv.setVisibility(SearchView.VISIBLE);
                music_mode = 1;
            } else {
                music_search_sv.setVisibility(SearchView.GONE);
                music_mode = 0;
            }
        });
        music_search_sv.setVisibility(SearchView.GONE);

        enter_btn.setOnClickListener(view -> {
            if(music_mode == 1 && search_txt.equals("")){
                Toast.makeText(MainActivity.this, "未输入任何搜索文本", Toast.LENGTH_SHORT).show();
            }
            Intent intent = new Intent(MainActivity.this, ContentActivity.class);
            intent.putExtra("music_mode", music_mode);
            intent.putExtra("search_txt", search_txt);
            startActivity(intent);
        });
    }

    private void testReq() {
        FreeMusicAPI.GetFreeMusics("起风了", 1, new ApiResponseCallback<FreeMusicAPIResp>() {
            @Override
            public void onSuccess(FreeMusicAPIResp response) {
                Log.d("Test", response.toString());
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void testDownload() {
        MainActivity mainActivity = this;
        new AndroidDownloadManager(this, "https://sharefs.ali.kugou.com/202401101252/aa1f1bbc8d917548cba6825855f09f99/v3/5c035cbf390fef6b536020a57749ef70/yp/full/a1000_u0_p409_s3779613493.mp3", "起风了.mp3")
                .setListener(new AndroidDownloadManagerListener() {
                    @Override
                    public void onPrepare() {
                        Log.d("Test", "onPrepare");
                    }

                    @Override
                    public void onSuccess(String path) {
                        Log.d("Test", "onSuccess >>>>" + path);
                        MediaScannerConnection.scanFile(
                                mainActivity,
                                new String[]{path},  // 你下载的文件的路径
                                null,
                                new MediaScannerConnection.OnScanCompletedListener() {
                                    @Override
                                    public void onScanCompleted(String path, Uri uri) {
                                        // 扫描完成后的回调
                                        Log.d("Test", "扫描完成 >>>> path:" + path + ",uri:" + uri);
                                    }
                                }
                        );
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        Log.e("Test", "onFailed", throwable);
                    }
                })
                .download();
    }
}