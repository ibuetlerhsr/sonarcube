package binders;

import play.mvc.QueryStringBindable;

import java.util.Map;
import java.util.Optional;

public class FilterQueryString implements QueryStringBindable<FilterQueryString> {

    public String fromDate = null;
    public String toDate = null;

    @Override
    public Optional<FilterQueryString> bind(String key, Map<String, String[]> data) {
        try{
            String[] fromDates = data.get("fromDate");
            if(fromDates != null)
                fromDate = fromDates[0];
            String[] toDates = data.get("toDate");
            if(toDates != null)
                toDate = toDates[0];
            return Optional.of(this);
        } catch (Exception e){ // no parameter match return None
            return Optional.empty();
        }
    }

    @Override
    public String unbind(String key) {
        return key +".fromDate=" + fromDate + "&" + key + ".toDate=" + toDate;
    }

    @Override
    public String javascriptUnbind() {
        return "function(k,v) {\n" +
                "    return encodeURIComponent(k+'.fromDate')+'='+v.fromDate'&'+encodeURIComponent(k+'.toDate')+'='+v.toDate;\n" +
                "}";
    }
}
