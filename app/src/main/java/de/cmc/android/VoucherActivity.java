package de.cmc.android;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import de.cmc.android.config.Config;
import de.cmc.android.config.FrontendConfig;
import de.cmc.android.data.Metadata;
import de.cmc.android.ui.StartpageFragment;
import de.cmc.android.utils.UrlConstants;
import de.cmc.android.utils.ValueConstants;

public class VoucherActivity extends AppCompatActivity {

	private Fragment        startpageFragment;
	private String          email;
	private String          zipCode;
	private String          firstName;
	private String          lastName;
	private String          birthday;
	private Metadata.Gender gender;
	private FrontendConfig  frontendConfig;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_voucher);

		frontendConfig = FrontendConfig.getInstance();

		if (savedInstanceState == null) {
			setFragment(startpageFragment = new StartpageFragment());
			email = "";
			zipCode = "";
			firstName = "";
			lastName = "";
			birthday = "";
			gender = Metadata.Gender.MALE;
		} else {
			startpageFragment = getSupportFragmentManager().getFragment(savedInstanceState, "startpage");
		}

		int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
		int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);

		this.getWindow().setLayout(width, height);

		TextView terms = (TextView) findViewById(R.id.tv_terms);
		TextView privacy = (TextView) findViewById(R.id.tv_privacy);
		TextView copyright = (TextView) findViewById(R.id.tv_copyright);

		terms.setPaintFlags(terms.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		privacy.setPaintFlags(privacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
		copyright.setPaintFlags(copyright.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

		String apiKey = getIntent().getStringExtra(Config.API_KEY);
		String apiSecret = getIntent().getStringExtra(Config.API_SECRET);
		String campaignTitle = getIntent().getStringExtra(Config.CAMPAIGN_TITLE);
		String campaignSubtitle = getIntent().getStringExtra(Config.CAMPAIGN_SUBTITLE);
		String accentColor = getIntent().getStringExtra(Config.ACCENT_COLOR);

		if (apiKey != null) {
			ValueConstants.API_KEY_VALUE = apiKey;
		}

		if (apiSecret != null) {
			ValueConstants.API_SECRET_VALUE = apiSecret;
		}

		if (campaignTitle != null) {
			ValueConstants.CAMPAIGN_TITLE_VALUE = campaignTitle;
		}

		if (campaignSubtitle != null) {
			ValueConstants.CAMPAIGN_SUBTITLE_VALUE = campaignSubtitle;
		}

		if (accentColor != null) {
			ValueConstants.ACCENT_COLOR_VALUE = accentColor;
		}

		if (getIntent().getStringExtra(Config.EMAIL) != null) {
			email = getIntent().getStringExtra(Config.EMAIL);
		}

		if (getIntent().getStringExtra(Config.ZIP_CODE) != null) {
			zipCode = getIntent().getStringExtra(Config.ZIP_CODE);
		}

		if (getIntent().getStringExtra(Config.FIRST_NAME) != null) {
			firstName = getIntent().getStringExtra(Config.FIRST_NAME);
		}

		if (getIntent().getStringExtra(Config.LAST_NAME) != null) {
			lastName = getIntent().getStringExtra(Config.LAST_NAME);
		}

		if (getIntent().getStringExtra(Config.BIRTHDAY) != null) {
			birthday = getIntent().getStringExtra(Config.BIRTHDAY);
		}

		if (getIntent().getSerializableExtra(Config.GENDER) != null) {
			gender = (Metadata.Gender) getIntent().getSerializableExtra(Config.GENDER);
		}

		if (savedInstanceState != null && savedInstanceState.get("metadata") != null) {
			ValueConstants.METADATA_VALUE = (Metadata) savedInstanceState.getSerializable("metadata");
		} else {
			ValueConstants.METADATA_VALUE = new Metadata(email, gender, zipCode, firstName, lastName, birthday);
		}
	}

	protected void setFragment(Fragment startpageFragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().add(R.id.fragment_start, startpageFragment, "startpage").commit();
	}

	public void showTermDetails(View view) {
		Intent urlIntent = new Intent(Intent.ACTION_VIEW);
		if (!frontendConfig.getTermsUrl().isEmpty()) {
			urlIntent.setData(Uri.parse(frontendConfig.getTermsUrl()));
		} else {
			urlIntent.setData(Uri.parse(UrlConstants.TERMS_URL));
		}
		startActivity(urlIntent);
	}

	public void showPrivacyDetails(View view) {
		Intent urlIntent = new Intent(Intent.ACTION_VIEW);
		if (!frontendConfig.getPrivacyUrl().isEmpty()) {
			urlIntent.setData(Uri.parse(frontendConfig.getPrivacyUrl()));
		} else {
			urlIntent.setData(Uri.parse(UrlConstants.PRIVACY_URL));
		}
		startActivity(urlIntent);
	}

	public void showCopyrightDetails(View view) {
		Intent urlIntent = new Intent(Intent.ACTION_VIEW);
		if (!frontendConfig.getCopyrightUrl().isEmpty()) {
			urlIntent.setData(Uri.parse(frontendConfig.getCopyrightUrl()));
		} else {
			urlIntent.setData(Uri.parse(UrlConstants.COPYRIGHT_URL));
		}
		startActivity(urlIntent);
	}

	public void quitDialog(View view) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.clear();
		editor.apply();
		finish();
	}

	public void backToOverView(View view) {
		super.onBackPressed();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (startpageFragment != null) {
			getSupportFragmentManager().putFragment(outState, "startpage", startpageFragment);
		}
		outState.putSerializable("metadata", ValueConstants.METADATA_VALUE);
	}

	@Override
	public void onBackPressed() {
		//		super.onBackPressed();
	}
}
