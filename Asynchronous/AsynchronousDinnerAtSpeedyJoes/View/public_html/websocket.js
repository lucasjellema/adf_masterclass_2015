//var wsUri = "ws://" + document.location.host + document.location.pathname + "mediatorendpoint";
var wsUri = "ws://" + document.location.host + "/DinnerAtSpeedyJoes-Asynch/mediatorendpoint";
var websocket = new WebSocket(wsUri);

function onMessage(evt) {
 console.log("received over websockets: " + evt.data);
 console.log("looked for appetizer index of: "+ evt.data.indexOf("appetizerOrMain"));
 var index = evt.data.indexOf("appetizer");
 writeToScreen(evt.data);
    // process json meal item delivery event
    if (index>1) {
         console.log("found appetizer index of: "+ evt.data.indexOf("appetizer"));   
      handleMealItemDelivery( evt.data);
    }
}

websocket.onerror = function(evt) { onError(evt) };

function onError(evt) {
    writeToScreen('<span style="color: red;">ERROR:</span> ' + evt.data);
}

// For testing purposes
var output = document.getElementById("output");
websocket.onopen = function(evt) { onOpen(evt) };

function writeToScreen(message) {
if (output==null) 
{output = document.getElementById("output");}
    //output.innerHTML += message + "<br>";
    output.innerHTML = message + "<br>";
}

function onOpen() {
    writeToScreen("Connected to " + wsUri);
}
// End test functions

websocket.onmessage = function(evt) { onMessage(evt) };

function sendText(json) {
    console.log("sending text: " + json);
    websocket.send(json);
}
                

