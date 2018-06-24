package binders;

import play.mvc.QueryStringBindable;
import utils.ControllerUtil;

import java.util.Map;
import java.util.Optional;

public class SectionQueryString implements QueryStringBindable<SectionQueryString> {

    public String challengeId;
    public String sectionId = "";

    @Override
    public Optional<SectionQueryString> bind(String key, Map<String, String[]> data) {
        try{
            challengeId = data.get("challengeId")[0];
            String[] sectionArray = data.get("sectionId");
            if(sectionArray != null)
                sectionId = sectionArray[0];
            return Optional.of(this);
        } catch (Exception e){ // no parameter match return None
            return Optional.empty();
        }
    }

    @Override
    public String unbind(String key) {
        return key +".challengeId=" + challengeId;
    }

    @Override
    public String javascriptUnbind() {
        return "function(k,v) {\n" +
                "    return encodeURIComponent(k+'.challengeId')+'='+v.challengeId'&'+encodeURIComponent(k+'.languageCode')+'='+v.lanugageCode;\n" +
                "}";
    }
}
