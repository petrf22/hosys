package hosys.pfservis.cz.hosys;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.text.StrBuilder;
import org.apache.commons.lang3.text.StrMatcher;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import static android.R.attr.format;
import static hosys.pfservis.cz.hosys.HosysPage.soutez;

/**
 * Created by petr on 8.10.16.
 */

public class HosysPageHelper {
    public static String getPostData(HosysPage hosysPage) {
        Validate.notNull(hosysPage);
        String soutez = "KVK%20LMD%20skZJ";
        Object[] params = null;

        switch (hosysPage) {
            case soutez:
            case tabulky:
                params = new Object[]{soutez};
                break;
            case rozpis:
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -7);
                Date datumOd = cal.getTime();
                cal.add(Calendar.DAY_OF_YEAR, +7 + 14);
                Date datumDo = cal.getTime();

                params = new Object[]{soutez, datumOd, datumDo};
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
        htmlSoroundPage(hosysPage, sb);

        return sb.toString();
    }

    private static void htmlSoroundPage(HosysPage hosysPage, StrBuilder sb) {
        // pozpátku, aby se nemusela počítat pozice vložení :-)
        sb.insert(0, "</style></head><body>");
        sb.insert(0, getPageCss(hosysPage));
        sb.insert(0, "<!DOCTYPE html><html><head><meta charset=\"UTF-8\"><style>");

        // konec
        sb.append("</body></html>");
    }

    private static String getPageCss(HosysPage hosysPage) {
        switch (hosysPage) {
            case soutez:
                return "body {\n" +
                        "    margin: 0px;\n" +
                        "}\n" +
                        ".cDivBar {\n" +
                        "    display: none;\n" +
                        "}\n" +
                        "form table.cTableData tr:nth-child(1),\n" +
                        "form table.cTableData tr:nth-child(2),\n" +
                        "form table.cTableData tr:nth-child(3),\n" +
                        "form table.cTableData tr:nth-child(4),\n" +
                        "form table.cTableData tr:nth-child(5),\n" +
                        "form table.cTableData tr:nth-child(6),\n" +
                        "form table.cTableData tr:nth-child(7),\n" +
                        "form table.cTableData tr:nth-child(8),\n" +
                        "form table.cTableData tr:nth-child(9) {\n" +
                        "   display: none;\n" +
                        "}\n" +
                        "TABLE {\n" +
                                "    font-family: open-sans, Tahoma, Verdana;\n" +
                                "    xfont-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;\n" +
                                "    font-size: 10pt;\n" +
                                "    font-weight: normal;\n" +
                                "    text-shadow: 0 -1px 0 rgba(0,0,0,0.15);\n" +
                                "}        \n" +
                        ".cTdSoutezeLabel,\n" +
                        ".cTdSoutezeValueKolo {\n" +
                        "    font-weight: bold;\n" +
                        "}\n" +
                        ".cTdSoutezeValueSmall {\n" +
                        "    white-space: nowrap;\n" +
                        "}\n" +
                        ".cTdSoutezeLabelSmall {\n" +
                        "    display: none;\n" +
                        "}\n" +
                        "tr:nth-child(even) {background: #eeeeee}\n" +
                        "tr:nth-child(odd) {background: #dddddd}";
            case tabulky:
                return "body {\n" +
                        "    margin: 0px;\n" +
                        "}\n" +
                        ".cDivBar {\n" +
                        "    display: none;\n" +
                        "}\n" +
                        "form table.cTableData tr:nth-child(1),\n" +
                        "form table.cTableData tr:nth-child(2),\n" +
                        "form table.cTableData tr:nth-child(3),\n" +
                        "form table.cTableData tr:nth-child(4),\n" +
                        "form table.cTableData tr:nth-child(5),\n" +
                        "form table.cTableData tr:nth-child(6),\n" +
                        "form table.cTableData tr:nth-child(7),\n" +
                        "form table.cTableData tr:nth-child(8) {\n" +
                        "   display: none;\n" +
                        "}\n" +
                        "TABLE {\n" +
                        "  font-family: open-sans, Tahoma, Verdana;\n" +
                        "  xfont-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;\n" +
                        "  font-size: 10pt;\n" +
                        "  font-weight: normal;\n" +
                        "  text-shadow: 0 -1px 0 rgba(0, 0, 0, 0.15);\n" +
                        "}\n" +
                        "tr.cTrOdd {\n" +
                        "  background-color: #dddddd;\n" +
                        "}\n" +
                        "tr.cTrEven {\n" +
                        "  background-color: #eeeeee;\n" +
                        "}\n" +
                        ".cTdNumberCifra,\n" +
                        ".cTdNumberSkorePlus,\n" +
                        ".cTdTextSkoreMinus,\n" +
                        ".cTdNumberBody,\n" +
                        ".cTdNumberUtkani {\n" +
                        "  text-align: right;\n" +
                        "}\n" +
                        ".cTdTextZkratka {\n" +
                        "  display: none;\n" +
                        "}\n";
            case rozpis:
                return "body {\n" +
                        "    margin: 0px;\n" +
                        "}\n" +
                        ".cDivBar,\n" +
                        ".cTdTextStadion,\n" +
                        ".cTdTextSoutez,\n" +
                        ".cTdTextCislo,\n" +
                        ".cTdTextOba,\n" +
                        "table.cTableData tr.cTrUtkani,\n" +
                        "table.cTableData tr.cTrUtkani + tr.cTrCara {\n" +
                        "    display: none;\n" +
                        "}\n" +
                        "table.cTableData tr.cTrUtkani {\n" +
                        "    display: none;\n" +
                        "}\n" +
                        "TABLE {\n" +
                        "    font-family: open-sans, Tahoma, Verdana;\n" +
                        "    xfont-family: \"Helvetica Neue\", Helvetica, Arial, sans-serif;\n" +
                        "    font-size: 10pt;\n" +
                        "    font-weight: normal;\n" +
                        "    text-shadow: 0 -1px 0 rgba(0,0,0,0.15);\n" +
                        "}        \n" +
                        "tr.cTrCara, td.cTrCara {\n" +
                        "    font-size: 2px;\n" +
                        "    padding: 0px;\n" +
                        "}\n" +
                        ".cTrRozlosovane {\n" +
                        "    background-color: #bfcbca;\n" +
                        "}\n" +
                        ".cTrOdehrane {\n" +
                        "    background-color: #89e987;\n" +
                        "}\n" +
                        ".cTrRozlosovane {\n" +
                        "    background-color: #bfcbca;\n" +
                        "}\n" +
                        ".cTrNahlasene {\n" +
                        "    background-color: #76bdea;\n" +
                        "}\n" +
                        ".cTrK_rozhodnuti {\n" +
                        "    background-color: #fcdc7c;\n" +
                        "}\n" +
                        ".cSpanZmena {\n" +
                        "    color: #0000ff;\n" +
                        "}\n";
            default:
                throw new IllegalStateException();
        }

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
                sb.replaceAll("<td class=\"cTdTitleCifra\">Vp</td><td class=\"cTdTitleCifra\"></td><td class=\"cTdTitleCifra\">Pp</td>",
                              "<td class=\"cTdTitleCifra\">Vp</td><td class=\"cTdTitleCifra\">Pp</td>");
                break;
            case rozpis:
                sb.deleteFirst("XY_Rozpis{~{~{~Item~}~}~}");
                break;
            default:
                throw new IllegalStateException();
        }
    }

}
