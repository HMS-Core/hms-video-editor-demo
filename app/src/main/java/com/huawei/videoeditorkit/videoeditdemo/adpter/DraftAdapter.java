
package com.huawei.videoeditorkit.videoeditdemo.adpter;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huawei.hms.videoeditor.ui.api.DraftInfo;
import com.huawei.videoeditorkit.videoeditdemo.R;
import com.huawei.videoeditorkit.videoeditdemo.custom.RoundImage;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DraftAdapter extends RecyclerView.Adapter<DraftAdapter.WorksHolder> {
    private Context context;

    private List<DraftInfo> list;

    private SimpleDateFormat mSimpleDateFormat;

    private DraftInfo draftInfo;

    public DraftAdapter(Context context) {
        this.context = context;
        mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    }

    public void setData(List<DraftInfo> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public WorksHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.adapter_draft_itme, parent, false);
        return new WorksHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull WorksHolder holder, int position) {
        draftInfo = list.get(position);
        Glide.with(context).load(list.get(position).getDraftCoverPath()).into(holder.mWorkimage);
        holder.mTime.setText(mSimpleDateFormat.format(draftInfo.getDraftCreateTime()));
        holder.mWorkimage.setOnClickListener(v -> {
            selectedListener.onStyleSelected(position);
        });

        holder.mWorkimage.setOnLongClickListener(new View.OnLongClickListener(){
            @Override
            public boolean onLongClick(View v) {
                longSelectedListener.onStyleLongSelected(v, position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class WorksHolder extends RecyclerView.ViewHolder {
        RoundImage mWorkimage;

        private final TextView mTime;

        public WorksHolder(@NonNull View itemView) {
            super(itemView);
            mWorkimage = itemView.findViewById(R.id.works_item_image);
            mTime = itemView.findViewById(R.id.time);
        }
    }

    OnStyleSelectedListener selectedListener;

    OnStyleLongSelectedListener longSelectedListener;

    public void setSelectedListener(OnStyleSelectedListener selectedListener) {
        this.selectedListener = selectedListener;
    }

    public void setLongSelectedListener(OnStyleLongSelectedListener longSelectedListener) {
        this.longSelectedListener = longSelectedListener;
    }

    public interface OnStyleSelectedListener {
        void onStyleSelected(int position);
    }

    public interface OnStyleLongSelectedListener {
        void onStyleLongSelected(View v, int position);
    }
}