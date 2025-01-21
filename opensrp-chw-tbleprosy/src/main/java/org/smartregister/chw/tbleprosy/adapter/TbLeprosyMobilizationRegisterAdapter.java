package org.smartregister.chw.tbleprosy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.smartregister.chw.tbleprosy.R;
import org.smartregister.chw.tbleprosy.model.TbLeprosyMobilizationModel;

import java.util.List;

public class TbLeprosyMobilizationRegisterAdapter extends RecyclerView.Adapter<TbLeprosyMobilizationRegisterAdapter.TbLeprosyMobilzationViewHolder> {

    private final Context context;
    private final List<TbLeprosyMobilizationModel> tbLeprosyMobilizationModels;


    public TbLeprosyMobilizationRegisterAdapter(List<TbLeprosyMobilizationModel> tbLeprosyMobilizationModels, Context context) {
        this.tbLeprosyMobilizationModels = tbLeprosyMobilizationModels;
        this.context = context;
    }

    @NonNull
    @Override
    public TbLeprosyMobilzationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View followupLayout = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tbleprosy_mobilization_session_card_view, viewGroup, false);
        return new TbLeprosyMobilzationViewHolder(followupLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull TbLeprosyMobilzationViewHolder holder, int position) {
        TbLeprosyMobilizationModel tbLeprosyMobilizationModel = tbLeprosyMobilizationModels.get(position);
        holder.bindData(tbLeprosyMobilizationModel);
    }

    @Override
    public int getItemCount() {
        return tbLeprosyMobilizationModels.size();
    }

    protected class TbLeprosyMobilzationViewHolder extends RecyclerView.ViewHolder {
        public TextView mobilizationSessionDate;
        public TextView mobilizationSessionParticipants;
//        public TextView mobilizationSessionCondomsIssued;

        public TbLeprosyMobilzationViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bindData(TbLeprosyMobilizationModel tbLeprosyMobilizationModel) {
            mobilizationSessionDate = itemView.findViewById(R.id.mobilization_session_date);
            mobilizationSessionParticipants = itemView.findViewById(R.id.mobilization_session_participants);
//            mobilizationSessionCondomsIssued = itemView.findViewById(R.id.mobilization_session_condoms_issued);

            mobilizationSessionDate.setText(context.getString(R.string.mobilziation_session_date, tbLeprosyMobilizationModel.getSessionDate()));
            mobilizationSessionParticipants.setText(context.getString(R.string.mobilization_session_participants, tbLeprosyMobilizationModel.getSessionParticipants()));
//            mobilizationSessionCondomsIssued.setText(context.getString(R.string.mobilization_condoms_issued, tbLeprosyMobilizationModel.getCondomsIssued()));
        }
    }
}
