package services;

import models.DataTransferObject.CTFChallengeDto;
import models.DataTransferObject.CTFHintDto;
import models.DataTransferObject.ImportedChallengeDtos.*;
import models.DatabaseObject.challenge.*;
import models.DatabaseObject.translation.AttributeTranslation;
import models.DatabaseObject.translation.TranslatableAttribute;
import utils.ControllerUtil;
import utils.TimestampUtil;

import javax.inject.Inject;
import java.util.*;

public class DataTransferService {
    private final TranslationService translationService;
    private final ChallengeService challengeService;
    private final MediaService mediaService;

    public class StepComparator implements Comparator<ChallengeStepReferenceDto> {
        @Override
        public int compare(ChallengeStepReferenceDto firstStepDto, ChallengeStepReferenceDto secondStepDto) {
            return Integer.compare(firstStepDto.getOrder(), secondStepDto.getOrder());
        }
    }

    @Inject
    public DataTransferService(ChallengeService challengeService,
                               TranslationService translationService,
                               MediaService mediaService){
        this.challengeService = challengeService;
        this.translationService = translationService;
        this.mediaService = mediaService;
    }

    private ChallengeResourceDto createChallengeResourceDto(ChallengeResource challengeResource) {
        return new ChallengeResourceDto(challengeResource.getId(), challengeResource.getType());
    }

    private ChallengeSectionReferenceDto createChallengeSectionDto(Section section) {
        return new ChallengeSectionReferenceDto(section.getId(), section.getOrder(), this.createStepReferenceDto(section));
    }

    private String createImportedInstruction(Section savedSection, ChallengeStepReferenceDto step) {
        Instruction instruction = new Instruction();
        instruction.setId(step.getId());
        instruction.setOrder(step.getOrder());
        instruction.setSection(savedSection);
        return this.challengeService.insertInstruction(instruction);
    }

    private String createImportedHint(Section savedSection, ChallengeStepReferenceDto step) {
        Hint hint = new Hint();
        hint.setId(step.getId());
        hint.setSection(savedSection);
        hint.setOrder(step.getOrder());
        return this.challengeService.insertHint(hint);
    }

    private List<Hint> createHints(Section savedSection, List<ChallengeStepReferenceDto> steps) {
        List<Hint> hints = new ArrayList<>();
        for(ChallengeStepReferenceDto step : steps) {
            if(step.getType().equals("hint")) {
                hints.add(this.challengeService.getHintById(createImportedHint(savedSection, step)));
            }
        }
        return hints;
    }

    private List<Instruction> createInstructions(Section savedSection, List<ChallengeStepReferenceDto> steps) {
        List<Instruction> instructions = new ArrayList<>();
        for(ChallengeStepReferenceDto step : steps) {
            if(step.getType().equals("instruction")) {
                instructions.add(this.challengeService.getInstructionById(createImportedInstruction(savedSection, step)));
            }
        }
        return instructions;
    }

    private String createSection(ChallengeSectionReferenceDto sectionDto, ChallengeVersion challengeVersion){
        Section section = new Section();
        section.setOrder(section.getOrder());
        section.setId(sectionDto.getId());
        section.setChallengeVersion(challengeVersion);
        String sectionId = this.challengeService.insertSection(section);
        Section savedSection = this.challengeService.getSectionById(sectionId);
        createInstructions(savedSection, sectionDto.getSteps());
        createHints(savedSection, sectionDto.getSteps());
        return sectionId;
    }

    private ArrayList<ChallengeStepReferenceDto> createStepReferenceDto(Section section) {
        ArrayList<ChallengeStepReferenceDto> stepDtos = new ArrayList<>();
        for(Instruction instruction : section.getInstructions()) {
            stepDtos.add(new ChallengeStepReferenceDto(instruction.getId(), instruction.getOrder(), "instruction"));
        }
        for(Hint hint : section.getHints()) {
            stepDtos.add(new ChallengeStepReferenceDto(hint.getId(), hint.getOrder(), "hint"));
        }

        stepDtos.sort(new StepComparator());
        return stepDtos;
    }

    private ArrayList<ChallengeStepMDDto> createStepMDDto(Section section, String isoCode) {
        ArrayList<ChallengeStepMDDto> stepDtos = null;
        for(Instruction instruction : section.getInstructions()) {
            if(instruction.getText() != null) {
                String translation = this.translationService.getTranslationByAttributeId(instruction.getText().getId(), isoCode);
                if(translation != null) {
                    if(stepDtos == null)
                        stepDtos = new ArrayList<>();
                    stepDtos.add(new ChallengeStepMDDto(instruction.getId(), translation));
                }
            }
        }
        for(Hint hint : section.getHints()) {
            if(hint.getText() != null) {
                String translation = this.translationService.getTranslationByAttributeId(hint.getText().getId(), isoCode);
                if(translation != null && !translation.equals("")) {
                    if(stepDtos == null)
                        stepDtos = new ArrayList<>();
                    stepDtos.add(new ChallengeStepMDDto(hint.getId(), translation));
                }
            }
        }
        return stepDtos;
    }

    private ArrayList<TranslatedStepDto> createTranslatedStepDto(Section section, String isoCode) {
        ArrayList<TranslatedStepDto> stepDtos = null;
        for(Instruction instruction : section.getInstructions()) {
            if(instruction.getText() != null) {
                String translation = this.translationService.getTranslationByAttributeId(instruction.getText().getId(), isoCode);
                if(translation == null)
                    translation = "";
                boolean isUserCreated = this.translationService.isTranslationUserCreated(instruction.getText().getId(), isoCode);
                if(stepDtos == null)
                    stepDtos = new ArrayList<>();
                stepDtos.add(new TranslatedStepDto(instruction.getId(), "instruction", instruction.getOrder(), isUserCreated, translation));
            }
        }
        for(Hint hint : section.getHints()) {
            if(hint.getText() != null) {
                String translation = this.translationService.getTranslationByAttributeId(hint.getText().getId(), isoCode);
                if(translation == null)
                    translation = "";
                boolean isUserCreated = this.translationService.isTranslationUserCreated(hint.getText().getId(), isoCode);
                if(stepDtos == null)
                    stepDtos = new ArrayList<>();
                stepDtos.add(new TranslatedStepDto(hint.getId(), "hint", hint.getOrder(), isUserCreated, translation));
            }
        }
        return stepDtos;
    }

