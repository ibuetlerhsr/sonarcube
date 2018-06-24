package binders;

import play.mvc.QueryStringBindable;

import java.util.Map;
import java.util.Optional;

public class MediaQueryString implements QueryStringBindable<MediaQueryString> {

    public String challengeId;
    public String languageIsoCode = "en";
    public String markdownType = null;
    public String markdownId = null;
    public String mediaId = null;

    @Override
    public Optional<MediaQueryString> bind(String key, Map<String, String[]> data) {
        try{
            String[] challengeIdArray = data.get("challengeId");
            if(challengeIdArray != null)
                challengeId = challengeIdArray[0];
            String[] languageCodeArray = data.get("languageCode");
            if(languageCodeArray != null)
                languageIsoCode = languageCodeArray[0];
            String[] markdownIds = data.get("markdownId");
            if(markdownIds != null)
                markdownId = markdownIds[0];
            String[] markdownTypes = data.get("markdownType");
            if(markdownTypes != null)
                markdownType = markdownTypes[0];
            String[] mediaIds = data.get("mediaId");
            if(mediaIds != null)
                mediaId = mediaIds[0];
            return Optional.of(this);
        } catch (Exception e){ // no parameter match return None
            return Optional.empty();
        }
    }

    @Override
    public String unbind(String key) {
        return key +".challengeId=" + challengeId + "&" + key + ".languageCode=" + languageIsoCode;
    }

    @Override
    public String javascriptUnbind() {
        return "function(k,v) {\n" +
                "    return encodeURIComponent(k+'.challengeId')+'='+v.challengeId'&'+encodeURIComponent(k+'.languageCode')+'='+v.lanugageCode;\n" +
                "}";
    }
}
