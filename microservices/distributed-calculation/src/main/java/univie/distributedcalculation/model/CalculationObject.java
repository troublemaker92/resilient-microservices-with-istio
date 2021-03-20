package univie.distributedcalculation.model;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CalculationObject {


    @NotNull
    private BigDecimal decimalOne;

    @NotNull
    private BigDecimal decimalTwo;


}