    private ChallengeSectionMDDto createSectionMDDto(Section section, String isoCode) {
        ArrayList<ChallengeStepMDDto> steps = createStepMDDto(section, isoCode);
        if(section.getText() != null) {
            String translation = this.translationService.getTranslationByAttributeId(section.getText().getId(), isoCode);
            if(steps != null || translation != null)
                return new ChallengeSectionMDDto(section.getId(), translation , steps);
            return null;
        } else if (steps != null && steps.size() > 0){
            return new ChallengeSectionMDDto(section.getId(), "" , steps);
        }
        return null;
    }

    private TranslatedSectionDto createTranslatedSectionDto(Section section, String isoCode) {
        ArrayList<TranslatedStepDto> steps = createTranslatedStepDto(section, isoCode);
        if(section.getText() != null) {
            String translation = this.translationService.getTranslationByAttributeId(section.getText().getId(), isoCode);
            if(translation == null)
                translation = "";
            boolean isUserCreated = this.translationService.isTranslationUserCreated(section.getText().getId(), isoCode);
            if(steps != null || !translation.equals(""))
                return new TranslatedSectionDto(section.getId(), translation , section.getOrder(), isUserCreated, steps);
            return null;
        }
        return null;
    }

    private ChallengeSectionReferenceDto createSectionDto(Section section) {
        return new ChallengeSectionReferenceDto(section.getId(), section.getOrder(), createStepReferenceDto(section));
    }

    private TranslatableAttribute handleImportType(String type) {
        TranslatableAttribute typeAttribute = this.translationService.getDefaultTranslation(type);
        if(typeAttribute != null) {
            return typeAttribute;
        }
        return this.translationService.createDefaultTranslation(type, "Type", true);
    }

    private ChallengeLevel handleImportLevel(Long levelId) {
        return this.challengeService.getChallengeLevelById(levelId);
    }

    private boolean hasDelimiter(String value) {
        return value != null && (value.contains(",") || value.contains(";") || value.contains("|"));
    }

    private String trimCategoryStringPart(String value) {
        if(value == null)
            return "";
        if(hasDelimiter(value)) {
            value = value.trim();
            return value.substring(1, value.length()).trim();
        }
        return value.trim();
    }

    private String getDelimiter(String value) {
        if(value == null)
            return null;
        if(value.indexOf(",") > 0)
            return ",";
        if(value.indexOf(";") > 0)
            return ";";
        if(value.indexOf("|") > 0)
            return "|";
        return null;
    }

    private void handleImportCategory(Challenge importedChallenge, String category) {
        if(category.equals(""))
            return;
        if(hasDelimiter(category)) {
            handleImportCategory(importedChallenge, trimCategoryStringPart(category.substring(category.indexOf(getDelimiter(category)), category.length())));
            int actualIndex = category.indexOf(getDelimiter(category));
            category = trimCategoryStringPart(category.substring(0, actualIndex));
        }

        TranslatableAttribute categoryAttribute = this.translationService.getDefaultTranslation(category);
        if(categoryAttribute == null) {
            Category newCategory = new Category();
            newCategory.setCategoryName(this.translationService.createDefaultTranslation(category, "Category", true));
            Long categoryId = this.challengeService.insertCategory(newCategory);
            this.challengeService.addCategoryToChallenge(importedChallenge, this.challengeService.getCategoryById(categoryId));
        } else {
            Category oldCategory =this.challengeService.findCategoryByTranslatableAttributeId(categoryAttribute.getId());
            this.challengeService.addCategoryToChallenge(importedChallenge, oldCategory);
        }
    }

    private Challenge mapCTFChallengeToChallenge(CTFChallengeDto ctfChallengeDto) {
        Challenge newChallenge = new Challenge();
        String challengeId = UUID.randomUUID().toString();
        newChallenge.setId(challengeId);
        newChallenge.setPrivate(false);
        newChallenge.setImportId(ctfChallengeDto.getId());
        if(ctfChallengeDto.getChallengeType() != null) {
            newChallenge.setType(handleImportType(ctfChallengeDto.getChallengeType()));
        }

        ChallengeVersion challengeVersion = new ChallengeVersion();
        challengeVersion.setName(ctfChallengeDto.getChallengeName());
        challengeVersion.setMaxPoints(ctfChallengeDto.getValue());
        //challengeVersion.setAnAbstract(this.handleImportAbstract(ctfChallengeDto.getChallengeCategory()));

        this.challengeService.createNewChallenge(newChallenge, challengeVersion );

        if(ctfChallengeDto.getChallengeCategory() != null) {
            Challenge createdChallenge = this.challengeService.getChallengeById(challengeId);
            this.handleImportCategory(createdChallenge, ctfChallengeDto.getChallengeCategory());
            return createdChallenge;
        }

        return this.challengeService.getChallengeById(challengeId);
    }

    private Hint mapCTFHintToHint(CTFHintDto ctfHintDto) {
        //TODO: noch korrekt implementieren
        Hint newHint = new Hint();
        String challengeId = this.challengeService.getChallengeByImportId(ctfHintDto.getChallengeId()).getId();
        this.challengeService.getChallengeVersionByChallengeId(challengeId);
        return newHint;
    }

    public void importNewCTFChallenge(CTFChallengeDto ctfChallengeDto) {
        //TODO: Description to Markdown muss hier noch implementiert werden
        mapCTFChallengeToChallenge(ctfChallengeDto);
    }

    public void importNewCTFHint(CTFHintDto ctfHintDto) {
        mapCTFHintToHint(ctfHintDto);
    }

    private ArrayList<ChallengeResource> handleImportResources(ArrayList<ChallengeResourceDto> resources) {
        ArrayList<ChallengeResource> challengeResourceArrayList = new ArrayList<>();
        for(ChallengeResourceDto importedResource : resources) {
            ChallengeResource challengeResource = this.challengeService.getResourceById(importedResource.getId());
            if(challengeResource != null) {
                challengeResourceArrayList.add(challengeResource);
            } else {
                this.challengeService.createChallengeResource(importedResource.getId(), importedResource.getType());
                challengeResourceArrayList.add(this.challengeService.getResourceById(importedResource.getId()));
            }
        }
        return challengeResourceArrayList;
    }

    private ArrayList<ChallengeResourceDto> handleExportResources(List<ChallengeResource> challengeResources) {
        ArrayList<ChallengeResourceDto> resourceArrayList = new ArrayList<>();
        for(ChallengeResource challengeResourceToExport : challengeResources) {
            resourceArrayList.add(this.createChallengeResourceDto(challengeResourceToExport));
        }
        return resourceArrayList;
    }

