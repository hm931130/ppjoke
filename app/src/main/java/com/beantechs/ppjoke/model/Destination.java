package com.beantechs.ppjoke.model;

public class Destination {


    /**
     * isFragment : true
     * asStarter : false
     * needLogin : false
     * clazName : com.beantechs.ppjoke.ui.dashboard.DashboardFragment
     * id : 1450339471
     */

    private boolean isFragment;
    private boolean asStarter;
    private boolean needLogin;
    private String clazName;
    private int id;
    private String pageUrl;


    public String getPageUrl() {
        return pageUrl;
    }

    public void setPageUrl(String pageUrl) {
        this.pageUrl = pageUrl;
    }

    public boolean isIsFragment() {
        return isFragment;
    }

    public void setIsFragment(boolean isFragment) {
        this.isFragment = isFragment;
    }

    public boolean isAsStarter() {
        return asStarter;
    }

    public void setAsStarter(boolean asStarter) {
        this.asStarter = asStarter;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }

    public String getClazName() {
        return clazName;
    }

    public void setClazName(String clazName) {
        this.clazName = clazName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
