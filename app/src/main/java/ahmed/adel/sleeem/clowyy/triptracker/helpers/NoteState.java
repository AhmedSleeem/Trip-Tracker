package ahmed.adel.sleeem.clowyy.triptracker.helpers;

public class NoteState {
    boolean isChecked;
    String note;

    public NoteState() {

    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public NoteState(boolean isChecked, String note){
        this.isChecked = isChecked;
        this.note = note;
    }
}
