package de.cmc.android.service;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import de.cmc.android.config.FrontendConfig;
import de.cmc.android.data.Campaign;
import de.cmc.android.data.Metadata;
import de.cmc.android.data.Token;
import de.cmc.android.data.Voucher;
import de.cmc.android.utils.UrlConstants;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static de.cmc.android.utils.UrlConstants.BASE_URL;

/**
 * Created by Deniz Kalem on 08.08.17.
 */

public class ApiAdapter {

	private Token token;
	private static final String TAG = ApiAdapter.class.getName();

	//================================================================================
	// Use Cases
	//================================================================================

	public ArrayList<Campaign> getCampaignsFromServer(Metadata metadata) throws JSONException {
		token = TokenService.updateToken(token);
		Request request = requestCampaigns(metadata);
		OkHttpClient okHttpClient = new OkHttpClient();
		String responseBody = null;
		try {
			Response response = okHttpClient.newCall(request).execute();
			responseBody = response.body().string();
		} catch (IOException e) {
			Log.e(TAG, "exception", e);
		}

		return getCampaignDataFromJson(responseBody);
	}


	public void getCampaignFromServer(Metadata metadata, Campaign campaign) {
		token = TokenService.updateToken(token);
		Request request = requestCampaign(metadata, campaign);
		OkHttpClient okHttpClient = new OkHttpClient();
		try {
			okHttpClient.newCall(request).execute();
		} catch (IOException e) {
			Log.e(TAG, "exception", e);
		}
	}

	public Voucher getVoucherFromServer(Metadata metadata, Campaign campaign) throws JSONException {
		token = TokenService.updateToken(token);
		Request request = requestVoucher(metadata, campaign);
		OkHttpClient okHttpClient = new OkHttpClient();
		String responseBody = null;
		try {
			Response response = okHttpClient.newCall(request).execute();
			responseBody = response.body().string();
		} catch (IOException e) {
			Log.e(TAG, "exception", e);
		}

		return getVoucherDataFromJson(responseBody);
	}

	public boolean subscribeToNewsletter(Metadata metadata) throws JSONException {
		boolean subscribed = false;
		token = TokenService.updateToken(token);
		Request request = requestNewsletter(metadata);
		OkHttpClient okHttpClient = new OkHttpClient();
		try {
			okHttpClient.newCall(request).execute();
			subscribed = true;
		} catch (IOException e) {
			Log.e(TAG, "exception", e);
		}

		return subscribed;
	}

	//================================================================================
	// Requests
	//================================================================================

	private Request requestCampaigns(Metadata metadata) {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + UrlConstants.CAMPAIGNS_CONTAINER_PATH).newBuilder();
		Request request = new Request.Builder().url(urlBuilder.build().toString()).addHeader("Authorization", "bearer " + token.getAccessToken()).build();

		return request;
	}

	private Request requestCampaign(Metadata metadata, Campaign campaign) {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + UrlConstants.CAMPAIGNS_PATH + "/" + campaign.getId()).newBuilder();
		Request request = new Request.Builder().url(urlBuilder.build().toString()).addHeader("Authorization", "bearer " + token.getAccessToken()).build();

		return request;
	}

	private Request requestVoucher(Metadata metadata, Campaign campaign) {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + UrlConstants.CAMPAIGNS_PATH + "/" + campaign.getId() + UrlConstants.VOUCHER_PATH).newBuilder();

		RequestBody requestBody = RequestBody.create(null, new byte[]{});

		if (metadata != null) {
			urlBuilder.addQueryParameter("email", metadata.getEmail())
					  .addQueryParameter("gender", metadata.getGender().name())
					  .addQueryParameter("zipCode", metadata.getZipCode())
					  .addQueryParameter("firstName", metadata.getFirstName())
					  .addQueryParameter("lastName", metadata.getLastName())
					  .addQueryParameter("birthday", metadata.getBirthday());
		}

		Request request = buildHeader(urlBuilder, requestBody);

		return request;
	}

	private Request requestNewsletter(Metadata metadata) {
		HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + UrlConstants.NEWSLETTER_PATH).newBuilder();

		RequestBody requestBody = RequestBody.create(null, new byte[]{});

		if (metadata != null) {
			urlBuilder.addQueryParameter("email", metadata.getEmail())
					  .addQueryParameter("gender", metadata.getGender().name())
					  .addQueryParameter("zipCode", metadata.getZipCode())
					  .addQueryParameter("firstName", metadata.getFirstName())
					  .addQueryParameter("lastName", metadata.getLastName())
					  .addQueryParameter("birthday", metadata.getBirthday());
		}

		Request request = buildHeader(urlBuilder, requestBody);

		return request;
	}

	//================================================================================
	// Parsing
	//================================================================================

	private ArrayList<Campaign> getCampaignDataFromJson(String jsonStringCampaign) throws JSONException {
		if (TextUtils.isEmpty(jsonStringCampaign)) {
			return null;
		}

		JSONObject jsonObject = new JSONObject(jsonStringCampaign);

		JSONObject jsonGroupConfig = jsonObject.getJSONObject("groupConfig");
		JSONArray jsonCampaigns = jsonObject.getJSONArray("campaigns");

		FrontendConfig.getInstance().setFrontendConfig(jsonGroupConfig);

		ArrayList<Campaign> campaignsList = new ArrayList<>();

		for (int i = 0; i < jsonCampaigns.length(); i++) {
			Campaign campaign = new Campaign(jsonCampaigns.getJSONObject(i));
			campaignsList.add(campaign);
		}

		return campaignsList;
	}

	private Voucher getVoucherDataFromJson(String jsonStringVoucher) throws JSONException {
		if (TextUtils.isEmpty(jsonStringVoucher)) {
			return null;
		}

		JSONObject jsonObject = new JSONObject(jsonStringVoucher);

		return new Voucher(jsonObject);
	}

	private Request buildHeader(HttpUrl.Builder urlBuilder, RequestBody requestBody) {
		return new Request.Builder().url(urlBuilder.build().toString())
									.addHeader("Authorization", "bearer " + token.getAccessToken())
									.method("POST", requestBody)
									.build();
	}
}
