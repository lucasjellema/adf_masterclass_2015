'use strict';

/*
 * OTNBridge is the glue between a host technology and one or more guest technologies on one page. Both the host and all guests should register
 * themselves with the OTNBridge.createBridge(element, callback) and OTNBridge.registerGuest(element, id, callback) methods. It will automatically buffer all messages
 * that were sent before registration of guests. These messages will be sent immediately after registration.
 */

(function OTNBridgeInit(window, document) {

  if (window.OTNBridge) return;

  // If events bubble up to document apparently no bridge was initialised to capture these events.
  document.addEventListener('otnRegisterGuest', function otnRegisterGuest() {
    throw new Error('registerGuest called but no bridge answered!');
  });
  document.addEventListener('otnMessageToHost', function otnMessageToHost() {
    throw new Error('toHost called but no bridge answered!');
  });

  function initBridge(element, hostCallback) {
    if (element.bridgeInstalled) return;
    element.bridgeInstalled = true;

    var guestCallbacks = {},
        guestBuffer = {};

    // Somewhere in this subtree a guest wants to register itself... this appears to be the closest bridge.
    element.addEventListener('otnRegisterGuest', function otnRegisterGuest(evt) {
      evt.stopPropagation();

      guestCallbacks[evt.detail.id] = evt.detail.callback;

      sendMessages();
    });

    // Somewhere in the subtree below element a guest wants to send a message to host.
    element.addEventListener('otnMessageToHost', function otnMessageToHost(evt) {
      evt.stopPropagation();

      hostCallback(evt.detail.message);
    });

    function sendMessages() {
      for (var id in guestBuffer) {
        var callback = guestCallbacks[id];
        var buffer = guestBuffer[id];
        while (callback && buffer && buffer.length) {
          callback(buffer.shift());
        }
      }
    }

    // The bridge (returned from this function) has a method to send messages to guests.
    var bridge = {
      toGuest: function toGuest(id, message) {
        (guestBuffer[id] = guestBuffer[id] || []).push(message);
        sendMessages();
      }
    };

    return bridge;
  }

  window.OTNBridge = {
    // Returns a bridge that can be used to send messages to guests.
    createBridge: initBridge,

    // Can be used by guests to send a message to the appropriate host.
    toHost: function toHost(element, guestId, message) {
      element.dispatchEvent(new CustomEvent('otnMessageToHost', {
        bubbles: true,
        detail: {
          message: {
            id: guestId,
            msg: message
          }
        }
      }));
    },

    // Can be used by guests to register themselves at the appropriate bridge.
    registerGuest: function registerGuest(element, guestId, callback) {
      element.dispatchEvent(new CustomEvent('otnRegisterGuest', {
        bubbles: true,
        detail: {
          id: guestId,
          callback: callback
        }
      }));
    }
  }

})(window, document);



// Polyfill for CustomEvent for IE 10 and 11
(function () {
  if (window.CustomEvent) return;

  function CustomEvent ( event, params ) {
    params = params || { bubbles: false, cancelable: false, detail: undefined };
    var evt = document.createEvent( 'CustomEvent' );
    evt.initCustomEvent( event, params.bubbles, params.cancelable, params.detail );
    return evt;
   };

  CustomEvent.prototype = window.Event.prototype;

  window.CustomEvent = CustomEvent;
})();


'use strict';

/*
 *
 */

(function OTNBootstrapperInit(window) {

  if (window.OTNBootstrapper) return;

  var components = {};

  function bootstrap(element) {
    var selector;
    for (var id in components) {
      selector = (selector ? selector + ',.' : '.') + id;
    }

    var elements = element.querySelectorAll(selector);
    for (var i = 0, n = elements.length; i < n; i++) {
      bootstrapNode(elements[i], arguments);
    }
  }

  function bootstrapNode(element, args) {
    if (element.guestBootstrapped) return;

    var classes = element.classList;

    for (var i = 0, n = classes.length; i < n; i++) {
      var bootstrapper = components[classes[i]];
      if (bootstrapper) {
        var copy = Array.prototype.slice.call(args);
        copy.unshift(element);
        bootstrapper.apply(this, copy);
        element.guestBootstrapped = true;
      }
    }
  }

  window.OTNBootstrapper = {
    registerBootstrapper: function registerComponent(id, bootstrapper) {
      components[id] = bootstrapper;
    },

    bootstrap: bootstrap
  };

}(window));
'use strict';

