package cz.pfservis.hosys.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class HtmlDataDto {
    private String hosysSoutezId;
    private String html;
}