    private ArrayList<Section> handleImportSections(ChallengeVersion challengeVersion, ArrayList<ChallengeSectionReferenceDto> sections) {
        ArrayList<Section> sectionArrayList = new ArrayList<>();
        for(ChallengeSectionReferenceDto importedSection : sections) {
            Section section = this.challengeService.getSectionById(importedSection.getId());
            if(section != null) {
                sectionArrayList.add(section);
            } else {
                String id = createSection(importedSection, challengeVersion);
                sectionArrayList.add(this.challengeService.getSectionById(id));
            }
        }
        return sectionArrayList;
    }

    private ArrayList<ChallengeSectionReferenceDto> handleExportSections(List<Section> sections) {
        ArrayList<ChallengeSectionReferenceDto> sectionArrayList = new ArrayList<>();
        for(Section sectionToExport : sections) {
            sectionArrayList.add(this.createChallengeSectionDto(sectionToExport));
        }
        return sectionArrayList;
    }

    private ArrayList<Category> handleImportCategories(ArrayList<String> categories) {
        ArrayList<Category> categoryArrayList = new ArrayList<>();
        for(String importedCategory : categories) {
            Category category = this.challengeService.getCategoryByText(importedCategory);
            if(category != null) {
                categoryArrayList.add(category);
            } else {
                Long categoryId = this.challengeService.createCategory(importedCategory);
                categoryArrayList.add(this.challengeService.getCategoryById(categoryId));
            }
        }
        return categoryArrayList;
    }

    private ArrayList<String> handleExportCategories(List<Category> categories) {
        ArrayList<String> categoryNames = new ArrayList<>();
        for(Category categoryToExport : categories) {
            String category = this.translationService.getTranslationByAttributeId(categoryToExport.getCategoryName().getId(), "en");
            categoryNames.add(category);
        }
        return categoryNames;
    }


    private ArrayList<Keyword> handleImportKeywords(ArrayList<String> keywords) {
        ArrayList<Keyword> keywordArrayList = new ArrayList<>();
        for(String importedKeyword : keywords) {
            Keyword keyword = this.challengeService.getKeywordByText(importedKeyword);
            if(keyword != null) {
                keywordArrayList.add(keyword);
            } else {
                Long keywordId = this.challengeService.createKeyword(importedKeyword);
                keywordArrayList.add(this.challengeService.getKeywordById(keywordId));
            }
        }
        return keywordArrayList;
    }

    private Abstract handleImportAbstract(String challengeId, List<LanguageComponentDto> languageComponentDtos) {
        Long abstractId = 0L;
        Long attributeId = 0L;
        for(LanguageComponentDto languageComponentDto : languageComponentDtos) {
            Abstract anAbstract = null;
            if(abstractId == 0L) {
                anAbstract = this.challengeService.getAbstractPerLanguageByChallengeId(challengeId, languageComponentDto.getIsoCode());
            } else {
                anAbstract = this.challengeService.getAbstractById(abstractId);
            }
            if(anAbstract == null) {
                anAbstract = new Abstract();
                attributeId = this.translationService.createTranslation(attributeId, languageComponentDto.getAbstractMD(), "Abstract", languageComponentDto.getIsoCode(), true);
                anAbstract.setText(this.translationService.getTranslatableAttributeById(attributeId));
                abstractId = this.challengeService.insertAbstract(anAbstract);
            } else {
                Long abstractTextId = 0L;
                if(anAbstract.getText() != null) {
                    abstractTextId = anAbstract.getText().getId();
                }
                attributeId = this.translationService.createTranslation(abstractTextId, languageComponentDto.getAbstractMD(), "Abstract", languageComponentDto.getIsoCode(), true);
                anAbstract.setText(this.translationService.getTranslatableAttributeById(attributeId));
                abstractId = this.challengeService.updateAbstract(anAbstract.getId(), anAbstract);
            }
        }
        return this.challengeService.getAbstractById(abstractId);
    }

    private Solution handleImportSolution(String challengeId, List<LanguageComponentDto> languageComponentDtos) {
        Long solutionId = 0L;
        Long attributeId = 0L;
        for(LanguageComponentDto languageComponentDto : languageComponentDtos) {
            Solution solution = null;
            if(solutionId == 0L) {
                solution = this.challengeService.getSolutionPerLanguageByChallengeId(challengeId, languageComponentDto.getIsoCode());
            } else {
                solution = this.challengeService.getSolutionById(solutionId);
            }
            if(solution == null) {
                solution = new Solution();
                attributeId = this.translationService.createTranslation(attributeId, languageComponentDto.getSolutionMD(), "Solution", languageComponentDto.getIsoCode(), true);
                solution.setText(this.translationService.getTranslatableAttributeById(attributeId));
                solutionId = this.challengeService.insertSolution(solution);
            } else {
                Long solutionTextId = 0L;
                if(solution.getText() != null) {
                    solutionTextId = solution.getText().getId();
                }
                attributeId = this.translationService.createTranslation(solutionTextId, languageComponentDto.getSolutionMD(), "Solution", languageComponentDto.getIsoCode(), true);
                solution.setText(this.translationService.getTranslatableAttributeById(attributeId));
                solutionId = this.challengeService.updateSolution(solution.getId(), solution);
            }
        }
        return this.challengeService.getSolutionById(solutionId);
    }

    private ArrayList<String> handleExportKeywords(List<Keyword> keywords) {
        ArrayList<String> keywordTexts = new ArrayList<>();
        for(Keyword keywordToExport : keywords) {
            keywordTexts.add(keywordToExport.getText());
        }
        return keywordTexts;
    }

    private ArrayList<Hint> handleImportHints(List<ChallengeStepMDDto> stepMDDtos, String isoLanguageCode) {
        Long tempAttributeId = 0L;
        Long attributeId = 0L;
        String hintId = null;
        ArrayList<Hint> hints = new ArrayList<>();
        for(ChallengeStepMDDto challengeStepMDDto : stepMDDtos) {
            Hint hint = this.challengeService.getHintById(challengeStepMDDto.getId());
            if(hint != null) {
                Long hintTextId = 0L;
                if(hint.getText() != null) {
                    hintTextId = hint.getText().getId();
                }
                attributeId = this.translationService.createTranslation(hintTextId, challengeStepMDDto.getStepMD(), "Hint", isoLanguageCode, true);
                hint.setText(this.translationService.getTranslatableAttributeById(attributeId));
                hintId = this.challengeService.updateHint(hint.getId(), hint);
            } else {
                hint = new Hint();
                attributeId = this.translationService.createTranslation(tempAttributeId, challengeStepMDDto.getStepMD(), "Hint", isoLanguageCode, true);
                hint.setText(this.translationService.getTranslatableAttributeById(attributeId));
                hintId = this.challengeService.insertHint(hint);
            }
            hints.add(this.challengeService.getHintById(hintId));
        }

        return hints;
    }

