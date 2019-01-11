package cz.pfservis.hosys.dto;

import java.util.Date;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class RozpisDto {
    private String hosysRozpisId;
    private String hosysSoutezId;
    private Date datumOd;
    private Date datumDo;
    private String statusRow;
    private String denTitle;
    private String den;
    private String datumTitle;
    private String datum;
    private String casTitle;
    private String cas;
    private String stadionTitle;
    private String stadion;
    private String soutezTitle;
    private String soutez;
    private String cisloTitle;
    private String cislo;
    private String domaciTitle;
    private String domaci;
    private String domaciZkrTitle;
    private String domaciZkr;
    private String hoste;
    private String hosteTitle;
    private String hosteZkrTitle;
    private String hosteZkr;
    private String status;
    private boolean zmena;
    private String vlozeno;
}
