package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mysql.cj.xdevapi.JsonArray;
import interfaces.repositories.*;
import models.ChallengeParameter;
import models.DataTransferObject.ChallengeDto;
import models.DataTransferObject.ImportedChallengeDtos.TranslatedChallengeDto;
import models.DataTransferObject.ImportedChallengeDtos.TranslatedSectionDto;
import models.DataTransferObject.ImportedChallengeDtos.TranslatedStepDto;
import models.DatabaseObject.challenge.*;
import models.DatabaseObject.translation.AttributeTranslation;
import models.DatabaseObject.translation.TranslatableAttribute;
import play.Logger;
import play.libs.Json;
import utils.ControllerUtil;
import utils.TimestampUtil;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.CompletionStage;

public class ChallengeService {

    private final IChallengeRepository challengeRepository;
    private final IChallengeVersionRepository challengeVersionRepository;
    private final IInstructionRepository instructionRepository;
    private final ISectionRepository sectionRepository;
    private final IHintRepository hintRepository;
    private final IAbstractRepository abstractRepository;
    private final ISolutionRepository solutionRepository;
    private final IRatingRepository ratingRepository;
    private final IChallengeLevelRepository challengeLevelRepository;
    private final IChallengeTypeRepository challengeTypeRepository;
    private final IKeywordRepository keywordRepository;
    private final IChallengeResourceRepository challengeResourceRepository;
    private final ICategoryRepository categoryRepository;
    private final IChallengeUsageRepository challengeUsageRepository;

    private final TranslationService translationService;
    private final MediaService mediaService;

    @Inject
    public ChallengeService(IChallengeRepository challengeRepository,
                            IChallengeVersionRepository challengeVersionRepository,
                            IInstructionRepository instructionRepository,
                            ISectionRepository sectionRepository,
                            IHintRepository hintRepository,
                            IAbstractRepository abstractRepository,
                            ISolutionRepository solutionRepository,
                            IRatingRepository ratingRepository,
                            IChallengeLevelRepository challengeLevelRepository,
                            IChallengeTypeRepository challengeTypeRepository,
                            IChallengeUsageRepository challengeUsageRepository,
                            ICategoryRepository categoryRepository,
                            IKeywordRepository keywordRepository,
                            IChallengeResourceRepository challengeResourceRepository,
                            TranslationService translationService,
                            MediaService mediaService){
        this.challengeRepository = challengeRepository;
        this.challengeVersionRepository = challengeVersionRepository;
        this.instructionRepository = instructionRepository;
        this.sectionRepository = sectionRepository;
        this.hintRepository = hintRepository;
        this.abstractRepository = abstractRepository;
        this.solutionRepository = solutionRepository;
        this.ratingRepository = ratingRepository;
        this.challengeLevelRepository = challengeLevelRepository;
        this.challengeTypeRepository = challengeTypeRepository;
        this.categoryRepository = categoryRepository;
        this.keywordRepository = keywordRepository;
        this.challengeResourceRepository = challengeResourceRepository;
        this.challengeUsageRepository = challengeUsageRepository;
        this.translationService = translationService;
        this.mediaService = mediaService;
    }

    private Category findCategoryInChallenge(String challengeId, Long categoryId) {
        Challenge savedChallenge = this.getChallengeById(challengeId);
        if(savedChallenge == null)
            return null;
        for (Category category : savedChallenge.getCategories()) {
            if(category.getId().equals(categoryId))
                return category;
        }

        return null;
    }

    private ChallengeUsage findUsageInChallenge(String challengeId, Long usageId) {
        Challenge savedChallenge = this.getChallengeById(challengeId);
        if(savedChallenge == null)
            return null;
        for (ChallengeUsage challengeUsage : savedChallenge.getUsages()) {
            if(challengeUsage.getId().equals(usageId))
                return challengeUsage;
        }

        return null;
    }

    private Keyword findKeywordInChallenge(String challengeId, Long keywordId) {
        Challenge savedChallenge = this.getChallengeById(challengeId);
        if(savedChallenge == null)
            return null;
        for (Keyword keyword : savedChallenge.getKeywords()) {
            if(keyword.getId().equals(keywordId))
                return keyword;
        }

        return null;
    }

    private boolean isCategoryAlreadyAttachedToChallenge(Challenge challenge, Category category) {
        return findCategoryInChallenge(challenge.getId(), category.getId()) != null;
    }

    private boolean isUsageAlreadyAttachedToChallenge(Challenge challenge, ChallengeUsage challengeUsage) {
        return findUsageInChallenge(challenge.getId(), challengeUsage.getId()) != null;
    }

    private boolean isKeywordAlreadyAttachedToChallenge(Challenge challenge, Keyword keyword) {
        return findKeywordInChallenge(challenge.getId(), keyword.getId()) != null;
    }

    public Long createKeyword(String keywordText) {
        Keyword keyword = new Keyword();
        keyword.setText(keywordText);
        return this.insertKeyword(keyword);
    }

    public String createChallengeResource(String resourceId, String challengeResourceType) {
        ChallengeResource challengeResource = new ChallengeResource();
        challengeResource.setId(resourceId);
        challengeResource.setType(challengeResourceType);
        return this.insertChallengeResource(challengeResource);
    }

    private void initializeKeyWords() {
        if (this.keywordRepository.find().query().findCount() == 0) {
            for(String value : ControllerUtil.readMetadataFile("Keyword.txt")) {
                createKeyword(value);
            }
        }
    }

    public Long createUsage(String usageText) {
        ChallengeUsage usage = new ChallengeUsage();
        usage.setText(translationService.createDefaultTranslation(usageText, "ChallengeUsage", true));
        return this.insertUsage(usage);
    }

    private void initializeUsages() {
        if (this.challengeUsageRepository.find().query().findCount() == 0) {
            for(String value : ControllerUtil.readMetadataFile("Usage.txt")) {
                createUsage(value);
            }
        }
    }

    private void createChallengeType(String text) {
        ChallengeType newChallengeType = new ChallengeType();
        newChallengeType.setText(translationService.createDefaultTranslation(text, "Type", true));
        this.insertChallengeType(newChallengeType);
    }

    private void createChallengeLevel(String text, int maxPoints) {
        ChallengeLevel newLevel = new ChallengeLevel();
        newLevel.setText(translationService.createDefaultTranslation(text, "Level", true));
        newLevel.setMaxPoint(maxPoints);
        this.insertChallengeLevel(newLevel);
    }

    public Long createCategory(String categoryName) {
        Category category = new Category();
        category.setCategoryName(translationService.createDefaultTranslation(categoryName, "Category", true));
        return this.insertCategory(category);
    }

    private void initializeChallengeLevel(){
        if (this.challengeLevelRepository.find().query().findCount() == 0) {
            int points = 10;
            for(String value : ControllerUtil.readMetadataFile("Level.txt")) {
                createChallengeLevel(value, points);
                points += 10;
            }
        }
    }

    private void initializeTypes() {
        if(this.challengeTypeRepository.find().query().findCount() == 0) {
            for(String value : ControllerUtil.readMetadataFile("Type.txt")) {
                createChallengeType(value);
            }
        }
    }

    private void initializeCategory() {
        if(this.categoryRepository.find().query().findCount() == 0) {
            for(String value : ControllerUtil.readMetadataFile("Category.txt")) {
                createCategory(value);
            }
        }
    }



