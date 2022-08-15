package net.suparking.chargeserver.project;

import net.suparking.chargeserver.exception.FieldValidator;
import net.suparking.chargeserver.exception.ParamNotNull;

@ParamNotNull
public class MatchStrategy extends FieldValidator {
    public Boolean useSubAreaStrictMatch;
    public Boolean useRecogMatch;
    public Boolean useDupMatch;
    public SubAreaStrategy subAreaStrategy;

    @Override
    public String toString() {
        return "MatchStrategy{" + "useSubAreaStrictMatch=" + useSubAreaStrictMatch + ", useRecogMatch=" +
               useRecogMatch + ", useDupMatch=" + useDupMatch + ", subAreaStrategy=" + subAreaStrategy + '}';
    }
}
