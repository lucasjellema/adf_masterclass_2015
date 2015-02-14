
function handleMealItemDelivery( json) {
   var mealItem =  JSON.parse(json);
   handleMealItemObjectDelivery(mealItem);
}

        
function handleMealItemObjectDelivery( mealItem) {
   var mealDiv = document.getElementById("mealDiv");
   dinner.push({ "menuItem":mealItem.menuItem, "price":mealItem.price});
   var arrayLength = dinner.length;
   mealDiv.innerHTML = "Dinner is being served... <br/>"
 
   writeTrace(mealItem.trace);

   var checkTotal = 0;
   for (var i = 0; i < arrayLength; i++) {
     mealDiv.innerHTML += dinner[i].menuItem+" ( \u20AC "+ dinner[i].price+ ") <br>";
     checkTotal += dinner[i].price;
   }//for
   mealDiv.innerHTML += "<br/>(running)Check Total: "+" ( \u20AC "+ checkTotal+ ") <br>";
   document.getElementById("message").innerHTML = json;     
x}

// The 17 colors from the HTML specification are: aqua, black, blue, fuchsia, gray, green, lime, maroon, navy, olive, orange, purple, red, silver, teal, white, and yellow.
var colors = ["blue","red","green","yellow","purple","orange","gray","black", "olive","silver" ];
var currentColorIndex = -1;
var threadColorMap = {};

function getColor(thread) {
    if (!(thread in threadColorMap)) {
        currentColorIndex++;
        threadColorMap[thread]= colors[currentColorIndex];
    }
    return threadColorMap[thread];
}//getColor


function writeTrace(trace) {
       // write trace
   traceItems = trace.logEntries;
   var traceCell = document.getElementById("trace");
   
   var block = "<ul>";
   var previousLevel = 1
   for (var i = 0; i < traceItems.length; i++) {
     level = traceItems[i].level;
     var line="";
     if (level > previousLevel) {
         line += "<ul>"
     }
     if (level < previousLevel) {
         line += "</ul>"
     }
     
     line += "<li>"+traceItems[i].component+"&nbsp;"+traceItems[i].level+ "("+traceItems[i].description+ ") - thread: "+traceItems[i].thread
     +"<div style=\"background-color: "+getColor(traceItems[i].thread)+"; width: "+ 500 * (traceItems[i].duration/5000) +"px; text-align: center; color: white;\">"+traceItems[i].duration+"&nbsp; ms</div>" 
     +"</li>"
     ;

     block += line;
     previousLevel = level;
   }//for
   traceCell.innerHTML += block+"</ul>";

}