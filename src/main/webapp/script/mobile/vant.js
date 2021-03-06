(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory(require("vue"));
	else if(typeof define === 'function' && define.amd)
		define("vant", ["vue"], factory);
	else if(typeof exports === 'object')
		exports["vant"] = factory(require("vue"));
	else
		root["vant"] = factory(root["Vue"]);
})(typeof self !== 'undefined' ? self : this, function(__WEBPACK_EXTERNAL_MODULE_8__) {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
/******/
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {
/******/
/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId]) {
/******/ 			return installedModules[moduleId].exports;
/******/ 		}
/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			i: moduleId,
/******/ 			l: false,
/******/ 			exports: {}
/******/ 		};
/******/
/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);
/******/
/******/ 		// Flag the module as loaded
/******/ 		module.l = true;
/******/
/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}
/******/
/******/
/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;
/******/
/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;
/******/
/******/ 	// define getter function for harmony exports
/******/ 	__webpack_require__.d = function(exports, name, getter) {
/******/ 		if(!__webpack_require__.o(exports, name)) {
/******/ 			Object.defineProperty(exports, name, {
/******/ 				configurable: false,
/******/ 				enumerable: true,
/******/ 				get: getter
/******/ 			});
/******/ 		}
/******/ 	};
/******/
/******/ 	// getDefaultExport function for compatibility with non-harmony modules
/******/ 	__webpack_require__.n = function(module) {
/******/ 		var getter = module && module.__esModule ?
/******/ 			function getDefault() { return module['default']; } :
/******/ 			function getModuleExports() { return module; };
/******/ 		__webpack_require__.d(getter, 'a', getter);
/******/ 		return getter;
/******/ 	};
/******/
/******/ 	// Object.prototype.hasOwnProperty.call
/******/ 	__webpack_require__.o = function(object, property) { return Object.prototype.hasOwnProperty.call(object, property); };
/******/
/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";
/******/
/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(__webpack_require__.s = 64);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ (function(module, exports) {

/* globals __VUE_SSR_CONTEXT__ */

// IMPORTANT: Do NOT use ES2015 features in this file.
// This module is a runtime utility for cleaner component module output and will
// be included in the final webpack user bundle.

module.exports = function normalizeComponent (
  rawScriptExports,
  compiledTemplate,
  functionalTemplate,
  injectStyles,
  scopeId,
  moduleIdentifier /* server only */
) {
  var esModule
  var scriptExports = rawScriptExports = rawScriptExports || {}

  // ES6 modules interop
  var type = typeof rawScriptExports.default
  if (type === 'object' || type === 'function') {
    esModule = rawScriptExports
    scriptExports = rawScriptExports.default
  }

  // Vue.extend constructor export interop
  var options = typeof scriptExports === 'function'
    ? scriptExports.options
    : scriptExports

  // render functions
  if (compiledTemplate) {
    options.render = compiledTemplate.render
    options.staticRenderFns = compiledTemplate.staticRenderFns
    options._compiled = true
  }

  // functional template
  if (functionalTemplate) {
    options.functional = true
  }

  // scopedId
  if (scopeId) {
    options._scopeId = scopeId
  }

  var hook
  if (moduleIdentifier) { // server build
    hook = function (context) {
      // 2.3 injection
      context =
        context || // cached call
        (this.$vnode && this.$vnode.ssrContext) || // stateful
        (this.parent && this.parent.$vnode && this.parent.$vnode.ssrContext) // functional
      // 2.2 with runInNewContext: true
      if (!context && typeof __VUE_SSR_CONTEXT__ !== 'undefined') {
        context = __VUE_SSR_CONTEXT__
      }
      // inject component styles
      if (injectStyles) {
        injectStyles.call(this, context)
      }
      // register component module identifier for async chunk inferrence
      if (context && context._registeredComponents) {
        context._registeredComponents.add(moduleIdentifier)
      }
    }
    // used by ssr in case component is cached and beforeCreate
    // never gets called
    options._ssrRegister = hook
  } else if (injectStyles) {
    hook = injectStyles
  }

  if (hook) {
    var functional = options.functional
    var existing = functional
      ? options.render
      : options.beforeCreate

    if (!functional) {
      // inject component registration as beforeCreate hook
      options.beforeCreate = existing
        ? [].concat(existing, hook)
        : [hook]
    } else {
      // for template-only hot-reload because in that case the render fn doesn't
      // go through the normalizer
      options._injectStyles = hook
      // register for functioal component in vue file
      options.render = function renderWithStyleInjection (h, context) {
        hook.call(context)
        return existing(h, context)
      }
    }
  }

  return {
    esModule: esModule,
    exports: scriptExports,
    options: options
  }
}


/***/ }),
/* 1 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";

// EXTERNAL MODULE: external {"root":"Vue","commonjs":"vue","commonjs2":"vue","amd":"vue"}
var external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue__ = __webpack_require__(8);
var external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default = /*#__PURE__*/__webpack_require__.n(external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue__);

// EXTERNAL MODULE: ./packages/locale/index.js + 1 modules
var locale = __webpack_require__(42);

// EXTERNAL MODULE: ./packages/icon/index.vue + 2 modules
var icon = __webpack_require__(56);

// CONCATENATED MODULE: ./packages/mixins/i18n.js
// component mixin


/* harmony default export */ var i18n = ({
  computed: {
    $t: function $t() {
      var name = this.$options.name;

      var prefix = name ? camelize(name) + '.' : '';
      var messages = this.$vantMessages[this.$vantLang];

      return function (path) {
        for (var _len = arguments.length, args = Array(_len > 1 ? _len - 1 : 0), _key = 1; _key < _len; _key++) {
          args[_key - 1] = arguments[_key];
        }

        var message = get(messages, prefix + path) || get(messages, path);
        return typeof message === 'function' ? message.apply(null, args) : message;
      };
    }
  }
});
// EXTERNAL MODULE: ./packages/utils/install.js
var install = __webpack_require__(40);

// EXTERNAL MODULE: ./packages/loading/index.vue + 2 modules
var loading = __webpack_require__(57);

// CONCATENATED MODULE: ./packages/utils/create.js
/**
 * Create a component with common options
 */






/* harmony default export */ var create = (function (sfc) {
  sfc.mixins = sfc.mixins || [];
  sfc.components = sfc.components || {};
  sfc.install = sfc.install || install["a" /* default */];
  sfc.mixins.push(i18n);
  sfc.components.icon = icon["a" /* default */];
  sfc.components.loading = loading["a" /* default */];

  return sfc;
});;
// CONCATENATED MODULE: ./packages/utils/index.js
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "c", function() { return get; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "e", function() { return isDef; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "f", function() { return isServer; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "a", function() { return camelize; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "d", function() { return isAndroid; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "b", function() { return create; });



var isServer = external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.prototype.$isServer;

function isDef(value) {
  return value !== undefined && value !== null;
}

function get(object, path) {
  var keys = path.split('.');
  var result = object;

  keys.forEach(function (key) {
    result = isDef(result[key]) ? result[key] : '';
  });

  return result;
}

var camelizeRE = /-(\w)/g;
function camelize(str) {
  return str.replace(camelizeRE, function (_, c) {
    return c.toUpperCase();
  });
}

function isAndroid() {
  /* istanbul ignore next */
  return isServer ? false : /android/.test(navigator.userAgent.toLowerCase());
}



/***/ }),
/* 2 */
/***/ (function(module, exports) {

// https://github.com/zloirock/core-js/issues/86#issuecomment-115759028
var global = module.exports = typeof window != 'undefined' && window.Math == Math
  ? window : typeof self != 'undefined' && self.Math == Math ? self
  // eslint-disable-next-line no-new-func
  : Function('return this')();
if (typeof __g == 'number') __g = global; // eslint-disable-line no-undef


/***/ }),
/* 3 */
/***/ (function(module, exports) {

var core = module.exports = { version: '2.5.3' };
if (typeof __e == 'number') __e = core; // eslint-disable-line no-undef


/***/ }),
/* 4 */
/***/ (function(module, exports, __webpack_require__) {

var store = __webpack_require__(34)('wks');
var uid = __webpack_require__(25);
var Symbol = __webpack_require__(2).Symbol;
var USE_SYMBOL = typeof Symbol == 'function';

var $exports = module.exports = function (name) {
  return store[name] || (store[name] =
    USE_SYMBOL && Symbol[name] || (USE_SYMBOL ? Symbol : uid)('Symbol.' + name));
};

$exports.store = store;


/***/ }),
/* 5 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


exports.__esModule = true;

var _assign = __webpack_require__(16);

var _assign2 = _interopRequireDefault(_assign);

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

exports.default = _assign2.default || function (target) {
  for (var i = 1; i < arguments.length; i++) {
    var source = arguments[i];

    for (var key in source) {
      if (Object.prototype.hasOwnProperty.call(source, key)) {
        target[key] = source[key];
      }
    }
  }

  return target;
};

/***/ }),
/* 6 */
/***/ (function(module, exports, __webpack_require__) {

var global = __webpack_require__(2);
var core = __webpack_require__(3);
var ctx = __webpack_require__(22);
var hide = __webpack_require__(9);
var PROTOTYPE = 'prototype';

var $export = function (type, name, source) {
  var IS_FORCED = type & $export.F;
  var IS_GLOBAL = type & $export.G;
  var IS_STATIC = type & $export.S;
  var IS_PROTO = type & $export.P;
  var IS_BIND = type & $export.B;
  var IS_WRAP = type & $export.W;
  var exports = IS_GLOBAL ? core : core[name] || (core[name] = {});
  var expProto = exports[PROTOTYPE];
  var target = IS_GLOBAL ? global : IS_STATIC ? global[name] : (global[name] || {})[PROTOTYPE];
  var key, own, out;
  if (IS_GLOBAL) source = name;
  for (key in source) {
    // contains in native
    own = !IS_FORCED && target && target[key] !== undefined;
    if (own && key in exports) continue;
    // export native or passed
    out = own ? target[key] : source[key];
    // prevent global pollution for namespaces
    exports[key] = IS_GLOBAL && typeof target[key] != 'function' ? source[key]
    // bind timers to global for call from export context
    : IS_BIND && own ? ctx(out, global)
    // wrap global constructors for prevent change them in library
    : IS_WRAP && target[key] == out ? (function (C) {
      var F = function (a, b, c) {
        if (this instanceof C) {
          switch (arguments.length) {
            case 0: return new C();
            case 1: return new C(a);
            case 2: return new C(a, b);
          } return new C(a, b, c);
        } return C.apply(this, arguments);
      };
      F[PROTOTYPE] = C[PROTOTYPE];
      return F;
    // make static versions for prototype methods
    })(out) : IS_PROTO && typeof out == 'function' ? ctx(Function.call, out) : out;
    // export proto methods to core.%CONSTRUCTOR%.methods.%NAME%
    if (IS_PROTO) {
      (exports.virtual || (exports.virtual = {}))[key] = out;
      // export proto methods to core.%CONSTRUCTOR%.prototype.%NAME%
      if (type & $export.R && expProto && !expProto[key]) hide(expProto, key, out);
    }
  }
};
// type bitmap
$export.F = 1;   // forced
$export.G = 2;   // global
$export.S = 4;   // static
$export.P = 8;   // proto
$export.B = 16;  // bind
$export.W = 32;  // wrap
$export.U = 64;  // safe
$export.R = 128; // real proto method for `library`
module.exports = $export;


/***/ }),
/* 7 */
/***/ (function(module, exports, __webpack_require__) {

var isObject = __webpack_require__(11);
module.exports = function (it) {
  if (!isObject(it)) throw TypeError(it + ' is not an object!');
  return it;
};


/***/ }),
/* 8 */
/***/ (function(module, exports) {

module.exports = __WEBPACK_EXTERNAL_MODULE_8__;

/***/ }),
/* 9 */
/***/ (function(module, exports, __webpack_require__) {

var dP = __webpack_require__(10);
var createDesc = __webpack_require__(24);
module.exports = __webpack_require__(12) ? function (object, key, value) {
  return dP.f(object, key, createDesc(1, value));
} : function (object, key, value) {
  object[key] = value;
  return object;
};


/***/ }),
/* 10 */
/***/ (function(module, exports, __webpack_require__) {

var anObject = __webpack_require__(7);
var IE8_DOM_DEFINE = __webpack_require__(46);
var toPrimitive = __webpack_require__(32);
var dP = Object.defineProperty;

exports.f = __webpack_require__(12) ? Object.defineProperty : function defineProperty(O, P, Attributes) {
  anObject(O);
  P = toPrimitive(P, true);
  anObject(Attributes);
  if (IE8_DOM_DEFINE) try {
    return dP(O, P, Attributes);
  } catch (e) { /* empty */ }
  if ('get' in Attributes || 'set' in Attributes) throw TypeError('Accessors not supported!');
  if ('value' in Attributes) O[P] = Attributes.value;
  return O;
};


/***/ }),
/* 11 */
/***/ (function(module, exports) {

module.exports = function (it) {
  return typeof it === 'object' ? it !== null : typeof it === 'function';
};


/***/ }),
/* 12 */
/***/ (function(module, exports, __webpack_require__) {

// Thank's IE8 for his funny defineProperty
module.exports = !__webpack_require__(14)(function () {
  return Object.defineProperty({}, 'a', { get: function () { return 7; } }).a != 7;
});


/***/ }),
/* 13 */
/***/ (function(module, exports) {

var hasOwnProperty = {}.hasOwnProperty;
module.exports = function (it, key) {
  return hasOwnProperty.call(it, key);
};


/***/ }),
/* 14 */
/***/ (function(module, exports) {

module.exports = function (exec) {
  try {
    return !!exec();
  } catch (e) {
    return true;
  }
};


/***/ }),
/* 15 */
/***/ (function(module, exports, __webpack_require__) {

// to indexed object, toObject with fallback for non-array-like ES3 strings
var IObject = __webpack_require__(50);
var defined = __webpack_require__(30);
module.exports = function (it) {
  return IObject(defined(it));
};


/***/ }),
/* 16 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = { "default": __webpack_require__(86), __esModule: true };

/***/ }),
/* 17 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";


exports.__esModule = true;

var _iterator = __webpack_require__(65);

var _iterator2 = _interopRequireDefault(_iterator);

var _symbol = __webpack_require__(76);

var _symbol2 = _interopRequireDefault(_symbol);

var _typeof = typeof _symbol2.default === "function" && typeof _iterator2.default === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof _symbol2.default === "function" && obj.constructor === _symbol2.default && obj !== _symbol2.default.prototype ? "symbol" : typeof obj; };

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

exports.default = typeof _symbol2.default === "function" && _typeof(_iterator2.default) === "symbol" ? function (obj) {
  return typeof obj === "undefined" ? "undefined" : _typeof(obj);
} : function (obj) {
  return obj && typeof _symbol2.default === "function" && obj.constructor === _symbol2.default && obj !== _symbol2.default.prototype ? "symbol" : typeof obj === "undefined" ? "undefined" : _typeof(obj);
};

/***/ }),
/* 18 */
/***/ (function(module, exports) {

module.exports = {};


/***/ }),
/* 19 */
/***/ (function(module, exports, __webpack_require__) {

// 19.1.2.14 / 15.2.3.14 Object.keys(O)
var $keys = __webpack_require__(49);
var enumBugKeys = __webpack_require__(35);

module.exports = Object.keys || function keys(O) {
  return $keys(O, enumBugKeys);
};


/***/ }),
/* 20 */
/***/ (function(module, exports) {

var toString = {}.toString;

module.exports = function (it) {
  return toString.call(it).slice(8, -1);
};


/***/ }),
/* 21 */
/***/ (function(module, exports) {

module.exports = true;


/***/ }),
/* 22 */
/***/ (function(module, exports, __webpack_require__) {

// optional / simple context binding
var aFunction = __webpack_require__(23);
module.exports = function (fn, that, length) {
  aFunction(fn);
  if (that === undefined) return fn;
  switch (length) {
    case 1: return function (a) {
      return fn.call(that, a);
    };
    case 2: return function (a, b) {
      return fn.call(that, a, b);
    };
    case 3: return function (a, b, c) {
      return fn.call(that, a, b, c);
    };
  }
  return function (/* ...args */) {
    return fn.apply(that, arguments);
  };
};


/***/ }),
/* 23 */
/***/ (function(module, exports) {

module.exports = function (it) {
  if (typeof it != 'function') throw TypeError(it + ' is not a function!');
  return it;
};


/***/ }),
/* 24 */
/***/ (function(module, exports) {

module.exports = function (bitmap, value) {
  return {
    enumerable: !(bitmap & 1),
    configurable: !(bitmap & 2),
    writable: !(bitmap & 4),
    value: value
  };
};


/***/ }),
/* 25 */
/***/ (function(module, exports) {

var id = 0;
var px = Math.random();
module.exports = function (key) {
  return 'Symbol('.concat(key === undefined ? '' : key, ')_', (++id + px).toString(36));
};


/***/ }),
/* 26 */
/***/ (function(module, exports, __webpack_require__) {

var def = __webpack_require__(10).f;
var has = __webpack_require__(13);
var TAG = __webpack_require__(4)('toStringTag');

module.exports = function (it, tag, stat) {
  if (it && !has(it = stat ? it : it.prototype, TAG)) def(it, TAG, { configurable: true, value: tag });
};


/***/ }),
/* 27 */
/***/ (function(module, exports) {

exports.f = {}.propertyIsEnumerable;


/***/ }),
/* 28 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = { "default": __webpack_require__(103), __esModule: true };

/***/ }),
/* 29 */
/***/ (function(module, exports) {

// 7.1.4 ToInteger
var ceil = Math.ceil;
var floor = Math.floor;
module.exports = function (it) {
  return isNaN(it = +it) ? 0 : (it > 0 ? floor : ceil)(it);
};


/***/ }),
/* 30 */
/***/ (function(module, exports) {

// 7.2.1 RequireObjectCoercible(argument)
module.exports = function (it) {
  if (it == undefined) throw TypeError("Can't call method on  " + it);
  return it;
};


/***/ }),
/* 31 */
/***/ (function(module, exports, __webpack_require__) {

var isObject = __webpack_require__(11);
var document = __webpack_require__(2).document;
// typeof document.createElement is 'object' in old IE
var is = isObject(document) && isObject(document.createElement);
module.exports = function (it) {
  return is ? document.createElement(it) : {};
};


/***/ }),
/* 32 */
/***/ (function(module, exports, __webpack_require__) {

// 7.1.1 ToPrimitive(input [, PreferredType])
var isObject = __webpack_require__(11);
// instead of the ES6 spec version, we didn't implement @@toPrimitive case
// and the second argument - flag - preferred type is a string
module.exports = function (it, S) {
  if (!isObject(it)) return it;
  var fn, val;
  if (S && typeof (fn = it.toString) == 'function' && !isObject(val = fn.call(it))) return val;
  if (typeof (fn = it.valueOf) == 'function' && !isObject(val = fn.call(it))) return val;
  if (!S && typeof (fn = it.toString) == 'function' && !isObject(val = fn.call(it))) return val;
  throw TypeError("Can't convert object to primitive value");
};


/***/ }),
/* 33 */
/***/ (function(module, exports, __webpack_require__) {

var shared = __webpack_require__(34)('keys');
var uid = __webpack_require__(25);
module.exports = function (key) {
  return shared[key] || (shared[key] = uid(key));
};


/***/ }),
/* 34 */
/***/ (function(module, exports, __webpack_require__) {

var global = __webpack_require__(2);
var SHARED = '__core-js_shared__';
var store = global[SHARED] || (global[SHARED] = {});
module.exports = function (key) {
  return store[key] || (store[key] = {});
};


/***/ }),
/* 35 */
/***/ (function(module, exports) {

// IE 8- don't enum bug keys
module.exports = (
  'constructor,hasOwnProperty,isPrototypeOf,propertyIsEnumerable,toLocaleString,toString,valueOf'
).split(',');


/***/ }),
/* 36 */
/***/ (function(module, exports, __webpack_require__) {

// 7.1.13 ToObject(argument)
var defined = __webpack_require__(30);
module.exports = function (it) {
  return Object(defined(it));
};


/***/ }),
/* 37 */
/***/ (function(module, exports, __webpack_require__) {

exports.f = __webpack_require__(4);


/***/ }),
/* 38 */
/***/ (function(module, exports, __webpack_require__) {

var global = __webpack_require__(2);
var core = __webpack_require__(3);
var LIBRARY = __webpack_require__(21);
var wksExt = __webpack_require__(37);
var defineProperty = __webpack_require__(10).f;
module.exports = function (name) {
  var $Symbol = core.Symbol || (core.Symbol = LIBRARY ? {} : global.Symbol || {});
  if (name.charAt(0) != '_' && !(name in $Symbol)) defineProperty($Symbol, name, { value: wksExt.f(name) });
};


/***/ }),
/* 39 */
/***/ (function(module, exports) {

exports.f = Object.getOwnPropertySymbols;


/***/ }),
/* 40 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/**
 * Install function to register a component
 */
/* harmony default export */ __webpack_exports__["a"] = (function (Vue) {
  Vue.component(this.name, this);
});

/***/ }),
/* 41 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

// 25.4.1.5 NewPromiseCapability(C)
var aFunction = __webpack_require__(23);

function PromiseCapability(C) {
  var resolve, reject;
  this.promise = new C(function ($$resolve, $$reject) {
    if (resolve !== undefined || reject !== undefined) throw TypeError('Bad Promise constructor');
    resolve = $$resolve;
    reject = $$reject;
  });
  this.resolve = aFunction(resolve);
  this.reject = aFunction(reject);
}

module.exports.f = function (C) {
  return new PromiseCapability(C);
};


/***/ }),
/* 42 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";

// EXTERNAL MODULE: external {"root":"Vue","commonjs":"vue","commonjs2":"vue","amd":"vue"}
var external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue__ = __webpack_require__(8);
var external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default = /*#__PURE__*/__webpack_require__.n(external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue__);

// EXTERNAL MODULE: ./packages/utils/deep-assign.js
var deep_assign = __webpack_require__(43);

// CONCATENATED MODULE: ./packages/locale/lang/zh-CN.js
/* harmony default export */ var zh_CN = ({
  confirm: '确认',
  cancel: '取消',
  save: '保存',
  complete: '完成',
  vanContactCard: {
    name: '联系人',
    tel: '联系电话',
    addText: '添加订单联系人信息'
  },
  vanContactList: {
    name: '联系人',
    tel: '联系电话',
    addText: '新建联系人'
  },
  vanContactEdit: {
    name: '联系人',
    namePlaceholder: '名字',
    nameEmpty: '请填写名字',
    nameOverlimit: '名字过长，请重新输入',
    tel: '联系电话',
    telPlaceholder: '手机或固定电话',
    telInvalid: '请填写正确的手机号码或电话号码',
    save: '保存',
    delete: '删除联系人',
    confirmDelete: '确定要删除这个联系人么'
  },
  vanPicker: {
    confirm: '完成'
  },
  vanPagination: {
    prev: '上一页',
    next: '下一页'
  },
  vanPullRefresh: {
    pullingText: '下拉即可刷新...',
    loosingText: '释放即可刷新...',
    loadingText: '加载中...'
  },
  vanSubmitBar: {
    label: '合计：'
  },
  vanCouponCell: {
    title: '优惠券码',
    tips: '使用优惠',
    reduce: '省',
    count: function count(_count) {
      return '\u60A8\u6709 ' + _count + ' \u4E2A\u53EF\u7528\u4F18\u60E0';
    }
  },
  vanCouponList: {
    empty: '暂无优惠券',
    exchange: '兑换',
    close: '不使用优惠',
    disabled: '不可用优惠',
    placeholder: '请输入优惠码'
  },
  vanCouponItem: {
    unlimited: '无使用门槛',
    discount: function discount(_discount) {
      return _discount + '\u6298';
    },
    condition: function condition(_condition) {
      return '\u6EE1' + _condition + '\u5143\u53EF\u7528';
    }
  },
  vanArea: {
    province: '选择省份',
    city: '选择城市',
    county: '选择地区'
  },
  vanAddressEdit: {
    areaTitle: '收件地区',
    addressText: '收货',
    areaWrong: '请选择正确的收件地区',
    areaEmpty: '请选择收件地区',
    nameEmpty: '请填写名字',
    nameOverlimit: '名字过长，请重新输入',
    telWrong: '请填写正确的手机号码或电话号码',
    addressOverlimit: '详细地址不能超过200个字符',
    addressEmpty: '请填写详细地址',
    postalEmpty: '邮政编码格式不正确',
    defaultAddress: function defaultAddress(text) {
      return '\u8BBE\u4E3A\u9ED8\u8BA4' + text + '\u5730\u5740';
    },
    deleteAddress: function deleteAddress(text) {
      return '\u5220\u9664' + text + '\u5730\u5740';
    },
    confirmDelete: function confirmDelete(text) {
      return '\u786E\u5B9A\u8981\u5220\u9664\u8FD9\u4E2A' + text + '\u5730\u5740\u4E48';
    },
    label: {
      name: function name(text) {
        return text + '\u4EBA';
      },
      tel: '联系电话',
      postal: '邮政编码'
    },
    placeholder: {
      name: '名字',
      tel: '手机或固定电话',
      postal: '邮政编码(选填)',
      province: '选择省份',
      city: '选择城市',
      county: '选择地区'
    }
  },
  vanAddressEditDetail: {
    label: {
      address: '详细地址'
    },
    placeholder: {
      address: '如街道、楼层、门牌号等'
    }
  },
  vanAddressList: {
    address: '收货地址',
    add: '新增收货地址'
  },
  vanSku: {
    unavailable: '商品已经无法购买啦',
    spec: '请选择完整的规格',
    least: '至少选择一件',
    quota: function quota(_quota) {
      return '\u9650\u8D2D' + _quota + '\u4EF6';
    },
    inventory: '库存不足',
    purchase: function purchase(count) {
      return '\u60A8\u5DF2\u8D2D\u4E70' + count + '\u4EF6';
    }
  },
  vanSkuActions: {
    cart: '加入购物车',
    buy: '立即购买'
  },
  vanSkuMessages: {
    fill: '请填写',
    number: '请填写正确的数字格式留言',
    email: '请填写正确的邮箱',
    'id_no': '请填写正确的身份证号码',
    overlimit: '写的太多了，不要超过200字',
    placeholder: {
      'id_no': '输入18位身份证号码',
      text: '输入文本',
      tel: '输入数字',
      email: '输入邮箱',
      date: '点击选择日期',
      time: '点击选择时间',
      textarea: '点击填写段落文本'
    }
  },
  vanSkuStepper: {
    title: '购买数量',
    remain: function remain(count) {
      return '\u5269\u4F59' + count + '\u4EF6';
    },
    quota: function quota(_quota2) {
      return '\u6BCF\u4EBA\u9650\u8D2D' + _quota2 + '\u4EF6';
    }
  }
});
// CONCATENATED MODULE: ./packages/locale/index.js




var proto = external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.prototype;
var defaultLang = 'zh-CN';
var locale = {
  init: function init() {
    var _Vue$util$defineReact;

    external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.util.defineReactive(proto, '$vantLang', defaultLang);
    external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.util.defineReactive(proto, '$vantMessages', (_Vue$util$defineReact = {}, _Vue$util$defineReact[defaultLang] = zh_CN, _Vue$util$defineReact));
  },
  use: function use(lang, messages) {
    var _add;

    proto.$vantLang = lang;
    this.add((_add = {}, _add[lang] = messages, _add));
  },
  add: function add() {
    var messages = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};

    Object(deep_assign["a" /* default */])(proto.$vantMessages, messages);
  }
};

locale.init();
/* harmony default export */ var packages_locale = __webpack_exports__["a"] = (locale);

/***/ }),
/* 43 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* harmony export (immutable) */ __webpack_exports__["a"] = assign;
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_babel_runtime_helpers_typeof__ = __webpack_require__(17);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0_babel_runtime_helpers_typeof___default = __webpack_require__.n(__WEBPACK_IMPORTED_MODULE_0_babel_runtime_helpers_typeof__);
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_1____ = __webpack_require__(1);



var hasOwnProperty = Object.prototype.hasOwnProperty;


function isObj(x) {
  var type = typeof x === 'undefined' ? 'undefined' : __WEBPACK_IMPORTED_MODULE_0_babel_runtime_helpers_typeof___default()(x);
  return x !== null && (type === 'object' || type === 'function');
}

function assignKey(to, from, key) {
  var val = from[key];

  if (!Object(__WEBPACK_IMPORTED_MODULE_1____["e" /* isDef */])(val) || hasOwnProperty.call(to, key) && !Object(__WEBPACK_IMPORTED_MODULE_1____["e" /* isDef */])(to[key])) {
    return;
  }

  if (!hasOwnProperty.call(to, key) || !isObj(val)) {
    to[key] = val;
  } else {
    to[key] = assign(Object(to[key]), from[key]);
  }
}

function assign(to, from) {
  for (var key in from) {
    if (hasOwnProperty.call(from, key)) {
      assignKey(to, from, key);
    }
  }
  return to;
}

/***/ }),
/* 44 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var $at = __webpack_require__(67)(true);

// 21.1.3.27 String.prototype[@@iterator]()
__webpack_require__(45)(String, 'String', function (iterated) {
  this._t = String(iterated); // target
  this._i = 0;                // next index
// 21.1.5.2.1 %StringIteratorPrototype%.next()
}, function () {
  var O = this._t;
  var index = this._i;
  var point;
  if (index >= O.length) return { value: undefined, done: true };
  point = $at(O, index);
  this._i += point.length;
  return { value: point, done: false };
});


/***/ }),
/* 45 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var LIBRARY = __webpack_require__(21);
var $export = __webpack_require__(6);
var redefine = __webpack_require__(47);
var hide = __webpack_require__(9);
var has = __webpack_require__(13);
var Iterators = __webpack_require__(18);
var $iterCreate = __webpack_require__(68);
var setToStringTag = __webpack_require__(26);
var getPrototypeOf = __webpack_require__(72);
var ITERATOR = __webpack_require__(4)('iterator');
var BUGGY = !([].keys && 'next' in [].keys()); // Safari has buggy iterators w/o `next`
var FF_ITERATOR = '@@iterator';
var KEYS = 'keys';
var VALUES = 'values';

var returnThis = function () { return this; };

module.exports = function (Base, NAME, Constructor, next, DEFAULT, IS_SET, FORCED) {
  $iterCreate(Constructor, NAME, next);
  var getMethod = function (kind) {
    if (!BUGGY && kind in proto) return proto[kind];
    switch (kind) {
      case KEYS: return function keys() { return new Constructor(this, kind); };
      case VALUES: return function values() { return new Constructor(this, kind); };
    } return function entries() { return new Constructor(this, kind); };
  };
  var TAG = NAME + ' Iterator';
  var DEF_VALUES = DEFAULT == VALUES;
  var VALUES_BUG = false;
  var proto = Base.prototype;
  var $native = proto[ITERATOR] || proto[FF_ITERATOR] || DEFAULT && proto[DEFAULT];
  var $default = (!BUGGY && $native) || getMethod(DEFAULT);
  var $entries = DEFAULT ? !DEF_VALUES ? $default : getMethod('entries') : undefined;
  var $anyNative = NAME == 'Array' ? proto.entries || $native : $native;
  var methods, key, IteratorPrototype;
  // Fix native
  if ($anyNative) {
    IteratorPrototype = getPrototypeOf($anyNative.call(new Base()));
    if (IteratorPrototype !== Object.prototype && IteratorPrototype.next) {
      // Set @@toStringTag to native iterators
      setToStringTag(IteratorPrototype, TAG, true);
      // fix for some old engines
      if (!LIBRARY && !has(IteratorPrototype, ITERATOR)) hide(IteratorPrototype, ITERATOR, returnThis);
    }
  }
  // fix Array#{values, @@iterator}.name in V8 / FF
  if (DEF_VALUES && $native && $native.name !== VALUES) {
    VALUES_BUG = true;
    $default = function values() { return $native.call(this); };
  }
  // Define iterator
  if ((!LIBRARY || FORCED) && (BUGGY || VALUES_BUG || !proto[ITERATOR])) {
    hide(proto, ITERATOR, $default);
  }
  // Plug for library
  Iterators[NAME] = $default;
  Iterators[TAG] = returnThis;
  if (DEFAULT) {
    methods = {
      values: DEF_VALUES ? $default : getMethod(VALUES),
      keys: IS_SET ? $default : getMethod(KEYS),
      entries: $entries
    };
    if (FORCED) for (key in methods) {
      if (!(key in proto)) redefine(proto, key, methods[key]);
    } else $export($export.P + $export.F * (BUGGY || VALUES_BUG), NAME, methods);
  }
  return methods;
};


/***/ }),
/* 46 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = !__webpack_require__(12) && !__webpack_require__(14)(function () {
  return Object.defineProperty(__webpack_require__(31)('div'), 'a', { get: function () { return 7; } }).a != 7;
});


/***/ }),
/* 47 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = __webpack_require__(9);


/***/ }),
/* 48 */
/***/ (function(module, exports, __webpack_require__) {

// 19.1.2.2 / 15.2.3.5 Object.create(O [, Properties])
var anObject = __webpack_require__(7);
var dPs = __webpack_require__(69);
var enumBugKeys = __webpack_require__(35);
var IE_PROTO = __webpack_require__(33)('IE_PROTO');
var Empty = function () { /* empty */ };
var PROTOTYPE = 'prototype';

// Create object with fake `null` prototype: use iframe Object with cleared prototype
var createDict = function () {
  // Thrash, waste and sodomy: IE GC bug
  var iframe = __webpack_require__(31)('iframe');
  var i = enumBugKeys.length;
  var lt = '<';
  var gt = '>';
  var iframeDocument;
  iframe.style.display = 'none';
  __webpack_require__(52).appendChild(iframe);
  iframe.src = 'javascript:'; // eslint-disable-line no-script-url
  // createDict = iframe.contentWindow.Object;
  // html.removeChild(iframe);
  iframeDocument = iframe.contentWindow.document;
  iframeDocument.open();
  iframeDocument.write(lt + 'script' + gt + 'document.F=Object' + lt + '/script' + gt);
  iframeDocument.close();
  createDict = iframeDocument.F;
  while (i--) delete createDict[PROTOTYPE][enumBugKeys[i]];
  return createDict();
};

module.exports = Object.create || function create(O, Properties) {
  var result;
  if (O !== null) {
    Empty[PROTOTYPE] = anObject(O);
    result = new Empty();
    Empty[PROTOTYPE] = null;
    // add "__proto__" for Object.getPrototypeOf polyfill
    result[IE_PROTO] = O;
  } else result = createDict();
  return Properties === undefined ? result : dPs(result, Properties);
};


/***/ }),
/* 49 */
/***/ (function(module, exports, __webpack_require__) {

var has = __webpack_require__(13);
var toIObject = __webpack_require__(15);
var arrayIndexOf = __webpack_require__(70)(false);
var IE_PROTO = __webpack_require__(33)('IE_PROTO');

module.exports = function (object, names) {
  var O = toIObject(object);
  var i = 0;
  var result = [];
  var key;
  for (key in O) if (key != IE_PROTO) has(O, key) && result.push(key);
  // Don't enum bug & hidden keys
  while (names.length > i) if (has(O, key = names[i++])) {
    ~arrayIndexOf(result, key) || result.push(key);
  }
  return result;
};


/***/ }),
/* 50 */
/***/ (function(module, exports, __webpack_require__) {

// fallback for non-array-like ES3 and non-enumerable old V8 strings
var cof = __webpack_require__(20);
// eslint-disable-next-line no-prototype-builtins
module.exports = Object('z').propertyIsEnumerable(0) ? Object : function (it) {
  return cof(it) == 'String' ? it.split('') : Object(it);
};


/***/ }),
/* 51 */
/***/ (function(module, exports, __webpack_require__) {

// 7.1.15 ToLength
var toInteger = __webpack_require__(29);
var min = Math.min;
module.exports = function (it) {
  return it > 0 ? min(toInteger(it), 0x1fffffffffffff) : 0; // pow(2, 53) - 1 == 9007199254740991
};


/***/ }),
/* 52 */
/***/ (function(module, exports, __webpack_require__) {

var document = __webpack_require__(2).document;
module.exports = document && document.documentElement;


/***/ }),
/* 53 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(73);
var global = __webpack_require__(2);
var hide = __webpack_require__(9);
var Iterators = __webpack_require__(18);
var TO_STRING_TAG = __webpack_require__(4)('toStringTag');

var DOMIterables = ('CSSRuleList,CSSStyleDeclaration,CSSValueList,ClientRectList,DOMRectList,DOMStringList,' +
  'DOMTokenList,DataTransferItemList,FileList,HTMLAllCollection,HTMLCollection,HTMLFormElement,HTMLSelectElement,' +
  'MediaList,MimeTypeArray,NamedNodeMap,NodeList,PaintRequestList,Plugin,PluginArray,SVGLengthList,SVGNumberList,' +
  'SVGPathSegList,SVGPointList,SVGStringList,SVGTransformList,SourceBufferList,StyleSheetList,TextTrackCueList,' +
  'TextTrackList,TouchList').split(',');

for (var i = 0; i < DOMIterables.length; i++) {
  var NAME = DOMIterables[i];
  var Collection = global[NAME];
  var proto = Collection && Collection.prototype;
  if (proto && !proto[TO_STRING_TAG]) hide(proto, TO_STRING_TAG, NAME);
  Iterators[NAME] = Iterators.Array;
}


/***/ }),
/* 54 */
/***/ (function(module, exports, __webpack_require__) {

// 19.1.2.7 / 15.2.3.4 Object.getOwnPropertyNames(O)
var $keys = __webpack_require__(49);
var hiddenKeys = __webpack_require__(35).concat('length', 'prototype');

exports.f = Object.getOwnPropertyNames || function getOwnPropertyNames(O) {
  return $keys(O, hiddenKeys);
};


/***/ }),
/* 55 */
/***/ (function(module, exports) {



/***/ }),
/* 56 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";

// EXTERNAL MODULE: ./packages/utils/install.js
var install = __webpack_require__(40);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/icon/index.vue
//
//
//
//
//
//
//



/* harmony default export */ var icon = ({
  install: install["a" /* default */],

  name: 'van-icon',

  props: {
    name: String,
    info: String
  }
});
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-be7494ce","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/icon/index.vue
var render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('i',_vm._g({staticClass:"van-icon",class:("van-icon-" + _vm.name)},_vm.$listeners),[_vm._t("default"),(_vm.info)?_c('div',{staticClass:"van-icon__info"},[_vm._v(_vm._s(_vm.info))]):_vm._e()],2)}
var staticRenderFns = []
var esExports = { render: render, staticRenderFns: staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_icon = (esExports);
// CONCATENATED MODULE: ./packages/icon/index.vue
var normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var __vue_template_functional__ = false
/* styles */
var __vue_styles__ = null
/* scopeId */
var __vue_scopeId__ = null
/* moduleIdentifier (server only) */
var __vue_module_identifier__ = null
var Component = normalizeComponent(
  icon,
  selectortype_template_index_0_packages_icon,
  __vue_template_functional__,
  __vue_styles__,
  __vue_scopeId__,
  __vue_module_identifier__
)

/* harmony default export */ var packages_icon = __webpack_exports__["a"] = (Component.exports);


/***/ }),
/* 57 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";

// EXTERNAL MODULE: ./packages/utils/install.js
var install = __webpack_require__(40);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/loading/index.vue
//
//
//
//
//
//
//
//



/* harmony default export */ var loading = ({
  install: install["a" /* default */],
  name: 'van-loading',
  props: {
    type: {
      type: String,
      default: 'gradient-circle'
    },
    color: {
      type: String,
      default: 'black'
    }
  }
});
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-78d9910a","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/loading/index.vue
var render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-loading",class:['van-loading--' + _vm.type, 'van-loading--' + _vm.color]},[_c('span',{staticClass:"van-loading__spinner",class:['van-loading__spinner--' + _vm.type, 'van-loading__spinner--' + _vm.color]},_vm._l((12),function(item){return (_vm.type === 'spinner')?_c('i'):_vm._e()}))])}
var staticRenderFns = []
var esExports = { render: render, staticRenderFns: staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_loading = (esExports);
// CONCATENATED MODULE: ./packages/loading/index.vue
var normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var __vue_template_functional__ = false
/* styles */
var __vue_styles__ = null
/* scopeId */
var __vue_scopeId__ = null
/* moduleIdentifier (server only) */
var __vue_module_identifier__ = null
var Component = normalizeComponent(
  loading,
  selectortype_template_index_0_packages_loading,
  __vue_template_functional__,
  __vue_styles__,
  __vue_scopeId__,
  __vue_module_identifier__
)

/* harmony default export */ var packages_loading = __webpack_exports__["a"] = (Component.exports);


/***/ }),
/* 58 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = { "default": __webpack_require__(89), __esModule: true };

/***/ }),
/* 59 */
/***/ (function(module, exports, __webpack_require__) {

// getting tag from 19.1.3.6 Object.prototype.toString()
var cof = __webpack_require__(20);
var TAG = __webpack_require__(4)('toStringTag');
// ES3 wrong here
var ARG = cof(function () { return arguments; }()) == 'Arguments';

// fallback for IE11 Script Access Denied error
var tryGet = function (it, key) {
  try {
    return it[key];
  } catch (e) { /* empty */ }
};

module.exports = function (it) {
  var O, T, B;
  return it === undefined ? 'Undefined' : it === null ? 'Null'
    // @@toStringTag case
    : typeof (T = tryGet(O = Object(it), TAG)) == 'string' ? T
    // builtinTag case
    : ARG ? cof(O)
    // ES3 arguments fallback
    : (B = cof(O)) == 'Object' && typeof O.callee == 'function' ? 'Arguments' : B;
};


/***/ }),
/* 60 */
/***/ (function(module, exports, __webpack_require__) {

// 7.3.20 SpeciesConstructor(O, defaultConstructor)
var anObject = __webpack_require__(7);
var aFunction = __webpack_require__(23);
var SPECIES = __webpack_require__(4)('species');
module.exports = function (O, D) {
  var C = anObject(O).constructor;
  var S;
  return C === undefined || (S = anObject(C)[SPECIES]) == undefined ? D : aFunction(S);
};


/***/ }),
/* 61 */
/***/ (function(module, exports, __webpack_require__) {

var ctx = __webpack_require__(22);
var invoke = __webpack_require__(96);
var html = __webpack_require__(52);
var cel = __webpack_require__(31);
var global = __webpack_require__(2);
var process = global.process;
var setTask = global.setImmediate;
var clearTask = global.clearImmediate;
var MessageChannel = global.MessageChannel;
var Dispatch = global.Dispatch;
var counter = 0;
var queue = {};
var ONREADYSTATECHANGE = 'onreadystatechange';
var defer, channel, port;
var run = function () {
  var id = +this;
  // eslint-disable-next-line no-prototype-builtins
  if (queue.hasOwnProperty(id)) {
    var fn = queue[id];
    delete queue[id];
    fn();
  }
};
var listener = function (event) {
  run.call(event.data);
};
// Node.js 0.9+ & IE10+ has setImmediate, otherwise:
if (!setTask || !clearTask) {
  setTask = function setImmediate(fn) {
    var args = [];
    var i = 1;
    while (arguments.length > i) args.push(arguments[i++]);
    queue[++counter] = function () {
      // eslint-disable-next-line no-new-func
      invoke(typeof fn == 'function' ? fn : Function(fn), args);
    };
    defer(counter);
    return counter;
  };
  clearTask = function clearImmediate(id) {
    delete queue[id];
  };
  // Node.js 0.8-
  if (__webpack_require__(20)(process) == 'process') {
    defer = function (id) {
      process.nextTick(ctx(run, id, 1));
    };
  // Sphere (JS game engine) Dispatch API
  } else if (Dispatch && Dispatch.now) {
    defer = function (id) {
      Dispatch.now(ctx(run, id, 1));
    };
  // Browsers with MessageChannel, includes WebWorkers
  } else if (MessageChannel) {
    channel = new MessageChannel();
    port = channel.port2;
    channel.port1.onmessage = listener;
    defer = ctx(port.postMessage, port, 1);
  // Browsers with postMessage, skip WebWorkers
  // IE8 has postMessage, but it's sync & typeof its postMessage is 'object'
  } else if (global.addEventListener && typeof postMessage == 'function' && !global.importScripts) {
    defer = function (id) {
      global.postMessage(id + '', '*');
    };
    global.addEventListener('message', listener, false);
  // IE8-
  } else if (ONREADYSTATECHANGE in cel('script')) {
    defer = function (id) {
      html.appendChild(cel('script'))[ONREADYSTATECHANGE] = function () {
        html.removeChild(this);
        run.call(id);
      };
    };
  // Rest old browsers
  } else {
    defer = function (id) {
      setTimeout(ctx(run, id, 1), 0);
    };
  }
}
module.exports = {
  set: setTask,
  clear: clearTask
};


/***/ }),
/* 62 */
/***/ (function(module, exports) {

module.exports = function (exec) {
  try {
    return { e: false, v: exec() };
  } catch (e) {
    return { e: true, v: e };
  }
};


/***/ }),
/* 63 */
/***/ (function(module, exports, __webpack_require__) {

var anObject = __webpack_require__(7);
var isObject = __webpack_require__(11);
var newPromiseCapability = __webpack_require__(41);

module.exports = function (C, x) {
  anObject(C);
  if (isObject(x) && x.constructor === C) return x;
  var promiseCapability = newPromiseCapability.f(C);
  var resolve = promiseCapability.resolve;
  resolve(x);
  return promiseCapability.promise;
};


/***/ }),
/* 64 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
Object.defineProperty(__webpack_exports__, "__esModule", { value: true });

// EXTERNAL MODULE: ./packages/utils/index.js + 2 modules
var utils = __webpack_require__(1);

// EXTERNAL MODULE: ./node_modules/babel-runtime/helpers/extends.js
var helpers_extends = __webpack_require__(5);
var extends_default = /*#__PURE__*/__webpack_require__.n(helpers_extends);

// EXTERNAL MODULE: ./node_modules/babel-runtime/core-js/object/assign.js
var object_assign = __webpack_require__(16);
var assign_default = /*#__PURE__*/__webpack_require__.n(object_assign);

// EXTERNAL MODULE: external {"root":"Vue","commonjs":"vue","commonjs2":"vue","amd":"vue"}
var external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue__ = __webpack_require__(8);
var external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default = /*#__PURE__*/__webpack_require__.n(external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue__);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/mixins/popup/Modal.vue

//
//
//
//
//
//
//
//
//
//
//
//
//

/* harmony default export */ var Modal = ({
  name: 'van-modal',

  props: {
    visible: Boolean,
    zIndex: Number,
    className: String,
    customStyle: Object
  },

  computed: {
    style: function style() {
      return extends_default()({
        zIndex: this.zIndex
      }, this.customStyle);
    }
  }
});
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-5458b9f5","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/mixins/popup/Modal.vue
var render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('transition',{attrs:{"name":"van-fade"}},[_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.visible),expression:"visible"}],staticClass:"van-modal",class:_vm.className,style:(_vm.style),on:{"touchmove":function($event){$event.preventDefault();$event.stopPropagation();},"click":function($event){_vm.$emit('click', $event)}}})])}
var staticRenderFns = []
var esExports = { render: render, staticRenderFns: staticRenderFns }
/* harmony default export */ var popup_Modal = (esExports);
// CONCATENATED MODULE: ./packages/mixins/popup/Modal.vue
var normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var __vue_template_functional__ = false
/* styles */
var __vue_styles__ = null
/* scopeId */
var __vue_scopeId__ = null
/* moduleIdentifier (server only) */
var __vue_module_identifier__ = null
var Component = normalizeComponent(
  Modal,
  popup_Modal,
  __vue_template_functional__,
  __vue_styles__,
  __vue_scopeId__,
  __vue_module_identifier__
)

/* harmony default export */ var mixins_popup_Modal = (Component.exports);

// CONCATENATED MODULE: ./packages/mixins/popup/context.js
var PopupContext = {
  idSeed: 1,
  zIndex: 2000,
  stack: [],

  plusKey: function plusKey(key) {
    return this[key]++;
  },


  get top() {
    return this.stack[this.stack.length - 1];
  }
};

/* harmony default export */ var popup_context = (PopupContext);
// CONCATENATED MODULE: ./packages/mixins/popup/manager.js






var modalDefaultConfig = {
  className: '',
  customStyle: {}
};

var manager = {
  getModal: function getModal() {
    var modal = popup_context.modal;


    if (!modal) {
      var ModalConstructor = external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.extend(mixins_popup_Modal);
      modal = new ModalConstructor({
        el: document.createElement('div')
      });
      modal.$on('click', function () {
        manager.onClickOverlay();
      });

      popup_context.modal = modal;
    }

    return modal;
  },


  // close popup when click modal && closeOnClickOverlay is true
  onClickOverlay: function onClickOverlay() {
    var top = popup_context.top;

    if (top) {
      var instance = top.instance;

      if (instance && instance.closeOnClickOverlay) {
        instance.close();
      }
    }
  },
  openModal: function openModal(instance, config) {
    var id = config.id,
        dom = config.dom;

    var exist = popup_context.stack.some(function (item) {
      return item.id === id;
    });

    if (!exist) {
      var targetNode = dom && dom.parentNode && dom.parentNode.nodeType !== 11 ? dom.parentNode : document.body;
      popup_context.stack.push({ instance: instance, id: id, config: config, targetNode: targetNode });
      this.updateModal();
    };
  },
  closeModal: function closeModal(id) {
    var stack = popup_context.stack;


    if (stack.length) {
      if (popup_context.top.id === id) {
        stack.pop();
        this.updateModal();
      } else {
        popup_context.stack = stack.filter(function (item) {
          return item.id !== id;
        });
      }
    }
  },
  updateModal: function updateModal() {
    var modal = this.getModal();

    if (modal.$el.parentNode) {
      modal.visible = false;
    }

    if (popup_context.top) {
      var _context$top = popup_context.top,
          targetNode = _context$top.targetNode,
          config = _context$top.config;


      targetNode.appendChild(modal.$el);
      assign_default()(modal, extends_default()({}, modalDefaultConfig, config, {
        visible: true
      }));
    }
  }
};

/* harmony default export */ var popup_manager = (manager);
// CONCATENATED MODULE: ./packages/utils/scroll.js


/* harmony default export */ var utils_scroll = ({
  debounce: function debounce(func, wait, immediate) {
    var timeout = void 0,
        args = void 0,
        context = void 0,
        timestamp = void 0,
        result = void 0;
    return function () {
      context = this;
      args = arguments;
      timestamp = new Date();
      var later = function later() {
        var last = new Date() - timestamp;
        if (last < wait) {
          timeout = setTimeout(later, wait - last);
        } else {
          timeout = null;
          result = func.apply(context, args);
        }
      };
      if (!timeout) {
        timeout = setTimeout(later, wait);
      }
      return result;
    };
  },


  /* 找到最近的触发滚动事件的元素
   * @param {Element} element
  * @param {Element} rootElement
  * @return {Element | window}
   */
  getScrollEventTarget: function getScrollEventTarget(element) {
    var rootParent = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : window;

    var currentNode = element;
    // bugfix, see http://w3help.org/zh-cn/causes/SD9013 and http://stackoverflow.com/questions/17016740/onscroll-function-is-not-working-for-chrome
    while (currentNode && currentNode.tagName !== 'HTML' && currentNode.tagName !== 'BODY' && currentNode.nodeType === 1 && currentNode !== rootParent) {
      var overflowY = this.getComputedStyle(currentNode).overflowY;
      if (overflowY === 'scroll' || overflowY === 'auto') {
        return currentNode;
      }
      currentNode = currentNode.parentNode;
    }
    return rootParent;
  },


  // 判断元素是否被加入到页面节点内
  isAttached: function isAttached(element) {
    var currentNode = element.parentNode;
    while (currentNode) {
      if (currentNode.tagName === 'HTML') {
        return true;
      }
      if (currentNode.nodeType === 11) {
        return false;
      }
      currentNode = currentNode.parentNode;
    }
    return false;
  },


  // 获取滚动高度
  getScrollTop: function getScrollTop(element) {
    return 'scrollTop' in element ? element.scrollTop : element.pageYOffset;
  },


  // 设置滚动高度
  setScrollTop: function setScrollTop(element, value) {
    'scrollTop' in element ? element.scrollTop = value : element.scrollTo(element.scrollX, value);
  },


  // 获取元素距离顶部高度
  getElementTop: function getElementTop(element) {
    return (element === window ? 0 : element.getBoundingClientRect().top) + this.getScrollTop(window);
  },
  getVisibleHeight: function getVisibleHeight(element) {
    return element === window ? element.innerHeight : element.getBoundingClientRect().height;
  },


  getComputedStyle: !utils["f" /* isServer */] && document.defaultView.getComputedStyle.bind(document.defaultView)
});
// CONCATENATED MODULE: ./packages/utils/event.js


var supportsPassive = false;
if (!utils["f" /* isServer */]) {
  try {
    var opts = {};
    Object.defineProperty(opts, 'passive', {
      get: function get() {
        /* istanbul ignore next */
        supportsPassive = true;
      }
    });
    window.addEventListener('test-passive', null, opts);
  } catch (e) {}
}

function on(target, event, handler) {
  var passive = arguments.length > 3 && arguments[3] !== undefined ? arguments[3] : false;

  !utils["f" /* isServer */] && target.addEventListener(event, handler, supportsPassive ? { capture: false, passive: passive } : false);
}

function off(target, event, handler) {
  !utils["f" /* isServer */] && target.removeEventListener(event, handler);
}
// CONCATENATED MODULE: ./packages/mixins/popup/index.js





/* harmony default export */ var popup = ({
  props: {
    // whether to show popup
    value: Boolean,
    // whether to show overlay
    overlay: Boolean,
    // overlay custom style
    overlayStyle: Object,
    // overlay custom class name
    overlayClass: String,
    // whether to close popup when click overlay
    closeOnClickOverlay: Boolean,
    // z-index
    zIndex: [String, Number],
    // prevent touchmove scroll
    preventScroll: Boolean,
    // prevent body scroll
    lockOnScroll: {
      type: Boolean,
      default: true
    }
  },

  watch: {
    value: function value(val) {
      this[val ? 'open' : 'close']();
    }
  },

  beforeMount: function beforeMount() {
    this._popupId = 'popup-' + popup_context.plusKey('idSeed');
  },
  data: function data() {
    return {
      opened: false,
      pos: {
        x: 0,
        y: 0
      }
    };
  },


  methods: {
    recordPosition: function recordPosition(e) {
      this.pos = {
        x: e.touches[0].clientX,
        y: e.touches[0].clientY
      };
    },
    watchTouchMove: function watchTouchMove(e) {
      var pos = this.pos;

      var dx = e.touches[0].clientX - pos.x;
      var dy = e.touches[0].clientY - pos.y;
      var direction = dy > 0 ? '10' : '01';
      var el = utils_scroll.getScrollEventTarget(e.target, this.$el);
      var scrollHeight = el.scrollHeight,
          offsetHeight = el.offsetHeight,
          scrollTop = el.scrollTop;

      var isVertical = Math.abs(dx) < Math.abs(dy);

      var status = '11';

      if (scrollTop === 0) {
        status = offsetHeight >= scrollHeight ? '00' : '01';
      } else if (scrollTop + offsetHeight >= scrollHeight) {
        status = '10';
      }

      if (status !== '11' && isVertical && !(parseInt(status, 2) & parseInt(direction, 2))) {
        e.preventDefault();
        e.stopPropagation();
      }
    },
    open: function open() {
      if (this.opened || this.$isServer) {
        return;
      }

      this.$emit('input', true);

      // 如果属性中传入了`zIndex`，则覆盖`context`中对应的`zIndex`
      if (this.zIndex !== undefined) {
        popup_context.zIndex = this.zIndex;
      }

      if (this.overlay) {
        popup_manager.openModal(this, {
          id: this._popupId,
          dom: this.$el,
          zIndex: popup_context.plusKey('zIndex'),
          className: this.overlayClass,
          customStyle: this.overlayStyle
        });

        if (this.lockOnScroll) {
          document.body.classList.add('van-overflow-hidden');
        }
      }

      this.$el.style.zIndex = popup_context.plusKey('zIndex');
      this.opened = true;

      if (this.preventScroll) {
        on(document, 'touchstart', this.recordPosition);
        on(document, 'touchmove', this.watchTouchMove);
      }
    },
    close: function close() {
      if (!this.opened || this.$isServer) {
        return;
      }

      this.$emit('input', false);
      this.opened = false;
      this.doAfterClose();
    },
    doAfterClose: function doAfterClose() {
      popup_manager.closeModal(this._popupId);

      if (this.lockOnScroll) {
        document.body.classList.remove('van-overflow-hidden');
      }

      if (this.preventScroll) {
        off(document, 'touchstart', this.recordPosition);
        off(document, 'touchmove', this.watchTouchMove);
      }
    }
  },

  beforeDestroy: function beforeDestroy() {
    this.doAfterClose();
  }
});
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/actionsheet/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var actionsheet = (Object(utils["b" /* create */])({
  name: 'van-actionsheet',

  mixins: [popup],

  props: {
    value: Boolean,
    title: String,
    cancelText: String,
    actions: {
      type: Array,
      default: function _default() {
        return [];
      }
    },
    overlay: {
      type: Boolean,
      default: true
    },
    closeOnClickOverlay: {
      type: Boolean,
      default: true
    }
  },

  mounted: function mounted() {
    this.value && this.open();
  },


  methods: {
    onClickItem: function onClickItem(item) {
      if (typeof item.callback === 'function') {
        item.callback(item);
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-261f5d2c","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/actionsheet/index.vue
var actionsheet_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('transition',{attrs:{"name":"van-actionsheet-float"}},[_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.value),expression:"value"}],staticClass:"van-actionsheet",class:{ 'van-actionsheet--withtitle': _vm.title }},[(_vm.title)?_c('div',{staticClass:"van-actionsheet__header van-hairline--top-bottom"},[_c('div',{domProps:{"textContent":_vm._s(_vm.title)}}),_c('icon',{attrs:{"name":"close"},on:{"click":function($event){$event.stopPropagation();_vm.$emit('input', false)}}})],1):_vm._e(),(!_vm.title)?_c('ul',{staticClass:"van-actionsheet__list"},_vm._l((_vm.actions),function(item,index){return _c('li',{key:index,staticClass:"van-actionsheet__item van-hairline--top",class:[item.className, { 'van-actionsheet__item--loading': item.loading }],on:{"click":function($event){$event.stopPropagation();_vm.onClickItem(item)}}},[(!item.loading)?[_c('span',{staticClass:"van-actionsheet__name"},[_vm._v(_vm._s(item.name))]),(item.subname)?_c('span',{staticClass:"van-actionsheet__subname"},[_vm._v(_vm._s(item.subname))]):_vm._e()]:_c('loading',{staticClass:"van-actionsheet__loading",attrs:{"type":"circle"}})],2)})):_vm._e(),(_vm.cancelText)?_c('div',{staticClass:"van-actionsheet__item van-actionsheet__cancel van-hairline--top",domProps:{"textContent":_vm._s(_vm.cancelText)},on:{"click":function($event){$event.stopPropagation();_vm.$emit('input', false)}}}):_c('div',{staticClass:"van-actionsheet__content"},[_vm._t("default")],2)])])}
var actionsheet_staticRenderFns = []
var actionsheet_esExports = { render: actionsheet_render, staticRenderFns: actionsheet_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_actionsheet = (actionsheet_esExports);
// CONCATENATED MODULE: ./packages/actionsheet/index.vue
var actionsheet_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var actionsheet___vue_template_functional__ = false
/* styles */
var actionsheet___vue_styles__ = null
/* scopeId */
var actionsheet___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var actionsheet___vue_module_identifier__ = null
var actionsheet_Component = actionsheet_normalizeComponent(
  actionsheet,
  selectortype_template_index_0_packages_actionsheet,
  actionsheet___vue_template_functional__,
  actionsheet___vue_styles__,
  actionsheet___vue_scopeId__,
  actionsheet___vue_module_identifier__
)

/* harmony default export */ var packages_actionsheet = (actionsheet_Component.exports);

// CONCATENATED MODULE: ./packages/mixins/router-link.js
/**
 * add Vue-Router support
 */

/* harmony default export */ var router_link = ({
  props: {
    url: String,
    replace: Boolean,
    to: [String, Object]
  },

  methods: {
    routerLink: function routerLink() {
      var to = this.to,
          url = this.url,
          $router = this.$router,
          replace = this.replace;

      if (to && $router) {
        $router[replace ? 'replace' : 'push'](to);
      } else if (url) {
        replace ? location.replace(url) : location.href = url;
      }
    }
  }
});
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/cell/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var cell = (Object(utils["b" /* create */])({
  name: 'van-cell',

  mixins: [router_link],

  props: {
    icon: String,
    title: String,
    label: String,
    isLink: Boolean,
    required: Boolean,
    clickable: Boolean,
    value: [String, Number],
    border: {
      type: Boolean,
      default: true
    }
  },

  methods: {
    onClick: function onClick() {
      this.$emit('click');
      this.routerLink();
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-b89df8c4","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/cell/index.vue
var cell_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-cell",class:{
    'van-hairline': _vm.border,
    'van-cell--required': _vm.required,
    'van-cell--clickable': _vm.isLink || _vm.clickable
  },on:{"click":_vm.onClick}},[(_vm.title || _vm.icon || _vm.$slots.title || _vm.$slots.icon)?_c('div',{staticClass:"van-cell__title"},[_vm._t("icon",[(_vm.icon)?_c('icon',{attrs:{"name":_vm.icon}}):_vm._e()]),_vm._t("title",[_c('span',{staticClass:"van-cell__text",domProps:{"textContent":_vm._s(_vm.title)}}),(_vm.label)?_c('span',{staticClass:"van-cell__label",domProps:{"textContent":_vm._s(_vm.label)}}):_vm._e()])],2):_vm._e(),(_vm.value || _vm.$slots.default)?_c('div',{staticClass:"van-cell__value",class:{
      'van-cell__value--link': _vm.isLink,
      'van-cell__value--alone': !_vm.$slots.title && !_vm.title && !_vm.label
    }},[_vm._t("default",[_c('span',{domProps:{"textContent":_vm._s(_vm.value)}})])],2):_vm._e(),_vm._t("right-icon",[(_vm.isLink)?_c('icon',{staticClass:"van-cell__right-icon",attrs:{"name":"arrow"}}):_vm._e()]),_vm._t("extra")],2)}
var cell_staticRenderFns = []
var cell_esExports = { render: cell_render, staticRenderFns: cell_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_cell = (cell_esExports);
// CONCATENATED MODULE: ./packages/cell/index.vue
var cell_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var cell___vue_template_functional__ = false
/* styles */
var cell___vue_styles__ = null
/* scopeId */
var cell___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var cell___vue_module_identifier__ = null
var cell_Component = cell_normalizeComponent(
  cell,
  selectortype_template_index_0_packages_cell,
  cell___vue_template_functional__,
  cell___vue_styles__,
  cell___vue_scopeId__,
  cell___vue_module_identifier__
)

/* harmony default export */ var packages_cell = (cell_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/field/index.vue

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var field = (Object(utils["b" /* create */])({
  name: 'van-field',

  inheritAttrs: false,

  components: {
    Cell: packages_cell
  },

  props: {
    type: {
      type: String,
      default: 'text'
    },
    value: {},
    icon: String,
    label: String,
    error: Boolean,
    border: Boolean,
    required: Boolean,
    autosize: Boolean,
    errorMessage: String,
    onIconClick: {
      type: Function,
      default: function _default() {}
    }
  },

  watch: {
    value: function value() {
      if (this.autosize && this.type === 'textarea') {
        this.$nextTick(this.adjustSize);
      }
    }
  },

  mounted: function mounted() {
    if (this.autosize && this.type === 'textarea') {
      var el = this.$refs.textarea;
      var scrollHeight = el.scrollHeight;
      if (scrollHeight !== 0) {
        el.style.height = scrollHeight + 'px';
      }
      el.style.overflowY = 'hidden';
    }
  },


  computed: {
    hasIcon: function hasIcon() {
      return this.$slots.icon || this.icon;
    },
    listeners: function listeners() {
      return extends_default()({}, this.$listeners, {
        input: this.onInput,
        keypress: this.onKeypress
      });
    }
  },

  methods: {
    onInput: function onInput(event) {
      this.$emit('input', event.target.value);
    },
    onClickIcon: function onClickIcon() {
      this.$emit('click-icon');
      this.onIconClick();
    },
    onKeypress: function onKeypress(event) {
      if (this.type === 'number') {
        var keyCode = event.keyCode;

        var allowPoint = this.value.indexOf('.') === -1;
        var isValidKey = keyCode >= 48 && keyCode <= 57 || keyCode === 46 && allowPoint;
        if (!isValidKey) {
          event.preventDefault();
        }
      }
      this.$emit('keypress', event);
    },
    adjustSize: function adjustSize() {
      var el = this.$refs.textarea;
      el.style.height = 'auto';
      el.style.height = el.scrollHeight + 'px';
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-3c3553bf","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/field/index.vue
var field_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('cell',{staticClass:"van-field",class:{
    'van-field--has-textarea': _vm.type === 'textarea',
    'van-field--nolabel': !_vm.label,
    'van-field--disabled': _vm.$attrs.disabled,
    'van-field--error': _vm.error,
    'van-field--border': _vm.border,
    'van-field--autosize': _vm.autosize,
    'van-field--has-icon': _vm.hasIcon,
    'van-hairline--surround': _vm.border
  },attrs:{"title":_vm.label,"required":_vm.required}},[(_vm.type === 'textarea')?_c('textarea',_vm._g(_vm._b({ref:"textarea",staticClass:"van-field__control",domProps:{"value":_vm.value}},'textarea',_vm.$attrs,false),_vm.listeners)):_c('input',_vm._g(_vm._b({staticClass:"van-field__control",attrs:{"type":_vm.type},domProps:{"value":_vm.value}},'input',_vm.$attrs,false),_vm.listeners)),(_vm.errorMessage)?_c('div',{staticClass:"van-field__error-message",domProps:{"textContent":_vm._s(_vm.errorMessage)}}):_vm._e(),(_vm.hasIcon)?_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.$slots.icon || _vm.value),expression:"$slots.icon || value"}],staticClass:"van-field__icon",on:{"touchstart":function($event){$event.preventDefault();_vm.onClickIcon($event)}}},[_vm._t("icon",[_c('icon',{attrs:{"name":_vm.icon}})])],2):_vm._e()])}
var field_staticRenderFns = []
var field_esExports = { render: field_render, staticRenderFns: field_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_field = (field_esExports);
// CONCATENATED MODULE: ./packages/field/index.vue
var field_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var field___vue_template_functional__ = false
/* styles */
var field___vue_styles__ = null
/* scopeId */
var field___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var field___vue_module_identifier__ = null
var field_Component = field_normalizeComponent(
  field,
  selectortype_template_index_0_packages_field,
  field___vue_template_functional__,
  field___vue_styles__,
  field___vue_scopeId__,
  field___vue_module_identifier__
)

/* harmony default export */ var packages_field = (field_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/cell-group/index.vue
//
//
//
//
//
//



/* harmony default export */ var cell_group = (Object(utils["b" /* create */])({
  name: 'van-cell-group',

  props: {
    border: {
      type: Boolean,
      default: true
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-78aa63ff","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/cell-group/index.vue
var cell_group_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-cell-group",class:{ 'van-hairline--top-bottom': _vm.border }},[_vm._t("default")],2)}
var cell_group_staticRenderFns = []
var cell_group_esExports = { render: cell_group_render, staticRenderFns: cell_group_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_cell_group = (cell_group_esExports);
// CONCATENATED MODULE: ./packages/cell-group/index.vue
var cell_group_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var cell_group___vue_template_functional__ = false
/* styles */
var cell_group___vue_styles__ = null
/* scopeId */
var cell_group___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var cell_group___vue_module_identifier__ = null
var cell_group_Component = cell_group_normalizeComponent(
  cell_group,
  selectortype_template_index_0_packages_cell_group,
  cell_group___vue_template_functional__,
  cell_group___vue_styles__,
  cell_group___vue_scopeId__,
  cell_group___vue_module_identifier__
)

/* harmony default export */ var packages_cell_group = (cell_group_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/button/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var selectortype_script_index_0_packages_button = (Object(utils["b" /* create */])({
  name: 'van-button',

  props: {
    text: String,
    block: Boolean,
    loading: Boolean,
    disabled: Boolean,
    nativeType: String,
    bottomAction: Boolean,
    tag: {
      type: String,
      default: 'button'
    },
    type: {
      type: String,
      default: 'default'
    },
    size: {
      type: String,
      default: 'normal'
    }
  },

  methods: {
    onClick: function onClick(event) {
      if (!this.loading && !this.disabled) {
        this.$emit('click', event);
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-2b93bef4","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/button/index.vue
var button_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c(_vm.tag,{tag:"component",staticClass:"van-button",class:[
    'van-button--' + _vm.type,
    'van-button--' + _vm.size,
    {
      'van-button--disabled': _vm.disabled,
      'van-button--loading': _vm.loading,
      'van-button--block': _vm.block,
      'van-button--bottom-action': _vm.bottomAction
    }
  ],attrs:{"type":_vm.nativeType,"disabled":_vm.disabled},on:{"click":_vm.onClick}},[(_vm.loading)?_c('loading',{staticClass:"van-button__icon-loading",attrs:{"type":"circle","color":_vm.type === 'default' ? 'black' : 'white'}}):_vm._e(),_c('span',{staticClass:"van-button__text"},[_vm._t("default",[_vm._v(_vm._s(_vm.text))])],2)],1)}
var button_staticRenderFns = []
var button_esExports = { render: button_render, staticRenderFns: button_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_button = (button_esExports);
// CONCATENATED MODULE: ./packages/button/index.vue
var button_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var button___vue_template_functional__ = false
/* styles */
var button___vue_styles__ = null
/* scopeId */
var button___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var button___vue_module_identifier__ = null
var button_Component = button_normalizeComponent(
  selectortype_script_index_0_packages_button,
  selectortype_template_index_0_packages_button,
  button___vue_template_functional__,
  button___vue_styles__,
  button___vue_scopeId__,
  button___vue_module_identifier__
)

/* harmony default export */ var packages_button = (button_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/popup/index.vue
//
//
//
//
//
//
//
//




/* harmony default export */ var selectortype_script_index_0_packages_popup = (Object(utils["b" /* create */])({
  name: 'van-popup',

  mixins: [popup],

  props: {
    transition: String,
    overlay: {
      type: Boolean,
      default: true
    },
    lockOnScroll: {
      type: Boolean,
      default: false
    },
    closeOnClickOverlay: {
      type: Boolean,
      default: true
    },
    position: {
      type: String,
      default: ''
    }
  },

  data: function data() {
    var transition = this.transition || (this.position === '' ? 'van-fade' : 'popup-slide-' + this.position);
    return {
      currentValue: false,
      currentTransition: transition
    };
  },
  mounted: function mounted() {
    if (this.value) {
      this.open();
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-58214590","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/popup/index.vue
var popup_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('transition',{attrs:{"name":_vm.currentTransition}},[_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.value),expression:"value"}],staticClass:"van-popup",class:( _obj = {}, _obj[("van-popup--" + _vm.position)] = _vm.position, _obj )},[_vm._t("default")],2)])
var _obj;}
var popup_staticRenderFns = []
var popup_esExports = { render: popup_render, staticRenderFns: popup_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_popup = (popup_esExports);
// CONCATENATED MODULE: ./packages/popup/index.vue
var popup_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var popup___vue_template_functional__ = false
/* styles */
var popup___vue_styles__ = null
/* scopeId */
var popup___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var popup___vue_module_identifier__ = null
var popup_Component = popup_normalizeComponent(
  selectortype_script_index_0_packages_popup,
  selectortype_template_index_0_packages_popup,
  popup___vue_template_functional__,
  popup___vue_styles__,
  popup___vue_scopeId__,
  popup___vue_module_identifier__
)

/* harmony default export */ var packages_popup = (popup_Component.exports);

// EXTERNAL MODULE: ./node_modules/babel-runtime/helpers/typeof.js
var helpers_typeof = __webpack_require__(17);
var typeof_default = /*#__PURE__*/__webpack_require__.n(helpers_typeof);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/toast/toast.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



var STYLE_LIST = ['success', 'fail', 'loading'];

/* harmony default export */ var toast_toast = (Object(utils["b" /* create */])({
  name: 'van-toast',

  props: {
    mask: Boolean,
    message: [String, Number],
    forbidClick: Boolean,
    type: {
      type: String,
      default: 'text'
    },
    position: {
      type: String,
      default: 'middle'
    }
  },

  data: function data() {
    return {
      visible: false
    };
  },


  computed: {
    displayStyle: function displayStyle() {
      return STYLE_LIST.indexOf(this.type) !== -1 ? 'default' : this.type;
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-59fc4640","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/toast/toast.vue
var toast_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('transition',{attrs:{"name":"van-fade"}},[_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.visible),expression:"visible"}],staticClass:"van-toast-wrapper"},[_c('div',{staticClass:"van-toast",class:[("van-toast--" + _vm.displayStyle), ("van-toast--" + _vm.position)]},[(_vm.displayStyle === 'text')?_c('div',[_vm._v(_vm._s(_vm.message))]):_vm._e(),(_vm.displayStyle === 'html')?_c('div',{domProps:{"innerHTML":_vm._s(_vm.message)}}):_vm._e(),(_vm.displayStyle === 'default')?[(_vm.type === 'loading')?_c('loading',{attrs:{"color":"white"}}):_c('icon',{staticClass:"van-toast__icon",attrs:{"name":_vm.type}}),(_vm.message !== undefined)?_c('div',{staticClass:"van-toast__text"},[_vm._v(_vm._s(_vm.message))]):_vm._e()]:_vm._e()],2),(_vm.forbidClick || _vm.mask)?_c('div',{staticClass:"van-toast__overlay",class:{ 'van-toast__overlay--mask': _vm.mask }}):_vm._e()])])}
var toast_staticRenderFns = []
var toast_esExports = { render: toast_render, staticRenderFns: toast_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_toast_toast = (toast_esExports);
// CONCATENATED MODULE: ./packages/toast/toast.vue
var toast_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var toast___vue_template_functional__ = false
/* styles */
var toast___vue_styles__ = null
/* scopeId */
var toast___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var toast___vue_module_identifier__ = null
var toast_Component = toast_normalizeComponent(
  toast_toast,
  selectortype_template_index_0_packages_toast_toast,
  toast___vue_template_functional__,
  toast___vue_styles__,
  toast___vue_scopeId__,
  toast___vue_module_identifier__
)

/* harmony default export */ var packages_toast_toast = (toast_Component.exports);

// CONCATENATED MODULE: ./packages/toast/index.js






var defaultOptions = {
  type: 'text',
  mask: false,
  message: '',
  visible: true,
  duration: 3000,
  position: 'middle',
  forbidClick: false
};
var toast_parseOptions = function parseOptions(message) {
  return (typeof message === 'undefined' ? 'undefined' : typeof_default()(message)) === 'object' ? message : { message: message };
};

var queue = [];
var singleton = true;
var currentOptions = extends_default()({}, defaultOptions);

function createInstance() {
  if (!queue.length || !singleton) {
    var toast = new (external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.extend(packages_toast_toast))({
      el: document.createElement('div')
    });
    document.body.appendChild(toast.$el);
    queue.push(toast);
  }
  return queue[queue.length - 1];
};

function Toast() {
  var options = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : {};

  var toast = createInstance();

  options = extends_default()({}, currentOptions, toast_parseOptions(options), {
    clear: function clear() {
      toast.visible = false;
    }
  });

  assign_default()(toast, options);
  clearTimeout(toast.timer);

  if (options.duration > 0) {
    toast.timer = setTimeout(function () {
      toast.clear();
    }, options.duration);
  }

  return toast;
};

var toast_createMethod = function createMethod(type) {
  return function (options) {
    return Toast(extends_default()({
      type: type }, toast_parseOptions(options)));
  };
};

['loading', 'success', 'fail'].forEach(function (method) {
  Toast[method] = toast_createMethod(method);
});

Toast.clear = function (all) {
  if (queue.length) {
    if (all) {
      queue.forEach(function (toast) {
        toast.clear();
      });
      queue = [];
    } else if (singleton) {
      queue[0].clear();
    } else {
      queue.shift().clear();
    }
  }
};

Toast.setDefaultOptions = function (options) {
  assign_default()(currentOptions, options);
};

Toast.resetDefaultOptions = function () {
  currentOptions = extends_default()({}, defaultOptions);
};

Toast.allowMultiple = function () {
  var allow = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : true;

  singleton = !allow;
};

external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.prototype.$toast = Toast;

/* harmony default export */ var packages_toast = (Toast);
// EXTERNAL MODULE: ./node_modules/babel-runtime/core-js/promise.js
var promise = __webpack_require__(58);
var promise_default = /*#__PURE__*/__webpack_require__.n(promise);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/dialog/dialog.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//





/* harmony default export */ var dialog = (Object(utils["b" /* create */])({
  name: 'van-dialog',

  components: {
    VanButton: packages_button
  },

  mixins: [popup],

  props: {
    title: String,
    message: String,
    callback: Function,
    confirmButtonText: String,
    cancelButtonText: String,
    showCancelButton: Boolean,
    showConfirmButton: {
      type: Boolean,
      default: true
    },
    overlay: {
      type: Boolean,
      default: true
    },
    closeOnClickOverlay: {
      type: Boolean,
      default: false
    },
    lockOnScroll: {
      type: Boolean,
      default: true
    }
  },

  methods: {
    handleAction: function handleAction(action) {
      this.$emit('input', false);
      this.$emit(action);
      this.callback && this.callback(action);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-3f9b7dae","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/dialog/dialog.vue
var dialog_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('transition',{attrs:{"name":"van-dialog-bounce"}},[_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.value),expression:"value"}],staticClass:"van-dialog"},[(_vm.title)?_c('div',{staticClass:"van-dialog__header",domProps:{"textContent":_vm._s(_vm.title)}}):_vm._e(),_c('div',{staticClass:"van-dialog__content van-hairline"},[_vm._t("default",[(_vm.message)?_c('div',{staticClass:"van-dialog__message",class:{ 'van-dialog__message--withtitle': _vm.title },domProps:{"innerHTML":_vm._s(_vm.message)}}):_vm._e()])],2),_c('div',{staticClass:"van-dialog__footer",class:{ 'is-twobtn': _vm.showCancelButton && _vm.showConfirmButton }},[_c('van-button',{directives:[{name:"show",rawName:"v-show",value:(_vm.showCancelButton),expression:"showCancelButton"}],staticClass:"van-dialog__cancel",attrs:{"size":"large"},on:{"click":function($event){_vm.handleAction('cancel')}}},[_vm._v("\n        "+_vm._s(_vm.cancelButtonText || _vm.$t('cancel'))+"\n      ")]),_c('van-button',{directives:[{name:"show",rawName:"v-show",value:(_vm.showConfirmButton),expression:"showConfirmButton"}],staticClass:"van-dialog__confirm",class:{ 'van-hairline--left': _vm.showCancelButton && _vm.showConfirmButton },attrs:{"size":"large"},on:{"click":function($event){_vm.handleAction('confirm')}}},[_vm._v("\n        "+_vm._s(_vm.confirmButtonText || _vm.$t('confirm'))+"\n      ")])],1)])])}
var dialog_staticRenderFns = []
var dialog_esExports = { render: dialog_render, staticRenderFns: dialog_staticRenderFns }
/* harmony default export */ var dialog_dialog = (dialog_esExports);
// CONCATENATED MODULE: ./packages/dialog/dialog.vue
var dialog_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var dialog___vue_template_functional__ = false
/* styles */
var dialog___vue_styles__ = null
/* scopeId */
var dialog___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var dialog___vue_module_identifier__ = null
var dialog_Component = dialog_normalizeComponent(
  dialog,
  dialog_dialog,
  dialog___vue_template_functional__,
  dialog___vue_styles__,
  dialog___vue_scopeId__,
  dialog___vue_module_identifier__
)

/* harmony default export */ var packages_dialog_dialog = (dialog_Component.exports);

// CONCATENATED MODULE: ./packages/dialog/index.js






var dialog_instance = void 0;

var dialog_initInstance = function initInstance() {
  dialog_instance = new (external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.extend(packages_dialog_dialog))({
    el: document.createElement('div')
  });

  dialog_instance.$on('input', function (value) {
    dialog_instance.value = value;
  });

  document.body.appendChild(dialog_instance.$el);
};

var dialog_Dialog = function Dialog(options) {
  return new promise_default.a(function (resolve, reject) {
    if (!dialog_instance) {
      dialog_initInstance();
    }

    assign_default()(dialog_instance, extends_default()({
      resolve: resolve,
      reject: reject
    }, options));
  });
};

dialog_Dialog.defaultOptions = {
  value: true,
  title: '',
  message: '',
  overlay: true,
  lockOnScroll: true,
  confirmButtonText: '',
  cancelButtonText: '',
  showConfirmButton: true,
  showCancelButton: false,
  closeOnClickOverlay: false,
  callback: function callback(action) {
    dialog_instance[action === 'confirm' ? 'resolve' : 'reject'](action);
  }
};

dialog_Dialog.alert = function (options) {
  return dialog_Dialog(extends_default()({}, dialog_Dialog.currentOptions, options));
};

dialog_Dialog.confirm = function (options) {
  return dialog_Dialog(extends_default()({}, dialog_Dialog.currentOptions, {
    showCancelButton: true
  }, options));
};

dialog_Dialog.close = function () {
  dialog_instance.value = false;
};

dialog_Dialog.setDefaultOptions = function (options) {
  assign_default()(dialog_Dialog.currentOptions, options);
};

dialog_Dialog.resetDefaultOptions = function () {
  dialog_Dialog.currentOptions = extends_default()({}, dialog_Dialog.defaultOptions);
};

dialog_Dialog.install = function () {
  external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.component(packages_dialog_dialog.name, packages_dialog_dialog);
};

external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.prototype.$dialog = dialog_Dialog;
dialog_Dialog.resetDefaultOptions();

/* harmony default export */ var packages_dialog = (dialog_Dialog);
// EXTERNAL MODULE: ./node_modules/babel-runtime/core-js/object/keys.js
var keys = __webpack_require__(28);
var keys_default = /*#__PURE__*/__webpack_require__.n(keys);

// EXTERNAL MODULE: ./node_modules/babel-runtime/core-js/json/stringify.js
var stringify = __webpack_require__(106);
var stringify_default = /*#__PURE__*/__webpack_require__.n(stringify);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/picker/PickerColumn.vue


//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



var DEFAULT_DURATION = 200;
var range = function range(num, arr) {
  return Math.min(Math.max(num, arr[0]), arr[1]);
};

/* harmony default export */ var PickerColumn = (Object(utils["b" /* create */])({
  name: 'van-picker-column',

  props: {
    valueKey: String,
    className: String,
    itemHeight: Number,
    options: {
      type: Array,
      default: function _default() {
        return [];
      }
    },
    visibleItemCount: {
      type: Number,
      default: 5
    },
    defaultIndex: {
      type: Number,
      default: 0
    }
  },

  data: function data() {
    return {
      startY: 0,
      offset: 0,
      duration: 0,
      startOffset: 0,
      currentIndex: this.defaultIndex
    };
  },
  created: function created() {
    this.$parent && this.$parent.children.push(this);
  },
  mounted: function mounted() {
    this.setIndex(this.currentIndex);
  },
  destroyed: function destroyed() {
    this.$parent && this.$parent.children.splice(this.$parent.children.indexOf(this), 1);
  },


  watch: {
    defaultIndex: function defaultIndex() {
      this.setIndex(this.defaultIndex);
    },
    options: function options(next, prev) {
      if (stringify_default()(next) !== stringify_default()(prev)) {
        this.setIndex(0);
      }
    }
  },

  computed: {
    count: function count() {
      return this.options.length;
    },
    baseOffset: function baseOffset() {
      return this.itemHeight * (this.visibleItemCount - 1) / 2;
    },
    columnStyle: function columnStyle() {
      return {
        height: this.itemHeight * this.visibleItemCount + 'px'
      };
    },
    wrapperStyle: function wrapperStyle() {
      return {
        transition: this.duration + 'ms',
        transform: 'translate3d(0, ' + (this.offset + this.baseOffset) + 'px, 0)',
        lineHeight: this.itemHeight + 'px'
      };
    },
    currentValue: function currentValue() {
      return this.options[this.currentIndex];
    }
  },

  methods: {
    onTouchStart: function onTouchStart(event) {
      this.startY = event.touches[0].clientY;
      this.startOffset = this.offset;
      this.duration = 0;
    },
    onTouchMove: function onTouchMove(event) {
      var deltaY = event.touches[0].clientY - this.startY;
      this.offset = range(this.startOffset + deltaY, [-(this.count * this.itemHeight), this.itemHeight]);
    },
    onTouchEnd: function onTouchEnd() {
      if (this.offset !== this.startOffset) {
        this.duration = DEFAULT_DURATION;
        var index = range(Math.round(-this.offset / this.itemHeight), [0, this.count - 1]);
        this.setIndex(index, true);
      }
    },
    adjustIndex: function adjustIndex(index) {
      index = range(index, [0, this.count]);
      for (var i = index; i < this.count; i++) {
        if (!this.isDisabled(this.options[i])) return i;
      }
      for (var _i = index - 1; _i >= 0; _i--) {
        if (!this.isDisabled(this.options[_i])) return _i;
      }
    },
    isDisabled: function isDisabled(option) {
      return (typeof option === 'undefined' ? 'undefined' : typeof_default()(option)) === 'object' && option.disabled;
    },
    getOptionText: function getOptionText(option) {
      return (typeof option === 'undefined' ? 'undefined' : typeof_default()(option)) === 'object' && this.valueKey in option ? option[this.valueKey] : option;
    },
    setIndex: function setIndex(index, userAction) {
      index = this.adjustIndex(index);
      this.offset = -index * this.itemHeight;

      if (index !== this.currentIndex) {
        this.currentIndex = index;
        userAction && this.$emit('change', index);
      }
    },
    setValue: function setValue(value) {
      var options = this.options;

      for (var i = 0; i < options.length; i++) {
        if (this.getOptionText(options[i]) === value) {
          this.setIndex(i);
          return;
        }
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-988dcf02","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/picker/PickerColumn.vue
var PickerColumn_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-picker-column",class:_vm.className,style:(_vm.columnStyle),on:{"touchstart":_vm.onTouchStart,"touchmove":function($event){$event.preventDefault();_vm.onTouchMove($event)},"touchend":_vm.onTouchEnd,"touchcancel":_vm.onTouchEnd}},[_c('ul',{style:(_vm.wrapperStyle)},_vm._l((_vm.options),function(option,index){return _c('li',{class:{
        'van-picker-column--disabled': _vm.isDisabled(option),
        'van-picker-column--selected': index === _vm.currentIndex
      },domProps:{"textContent":_vm._s(_vm.getOptionText(option))},on:{"click":function($event){_vm.setIndex(index, true)}}})}))])}
var PickerColumn_staticRenderFns = []
var PickerColumn_esExports = { render: PickerColumn_render, staticRenderFns: PickerColumn_staticRenderFns }
/* harmony default export */ var picker_PickerColumn = (PickerColumn_esExports);
// CONCATENATED MODULE: ./packages/picker/PickerColumn.vue
var PickerColumn_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var PickerColumn___vue_template_functional__ = false
/* styles */
var PickerColumn___vue_styles__ = null
/* scopeId */
var PickerColumn___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var PickerColumn___vue_module_identifier__ = null
var PickerColumn_Component = PickerColumn_normalizeComponent(
  PickerColumn,
  picker_PickerColumn,
  PickerColumn___vue_template_functional__,
  PickerColumn___vue_styles__,
  PickerColumn___vue_scopeId__,
  PickerColumn___vue_module_identifier__
)

/* harmony default export */ var packages_picker_PickerColumn = (PickerColumn_Component.exports);

// EXTERNAL MODULE: ./packages/utils/deep-assign.js
var deep_assign = __webpack_require__(43);

// CONCATENATED MODULE: ./packages/utils/deep-clone.js



function deepClone(obj) {
  if (Array.isArray(obj)) {
    return obj.map(function (item) {
      return deepClone(item);
    });
  } else if ((typeof obj === 'undefined' ? 'undefined' : typeof_default()(obj)) === 'object') {
    return Object(deep_assign["a" /* default */])({}, obj);
  }
  return obj;
}
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/picker/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//





/* harmony default export */ var picker = (Object(utils["b" /* create */])({
  name: 'van-picker',

  components: {
    PickerColumn: packages_picker_PickerColumn
  },

  props: {
    title: String,
    showToolbar: Boolean,
    confirmButtonText: String,
    cancelButtonText: String,
    visibleItemCount: Number,
    valueKey: {
      type: String,
      default: 'text'
    },
    itemHeight: {
      type: Number,
      default: 44
    },
    columns: {
      type: Array,
      default: function _default() {
        return [];
      }
    }
  },

  data: function data() {
    return {
      children: [],
      currentColumns: []
    };
  },
  created: function created() {
    this.initColumns();
  },


  watch: {
    columns: function columns() {
      this.initColumns();
    }
  },

  computed: {
    frameStyle: function frameStyle() {
      return {
        height: this.itemHeight + 'px'
      };
    }
  },

  methods: {
    initColumns: function initColumns() {
      
      var columns = this.columns.map(deepClone);
      
      this.isSimpleColumn = columns.length && !columns[0].values;
      this.currentColumns = this.isSimpleColumn ? [{ values: columns }] : columns;
    },
    emit: function emit(event) {
      if (this.isSimpleColumn) {
        this.$emit(event, this.getColumnValue(0), this.getColumnIndex(0));
      } else {
        this.$emit(event, this.getValues(), this.getIndexes());
      }
    },
    onChange: function onChange(columnIndex) {
      if (this.isSimpleColumn) {
        this.$emit('change', this, this.getColumnValue(0), this.getColumnIndex(0));
      } else {
        this.$emit('change', this, this.getValues(), columnIndex);
      }
    },


    // get column instance by index
    getColumn: function getColumn(index) {
      return this.children[index];
    },


    // get column value by index
    getColumnValue: function getColumnValue(index) {
      return (this.getColumn(index) || {}).currentValue;
    },


    // set column value by index
    setColumnValue: function setColumnValue(index, value) {
      var column = this.getColumn(index);
      column && column.setValue(value);
    },


    // get column option index by column index
    getColumnIndex: function getColumnIndex(columnIndex) {
      return (this.getColumn(columnIndex) || {}).currentIndex;
    },


    // set column option index by column index
    setColumnIndex: function setColumnIndex(columnIndex, optionIndex) {
      var column = this.getColumn(columnIndex);
      column && column.setIndex(optionIndex);
    },


    // get options of column by index
    getColumnValues: function getColumnValues(index) {
      return (this.currentColumns[index] || {}).values;
    },


    // set options of column by index
    setColumnValues: function setColumnValues(index, options) {
      var column = this.currentColumns[index];
      if (column) {
        column.values = options;
      }
    },


    // get values of all columns
    getValues: function getValues() {
      return this.children.map(function (child) {
        return child.currentValue;
      });
    },


    // set values of all columns
    setValues: function setValues(values) {
      var _this = this;

      values.forEach(function (value, index) {
        _this.setColumnValue(index, value);
      });
    },


    // get indexes of all columns
    getIndexes: function getIndexes() {
      return this.children.map(function (child) {
        return child.currentIndex;
      });
    },


    // set indexes of all columns
    setIndexes: function setIndexes(indexes) {
      var _this2 = this;

      indexes.forEach(function (optionIndex, columnIndex) {
        _this2.setColumnIndex(columnIndex, optionIndex);
      });
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-6a2941b9","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/picker/index.vue
var picker_render = function () {
  var _vm=this;
  var _h=_vm.$createElement;
  var _c=_vm._self._c||_h;
  return _c('div',{staticClass:"van-picker"},
  [(_vm.showToolbar)?_c('div',{staticClass:"van-picker__toolbar van-hairline--top-bottom"},[_vm._t("default",[_c('div',{staticClass:"van-picker__cancel",on:{"click":function($event){_vm.emit('cancel')}}},[_vm._v(_vm._s(_vm.cancelButtonText || _vm.$t('cancel')))]),_c('div',{staticClass:"van-picker__confirm",on:{"click":function($event){_vm.emit('confirm')}}},[_vm._v(_vm._s(_vm.confirmButtonText || _vm.$t('confirm')))]),(_vm.title)?_c('div',{staticClass:"van-picker__title",domProps:{"textContent":_vm._s(_vm.title)}}):_vm._e()])],2):_vm._e(),_c('div',{staticClass:"van-picker__columns",on:{"touchmove":function($event){$event.preventDefault();}}},[_vm._l((_vm.currentColumns),
    function(item,index){return _c('picker-column',{key:index,attrs:{"value-key":_vm.valueKey,"options":item.values,"class-name":item.className,"default-index":item.defaultIndex,"item-height":_vm.itemHeight,"visible-item-count":_vm.visibleItemCount},on:{"change":function($event){_vm.onChange(index)}}})}
  ),_c('div',{staticClass:"van-picker__frame van-hairline--top-bottom",style:(_vm.frameStyle)})],2)])
}
var picker_staticRenderFns = []
var picker_esExports = { render: picker_render, staticRenderFns: picker_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_picker = (picker_esExports);
// CONCATENATED MODULE: ./packages/picker/index.vue
var picker_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var picker___vue_template_functional__ = false
/* styles */
var picker___vue_styles__ = null
/* scopeId */
var picker___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var picker___vue_module_identifier__ = null
var picker_Component = picker_normalizeComponent(
  picker,
  selectortype_template_index_0_packages_picker,
  picker___vue_template_functional__,
  picker___vue_styles__,
  picker___vue_scopeId__,
  picker___vue_module_identifier__
)

/* harmony default export */ var packages_picker = (picker_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/area/index.vue


//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var selectortype_script_index_0_packages_area = (Object(utils["b" /* create */])({
  name: 'van-area',

  components: {
    Picker: packages_picker
  },

  props: {
    value: {},
    title: String,
    areaList: Object,
    itemHeight: Number,
    visibleItemCount: Number,
    // 省市县显示列数，3-省市县，2-省市，1-省
    columnsNum: {
      type: [String, Number],
      default: 3
    }
  },

  computed: {
    listValid: function listValid() {
      return this.areaList && typeof_default()(this.areaList.province_list) === 'object';
    },
    columns: function columns() {
      var columns = [];

      if (!this.listValid) {
        return columns;
      }

      var code = this.value || '';
      var columnsNum = +this.columnsNum;

      columns.push({
        values: this.getList('province')
      });

      if (columnsNum > 1) {
        columns.push({
          values: this.getList('city', code.slice(0, 2))
        });
      }

      if (columnsNum > 2) {
        columns.push({
          values: this.getList('county', code.slice(0, 4))
        });
      }

      return columns;
    }
  },

  mounted: function mounted() {
    this.setIndex();
  },


  watch: {
    value: function value() {
      this.setIndex();
    }
  },

  methods: {
    setIndex: function setIndex() {
      var _this = this;

      this.$nextTick(function () {
        var code = _this.value || '';
        var picker = _this.$refs.picker;

        picker && picker.setIndexes([_this.getIndex('province', code), _this.getIndex('city', code), _this.getIndex('county', code)]);
      });
    },


    // 根据省市县类型和对应的`code`获取对应列表
    getList: function getList(type, code) {
      var result = [];

      if (this.listValid && (type === 'province' || code)) {
        var areaList = this.areaList;

        var list = type === 'province' ? areaList.province_list : type === 'city' ? areaList.city_list : areaList.county_list;

        result = keys_default()(list).map(function (code) {
          return {
            code: code,
            name: list[code]
          };
        });

        if (type !== 'province' && code) {
          result = result.filter(function (item) {
            return item.code.indexOf(code) === 0;
          });
        }
      }

      result.unshift({
        code: '-1',
        name: this.$t(type)
      });

      return result;
    },


    // 获取对应省市县在列表中的索引
    getIndex: function getIndex(type, code) {
      var compareNum = type === 'province' ? 2 : type === 'city' ? 4 : 6;
      var areaList = this.getList(type, code.slice(0, compareNum - 2));
      code = code.slice(0, compareNum);

      for (var i = 0; i < areaList.length; i++) {
        if (areaList[i].code.slice(0, compareNum) === code) {
          return i;
        }
      }

      return 0;
    },
    onChange: function onChange(picker, values, index) {
      var code = values[index].code;
      // 处理省变化
      if (index === 0) {
        picker.setColumnValues(1, this.getList('city', code.slice(0, 2)));
        picker.setColumnValues(2, this.getList('county', code.slice(0, 4)));
      } else if (index === 1) {
        picker.setColumnValues(2, this.getList('county', code.slice(0, 4)));
      }
    },
    getValues: function getValues() {
      return this.$refs.picker ? this.$refs.picker.getValues() : [];
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-3fd0b270","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/area/index.vue
var area_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('picker',{ref:"picker",staticClass:"van-area",attrs:{"show-toolbar":"","value-key":"name","title":_vm.title,"columns":_vm.columns,"item-height":_vm.itemHeight,"visible-item-count":_vm.visibleItemCount},on:{"change":_vm.onChange,"confirm":function($event){_vm.$emit('confirm', $event)},"cancel":function($event){_vm.$emit('cancel', $event)}}})}
var area_staticRenderFns = []
var area_esExports = { render: area_render, staticRenderFns: area_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_area = (area_esExports);
// CONCATENATED MODULE: ./packages/area/index.vue
var area_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var area___vue_template_functional__ = false
/* styles */
var area___vue_styles__ = null
/* scopeId */
var area___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var area___vue_module_identifier__ = null
var area_Component = area_normalizeComponent(
  selectortype_script_index_0_packages_area,
  selectortype_template_index_0_packages_area,
  area___vue_template_functional__,
  area___vue_styles__,
  area___vue_scopeId__,
  area___vue_module_identifier__
)

/* harmony default export */ var packages_area = (area_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/address-edit/Detail.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//







/* harmony default export */ var Detail = (Object(utils["b" /* create */])({
  name: 'van-address-edit-detail',

  components: {
    Field: packages_field,
    Cell: packages_cell,
    CellGroup: packages_cell_group
  },

  props: {
    value: {},
    isError: Boolean,
    searchResult: Array,
    showSearchResult: Boolean
  },

  data: function data() {
    return {
      isAndroid: Object(utils["d" /* isAndroid */])(),
      isFocused: false
    };
  },


  computed: {
    showSearchList: function showSearchList() {
      return this.showSearchResult && this.isFocused && this.searchResult.length > 0;
    },
    showIcon: function showIcon() {
      return this.value && this.isFocused;
    }
  },

  methods: {
    handleFocus: function handleFocus(e) {
      this.isFocused = true;
      this.$emit('focus', e);
      this.$refs.root.scrollIntoView();
    },
    handleBlur: function handleBlur(e) {
      var _this = this;

      // wait for click event finished
      setTimeout(function () {
        _this.isFocused = false;
        _this.$emit('blur', e);
      }, 100);
    },
    onIconClick: function onIconClick() {
      if (this.isAndroid) {
        this.$refs.root.querySelector('.van-field__control').blur();
      } else {
        this.$emit('input', '');
      }
    },
    onSuggestSelect: function onSuggestSelect(express) {
      this.$emit('input', ((express.address || '') + ' ' + (express.name || '')).trim());
      this.$emit('select-search', express);
    },
    isString: function isString(str) {
      return typeof str === 'string';
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-06562ea7","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/address-edit/Detail.vue
var Detail_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{ref:"root"},[_c('field',{attrs:{"label":_vm.$t('label.address'),"placeholder":_vm.$t('placeholder.address'),"maxlength":"200","type":"textarea","autosize":"","rows":"1","value":_vm.value,"error":_vm.isError,"on-icon-click":_vm.onIconClick},on:{"input":function($event){_vm.$emit('input', $event)},"focus":_vm.handleFocus,"blur":_vm.handleBlur}},[_c('div',{attrs:{"slot":"icon"},slot:"icon"},[(_vm.showIcon && _vm.isAndroid)?_c('span',{staticClass:"van-address-edit-detail__finish-edit"},[_vm._v(_vm._s(_vm.$t('complete')))]):(_vm.showIcon)?_c('icon',{attrs:{"name":"clear"}}):_vm._e()],1)]),(_vm.showSearchList)?_c('cell-group',{staticClass:"van-address-edit-detail__suggest-list"},_vm._l((_vm.searchResult),function(express){return _c('cell',{key:express.name + express.address,staticClass:"van-address-edit-detail__suggest-item",attrs:{"clickable":""},on:{"click":function($event){_vm.onSuggestSelect(express)}}},[_c('icon',{staticClass:"van-address-edit-detail__location",attrs:{"name":"location"}}),_c('div',{staticClass:"van-address-edit-detail__item-info"},[(_vm.isString(express.name))?_c('p',{staticClass:"van-address-edit-detail__title"},[_vm._v(_vm._s(express.name))]):_vm._e(),(_vm.isString(express.address))?_c('p',{staticClass:"van-address-edit-detail__subtitle"},[_vm._v(_vm._s(express.address))]):_vm._e()])],1)})):_vm._e()],1)}
var Detail_staticRenderFns = []
var Detail_esExports = { render: Detail_render, staticRenderFns: Detail_staticRenderFns }
/* harmony default export */ var address_edit_Detail = (Detail_esExports);
// CONCATENATED MODULE: ./packages/address-edit/Detail.vue
var Detail_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var Detail___vue_template_functional__ = false
/* styles */
var Detail___vue_styles__ = null
/* scopeId */
var Detail___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var Detail___vue_module_identifier__ = null
var Detail_Component = Detail_normalizeComponent(
  Detail,
  address_edit_Detail,
  Detail___vue_template_functional__,
  Detail___vue_styles__,
  Detail___vue_scopeId__,
  Detail___vue_module_identifier__
)

/* harmony default export */ var packages_address_edit_Detail = (Detail_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/switch/index.vue
//
//
//
//
//
//
//
//
//



/* harmony default export */ var selectortype_script_index_0_packages_switch = (Object(utils["b" /* create */])({
  name: 'van-switch',

  props: {
    value: Boolean,
    loading: Boolean,
    disabled: Boolean
  },

  methods: {
    toggleState: function toggleState() {
      if (!this.disabled && !this.loading) {
        this.$emit('input', !this.value);
        this.$emit('change', !this.value);
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-2d98322a","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/switch/index.vue
var switch_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-switch",class:[("van-switch--" + (_vm.value ? 'on' : 'off')), { 'van-switch--disabled': _vm.disabled }],on:{"click":_vm.toggleState}},[_c('div',{staticClass:"van-switch__node van-hairline-surround"},[(_vm.loading)?_c('loading',{staticClass:"van-switch__loading"}):_vm._e()],1),_c('div',{staticClass:"van-switch__bg"})])}
var switch_staticRenderFns = []
var switch_esExports = { render: switch_render, staticRenderFns: switch_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_switch = (switch_esExports);
// CONCATENATED MODULE: ./packages/switch/index.vue
var switch_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var switch___vue_template_functional__ = false
/* styles */
var switch___vue_styles__ = null
/* scopeId */
var switch___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var switch___vue_module_identifier__ = null
var switch_Component = switch_normalizeComponent(
  selectortype_script_index_0_packages_switch,
  selectortype_template_index_0_packages_switch,
  switch___vue_template_functional__,
  switch___vue_styles__,
  switch___vue_scopeId__,
  switch___vue_module_identifier__
)

/* harmony default export */ var packages_switch = (switch_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/switch-cell/index.vue
//
//
//
//
//
//





/* harmony default export */ var switch_cell = (Object(utils["b" /* create */])({
  name: 'van-switch-cell',

  components: {
    Cell: packages_cell,
    VanSwitch: packages_switch
  },

  props: {
    title: String,
    value: Boolean,
    loading: Boolean,
    disabled: Boolean
  },

  watch: {
    value: function value() {
      this.$emit('change', this.value);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-4860b8bb","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/switch-cell/index.vue
var switch_cell_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('cell',{staticClass:"van-switch-cell",attrs:{"title":_vm.title}},[_c('van-switch',_vm._b({on:{"input":function($event){_vm.$emit('input', $event)}}},'van-switch',_vm.$props,false))],1)}
var switch_cell_staticRenderFns = []
var switch_cell_esExports = { render: switch_cell_render, staticRenderFns: switch_cell_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_switch_cell = (switch_cell_esExports);
// CONCATENATED MODULE: ./packages/switch-cell/index.vue
var switch_cell_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var switch_cell___vue_template_functional__ = false
/* styles */
var switch_cell___vue_styles__ = null
/* scopeId */
var switch_cell___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var switch_cell___vue_module_identifier__ = null
var switch_cell_Component = switch_cell_normalizeComponent(
  switch_cell,
  selectortype_template_index_0_packages_switch_cell,
  switch_cell___vue_template_functional__,
  switch_cell___vue_styles__,
  switch_cell___vue_scopeId__,
  switch_cell___vue_module_identifier__
)

/* harmony default export */ var packages_switch_cell = (switch_cell_Component.exports);

// CONCATENATED MODULE: ./packages/utils/validate/mobile.js
function mobile(value) {
  return (/^((\+86)|(86))?(1)\d{10}$/.test(value) || /^\+?(9[976]\d|8[987530]\d|6[987]\d|5[90]\d|42\d|3[875]\d|2[98654321]\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1|)-?\d{1,14}$/.test(String(value))
  );
}
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/address-edit/index.vue

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

/* eslint-disable camelcase */













/* harmony default export */ var address_edit = (Object(utils["b" /* create */])({
  name: 'van-address-edit',

  components: {
    Field: packages_field,
    Cell: packages_cell,
    CellGroup: packages_cell_group,
    SwitchCell: packages_switch_cell,
    VanButton: packages_button,
    Popup: packages_popup,
    VanArea: packages_area,
    AddressEditDetail: packages_address_edit_Detail
  },

  props: {
    isSaving: Boolean,
    isDeleting: Boolean,
    areaList: Object,
    showPostal: Boolean,
    showSetDefault: Boolean,
    showSearchResult: Boolean,
    addressText: String,
    addressInfo: {
      type: Object,
      default: function _default() {
        return {
          name: '',
          tel: '',
          province: '',
          city: '',
          county: '',
          area_code: '',
          postal_code: '',
          address_detail: '',
          is_default: false
        };
      }
    },
    searchResult: {
      type: Array,
      default: function _default() {
        return [];
      }
    }
  },

  data: function data() {
    return {
      showAreaSelect: false,
      currentInfo: this.addressInfo,
      isEdit: !!this.addressInfo.id,
      detailFocused: false,
      errorInfo: {
        name: false,
        tel: false,
        address_detail: false,
        postal_code: false
      }
    };
  },


  watch: {
    addressInfo: {
      handler: function handler(val) {
        this.currentInfo = val;
        this.isEdit = !!val.id;
      },

      deep: true
    }
  },

  computed: {
    // hide bottom field when use search && detail get focused
    hideBottomFields: function hideBottomFields() {
      return this.searchResult.length && this.detailFocused;
    },
    computedAddressText: function computedAddressText() {
      return this.addressText || this.$t('addressText');
    }
  },

  methods: {
    onFocus: function onFocus(key) {
      this.errorInfo[key] = false;
      this.detailFocused = key === 'address_detail';
      this.$emit('focus', key);
    },
    onDetailBlur: function onDetailBlur() {
      this.detailFocused = false;
    },
    onChangeDetail: function onChangeDetail(val) {
      this.currentInfo.address_detail = val;
      this.$emit('change-detail', val);
    },
    onAreaConfirm: function onAreaConfirm(values) {
      if (values.length !== 3 || +values[0].code === -1 || +values[1].code === -1 || +values[2].code === -1) {
        return packages_toast(this.$t('areaWrong'));
      }
      this.assignAreaValues(values);
      this.showAreaSelect = false;
      this.$emit('change-area', values);
    },
    assignAreaValues: function assignAreaValues(values) {
      assign_default()(this.currentInfo, {
        province: values[0].name,
        city: values[1].name,
        county: values[2].name,
        area_code: values[2].code
      });
    },
    onSaveAddress: function onSaveAddress() {
      var _this = this;

      var items = ['name', 'tel', 'area_code', 'address_detail'];

      if (this.showPostal) {
        items.push('postal_code');
      }

      var isValid = items.every(function (item) {
        var msg = _this.getErrorMessageByKey(item);
        if (msg) {
          _this.errorInfo[item] = true;
          packages_toast(msg);
        }
        return !msg;
      });

      if (isValid && !this.isSaving) {
        this.$emit('save', this.currentInfo);
      }
    },
    getErrorMessageByKey: function getErrorMessageByKey(key) {
      var value = this.currentInfo[key];
      var $t = this.$t;


      switch (key) {
        case 'name':
          return value ? value.length <= 15 ? '' : $t('nameOverlimit') : $t('nameEmpty');
        case 'tel':
          return mobile(value) ? '' : $t('telWrong');
        case 'area_code':
          return value ? +value !== -1 ? '' : $t('areaWrong') : $t('areaEmpty');
        case 'address_detail':
          return value ? value.length <= 200 ? '' : $t('addressOverlimit') : $t('addressEmpty');
        case 'postal_code':
          return value && !/^\d{6}$/.test(value) ? $t('postalEmpty') : '';
      }
    },
    onDeleteAddress: function onDeleteAddress() {
      var _this2 = this;

      if (this.isDeleting) {
        return;
      }

      packages_dialog.confirm({
        message: this.$t('confirmDelete', this.computedAddressText)
      }).then(function () {
        _this2.$emit('delete', _this2.currentInfo);
      });
    },


    // get values of area component
    getArea: function getArea() {
      var area = this.$refs.area;

      return area ? area.getValues() : [];
    },


    // set area code to area component
    setAreaCode: function setAreaCode(code) {
      var _this3 = this;

      this.currentInfo.area_code = code;
      this.$nextTick(function () {
        _this3.$nextTick(function () {
          var area = _this3.$refs.area;

          if (area) {
            _this3.assignAreaValues(area.getValues());
          }
        });
      });
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-2b00a8f3","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/address-edit/index.vue
var address_edit_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-address-edit"},[_c('cell-group',[_c('field',{attrs:{"maxlength":"15","placeholder":_vm.$t('placeholder.name'),"label":_vm.$t('label.name', _vm.computedAddressText),"error":_vm.errorInfo.name},on:{"focus":function($event){_vm.onFocus('name')}},model:{value:(_vm.currentInfo.name),callback:function ($$v) {_vm.$set(_vm.currentInfo, "name", $$v)},expression:"currentInfo.name"}}),_c('field',{attrs:{"type":"tel","label":_vm.$t('label.tel'),"placeholder":_vm.$t('placeholder.tel'),"error":_vm.errorInfo.tel},on:{"focus":function($event){_vm.onFocus('tel')}},model:{value:(_vm.currentInfo.tel),callback:function ($$v) {_vm.$set(_vm.currentInfo, "tel", $$v)},expression:"currentInfo.tel"}}),_c('cell',{staticClass:"van-address-edit__area",attrs:{"title":_vm.$t('areaTitle')},on:{"click":function($event){_vm.showAreaSelect = true}}},[_c('span',[_vm._v(_vm._s(_vm.currentInfo.province || _vm.$t('placeholder.province')))]),_c('span',[_vm._v(_vm._s(_vm.currentInfo.city || _vm.$t('placeholder.city')))]),_c('span',[_vm._v(_vm._s(_vm.currentInfo.county || _vm.$t('placeholder.county')))])]),_c('address-edit-detail',{attrs:{"value":_vm.currentInfo.address_detail,"is-error":_vm.errorInfo.address_detail,"show-search-result":_vm.showSearchResult,"search-result":_vm.searchResult},on:{"focus":function($event){_vm.onFocus('address_detail')},"blur":_vm.onDetailBlur,"input":_vm.onChangeDetail,"select-search":function($event){_vm.$emit('select-search', $event)}}}),(_vm.showPostal)?_c('field',{directives:[{name:"show",rawName:"v-show",value:(!_vm.hideBottomFields),expression:"!hideBottomFields"}],staticClass:"van-hairline--top",attrs:{"type":"tel","label":_vm.$t('label.postal'),"placeholder":_vm.$t('placeholder.postal'),"maxlength":"6","error":_vm.errorInfo.postal_code},on:{"focus":function($event){_vm.onFocus('postal_code')}},model:{value:(_vm.currentInfo.postal_code),callback:function ($$v) {_vm.$set(_vm.currentInfo, "postal_code", $$v)},expression:"currentInfo.postal_code"}}):_vm._e(),_vm._t("default"),(_vm.showSetDefault)?_c('switch-cell',{directives:[{name:"show",rawName:"v-show",value:(!_vm.hideBottomFields),expression:"!hideBottomFields"}],attrs:{"title":_vm.$t('defaultAddress', _vm.computedAddressText)},model:{value:(_vm.currentInfo.is_default),callback:function ($$v) {_vm.$set(_vm.currentInfo, "is_default", $$v)},expression:"currentInfo.is_default"}}):_vm._e()],2),_c('div',{directives:[{name:"show",rawName:"v-show",value:(!_vm.hideBottomFields),expression:"!hideBottomFields"}],staticClass:"van-address-edit__buttons"},[_c('van-button',{attrs:{"block":"","loading":_vm.isSaving,"type":"primary"},on:{"click":_vm.onSaveAddress}},[_vm._v("\n      "+_vm._s(_vm.$t('save'))+"\n    ")]),(_vm.isEdit)?_c('van-button',{attrs:{"block":"","loading":_vm.isDeleting},on:{"click":_vm.onDeleteAddress}},[_vm._v("\n      "+_vm._s(_vm.$t('deleteAddress', _vm.computedAddressText))+"\n    ")]):_vm._e()],1),_c('popup',{attrs:{"position":"bottom"},model:{value:(_vm.showAreaSelect),callback:function ($$v) {_vm.showAreaSelect=$$v},expression:"showAreaSelect"}},[_c('van-area',{ref:"area",attrs:{"value":_vm.currentInfo.area_code,"area-list":_vm.areaList},on:{"confirm":_vm.onAreaConfirm,"cancel":function($event){_vm.showAreaSelect = false}}})],1)],1)}
var address_edit_staticRenderFns = []
var address_edit_esExports = { render: address_edit_render, staticRenderFns: address_edit_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_address_edit = (address_edit_esExports);
// CONCATENATED MODULE: ./packages/address-edit/index.vue
var address_edit_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var address_edit___vue_template_functional__ = false
/* styles */
var address_edit___vue_styles__ = null
/* scopeId */
var address_edit___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var address_edit___vue_module_identifier__ = null
var address_edit_Component = address_edit_normalizeComponent(
  address_edit,
  selectortype_template_index_0_packages_address_edit,
  address_edit___vue_template_functional__,
  address_edit___vue_styles__,
  address_edit___vue_scopeId__,
  address_edit___vue_module_identifier__
)

/* harmony default export */ var packages_address_edit = (address_edit_Component.exports);

// CONCATENATED MODULE: ./packages/mixins/find-parent.js
/**
 * find parent component by name
 */

/* harmony default export */ var find_parent = ({
  methods: {
    findParentByName: function findParentByName(name) {
      if (!this.parentGroup) {
        var parent = this.$parent;
        while (parent) {
          if (parent.$options.name === name) {
            this.parentGroup = parent;
            break;
          }
          parent = parent.$parent;
        }
      }

      return this.parentGroup;
    }
  }
});
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/radio/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var selectortype_script_index_0_packages_radio = (Object(utils["b" /* create */])({
  name: 'van-radio',

  mixins: [find_parent],

  props: {
    value: {},
    disabled: Boolean,
    name: [String, Number]
  },

  computed: {
    isGroup: function isGroup() {
      return !!this.findParentByName('van-radio-group');
    },


    currentValue: {
      get: function get() {
        return this.isGroup && this.parentGroup ? this.parentGroup.value : this.value;
      },
      set: function set(val) {
        if (this.isGroup && this.parentGroup) {
          this.parentGroup.$emit('input', val);
        } else {
          this.$emit('input', val);
        }
      }
    },

    isDisabled: function isDisabled() {
      return this.isGroup && this.parentGroup ? this.parentGroup.disabled || this.disabled : this.disabled;
    }
  },

  methods: {
    onClickLabel: function onClickLabel() {
      if (this.isDisabled) {
        return;
      }
      this.currentValue = this.name;
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-e8571e02","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/radio/index.vue
var radio_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-radio",class:{ 'van-radio--disabled': _vm.isDisabled },on:{"click":function($event){_vm.$emit('click')}}},[_c('span',{staticClass:"van-radio__input"},[_c('input',{directives:[{name:"model",rawName:"v-model",value:(_vm.currentValue),expression:"currentValue"}],staticClass:"van-radio__control",attrs:{"type":"radio","disabled":_vm.isDisabled},domProps:{"value":_vm.name,"checked":_vm._q(_vm.currentValue,_vm.name)},on:{"change":function($event){_vm.currentValue=_vm.name}}}),_c('icon',{attrs:{"name":_vm.currentValue === _vm.name ? 'checked' : 'check'}})],1),_c('span',{staticClass:"van-radio__label",on:{"click":_vm.onClickLabel}},[_vm._t("default")],2)])}
var radio_staticRenderFns = []
var radio_esExports = { render: radio_render, staticRenderFns: radio_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_radio = (radio_esExports);
// CONCATENATED MODULE: ./packages/radio/index.vue
var radio_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var radio___vue_template_functional__ = false
/* styles */
var radio___vue_styles__ = null
/* scopeId */
var radio___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var radio___vue_module_identifier__ = null
var radio_Component = radio_normalizeComponent(
  selectortype_script_index_0_packages_radio,
  selectortype_template_index_0_packages_radio,
  radio___vue_template_functional__,
  radio___vue_styles__,
  radio___vue_scopeId__,
  radio___vue_module_identifier__
)

/* harmony default export */ var packages_radio = (radio_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/radio-group/index.vue
//
//
//
//
//
//



/* harmony default export */ var radio_group = (Object(utils["b" /* create */])({
  name: 'van-radio-group',

  props: {
    value: {},
    disabled: Boolean
  },

  watch: {
    value: function value(_value) {
      this.$emit('change', _value);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-2d98511c","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/radio-group/index.vue
var radio_group_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-radio-group"},[_vm._t("default")],2)}
var radio_group_staticRenderFns = []
var radio_group_esExports = { render: radio_group_render, staticRenderFns: radio_group_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_radio_group = (radio_group_esExports);
// CONCATENATED MODULE: ./packages/radio-group/index.vue
var radio_group_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var radio_group___vue_template_functional__ = false
/* styles */
var radio_group___vue_styles__ = null
/* scopeId */
var radio_group___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var radio_group___vue_module_identifier__ = null
var radio_group_Component = radio_group_normalizeComponent(
  radio_group,
  selectortype_template_index_0_packages_radio_group,
  radio_group___vue_template_functional__,
  radio_group___vue_styles__,
  radio_group___vue_scopeId__,
  radio_group___vue_module_identifier__
)

/* harmony default export */ var packages_radio_group = (radio_group_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/address-list/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//







/* harmony default export */ var address_list = (Object(utils["b" /* create */])({
  name: 'van-address-list',

  components: {
    Cell: packages_cell,
    Radio: packages_radio,
    CellGroup: packages_cell_group,
    RadioGroup: packages_radio_group
  },

  props: {
    addButtonText: String,
    value: [String, Number],
    list: {
      type: Array,
      default: function _default() {
        return [];
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-7691d274","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/address-list/index.vue
var address_list_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-address-list"},[_c('radio-group',{staticClass:"van-address-list__group",attrs:{"value":_vm.value},on:{"input":function($event){_vm.$emit('input', $event)}}},[_c('cell-group',_vm._l((_vm.list),function(item,index){return _c('cell',{key:item.id,attrs:{"is-link":""}},[_c('radio',{attrs:{"name":item.id},on:{"click":function($event){_vm.$emit('select', item, index)}}},[_c('div',{staticClass:"van-address-list__name"},[_vm._v(_vm._s(item.name)+"，"+_vm._s(item.tel))]),_c('div',{staticClass:"van-address-list__address"},[_vm._v(_vm._s(_vm.$t('address'))+"："+_vm._s(item.address))])]),_c('icon',{staticClass:"van-address-list__edit",attrs:{"slot":"right-icon","name":"edit"},on:{"click":function($event){_vm.$emit('edit', item, index)}},slot:"right-icon"})],1)}))],1),_c('cell',{staticClass:"van-address-list__add van-hairline--top",attrs:{"icon":"add","title":_vm.addButtonText || _vm.$t('add'),"is-link":""},on:{"click":function($event){_vm.$emit('add')}}})],1)}
var address_list_staticRenderFns = []
var address_list_esExports = { render: address_list_render, staticRenderFns: address_list_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_address_list = (address_list_esExports);
// CONCATENATED MODULE: ./packages/address-list/index.vue
var address_list_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var address_list___vue_template_functional__ = false
/* styles */
var address_list___vue_styles__ = null
/* scopeId */
var address_list___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var address_list___vue_module_identifier__ = null
var address_list_Component = address_list_normalizeComponent(
  address_list,
  selectortype_template_index_0_packages_address_list,
  address_list___vue_template_functional__,
  address_list___vue_styles__,
  address_list___vue_scopeId__,
  address_list___vue_module_identifier__
)

/* harmony default export */ var packages_address_list = (address_list_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/badge/index.vue
//
//
//
//
//
//
//



/* harmony default export */ var badge = (Object(utils["b" /* create */])({
  name: 'van-badge',

  props: {
    url: String,
    info: String,
    title: String
  },

  beforeCreate: function beforeCreate() {
    this.$parent.badges.push(this);
  },


  computed: {
    isSelect: function isSelect() {
      return this.$parent.badges.indexOf(this) === this.$parent.activeKey;
    }
  },

  methods: {
    onClick: function onClick() {
      this.$emit('click', this.$parent.badges.indexOf(this));
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-3fa7a3cd","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/badge/index.vue
var badge_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('a',{staticClass:"van-badge van-hairline",class:{ 'van-badge--select': _vm.isSelect },attrs:{"href":_vm.url},on:{"click":_vm.onClick}},[(_vm.info)?_c('div',{staticClass:"van-badge__info"},[_vm._v(_vm._s(_vm.info))]):_vm._e(),_vm._v("\n  "+_vm._s(_vm.title)+"\n")])}
var badge_staticRenderFns = []
var badge_esExports = { render: badge_render, staticRenderFns: badge_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_badge = (badge_esExports);
// CONCATENATED MODULE: ./packages/badge/index.vue
var badge_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var badge___vue_template_functional__ = false
/* styles */
var badge___vue_styles__ = null
/* scopeId */
var badge___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var badge___vue_module_identifier__ = null
var badge_Component = badge_normalizeComponent(
  badge,
  selectortype_template_index_0_packages_badge,
  badge___vue_template_functional__,
  badge___vue_styles__,
  badge___vue_scopeId__,
  badge___vue_module_identifier__
)

/* harmony default export */ var packages_badge = (badge_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/badge-group/index.vue
//
//
//
//
//
//



/* harmony default export */ var badge_group = (Object(utils["b" /* create */])({
  name: 'van-badge-group',

  props: {
    activeKey: {
      type: [Number, String],
      default: 0
    }
  },

  data: function data() {
    return {
      badges: []
    };
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-258b3512","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/badge-group/index.vue
var badge_group_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-badge-group van-hairline--top-bottom"},[_vm._t("default")],2)}
var badge_group_staticRenderFns = []
var badge_group_esExports = { render: badge_group_render, staticRenderFns: badge_group_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_badge_group = (badge_group_esExports);
// CONCATENATED MODULE: ./packages/badge-group/index.vue
var badge_group_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var badge_group___vue_template_functional__ = false
/* styles */
var badge_group___vue_styles__ = null
/* scopeId */
var badge_group___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var badge_group___vue_module_identifier__ = null
var badge_group_Component = badge_group_normalizeComponent(
  badge_group,
  selectortype_template_index_0_packages_badge_group,
  badge_group___vue_template_functional__,
  badge_group___vue_styles__,
  badge_group___vue_scopeId__,
  badge_group___vue_module_identifier__
)

/* harmony default export */ var packages_badge_group = (badge_group_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/card/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var card = (Object(utils["b" /* create */])({
  name: 'van-card',

  props: {
    thumb: String,
    title: String,
    desc: String,
    centered: Boolean,
    num: [Number, String],
    price: [Number, String]
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-09fcbf1c","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/card/index.vue
var card_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-card",class:{ 'van-card--center': _vm.centered }},[_c('div',{staticClass:"van-card__thumb"},[_vm._t("thumb",[_c('img',{staticClass:"van-card__img",attrs:{"src":_vm.thumb}})])],2),_c('div',{staticClass:"van-card__content"},[_vm._t("title",[(_vm.title || _vm.price !== undefined)?_c('div',{staticClass:"van-card__row"},[(_vm.title)?_c('div',{staticClass:"van-card__title"},[_vm._v(_vm._s(_vm.title))]):_vm._e(),(_vm.price !== undefined)?_c('div',{staticClass:"van-card__price"},[_vm._v("¥ "+_vm._s(_vm.price))]):_vm._e()]):_vm._e()]),_vm._t("desc",[(_vm.desc || _vm.num !== undefined)?_c('div',{staticClass:"van-card__row"},[(_vm.desc)?_c('div',{staticClass:"van-card__desc"},[_vm._v(_vm._s(_vm.desc))]):_vm._e(),(_vm.num !== undefined)?_c('div',{staticClass:"van-card__num"},[_vm._v("x "+_vm._s(_vm.num))]):_vm._e()]):_vm._e()]),_vm._t("tags")],2),(_vm.$slots.footer)?_c('div',{staticClass:"van-card__footer"},[_vm._t("footer")],2):_vm._e()])}
var card_staticRenderFns = []
var card_esExports = { render: card_render, staticRenderFns: card_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_card = (card_esExports);
// CONCATENATED MODULE: ./packages/card/index.vue
var card_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var card___vue_template_functional__ = false
/* styles */
var card___vue_styles__ = null
/* scopeId */
var card___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var card___vue_module_identifier__ = null
var card_Component = card_normalizeComponent(
  card,
  selectortype_template_index_0_packages_card,
  card___vue_template_functional__,
  card___vue_styles__,
  card___vue_scopeId__,
  card___vue_module_identifier__
)

/* harmony default export */ var packages_card = (card_Component.exports);

// CONCATENATED MODULE: ./packages/utils/clickoutside.js
/**
 * v-clickoutside
 *
 * ```vue
 * <div v-clickoutside="onClose">
 * ```
 */



var clickoutside_context = '@@clickoutsideContext';

/* harmony default export */ var clickoutside = ({
  bind: function bind(el, binding) {
    var handler = function handler(event) {
      if (!el.contains(event.target)) {
        el[clickoutside_context].callback();
      }
    };

    el[clickoutside_context] = {
      handler: handler,
      callback: binding.value,
      arg: binding.arg || 'click'
    };

    on(document, el[clickoutside_context].arg, handler);
  },
  update: function update(el, binding) {
    el[clickoutside_context].callback = binding.value;
  },
  unbind: function unbind(el) {
    off(document, el[clickoutside_context].arg, el[clickoutside_context].handler);
  },
  install: function install(Vue) {
    Vue.directive('clickoutside', {
      bind: this.bind,
      unbind: this.unbind
    });
  }
});
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/cell-swipe/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var cell_swipe = (Object(utils["b" /* create */])({
  name: 'van-cell-swipe',

  props: {
    onClose: Function,
    leftWidth: {
      type: Number,
      default: 0
    },
    rightWidth: {
      type: Number,
      default: 0
    }
  },

  directives: {
    Clickoutside: clickoutside
  },

  data: function data() {
    return {
      offset: 0
    };
  },


  computed: {
    wrapperStyle: function wrapperStyle() {
      return {
        transform: 'translate3d(' + this.offset + 'px, 0, 0)'
      };
    }
  },

  methods: {
    close: function close() {
      this.offset = 0;
    },
    resetSwipeStatus: function resetSwipeStatus() {
      this.swiping = false;
      this.opened = true;
    },
    swipeMove: function swipeMove() {
      var offset = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;

      this.offset = offset;
      offset && (this.swiping = true);
    },
    swipeLeaveTransition: function swipeLeaveTransition(direction) {
      var offset = this.offset,
          leftWidth = this.leftWidth,
          rightWidth = this.rightWidth;
      // right

      if (direction > 0 && -offset > rightWidth * 0.4 && rightWidth > 0) {
        this.swipeMove(-rightWidth);
        this.resetSwipeStatus();
        // left
      } else if (direction < 0 && offset > leftWidth * 0.4 && leftWidth > 0) {
        this.swipeMove(leftWidth);
        this.resetSwipeStatus();
      } else {
        this.swipeMove();
      }
    },
    startDrag: function startDrag(event) {
      this.startX = event.touches[0].pageX;
      this.startY = event.touches[0].pageY;
    },
    onDrag: function onDrag(event) {
      if (this.opened) {
        !this.swiping && this.swipeMove();
        this.opened = false;
        return;
      }

      var offsetTop = event.touches[0].pageY - this.startY;
      var offsetLeft = event.touches[0].pageX - this.startX;
      if (offsetLeft < 0 && -offsetLeft > this.rightWidth || offsetLeft > 0 && offsetLeft > this.leftWidth || offsetLeft > 0 && !this.leftWidth || offsetLeft < 0 && !this.rightWidth) {
        return;
      }

      var y = Math.abs(offsetTop);
      var x = Math.abs(offsetLeft);
      var swiping = !(x < 5 || x >= 5 && y >= x * 1.73);
      if (swiping) {
        event.preventDefault();
        this.swipeMove(offsetLeft);
      };
    },
    endDrag: function endDrag() {
      if (this.swiping) {
        this.swipeLeaveTransition(this.offset > 0 ? -1 : 1);
      };
    },
    onClick: function onClick() {
      var position = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 'outside';

      if (!this.offset) {
        return;
      }

      if (this.onClose) {
        this.onClose(position, this);
      } else {
        this.swipeMove(0);
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-2a7f8dd4","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/cell-swipe/index.vue
var cell_swipe_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{directives:[{name:"clickoutside",rawName:"v-clickoutside:touchstart",value:(_vm.onClick),expression:"onClick",arg:"touchstart"}],staticClass:"van-cell-swipe",on:{"click":function($event){_vm.onClick('cell')},"touchstart":_vm.startDrag,"touchmove":_vm.onDrag,"touchend":_vm.endDrag,"touchcancel":_vm.endDrag}},[_c('div',{staticClass:"van-cell-swipe__wrapper",style:(_vm.wrapperStyle),on:{"transitionend":function($event){_vm.swipe = false}}},[(_vm.leftWidth)?_c('div',{staticClass:"van-cell-swipe__left",on:{"click":function($event){$event.stopPropagation();_vm.onClick('left')}}},[_vm._t("left")],2):_vm._e(),_vm._t("default"),(_vm.rightWidth)?_c('div',{staticClass:"van-cell-swipe__right",on:{"click":function($event){$event.stopPropagation();_vm.onClick('right')}}},[_vm._t("right")],2):_vm._e()],2)])}
var cell_swipe_staticRenderFns = []
var cell_swipe_esExports = { render: cell_swipe_render, staticRenderFns: cell_swipe_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_cell_swipe = (cell_swipe_esExports);
// CONCATENATED MODULE: ./packages/cell-swipe/index.vue
var cell_swipe_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var cell_swipe___vue_template_functional__ = false
/* styles */
var cell_swipe___vue_styles__ = null
/* scopeId */
var cell_swipe___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var cell_swipe___vue_module_identifier__ = null
var cell_swipe_Component = cell_swipe_normalizeComponent(
  cell_swipe,
  selectortype_template_index_0_packages_cell_swipe,
  cell_swipe___vue_template_functional__,
  cell_swipe___vue_styles__,
  cell_swipe___vue_scopeId__,
  cell_swipe___vue_module_identifier__
)

/* harmony default export */ var packages_cell_swipe = (cell_swipe_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/checkbox/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var selectortype_script_index_0_packages_checkbox = (Object(utils["b" /* create */])({
  name: 'van-checkbox',

  mixins: [find_parent],

  props: {
    value: {},
    disabled: Boolean,
    name: [String, Number],
    shape: {
      type: String,
      default: 'round'
    }
  },

  watch: {
    value: function value(val) {
      this.$emit('change', val);
    }
  },

  computed: {
    // whether is in van-checkbox-group
    isGroup: function isGroup() {
      return !!this.findParentByName('van-checkbox-group');
    },


    currentValue: {
      get: function get() {
        return this.isGroup && this.parentGroup ? this.parentGroup.value.indexOf(this.name) !== -1 : this.value;
      },
      set: function set(val) {
        if (this.isGroup && this.parentGroup) {
          var parentValue = this.parentGroup.value.slice();
          if (val) {
            /* istanbul ignore else */
            if (parentValue.indexOf(this.name) === -1) {
              parentValue.push(this.name);
              this.parentGroup.$emit('input', parentValue);
            }
          } else {
            var index = parentValue.indexOf(this.name);
            /* istanbul ignore else */
            if (index !== -1) {
              parentValue.splice(index, 1);
              this.parentGroup.$emit('input', parentValue);
            }
          }
        } else {
          this.$emit('input', val);
        }
      }
    },

    isChecked: function isChecked() {
      var currentValue = this.currentValue;

      if ({}.toString.call(currentValue) === '[object Boolean]') {
        return currentValue;
      } else if (currentValue !== null && currentValue !== undefined) {
        return currentValue === this.name;
      }
    },
    isDisabled: function isDisabled() {
      return this.isGroup && this.parentGroup && this.parentGroup.disabled || this.disabled;
    }
  },

  methods: {
    onClickLabel: function onClickLabel() {
      if (!this.isDisabled) {
        this.currentValue = !this.currentValue;
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-2b7c4213","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/checkbox/index.vue
var checkbox_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-checkbox",class:[
    ("van-checkbox--" + _vm.shape),
    { 'van-checkbox--disabled': _vm.isDisabled }
  ]},[_c('span',{staticClass:"van-checkbox__input"},[_c('input',{directives:[{name:"model",rawName:"v-model",value:(_vm.currentValue),expression:"currentValue"}],staticClass:"van-checkbox__control",attrs:{"type":"checkbox","disabled":_vm.isDisabled},domProps:{"checked":Array.isArray(_vm.currentValue)?_vm._i(_vm.currentValue,null)>-1:(_vm.currentValue)},on:{"change":function($event){var $$a=_vm.currentValue,$$el=$event.target,$$c=$$el.checked?(true):(false);if(Array.isArray($$a)){var $$v=null,$$i=_vm._i($$a,$$v);if($$el.checked){$$i<0&&(_vm.currentValue=$$a.concat([$$v]))}else{$$i>-1&&(_vm.currentValue=$$a.slice(0,$$i).concat($$a.slice($$i+1)))}}else{_vm.currentValue=$$c}}}}),_c('icon',{attrs:{"name":"success"}})],1),_c('span',{staticClass:"van-checkbox__label",on:{"click":_vm.onClickLabel}},[_vm._t("default")],2)])}
var checkbox_staticRenderFns = []
var checkbox_esExports = { render: checkbox_render, staticRenderFns: checkbox_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_checkbox = (checkbox_esExports);
// CONCATENATED MODULE: ./packages/checkbox/index.vue
var checkbox_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var checkbox___vue_template_functional__ = false
/* styles */
var checkbox___vue_styles__ = null
/* scopeId */
var checkbox___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var checkbox___vue_module_identifier__ = null
var checkbox_Component = checkbox_normalizeComponent(
  selectortype_script_index_0_packages_checkbox,
  selectortype_template_index_0_packages_checkbox,
  checkbox___vue_template_functional__,
  checkbox___vue_styles__,
  checkbox___vue_scopeId__,
  checkbox___vue_module_identifier__
)

/* harmony default export */ var packages_checkbox = (checkbox_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/checkbox-group/index.vue
//
//
//
//
//
//



/* harmony default export */ var checkbox_group = (Object(utils["b" /* create */])({
  name: 'van-checkbox-group',

  props: {
    value: {},
    disabled: Boolean
  },

  watch: {
    value: function value(val) {
      this.$emit('change', val);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-fb1a6664","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/checkbox-group/index.vue
var checkbox_group_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-checkbox-group"},[_vm._t("default")],2)}
var checkbox_group_staticRenderFns = []
var checkbox_group_esExports = { render: checkbox_group_render, staticRenderFns: checkbox_group_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_checkbox_group = (checkbox_group_esExports);
// CONCATENATED MODULE: ./packages/checkbox-group/index.vue
var checkbox_group_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var checkbox_group___vue_template_functional__ = false
/* styles */
var checkbox_group___vue_styles__ = null
/* scopeId */
var checkbox_group___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var checkbox_group___vue_module_identifier__ = null
var checkbox_group_Component = checkbox_group_normalizeComponent(
  checkbox_group,
  selectortype_template_index_0_packages_checkbox_group,
  checkbox_group___vue_template_functional__,
  checkbox_group___vue_styles__,
  checkbox_group___vue_scopeId__,
  checkbox_group___vue_module_identifier__
)

/* harmony default export */ var packages_checkbox_group = (checkbox_group_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/col/index.vue
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var col = (Object(utils["b" /* create */])({
  name: 'van-col',

  props: {
    span: [Number, String],
    offset: [Number, String]
  },

  computed: {
    gutter: function gutter() {
      return this.$parent && Number(this.$parent.gutter) || 0;
    },
    style: function style() {
      var padding = this.gutter / 2 + 'px';
      return this.gutter ? { paddingLeft: padding, paddingRight: padding } : {};
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-35686fc9","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/col/index.vue
var col_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-col",class:( _obj = {}, _obj[("van-col-" + _vm.span)] = _vm.span, _obj[("van-col-offset-" + _vm.offset)] = _vm.offset, _obj ),style:(_vm.style)},[_vm._t("default")],2)
var _obj;}
var col_staticRenderFns = []
var col_esExports = { render: col_render, staticRenderFns: col_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_col = (col_esExports);
// CONCATENATED MODULE: ./packages/col/index.vue
var col_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var col___vue_template_functional__ = false
/* styles */
var col___vue_styles__ = null
/* scopeId */
var col___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var col___vue_module_identifier__ = null
var col_Component = col_normalizeComponent(
  col,
  selectortype_template_index_0_packages_col,
  col___vue_template_functional__,
  col___vue_styles__,
  col___vue_scopeId__,
  col___vue_module_identifier__
)

/* harmony default export */ var packages_col = (col_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/contact-card/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var contact_card = (Object(utils["b" /* create */])({
  name: 'van-contact-card',

  props: {
    tel: String,
    name: String,
    addText: String,
    editable: {
      type: Boolean,
      default: true
    },
    type: {
      type: String,
      default: 'add'
    }
  },

  methods: {
    onClick: function onClick(event) {
      if (this.editable) {
        this.$emit('click', event);
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-18d50d4f","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/contact-card/index.vue
var contact_card_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-contact-card",class:[("van-contact-card--" + _vm.type), { 'van-contact-card--uneditable': !_vm.editable }],on:{"click":_vm.onClick}},[_c('div',{staticClass:"van-contact-card__content"},[(_vm.type === 'add')?[_c('icon',{staticClass:"van-contact-card__icon",attrs:{"name":"add2"}}),_c('div',{staticClass:"van-contact-card__text"},[_vm._v(_vm._s(_vm.addText || _vm.$t('addText')))])]:(_vm.type === 'edit')?[_c('icon',{staticClass:"van-contact-card__icon",attrs:{"name":"contact"}}),_c('div',{staticClass:"van-contact-card__text"},[_c('div',[_vm._v(_vm._s(_vm.$t('name'))+"："+_vm._s(_vm.name))]),_c('div',[_vm._v(_vm._s(_vm.$t('tel'))+"："+_vm._s(_vm.tel))])])]:_vm._e()],2),(_vm.editable)?_c('icon',{staticClass:"van-contact-card__arrow",attrs:{"name":"arrow"}}):_vm._e()],1)}
var contact_card_staticRenderFns = []
var contact_card_esExports = { render: contact_card_render, staticRenderFns: contact_card_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_contact_card = (contact_card_esExports);
// CONCATENATED MODULE: ./packages/contact-card/index.vue
var contact_card_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var contact_card___vue_template_functional__ = false
/* styles */
var contact_card___vue_styles__ = null
/* scopeId */
var contact_card___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var contact_card___vue_module_identifier__ = null
var contact_card_Component = contact_card_normalizeComponent(
  contact_card,
  selectortype_template_index_0_packages_contact_card,
  contact_card___vue_template_functional__,
  contact_card___vue_styles__,
  contact_card___vue_scopeId__,
  contact_card___vue_module_identifier__
)

/* harmony default export */ var packages_contact_card = (contact_card_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/contact-edit/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//









/* harmony default export */ var contact_edit = (Object(utils["b" /* create */])({
  name: 'van-contact-edit',

  components: {
    Field: packages_field,
    VanButton: packages_button,
    CellGroup: packages_cell_group
  },

  props: {
    isEdit: Boolean,
    isSaving: Boolean,
    isDeleting: Boolean,
    contactInfo: {
      type: Object,
      default: function _default() {
        return {
          id: '',
          tel: '',
          name: ''
        };
      }
    }
  },

  data: function data() {
    return {
      currentInfo: this.contactInfo,
      errorInfo: {
        name: false,
        tel: false
      }
    };
  },


  watch: {
    contactInfo: function contactInfo(val) {
      this.currentInfo = val;
    }
  },

  methods: {
    onFocus: function onFocus(key) {
      this.errorInfo[key] = false;
    },
    getErrorMessageByKey: function getErrorMessageByKey(key) {
      var value = this.currentInfo[key];
      switch (key) {
        case 'name':
          return value ? value.length <= 15 ? '' : this.$t('nameOverlimit') : this.$t('nameEmpty');
        case 'tel':
          return mobile(value) ? '' : this.$t('telInvalid');
      }
    },
    onSaveContact: function onSaveContact() {
      var _this = this;

      var items = ['name', 'tel'];

      var isValid = items.every(function (item) {
        var msg = _this.getErrorMessageByKey(item);
        if (msg) {
          _this.errorInfo[item] = true;
          packages_toast(msg);
        }
        return !msg;
      });

      if (isValid && !this.isSaving) {
        this.$emit('save', this.currentInfo);
      }
    },
    onDeleteContact: function onDeleteContact() {
      var _this2 = this;

      if (this.isDeleting) {
        return;
      }

      packages_dialog.confirm({
        message: this.$t('confirmDelete')
      }).then(function () {
        _this2.$emit('delete', _this2.currentInfo);
      });
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-8b385198","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/contact-edit/index.vue
var contact_edit_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-contact-edit"},[_c('cell-group',[_c('field',{attrs:{"maxlength":"30","label":_vm.$t('name'),"placeholder":_vm.$t('namePlaceholder'),"error":_vm.errorInfo.name},on:{"focus":function($event){_vm.onFocus('name')}},model:{value:(_vm.currentInfo.name),callback:function ($$v) {_vm.$set(_vm.currentInfo, "name", $$v)},expression:"currentInfo.name"}}),_c('field',{attrs:{"type":"tel","label":_vm.$t('tel'),"placeholder":_vm.$t('telPlaceholder'),"error":_vm.errorInfo.tel},on:{"focus":function($event){_vm.onFocus('tel')}},model:{value:(_vm.currentInfo.tel),callback:function ($$v) {_vm.$set(_vm.currentInfo, "tel", $$v)},expression:"currentInfo.tel"}})],1),_c('div',{staticClass:"van-contact-edit__buttons"},[_c('van-button',{attrs:{"block":"","loading":_vm.isSaving,"type":"primary"},on:{"click":_vm.onSaveContact}},[_vm._v(_vm._s(_vm.$t('save')))]),(_vm.isEdit)?_c('van-button',{attrs:{"block":"","loading":_vm.isDeleting},on:{"click":_vm.onDeleteContact}},[_vm._v(_vm._s(_vm.$t('delete')))]):_vm._e()],1)],1)}
var contact_edit_staticRenderFns = []
var contact_edit_esExports = { render: contact_edit_render, staticRenderFns: contact_edit_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_contact_edit = (contact_edit_esExports);
// CONCATENATED MODULE: ./packages/contact-edit/index.vue
var contact_edit_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var contact_edit___vue_template_functional__ = false
/* styles */
var contact_edit___vue_styles__ = null
/* scopeId */
var contact_edit___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var contact_edit___vue_module_identifier__ = null
var contact_edit_Component = contact_edit_normalizeComponent(
  contact_edit,
  selectortype_template_index_0_packages_contact_edit,
  contact_edit___vue_template_functional__,
  contact_edit___vue_styles__,
  contact_edit___vue_scopeId__,
  contact_edit___vue_module_identifier__
)

/* harmony default export */ var packages_contact_edit = (contact_edit_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/contact-list/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//







/* harmony default export */ var contact_list = (Object(utils["b" /* create */])({
  name: 'van-contact-list',

  components: {
    Cell: packages_cell,
    Radio: packages_radio,
    CellGroup: packages_cell_group,
    RadioGroup: packages_radio_group
  },

  props: {
    value: {},
    addText: String,
    list: {
      type: Array,
      default: function _default() {
        return [];
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-8cc60cce","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/contact-list/index.vue
var contact_list_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-contact-list"},[_c('radio-group',{attrs:{"value":_vm.value},on:{"input":function($event){_vm.$emit('input', $event)}}},[_c('cell-group',_vm._l((_vm.list),function(item,index){return _c('cell',{key:item.id,attrs:{"is-link":""}},[_c('radio',{attrs:{"name":item.id},on:{"click":function($event){_vm.$emit('select', item, index)}}},[_c('p',{staticClass:"van-contact-list__text"},[_vm._v(_vm._s(_vm.$t('name'))+"："+_vm._s(item.name))]),_c('p',{staticClass:"van-contact-list__text"},[_vm._v(_vm._s(_vm.$t('tel'))+"："+_vm._s(item.tel))])]),_c('icon',{staticClass:"van-contact-list__edit",attrs:{"slot":"right-icon","name":"edit"},on:{"click":function($event){_vm.$emit('edit', item, index)}},slot:"right-icon"})],1)}))],1),_c('cell',{staticClass:"van-contact-list__add van-hairline--top",attrs:{"icon":"add","title":_vm.addText || _vm.$t('addText'),"is-link":""},on:{"click":function($event){_vm.$emit('add')}}})],1)}
var contact_list_staticRenderFns = []
var contact_list_esExports = { render: contact_list_render, staticRenderFns: contact_list_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_contact_list = (contact_list_esExports);
// CONCATENATED MODULE: ./packages/contact-list/index.vue
var contact_list_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var contact_list___vue_template_functional__ = false
/* styles */
var contact_list___vue_styles__ = null
/* scopeId */
var contact_list___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var contact_list___vue_module_identifier__ = null
var contact_list_Component = contact_list_normalizeComponent(
  contact_list,
  selectortype_template_index_0_packages_contact_list,
  contact_list___vue_template_functional__,
  contact_list___vue_styles__,
  contact_list___vue_scopeId__,
  contact_list___vue_module_identifier__
)

/* harmony default export */ var packages_contact_list = (contact_list_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/coupon-cell/index.vue
//
//
//
//
//
//





/* harmony default export */ var coupon_cell = (Object(utils["b" /* create */])({
  name: 'van-coupon-cell',

  components: {
    Cell: packages_cell,
    CellGroup: packages_cell_group
  },

  model: {
    prop: 'chosenCoupon'
  },

  props: {
    title: String,
    coupons: {
      type: Array,
      default: function _default() {
        return [];
      }
    },
    chosenCoupon: {
      type: Number,
      default: -1
    },
    editable: {
      type: Boolean,
      default: true
    }
  },

  computed: {
    value: function value() {
      var coupons = this.coupons;

      var coupon = coupons[this.chosenCoupon];
      if (coupon) {
        return this.$t('reduce') + '\uFFE5' + (coupon.value / 100).toFixed(2);
      }
      return coupons.length === 0 ? this.$t('tips') : this.$t('count', coupons.length);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-5bcdb30a","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/coupon-cell/index.vue
var coupon_cell_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('cell-group',{staticClass:"van-coupon-cell"},[_c('cell',{attrs:{"title":_vm.title || _vm.$t('title'),"value":_vm.value,"is-link":_vm.editable},on:{"click":function($event){_vm.$emit('click')}}})],1)}
var coupon_cell_staticRenderFns = []
var coupon_cell_esExports = { render: coupon_cell_render, staticRenderFns: coupon_cell_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_coupon_cell = (coupon_cell_esExports);
// CONCATENATED MODULE: ./packages/coupon-cell/index.vue
var coupon_cell_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var coupon_cell___vue_template_functional__ = false
/* styles */
var coupon_cell___vue_styles__ = null
/* scopeId */
var coupon_cell___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var coupon_cell___vue_module_identifier__ = null
var coupon_cell_Component = coupon_cell_normalizeComponent(
  coupon_cell,
  selectortype_template_index_0_packages_coupon_cell,
  coupon_cell___vue_template_functional__,
  coupon_cell___vue_styles__,
  coupon_cell___vue_scopeId__,
  coupon_cell___vue_module_identifier__
)

/* harmony default export */ var packages_coupon_cell = (coupon_cell_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/coupon-list/Item.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var Item = (Object(utils["b" /* create */])({
  name: 'van-coupon-item',

  props: {
    data: Object,
    chosen: Boolean,
    disabled: Boolean
  },

  computed: {
    validPeriod: function validPeriod() {
      return this.getDate(this.data.start_at) + '-' + this.getDate(this.data.end_at);
    },
    faceAmount: function faceAmount() {
      return this.data.denominations !== 0 ? '<span>\xA5</span> ' + this.formatAmount(this.data.denominations) : this.data.discount !== 0 ? this.formatDiscount(this.data.discount) : '';
    },
    conditionMessage: function conditionMessage() {
      var condition = this.data.origin_condition;
      condition = condition % 100 === 0 ? Math.round(condition / 100) : (condition / 100).toFixed(2);
      return this.data.origin_condition === 0 ? this.$t('unlimited') : this.$t('condition', condition);
    }
  },

  methods: {
    getDate: function getDate(timeStamp) {
      var date = new Date(timeStamp * 1000);
      return date.getFullYear() + '.' + this.padZero(date.getMonth() + 1) + '.' + this.padZero(date.getDate());
    },
    padZero: function padZero(num) {
      return (num < 10 ? '0' : '') + num;
    },
    formatDiscount: function formatDiscount(discount) {
      return this.$t('discount', '' + (discount / 10).toFixed(discount % 10 === 0 ? 0 : 1));
    },
    formatAmount: function formatAmount(amount) {
      return (amount / 100).toFixed(amount % 100 === 0 ? 0 : amount % 10 === 0 ? 1 : 2);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-291e56f0","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/coupon-list/Item.vue
var Item_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-coupon-item",class:{ 'van-coupon-item--disabled': _vm.disabled }},[_c('div',{staticClass:"van-coupon-item__head"},[_c('div',{staticClass:"van-coupon-item__lines"}),_c('div',{staticClass:"van-coupon-item__gradient"},[_c('h2',{domProps:{"innerHTML":_vm._s(_vm.faceAmount)}}),_c('p',[_vm._v(_vm._s(_vm.conditionMessage))])])]),_c('div',{staticClass:"van-coupon-item__body"},[_c('h2',[_vm._v(_vm._s(_vm.data.name))]),_c('span',[_vm._v(_vm._s(_vm.validPeriod))]),(_vm.disabled && _vm.data.reason)?_c('p',[_vm._v(_vm._s(_vm.data.reason))]):_vm._e(),(_vm.chosen)?_c('div',{staticClass:"van-coupon-item__corner"},[_c('icon',{attrs:{"name":"success"}})],1):_vm._e()])])}
var Item_staticRenderFns = []
var Item_esExports = { render: Item_render, staticRenderFns: Item_staticRenderFns }
/* harmony default export */ var coupon_list_Item = (Item_esExports);
// CONCATENATED MODULE: ./packages/coupon-list/Item.vue
var Item_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var Item___vue_template_functional__ = false
/* styles */
var Item___vue_styles__ = null
/* scopeId */
var Item___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var Item___vue_module_identifier__ = null
var Item_Component = Item_normalizeComponent(
  Item,
  coupon_list_Item,
  Item___vue_template_functional__,
  Item___vue_styles__,
  Item___vue_scopeId__,
  Item___vue_module_identifier__
)

/* harmony default export */ var packages_coupon_list_Item = (Item_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/coupon-list/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//









/* harmony default export */ var coupon_list = (Object(utils["b" /* create */])({
  name: 'van-coupon-list',

  components: {
    VanButton: packages_button,
    Cell: packages_cell,
    CellGroup: packages_cell_group,
    Field: packages_field,
    Popup: packages_popup,
    CouponItem: packages_coupon_list_Item
  },

  model: {
    prop: 'code'
  },

  props: {
    code: String,
    closeButtonText: String,
    inputPlaceholder: String,
    disabledListTitle: String,
    exchangeButtonText: String,
    exchangeButtonLoading: Boolean,
    exchangeButtonDisabled: Boolean,
    exchangeMinLength: {
      type: Number,
      default: 1
    },
    chosenCoupon: {
      type: Number,
      default: -1
    },
    coupons: {
      type: Array,
      default: function _default() {
        return [];
      }
    },
    disabledCoupons: {
      type: Array,
      default: function _default() {
        return [];
      }
    },
    displayedCouponIndex: {
      type: Number,
      default: -1
    },
    showExchangeBar: {
      type: Boolean,
      default: true
    },
    showCloseButton: {
      type: Boolean,
      default: true
    }
  },

  data: function data() {
    return {
      currentCode: this.code || ''
    };
  },


  computed: {
    buttonDisabled: function buttonDisabled() {
      return !this.exchangeButtonLoading && (this.exchangeButtonDisabled || this.currentCode.length < this.exchangeMinLength);
    }
  },

  watch: {
    code: function code(_code) {
      this.currentCode = _code;
    },
    currentCode: function currentCode(code) {
      this.$emit('input', code);
    },
    displayedCouponIndex: function displayedCouponIndex(val) {
      this.scrollToShowCoupon(val);
    }
  },

  mounted: function mounted() {
    this.scrollToShowCoupon(this.displayedCouponIndex);
  },


  methods: {
    onClickExchangeButton: function onClickExchangeButton() {
      this.$emit('exchange', this.currentCode);

      // auto clear currentCode when not use v-model
      if (!this.code) {
        this.currentCode = '';
      }
    },


    // scroll to show specific coupon
    scrollToShowCoupon: function scrollToShowCoupon(index) {
      var _this = this;

      if (index === -1) {
        return;
      }

      this.$nextTick(function () {
        var _$refs = _this.$refs,
            card = _$refs.card,
            list = _$refs.list;


        if (list && card && card[index]) {
          list.scrollTop = card[index].$el.offsetTop - 100;
        }
      });
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-4bc6d832","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/coupon-list/index.vue
var coupon_list_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-coupon-list"},[(_vm.showExchangeBar)?_c('cell-group',{staticClass:"van-coupon-list__top"},[_c('field',{staticClass:"van-coupon-list__filed van-hairline--surround",attrs:{"placeholder":_vm.inputPlaceholder || _vm.$t('placeholder'),"maxlength":20},model:{value:(_vm.currentCode),callback:function ($$v) {_vm.currentCode=$$v},expression:"currentCode"}}),_c('van-button',{staticClass:"van-coupon-list__exchange",attrs:{"size":"small","type":"danger","text":_vm.exchangeButtonText || _vm.$t('exchange'),"loading":_vm.exchangeButtonLoading,"disabled":_vm.buttonDisabled},on:{"click":_vm.onClickExchangeButton}})],1):_vm._e(),_c('div',{ref:"list",staticClass:"van-coupon-list__list",class:{ 'van-coupon-list--with-exchange': _vm.showExchangeBar }},[_vm._l((_vm.coupons),function(item,index){return _c('coupon-item',{key:item.id || item.name,ref:"card",refInFor:true,attrs:{"data":item,"chosen":index === _vm.chosenCoupon},nativeOn:{"click":function($event){_vm.$emit('change', index)}}})}),(_vm.disabledCoupons.length)?_c('h3',[_vm._v(_vm._s(_vm.disabledListTitle || _vm.$t('disabled')))]):_vm._e(),_vm._l((_vm.disabledCoupons),function(item){return _c('coupon-item',{key:item.id || item.name,attrs:{"disabled":"","data":item}})}),(!_vm.coupons.length && !_vm.disabledCoupons.length)?_c('div',{staticClass:"van-coupon-list__empty"},[_c('img',{attrs:{"src":"https://b.yzcdn.cn/v2/image/wap/trade/new_order/empty@2x.png"}}),_c('p',[_vm._v(_vm._s(_vm.$t('empty')))])]):_vm._e()],2),_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.showCloseButton),expression:"showCloseButton"}],staticClass:"van-coupon-list__close van-hairline--top",domProps:{"textContent":_vm._s(_vm.closeButtonText || _vm.$t('close'))},on:{"click":function($event){_vm.$emit('change', -1)}}})],1)}
var coupon_list_staticRenderFns = []
var coupon_list_esExports = { render: coupon_list_render, staticRenderFns: coupon_list_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_coupon_list = (coupon_list_esExports);
// CONCATENATED MODULE: ./packages/coupon-list/index.vue
var coupon_list_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var coupon_list___vue_template_functional__ = false
/* styles */
var coupon_list___vue_styles__ = null
/* scopeId */
var coupon_list___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var coupon_list___vue_module_identifier__ = null
var coupon_list_Component = coupon_list_normalizeComponent(
  coupon_list,
  selectortype_template_index_0_packages_coupon_list,
  coupon_list___vue_template_functional__,
  coupon_list___vue_styles__,
  coupon_list___vue_scopeId__,
  coupon_list___vue_module_identifier__
)

/* harmony default export */ var packages_coupon_list = (coupon_list_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/datetime-picker/index.vue
//
//
//
//
//
//
//
//
//
//
//
//




var isValidDate = function isValidDate(date) {
  return Object.prototype.toString.call(date) === '[object Date]' && !isNaN(date.getTime());
};

/* harmony default export */ var datetime_picker = (Object(utils["b" /* create */])({
  name: 'van-datetime-picker',

  components: {
    Picker: packages_picker
  },

  props: {
    type: {
      type: String,
      default: 'datetime'
    },
    format: {
      type: String,
      default: 'YYYY.MM.DD HH时 mm分'
    },
    visibleItemCount: {
      type: Number,
      default: 5
    },
    minDate: {
      type: Date,
      default: function _default() {
        return new Date(new Date().getFullYear() - 10, 0, 1);
      },

      validator: isValidDate
    },
    maxDate: {
      type: Date,
      default: function _default() {
        return new Date(new Date().getFullYear() + 10, 11, 31);
      },

      validator: isValidDate
    },
    minHour: {
      type: Number,
      default: 0
    },
    maxHour: {
      type: Number,
      default: 23
    },
    value: {}
  },

  data: function data() {
    return {
      innerValue: this.correctValue(this.value)
    };
  },


  watch: {
    value: function value(val) {
      val = this.correctValue(val);
      var isEqual = this.type === 'time' ? val === this.innerValue : val.valueOf() === this.innerValue.valueOf();
      if (!isEqual) this.innerValue = val;
    },
    innerValue: function innerValue(val) {
      this.updateColumnValue(val);
      this.$emit('input', val);
    }
  },

  computed: {
    ranges: function ranges() {
      if (this.type === 'time') {
        return [[this.minHour, this.maxHour], [0, 59]];
      }

      var _getBoundary = this.getBoundary('max', this.innerValue),
          maxYear = _getBoundary.maxYear,
          maxDate = _getBoundary.maxDate,
          maxMonth = _getBoundary.maxMonth,
          maxHour = _getBoundary.maxHour,
          maxMinute = _getBoundary.maxMinute;

      var _getBoundary2 = this.getBoundary('min', this.innerValue),
          minYear = _getBoundary2.minYear,
          minDate = _getBoundary2.minDate,
          minMonth = _getBoundary2.minMonth,
          minHour = _getBoundary2.minHour,
          minMinute = _getBoundary2.minMinute;

      var result = [[minYear, maxYear], [minMonth, maxMonth], [minDate, maxDate], [minHour, maxHour], [minMinute, maxMinute]];

      if (this.type === 'date') result.splice(3, 2);
      return result;
    },
    columns: function columns() {
      var _this = this;

      var results = this.ranges.map(function (range) {
        var values = _this.times(range[1] - range[0] + 1, function (index) {
          var value = range[0] + index;
          return value < 10 ? '0' + value : '' + value;
        });

        return {
          values: values
        };
      });
      return results;
    }
  },

  methods: {
    correctValue: function correctValue(value) {
      // validate value
      var isDateType = this.type.indexOf('date') > -1;
      if (isDateType && !isValidDate(value)) {
        value = this.minDate;
      } else if (!value) {
        var _minHour = this.minHour;

        value = (_minHour > 10 ? _minHour : '0' + _minHour) + ':00';
      }

      // time type
      if (!isDateType) {
        var _value$split = value.split(':'),
            hour = _value$split[0],
            minute = _value$split[1];

        var correctedHour = Math.max(hour, this.minHour);
        correctedHour = Math.min(correctedHour, this.maxHour);

        return correctedHour + ':' + minute;
      }

      // date type

      var _getBoundary3 = this.getBoundary('max', value),
          maxYear = _getBoundary3.maxYear,
          maxDate = _getBoundary3.maxDate,
          maxMonth = _getBoundary3.maxMonth,
          maxHour = _getBoundary3.maxHour,
          maxMinute = _getBoundary3.maxMinute;

      var _getBoundary4 = this.getBoundary('min', value),
          minYear = _getBoundary4.minYear,
          minDate = _getBoundary4.minDate,
          minMonth = _getBoundary4.minMonth,
          minHour = _getBoundary4.minHour,
          minMinute = _getBoundary4.minMinute;

      var minDay = new Date(minYear, minMonth - 1, minDate, minHour, minMinute);
      var maxDay = new Date(maxYear, maxMonth - 1, maxDate, maxHour, maxMinute);
      value = Math.max(value, minDay);
      value = Math.min(value, maxDay);

      return new Date(value);
    },
    times: function times(n, iteratee) {
      var index = -1;
      var result = Array(n);

      while (++index < n) {
        result[index] = iteratee(index);
      }
      return result;
    },
    getBoundary: function getBoundary(type, value) {
      var _ref;

      var boundary = this[type + 'Date'];
      var year = boundary.getFullYear();
      var month = 1;
      var date = 1;
      var hour = 0;
      var minute = 0;

      if (type === 'max') {
        month = 12;
        date = this.getMonthEndDay(value.getFullYear(), value.getMonth() + 1);
        hour = 23;
        minute = 59;
      }

      if (value.getFullYear() === year) {
        month = boundary.getMonth() + 1;
        if (value.getMonth() + 1 === month) {
          date = boundary.getDate();
          if (value.getDate() === date) {
            hour = boundary.getHours();
            if (value.getHours() === hour) {
              minute = boundary.getMinutes();
            }
          }
        }
      }

      return _ref = {}, _ref[type + 'Year'] = year, _ref[type + 'Month'] = month, _ref[type + 'Date'] = date, _ref[type + 'Hour'] = hour, _ref[type + 'Minute'] = minute, _ref;
    },
    getTrueValue: function getTrueValue(formattedValue) {
      if (!formattedValue) return;
      while (isNaN(parseInt(formattedValue, 10))) {
        formattedValue = formattedValue.slice(1);
      }
      return parseInt(formattedValue, 10);
    },
    getMonthEndDay: function getMonthEndDay(year, month) {
      if (this.isShortMonth(month)) {
        return 30;
      } else if (month === 2) {
        return this.isLeapYear(year) ? 29 : 28;
      } else {
        return 31;
      }
    },
    isLeapYear: function isLeapYear(year) {
      return year % 400 === 0 || year % 100 !== 0 && year % 4 === 0;
    },
    isShortMonth: function isShortMonth(month) {
      return [4, 6, 9, 11].indexOf(month) > -1;
    },
    onConfirm: function onConfirm() {
      this.$emit('confirm', this.innerValue);
    },
    onChange: function onChange(picker) {
      var values = picker.getValues();
      var value = void 0;

      if (this.type === 'time') {
        value = values.join(':');
      } else {
        var year = this.getTrueValue(values[0]);
        var month = this.getTrueValue(values[1]);
        var maxDate = this.getMonthEndDay(year, month);
        var date = this.getTrueValue(values[2]);
        date = date > maxDate ? maxDate : date;
        var hour = 0;
        var minute = 0;
        if (this.type === 'datetime') {
          hour = this.getTrueValue(values[3]);
          minute = this.getTrueValue(values[4]);
        }
        value = new Date(year, month - 1, date, hour, minute);
      }
      value = this.correctValue(value);
      this.innerValue = value;
      this.$emit('change', picker);
    },
    updateColumnValue: function updateColumnValue(value) {
      var _this2 = this;

      var values = [];
      if (this.type === 'time') {
        var currentValue = value.split(':');
        values = [currentValue[0], currentValue[1]];
      } else {
        values = ['' + value.getFullYear(), ('0' + (value.getMonth() + 1)).slice(-2), ('0' + value.getDate()).slice(-2)];
        if (this.type === 'datetime') {
          values.push(('0' + value.getHours()).slice(-2), ('0' + value.getMinutes()).slice(-2));
        }
      }

      this.$nextTick(function () {
        _this2.setColumnByValues(values);
      });
    },
    setColumnByValues: function setColumnByValues(values) {
      if (!this.$refs.picker) {
        return;
      }
      this.$refs.picker.setValues(values);
    }
  },

  mounted: function mounted() {
    this.updateColumnValue(this.innerValue);
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-a6d06f46","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/datetime-picker/index.vue
var datetime_picker_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('picker',{ref:"picker",attrs:{"show-toolbar":"","columns":_vm.columns,"visible-item-count":_vm.visibleItemCount},on:{"change":_vm.onChange,"confirm":_vm.onConfirm,"cancel":function($event){_vm.$emit('cancel')}}})}
var datetime_picker_staticRenderFns = []
var datetime_picker_esExports = { render: datetime_picker_render, staticRenderFns: datetime_picker_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_datetime_picker = (datetime_picker_esExports);
// CONCATENATED MODULE: ./packages/datetime-picker/index.vue
var datetime_picker_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var datetime_picker___vue_template_functional__ = false
/* styles */
var datetime_picker___vue_styles__ = null
/* scopeId */
var datetime_picker___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var datetime_picker___vue_module_identifier__ = null
var datetime_picker_Component = datetime_picker_normalizeComponent(
  datetime_picker,
  selectortype_template_index_0_packages_datetime_picker,
  datetime_picker___vue_template_functional__,
  datetime_picker___vue_styles__,
  datetime_picker___vue_scopeId__,
  datetime_picker___vue_module_identifier__
)

/* harmony default export */ var packages_datetime_picker = (datetime_picker_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/goods-action/index.vue
//
//
//
//
//
//



/* harmony default export */ var goods_action = (Object(utils["b" /* create */])({
  name: 'van-goods-action'
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-1e722987","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/goods-action/index.vue
var goods_action_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-goods-action"},[_vm._t("default")],2)}
var goods_action_staticRenderFns = []
var goods_action_esExports = { render: goods_action_render, staticRenderFns: goods_action_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_goods_action = (goods_action_esExports);
// CONCATENATED MODULE: ./packages/goods-action/index.vue
var goods_action_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var goods_action___vue_template_functional__ = false
/* styles */
var goods_action___vue_styles__ = null
/* scopeId */
var goods_action___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var goods_action___vue_module_identifier__ = null
var goods_action_Component = goods_action_normalizeComponent(
  goods_action,
  selectortype_template_index_0_packages_goods_action,
  goods_action___vue_template_functional__,
  goods_action___vue_styles__,
  goods_action___vue_scopeId__,
  goods_action___vue_module_identifier__
)

/* harmony default export */ var packages_goods_action = (goods_action_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/goods-action-big-btn/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//





/* harmony default export */ var goods_action_big_btn = (Object(utils["b" /* create */])({
  name: 'van-goods-action-big-btn',

  mixins: [router_link],

  components: {
    VanButton: packages_button
  },

  props: {
    url: String,
    text: String,
    primary: Boolean
  },

  methods: {
    onClick: function onClick(event) {
      this.$emit('click', event);
      this.routerLink();
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-52d629b0","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/goods-action-big-btn/index.vue
var goods_action_big_btn_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('van-button',{staticClass:"van-goods-action__big-btn",attrs:{"tag":"a","href":_vm.url,"type":_vm.primary ? 'primary' : 'default',"bottom-action":""},on:{"click":_vm.onClick}},[_vm._t("default",[_vm._v(_vm._s(_vm.text))])],2)}
var goods_action_big_btn_staticRenderFns = []
var goods_action_big_btn_esExports = { render: goods_action_big_btn_render, staticRenderFns: goods_action_big_btn_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_goods_action_big_btn = (goods_action_big_btn_esExports);
// CONCATENATED MODULE: ./packages/goods-action-big-btn/index.vue
var goods_action_big_btn_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var goods_action_big_btn___vue_template_functional__ = false
/* styles */
var goods_action_big_btn___vue_styles__ = null
/* scopeId */
var goods_action_big_btn___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var goods_action_big_btn___vue_module_identifier__ = null
var goods_action_big_btn_Component = goods_action_big_btn_normalizeComponent(
  goods_action_big_btn,
  selectortype_template_index_0_packages_goods_action_big_btn,
  goods_action_big_btn___vue_template_functional__,
  goods_action_big_btn___vue_styles__,
  goods_action_big_btn___vue_scopeId__,
  goods_action_big_btn___vue_module_identifier__
)

/* harmony default export */ var packages_goods_action_big_btn = (goods_action_big_btn_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/goods-action-mini-btn/index.vue
//
//
//
//
//
//
//




/* harmony default export */ var goods_action_mini_btn = (Object(utils["b" /* create */])({
  name: 'van-goods-action-mini-btn',

  mixins: [router_link],

  props: {
    url: String,
    text: String,
    info: String,
    icon: String,
    iconClass: String
  },

  methods: {
    onClick: function onClick(event) {
      this.$emit('click', event);
      this.routerLink();
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-412bb4ca","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/goods-action-mini-btn/index.vue
var goods_action_mini_btn_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('a',{staticClass:"van-goods-action__mini-btn van-hairline",attrs:{"href":_vm.url},on:{"click":_vm.onClick}},[_c('icon',{staticClass:"van-goods-action__mini-btn-icon",class:_vm.iconClass,attrs:{"info":_vm.info,"name":_vm.icon}}),_vm._t("default",[_vm._v(_vm._s(_vm.text))])],2)}
var goods_action_mini_btn_staticRenderFns = []
var goods_action_mini_btn_esExports = { render: goods_action_mini_btn_render, staticRenderFns: goods_action_mini_btn_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_goods_action_mini_btn = (goods_action_mini_btn_esExports);
// CONCATENATED MODULE: ./packages/goods-action-mini-btn/index.vue
var goods_action_mini_btn_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var goods_action_mini_btn___vue_template_functional__ = false
/* styles */
var goods_action_mini_btn___vue_styles__ = null
/* scopeId */
var goods_action_mini_btn___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var goods_action_mini_btn___vue_module_identifier__ = null
var goods_action_mini_btn_Component = goods_action_mini_btn_normalizeComponent(
  goods_action_mini_btn,
  selectortype_template_index_0_packages_goods_action_mini_btn,
  goods_action_mini_btn___vue_template_functional__,
  goods_action_mini_btn___vue_styles__,
  goods_action_mini_btn___vue_scopeId__,
  goods_action_mini_btn___vue_module_identifier__
)

/* harmony default export */ var packages_goods_action_mini_btn = (goods_action_mini_btn_Component.exports);

// EXTERNAL MODULE: ./packages/icon/index.vue + 2 modules
var icon = __webpack_require__(56);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/swipe/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var swipe = (Object(utils["b" /* create */])({
  name: 'van-swipe',

  props: {
    autoplay: Number,
    initialSwipe: {
      type: Number,
      default: 0
    },
    showIndicators: {
      type: Boolean,
      default: true
    },
    duration: {
      type: Number,
      default: 500
    }
  },

  data: function data() {
    return {
      width: 0,
      offset: 0,
      startX: 0,
      startY: 0,
      active: 0,
      deltaX: 0,
      swipes: [],
      direction: '',
      currentDuration: 0
    };
  },
  mounted: function mounted() {
    this.initialize();
  },
  destroyed: function destroyed() {
    clearTimeout(this.timer);
  },


  watch: {
    swipes: function swipes() {
      this.initialize();
    },
    initialSwipe: function initialSwipe() {
      this.initialize();
    }
  },

  computed: {
    count: function count() {
      return this.swipes.length;
    },
    trackStyle: function trackStyle() {
      return {
        paddingLeft: this.width + 'px',
        width: (this.count + 2) * this.width + 'px',
        transitionDuration: this.currentDuration + 'ms',
        transform: 'translate(' + this.offset + 'px, 0)'
      };
    },
    activeIndicator: function activeIndicator() {
      return (this.active + this.count) % this.count;
    }
  },

  methods: {
    initialize: function initialize() {
      // reset offset when children changes
      clearTimeout(this.timer);
      this.width = this.$el.getBoundingClientRect().width;
      this.active = this.initialSwipe;
      this.currentDuration = 0;
      this.offset = this.count > 1 ? -this.width * (this.active + 1) : 0;
      this.swipes.forEach(function (swipe) {
        swipe.offset = 0;
      });
      this.autoPlay();
    },
    onTouchStart: function onTouchStart(event) {
      clearTimeout(this.timer);

      this.deltaX = 0;
      this.direction = '';
      this.currentDuration = 0;
      this.startX = event.touches[0].clientX;
      this.startY = event.touches[0].clientY;

      if (this.active <= -1) {
        this.move(this.count);
      }
      if (this.active >= this.count) {
        this.move(-this.count);
      }
    },
    onTouchMove: function onTouchMove(event) {
      this.direction = this.direction || this.getDirection(event.touches[0]);

      if (this.direction === 'horizontal') {
        event.preventDefault();
        this.deltaX = event.touches[0].clientX - this.startX;
        this.move(0, this.range(this.deltaX, [-this.width, this.width]));
      }
    },
    onTouchEnd: function onTouchEnd() {
      if (this.deltaX) {
        this.move(Math.abs(this.deltaX) > 50 ? this.deltaX > 0 ? -1 : 1 : 0);
        this.currentDuration = this.duration;
      }
      this.autoPlay();
    },
    move: function move() {
      var move = arguments.length > 0 && arguments[0] !== undefined ? arguments[0] : 0;
      var offset = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;
      var active = this.active,
          count = this.count,
          swipes = this.swipes,
          deltaX = this.deltaX,
          width = this.width;


      if (move) {
        if (active === -1) {
          swipes[count - 1].offset = 0;
        }
        swipes[0].offset = active === count - 1 && move > 0 ? count * width : 0;

        this.active += move;
      } else {
        if (active === 0) {
          swipes[count - 1].offset = deltaX > 0 ? -count * width : 0;
        } else if (active === count - 1) {
          swipes[0].offset = deltaX < 0 ? count * width : 0;
        }
      }
      this.offset = offset - (this.active + 1) * this.width;
    },
    autoPlay: function autoPlay() {
      var _this = this;

      var autoplay = this.autoplay;

      if (autoplay && this.count > 1) {
        clearTimeout(this.timer);
        this.timer = setTimeout(function () {
          _this.currentDuration = 0;

          if (_this.active >= _this.count) {
            _this.move(-_this.count);
          }

          setTimeout(function () {
            _this.currentDuration = _this.duration;
            _this.move(1);
            _this.autoPlay();
          }, 30);
        }, autoplay);
      }
    },
    getDirection: function getDirection(touch) {
      var distanceX = Math.abs(touch.clientX - this.startX);
      var distanceY = Math.abs(touch.clientY - this.startY);
      return distanceX > distanceY ? 'horizontal' : distanceX < distanceY ? 'vertical' : '';
    },
    range: function range(num, arr) {
      return Math.min(Math.max(num, arr[0]), arr[1]);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-b0a743f8","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/swipe/index.vue
var swipe_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-swipe"},[(_vm.count > 1)?_c('div',{staticClass:"van-swipe__track",style:(_vm.trackStyle),on:{"touchstart":_vm.onTouchStart,"touchmove":_vm.onTouchMove,"touchend":_vm.onTouchEnd,"touchcancel":_vm.onTouchEnd,"transitionend":function($event){_vm.$emit('change', _vm.activeIndicator)}}},[_vm._t("default")],2):_c('div',{staticClass:"van-swipe__track"},[_vm._t("default")],2),(_vm.showIndicators && _vm.count > 1)?_c('div',{staticClass:"van-swipe__indicators"},_vm._l((_vm.count),function(index){return _c('i',{class:{ 'van-swipe__indicator--active': index - 1 === _vm.activeIndicator }})})):_vm._e()])}
var swipe_staticRenderFns = []
var swipe_esExports = { render: swipe_render, staticRenderFns: swipe_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_swipe = (swipe_esExports);
// CONCATENATED MODULE: ./packages/swipe/index.vue
var swipe_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var swipe___vue_template_functional__ = false
/* styles */
var swipe___vue_styles__ = null
/* scopeId */
var swipe___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var swipe___vue_module_identifier__ = null
var swipe_Component = swipe_normalizeComponent(
  swipe,
  selectortype_template_index_0_packages_swipe,
  swipe___vue_template_functional__,
  swipe___vue_styles__,
  swipe___vue_scopeId__,
  swipe___vue_module_identifier__
)

/* harmony default export */ var packages_swipe = (swipe_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/swipe-item/index.vue
//
//
//
//
//
//



/* harmony default export */ var swipe_item = (Object(utils["b" /* create */])({
  name: 'van-swipe-item',

  data: function data() {
    return {
      offset: 0
    };
  },


  computed: {
    style: function style() {
      return {
        width: this.$parent.width + 'px',
        transform: 'translate(' + this.offset + 'px, 0)'
      };
    }
  },

  beforeCreate: function beforeCreate() {
    this.$parent.swipes.push(this);
  },
  destroyed: function destroyed() {
    this.$parent.swipes.splice(this.$parent.swipes.indexOf(this), 1);
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-58c92f4e","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/swipe-item/index.vue
var swipe_item_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-swipe-item",style:(_vm.style)},[_vm._t("default")],2)}
var swipe_item_staticRenderFns = []
var swipe_item_esExports = { render: swipe_item_render, staticRenderFns: swipe_item_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_swipe_item = (swipe_item_esExports);
// CONCATENATED MODULE: ./packages/swipe-item/index.vue
var swipe_item_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var swipe_item___vue_template_functional__ = false
/* styles */
var swipe_item___vue_styles__ = null
/* scopeId */
var swipe_item___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var swipe_item___vue_module_identifier__ = null
var swipe_item_Component = swipe_item_normalizeComponent(
  swipe_item,
  selectortype_template_index_0_packages_swipe_item,
  swipe_item___vue_template_functional__,
  swipe_item___vue_styles__,
  swipe_item___vue_scopeId__,
  swipe_item___vue_module_identifier__
)

/* harmony default export */ var packages_swipe_item = (swipe_item_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/image-preview/image-preview.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//






/* harmony default export */ var image_preview = (Object(utils["b" /* create */])({
  name: 'van-image-preview',

  mixins: [popup],

  components: {
    Swipe: packages_swipe,
    SwipeItem: packages_swipe_item
  },

  props: {
    overlay: {
      type: Boolean,
      default: true
    },
    closeOnClickOverlay: {
      type: Boolean,
      default: true
    }
  },

  data: function data() {
    return {
      images: [],
      startPosition: 0
    };
  },


  methods: {
    onTouchStart: function onTouchStart(event) {
      this.touchStartTime = new Date();
      this.touchStartX = event.touches[0].clientX;
      this.touchStartY = event.touches[0].clientY;
      this.deltaX = 0;
      this.deltaY = 0;
    },
    onTouchMove: function onTouchMove(event) {
      event.preventDefault();
      this.deltaX = event.touches[0].clientX - this.touchStartX;
      this.deltaY = event.touches[0].clientY - this.touchStartY;
    },
    onTouchEnd: function onTouchEnd(event) {
      event.preventDefault();
      // prevent long tap to close component
      var deltaTime = new Date() - this.touchStartTime;
      if (deltaTime < 100 && Math.abs(this.deltaX) < 20 && Math.abs(this.deltaY) < 20) {
        this.close();
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-56e4111e","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/image-preview/image-preview.vue
var image_preview_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.value),expression:"value"}],staticClass:"van-image-preview",on:{"touchstart":_vm.onTouchStart,"touchmove":_vm.onTouchMove,"touchend":_vm.onTouchEnd,"touchcancel":_vm.onTouchEnd}},[_c('swipe',{attrs:{"initial-swipe":_vm.startPosition}},_vm._l((_vm.images),function(item,index){return _c('swipe-item',{key:index},[_c('img',{staticClass:"van-image-preview__image",attrs:{"src":item}})])}))],1)}
var image_preview_staticRenderFns = []
var image_preview_esExports = { render: image_preview_render, staticRenderFns: image_preview_staticRenderFns }
/* harmony default export */ var image_preview_image_preview = (image_preview_esExports);
// CONCATENATED MODULE: ./packages/image-preview/image-preview.vue
var image_preview_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var image_preview___vue_template_functional__ = false
/* styles */
var image_preview___vue_styles__ = null
/* scopeId */
var image_preview___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var image_preview___vue_module_identifier__ = null
var image_preview_Component = image_preview_normalizeComponent(
  image_preview,
  image_preview_image_preview,
  image_preview___vue_template_functional__,
  image_preview___vue_styles__,
  image_preview___vue_scopeId__,
  image_preview___vue_module_identifier__
)

/* harmony default export */ var packages_image_preview_image_preview = (image_preview_Component.exports);

// CONCATENATED MODULE: ./packages/image-preview/index.js



var image_preview_instance = void 0;

var ImagePreviewConstructor = external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a.extend(packages_image_preview_image_preview);

var image_preview_initInstance = function initInstance() {
  image_preview_instance = new ImagePreviewConstructor({
    el: document.createElement('div')
  });
  document.body.appendChild(image_preview_instance.$el);
};

var ImagePreviewBox = function ImagePreviewBox(images) {
  var startPosition = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

  if (!image_preview_instance) {
    image_preview_initInstance();
  }

  image_preview_instance.images = images;
  image_preview_instance.startPosition = startPosition;
  image_preview_instance.value = true;
  image_preview_instance.$on('input', function (show) {
    image_preview_instance.value = show;
  });

  return image_preview_instance;
};

/* harmony default export */ var packages_image_preview = (ImagePreviewBox);
// EXTERNAL MODULE: ./node_modules/vue-lazyload/vue-lazyload.js
var vue_lazyload = __webpack_require__(108);
var vue_lazyload_default = /*#__PURE__*/__webpack_require__.n(vue_lazyload);

// CONCATENATED MODULE: ./packages/lazyload/index.js


/* harmony default export */ var lazyload = (vue_lazyload_default.a);
// EXTERNAL MODULE: ./packages/loading/index.vue + 2 modules
var loading = __webpack_require__(57);

// EXTERNAL MODULE: ./packages/locale/index.js + 1 modules
var locale = __webpack_require__(42);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/nav-bar/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var nav_bar = (Object(utils["b" /* create */])({
  name: 'van-nav-bar',

  props: {
    title: String,
    leftText: String,
    rightText: String,
    leftArrow: Boolean,
    fixed: Boolean,
    zIndex: {
      type: Number,
      default: 1
    }
  },

  computed: {
    style: function style() {
      return {
        zIndex: this.zIndex
      };
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-4ef54693","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/nav-bar/index.vue
var nav_bar_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-nav-bar van-hairline--top-bottom",class:{ 'van-nav-bar--fixed': _vm.fixed },style:(_vm.style)},[_c('div',{staticClass:"van-nav-bar__left",on:{"click":function($event){_vm.$emit('click-left')}}},[_vm._t("left",[(_vm.leftArrow)?_c('icon',{staticClass:"van-nav-bar__arrow",attrs:{"name":"arrow"}}):_vm._e(),(_vm.leftText)?_c('span',{staticClass:"van-nav-bar__text",domProps:{"textContent":_vm._s(_vm.leftText)}}):_vm._e()])],2),_c('div',{staticClass:"van-nav-bar__title"},[_vm._t("title",[_vm._v(_vm._s(_vm.title))])],2),_c('div',{staticClass:"van-nav-bar__right",on:{"click":function($event){_vm.$emit('click-right')}}},[_vm._t("right",[(_vm.rightText)?_c('span',{staticClass:"van-nav-bar__text",domProps:{"textContent":_vm._s(_vm.rightText)}}):_vm._e()])],2)])}
var nav_bar_staticRenderFns = []
var nav_bar_esExports = { render: nav_bar_render, staticRenderFns: nav_bar_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_nav_bar = (nav_bar_esExports);
// CONCATENATED MODULE: ./packages/nav-bar/index.vue
var nav_bar_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var nav_bar___vue_template_functional__ = false
/* styles */
var nav_bar___vue_styles__ = null
/* scopeId */
var nav_bar___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var nav_bar___vue_module_identifier__ = null
var nav_bar_Component = nav_bar_normalizeComponent(
  nav_bar,
  selectortype_template_index_0_packages_nav_bar,
  nav_bar___vue_template_functional__,
  nav_bar___vue_styles__,
  nav_bar___vue_scopeId__,
  nav_bar___vue_module_identifier__
)

/* harmony default export */ var packages_nav_bar = (nav_bar_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/notice-bar/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var notice_bar = (Object(utils["b" /* create */])({
  name: 'van-notice-bar',

  props: {
    text: String,
    mode: String,
    color: String,
    leftIcon: String,
    background: String,
    delay: {
      type: [String, Number],
      default: 1
    },
    scrollable: {
      type: Boolean,
      default: true
    },
    speed: {
      type: Number,
      default: 50
    }
  },

  data: function data() {
    return {
      wrapWidth: 0,
      firstRound: true,
      duration: 0,
      offsetWidth: 0,
      showNoticeBar: true,
      animationClass: ''
    };
  },


  computed: {
    iconName: function iconName() {
      return this.mode === 'closeable' ? 'close' : this.mode === 'link' ? 'arrow' : '';
    },
    barStyle: function barStyle() {
      return {
        color: this.color,
        background: this.background
      };
    },
    contentStyle: function contentStyle() {
      return {
        paddingLeft: this.firstRound ? 0 : this.wrapWidth + 'px',
        animationDelay: (this.firstRound ? this.delay : 0) + 's',
        animationDuration: this.duration + 's'
      };
    }
  },

  mounted: function mounted() {
    this.initAnimation();
  },


  watch: {
    text: function text() {
      this.$nextTick(this.initAnimation);
    }
  },

  methods: {
    onClickIcon: function onClickIcon() {
      this.showNoticeBar = this.mode !== 'closeable';
    },
    onAnimationEnd: function onAnimationEnd() {
      var _this = this;

      this.firstRound = false;
      this.$nextTick(function () {
        _this.duration = (_this.offsetWidth + _this.wrapWidth) / _this.speed;
        _this.animationClass = 'van-notice-bar__play--infinite';
      });
    },
    initAnimation: function initAnimation() {
      var offsetWidth = this.$refs.content.getBoundingClientRect().width;
      var wrapWidth = this.$refs.contentWrap.getBoundingClientRect().width;
      if (this.scrollable && offsetWidth > wrapWidth) {
        this.wrapWidth = wrapWidth;
        this.offsetWidth = offsetWidth;
        this.duration = offsetWidth / this.speed;
        this.animationClass = 'van-notice-bar__play';
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-1daa995e","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/notice-bar/index.vue
var notice_bar_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.showNoticeBar),expression:"showNoticeBar"}],staticClass:"van-notice-bar",class:{ 'van-notice-bar--withicon': _vm.mode },style:(_vm.barStyle),on:{"click":function($event){_vm.$emit('click')}}},[(_vm.leftIcon)?_c('div',{staticClass:"van-notice-bar__left-icon"},[_c('img',{attrs:{"src":_vm.leftIcon}})]):_vm._e(),_c('div',{ref:"contentWrap",staticClass:"van-notice-bar__content-wrap"},[_c('div',{ref:"content",staticClass:"van-notice-bar__content",class:_vm.animationClass,style:(_vm.contentStyle),on:{"animationend":_vm.onAnimationEnd,"webkitAnimationEnd":_vm.onAnimationEnd}},[_vm._t("default",[_vm._v(_vm._s(_vm.text))])],2)]),(_vm.iconName)?_c('icon',{staticClass:"van-notice-bar__right-icon",attrs:{"name":_vm.iconName},on:{"click":_vm.onClickIcon}}):_vm._e()],1)}
var notice_bar_staticRenderFns = []
var notice_bar_esExports = { render: notice_bar_render, staticRenderFns: notice_bar_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_notice_bar = (notice_bar_esExports);
// CONCATENATED MODULE: ./packages/notice-bar/index.vue
var notice_bar_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var notice_bar___vue_template_functional__ = false
/* styles */
var notice_bar___vue_styles__ = null
/* scopeId */
var notice_bar___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var notice_bar___vue_module_identifier__ = null
var notice_bar_Component = notice_bar_normalizeComponent(
  notice_bar,
  selectortype_template_index_0_packages_notice_bar,
  notice_bar___vue_template_functional__,
  notice_bar___vue_styles__,
  notice_bar___vue_scopeId__,
  notice_bar___vue_module_identifier__
)

/* harmony default export */ var packages_notice_bar = (notice_bar_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/number-keyboard/Key.vue
//
//
//
//
//
//
//
//
//
//
//
//

/* harmony default export */ var Key = ({
  props: {
    text: [String, Number],
    type: {
      type: Array,
      default: function _default() {
        return [];
      }
    }
  },

  data: function data() {
    return {
      active: false
    };
  },


  computed: {
    className: function className() {
      var types = this.type.slice(0);
      this.active && types.push('active');

      return types.map(function (type) {
        return 'van-key--' + type;
      });
    }
  },

  methods: {
    onFocus: function onFocus() {
      this.active = true;
      this.$emit('press', this.text);
    },
    onBlur: function onBlur() {
      this.active = false;
    }
  }
});
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-71ce0aef","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/number-keyboard/Key.vue
var Key_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('i',{staticClass:"van-hairline van-key",class:_vm.className,domProps:{"textContent":_vm._s(_vm.text)},on:{"touchstart":function($event){$event.stopPropagation();$event.preventDefault();_vm.onFocus($event)},"touchmove":_vm.onBlur,"touchend":_vm.onBlur,"touchcancel":_vm.onBlur}})}
var Key_staticRenderFns = []
var Key_esExports = { render: Key_render, staticRenderFns: Key_staticRenderFns }
/* harmony default export */ var number_keyboard_Key = (Key_esExports);
// CONCATENATED MODULE: ./packages/number-keyboard/Key.vue
var Key_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var Key___vue_template_functional__ = false
/* styles */
var Key___vue_styles__ = null
/* scopeId */
var Key___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var Key___vue_module_identifier__ = null
var Key_Component = Key_normalizeComponent(
  Key,
  number_keyboard_Key,
  Key___vue_template_functional__,
  Key___vue_styles__,
  Key___vue_scopeId__,
  Key___vue_module_identifier__
)

/* harmony default export */ var packages_number_keyboard_Key = (Key_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/number-keyboard/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var number_keyboard = (Object(utils["b" /* create */])({
  name: 'van-number-keyboard',

  components: { Key: packages_number_keyboard_Key },

  props: {
    show: Boolean,
    title: String,
    closeButtonText: String,
    theme: {
      type: String,
      default: 'default'
    },
    extraKey: {
      type: String,
      default: ''
    },
    zIndex: {
      type: Number,
      default: 100
    },
    transition: {
      type: Boolean,
      default: true
    },
    showDeleteKey: {
      type: Boolean,
      default: true
    },
    hideOnClickOutside: {
      type: Boolean,
      default: true
    }
  },

  mounted: function mounted() {
    this.handler(true);
  },
  destroyed: function destroyed() {
    this.handler(false);
  },
  activated: function activated() {
    this.handler(true);
  },
  deactivated: function deactivated() {
    this.handler(false);
  },


  watch: {
    show: function show() {
      if (!this.transition) {
        this.$emit(this.show ? 'show' : 'hide');
      }
    }
  },

  computed: {
    keys: function keys() {
      var keys = [];
      for (var i = 1; i <= 9; i++) {
        keys.push({ text: i });
      }

      switch (this.theme) {
        case 'default':
          keys.push({ text: this.extraKey, type: ['gray'] }, { text: 0 }, { text: 'delete', type: ['gray', 'delete'] });
          break;
        case 'custom':
          keys.push({ text: 0, type: ['middle'] }, { text: this.extraKey });
          break;
      }

      return keys;
    },
    style: function style() {
      return {
        zIndex: this.zIndex
      };
    },
    showTitleClose: function showTitleClose() {
      return this.closeButtonText && this.theme === 'default';
    }
  },

  methods: {
    handler: function handler(action) {
      if (action !== this.handlerStatus && this.hideOnClickOutside) {
        this.handlerStatus = action;
        document.body[(action ? 'add' : 'remove') + 'EventListener']('touchstart', this.onBlur);
      }
    },
    onBlur: function onBlur() {
      this.$emit('blur');
    },
    onAnimationEnd: function onAnimationEnd() {
      this.$emit(this.show ? 'show' : 'hide');
    },
    onPressKey: function onPressKey(text) {
      if (text === '') {
        return;
      }

      if (text === 'delete') {
        this.$emit('delete');
      } else if (text === this.closeButtonText) {
        this.onBlur();
      } else {
        this.$emit('input', text);
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-f84dc7da","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/number-keyboard/index.vue
var number_keyboard_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('transition',{attrs:{"name":_vm.transition ? 'van-slide-bottom' : ''}},[_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.show),expression:"show"}],staticClass:"van-number-keyboard",class:("van-number-keyboard--" + _vm.theme),style:(_vm.style),on:{"animationend":_vm.onAnimationEnd,"webkitAnimationEnd":_vm.onAnimationEnd}},[(_vm.title || _vm.showTitleClose)?_c('div',{staticClass:"van-number-keyboard__title van-hairline--top"},[_c('span',{domProps:{"textContent":_vm._s(_vm.title)}}),(_vm.showTitleClose)?_c('span',{staticClass:"van-number-keyboard__close",domProps:{"textContent":_vm._s(_vm.closeButtonText)},on:{"click":_vm.onBlur}}):_vm._e()]):_vm._e(),_c('div',{staticClass:"van-number-keyboard__body"},_vm._l((_vm.keys),function(key,index){return _c('key',{key:index,attrs:{"text":key.text,"type":key.type},on:{"press":_vm.onPressKey}})})),(_vm.theme === 'custom')?_c('div',{staticClass:"van-number-keyboard__sidebar"},[_c('key',{attrs:{"text":'delete',"type":['delete', 'big']},on:{"press":_vm.onPressKey}}),_c('key',{attrs:{"text":_vm.closeButtonText,"type":['green', 'big']},on:{"press":_vm.onPressKey}})],1):_vm._e()])])}
var number_keyboard_staticRenderFns = []
var number_keyboard_esExports = { render: number_keyboard_render, staticRenderFns: number_keyboard_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_number_keyboard = (number_keyboard_esExports);
// CONCATENATED MODULE: ./packages/number-keyboard/index.vue
var number_keyboard_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var number_keyboard___vue_template_functional__ = false
/* styles */
var number_keyboard___vue_styles__ = null
/* scopeId */
var number_keyboard___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var number_keyboard___vue_module_identifier__ = null
var number_keyboard_Component = number_keyboard_normalizeComponent(
  number_keyboard,
  selectortype_template_index_0_packages_number_keyboard,
  number_keyboard___vue_template_functional__,
  number_keyboard___vue_styles__,
  number_keyboard___vue_scopeId__,
  number_keyboard___vue_module_identifier__
)

/* harmony default export */ var packages_number_keyboard = (number_keyboard_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/pagination/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var pagination = (Object(utils["b" /* create */])({
  name: 'van-pagination',

  props: {
    value: Number,
    prevText: String,
    nextText: String,
    pageCount: Number,
    forceEllipses: Boolean,
    mode: {
      type: String,
      default: 'multi'
    },
    itemsPerPage: {
      type: Number,
      default: 10
    },
    showPageSize: {
      type: Number,
      default: 5
    },
    totalItems: {
      type: Number,
      default: 0
    }
  },

  computed: {
    isMultiMode: function isMultiMode() {
      return this.mode === 'multi';
    },
    computedPageCount: function computedPageCount() {
      var count = this.pageCount || Math.ceil(this.totalItems / this.itemsPerPage);
      return Math.max(1, count);
    },
    pageDesc: function pageDesc() {
      return this.value + '/' + this.computedPageCount;
    },
    pages: function pages() {
      var pages = [];
      var pageCount = this.computedPageCount;

      // Default page limits
      var startPage = 1;
      var endPage = pageCount;
      var isMaxSized = this.showPageSize !== undefined && this.showPageSize < pageCount;

      // recompute if showPageSize
      if (isMaxSized) {
        // Current page is displayed in the middle of the visible ones
        startPage = Math.max(this.value - Math.floor(this.showPageSize / 2), 1);
        endPage = startPage + this.showPageSize - 1;

        // Adjust if limit is exceeded
        if (endPage > pageCount) {
          endPage = pageCount;
          startPage = endPage - this.showPageSize + 1;
        }
      }

      // Add page number links
      for (var number = startPage; number <= endPage; number++) {
        var page = this.makePage(number, number, number === this.value);
        pages.push(page);
      }

      // Add links to move between page sets
      if (isMaxSized && this.showPageSize > 0 && this.forceEllipses) {
        if (startPage > 1) {
          var previousPageSet = this.makePage(startPage - 1, '...', false);
          pages.unshift(previousPageSet);
        }

        if (endPage < pageCount) {
          var nextPageSet = this.makePage(endPage + 1, '...', false);
          pages.push(nextPageSet);
        }
      }

      return pages;
    }
  },

  created: function created() {
    this.selectPage(this.value);
  },


  watch: {
    value: function value(page) {
      this.selectPage(page);
    }
  },

  methods: {
    selectPage: function selectPage(page) {
      page = Math.max(1, page);
      page = Math.min(this.computedPageCount, page);
      if (this.value !== page) {
        this.$emit('input', page);
        this.$emit('change', page);
      }
    },
    makePage: function makePage(number, text, active) {
      return { number: number, text: text, active: active };
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-0d60c2ed","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/pagination/index.vue
var pagination_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('ul',{staticClass:"van-pagination",class:{ 'van-pagination-simple': !_vm.isMultiMode }},[_c('li',{staticClass:"van-pagination__item van-pagination__prev van-hairline",class:{ 'van-pagination--disabled': _vm.value === 1 },on:{"click":function($event){_vm.selectPage(_vm.value - 1)}}},[_vm._v("\n    "+_vm._s(_vm.prevText || _vm.$t('prev'))+"\n  ")]),_vm._l((_vm.pages),function(page,index){return (_vm.isMultiMode)?_c('li',{key:index,staticClass:"van-pagination__item van-pagination__page van-hairline",class:{ 'van-pagination--active': page.active },on:{"click":function($event){_vm.selectPage(page.number)}}},[_vm._v("\n    "+_vm._s(page.text)+"\n  ")]):_vm._e()}),(!_vm.isMultiMode)?_c('li',{staticClass:"van-pagination__page-desc"},[_vm._t("pageDesc",[_vm._v(_vm._s(_vm.pageDesc))])],2):_vm._e(),_c('li',{staticClass:"van-pagination__item van-pagination__next van-hairline",class:{ 'van-pagination--disabled': _vm.value === _vm.computedPageCount },on:{"click":function($event){_vm.selectPage(_vm.value + 1)}}},[_vm._v("\n    "+_vm._s(_vm.nextText || _vm.$t('next'))+"\n  ")])],2)}
var pagination_staticRenderFns = []
var pagination_esExports = { render: pagination_render, staticRenderFns: pagination_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_pagination = (pagination_esExports);
// CONCATENATED MODULE: ./packages/pagination/index.vue
var pagination_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var pagination___vue_template_functional__ = false
/* styles */
var pagination___vue_styles__ = null
/* scopeId */
var pagination___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var pagination___vue_module_identifier__ = null
var pagination_Component = pagination_normalizeComponent(
  pagination,
  selectortype_template_index_0_packages_pagination,
  pagination___vue_template_functional__,
  pagination___vue_styles__,
  pagination___vue_scopeId__,
  pagination___vue_module_identifier__
)

/* harmony default export */ var packages_pagination = (pagination_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/panel/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var panel = (Object(utils["b" /* create */])({
  name: 'van-panel',
  props: {
    desc: String,
    title: String,
    status: String
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-4a8d51f0","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/panel/index.vue
var panel_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-panel van-hairline--top-bottom"},[_c('div',{staticClass:"van-panel__header van-hairline--bottom"},[_vm._t("header",[_c('div',{staticClass:"van-panel__title",domProps:{"textContent":_vm._s(_vm.title)}}),(_vm.desc)?_c('span',{staticClass:"van-panel__desc",domProps:{"textContent":_vm._s(_vm.desc)}}):_vm._e(),(_vm.status)?_c('span',{staticClass:"van-panel__status",domProps:{"textContent":_vm._s(_vm.status)}}):_vm._e()])],2),_c('div',{staticClass:"van-panel__content"},[_vm._t("default")],2),(_vm.$slots.footer)?_c('div',{staticClass:"van-panel__footer van-hairline--top"},[_vm._t("footer")],2):_vm._e()])}
var panel_staticRenderFns = []
var panel_esExports = { render: panel_render, staticRenderFns: panel_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_panel = (panel_esExports);
// CONCATENATED MODULE: ./packages/panel/index.vue
var panel_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var panel___vue_template_functional__ = false
/* styles */
var panel___vue_styles__ = null
/* scopeId */
var panel___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var panel___vue_module_identifier__ = null
var panel_Component = panel_normalizeComponent(
  panel,
  selectortype_template_index_0_packages_panel,
  panel___vue_template_functional__,
  panel___vue_styles__,
  panel___vue_scopeId__,
  panel___vue_module_identifier__
)

/* harmony default export */ var packages_panel = (panel_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/password-input/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var password_input = (Object(utils["b" /* create */])({
  name: 'van-password-input',

  props: {
    info: String,
    errorInfo: String,
    value: {
      type: String,
      default: ''
    },
    length: {
      type: Number,
      default: 6
    }
  },

  computed: {
    points: function points() {
      var arr = [];
      for (var i = 0; i < this.length; i++) {
        arr[i] = this.value[i] ? 'visible' : 'hidden';
      }
      return arr;
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-4b534ea6","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/password-input/index.vue
var password_input_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-password-input"},[_c('ul',{staticClass:"van-password-input__security van-hairline--surround",on:{"touchstart":function($event){$event.stopPropagation();_vm.$emit('focus')}}},_vm._l((_vm.points),function(visibility){return _c('li',{staticClass:"van-hairline"},[_c('i',{style:(("visibility: " + visibility))})])})),(_vm.errorInfo || _vm.info)?_c('div',{class:_vm.errorInfo ? 'van-password-input__error-info' : 'van-password-input__info',domProps:{"textContent":_vm._s(_vm.errorInfo || _vm.info)}}):_vm._e()])}
var password_input_staticRenderFns = []
var password_input_esExports = { render: password_input_render, staticRenderFns: password_input_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_password_input = (password_input_esExports);
// CONCATENATED MODULE: ./packages/password-input/index.vue
var password_input_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var password_input___vue_template_functional__ = false
/* styles */
var password_input___vue_styles__ = null
/* scopeId */
var password_input___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var password_input___vue_module_identifier__ = null
var password_input_Component = password_input_normalizeComponent(
  password_input,
  selectortype_template_index_0_packages_password_input,
  password_input___vue_template_functional__,
  password_input___vue_styles__,
  password_input___vue_scopeId__,
  password_input___vue_module_identifier__
)

/* harmony default export */ var packages_password_input = (password_input_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/progress/index.vue
//
//
//
//
//
//
//



/* harmony default export */ var progress = (Object(utils["b" /* create */])({
  name: 'van-progress',

  props: {
    inactive: Boolean,
    percentage: {
      type: Number,
      required: true,
      validator: function validator(value) {
        return value >= 0 && value <= 100;
      }
    },
    showPivot: {
      type: Boolean,
      default: true
    },
    pivotText: {
      type: String,
      default: function _default() {
        return this.percentage + '%';
      }
    },
    color: {
      type: String,
      default: '#38f'
    },
    textColor: {
      type: String,
      default: '#fff'
    }
  },

  computed: {
    componentColor: function componentColor() {
      return this.inactive ? '#cacaca' : this.color;
    },
    pivotStyle: function pivotStyle() {
      var percentage = this.percentage;

      return {
        color: this.textColor,
        backgroundColor: this.componentColor,
        left: percentage <= 5 ? '0%' : percentage >= 95 ? '100%' : percentage + '%',
        marginLeft: percentage <= 5 ? '0' : percentage >= 95 ? '-28px' : '-14px'
      };
    },
    portionStyle: function portionStyle() {
      return {
        width: this.percentage + '%',
        backgroundColor: this.componentColor
      };
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-55974366","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/progress/index.vue
var progress_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-progress"},[_c('span',{staticClass:"van-progress__portion",style:(_vm.portionStyle)}),_c('span',{directives:[{name:"show",rawName:"v-show",value:(_vm.showPivot),expression:"showPivot"}],staticClass:"van-progress__pivot",style:(_vm.pivotStyle)},[_vm._v(_vm._s(_vm.pivotText))])])}
var progress_staticRenderFns = []
var progress_esExports = { render: progress_render, staticRenderFns: progress_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_progress = (progress_esExports);
// CONCATENATED MODULE: ./packages/progress/index.vue
var progress_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var progress___vue_template_functional__ = false
/* styles */
var progress___vue_styles__ = null
/* scopeId */
var progress___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var progress___vue_module_identifier__ = null
var progress_Component = progress_normalizeComponent(
  progress,
  selectortype_template_index_0_packages_progress,
  progress___vue_template_functional__,
  progress___vue_styles__,
  progress___vue_scopeId__,
  progress___vue_module_identifier__
)

/* harmony default export */ var packages_progress = (progress_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/pull-refresh/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var pull_refresh = (Object(utils["b" /* create */])({
  name: 'van-pull-refresh',

  props: {
    pullingText: String,
    loosingText: String,
    loadingText: String,
    value: {
      type: Boolean,
      required: true
    },
    animationDuration: {
      type: Number,
      default: 300
    },
    headHeight: {
      type: Number,
      default: 50
    }
  },

  data: function data() {
    return {
      status: 'normal',
      height: 0,
      duration: 0
    };
  },


  computed: {
    style: function style() {
      return {
        transition: this.duration + 'ms',
        transform: 'translate3d(0,' + this.height + 'px, 0)'
      };
    }
  },

  mounted: function mounted() {
    this.scrollEl = utils_scroll.getScrollEventTarget(this.$el);
  },


  watch: {
    value: function value(val) {
      if (!val) {
        this.duration = this.animationDuration;
        this.getStatus(0);
      }
    }
  },

  methods: {
    onTouchStart: function onTouchStart(event) {
      if (this.status === 'loading') {
        return;
      }
      if (this.getCeiling()) {
        this.duration = 0;
        this.startX = event.touches[0].clientX;
        this.startY = event.touches[0].clientY;
      }
    },
    onTouchMove: function onTouchMove(event) {
      if (this.status === 'loading') {
        return;
      }

      this.deltaY = event.touches[0].clientY - this.startY;
      this.direction = this.getDirection(event.touches[0]);

      if (!this.ceiling && this.getCeiling()) {
        this.duration = 0;
        this.startY = event.touches[0].clientY;
        this.deltaY = 0;
      }

      if (this.ceiling && this.deltaY >= 0) {
        if (this.direction === 'vertical') {
          this.getStatus(this.ease(this.deltaY));
          event.preventDefault();
        }
      }
    },
    onTouchEnd: function onTouchEnd() {
      if (this.status === 'loading') {
        return;
      }

      if (this.ceiling && this.deltaY) {
        this.duration = this.animationDuration;
        if (this.status === 'loosing') {
          this.getStatus(this.headHeight, true);
          this.$emit('input', true);
        } else {
          this.getStatus(0);
        }
      }
    },
    getCeiling: function getCeiling() {
      this.ceiling = utils_scroll.getScrollTop(this.scrollEl) === 0;
      return this.ceiling;
    },
    ease: function ease(height) {
      var headHeight = this.headHeight;

      return height < headHeight ? height : height < headHeight * 2 ? Math.round(headHeight + (height - headHeight) / 2) : Math.round(headHeight * 1.5 + (height - headHeight * 2) / 4);
    },
    getStatus: function getStatus(height, isLoading) {
      this.height = height;

      var status = isLoading ? 'loading' : height === 0 ? 'normal' : height < this.headHeight ? 'pulling' : 'loosing';

      if (status !== this.status) {
        this.status = status;
      }
    },
    getDirection: function getDirection(touch) {
      var distanceX = Math.abs(touch.clientX - this.startX);
      var distanceY = Math.abs(touch.clientY - this.startY);
      return distanceX > distanceY ? 'horizontal' : distanceX < distanceY ? 'vertical' : '';
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-76b37c43","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/pull-refresh/index.vue
var pull_refresh_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-pull-refresh"},[_c('div',{staticClass:"van-pull-refresh__track",style:(_vm.style),on:{"touchstart":_vm.onTouchStart,"touchmove":_vm.onTouchMove,"touchend":_vm.onTouchEnd,"touchcancel":_vm.onTouchEnd}},[_c('div',{staticClass:"van-pull-refresh__head"},[(_vm.status === 'normal')?_vm._t("normal"):_vm._e(),(_vm.status === 'pulling')?_vm._t("pulling",[_c('span',{staticClass:"van-pull-refresh__text"},[_vm._v(_vm._s(_vm.pullingText || _vm.$t('pullingText')))])]):_vm._e(),(_vm.status === 'loosing')?_vm._t("loosing",[_c('span',{staticClass:"van-pull-refresh__text"},[_vm._v(_vm._s(_vm.loosingText || _vm.$t('loosingText')))])]):_vm._e(),(_vm.status === 'loading')?_vm._t("loading",[_c('div',{staticClass:"van-pull-refresh__loading"},[_c('loading'),_c('span',[_vm._v(_vm._s(_vm.loadingText || _vm.$t('loadingText')))])],1)]):_vm._e()],2),_vm._t("default")],2)])}
var pull_refresh_staticRenderFns = []
var pull_refresh_esExports = { render: pull_refresh_render, staticRenderFns: pull_refresh_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_pull_refresh = (pull_refresh_esExports);
// CONCATENATED MODULE: ./packages/pull-refresh/index.vue
var pull_refresh_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var pull_refresh___vue_template_functional__ = false
/* styles */
var pull_refresh___vue_styles__ = null
/* scopeId */
var pull_refresh___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var pull_refresh___vue_module_identifier__ = null
var pull_refresh_Component = pull_refresh_normalizeComponent(
  pull_refresh,
  selectortype_template_index_0_packages_pull_refresh,
  pull_refresh___vue_template_functional__,
  pull_refresh___vue_styles__,
  pull_refresh___vue_scopeId__,
  pull_refresh___vue_module_identifier__
)

/* harmony default export */ var packages_pull_refresh = (pull_refresh_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/row/index.vue
//
//
//
//
//
//



/* harmony default export */ var row = (Object(utils["b" /* create */])({
  name: 'van-row',

  props: {
    gutter: {
      type: [Number, String],
      default: 0
    }
  },

  computed: {
    style: function style() {
      var margin = '-' + Number(this.gutter) / 2 + 'px';
      return this.gutter ? { marginLeft: margin, marginRight: margin } : {};
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-65344d0a","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/row/index.vue
var row_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-row",style:(_vm.style)},[_vm._t("default")],2)}
var row_staticRenderFns = []
var row_esExports = { render: row_render, staticRenderFns: row_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_row = (row_esExports);
// CONCATENATED MODULE: ./packages/row/index.vue
var row_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var row___vue_template_functional__ = false
/* styles */
var row___vue_styles__ = null
/* scopeId */
var row___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var row___vue_module_identifier__ = null
var row_Component = row_normalizeComponent(
  row,
  selectortype_template_index_0_packages_row,
  row___vue_template_functional__,
  row___vue_styles__,
  row___vue_scopeId__,
  row___vue_module_identifier__
)

/* harmony default export */ var packages_row = (row_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/search/index.vue

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var search = (Object(utils["b" /* create */])({
  name: 'van-search',

  inheritAttrs: false,

  props: {
    value: String,
    showAction: Boolean,
    background: {
      type: String,
      default: '#f2f2f2'
    }
  },

  data: function data() {
    return {
      isFocus: false,
      focusStatus: false
    };
  },


  directives: {
    Clickoutside: clickoutside,
    refocus: {
      update: function update(el, state) {
        if (state.value) {
          el.focus();
        }
      }
    }
  },

  computed: {
    listeners: function listeners() {
      return extends_default()({}, this.$listeners, {
        focus: this.onFocus,
        input: this.onInput,
        keypress: this.onKeypress
      });
    }
  },

  methods: {
    onFocus: function onFocus() {
      this.isFocus = true;
      this.$emit('focus');
    },
    onInput: function onInput(event) {
      this.$emit('input', event.target.value);
    },
    onKeypress: function onKeypress(event) {
      // press enter
      if (event.keyCode === 13) {
        event.preventDefault();
        this.$emit('search', this.value);
      }
      this.$emit('keypress', event);
    },


    // refocus after click close icon
    onClean: function onClean() {
      var _this = this;

      this.$emit('input', '');
      this.focusStatus = true;

      // ensure refocus can work after click clean icon
      this.$nextTick(function () {
        _this.focusStatus = false;
      });
    },
    onBack: function onBack() {
      this.$emit('input', '');
      this.$emit('cancel');
    },
    onClickoutside: function onClickoutside() {
      this.isFocus = false;
      this.focusStatus = false;
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-58e57c8c","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/search/index.vue
var search_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-search",class:{ 'van-search--show-action': _vm.showAction },style:({ 'background-color': _vm.background })},[_c('div',{directives:[{name:"clickoutside",rawName:"v-clickoutside",value:(_vm.onClickoutside),expression:"onClickoutside"}],staticClass:"van-search__input-wrap"},[_c('icon',{attrs:{"name":"search"}}),_c('input',_vm._g(_vm._b({directives:[{name:"refocus",rawName:"v-refocus",value:(_vm.focusStatus),expression:"focusStatus"}],staticClass:"van-search__input",attrs:{"type":"search"},domProps:{"value":_vm.value}},'input',_vm.$attrs,false),_vm.listeners)),_c('icon',{directives:[{name:"show",rawName:"v-show",value:(_vm.isFocus && _vm.value),expression:"isFocus && value"}],attrs:{"name":"clear"},on:{"click":_vm.onClean}})],1),(_vm.showAction)?_c('div',{staticClass:"van-search__action"},[_vm._t("action",[_c('div',{staticClass:"van-search__action-text",on:{"click":_vm.onBack}},[_vm._v(_vm._s(_vm.$t('cancel')))])])],2):_vm._e()])}
var search_staticRenderFns = []
var search_esExports = { render: search_render, staticRenderFns: search_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_search = (search_esExports);
// CONCATENATED MODULE: ./packages/search/index.vue
var search_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var search___vue_template_functional__ = false
/* styles */
var search___vue_styles__ = null
/* scopeId */
var search___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var search___vue_module_identifier__ = null
var search_Component = search_normalizeComponent(
  search,
  selectortype_template_index_0_packages_search,
  search___vue_template_functional__,
  search___vue_styles__,
  search___vue_scopeId__,
  search___vue_module_identifier__
)

/* harmony default export */ var packages_search = (search_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/sku/components/SkuHeader.vue
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var SkuHeader = (Object(utils["b" /* create */])({
  name: 'van-sku-header',

  props: {
    skuEventBus: Object,
    sku: Object,
    selectedSku: Object,
    selectedSkuComb: Object,
    goods: Object
  },

  computed: {
    goodsImg: function goodsImg() {
      var s1Id = this.selectedSku.s1;
      var skuImg = this.getSkuImg(s1Id);
      // 优先使用选中sku的图片
      return skuImg || this.goods.picture;
    },
    price: function price() {
      if (this.selectedSkuComb) {
        return (this.selectedSkuComb.price / 100).toFixed(2);
      }
      // sku.price是一个格式化好的价格区间
      return this.sku.price;
    }
  },

  methods: {
    getSkuImg: function getSkuImg(id) {
      if (!id) return;

      // 目前skuImg都挂载在skuTree中s1那类sku上
      var treeItem = this.sku.tree.filter(function (treeItem) {
        return treeItem.k_s === 's1';
      })[0] || {};

      if (!treeItem.v) {
        return;
      }

      var matchedSku = treeItem.v.filter(function (skuValue) {
        return skuValue.id === id;
      })[0];
      if (matchedSku && matchedSku.imgUrl) {
        return matchedSku.imgUrl;
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-41997020","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/sku/components/SkuHeader.vue
var SkuHeader_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-sku-header van-hairline--bottom"},[_c('div',{staticClass:"van-sku-header__img-wrap"},[_c('img',{staticClass:"van-sku__goods-img",attrs:{"src":_vm.goodsImg}})]),_c('div',{staticClass:"van-sku-header__goods-info"},[_c('div',{staticClass:"van-sku__goods-name"},[_vm._v(_vm._s(_vm.goods.title))]),_c('div',{staticClass:"van-sku__goods-price"},[_c('span',{staticClass:"van-sku__price-symbol"},[_vm._v("￥")]),_c('span',{staticClass:"van-sku__price-num"},[_vm._v(_vm._s(_vm.price))])]),_c('span',{staticClass:"van-sku__close-icon",on:{"click":function($event){_vm.skuEventBus.$emit('sku:close')}}})])])}
var SkuHeader_staticRenderFns = []
var SkuHeader_esExports = { render: SkuHeader_render, staticRenderFns: SkuHeader_staticRenderFns }
/* harmony default export */ var components_SkuHeader = (SkuHeader_esExports);
// CONCATENATED MODULE: ./packages/sku/components/SkuHeader.vue
var SkuHeader_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var SkuHeader___vue_template_functional__ = false
/* styles */
var SkuHeader___vue_styles__ = null
/* scopeId */
var SkuHeader___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var SkuHeader___vue_module_identifier__ = null
var SkuHeader_Component = SkuHeader_normalizeComponent(
  SkuHeader,
  components_SkuHeader,
  SkuHeader___vue_template_functional__,
  SkuHeader___vue_styles__,
  SkuHeader___vue_scopeId__,
  SkuHeader___vue_module_identifier__
)

/* harmony default export */ var sku_components_SkuHeader = (SkuHeader_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/sku/components/SkuRow.vue
//
//
//
//
//
//
//
//
//

/* harmony default export */ var SkuRow = ({
  name: 'van-sku-row',

  props: {
    skuRow: Object
  }
});
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-4de977dc","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/sku/components/SkuRow.vue
var SkuRow_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-sku-row"},[_c('div',{staticClass:"van-sku-row__title"},[_vm._v(_vm._s(_vm.skuRow.k)+"：")]),_c('div',{staticClass:"van-sku-row__items"},[_vm._t("default")],2)])}
var SkuRow_staticRenderFns = []
var SkuRow_esExports = { render: SkuRow_render, staticRenderFns: SkuRow_staticRenderFns }
/* harmony default export */ var components_SkuRow = (SkuRow_esExports);
// CONCATENATED MODULE: ./packages/sku/components/SkuRow.vue
var SkuRow_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var SkuRow___vue_template_functional__ = false
/* styles */
var SkuRow___vue_styles__ = null
/* scopeId */
var SkuRow___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var SkuRow___vue_module_identifier__ = null
var SkuRow_Component = SkuRow_normalizeComponent(
  SkuRow,
  components_SkuRow,
  SkuRow___vue_template_functional__,
  SkuRow___vue_styles__,
  SkuRow___vue_scopeId__,
  SkuRow___vue_module_identifier__
)

/* harmony default export */ var sku_components_SkuRow = (SkuRow_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/sku/components/SkuRowItem.vue



//
//
//
//
//
//
//
//
//
//
//
//
//

/* harmony default export */ var SkuRowItem = ({
  name: 'van-sku-row-item',

  props: {
    skuEventBus: Object,
    skuValue: Object,
    skuList: Array,
    selectedSku: Object,
    skuKeyStr: String
  },

  computed: {
    isChoosed: function isChoosed() {
      return this.skuValue.id === this.selectedSku[this.skuKeyStr];
    },
    isChoosable: function isChoosable() {
      var _Object$assign2;

      var matchedSku = assign_default()({}, this.selectedSku, (_Object$assign2 = {}, _Object$assign2[this.skuKeyStr] = this.skuValue.id, _Object$assign2));
      var skusToCheck = keys_default()(matchedSku).filter(function (skuKey) {
        return matchedSku[skuKey] !== '';
      });
      var filteredSku = this.skuList.filter(function (sku) {
        return skusToCheck.every(function (skuKey) {
          // 后端给的skuValue.id有时候是数字有时候是字符串，全等会匹配不上
          return matchedSku[skuKey] == sku[skuKey]; // eslint-disable-line
        });
      });
      var stock = filteredSku.reduce(function (total, sku) {
        return total += sku.stock_num;
      }, 0);

      return stock > 0;
    }
  },

  methods: {
    onSkuSelected: function onSkuSelected() {
      if (this.isChoosable) {
        this.skuEventBus.$emit('sku:select', extends_default()({}, this.skuValue, {
          skuKeyStr: this.skuKeyStr
        }));
      }
    }
  }
});
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-1f69ecf0","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/sku/components/SkuRowItem.vue
var SkuRowItem_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('span',{staticClass:"van-sku-row__item",class:{
    'van-sku-row__item--active': _vm.isChoosed,
    'van-sku-row__item--disabled': !_vm.isChoosable
  },on:{"click":_vm.onSkuSelected}},[_vm._v("\n  "+_vm._s(_vm.skuValue.name)+"\n")])}
var SkuRowItem_staticRenderFns = []
var SkuRowItem_esExports = { render: SkuRowItem_render, staticRenderFns: SkuRowItem_staticRenderFns }
/* harmony default export */ var components_SkuRowItem = (SkuRowItem_esExports);
// CONCATENATED MODULE: ./packages/sku/components/SkuRowItem.vue
var SkuRowItem_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var SkuRowItem___vue_template_functional__ = false
/* styles */
var SkuRowItem___vue_styles__ = null
/* scopeId */
var SkuRowItem___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var SkuRowItem___vue_module_identifier__ = null
var SkuRowItem_Component = SkuRowItem_normalizeComponent(
  SkuRowItem,
  components_SkuRowItem,
  SkuRowItem___vue_template_functional__,
  SkuRowItem___vue_styles__,
  SkuRowItem___vue_scopeId__,
  SkuRowItem___vue_module_identifier__
)

/* harmony default export */ var sku_components_SkuRowItem = (SkuRowItem_Component.exports);

// EXTERNAL MODULE: ./node_modules/babel-runtime/core-js/number/is-nan.js
var is_nan = __webpack_require__(109);
var is_nan_default = /*#__PURE__*/__webpack_require__.n(is_nan);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/stepper/index.vue

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var stepper = (Object(utils["b" /* create */])({
  name: 'van-stepper',

  props: {
    value: {},
    disabled: Boolean,
    disableInput: Boolean,
    min: {
      type: [String, Number],
      default: 1
    },
    max: {
      type: [String, Number],
      default: Infinity
    },
    step: {
      type: [String, Number],
      default: 1
    },
    defaultValue: {
      type: [String, Number],
      default: 1
    }
  },

  data: function data() {
    var value = this.value ? +this.value : +this.defaultValue;
    var correctedValue = this.correctValue(value);
    if (value !== correctedValue) {
      value = correctedValue;
      this.$emit('input', value);
    }

    return {
      currentValue: value
    };
  },


  computed: {
    isMinusDisabled: function isMinusDisabled() {
      var min = +this.min;
      var step = +this.step;
      var currentValue = +this.currentValue;
      return min === currentValue || currentValue - step < min || this.disabled;
    },
    isPlusDisabled: function isPlusDisabled() {
      var max = +this.max;
      var step = +this.step;
      var currentValue = +this.currentValue;
      return max === currentValue || currentValue + step > max || this.disabled;
    }
  },

  watch: {
    value: function value(val) {
      val = this.correctValue(+val);
      if (val !== this.currentValue) {
        this.currentValue = val;
      }
    }
  },

  methods: {
    correctValue: function correctValue(value) {
      if (is_nan_default()(value)) {
        value = this.min;
      } else {
        value = Math.max(this.min, value);
        value = Math.min(this.max, value);
      }

      return value;
    },
    onInput: function onInput(event) {
      var val = +event.target.value;
      this.currentValue = this.correctValue(val);
      this.emitInput();
    },
    onChange: function onChange(type) {
      if (this.isMinusDisabled && type === 'minus' || this.isPlusDisabled && type === 'plus') {
        this.$emit('overlimit', type);
        return;
      }

      var step = +this.step;
      var currentValue = +this.currentValue;
      this.currentValue = type === 'minus' ? currentValue - step : currentValue + step;
      this.emitInput();
      this.$emit(type);
    },
    emitInput: function emitInput() {
      this.$emit('input', this.currentValue);
      this.$emit('change', this.currentValue);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-22940b42","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/stepper/index.vue
var stepper_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-stepper",class:{ 'van-stepper--disabled': _vm.disabled }},[_c('button',{staticClass:"van-stepper__stepper van-stepper__minus",class:{ 'van-stepper__minus--disabled': _vm.isMinusDisabled },on:{"click":function($event){_vm.onChange('minus')}}}),_c('input',{staticClass:"van-stepper__input",attrs:{"type":"number","disabled":_vm.disabled || _vm.disableInput},domProps:{"value":_vm.currentValue},on:{"input":_vm.onInput}}),_c('button',{staticClass:"van-stepper__stepper van-stepper__plus",class:{ 'van-stepper__plus--disabled': _vm.isPlusDisabled },on:{"click":function($event){_vm.onChange('plus')}}})])}
var stepper_staticRenderFns = []
var stepper_esExports = { render: stepper_render, staticRenderFns: stepper_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_stepper = (stepper_esExports);
// CONCATENATED MODULE: ./packages/stepper/index.vue
var stepper_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var stepper___vue_template_functional__ = false
/* styles */
var stepper___vue_styles__ = null
/* scopeId */
var stepper___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var stepper___vue_module_identifier__ = null
var stepper_Component = stepper_normalizeComponent(
  stepper,
  selectortype_template_index_0_packages_stepper,
  stepper___vue_template_functional__,
  stepper___vue_styles__,
  stepper___vue_scopeId__,
  stepper___vue_module_identifier__
)

/* harmony default export */ var packages_stepper = (stepper_Component.exports);

// CONCATENATED MODULE: ./packages/sku/constants.js
var LIMIT_TYPE = {
  QUOTA_LIMIT: 0,
  STOCK_LIMIT: 1
};
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/sku/components/SkuStepper.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//





var QUOTA_LIMIT = LIMIT_TYPE.QUOTA_LIMIT,
    STOCK_LIMIT = LIMIT_TYPE.STOCK_LIMIT;


/* harmony default export */ var SkuStepper = (Object(utils["b" /* create */])({
  name: 'van-sku-stepper',

  components: {
    Stepper: packages_stepper
  },

  props: {
    skuEventBus: Object,
    skuStockNum: Number,
    selectedSku: Object,
    selectedSkuComb: Object,
    selectedNum: Number,
    stepperTitle: String,
    quota: Number,
    quotaUsed: Number,
    hideStock: Boolean,
    disableStepperInput: Boolean
  },

  data: function data() {
    return {
      currentNum: this.selectedNum,
      // 购买限制类型: 限购/库存
      limitType: STOCK_LIMIT
    };
  },


  watch: {
    currentNum: function currentNum(num) {
      this.skuEventBus.$emit('sku:numChange', num);
    },
    stepperLimit: function stepperLimit(limit) {
      if (limit < this.currentNum) {
        this.currentNum = limit;
      }
    }
  },

  computed: {
    stock: function stock() {
      if (this.selectedSkuComb) {
        return this.selectedSkuComb.stock_num;
      }
      return this.skuStockNum;
    },
    stepperLimit: function stepperLimit() {
      var quotaLimit = this.quota - this.quotaUsed;
      var limit = void 0;

      // 无限购时直接取库存，有限购时取限购数和库存数中小的那个
      if (this.quota > 0 && quotaLimit <= this.stock) {
        // 修正负的limit
        limit = quotaLimit < 0 ? 0 : quotaLimit;
        this.limitType = QUOTA_LIMIT;
      } else {
        limit = this.stock;
        this.limitType = STOCK_LIMIT;
      }

      return limit;
    }
  },

  methods: {
    setCurrentNum: function setCurrentNum(num) {
      this.currentNum = num;
    },
    onOverLimit: function onOverLimit(action) {
      this.skuEventBus.$emit('sku:overLimit', {
        action: action,
        limitType: this.limitType,
        quota: this.quota,
        quotaUsed: this.quotaUsed
      });
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-a33d503c","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/sku/components/SkuStepper.vue
var SkuStepper_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-sku-stepper-stock"},[_c('div',{staticClass:"van-sku-stepper-container"},[_c('div',{staticClass:"van-sku__stepper-title"},[_vm._v(_vm._s(_vm.stepperTitle || _vm.$t('title'))+"：")]),_c('stepper',{staticClass:"van-sku__stepper",attrs:{"min":1,"max":_vm.stepperLimit,"disable-input":_vm.disableStepperInput},on:{"overlimit":_vm.onOverLimit},model:{value:(_vm.currentNum),callback:function ($$v) {_vm.currentNum=$$v},expression:"currentNum"}})],1),(!_vm.hideStock)?_c('div',{staticClass:"van-sku__stock"},[_vm._v(_vm._s(_vm.$t('remain', _vm.stock)))]):_vm._e(),(_vm.quota > 0)?_c('div',{staticClass:"van-sku__quota"},[_vm._v(_vm._s(_vm.$t('quota', _vm.quota)))]):_vm._e()])}
var SkuStepper_staticRenderFns = []
var SkuStepper_esExports = { render: SkuStepper_render, staticRenderFns: SkuStepper_staticRenderFns }
/* harmony default export */ var components_SkuStepper = (SkuStepper_esExports);
// CONCATENATED MODULE: ./packages/sku/components/SkuStepper.vue
var SkuStepper_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var SkuStepper___vue_template_functional__ = false
/* styles */
var SkuStepper___vue_styles__ = null
/* scopeId */
var SkuStepper___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var SkuStepper___vue_module_identifier__ = null
var SkuStepper_Component = SkuStepper_normalizeComponent(
  SkuStepper,
  components_SkuStepper,
  SkuStepper___vue_template_functional__,
  SkuStepper___vue_styles__,
  SkuStepper___vue_scopeId__,
  SkuStepper___vue_module_identifier__
)

/* harmony default export */ var sku_components_SkuStepper = (SkuStepper_Component.exports);

// CONCATENATED MODULE: ./packages/utils/validate/email.js
/* eslint-disable */
function email(value) {
  var reg = /^((([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+(\.([a-z]|\d|[!#\$%&'\*\+\-\/=\?\^_`{\|}~]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])+)*)|((\x22)((((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(([\x01-\x08\x0b\x0c\x0e-\x1f\x7f]|\x21|[\x23-\x5b]|[\x5d-\x7e]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(\\([\x01-\x09\x0b\x0c\x0d-\x7f]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF]))))*(((\x20|\x09)*(\x0d\x0a))?(\x20|\x09)+)?(\x22)))@((([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|\d|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))\.)+(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])|(([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])([a-z]|\d|-|\.|_|~|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])*([a-z]|[\u00A0-\uD7FF\uF900-\uFDCF\uFDF0-\uFFEF])))$/i;
  return reg.test(value);
}
// CONCATENATED MODULE: ./packages/utils/validate/number.js
function number(value) {
  return (/^\d+$/.test(value)
  );
}
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/sku/components/SkuMessages.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//







/* harmony default export */ var SkuMessages = (Object(utils["b" /* create */])({
  name: 'van-sku-messages',

  components: {
    Field: packages_field,
    CellGroup: packages_cell_group
  },

  props: {
    messages: Array,
    messagePlaceholderMap: Object,
    goodsId: [Number, String]
  },

  computed: {
    internalMessages: function internalMessages() {
      return Array.isArray(this.messages) ? this.messages.filter(function (message) {
        return message.type !== 'image';
      }) : [];
    },
    messageValues: function messageValues() {
      return this.internalMessages.map(function () {
        return '';
      });
    }
  },

  methods: {
    getType: function getType(message) {
      if (+message.multiple === 1) {
        return 'textarea';
      }
      if (message.type === 'id_no') {
        return 'text';
      }
      return message.datetime > 0 ? 'datetime-local' : message.type;
    },
    getMessages: function getMessages() {
      var _this = this;

      var messages = {};

      this.messageValues.forEach(function (value, index) {
        if (_this.internalMessages[index].datetime > 0) {
          value = value.replace(/T/g, ' ');
        }
        messages['message_' + index] = value;
      });

      return messages;
    },
    getCartMessages: function getCartMessages() {
      var _this2 = this;

      var messages = {};

      this.messageValues.forEach(function (value, index) {
        var message = _this2.internalMessages[index];
        if (message.datetime > 0) {
          value = value.replace(/T/g, ' ');
        }
        messages[message.name] = value;
      });

      return messages;
    },
    getPlaceholder: function getPlaceholder(message) {
      var type = +message.multiple === 1 ? 'textarea' : message.type;
      return this.messagePlaceholderMap[type] || this.$t('placeholder.' + type);
    },
    validateMessages: function validateMessages() {
      var values = this.messageValues;

      for (var i = 0; i < values.length; i++) {
        var value = values[i];
        var message = this.internalMessages[i];

        if (value === '') {
          // 必填字段的校验
          if (message.required == '1') {
            // eslint-disable-line
            if (message.type === 'image') {
              continue;
            } else {
              return this.$t('fill') + message.name;
            }
          }
        } else {
          if (message.type === 'tel' && !number(value)) {
            return this.$t('number');
          }
          if (message.type === 'email' && !email(value)) {
            return this.$t('email');
          }
          if (message.type === 'id_no' && (value.length < 15 || value.length > 18)) {
            return this.$t('id_no');
          }
        }

        if (value.length > 200) {
          return message.name + ' ' + this.$t('overlimit');
        }
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-54ea398b","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/sku/components/SkuMessages.vue
var SkuMessages_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('cell-group',{staticClass:"van-sku-messages"},_vm._l((_vm.internalMessages),function(message,index){return _c('field',{key:(_vm.goodsId + "-" + index),attrs:{"required":message.required == '1',"label":message.name,"placeholder":_vm.getPlaceholder(message),"type":_vm.getType(message)},model:{value:(_vm.messageValues[index]),callback:function ($$v) {_vm.$set(_vm.messageValues, index, $$v)},expression:"messageValues[index]"}})}))}
var SkuMessages_staticRenderFns = []
var SkuMessages_esExports = { render: SkuMessages_render, staticRenderFns: SkuMessages_staticRenderFns }
/* harmony default export */ var components_SkuMessages = (SkuMessages_esExports);
// CONCATENATED MODULE: ./packages/sku/components/SkuMessages.vue
var SkuMessages_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var SkuMessages___vue_template_functional__ = false
/* styles */
var SkuMessages___vue_styles__ = null
/* scopeId */
var SkuMessages___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var SkuMessages___vue_module_identifier__ = null
var SkuMessages_Component = SkuMessages_normalizeComponent(
  SkuMessages,
  components_SkuMessages,
  SkuMessages___vue_template_functional__,
  SkuMessages___vue_styles__,
  SkuMessages___vue_scopeId__,
  SkuMessages___vue_module_identifier__
)

/* harmony default export */ var sku_components_SkuMessages = (SkuMessages_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/sku/components/SkuActions.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var SkuActions = (Object(utils["b" /* create */])({
  name: 'van-sku-actions',

  components: {
    VanButton: packages_button
  },

  props: {
    buyText: String,
    skuEventBus: Object,
    showAddCartBtn: Boolean
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-060f3084","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/sku/components/SkuActions.vue
var SkuActions_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-sku-actions"},[(_vm.showAddCartBtn)?_c('van-button',{attrs:{"bottom-action":"","text":_vm.$t('cart')},on:{"click":function($event){_vm.skuEventBus.$emit('sku:addCart')}}}):_vm._e(),_c('van-button',{attrs:{"type":"primary","bottom-action":"","text":_vm.buyText || _vm.$t('buy')},on:{"click":function($event){_vm.skuEventBus.$emit('sku:buy')}}})],1)}
var SkuActions_staticRenderFns = []
var SkuActions_esExports = { render: SkuActions_render, staticRenderFns: SkuActions_staticRenderFns }
/* harmony default export */ var components_SkuActions = (SkuActions_esExports);
// CONCATENATED MODULE: ./packages/sku/components/SkuActions.vue
var SkuActions_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var SkuActions___vue_template_functional__ = false
/* styles */
var SkuActions___vue_styles__ = null
/* scopeId */
var SkuActions___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var SkuActions___vue_module_identifier__ = null
var SkuActions_Component = SkuActions_normalizeComponent(
  SkuActions,
  components_SkuActions,
  SkuActions___vue_template_functional__,
  SkuActions___vue_styles__,
  SkuActions___vue_scopeId__,
  SkuActions___vue_module_identifier__
)

/* harmony default export */ var sku_components_SkuActions = (SkuActions_Component.exports);

// CONCATENATED MODULE: ./packages/sku/utils/skuHelper.js

/*
  normalize sku tree

  [
    {
      count: 2,
      k: "品种", // 规格名称 skuKeyName
      k_id: "1200", // skuKeyId
      k_s: "s1" // skuKeyStr
      v: [ // skuValues
        { // skuValue
          id: "1201", // skuValueId
          name: "萌" // 具体的规格值 skuValueName
        }, {
          id: "973",
          name: "帅"
        }
      ]
    },
    ...
  ]
                |
                v
  {
    s1: [{
      id: "1201",
      name: "萌"
    }, {
      id: "973",
      name: "帅"
    }],
    ...
  }
 */
var normalizeSkuTree = function normalizeSkuTree(skuTree) {
  var normalizedTree = {};
  skuTree.forEach(function (treeItem) {
    normalizedTree[treeItem.k_s] = treeItem.v;
  });
  return normalizedTree;
};

// 判断是否所有的sku都已经选中
var skuHelper_isAllSelected = function isAllSelected(skuTree, selectedSku) {
  // 筛选selectedSku对象中key值不为空的值
  var selected = keys_default()(selectedSku).filter(function (skuKeyStr) {
    return selectedSku[skuKeyStr] !== '';
  });
  return skuTree.length === selected.length;
};

// 根据已选择的sku获取skuComb
var skuHelper_getSkuComb = function getSkuComb(skuList, selectedSku) {
  var skuComb = skuList.filter(function (skuComb) {
    return keys_default()(selectedSku).every(function (skuKeyStr) {
      return String(skuComb[skuKeyStr]) === String(selectedSku[skuKeyStr]); // eslint-disable-line
    });
  })[0];
  return skuComb;
};

// 获取已选择的sku名称
var skuHelper_getSelectedSkuValues = function getSelectedSkuValues(skuTree, selectedSku) {
  var normalizedTree = normalizeSkuTree(skuTree);
  return keys_default()(selectedSku).reduce(function (selectedValues, skuKeyStr) {
    var skuValues = normalizedTree[skuKeyStr];
    var skuValueId = selectedSku[skuKeyStr];

    if (skuValueId) {
      var skuValue = skuValues.filter(function (skuValue) {
        return skuValue.id === skuValueId;
      })[0];
      skuValue && selectedValues.push(skuValue);
    }
    return selectedValues;
  }, []);
};

var SkuHelper = {
  normalizeSkuTree: normalizeSkuTree,
  isAllSelected: skuHelper_isAllSelected,
  getSkuComb: skuHelper_getSkuComb,
  getSelectedSkuValues: skuHelper_getSelectedSkuValues
};
/* harmony default export */ var skuHelper = (SkuHelper);
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/sku/Sku.vue


//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//

/* eslint-disable camelcase */













var Sku_QUOTA_LIMIT = LIMIT_TYPE.QUOTA_LIMIT;


/* harmony default export */ var Sku = (Object(utils["b" /* create */])({
  name: 'van-sku',

  components: {
    Popup: packages_popup,
    SkuHeader: sku_components_SkuHeader,
    SkuRow: sku_components_SkuRow,
    SkuRowItem: sku_components_SkuRowItem,
    SkuStepper: sku_components_SkuStepper,
    SkuMessages: sku_components_SkuMessages,
    SkuActions: sku_components_SkuActions
  },

  props: {
    sku: Object,
    goods: Object,
    value: Boolean,
    buyText: String,
    goodsId: [Number, String],
    stepperTitle: String,
    hideStock: Boolean,
    resetStepperOnHide: Boolean,
    resetSelectedSkuOnHide: Boolean,
    disableStepperInput: Boolean,
    initialSku: {
      type: Object,
      default: function _default() {
        return {};
      }
    },
    quota: {
      type: Number,
      default: 0
    },
    quotaUsed: {
      type: Number,
      default: 0
    },
    showAddCartBtn: {
      type: Boolean,
      default: true
    },
    bodyOffsetTop: {
      type: Number,
      default: 200
    },
    messagePlaceholderMap: {
      type: Object,
      default: function _default() {
        return {};
      }
    }
  },

  data: function data() {
    return {
      selectedSku: {},
      selectedNum: 1,
      show: this.value
    };
  },


  watch: {
    show: function show(val) {
      this.$emit('input', val);
      if (!val) {
        var selectedSkuValues = skuHelper_getSelectedSkuValues(this.sku.tree, this.selectedSku);

        this.$emit('sku-close', {
          selectedSkuValues: selectedSkuValues,
          selectedNum: this.selectedNum,
          selectedSkuComb: this.selectedSkuComb
        });

        if (this.resetStepperOnHide) {
          this.$refs.skuStepper && this.$refs.skuStepper.setCurrentNum(1);
        }

        if (this.resetSelectedSkuOnHide) {
          this.resetSelectedSku(this.skuTree);
        }
      }
    },
    value: function value(val) {
      this.show = val;
    },
    skuTree: function skuTree(val) {
      this.resetSelectedSku(val);
    }
  },

  computed: {
    bodyStyle: function bodyStyle() {
      if (this.$isServer) {
        return;
      }

      var windowHeight = window.innerHeight;
      // header高度82px, sku actions高度50px，如果改动了样式自己传下bodyOffsetTop调整下
      var maxHeight = windowHeight - this.bodyOffsetTop;

      return {
        maxHeight: maxHeight + 'px'
      };
    },
    isSkuCombSelected: function isSkuCombSelected() {
      return skuHelper_isAllSelected(this.sku.tree, this.selectedSku);
    },
    isSkuEmpty: function isSkuEmpty() {
      return keys_default()(this.sku).length === 0;
    },
    hasSku: function hasSku() {
      return !this.sku.none_sku;
    },
    selectedSkuComb: function selectedSkuComb() {
      if (!this.hasSku) {
        return {
          id: this.sku.collection_id,
          price: Math.round(this.sku.price * 100),
          stock_num: this.sku.stock_num
        };
      } else if (this.isSkuCombSelected) {
        return skuHelper_getSkuComb(this.sku.list, this.selectedSku);
      }
      return null;
    },
    skuTree: function skuTree() {
      return this.sku.tree || [];
    }
  },

  created: function created() {
    var skuEventBus = new external___root___Vue___commonjs___vue___commonjs2___vue___amd___vue___default.a();
    this.skuEventBus = skuEventBus;

    skuEventBus.$on('sku:close', this.onCloseClicked);
    skuEventBus.$on('sku:select', this.onSkuSelected);
    skuEventBus.$on('sku:numChange', this.onNumChange);
    skuEventBus.$on('sku:overLimit', this.onOverLimit);
    skuEventBus.$on('sku:addCart', this.onAddCartClicked);
    skuEventBus.$on('sku:buy', this.onBuyClicked);

    this.resetSelectedSku(this.skuTree);
    // 组件初始化后的钩子，抛出skuEventBus
    this.$emit('after-sku-create', skuEventBus);
  },


  methods: {
    resetSelectedSku: function resetSelectedSku(skuTree) {
      var _this = this;

      this.selectedSku = {};
      skuTree.forEach(function (item) {
        // 只有一个sku规格值时默认选中
        if (item.v.length === 1) {
          _this.selectedSku[item.k_s] = item.v[0].id;
        } else {
          _this.selectedSku[item.k_s] = _this.initialSku[item.k_s] || '';
        }
      });
    },
    getSkuMessages: function getSkuMessages() {
      return this.$refs.skuMessages ? this.$refs.skuMessages.getMessages() : {};
    },
    getSkuCartMessages: function getSkuCartMessages() {
      return this.$refs.skuMessages ? this.$refs.skuMessages.getCartMessages() : {};
    },
    validateSkuMessages: function validateSkuMessages() {
      return this.$refs.skuMessages ? this.$refs.skuMessages.validateMessages() : '';
    },
    validateSku: function validateSku() {
      if (this.selectedNum === 0) {
        return this.$t('unavailable');
      }

      if (this.isSkuCombSelected) {
        var error = this.validateSkuMessages();
        // sku留言没有错误则校验通过
        return error;
      } else {
        return this.$t('spec');
      }
    },
    onCloseClicked: function onCloseClicked() {
      this.show = false;
    },
    onSkuSelected: function onSkuSelected(skuValue) {
      var _extends2, _extends3;

      // 点击已选中的sku时则取消选中
      this.selectedSku = this.selectedSku[skuValue.skuKeyStr] === skuValue.id ? extends_default()({}, this.selectedSku, (_extends2 = {}, _extends2[skuValue.skuKeyStr] = '', _extends2)) : extends_default()({}, this.selectedSku, (_extends3 = {}, _extends3[skuValue.skuKeyStr] = skuValue.id, _extends3));

      this.$emit('sku-selected', {
        skuValue: skuValue,
        selectedSku: this.selectedSku,
        selectedSkuComb: this.selectedSkuComb
      });
    },
    onNumChange: function onNumChange(num) {
      this.selectedNum = num;
    },
    onOverLimit: function onOverLimit(_ref) {
      var action = _ref.action,
          limitType = _ref.limitType,
          quota = _ref.quota,
          quotaUsed = _ref.quotaUsed;

      if (action === 'minus') {
        packages_toast(this.$t('least'));
      } else if (action === 'plus') {
        if (limitType === Sku_QUOTA_LIMIT) {
          var msg = this.$t('quota', quota);
          if (quotaUsed > 0) msg += '\uFF0C' + this.$t('purchase', quotaUsed);
          packages_toast(msg);
        } else {
          packages_toast(this.$t('inventory'));
        }
      }
    },
    onAddCartClicked: function onAddCartClicked() {
      this.onBuyOrAddCart('add-cart');
    },
    onBuyClicked: function onBuyClicked() {
      this.onBuyOrAddCart('buy-clicked');
    },
    onBuyOrAddCart: function onBuyOrAddCart(type) {
      var error = this.validateSku();
      if (error) {
        packages_toast(error);
      } else {
        this.$emit(type, this.getSkuData());
      }
    },
    getSkuData: function getSkuData() {
      return {
        goodsId: this.goodsId,
        selectedNum: this.selectedNum,
        selectedSkuComb: this.selectedSkuComb,
        messages: this.getSkuMessages(),
        cartMessages: this.getSkuCartMessages()
      };
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-79068744","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/sku/Sku.vue
var Sku_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return (!_vm.isSkuEmpty)?_c('popup',{attrs:{"position":"bottom","lock-on-scroll":"","prevent-scroll":""},model:{value:(_vm.show),callback:function ($$v) {_vm.show=$$v},expression:"show"}},[_c('div',{staticClass:"van-sku-container"},[_c('div',{staticClass:"van-sku-layout"},[_vm._t("sku-header",[_c('sku-header',{attrs:{"sku-event-bus":_vm.skuEventBus,"selected-sku":_vm.selectedSku,"selected-sku-comb":_vm.selectedSkuComb,"goods":_vm.goods,"sku":_vm.sku}})],{skuEventBus:_vm.skuEventBus,selectedSku:_vm.selectedSku,selectedSkuComb:_vm.selectedSkuComb}),_c('div',{staticClass:"van-sku-body",style:(_vm.bodyStyle)},[_vm._t("sku-body-top",null,{selectedSku:_vm.selectedSku,skuEventBus:_vm.skuEventBus}),_vm._t("sku-group",[(_vm.hasSku)?_c('div',{staticClass:"van-sku-group-container van-hairline--bottom"},_vm._l((_vm.skuTree),function(skuTreeItem,index){return _c('div',{key:index,staticClass:"van-sku-row-group"},[_c('sku-row',{attrs:{"sku-event-bus":_vm.skuEventBus,"sku-row":skuTreeItem}},_vm._l((skuTreeItem.v),function(skuValue,index){return _c('sku-row-item',{key:index,attrs:{"sku-key-str":skuTreeItem.k_s,"sku-value":skuValue,"sku-event-bus":_vm.skuEventBus,"selected-sku":_vm.selectedSku,"sku-list":_vm.sku.list}})}))],1)})):_vm._e()],{selectedSku:_vm.selectedSku,skuEventBus:_vm.skuEventBus}),_vm._t("extra-sku-group",null,{skuEventBus:_vm.skuEventBus}),_vm._t("sku-stepper",[_c('sku-stepper',{ref:"skuStepper",attrs:{"sku-event-bus":_vm.skuEventBus,"selected-sku":_vm.selectedSku,"selected-sku-comb":_vm.selectedSkuComb,"selected-num":_vm.selectedNum,"stepper-title":_vm.stepperTitle,"sku-stock-num":_vm.sku.stock_num,"quota":_vm.quota,"quota-used":_vm.quotaUsed,"disable-stepper-input":_vm.disableStepperInput,"hide-stock":_vm.hideStock}})],{skuEventBus:_vm.skuEventBus,selectedSku:_vm.selectedSku,selectedSkuComb:_vm.selectedSkuComb,selectedNum:_vm.selectedNum}),_vm._t("sku-messages",[_c('sku-messages',{ref:"skuMessages",attrs:{"goods-id":_vm.goodsId,"message-placeholder-map":_vm.messagePlaceholderMap,"messages":_vm.sku.messages}})])],2),_vm._t("sku-actions",[_c('sku-actions',{attrs:{"sku-event-bus":_vm.skuEventBus,"buy-text":_vm.buyText,"show-add-cart-btn":_vm.showAddCartBtn}})],{skuEventBus:_vm.skuEventBus})],2)])]):_vm._e()}
var Sku_staticRenderFns = []
var Sku_esExports = { render: Sku_render, staticRenderFns: Sku_staticRenderFns }
/* harmony default export */ var sku_Sku = (Sku_esExports);
// CONCATENATED MODULE: ./packages/sku/Sku.vue
var Sku_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var Sku___vue_template_functional__ = false
/* styles */
var Sku___vue_styles__ = null
/* scopeId */
var Sku___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var Sku___vue_module_identifier__ = null
var Sku_Component = Sku_normalizeComponent(
  Sku,
  sku_Sku,
  Sku___vue_template_functional__,
  Sku___vue_styles__,
  Sku___vue_scopeId__,
  Sku___vue_module_identifier__
)

/* harmony default export */ var packages_sku_Sku = (Sku_Component.exports);

// CONCATENATED MODULE: ./packages/sku/index.js









packages_sku_Sku.SkuActions = sku_components_SkuActions;
packages_sku_Sku.SkuHeader = sku_components_SkuHeader;
packages_sku_Sku.SkuMessages = sku_components_SkuMessages;
packages_sku_Sku.SkuStepper = sku_components_SkuStepper;
packages_sku_Sku.SkuRow = sku_components_SkuRow;
packages_sku_Sku.SkuRowItem = sku_components_SkuRowItem;
packages_sku_Sku.skuHelper = skuHelper;

/* harmony default export */ var sku = (packages_sku_Sku);
// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/step/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var step = (Object(utils["b" /* create */])({
  name: 'van-step',

  beforeCreate: function beforeCreate() {
    this.$parent.steps.push(this);
  },


  computed: {
    status: function status() {
      var index = this.$parent.steps.indexOf(this);
      var active = this.$parent.active;

      if (index < active) {
        return 'finish';
      } else if (index === active) {
        return 'process';
      }
    },
    titleStyle: function titleStyle() {
      return this.status === 'process' ? {
        color: this.$parent.activeColor
      } : {};
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-8c7bcd02","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/step/index.vue
var step_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-step van-hairline",class:[("van-step--" + (_vm.$parent.direction)), ( _obj = {}, _obj[("van-step--" + _vm.status)] = _vm.status, _obj )]},[_c('div',{staticClass:"van-step__circle-container"},[(_vm.status !== 'process')?_c('i',{staticClass:"van-step__circle"}):_c('icon',{style:({ color: _vm.$parent.activeColor }),attrs:{"name":"checked"}})],1),_c('div',{staticClass:"van-step__title",style:(_vm.titleStyle)},[_vm._t("default")],2),_c('div',{staticClass:"van-step__line"})])
var _obj;}
var step_staticRenderFns = []
var step_esExports = { render: step_render, staticRenderFns: step_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_step = (step_esExports);
// CONCATENATED MODULE: ./packages/step/index.vue
var step_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var step___vue_template_functional__ = false
/* styles */
var step___vue_styles__ = null
/* scopeId */
var step___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var step___vue_module_identifier__ = null
var step_Component = step_normalizeComponent(
  step,
  selectortype_template_index_0_packages_step,
  step___vue_template_functional__,
  step___vue_styles__,
  step___vue_scopeId__,
  step___vue_module_identifier__
)

/* harmony default export */ var packages_step = (step_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/steps/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var steps = (Object(utils["b" /* create */])({
  name: 'van-steps',

  props: {
    icon: String,
    title: String,
    active: Number,
    iconClass: String,
    description: String,
    direction: {
      type: String,
      default: 'horizontal'
    },
    activeColor: {
      type: String,
      default: '#06bf04'
    }
  },

  data: function data() {
    return {
      steps: []
    };
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-67b49e48","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/steps/index.vue
var steps_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-steps",class:("van-steps--" + _vm.direction)},[(_vm.title || _vm.description)?_c('div',{staticClass:"van-steps__status"},[(_vm.icon || _vm.$slots.icon)?_c('div',{staticClass:"van-steps__icon"},[_vm._t("icon",[_c('icon',{class:_vm.iconClass,attrs:{"name":_vm.icon}})])],2):_vm._e(),_c('div',{staticClass:"van-steps__message"},[_c('div',{staticClass:"van-steps__title",domProps:{"textContent":_vm._s(_vm.title)}}),_c('div',{staticClass:"van-steps__desc",domProps:{"textContent":_vm._s(_vm.description)}})]),_vm._t("message-extra")],2):_vm._e(),_c('div',{staticClass:"van-steps__items",class:{ 'van-steps__items--alone': !_vm.title && !_vm.description }},[_vm._t("default")],2)])}
var steps_staticRenderFns = []
var steps_esExports = { render: steps_render, staticRenderFns: steps_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_steps = (steps_esExports);
// CONCATENATED MODULE: ./packages/steps/index.vue
var steps_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var steps___vue_template_functional__ = false
/* styles */
var steps___vue_styles__ = null
/* scopeId */
var steps___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var steps___vue_module_identifier__ = null
var steps_Component = steps_normalizeComponent(
  steps,
  selectortype_template_index_0_packages_steps,
  steps___vue_template_functional__,
  steps___vue_styles__,
  steps___vue_scopeId__,
  steps___vue_module_identifier__
)

/* harmony default export */ var packages_steps = (steps_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/submit-bar/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var submit_bar = (Object(utils["b" /* create */])({
  name: 'van-submit-bar',

  components: {
    VanButton: packages_button
  },

  props: {
    tip: String,
    type: Number,
    price: Number,
    label: String,
    loading: Boolean,
    disabled: Boolean,
    buttonText: String,
    buttonType: {
      type: String,
      default: 'danger'
    }
  },

  computed: {
    hasPrice: function hasPrice() {
      return typeof this.price === 'number';
    },
    priceInterger: function priceInterger() {
      return Math.floor(this.price / 100);
    },
    priceDecimal: function priceDecimal() {
      var decimal = this.price % 100;
      return (decimal < 10 ? '0' : '') + decimal;
    }
  },

  methods: {
    onSubmit: function onSubmit() {
      if (!this.disabled && !this.loading) {
        this.$emit('submit');
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-76f9aad5","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/submit-bar/index.vue
var submit_bar_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-submit-bar"},[_c('div',{directives:[{name:"show",rawName:"v-show",value:(_vm.tip || _vm.$slots.tip),expression:"tip || $slots.tip"}],staticClass:"van-submit-bar__tip"},[_vm._v("\n    "+_vm._s(_vm.tip)),_vm._t("tip")],2),_c('div',{staticClass:"van-submit-bar__bar"},[_vm._t("default"),_c('div',{staticClass:"van-submit-bar__price"},[(_vm.hasPrice)?[_c('span',{staticClass:"van-submit-bar__price-text"},[_vm._v(_vm._s(_vm.label || _vm.$t('label')))]),_c('span',{staticClass:"van-submit-bar__price-interger"},[_vm._v("¥"+_vm._s(_vm.priceInterger)+".")]),_c('span',{staticClass:"van-submit-bar__price-decimal"},[_vm._v(_vm._s(_vm.priceDecimal))])]:_vm._e()],2),_c('van-button',{attrs:{"type":_vm.buttonType,"disabled":_vm.disabled,"loading":_vm.loading},on:{"click":_vm.onSubmit}},[_vm._v("\n      "+_vm._s(_vm.loading ? '' : _vm.buttonText)+"\n    ")])],2)])}
var submit_bar_staticRenderFns = []
var submit_bar_esExports = { render: submit_bar_render, staticRenderFns: submit_bar_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_submit_bar = (submit_bar_esExports);
// CONCATENATED MODULE: ./packages/submit-bar/index.vue
var submit_bar_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var submit_bar___vue_template_functional__ = false
/* styles */
var submit_bar___vue_styles__ = null
/* scopeId */
var submit_bar___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var submit_bar___vue_module_identifier__ = null
var submit_bar_Component = submit_bar_normalizeComponent(
  submit_bar,
  selectortype_template_index_0_packages_submit_bar,
  submit_bar___vue_template_functional__,
  submit_bar___vue_styles__,
  submit_bar___vue_scopeId__,
  submit_bar___vue_module_identifier__
)

/* harmony default export */ var packages_submit_bar = (submit_bar_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/tab/index.vue
//
//
//
//
//
//




/* harmony default export */ var tab = (Object(utils["b" /* create */])({
  name: 'van-tab',

  mixins: [find_parent],

  props: {
    title: {
      type: String,
      required: true
    },
    disabled: Boolean
  },

  computed: {
    index: function index() {
      return this.parentGroup.tabs.indexOf(this);
    }
  },

  created: function created() {
    this.findParentByName('van-tabs');
    this.parentGroup.tabs.push(this);
  },
  destroyed: function destroyed() {
    this.parentGroup.tabs.splice(this.index, 1);
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-673bbfdc","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/tab/index.vue
var tab_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-tab__pane",class:{ 'van-tab__pane--select': _vm.index === _vm.parentGroup.curActive }},[_vm._t("default")],2)}
var tab_staticRenderFns = []
var tab_esExports = { render: tab_render, staticRenderFns: tab_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_tab = (tab_esExports);
// CONCATENATED MODULE: ./packages/tab/index.vue
var tab_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var tab___vue_template_functional__ = false
/* styles */
var tab___vue_styles__ = null
/* scopeId */
var tab___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var tab___vue_module_identifier__ = null
var tab_Component = tab_normalizeComponent(
  tab,
  selectortype_template_index_0_packages_tab,
  tab___vue_template_functional__,
  tab___vue_styles__,
  tab___vue_scopeId__,
  tab___vue_module_identifier__
)

/* harmony default export */ var packages_tab = (tab_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/tabbar/index.vue
//
//
//
//
//
//



/* harmony default export */ var tabbar = (Object(utils["b" /* create */])({
  name: 'van-tabbar',

  data: function data() {
    return {
      items: []
    };
  },


  props: {
    value: Number,
    fixed: {
      type: Boolean,
      default: true
    }
  },

  watch: {
    items: function items() {
      this.setActiveItem();
    },
    value: function value() {
      this.setActiveItem();
    }
  },

  methods: {
    setActiveItem: function setActiveItem() {
      var _this = this;

      this.items.forEach(function (item, index) {
        item.active = index === _this.value;
      });
    },
    onChange: function onChange(active) {
      this.$emit('input', active);
      this.$emit('change', active);
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-e8f6304e","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/tabbar/index.vue
var tabbar_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-tabbar van-hairline--top-bottom",class:{ 'van-tabbar--fixed': _vm.fixed }},[_vm._t("default")],2)}
var tabbar_staticRenderFns = []
var tabbar_esExports = { render: tabbar_render, staticRenderFns: tabbar_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_tabbar = (tabbar_esExports);
// CONCATENATED MODULE: ./packages/tabbar/index.vue
var tabbar_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var tabbar___vue_template_functional__ = false
/* styles */
var tabbar___vue_styles__ = null
/* scopeId */
var tabbar___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var tabbar___vue_module_identifier__ = null
var tabbar_Component = tabbar_normalizeComponent(
  tabbar,
  selectortype_template_index_0_packages_tabbar,
  tabbar___vue_template_functional__,
  tabbar___vue_styles__,
  tabbar___vue_scopeId__,
  tabbar___vue_module_identifier__
)

/* harmony default export */ var packages_tabbar = (tabbar_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/tabbar-item/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//




/* harmony default export */ var tabbar_item = (Object(utils["b" /* create */])({
  name: 'van-tabbar-item',

  mixins: [router_link],

  props: {
    icon: String,
    dot: Boolean,
    info: String
  },

  data: function data() {
    return {
      active: false
    };
  },
  beforeCreate: function beforeCreate() {
    this.$parent.items.push(this);
  },
  destroyed: function destroyed() {
    this.$parent.items.splice(this.$parent.items.indexOf(this), 1);
  },


  methods: {
    onClick: function onClick(event) {
      this.$parent.onChange(this.$parent.items.indexOf(this));
      this.$emit('click', event);
      this.routerLink();
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-f4c1eff4","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/tabbar-item/index.vue
var tabbar_item_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-tabbar-item",class:{ 'van-tabbar-item--active': _vm.active },on:{"click":_vm.onClick}},[_c('div',{staticClass:"van-tabbar-item__icon",class:{ 'van-tabbar-item__icon-dot': _vm.dot }},[_vm._t("icon",[(_vm.icon)?_c('icon',{attrs:{"name":_vm.icon}}):_vm._e()],{active:_vm.active}),(_vm.info)?_c('div',{staticClass:"van-icon__info"},[_vm._v(_vm._s(_vm.info))]):_vm._e()],2),_c('div',{staticClass:"van-tabbar-item__text"},[_vm._t("default",null,{active:_vm.active})],2)])}
var tabbar_item_staticRenderFns = []
var tabbar_item_esExports = { render: tabbar_item_render, staticRenderFns: tabbar_item_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_tabbar_item = (tabbar_item_esExports);
// CONCATENATED MODULE: ./packages/tabbar-item/index.vue
var tabbar_item_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var tabbar_item___vue_template_functional__ = false
/* styles */
var tabbar_item___vue_styles__ = null
/* scopeId */
var tabbar_item___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var tabbar_item___vue_module_identifier__ = null
var tabbar_item_Component = tabbar_item_normalizeComponent(
  tabbar_item,
  selectortype_template_index_0_packages_tabbar_item,
  tabbar_item___vue_template_functional__,
  tabbar_item___vue_styles__,
  tabbar_item___vue_scopeId__,
  tabbar_item___vue_module_identifier__
)

/* harmony default export */ var packages_tabbar_item = (tabbar_item_Component.exports);

// EXTERNAL MODULE: ./packages/utils/raf.js
var raf = __webpack_require__(112);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/tabs/index.vue
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//






/* harmony default export */ var tabs = (Object(utils["b" /* create */])({
  name: 'van-tabs',

  props: {
    sticky: Boolean,
    active: {
      type: [Number, String],
      default: 0
    },
    type: {
      type: String,
      default: 'line'
    },
    duration: {
      type: Number,
      default: 0.2
    },
    swipeThreshold: {
      type: Number,
      default: 4
    }
  },

  data: function data() {
    return {
      tabs: [],
      position: 'content-top',
      curActive: 0,
      navBarStyle: {}
    };
  },


  watch: {
    active: function active(val) {
      this.correctActive(val);
    },
    tabs: function tabs(_tabs) {
      this.correctActive(this.curActive || this.active);
      this.setNavBar();
    },
    curActive: function curActive() {
      this.scrollIntoView();
      this.setNavBar();

      // scroll to correct position
      if (this.position === 'page-top' || this.position === 'content-bottom') {
        utils_scroll.setScrollTop(this.scrollEl, utils_scroll.getElementTop(this.$el));
      }
    },
    sticky: function sticky(isSticky) {
      this.scrollHandler(isSticky);
    }
  },

  mounted: function mounted() {
    var _this = this;

    this.correctActive(this.active);
    this.setNavBar();

    this.$nextTick(function () {
      if (_this.sticky) {
        _this.scrollHandler(true);
      }
      _this.scrollIntoView();
    });
  },
  beforeDestroy: function beforeDestroy() {
    /* istanbul ignore next */
    if (this.sticky) {
      this.scrollHandler(false);
    }
  },


  computed: {
    // whether the nav is scrollable
    scrollable: function scrollable() {
      return this.tabs.length > this.swipeThreshold;
    }
  },

  methods: {
    // whether to bind sticky listener
    scrollHandler: function scrollHandler(init) {
      this.scrollEl = this.scrollEl || utils_scroll.getScrollEventTarget(this.$el);
      (init ? on : off)(this.scrollEl, 'scroll', this.onScroll, true);
      if (init) {
        this.onScroll();
      }
    },


    // adjust tab position
    onScroll: function onScroll() {
      var scrollTop = utils_scroll.getScrollTop(this.scrollEl);
      var elTopToPageTop = utils_scroll.getElementTop(this.$el);
      var elBottomToPageTop = elTopToPageTop + this.$el.offsetHeight - this.$refs.wrap.offsetHeight;
      if (scrollTop > elBottomToPageTop) {
        this.position = 'content-bottom';
      } else if (scrollTop > elTopToPageTop) {
        this.position = 'page-top';
      } else {
        this.position = 'content-top';
      }
    },


    // update nav bar style
    setNavBar: function setNavBar() {
      var _this2 = this;

      this.$nextTick(function () {
        if (!_this2.$refs.tabs) {
          return;
        }

        var tab = _this2.$refs.tabs[_this2.curActive];
        _this2.navBarStyle = {
          width: (tab.offsetWidth || 0) + 'px',
          transform: 'translate(' + (tab.offsetLeft || 0) + 'px, 0)',
          transitionDuration: _this2.duration + 's'
        };
      });
    },


    // correct the value of active
    correctActive: function correctActive(active) {
      active = +active;
      var exist = this.tabs.some(function (tab) {
        return tab.index === active;
      });
      var defaultActive = (this.tabs[0] || {}).index || 0;
      this.curActive = exist ? active : defaultActive;
    },


    // emit event when clicked
    onClick: function onClick(index) {
      if (this.tabs[index].disabled) {
        this.$emit('disabled', index);
      } else {
        this.$emit('click', index);
        this.curActive = index;
      }
    },


    // scroll active tab into view
    scrollIntoView: function scrollIntoView() {
      if (!this.scrollable || !this.$refs.tabs) {
        return;
      }

      var tab = this.$refs.tabs[this.curActive];
      var nav = this.$refs.nav;
      var scrollLeft = nav.scrollLeft,
          navWidth = nav.offsetWidth;
      var offsetLeft = tab.offsetLeft,
          tabWidth = tab.offsetWidth;


      this.scrollTo(nav, scrollLeft, offsetLeft - (navWidth - tabWidth) / 2);
    },


    // animate the scrollLeft of nav
    scrollTo: function scrollTo(el, from, to) {
      var count = 0;
      var frames = Math.round(this.duration * 1000 / 16);
      var animate = function animate() {
        el.scrollLeft += (to - from) / frames;
        /* istanbul ignore next */
        if (++count < frames) {
          Object(raf["a" /* raf */])(animate);
        }
      };
      animate();
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-1a471754","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/tabs/index.vue
var tabs_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-tabs",class:[("van-tabs--" + _vm.type)]},[_c('div',{ref:"wrap",staticClass:"van-tabs__wrap",class:[("van-tabs__wrap--" + _vm.position), {
      'van-tabs--scrollable': _vm.scrollable,
      'van-hairline--top-bottom': _vm.type === 'line'
    }]},[_c('div',{ref:"nav",staticClass:"van-tabs__nav",class:("van-tabs__nav--" + _vm.type)},[(_vm.type === 'line')?_c('div',{staticClass:"van-tabs__nav-bar",style:(_vm.navBarStyle)}):_vm._e(),_vm._l((_vm.tabs),function(tab,index){return _c('div',{key:index,ref:"tabs",refInFor:true,staticClass:"van-tab",class:{
          'van-tab--active': index === _vm.curActive,
          'van-tab--disabled': tab.disabled
        },on:{"click":function($event){_vm.onClick(index)}}},[_c('span',[_vm._v(_vm._s(tab.title))])])})],2)]),_c('div',{staticClass:"van-tabs__content"},[_vm._t("default")],2)])}
var tabs_staticRenderFns = []
var tabs_esExports = { render: tabs_render, staticRenderFns: tabs_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_tabs = (tabs_esExports);
// CONCATENATED MODULE: ./packages/tabs/index.vue
var tabs_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var tabs___vue_template_functional__ = false
/* styles */
var tabs___vue_styles__ = null
/* scopeId */
var tabs___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var tabs___vue_module_identifier__ = null
var tabs_Component = tabs_normalizeComponent(
  tabs,
  selectortype_template_index_0_packages_tabs,
  tabs___vue_template_functional__,
  tabs___vue_styles__,
  tabs___vue_scopeId__,
  tabs___vue_module_identifier__
)

/* harmony default export */ var packages_tabs = (tabs_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/tag/index.vue
//
//
//
//
//
//



/* harmony default export */ var tag = (Object(utils["b" /* create */])({
  name: 'van-tag',
  props: {
    type: String,
    mark: Boolean,
    plain: Boolean
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-25788da1","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/tag/index.vue
var tag_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('span',{staticClass:"van-tag van-hairline--surround",class:( _obj = { 'van-tag--plain': _vm.plain, 'van-tag--mark': _vm.mark }, _obj[("van-tag--" + _vm.type)] = _vm.type, _obj )},[_vm._t("default")],2)
var _obj;}
var tag_staticRenderFns = []
var tag_esExports = { render: tag_render, staticRenderFns: tag_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_tag = (tag_esExports);
// CONCATENATED MODULE: ./packages/tag/index.vue
var tag_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var tag___vue_template_functional__ = false
/* styles */
var tag___vue_styles__ = null
/* scopeId */
var tag___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var tag___vue_module_identifier__ = null
var tag_Component = tag_normalizeComponent(
  tag,
  selectortype_template_index_0_packages_tag,
  tag___vue_template_functional__,
  tag___vue_styles__,
  tag___vue_scopeId__,
  tag___vue_module_identifier__
)

/* harmony default export */ var packages_tag = (tag_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/tree-select/index.vue

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var tree_select = (Object(utils["b" /* create */])({
  name: 'van-tree-select',

  props: {
    items: {
      type: Array,
      default: function _default() {
        return [];
      }
    },
    mainActiveIndex: {
      type: Number,
      default: 0
    },
    activeId: {
      type: Number,
      default: 0
    },
    maxHeight: {
      type: Number,
      default: 300
    }
  },

  computed: {
    subItems: function subItems() {
      var selectedItem = this.items[this.mainActiveIndex] || {};
      return selectedItem.children || [];
    },
    mainHeight: function mainHeight() {
      var maxHeight = Math.max(this.items.length * 44, this.subItems.length * 44);
      return Math.min(maxHeight, this.maxHeight);
    },
    itemHeight: function itemHeight() {
      return Math.min(this.subItems.length * 44, this.maxHeight);
    }
  },

  methods: {
    onItemSelect: function onItemSelect(data) {
      this.$emit('itemclick', extends_default()({}, data));
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-025b100a","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/tree-select/index.vue
var tree_select_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-tree-select",style:({ height: _vm.mainHeight + 'px' })},[_c('div',{staticClass:"van-tree-select__nav"},_vm._l((_vm.items),function(item,index){return _c('div',{staticClass:"van-tree-select__nitem",class:{ 'van-tree-select__nitem--active': _vm.mainActiveIndex === index },on:{"click":function($event){_vm.$emit('navclick', index)}}},[_vm._v("\n      "+_vm._s(item.text)+"\n    ")])})),_c('div',{staticClass:"van-tree-select__content",style:({ height: _vm.itemHeight + 'px' })},_vm._l((_vm.subItems),function(item){return _c('div',{key:item.id,staticClass:"van-tree-select__item",class:{ 'van-tree-select__item--active': _vm.activeId === item.id },on:{"click":function($event){_vm.onItemSelect(item)}}},[_vm._v("\n      "+_vm._s(item.text)+"\n      "),(_vm.activeId === item.id)?_c('icon',{staticClass:"van-tree-select__selected",attrs:{"name":"success"}}):_vm._e()],1)}))])}
var tree_select_staticRenderFns = []
var tree_select_esExports = { render: tree_select_render, staticRenderFns: tree_select_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_tree_select = (tree_select_esExports);
// CONCATENATED MODULE: ./packages/tree-select/index.vue
var tree_select_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var tree_select___vue_template_functional__ = false
/* styles */
var tree_select___vue_styles__ = null
/* scopeId */
var tree_select___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var tree_select___vue_module_identifier__ = null
var tree_select_Component = tree_select_normalizeComponent(
  tree_select,
  selectortype_template_index_0_packages_tree_select,
  tree_select___vue_template_functional__,
  tree_select___vue_styles__,
  tree_select___vue_scopeId__,
  tree_select___vue_module_identifier__
)

/* harmony default export */ var packages_tree_select = (tree_select_Component.exports);

// CONCATENATED MODULE: ./node_modules/babel-loader/lib!./node_modules/vue-loader/lib/selector.js?type=script&index=0!./packages/uploader/index.vue

//
//
//
//
//
//
//
//
//
//
//
//
//
//



/* harmony default export */ var uploader = (Object(utils["b" /* create */])({
  name: 'van-uploader',

  inheritAttrs: false,

  props: {
    disabled: Boolean,
    beforeRead: Function,
    afterRead: Function,
    resultType: {
      type: String,
      default: 'dataUrl'
    },
    maxSize: {
      type: Number,
      default: Number.MAX_VALUE
    }
  },

  methods: {
    onChange: function onChange(event) {
      var _this = this;

      var files = event.target.files;

      if (this.disabled || !files.length) {
        return;
      }

      files = files.length === 1 ? files[0] : [].slice.call(files, 0);
      if (!files || this.beforeRead && !this.beforeRead(files)) {
        return;
      }

      if (Array.isArray(files)) {
        promise_default.a.all(files.map(this.readFile)).then(function (contents) {
          var oversize = false;
          var payload = files.map(function (file, index) {
            if (file.size > _this.maxSize) {
              oversize = true;
            }

            return {
              file: files[index],
              content: contents[index]
            };
          });

          _this.onAfterRead(payload, oversize);
        });
      } else {
        this.readFile(files).then(function (content) {
          _this.onAfterRead({ file: files, content: content }, files.size > _this.maxSize);
        });
      }
    },
    readFile: function readFile(file) {
      var _this2 = this;

      return new promise_default.a(function (resolve) {
        var reader = new FileReader();

        reader.onload = function (event) {
          resolve(event.target.result);
        };

        if (_this2.resultType === 'dataUrl') {
          reader.readAsDataURL(file);
        } else if (_this2.resultType === 'text') {
          reader.readAsText(file);
        }
      });
    },
    onAfterRead: function onAfterRead(files, oversize) {
      if (oversize) {
        this.$emit('oversize', files);
      } else {
        this.afterRead && this.afterRead(files);
        this.$refs.input && (this.$refs.input.value = '');
      }
    }
  }
}));
// CONCATENATED MODULE: ./node_modules/vue-loader/lib/template-compiler?{"id":"data-v-5806c2be","hasScoped":false,"preserveWhitespace":false,"buble":{"transforms":{}}}!./node_modules/vue-loader/lib/selector.js?type=template&index=0!./packages/uploader/index.vue
var uploader_render = function () {var _vm=this;var _h=_vm.$createElement;var _c=_vm._self._c||_h;return _c('div',{staticClass:"van-uploader"},[_vm._t("default"),_c('input',_vm._b({ref:"input",staticClass:"van-uploader__input",attrs:{"type":"file","disabled":_vm.disabled},on:{"change":_vm.onChange}},'input',_vm.$attrs,false))],2)}
var uploader_staticRenderFns = []
var uploader_esExports = { render: uploader_render, staticRenderFns: uploader_staticRenderFns }
/* harmony default export */ var selectortype_template_index_0_packages_uploader = (uploader_esExports);
// CONCATENATED MODULE: ./packages/uploader/index.vue
var uploader_normalizeComponent = __webpack_require__(0)
/* script */


/* template */

/* template functional */
var uploader___vue_template_functional__ = false
/* styles */
var uploader___vue_styles__ = null
/* scopeId */
var uploader___vue_scopeId__ = null
/* moduleIdentifier (server only) */
var uploader___vue_module_identifier__ = null
var uploader_Component = uploader_normalizeComponent(
  uploader,
  selectortype_template_index_0_packages_uploader,
  uploader___vue_template_functional__,
  uploader___vue_styles__,
  uploader___vue_scopeId__,
  uploader___vue_module_identifier__
)

/* harmony default export */ var packages_uploader = (uploader_Component.exports);

// CONCATENATED MODULE: ./packages/waterfall/directive.js



var CONTEXT = '@@Waterfall';
var OFFSET = 300;

// 绑定事件到元素上
// 读取基本的控制变量
function doBindEvent() {
  var _this = this;

  if (this.el[CONTEXT].binded) {
    return;
  }
  this.el[CONTEXT].binded = true;

  this.scrollEventListener = utils_scroll.debounce(handleScrollEvent.bind(this), 200);
  this.scrollEventTarget = utils_scroll.getScrollEventTarget(this.el);

  var disabledExpr = this.el.getAttribute('waterfall-disabled');
  var disabled = false;
  if (disabledExpr) {
    this.vm.$watch(disabledExpr, function (value) {
      _this.disabled = value;
      _this.scrollEventListener();
    });
    disabled = Boolean(this.vm[disabledExpr]);
  }
  this.disabled = disabled;

  var offset = this.el.getAttribute('waterfall-offset');
  this.offset = Number(offset) || OFFSET;

  on(this.scrollEventTarget, 'scroll', this.scrollEventListener, true);

  this.scrollEventListener();
}

// 处理滚动函数
function handleScrollEvent() {
  var element = this.el;
  var scrollEventTarget = this.scrollEventTarget;
  // 已被禁止的滚动处理
  if (this.disabled) return;

  var targetScrollTop = utils_scroll.getScrollTop(scrollEventTarget);
  var targetVisibleHeight = utils_scroll.getVisibleHeight(scrollEventTarget);
  // 滚动元素可视区域下边沿到滚动元素元素最顶上 距离
  var targetBottom = targetScrollTop + targetVisibleHeight;

  // 如果无元素高度，考虑为元素隐藏，直接返回
  if (!targetVisibleHeight) return;

  // 判断是否到了底
  var needLoadMoreToLower = false;
  if (element === scrollEventTarget) {
    needLoadMoreToLower = scrollEventTarget.scrollHeight - targetBottom < this.offset;
  } else {
    var elementBottom = utils_scroll.getElementTop(element) - utils_scroll.getElementTop(scrollEventTarget) + utils_scroll.getVisibleHeight(element);
    needLoadMoreToLower = elementBottom - targetVisibleHeight < this.offset;
  }
  if (needLoadMoreToLower) {
    this.cb.lower && this.cb.lower({ target: scrollEventTarget, top: targetScrollTop });
  }

  // 判断是否到了顶
  var needLoadMoreToUpper = false;
  if (element === scrollEventTarget) {
    needLoadMoreToUpper = targetScrollTop < this.offset;
  } else {
    var elementTop = utils_scroll.getElementTop(element) - utils_scroll.getElementTop(scrollEventTarget);
    needLoadMoreToUpper = elementTop + this.offset > 0;
  }
  if (needLoadMoreToUpper) {
    this.cb.upper && this.cb.upper({ target: scrollEventTarget, top: targetScrollTop });
  }
}

// 绑定事件
function startBind(el) {
  var context = el[CONTEXT];

  context.vm.$nextTick(function () {
    if (utils_scroll.isAttached(el)) {
      doBindEvent.call(el[CONTEXT]);
    }
  });
}

// 确认何时绑事件监听函数
function doCheckStartBind(el) {
  var context = el[CONTEXT];

  if (context.vm._isMounted) {
    startBind(el);
  } else {
    context.vm.$on('hook:mounted', function () {
      startBind(el);
    });
  }
}

/* harmony default export */ var directive = (function (type) {
  return {
    bind: function bind(el, binding, vnode) {
      if (!el[CONTEXT]) {
        el[CONTEXT] = {
          el: el,
          vm: vnode.context,
          cb: {}
        };
      }
      el[CONTEXT].cb[type] = binding.value;

      doCheckStartBind(el);
    },
    update: function update(el) {
      var context = el[CONTEXT];
      context.scrollEventListener && context.scrollEventListener();
    },
    unbind: function unbind(el) {
      var context = el[CONTEXT];
      if (context.scrollEventTarget) {
        off(context.scrollEventTarget, 'scroll', context.scrollEventListener);
      }
    }
  };
});;
// CONCATENATED MODULE: ./packages/waterfall/index.js


var waterfall_install = function install(Vue) {
  Vue.directive('WaterfallLower', directive('lower'));
  Vue.directive('WaterfallUpper', directive('upper'));
};

directive.install = waterfall_install;
/* harmony default export */ var waterfall = (directive);
// CONCATENATED MODULE: ./packages/index.js
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "install", function() { return packages_install; });
/* harmony export (binding) */ __webpack_require__.d(__webpack_exports__, "version", function() { return version; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Actionsheet", function() { return packages_actionsheet; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "AddressEdit", function() { return packages_address_edit; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "AddressList", function() { return packages_address_list; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Area", function() { return packages_area; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Badge", function() { return packages_badge; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "BadgeGroup", function() { return packages_badge_group; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Button", function() { return packages_button; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Card", function() { return packages_card; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Cell", function() { return packages_cell; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "CellGroup", function() { return packages_cell_group; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "CellSwipe", function() { return packages_cell_swipe; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Checkbox", function() { return packages_checkbox; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "CheckboxGroup", function() { return packages_checkbox_group; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Col", function() { return packages_col; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "ContactCard", function() { return packages_contact_card; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "ContactEdit", function() { return packages_contact_edit; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "ContactList", function() { return packages_contact_list; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "CouponCell", function() { return packages_coupon_cell; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "CouponList", function() { return packages_coupon_list; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "DatetimePicker", function() { return packages_datetime_picker; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Dialog", function() { return packages_dialog; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Field", function() { return packages_field; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "GoodsAction", function() { return packages_goods_action; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "GoodsActionBigBtn", function() { return packages_goods_action_big_btn; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "GoodsActionMiniBtn", function() { return packages_goods_action_mini_btn; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Icon", function() { return icon["a" /* default */]; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "ImagePreview", function() { return packages_image_preview; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Lazyload", function() { return lazyload; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Loading", function() { return loading["a" /* default */]; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Locale", function() { return locale["a" /* default */]; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "NavBar", function() { return packages_nav_bar; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "NoticeBar", function() { return packages_notice_bar; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "NumberKeyboard", function() { return packages_number_keyboard; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Pagination", function() { return packages_pagination; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Panel", function() { return packages_panel; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "PasswordInput", function() { return packages_password_input; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Picker", function() { return packages_picker; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Popup", function() { return packages_popup; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Progress", function() { return packages_progress; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "PullRefresh", function() { return packages_pull_refresh; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Radio", function() { return packages_radio; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "RadioGroup", function() { return packages_radio_group; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Row", function() { return packages_row; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Search", function() { return packages_search; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Sku", function() { return sku; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Step", function() { return packages_step; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Stepper", function() { return packages_stepper; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Steps", function() { return packages_steps; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "SubmitBar", function() { return packages_submit_bar; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Swipe", function() { return packages_swipe; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "SwipeItem", function() { return packages_swipe_item; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Switch", function() { return packages_switch; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "SwitchCell", function() { return packages_switch_cell; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Tab", function() { return packages_tab; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Tabbar", function() { return packages_tabbar; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "TabbarItem", function() { return packages_tabbar_item; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Tabs", function() { return packages_tabs; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Tag", function() { return packages_tag; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Toast", function() { return packages_toast; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "TreeSelect", function() { return packages_tree_select; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Uploader", function() { return packages_uploader; });
/* concated harmony reexport */__webpack_require__.d(__webpack_exports__, "Waterfall", function() { return waterfall; });
// This file is auto gererated by build/bin/build-entry.js































































var version = '0.12.7';
var components = [packages_actionsheet, packages_address_edit, packages_address_list, packages_area, packages_badge, packages_badge_group, packages_button, packages_card, packages_cell, packages_cell_group, packages_cell_swipe, packages_checkbox, packages_checkbox_group, packages_col, packages_contact_card, packages_contact_edit, packages_contact_list, packages_coupon_cell, packages_coupon_list, packages_datetime_picker, packages_dialog, packages_field, packages_goods_action, packages_goods_action_big_btn, packages_goods_action_mini_btn, icon["a" /* default */], loading["a" /* default */], packages_nav_bar, packages_notice_bar, packages_number_keyboard, packages_pagination, packages_panel, packages_password_input, packages_picker, packages_popup, packages_progress, packages_pull_refresh, packages_radio, packages_radio_group, packages_row, packages_search, sku, packages_step, packages_stepper, packages_steps, packages_submit_bar, packages_swipe, packages_swipe_item, packages_switch, packages_switch_cell, packages_tab, packages_tabbar, packages_tabbar_item, packages_tabs, packages_tag, packages_tree_select, packages_uploader];

var packages_install = function install(Vue) {
  components.forEach(function (Component) {
    Vue.use(Component);
  });
};

if (typeof window !== 'undefined' && window.Vue) {
  packages_install(window.Vue);
}



/* harmony default export */ var packages_0 = __webpack_exports__["default"] = ({
  install: packages_install,
  version: version
});

/***/ }),
/* 65 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = { "default": __webpack_require__(66), __esModule: true };

/***/ }),
/* 66 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(44);
__webpack_require__(53);
module.exports = __webpack_require__(37).f('iterator');


/***/ }),
/* 67 */
/***/ (function(module, exports, __webpack_require__) {

var toInteger = __webpack_require__(29);
var defined = __webpack_require__(30);
// true  -> String#at
// false -> String#codePointAt
module.exports = function (TO_STRING) {
  return function (that, pos) {
    var s = String(defined(that));
    var i = toInteger(pos);
    var l = s.length;
    var a, b;
    if (i < 0 || i >= l) return TO_STRING ? '' : undefined;
    a = s.charCodeAt(i);
    return a < 0xd800 || a > 0xdbff || i + 1 === l || (b = s.charCodeAt(i + 1)) < 0xdc00 || b > 0xdfff
      ? TO_STRING ? s.charAt(i) : a
      : TO_STRING ? s.slice(i, i + 2) : (a - 0xd800 << 10) + (b - 0xdc00) + 0x10000;
  };
};


/***/ }),
/* 68 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var create = __webpack_require__(48);
var descriptor = __webpack_require__(24);
var setToStringTag = __webpack_require__(26);
var IteratorPrototype = {};

// 25.1.2.1.1 %IteratorPrototype%[@@iterator]()
__webpack_require__(9)(IteratorPrototype, __webpack_require__(4)('iterator'), function () { return this; });

module.exports = function (Constructor, NAME, next) {
  Constructor.prototype = create(IteratorPrototype, { next: descriptor(1, next) });
  setToStringTag(Constructor, NAME + ' Iterator');
};


/***/ }),
/* 69 */
/***/ (function(module, exports, __webpack_require__) {

var dP = __webpack_require__(10);
var anObject = __webpack_require__(7);
var getKeys = __webpack_require__(19);

module.exports = __webpack_require__(12) ? Object.defineProperties : function defineProperties(O, Properties) {
  anObject(O);
  var keys = getKeys(Properties);
  var length = keys.length;
  var i = 0;
  var P;
  while (length > i) dP.f(O, P = keys[i++], Properties[P]);
  return O;
};


/***/ }),
/* 70 */
/***/ (function(module, exports, __webpack_require__) {

// false -> Array#indexOf
// true  -> Array#includes
var toIObject = __webpack_require__(15);
var toLength = __webpack_require__(51);
var toAbsoluteIndex = __webpack_require__(71);
module.exports = function (IS_INCLUDES) {
  return function ($this, el, fromIndex) {
    var O = toIObject($this);
    var length = toLength(O.length);
    var index = toAbsoluteIndex(fromIndex, length);
    var value;
    // Array#includes uses SameValueZero equality algorithm
    // eslint-disable-next-line no-self-compare
    if (IS_INCLUDES && el != el) while (length > index) {
      value = O[index++];
      // eslint-disable-next-line no-self-compare
      if (value != value) return true;
    // Array#indexOf ignores holes, Array#includes - not
    } else for (;length > index; index++) if (IS_INCLUDES || index in O) {
      if (O[index] === el) return IS_INCLUDES || index || 0;
    } return !IS_INCLUDES && -1;
  };
};


/***/ }),
/* 71 */
/***/ (function(module, exports, __webpack_require__) {

var toInteger = __webpack_require__(29);
var max = Math.max;
var min = Math.min;
module.exports = function (index, length) {
  index = toInteger(index);
  return index < 0 ? max(index + length, 0) : min(index, length);
};


/***/ }),
/* 72 */
/***/ (function(module, exports, __webpack_require__) {

// 19.1.2.9 / 15.2.3.2 Object.getPrototypeOf(O)
var has = __webpack_require__(13);
var toObject = __webpack_require__(36);
var IE_PROTO = __webpack_require__(33)('IE_PROTO');
var ObjectProto = Object.prototype;

module.exports = Object.getPrototypeOf || function (O) {
  O = toObject(O);
  if (has(O, IE_PROTO)) return O[IE_PROTO];
  if (typeof O.constructor == 'function' && O instanceof O.constructor) {
    return O.constructor.prototype;
  } return O instanceof Object ? ObjectProto : null;
};


/***/ }),
/* 73 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var addToUnscopables = __webpack_require__(74);
var step = __webpack_require__(75);
var Iterators = __webpack_require__(18);
var toIObject = __webpack_require__(15);

// 22.1.3.4 Array.prototype.entries()
// 22.1.3.13 Array.prototype.keys()
// 22.1.3.29 Array.prototype.values()
// 22.1.3.30 Array.prototype[@@iterator]()
module.exports = __webpack_require__(45)(Array, 'Array', function (iterated, kind) {
  this._t = toIObject(iterated); // target
  this._i = 0;                   // next index
  this._k = kind;                // kind
// 22.1.5.2.1 %ArrayIteratorPrototype%.next()
}, function () {
  var O = this._t;
  var kind = this._k;
  var index = this._i++;
  if (!O || index >= O.length) {
    this._t = undefined;
    return step(1);
  }
  if (kind == 'keys') return step(0, index);
  if (kind == 'values') return step(0, O[index]);
  return step(0, [index, O[index]]);
}, 'values');

// argumentsList[@@iterator] is %ArrayProto_values% (9.4.4.6, 9.4.4.7)
Iterators.Arguments = Iterators.Array;

addToUnscopables('keys');
addToUnscopables('values');
addToUnscopables('entries');


/***/ }),
/* 74 */
/***/ (function(module, exports) {

module.exports = function () { /* empty */ };


/***/ }),
/* 75 */
/***/ (function(module, exports) {

module.exports = function (done, value) {
  return { value: value, done: !!done };
};


/***/ }),
/* 76 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = { "default": __webpack_require__(77), __esModule: true };

/***/ }),
/* 77 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(78);
__webpack_require__(55);
__webpack_require__(84);
__webpack_require__(85);
module.exports = __webpack_require__(3).Symbol;


/***/ }),
/* 78 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

// ECMAScript 6 symbols shim
var global = __webpack_require__(2);
var has = __webpack_require__(13);
var DESCRIPTORS = __webpack_require__(12);
var $export = __webpack_require__(6);
var redefine = __webpack_require__(47);
var META = __webpack_require__(79).KEY;
var $fails = __webpack_require__(14);
var shared = __webpack_require__(34);
var setToStringTag = __webpack_require__(26);
var uid = __webpack_require__(25);
var wks = __webpack_require__(4);
var wksExt = __webpack_require__(37);
var wksDefine = __webpack_require__(38);
var enumKeys = __webpack_require__(80);
var isArray = __webpack_require__(81);
var anObject = __webpack_require__(7);
var isObject = __webpack_require__(11);
var toIObject = __webpack_require__(15);
var toPrimitive = __webpack_require__(32);
var createDesc = __webpack_require__(24);
var _create = __webpack_require__(48);
var gOPNExt = __webpack_require__(82);
var $GOPD = __webpack_require__(83);
var $DP = __webpack_require__(10);
var $keys = __webpack_require__(19);
var gOPD = $GOPD.f;
var dP = $DP.f;
var gOPN = gOPNExt.f;
var $Symbol = global.Symbol;
var $JSON = global.JSON;
var _stringify = $JSON && $JSON.stringify;
var PROTOTYPE = 'prototype';
var HIDDEN = wks('_hidden');
var TO_PRIMITIVE = wks('toPrimitive');
var isEnum = {}.propertyIsEnumerable;
var SymbolRegistry = shared('symbol-registry');
var AllSymbols = shared('symbols');
var OPSymbols = shared('op-symbols');
var ObjectProto = Object[PROTOTYPE];
var USE_NATIVE = typeof $Symbol == 'function';
var QObject = global.QObject;
// Don't use setters in Qt Script, https://github.com/zloirock/core-js/issues/173
var setter = !QObject || !QObject[PROTOTYPE] || !QObject[PROTOTYPE].findChild;

// fallback for old Android, https://code.google.com/p/v8/issues/detail?id=687
var setSymbolDesc = DESCRIPTORS && $fails(function () {
  return _create(dP({}, 'a', {
    get: function () { return dP(this, 'a', { value: 7 }).a; }
  })).a != 7;
}) ? function (it, key, D) {
  var protoDesc = gOPD(ObjectProto, key);
  if (protoDesc) delete ObjectProto[key];
  dP(it, key, D);
  if (protoDesc && it !== ObjectProto) dP(ObjectProto, key, protoDesc);
} : dP;

var wrap = function (tag) {
  var sym = AllSymbols[tag] = _create($Symbol[PROTOTYPE]);
  sym._k = tag;
  return sym;
};

var isSymbol = USE_NATIVE && typeof $Symbol.iterator == 'symbol' ? function (it) {
  return typeof it == 'symbol';
} : function (it) {
  return it instanceof $Symbol;
};

var $defineProperty = function defineProperty(it, key, D) {
  if (it === ObjectProto) $defineProperty(OPSymbols, key, D);
  anObject(it);
  key = toPrimitive(key, true);
  anObject(D);
  if (has(AllSymbols, key)) {
    if (!D.enumerable) {
      if (!has(it, HIDDEN)) dP(it, HIDDEN, createDesc(1, {}));
      it[HIDDEN][key] = true;
    } else {
      if (has(it, HIDDEN) && it[HIDDEN][key]) it[HIDDEN][key] = false;
      D = _create(D, { enumerable: createDesc(0, false) });
    } return setSymbolDesc(it, key, D);
  } return dP(it, key, D);
};
var $defineProperties = function defineProperties(it, P) {
  anObject(it);
  var keys = enumKeys(P = toIObject(P));
  var i = 0;
  var l = keys.length;
  var key;
  while (l > i) $defineProperty(it, key = keys[i++], P[key]);
  return it;
};
var $create = function create(it, P) {
  return P === undefined ? _create(it) : $defineProperties(_create(it), P);
};
var $propertyIsEnumerable = function propertyIsEnumerable(key) {
  var E = isEnum.call(this, key = toPrimitive(key, true));
  if (this === ObjectProto && has(AllSymbols, key) && !has(OPSymbols, key)) return false;
  return E || !has(this, key) || !has(AllSymbols, key) || has(this, HIDDEN) && this[HIDDEN][key] ? E : true;
};
var $getOwnPropertyDescriptor = function getOwnPropertyDescriptor(it, key) {
  it = toIObject(it);
  key = toPrimitive(key, true);
  if (it === ObjectProto && has(AllSymbols, key) && !has(OPSymbols, key)) return;
  var D = gOPD(it, key);
  if (D && has(AllSymbols, key) && !(has(it, HIDDEN) && it[HIDDEN][key])) D.enumerable = true;
  return D;
};
var $getOwnPropertyNames = function getOwnPropertyNames(it) {
  var names = gOPN(toIObject(it));
  var result = [];
  var i = 0;
  var key;
  while (names.length > i) {
    if (!has(AllSymbols, key = names[i++]) && key != HIDDEN && key != META) result.push(key);
  } return result;
};
var $getOwnPropertySymbols = function getOwnPropertySymbols(it) {
  var IS_OP = it === ObjectProto;
  var names = gOPN(IS_OP ? OPSymbols : toIObject(it));
  var result = [];
  var i = 0;
  var key;
  while (names.length > i) {
    if (has(AllSymbols, key = names[i++]) && (IS_OP ? has(ObjectProto, key) : true)) result.push(AllSymbols[key]);
  } return result;
};

// 19.4.1.1 Symbol([description])
if (!USE_NATIVE) {
  $Symbol = function Symbol() {
    if (this instanceof $Symbol) throw TypeError('Symbol is not a constructor!');
    var tag = uid(arguments.length > 0 ? arguments[0] : undefined);
    var $set = function (value) {
      if (this === ObjectProto) $set.call(OPSymbols, value);
      if (has(this, HIDDEN) && has(this[HIDDEN], tag)) this[HIDDEN][tag] = false;
      setSymbolDesc(this, tag, createDesc(1, value));
    };
    if (DESCRIPTORS && setter) setSymbolDesc(ObjectProto, tag, { configurable: true, set: $set });
    return wrap(tag);
  };
  redefine($Symbol[PROTOTYPE], 'toString', function toString() {
    return this._k;
  });

  $GOPD.f = $getOwnPropertyDescriptor;
  $DP.f = $defineProperty;
  __webpack_require__(54).f = gOPNExt.f = $getOwnPropertyNames;
  __webpack_require__(27).f = $propertyIsEnumerable;
  __webpack_require__(39).f = $getOwnPropertySymbols;

  if (DESCRIPTORS && !__webpack_require__(21)) {
    redefine(ObjectProto, 'propertyIsEnumerable', $propertyIsEnumerable, true);
  }

  wksExt.f = function (name) {
    return wrap(wks(name));
  };
}

$export($export.G + $export.W + $export.F * !USE_NATIVE, { Symbol: $Symbol });

for (var es6Symbols = (
  // 19.4.2.2, 19.4.2.3, 19.4.2.4, 19.4.2.6, 19.4.2.8, 19.4.2.9, 19.4.2.10, 19.4.2.11, 19.4.2.12, 19.4.2.13, 19.4.2.14
  'hasInstance,isConcatSpreadable,iterator,match,replace,search,species,split,toPrimitive,toStringTag,unscopables'
).split(','), j = 0; es6Symbols.length > j;)wks(es6Symbols[j++]);

for (var wellKnownSymbols = $keys(wks.store), k = 0; wellKnownSymbols.length > k;) wksDefine(wellKnownSymbols[k++]);

$export($export.S + $export.F * !USE_NATIVE, 'Symbol', {
  // 19.4.2.1 Symbol.for(key)
  'for': function (key) {
    return has(SymbolRegistry, key += '')
      ? SymbolRegistry[key]
      : SymbolRegistry[key] = $Symbol(key);
  },
  // 19.4.2.5 Symbol.keyFor(sym)
  keyFor: function keyFor(sym) {
    if (!isSymbol(sym)) throw TypeError(sym + ' is not a symbol!');
    for (var key in SymbolRegistry) if (SymbolRegistry[key] === sym) return key;
  },
  useSetter: function () { setter = true; },
  useSimple: function () { setter = false; }
});

$export($export.S + $export.F * !USE_NATIVE, 'Object', {
  // 19.1.2.2 Object.create(O [, Properties])
  create: $create,
  // 19.1.2.4 Object.defineProperty(O, P, Attributes)
  defineProperty: $defineProperty,
  // 19.1.2.3 Object.defineProperties(O, Properties)
  defineProperties: $defineProperties,
  // 19.1.2.6 Object.getOwnPropertyDescriptor(O, P)
  getOwnPropertyDescriptor: $getOwnPropertyDescriptor,
  // 19.1.2.7 Object.getOwnPropertyNames(O)
  getOwnPropertyNames: $getOwnPropertyNames,
  // 19.1.2.8 Object.getOwnPropertySymbols(O)
  getOwnPropertySymbols: $getOwnPropertySymbols
});

// 24.3.2 JSON.stringify(value [, replacer [, space]])
$JSON && $export($export.S + $export.F * (!USE_NATIVE || $fails(function () {
  var S = $Symbol();
  // MS Edge converts symbol values to JSON as {}
  // WebKit converts symbol values to JSON as null
  // V8 throws on boxed symbols
  return _stringify([S]) != '[null]' || _stringify({ a: S }) != '{}' || _stringify(Object(S)) != '{}';
})), 'JSON', {
  stringify: function stringify(it) {
    var args = [it];
    var i = 1;
    var replacer, $replacer;
    while (arguments.length > i) args.push(arguments[i++]);
    $replacer = replacer = args[1];
    if (!isObject(replacer) && it === undefined || isSymbol(it)) return; // IE8 returns string on undefined
    if (!isArray(replacer)) replacer = function (key, value) {
      if (typeof $replacer == 'function') value = $replacer.call(this, key, value);
      if (!isSymbol(value)) return value;
    };
    args[1] = replacer;
    return _stringify.apply($JSON, args);
  }
});

// 19.4.3.4 Symbol.prototype[@@toPrimitive](hint)
$Symbol[PROTOTYPE][TO_PRIMITIVE] || __webpack_require__(9)($Symbol[PROTOTYPE], TO_PRIMITIVE, $Symbol[PROTOTYPE].valueOf);
// 19.4.3.5 Symbol.prototype[@@toStringTag]
setToStringTag($Symbol, 'Symbol');
// 20.2.1.9 Math[@@toStringTag]
setToStringTag(Math, 'Math', true);
// 24.3.3 JSON[@@toStringTag]
setToStringTag(global.JSON, 'JSON', true);


/***/ }),
/* 79 */
/***/ (function(module, exports, __webpack_require__) {

var META = __webpack_require__(25)('meta');
var isObject = __webpack_require__(11);
var has = __webpack_require__(13);
var setDesc = __webpack_require__(10).f;
var id = 0;
var isExtensible = Object.isExtensible || function () {
  return true;
};
var FREEZE = !__webpack_require__(14)(function () {
  return isExtensible(Object.preventExtensions({}));
});
var setMeta = function (it) {
  setDesc(it, META, { value: {
    i: 'O' + ++id, // object ID
    w: {}          // weak collections IDs
  } });
};
var fastKey = function (it, create) {
  // return primitive with prefix
  if (!isObject(it)) return typeof it == 'symbol' ? it : (typeof it == 'string' ? 'S' : 'P') + it;
  if (!has(it, META)) {
    // can't set metadata to uncaught frozen object
    if (!isExtensible(it)) return 'F';
    // not necessary to add metadata
    if (!create) return 'E';
    // add missing metadata
    setMeta(it);
  // return object ID
  } return it[META].i;
};
var getWeak = function (it, create) {
  if (!has(it, META)) {
    // can't set metadata to uncaught frozen object
    if (!isExtensible(it)) return true;
    // not necessary to add metadata
    if (!create) return false;
    // add missing metadata
    setMeta(it);
  // return hash weak collections IDs
  } return it[META].w;
};
// add metadata on freeze-family methods calling
var onFreeze = function (it) {
  if (FREEZE && meta.NEED && isExtensible(it) && !has(it, META)) setMeta(it);
  return it;
};
var meta = module.exports = {
  KEY: META,
  NEED: false,
  fastKey: fastKey,
  getWeak: getWeak,
  onFreeze: onFreeze
};


/***/ }),
/* 80 */
/***/ (function(module, exports, __webpack_require__) {

// all enumerable object keys, includes symbols
var getKeys = __webpack_require__(19);
var gOPS = __webpack_require__(39);
var pIE = __webpack_require__(27);
module.exports = function (it) {
  var result = getKeys(it);
  var getSymbols = gOPS.f;
  if (getSymbols) {
    var symbols = getSymbols(it);
    var isEnum = pIE.f;
    var i = 0;
    var key;
    while (symbols.length > i) if (isEnum.call(it, key = symbols[i++])) result.push(key);
  } return result;
};


/***/ }),
/* 81 */
/***/ (function(module, exports, __webpack_require__) {

// 7.2.2 IsArray(argument)
var cof = __webpack_require__(20);
module.exports = Array.isArray || function isArray(arg) {
  return cof(arg) == 'Array';
};


/***/ }),
/* 82 */
/***/ (function(module, exports, __webpack_require__) {

// fallback for IE11 buggy Object.getOwnPropertyNames with iframe and window
var toIObject = __webpack_require__(15);
var gOPN = __webpack_require__(54).f;
var toString = {}.toString;

var windowNames = typeof window == 'object' && window && Object.getOwnPropertyNames
  ? Object.getOwnPropertyNames(window) : [];

var getWindowNames = function (it) {
  try {
    return gOPN(it);
  } catch (e) {
    return windowNames.slice();
  }
};

module.exports.f = function getOwnPropertyNames(it) {
  return windowNames && toString.call(it) == '[object Window]' ? getWindowNames(it) : gOPN(toIObject(it));
};


/***/ }),
/* 83 */
/***/ (function(module, exports, __webpack_require__) {

var pIE = __webpack_require__(27);
var createDesc = __webpack_require__(24);
var toIObject = __webpack_require__(15);
var toPrimitive = __webpack_require__(32);
var has = __webpack_require__(13);
var IE8_DOM_DEFINE = __webpack_require__(46);
var gOPD = Object.getOwnPropertyDescriptor;

exports.f = __webpack_require__(12) ? gOPD : function getOwnPropertyDescriptor(O, P) {
  O = toIObject(O);
  P = toPrimitive(P, true);
  if (IE8_DOM_DEFINE) try {
    return gOPD(O, P);
  } catch (e) { /* empty */ }
  if (has(O, P)) return createDesc(!pIE.f.call(O, P), O[P]);
};


/***/ }),
/* 84 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(38)('asyncIterator');


/***/ }),
/* 85 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(38)('observable');


/***/ }),
/* 86 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(87);
module.exports = __webpack_require__(3).Object.assign;


/***/ }),
/* 87 */
/***/ (function(module, exports, __webpack_require__) {

// 19.1.3.1 Object.assign(target, source)
var $export = __webpack_require__(6);

$export($export.S + $export.F, 'Object', { assign: __webpack_require__(88) });


/***/ }),
/* 88 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

// 19.1.2.1 Object.assign(target, source, ...)
var getKeys = __webpack_require__(19);
var gOPS = __webpack_require__(39);
var pIE = __webpack_require__(27);
var toObject = __webpack_require__(36);
var IObject = __webpack_require__(50);
var $assign = Object.assign;

// should work with symbols and should have deterministic property order (V8 bug)
module.exports = !$assign || __webpack_require__(14)(function () {
  var A = {};
  var B = {};
  // eslint-disable-next-line no-undef
  var S = Symbol();
  var K = 'abcdefghijklmnopqrst';
  A[S] = 7;
  K.split('').forEach(function (k) { B[k] = k; });
  return $assign({}, A)[S] != 7 || Object.keys($assign({}, B)).join('') != K;
}) ? function assign(target, source) { // eslint-disable-line no-unused-vars
  var T = toObject(target);
  var aLen = arguments.length;
  var index = 1;
  var getSymbols = gOPS.f;
  var isEnum = pIE.f;
  while (aLen > index) {
    var S = IObject(arguments[index++]);
    var keys = getSymbols ? getKeys(S).concat(getSymbols(S)) : getKeys(S);
    var length = keys.length;
    var j = 0;
    var key;
    while (length > j) if (isEnum.call(S, key = keys[j++])) T[key] = S[key];
  } return T;
} : $assign;


/***/ }),
/* 89 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(55);
__webpack_require__(44);
__webpack_require__(53);
__webpack_require__(90);
__webpack_require__(101);
__webpack_require__(102);
module.exports = __webpack_require__(3).Promise;


/***/ }),
/* 90 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var LIBRARY = __webpack_require__(21);
var global = __webpack_require__(2);
var ctx = __webpack_require__(22);
var classof = __webpack_require__(59);
var $export = __webpack_require__(6);
var isObject = __webpack_require__(11);
var aFunction = __webpack_require__(23);
var anInstance = __webpack_require__(91);
var forOf = __webpack_require__(92);
var speciesConstructor = __webpack_require__(60);
var task = __webpack_require__(61).set;
var microtask = __webpack_require__(97)();
var newPromiseCapabilityModule = __webpack_require__(41);
var perform = __webpack_require__(62);
var promiseResolve = __webpack_require__(63);
var PROMISE = 'Promise';
var TypeError = global.TypeError;
var process = global.process;
var $Promise = global[PROMISE];
var isNode = classof(process) == 'process';
var empty = function () { /* empty */ };
var Internal, newGenericPromiseCapability, OwnPromiseCapability, Wrapper;
var newPromiseCapability = newGenericPromiseCapability = newPromiseCapabilityModule.f;

var USE_NATIVE = !!function () {
  try {
    // correct subclassing with @@species support
    var promise = $Promise.resolve(1);
    var FakePromise = (promise.constructor = {})[__webpack_require__(4)('species')] = function (exec) {
      exec(empty, empty);
    };
    // unhandled rejections tracking support, NodeJS Promise without it fails @@species test
    return (isNode || typeof PromiseRejectionEvent == 'function') && promise.then(empty) instanceof FakePromise;
  } catch (e) { /* empty */ }
}();

// helpers
var isThenable = function (it) {
  var then;
  return isObject(it) && typeof (then = it.then) == 'function' ? then : false;
};
var notify = function (promise, isReject) {
  if (promise._n) return;
  promise._n = true;
  var chain = promise._c;
  microtask(function () {
    var value = promise._v;
    var ok = promise._s == 1;
    var i = 0;
    var run = function (reaction) {
      var handler = ok ? reaction.ok : reaction.fail;
      var resolve = reaction.resolve;
      var reject = reaction.reject;
      var domain = reaction.domain;
      var result, then;
      try {
        if (handler) {
          if (!ok) {
            if (promise._h == 2) onHandleUnhandled(promise);
            promise._h = 1;
          }
          if (handler === true) result = value;
          else {
            if (domain) domain.enter();
            result = handler(value);
            if (domain) domain.exit();
          }
          if (result === reaction.promise) {
            reject(TypeError('Promise-chain cycle'));
          } else if (then = isThenable(result)) {
            then.call(result, resolve, reject);
          } else resolve(result);
        } else reject(value);
      } catch (e) {
        reject(e);
      }
    };
    while (chain.length > i) run(chain[i++]); // variable length - can't use forEach
    promise._c = [];
    promise._n = false;
    if (isReject && !promise._h) onUnhandled(promise);
  });
};
var onUnhandled = function (promise) {
  task.call(global, function () {
    var value = promise._v;
    var unhandled = isUnhandled(promise);
    var result, handler, console;
    if (unhandled) {
      result = perform(function () {
        if (isNode) {
          process.emit('unhandledRejection', value, promise);
        } else if (handler = global.onunhandledrejection) {
          handler({ promise: promise, reason: value });
        } else if ((console = global.console) && console.error) {
          console.error('Unhandled promise rejection', value);
        }
      });
      // Browsers should not trigger `rejectionHandled` event if it was handled here, NodeJS - should
      promise._h = isNode || isUnhandled(promise) ? 2 : 1;
    } promise._a = undefined;
    if (unhandled && result.e) throw result.v;
  });
};
var isUnhandled = function (promise) {
  return promise._h !== 1 && (promise._a || promise._c).length === 0;
};
var onHandleUnhandled = function (promise) {
  task.call(global, function () {
    var handler;
    if (isNode) {
      process.emit('rejectionHandled', promise);
    } else if (handler = global.onrejectionhandled) {
      handler({ promise: promise, reason: promise._v });
    }
  });
};
var $reject = function (value) {
  var promise = this;
  if (promise._d) return;
  promise._d = true;
  promise = promise._w || promise; // unwrap
  promise._v = value;
  promise._s = 2;
  if (!promise._a) promise._a = promise._c.slice();
  notify(promise, true);
};
var $resolve = function (value) {
  var promise = this;
  var then;
  if (promise._d) return;
  promise._d = true;
  promise = promise._w || promise; // unwrap
  try {
    if (promise === value) throw TypeError("Promise can't be resolved itself");
    if (then = isThenable(value)) {
      microtask(function () {
        var wrapper = { _w: promise, _d: false }; // wrap
        try {
          then.call(value, ctx($resolve, wrapper, 1), ctx($reject, wrapper, 1));
        } catch (e) {
          $reject.call(wrapper, e);
        }
      });
    } else {
      promise._v = value;
      promise._s = 1;
      notify(promise, false);
    }
  } catch (e) {
    $reject.call({ _w: promise, _d: false }, e); // wrap
  }
};

// constructor polyfill
if (!USE_NATIVE) {
  // 25.4.3.1 Promise(executor)
  $Promise = function Promise(executor) {
    anInstance(this, $Promise, PROMISE, '_h');
    aFunction(executor);
    Internal.call(this);
    try {
      executor(ctx($resolve, this, 1), ctx($reject, this, 1));
    } catch (err) {
      $reject.call(this, err);
    }
  };
  // eslint-disable-next-line no-unused-vars
  Internal = function Promise(executor) {
    this._c = [];             // <- awaiting reactions
    this._a = undefined;      // <- checked in isUnhandled reactions
    this._s = 0;              // <- state
    this._d = false;          // <- done
    this._v = undefined;      // <- value
    this._h = 0;              // <- rejection state, 0 - default, 1 - handled, 2 - unhandled
    this._n = false;          // <- notify
  };
  Internal.prototype = __webpack_require__(98)($Promise.prototype, {
    // 25.4.5.3 Promise.prototype.then(onFulfilled, onRejected)
    then: function then(onFulfilled, onRejected) {
      var reaction = newPromiseCapability(speciesConstructor(this, $Promise));
      reaction.ok = typeof onFulfilled == 'function' ? onFulfilled : true;
      reaction.fail = typeof onRejected == 'function' && onRejected;
      reaction.domain = isNode ? process.domain : undefined;
      this._c.push(reaction);
      if (this._a) this._a.push(reaction);
      if (this._s) notify(this, false);
      return reaction.promise;
    },
    // 25.4.5.1 Promise.prototype.catch(onRejected)
    'catch': function (onRejected) {
      return this.then(undefined, onRejected);
    }
  });
  OwnPromiseCapability = function () {
    var promise = new Internal();
    this.promise = promise;
    this.resolve = ctx($resolve, promise, 1);
    this.reject = ctx($reject, promise, 1);
  };
  newPromiseCapabilityModule.f = newPromiseCapability = function (C) {
    return C === $Promise || C === Wrapper
      ? new OwnPromiseCapability(C)
      : newGenericPromiseCapability(C);
  };
}

$export($export.G + $export.W + $export.F * !USE_NATIVE, { Promise: $Promise });
__webpack_require__(26)($Promise, PROMISE);
__webpack_require__(99)(PROMISE);
Wrapper = __webpack_require__(3)[PROMISE];

// statics
$export($export.S + $export.F * !USE_NATIVE, PROMISE, {
  // 25.4.4.5 Promise.reject(r)
  reject: function reject(r) {
    var capability = newPromiseCapability(this);
    var $$reject = capability.reject;
    $$reject(r);
    return capability.promise;
  }
});
$export($export.S + $export.F * (LIBRARY || !USE_NATIVE), PROMISE, {
  // 25.4.4.6 Promise.resolve(x)
  resolve: function resolve(x) {
    return promiseResolve(LIBRARY && this === Wrapper ? $Promise : this, x);
  }
});
$export($export.S + $export.F * !(USE_NATIVE && __webpack_require__(100)(function (iter) {
  $Promise.all(iter)['catch'](empty);
})), PROMISE, {
  // 25.4.4.1 Promise.all(iterable)
  all: function all(iterable) {
    var C = this;
    var capability = newPromiseCapability(C);
    var resolve = capability.resolve;
    var reject = capability.reject;
    var result = perform(function () {
      var values = [];
      var index = 0;
      var remaining = 1;
      forOf(iterable, false, function (promise) {
        var $index = index++;
        var alreadyCalled = false;
        values.push(undefined);
        remaining++;
        C.resolve(promise).then(function (value) {
          if (alreadyCalled) return;
          alreadyCalled = true;
          values[$index] = value;
          --remaining || resolve(values);
        }, reject);
      });
      --remaining || resolve(values);
    });
    if (result.e) reject(result.v);
    return capability.promise;
  },
  // 25.4.4.4 Promise.race(iterable)
  race: function race(iterable) {
    var C = this;
    var capability = newPromiseCapability(C);
    var reject = capability.reject;
    var result = perform(function () {
      forOf(iterable, false, function (promise) {
        C.resolve(promise).then(capability.resolve, reject);
      });
    });
    if (result.e) reject(result.v);
    return capability.promise;
  }
});


/***/ }),
/* 91 */
/***/ (function(module, exports) {

module.exports = function (it, Constructor, name, forbiddenField) {
  if (!(it instanceof Constructor) || (forbiddenField !== undefined && forbiddenField in it)) {
    throw TypeError(name + ': incorrect invocation!');
  } return it;
};


/***/ }),
/* 92 */
/***/ (function(module, exports, __webpack_require__) {

var ctx = __webpack_require__(22);
var call = __webpack_require__(93);
var isArrayIter = __webpack_require__(94);
var anObject = __webpack_require__(7);
var toLength = __webpack_require__(51);
var getIterFn = __webpack_require__(95);
var BREAK = {};
var RETURN = {};
var exports = module.exports = function (iterable, entries, fn, that, ITERATOR) {
  var iterFn = ITERATOR ? function () { return iterable; } : getIterFn(iterable);
  var f = ctx(fn, that, entries ? 2 : 1);
  var index = 0;
  var length, step, iterator, result;
  if (typeof iterFn != 'function') throw TypeError(iterable + ' is not iterable!');
  // fast case for arrays with default iterator
  if (isArrayIter(iterFn)) for (length = toLength(iterable.length); length > index; index++) {
    result = entries ? f(anObject(step = iterable[index])[0], step[1]) : f(iterable[index]);
    if (result === BREAK || result === RETURN) return result;
  } else for (iterator = iterFn.call(iterable); !(step = iterator.next()).done;) {
    result = call(iterator, f, step.value, entries);
    if (result === BREAK || result === RETURN) return result;
  }
};
exports.BREAK = BREAK;
exports.RETURN = RETURN;


/***/ }),
/* 93 */
/***/ (function(module, exports, __webpack_require__) {

// call something on iterator step with safe closing on error
var anObject = __webpack_require__(7);
module.exports = function (iterator, fn, value, entries) {
  try {
    return entries ? fn(anObject(value)[0], value[1]) : fn(value);
  // 7.4.6 IteratorClose(iterator, completion)
  } catch (e) {
    var ret = iterator['return'];
    if (ret !== undefined) anObject(ret.call(iterator));
    throw e;
  }
};


/***/ }),
/* 94 */
/***/ (function(module, exports, __webpack_require__) {

// check on default Array iterator
var Iterators = __webpack_require__(18);
var ITERATOR = __webpack_require__(4)('iterator');
var ArrayProto = Array.prototype;

module.exports = function (it) {
  return it !== undefined && (Iterators.Array === it || ArrayProto[ITERATOR] === it);
};


/***/ }),
/* 95 */
/***/ (function(module, exports, __webpack_require__) {

var classof = __webpack_require__(59);
var ITERATOR = __webpack_require__(4)('iterator');
var Iterators = __webpack_require__(18);
module.exports = __webpack_require__(3).getIteratorMethod = function (it) {
  if (it != undefined) return it[ITERATOR]
    || it['@@iterator']
    || Iterators[classof(it)];
};


/***/ }),
/* 96 */
/***/ (function(module, exports) {

// fast apply, http://jsperf.lnkit.com/fast-apply/5
module.exports = function (fn, args, that) {
  var un = that === undefined;
  switch (args.length) {
    case 0: return un ? fn()
                      : fn.call(that);
    case 1: return un ? fn(args[0])
                      : fn.call(that, args[0]);
    case 2: return un ? fn(args[0], args[1])
                      : fn.call(that, args[0], args[1]);
    case 3: return un ? fn(args[0], args[1], args[2])
                      : fn.call(that, args[0], args[1], args[2]);
    case 4: return un ? fn(args[0], args[1], args[2], args[3])
                      : fn.call(that, args[0], args[1], args[2], args[3]);
  } return fn.apply(that, args);
};


/***/ }),
/* 97 */
/***/ (function(module, exports, __webpack_require__) {

var global = __webpack_require__(2);
var macrotask = __webpack_require__(61).set;
var Observer = global.MutationObserver || global.WebKitMutationObserver;
var process = global.process;
var Promise = global.Promise;
var isNode = __webpack_require__(20)(process) == 'process';

module.exports = function () {
  var head, last, notify;

  var flush = function () {
    var parent, fn;
    if (isNode && (parent = process.domain)) parent.exit();
    while (head) {
      fn = head.fn;
      head = head.next;
      try {
        fn();
      } catch (e) {
        if (head) notify();
        else last = undefined;
        throw e;
      }
    } last = undefined;
    if (parent) parent.enter();
  };

  // Node.js
  if (isNode) {
    notify = function () {
      process.nextTick(flush);
    };
  // browsers with MutationObserver, except iOS Safari - https://github.com/zloirock/core-js/issues/339
  } else if (Observer && !(global.navigator && global.navigator.standalone)) {
    var toggle = true;
    var node = document.createTextNode('');
    new Observer(flush).observe(node, { characterData: true }); // eslint-disable-line no-new
    notify = function () {
      node.data = toggle = !toggle;
    };
  // environments with maybe non-completely correct, but existent Promise
  } else if (Promise && Promise.resolve) {
    var promise = Promise.resolve();
    notify = function () {
      promise.then(flush);
    };
  // for other environments - macrotask based on:
  // - setImmediate
  // - MessageChannel
  // - window.postMessag
  // - onreadystatechange
  // - setTimeout
  } else {
    notify = function () {
      // strange IE + webpack dev server bug - use .call(global)
      macrotask.call(global, flush);
    };
  }

  return function (fn) {
    var task = { fn: fn, next: undefined };
    if (last) last.next = task;
    if (!head) {
      head = task;
      notify();
    } last = task;
  };
};


/***/ }),
/* 98 */
/***/ (function(module, exports, __webpack_require__) {

var hide = __webpack_require__(9);
module.exports = function (target, src, safe) {
  for (var key in src) {
    if (safe && target[key]) target[key] = src[key];
    else hide(target, key, src[key]);
  } return target;
};


/***/ }),
/* 99 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

var global = __webpack_require__(2);
var core = __webpack_require__(3);
var dP = __webpack_require__(10);
var DESCRIPTORS = __webpack_require__(12);
var SPECIES = __webpack_require__(4)('species');

module.exports = function (KEY) {
  var C = typeof core[KEY] == 'function' ? core[KEY] : global[KEY];
  if (DESCRIPTORS && C && !C[SPECIES]) dP.f(C, SPECIES, {
    configurable: true,
    get: function () { return this; }
  });
};


/***/ }),
/* 100 */
/***/ (function(module, exports, __webpack_require__) {

var ITERATOR = __webpack_require__(4)('iterator');
var SAFE_CLOSING = false;

try {
  var riter = [7][ITERATOR]();
  riter['return'] = function () { SAFE_CLOSING = true; };
  // eslint-disable-next-line no-throw-literal
  Array.from(riter, function () { throw 2; });
} catch (e) { /* empty */ }

module.exports = function (exec, skipClosing) {
  if (!skipClosing && !SAFE_CLOSING) return false;
  var safe = false;
  try {
    var arr = [7];
    var iter = arr[ITERATOR]();
    iter.next = function () { return { done: safe = true }; };
    arr[ITERATOR] = function () { return iter; };
    exec(arr);
  } catch (e) { /* empty */ }
  return safe;
};


/***/ }),
/* 101 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";
// https://github.com/tc39/proposal-promise-finally

var $export = __webpack_require__(6);
var core = __webpack_require__(3);
var global = __webpack_require__(2);
var speciesConstructor = __webpack_require__(60);
var promiseResolve = __webpack_require__(63);

$export($export.P + $export.R, 'Promise', { 'finally': function (onFinally) {
  var C = speciesConstructor(this, core.Promise || global.Promise);
  var isFunction = typeof onFinally == 'function';
  return this.then(
    isFunction ? function (x) {
      return promiseResolve(C, onFinally()).then(function () { return x; });
    } : onFinally,
    isFunction ? function (e) {
      return promiseResolve(C, onFinally()).then(function () { throw e; });
    } : onFinally
  );
} });


/***/ }),
/* 102 */
/***/ (function(module, exports, __webpack_require__) {

"use strict";

// https://github.com/tc39/proposal-promise-try
var $export = __webpack_require__(6);
var newPromiseCapability = __webpack_require__(41);
var perform = __webpack_require__(62);

$export($export.S, 'Promise', { 'try': function (callbackfn) {
  var promiseCapability = newPromiseCapability.f(this);
  var result = perform(callbackfn);
  (result.e ? promiseCapability.reject : promiseCapability.resolve)(result.v);
  return promiseCapability.promise;
} });


/***/ }),
/* 103 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(104);
module.exports = __webpack_require__(3).Object.keys;


/***/ }),
/* 104 */
/***/ (function(module, exports, __webpack_require__) {

// 19.1.2.14 Object.keys(O)
var toObject = __webpack_require__(36);
var $keys = __webpack_require__(19);

__webpack_require__(105)('keys', function () {
  return function keys(it) {
    return $keys(toObject(it));
  };
});


/***/ }),
/* 105 */
/***/ (function(module, exports, __webpack_require__) {

// most Object methods by ES6 should accept primitives
var $export = __webpack_require__(6);
var core = __webpack_require__(3);
var fails = __webpack_require__(14);
module.exports = function (KEY, exec) {
  var fn = (core.Object || {})[KEY] || Object[KEY];
  var exp = {};
  exp[KEY] = exec(fn);
  $export($export.S + $export.F * fails(function () { fn(1); }), 'Object', exp);
};


/***/ }),
/* 106 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = { "default": __webpack_require__(107), __esModule: true };

/***/ }),
/* 107 */
/***/ (function(module, exports, __webpack_require__) {

var core = __webpack_require__(3);
var $JSON = core.JSON || (core.JSON = { stringify: JSON.stringify });
module.exports = function stringify(it) { // eslint-disable-line no-unused-vars
  return $JSON.stringify.apply($JSON, arguments);
};


/***/ }),
/* 108 */
/***/ (function(module, exports, __webpack_require__) {

/*!
 * Vue-Lazyload.js v1.1.4
 * (c) 2017 Awe <hilongjw@gmail.com>
 * Released under the MIT License.
 */
!function(e,t){ true?module.exports=t():"function"==typeof define&&define.amd?define(t):e.VueLazyload=t()}(this,function(){"use strict";function e(e,t){if(e.length){var n=e.indexOf(t);return n>-1?e.splice(n,1):void 0}}function t(e,t){if(!e||!t)return e||{};if(e instanceof Object)for(var n in t)e[n]=t[n];return e}function n(e,t){for(var n=!1,r=0,i=e.length;r<i;r++)if(t(e[r])){n=!0;break}return n}function r(e,t){if("IMG"===e.tagName&&e.getAttribute("data-srcset")){var n=e.getAttribute("data-srcset"),r=[],i=e.parentNode,o=i.offsetWidth*t,s=void 0,a=void 0,u=void 0;n=n.trim().split(","),n.map(function(e){e=e.trim(),s=e.lastIndexOf(" "),-1===s?(a=e,u=999998):(a=e.substr(0,s),u=parseInt(e.substr(s+1,e.length-s-2),10)),r.push([u,a])}),r.sort(function(e,t){if(e[0]<t[0])return-1;if(e[0]>t[0])return 1;if(e[0]===t[0]){if(-1!==t[1].indexOf(".webp",t[1].length-5))return 1;if(-1!==e[1].indexOf(".webp",e[1].length-5))return-1}return 0});for(var l="",d=void 0,c=r.length,h=0;h<c;h++)if(d=r[h],d[0]>=o){l=d[1];break}return l}}function i(e,t){for(var n=void 0,r=0,i=e.length;r<i;r++)if(t(e[r])){n=e[r];break}return n}function o(){if(!h)return!1;var e=!0,t=document;try{var n=t.createElement("object");n.type="image/webp",n.style.visibility="hidden",n.innerHTML="!",t.body.appendChild(n),e=!n.offsetWidth,t.body.removeChild(n)}catch(t){e=!1}return e}function s(e,t){var n=null,r=0;return function(){if(!n){var i=Date.now()-r,o=this,s=arguments,a=function(){r=Date.now(),n=!1,e.apply(o,s)};i>=t?a():n=setTimeout(a,t)}}}function a(e){return null!==e&&"object"===(void 0===e?"undefined":c(e))}function u(e){if(!(e instanceof Object))return[];if(Object.keys)return Object.keys(e);var t=[];for(var n in e)e.hasOwnProperty(n)&&t.push(n);return t}function l(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}function d(e,t){if(!(e instanceof t))throw new TypeError("Cannot call a class as a function")}var c="function"==typeof Symbol&&"symbol"==typeof Symbol.iterator?function(e){return typeof e}:function(e){return e&&"function"==typeof Symbol&&e.constructor===Symbol&&e!==Symbol.prototype?"symbol":typeof e},h="undefined"!=typeof window,f=h&&"IntersectionObserver"in window,v={event:"event",observer:"observer"},p=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:1;return h&&window.devicePixelRatio||e},g=function(){if(h){var e=!1;try{var t=Object.defineProperty({},"passive",{get:function(){e=!0}});window.addEventListener("test",null,t)}catch(e){}return e}}(),y={on:function(e,t,n){var r=arguments.length>3&&void 0!==arguments[3]&&arguments[3];g?e.addEventListener(t,n,{capture:r,passive:!0}):e.addEventListener(t,n,r)},off:function(e,t,n){var r=arguments.length>3&&void 0!==arguments[3]&&arguments[3];e.removeEventListener(t,n,r)}},b=function(e,t,n){var r=new Image;r.src=e.src,r.onload=function(){t({naturalHeight:r.naturalHeight,naturalWidth:r.naturalWidth,src:r.src})},r.onerror=function(e){n(e)}},m=function(e,t){return"undefined"!=typeof getComputedStyle?getComputedStyle(e,null).getPropertyValue(t):e.style[t]},L=function(e){return m(e,"overflow")+m(e,"overflow-y")+m(e,"overflow-x")},w=function(e){if(h){if(!(e instanceof HTMLElement))return window;for(var t=e;t&&t!==document.body&&t!==document.documentElement&&t.parentNode;){if(/(scroll|auto)/.test(L(t)))return t;t=t.parentNode}return window}},_=function(){function e(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}return function(t,n,r){return n&&e(t.prototype,n),r&&e(t,r),t}}(),k={},E=function(){function e(t){var n=t.el,r=t.src,i=t.error,o=t.loading,s=t.bindType,a=t.$parent,u=t.options,d=t.elRenderer;l(this,e),this.el=n,this.src=r,this.error=i,this.loading=o,this.bindType=s,this.attempt=0,this.naturalHeight=0,this.naturalWidth=0,this.options=u,this.filter(),this.initState(),this.performanceData={init:Date.now(),loadStart:null,loadEnd:null},this.rect=n.getBoundingClientRect(),this.$parent=a,this.elRenderer=d,this.render("loading",!1)}return _(e,[{key:"initState",value:function(){this.state={error:!1,loaded:!1,rendered:!1}}},{key:"record",value:function(e){this.performanceData[e]=Date.now()}},{key:"update",value:function(e){var t=e.src,n=e.loading,r=e.error,i=this.src;this.src=t,this.loading=n,this.error=r,this.filter(),i!==this.src&&(this.attempt=0,this.initState())}},{key:"getRect",value:function(){this.rect=this.el.getBoundingClientRect()}},{key:"checkInView",value:function(){return this.getRect(),this.rect.top<window.innerHeight*this.options.preLoad&&this.rect.bottom>this.options.preLoadTop&&this.rect.left<window.innerWidth*this.options.preLoad&&this.rect.right>0}},{key:"filter",value:function(){var e=this;u(this.options.filter).map(function(t){e.options.filter[t](e,e.options)})}},{key:"renderLoading",value:function(e){var t=this;b({src:this.loading},function(n){t.render("loading",!1),e()},function(n){e(),t.options.silent||console.warn("VueLazyload log: load failed with loading image("+t.loading+")")})}},{key:"load",value:function(){var e=this;return this.attempt>this.options.attempt-1&&this.state.error?void(this.options.silent||console.log("VueLazyload log: "+this.src+" tried too more than "+this.options.attempt+" times")):this.state.loaded||k[this.src]?this.render("loaded",!0):void this.renderLoading(function(){e.attempt++,e.record("loadStart"),b({src:e.src},function(t){e.naturalHeight=t.naturalHeight,e.naturalWidth=t.naturalWidth,e.state.loaded=!0,e.state.error=!1,e.record("loadEnd"),e.render("loaded",!1),k[e.src]=1},function(t){e.state.error=!0,e.state.loaded=!1,e.render("error",!1)})})}},{key:"render",value:function(e,t){this.elRenderer(this,e,t)}},{key:"performance",value:function(){var e="loading",t=0;return this.state.loaded&&(e="loaded",t=(this.performanceData.loadEnd-this.performanceData.loadStart)/1e3),this.state.error&&(e="error"),{src:this.src,state:e,time:t}}},{key:"destroy",value:function(){this.el=null,this.src=null,this.error=null,this.loading=null,this.bindType=null,this.attempt=0}}]),e}(),T=function(){function e(e,t){for(var n=0;n<t.length;n++){var r=t[n];r.enumerable=r.enumerable||!1,r.configurable=!0,"value"in r&&(r.writable=!0),Object.defineProperty(e,r.key,r)}}return function(t,n,r){return n&&e(t.prototype,n),r&&e(t,r),t}}(),A="data:image/gif;base64,R0lGODlhAQABAIAAAAAAAP///yH5BAEAAAAALAAAAAABAAEAAAIBRAA7",$=["scroll","wheel","mousewheel","resize","animationend","transitionend","touchmove"],z={rootMargin:"0px",threshold:0},H=function(u){return function(){function l(e){var t=e.preLoad,n=e.error,r=e.throttleWait,i=e.preLoadTop,a=e.dispatchEvent,u=e.loading,c=e.attempt,h=e.silent,f=e.scale,g=e.listenEvents,y=(e.hasbind,e.filter),b=e.adapter,m=e.observer,L=e.observerOptions;d(this,l),this.version="1.1.4",this.mode=v.event,this.ListenerQueue=[],this.TargetIndex=0,this.TargetQueue=[],this.options={silent:h||!0,dispatchEvent:!!a,throttleWait:r||200,preLoad:t||1.3,preLoadTop:i||0,error:n||A,loading:u||A,attempt:c||3,scale:f||p(f),ListenEvents:g||$,hasbind:!1,supportWebp:o(),filter:y||{},adapter:b||{},observer:!!m,observerOptions:L||z},this._initEvent(),this.lazyLoadHandler=s(this._lazyLoadHandler.bind(this),this.options.throttleWait),this.setMode(this.options.observer?v.observer:v.event)}return T(l,[{key:"config",value:function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:{};t(this.options,e)}},{key:"performance",value:function(){var e=[];return this.ListenerQueue.map(function(t){e.push(t.performance())}),e}},{key:"addLazyBox",value:function(e){this.ListenerQueue.push(e),h&&(this._addListenerTarget(window),this._observer&&this._observer.observe(e.el),e.$el&&e.$el.parentNode&&this._addListenerTarget(e.$el.parentNode))}},{key:"add",value:function(e,t,i){var o=this;if(n(this.ListenerQueue,function(t){return t.el===e}))return this.update(e,t),u.nextTick(this.lazyLoadHandler);var s=this._valueFormatter(t.value),a=s.src,l=s.loading,d=s.error;u.nextTick(function(){a=r(e,o.options.scale)||a,o._observer&&o._observer.observe(e);var n=Object.keys(t.modifiers)[0],s=void 0;n&&(s=i.context.$refs[n],s=s?s.$el||s:document.getElementById(n)),s||(s=w(e));var c=new E({bindType:t.arg,$parent:s,el:e,loading:l,error:d,src:a,elRenderer:o._elRenderer.bind(o),options:o.options});o.ListenerQueue.push(c),h&&(o._addListenerTarget(window),o._addListenerTarget(s)),o.lazyLoadHandler(),u.nextTick(function(){return o.lazyLoadHandler()})})}},{key:"update",value:function(e,t){var n=this,o=this._valueFormatter(t.value),s=o.src,a=o.loading,l=o.error;s=r(e,this.options.scale)||s;var d=i(this.ListenerQueue,function(t){return t.el===e});d&&d.update({src:s,loading:a,error:l}),this._observer&&this._observer.observe(e),this.lazyLoadHandler(),u.nextTick(function(){return n.lazyLoadHandler()})}},{key:"remove",value:function(t){if(t){this._observer&&this._observer.unobserve(t);var n=i(this.ListenerQueue,function(e){return e.el===t});n&&(this._removeListenerTarget(n.$parent),this._removeListenerTarget(window),e(this.ListenerQueue,n)&&n.destroy())}}},{key:"removeComponent",value:function(t){t&&(e(this.ListenerQueue,t),this._observer&&this._observer.unobserve(t.el),t.$parent&&t.$el.parentNode&&this._removeListenerTarget(t.$el.parentNode),this._removeListenerTarget(window))}},{key:"setMode",value:function(e){var t=this;f||e!==v.observer||(e=v.event),this.mode=e,e===v.event?(this._observer&&(this.ListenerQueue.forEach(function(e){t._observer.unobserve(e.el)}),this._observer=null),this.TargetQueue.forEach(function(e){t._initListen(e.el,!0)})):(this.TargetQueue.forEach(function(e){t._initListen(e.el,!1)}),this._initIntersectionObserver())}},{key:"_addListenerTarget",value:function(e){if(e){var t=i(this.TargetQueue,function(t){return t.el===e});return t?t.childrenCount++:(t={el:e,id:++this.TargetIndex,childrenCount:1,listened:!0},this.mode===v.event&&this._initListen(t.el,!0),this.TargetQueue.push(t)),this.TargetIndex}}},{key:"_removeListenerTarget",value:function(e){var t=this;this.TargetQueue.forEach(function(n,r){n.el===e&&(--n.childrenCount||(t._initListen(n.el,!1),t.TargetQueue.splice(r,1),n=null))})}},{key:"_initListen",value:function(e,t){var n=this;this.options.ListenEvents.forEach(function(r){return y[t?"on":"off"](e,r,n.lazyLoadHandler)})}},{key:"_initEvent",value:function(){var t=this;this.Event={listeners:{loading:[],loaded:[],error:[]}},this.$on=function(e,n){t.Event.listeners[e].push(n)},this.$once=function(e,n){function r(){i.$off(e,r),n.apply(i,arguments)}var i=t;t.$on(e,r)},this.$off=function(n,r){if(!r)return void(t.Event.listeners[n]=[]);e(t.Event.listeners[n],r)},this.$emit=function(e,n,r){t.Event.listeners[e].forEach(function(e){return e(n,r)})}}},{key:"_lazyLoadHandler",value:function(){var e=!1;this.ListenerQueue.forEach(function(t){t.state.loaded||(e=t.checkInView())&&t.load()})}},{key:"_initIntersectionObserver",value:function(){var e=this;f&&(this._observer=new IntersectionObserver(this._observerHandler.bind(this),this.options.observerOptions),this.ListenerQueue.length&&this.ListenerQueue.forEach(function(t){e._observer.observe(t.el)}))}},{key:"_observerHandler",value:function(e,t){var n=this;e.forEach(function(e){e.isIntersecting&&n.ListenerQueue.forEach(function(t){if(t.el===e.target){if(t.state.loaded)return n._observer.unobserve(t.el);t.load()}})})}},{key:"_elRenderer",value:function(e,t,n){if(e.el){var r=e.el,i=e.bindType,o=void 0;switch(t){case"loading":o=e.loading;break;case"error":o=e.error;break;default:o=e.src}if(i?r.style[i]="url("+o+")":r.getAttribute("src")!==o&&r.setAttribute("src",o),r.setAttribute("lazy",t),this.$emit(t,e,n),this.options.adapter[t]&&this.options.adapter[t](e,this.options),this.options.dispatchEvent){var s=new CustomEvent(t,{detail:e});r.dispatchEvent(s)}}}},{key:"_valueFormatter",value:function(e){var t=e,n=this.options.loading,r=this.options.error;return a(e)&&(e.src||this.options.silent||console.error("Vue Lazyload warning: miss src with "+e),t=e.src,n=e.loading||this.options.loading,r=e.error||this.options.error),{src:t,loading:n,error:r}}}]),l}()},O=function(e){return{props:{tag:{type:String,default:"div"}},render:function(e){return!1===this.show?e(this.tag):e(this.tag,null,this.$slots.default)},data:function(){return{el:null,state:{loaded:!1},rect:{},show:!1}},mounted:function(){this.el=this.$el,e.addLazyBox(this),e.lazyLoadHandler()},beforeDestroy:function(){e.removeComponent(this)},methods:{getRect:function(){this.rect=this.$el.getBoundingClientRect()},checkInView:function(){return this.getRect(),h&&this.rect.top<window.innerHeight*e.options.preLoad&&this.rect.bottom>0&&this.rect.left<window.innerWidth*e.options.preLoad&&this.rect.right>0},load:function(){this.show=!0,this.state.loaded=!0,this.$emit("show",this)}}}};return{install:function(e){var n=arguments.length>1&&void 0!==arguments[1]?arguments[1]:{},r=H(e),i=new r(n),o="2"===e.version.split(".")[0];e.prototype.$Lazyload=i,n.lazyComponent&&e.component("lazy-component",O(i)),o?e.directive("lazy",{bind:i.add.bind(i),update:i.update.bind(i),componentUpdated:i.lazyLoadHandler.bind(i),unbind:i.remove.bind(i)}):e.directive("lazy",{bind:i.lazyLoadHandler.bind(i),update:function(e,n){t(this.vm.$refs,this.vm.$els),i.add(this.el,{modifiers:this.modifiers||{},arg:this.arg,value:e,oldValue:n},{context:this.vm})},unbind:function(){i.remove(this.el)}})}}});


/***/ }),
/* 109 */
/***/ (function(module, exports, __webpack_require__) {

module.exports = { "default": __webpack_require__(110), __esModule: true };

/***/ }),
/* 110 */
/***/ (function(module, exports, __webpack_require__) {

__webpack_require__(111);
module.exports = __webpack_require__(3).Number.isNaN;


/***/ }),
/* 111 */
/***/ (function(module, exports, __webpack_require__) {

// 20.1.2.4 Number.isNaN(number)
var $export = __webpack_require__(6);

$export($export.S, 'Number', {
  isNaN: function isNaN(number) {
    // eslint-disable-next-line no-self-compare
    return number != number;
  }
});


/***/ }),
/* 112 */
/***/ (function(module, __webpack_exports__, __webpack_require__) {

"use strict";
/* WEBPACK VAR INJECTION */(function(global) {/* harmony export (immutable) */ __webpack_exports__["a"] = raf;
/* unused harmony export cancel */
/* harmony import */ var __WEBPACK_IMPORTED_MODULE_0__index__ = __webpack_require__(1);
/**
 * requestAnimationFrame polyfill
 */



var prev = Date.now();

/* istanbul ignore next */
function fallback(fn) {
  var curr = Date.now();
  var ms = Math.max(0, 16 - (curr - prev));
  var id = setTimeout(fn, ms);
  prev = curr + ms;
  return id;
}

/* istanbul ignore next */
var root = __WEBPACK_IMPORTED_MODULE_0__index__["f" /* isServer */] ? global : window;

/* istanbul ignore next */
var iRaf = root.requestAnimationFrame || root.webkitRequestAnimationFrame || fallback;

/* istanbul ignore next */
var iCancel = root.cancelAnimationFrame || root.webkitCancelAnimationFrame || root.clearTimeout;

function raf(fn) {
  return iRaf.call(root, fn);
}

function cancel(id) {
  iCancel.call(root, id);
}
/* WEBPACK VAR INJECTION */}.call(__webpack_exports__, __webpack_require__(113)))

/***/ }),
/* 113 */
/***/ (function(module, exports) {

var g;

// This works in non-strict mode
g = (function() {
	return this;
})();

try {
	// This works if eval is allowed (see CSP)
	g = g || Function("return this")() || (1,eval)("this");
} catch(e) {
	// This works if the window reference is available
	if(typeof window === "object")
		g = window;
}

// g can still be undefined, but nothing to do about it...
// We return undefined, instead of nothing here, so it's
// easier to handle this case. if(!global) { ...}

module.exports = g;


/***/ })
/******/ ]);
});