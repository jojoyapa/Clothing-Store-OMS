import { useState } from 'react';

export default function LoginForm({ onLoginSuccess }) {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [message, setMessage] = useState('');

  const handleLogin = (e) => {
    e.preventDefault(); 
    fetch('http://localhost:8080/api/auth/login', {
      method: 'POST', 
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password })
    })
    .then(async response => {
      const text = await response.text();
      
      if (response.ok) {
        const cleanToken = text.replace(/"/g, '');
        localStorage.setItem('token', cleanToken);
        
        try {
          const payloadBase64Url = cleanToken.split('.')[1];
          // converts Base64Url to standard Base64 so atob() doesn't crash
          const base64 = payloadBase64Url.replace(/-/g, '+').replace(/_/g, '/');
          const decodedPayload = JSON.parse(atob(base64));
          
          console.log("Decoded Token Payload:", decodedPayload);
          localStorage.setItem("role", decodedPayload.role); 
        } catch (error) {
          console.error("Failed to decode token role", error);
        }

        if (onLoginSuccess) onLoginSuccess();
      } else {
        setMessage(`❌ Error: ${text}`);
      }
    })
    .catch(error => {
      console.error(error); 
      setMessage("Network error or connection refused.");
    });
  };

  return (
    <div style={{ padding: '20px', background: '#f5f5f5', borderRadius: '8px', maxWidth: '400px' }}>
      <h3>System Login</h3>
      <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
        <input 
          type="email" 
          placeholder="Email Address" 
          value={email} 
          onChange={(e) => setEmail(e.target.value)} 
          required 
          style={{ padding: '8px' }}
        />
        <input 
          type="password" 
          placeholder="Password" 
          value={password} 
          onChange={(e) => setPassword(e.target.value)} 
          required 
          style={{ padding: '8px' }}
        />
        <button type="submit" style={{ padding: '10px', background: '#2e7d32', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer', fontWeight: 'bold' }}>
          Sign In
        </button>
      </form>
      
      {message && (
        <div style={{ 
            marginTop: '15px', 
            padding: '10px', 
            background: '#e8f5e9', 
            border: '1px solid #c8e6c9', 
            borderRadius: '4px',
            wordBreak: 'break-all',    
            overflowWrap: 'break-word'
        }}>
            <p style={{ margin: 0, fontWeight: 'bold', color: '#2e7d32' }}>{message}</p>
        </div>
      )}
    </div>
  );
}