package refried.rfid_inventory.PrivacyPolicyWebView;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import refried.rfid_inventory.R;

public class privacy_policy extends Fragment {

    private static String ppUrl ="https://docs.google.com/document/u/1/d/e/2PACX-1vTBCfZVcWIx1N1J_ThrFkWAIranRq4r0HCMJmZTYt0aq9ZTa-Z6ucIdNsug7tAmu_cBB5spi9KZjeHX/pub";
    WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_privacy_policy, container, false);
        mWebView = rootView.findViewById(R.id.privacyPolicyWebView);
        mWebView.loadUrl(ppUrl);
        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        getActivity().setTitle("Privacy Policy");
        return rootView;
    }
}
