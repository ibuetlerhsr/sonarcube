function compareOrderedMarkdownItems(a, b) {
    if (a.order < b.order) {
        return -1;
    }
    if (a.order > b.order) {
        return 1;
    }
    return 0;
}

function getStepById(stepId, steps) {
    let stepFound = null;
    if(steps === null || steps === undefined) {
        return null;
    }
    steps.forEach(step => {
        if(step.id === stepId) {
            stepFound = step;
            return;
        }
    });
    return stepFound;
}

function extractStepText(stepReferences, steps) {
    const additionalStepReferences = [];
    if(stepReferences === null || stepReferences === undefined) {
        return null;
    }
    stepReferences.forEach(stepReference => {
        const step = getStepById(stepReference.id, steps);
        if(step !== undefined && step !== null) {
            additionalStepReferences.push({
                id: step.id,
                stepMD: step.stepMD,
                order: stepReference.order,
                show: true,
                type: stepReference.type
            });
        }
    });
    additionalStepReferences.sort(compareOrderedMarkdownItems);
    return additionalStepReferences;
}

function getSectionById(sectionId, sections) {
    let sectionFound = null;
    if(sections === null || sections === undefined) {
        return null;
    }
    sections.forEach(section => {
        if(section.id === sectionId) {
            sectionFound = section;
            return;
        }
    });
    return sectionFound;
}

export function extractSectionReferences(sections) {
    const sectionReferences = [];
    let index = 0;
    if(sections === null || sections === undefined) {
        return null;
    }
    while(sections.length > index) {
        sectionReferences.push({id: sections[index].id, order: sections[index].order, steps: sections[index].steps});
        index++;
    }
    sectionReferences.sort(compareOrderedMarkdownItems);
    return sectionReferences;
}

export function extractSectionText(sectionReferences, sections) {
    const additionalSectionReferences = [];
    if(sectionReferences === null || sectionReferences === undefined) {
        return null;
    }
    sectionReferences.forEach(sectionReference => {
        const section = getSectionById(sectionReference.id, sections);
        if(section !== undefined && section !== null) {
            additionalSectionReferences.push({id: section.id, sectionMD: section.sectionMD, show: true, order: sectionReference.order, steps: extractStepText(sectionReference.steps, section.steps)});
        }
    });
    additionalSectionReferences.sort(compareOrderedMarkdownItems);
    return additionalSectionReferences;
}

export function fillSectionReferences(sectionReferences, section, stepObject) {
    let newSectionReferences = [];
    if(sectionReferences !== null && sectionReferences !== undefined && sectionReferences.length === 0) {
        if(stepObject !== null) {
            newSectionReferences.push({id: section, show: true, order: 0, steps: [{id: stepObject.id, show: true, order: stepObject.order, type: stepObject.type}]});
        } else {
            newSectionReferences.push({id: section, show: true, order: 0, steps: []});
        }
    } else if(sectionReferences !== null && sectionReferences !== undefined) {
        if(stepObject !== null) {
            sectionReferences.forEach(sectionReference =>  {
                if(sectionReference.id === section) {
                    sectionReference.steps[sectionReference.steps.length] = {id: stepObject.id, show: true, order: stepObject.order, type: stepObject.type};
                }
            });
        } else {
            sectionReferences[sectionReferences.length] = {id: section, show: true, order: sectionReferences.length, steps: []};
        }
        newSectionReferences = sectionReferences;
    }
    newSectionReferences.sort(compareOrderedMarkdownItems);
    return newSectionReferences;
}

export function fillSectionReferencesWithShow(sectionReferences, id, show) {
    let found = false;
    if(sectionReferences !== null && sectionReferences !== undefined) {
        sectionReferences.forEach(function(value, index) {
            if(value.id === id) {
                found = true;
                sectionReferences[index] = {id: value.id, show: show, order: value.order, steps: value.steps};
            }
        });
        if(!found) {
            sectionReferences.forEach(sectionReference =>  {
                if(sectionReference.steps !== null && sectionReference.steps !== undefined && sectionReference.steps.length > 0) {
                    const step = getStepById(id, sectionReference.steps);
                    if(step !== null) {
                        sectionReference.steps.forEach(function(v, i) {
                            if(v.id === id) {
                                sectionReference.steps[i] = {id: v.id, show: show, order: v.order, type: v.type};
                            }
                        });
                    }
                }
            });
        }
    }
    sectionReferences.sort(compareOrderedMarkdownItems);
    return sectionReferences;
}