    private ArrayList<Instruction> handleImportInstructions(List<ChallengeStepMDDto> stepMDDtos, String isoLanguageCode) {
        Long tempAttributeId = 0L;
        Long attributeId = 0L;
        String instructionId = null;
        ArrayList<Instruction> instructions = new ArrayList<>();
        for(ChallengeStepMDDto challengeStepMDDto : stepMDDtos) {
            Instruction instruction = this.challengeService.getInstructionById(challengeStepMDDto.getId());
            if(instruction != null) {
                Long instructionTextId = 0L;
                if(instruction.getText() != null) {
                    instructionTextId = instruction.getText().getId();
                }
                attributeId = this.translationService.createTranslation(instructionTextId, challengeStepMDDto.getStepMD(), "Instruction", isoLanguageCode, true);
                instruction.setText(this.translationService.getTranslatableAttributeById(attributeId));
                instructionId = this.challengeService.updateInstruction(instruction.getId(), instruction);
            } else {
                instruction = new Instruction();
                attributeId = this.translationService.createTranslation(tempAttributeId, challengeStepMDDto.getStepMD(), "Instruction", isoLanguageCode, true);
                instruction.setText(this.translationService.getTranslatableAttributeById(attributeId));
                instructionId = this.challengeService.insertInstruction(instruction);
            }
            instructions.add(this.challengeService.getInstructionById(instructionId));
        }

        return instructions;
    }

    private List<ChallengeStepMDDto> getHintStepMDDtos(List<Hint> hints, List<ChallengeStepMDDto> stepMDDtos) {
        List<ChallengeStepMDDto> hintStepMDDtos = new ArrayList<>();
        for(Hint hint : hints) {
            for(ChallengeStepMDDto stepMDDto : stepMDDtos) {
                if(hint.getId().equals(stepMDDto.getId())) {
                    hintStepMDDtos.add(stepMDDto);
                    break;
                }
            }
        }
        return hintStepMDDtos;
    }

    private List<ChallengeStepMDDto> getInstructionStepMDDtos(List<Instruction> instructions, List<ChallengeStepMDDto> stepMDDtos) {
        List<ChallengeStepMDDto> instructionStepMDDtos = new ArrayList<>();
        for(Instruction instruction : instructions) {
            for(ChallengeStepMDDto stepMDDto : stepMDDtos) {
                if(instruction.getId().equals(stepMDDto.getId())) {
                    instructionStepMDDtos.add(stepMDDto);
                    break;
                }
            }
        }
        return instructionStepMDDtos;
    }

    private ArrayList<Section> handleImportSectionsPerLanguage(List<ChallengeSectionMDDto> sectionMDDtos, String isoLanguageCode) {
        Long tempAttributeId = 0L;
        Long attributeId = 0L;
        String sectionId = null;
        ArrayList<Section> sections = new ArrayList<>();
        for(ChallengeSectionMDDto sectionMDDto : sectionMDDtos) {
            Section section = this.challengeService.getSectionById(sectionMDDto.getId());
            if(section != null) {
                Long sectionTextId = 0L;
                if(section.getText() != null) {
                    sectionTextId = section.getText().getId();
                }
                attributeId = this.translationService.createTranslation(sectionTextId, sectionMDDto.getSectionMD(), "Section", isoLanguageCode, true);
                section.setText(this.translationService.getTranslatableAttributeById(attributeId));
                sectionId = this.challengeService.updateSection(section.getId(), section);
            } else {
                section = new Section();
                attributeId = this.translationService.createTranslation(tempAttributeId, sectionMDDto.getSectionMD(), "Section", isoLanguageCode, true);
                section.setText(this.translationService.getTranslatableAttributeById(attributeId));
                sectionId = this.challengeService.insertSection(section);
            }

            Section savedSection = this.challengeService.getSectionById(sectionId);
            handleImportInstructions(getInstructionStepMDDtos(savedSection.getInstructions(), sectionMDDto.getSteps()), isoLanguageCode);
            handleImportHints(getHintStepMDDtos(savedSection.getHints(), sectionMDDto.getSteps()), isoLanguageCode);
            sections.add(savedSection);
        }

        return sections;
    }
    private ArrayList<Section> handleImportSections(List<LanguageComponentDto> languageComponentDtos) {
        Long tempAttributeId = 0L;
        Long attributeId = 0L;
        HashSet<String> sectionIds = new HashSet<>();
        ArrayList<Section> sections = new ArrayList<>();
        for(LanguageComponentDto languageComponentDto : languageComponentDtos) {
            handleImportSectionsPerLanguage(languageComponentDto.getSections(), languageComponentDto.getIsoCode());
        }

        for(LanguageComponentDto languageComponentDto : languageComponentDtos) {
            for(ChallengeSectionMDDto sectionMDDto : languageComponentDto.getSections()) {
                sectionIds.add(sectionMDDto.getId());
            }
        }

        for(String sectionId : sectionIds) {
            sections.add(this.challengeService.getSectionById(sectionId));
        }
        return sections;
    }

    private ArrayList<ChallengeUsage> handleImportUsages(ArrayList<String> usages) {
        ArrayList<ChallengeUsage> usageArrayList = new ArrayList<>();
        for(String importedUsage : usages) {
            ChallengeUsage usage = this.challengeService.getUsageByText(importedUsage);
            if(usage != null) {
                usageArrayList.add(usage);
            } else {
                Long usageId = this.challengeService.createUsage(importedUsage);
                usageArrayList.add(this.challengeService.getUsageById(usageId));
            }
        }
        return usageArrayList;
    }

    private ArrayList<String> handleExportUsages(List<ChallengeUsage> usages) {
        ArrayList<String> usageTexts = new ArrayList<>();
        for(ChallengeUsage usageToExport : usages) {
            if(usageToExport.getText() != null) {
                String usage = this.translationService.getTranslationByAttributeId(usageToExport.getText().getId(), "en");
                usageTexts.add(usage);
            }
        }
        return usageTexts;
    }