function bootstrapGuestModules(clientId) {
  var element = document.getElementById(clientId);
  
  element.OTNBridge = element.OTNBridge || OTNBridge.createBridge(element, function sendMessageToHost(msg) {
    var source = AdfPage.PAGE.findComponentByAbsoluteId(clientId);
    AdfCustomEvent.queue(source, 'guestMsg', msg);
  });

  OTNBootstrapper.bootstrap(element);
}

function sendMessageToGuest(clientId, guestId, message) {
  var element = document.getElementById(clientId);
  element.OTNBridge.toGuest(guestId, message);
}

'use strict';

(function AngularGuestInit(OTNBridge, OTNBootstrapper) {

  angular.module('angularGuest', [])
    .directive('guestComponent', function () {
      return {
        restrict: 'C',
        link: function link(scope, element, attrs) {
          var id = attrs.guestComponent.split(' ')[1];

          scope.toHost = function toHost(msg) {
            OTNBridge.toHost(element[0], id, msg);
          };

          OTNBridge.registerGuest(element[0], id, function (msg) {
            scope.$apply(function () {
              angular.extend(scope, msg);
            });
          });
        }
      }
    });

  OTNBootstrapper.registerBootstrapper('tagcloud', function (node) {
    if (!node.classList.contains('guest-component:')) return;

    angular.element(node).html('<tag-cloud tags="tags" tag-clicked="toHost({clicked:tag.id})"></tag-cloud>');
    angular.bootstrap(node, ['angularGuest', 'tagcloud']);
  });

}(OTNBridge, OTNBootstrapper));

function inspectStyles(guestId, clientId, elementsToInspect) {
  var jsonTags = {tags : []};
  for (var e=0; e<elementsToInspect.elements.length; e++) {
    var element = elementsToInspect.elements[e]; 
    var el = document.getElementById(element.clientId);
    if (element.detailElementType) {
       el = el.getElementsByTagName(element.detailType)[0]
    }
    var styles = element.stylesToRetrieve;
    for(var i=0; i<styles.length; i++) {
      jsonTags.tags.push({id: styles[i], text: styles[i] + ' : ' + getStyle(el,styles[i]), value: 40});
    }
  }//for elementsToInspect
  sendMessageToGuest(clientId, guestId, jsonTags);   
}

function extractTagsForResourceBundleEntries( guestId, clientId ) {
  var jsonTags = {tags : []};
  //find element with id dictionary
  var dictionaryComponent = AdfPage.PAGE.findComponentByAbsoluteId(clientId).findComponent("dictionary");
    
  // extract client attribute dictionary, which is a JSON map with properties equal to the keys for the resources we are interested in
  var dictionaryContents = dictionaryComponent.getProperty("dictionary");
  // to parse JSON we have to use double quotes but clientAttribute values we have to use single quotes
  // that is why the next line replace all single quotes with double quotes
  var dictionaryContents = dictionaryContents.replace(/'/g, '"')
  var dictionary =  JSON.parse(dictionaryContents);

  // create a tag for each these resources.
  for(var key in dictionary) { 
    jsonTags.tags.push({id: key, text: key + ' = ' + dictionary[key], value: 70});
  }//for key in dictionary 
  sendMessageToGuest(clientId, guestId, jsonTags);   
}//extractTagsForResourceBundleEntries

// found on http://stackoverflow.com/questions/6338217/get-a-css-value-with-javascript
function getStyle(el, styleProp) {
  var value, defaultView = (el.ownerDocument || document).defaultView;
  // W3C standard way:
  if (defaultView && defaultView.getComputedStyle) {
    // sanitize property name to css notation
    // (hypen separated words eg. font-Size)
    styleProp = styleProp.replace(/([A-Z])/g, "-$1").toLowerCase();
    return defaultView.getComputedStyle(el, null).getPropertyValue(styleProp);
  } else if (el.currentStyle) { // IE
    // sanitize property name to camelCase
    styleProp = styleProp.replace(/\-(\w)/g, function(str, letter) {
      return letter.toUpperCase();
    });
    value = el.currentStyle[styleProp];
    // convert other units to pixels on IE
    if (/^\d+(em|pt|%|ex)?$/i.test(value)) { 
      return (function(value) {
        var oldLeft = el.style.left, oldRsLeft = el.runtimeStyle.left;
        el.runtimeStyle.left = el.currentStyle.left;
        el.style.left = value || 0;
        value = el.style.pixelLeft + "px";
        el.style.left = oldLeft;
        el.runtimeStyle.left = oldRsLeft;
        return value;
      })(value);
    }
    return value;
  }
}