function extractCodeFragments(text) {
    let index = 0;
    const array = [];
    let firstIndex = 0;
    let secondIndex = 0;

    while(text.indexOf('```', secondIndex) >= 0) {
        firstIndex = text.indexOf('```', secondIndex);
        secondIndex = text.indexOf('```', firstIndex + 3);
        secondIndex = secondIndex + 3;
        array[index] = text.substr(firstIndex, secondIndex  - firstIndex);
        index++;
    }

    return array;
}

function extractImages(text) {
    let index = 0;
    const array = [];
    let firstIndex = 0;
    let secondIndex = 0;

    while(text.indexOf('](', secondIndex) >= 0) {
        firstIndex = text.indexOf('](', secondIndex);
        secondIndex = text.indexOf(')', firstIndex + 2);
        array[index] = text.substr(firstIndex + 1, secondIndex - firstIndex);

        index++;
    }

    return array;
}

function extractLinks(text) {
    let index = 0;
    const array = [];
    let firstIndex = 0;
    let secondIndex = 0;

    while(text.indexOf('https://', secondIndex) >= 0) {
        firstIndex = text.indexOf('https://', secondIndex);
        secondIndex = text.indexOf(' ', firstIndex + 8);
        array[index] = text.substr(firstIndex, secondIndex - firstIndex);
        index++;
    }
    firstIndex = 0;
    secondIndex = 0;
    while(text.indexOf('http://', secondIndex) >= 0) {
        firstIndex = text.indexOf('http://', secondIndex);
        secondIndex = text.indexOf(' ', firstIndex + 7);
        array[index] = text.substr(firstIndex, secondIndex - firstIndex);
        index++;
    }

    return array;
}

export function extractNonTranslatableParts(text) {
    let replaceArray = [];
    replaceArray = replaceArray.concat(extractCodeFragments(text));
    replaceArray = replaceArray.concat(extractImages(text));
    replaceArray = replaceArray.concat(extractLinks(text));
    let replacedText = text;
    let index = 0;
    replaceArray.forEach(item => {
        replacedText = replacedText.replace(item, '-[' + index + ']-');
        index++;
    });

    return {'text': replacedText, 'array': replaceArray};
}

export function fillTranslatedText(translatedText, array) {
    let filledText = translatedText;
    let index = 0;
    if(array === null || array === undefined) {
        return null;
    }
    array.forEach(item => {
        filledText = filledText.replace('-[' + index + ']-', item);
        index++;
    });

    return filledText;
}

export function fillSectionTranslation(sectionTranslations, section) {
    const sectionTranslationArray = sectionTranslations;
    sectionTranslationArray.push(section);
    return sectionTranslationArray;
}

export function fillStepTranslation(stepTranslations, step) {
    const stepTranslationArray = stepTranslations;
    stepTranslationArray.push(step);
    return stepTranslationArray;
}

export function parseJsonData(valueType, jsonData) {
    if(jsonData === undefined) {
        return '';
    }
    const data = JSON.parse(jsonData);
    if(data === undefined) {
        return '';
    }
    if(valueType === '_id') {
        if(data._id !== '' && data._id !== undefined && data._id !== 'null') {
            return data._id;
        }
        return '';
    }
    if(valueType === 'title') {
        if(data.challengeTitle !== '' && data.challengeTitle !== undefined && data.challengeTitle !== 'null') {
            return data.challengeTitle;
        }
        return '';
    }

    if(valueType === 'name') {
        if(data.challengeName !== '' && data.challengeName !== undefined && data.challengeName !== 'null') {
            return data.challengeName;
        }
        return '';
    }

    if(valueType === 'level') {
        if(data.challengeLevel !== '' && data.challengeLevel !== undefined && data.challengeLevel !== 'null') {
            return data.challengeLevel;
        }
        return '';
    }
    if(valueType === 'usage') {
        if(data.challengeUsage !== '' && data.challengeUsage !== undefined && data.challengeUsage !== 'null') {
            return data.challengeUsage;
        }
        return '';
    }
    if(valueType === 'goldnuggetType') {
        if(data.goldnuggetType !== '' && data.goldnuggetType !== undefined && data.goldnuggetType !== 'null') {
            return data.goldnuggetType;
        }
        return '';
    }
    if(valueType === 'staticGoldnuggetSecret') {
        if(data.staticGoldnuggetSecret !== '' && data.staticGoldnuggetSecret !== undefined && data.staticGoldnuggetSecret !== 'null') {
            return data.staticGoldnuggetSecret;
        }
        return '';
    }
    if(valueType === 'isPrivate') {
        if(data.isPrivate !== '' && data.isPrivate !== undefined && data.isPrivate !== 'null') {
            return data.isPrivate;
        }
        return '';
    }
    if(valueType === 'type') {
        if(data.challengeType !== '' && data.challengeType !== undefined && data.challengeType !== 'null') {
            return data.challengeType;
        }
        return '';
    }
    if(valueType === 'categories') {
        if(data.challengeCategories !== '' && data.challengeCategories !== undefined && data.challengeCategories !== 'null') {
            return data.challengeCategories;
        }
        return '';
    }
    return '';
}

