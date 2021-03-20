package univie.distributedcalculation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import univie.distributedcalculation.model.CalculationObject;
import univie.distributedcalculation.service.CalculationService;

import java.math.BigDecimal;

@Service
public class Controller {

    @Autowired
    private CalculationService calculationService;

    public String add(CalculationObject calculationObject) {
        BigDecimal result = calculationService.add(calculationObject.getDecimalOne(), calculationObject.getDecimalTwo());
        sleep(10000);
        return String.valueOf(result);
    }

    public String multiply(CalculationObject calculationObject) {
        BigDecimal result = calculationService.multiply(calculationObject.getDecimalOne(),
                calculationObject.getDecimalTwo());
        sleep(10000);
        return String.valueOf(result);
    }



    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
