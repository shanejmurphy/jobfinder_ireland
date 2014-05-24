package soapWebService;

import java.util.Hashtable;
import java.util.Vector;
import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

public class JobCategoryIds extends Vector<Integer> implements KvmSerializable {
	private static final long serialVersionUID = -1166006770093411055L;
	
    @Override
    public Object getProperty(int arg0) {
            return this.get(arg0);
    }

    @Override
    public int getPropertyCount() {
            return 1;
    }

    @Override
    public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
            arg2.name = "int";
            arg2.type = PropertyInfo.INTEGER_CLASS;
    }

    @Override
    public void setProperty(int arg0, Object arg1) {
            this.add((Integer) arg1);
    }
}
