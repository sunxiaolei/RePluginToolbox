package xiaolei.plugintoolbox.ui

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

import xiaolei.plugintoolbox.R
import xiaolei.plugintoolbox.base.BaseActivity

class MainActivity : BaseActivity() {

    private var mAdapter: MainAdapter? = null

    override fun setContentViewId(): Int {
        return R.layout.activity_main
    }

    override fun init() {
        tvTitle?.text = resources.getString(R.string.app_name)
        recyclerview_main!!.layoutManager = LinearLayoutManager(this)
        mAdapter = MainAdapter()
        recyclerview_main.adapter = mAdapter

        mAdapter!!.addData("设备信息")
        mAdapter!!.addData("应用管理")
        mAdapter!!.addData("取色器")
        mAdapter!!.addData("生成二维码")

        mAdapter!!.setOnItemClickListener { adapter, _, position ->
            val intent = Intent()
            when (adapter.getItem(position)!!.toString()) {
                "应用管理" -> intent.setClass(this@MainActivity, AppListActivity::class.java)
                "设备信息" -> intent.setClass(this@MainActivity, DeviceInfoActivity::class.java)
            }
            startActivity(intent)
        }
    }
}
