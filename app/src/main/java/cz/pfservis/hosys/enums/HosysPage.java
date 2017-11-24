package cz.pfservis.hosys.enums;

import cz.pfservis.hosys.HosysConfig;

/**
 * Created by petr on 8.10.16.
 */
public enum HosysPage {
    rozpis("Rozpis.htm", "My_Xxx_Reserved_Form=Tygrik-Ajax" +
            "&My_Face=Nooks" +
            "&My_IDs=XY_Rozpis" +
            "&My_Cislo=" +
            "&My_FiltrSoutez={0}" +
            "&My_FiltrDatumOd={1,date," + HosysConfig.POST_DATE_FORMAT + "}" +
            "&My_FiltrDatumDo={2,date," + HosysConfig.POST_DATE_FORMAT + "}" +
            "&My_FiltrDelegujeLed=~" +
            "&My_FiltrUzemi=~" +
            "&My_FiltrDelegujeFce=~" +
            "&My_FiltrPratelske=A" +
            "&My_Varianta=STR" +
            "&My_FiltrRidi=~" +
            "&My_FiltrDatum=DOD" +
            "&My_FiltrStadion=~" +
            "&My_FiltrCislo=~" +
            "&My_Soutez=" +
            "&My_PIX=0" +
            "&My_PortHeight=444" +
            "&My_FiltrMinihokej=A" +
            "&My_Razeni=DUC" +
            "&My_FiltrSouperi=~" +
            "&My_FiltrOba=OBA" +
            "&My_FiltrStav=VSE" +
            "&My_FiltrRequest=Filtrovat"),
    tabulky("Tabulky.htm", "My_Xxx_Reserved_Form=Tygrik-Ajax" +
            "&My_Face=Nooks" +
            "&My_IDs=XY_Tabulky" +
            "&My_PortHeight=369" +
            "&My_FiltrSoutez={0}" +
            "&My_FiltrRequest=Filtrovat"),
    soutez("Souteze.htm", "My_Xxx_Reserved_Form=Tygrik-Ajax" +
            "&My_Face=Nooks" +
            "&My_IDs=XY_Souteze" +
            "&My_PortHeight=369" +
            "&My_FiltrSoutez={0}" +
            "&My_FiltrRequest=Filtrovat");

    private final String page;
    private final String postDataFormat;
    private final String cssUrl;

    private HosysPage(String page, String postDataFormat) {
        this.page = page;
        this.postDataFormat = postDataFormat;
        this.cssUrl = "https://hosys.pfservis.cz/css/" + this.toString() + ".css";
    }

    public String getPage() {
        return page;
    }

    public String getPostDataFormat() {
        return postDataFormat;
    }

    public String getCssUrl() {
        return cssUrl;
    }
}
