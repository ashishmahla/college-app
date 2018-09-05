package in.ac.bkbiet.bkbiet.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import in.ac.bkbiet.bkbiet.R;
import in.ac.bkbiet.bkbiet.models.ActiveLink;
import in.ac.bkbiet.bkbiet.models.ActiveLinkAdapter;
import in.ac.bkbiet.bkbiet.utils.Uv;

/**
 * +Created by Ashish on 2/16/2018.
 */

public class FragWeb extends Fragment implements ActiveLinkAdapter.ActiveLinkClickListener {

    private static final String BASE_URL = "http://bkbiet.ml/index.php/login";
    private static final String ERROR_PAGE = "file:///android_asset/raw/error.html";
    private static final String HOME_PAGE = "file:///android_asset/raw/home_page.html";
    private static final String TAG = FragWeb.class.getSimpleName();
    final DatabaseReference activeLinks = FirebaseDatabase.getInstance().getReference().child("active_links");
    WebView webView;
    TextView urlBar;
    ProgressBar progressBar;
    ImageView iv_home;
    ErrorObject errorObject;
    CardView cardUrlBar;
    ImageView lol;
    ImageView iv_reload;
    ActiveLinkAdapter mAdapter;
    ArrayList<ActiveLink> activeLinkList = new ArrayList<>();
    RecyclerView recyclerView;
    ValueEventListener activeLinksListener = null;
    RelativeLayout homeWrapper;
    FloatingActionButton addNewLink;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.frag_web, container, false);

        initViews(parent);
        initRecyclerView(getActivity(), parent, R.id.rv_fw_active_links);
        loadHome();

        return parent;
    }

    private void refreshList() {
        activeLinkList.clear();
        ActiveLink activeLink = new ActiveLink();
        activeLink.setName("Go to home page");
        activeLink.setUrl(BASE_URL);
        activeLink.setAuthorName("");
        activeLink.setDesc("");
        activeLink.setAuthorUsername("14ebkcs017");
        activeLink.setExpiryDate("16/06/1996");
        activeLink.setExpirable(false);

        activeLinkList.add(activeLink);
    }

    private void loadHome() {
        if (activeLinksListener != null) {
            activeLinks.removeEventListener(activeLinksListener);
            activeLinksListener = null;
        }

        activeLinksListener = activeLinks.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                refreshList();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActiveLink activeLink = snapshot.getValue(ActiveLink.class);
                    if (!activeLinkList.contains(activeLink)) {
                        activeLinkList.add(activeLink);
                    }
                }
                Collections.sort(activeLinkList);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerView(Context context, View parent, int rv_id) {
        recyclerView = parent.findViewById(rv_id);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new ActiveLinkAdapter(activeLinkList, this);
        recyclerView.setAdapter(mAdapter);
    }

    private void initViews(final View parent) {
        homeWrapper = parent.findViewById(R.id.rl_fw_home_layout);
        urlBar = parent.findViewById(R.id.tv_fw_url_bar);
        lol = parent.findViewById(R.id.iv_fw_lol);
        cardUrlBar = parent.findViewById(R.id.card_fw_url_bar);
        cardUrlBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lol.animate().alpha(1).setDuration(500).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        lol.animate().alpha(0).setDuration(1000);
                    }
                });

            }
        });

        progressBar = parent.findViewById(R.id.pb_fw_progress);
        webView = parent.findViewById(R.id.wv_fw_web_view);

        iv_home = parent.findViewById(R.id.iv_fw_home);
        iv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webView.loadUrl(HOME_PAGE);
                urlBar.setText(Html.fromHtml("<font color= #009900>Home</font>"));
                webView.clearHistory();
                homeWrapper.setVisibility(View.VISIBLE);
            }
        });

        iv_reload = parent.findViewById(R.id.iv_fw_reload);
        iv_reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (webView.getUrl()) {
                    case ERROR_PAGE:
                        webView.loadUrl(errorObject.errorUrl);
                        urlBar.setText(webView.getUrl());
                        break;

                    case HOME_PAGE:
                        break;

                    default:
                        webView.reload();
                        urlBar.setText(webView.getUrl());
                }
            }
        });

        webView.loadUrl(HOME_PAGE);
        urlBar.setText(Html.fromHtml("<font color= #009900>Home</font>"));

        webView.addJavascriptInterface(new JavascriptInterface(getActivity()), "Android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //String url = webView.getUrl();
                if (url.contains("bkbiet.ml/")) {
                    urlBar.setText(url);
                    return false;
                }

                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);
                progressBar.setMax(100);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
                progressBar.setProgress(0);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if (!failingUrl.equals(ERROR_PAGE)) {
                    errorObject = new ErrorObject(errorCode, failingUrl, description);

                    Log.e(TAG, "onReceivedError: " + errorCode + description);
                    webView.loadUrl(ERROR_PAGE);
                    urlBar.setText(Html.fromHtml("<font color= #d50000>Error</font>"));
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);

        addNewLink = parent.findViewById(R.id.fab_fw_add_link);
        addNewLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
                adb.setTitle("Add new link");
                LayoutInflater inflater = LayoutInflater.from(getContext());
                View layout = inflater.inflate(R.layout.dialog_new_link, null);
                adb.setView(layout);

                final TextInputEditText name, url, desc, expiryDate;
                name = layout.findViewById(R.id.tiet_dnl_name);
                url = layout.findViewById(R.id.tiet_dnl_url);
                desc = layout.findViewById(R.id.tiet_dnl_desc);
                expiryDate = layout.findViewById(R.id.tiet_dnl_expiry_date);

                adb.setPositiveButton("Add", null);
                adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                final AlertDialog dialog = adb.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ((TextInputLayout) name.getParentForAccessibility()).setError("");
                                ((TextInputLayout) name.getParentForAccessibility()).setError("");
                                if (name.getText().toString().isEmpty()) {
                                    ((TextInputLayout) name.getParentForAccessibility()).setError("Name required.");
                                } else if (url.getText().toString().isEmpty()) {
                                    ((TextInputLayout) name.getParentForAccessibility()).setError("URL required.");
                                } else if (
                                        !expiryDate.getText().toString().isEmpty() &&
                                                !(expiryDate.getText().toString().matches("[0-3][0-9]/[0-1][0-9]/[0-2][0-1][0-9][0-9]") &&
                                                        Integer.parseInt(expiryDate.getText().toString().substring(0, 2)) <= 31 &&
                                                        Integer.parseInt(expiryDate.getText().toString().substring(3, 5)) <= 12 &&
                                                        Integer.parseInt(expiryDate.getText().toString().substring(6, 10)) >= 2018)
                                        ) {
                                    ((TextInputLayout) name.getParentForAccessibility()).setError("Invalid expiry date. ");
                                } else {
                                    ActiveLink activeLink = new ActiveLink();
                                    activeLink.setName(name.getText().toString());
                                    activeLink.setUrl(url.getText().toString());
                                    activeLink.setDesc(desc.getText().toString());
                                    activeLink.setExpirable(!expiryDate.getText().toString().isEmpty());
                                    if (!expiryDate.getText().toString().isEmpty()) {
                                        activeLink.setExpiryDate(expiryDate.getText().toString());
                                    } else {
                                        activeLink.setExpiryDate("16/06/3996");
                                    }
                                    activeLink.setAuthorName(Uv.currUser.getName());
                                    activeLink.setAuthorUsername(Uv.currUser.getUsername());
                                    Calendar calendar = Calendar.getInstance();
                                    String date = calendar.get(Calendar.DAY_OF_MONTH) + "/"
                                            + calendar.get(Calendar.MONTH + 1) + "/"
                                            + calendar.get(Calendar.YEAR);
                                    activeLink.setCreatedOn(date);

                                    DatabaseReference ref = activeLinks.push();
                                    String key = ref.getKey();
                                    activeLink.setId(key);
                                    ref.setValue(activeLink);

                                    Toast.makeText(getActivity(), "Added", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public void onActiveLinkClickListener(View view, int position) {
        homeWrapper.setVisibility(View.GONE);
        webView.loadUrl(activeLinkList.get(position).getUrl());
        urlBar.setText(webView.getUrl());
    }

    @Override
    public boolean onActiveLinkLongClickListener(View view, final int position) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
        adb.setTitle(activeLinkList.get(position).getName());
        adb.setMessage(activeLinkList.get(position).getDesc()
                + "\n\n-" + activeLinkList.get(position).getAuthorName()
                + " on " + activeLinkList.get(position).getCreatedOn()
        );
        adb.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        if (position != 0)
            adb.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    final ActiveLink activeLink = activeLinkList.get(position);
                    activeLinks.child(activeLink.getId()).removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(getActivity(), "Deleted Link : " + activeLink.getName(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        adb.show();
        return true;
    }

    private class JavascriptInterface {
        Context context;

        JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(getActivity(), toast, Toast.LENGTH_SHORT).show();
        }

        @android.webkit.JavascriptInterface
        public ErrorObject getErrorObject() {
            return errorObject;
        }

        @android.webkit.JavascriptInterface
        public int getErrorCode() {
            return errorObject.errorCode;
        }

        @android.webkit.JavascriptInterface
        public String getErrorUrl() {
            return errorObject.errorUrl;
        }

        @android.webkit.JavascriptInterface
        public String getErrorDesc() {
            return errorObject.errorDesc;
        }

        @android.webkit.JavascriptInterface
        public void loadUrl(String url) {
            webView.loadUrl(url);
        }
    }

    private class ErrorObject {
        public int errorCode;
        public String errorUrl;
        public String errorDesc;
        public boolean isError;

        public ErrorObject() {
        }

        public ErrorObject(int errorCode, String errorUrl, String errorDesc) {
            this.errorCode = errorCode;
            this.errorDesc = errorDesc;
            this.errorUrl = errorUrl;
            this.isError = true;
        }
    }
}