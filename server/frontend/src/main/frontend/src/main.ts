import 'brace/index';
import 'brace/theme/eclipse';
import 'brace/mode/typescript';
import 'brace/mode/javascript';
import 'brace/mode/sql';
import 'brace/mode/c_cpp';
import 'brace/ext/language_tools.js';
import './polyfills.ts';

import {platformBrowserDynamic} from '@angular/platform-browser-dynamic';
import {enableProdMode} from '@angular/core';
import {environment} from './environments/environment';
import {AppModule} from './app/';

declare var ace: any;

if (environment.production) {
    enableProdMode();
}

platformBrowserDynamic().bootstrapModule(AppModule)
    .catch(err => console.log(err));
