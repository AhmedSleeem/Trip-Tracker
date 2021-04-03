package ahmed.adel.sleeem.clowyy.triptracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ahmed.adel.sleeem.clowyy.triptracker.helpers.NoteState;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHandler> {

    private final Context context;
    private List<NoteState> notes ;

    public NotesAdapter(Context context , List<NoteState> myNotes) {
        this.context = context;
        notes = myNotes;
    }

    @NonNull
    @Override
    public NotesAdapter.ViewHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.notes_singlerow,parent,false);
        ViewHandler vh = new ViewHandler(v);
        return vh;
    }


    @Override
    public void onBindViewHolder(@NonNull NotesAdapter.ViewHandler holder, int position) {

        holder.notesCheckbox.setText(notes.get(position).getNote());
        holder.notesCheckbox.setChecked(notes.get(position).isChecked());

        holder.notesCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            notes.get(position).setChecked(isChecked);
        });

    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHandler extends RecyclerView.ViewHolder {

        public CheckBox notesCheckbox;
        public View layout;


        public ViewHandler(@NonNull View itemView) {
            super(itemView);
            layout = itemView;
            notesCheckbox = itemView.findViewById(R.id.checkBox);
        }
    }
}
