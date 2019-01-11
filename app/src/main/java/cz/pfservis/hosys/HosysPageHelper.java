package cz.pfservis.hosys;

import android.text.TextUtils;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.text.TextStringBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import cz.pfservis.hosys.HosysHtmlProcesor;
import cz.pfservis.hosys.dto.RozpisDto;
import cz.pfservis.hosys.enums.HosysPage;

import static cz.pfservis.hosys.HosysConfig.UTF8;

/**
 * Created by petr on 8.10.16.
 */
public class HosysPageHelper {
    public static String buildWebViewPage(HosysHtmlProcesor hosysHtmlProcesor) {
        Validate.notNull(hosysHtmlProcesor);

        HosysPage hosysPage = hosysHtmlProcesor.getHosysPage();
        TextStringBuilder sb = new TextStringBuilder(StringUtils.trimToEmpty(hosysHtmlProcesor.getHtml()));

        htmlSoroundPage(hosysHtmlProcesor.getCss(), sb);

        return sb.toString();
    }

    private static void htmlSoroundPage(String cssText , TextStringBuilder sb) {
        // pozpátku, aby se nemusela počítat pozice vložení :-)
        sb.insert(0, "</style></head><body>");
        sb.insert(0, cssText);
        sb.insert(0, "<!DOCTYPE html><html><head><meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1, user-scalable=yes\" /><style>");

        // konec
        sb.append("</body></html>");
    }

    public static String createRozpisHtmlTable(RozpisDto jsonData[]) {
        if (ArrayUtils.isEmpty(jsonData)) {
            return "<table class=\"cTableData\"><tbody><tr class=\"cTrPrazdneTop\"><td class=\"cTdPrazdne\" colspan=\"10\">&nbsp;</td></tr><tr class=\"cTrPrazdneMiddle\"><td class=\"cTdPrazdne\" colspan=\"10\">Výběru nevyhovují žádná data.</td></tr><tr class=\"cTrPrazdneBottom\"><td class=\"cTdPrazdne\" colspan=\"10\">&nbsp;</td></tr></tbody></table>";
        }

        TextStringBuilder sb = new TextStringBuilder("<table class=\"cTableData\"> <tbody> <tr class=\"cTrUtkani\"> <td class=\"cTdTi cTdTiDen\">Den</td> <td class=\"cTdTi cTdTiDatumRO\">Datum</td> <td class=\"cTdTi cTdTiCasRO\">Čas</td> <td class=\"cTdTi cTdTiStadionRO\">ZS</td> <td class=\"cTdTi cTdTiSoutezRO\">Soutěž</td> <td class=\"cTdTi cTdTiCislo\">Utkání</td> <td class=\"cTdTi cTdTiSouperiRO\">Domácí</td> <td class=\"cTdTi cTdTiSouperiRO\">Hosté</td> <td class=\"cTdTi cTdTiOba\">Týmy</td> <td class=\"cTdTi cTdTiStav\">Stav</td> </tr>");

        for (RozpisDto json : jsonData) {
            sb.append("<tr class=\"cTr").append(json.getStatusRow()).append("\">");
            sb.append("<td class=\"cTdTe cTdTeDen\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getDenTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getDen())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeDatum\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getDatumTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getDatum())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeCas\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getCasTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getCas())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeStadion\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getStadionTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getStadion())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeSoutez\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getSoutezTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getSoutez())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeCislo\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getCisloTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getCislo())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeSouperiRO\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getDomaciTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getDomaci())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeSouperiRO\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getHosteTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getHoste())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeOba\"><span title=\"")
                    .append(TextUtils.htmlEncode(json.getDomaciZkrTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getDomaciZkr())).append("</span>-<span title=\"")
                    .append(TextUtils.htmlEncode(json.getHosteZkrTitle())).append("\">")
                    .append(TextUtils.htmlEncode(json.getHosteZkr())).append("</span></td>");
            sb.append("<td class=\"cTdTe cTdTeStav\"><span class=\"kImportant\" title=\"")
                    .append(TextUtils.htmlEncode(json.getStatus())).append("\">")
                    .append(TextUtils.htmlEncode(json.getStatus())).append("</span></td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table>");

        return sb.toString();
    }
}
