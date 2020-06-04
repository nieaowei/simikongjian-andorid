package a1.example.com.myapplication.Model;

import java.io.Serializable;
import java.util.Date;

public class WeekDayModel implements Serializable {
    public String shenfen;
    public String birthday;
    public String createby;

    public String getCreateby() {
        return createby;
    }

    public void setCreateby(String createby) {
        this.createby = createby;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getShenfen() {

        return shenfen;
    }

    public void setShenfen(String shenfen) {
        this.shenfen = shenfen;
    }
}
