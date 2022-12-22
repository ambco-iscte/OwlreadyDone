let currentAntecedentTermNumber = 1;
let currentConsequentTermNumber = 1;

function antecedentAddNewTermClicked(clickedElementID, ontoClasses, ontoIndividuals, ontoRelations) {
    if (clickedElementID.toString().startsWith("queryBuilderAntecedentAddTermButton")) {
        let elem = document.getElementById(clickedElementID);
        let term = elem.parentElement;
        let section = term.parentElement;
        let andText = elem.previousElementSibling;
        switch (andText.style.display) {
            case "block": {   // AND is visible, disable the current term.
                term.remove();
                break;
            }
            case "none": {  // AND is not visible, make visible and add new term.
                elem.innerText = "-";
                andText.style.display = "block";
                section.appendChild(createBlankAntecedentTerm(++currentAntecedentTermNumber, ontoClasses, ontoIndividuals, ontoRelations));
                break;
            }
        }
    }
}

function consequentAddNewTermClicked(clickedElementID, builtInNames) {
    if (clickedElementID.toString().startsWith("queryBuilderConsequentAddTermButton")) {
        let elem = document.getElementById(clickedElementID);
        let term = elem.parentElement;
        let section = term.parentElement;
        let andText = elem.previousElementSibling;2
        switch (andText.style.display) {
            case "block": {   // AND is visible, disable the current term.
                term.remove();
                break;
            }
            case "none": {  // AND is not visible, make visible and add new term.
                elem.innerText = "-";
                andText.style.display = "block";
                section.appendChild(createBlankConsequentTerm(++currentConsequentTermNumber, builtInNames));
                break;
            }
        }
    }
}

function createBlankConsequentTerm(index, builtInNames) {
    let term = document.createElement("section");
    term.classList.add("query-builder-term");

    let inputFieldSection = document.createElement("section");
    inputFieldSection.classList.add("query-builder-term-input-fields");
    term.appendChild(inputFieldSection);

    inputFieldSection.appendChild(createSelectWithOptions("consequentTerm" + index + "-rel", "query-builder-input-select",
        "query-builder-input-select-option", builtInNames));

    inputFieldSection.appendChild(createTextInput("consequentTerm" + index + "-var1", "?var1, ?var2, ...", true, "query-builder-input-text"));

    term.appendChild(createAndText());

    let addIcon = document.createElement("h3");
    addIcon.id = "queryBuilderConsequentAddTermButton-" + index;
    addIcon.classList.add("oxanium-purple", "no-bottom-margin", "highlight-on-hover", "unselectable");
    addIcon.innerText = "+";
    addIcon.onclick = function () {
        consequentAddNewTermClicked(addIcon.id, builtInNames);
    };
    term.appendChild(addIcon);

    return term;
}

/**
 * Creates a blank query builder term element.
 * @param index The index to use for the new element.
 * @param ontoClasses The list of all classes present in the ontology.
 * @param ontoIndividuals The list of all individuals present in the ontology.
 * @param ontoRelations The list of all relations (data properties, object properties, etc.) present in the ontology.
 * @returns {HTMLElement}
 */
function createBlankAntecedentTerm(index, ontoClasses, ontoIndividuals, ontoRelations) {
    let term = document.createElement("section");
    term.classList.add("query-builder-term");

    let inputFieldSection = document.createElement("section");
    inputFieldSection.classList.add("query-builder-term-input-fields");
    term.appendChild(inputFieldSection);

    inputFieldSection.appendChild(createTextInput("antecedentTerm" + index + "-var1", "?var1", true, "query-builder-input-text"));

    inputFieldSection.appendChild(createSelectWithOptions("antecedentTerm" + index + "-rel", "query-builder-input-select",
        "query-builder-input-select-option", ontoRelations));

    let datalist = createDatalist("antecedentTerm" + index + "-datalist-var2", ontoClasses.concat(ontoIndividuals),
        "query-builder-input-select-option");

    inputFieldSection.appendChild(createTextInputWithDatalist("antecedentTerm" + index + "-var2", "?var2", true,
        "query-builder-input-text", datalist));

    inputFieldSection.appendChild(datalist);

    term.appendChild(createAndText());

    let addIcon = document.createElement("h3");
    addIcon.id = "queryBuilderAntecedentAddTermButton-" + index;
    addIcon.classList.add("oxanium-purple", "no-bottom-margin", "highlight-on-hover", "unselectable");
    addIcon.innerText = "+";
    addIcon.onclick = function () {
        antecedentAddNewTermClicked(addIcon.id, ontoClasses, ontoIndividuals, ontoRelations);
    };
    term.appendChild(addIcon);

    return term;
}

