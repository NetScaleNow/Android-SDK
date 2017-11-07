package de.cmc.android.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deniz Kalem on 08.08.17.
 */

public class Voucher implements Parcelable {

	private String   code;
	private boolean  subscribedToNewsletter;
	private Campaign campaign;

	public Voucher(String code, boolean subscribedToNewsletter) {
		this.code = code;
		this.subscribedToNewsletter = subscribedToNewsletter;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isSubscribedToNewsletter() {
		return subscribedToNewsletter;
	}

	public void setSubscribedToNewsletter(boolean subscribedToNewsletter) {
		this.subscribedToNewsletter = subscribedToNewsletter;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Voucher(JSONObject json) throws JSONException {
		code = json.optString("code");
		subscribedToNewsletter = json.optBoolean("hasSubscribedToNewsletter");
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.code);
		dest.writeByte(this.subscribedToNewsletter ? (byte) 1 : (byte) 0);
		dest.writeParcelable(this.campaign, flags);
	}

	protected Voucher(Parcel in) {
		this.code = in.readString();
		this.subscribedToNewsletter = in.readByte() != 0;
		this.campaign = in.readParcelable(Campaign.class.getClassLoader());
	}

	public static final Parcelable.Creator<Voucher> CREATOR = new Parcelable.Creator<Voucher>() {
		@Override
		public Voucher createFromParcel(Parcel source) {return new Voucher(source);}

		@Override
		public Voucher[] newArray(int size) {return new Voucher[size];}
	};
}
