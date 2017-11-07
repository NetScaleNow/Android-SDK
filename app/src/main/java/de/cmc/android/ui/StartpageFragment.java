package de.cmc.android.ui;


import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import de.cmc.android.R;
import de.cmc.android.adapter.CampaignAdapter;
import de.cmc.android.callback.Callback;
import de.cmc.android.config.FrontendConfig;
import de.cmc.android.data.Campaign;
import de.cmc.android.service.ApiAdapter;
import de.cmc.android.utils.ValueConstants;

import static de.cmc.android.utils.ValueConstants.ACCENT_COLOR_VALUE;


public class StartpageFragment extends Fragment {

	private CampaignAdapter     campaignAdapter;
	private ArrayList<Campaign> campaignList;
	private ApiAdapter          apiAdapter;
	private ProgressBar         loadingIndicator;
	private TextView            errorMessage;
	private LinearLayout        header;
	private TextView            campaignTitle;
	private TextView            campaignSubtitle;
	private LinearLayout        footer;
	private FrontendConfig      frontendConfig;
	private int                 usedVoucher;
	private SharedPreferences   sharedPreferences;
	public static int APP_PRIMARY_COLOR = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_startpage, container, false);

		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		usedVoucher = sharedPreferences.getInt("usedVoucher", 0);

		frontendConfig = FrontendConfig.getInstance();
		campaignTitle = (TextView) view.findViewById(R.id.tv_campaign_title);
		campaignSubtitle = (TextView) view.findViewById(R.id.tv_campaign_subtitle);
		RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
		header = (LinearLayout) view.findViewById(R.id.layout_header);
		loadingIndicator = (ProgressBar) view.findViewById(R.id.pb_loading_indicator);
		errorMessage = (TextView) view.findViewById(R.id.tv_error_message_display);
		footer = (LinearLayout) ((Activity) getContext()).findViewById(R.id.layout_footer);
		footer.setVisibility(View.GONE);

		campaignList = new ArrayList<>();
		campaignAdapter = new CampaignAdapter(getActivity(), campaignList);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setItemAnimator(new DefaultItemAnimator());
		recyclerView.setAdapter(campaignAdapter);

		apiAdapter = new ApiAdapter();

		setPrimaryColor();
		updateUserInterface();

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getParcelableArrayList("campaigns") != null || usedVoucher == 1) {
			if (usedVoucher == 1) {
				Gson gson = new Gson();
				String json = sharedPreferences.getString("allCampaigns", null);
				Type type = new TypeToken<ArrayList<Campaign>>() {}.getType();
				campaignList = gson.fromJson(json, type);
				boolean campaignRemoved = sharedPreferences.getBoolean("campaignRemoved", false);

				if (!campaignRemoved) {
					int usedCampaignPosition = sharedPreferences.getInt("selectedCampaignPosition", 0);
					campaignList.remove(usedCampaignPosition);

					SharedPreferences.Editor editor = sharedPreferences.edit();
					editor.putBoolean("campaignRemoved", true);
					editor.putString("allCampaigns", gson.toJson(campaignList));
					editor.apply();
				}
			} else {
				campaignList = savedInstanceState.getParcelableArrayList("campaigns");
			}
			campaignAdapter.setCampaigns(campaignList);
			loadingIndicator.setVisibility(View.GONE);
			header.setVisibility(View.VISIBLE);
			footer.setVisibility(View.VISIBLE);
		} else {
			showCampaigns();
		}
	}

	public void showCampaigns() {

		errorMessage.setVisibility(View.INVISIBLE);

		FetchCampaignTask campaignTask = new FetchCampaignTask(new Callback() {
			@Override
			public void updateAdapter(ArrayList<Campaign> campaigns) {
				if (campaigns != null) {
					campaignAdapter.clear();
					campaignList.addAll(campaigns);
					campaignAdapter.notifyDataSetChanged();
					loadingIndicator.setVisibility(View.GONE);
					header.setVisibility(View.VISIBLE);
					footer.setVisibility(View.VISIBLE);
					setPrimaryColor();
					updateUserInterface();
				}
			}
		});

		campaignTask.execute();
	}


	private class FetchCampaignTask extends AsyncTask<String, Void, ArrayList<Campaign>> {

		private final Callback callback;

		FetchCampaignTask(Callback campaignTaskCallback) {
			this.callback = campaignTaskCallback;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			loadingIndicator.setVisibility(View.VISIBLE);
		}

		@Override
		protected ArrayList<Campaign> doInBackground(String... params) {
			ArrayList<Campaign> campaigns = null;
			try {
				campaigns = apiAdapter.getCampaignsFromServer(ValueConstants.METADATA_VALUE);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return campaigns;
		}

		@Override
		protected void onPostExecute(ArrayList<Campaign> campaigns) {
			loadingIndicator.setVisibility(View.INVISIBLE);
			if (campaigns != null) {
				callback.updateAdapter(campaigns);
			} else {
				errorMessage.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelableArrayList("campaigns", campaignList);
	}

	public void setPrimaryColor() {
		if (ACCENT_COLOR_VALUE.length() > 1) {
			try {
				APP_PRIMARY_COLOR = Color.parseColor("#" + ACCENT_COLOR_VALUE);
			} catch (IllegalArgumentException e) {
				APP_PRIMARY_COLOR = ContextCompat.getColor(getActivity(), (R.color.colorAccentCmc));
			}
		} else if (frontendConfig.getPrimaryColor().length() > 1) {
			try {
				APP_PRIMARY_COLOR = Color.parseColor("#" + frontendConfig.getPrimaryColor());
			} catch (IllegalArgumentException e) {
				APP_PRIMARY_COLOR = ContextCompat.getColor(getActivity(), (R.color.colorAccentCmc));
			}
		} else {
			APP_PRIMARY_COLOR = ContextCompat.getColor(getActivity(), (R.color.colorAccentCmc));
		}
	}

	public void updateUserInterface() {
		if (!ValueConstants.CAMPAIGN_TITLE_VALUE.isEmpty()) {
			campaignTitle.setText(ValueConstants.CAMPAIGN_TITLE_VALUE);
		} else if (!frontendConfig.getCampaignTitle().isEmpty()) {
			campaignTitle.setText(frontendConfig.getCampaignTitle());
		}

		if (!ValueConstants.CAMPAIGN_SUBTITLE_VALUE.isEmpty()) {
			campaignSubtitle.setText(ValueConstants.CAMPAIGN_SUBTITLE_VALUE);
		} else if (!frontendConfig.getCampaignSubtitle().isEmpty()) {
			campaignSubtitle.setText(frontendConfig.getCampaignSubtitle());
		}

		if (!frontendConfig.getCopyrightText().isEmpty()) {
			TextView copyright = (TextView) ((Activity) getContext()).findViewById(R.id.tv_copyright);
			copyright.setText(frontendConfig.getCopyrightText());
		}

		campaignTitle.setTextColor(APP_PRIMARY_COLOR);
		loadingIndicator.setIndeterminateTintList(ColorStateList.valueOf(APP_PRIMARY_COLOR));
	}
}
