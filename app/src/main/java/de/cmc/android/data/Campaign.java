package de.cmc.android.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Deniz Kalem on 08.08.17.
 */

public class Campaign implements Parcelable {

	private int    id;
	private String name;
	private String discount;
	private String description;
	private String limitations;
	private String logUrl;
	private String headerUrl;
	private String shopLink;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLimitations() {
		return limitations;
	}

	public void setLimitations(String limitations) {
		this.limitations = limitations;
	}

	public String getLogUrl() {
		return logUrl;
	}

	public void setLogUrl(String logUrl) {
		this.logUrl = logUrl;
	}

	public String getHeaderUrl() {
		return headerUrl;
	}

	public void setHeaderUrl(String headerUrl) {
		this.headerUrl = headerUrl;
	}

	public String getShopLink() {
		return shopLink;
	}

	public void setShopLink(String shopLink) {
		this.shopLink = shopLink;
	}

	public Campaign(JSONObject json) throws JSONException {
		id = json.optInt("id");
		name = json.optString("name");
		discount = json.optString("value");
		logUrl = json.optString("advertiserLogoUrl");
		headerUrl = json.optString("campaignImageUrl");
		description = json.optString("description");
		shopLink = json.optString("shopLink");

		final String limits = json.optString("limitations");
		if (limits != null) {
			limitations = limits.replace("\\n", "\n");
		}
	}

	public Campaign(int id, String name, String discount, String logUrl, String headerUrl, String limitations, String description, String shopLink) {
		this.id = id;
		this.name = name;
		this.discount = discount;
		this.logUrl = logUrl;
		this.headerUrl = headerUrl;
		this.limitations = limitations;
		this.description = description;
		this.shopLink = shopLink;
	}

	@Override
	public int describeContents() { return 0; }

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		dest.writeString(this.name);
		dest.writeString(this.discount);
		dest.writeString(this.description);
		dest.writeString(this.limitations);
		dest.writeString(this.logUrl);
		dest.writeString(this.headerUrl);
		dest.writeString(this.shopLink);
	}

	protected Campaign(Parcel in) {
		this.id = in.readInt();
		this.name = in.readString();
		this.discount = in.readString();
		this.description = in.readString();
		this.limitations = in.readString();
		this.logUrl = in.readString();
		this.headerUrl = in.readString();
		this.shopLink = in.readString();
	}

	public static final Parcelable.Creator<Campaign> CREATOR = new Parcelable.Creator<Campaign>() {
		@Override
		public Campaign createFromParcel(Parcel source) {return new Campaign(source);}

		@Override
		public Campaign[] newArray(int size) {return new Campaign[size];}
	};
}
