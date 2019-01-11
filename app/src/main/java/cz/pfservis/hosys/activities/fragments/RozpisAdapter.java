package cz.pfservis.hosys.activities.fragments;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Objects;

import cz.pfservis.hosys.dto.RozpisDto;
import hosys.pfservis.cz.hosys.R;

public class RozpisAdapter extends RecyclerView.Adapter<RozpisAdapter.RozpisViewHolder> {
    private RozpisDto[] mDataset;

    public class RozpisViewHolder extends RecyclerView.ViewHolder {
        public TextView datum;
        public TextView cas;
        public TextView domaci;
        public TextView hoste;
        public TextView stav;


        public RozpisViewHolder(View itemView) {
            super(itemView);

            datum = (TextView) itemView.findViewById(R.id.datum);
            cas = (TextView) itemView.findViewById(R.id.cas);
            domaci = (TextView) itemView.findViewById(R.id.domaci);
            hoste = (TextView) itemView.findViewById(R.id.hoste);
            stav = (TextView) itemView.findViewById(R.id.stav);
        }

        public void updateBackgroundColor(String statusRow) {
            int color;

            if ("Odehrane".equals(statusRow)) {
                color = itemView.getResources().getColor(R.color.colorHosysStatusOdehrane);
            } else if ("Nahlasene".equals(statusRow)) {
                color = itemView.getResources().getColor(R.color.colorHosysStatusNahlasene);
            } else if ("Rozlosovane".equals(statusRow)) {
                color = itemView.getResources().getColor(R.color.colorHosysStatusRozlosovane);
            } else if ("Navrh".equals(statusRow)) {
                color = itemView.getResources().getColor(R.color.colorHosysStatusNavrh);
            } else if ("K_rozhodnuti".equals(statusRow)) {
                color = itemView.getResources().getColor(R.color.colorHosysStatusK_rozhodnuti);
            } else if ("Zrusene".equals(statusRow)) {
                color = itemView.getResources().getColor(R.color.colorHosysStatusZrusene);
            } else {
                color = Color.TRANSPARENT;
            }

            itemView.setBackgroundColor(color);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RozpisAdapter(RozpisDto[] myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RozpisAdapter.RozpisViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rozpis_item, parent, false);

        return new RozpisViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RozpisViewHolder holder, int position) {
        holder.datum.setText(mDataset[position].getDatumTitle());
        holder.cas.setText(mDataset[position].getCasTitle());
        holder.domaci.setText(mDataset[position].getDomaciTitle());
        holder.hoste.setText(mDataset[position].getHoste());
        holder.stav.setText(mDataset[position].getStatus());

        holder.updateBackgroundColor(mDataset[position].getStatusRow());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.length;
    }

    public void updataDataSet(RozpisDto[] dataset) {
        this.mDataset = dataset;

        notifyDataSetChanged();
    }
}
