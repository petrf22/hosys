package cz.pfservis.hosys;

import java.util.Locale;

/**
 * Created by petr on 8.10.16.
 */

public class HosysConfig {
    public static final String UTF8 = "UTF-8";
    public static final String SERVER = "https://hosys.pfservis.cz/";
    public static final String SERVER_API = SERVER + "api/";
    public static final String SERVER_CSS = SERVER + "css/";
    public static final String API_URL_SOUTEZ = SERVER_API + "soutez";
    public static final String API_URL_HTML_SOUTEZ = API_URL_SOUTEZ + "/%s/info";
    public static final String API_URL_HTML_TABULKA = API_URL_SOUTEZ + "/%s/tab";
    public static final String API_URL_ROZPIS = SERVER_API + "rozpis";
    public static final Locale CZECH = new Locale("cs", "CZ");
}