    private TranslatableAttribute createTitleTranslatableAttribute(String challengeId, List<LanguageComponentDto> languageComponentDtos) {
        Long attributeId = 0L;
        for(LanguageComponentDto languageComponentDto : languageComponentDtos) {
            TranslatableAttribute translatableAttribute = null;
            if(attributeId == 0L) {
                translatableAttribute = this.challengeService.getTitlePerLanguageByChallengeId(challengeId, languageComponentDto.getIsoCode());
            } else {
                translatableAttribute = this.challengeService.getTitleByAttributeId(attributeId);
            }
            if(translatableAttribute == null) {
                attributeId = this.translationService.createTranslation(attributeId, languageComponentDto.getTitleMD(), "Title", languageComponentDto.getIsoCode(), true);
            } else {
                attributeId = this.translationService.createTranslation(translatableAttribute.getId(), languageComponentDto.getTitleMD(), "Title", languageComponentDto.getIsoCode(), true);
            }
        }
        return this.translationService.getTranslatableAttributeById(attributeId);
    }

    private MediaReference createChallengeMediaReference(ChallengeVersion version, List<LanguageComponentDto> languageComponentDtos) {
        Long mediaReferenceId = 0L;
        for(LanguageComponentDto languageComponentDto : languageComponentDtos) {
            if(languageComponentDto.getTitlePNG() != null && !languageComponentDto.getTitlePNG().equals("")) {
                String challengeId = version.getChallenge() != null ? version.getChallenge().getId() : UUID.randomUUID().toString();
                mediaReferenceId = this.mediaService.createMediaEntry(languageComponentDto.getTitlePNG(), "media\\png\\"+ challengeId +"title.png", languageComponentDto.getIsoCode());
            }
        }

        return this.mediaService.getMediaReferenceById(mediaReferenceId);
    }

    private String createChallengeByImportData(FatChallengeDto fatChallengeDto) {
        Challenge challenge = this.challengeService.getChallengeById(fatChallengeDto.getChallenge().getId());
        ChallengeVersion challengeVersion = this.challengeService.getChallengeVersionByChallengeId(fatChallengeDto.getChallenge().getId());
        if(challenge == null) {
            challenge = new Challenge();
        }
        if(challengeVersion == null) {
            challengeVersion = new ChallengeVersion();
        }
        challenge.setId(fatChallengeDto.getChallenge().getId());
        challenge.setPrivate(!fatChallengeDto.getChallenge().isPublic());
        challenge.setGoldnuggetType(fatChallengeDto.getChallenge().getGoldnuggetType());
        challenge.setLastGitCommit(fatChallengeDto.getChallenge().getLastGitCommit());
        challenge.setLastUpdate(TimestampUtil.getTimestamp(fatChallengeDto.getChallenge().getLastUpdate()));
        challenge.setType(handleImportType(fatChallengeDto.getChallenge().getType()));
        challenge.setCategories(handleImportCategories(fatChallengeDto.getChallenge().getCategories()));
        challenge.setKeywords(handleImportKeywords(fatChallengeDto.getChallenge().getKeywords()));
        challenge.setUsages(handleImportUsages(fatChallengeDto.getChallenge().getUsages()));
        challenge.setChallengeResources(handleImportResources(fatChallengeDto.getChallenge().getResources()));
        if(this.challengeService.getChallengeById(fatChallengeDto.getChallenge().getId()) == null) {
            this.challengeService.insertChallenge(challenge);
        } else {
            this.challengeService.updateChallenge(challenge.getId(), challenge);
        }

        challengeVersion.setChallenge(this.challengeService.getChallengeById(fatChallengeDto.getChallenge().getId()));
        challengeVersion.setAuthor(fatChallengeDto.getChallenge().getAuthor());
        challengeVersion.setCreated(TimestampUtil.getTimestamp(fatChallengeDto.getChallenge().getCreated()));
        challengeVersion.setName(fatChallengeDto.getChallenge().getName());
        challengeVersion.setChallengeLevel(handleImportLevel(fatChallengeDto.getChallenge().getLevel()));
        challengeVersion.setStaticGoldnuggetSecret(fatChallengeDto.getChallenge().getStaticGoldnuggetSecret());
        Long challengeVersionId = 0L;
        if(this.challengeService.getChallengeVersionByChallengeId(fatChallengeDto.getChallenge().getId())== null) {
            challengeVersionId = this.challengeService.insertChallengeVersion(challengeVersion);
        } else {
            challengeVersionId = this.challengeService.updateChallengeVersion(challengeVersion.getId(), challengeVersion);
        }

        ChallengeVersion savedChallengeVersion = this.challengeService.getChallengeVersionById(challengeVersionId);

        savedChallengeVersion.setAnAbstract(handleImportAbstract(fatChallengeDto.getChallenge().getId(), fatChallengeDto.getLanguageComponents()));
        savedChallengeVersion.setSolution(handleImportSolution(fatChallengeDto.getChallenge().getId(), fatChallengeDto.getLanguageComponents()));
        savedChallengeVersion.setTitle(createTitleTranslatableAttribute(fatChallengeDto.getChallenge().getId(), fatChallengeDto.getLanguageComponents()));
        handleImportSections(savedChallengeVersion, fatChallengeDto.getChallenge().getSections());
        handleImportSections(fatChallengeDto.getLanguageComponents());
        for(LanguageComponentDto languageComponent : fatChallengeDto.getLanguageComponents())
            this.challengeService.createMediaReferencesFromChallengeVersion(savedChallengeVersion, languageComponent.getIsoCode());
        this.challengeService.updateChallengeVersion(savedChallengeVersion.getId(), savedChallengeVersion);
        savedChallengeVersion = this.challengeService.getChallengeVersionById(savedChallengeVersion.getId());
        savedChallengeVersion.setMediaReference(createChallengeMediaReference(savedChallengeVersion, fatChallengeDto.getLanguageComponents()));
        this.challengeService.updateChallengeVersion(savedChallengeVersion.getId(), savedChallengeVersion);
        if(this.challengeService.getChallengeById(fatChallengeDto.getChallenge().getId()) != null) {
            return fatChallengeDto.getChallenge().getId();
        }
        return "";
    }

    public ChallengeReferenceDto createChallengeDto(Challenge challenge) {
        Long challengeLevelId = -1L;
        Long challengeTypeId = -1L;
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        if(challengeVersion == null) {
            return null;
        }

        if(challengeVersion.getChallengeLevel() != null) {
            challengeLevelId = challengeVersion.getChallengeLevel().getId();
        }

        if(challenge.getType() != null) {
            challengeTypeId = challenge.getType().getId();
        }
        return new ChallengeReferenceDto(challenge.getId(),
                challengeVersion.getName(),
                challengeLevelId,
                this.translationService.getTranslationByAttributeId(challengeTypeId, "en"),
                this.handleExportUsages(challenge.getUsages()),
                this.handleExportKeywords(challenge.getKeywords()),
                this.handleExportCategories(challenge.getCategories()),
                TimestampUtil.getString(challengeVersion.getCreated()),
                challengeVersion.getAuthor(),
                TimestampUtil.getString(challenge.getLastUpdate()),
                challenge.getLastGitCommit(),
                this.handleExportResources(challenge.getChallengeResources()),
                challenge.getGoldnuggetType(),
                challengeVersion.getStaticGoldnuggetSecret(),
                !challenge.isPrivate(),
                this.handleExportSections(challengeVersion.getSections()));
    }

