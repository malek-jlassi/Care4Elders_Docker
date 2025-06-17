import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import {provideAnimations} from '@angular/platform-browser/animations'

// Modify your app config to include HttpClient
const updatedConfig = {
  ...appConfig,
  providers: [
    ...(appConfig.providers || []),
    provideHttpClient(withInterceptorsFromDi()),
    provideAnimations(),
  ]
};

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));
