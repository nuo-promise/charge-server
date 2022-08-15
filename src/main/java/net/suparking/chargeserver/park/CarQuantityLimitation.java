package net.suparking.chargeserver.park;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;
import org.bson.types.ObjectId;

@ParamNotNull
public class CarQuantityLimitation extends FieldValidator {
    public ObjectId carTypeId;
    public Integer maxAllowedQuantity;
    public Integer quantity;

    public CarQuantityLimitation() {}

    public CarQuantityLimitation(CarQuantityLimitation cql) {
        this.carTypeId = cql.carTypeId;
        this.maxAllowedQuantity = cql.maxAllowedQuantity;
        this.quantity = cql.quantity;
    }

    @Override
    public String toString() {
        return "CarQuantityLimitation{" + "carTypeId='" + carTypeId + '\'' + ", maxAllowedQuantity=" +
               maxAllowedQuantity + ", quantity=" + quantity + '}';
    }
}
