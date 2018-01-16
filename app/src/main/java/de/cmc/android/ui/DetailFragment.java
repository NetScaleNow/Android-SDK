package de.cmc.android.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import de.cmc.android.R;
import de.cmc.android.adapter.ViewPagerAdapter;
import de.cmc.android.config.FrontendConfig;
import de.cmc.android.data.Campaign;
import de.cmc.android.data.Voucher;
import de.cmc.android.service.ApiAdapter;
import de.cmc.android.utils.UrlConstants;
import de.cmc.android.utils.ValueConstants;

import static de.cmc.android.R.id.circle_indicator;

public class DetailFragment extends Fragment implements View.OnClickListener {

	private CheckBox acceptTerms;
	private Button   showVoucher;
	private EditText emailAddress;
	private Campaign campaign;
	private int      bottom, top, right, left;
	private ApiAdapter     apiAdapter;
	private Voucher        voucher;
	private ViewPager      viewPager;
	private Dialog         urlDialog;
	private FrontendConfig frontendConfig;
	de.cmc.android.utils.CircleIndicator indicator;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if (bundle != null) {
			campaign = bundle.getParcelable("campaign");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_detail, container, false);

		frontendConfig = FrontendConfig.getInstance();
		acceptTerms = (CheckBox) view.findViewById(R.id.cb_terms);
		TextView terms = (TextView) view.findViewById(R.id.tv_terms_text);
		String termsText = getString(R.string.detail_terms_1) + " <a href='" + UrlConstants.PRIVACY_URL + "' > " + getString(R.string.detail_terms_2) + "</a> " + getString(
				R.string.detail_terms_3);
		terms.setText(fromHtml(termsText));
		terms.setLinkTextColor(Color.BLACK);
		terms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createUrlDialog();
			}
		});

		showVoucher = (Button) view.findViewById(R.id.bt_show_voucher);
		ImageButton showLimitations = (ImageButton) view.findViewById(R.id.ib_limitations);
		ImageView campaignLogo = (ImageView) view.findViewById(R.id.iv_campaign_logo);
		emailAddress = (EditText) view.findViewById(R.id.et_email);

		bottom = emailAddress.getPaddingBottom();
		top = emailAddress.getPaddingTop();
		right = emailAddress.getPaddingRight();
		left = emailAddress.getPaddingLeft();

		if (!TextUtils.isEmpty(ValueConstants.METADATA_VALUE.email)) {
			emailAddress.setText(ValueConstants.METADATA_VALUE.getEmail());
		}

		Picasso.with(getContext()).load(campaign.getLogUrl()).into(campaignLogo);
		TextView discount = (TextView) view.findViewById(R.id.tv_discount);
		discount.setText(campaign.getDiscount() + " " + getString(R.string.discount_text));
		showVoucher.setOnClickListener(this);

		apiAdapter = new ApiAdapter();

		acceptTerms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					acceptTerms.setButtonTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
				}
			}
		});

		emailAddress.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (isValidEmail(s)) {
					emailAddress.setBackgroundResource(R.drawable.edit_text_border);
				} else {
					if (count != 0) {
						emailAddress.setBackgroundResource(R.drawable.edit_text_border_invalid);
					}
				}
				emailAddress.setPadding(left, top, right, bottom);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		showLimitations.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (viewPager.getCurrentItem() == 0) {
					viewPager.setCurrentItem(1, true);
				} else {
					viewPager.setCurrentItem(0, true);
				}
			}
		});

		initViewPager(view, campaign.getHeaderUrl(), campaign.getLimitationsDescription());

		showLimitations.setBackgroundTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
		acceptTerms.setButtonTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
		showVoucher.setBackgroundTintList(ColorStateList.valueOf(StartpageFragment.APP_PRIMARY_COLOR));
		indicator.setIndicatorColor(StartpageFragment.APP_PRIMARY_COLOR);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new FetchCampaignTask().execute("");
	}

	private void initViewPager(View view, String headerUrl, String limitations) {
		viewPager = (ViewPager) view.findViewById(R.id.pager);
		viewPager.setAdapter(new ViewPagerAdapter(getContext(), headerUrl, limitations));
		indicator = (de.cmc.android.utils.CircleIndicator) view.findViewById(circle_indicator);
		indicator.setViewPager(viewPager);
	}

	@Override
	public void onClick(View v) {
		if (acceptTerms.isChecked()) {
			if (isValidEmail(emailAddress.getText().toString())) {
				showVoucher.setEnabled(false);
				ValueConstants.METADATA_VALUE.setEmail(emailAddress.getText().toString());
				FetchVoucherTask fetchVoucherTask = new FetchVoucherTask(new VoucherCallback() {
					@Override
					public void processFinish(Voucher output) {
						VoucherFragment voucherFragment = new VoucherFragment();
						if (output != null) {
							voucher = output;
							voucher.setCampaign(campaign);

							if (voucher != null) {
								Bundle bundle = new Bundle();
								bundle.putParcelable("voucher", voucher);
								voucherFragment.setArguments(bundle);
							}
						}

						FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
						FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
						fragmentTransaction.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
						fragmentTransaction.replace(R.id.fragment_start, voucherFragment, "voucher");
						fragmentTransaction.addToBackStack("detail");
						fragmentTransaction.commit();
					}
				});

				fetchVoucherTask.execute();
			} else {
				emailAddress.setBackgroundResource(R.drawable.edit_text_border_invalid);
				emailAddress.setPadding(left, top, right, bottom);
			}

		} else {
			if (!isValidEmail(emailAddress.getText().toString())) {
				emailAddress.setBackgroundResource(R.drawable.edit_text_border_invalid);
				emailAddress.setPadding(left, top, right, bottom);
			}
			acceptTerms.setButtonTintList(ContextCompat.getColorStateList(getActivity(), R.color.colorWarning));
		}
	}

	public boolean isValidEmail(CharSequence target) {
		return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	@Override
	public void setRetainInstance(boolean retain) {
		super.setRetainInstance(retain);
	}


	private class FetchCampaignTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			apiAdapter.getCampaignFromServer(ValueConstants.METADATA_VALUE, campaign);
			return "Executed";
		}
	}

	private class FetchVoucherTask extends AsyncTask<Void, Void, Voucher> {

		private final VoucherCallback voucherCallback;

		FetchVoucherTask(VoucherCallback voucherCallback) {
			this.voucherCallback = voucherCallback;
		}

		@Override
		protected Voucher doInBackground(Void... params) {
			Voucher voucher = null;
			try {
				voucher = apiAdapter.getVoucherFromServer(ValueConstants.METADATA_VALUE, campaign);
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return voucher;
		}

		@Override
		protected void onPostExecute(Voucher voucherFromTask) {
			if (voucherFromTask != null) {
				voucherCallback.processFinish(voucherFromTask);
			}
		}
	}

	private interface VoucherCallback {
		void processFinish(Voucher output);
	}

	@SuppressWarnings("deprecation")
	public static Spanned fromHtml(String html) {
		Spanned result;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
			result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
		} else {
			result = Html.fromHtml(html);
		}
		return result;
	}

	public void createUrlDialog() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
		builderSingle.setTitle(getString(R.string.detail_choose_action));


		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.url_item_dialog) {
			@NonNull
			@Override
			public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
				TextView textView = (TextView) super.getView(position, convertView, parent);
				textView.setTextColor(StartpageFragment.APP_PRIMARY_COLOR);
				return textView;
			}
		};
		arrayAdapter.add(getString(R.string.detail_show_privacy));
		arrayAdapter.add(getString(R.string.detail_show_terms));

		builderSingle.setNegativeButton(getString(R.string.detail_dialog_cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int position) {
				Intent urlIntent = new Intent(Intent.ACTION_VIEW);
				if (position == 0) {
					if (!frontendConfig.getPrivacyUrl().isEmpty()) {
						urlIntent.setData(Uri.parse(frontendConfig.getPrivacyUrl()));
					} else {
						urlIntent.setData(Uri.parse(UrlConstants.PRIVACY_URL));
					}
				} else if (!frontendConfig.getTermsUrl().isEmpty()) {
					urlIntent.setData(Uri.parse(frontendConfig.getTermsUrl()));
				} else {
					urlIntent.setData(Uri.parse(UrlConstants.TERMS_URL));
				}
				startActivity(urlIntent);
			}
		});

		urlDialog = builderSingle.show();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (urlDialog != null) {
			urlDialog.dismiss();
		}
	}
}
