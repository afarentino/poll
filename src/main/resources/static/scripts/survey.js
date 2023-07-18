
document.addEventListener('DOMContentLoaded', function() {
    // This function will attach click event handlers that we need
    console.log("DOM Content loaded!");
    let selectAllBtn = document.getElementById("selectAllBtn");
    selectAllBtn.addEventListener( 'click', selectAllEntries);
});

function selectAllEntries(e) {
    const allCheckboxes = document.getElementsByClassName('form-check-input');
    for (let i=0 ; i < allCheckboxes.length; i++) {
        allCheckboxes[i].checked = true;
    }
}

// Clear or select all checkboxes that match the provided date
// click event handler
// @param date - the date that uniquely identifies the checkbox elements
// @param state - true (check the checkboxes) false (uncheck the checkboxes)
function toggleEntriesFor(date, state) {
    let selectors = `input[type="checkbox"].form-check-input[id$="${date}"]`;  // all els that have an id ending w/date
    let checkBoxes = document.querySelectorAll(selectors);
    checkBoxes.forEach( (checkbox) => {
       checkbox.checked = state;
    });
}