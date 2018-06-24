package binders;

import play.mvc.QueryStringBindable;

import java.util.Map;
import java.util.Optional;

public class MarkdownItemQueryString implements QueryStringBindable<MarkdownItemQueryString> {

    public String challengeId;
    public String abstractId = "";
    public String solutionId = "";

    @Override
    public Optional<MarkdownItemQueryString> bind(String key, Map<String, String[]> data) {
        try{
            challengeId = data.get("challengeId")[0];
            String[] solutionArray = data.get("solutionId");
            if(solutionArray != null)
                solutionId = solutionArray[0];
            String[] abstractArray = data.get("abstractId");
            if(abstractArray != null)
                abstractId = abstractArray[0];
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