    public void intializeChallengeDbValues(){
        initializeChallengeLevel();
        initializeTypes();
        initializeKeyWords();
        initializeCategory();
        initializeUsages();
    }

    public void connectChallengeWithMedia(String challengeId, Long mediaId) {
        ChallengeVersion savedChallengeVersion = this.getChallengeVersionByChallengeId(challengeId);
        if(savedChallengeVersion == null) {
            Challenge newChallenge = createChallengeById(challengeId);
            savedChallengeVersion = this.getChallengeVersionByChallengeId(newChallenge.getId());
        }
        savedChallengeVersion.setMediaReference(this.mediaService.getMediaReferenceById(mediaId));
        this.updateChallengeVersion(savedChallengeVersion.getId(), savedChallengeVersion);
    }

    public void connectMarkdownWithMedia(String markdownType, String markdownId, Long mediaId) {
        switch(markdownType) {
            case "abstract":
                Abstract anAbstract = getAbstractById(Long.parseLong(markdownId));
                anAbstract.addMarkdownMediaReference(this.mediaService.getSpecificMarkdownMediaReference(markdownType, markdownId, mediaId));
                this.updateAbstract(anAbstract.getId(), anAbstract);
                break;
            case "solution":
                Solution solution = getSolutionById(Long.parseLong(markdownId));
                solution.addMarkdownMediaReference(this.mediaService.getSpecificMarkdownMediaReference(markdownType, markdownId, mediaId));
                this.updateSolution(solution.getId(), solution);
                break;
            case "hint":
                Hint hint = getHintById(markdownId);
                hint.addMarkdownMediaReference(this.mediaService.getSpecificMarkdownMediaReference(markdownType, markdownId, mediaId));
                this.updateHint(hint.getId(), hint);
                break;
            case "instruction":
                Instruction instruction = getInstructionById(markdownId);
                instruction.addMarkdownMediaReference(this.mediaService.getSpecificMarkdownMediaReference(markdownType, markdownId, mediaId));
                this.updateInstruction(instruction.getId(), instruction);
                break;
            case "section":
                Section section = getSectionById(markdownId);
                section.addMarkdownMediaReference(this.mediaService.getSpecificMarkdownMediaReference(markdownType, markdownId, mediaId));
                this.updateSection(section.getId(), section);
                break;
        }
    }

    private void addOrModifySection(String sectionId, Section section) {
        if(sectionId != null && !sectionId.equals("")){
            this.updateSection(sectionId, section);
        } else {
            this.insertSection(section);
        }
    }

    private void addOrModifyInstruction(String instructionId, Instruction instruction) {
        if(instructionId != null && !instructionId.equals("")){
            this.updateInstruction(instructionId, instruction);
        } else {
            this.insertInstruction(instruction);
        }
    }

    private void addOrModifyHint(String hintId, Hint hint) {
        if(hintId != null && !hintId.equals("")){
            this.updateHint(hintId, hint);
        } else {
            this.insertHint(hint);
        }
    }

    public Section initializeSectionWithChallengeVersion(ChallengeVersion challengeVersion, Section section) {
        if(section == null){
            section = new Section();
            section.setChallengeVersion(challengeVersion);
        } else {
            section.setChallengeVersion(challengeVersion);
        }

        return section;
    }

    public Abstract createNewAbstractOnDb() {
        Abstract anAbstract = new Abstract();
        Long id = insertAbstract(anAbstract);
        return this.getAbstractById(id);
    }

    public Solution createNewSolutionOnDb() {
        Solution solution = new Solution();
        Long id = insertSolution(solution);
        return this.getSolutionById(id);
    }

    public Section initializeNewSectionOnDb(ChallengeVersion challengeVersion, int order) {
        String sectionUUID = UUID.randomUUID().toString();
        while(this.getSectionById(sectionUUID) != null) {
            sectionUUID = UUID.randomUUID().toString();
        }
        Section section = new Section();
        section.setId(sectionUUID);
        section.setChallengeVersion(challengeVersion);
        section.setOrder(order);
        String id = insertSection(section);
        return this.getSectionById(id);
    }

    private Hint initializeNewHintOnDb(Section section, String hintId, int order) {
        Hint hint = new Hint();
        hint.setId(hintId);
        hint.setOrder(order);
        hint.setSection(section);
        String id = insertHint(hint);
        return this.getHintById(id);
    }

    private Instruction initializeNewInstructionOnDb(Section section, String instructionId, int order) {
        Instruction instruction = new Instruction();
        instruction.setId(instructionId);
        instruction.setOrder(order);
        instruction.setSection(section);
        String id = insertInstruction(instruction);
        return this.getInstructionById(id);
    }

    public Section initializeSectionForChallengeOnDb(String challengeId) {
        if(challengeId == null || challengeId.equals(""))
            return null;
        Challenge challenge = this.getChallengeById(challengeId);
        if(challenge == null)
            return null;
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        Section newSection = this.initializeNewSectionOnDb(challengeVersion, challengeVersion.getSections().size());
        challengeVersion.addSection(newSection);
        this.updateChallengeVersion(challengeVersion.getId(), challengeVersion);
        return newSection;
    }

    public Long initializeNewSolutionOnDb(String challengeId) {
        Challenge challenge = this.getChallengeById(challengeId);
        if(challenge == null)
            return null;
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        Solution solution = createNewSolutionOnDb();
        challengeVersion.setSolution(solution);
        this.updateChallengeVersion(challengeVersion.getId(), challengeVersion);
        return solution.getId();
    }

    public Long initializeNewAbstractOnDb(String challengeId) {
        Challenge challenge = this.getChallengeById(challengeId);
        if(challenge == null)
            return null;
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        Abstract anAbstract = createNewAbstractOnDb();
        challengeVersion.setAnAbstract(anAbstract);
        this.updateChallengeVersion(challengeVersion.getId(), challengeVersion);
        return anAbstract.getId();
    }

    public Hint initializeHintForSectionOnDb(String sectionId, String hintId) {
        if(sectionId == null || sectionId.equals(""))
            return null;
        Section section = this.getSectionById(sectionId);
        if(section == null)
            return null;
        Hint hint = this.initializeNewHintOnDb(section, hintId, section.getHints().size());
        section.addHint(hint);
        this.updateSection(sectionId, section);
        return hint;
    }

    public Instruction initializeInstructionForSectionOnDb(String sectionId, String instructionId) {
        if(sectionId == null || sectionId.equals(""))
            return null;
        Section section = this.getSectionById(sectionId);
        if(section == null)
            return null;
        Instruction instruction = this.initializeNewInstructionOnDb(section, instructionId, section.getInstructions().size());
        section.addInstruction(instruction);
        this.updateSection(sectionId, section);
        return instruction;
    }

    private ChallengeVersion initializeChallengeVersion(String challengeId) {
        ChallengeVersion challengeVersion = this.getChallengeVersionByChallengeId(challengeId);
        if(challengeVersion == null){
            challengeVersion = new ChallengeVersion();
            this.insertChallengeVersion(challengeVersion);
        }

        return challengeVersion;
    }

    public void removeMarkdownById(String markdownId) {
        Hint hint = this.getHintById(markdownId);
        if(hint != null) {
            hintRepository.delete(hint.getId());
            return;
        }
        Instruction instruction = this.getInstructionById(markdownId);
        if(instruction != null) {
            instructionRepository.delete(instruction.getId());
            return;
        }
        Section section = this.getSectionById(markdownId);
        if(section != null) {
            sectionRepository.delete(section.getId());
            return;
        }
    }

