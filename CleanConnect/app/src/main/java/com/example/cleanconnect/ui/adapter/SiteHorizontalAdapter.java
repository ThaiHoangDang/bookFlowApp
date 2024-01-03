package com.example.cleanconnect.ui.adapter;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cleanconnect.databinding.ItemHorizontalSiteBinding;
import com.example.cleanconnect.interfaces.ClickCallBack;
import com.example.cleanconnect.model.Site;
import com.example.cleanconnect.util.LocationUtils;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SiteHorizontalAdapter extends RecyclerView.Adapter<SiteHorizontalAdapter.ViewHolder> implements Filterable {
    private final ClickCallBack click;

    private List<Site> sites;
    private LatLng userLocation;
    public SiteHorizontalAdapter(ClickCallBack click, LatLng userLocation) {
        this.click = click;
        this.sites = new ArrayList<Site>();
        this.userLocation = userLocation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHorizontalSiteBinding view = ItemHorizontalSiteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Site site = sites.get(position);
        holder.item.siteTitleTextView.setText(site.getTitle());
        StringBuilder builder = new StringBuilder().append("Distance: ");
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        builder.append(decimalFormat.format(LocationUtils.calculateHaversineDistance(new LatLng(site.getLatitude(), site.getLongitude()), userLocation)));
        builder.append(" km");
        holder.item.siteSubtitleTextView.setText(builder.toString());
    }

    @Override
    public int getItemCount() {
        return sites.size();
    }

    public Site getItem(int id) {
        return sites.get(id);
    }

    public void setItems(List<Site> playlists) {
        this.sites.clear();
        this.sites.addAll(playlists);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemHorizontalSiteBinding item;

        ViewHolder(ItemHorizontalSiteBinding item) {
            super(item.getRoot());

            this.item = item;
            item.siteTitleTextView.setSelected(true);

            itemView.setOnClickListener(v -> onClick());
//            itemView.setOnLongClickListener(v -> onLongClick());
//
//            item.playlistMoreButton.setOnClickListener(v -> onLongClick());
        }

        public void onClick() {
            Bundle bundle = new Bundle();
            Site site = sites.get(getBindingAdapterPosition());
            bundle.putParcelable("SITE_OBJECT", site);
            bundle.putDouble("SITE_DISTANCE", LocationUtils.calculateHaversineDistance(new LatLng(site.getLatitude(), site.getLongitude()), userLocation));

            click.onSiteClick(bundle);
        }
//
//        public boolean onLongClick() {
//            Bundle bundle = new Bundle();
//            bundle.putParcelable(Constants.PLAYLIST_OBJECT, playlists.get(getBindingAdapterPosition()));
//
//            click.onPlaylistLongClick(bundle);
//
//            return true;
//        }
    }
//
//    public void sort(String order) {
//        switch (order) {
//            case Constants.PLAYLIST_ORDER_BY_NAME:
//                playlists.sort(Comparator.comparing(Playlist::getName));
//                break;
//            case Constants.PLAYLIST_ORDER_BY_RANDOM:
//                Collections.shuffle(playlists);
//                break;
//        }
//
//        notifyDataSetChanged();
//    }
}
