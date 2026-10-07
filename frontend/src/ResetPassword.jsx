import { useState } from 'react';
import { useSearchParams, Link } from 'react-router-dom';

export default function ResetPassword() {
    const [searchParams] = useSearchParams();
    const token = searchParams.get('token'); // Extracts ?token=XYZ from the URL
    
    const [newPassword, setNewPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('');
        setError('');

        if (!token) {
            setError('Invalid or missing reset token.');
            return;
        }

        try {
            const response = await fetch('http://localhost:8080/api/auth/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ token, newPassword }),
            });

            const data = await response.text();

            if (response.ok) {
                setMessage(data);
                setNewPassword('');
            } else {
                setError(data || 'Failed to reset password.');
            }
        } catch (err) {
            setError('Network error. Please try again.');
        }
    };

    return (
        <div className="auth-container">
            <h2>Enter New Password</h2>
            {token ? (
                <form onSubmit={handleSubmit}>
                    <label>New Password</label>
                    <input 
                        type="password" 
                        value={newPassword} 
                        onChange={(e) => setNewPassword(e.target.value)} 
                        required 
                    />
                    <button type="submit">Update Password</button>
                </form>
            ) : (
                <p style={{ color: 'red' }}>No reset token provided in the URL.</p>
            )}

            {message && <p style={{ color: 'green' }}>{message} <Link to="/login">Login here.</Link></p>}
            {error && <p style={{ color: 'red' }}>{error}</p>}
        </div>
    );
}