    private ArrayList<ChallengeSectionMDDto> initializeChallengeSectionDtoFromDb(List<Section> sections, String isoCode) {
        ArrayList<ChallengeSectionMDDto> sectionMDDtos = null;
        for(Section section : sections) {
            ChallengeSectionMDDto sectionMDDto = createSectionMDDto(section, isoCode);
            if(sectionMDDto != null) {
                if(sectionMDDtos == null) {
                    sectionMDDtos = new ArrayList<>();
                }
                sectionMDDtos.add(sectionMDDto);
            }
        }

        return sectionMDDtos;
    }

    private ArrayList<TranslatedSectionDto> initializeTranslatedSectionDtoFromDb(List<Section> sections, String isoCode) {
        ArrayList<TranslatedSectionDto> translatedSectionDtos = null;
        for(Section section : sections) {
            TranslatedSectionDto sectionMDDto = createTranslatedSectionDto(section, isoCode);
            if(sectionMDDto != null) {
                if(translatedSectionDtos == null) {
                    translatedSectionDtos = new ArrayList<>();
                }
                translatedSectionDtos.add(sectionMDDto);
            }
        }

        return translatedSectionDtos;
    }

    private ChallengeMediaDto createChallengeMediaDto(MediaReference mediaReference, String languageIsoCode) {
        MediaObject mediaObject = this.mediaService.getMediaObjectByUrlAndLanguage(mediaReference.getUrl(), languageIsoCode);
        if(mediaObject == null)
            return null;
        return new ChallengeMediaDto(mediaReference.getUrl(), mediaObject.getContent());
    }

    private List<ChallengeMediaDto> createChallengeMediaDtos(ChallengeVersion version, String languageIsoCode) {
        List<ChallengeMediaDto> mediaDtos = new ArrayList<ChallengeMediaDto>();
        for(MediaReference mediaReference : this.challengeService.getMediaReferencesFromChallengeVersion(version)) {
            mediaDtos.add(createChallengeMediaDto(mediaReference, languageIsoCode));
        }
        return mediaDtos;
    }

    private List<ChallengeMediaDto> createSolutionMediaDtos(Solution solution, String languageIsoCode) {
        List<ChallengeMediaDto> mediaDtos = new ArrayList<ChallengeMediaDto>();
        if(solution != null) {
            ArrayList<MediaReference> medias = new ArrayList<>();
            List<MediaReference> mediaReferences = this.challengeService.getMediaReferencesForSolution(solution);
            if(mediaReferences != null) {
                medias.addAll(mediaReferences);
                for(MediaReference mediaReference : medias) {
                    mediaDtos.add(createChallengeMediaDto(mediaReference, languageIsoCode));
                }
            }
            return mediaDtos;
        }
        return null;
    }

    private Map<String, ArrayList<ChallengeMediaDto>> initializeMediaContainerMap() {
        Map<String, ArrayList<ChallengeMediaDto>> mediaContainerMap = new HashMap<>();
        mediaContainerMap.put("png", new ArrayList<ChallengeMediaDto>());
        mediaContainerMap.put("jpg", new ArrayList<ChallengeMediaDto>());
        mediaContainerMap.put("jpeg", new ArrayList<ChallengeMediaDto>());
        mediaContainerMap.put("gif", new ArrayList<ChallengeMediaDto>());
        mediaContainerMap.put("svg", new ArrayList<ChallengeMediaDto>());
        mediaContainerMap.put("mpeg", new ArrayList<ChallengeMediaDto>());
        mediaContainerMap.put("wav", new ArrayList<ChallengeMediaDto>());
        mediaContainerMap.put("mp3", new ArrayList<ChallengeMediaDto>());
        mediaContainerMap.put("mp4", new ArrayList<ChallengeMediaDto>());
        return mediaContainerMap;
    }

    private String extractFileTypeFromPath(String path) {
        if(path == null || path.equals(""))
            return null;
        int lastIndex = path.lastIndexOf(".");
        return path.substring(lastIndex + 1);
    }

    private Map<String, ArrayList<ChallengeMediaDto>> fillChallengeMediaMap(List<ChallengeMediaDto> mediaDtos) {
        Map<String, ArrayList<ChallengeMediaDto>> mediaContainerMap = initializeMediaContainerMap();
        for(ChallengeMediaDto mediaDto : mediaDtos) {
            if(mediaDto != null){
                String fileType = extractFileTypeFromPath(mediaDto.getFilePath());
                if(fileType != null)
                    mediaContainerMap.get(fileType).add(mediaDto);
            }
        }

        return mediaContainerMap;
    }

    private ChallengeMediaContainerDto initializeChallengeMediaContainer(ChallengeVersion version, String isoCode) {
        Map<String, ArrayList<ChallengeMediaDto>> mediaContainerMap = initializeMediaContainerMap();
        List<ChallengeMediaDto> mediaDtos = createChallengeMediaDtos(version, isoCode);
        Map<String, ArrayList<ChallengeMediaDto>> map = fillChallengeMediaMap(mediaDtos);
        ChallengeMediaContainerDto mediaContainerDto = new ChallengeMediaContainerDto(
                shouldMediaDtoListBeExported(map.get("png")) ? map.get("png") : null,
                shouldMediaDtoListBeExported(map.get("jpg")) ? map.get("jpg") : null,
                shouldMediaDtoListBeExported(map.get("jpeg")) ? map.get("jpeg") : null,
                shouldMediaDtoListBeExported(map.get("gif")) ? map.get("gif") : null,
                shouldMediaDtoListBeExported(map.get("svg")) ? map.get("svg") : null,
                shouldMediaDtoListBeExported(map.get("mpeg")) ? map.get("mpeg") : null,
                shouldMediaDtoListBeExported(map.get("wav")) ? map.get("wav") : null,
                shouldMediaDtoListBeExported(map.get("mp3")) ? map.get("mp4") : null,
                shouldMediaDtoListBeExported(map.get("mp4")) ? map.get("mp4") : null);
        return mediaContainerDto;
    }

