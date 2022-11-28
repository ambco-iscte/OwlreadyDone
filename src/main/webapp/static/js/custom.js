/*
 TODO: implement show button for the consequent and its features
       Nas relacoes faltam as Built-in functions. E.g., the math built-in swrlb:greaterThan(?np, 1) succeeds if the value of ?np is greater than 1.
 */
function antecedentAddNewTermClicked(clickedElementID, ontoClasses, ontoIndividuals, ontoRelations) {
    if (clickedElementID.toString().startsWith("queryBuilderAntecedentAddTermButton")) {
        let elem = document.getElementById(clickedElementID);
        let term = elem.parentElement;
        let section = term.parentElement;

        let andText = elem.previousElementSibling;

        let showText = andText.previousElementSibling;
        let index = clickedElementID.toString().split('-')[1];

        let var1 = "antecedentTerm" + index + "-var1"
        let rel = "antecedentTerm" + index + "-rel"
        let var2 = "antecedentTerm" + index + "-var2"

        switch (andText.style.display) {
            case "block": {   // AND is visible, disable the current term.
                if (checkIfMissingVariables(var1, rel, var2)) {
                    alert("Unable to add new term: Check if the term fields are correctly filled out!")
                    break;
                }
                removeQueryTerm(var1, rel, var2);
                term.remove();
                break;
            }
            case "none": {  // AND is not visible, make visible and add new term.
                if (checkIfMissingVariables(var1, rel, var2)) {
                    alert("Unable to add new term: Check if the term fields are correctly filled out!")
                    break;
                }
                elem.innerText = "-";
                andText.style.display = "block";
                showText.style.display = "block";
                section.appendChild(createBlankAntecedentTerm(section.childElementCount + 1, ontoClasses, ontoIndividuals, ontoRelations));
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

        let andText = elem.previousElementSibling;
        switch (andText.style.display) {
            case "block": {   // AND is visible, disable the current term.
                term.remove();
                break;
            }
            case "none": {  // AND is not visible, make visible and add new term.
                elem.innerText = "-";
                andText.style.display = "block";
                section.appendChild(createBlankConsequentTerm(section.childElementCount + 1, builtInNames));
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

    let addTermToDisplayField = document.createElement("h3");
    addTermToDisplayField.id = "queryBuilderAntecedentAddTermToDisplayField-" + index;
    addTermToDisplayField.classList.add("oxanium-purple", "no-bottom-margin", "highlight-on-hover", "unselectable");
    addTermToDisplayField.innerText = "Show";
    addTermToDisplayField.style.display = "none";
    addTermToDisplayField.onclick = function () {
        antecedentShow("antecedentTerm" + index + "-var1", "antecedentTerm" + index + "-rel", "antecedentTerm" + index + "-var2");
    };
    term.appendChild(addTermToDisplayField);

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
 * @param var1
 * @param rel
 * @param var2
 */
function checkIfMissingVariables(var1, rel, var2){
    let term = constructTerm(var1, rel, var2);
    console.log(term.includes("?undefined"))
    console.log(term.includes("()"))
    return term.includes("?undefined") || term.includes("()")
}

/**
 * Given the term variables, it constructs a Term in SQWRL format and appends it to the queryField in query,jsp
 * @param var1 corresponds to first variable of the antecedent
 * @param rel corresponds to second variable of the antecedent
 * @param var2 corresponds to third variable of the antecedent
 */
function antecedentShow (var1, rel, var2) {
    let name = constructTerm(var1, rel, var2);
    let queryBuilderStringValue = document.getElementById("queryBuilderString").value;
    if (queryBuilderStringValue.includes(name)) {
        alert("You have already added that term!")
    }else
        if (queryBuilderStringValue !== ""){
            document.getElementById("queryBuilderString").value = queryBuilderStringValue + " ∧ " + name;
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
    let var1Value = document.getElementById(var1).value;
    let varRelValue = document.getElementById(rel).value;
    let var2Value = document.getElementById(var2).value;

    if (varRelValue.toString().includes("isA")) return var2Value + "(" + var1Value + ")";
    else {
        return varRelValue + "(?" + var1Value.toString().split('?')[1] + " ,?" + var1Value.toString().split('?')[2] + ")";}
}

/**
 * Removes the given term from the queryField in query.jsp
 * @param var1 corresponds to first variable of the antecedent
 * @param rel corresponds to second variable of the antecedent
 * @param var2 corresponds to third variable of the antecedent
 */
function removeQueryTerm(var1, rel, var2){
    let constructedQuery = document.getElementById("queryBuilderString").value;
    let termToBeRemoved = constructTerm(var1, rel, var2);
    console.log("Term to be removed "+termToBeRemoved)
    if (constructedQuery.includes('∧') && constructedQuery.toString().split(' ∧ ')[0] !== termToBeRemoved)
        termToBeRemoved = " ∧ " + constructTerm(var1, rel, var2);
    else {
        if (!constructedQuery.includes('∧')) termToBeRemoved = constructTerm(var1, rel, var2)
        else
            if (constructedQuery.toString().split(' ∧ ')[0] === termToBeRemoved) termToBeRemoved = constructTerm(var1, rel, var2) + " ∧ ";
    }
    document.getElementById("queryBuilderString").value = constructedQuery.replace(termToBeRemoved, '');
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