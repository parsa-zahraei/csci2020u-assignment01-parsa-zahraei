// TODO: onload function should retrieve the data needed to populate the UI

//Populates the tabel with data of the testFile list
function fill_table(tableID, json){
  let tableRef = document.getElementById(tableID);

  for (e in json){
    console.log("Testing Row")
    let newRow = tableRef.insertRow(-1);
    let fileCell = newRow.insertCell(0);
    let spamCell = newRow.insertCell(1);
    let classCell = newRow.insertCell(2);

    let fileText = document.createTextNode(json[e].file);
    let spamText = document.createTextNode(json[e].spamProbability);
    let classText = document.createTextNode(json[e].actualClass);

    fileCell.appendChild(fileText);
    spamCell.appendChild(spamText);
    classCell.appendChild(classText);
}


}

//Root JavaScript function
(function() {
  //Endpoint call to populate table
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/json", {
    method: "Get",
    headers: {
      "Accept": "application/json"
    },
  })
    .then(response => response.json())
    .then(response => fill_table("chart", response))
    .catch((err) => {
      console.log("something went wrong: " + err);
    });

  //Endpoint call to record accuracy and number of true positives and negatives
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/accuracy", {
    method: "Get",
    headers: {
      "Accept": "application/json"
    },
  })
    .then(response => response.json())
    .then(response => load_accuracy("Stats", "Values", response))
    .catch((err) => {
      console.log("something went wrong: " + err);
    });

  //Endpoint call to record precision and number of false positives and negatives
  fetch("http://localhost:8080/spamDetector-1.0/api/spam/precision", {
    method: "Get",
    headers: {
      "Accept": "application/json"
    },
  })
    .then(response => response.json())
    .then(response => load_precision("Stats","Values", response))
    .catch((err) => {
      console.log("something went wrong: " + err);
    });

  })();




//Function records the accuracy of the model, as well as the number of true positives and true negatives produced
function load_accuracy(statsID, performanceID, json){

  //Access the first td element of the stats table
  let tdElementsStats = document.getElementById(statsID).getElementsByTagName("td");

  //Add the accuracy measurement to the stats table
  tdElementsStats[0].innerHTML = json.accuracyPercent;

  //Access the first and third td elements
  let tdElementsPerform = document.getElementById(performanceID).getElementsByTagName("td");

  //Record the number of true positives
  tdElementsPerform[0].innerHTML = json.truePositives;

  //Record the number of true negatives
  tdElementsPerform[2].innerHTML = json.trueNegatives;

}

//Function records the precision of the model, as well as the number of false positives and false negatives produced
function load_precision(statsID, performanceID, json){

  //Access the second td element of the stats table
  let tdElementsStats = document.getElementById(statsID).getElementsByTagName("td");

  //Add the accuracy measurement to the stats table
  tdElementsStats[1].innerHTML = json.precisionPercent;

  //Access the second and fourth td elements
  let tdElementsPerform = document.getElementById(performanceID).getElementsByTagName("td");

  //Record the number of false positives
  tdElementsPerform[1].innerHTML = json.falsePositives;

  //Record the number of false negatives
  tdElementsPerform[3].innerHTML = json.falseNegatives;
}
