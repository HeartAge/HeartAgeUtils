package Color_yr.HeartAge_year.Obj;

public class Config_Obj {//主配置对象

    private String version;
    private Config_tpStone_Obj Config_tpStone_Obj;

    public String getVersion() {
        return version;
    }

    public void setVersion(String vision) {
        this.version = vision;
    }

    public Config_tpStone_Obj gettpStone() {
        return Config_tpStone_Obj;
    }

    public void settpSone(Config_tpStone_Obj item) {
        this.Config_tpStone_Obj = item;
    }
}

