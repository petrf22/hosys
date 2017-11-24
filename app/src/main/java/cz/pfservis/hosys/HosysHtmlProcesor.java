package cz.pfservis.hosys;

import cz.pfservis.hosys.enums.HosysPage;

/**
 * Created by petr on 5.10.16.
 */
public class HosysHtmlProcesor {
    private final HosysPage hosysPage;
    private String html;
    private String css;
    private int responseCode;
    private Exception exception;

    public HosysHtmlProcesor(HosysPage hosysPage) {
        this.hosysPage = hosysPage;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public String getCss() {
        return css;
    }

    public void setCss(String css) {
        this.css = css;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    public HosysPage getHosysPage() {
        return hosysPage;
    }
}
