package de.cmc.android.service;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import de.cmc.android.data.Token;
import de.cmc.android.utils.UrlConstants;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static de.cmc.android.utils.ValueConstants.API_KEY_VALUE;
import static de.cmc.android.utils.ValueConstants.API_SECRET_VALUE;
import static de.cmc.android.utils.UrlConstants.BASE_URL;
import static de.cmc.android.utils.ValueConstants.CLIENT_ID_VALUE;

/**
 * Created by Deniz Kalem on 08.08.17.
 */

public class TokenService {

	public final static  MediaType JSON = MediaType.parse("application/json; charset=utf-8");
	private static final String    TAG  = TokenService.class.getName();

	public static Token requestToken() {
		Token token = new Token();
		OkHttpClient okHttpClient;
		Request request;
		Response response;

		try {
			okHttpClient = new OkHttpClient();
			HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + UrlConstants.TOKEN_PATH).newBuilder();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("clientId", CLIENT_ID_VALUE);
			jsonObject.put("username", API_KEY_VALUE);
			jsonObject.put("password", API_SECRET_VALUE);

			RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

			request = new Request.Builder().url(urlBuilder.build().toString())
										   .addHeader("content-type", "application/json; charset=utf-8")
										   .post(requestBody)
										   .build();

			response = okHttpClient.newCall(request).execute();

			performTokenRequest(token, response.body().string());

			Log.d(TAG, token.getAccessToken());
		} catch (Exception e) {
			Log.e(TAG, "exception", e);
		}

		return token;
	}

	public static Token updateToken(Token token) {
		boolean tokenValid = false;
		boolean refreshTokenValid = false;

		if (token != null && token.getAccessToken() != null && token.getTokenTimeout() != null) {
			if (new Date().getTime() < token.getTokenTimeout().getTime()) {
				tokenValid = true;
			}

			if (token.getRefreshToken() != null && token.getRefreshTokenTimeout() != null && new Date().getTime() < token.getRefreshTokenTimeout().getTime()) {
				refreshTokenValid = true;
			}
		}

		if (tokenValid) {
			// TODO Callback
			return null;
		} else if (refreshTokenValid) {
			return refreshToken(token);
		} else {
			return requestToken();
		}
	}

	public static Token refreshToken(Token refreshToken) {
		OkHttpClient okHttpClient;
		Request request;
		Response response;

		try {
			okHttpClient = new OkHttpClient();
			HttpUrl.Builder urlBuilder = HttpUrl.parse(BASE_URL + UrlConstants.REFRESH_PATH).newBuilder();

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("clientId", CLIENT_ID_VALUE);
			jsonObject.put("refreshToken", refreshToken);

			RequestBody requestBody = RequestBody.create(JSON, jsonObject.toString());

			request = new Request.Builder().url(urlBuilder.build().toString())
										   .addHeader("content-type", "application/json; charset=utf-8")
										   .post(requestBody)
										   .build();

			response = okHttpClient.newCall(request).execute();

			performTokenRequest(refreshToken, response.body().string());

			Log.d(TAG, refreshToken.getRefreshToken());

		} catch (Exception e) {
			Log.e(TAG, "exception", e);
		}

		return refreshToken;
	}

	private static void performTokenRequest(Token token, String jsonStringToken) throws JSONException {
		if (jsonStringToken == null || "".equals(jsonStringToken)) {
			return;
		}

		JSONObject jsonObjectToken = new JSONObject(jsonStringToken);

		token.setAccessToken(jsonObjectToken.optString("access_token"));
		token.setRefreshToken(jsonObjectToken.optString("refresh_token"));
		token.setTokenTimeout(new Date((long) jsonObjectToken.optDouble("expires_in")));
		token.setRefreshTokenTimeout(new Date((long) jsonObjectToken.optDouble("refresh_expires_in")));
	}

}
