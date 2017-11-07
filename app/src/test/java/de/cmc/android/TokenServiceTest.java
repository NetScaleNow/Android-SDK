//package de.cmc.android;
//
//import org.json.JSONException;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.ArrayList;
//
//import de.cmc.android.data.Campaign;
//import de.cmc.android.data.Metadata;
//import de.cmc.android.data.Token;
//import de.cmc.android.data.Voucher;
//import de.cmc.android.service.ApiAdapter;
//import de.cmc.android.service.TokenService;
//
//import static org.junit.Assert.*;
//
///**
// * Created by Deniz Kalem on 09.08.17.
// */
//
//public class TokenServiceTest {
//
//	private TokenService tokenService;
//	private ApiAdapter   apiAdapter;
//	private Token        token;
//	private Metadata     metadata;
//
//	@Before
//	public void setUp() {
//		tokenService = new TokenService();
//		apiAdapter = new ApiAdapter();
//		token = new Token();
//		metadata = new Metadata("mustermann@gmx.de", Metadata.Gender.MALE, "70736", "Max", "Mustermann", "01.01.1985");
//	}
//
//	@Test
//	public void requestToken() {
//		tokenService.requestToken();
//	}
//
//	@Test
//	public void refreshToken() {
//		tokenService.refreshToken(token);
//	}
//
//	@Test
//	public void getCampaignsFromServer() throws JSONException {
//		ArrayList<Campaign> campaignList = apiAdapter.getCampaignsFromServer(metadata);
//
//		assertTrue(campaignList != null && campaignList.size() > 0);
//	}
//
//	@Test
//	public void getVoucherFromServer() throws JSONException {
//		Campaign campaign = apiAdapter.getCampaignsFromServer(metadata).get(0);
//		Voucher voucher = apiAdapter.getVoucherFromServer(metadata, campaign);
//
//		assertTrue(voucher != null);
//	}
//
//	@Test
//	public void subscribeToNewsletter() throws JSONException {
//		apiAdapter.subscribeToNewsletter(metadata);
//	}
//
//}
