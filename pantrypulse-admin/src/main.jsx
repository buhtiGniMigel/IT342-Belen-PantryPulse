import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { GoogleOAuthProvider } from '@react-oauth/google'

// ⚠️  Replace with your actual Google Client ID from console.cloud.google.com
const GOOGLE_CLIENT_ID = '139752023449-c3nslgtpghn017tgg1qd8ahs6ubkopao.apps.googleusercontent.com'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <GoogleOAuthProvider clientId={GOOGLE_CLIENT_ID}>
      <App />
    </GoogleOAuthProvider>
  </StrictMode>,
)
