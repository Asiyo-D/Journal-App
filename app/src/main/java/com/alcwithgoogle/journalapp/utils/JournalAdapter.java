package com.alcwithgoogle.journalapp.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alcwithgoogle.journalapp.R;
import com.alcwithgoogle.journalapp.database.JournalEntry;

import java.util.Calendar;
import java.util.List;

/**
 * Created by root on 6/25/18 for LoqourSys
 */
public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {
    private Context context;
    public List<JournalEntry> entries;
    private Calendar cal;
    private ItemClickListener clickListener;


    public JournalAdapter(Context context, ItemClickListener clickListener) {
        this.context = context;
        cal = Calendar.getInstance();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_item, parent, false);

        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalEntry entry = entries.get(position);
        holder.bindView(entry);
    }

    @Override
    public int getItemCount() {
        if (entries == null) {
            return 0;
        }
        return entries.size();
    }

    public void setEntries(List<JournalEntry> entries) {
        this.entries = entries;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onClick(int entryId);
    }

    public void addItem(int pos, JournalEntry item) {
        entries.add(pos, item);
        notifyItemInserted(pos);
        redrawViews();
    }

    public void removeItem(int pos) {
        entries.remove(pos);
        notifyItemRemoved(pos);
        redrawViews();
    }

    private void redrawViews() {
        notifyDataSetChanged();
    }

    class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtTitle;
        TextView txtDate;
        TextView txtEntryText;

        JournalViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txt_entry_title);
            txtDate = itemView.findViewById(R.id.txt_entry_date);
            txtEntryText = itemView.findViewById(R.id.txt_entry_text);
            itemView.setOnClickListener(this);
        }

        void bindView(JournalEntry entry) {
            txtTitle.setText(entry.getTitle());
            cal.setTimeInMillis(entry.getEntryDate());

            String entryDate = DateTemplate.format(cal, "%MM% %dd%, %yy%");
            txtDate.setText(entryDate);
            txtEntryText.setText(entry.getDescription());
        }

        @Override
        public void onClick(View view) {
            int entryId = entries.get(getAdapterPosition()).getId();
            clickListener.onClick(entryId);
        }
    }

    public abstract static class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
        private Drawable deleteIcon;
        private ColorDrawable deleteBg = new ColorDrawable();
        private int intrinsicWidth;
        private int intrinsicHeight;
        private int colorBG;

        protected SwipeToDeleteCallback(Context context) {
            super(0, ItemTouchHelper.RIGHT);
            deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete_white_24dp);
            colorBG = Color.parseColor("#F44336");

            intrinsicWidth = deleteIcon.getIntrinsicWidth();
            intrinsicHeight = deleteIcon.getIntrinsicHeight();
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            View view = viewHolder.itemView;
            int itemHeight = view.getBottom() - view.getTop();

//            Draw the delete background
            deleteBg.setColor(colorBG);
            deleteBg.setBounds(view.getLeft(),
                    view.getTop(),
                    view.getLeft() + (int) dX,
                    view.getBottom());

            deleteBg.draw(c);

//        Calculate icon position
            int iconTop = view.getTop() + (itemHeight - intrinsicHeight) / 2;
            int iconMargin = (itemHeight - intrinsicHeight) / 2;

            int iconLeft = view.getLeft() + iconMargin;
            int iconRight = view.getLeft() + iconMargin + intrinsicWidth;

            int iconBottom = iconTop + intrinsicHeight;

//        Draw delete icon
            deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
            deleteIcon.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

}
