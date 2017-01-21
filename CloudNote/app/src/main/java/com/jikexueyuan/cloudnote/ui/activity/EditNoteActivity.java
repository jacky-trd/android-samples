package com.jikexueyuan.cloudnote.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jikexueyuan.cloudnote.R;
import com.jikexueyuan.cloudnote.db.entity.NoteEntity;
import com.jikexueyuan.cloudnote.observable.NotesSaveToDbAndBmobObservable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observer;

/**
 * 添加，编辑笔记的activity
 */
public class EditNoteActivity extends BaseActivity implements Observer<NoteEntity> {

    //后退按钮
    @BindView(R.id.ivBack)
    ImageView mIvBack;
    //完成按钮（OK按钮）
    @BindView(R.id.ivDone)
    ImageView mIvDone;
    //笔记标题
    @BindView(R.id.etNoteTitle)
    EditText mEtNoteTitle;
    //笔记内容
    @BindView(R.id.etNoteContent)
    EditText mEtNoteContent;
    //添加笔记图片按钮
    @BindView(R.id.btnImage)
    LinearLayout mBtnImage;
    //添加笔记视频按钮
    @BindView(R.id.btnVideo)
    LinearLayout mBtnVideo;
    //笔记图片缩略图
    @BindView(R.id.ibImage)
    ImageButton mIbImage;
    //笔记视频缩略图
    @BindView(R.id.ibVideo)
    ImageButton mIbVideo;

    //添加或者编辑的dao笔记对象
    private NoteEntity mNoteEntity;
    //是否是新笔记的flag
    private boolean isNew;
    //图片视频文件
    private File mImageFile, mVideoFile;
    //图片视频Uri
    private Uri mImageUri, mVideoUri;
    //拍照的request code
    private static final int TAKE_IMAGE = 1;
    //视频的request code
    private static final int TAKE_VIDEO = 2;
    //本地数据库id
    private long id = 0L;

    //重写父类视图初始化，无逻辑
    @Override
    protected void initView() {
    }

    //重写父类数据初始化
    @Override
    protected void initData() {
        mNoteEntity = getIntent().getParcelableExtra("Note");
        if (mNoteEntity != null) {
            //不为空说明是编辑
            isNew = false;
            String title = mNoteEntity.getTitle();
            String content = mNoteEntity.getContent();
            String image = mNoteEntity.getImage();
            String video = mNoteEntity.getVideo();

            if (title != null) {
                mEtNoteTitle.setText(title);
            }
            if (content != null) {
                mEtNoteContent.setText(content);
            }
            if (image != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(image);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, Math.max(500, mIbImage.getWidth()), Math.max(500, mIbImage.getHeight()), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                mIbImage.setImageBitmap(bitmap);
            }
            if (video != null) {
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Images.Thumbnails.MICRO_KIND);
                bitmap = ThumbnailUtils.extractThumbnail(bitmap, Math.max(500, mIbVideo.getWidth()), Math.max(500, mIbVideo.getHeight()), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                mIbVideo.setImageBitmap(bitmap);
            }
        } else {
            //为新笔记
            isNew = true;
        }
    }

    //重写父类监听器初始化
    @Override
    protected void setListeners() {
    }

    //重写父类获取布局id
    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_note;
    }

    //监听器相应函数
    @OnClick({R.id.ivBack, R.id.ivDone, R.id.btnImage, R.id.btnVideo, R.id.ibImage, R.id.ibVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                //后退图标
                finish();
                break;
            case R.id.ivDone:
                //如果是新建笔记就新建实例，否则使用原来的实例
                NoteEntity noteEntity = isNew ? new NoteEntity() : mNoteEntity;
                String title = mEtNoteTitle.getText().toString().trim();
                String content = mEtNoteContent.getText().toString().trim();
                String summary = getSummary(content);
                if (mImageUri != null) {
                    String image = mImageUri.getPath();
                    noteEntity.setImage(image);
                }
                if (mVideoUri != null) {
                    String video = mVideoUri.getPath();
                    noteEntity.setVideo(video);
                }

                noteEntity.setTitle(title);
                noteEntity.setContent(content);
                noteEntity.setSummary(summary);
                noteEntity.setDate(getStringDate());

                //保存到本地数据库和bmob
                NotesSaveToDbAndBmobObservable.saveToDbAndBmob(noteEntity, isNew)
                        .subscribe(this);
                //显示进度条
                showProgress();
                break;
            case R.id.btnImage:
                //添加照片
                insertImage();
                break;
            case R.id.btnVideo:
                //添加视频
                insertVideo();
                break;
            case R.id.ibImage:
                //显示图片大图
                if (mImageUri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mImageUri, "image/*");
                    startActivity(intent);
                }
                break;
            case R.id.ibVideo:
                //播放视频
                if (mVideoUri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mVideoUri, "video/mpeg4");
                    startActivity(intent);
                }
                break;
        }
    }

    //根据内容获得概要，取前10个字符
    private String getSummary(String content) {
        String summary;
        if (content.length() >= 10) {
            summary = content.substring(0, 9);
        } else {
            summary = content;
        }
        return summary;
    }

    //获取字符串格式的时间
    private String getTime() {
        Calendar c = Calendar.getInstance();
        long time = c.getTime().getTime();
        return String.valueOf(time);
    }

    //获取当前时间
    public static String getStringDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);
        return dateString;
    }

    //插入图片
    private void insertImage() {
        mImageFile = new File(Environment.getExternalStorageDirectory(), getTime() + ".png");
        mImageUri = Uri.fromFile(mImageFile);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, TAKE_IMAGE);
    }

    //插入视频
    private void insertVideo() {
        mVideoFile = new File(Environment.getExternalStorageDirectory(), getTime() + ".mp4");
        mVideoUri = Uri.fromFile(mVideoFile);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);
        startActivityForResult(intent, TAKE_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_IMAGE) {
            //显示图片缩略图
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mImageUri.getPath(), options);
            int scale = Math.min(options.outWidth / mIbImage.getWidth(), options.outHeight / mIbImage.getHeight());
            scale = scale == 0 ? 1 : scale;
            options.inJustDecodeBounds = false;
            options.inSampleSize = scale;
            Bitmap bitmap = BitmapFactory.decodeFile(mImageUri.getPath(), options);
            mIbImage.setImageBitmap(bitmap);
        } else if (requestCode == TAKE_VIDEO) {
            //显示视频缩略图
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mVideoUri.getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, mIbVideo.getWidth(), mIbVideo.getHeight(), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            mIbVideo.setImageBitmap(bitmap);
        }
    }


    @Override
    public void onCompleted() {
        closeProgress();
        showShortToast(getString(R.string.save_success));
        finish();
    }

    @Override
    public void onError(Throwable throwable) {

    }

    @Override
    public void onNext(NoteEntity noteEntity) {
        Intent intent = new Intent();
        intent.putExtra("Note", noteEntity);
        setResult(RESULT_OK, intent);
    }
}
