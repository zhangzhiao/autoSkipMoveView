package com.zza.moveview

import android.graphics.RectF
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //设置规避的view坐标 主要 请务必在view完全绘制完成后调用 不然会拿不到坐标 常用方法是使用View.post(R r);
        //这里我用了kotlin-android-extensions 这个就是id可以直接用
        //这里如果看不懂 emm 就是kt的语法糖
        skipView.post {
            moveView.setSkipViewRectF(
                //这里创建矩形对象 然后把四个边传入进去 这里我是直接算的 当然也可以直接获得
                RectF(
                    skipView.x,
                    skipView.y,
                    skipView.x + skipView.measuredWidth,
                    skipView.y + skipView.measuredHeight
                )
            )
        }
    }
}