package hosys.pfservis.cz.hosys;

import java.util.List;
import java.util.Map;

/**
 * Created by petr on 5.10.16.
 */
public class HosysHtmlProcesor {
    private final HosysPage hosysPage;
    private String html;
    private int responseCode;
    private Exception exception;

    public HosysHtmlProcesor(HosysPage hosysPage) {
        this.hosysPage = hosysPage;
    }

    public String getHtml() {
        return html;
    }

    public void setHtmlData(String html) {
        this.html = html;
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
