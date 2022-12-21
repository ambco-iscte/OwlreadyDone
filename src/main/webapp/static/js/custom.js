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
 * Checks if a given term is missing variables
 * @param var1 corresponds to first variable of the antecedent
 * @param rel corresponds to second variable of the antecedent
 * @param var2 corresponds to third variable of the antecedent
 */
function checkIfMissingAntecedentVariables(var1, rel, var2){
    return constructTerm(var1, rel, var2) === "" || constructTerm(var1, rel, var2).includes("?undefined") || constructTerm(var1, rel, var2).includes("()")
}

/**
 * Checks if a given term is missing variables
 * @param var1Id corresponds to first variable of the consequent
 */
function checkIfMissingConsequentVariables(var1Id){
    return document.getElementById(var1Id).value === "";
}

/**
 * Cleans the variable removing unwanted spaces, and formats it in the correct way, even if the user doesnt, eg., ?x?z => ?x, ?z, ?k                ,           ?a => ?k, ?a
 * @param varXValue The variable field to be cleaned
 * @returns {string} The cleaned variable
 */
function cleanQueryShowVariables(varXValue){
    let aux;
    let splitVarFieldByQuestionMarks = varXValue.toString().split('?').map(element => element.trim()).filter(element => element !== '')
    varXValue = ""
    for (let i = 0; i < splitVarFieldByQuestionMarks.length; i++) {
        splitVarFieldByQuestionMarks[i] = splitVarFieldByQuestionMarks[i].replace(" ","")
        if(i !== splitVarFieldByQuestionMarks.length-1) {
            let cleanInBetweenVariables = splitVarFieldByQuestionMarks[i].split(',').map(element => element.trim()).filter(element => element !== '')
            aux = "?" + cleanInBetweenVariables[0]+", "
        }else
            aux = "?"+splitVarFieldByQuestionMarks[i]
        varXValue+=aux
    }
    return varXValue
}
/**
 * Given the term variables, it constructs a Term antecedent in SQWRL format and appends it to the queryField in query,jsp
 * @param var1 corresponds to first variable of the antecedent
 * @param rel corresponds to second variable of the antecedent
 * @param var2 corresponds to third variable of the antecedent
 */
function antecedentShow (var1, rel, var2) {
    let name = constructTerm(var1, rel, var2);
    let queryBuilderStringValue = document.getElementById("queryBuilderString").value;
        if (queryBuilderStringValue !== ""){
            document.getElementById("queryBuilderString").value = queryBuilderStringValue + " ^ " + name;
        }else
            document.getElementById("queryBuilderString").value = name;
}

/**
 * Given the term variables, it constructs a Term consequent in SQWRL format and appends it to the queryField in query,jsp
 * @param var1 corresponds to first variable of the consequent
 * @param rel corresponds to second variable of the consequent
 * @param isFirstConsequent checks if the consequent term it's the first, for viewing purpose
 */
function consequentShow(var1, rel, isFirstConsequent){
    if (document.getElementById(var1) === null) {
        return '';}
    let var1Value = cleanQueryShowVariables(document.getElementById(var1).value)
    let varRelValue = document.getElementById(rel).value;

    document.getElementById(var1).value = var1Value

    let name = varRelValue +"("+ var1Value +")"
    let queryBuilderStringValue = document.getElementById("queryBuilderString").value;
    if (queryBuilderStringValue !== ""){
        if (isFirstConsequent)
            document.getElementById("queryBuilderString").value = queryBuilderStringValue + '->' + name;
        else
            document.getElementById("queryBuilderString").value = queryBuilderStringValue + '^' + name;
    }else
        document.getElementById("queryBuilderString").value = name;
}

/**
 * Given the variables it creates a SQWRL term
 * @param var1 corresponds to first variable of the antecedent
 * @param rel corresponds to second variable of the antecedent
 * @param var2 corresponds to third variable of the antecedent
 * @returns {string} returns the given variables in the SQWRL query format
 */
function constructTerm(var1, rel, var2){
    if (document.getElementById(var1) === null || document.getElementById(var1).value === "") {
        return '';}
    let var1Value = cleanQueryShowVariables(document.getElementById(var1).value)
    let varRelValue = document.getElementById(rel).value;
    let var2Value
    if(document.getElementById(var2).value.toString().includes('?'))
        var2Value = cleanQueryShowVariables(document.getElementById(var2).value)
    else
        var2Value = document.getElementById(var2).value

    document.getElementById(var1).value = var1Value
    document.getElementById(var2).value = var2Value

    if (varRelValue.toString()==="isA") return var2Value + "(" + var1Value + ")";
    else
        if(var2Value !== "") return varRelValue + "(" + var1Value.toString() + ", " + var2Value.toString() + ")";
        else return varRelValue + "(" + var1Value.toString() + ")";
}
/**
 * cleans the query field
 */
function cleanQueryField(){
    if (document.getElementById("queryBuilderString").value !== "")
        document.getElementById("queryBuilderString").value = '';
}

/**
 * Gets all terms and shows them in the query field, does no verifications, so it lets you add repeated terms!!
 */
function refreshQueryFieldButton() {
    let isFirstConsequent = true;
    cleanQueryField();
    const var1Inputs = document.querySelectorAll('input[name*="-var1"]');
    for (let i = 0; i < var1Inputs.length; i++) {
        let var1 = var1Inputs[i].id
        let fieldId = var1Inputs[i].id.split("Term")[1].charAt(0)
        if (var1.includes("antecedent") && !checkIfMissingAntecedentVariables("antecedentTerm" + fieldId + "-var1", "antecedentTerm" + fieldId + "-rel", "antecedentTerm" + fieldId + "-var2"))
            antecedentShow("antecedentTerm" + fieldId + "-var1", "antecedentTerm" + fieldId + "-rel", "antecedentTerm" + fieldId + "-var2");
        else
            if (var1.includes("consequent") && !checkIfMissingConsequentVariables(var1)) {
                consequentShow(var1, var1.replace('var1','rel'), isFirstConsequent)
                if (isFirstConsequent) isFirstConsequent = false;
            }
    }
}

function createAndText() {
    let andText = document.createElement("h3");
    andText.classList.add("oxanium-purple");
    andText.classList.add("no-bottom-margin");
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
    input.setAttribute("list", datalist.id)
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