    private ChallengeMediaContainerDto initializeSolutionMediaContainer(ChallengeVersion challengeVersion, String languageIsoCode) {
        Map<String, ArrayList<ChallengeMediaDto>> mediaContainerMap = initializeMediaContainerMap();
        List<ChallengeMediaDto> mediaDtos = createSolutionMediaDtos(challengeVersion.getSolution(), languageIsoCode);
        Map<String, ArrayList<ChallengeMediaDto>> map = fillChallengeMediaMap(mediaDtos);
        ChallengeMediaContainerDto mediaContainerDto = new ChallengeMediaContainerDto(
                shouldMediaDtoListBeExported(map.get("png")) ? map.get("png") : null,
                shouldMediaDtoListBeExported(map.get("jpg")) ? map.get("jpg") : null,
                shouldMediaDtoListBeExported(map.get("jpeg")) ? map.get("jpeg") : null,
                shouldMediaDtoListBeExported(map.get("gif")) ? map.get("gif") : null,
                shouldMediaDtoListBeExported(map.get("svg")) ? map.get("svg") : null,
                shouldMediaDtoListBeExported(map.get("mpeg")) ? map.get("mpeg") : null,
                shouldMediaDtoListBeExported(map.get("wav")) ? map.get("wav") : null,
                shouldMediaDtoListBeExported(map.get("mp3")) ? map.get("mp4") : null,
                shouldMediaDtoListBeExported(map.get("mp4")) ? map.get("mp4") : null);
        return mediaContainerDto;
    }

    private boolean isMediaDtoListEmpty(ArrayList<ChallengeMediaDto> challengeMediaDtos) {
        boolean isMediaDtoEmpty = true;
        for(ChallengeMediaDto mediaDto : challengeMediaDtos) {
            if(mediaDto != null && !mediaDto.getBase64Encoded().equals(""))
                isMediaDtoEmpty = false;
        }

        return isMediaDtoEmpty;
    }

    private boolean shouldMediaDtoListBeExported(ArrayList<ChallengeMediaDto> challengeMediaDtos) {
        if(challengeMediaDtos.size() == 0) {
            return false;
        } else return !isMediaDtoListEmpty(challengeMediaDtos);
    }

    private boolean isMediaDtoContainerEmpty(ChallengeMediaContainerDto mediaContainerDto) {
        if(mediaContainerDto.getPngFiles() != null)
            return false;
        if(mediaContainerDto.getJpgFiles() != null)
            return false;
        if(mediaContainerDto.getJpegFiles() != null)
            return false;
        if(mediaContainerDto.getGifFiles() != null)
            return false;
        if(mediaContainerDto.getSvgFiles() != null)
            return false;
        if(mediaContainerDto.getMpegFiles() != null)
            return false;
        if(mediaContainerDto.getWavFiles() != null)
            return false;
        if(mediaContainerDto.getMp3Files() != null)
            return false;
        return mediaContainerDto.getMp4Files() == null;
    }

    private boolean areAllStepsEmpty(ArrayList<ChallengeStepMDDto> challengeStepMDDtos) {
        for(ChallengeStepMDDto challengeStepMDDto : challengeStepMDDtos) {
            if(challengeStepMDDto.getStepMD() != null && !challengeStepMDDto.getStepMD().equals(""))
                return false;
        }
        return true;
    }

    private boolean areAllSectionsEmpty(ArrayList<ChallengeSectionMDDto> challengeSectionMDDtos) {
        if(challengeSectionMDDtos == null)
            return true;
        for(ChallengeSectionMDDto challengeSectionMDDto : challengeSectionMDDtos) {
            if(challengeSectionMDDto.getSectionMD() != null && !challengeSectionMDDto.getSectionMD().equals(""))
                return false;
            if(!areAllStepsEmpty(challengeSectionMDDto.getSteps()))
                return false;
        }
        return true;
    }

    private boolean isLanguageComponentEmpty(LanguageComponentDto languageComponentDto) {
        if(languageComponentDto == null)
            return true;
        if(languageComponentDto.getAbstractMD() != null && !languageComponentDto.getAbstractMD().equals(""))
            return false;
        if(languageComponentDto.getSolutionMD() != null && !languageComponentDto.getSolutionMD().equals(""))
            return false;
        if(languageComponentDto.getTitleMD() != null && !languageComponentDto.getTitleMD().equals(""))
            return false;
        if(languageComponentDto.getTitlePNG() != null && !languageComponentDto.getTitlePNG().equals(""))
            return false;
        if(!isMediaDtoContainerEmpty(languageComponentDto.getMediaContainer()))
            return false;
        return areAllSectionsEmpty(languageComponentDto.getSections());
    }

    private LanguageComponentDto createLanguageComponentDto(ChallengeVersion version, String isoCode) {
        String mediaContent = null;
        if(version == null)
            return null;
        if(version.getMediaReference() != null) {
            MediaObject object = this.mediaService.getMediaObjectByUrlAndLanguage(version.getMediaReference().getUrl(), isoCode);
            mediaContent = object != null ? object.getContent() : null;
        }
        return new LanguageComponentDto(isoCode,
                version.getTitle() != null ? this.translationService.getTranslationByAttributeId(version.getTitle().getId(), isoCode) : null,
                mediaContent,
                version.getSolution() != null && version.getSolution().getText() != null ? this.translationService.getTranslationByAttributeId(version.getSolution().getText().getId(), isoCode) : null,
                version.getAnAbstract() != null && version.getAnAbstract().getText() != null ? this.translationService.getTranslationByAttributeId(version.getAnAbstract().getText().getId(), isoCode) : null,
                initializeChallengeSectionDtoFromDb(version.getSections(), isoCode),
                initializeChallengeMediaContainer(version, isoCode));
    }

    public ArrayList<LanguageComponentDto> createLanguageComponentDtos(Challenge challenge, ChallengeVersion version) {
        ArrayList<LanguageComponentDto> languageComponentDtos = new ArrayList<LanguageComponentDto>();
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        for(String isoCode : this.translationService.getAvailableIsoCodes()) {
            LanguageComponentDto languageComponentDto  = createLanguageComponentDto(version, isoCode);
            if(!isLanguageComponentEmpty(languageComponentDto))
                languageComponentDtos.add(languageComponentDto);
        }

        return languageComponentDtos;
    }

