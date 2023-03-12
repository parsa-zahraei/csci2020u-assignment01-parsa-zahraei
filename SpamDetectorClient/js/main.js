// TODO: onload function should retrieve the data needed to populate the UI


function full_table(tableID, json){
  let tableRef = document.getElementById(tableID);

  for (e in json.TestFiles){
    let newRow = tableRef.insertRow(-1);
    let fileCell = newRow.insertCell(0);
    let spamCell = newRow.insertCell(1);
    let classCell = newRow.insertCell(2);

    let fileText = document.createTextNode(json.TestFiles[e].file);
    let spamText = document.createTextNode(json.TestFiles[e].spamProbability);
    let classText = document.createTextNode(json.TestFiles[e].actualClass);

    fileCell.appendChild(fileText);
    spamCell.appendChild(spamText);
    classCell.appendChild(classText);
}


}

(function() {
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/json", {
    method: "Get",
      headers: {
        "Accept": "application/json"
      },
  })
    .then(response => response.json())
    .then(response => fill_table("table", response))
    .catch((err) => {
      console.log("something went wrong: " + err);
    });
})();


function load_accuracy(){
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/accuracy", {
    method: "Get",
    headers: {
      "Accept": "application/json"
    },
  })
    .then(response => response.json());
    .catch((err)) => {

  }

}
