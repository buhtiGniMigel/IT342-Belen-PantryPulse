// Login.jsx
import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import axios from 'axios';

export default function Login() {
  const [credentials, setCredentials] = useState({ email: '', password: '' });
  const [message, setMessage] = useState('');
  const [isError, setIsError] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
    if (message) setMessage('');
  };

  const handleLogin = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      const response = await axios.post('http://localhost:8080/api/auth/login', credentials);
      setMessage(response.data.message);
      setIsError(false);
      setTimeout(() => navigate('/dashboard'), 1500);
    } catch (error) {
      setMessage(error.response?.data || 'Invalid credentials');
      setIsError(true);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div style={styles.container}>
      <div style={styles.cardWrapper}>
        <div style={styles.brandHeader}>
          <div style={styles.logoIcon}>
            <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
              <path d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
            </svg>
          </div>
          <h1 style={styles.brandTitle}>PantryPulse</h1>
          <p style={styles.brandSubtitle}>Admin Portal</p>
        </div>

        <div style={styles.card}>
          <div style={styles.gradientBar}></div>
          <div style={styles.cardContent}>
            <h2 style={styles.welcomeTitle}>Welcome back</h2>
            
            <form onSubmit={handleLogin} style={styles.form}>
              <div style={styles.inputGroup}>
                <label style={styles.label}>Email Address</label>
                <div style={styles.inputWrapper}>
                  <svg style={styles.inputIcon} width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" strokeWidth="2">
                    <path d="M16 12a4 4 0 10-8 0 4 4 0 008 0zm0 0v1.5a2.5 2.5 0 005 0V12a9 9 0 10-9 9m4.5-1.206a8.959 8.959 0 01-4.5 1.207"/>
                  </svg>
                  <input
                    type="email"
                    name="email"
                    onChange={handleChange}
                    required
                    style={styles.input}
                    placeholder="admin@pantrypulse.com"
                  />
                </div>
              </div>

              <div style={styles.inputGroup}>
                <label style={styles.label}>Password</label>
                <div style={styles.inputWrapper}>
                  <svg style={styles.inputIcon} width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" strokeWidth="2">
                    <path d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                  </svg>
                  <input
                    type="password"
                    name="password"
                    onChange={handleChange}
                    required
                    style={styles.input}
                    placeholder="••••••••"
                  />
                </div>
              </div>

              <button type="submit" disabled={isLoading} style={isLoading ? {...styles.button, ...styles.buttonDisabled} : styles.button}>
                {isLoading ? (
                  <>
                    <svg style={styles.spinner} width="20" height="20" viewBox="0 0 24 24" fill="none">
                      <circle cx="12" cy="12" r="10" stroke="white" strokeWidth="4" opacity="0.25"/>
                      <path fill="white" opacity="0.75" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"/>
                    </svg>
                    Signing in...
                  </>
                ) : 'Sign In'}
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
                Don't have an account?{' '}
                <Link to="/register" style={styles.link}>Create one now</Link>
              </p>
            </div>
          </div>
        </div>

        <p style={styles.copyright}>© 2026 PantryPulse. All rights reserved.</p>
      </div>
    </div>
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
    margin: '0 0 24px 0',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '20px',
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
  inputIcon: {
    position: 'absolute',
    left: '12px',
    top: '50%',
    transform: 'translateY(-50%)',
    pointerEvents: 'none',
  },
  input: {
    width: '100%',
    padding: '12px 12px 12px 40px',
    border: '1px solid #E5E7EB',
    borderRadius: '12px',
    fontSize: '14px',
    backgroundColor: '#F9FAFB',
    boxSizing: 'border-box',
    transition: 'all 0.2s',
    outline: 'none',
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
    marginTop: '4px',
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
    alignItems: 'flex-start',
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