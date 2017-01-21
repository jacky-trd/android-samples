package com.jikexueyuan.cloudnote.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jikexueyuan.cloudnote.CloudNoteApp;
import com.jikexueyuan.cloudnote.R;
import com.jikexueyuan.cloudnote.db.entity.Note;
import com.jikexueyuan.cloudnote.db.entity.NoteEntity;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 查看笔记的activity
 */
public class NoteDetailActivity extends BaseActivity {

    //笔记标题
    @BindView(R.id.tvNoteTitle)
    TextView mTvNoteTitle;
    //笔记内容
    @BindView(R.id.tvNoteContent)
    TextView mTvNoteContent;
    //编辑按钮
    @BindView(R.id.btnEdit)
    LinearLayout mBtnEdit;
    //删除按钮
    @BindView(R.id.btnDelete)
    LinearLayout mBtnDelete;
    //返回按钮
    @BindView(R.id.ivBack)
    ImageView mIvBack;
    //完成按钮
    @BindView(R.id.ivDone)
    ImageView mIvDone;
    //图片缩略图
    @BindView(R.id.ivImage)
    ImageView mIvImage;
    //视频缩略图
    @BindView(R.id.ibVideo)
    ImageButton mIbVideo;
    //图片路径字符串
    public String image;
    //视频路径字符串
    public String video;
    //选择的笔记对象
    private NoteEntity mNoteEntity;
    //request code
    private static final int EDIT_NOTE = 3;

    //重写父类视图初始化
    @Override
    protected void initView() {
        //完成按钮初始状态不可见，编辑后可见
        mIvDone.setVisibility(View.GONE);
        //初始化视图
        inflateView(mNoteEntity);
    }

    //初始化视图
    private void inflateView(NoteEntity noteEntity) {
        String title = noteEntity.getTitle();
        String content = noteEntity.getContent();
        image = noteEntity.getImage();
        video = noteEntity.getVideo();

        if (title != null) {
            mTvNoteTitle.setText(title);
        }
        if (content != null) {
            mTvNoteContent.setText(content);
        }
        if (image != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(image);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, Math.max(500, mIvImage.getWidth()), Math.max(500, mIvImage.getHeight()), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            mIvImage.setImageBitmap(bitmap);
        }
        if (video != null) {
            Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(video, MediaStore.Images.Thumbnails.MICRO_KIND);
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, Math.max(500, mIbVideo.getWidth()), Math.max(500, mIbVideo.getHeight()), ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            mIbVideo.setImageBitmap(bitmap);
        }

    }

    //重写父类数据初始化
    @Override
    protected void initData() {
        mNoteEntity = getIntent().getParcelableExtra("Note");
    }

    //重写父类监听器初始化
    @Override
    protected void setListeners() {}

    //重写父类获取布局id
    @Override
    protected int getLayoutId() {
        return R.layout.activity_note_detail;
    }

    //监听器响应函数
    @OnClick({R.id.btnEdit, R.id.btnDelete, R.id.ivBack, R.id.ivImage, R.id.ibVideo})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                //如果是编辑则打开编辑窗口
                Intent editIntent = new Intent(mContext, EditNoteActivity.class);
                editIntent.putExtra("Note", mNoteEntity);
                startActivityForResult(editIntent, EDIT_NOTE);
                break;
            case R.id.btnDelete:
                //删除则弹出确认对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("确认要删除这个笔记么？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Note note = new Note();
                        note.setObjectId(mNoteEntity.getObjId());
                        note.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Logger.i("bmob数据删除成功");
                                    //清除本地数据
                                    CloudNoteApp.getNoteEntityDao().delete(mNoteEntity);
                                    showShortToast(getString(R.string.del_success));
                                    finish();
                                } else {
                                    Logger.d("bmob数据删除失败：" + e.getMessage() + "," + e.getErrorCode());
                                    showShortToast(getString(R.string.del_bmob_err));
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
                break;
            case R.id.ivBack:
                //返回
                finish();
                break;
            case R.id.ivImage:
                //显示图片大图
                Uri mImageUri = Uri.parse("file://" + (String) image);
                if (mImageUri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mImageUri, "image/*");
                    startActivity(intent);
                }
                break;
            case R.id.ibVideo:
                //显示视频
                Uri mVideoUri = Uri.parse("file://" + (String) video);
                if (mVideoUri != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(mVideoUri, "video/mpeg4");
                    startActivity(intent);
                }
                break;
        }
    }

    //编辑后返回变更
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mNoteEntity=data.getParcelableExtra("Note");
            inflateView(mNoteEntity);
        }
    }
}