/**
 * Is an antecedent term missing any variables?
 * @param var1 First variable of the term.
 * @param rel Relation of the term.
 * @param var2 Second variable of the term.
 */
function isAntecedentTermMissingVariables(var1, rel, var2){
    return constructTerm(var1, rel, var2) === "" || constructTerm(var1, rel, var2).includes("?undefined") || constructTerm(var1, rel, var2).includes("()")
}

/**
 * Is a consequent term missing variables, i.e. is its variable field empty?
 * @param variableElementId The variable field of the Term.
 */
function isConsequentTermMissingVariables(variableElementId){
    return document.getElementById(variableElementId).value === "";
}

/**
 * Cleans the variable, removing unwanted spaces, and formats it correctly. E.g. ?x?z => ?x, ?z
 * @param varXValue The variable field to be cleaned.
 * @returns {string} The cleaned variable.
 */
function cleanQueryShowVariables(varXValue){
    let aux;
    let splitVarFieldByQuestionMarks = varXValue.toString().split('?').map(element => element.trim()).filter(element => element !== '')
    varXValue = ""
    for (let i = 0; i < splitVarFieldByQuestionMarks.length; i++) {
        splitVarFieldByQuestionMarks[i] = splitVarFieldByQuestionMarks[i].replace(" ","")
        if(i !== splitVarFieldByQuestionMarks.length - 1) {
            let cleanInBetweenVariables = splitVarFieldByQuestionMarks[i].split(',').map(element => element.trim()).filter(element => element !== '')
            aux = "?" + cleanInBetweenVariables[0]+", "
        } else
            aux = "?" + splitVarFieldByQuestionMarks[i]
        varXValue+=aux
    }
    return varXValue
}

/**
 * Given the term variables, constructs an antecedent term in SQWRL format and appends it to the query field.
 * @param var1 First variable of the term.
 * @param rel Relation of the term.
 * @param var2 Second variable of the term.
 */
function antecedentShow (var1, rel, var2) {
    let name = constructTerm(var1, rel, var2);
    let queryField = document.getElementById("queryBuilderString");
    let queryBuilderStringValue = queryField.value;
        if (queryBuilderStringValue !== "")
            queryField.value = queryBuilderStringValue + " ^ " + name;
        else
            queryField.value = name;
}

/**
 * Given the term variables, it constructs a Term consequent in SQWRL format and appends it to the queryField in query,jsp
 * @param var1 corresponds to first variable of the consequent
 * @param rel corresponds to second variable of the consequent
 * @param isFirstConsequent checks if the consequent term it's the first, for viewing purpose
 */
function consequentShow(var1, rel, isFirstConsequent){
    let var1Element = document.getElementById(var1);
    if (var1Element === null)
        return '';

    let queryField = document.getElementById("queryBuilderString");
    let var1Value = cleanQueryShowVariables(document.getElementById(var1).value);
    let varRelValue = document.getElementById(rel).value;

    var1Element.value = var1Value;

    let name = varRelValue +"("+ var1Value +")";
    let queryBuilderStringValue = queryField.value;

    if (queryBuilderStringValue !== "") {
        if (isFirstConsequent)
            queryField.value = queryBuilderStringValue + ' -> ' + name;
        else
            queryField.value = queryBuilderStringValue + ' ^ ' + name;
    } else
        queryField.value = name;
}

/**
 * Given the correct variables, creates a SQWRL query term.
 * @param var1 First variable of the term.
 * @param rel Relation of the term.
 * @param var2 Second variable of the term.
 * @returns {string} The given variables in SQWRL query format.
 */
function constructTerm(var1, rel, var2){
    let var1Element = document.getElementById(var1);
    let var2Element = document.getElementById(var2);

    if (var1Element === null || var1Element.value === "")
        return '';

    let var1Value = cleanQueryShowVariables(var1Element.value);
    let varRelValue = document.getElementById(rel).value;
    let var2Value;
    if (var2Element.value.toString().includes('?'))
        var2Value = cleanQueryShowVariables(var2Element.value);
    else
        var2Value = var2Element.value;

    var1Element.value = var1Value;
    var2Element.value = var2Value;

    if (varRelValue.toString() === "is a")
        return var2Value + "(" + var1Value + ")";
    else {
        if (var2Value !== "")
            return varRelValue + "(" + var1Value.toString() + ", " + var2Value.toString() + ")";
        return varRelValue + "(" + var1Value.toString() + ")";
    }
}

