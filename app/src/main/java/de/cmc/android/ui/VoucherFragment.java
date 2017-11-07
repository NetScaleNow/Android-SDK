package de.cmc.android.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import de.cmc.android.R;
import de.cmc.android.config.FrontendConfig;
import de.cmc.android.data.Voucher;
import de.cmc.android.service.ApiAdapter;
import de.cmc.android.utils.ValueConstants;

public class VoucherFragment extends Fragment {

	private Button   registerNewsletter;
	private CheckBox userAgreement;
	private TextView newsletterTitle, newsletterSubtitle, maximum;
	private ApiAdapter        apiAdapter;
	private boolean           subscribedToNewsletter;
	private SharedPreferences sharedPreferences;
	private Voucher           voucher;
	private int               usedVoucher;
	private FrontendConfig    frontendConfig;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_voucher, container, false);

		frontendConfig = FrontendConfig.getInstance();
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
		subscribedToNewsletter = sharedPreferences.getBoolean("subscribed", false);
		usedVoucher = sharedPreferences.getInt("usedVoucher", 0);

		if (savedInstanceState != null) {
			subscribedToNewsletter = savedInstanceState.getBoolean("subscribed");
			usedVoucher = savedInstanceState.getInt("usedVoucher");
		}

		apiAdapter = new ApiAdapter();
		userAgreement = (CheckBox) view.findViewById(R.id.cb_newsletter_user_agreement);
		registerNewsletter = (Button) view.findViewById(R.id.bt_register_newsletter);
		Typeface type = Typeface.createFromAsset(getActivity().getAssets(), "AbrilFatface-Regular.otf");
		newsletterTitle = (TextView) view.findViewById(R.id.tv_newsletter_title);
		newsletterTitle.setTypeface(type);
		newsletterSubtitle = (TextView) view.findViewById(R.id.tv_newsletter_subtitle);
		newsletterSubtitle.setTypeface(type);
		maximum = (TextView) view.findViewById(R.id.tv_voucher_maximum);
		maximum.setTypeface(type);
		TextView shopLink = (TextView) view.findViewById(R.id.tv_shoplink);
		TextView voucherTitle = (TextView) view.findViewById(R.id.tv_voucher_title);
		TextView voucherNote = (TextView) view.findViewById(R.id.tv_voucher_note);

		if (!frontendConfig.getNewsletterTitle().isEmpty()) {
			newsletterTitle.setText(frontendConfig.getNewsletterTitle());
		}

		if (!frontendConfig.getNewsletterSubtitle().isEmpty()) {
			newsletterSubtitle.setText(frontendConfig.getNewsletterSubtitle());
		}

		if (!frontendConfig.getNewsletterUserAgreement().isEmpty()) {
			userAgreement.setText(frontendConfig.getNewsletterUserAgreement());
		}

		if (!frontendConfig.getVoucherTitle().isEmpty()) {
			voucherTitle.setText(frontendConfig.getVoucherTitle());
		}

		if (!frontendConfig.getVoucherNote().isEmpty()) {
			voucherNote.setText(frontendConfig.getVoucherNote());
		}

		EditText code = (EditText) view.findViewById(R.id.et_code);
		code.setText(voucher.getCode());

		userAgreement.setButtonTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
		registerNewsletter.setBackgroundTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
		shopLink.setTextColor(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));

		userAgreement.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					userAgreement.setButtonTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
				}
			}
		});

		registerNewsletter.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (userAgreement.isChecked()) {
					if (subscribedToNewsletter) {
						usedVoucher = 1;
						SharedPreferences.Editor editor = sharedPreferences.edit();
						editor.putInt("usedVoucher", usedVoucher);
						editor.apply();
						getActivity().getSupportFragmentManager().popBackStack("startpage", FragmentManager.POP_BACK_STACK_INCLUSIVE);
					} else {
						new NewsletterTask().execute();
					}
				} else {
					userAgreement.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorWarning));
				}
			}
		});

		if (!TextUtils.isEmpty(voucher.getCampaign().getShopLink())) {
			shopLink.setVisibility(View.VISIBLE);
		}

		shopLink.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent urlIntent = new Intent(Intent.ACTION_VIEW);
				urlIntent.setData(Uri.parse(voucher.getCampaign().getShopLink()));
				startActivity(urlIntent);
			}
		});

		if (usedVoucher == 1) {
			maximumVoucher();
		} else if (subscribedToNewsletter) {
			changeText();
		}

		return view;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle != null) {
			voucher = bundle.getParcelable("voucher");
		}
	}

	private class NewsletterTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				return apiAdapter.subscribeToNewsletter(ValueConstants.METADATA_VALUE);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return false;
		}

		@Override
		protected void onPostExecute(Boolean subscribed) {
			super.onPostExecute(subscribed);
			if (subscribed) {
				subscribedToNewsletter = true;
				SharedPreferences.Editor editor = sharedPreferences.edit();
				editor.putBoolean("subscribed", subscribedToNewsletter);
				editor.apply();
				changeText();
			} else {
				Toast.makeText(getActivity().getApplicationContext(), "Es ist ein Fehler aufgetreten", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void changeText() {
		if (!frontendConfig.getNewsletterTitleNew().isEmpty()) {
			newsletterTitle.setText(frontendConfig.getNewsletterTitleNew());
		} else {
			newsletterTitle.setText(getString(R.string.newsletter_title_new));
		}

		if (!frontendConfig.getNewsletterSubtitleNew().isEmpty()) {
			newsletterSubtitle.setText(frontendConfig.getNewsletterSubtitleNew());
		} else {
			newsletterSubtitle.setText(getString(R.string.newsletter_subtitle_new));
		}

		registerNewsletter.setText(getString(R.string.voucher_button_newsletter_new));
		userAgreement.setVisibility(View.INVISIBLE);
	}

	public void maximumVoucher() {
		newsletterTitle.setVisibility(View.INVISIBLE);
		userAgreement.setVisibility(View.INVISIBLE);
		registerNewsletter.setVisibility(View.INVISIBLE);
		newsletterSubtitle.setVisibility(View.INVISIBLE);
		maximum.setVisibility(View.VISIBLE);
		if (!frontendConfig.getMaximumReached().isEmpty()) {
			String lineSep = System.getProperty("line.separator");
			String maximumReached = frontendConfig.getMaximumReached().replaceAll("<br>", lineSep);
			maximum.setText(maximumReached);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("subscribed", subscribedToNewsletter);
		outState.putInt("usedVoucher", usedVoucher);
	}

}
