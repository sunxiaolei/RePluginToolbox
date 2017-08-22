package xiaolei.plugintoolbox.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import xiaolei.plugintoolbox.R;
import xiaolei.plugintoolbox.base.BaseActivity;

public class MainActivity extends BaseActivity {

    private RecyclerView mRecyclerView;
    private MainAdapter mAdapter;

    @Override
    protected int setContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        tvTitle.setText(getResources().getString(R.string.app_name));
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new MainAdapter();
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.addData("设备信息");
        mAdapter.addData("应用管理");
        mAdapter.addData("取色器");
        mAdapter.addData("生成二维码");

        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            Intent intent = new Intent();
            switch (adapter.getItem(position).toString()) {
                case "应用管理":
                    intent.setClass(MainActivity.this, AppListActivity.class);
                    break;
                case "设备信息":
                    intent.setClass(MainActivity.this, DeviceInfoActivity.class);
                    break;

            }
            startActivity(intent);
        });
    }
}
