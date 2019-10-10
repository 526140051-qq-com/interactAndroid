package com.lemi.interact.util;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.Button;

/**
 * 验证码倒计时 重新验证
 */
public class CountDownTimerUtils extends CountDownTimer {
    private Button btn;

    public CountDownTimerUtils(Button btn, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        btn.setEnabled(false); //设置不可点击
        btn.setText(millisUntilFinished / 1000+"");  //设置倒计时时间
        btn.setTextColor(Color.parseColor("#a5ffffff"));
    }

    @Override
    public void onFinish() {
        btn.setText("获取验证码");
        btn.setEnabled(true);
        btn.setTextColor(Color.parseColor("#ffffff"));
    }
}