    public void handleRatingCreationOnDB(String challengeId, Rating rating, String text) {
        ChallengeVersion challengeVersion = initializeChallengeVersion(challengeId);

        if(rating == null){
            rating = new Rating();
            rating.setChallengeVersion(this.getChallengeVersionByChallengeId(challengeId));
        }

        rating.setText(text);

        if(rating.getId() != 0){
            this.updateRating(rating.getId(), rating);
        } else {
            this.insertRating(rating);
        }
    }

    public void handleAbstractCreationOnDB(String challengeId, Abstract anAbstract, String text, boolean isUserCreated) {
        ChallengeVersion challengeVersion = initializeChallengeVersion(challengeId);

        if(anAbstract == null){
            anAbstract = new Abstract();
        }

        TranslatableAttribute translatableAttribute = anAbstract.getText();

        anAbstract.setText(this.translationService.createDefaultTranslation(text, "Abstract", isUserCreated));

        if(anAbstract.getId() != null){
            this.updateAbstract(anAbstract.getId(), anAbstract);
        } else {
            this.insertAbstract(anAbstract);
        }

        challengeVersion.setAnAbstract(anAbstract);

        if(challengeVersion.getId() != null){
            this.updateChallengeVersion(challengeVersion.getId(), challengeVersion);
        } else {
            this.insertChallengeVersion(challengeVersion);
        }

        if(translatableAttribute != null ){
            this.translationService.removeAttributeTranslationByAttributeId(translatableAttribute.getId());
        }
    }

    public void handleSolutionCreationOnDB(String challengeId, Solution solution, String text, boolean isUserCreated) {
        ChallengeVersion challengeVersion = initializeChallengeVersion(challengeId);

        if(solution == null){
            solution = new Solution();
        }

        TranslatableAttribute translatableAttribute = solution.getText();

        solution.setText(this.translationService.createDefaultTranslation(text, "Solution", isUserCreated));

        if(solution.getId() != null){
            this.updateSolution(solution.getId(), solution);
        } else {
            this.insertSolution(solution);
        }

        challengeVersion.setSolution(solution);

        if(challengeVersion.getId() != null){
            this.updateChallengeVersion(challengeVersion.getId(), challengeVersion);
        } else {
            this.insertChallengeVersion(challengeVersion);
        }

        if(translatableAttribute != null ){
            this.translationService.removeAttributeTranslationByAttributeId(translatableAttribute.getId());
        }
    }

    public void handleSectionCreationOnDB(String challengeId, Section section, String text, boolean isUserCreated) {
        String sectionId = "";
        ChallengeVersion challengeVersion = initializeChallengeVersion(challengeId);

        if(section == null){
            section = new Section();
        } else {
            sectionId = section.getId();
        }

        TranslatableAttribute translatableAttribute = section.getText();
        section.setText(this.translationService.createDefaultTranslation(text, "Section", isUserCreated));

        if(translatableAttribute != null ){
            this.translationService.removeAttributeTranslationByAttributeId(translatableAttribute.getId());
        }
        addOrModifySection(sectionId, section);

        initializeSectionWithChallengeVersion(challengeVersion, section);
    }

    public void handleInstructionCreationOnDB(int sectionOrder, String challengeId, Section section, Instruction instruction, String text, boolean isUserCreated) {
        String instructionId = "";
        ChallengeVersion challengeVersion = initializeChallengeVersion(challengeId);

        if(instruction == null){
            instruction = new Instruction();
        } else {
            instructionId = instruction.getId();
        }


        TranslatableAttribute translatableAttribute = instruction.getText();

        instruction.setText(this.translationService.createDefaultTranslation(text, "Instruction", isUserCreated));
        if(section == null) {
            section = initializeNewSectionOnDb(challengeVersion, sectionOrder);
        }

        addOrModifyInstruction(instructionId, instruction);

        if(translatableAttribute != null ){
            this.translationService.removeAttributeTranslationByAttributeId(translatableAttribute.getId());
        }

        addOrModifySection(section.getId(), section);

        initializeSectionWithChallengeVersion(challengeVersion, section);
    }

    public void handleTranslatedChallengeDto(ChallengeVersion challengeVersion, TranslatedChallengeDto translatedChallengeDto) {
        String languageIsoCode = translatedChallengeDto.getLanguageIsoCode();
        if(languageIsoCode == null || languageIsoCode.equals(""))
            return;
        if(translatedChallengeDto.getTranslatedTitle() != null && translatedChallengeDto.getTranslatedTitle().getText() != null && !translatedChallengeDto.getTranslatedTitle().getText().equals("")) {
            if(challengeVersion.getTitle() != null) {
                Long translatableAttributeId = challengeVersion.getTitle().getId();
                this.translationService.setTranslationByAttributeId(translatableAttributeId, languageIsoCode, translatedChallengeDto.getTranslatedTitle().getText(), "Title", translatedChallengeDto.getTranslatedTitle().getIsUserCreated());
            }
        }

        if(translatedChallengeDto.getTranslatedAbstract() != null && translatedChallengeDto.getTranslatedAbstract().getText() != null && !translatedChallengeDto.getTranslatedAbstract().getText().equals("")) {
            if(challengeVersion.getAnAbstract() != null && challengeVersion.getAnAbstract().getText() != null) {
                Long translatableAttributeId = challengeVersion.getAnAbstract().getText().getId();
                this.translationService.setTranslationByAttributeId(translatableAttributeId, languageIsoCode, translatedChallengeDto.getTranslatedAbstract().getText(), "Abstract", translatedChallengeDto.getTranslatedAbstract().getIsUserCreated());
            }
        }

        if(translatedChallengeDto.getTranslatedSolution() != null && translatedChallengeDto.getTranslatedSolution().getText() != null && !translatedChallengeDto.getTranslatedSolution().getText().equals("")) {
            if(challengeVersion.getSolution() != null && challengeVersion.getSolution().getText() != null) {
                Long translatableAttributeId = challengeVersion.getSolution().getText().getId();
                this.translationService.setTranslationByAttributeId(translatableAttributeId, languageIsoCode, translatedChallengeDto.getTranslatedSolution().getText(), "Solution", translatedChallengeDto.getTranslatedSolution().getIsUserCreated());
            }
        }

        if(translatedChallengeDto.getTranslatedSections() != null && translatedChallengeDto.getTranslatedSections().size() > 0) {
            if(challengeVersion.getSections().size() > 0) {

                for(TranslatedSectionDto sectionDto : translatedChallengeDto.getTranslatedSections()) {
                    Section section = challengeVersion.findSectionById(sectionDto.getId());
                    if(section != null) {
                        if(section.getText() != null) {
                            Long translatableAttributeId = section.getText().getId();
                            this.translationService.setTranslationByAttributeId(translatableAttributeId, languageIsoCode, sectionDto.getSectionMD(), "Section", sectionDto.getIsUserCreated());
                        }
                        setTranslationForSteps(section, sectionDto.getSteps(), languageIsoCode);
                    }

                }
            }
        }
    }

