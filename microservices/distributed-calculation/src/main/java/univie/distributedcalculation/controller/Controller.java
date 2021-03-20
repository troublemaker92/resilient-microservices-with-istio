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

    private void registerMe() { }

    public String add(CalculationObject calculationObject) {
        BigDecimal result = null;
        try {
            result = calculationService.add(calculationObject.getDecimalOne(), calculationObject.getDecimalTwo());
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return String.valueOf(result);
    }




}
