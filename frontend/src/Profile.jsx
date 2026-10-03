import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Profile() {
  const navigate = useNavigate();
  const userRole = localStorage.getItem("role");

  const [profile, setProfile] = useState({
    firstName: '', lastName: '', contactNumber: '',
    shippingAddress: '', city: '', district: '', province: '', postalCode: ''
  });
  const [message, setMessage] = useState('');

  // fetches the user's data when the component loads
  useEffect(() => {
    const token = localStorage.getItem('token');
    
    if (!token) {
      setMessage("No active session. Please log in first.");
      return;
    }

    fetch('http://localhost:8080/api/profile', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}` // how the backend identifies the user
      }
    })
    .then(res => {
      if (!res.ok) throw new Error("Unauthorized or session expired.");
      return res.json();
    })
    .then(data => setProfile(data))
    .catch(err => setMessage(err.message));
  }, []);

  const handleChange = (e) => {
    setProfile({ ...profile, [e.target.name]: e.target.value });
  };

  // sends the updated data back to the server
  const handleUpdate = (e) => {
    e.preventDefault();
    const token = localStorage.getItem('token');

    fetch('http://localhost:8080/api/profile', {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}` 
      },
      body: JSON.stringify(profile)
    })
    .then(async res => {
      const text = await res.text();
      if (res.ok) setMessage(`✅ ${text}`);
      else setMessage(`❌ Error: ${text}`);
    })
    .catch(() => setMessage("❌ Connection failed."));
  };

  return (
    <div style={{ padding: '20px', background: '#f5f5f5', borderRadius: '8px', maxWidth: '500px', marginTop: '20px', margin: '20px auto' }}>
      
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' }}>
        <h3 style={{ margin: 0 }}>My Profile</h3>
        
        {/* this button will ONLY show up if the logged-in user is a staff member */}
        {userRole === 'STORE_STAFF' && (
          <button 
            onClick={() => navigate('/admin/dashboard')}
            style={{ padding: '8px 16px', background: '#1976d2', color: 'white', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold', border: 'none' }}
          >
            Admin Dashboard
          </button>
        )}
      </div>
      
      {message && (
        <div style={{ marginBottom: '15px', padding: '10px', background: '#e3f2fd', borderRadius: '4px' }}>
          <strong>{message}</strong>
        </div>
      )}

      <form onSubmit={handleUpdate} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <div style={{ display: 'flex', gap: '10px' }}>
          <input type="text" name="firstName" value={profile.firstName} onChange={handleChange} placeholder="First Name" required style={{ padding: '8px', flex: 1 }} />
          <input type="text" name="lastName" value={profile.lastName} onChange={handleChange} placeholder="Last Name" required style={{ padding: '8px', flex: 1 }} />
        </div>
        <input type="tel" name="contactNumber" value={profile.contactNumber} onChange={handleChange} placeholder="Contact Number" required style={{ padding: '8px' }} />
        
        <h4 style={{ margin: '10px 0 0 0' }}>Shipping Address</h4>
        <input type="text" name="shippingAddress" value={profile.shippingAddress} onChange={handleChange} placeholder="Street Address" required style={{ padding: '8px' }} />
        
        <div style={{ display: 'flex', gap: '10px' }}>
          <input type="text" name="city" value={profile.city} onChange={handleChange} placeholder="City" required style={{ padding: '8px', flex: 1 }} />
          <input type="text" name="district" value={profile.district} onChange={handleChange} placeholder="District" required style={{ padding: '8px', flex: 1 }} />
        </div>
        
        <div style={{ display: 'flex', gap: '10px' }}>
          <input type="text" name="province" value={profile.province} onChange={handleChange} placeholder="Province" required style={{ padding: '8px', flex: 1 }} />
          <input type="text" name="postalCode" value={profile.postalCode || ''} onChange={handleChange} placeholder="Postal Code" style={{ padding: '8px', flex: 1 }} />
        </div>
        
        <button type="submit" style={{ padding: '12px', background: '#1976d2', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', marginTop: '10px', fontWeight: 'bold' }}>
          Update Profile
        </button>
      </form>
    </div>
  );
}