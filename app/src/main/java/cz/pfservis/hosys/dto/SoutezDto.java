package cz.pfservis.hosys.dto;

import lombok.Data;
import lombok.Builder;
import lombok.ToString;

@Data
@Builder
@ToString
public class SoutezDto {
    private String hosysSoutezId;
    private int uroven;
    private String nazev;
    private int poradi;
}
