package cz.pfservis.hosys.enums;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.StrBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import cz.pfservis.hosys.HosysHtmlProcesor;

import static cz.pfservis.hosys.HosysConfig.UTF8;

/**
 * Created by petr on 8.10.16.
 */

public class HosysPageHelper {
    public static String getPostData(HosysPage hosysPage, String soutezKey, Integer dnyZpet, Integer dnyDopredu) {
        Validate.notNull(hosysPage);
        Object[] params = null;
        String soutezKeyEnc = null;

        try {
            soutezKeyEnc = URLEncoder.encode(soutezKey, UTF8);
        } catch (UnsupportedEncodingException e) {
            // Toto by nemělo nastat!
            throw new RuntimeException("Nepodařilo se zakódovat URL parametr do formátu " + UTF8, e);
        }

        switch (hosysPage) {
            case soutez:
            case tabulky:
                params = new Object[]{soutezKeyEnc};
                break;
            case rozpis:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -dnyZpet);
                Date datumOd = cal.getTime();
                cal.add(Calendar.DAY_OF_YEAR, +dnyZpet + dnyDopredu);
                Date datumDo = cal.getTime();

                params = new Object[]{soutezKeyEnc, datumOd, datumDo};
                break;
            default:
                throw new IllegalStateException();
        }

        return MessageFormat.format(hosysPage.getPostDataFormat(), params);
    }

    public static String buildWebViewPage(HosysHtmlProcesor hosysHtmlProcesor) {
        Validate.notNull(hosysHtmlProcesor);

        HosysPage hosysPage = hosysHtmlProcesor.getHosysPage();
        StrBuilder sb = new StrBuilder(StringUtils.trimToEmpty(hosysHtmlProcesor.getHtml()));

        deleteTemplate(hosysPage, sb);
        htmlSoroundPage(hosysHtmlProcesor.getCss(), sb);

        return sb.toString();
    }

    private static void htmlSoroundPage(String cssText , StrBuilder sb) {
        // pozpátku, aby se nemusela počítat pozice vložení :-)
        sb.insert(0, "</style></head><body>");
        sb.insert(0, cssText);
        sb.insert(0, "<!DOCTYPE html><html><head><meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=yes\" /><style>");

        // konec
        sb.append("</body></html>");
    }

    private static void deleteTemplate(HosysPage hosysPage, StrBuilder sb) {
        sb.deleteFirst("{~{~{~Start~}~}~}");
        sb.deleteFirst("{~{~{~Stop~}~}~}");

        switch (hosysPage) {
            case soutez:
                sb.deleteFirst("XY_Souteze{~{~{~Item~}~}~}");
                break;
            case tabulky:
                sb.deleteFirst("XY_Tabulky{~{~{~Item~}~}~}");
                break;
            case rozpis:
                sb.deleteFirst("XY_Rozpis{~{~{~Item~}~}~}");
                break;
            default:
                throw new IllegalStateException();
        }
    }
}
