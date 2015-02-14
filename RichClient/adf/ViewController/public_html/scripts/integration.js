'use strict';

/*
 * OTNBridge is the glue between a host technology and one or more guest technologies on one page. Both the host and all guests should register
 * themselves with the OTNBridge.createBridge(element, callback) and OTNBridge.registerGuest(element, id, callback) methods. It will automatically buffer all messages
 * that were sent before registration of guests. These messages will be sent immediately after registration.
 *
 * It is possible to use multiple nested OTNBridges on one page. It uses the event propagation mechanism of the browser to automatically select the right OTNBridge
 * for a certain DOM subtree.
 * 
 * The reason we need multiple bridges is that some host technologies (read: ADF) have the ability to divide the application in reusable pieces (read: taskflows). We need to be able
 * to determine which part of the screen sent which message.
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

  /*
   * Initialise a new bridge. This bridge will handle all guest registrations and messages from guests in the subtree starting with [element].
   * It returns a handle to the bridge to enable the host to send messages to the guests. All messages from guests to host will be handed to [hostCallback].
   */
  function initBridge(element, hostCallback) {
    
    if (element.otnBridge) return element.otnBridge; // hostCallback is not replaced here. Could that be a problem?

    var guestCallbacks = {},  // Mapping from guestId to guest callback function.
        guestBuffer = {};     // Mapping from guestId to an array of messages waiting to be delivered.

    // Somewhere in this subtree a guest wants to register itself... events are created by the OTNBridge.registerGuest method (below).
    element.addEventListener('otnRegisterGuest', function otnRegisterGuest(evt) {
      evt.stopPropagation();

      guestCallbacks[evt.detail.id] = evt.detail.callback;

      sendMessages();
    });

    // Somewhere in the subtree below element a guest wants to send a message to host. (event created by OTNBridge.toHost method)
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
    return element.otnBridge = {
      toGuest: function toGuest(id, message) {
        (guestBuffer[id] = guestBuffer[id] || []).push(message);
        sendMessages();
      }
    };
  }

  window.OTNBridge = {
    // Returns a bridge that can be used to send messages to guests.
    createBridge: initBridge,

    // Can be used by guests to send a message to the host.
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
 * The OTNBootstrapper is responsible for bootstrapping all guest components. These guest components need to register their bootstrap method once with [registerBootstrapper].
 */

(function OTNBootstrapperInit(window) {

  if (window.OTNBootstrapper) return;

  var components = {};

  function bootstrap(element) {
    // Build a selector based on all the ids (we are looking for elements that have className equal to one of the ids).
    var selector;
    for (var id in components) {
      selector = (selector ? selector + ',.' : '.') + id;
    }

    // Now try to bootstrap all "possibly interesting" elements.
    var elements = element.querySelectorAll(selector);
    for (var i = 0, n = elements.length; i < n; i++) {
      bootstrapNode(elements[i], arguments);
    }
  }

  function bootstrapNode(element, args) {
    if (element.guestBootstrapped) return;

    var classes = element.classList;

    for (var i = 0, n = classes.length; i < n; i++) {
      // If we find a bootstrapper for this element, we call it providing it with all the additional arguments that were passed to the bootstrap method. Is
      // not needed anymore, but I like it. ;-)
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
    /*
     * Register a bootstrapper. This [bootstrapper] will be notified of all elements that have the class [id].
     */
    registerBootstrapper: function registerBootstrapper(id, bootstrapper) {
      components[id] = bootstrapper;
    },

    /*
     * Bootstrap all components in the subtree below [element] (first argument to this function).
     */
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

/*
 * Here the tagcloud AngularJS component is registered as a guest technology with the OTNBridge and the OTNBootstrapper. This supports the following syntax to
 * bootstrap components: class="guest-component: componentToBootstrap guestId".
 */
(function AngularGuestInit(OTNBridge, OTNBootstrapper) {

  /*
   * This is the definition of the angularGuest module that is used to bootstrap our components.
   */
  angular.module('angularGuest', [])
    // Because we use the class="guest-component: componentToBootstrap guestId" syntax we can use the builtin support from Angular to process this.
    .directive('guestComponent', function () {
      return {
        restrict: 'C',
        link: function link(scope, element, attrs) {
          // Fetch the id of the component. This is always the second part of the declaration (in our syntax).
          var id = attrs.guestComponent.split(' ')[1];

          // This toHost function can be used by our component to send a message to the host.
          scope.toHost = function toHost(msg) {
            OTNBridge.toHost(element[0], id, msg);
          };

          // We need to register this component with the OTNBridge to receive messages. Messages are supposed to be a map. All key-value pairs in this
          // map are put on the scope to be used by the guest component.
          OTNBridge.registerGuest(element[0], id, function (msg) {
            scope.$apply(function () {
              angular.extend(scope, msg);
            });
          });
        }
      }
    });

  /*
   * Now we register the tagcloud component with the OTNBootstrapper.
   */
  OTNBootstrapper.registerBootstrapper('tagcloud', function (node) {
    if (!node.classList.contains('guest-component:')) return;

    // First we replace the contents of the given [node] with the following HTML.
    angular.element(node).html('<tag-cloud tags="tags" tag-clicked="toHost({clicked:tag.id})"></tag-cloud>');
    
    // We now bootstrap a new Angular application at this [node]. Of course we make sure to load the tagcloud module as a dependency.
    angular.bootstrap(node, ['angularGuest', 'tagcloud']);
  });

}(OTNBridge, OTNBootstrapper));