    private void setTranslationForSteps(Section section, List<TranslatedStepDto> translatedStepDtos, String languageIsoCode) {
        for(TranslatedStepDto translatedStepDto : translatedStepDtos) {
            if(translatedStepDto.getType().equals("hint")) {
                Hint hint = section.findHintById(translatedStepDto.getId());
                if(hint != null) {
                    Long translatableAttributeId = hint.getText().getId();
                    this.translationService.setTranslationByAttributeId(translatableAttributeId, languageIsoCode, translatedStepDto.getStepMD(), "Hint", translatedStepDto.getIsUserCreated());
                }
            } else if(translatedStepDto.getType().equals("instruction")) {
                Instruction instruction = section.findInstructionById(translatedStepDto.getId());
                if(instruction != null) {
                    Long translatableAttributeId = instruction.getText().getId();
                    this.translationService.setTranslationByAttributeId(translatableAttributeId, languageIsoCode, translatedStepDto.getStepMD(), "Instruction", translatedStepDto.getIsUserCreated());
                }
            }
        }
    }

    public void handleHintCreationOnDB(int sectionOrder, String challengeId, Section section, Hint hint, String text, boolean isUserCreated) {
        String hintId = "";
        ChallengeVersion challengeVersion = initializeChallengeVersion(challengeId);
        if(hint == null){
            hint = new Hint();
        } else {
            hintId = hint.getId();
        }


        TranslatableAttribute translatableAttribute = hint.getText();

        hint.setText(this.translationService.createDefaultTranslation(text, "Hint", isUserCreated));
        if(section == null) {
            section = initializeNewSectionOnDb(challengeVersion, sectionOrder);
        }

        addOrModifyHint(hintId, hint);

        if(translatableAttribute != null ){
            this.translationService.removeAttributeTranslationByAttributeId(translatableAttribute.getId());
        }

        addOrModifySection(section.getId(), section);

        initializeSectionWithChallengeVersion(challengeVersion, section);
    }

    public Challenge handleCategory(Challenge challenge, String challengeCategory){
        Category category;
        if(ControllerUtil.tryParseLong(challengeCategory)) {
            category = this.getCategoryById(Long.parseLong(challengeCategory));
        } else {
            category = this.getCategoryByText(challengeCategory);
        }

        if(category == null) {
            category = new Category();
            category.setCategoryName(this.translationService.handleTranslationByCodeType(challengeCategory, "Category", true));
            Long id = this.insertCategory(category);
            this.addCategoryToChallenge(challenge, this.getCategoryById(id));
        } else {
            this.addCategoryToChallenge(challenge, category);
        }
        return challenge;
    }

    public Challenge handleUsage(Challenge challenge, String challengeUsage){
        ChallengeUsage usage;
        if(ControllerUtil.tryParseLong(challengeUsage)) {
            usage = this.getUsageById(Long.parseLong(challengeUsage));
        } else {
            usage = this.getUsageByText(challengeUsage);
        }

        if(usage == null) {
            usage = new ChallengeUsage();
            usage.setText(this.translationService.handleTranslationByCodeType(challengeUsage, "ChallengeUsage", true));
            Long id = this.insertUsage(usage);
            this.addUsageToChallenge(challenge, this.getUsageById(id));
        } else {
            this.addUsageToChallenge(challenge, usage);
        }
        return challenge;
    }

    public Challenge handleKeyword(Challenge challenge, String keywordValue){
        Keyword keyword;
        if(ControllerUtil.tryParseLong(keywordValue)) {
            keyword = this.getKeywordById(Long.parseLong(keywordValue));
        } else {
            keyword = this.getKeywordByText(keywordValue);
        }

        if(keyword == null) {
            keyword = new Keyword();
            keyword.setText(keywordValue);
            Long id = this.insertKeyword(keyword);
            this.addKeywordsToChallenge(challenge, this.getKeywordById(id));
        } else {
            this.addKeywordsToChallenge(challenge, keyword);
        }
        return challenge;
    }

    public String getChallengeLevelTranslationByText(String challengeLevel){
        Long levelAttributeId = this.getChallengeLevelByText(challengeLevel).getText().getId();
        return translationService.getDefaultTranslation(levelAttributeId);
    }

    public Map<String, String> getValuesFromChallenge(String challengeId) {
        Map<String, String> valueMap = null;
        boolean isValidChallenge = this.getChallengeById(challengeId) != null;

        if(isValidChallenge){
            valueMap = new HashMap<>();
            ChallengeVersion challengeVersion = this.getChallengeVersionByChallengeId(challengeId);
            Challenge challengeDbObject = this.getChallengeById(challengeId);
            if(challengeDbObject.getType() != null){
                valueMap.put("type", translationService.getDefaultTranslation(challengeDbObject.getType().getId()));
            }
            if(challengeVersion.getChallengeLevel() != null) {
                valueMap.put("level", translationService.getDefaultTranslation(challengeVersion.getChallengeLevel().getText().getId()));
            }
            if(challengeVersion.getName() != null){
                valueMap.put("name", challengeVersion.getName());
            }
            if(challengeVersion.getTitle() != null){
                valueMap.put("title", translationService.getDefaultTranslation(challengeVersion.getTitle().getId()));
            }
        }

        return valueMap;
    }

    public void insertToLocalDB(ChallengeDto newChallenge, ChallengeParameter challengeParameter){
        Challenge createChallenge = new Challenge();
        if(challengeParameter.isPrivate() != null && !challengeParameter.isPrivate().isEmpty()) {
            createChallenge.setPrivate(challengeParameter.isPrivate().equals("true"));
        }
        ChallengeVersion challengeVersion = new ChallengeVersion();
        createChallenge.setId(newChallenge.get_id());
        //createChallenge.setMandant();
        if(newChallenge.getChallengeName() != null && !newChallenge.getChallengeName().isEmpty()){
            challengeVersion.setName(newChallenge.getChallengeName());
        }
        if(newChallenge.getChallengeLevel() != null && !newChallenge.getChallengeLevel().isEmpty()){
            if(challengeVersion.getChallengeLevel() != null && challengeVersion.getChallengeLevel().getText().getId().equals(
                    this.getChallengeLevelByText(newChallenge.getChallengeLevel()).getText().getId())) {
                challengeVersion.setChallengeLevel(null);
            } else {
                challengeVersion.setChallengeLevel(this.getChallengeLevelByText(newChallenge.getChallengeLevel()));
            }
        }
        if(challengeParameter.getAuthor() != null && !challengeParameter.getAuthor().isEmpty()) {
            challengeVersion.setAuthor(challengeParameter.getAuthor());
        }
        if(challengeParameter.getGoldnuggetType() != null && !challengeParameter.getGoldnuggetType().isEmpty()) {
            createChallenge.setGoldnuggetType(challengeParameter.getGoldnuggetType());
        }
        if(challengeParameter.getStaticGoldnuggetSecret() != null && !challengeParameter.getStaticGoldnuggetSecret().isEmpty()) {
            challengeVersion.setStaticGoldnuggetSecret(challengeParameter.getStaticGoldnuggetSecret());
        }
        if(challengeParameter.getTitle() != null && !challengeParameter.getTitle().isEmpty()){
            challengeVersion.setTitle(translationService.createDefaultTranslation(challengeParameter.getTitle(), "Title", true));
        }
        if(newChallenge.getChallengeType() != null && !newChallenge.getChallengeType().isEmpty()){
            createChallenge.setType(this.translationService.handleTranslationByCodeType(newChallenge.getChallengeType(), "Type", true));
        }
        String challengeId = this.createNewChallenge(createChallenge, challengeVersion );
    }

