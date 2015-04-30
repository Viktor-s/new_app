package me.justup.upme.view.cwvm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import me.justup.upme.R;

import static me.justup.upme.view.cwvm.CWVUtils.getScreenDensity;
import static me.justup.upme.view.cwvm.CWVUtils.isSingleColumnLayoutSupported;
import static me.justup.upme.view.cwvm.CWVUtils.isSupportZoomControlButton;

/**
 * CWVM Web View
 */
public class CWView extends WebView {

    // CWView Setting
    private boolean VAR_ZOOM = true;
    private boolean VAR_ZOOM_BUTTON = false;
    private boolean VAR_JScript = true;
    private boolean VAR_BLOCK_LOAD_IMG = false;
    private boolean VAR_TEXT_STYLE = false;
    private int VAR_TEXT_SIZE = 0;
    private int VAR_MAX_SCALE = 2;

    public CWView(Context context) {
        super(context);
    }

    public CWView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Get the attributes specified in attrs.xml using the name we included
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CWView, 0, 0);

        try {
            // Get the text and colors specified using the names in attrs.xml
            VAR_ZOOM = a.getBoolean(R.styleable.CWView_Zoom, true);
            VAR_ZOOM_BUTTON = a.getBoolean(R.styleable.CWView_ZoomButton, false);
            VAR_JScript = a.getBoolean(R.styleable.CWView_JavaScript, true);
            VAR_BLOCK_LOAD_IMG = a.getBoolean(R.styleable.CWView_BlockLoadImg, false);

            VAR_TEXT_STYLE = a.getInteger(R.styleable.CWView_TextStyle, 0) != 0;

            VAR_TEXT_SIZE = a.getInteger(R.styleable.CWView_MailTextSize, 0);
            VAR_MAX_SCALE = a.getInteger(R.styleable.CWView_MaximumScale, 2);
        } finally {
            a.recycle();
        }
    }

    /**
     * Init CWVM View
     */
    @SuppressLint("SetJavaScriptEnabled")
    public void init(Context context){

        this.setVerticalScrollBarEnabled(true);
        this.setVerticalScrollbarOverlay(true);
        this.setScrollBarStyle(SCROLLBARS_INSIDE_OVERLAY);
        this.setLongClickable(true);
        this.setClickable(true);

        if (Build.VERSION.SDK_INT >= 9) this.setOverScrollMode(OVER_SCROLL_NEVER);

        // Get setting from View and set from attr
        final WebSettings webSettings = this.getSettings();

        if (getSettings() == null) {
            return;
        }

        // Set Zoom + and -
        webSettings.setBuiltInZoomControls(VAR_ZOOM);

        if(isSupportZoomControlButton(context)){
            webSettings.setDisplayZoomControls(VAR_ZOOM_BUTTON);
        }else {
            webSettings.setDisplayZoomControls(false);
        }

        webSettings.setSupportZoom(VAR_ZOOM);
        // Enable Javascript
        webSettings.setJavaScriptEnabled(VAR_JScript);
        // Block network DATA / Images.
        webSettings.setBlockNetworkLoads(false);
        webSettings.setBlockNetworkImage(VAR_BLOCK_LOAD_IMG);
        if(VAR_BLOCK_LOAD_IMG){
            webSettings.setLoadsImagesAutomatically(false);
        }else{
            webSettings.setLoadsImagesAutomatically(true);
        }

        // Set View Port to MSG and Text HTML Site
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        if (isSingleColumnLayoutSupported()) {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        } else {
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        }

        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);

        // Set text Size JAVA or CSS
        if (Build.VERSION.SDK_INT >= 14 && VAR_TEXT_SIZE!=0){
            webSettings.setTextZoom(VAR_TEXT_SIZE);
        }else {
            webSettings.setTextSize(getScreenDensity(context));
        }

        // Set WebView Client
        // Disable the ability to click links in the quoted HTML page. I think this is a nice feature, but if someone
        // feels this should be a preference (or should go away all together), I'm ok with that too. -achen 20101130
        setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(Uri.parse(url).getHost().length() == 0) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                view.getContext().startActivity(intent);
                return true;
            }
        });

    }

    public void setText(String text){
        if(text.isEmpty() || text.equals("")){
            text = EMPTY_TEXT;
        }

        // Viewport HTML Param
        String viewport = "<meta name=\"viewport\"\n" +
                "      content=\"\n" +
                "          minimum-scale = 0.7 ,\n" +
                "          maximum-scale = " + VAR_MAX_SCALE + ",\n" +
                "          width=device-width ,\n"+
                "          target-density=device-px ,\n"+
                "          \" />";

        viewport += CWVUtils.getCSSStylePreferences(VAR_TEXT_STYLE, VAR_TEXT_SIZE);
        viewport += "</head><body>" + text + "</body></html>";

        // Workaround for transformer devices
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            loadData(viewport, "text/html; charset=UTF-8", "utf-8");
            resumeTimers();
        } else {
            loadDataWithBaseURL("http://", viewport, "text/html", "utf-8", null);
            resumeTimers();
        }

    }

    public boolean isVAR_ZOOM() {
        return VAR_ZOOM;
    }

    /**
     * On/Off Zoom
     * @param VAR_ZOOM
     */
    public void setVAR_ZOOM(boolean VAR_ZOOM) {
        this.VAR_ZOOM = VAR_ZOOM;
    }

    public boolean isVAR_ZOOM_BUTTON() {
        return VAR_ZOOM_BUTTON;
    }

    /**
     * On/Off Zoom Button
     * @param VAR_ZOOM_BUTTON
     */
    public void setVAR_ZOOM_BUTTON(boolean VAR_ZOOM_BUTTON) {
        this.VAR_ZOOM_BUTTON = VAR_ZOOM_BUTTON;
    }

    public boolean isVAR_JScript() {
        return VAR_JScript;
    }

    /**
     * On/Off Java Script
     * @param VAR_JScript
     */
    public void setVAR_JScript(boolean VAR_JScript) {
        this.VAR_JScript = VAR_JScript;
    }

    public boolean isVAR_BLOCK_LOAD_IMG() {
        return VAR_BLOCK_LOAD_IMG;
    }

    /**
     * Block All Img in MAIL
     * @param VAR_BLOCK_LOAD_IMG
     */
    public void setVAR_BLOCK_LOAD_IMG(boolean VAR_BLOCK_LOAD_IMG) {
        this.VAR_BLOCK_LOAD_IMG = VAR_BLOCK_LOAD_IMG;
    }

    public boolean isVAR_TEXT_STYLE() {
        return VAR_TEXT_STYLE;
    }

    /**
     * Set Text Style (true) monospace / (false) sansSerif
     * @param VAR_TEXT_STYLE
     */
    public void setVAR_TEXT_STYLE(boolean VAR_TEXT_STYLE) {
        this.VAR_TEXT_STYLE = VAR_TEXT_STYLE;
    }

    public int getVAR_TEXT_SIZE() {
        return VAR_TEXT_SIZE;
    }

    /**
     * Set Text Size in MAIL
     * @param VAR_TEXT_SIZE
     */
    public void setVAR_TEXT_SIZE(int VAR_TEXT_SIZE) {
        this.VAR_TEXT_SIZE = VAR_TEXT_SIZE;
    }

    public int getVAR_MAX_SCALE() {
        return VAR_MAX_SCALE;
    }

    /**
     * Set MAX View Scale. Recommended 2 max 4
     * @param VAR_MAX_SCALE
     */
    public void setVAR_MAX_SCALE(int VAR_MAX_SCALE) {
        this.VAR_MAX_SCALE = VAR_MAX_SCALE;
    }

    private static final String EMPTY_TEXT = "<p>\n" +
            "\t<link href=\"http://test.justup.me/static/def/index.css\" rel=\"stylesheet\"></p>\n" +
            "<div class=\"news-one\" style=\"height:700px;\">\n" +
            "\t<div class=\"wrap\">\n" +
            "\t\t<div class=\"large\">\n" +
            "\t\t\t<div data-src=\"/uploads/news/large-20.jpg\" id=\"large-can\">&nbsp;</div>\n" +
            "\t\t\t<div class=\"t-wrap\">\n" +
            "\t\t\t\t<div class=\"title b up\">А ТУТ ПУСТО (</div>\n" +
            "\t\t\t</div>\n" +
            "\t\t</div>";
}