function dataURLtoFile(dataUrl, filename) {
    const arr = dataUrl.split(',');
    const mime = arr[0].match(/:(.*?);/)[1];
    const bstr = atob(arr[1]);
    let n = bstr.length, u8arr = new Uint8Array(n); // eslint-disable-line
    while(n > 0) {
        n--;
        u8arr[n] = bstr.charCodeAt(n);
    }
    return new File([u8arr], filename, {type: mime});
}

export function getSpecificValueFromFatChallenge(fieldName, fatChallenge) {
    if(fatChallenge === undefined || fatChallenge === null) {
        return null;
    }
    if(fatChallenge.languageComponents === undefined || fatChallenge.languageComponents === null || fatChallenge.languageComponents.length !== 1 || fatChallenge.challengeJson === undefined || fatChallenge.challengeJson === null) {
        return null;
    }
    if(fieldName === 'challengeId') {
        return fatChallenge.challengeJson.id;
    }
    if(fieldName === 'abstract') {
        return fatChallenge.languageComponents[0].abstractMD;
    }
    if(fieldName === 'solution') {
        return fatChallenge.languageComponents[0].solutionMD;
    }
    if(fieldName === 'sectionReferences') {
        return extractSectionText(extractSectionReferences(fatChallenge.challengeJson.sections), fatChallenge.languageComponents[0].sections);
    }
    if(fieldName === 'title') {
        return fatChallenge.languageComponents[0].titleMD;
    }
    if(fieldName === 'titlePNG') {
        // return base64ToFileObject(fatChallenge.languageComponents[0].titlePNG, 'title.png', 'image/png');
        return dataURLtoFile('data:image/png;base64,' + fatChallenge.languageComponents[0].titlePNG, 'title.png');
        // return fatChallenge.languageComponents[0].titlePNG;
    }
    if(fieldName === 'abstract') {
        return fatChallenge.languageComponents[0].abstractMD;
    }
    if(fieldName === 'solution') {
        return fatChallenge.languageComponents[0].solutionMD;
    }
    if(fieldName === 'sections') {
        return fatChallenge.languageComponents[0].sections;
    }
    if(fieldName === 'name') {
        return fatChallenge.challengeJson.name;
    }
    if(fieldName === 'level') {
        return fatChallenge.challengeJson.level;
    }
    if(fieldName === 'type') {
        return fatChallenge.challengeJson.type;
    }
    if(fieldName === 'usages') {
        return fatChallenge.challengeJson.usages;
    }
    if(fieldName === 'keywords') {
        return fatChallenge.challengeJson.keywords;
    }
    if(fieldName === 'categories') {
        return fatChallenge.challengeJson.categories;
    }
    if(fieldName === 'goldnuggetType') {
        return fatChallenge.challengeJson['goldnugget-type'];
    }
    if(fieldName === 'staticGoldnuggetSecret') {
        return fatChallenge.challengeJson['goldnugget-secret-static'];
    }
    if(fieldName === 'isPrivate') {
        return (!fatChallenge.challengeJson.public).toString();
    }
    return null;
}