    public void updateLocalDB(ChallengeDto updatedChallenge, ChallengeParameter challengeParameter){
        Challenge oldChallenge = this.getChallengeById(updatedChallenge.get_id());
        if(challengeParameter.isPrivate() != null && !challengeParameter.isPrivate().isEmpty()) {
            oldChallenge.setPrivate(challengeParameter.isPrivate().equals("true"));
        }
        ChallengeVersion challengeVersion = this.getChallengeVersionByChallengeId(oldChallenge.getId());
        if(updatedChallenge.getChallengeName() != null && !updatedChallenge.getChallengeName().equals("default")){
            challengeVersion.setName(updatedChallenge.getChallengeName());
        }
        if(challengeParameter.getTitle() != null && !challengeParameter.getTitle().isEmpty()){
            TranslatableAttribute attribute = challengeVersion.getTitle();
            if(attribute != null) {
                challengeVersion.setTitle(this.translationService.handleTitleTranslation(attribute.getId(), challengeParameter.getTitle(),true));
            } else {
                challengeVersion.setTitle(this.translationService.handleTitleTranslation(0L, challengeParameter.getTitle(),true));
            }
        }
        if(updatedChallenge.getChallengeType() != null && !updatedChallenge.getChallengeType().isEmpty()){
            oldChallenge.setType(this.translationService.handleTranslationByCodeType(updatedChallenge.getChallengeType(), "Type", true));
        }
        if(challengeParameter.getGoldnuggetType() != null && !challengeParameter.getGoldnuggetType().isEmpty()) {
            oldChallenge.setGoldnuggetType(challengeParameter.getGoldnuggetType());
        }
        if(challengeParameter.getStaticGoldnuggetSecret() != null && !challengeParameter.getStaticGoldnuggetSecret().isEmpty()) {
            challengeVersion.setStaticGoldnuggetSecret(challengeParameter.getStaticGoldnuggetSecret());
        }
        if(challengeParameter.getChallengeCategories() != null && !challengeParameter.getChallengeCategories().equals("")) {
            oldChallenge = this.handleCategory(oldChallenge, challengeParameter.getChallengeCategories());
        }
        if(challengeParameter.getChallengeUsages() != null && !challengeParameter.getChallengeUsages().equals("")) {
            oldChallenge = this.handleUsage(oldChallenge, challengeParameter.getChallengeUsages());
        }
        if(challengeParameter.getChallengeKeywords() != null && !challengeParameter.getChallengeKeywords().equals("")) {
            oldChallenge = this.handleKeyword(oldChallenge, challengeParameter.getChallengeKeywords());
        }
        if(updatedChallenge.getChallengeLevel() != null && !updatedChallenge.getChallengeLevel().isEmpty()){
            if(challengeVersion.getChallengeLevel() != null && challengeVersion.getChallengeLevel().getText().getId().equals(
                    this.getChallengeLevelByText(updatedChallenge.getChallengeLevel()).getText().getId())) {
                challengeVersion.setChallengeLevel(null);
            } else {
                challengeVersion.setChallengeLevel(this.getChallengeLevelByText(updatedChallenge.getChallengeLevel()));
            }
        }

        this.modifyChallenge(oldChallenge, challengeVersion);
    }

    public String createNewChallenge(Challenge newChallenge, ChallengeVersion challengeVersion){
        String challengeId = this.insertChallenge(newChallenge);

        challengeVersion.setChallenge(getChallengeById(newChallenge.getId()));
        this.insertChallengeVersion(challengeVersion);
        return challengeId;
    }

    private List<MediaReference> getMediaReferencesForHints(List<Hint> hints) {
        List<MediaReference> medias = new ArrayList<MediaReference>();
        for(Hint hint : hints) {
            List<MediaReference> mediaReferences = this.mediaService.getMediaReferencesByMarkdownKeys("hint", hint.getId());
            if(mediaReferences != null)
                medias.addAll(mediaReferences);
        }

        return medias;
    }

    private void createMediaReferencesForHints(String isoCode, List<Hint> hints) {
        for(Hint hint : hints) {
            if(hint.getText() != null)
                createMediaReferencesForMarkdown(isoCode, hint.getId(), "hint", hint.getText().getId());
        }
    }

    private List<MediaReference> getMediaReferencesForInstructions(List<Instruction> instructions) {
        List<MediaReference> medias = new ArrayList<MediaReference>();
        for(Instruction instruction : instructions) {
            List<MediaReference> mediaReferences = this.mediaService.getMediaReferencesByMarkdownKeys("instruction", instruction.getId());
            if(mediaReferences != null)
                medias.addAll(mediaReferences);
        }

        return medias;
    }

    private void createMediaReferencesForInstructions(String isoCode, List<Instruction> instructions) {
        for(Instruction instruction : instructions) {
            if(instruction.getText() != null)
                createMediaReferencesForMarkdown(isoCode, instruction.getId(), "instruction", instruction.getText().getId());
        }
    }

    private List<MediaReference> getMediaReferencesForSection(Section section) {
        if(section == null)
            return null;
        List<MediaReference> mediaReferences = this.mediaService.getMediaReferencesByMarkdownKeys("section", section.getId());
        if(mediaReferences != null)
            return new ArrayList<MediaReference>(mediaReferences);
        return null;
    }

    private void createMediaReferenceForSection(String isoCode, Section section) {
        if(section == null || section.getText() == null)
            return;
        createMediaReferencesForMarkdown(isoCode, section.getId(), "section", section.getText().getId());
    }

    private List<MediaReference> getMediaReferencesForSections(List<Section> sections) {
        List<MediaReference> medias = new ArrayList<MediaReference>();
        for(Section section : sections) {
            List<MediaReference> mediaReferences = getMediaReferencesForSection(section);
            if(mediaReferences != null)
                medias.addAll(mediaReferences);
            mediaReferences = getMediaReferencesForHints(section.getHints());
            if(mediaReferences != null)
                medias.addAll(mediaReferences);
            mediaReferences = getMediaReferencesForInstructions(section.getInstructions());
            if(mediaReferences != null)
                medias.addAll(mediaReferences);
        }
        return medias;
    }

    private void createMediaReferencesForSections(String isoCode, List<Section> sections) {
        for(Section section : sections) {
            createMediaReferenceForSection(isoCode, section);
            createMediaReferencesForHints(isoCode, section.getHints());
            createMediaReferencesForInstructions(isoCode, section.getInstructions());
        }
    }

    private List<MediaReference> getMediaReferencesForAbstract(Abstract anAbstract) {
        List<MediaReference> medias = new ArrayList<MediaReference>();
        if(anAbstract == null)
            return null;
        return this.mediaService.getMediaReferencesByMarkdownKeys("abstract", anAbstract.getId().toString());
    }

    private void createMediaReferencesForMarkdown(String isoCode, String markdownId, String markdownType, Long attributeId) {
        List<String> paths = ControllerUtil.extractPathFromMarkdown(this.translationService.getTranslationByAttributeId(attributeId, isoCode));
        if(paths != null && paths.size() > 0) {
            for(String path : paths) {
                MarkdownMediaReference mdMediaRef = this.mediaService.getMarkdownMediaReferenceByUrlAndKeys(markdownId, "abstract", path);
                if(mdMediaRef == null)
                    this.mediaService.createMarkdownMediaEntryByMediaReferenceAndKeys(markdownId, markdownType, this.mediaService.getMediaReferenceByUrl(path));
            }
        }

    }

