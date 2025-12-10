import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import { AuthProvider, type AuthProviderProps } from 'react-oidc-context';
import { WebStorageStateStore } from 'oidc-client-ts';

const oidcConfig : AuthProviderProps = {
  authority: "http://localhost:8443/realms/upcomingMovies", // keycloak realm url
  client_id: "upcominmovies-frontend", // client id
  redirect_uri: window.location.origin, // where to go after login keycloak
  post_logout_redirect_uri: window.location.origin,
  userStore: new WebStorageStateStore({ store: window.localStorage }),
  onSigninCallback: () => {
    //Remove the code from the URL after login to keep it clean! Comment it out to see the difference
    window.history.replaceState({}, document.title,window.location.pathname);
  }
}

// Note: Ensure your upcomingmovies-users client in Keycloak has http://localhost:5173 (or your app's URL) listed in "Valid Redirect URIs".

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AuthProvider {...oidcConfig}>
      <App />
    </AuthProvider>
  </StrictMode>,
)