function fillSelectedMetadataFieldsWithAvailableValues(availableMetadataList, metadataList) {
    const newMetadataStateList = [];
    if(availableMetadataList !== undefined && metadataList !== undefined && availableMetadataList.length > 0 && metadataList.length > 0) {
        availableMetadataList.forEach(metadataValue => {
            const index = metadataList.indexOf(metadataValue.text);
            if(index > -1) {
                newMetadataStateList.push(metadataValue.text);
            } else {
                newMetadataStateList.push(null);
            }
        });

        return newMetadataStateList;
    }
    return undefined;
}

export function fillSelectedChallengeUsages(actionSelectedUsages, stateSelectedUsages, actionChallengeUsages, stateChallengeUsages, fatChallenge) {
    if(actionSelectedUsages !== undefined) {
        if(stateChallengeUsages.length > 0) {
            return actionSelectedUsages;
        }
        return fillSelectedMetadataFieldsWithAvailableValues(stateChallengeUsages, actionSelectedUsages);
    }
    if(getSpecificValueFromFatChallenge('usages', fatChallenge) === null) {
        if(fillSelectedMetadataFieldsWithAvailableValues(actionChallengeUsages, stateSelectedUsages) === undefined) {
            return stateSelectedUsages;
        }
        return fillSelectedMetadataFieldsWithAvailableValues(actionChallengeUsages, stateSelectedUsages);
    }
    return getSpecificValueFromFatChallenge('usages', fatChallenge);
}

function getSelectedCategoryInAvailableCategories(selectedCategory, availableCategories) {
    let selectedCategoryObject = null;
    if(availableCategories === null || availableCategories === undefined) {
        return null;
    }
    availableCategories.forEach(availableCategory => {
        if(availableCategory.categoryName === selectedCategory) {
            selectedCategoryObject = availableCategory;
        }
    });
    return selectedCategoryObject;
}

function fillSelectedCategoriesWithAvailableValues(availableCategories, categoryList) {
    const selectedCategories = [];
    if(availableCategories !== undefined && categoryList !== undefined && availableCategories.length > 0 && categoryList.length > 0) {
        categoryList.forEach(category => {
            selectedCategories.push(getSelectedCategoryInAvailableCategories(category, availableCategories));
        });

        return selectedCategories;
    }
    return undefined;
}

export function fillSelectedCategories(actionSelectedCategories, stateSelectedCategories, actionCategories, stateCategories, fatChallenge) {
    if(actionSelectedCategories !== undefined) {
        if(stateCategories.length > 0) {
            return actionSelectedCategories;
        }
        return fillSelectedCategoriesWithAvailableValues(stateCategories, actionSelectedCategories);
    }
    if(getSpecificValueFromFatChallenge('categories', fatChallenge) === null) {
        if(fillSelectedCategoriesWithAvailableValues(actionCategories, stateSelectedCategories) === undefined) {
            return stateSelectedCategories;
        }
        return fillSelectedCategoriesWithAvailableValues(actionCategories, stateSelectedCategories);
    }
    return getSpecificValueFromFatChallenge('categories', fatChallenge);
}

function fillSelectedKeywordObjects(keywordList) {
    const selectedKeywords = [];
    if(keywordList !== undefined && keywordList.length > 0) {
        keywordList.forEach(keyword => {
            selectedKeywords.push({text: keyword.text});
        });

        return selectedKeywords;
    }
    return undefined;
}

function fillSelectedKeywordObjectsByArray(keywordList) {
    const selectedKeywords = [];
    if(keywordList !== undefined && keywordList.length > 0) {
        keywordList.forEach(keyword => {
            selectedKeywords.push({text: keyword});
        });

        return selectedKeywords;
    }
    return undefined;
}

export function fillSelectedKeywords(actionSelectedKeywords, stateSelectedKeywords, actionChallengeKeywords, stateChallengeKeywords, fatChallenge) {
    if(actionSelectedKeywords !== undefined) {
        if(stateChallengeKeywords.length > 0) {
            return actionSelectedKeywords;
        }
        return fillSelectedKeywordObjects(actionSelectedKeywords);
    }
    if(getSpecificValueFromFatChallenge('keywords', fatChallenge) === null) {
        if(fillSelectedKeywordObjects(stateSelectedKeywords) === undefined) {
            return stateSelectedKeywords;
        }
        return fillSelectedKeywordObjects(stateSelectedKeywords);
    }
    return fillSelectedKeywordObjectsByArray(getSpecificValueFromFatChallenge('keywords', fatChallenge));
}