    public List<MediaReference> getMediaReferencesForSolution(Solution solution) {
        List<MediaReference> medias = new ArrayList<MediaReference>();
        if(solution == null)
            return null;
        return this.mediaService.getMediaReferencesByMarkdownKeys("solution", solution.getId().toString());
    }

    public List<MediaReference> getMediaReferencesFromChallengeVersion(ChallengeVersion version) {
        ArrayList<MediaReference> medias = new ArrayList<>();
        List<MediaReference> mediaReferences = getMediaReferencesForAbstract(version.getAnAbstract());
        if(mediaReferences != null) {
            medias.addAll(mediaReferences);
        }
        mediaReferences = getMediaReferencesForSolution(version.getSolution());
        if(mediaReferences != null)
            medias.addAll(mediaReferences);
        mediaReferences = getMediaReferencesForSections(version.getSections());
        if(mediaReferences != null)
            medias.addAll(mediaReferences);
        return medias;
    }

    public void createMediaReferencesFromChallengeVersion(ChallengeVersion version, String isoCode) {
        if(version == null)
            return;
        if(version.getAnAbstract() != null && version.getAnAbstract().getText() != null && version.getAnAbstract().getText().getId() != null)
            createMediaReferencesForMarkdown(isoCode, version.getAnAbstract().getId().toString(), "abstract", version.getAnAbstract().getText().getId());
        if(version.getSolution() != null && version.getSolution().getText() != null && version.getSolution().getText().getId() != null)
            createMediaReferencesForMarkdown(isoCode, version.getSolution().getId().toString(), "solution", version.getSolution().getText().getId());
        if(version.getSections() != null)
            createMediaReferencesForSections(isoCode, version.getSections());

    }

    public void modifyChallenge(Challenge modifiedChallenge, ChallengeVersion challengeVersion){
        this.updateChallenge(modifiedChallenge.getId(), modifiedChallenge);

        challengeVersion.setChallenge(this.challengeRepository.find().query().where().eq("id", modifiedChallenge.getId()).findUnique());
        if(this.challengeVersionRepository.find().query().where().eq("id", challengeVersion.getId()).findUnique() == null){
            this.insertChallengeVersion(challengeVersion);
        } else {
            this.updateChallengeVersion(challengeVersion.getId(), challengeVersion);
        }
    }

    public Challenge getChallengeByImportId(int importId) {
        return this.challengeRepository.find().query().where().eq("importId", importId).findUnique();
    }

    public Challenge getChallengeById(String challengeId){
        return this.challengeRepository.find().query().where().eq("id", challengeId).findUnique();
    }

    public List<Challenge> getChallenges(){
        return this.challengeRepository.find().query().where().findList();
    }

    public ChallengeVersion getChallengeVersionById(Long challengeVersionId){
        return this.challengeVersionRepository.find().query().where().eq("id", challengeVersionId).findUnique();
    }

    public TranslatableAttribute getTitleByAttributeId(Long attributeId){
        return this.translationService.getTranslatableAttributeById(attributeId);
    }

    public Abstract getAbstractByChallengeId(String challengeId) {
        ChallengeVersion challengeVersion = this.challengeVersionRepository.find().query().where().eq("challengeid", challengeId).findUnique();
        if(challengeVersion != null)
            return challengeVersion.getAnAbstract();
        return null;
    }

    public Abstract getAbstractById(Long abstractId){
        return this.abstractRepository.find().query().where().eq("id", abstractId).findUnique();
    }

    public Solution getSolutionByChallengeId(String challengeId) {
        ChallengeVersion challengeVersion = this.challengeVersionRepository.find().query().where().eq("challengeid", challengeId).findUnique();
        if(challengeVersion != null)
            return challengeVersion.getSolution();
        return null;
    }

    public Solution getSolutionById(Long solutionId){
        return this.solutionRepository.find().query().where().eq("id", solutionId).findUnique();
    }

    public Section getSectionById(String sectionId){
        return this.sectionRepository.find().query().where().eq("id", sectionId).findUnique();
    }

    public Hint getHintById(String hintId){
        return this.hintRepository.find().query().where().eq("id", hintId).findUnique();
    }

    public Instruction getInstructionById(String instructionId){
        return this.instructionRepository.find().query().where().eq("id", instructionId).findUnique();
    }

    TranslatableAttribute getTitlePerLanguageByChallengeId(String challengeId, String isoLangCode){
        if(challengeId == null) {
            return null;
        }
        ChallengeVersion version = getChallengeVersionByChallengeId(challengeId);
        if(version.getTitle() != null) {
            TranslatableAttribute attribute = this.translationService.getTranslationPerLanguageByAttributeId(version.getTitle().getId(), isoLangCode, "Title");
            if(attribute == null)
                return null;
            return attribute;
        }
        return null;
    }

    Abstract getAbstractPerLanguageByChallengeId(String challengeId, String isoLangCode){
        if(challengeId == null) {
            return null;
        }
        ChallengeVersion version = getChallengeVersionByChallengeId(challengeId);
        if(version.getAnAbstract() != null && version.getAnAbstract().getText() != null) {
            TranslatableAttribute attribute = this.translationService.getTranslationPerLanguageByAttributeId(version.getAnAbstract().getText().getId(), isoLangCode, "Abstract");
            if(attribute == null)
                return null;
            return this.abstractRepository.find().query().where().eq("text", attribute).findUnique();
        }
        return null;
    }

    Solution getSolutionPerLanguageByChallengeId(String challengeId, String isoLangCode){
        if(challengeId == null) {
            return null;
        }
        ChallengeVersion version = getChallengeVersionByChallengeId(challengeId);
        if(version.getSolution() != null && version.getSolution().getText() != null) {
            TranslatableAttribute attribute = this.translationService.getTranslationPerLanguageByAttributeId(version.getSolution().getText().getId(), isoLangCode, "Solution");
            if (attribute == null)
                return null;
            return this.solutionRepository.find().query().where().eq("text", attribute).findUnique();
        }
        return null;
    }

    public Section getSectionPerLanguageByText(String sectionMd, String isoLangCode){
        if(sectionMd == null) {
            return null;
        }
        TranslatableAttribute attribute = this.translationService.getTranslationPerLanguage(sectionMd, isoLangCode, "Section");
        if(attribute == null)
            return null;
        return this.sectionRepository.find().query().where().eq("text", attribute).findUnique();
    }

    public Hint getHintPerLanguageByText(String hintMD, String isoLangCode){
        if(hintMD == null) {
            return null;
        }
        TranslatableAttribute attribute = this.translationService.getTranslationPerLanguage(hintMD, isoLangCode, "Hint");
        if(attribute == null)
            return null;
        return this.hintRepository.find().query().where().eq("text", attribute).findUnique();
    }

    public Instruction getInstructionPerLanguageByText(String instructionMD, String isoLangCode){
        if(instructionMD == null) {
            return null;
        }
        TranslatableAttribute attribute = this.translationService.getTranslationPerLanguage(instructionMD, isoLangCode, "Instruction");
        if(attribute == null)
            return null;
        return this.instructionRepository.find().query().where().eq("text", attribute).findUnique();
    }

