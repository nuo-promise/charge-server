package net.suparking.chargeserver.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {

    private BigDecimal longitude;

    private BigDecimal latitude;
}
