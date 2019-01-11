package cz.pfservis.hosys.enums;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Primitives;
import com.google.gson.stream.JsonReader;

import java.io.Reader;

import cz.pfservis.hosys.HosysConfig;
import cz.pfservis.hosys.dto.HtmlDataDto;
import cz.pfservis.hosys.dto.RozpisDto;
import cz.pfservis.hosys.dto.SoutezDto;

/**
 * Created by petr on 8.10.16.
 */
public enum HosysPage {
    rozpis(HosysConfig.API_URL_ROZPIS) {
        @Override
        public RozpisDto[] fromJson(Reader json) throws JsonSyntaxException, JsonIOException {
            Gson gson = new Gson();
            return (RozpisDto[]) gson.fromJson(json, RozpisDto[].class);
        }
    },
    tabulky(HosysConfig.API_URL_HTML_TABULKA) {
        @Override
        public HtmlDataDto fromJson(Reader json) throws JsonSyntaxException, JsonIOException {
            Gson gson = new Gson();
            return (HtmlDataDto) gson.fromJson(json, HtmlDataDto.class);
        }
    },
    soutez(HosysConfig.API_URL_HTML_SOUTEZ) {
        @Override
        public HtmlDataDto fromJson(Reader json) throws JsonSyntaxException, JsonIOException {
            Gson gson = new Gson();
            return (HtmlDataDto) gson.fromJson(json, HtmlDataDto.class);
        }
    },
    soutezNastaveni(HosysConfig.API_URL_SOUTEZ) {
        @Override
        public SoutezDto[] fromJson(Reader json) throws JsonSyntaxException, JsonIOException {
            Gson gson = new Gson();
            return (SoutezDto[]) gson.fromJson(json, SoutezDto[].class);
        }
    };

    public static HosysPage[] FRAGMENTS = new HosysPage[] {rozpis, tabulky, soutez};
    private final String page;
    private final String cssUrl;

    private HosysPage(String page) {
        this.page = page;
        this.cssUrl = HosysConfig.SERVER_CSS + this.toString() + ".css";
    }

    public String getPage() {
        return page;
    }

    public String getCssUrl() {
        return cssUrl;
    }

    abstract public <T> T fromJson(Reader json) throws JsonSyntaxException, JsonIOException;
}