    public Rating getRatingById(Long ratingId){
        return this.ratingRepository.find().query().where().eq("id", ratingId).findUnique();
    }

    public ChallengeLevel getChallengeLevelById(Long challengeLevelId){
        return this.challengeLevelRepository.find().query().where().eq("id", challengeLevelId).findUnique();
    }

    public ChallengeVersion getChallengeVersionByChallengeId(String challengeId){
        List<ChallengeVersion> challengeVersions = this.challengeVersionRepository.find().query().where().eq("challengeId", challengeId).findList();
        Long id = 0L;
        for(ChallengeVersion challengeVersion : challengeVersions) {
            if(challengeVersion.getId() > id) {
                id = challengeVersion.getId();
            }
        }
        return this.challengeVersionRepository.find().query().where().eq("id", id).findUnique();
    }

    public List<ChallengeType> getChallengeTypes(){
        return this.challengeTypeRepository.find().query().findList();
    }
    public List<ChallengeLevel> getChallengeLevels(){
        return this.challengeLevelRepository.find().query().findList();
    }
    public List<ChallengeUsage> getChallengeUsages(){
        return this.challengeUsageRepository.find().query().findList();
    }
    public List<Category> getCategories(){
        return this.categoryRepository.find().query().findList();
    }
    public List<Keyword> getKeywords(){
        return this.keywordRepository.find().query().findList();
    }


    public List<Category> getCategoriesByChallengeId(String challengeId){
        return this.challengeRepository.find().query().where().eq("id", challengeId).findUnique().getCategories();
    }

    public List<ChallengeUsage> getUsagesByChallengeId(String challengeId){
        return this.challengeRepository.find().query().where().eq("id", challengeId).findUnique().getUsages();
    }

    public ChallengeLevel getChallengeLevelByText(String type){
        return this.challengeLevelRepository.find().query().where().eq("text", this.translationService.getDefaultTranslation(type)).findUnique();
    }

    public Category getCategoryByText(String text){
        return this.categoryRepository.find().query().where().eq("categoryName", this.translationService.getDefaultTranslation(text)).findUnique();
    }

    public Category getCategoryById(Long id){
        return this.categoryRepository.find().query().where().eq("id", id).findUnique();
    }

    public ChallengeUsage getUsageByText(String text){
        return this.challengeUsageRepository.find().query().where().eq("text", this.translationService.getDefaultTranslation(text)).findUnique();
    }

    public ChallengeUsage getUsageById(Long id){
        return this.challengeUsageRepository.find().query().where().eq("id", id).findUnique();
    }

    public Keyword getKeywordByText(String text){
        return this.keywordRepository.find().query().where().eq("text", this.translationService.getDefaultTranslation(text)).findUnique();
    }

    public Keyword getKeywordById(Long id){
        return this.keywordRepository.find().query().where().eq("id", id).findUnique();
    }

    public ChallengeResource getResourceById(String id){
        return this.challengeResourceRepository.find().query().where().eq("id", id).findUnique();
    }

    private void removeMediaReferenceCascading(Long mediaReferenceId) {
        MediaReference mediaReference = this.mediaService.getMediaReferenceById(mediaReferenceId);
        if(mediaReference != null) {
            this.mediaService.removeMediaObjectByReference(mediaReference);
            this.mediaService.removeMediaReferenceById(mediaReference.getId());
        }
    }

    private void cleanChallengeVersionFromMediaByMediaId(Long mediaId) {
        for(ChallengeVersion version : this.challengeVersionRepository.find().query().where().eq("mediareferenceid", mediaId).findList()) {
            version.setMediaReference(null);
            this.updateChallengeVersion(version.getId(), version);
        }
    }

    public void disconnectChallengeFromMedia(String challengeId, Long mediaId) {
        if(this.mediaService.getMediaReferenceById(mediaId) == null)
            return;
        ChallengeVersion savedChallengeVersion = this.getChallengeVersionByChallengeId(challengeId);
        if(savedChallengeVersion == null) {
            return;
        }

        removeMediaReferenceCascading(mediaId);
        savedChallengeVersion.setMediaReference(null);
        this.updateChallengeVersion(savedChallengeVersion.getId(), savedChallengeVersion);
    }

    public void disconnectChallengeFromMediaByMediaId(Long mediaId) {
        if(this.mediaService.getMediaReferenceById(mediaId) == null)
            return;
        this.cleanChallengeVersionFromMediaByMediaId(mediaId);
        removeMediaReferenceCascading(mediaId);
    }

    public String addCategoryToChallenge(Challenge challenge, Category category){
        if(this.isCategoryAlreadyAttachedToChallenge(challenge, category))
            challenge.removeCategory(category.getId());
        else
            challenge.addCategory(category);
        return this.updateChallenge(challenge.getId(), challenge);
    }

    public String addUsageToChallenge(Challenge challenge, ChallengeUsage challengeUsage){
        if(this.isUsageAlreadyAttachedToChallenge(challenge, challengeUsage))
            challenge.removeChallengeUsage(challengeUsage.getId());
        else
            challenge.addUsage(challengeUsage);
        return this.updateChallenge(challenge.getId(), challenge);
    }

    public String addKeywordsToChallenge(Challenge challenge, Keyword keyword){
        if(this.isKeywordAlreadyAttachedToChallenge(challenge, keyword))
            return "";
        challenge.addKeyword(keyword);
        return this.updateChallenge(challenge.getId(), challenge);
    }

    public Long removeCategoryFromChallenge(Challenge challenge, Category category) {
        return this.challengeRepository.find().query().where().eq("challengeId", challenge).findUnique().removeCategory(category.getId());
    }

    public Category findCategoryByTranslatableAttributeId(Long categoryAttribute){
        return this.categoryRepository.find().query().where().eq("categoryname", categoryAttribute).findUnique();
    }

    public Long getSimpleMarkdownIdByChallengeId(String challengeId, String markdowType) {
        ChallengeVersion version = getChallengeVersionByChallengeId(challengeId);
        if(markdowType.equals("abstract") && version.getAnAbstract() != null) {
            return version.getAnAbstract().getId();
        }
        if(markdowType.equals("solution") && version.getSolution() != null) {
            return version.getSolution().getId();
        }
        return 0L;
    }

    public ArrayNode getChallengesMetadata() {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Challenge> challenges = this.challengeRepository.find().all();
        ArrayNode jsonArray = objectMapper.createArrayNode();
        for(Challenge challenge : challenges) {
            String challengeType = null;
            ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
            if(challenge.getType() != null) {
                challengeType = this.translationService.getTranslationByAttributeId(challenge.getType().getId(), "en");
            }
            if(challengeVersion != null) {
                String challengeLevel = null;
                if(challengeVersion.getChallengeLevel() != null) {
                    challengeLevel = this.translationService.getTranslationByAttributeId(challengeVersion.getChallengeLevel().getText().getId(), "en");
                }
                JsonNode node = Json.parse("{\"challengeId\":\"" + challenge.getId() + "\",\"created\":\"" + challengeVersion.getCreated() + "\",\"challengeName\":\"" + challengeVersion.getName() + "\", \"challengeType\":\"" + challengeType + "\",  \"challengeLevel\":\"" + challengeLevel + "\"}");
                jsonArray.add(node);
            } else {
                JsonNode node = Json.parse("{\"challengeId\":\"" + challenge.getId() + "\",\"created\":\"" + "not yet created" + "\",\"challengeName\":\"" + null + "\", \"challengeType\":\"" + challengeType + "\",  \"challengeLevel\":\"" + null + "\"}");
                jsonArray.add(node);
            }
        }
        return jsonArray;
    }

