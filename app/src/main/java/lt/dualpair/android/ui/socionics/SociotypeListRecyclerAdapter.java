package lt.dualpair.android.ui.socionics;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import lt.dualpair.android.R;
import lt.dualpair.android.data.local.entity.Sociotype;
import lt.dualpair.android.utils.LabelUtils;

public class SociotypeListRecyclerAdapter extends RecyclerView.Adapter<SociotypeListRecyclerAdapter.SociotypeViewHolder> {

    private List<Sociotype> sociotypes;
    private OnSociotypeClickListener onSociotypeClickListener;

    public SociotypeListRecyclerAdapter(List<Sociotype> sociotypes, OnSociotypeClickListener onSociotypeClickListener) {
        this.sociotypes = sociotypes;
        this.onSociotypeClickListener = onSociotypeClickListener;
    }

    @NonNull
    @Override
    public SociotypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_sociotype, parent, false);
        SociotypeViewHolder viewHolder = new SociotypeViewHolder(parent.getContext(), v);
        ButterKnife.bind(viewHolder, v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SociotypeViewHolder holder, int position) {
        Sociotype sociotype = sociotypes.get(position);
        holder.set(sociotype, onSociotypeClickListener);
    }

    @Override
    public int getItemCount() {
        return sociotypes.size();
    }

    public static class SociotypeViewHolder extends RecyclerView.ViewHolder {

        private Context context;

        @Bind(R.id.code1) TextView code1;
        @Bind(R.id.code2) TextView code2;
        @Bind(R.id.title) TextView title;

        public SociotypeViewHolder(Context context, View itemView) {
            super(itemView);
            this.context = context;
        }

        public void set(Sociotype sociotype, OnSociotypeClickListener onSociotypeClickListener) {
            this.code1.setText(LabelUtils.getSociotypeAcronym(context, sociotype.getCode()));
            this.code2.setText(LabelUtils.getSociotype4LetterAcronym(context, sociotype.getCode()));
            String title = LabelUtils.getSociotypeSocialRole(context, sociotype.getCode());
            this.title.setText(title);
            itemView.setOnClickListener(v -> onSociotypeClickListener.onSociotypeClick(sociotype));
        }
    }

    public interface OnSociotypeClickListener {
        void onSociotypeClick(Sociotype sociotype);
    }

}
