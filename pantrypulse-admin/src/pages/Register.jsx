// Register.jsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';
import { useGoogleLogin } from '@react-oauth/google';

export default function Register() {
  // ✅ FIX: field is 'password' (matches @Transient field in User.java)
  const [formData, setFormData] = useState({ firstName: '', lastName: '', email: '', password: '' });
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isGoogleLoading, setIsGoogleLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    if (message) setMessage('');
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const response = await axios.post('http://localhost:8080/api/auth/register', formData);
      setMessage(response.data);
      setIsError(false);
      setTimeout(() => navigate('/login'), 3000);
    } catch (error) {
      setMessage(error.response?.data || 'Registration failed');
      setIsError(true);
    } finally {
      setIsLoading(false);
    }
  };

  const handleGoogleRegister = useGoogleLogin({
    onSuccess: async (tokenResponse) => {
      setIsGoogleLoading(true);
      try {
        const response = await axios.post('http://localhost:8080/api/auth/google', {
          accessToken: tokenResponse.access_token,
        });
        setMessage('Google account linked! Redirecting to login...');
        setIsError(false);
        localStorage.setItem('token', response.data.token);
        localStorage.setItem('email', response.data.email);
        setTimeout(() => navigate('/dashboard'), 1500);
      } catch (error) {
        setMessage(error.response?.data || 'Google sign-up failed');
        setIsError(true);
      } finally {
        setIsGoogleLoading(false);
      }
    },
    onError: () => {
      setMessage('Google sign-in was cancelled or failed');
      setIsError(true);
    },
  });

  return (
    <div style={styles.container}>
      <div style={styles.cardWrapper}>
        <div style={styles.brandHeader}>
          <div style={styles.logoIcon}>
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
              <path d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z"/>
            </svg>
          </div>
          <h1 style={styles.brandTitle}>PantryPulse</h1>
          <p style={styles.brandSubtitle}>Admin Portal</p>
        </div>

        <div style={styles.card}>
          <div style={styles.gradientBar}></div>
          <div style={styles.cardContent}>
            <h2 style={styles.welcomeTitle}>Create Account</h2>
            <p style={styles.subtitle}>Join PantryPulse to manage your inventory</p>

            {/* Google Sign-Up Button */}
            <button
              type="button"
              onClick={() => handleGoogleRegister()}
              disabled={isGoogleLoading}
              style={isGoogleLoading ? {...styles.googleButton, ...styles.buttonDisabled} : styles.googleButton}
            >
              {isGoogleLoading ? (
                <>
                  <svg style={styles.spinner} width="20" height="20" viewBox="0 0 24 24" fill="none">
                    <circle cx="12" cy="12" r="10" stroke="#374151" strokeWidth="4" opacity="0.25"/>
                    <path fill="#374151" opacity="0.75" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                  </svg>
                  Connecting to Google...
                </>
              ) : (
                <>
                  <GoogleIcon />
                  Sign up with Google
                </>
              )}
            </button>

            {/* Divider */}
            <div style={styles.divider}>
              <div style={styles.dividerLine}></div>
              <span style={styles.dividerText}>or register with email</span>
              <div style={styles.dividerLine}></div>
            </div>

            <form onSubmit={handleRegister} style={styles.form}>
              <div style={styles.nameRow}>
                <div style={styles.inputGroup}>
                  <label style={styles.label}>First Name</label>
                  <input
                    type="text"
                    name="firstName"
                    onChange={handleChange}
                    required
                    style={styles.input}
                    placeholder="John"
                  />
                </div>
                <div style={styles.inputGroup}>
                  <label style={styles.label}>Last Name</label>
                  <input
                    type="text"
                    name="lastName"
                    onChange={handleChange}
                    required
                    style={styles.input}
                    placeholder="Doe"
                  />
                </div>
              </div>

              <div style={styles.inputGroup}>
                <label style={styles.label}>Email Address</label>
                <div style={styles.inputWrapper}>
                  <span style={styles.atIcon}>@</span>
                  <input
                    type="email"
                    name="email"
                    onChange={handleChange}
                    required
                    style={{...styles.input, paddingLeft: '40px'}}
                    placeholder="john@company.com"
                  />
                </div>
              </div>

              <div style={styles.inputGroup}>
                <label style={styles.label}>Password</label>
                <div style={styles.inputWrapper}>
                  <svg style={styles.inputIcon} width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" strokeWidth="2">
                    <path d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                  </svg>
                  {/* ✅ FIX: name="password" matches the @Transient field in User.java */}
                  <input
                    type="password"
                    name="password"
                    onChange={handleChange}
                    required
                    style={{...styles.input, paddingLeft: '40px'}}
                    placeholder="Min 8 characters"
                  />
                </div>
                <p style={styles.hint}>Must contain at least 8 characters</p>
              </div>

              <button type="submit" disabled={isLoading} style={isLoading ? {...styles.button, ...styles.buttonDisabled} : styles.button}>
                {isLoading ? (
                  <>
                    <svg style={styles.spinner} width="20" height="20" viewBox="0 0 24 24" fill="none">
                      <circle cx="12" cy="12" r="10" stroke="white" strokeWidth="4" opacity="0.25"/>
                      <path fill="white" opacity="0.75" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                    </svg>
                    Creating account...
                  </>
                ) : 'Create Account'}
              </button>
            </form>

            {message && (
              <div style={isError ? {...styles.alert, ...styles.alertError} : {...styles.alert, ...styles.alertSuccess}}>
                <div style={isError ? {...styles.alertIcon, backgroundColor: '#EF4444'} : {...styles.alertIcon, backgroundColor: '#10B981'}}>
                  <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="3">
                    {isError ? <path d="M6 18L18 6M6 6l12 12"/> : <path d="M5 13l4 4L19 7"/>}
                  </svg>
                </div>
                <p style={isError ? styles.alertTextError : styles.alertTextSuccess}>{message}</p>
              </div>
            )}

            <div style={styles.footer}>
              <p style={styles.footerText}>
                Already have an account?{' '}
                <Link to="/login" style={styles.link}>Sign in here</Link>
              </p>
            </div>
          </div>
        </div>

        <p style={styles.copyright}>© 2026 PantryPulse. All rights reserved.</p>
      </div>
    </div>
  );
}

