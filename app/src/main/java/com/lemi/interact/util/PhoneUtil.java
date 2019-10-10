package com.lemi.interact.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtil {

    // 移动
    private static final String yd = "(^((13[4-9]{1})|(147)|(15[0-2]{1})|(15[7-9]{1})|(172)|(178)|(18[2-4]{1})|(18[7-8]{1})|(198))\\d{8}$)|(^((1703)|(1705)|(1706))\\d{7}$)";
    // 联通
    private static final String lt = "(^((13[0-2]{1})|(145)|(155)|(156)|(166)|(171)|(175)|(176)|(185)|(186))\\d{8}$)|(^(170[7-9]{1})\\d{7}$)";
    // 电信
    private static final String tx = "(^((133)|(149)|(153)|(173)|(177)|(180)|(181)|(189)|(191)|(199))\\d{8}$)|(^(170[0-2])\\d{7}$)";


    /**
     * 0代表未知,1代表移动,2代表联通,3代表电信
     *
     * @param phone
     * @return
     */
    public static int getOperator(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return 0;
        }
        if (phone.matches(yd)) {
            return 1;
        } else if (phone.matches(lt)) {
            return 2;
        } else if (phone.matches(tx)) {
            return 3;
        } else {
            return 0;
        }
    }

    //手机号码验证
    public static boolean isMobileNO(String mobiles) {
        if (mobiles == null || "".equals(mobiles.trim())){
            return false;
        }
        Pattern ydp = Pattern.compile(yd);

        Pattern ltp = Pattern.compile(lt);

        Pattern txp = Pattern.compile(tx);

        Matcher ydm = ydp.matcher(mobiles);

        Matcher ltm = ltp.matcher(mobiles);

        Matcher txm = txp.matcher(mobiles);

        if (ydm.matches() || ltm.matches() || txm.matches()) {
            return true;
        } else {
            return false;
        }

    }

    //电话号码验证
    public static boolean isPhone(String str) {
        boolean b = false;
        if (str == null || "".equals(str.trim())){
            return b;
        }
        Pattern p1 = null, p2 = null;
        Matcher m = null;
        p1 = Pattern.compile("^[0][1-9]{2,3}-[0-9]{5,10}$");  // 验证带区号的
        p2 = Pattern.compile("^[1-9]{1}[0-9]{5,8}$");         // 验证没有区号的
        if (str.length() > 9) {
            m = p1.matcher(str);
            b = m.matches();
        } else {
            m = p2.matcher(str);
            b = m.matches();
        }
        return b;
    }



    public static void main(String[] args) {
       // System.out.println(isMobileNO("15651981910"));
       // System.out.println(isMobileNO("16606120198"));
        //System.out.println(isMobileNO("17855692206"));
        System.out.println(isMobileNO("18136074162"));
        System.out.println(isMobileNO("18136074162"));
        System.out.println(isMobileNO("16606120198"));
        System.out.println(isMobileNO("17855692206"));
    }
}
