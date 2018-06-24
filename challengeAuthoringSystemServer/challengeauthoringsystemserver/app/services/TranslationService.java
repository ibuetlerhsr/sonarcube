package services;

import interfaces.repositories.IAttributeTranslationRepository;
import interfaces.repositories.ICodeTypeRepository;
import interfaces.repositories.ILanguageDefinitionRepository;
import interfaces.repositories.ITranslatableAttributeRepository;
import models.DataTransferObject.ImportedChallengeDtos.TranslatedChallengeDto;
import models.DatabaseObject.translation.AttributeTranslation;
import models.DatabaseObject.translation.CodeType;
import models.DatabaseObject.translation.LanguageDefinition;
import models.DatabaseObject.translation.TranslatableAttribute;
import play.Logger;
import utils.ControllerUtil;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class TranslationService {

    private final String defaultLanguageIsoCode = "en";
    private Map<String, String> languageMap;
    private final ILanguageDefinitionRepository languageDefinitionRepository;
    private final ITranslatableAttributeRepository translatableAttributeRepository;
    private final IAttributeTranslationRepository attributeTranslationRepository;
    private final ICodeTypeRepository codeTypeRepository;

    @Inject
    private TranslationService(ILanguageDefinitionRepository languageDefinitionRepository,
                               ITranslatableAttributeRepository translatableAttributeRepository,
                               IAttributeTranslationRepository attributeTranslationRepository,
                               ICodeTypeRepository codeTypeRepository) {
        this.languageDefinitionRepository = languageDefinitionRepository;
        this.translatableAttributeRepository = translatableAttributeRepository;
        this.attributeTranslationRepository = attributeTranslationRepository;
        this.codeTypeRepository = codeTypeRepository;
    }

    public void intializeTranslationDbValues(){
        initializeLanguageMapEnglish();
        initializeCodeTypes();
        initializeLanguageDefinition();

        initializeLanguageMapGerman();
        addGermanTranslationToLanguages();
    }

    private void createCodeType(String codeName) {
        CodeType codeType = new CodeType();
        codeType.setCodeName(codeName);
        this.insertCodeType(codeType);
    }

    private List<CodeType> getCodeTypes() {
        return this.codeTypeRepository.find().query().findList();
    }

    private void initializeCodeTypes(){
        if (this.codeTypeRepository.find().query().findCount() == 0) {
            for(String value : ControllerUtil.readMetadataFile("CodeType.txt")) {
                createCodeType(value);
            }
        }
    }

    private void createLanguageDefinition(String isoCode) {
        LanguageDefinition languageDefinition = new LanguageDefinition();
        languageDefinition.setIsoLangCode(isoCode);
        this.insertLanguageDefintion(languageDefinition);
    }
    private void initializeLanguageDefinition(){
        if (this.languageDefinitionRepository.find().query().findCount() == 0) {
            for(String value : ControllerUtil.readMetadataFile("LanguageDefinition.txt")) {
                createLanguageDefinition(value);
            }

            addTranslationToLanguages();
        }
    }

    private void waitOnDB(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            Logger.error(e.getMessage());
        }
    }

    private void addTranslationToLanguages(){
        if(this.languageDefinitionRepository.find().query().findCount() != 7) {
            waitOnDB(5);
        }

        for (LanguageDefinition languageDefinition : this.languageDefinitionRepository.find().query().findList()) {
            languageDefinition.setLanguageName(createDefaultTranslation(languageMap.get(languageDefinition.getIsoLangCode()), "Language", true));
            this.updateLanguageDefinition(languageDefinition);
        }
    }

    private void addGermanTranslationToLanguages(){
        if(this.languageDefinitionRepository.find().query().findCount() != 7)
            waitOnDB(5);
        for (LanguageDefinition languageDefinition : this.languageDefinitionRepository.find().query().findList()) {
            handleAttributeTranslation(languageDefinition.getLanguageName(), languageMap.get(languageDefinition.getIsoLangCode()), "de", true);
        }
    }

    private void initializeLanguageMapEnglish(){
        languageMap = new HashMap<String, String>();
        languageMap.put("en", "English");
        languageMap.put("de", "German");
        languageMap.put("fr", "French");
        languageMap.put("it", "Italian");
        languageMap.put("es", "Spanish");
        languageMap.put("zh", "Chinese");
        languageMap.put("ru", "Russian");
        languageMap.put("pl", "Polish");
        languageMap.put("nl", "Dutch");
    }

    private void initializeLanguageMapGerman(){
        languageMap = new HashMap<String, String>();
        languageMap.put("en", "Englisch");
        languageMap.put("de", "Deutsch");
        languageMap.put("fr", "Französisch");
        languageMap.put("it", "Italienisch");
        languageMap.put("es", "Spanisch");
        languageMap.put("zh", "Chinesisch");
        languageMap.put("ru", "Russisch");
        languageMap.put("pl", "Polnisch");
        languageMap.put("nl", "Holländisch");
    }

    public String getDefaultTranslation(Long id){
        Long languageId = this.languageDefinitionRepository.find().query().where().eq("isoLangCode", defaultLanguageIsoCode).findUnique().getId();
        return this.attributeTranslationRepository.find().
                query().
                where().
                eq("attributeId", id).
                eq("languageId", languageId).
                findUnique().
                getTranslation();
    }

    public Long getAttributeIdByDefaultTranslation(String text){
        Long languageId = this.languageDefinitionRepository.find().query().where().eq("isoLangCode", defaultLanguageIsoCode).findUnique().getId();
        return this.attributeTranslationRepository.find().
                query().
                where().
                eq("translation", text).
                eq("languageId", languageId).
                findUnique().getId();
    }

    public TranslatableAttribute getDefaultTranslation(String value){
        AttributeTranslation translation = this.attributeTranslationRepository.find().query().where().eq("translation", value).findUnique();
        if(translation == null)
            return null;
        return this.attributeTranslationRepository.find().query().where().eq("translation", value).findUnique().getAttribute();
    }

    public TranslatableAttribute getTranslationPerLanguage(String value, String isoLangCode, String codeType) {
        LanguageDefinition languageDefinition = getLanguageDefinitionByIsoCode(isoLangCode);
        CodeType codeTypeObject = getCodeType(codeType);
        AttributeTranslation translation = this.attributeTranslationRepository.find().query().where().in("attributeid", getTranslatableAttributeIdsByCodeType(codeTypeObject)).eq("languageid", languageDefinition.getId()).eq("translation", value).findUnique();
        if(translation == null)
            return null;
        return translation.getAttribute();
    }

    public TranslatableAttribute getTranslationPerLanguageByAttributeId(Long id, String isoLangCode, String codeType) {
        LanguageDefinition languageDefinition = getLanguageDefinitionByIsoCode(isoLangCode);
        CodeType codeTypeObject = getCodeType(codeType);
        AttributeTranslation translation = this.attributeTranslationRepository.find().query().where().eq("attributeid", id).eq("languageid", languageDefinition.getId()).findUnique();
        if(translation == null)
            return null;
        return translation.getAttribute();
    }

    public TranslatableAttribute handleTranslationByCodeType(String value, String codeType, boolean isUserCreated) {
        TranslatableAttribute attribute = this.getDefaultTranslation(value);
        if(attribute != null){
            return attribute;
        }
        return this.createDefaultTranslation(value, codeType, isUserCreated);
    }

    public TranslatableAttribute handleTitleTranslation(Long attributeId, String value, boolean isUserCreated) {
        if(attributeId != null && attributeId != 0L) {
            TranslatableAttribute attribute = this.getTranslatableAttributeById(attributeId);
            handleAttributeTranslation(attribute, value, "en", isUserCreated);
            return attribute;

        } else {
            TranslatableAttribute attribute = this.getDefaultTranslation(value);
            if(attribute != null){
                return attribute;
            }
        }

        return this.createDefaultTranslation(value, "Title", isUserCreated);
    }

    public ArrayList<String> getAvailableIsoCodes() {
        ArrayList<String> availableIsoCodes = new ArrayList<>();
        List<LanguageDefinition> languageDefinitions = this.languageDefinitionRepository.find().query().findList();
        for(LanguageDefinition languageDefinition : languageDefinitions) {
            availableIsoCodes.add(languageDefinition.getIsoLangCode());
        }

        return availableIsoCodes;
    }

    public String getTranslationByAttributeId(Long attributeId, String isoCode){
        LanguageDefinition language = this.languageDefinitionRepository.find().query().where().eq("isoLangCode", isoCode).findUnique();
        AttributeTranslation attributeTranslation = this.attributeTranslationRepository.find().query().where().eq("attributeId", attributeId).eq("languageId", language.getId()).findUnique();
        if(attributeTranslation != null)
            return attributeTranslation.getTranslation();
        return null;
    }

    public boolean isTranslationUserCreated(Long attributeId, String isoCode){
        LanguageDefinition language = this.languageDefinitionRepository.find().query().where().eq("isoLangCode", isoCode).findUnique();
        AttributeTranslation attributeTranslation = this.attributeTranslationRepository.find().query().where().eq("attributeId", attributeId).eq("languageId", language.getId()).findUnique();
        if(attributeTranslation != null)
            return attributeTranslation.isUserCreated();
        return false;
    }

    public void setTranslationByAttributeId(Long attributeId, String isoCode, String text, String codeType, boolean isUserCreated){
        LanguageDefinition language = this.languageDefinitionRepository.find().query().where().eq("isoLangCode", isoCode).findUnique();
        AttributeTranslation attributeTranslation = this.attributeTranslationRepository.find().query().where().eq("attributeId", attributeId).eq("languageId", language.getId()).findUnique();
        if(attributeTranslation != null){
            /*if(attributeTranslation.isUserCreated() && !isUserCreated)
                return;*/
            attributeTranslation.setTranslation(text);
            attributeTranslation.setUserCreated(isUserCreated);
            this.attributeTranslationRepository.update(attributeTranslation.getId(), attributeTranslation);
        } else {
            createTranslation(attributeId, text, codeType, isoCode, isUserCreated);
        }
    }

    public AttributeTranslation getAttributeTranslationByAttributeId(Long attributeId, String isoCode){
        LanguageDefinition language = this.languageDefinitionRepository.find().query().where().eq("isoLangCode", isoCode).findUnique();
        AttributeTranslation attributeTranslation = this.attributeTranslationRepository.find().query().where().eq("attributeId", attributeId).eq("languageId", language.getId()).findUnique();
        if(attributeTranslation != null)
            return attributeTranslation;
        return null;
    }

    public TranslatableAttribute createDefaultTranslation(String text, String codeType, boolean isUserCreated){
        return createTranslation(text, codeType, defaultLanguageIsoCode, isUserCreated);
    }

    void removeAttributeTranslationByAttributeId(Long attributeId) {
        for(AttributeTranslation translation : this.attributeTranslationRepository.find().query().where().eq("attributeId", attributeId).findList()) {
            deleteAttributeTranslation(translation.getId());
        }
        this.translatableAttributeRepository.delete(attributeId);
    }

    Long createTranslation(Long translatableAttributeId, String value, String codeType, String actualIsoCode, boolean isUserCreated){
        if(value == null || codeType == null || actualIsoCode == null)
            return null;
        Long attributeId = 0L;
        if(translatableAttributeId == 0L) {
            TranslatableAttribute translatableAttribute = new TranslatableAttribute();
            translatableAttribute.setCodeType(getCodeType(codeType));
            attributeId = this.insertTranslatableAttribute(translatableAttribute);;
        } else {
            attributeId = translatableAttributeId;
        }

        TranslatableAttribute attribute = getTranslatableAttributeById(attributeId);
        handleAttributeTranslation(attribute, value, actualIsoCode, isUserCreated);
        return attribute.getId();
    }

    public TranslatableAttribute createTranslation(String value, String codeType, String actualIsoCode, boolean isUserCreated){
        if(value == null || codeType == null || actualIsoCode == null)
            return null;
        TranslatableAttribute translatableAttribute = new TranslatableAttribute();
        translatableAttribute.setCodeType(getCodeType(codeType));
        Long attributeId = this.insertTranslatableAttribute(translatableAttribute);;
        TranslatableAttribute attribute = getTranslatableAttributeById(attributeId);
        handleAttributeTranslation(attribute, value, actualIsoCode, isUserCreated);
        return attribute;
    }

    private void handleAttributeTranslation(TranslatableAttribute attribute, String value, String actualIsoCode, boolean isUserCreated){
        if(value == null || actualIsoCode == null)
            return;
        AttributeTranslation translation = getAttributeTranslationByAttributeId(attribute.getId(), actualIsoCode);
        if(translation != null) {
            translation.setTranslation(value);
            translation.setUserCreated(isUserCreated);
            this.updateAttributeTranslation(translation);
        } else {
            translation = new AttributeTranslation();
            translation.setAttribute(attribute);
            translation.setLanguage(getLanguageDefinitionByIsoCode(actualIsoCode));
            translation.setUserCreated(isUserCreated);
            translation.setTranslation(value);
            this.insertAttributeTranslation(translation);
        }
    }

    private void insertCodeType(CodeType codeType) {
        CompletionStage<Long> id = this.codeTypeRepository.insert(codeType);
        ControllerUtil.handleCompletionStageLong(id);
    }

    private CodeType getCodeType(String codeType) {
        return this.codeTypeRepository.find().query().where().eq("codeName", codeType).findUnique();
    }

    private List<Long> getTranslatableAttributeIdsByCodeType(CodeType codeType) {
        List<Long> ids = new ArrayList<>();
        List<TranslatableAttribute> translatableAttributes = this.translatableAttributeRepository.find().query().where().eq("codetypeid", codeType.getId()).findList();
        if(translatableAttributes == null)
            return null;
        for(TranslatableAttribute translatableAttribute : translatableAttributes) {
            ids.add(translatableAttribute.getId());
        }

        return ids;
    }

    private LanguageDefinition getLanguageDefinitionByIsoCode(String actualIsoCode) {
        return this.languageDefinitionRepository.find().query().where().eq("isoLangCode", actualIsoCode).findUnique();
    }

    public TranslatableAttribute getTranslatableAttributeById(Long attributeId) {
        return this.translatableAttributeRepository.find().query().setId(attributeId).findUnique();
    }

    private void insertLanguageDefintion(LanguageDefinition languageDefinition) {
        CompletionStage<Long> id = this.languageDefinitionRepository.insert(languageDefinition);
        ControllerUtil.handleCompletionStageLong(id);
    }

    private void updateLanguageDefinition(LanguageDefinition languageDefinition) {
        CompletionStage<Optional<Long>> id = this.languageDefinitionRepository.update(languageDefinition.getId(), languageDefinition);
        ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    private Long insertTranslatableAttribute(TranslatableAttribute translatableAttribute) {
        CompletionStage<Long> id = this.translatableAttributeRepository.insert(translatableAttribute);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    private void insertAttributeTranslation(AttributeTranslation attributeTranslation) {
        CompletionStage<Long> id = this.attributeTranslationRepository.insert(attributeTranslation);
        ControllerUtil.handleCompletionStageLong(id);
    }

    private void updateAttributeTranslation(AttributeTranslation attributeTranslation) {
        CompletionStage<Optional<Long>> id = this.attributeTranslationRepository.update(attributeTranslation.getId(), attributeTranslation);
        ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long deleteTranslatableAttribute(Long id) {
        TranslatableAttribute translatableAttribute = this.translatableAttributeRepository.find().query().where().eq("id", id).findUnique();
        if(translatableAttribute != null) {
            deleteAttributeTranslation(translatableAttribute.getId());
            CompletionStage<Optional<Long>> deletedId = this.translatableAttributeRepository.delete(id);
            return ControllerUtil.handleCompletionStageOptionalLong(deletedId);
        }
        return 0L;
    }

    private void deleteAttributeTranslation(Long id) {
        CompletionStage<Optional<Long>> deletedId = this.attributeTranslationRepository.delete(id);
        ControllerUtil.handleCompletionStageOptionalLong(deletedId);
    }
}
