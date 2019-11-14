package com.lemi.interact.bean;

import java.util.ArrayList;

public class RegionProvinceBean {

    private String areacode;
    private String areaname;
    private ArrayList subarea;
    public String getAreacode() {
        return areacode;
    }
    public void setAreacode(String areacode) {
        this.areacode = areacode;
    }
    public String getAreaname() {
        return areaname;
    }
    public void setAreaname(String areaname) {
        this.areaname = areaname;
    }
    public ArrayList getSubarea() {
        return subarea;
    }
    public void setSubarea(ArrayList subarea) {
        this.subarea = subarea;
    }

}
