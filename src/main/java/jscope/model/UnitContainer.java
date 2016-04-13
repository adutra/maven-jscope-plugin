package jscope.model;

import java.util.List;



public interface UnitContainer extends Unit {

    public List<Unit> getChildUnits();

    public void addUnit(final Unit unit);

}
