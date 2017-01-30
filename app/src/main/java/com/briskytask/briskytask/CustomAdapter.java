package com.briskytask.briskytask;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by root on 28/1/17.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>{
    private Context context;
    private List<UserData> userData;

    public CustomAdapter(Context context, List<UserData> userData) {
        this.context = context;
        this.userData = userData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.id.setText(userData.get(position).getId());
        holder.name.setText(userData.get(position).getName());
        holder.username.setText(userData.get(position).getUsername());
        holder.email.setText(userData.get(position).getEmail());
        holder.address.setText(userData.get(position).getAddress());
        holder.phone.setText(userData.get(position).getPhone());
        holder.website.setText(userData.get(position).getWebsite());
        holder.company.setText(userData.get(position).getCompany());
        holder.eta.setText(userData.get(position).getEta());
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id;
        public TextView name;
        public TextView username;
        public TextView email;
        public TextView address;
        public TextView phone;
        public TextView website;
        public TextView company;
        public TextView eta;

        public ViewHolder(View itemView) {
            super(itemView);
            id = (TextView) itemView.findViewById(R.id.id);
            name = (TextView) itemView.findViewById(R.id.name);
            username = (TextView) itemView.findViewById(R.id.username);
            email = (TextView) itemView.findViewById(R.id.email);
            address = (TextView) itemView.findViewById(R.id.address);
            phone = (TextView) itemView.findViewById(R.id.phone);
            website = (TextView) itemView.findViewById(R.id.website);
            company = (TextView) itemView.findViewById(R.id.company);
            eta = (TextView) itemView.findViewById(R.id.eta);
        }
    }
}
