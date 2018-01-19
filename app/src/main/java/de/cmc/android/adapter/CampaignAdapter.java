package de.cmc.android.adapter;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.cmc.android.R;
import de.cmc.android.VoucherActivity;
import de.cmc.android.data.Campaign;
import de.cmc.android.ui.DetailFragment;
import de.cmc.android.ui.StartpageFragment;

/**
 * Created by Deniz Kalem on 11.08.17.
 */

public class CampaignAdapter extends RecyclerView.Adapter<CampaignAdapter.MyViewHolder> {

	private Context        context;
	private List<Campaign> campaignList;
	private SparseBooleanArray expandState = new SparseBooleanArray();

	public static class MyViewHolder extends RecyclerView.ViewHolder {
		private TextView discount, limitations;
		private ImageView logo, details;
		private RelativeLayout expandableLayout;
		private ImageButton    info;

		private MyViewHolder(View view) {
			super(view);
			discount = (TextView) view.findViewById(R.id.tv_discount);
			logo = (ImageView) view.findViewById(R.id.iv_logo);
			expandableLayout = (RelativeLayout) view.findViewById(R.id.layout_limitations);
			info = (ImageButton) view.findViewById(R.id.ib_info);
			details = (ImageButton) view.findViewById(R.id.ib_details);
			limitations = (TextView) view.findViewById(R.id.tv_limitations);

			info.setBackgroundTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
			details.setBackgroundTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
		}

	}

	public void setCampaigns(ArrayList<Campaign> campaigns) {
		campaignList = campaigns;
		notifyDataSetChanged();
	}


	public CampaignAdapter(Context context, List<Campaign> campaignList) {
		this.context = context;
		this.campaignList = campaignList;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.campaign_card, parent, false);

		return new MyViewHolder(itemView);
	}


	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position) {
		final Campaign campaign = campaignList.get(position);
		holder.discount.setText(campaign.getDiscount());
		Picasso.with(context).load("https://my.netscalenow.de/r/fit?url=" + campaign.getLogUrl() + "&width=900&height=200").into(holder.logo);

		//check if view is expanded
		final boolean isExpanded = expandState.get(position);
		holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
		holder.limitations.setText(campaign.getLimitations());

		holder.info.setRotation(expandState.get(position) ? 180f : 0f);

		holder.info.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!"null".equals(campaign.getLimitations())) {
					onClickButton(holder.expandableLayout, holder.info, position);
				}
			}
		});


		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DetailFragment detailFragment = new DetailFragment();
				Bundle bundle = new Bundle();
				bundle.putParcelable("campaign", campaign);
				detailFragment.setArguments(bundle);

				saveCampaignsAndSelected(campaign);

				FragmentManager fragmentManager = ((VoucherActivity) context).getSupportFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
				fragmentTransaction.replace(R.id.fragment_start, detailFragment, "detail");
				fragmentTransaction.addToBackStack("startpage");
				fragmentTransaction.commit();
			}
		});
	}

	private void onClickButton(final RelativeLayout expandableLayout, final ImageButton imageButton, final int i) {
		//Simply set View to Gone if not expanded
		if (expandableLayout.getVisibility() == View.VISIBLE) {
			createRotateAnimator(imageButton, 180f, 0f).start();
			expandableLayout.setVisibility(View.GONE);
			expandState.put(i, false);
		} else {
			createRotateAnimator(imageButton, 0f, 180f).start();
			expandableLayout.setVisibility(View.VISIBLE);
			expandState.put(i, true);
		}
	}

	//Code to rotate button
	private ObjectAnimator createRotateAnimator(final View target, final float from, final float to) {
		ObjectAnimator animator = ObjectAnimator.ofFloat(target, "rotation", from, to);
		animator.setDuration(300);
		animator.setInterpolator(new LinearInterpolator());
		return animator;
	}

	@Override
	public int getItemCount() {
		return campaignList.size();
	}

	public int getItemPosition(Campaign campaign) {
		return campaignList.indexOf(campaign);
	}

	public Campaign getItem(int position) {
		return campaignList.get(position);
	}

	public void clear() {
		if (campaignList.size() > 0) {
			campaignList.clear();
		}
	}

	public void saveCampaignsAndSelected(Campaign selectedCampaign) {
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = sharedPrefs.edit();
		Gson gson = new Gson();
		editor.putString("allCampaigns", gson.toJson(campaignList));
		editor.putInt("selectedCampaignPosition", getItemPosition(selectedCampaign));
		editor.commit();
	}
}