function GoogleIcon() {
  return (
    <svg width="20" height="20" viewBox="0 0 24 24">
      <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
      <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
      <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
      <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
    </svg>
  );
}

const styles = {
  container: {
    minHeight: '100vh',
    background: 'linear-gradient(135deg, #F9FAFB 0%, #F0FDF4 100%)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    padding: '16px',
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
  },
  cardWrapper: {
    width: '100%',
    maxWidth: '400px',
  },
  brandHeader: {
    textAlign: 'center',
    marginBottom: '32px',
  },
  logoIcon: {
    width: '64px',
    height: '64px',
    backgroundColor: '#2E7D32',
    borderRadius: '16px',
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    marginBottom: '16px',
    boxShadow: '0 10px 15px -3px rgba(46, 125, 50, 0.3)',
  },
  brandTitle: {
    fontSize: '30px',
    fontWeight: '700',
    color: '#111827',
    margin: '0 0 8px 0',
    letterSpacing: '-0.025em',
  },
  brandSubtitle: {
    fontSize: '14px',
    color: '#6B7280',
    margin: 0,
  },
  card: {
    backgroundColor: 'white',
    borderRadius: '16px',
    boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    border: '1px solid #F3F4F6',
    overflow: 'hidden',
  },
  gradientBar: {
    height: '4px',
    background: 'linear-gradient(90deg, #2E7D32 0%, #81C784 50%, #10B981 100%)',
  },
  cardContent: {
    padding: '32px',
  },
  welcomeTitle: {
    fontSize: '24px',
    fontWeight: '600',
    color: '#1F2937',
    margin: '0 0 4px 0',
  },
  subtitle: {
    fontSize: '14px',
    color: '#6B7280',
    margin: '0 0 20px 0',
  },
  googleButton: {
    width: '100%',
    backgroundColor: 'white',
    color: '#374151',
    fontWeight: '600',
    padding: '12px',
    borderRadius: '12px',
    border: '1px solid #E5E7EB',
    fontSize: '14px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '10px',
    boxShadow: '0 1px 3px rgba(0,0,0,0.08)',
    transition: 'all 0.2s',
    boxSizing: 'border-box',
  },
  divider: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    margin: '20px 0',
  },
  dividerLine: {
    flex: 1,
    height: '1px',
    backgroundColor: '#E5E7EB',
  },
  dividerText: {
    fontSize: '13px',
    color: '#9CA3AF',
    whiteSpace: 'nowrap',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px',
  },
  nameRow: {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '16px',
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column',
  },
  label: {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
    marginBottom: '8px',
  },
  inputWrapper: {
    position: 'relative',
  },
  atIcon: {
    position: 'absolute',
    left: '14px',
    top: '50%',
    transform: 'translateY(-50%)',
    color: '#9CA3AF',
    fontSize: '16px',
    fontWeight: '600',
    pointerEvents: 'none',
  },
  inputIcon: {
    position: 'absolute',
    left: '12px',
    top: '50%',
    transform: 'translateY(-50%)',
    pointerEvents: 'none',
  },
  input: {
    width: '100%',
    padding: '12px',
    border: '1px solid #E5E7EB',
    borderRadius: '12px',
    fontSize: '14px',
    backgroundColor: '#F9FAFB',
    boxSizing: 'border-box',
    transition: 'all 0.2s',
    outline: 'none',
  },
  hint: {
    fontSize: '12px',
    color: '#9CA3AF',
    margin: '4px 0 0 0',
  },
  button: {
    width: '100%',
    backgroundColor: '#2E7D32',
    color: 'white',
    fontWeight: '600',
    padding: '14px',
    borderRadius: '12px',
    border: 'none',
    fontSize: '15px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    gap: '8px',
    boxShadow: '0 4px 6px -1px rgba(46, 125, 50, 0.2)',
    transition: 'all 0.2s',
    marginTop: '8px',
  },
  buttonDisabled: {
    opacity: '0.5',
    cursor: 'not-allowed',
  },
  spinner: {
    animation: 'spin 1s linear infinite',
  },
  alert: {
    marginTop: '16px',
    padding: '16px',
    borderRadius: '12px',
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
  },
  alertError: {
    backgroundColor: '#FEF2F2',
    border: '1px solid #FEE2E2',
  },
  alertSuccess: {
    backgroundColor: '#F0FDF4',
    border: '1px solid #DCFCE7',
  },
  alertIcon: {
    width: '20px',
    height: '20px',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    flexShrink: 0,
  },
  alertTextError: {
    fontSize: '14px',
    fontWeight: '500',
    color: '#991B1B',
    margin: 0,
  },
  alertTextSuccess: {
    fontSize: '14px',
    fontWeight: '500',
    color: '#166534',
    margin: 0,
  },
  footer: {
    marginTop: '24px',
    textAlign: 'center',
  },
  footerText: {
    fontSize: '14px',
    color: '#6B7280',
    margin: 0,
  },
  link: {
    fontWeight: '600',
    color: '#2E7D32',
    textDecoration: 'none',
  },
  copyright: {
    textAlign: 'center',
    fontSize: '12px',
    color: '#9CA3AF',
    marginTop: '32px',
  },
};