package a1.example.com.myapplication.Model;

public class HappyNoteModel {
    public String noteuser;
    public String notetitle;
    public String note;
    public String writetime;
    public String weather;
    public String address;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weater) {
        this.weather = weater;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNoteuser() {
        return noteuser;
    }

    public void setNoteuser(String noteuser) {
        this.noteuser = noteuser;
    }

    public String getNotetitle() {
        return notetitle;
    }

    public void setNotetitle(String notetitle) {
        this.notetitle = notetitle;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getWritetime() {
        return writetime;
    }

    public void setWritetime(String writetime) {
        this.writetime = writetime;
    }
}
