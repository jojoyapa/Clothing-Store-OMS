import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; 

const Navbar = () => {
  const { user } = useAuth(); // 'user' should contain the decoded JWT payload or state

  return (
    <nav>
      {/* Public links */}
      <Link to="/">Home</Link>
      <Link to="/products">Shop</Link>

      {/* Admin Link */}
      {user && (user.role === 'STORE_STAFF' || user.role === 'ADMIN') && (
        <Link to="/admin/dashboard" className="admin-shortcut-btn font-bold text-blue-600">
          Admin Dashboard
        </Link>
      )}

      {/* Profile Link */}
      {user ? (
        <Link to="/profile">My Details</Link>
      ) : (
        <Link to="/login">Login</Link>
      )}
    </nav>
  );
};