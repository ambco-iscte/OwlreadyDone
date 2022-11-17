function onClick(clickedElementID, ontoClasses, ontoIndividuals, ontoRelations) { // FIXME: UncaughtSyntaxException
    if (clickedElementID.toString().startsWith("queryBuilderAddTermButton")) {
        let elem = document.getElementById(clickedElementID);
        let term = elem.parentElement;
        let section = term.parentElement;

        let andText = elem.previousElementSibling;
        switch (andText.style.display) {
            case "box": {   // AND is visible, disable the current term.
                term.remove();
                break;
            }
            case "none": {  // AND is not visible, make visible and add new term.
                elem.innerText = "-";
                andText.style.display = "block";
                section.appendChild(createBlankTerm(section.childElementCount + 1, ontoClasses, ontoIndividuals, ontoRelations));
                break;
            }
        }
    }
}

/**
 * Creates a blank query builder term element.
 * @param index The index to use for the new element.
 * @param ontoClasses The list of all classes present in the ontology.
 * @param ontoIndividuals The list of all individuals present in the ontology.
 * @param ontoRelations The list of all relations (data properties, object properties, etc.) present in the ontology.
 * @returns {HTMLElement}
 */
function createBlankTerm(index, ontoClasses, ontoIndividuals, ontoRelations) {
    let term = document.createElement("section");
    term.classList.add("query-builder-term");

    let inputFieldSection = document.createElement("section");
    term.appendChild(inputFieldSection);

    term.append(createTextInput("antecedentTerm" + index + "-var1", "?var1", true, "query-builder-input-text"));

    term.append(createSelectWithOptions("antecedentTerm" + index + "rel", "query-builder-input-select",
        "query-builder-input-select-option", ontoRelations));

    term.append(createTextInputWithDatalist("antecedentTerm" + index + "-var2", "?var2", true,
        ontoClasses.concat(ontoIndividuals), "query-builder-input-text", "query-builder-input-select-option"));

    let andText = document.createElement("h3");
    andText.classList.add("oxanium-purple");
    andText.innerText = "and";
    andText.style.display = "none";
    term.appendChild(andText);

    let addIcon = document.createElement("h3");
    addIcon.classList.add("oxanium-purple", "no-bottom-margin", "highlight-on-hover", "unselectable");
    addIcon.innerText = "+";
    term.appendChild(addIcon);

    return term;
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
 * @param options The list of options to include in the datalist.
 * @param inputClass The CSS class to use for the text field.
 * @param optionClass The CSS class to for each datalist child element.
 * @returns {HTMLInputElement}
 */
function createTextInputWithDatalist(id, placeholder, required, options, inputClass, optionClass) {
    let input = createTextInput(id, placeholder, required, inputClass);
    let datalist = document.createElement("datalist");
    for (let option in options) {
        let optionElement = document.createElement("option");
        optionElement.classList.add(optionClass);
        optionElement.value = option;
        optionElement.innerText = option;
        datalist.appendChild(optionElement);
    }
    input.list = datalist;
    return input;
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
    for (let option in options) {
        let optionElement = document.createElement("option");
        optionElement.classList.add(optionClass);
        optionElement.value = option;
        optionElement.innerText = option;
        elem.appendChild(optionElement);
    }
    return elem;
}