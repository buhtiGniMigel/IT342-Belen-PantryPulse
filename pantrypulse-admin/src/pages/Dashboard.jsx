import { useNavigate } from 'react-router-dom';

export default function Dashboard() {
  const navigate = useNavigate();

  const handleLogout = () => {
    // In Phase 2, we will also clear the JWT authentication token here.
    // For now, simply navigate the user back to the login screen.
    navigate('/login');
  };

  return (
    <div className="wireframe-container">
      <div className="wireframe-card" style={{ textAlign: 'center' }}>
        <h1 className="brand-title">PantryPulse Admin</h1>
        <h2 className="page-title">Dashboard Overview</h2>
        
        <p style={{ margin: '40px 0', fontSize: '18px', color: '#374151' }}>
          Welcome to the Admin Dashboard!
        </p>
        
        <button onClick={handleLogout} className="wireframe-btn" style={{ width: '100%' }}>
          LOGOUT
        </button>
      </div>
    </div>
  );
}