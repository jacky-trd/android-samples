package com.jikexueyuan.taxibookingclient;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

import java.util.List;

/**
 * 起始地和目的地设置界面
 */
public class AddressFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, TextWatcher {

    //UI控件
    private ListView listView;
    private ImageButton backBtn;
    private Button setBtn;
    private TextView addressEtv;

    //该接口用于fragment退出时传递数据
    private OnFragmentInterfaceListener mListener;

    //城市名称字符串
    private String city;

    //adapter与listview配合使用
    private ArrayAdapter<String> arrd;

    //根据用户输入，获取推荐的起始地和目的地
    private SuggestionSearch suggestionSearch;

    //bundle的tag，用于标记保存的数据为城市名称
    private static final String ARG_CITY = "city";

    //测试用的log tag
    final String TEST = "test";

    public AddressFragment(){}

    //创建实例
    public static AddressFragment newInstance(String city){
        AddressFragment addressFragment = new AddressFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);
        addressFragment.setArguments(args);
        return addressFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //获取城市名称
        if (getArguments() != null) {
            city = getArguments().getString("city");
        }

        //初始化
        View root = inflater.inflate(R.layout.fragment_address, container, false);
        init(root);

        return root;
    }

    //初始化
    private void init(View root){
        //UI初始化
        listView = (ListView) root.findViewById(R.id.origin_listview);
        backBtn = (ImageButton) root.findViewById(R.id.back_button);
        backBtn.setOnClickListener(this);
        addressEtv = (TextView) root.findViewById(R.id.address_edittext);
        addressEtv.addTextChangedListener(this);
        setBtn = (Button) root.findViewById(R.id.set_button);
        setBtn.setOnClickListener(this);

        //adapter和listview初始化
        arrd = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(arrd);
        listView.setOnItemClickListener(this);

        //suggestionSearch初始化
        suggestionSearch = SuggestionSearch.newInstance();
        suggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
            //获得推荐地点结果的响应函数，更新listview
            @Override
            public void onGetSuggestionResult(SuggestionResult suggestionResult) {
                if (suggestionResult == null || suggestionResult.getAllSuggestions()==null){
                    return;
                }
                //获取推荐地点结果
                List<SuggestionResult.SuggestionInfo> allAddr = suggestionResult.getAllSuggestions();
                Log.d(TEST, "onGetSuggestionResult() called with: " + "suggestionResult size = [" + suggestionResult.getAllSuggestions().size() + "]");
                arrd.clear();
                for (int i=0;i<allAddr.size();i++){
                    arrd.add(allAddr.get(i).key);
                }
                //通知listview变化
                arrd.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInterfaceListener) {
            //绑定OnFragmentInterfaceListener
            mListener = (OnFragmentInterfaceListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

        System.out.println("Fragment onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("Fragment onDetach");
        suggestionSearch.destroy();
    }

    public void onExit(String data){
        if (mListener != null) {
            //退出时将数据返回
            mListener.onFragmentInterface(data, getTag());
        } else {
            System.out.println("mListener is null");
        }
    }

    //TextWatcher 方法
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d(TEST, "beforeTextChanged() called with: " + "s = [" + s + "], start = [" + start + "], count = [" + count + "], after = [" + after + "]");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d(TEST, "onTextChanged() called with: " + "s = [" + s + "], start = [" + start + "], before = [" + before + "], count = [" + count + "]");
        if (!TextUtils.isEmpty(s)){
            //请求推荐地点
            suggestionSearch.requestSuggestion((new SuggestionSearchOption())
                    .city(city)
                    .keyword(s.toString()));
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d(TEST, "afterTextChanged() called with: " + "s = [" + s + "]");
    }

    //FragmentListener接口
    public interface OnFragmentInterfaceListener{
        void onFragmentInterface(String data, String tag);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.set_button:
                //设置地点按钮输出结果，并返回
                onExit(addressEtv.getText().toString());
                getFragmentManager().popBackStack();
                break;
            case R.id.back_button:
                //返回按钮直接返回
                getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    //ListView列表的点击事件
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        addressEtv.setText("");
        addressEtv.setText(arrd.getItem(i));
    }
}