/**
 * Cleans the query field.
 */
function cleanQueryField(){
    document.getElementById("queryBuilderString").value = '';
}

/**
 * Constructs a full SQWRL query from all the terms in the query builder interface.
 */
function onRefreshQueryFieldButtonClicked() {
    let isFirstConsequent = true;
    cleanQueryField();

    const var1Inputs = document.querySelectorAll('input[name*="-var1"]');

    for (let i = 0; i < var1Inputs.length; i++) {
        let var1 = var1Inputs[i].id;
        let fieldId = var1Inputs[i].id.split("Term")[1].charAt(0);

        let var1ElementId = "antecedentTerm" + fieldId + "-var1";
        let relElementId = "antecedentTerm" + fieldId + "-rel";
        let var2ElementId = "antecedentTerm" + fieldId + "-var2";

        if (var1.includes("antecedent") && !isAntecedentTermMissingVariables(var1ElementId, relElementId, var2ElementId)) {
            antecedentShow(var1ElementId, relElementId, var2ElementId);
        }
        else if (var1.includes("consequent") && !isConsequentTermMissingVariables(var1)) {
            consequentShow(var1, var1.replace('var1','rel'), isFirstConsequent);
            if (isFirstConsequent)
                isFirstConsequent = false;
        }
    }
}

function createAndText() {
    let andText = document.createElement("h3");
    andText.classList.add("oxanium-purple", "no-bottom-margin", "term-and-text");
    andText.innerText = "and";
    andText.style.display = "none";
    return andText;
}

/**
 * Creates an HTML text input element.
 * @param id The id (and name) to use for the new element.
 * @param placeholder The placeholder text of the input.
 * @param required Is the input required?
 * @param classes The CSS classes to add to the new element.
 * @returns {HTMLInputElement}
 */
function createTextInput(id, placeholder, required, ...classes) {
    let elem = document.createElement("input");
    elem.classList.add(classes);
    elem.type = "text";
    elem.id = id;
    elem.name = id;
    elem.placeholder = placeholder;
    elem.required = required;
    return elem;
}

/**
 * Creates an HTML text input element with a datalist of options.
 * @param id The id (and name) to use for the new element.
 * @param placeholder The placeholder text of the input.
 * @param required Is the input required?
 * @param inputClass The CSS class to use for the text field.
 * @param datalist The datalist element to use.
 * @returns {HTMLInputElement}
 */
function createTextInputWithDatalist(id, placeholder, required, inputClass, datalist) {
    let input = createTextInput(id, placeholder, required, inputClass);
    input.setAttribute("list", datalist.id);
    return input;
}

/**
 * Creates a datalist of options.
 * @param id The ID of the new element to be created.
 * @param options The list of options to include in the datalist.
 * @param optionClass The CSS class to for each datalist child element.
 * @returns {HTMLDataListElement}
 */
function createDatalist(id, options, optionClass) {
    let datalist = document.createElement("datalist");
    datalist.id = id;
    datalist.name = id;
    options.forEach(option => {
        let optionElement = document.createElement("option");
        optionElement.classList.add(optionClass);
        optionElement.value = option;
        optionElement.innerText = option;
        datalist.appendChild(optionElement);
    })
    return datalist;
}

/**
 * Creates an HTML select (dropdown) element.
 * @param id The id (and name) to use for the new element.
 * @param options The list of options to include in the dropdown.
 * @param selectClass The CSS class to use for the input field.
 * @param optionClass The CSS class to use for each option (child) element.
 * @returns {HTMLSelectElement}
 */
function createSelectWithOptions(id, selectClass, optionClass, options) {
    let elem = document.createElement("select");
    elem.classList.add(selectClass);
    elem.id = id;
    elem.name = id;
    options.forEach(option => {
        let optionElement = document.createElement("option");
        optionElement.classList.add(optionClass);
        optionElement.value = option;
        optionElement.innerText = option;
        elem.appendChild(optionElement);
    })
    return elem;
}