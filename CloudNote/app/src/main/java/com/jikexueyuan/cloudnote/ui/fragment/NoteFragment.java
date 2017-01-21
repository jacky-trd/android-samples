package com.jikexueyuan.cloudnote.ui.fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.jdsjlzx.interfaces.OnItemClickListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;
import com.jikexueyuan.cloudnote.CloudNoteApp;
import com.jikexueyuan.cloudnote.R;
import com.jikexueyuan.cloudnote.adapter.NoteRecyclerAdapter;
import com.jikexueyuan.cloudnote.db.entity.Note;
import com.jikexueyuan.cloudnote.db.entity.NoteEntity;
import com.jikexueyuan.cloudnote.observable.NotesFromDatabaseObservable;
import com.jikexueyuan.cloudnote.observable.NotesSyncToBmobObservable;
import com.jikexueyuan.cloudnote.ui.activity.EditNoteActivity;
import com.jikexueyuan.cloudnote.ui.activity.NoteDetailActivity;
import com.jikexueyuan.cloudnote.utils.NetUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import rx.Observer;
import rx.functions.Action0;

/**
 * 显示笔记的activity
 */
public class NoteFragment extends BaseFragment implements Observer<List<NoteEntity>> {

    //用户头像
    @BindView(R.id.ivMine)
    ImageView mIvMine;
    //同步按钮
    @BindView(R.id.ivSync)
    ImageView mIvSync;
    //笔记列表
    @BindView(R.id.rvList)
    LRecyclerView mRvList;
    //添加笔记按钮
    @BindView(R.id.fabAddNote)
    FloatingActionButton mFabAddNote;

    //笔记列表变量
    private List<NoteEntity> mNoteEntities = new ArrayList<>();
    //选择的笔记变量
    private NoteEntity selectedNoteEntity;

    private LRecyclerViewAdapter mLRecyclerViewAdapter;
    private NoteRecyclerAdapter mDataAdapter;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void init() {

        mDataAdapter = new NoteRecyclerAdapter(mContext);
        mDataAdapter.setDataList(mNoteEntities);
        mLRecyclerViewAdapter = new LRecyclerViewAdapter(mContext, mDataAdapter);

        mRvList.setAdapter(mLRecyclerViewAdapter);
        //设置固定大小
        mRvList.setHasFixedSize(true);
        //创建线性布局
        mLayoutManager = new LinearLayoutManager(mContext);
        //垂直方向
        mLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        //给RecyclerView设置布局管理器
        mRvList.setLayoutManager(mLayoutManager);
        //禁用下拉刷新
        mRvList.setPullRefreshEnabled(false);
        //设置点击事件
        mLRecyclerViewAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int i) {
                //显示笔记信息界面
                NoteEntity noteEntity = mDataAdapter.getDataList().get(i);
                Intent showIntent = new Intent(mContext, NoteDetailActivity.class);
                showIntent.putExtra("Note", noteEntity);
                startActivity(showIntent);
            }

            @Override
            public void onItemLongClick(View view, int i) {
                //长按删除笔记
                selectedNoteEntity = mDataAdapter.getDataList().get(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("确认要删除这个笔记么？");
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Note note = new Note();
                        note.setObjectId(selectedNoteEntity.getObjId());
                        note.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Logger.i("bmob数据删除成功");
                                    //清除本地数据
                                    CloudNoteApp.getNoteEntityDao().delete(selectedNoteEntity);
                                    showShortToast(getString(R.string.del_success));
                                    //更新同步
                                    synvToDb();
                                    NotesSyncToBmobObservable.syncToBmob()
                                            .subscribe();
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
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_note;
    }

    //监听器响应函数
    @OnClick({R.id.ivMine, R.id.ivSync})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivMine:
                break;
            case R.id.ivSync:
                //同步按钮动画效果
                mIvSync.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.anim_sync));
                //从bmob同步到本地
                synvToDb();
                //从本地同步到bmob
                NotesSyncToBmobObservable.syncToBmob()
                        .doOnCompleted(new Action0() {
                            @Override
                            public void call() {
                                if (NetUtils.isNetworkConnected(mContext)) {
                                    showShortToast(mContext.getString(R.string.sync_success));
                                } else {
                                    showShortToast(mContext.getString(R.string.check_connect));
                                }
                            }
                        })
                        .subscribe();
                break;
        }
    }

    @OnClick(R.id.fabAddNote)
    public void onClick() {
        //进入添加页面
        startActivity(new Intent(mContext, EditNoteActivity.class));
    }


    @Override
    public void onResume() {
        super.onResume();
        NotesFromDatabaseObservable.getObservableFromDB()
                .subscribe(this);
    }

    @Override
    public void onCompleted() {
        //更新配适器数据
        mDataAdapter.setDataList(mNoteEntities);
    }

    @Override
    public void onError(Throwable throwable) {}

    @Override
    public void onNext(List<NoteEntity> noteEntities) {
        //从数据库获取本地数据
        this.mNoteEntities = noteEntities;
    }

    //从bmob同步到本地
    public void synvToDb() {
        BmobQuery<Note> query = new BmobQuery<Note>();
        query.addWhereEqualTo("userObjId", BmobUser.getCurrentUser().getUsername());
        query.setLimit(50);
        query.findObjects(new FindListener<Note>() {
            @Override
            public void done(List<Note> list, BmobException e) {
                if (e == null) {
                    Logger.d("共查询到：" + list.size());
                    for (Note note : list) {
                        //同步到本地
                        NoteEntity entity = note.convert2NoteEntity();
                        entity.setObjId(note.getObjectId());
                        CloudNoteApp.getNoteEntityDao().insertOrReplace(entity);
                    }
                    //更新ui
                    NotesFromDatabaseObservable.getObservableFromDB()
                            .subscribe(NoteFragment.this);
                } else {
                    Logger.d("bmob查询失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
}
