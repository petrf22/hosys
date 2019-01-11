package cz.pfservis.hosys;

import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import java.io.Reader;

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
    private Object jsonObject;

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

    public void setJsonObject(Object jsonObject) {
        this.jsonObject = jsonObject;
    }

    public <T> T getJson() {
        return (T) jsonObject;
    }
}
