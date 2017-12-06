package com.linyuzai.demo4easonrecyclerview;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.linyuzai.easonrecyclerview.EasonRecyclerView;
import com.linyuzai.easonrecyclerview.IndexBar;
import com.linyuzai.easonrecyclerview.index.Indexable;
import com.linyuzai.easonrecyclerview.IndexableAdapter;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String DATA1 = "微博的热搜不是说面试时不能提五险更不能提加班就是岁还单身面试被刷这可把我们吓死了现在找工作连面试这关都这么难了吗单身的我是不是得先找到对象再考虑辞职了" +
            "好友发了条朋友圈内容是这样的" +
            "同事老板我的电脑每次开机都得半个小时给我换台电脑吧" +
            "老板你就不会每天早来半个小时上班吗" +
            "还有这种操作哎跟老板比我们终究还是嫩了点";

    String DATA2 = "这年头谁没跟老板斗智斗勇过如果你赢了那么恭喜你老板说你可以拿单子走人了如果你输了那还是乖乖上你的班吧" +
            "多少职场中人无数次想拍桌子走人可为了活着最后还是乖乖坐下了刚大学毕业那会儿我们总是谈梦想谈未来可现在我们被房贷车贷压得喘不过气我们不谈梦想我们只谈现实这样老板可就不高兴了眯着眼笑容可掬地说年轻人还是要往前看的听过了现在未来前途一片光明好好干公司不会亏待你的" +
            "年底了又有一部分人正在蠢蠢欲动不知道今年的年终奖能拿多少要是再那么一点我就真的走人了殊不知同样的话去年也说过" +
            "我们到底在为什么而将就";

    EasonAdapter adapter;
    List<IndexData> list1;
    List<IndexData> list2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        //addContentView();
        IndexBar.Config config = null;//new IndexBar.Config();
        //config.background(Color.LTGRAY).
        final EasonRecyclerView eason = new EasonRecyclerView(this);
        setContentView(eason, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        eason.setLayoutManager(new LinearLayoutManager(this));
        eason.getSmartRefreshLayout().setRefreshHeader(new ClassicsHeader(this));
        eason.getSmartRefreshLayout().setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(final RefreshLayout refreshlayout) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshlayout.finishRefresh(true);
                        adapter.setDatas(list2);
                    }
                }, 2000);
            }
        });
        eason.setOverlayAtCenter();
        adapter = new EasonAdapter();
        adapter.setOnItemContentClickListener(new IndexableAdapter.OnItemContentClickListener<IndexData>() {
            @Override
            public void onItemClick(View v, int originalPosition, int currentPosition, IndexData entity) {
                Toast.makeText(MainActivity.this, entity.name, Toast.LENGTH_SHORT).show();
            }
        });
        eason.setAdapter(adapter);
        list1 = new ArrayList<>();
        list2 = new ArrayList<>();

        for (int i = 0; i < DATA1.length(); i++) {
            list1.add(new IndexData(String.valueOf(DATA1.charAt(i))));
        }
        for (int i = 0; i < DATA2.length(); i++) {
            list2.add(new IndexData(String.valueOf(DATA2.charAt(i))));
        }
        adapter.setDatas(list1);
    }

    class IndexData implements Indexable {

        private String name;

        IndexData(String name) {
            this.name = name;
        }

        @Override
        public String getFieldIndexBy() {
            return name;
        }

        @Override
        public void setFieldIndexBy(String indexField) {
            this.name = indexField;
        }

        @Override
        public void setFieldPinyinIndexBy(String pinyin) {

        }
    }

    class EasonAdapter extends IndexableAdapter<IndexData> {

        @Override
        public RecyclerView.ViewHolder onCreateTitleViewHolder(ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(5, 5, 5, 5);
            textView.setBackgroundColor(Color.LTGRAY);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new EasonViewHolder(textView);
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(15, 15, 15, 15);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new EasonViewHolder(textView);
        }

        @Override
        public void onBindTitleViewHolder(RecyclerView.ViewHolder holder, String indexTitle) {
            ((TextView) holder.itemView).setText(indexTitle);
        }

        @Override
        public void onBindContentViewHolder(RecyclerView.ViewHolder holder, IndexData entity) {
            ((TextView) holder.itemView).setText(entity.name);
        }

        class EasonViewHolder extends RecyclerView.ViewHolder {

            EasonViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
