package com.hdu;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdu.adapter.MusicListAdaper;
import com.hdu.api.ApiResponseCallback;
import com.hdu.api.FreeMusicAPI;
import com.hdu.bean.FreeMusicAPIResp;
import com.hdu.bean.MusicBean;
import com.hdu.bean.RespMusicItem2;
import com.hdu.utils.AndroidDownloadManager;
import com.hdu.utils.AndroidDownloadManagerListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ContentActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView op_next_iv, op_play_iv, op_last_iv, op_download_iv, music_bottom_ico_iv;
    private TextView bottom_singer_name_tv, bottom_song_name_tv;
    private ImageView bottom_music_img_iv;
    private RecyclerView music_list_rv;

    // 音乐播放器模式
    private int music_mode = 0;

    // 查询文本
    private String search_txt = "";

    // 搜索页码
    private int page = 1;

    private boolean req_isfinish = false;

    // 歌曲id
    private int id = 0;


    // 歌曲数据列表
    List<MusicBean> music_data_list;
    private MusicListAdaper musicListAdaper;

    // 记录当前正在播放的音乐的位置
    int currentPlayPosition = -1;

    // 记录暂停音乐时进度条的位置
    int currentPausePositionInSong = 0;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        initView();

        // 创建和设置适配器
        musicListAdaper = new MusicListAdaper(this, music_data_list);
        music_list_rv.setAdapter(musicListAdaper);
        // 设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        music_list_rv.setLayoutManager(linearLayoutManager);

        // 根据模式加载music数据
        if (music_mode == 1 && !search_txt.equals("")) {
            // 滚动到底部翻页
            music_list_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
                    if (req_isfinish) {
                        Toast.makeText(ContentActivity.this, "没有更多结果了！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (lastCompletelyVisibleItemPosition == layoutManager.getItemCount() - 1) {
                        //滑动到底部
                        page++;
                        loadWebMusicData();
                    }
                }
            });
            // 显示下载按钮
            op_download_iv.setVisibility(ImageView.VISIBLE);
            loadWebMusicData();
        } else {
            loadLocalMusicData();
        }

        setMusicItemEventListener();
    }

    /**
     * 设置每一项的点击事件
     */
    private void setMusicItemEventListener() {
        musicListAdaper.setOnItemClickListener((view, position) -> {
            if (currentPlayPosition == -1) {
                music_bottom_ico_iv.setVisibility(ImageView.GONE);
                bottom_music_img_iv.setVisibility(ImageView.VISIBLE);
            }
            currentPlayPosition = position;
            MusicBean musicBean = music_data_list.get(position);
            playSelectedMusic(musicBean);
        });
    }


    /**
     * 播放指定的音乐
     */
    private void playSelectedMusic(MusicBean musicBean) {
        // 设置底部显示
        bottom_singer_name_tv.setText(musicBean.getSinger());
        bottom_song_name_tv.setText(musicBean.getSong());

        if (musicBean.getImg() != null)
//            bottom_music_img_iv.setImageURL(musicBean.getImg());
            Glide.with(this).load(musicBean.getImg()).into(bottom_music_img_iv);

        else
            Glide.with(this).load(R.drawable.music_item_img_default).into(bottom_music_img_iv);
        stopMusic();
        // 重置播放器为新路径
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(musicBean.getPath());
            playMusic();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放音乐
     */
    private void playMusic() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            if (currentPausePositionInSong == 0) {
                // 非暂停状态，从头播放
                try {
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                // 暂停转播放，跳转到暂停位置
                mediaPlayer.seekTo(currentPausePositionInSong);
                mediaPlayer.start();
            }
            op_play_iv.setImageResource(R.drawable.op_pause);
        }
    }

    /**
     * 暂停音乐
     */
    private void pauseMusic() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPausePositionInSong = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
            op_play_iv.setImageResource(R.drawable.op_play);
        }
    }

    /**
     * 停止播放
     */
    private void stopMusic() {
        if (mediaPlayer != null) {
            currentPausePositionInSong = 0;
            op_play_iv.setImageResource(R.drawable.op_play);
            if (currentPlayPosition != -1) {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
                mediaPlayer.stop();
            }
        }

    }

    /**
     * 加载网络请求得到的音乐数据到集合
     */
    private void loadWebMusicData() {
        FreeMusicAPI.GetFreeMusics(search_txt, page, new ApiResponseCallback<FreeMusicAPIResp>() {
            @Override
            public void onSuccess(FreeMusicAPIResp response) {
                List<RespMusicItem2> data = response.getData();
                if (data.size() == 0) {
                    req_isfinish = true;
                }

                for (RespMusicItem2 respMusicItem : data) {
                    if (respMusicItem.getUrl() == null || respMusicItem.getUrl().equals("null"))
                        continue;
                    id++;
                    MusicBean musicBean = new MusicBean(
                            String.valueOf(id),
                            respMusicItem.getTitle(),
                            respMusicItem.getAuthor(),
                            respMusicItem.getTitle(),
                            "none",
                            respMusicItem.getUrl(),
                            respMusicItem.getPic()
                    );
                    music_data_list.add(musicBean);
                }
                // 在主线程中更新UI
                runOnUiThread(() -> {
                    Toast.makeText(ContentActivity.this, "搜索查询请求成功，这是第" + page + "页！", Toast.LENGTH_SHORT).show();
                    // 数据源变化，通知adapter更新
                    musicListAdaper.notifyDataSetChanged();
                });
            }

            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                Toast.makeText(ContentActivity.this, "搜索查询请求失败！error:" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadSelectedMusic(String url, String name) {
        Context context = this;
        copyStr(url);
        new AndroidDownloadManager(context, url, name + ".mp3")
                .setListener(new AndroidDownloadManagerListener() {
                    @Override
                    public void onPrepare() {
                        Log.d("Test", "onPrepare");
                        runOnUiThread(() -> {
                            Toast.makeText(ContentActivity.this, "下载准备中，已将链接复制到剪贴板...", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onSuccess(String path) {
                        Log.d("Test", "onSuccess >>>>" + path);
                        runOnUiThread(() -> {
                            Toast.makeText(ContentActivity.this, "下载成功，文件路径：" + path, Toast.LENGTH_SHORT).show();
                        });
                        MediaScannerConnection.scanFile(
                                context,
                                new String[]{path},  // 你下载的文件的路径
                                null,
                                (path1, uri) -> {
                                    // 扫描完成后的回调
                                    Log.d("Test", "扫描完成 >>>> path:" + path1 + ",uri:" + uri);
                                }
                        );
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        runOnUiThread(() -> {
                            Toast.makeText(ContentActivity.this, "下载失败，error:" + throwable.toString(), Toast.LENGTH_SHORT).show();
                        });
                        Log.e("Test", "onFailed", throwable);
                    }
                })
                .download();
    }

    /**
     * 加载本地存储中的音乐文件到集合
     */
    private void loadLocalMusicData() {
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.IS_MUSIC);
        int id = 0;
        while (cursor.moveToNext()) {
            int title_index = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            String song_name = (title_index >= 0) ? cursor.getString(title_index) : "none";
            if (song_name.equals("Hangouts Message") || song_name.equals("Hangouts Call"))
                continue;
            int artist_index = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            String singer_name = (artist_index >= 0) ? cursor.getString(artist_index) : "none";

            int album_index = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            String album_name = (album_index >= 0) ? cursor.getString(album_index) : "none";

            id++;
            String sid = String.valueOf(id);

            int data_index = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            String data_path = (data_index >= 0) ? cursor.getString(data_index) : "none";

            int duration_index = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            Long duration = (duration_index >= 0) ? cursor.getLong(duration_index) : 0;
//            if (duration == 0) continue;
            SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
            String duration_time = sdf.format(duration);

            MusicBean musicBean = new MusicBean(sid, song_name, singer_name, album_name, duration_time, data_path);
            music_data_list.add(musicBean);
        }

        // 数据源变化，通知adapter更新
        musicListAdaper.notifyDataSetChanged();
    }

    /**
     * 初始化页面
     */
    private void initView() {
        // 获取传值
        Intent intent = getIntent();
        music_mode = intent.getIntExtra("music_mode", 0);
        search_txt = intent.getStringExtra("search_txt");

        // 准备控件
        op_next_iv = findViewById(R.id.music_bottom_op_next);
        op_play_iv = findViewById(R.id.music_bottom_op_play);
        op_last_iv = findViewById(R.id.music_bottom_op_last);
        op_download_iv = findViewById(R.id.music_bottom_op_download);
        music_bottom_ico_iv = findViewById(R.id.music_bottom_ico);
        bottom_singer_name_tv = findViewById(R.id.music_bottom_singer_name);
        bottom_song_name_tv = findViewById(R.id.music_bottom_song_name);
        bottom_music_img_iv = findViewById(R.id.music_bottom_img);
        music_list_rv = findViewById(R.id.music_list);
        op_next_iv.setOnClickListener(this);
        op_play_iv.setOnClickListener(this);
        op_last_iv.setOnClickListener(this);
        op_download_iv.setOnClickListener(this);

        // 创建需要的其它对象
        mediaPlayer = new MediaPlayer();
        music_data_list = new ArrayList<>();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.music_bottom_op_last) {
            if (currentPlayPosition == 0) {
                currentPlayPosition = music_data_list.size() - 1;
            } else
                currentPlayPosition = currentPlayPosition - 1;
            MusicBean MusicBean = music_data_list.get(currentPlayPosition);
            playSelectedMusic(MusicBean);
        } else if (viewId == R.id.music_bottom_op_next) {
            if (currentPlayPosition == music_data_list.size() - 1) {
                currentPlayPosition = 0;
            } else
                currentPlayPosition = currentPlayPosition + 1;
            MusicBean MusicBean = music_data_list.get(currentPlayPosition);
            playSelectedMusic(MusicBean);
        } else if (viewId == R.id.music_bottom_op_play) {
            // 未选中音乐
            if (currentPlayPosition == -1) {
                Toast.makeText(this, "请选择想要播放的音乐", Toast.LENGTH_SHORT).show();
                return;
            }
            // 选中音乐
            if (mediaPlayer.isPlaying()) {
                // 播放状态暂停
                pauseMusic();
            } else {
                // 暂停状态播放
                playMusic();
            }
        } else if (viewId == R.id.music_bottom_op_download) {
            // 下载
            if (currentPlayPosition == -1) {
                Toast.makeText(this, "未选择任何音乐", Toast.LENGTH_SHORT).show();
                return;
            }
            MusicBean musicBean = music_data_list.get(currentPlayPosition);
            downloadSelectedMusic(musicBean.getPath(), musicBean.getSong());
        }
    }

    /**
     * 复制内容到剪切板
     *
     * @param copyStr
     * @return
     */
    private boolean copyStr(String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 销毁停止播放
        stopMusic();
    }
}





























