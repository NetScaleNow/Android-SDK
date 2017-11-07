package de.cmc.android.config;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deniz Kalem on 10.10.17.
 */

public class FrontendConfig {
	private static FrontendConfig instance                = null;
	private        String         termsUrl                = "";
	private        String         privacyUrl              = "";
	private        String         copyrightUrl            = "";
	private        String         copyrightText           = "";
	private        String         primaryColor            = "/";
	private        String         campaignTitle           = "";
	private        String         campaignSubtitle        = "";
	private        String         newsletterTitle         = "";
	private        String         newsletterTitleNew      = "";
	private        String         newsletterSubtitle      = "";
	private        String         newsletterSubtitleNew   = "";
	private        String         newsletterUserAgreement = "";
	private        String         voucherTitle            = "";
	private        String         voucherNote             = "";
	private        String         maximumReached          = "";

	protected FrontendConfig() {

	}

	public static synchronized FrontendConfig getInstance() {
		if (instance == null) {
			instance = new FrontendConfig();
		}
		return instance;
	}

	public void setFrontendConfig(JSONObject json) throws JSONException {
		termsUrl = json.optString("termsLink");
		privacyUrl = json.optString("privacyPolicyLink");
		copyrightUrl = json.optString("copyrightLink");
		copyrightText = json.optString("copyrightText");
		primaryColor = json.optString("primaryColor");
		campaignTitle = json.optString("campaignListTitle");
		campaignSubtitle = json.optString("campaignListSubtitle");
		newsletterTitle = json.optString("newsletterTitle");
		newsletterTitleNew = json.optString("nextVoucherTitle");
		newsletterSubtitle = json.optString("newsletterSubtitle");
		newsletterSubtitleNew = json.optString("nextVoucherSubtitle");
		newsletterUserAgreement = json.optString("userAgreementText");
		voucherTitle = json.optString("voucherTitle");
		voucherNote = json.optString("voucherNote");
		maximumReached = json.optString("maximumReachedText");
	}

	public String getTermsUrl() {
		return termsUrl;
	}

	public String getPrivacyUrl() {
		return privacyUrl;
	}

	public String getCopyrightUrl() {
		return copyrightUrl;
	}

	public String getCopyrightText() {
		return copyrightText;
	}

	public String getPrimaryColor() {
		return primaryColor;
	}

	public String getCampaignTitle() {
		return campaignTitle;
	}

	public String getCampaignSubtitle() {
		return campaignSubtitle;
	}

	public String getNewsletterTitle() {
		return newsletterTitle;
	}

	public String getNewsletterTitleNew() {
		return newsletterTitleNew;
	}

	public String getNewsletterSubtitle() {
		return newsletterSubtitle;
	}

	public String getNewsletterSubtitleNew() {
		return newsletterSubtitleNew;
	}

	public String getNewsletterUserAgreement() {
		return newsletterUserAgreement;
	}

	public String getVoucherTitle() {
		return voucherTitle;
	}

	public String getVoucherNote() {
		return voucherNote;
	}

	public String getMaximumReached() {
		return maximumReached;
	}
}