    public ArrayNode getFilteredChallengesMetadata(String fromDate, String toDate) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Challenge> challenges = this.challengeRepository.find().all();
        ArrayNode jsonArray = objectMapper.createArrayNode();
        for(Challenge challenge : challenges) {
            String challengeType = null;
            ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
            if(challenge.getType() != null) {
                challengeType = this.translationService.getTranslationByAttributeId(challenge.getType().getId(), "en");
            }
            if(challengeVersion != null) {
                if(challengeVersion.getCreated().before(TimestampUtil.getTimestampForFilter(toDate)) && challengeVersion.getCreated().after(TimestampUtil.getTimestampForFilter(fromDate))) {
                    String challengeLevel = null;
                    if(challengeVersion.getChallengeLevel() != null) {
                        challengeLevel = this.translationService.getTranslationByAttributeId(challengeVersion.getChallengeLevel().getText().getId(), "en");
                    }
                    JsonNode node = Json.parse("{\"challengeId\":\"" + challenge.getId() + "\",\"created\":\"" + challengeVersion.getCreated() + "\",\"challengeName\":\"" + challengeVersion.getName() + "\", \"challengeType\":\"" + challengeType + "\",  \"challengeLevel\":\"" + challengeLevel + "\"}");
                    jsonArray.add(node);
                }
            }
        }
        return jsonArray;
    }

    public Long insertChallengeVersion(ChallengeVersion challengeVersion){
        CompletionStage<Long> id = this.challengeVersionRepository.insert(challengeVersion);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    public Long updateChallengeVersion(Long oldId, ChallengeVersion challengeVersion){
        CompletionStage<Optional<Long>> id = this.challengeVersionRepository.update(oldId, challengeVersion);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long deleteChallengeVersion(Long idToRemove){
        CompletionStage<Optional<Long>> id = this.challengeVersionRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long insertChallengeType(ChallengeType challengeType){
        CompletionStage<Long> id = this.challengeTypeRepository.insert(challengeType);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    public Long insertChallengeLevel(ChallengeLevel challengeLevel){
        CompletionStage<Long> id = this.challengeLevelRepository.insert(challengeLevel);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    public Long insertKeyword(Keyword keyword){
        CompletionStage<Long> id = this.keywordRepository.insert(keyword);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    private String insertChallengeResource(ChallengeResource challengeResource) {
        CompletionStage<String> id = this.challengeResourceRepository.insert(challengeResource);
        return ControllerUtil.handleCompletionStageString(id);
    }

    public Long insertUsage(ChallengeUsage usage){
        CompletionStage<Long> id = this.challengeUsageRepository.insert(usage);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    Challenge createChallengeById(String challengeId) {
        Challenge newChallenge = new Challenge();
        newChallenge.setId(challengeId);

        if(this.getChallengeById(challengeId) == null) {
            this.insertChallenge(newChallenge);
            ChallengeVersion challengeVersion = new ChallengeVersion();
            challengeVersion.setChallenge(this.getChallengeById(challengeId));
            insertChallengeVersion(challengeVersion);
        }
        return this.getChallengeById(challengeId);
    }

    public String insertChallenge(Challenge challenge){
        CompletionStage<String> id = this.challengeRepository.insert(challenge);
        return ControllerUtil.handleCompletionStageString(id);
    }

    public String updateChallenge(String oldId, Challenge challenge){
        CompletionStage<Optional<String>> id = this.challengeRepository.update(oldId, challenge);
        return ControllerUtil.handleCompletionStageOptionalString(id);
    }

    public String deleteChallenge(String idToRemove){
        CompletionStage<Optional<String>> id = this.challengeRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalString(id);
    }

    public Long insertAbstract(Abstract anAbstract){
        CompletionStage<Long> id = this.abstractRepository.insert(anAbstract);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    public Long updateAbstract(Long oldId, Abstract anAbstract){
        CompletionStage<Optional<Long>> id = this.abstractRepository.update(oldId, anAbstract);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long deleteAbstract(Long idToRemove){
        CompletionStage<Optional<Long>> id = this.abstractRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long insertSolution(Solution solution){
        CompletionStage<Long> id = this.solutionRepository.insert(solution);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    public Long updateSolution(Long oldId, Solution solution){
        CompletionStage<Optional<Long>> id = this.solutionRepository.update(oldId, solution);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long deleteSolution(Long idToRemove){
        CompletionStage<Optional<Long>> id = this.solutionRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Abstract findAbstractByTranslatableAttributeId(Long translatableAttributeId) {
        return this.abstractRepository.find().query().where().eq("text", translatableAttributeId).findUnique();
    }

    public String insertHint(Hint hint){
        CompletionStage<String> id = this.hintRepository.insert(hint);
        return ControllerUtil.handleCompletionStageString(id);
    }

    public String updateHint(String oldId, Hint hint){
        CompletionStage<Optional<String>> id = this.hintRepository.update(oldId, hint);
        return ControllerUtil.handleCompletionStageOptionalString(id);
    }

    public String deleteHint(String idToRemove){
        CompletionStage<Optional<String>> id = this.hintRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalString(id);
    }

    public String insertInstruction(Instruction instruction){
        CompletionStage<String> id = this.instructionRepository.insert(instruction);
        return ControllerUtil.handleCompletionStageString(id);
    }

    public String updateInstruction(String oldId, Instruction instruction){
        CompletionStage<Optional<String>> id = this.instructionRepository.update(oldId, instruction);
        return ControllerUtil.handleCompletionStageOptionalString(id);
    }

    public String deleteInstruction(String idToRemove){
        CompletionStage<Optional<String>> id = this.instructionRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalString(id);
    }

    public String insertSection(Section section){
        CompletionStage<String> id = this.sectionRepository.insert(section);
        return ControllerUtil.handleCompletionStageString(id);
    }

    public String updateSection(String oldId, Section section){
        CompletionStage<Optional<String>> id = this.sectionRepository.update(oldId, section);
        return ControllerUtil.handleCompletionStageOptionalString(id);
    }

    public String deleteSection(String idToRemove){
        CompletionStage<Optional<String>> id = this.sectionRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalString(id);
    }

    public Long insertRating(Rating rating){
        CompletionStage<Long> id = this.ratingRepository.insert(rating);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    public Long updateRating(Long oldId, Rating rating){
        CompletionStage<Optional<Long>> id = this.ratingRepository.update(oldId, rating);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long deleteRating(Long idToRemove){
        CompletionStage<Optional<Long>> id = this.ratingRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long insertCategory(Category category){
        CompletionStage<Long> id = this.categoryRepository.insert(category);
        return ControllerUtil.handleCompletionStageLong(id);
    }

    public Long updateCategory(Long oldId, Category category){
        CompletionStage<Optional<Long>> id = this.categoryRepository.update(oldId, category);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }

    public Long deleteCategory(Long idToRemove){
        CompletionStage<Optional<Long>> id = this.ratingRepository.delete(idToRemove);
        return ControllerUtil.handleCompletionStageOptionalLong(id);
    }
}
