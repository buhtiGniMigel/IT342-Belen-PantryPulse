import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const navigate = useNavigate();
  const [view, setView] = useState('overview');
  const [stats, setStats] = useState({ users: 0, recipes: 0, logs: 0 });
  const [listData, setListData] = useState([]);
  const [recipeForm, setRecipeForm] = useState({ title: '', keyIngredients: '', instructions: '' });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [selectedRecipe, setSelectedRecipe] = useState(null);

  useEffect(() => { 
    fetchStats();
    if (view !== 'overview') fetchList();
  }, [view]);

  const fetchStats = () => {
    fetch("http://localhost:8080/api/admin/stats")
      .then(res => res.json())
      .then(setStats)
      .catch(err => console.error("Stats fetch error:", err));
  };

  const fetchList = () => {
    fetch(`http://localhost:8080/api/admin/${view}`)
      .then(res => res.json())
      .then(data => setListData(Array.isArray(data) ? data : []))
      .catch(err => {
        console.error(`${view} fetch error:`, err);
        setListData([]);
      });
  };

  const handleRecipeSubmit = (e) => {
    e.preventDefault();
    setIsSubmitting(true);
    fetch("http://localhost:8080/api/admin/recipes", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(recipeForm)
    }).then((res) => {
      if (res.ok) {
        setRecipeForm({ title: '', keyIngredients: '', instructions: '' });
        if (view === 'recipes') fetchList();
        fetchStats();
      }
    }).catch(err => alert("Backend connection failed. Check if Spring Boot is running."))
    .finally(() => setIsSubmitting(false));
  };

  const handleLogout = () => {
    navigate('/login');
  };

  const navItems = [
    { id: 'overview', label: 'Overview', icon: 'M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z' },
    { id: 'recipes', label: 'Recipe CMS', icon: 'M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253' },
    { id: 'users', label: 'User Activity', icon: 'M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z' },
  ];

  return (
    <div style={styles.container}>
      {/* Sidebar */}
      <aside style={styles.sidebar}>
        <div style={styles.brand}>
          <div style={styles.logoIcon}>
            <svg width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="white" strokeWidth="2">
              <path d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"/>
            </svg>
          </div>
          <h1 style={styles.brandTitle}>PantryPulse</h1>
        </div>

        <nav style={styles.nav}>
          {navItems.map(item => (
            <button
              key={item.id}
              onClick={() => setView(item.id)}
              style={{
                ...styles.navButton,
                ...(view === item.id ? styles.navButtonActive : {})
              }}
            >
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" style={{ flexShrink: 0 }}>
                <path d={item.icon}/>
              </svg>
              {item.label}
            </button>
          ))}
        </nav>

        <button onClick={handleLogout} style={styles.logoutButton}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
            <path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
          </svg>
          Logout
        </button>
      </aside>

      {/* Main Content */}
      <main style={styles.main}>
        {view === 'overview' && (
          <div style={styles.contentWrapper}>
            <h2 style={styles.pageTitle}>Dashboard Overview</h2>
            <div style={styles.statsGrid}>
              <div style={styles.statCard}>
                <div style={styles.statIcon}>
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2E7D32" strokeWidth="2">
                    <path d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z"/>
                  </svg>
                </div>
                <div>
                  <p style={styles.statLabel}>Total Users</p>
                  <p style={styles.statValue}>{stats.users}</p>
                </div>
              </div>
              <div style={styles.statCard}>
                <div style={styles.statIcon}>
                  <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#2E7D32" strokeWidth="2">
                    <path d="M12 6.253v13m0-13C10.832 5.477 9.246 5 7.5 5S4.168 5.477 3 6.253v13C4.168 18.477 5.754 18 7.5 18s3.332.477 4.5 1.253m0-13C13.168 5.477 14.754 5 16.5 5c1.747 0 3.332.477 4.5 1.253v13C19.832 18.477 18.247 18 16.5 18c-1.746 0-3.332.477-4.5 1.253"/>
                  </svg>
                </div>
                <div>
                  <p style={styles.statLabel}>Recipes</p>
                  <p style={styles.statValue}>{stats.recipes}</p>
                </div>
              </div>
            </div>
          </div>
        )}

        {view === 'recipes' && (
          <div style={styles.contentWrapper}>
            <h2 style={styles.pageTitle}>Recipe Management</h2>

            {/* Create Recipe Form */}
            <div style={styles.card}>
              <h3 style={styles.cardTitle}>Create New Recipe</h3>
              <form onSubmit={handleRecipeSubmit} style={styles.form}>
                <div style={styles.formGrid}>
                  <div style={styles.inputGroup}>
                    <label style={styles.label}>Recipe Title</label>
                    <input
                      type="text"
                      placeholder="Enter recipe name"
                      value={recipeForm.title}
                      onChange={e => setRecipeForm({...recipeForm, title: e.target.value})}
                      required
                      style={styles.input}
                    />
                  </div>
                  <div style={styles.inputGroup}>
                    <label style={styles.label}>Key Ingredients</label>
                    <input
                      type="text"
                      placeholder="e.g., Chicken, Rice, Vegetables"
                      value={recipeForm.keyIngredients}
                      onChange={e => setRecipeForm({...recipeForm, keyIngredients: e.target.value})}
                      required
                      style={styles.input}
                    />
                  </div>
                </div>
                <div style={styles.inputGroup}>
                  <label style={styles.label}>Cooking Instructions</label>
                  <textarea
                    placeholder="Step-by-step cooking instructions..."
                    value={recipeForm.instructions}
                    onChange={e => setRecipeForm({...recipeForm, instructions: e.target.value})}
                    required
                    rows="4"
                    style={{...styles.input, resize: 'vertical', minHeight: '100px'}}
                  />
                </div>
                <button type="submit" disabled={isSubmitting} style={isSubmitting ? {...styles.button, ...styles.buttonDisabled} : styles.button}>
                  {isSubmitting ? 'Publishing...' : 'Publish Recipe'}
                </button>
              </form>
            </div>

            {/* Recipe Table */}
            <div style={styles.card}>
              <h3 style={styles.cardTitle}>Active Recipe Library</h3>
              <div style={styles.tableContainer}>
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>Title</th>
                      <th style={styles.th}>Ingredients</th>
                      <th style={styles.th}>Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {listData.length === 0 ? (
                      <tr>
                        <td colSpan="3" style={styles.emptyState}>
                          <div style={styles.emptyStateContent}>
                            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" strokeWidth="1.5">
                              <path d="M9.172 16.172a4 4 0 015.656 0M9 10h.01M15 10h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                            </svg>
                            <p>No recipes found. Create your first recipe above!</p>
                          </div>
                        </td>
                      </tr>
                    ) : (
                      listData.map(recipe => (
                        <tr key={recipe.id} style={styles.tr}>
                          <td style={styles.td}>{recipe.title}</td>
                          <td style={styles.td}>{recipe.keyIngredients}</td>
                          <td style={styles.td}>
                            <button
                              onClick={() => setSelectedRecipe(recipe)}
                              style={styles.actionButton}
                            >
                              View
                            </button>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}

        {view === 'users' && (
          <div style={styles.contentWrapper}>
            <h2 style={styles.pageTitle}>User Management</h2>
            <div style={styles.card}>
              <div style={styles.tableContainer}>
                <table style={styles.table}>
                  <thead>
                    <tr>
                      <th style={styles.th}>ID</th>
                      <th style={styles.th}>Email Address</th>
                      <th style={styles.th}>Date Registered</th>
                    </tr>
                  </thead>
                  <tbody>
                    {listData.length === 0 ? (
                      <tr>
                        <td colSpan="3" style={styles.emptyState}>
                          <div style={styles.emptyStateContent}>
                            <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="#9CA3AF" strokeWidth="1.5">
                              <path d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"/>
                            </svg>
                            <p>No data available.</p>
                          </div>
                        </td>
                      </tr>
                    ) : (
                      listData.map(item => (
                        <tr key={item.id} style={styles.tr}>
                          <td style={styles.td}>#{item.id}</td>
                          <td style={styles.td}>{item.email}</td>
                          <td style={styles.td}>{new Date(item.createdAt || Date.now()).toLocaleDateString()}</td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        )}
      </main>

      {/* Recipe Detail Modal Overlay */}
      {selectedRecipe && (
        <div style={styles.modalOverlay}>
          <div style={styles.modalContent}>
            <div style={styles.modalHeader}>
              <h3 style={styles.modalTitle}>{selectedRecipe.title}</h3>
              <button onClick={() => setSelectedRecipe(null)} style={styles.closeButton}>×</button>
            </div>
            <div style={styles.modalBody}>
              <div style={styles.modalSection}>
                <h4 style={styles.modalSectionTitle}>Key Ingredients</h4>
                <p style={styles.modalSectionText}>{selectedRecipe.keyIngredients}</p>
              </div>
              <div style={styles.modalSection}>
                <h4 style={styles.modalSectionTitle}>Instructions / Preparation</h4>
                <p style={styles.modalSectionText}>{selectedRecipe.instructions}</p>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

const styles = {
  container: {
    display: 'flex',
    minHeight: '100vh',
    backgroundColor: '#F9FAFB',
    fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
  },
  sidebar: {
    width: '260px',
    backgroundColor: 'white',
    borderRight: '1px solid #E5E7EB',
    display: 'flex',
    flexDirection: 'column',
    padding: '24px',
    position: 'fixed',
    height: '100vh',
    overflowY: 'auto',
  },
  brand: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    marginBottom: '32px',
    paddingBottom: '24px',
    borderBottom: '1px solid #F3F4F6',
  },
  logoIcon: {
    width: '40px',
    height: '40px',
    backgroundColor: '#2E7D32',
    borderRadius: '10px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    boxShadow: '0 4px 6px -1px rgba(46, 125, 50, 0.2)',
  },
  brandTitle: {
    fontSize: '20px',
    fontWeight: '700',
    color: '#111827',
    margin: 0,
  },
  nav: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
    flexGrow: 1,
  },
  navButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    padding: '12px 16px',
    borderRadius: '10px',
    border: 'none',
    backgroundColor: 'transparent',
    color: '#6B7280',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    transition: 'all 0.2s',
    textAlign: 'left',
  },
  navButtonActive: {
    backgroundColor: '#F0FDF4',
    color: '#2E7D32',
  },
  logoutButton: {
    display: 'flex',
    alignItems: 'center',
    gap: '12px',
    padding: '12px 16px',
    borderRadius: '10px',
    border: 'none',
    backgroundColor: '#FEF2F2',
    color: '#DC2626',
    fontSize: '14px',
    fontWeight: '500',
    cursor: 'pointer',
    transition: 'all 0.2s',
    marginTop: 'auto',
  },
  main: {
    flexGrow: 1,
    marginLeft: '260px',
    padding: '32px',
    maxWidth: 'calc(100vw - 260px)',
  },
  contentWrapper: {
    maxWidth: '1200px',
    margin: '0 auto',
  },
  pageTitle: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#111827',
    margin: '0 0 24px 0',
  },
  statsGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
    gap: '20px',
    marginBottom: '32px',
  },
  statCard: {
    backgroundColor: 'white',
    borderRadius: '16px',
    padding: '24px',
    display: 'flex',
    alignItems: 'center',
    gap: '16px',
    boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
    border: '1px solid #F3F4F6',
  },
  statIcon: {
    width: '48px',
    height: '48px',
    backgroundColor: '#F0FDF4',
    borderRadius: '12px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  statLabel: {
    fontSize: '14px',
    color: '#6B7280',
    margin: '0 0 4px 0',
  },
  statValue: {
    fontSize: '24px',
    fontWeight: '700',
    color: '#111827',
    margin: 0,
  },
  card: {
    backgroundColor: 'white',
    borderRadius: '16px',
    padding: '24px',
    marginBottom: '24px',
    boxShadow: '0 1px 3px rgba(0, 0, 0, 0.1)',
    border: '1px solid #F3F4F6',
  },
  cardTitle: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#1F2937',
    margin: '0 0 20px 0',
  },
  form: {
    display: 'flex',
    flexDirection: 'column',
    gap: '20px',
  },
  formGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
    gap: '20px',
  },
  inputGroup: {
    display: 'flex',
    flexDirection: 'column',
    gap: '6px',
  },
  label: {
    fontSize: '14px',
    fontWeight: '500',
    color: '#374151',
  },
  input: {
    padding: '10px 14px',
    border: '1px solid #E5E7EB',
    borderRadius: '10px',
    fontSize: '14px',
    backgroundColor: '#F9FAFB',
    outline: 'none',
    transition: 'all 0.2s',
    fontFamily: 'inherit',
  },
  button: {
    padding: '12px 24px',
    backgroundColor: '#2E7D32',
    color: 'white',
    border: 'none',
    borderRadius: '10px',
    fontSize: '14px',
    fontWeight: '600',
    cursor: 'pointer',
    transition: 'all 0.2s',
    alignSelf: 'flex-start',
    boxShadow: '0 4px 6px -1px rgba(46, 125, 50, 0.2)',
  },
  buttonDisabled: {
    opacity: '0.5',
    cursor: 'not-allowed',
  },
  tableContainer: {
    overflowX: 'auto',
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
  },
  th: {
    textAlign: 'left',
    padding: '12px 16px',
    fontSize: '12px',
    fontWeight: '600',
    color: '#6B7280',
    textTransform: 'uppercase',
    letterSpacing: '0.05em',
    borderBottom: '1px solid #E5E7EB',
    backgroundColor: '#F9FAFB',
  },
  tr: {
    borderBottom: '1px solid #F3F4F6',
    transition: 'background-color 0.2s',
  },
  td: {
    padding: '16px',
    fontSize: '14px',
    color: '#374151',
  },
  emptyState: {
    textAlign: 'center',
    padding: '48px',
    color: '#9CA3AF',
  },
  emptyStateContent: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '12px',
  },
  actionButton: {
    padding: '6px 12px',
    backgroundColor: '#F3F4F6',
    border: 'none',
    borderRadius: '6px',
    fontSize: '12px',
    fontWeight: '500',
    color: '#374151',
    cursor: 'pointer',
    transition: 'all 0.2s',
  },
  modalOverlay: {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(17, 24, 39, 0.6)',
    backdropFilter: 'blur(4px)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 9999,
  },
  modalContent: {
    backgroundColor: 'white',
    borderRadius: '16px',
    width: '90%',
    maxWidth: '500px',
    boxShadow: '0 20px 25px -5px rgba(0, 0, 0, 0.15), 0 10px 10px -5px rgba(0, 0, 0, 0.04)',
    border: '1px solid #F3F4F6',
    overflow: 'hidden',
  },
  modalHeader: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    padding: '20px 24px',
    borderBottom: '1px solid #F3F4F6',
  },
  modalTitle: {
    fontSize: '20px',
    fontWeight: '700',
    color: '#111827',
    margin: 0,
  },
  closeButton: {
    background: 'none',
    border: 'none',
    fontSize: '24px',
    color: '#9CA3AF',
    cursor: 'pointer',
    padding: '4px',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    transition: 'color 0.2s',
    lineHeight: 1,
  },
  modalBody: {
    padding: '24px',
    display: 'flex',
    flexDirection: 'column',
    gap: '20px',
    maxHeight: '70vh',
    overflowY: 'auto',
  },
  modalSection: {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px',
  },
  modalSectionTitle: {
    fontSize: '11px',
    fontWeight: '600',
    color: '#9CA3AF',
    textTransform: 'uppercase',
    letterSpacing: '0.05em',
    margin: 0,
  },
  modalSectionText: {
    fontSize: '14px',
    color: '#4B5563',
    lineHeight: '1.6',
    margin: 0,
    whiteSpace: 'pre-wrap',
  },
};