    private void insertMediaFilesOnDB(ChallengeMediaContainerDto challengeMediaContainerDto, String isoLangCode) {
        if(challengeMediaContainerDto == null)
            return;
        if(challengeMediaContainerDto.getPngFiles() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getPngFiles()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
        if(challengeMediaContainerDto.getJpgFiles() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getJpgFiles()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
        if(challengeMediaContainerDto.getJpegFiles() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getJpegFiles()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
        if(challengeMediaContainerDto.getGifFiles() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getGifFiles()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
        if(challengeMediaContainerDto.getSvgFiles() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getSvgFiles()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
        if(challengeMediaContainerDto.getWavFiles() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getWavFiles()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
        if(challengeMediaContainerDto.getMpegFiles() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getMpegFiles()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
        if(challengeMediaContainerDto.getMp3Files() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getMp3Files()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
        if(challengeMediaContainerDto.getMp4Files() != null) {
            for(ChallengeMediaDto mediaDto : challengeMediaContainerDto.getMp4Files()) {
                this.mediaService.createMediaEntry(mediaDto.getBase64Encoded(), mediaDto.getFilePath(), isoLangCode);
            }
        }
    }

    private void importMediaFiles(List<LanguageComponentDto> languageComponentDtos) {
        for(LanguageComponentDto languageComponentDto : languageComponentDtos) {
            if(languageComponentDto != null)
                insertMediaFilesOnDB(languageComponentDto.getMediaContainer(), languageComponentDto.getIsoCode());
        }
    }

    public String importChallenge(FatChallengeDto fatChallengeDto) {
        importMediaFiles(fatChallengeDto.getLanguageComponents());
        return this.createChallengeByImportData(fatChallengeDto);
    }

    private FatChallengeDto initializeFatChallengeByChallenge(Challenge challenge) {
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        return new FatChallengeDto(createChallengeDto(challenge),createLanguageComponentDtos(challenge, challengeVersion) );
    }

    private FatChallengeDto initializeFatChallengePerLanguage(Challenge challenge, String languageIsoCode) {
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        if(challengeVersion == null) {
            return null;
        }
        ArrayList<LanguageComponentDto> languageComponentDtos = new ArrayList<LanguageComponentDto>();
        languageComponentDtos.add(createLanguageComponentDto(challengeVersion, languageIsoCode));
        return new FatChallengeDto(createChallengeDto(challenge), languageComponentDtos );
    }

    private SolutionMediaDto initializeSolutionMediaDtoPerLanguage(Challenge challenge, String languageIsoCode) {
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        if(challengeVersion == null) {
            return null;
        }
        String solutionText = challengeVersion.getSolution() != null && challengeVersion.getSolution().getText() != null ? this.translationService.getTranslationByAttributeId(challengeVersion.getSolution().getText().getId(), languageIsoCode) : null;

        return new SolutionMediaDto(solutionText, initializeSolutionMediaContainer(challengeVersion, languageIsoCode));
    }

    public TranslatedChallengeDto initializeTranslatedChallenge(Challenge challenge, String languageIsoCode) {
        ChallengeVersion challengeVersion = challenge.getNewestChallengeVersion();
        if(challengeVersion == null) {
            return null;
        }

        String translatedTitle = challengeVersion.getTitle() != null ? this.translationService.getTranslationByAttributeId(challengeVersion.getTitle().getId(), languageIsoCode) : "";
        boolean isTitleUserCreated = challengeVersion.getTitle() != null ? this.translationService.isTranslationUserCreated(challengeVersion.getTitle().getId(), languageIsoCode) : false;
        String translatedSolution = challengeVersion.getSolution() != null && challengeVersion.getSolution().getText() != null ? this.translationService.getTranslationByAttributeId(challengeVersion.getSolution().getText().getId(), languageIsoCode) : "";
        boolean isSolutionUserCreated = challengeVersion.getSolution() != null && challengeVersion.getSolution().getText() != null ? this.translationService.isTranslationUserCreated(challengeVersion.getSolution().getText().getId(), languageIsoCode) : false;
        String translatedAbstract = challengeVersion.getAnAbstract() != null && challengeVersion.getAnAbstract().getText() != null ? this.translationService.getTranslationByAttributeId(challengeVersion.getAnAbstract().getText().getId(), languageIsoCode) : "";
        boolean isAbstractUserCreated = challengeVersion.getAnAbstract() != null && challengeVersion.getAnAbstract().getText() != null ? this.translationService.isTranslationUserCreated(challengeVersion.getAnAbstract().getText().getId(), languageIsoCode) : false;
        return new TranslatedChallengeDto(challenge.getId(), languageIsoCode, new TranslatedTitleDto(isTitleUserCreated, translatedTitle), new TranslatedAbstractDto(isAbstractUserCreated, translatedAbstract), new TranslatedSolutionDto(isSolutionUserCreated, translatedSolution), initializeTranslatedSectionDtoFromDb(challengeVersion.getSections(), languageIsoCode));
    }

    public FatChallengeDto getChallengeForExport(String challengeId, String languageIsoCode) {
        Challenge challenge = this.challengeService.getChallengeById(challengeId);
        if(challenge == null)
            return null;
        if(languageIsoCode != null && !languageIsoCode.equals(""))
            return this.initializeFatChallengePerLanguage(challenge, languageIsoCode);
        return this.initializeFatChallengeByChallenge(challenge);
    }

    public SolutionMediaDto getSolutionForExport(String challengeId, String languageIsoCode) {
        Challenge challenge = this.challengeService.getChallengeById(challengeId);
        if(challenge == null)
            return null;
        if(languageIsoCode != null && !languageIsoCode.equals("")) {
            return initializeSolutionMediaDtoPerLanguage(challenge, languageIsoCode);
        } else {
            return initializeSolutionMediaDtoPerLanguage(challenge, "en");
        }
    }

    public ArrayList<FatChallengeDto> getChallengesForExport(String languageIsoCode) {
        ArrayList<FatChallengeDto> challengeDtos = new ArrayList<>();
        List<Challenge> challenges = this.challengeService.getChallenges();
        for(Challenge challenge : challenges) {
            if(challenge == null)
                return null;
            if(languageIsoCode != null && !languageIsoCode.equals(""))
                challengeDtos.add(this.initializeFatChallengePerLanguage(challenge, languageIsoCode));
            else
                challengeDtos.add(this.initializeFatChallengeByChallenge(challenge));
        }
        return challengeDtos;
    }

    public TranslatedChallengeDto getChallengeTranslationForExport(String challengeId, String languageIsoCode) {
        Challenge challenge = this.challengeService.getChallengeById(challengeId);
        if(challenge == null)
            return null;
        if(languageIsoCode != null && !languageIsoCode.equals(""))
            return this.initializeTranslatedChallenge(challenge, languageIsoCode);
        return null;
    }
}
