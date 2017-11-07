package de.cmc.android.data;

import java.util.Date;

/**
 * Created by Deniz Kalem on 09.08.17.
 */

public class Token {

	private String accessToken;
	private String refreshToken;
	private Date   tokenTimeout;
	private Date   refreshTokenTimeout;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Date getTokenTimeout() {
		return tokenTimeout;
	}

	public void setTokenTimeout(Date tokenTimeout) {
		this.tokenTimeout = tokenTimeout;
	}

	public Date getRefreshTokenTimeout() {
		return refreshTokenTimeout;
	}

	public void setRefreshTokenTimeout(Date refreshTokenTimeout) {
		this.refreshTokenTimeout = refreshTokenTimeout;
	}
}
